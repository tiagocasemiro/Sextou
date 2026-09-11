package com.sextou.features.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sextou.domain.Failure
import com.sextou.domain.Loading
import com.sextou.domain.Success
import com.sextou.domain.favorites.usecase.ObserveFavoritesUseCase
import com.sextou.domain.ignored.usecase.ObserveIgnoredPlacesUseCase
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlacePhotoReference
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.usecase.GetPlacePhotoUseCase
import com.sextou.domain.places.usecase.LoadedPlacesUseCase
import com.sextou.domain.routes.usecase.GetRouteUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.ArrayDeque
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapViewModel(
    private val loadedPlacesUseCase: LoadedPlacesUseCase,
    private val getPlacePhotoUseCase: GetPlacePhotoUseCase,
    private val getRouteUseCase: GetRouteUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val observeIgnoredPlacesUseCase: ObserveIgnoredPlacesUseCase,
    initialLocation: GeoPoint? = null,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(
        MapUiState(userLocation = initialLocation?.toUiModel()),
    )
    private var searchLocation: GeoPoint? = initialLocation
    private var loadedLocation: GeoPoint? = null
    private var pendingSearchLocation: GeoPoint? = null
    private var opening = false
    private var openingRefreshStarted = false
    private var dialogCenter: GeoPoint? = null
    private var currentMapCenter: GeoPoint? = null
    private var confirmedRadius: Int? = null
    private var confirmedCustomRadius = 3_000
    private val resolvedPhotos = mutableMapOf<String, PlacePhoto>()
    private var searchJob: Job? = null
    private var photoJob: Job? = null
    private var routeJob: Job? = null
    private val photoReferences = mutableMapOf<String, PlacePhotoReference>()
    private val requestedPhotoPlaceIds = mutableSetOf<String>()
    private val photoQueue = ArrayDeque<PhotoRequest>()
    private var routeDestination: GeoPoint? = null
    private var requestedRoute: RouteRequest? = null

    val uiState: StateFlow<MapUiState> = mutableUiState.asStateFlow()

    init {
        loadedPlacesUseCase.places.onEach { renderPlaces() }.launchIn(viewModelScope)
        loadedPlacesUseCase.hasLocalFailure.onEach { failed ->
            mutableUiState.update { it.copy(isLocalError = failed) }
        }.launchIn(viewModelScope)
        observeFavoritesUseCase()
            .onEach { placeIds ->
                mutableUiState.update { state -> state.copy(favoritePlaceIds = placeIds) }
            }
            .catch { throwable -> handleMarkerStateFailure(throwable) }
            .launchIn(viewModelScope)
        observeIgnoredPlacesUseCase()
            .onEach { placeIds ->
                mutableUiState.update { state -> state.copy(ignoredPlaceIds = placeIds) }
            }
            .catch { throwable -> handleMarkerStateFailure(throwable) }
            .launchIn(viewModelScope)
    }

    fun load(query: String) {
        onQueryChanged(query)
        onScreenOpened()
    }

    fun onScreenOpened() {
        if (opening) return
        opening = true
        openingRefreshStarted = false
        startOpeningRefresh()
    }

    fun onScreenClosed() { opening = false }

    private fun startOpeningRefresh() {
        val location = searchLocation ?: return
        if (!opening || openingRefreshStarted) return
        openingRefreshStarted = true
        if (loadedLocation == null) loadedLocation = location
        searchJob = viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true, isError = false) }
            val result = loadedPlacesUseCase.open(location)
            mutableUiState.update { it.copy(isLoading = false, isError = result is Failure) }
        }
    }

    private fun renderPlaces() {
        rememberPhotoReferences(loadedPlacesUseCase.places.value)
        val mapPlaces = loadedPlacesUseCase.filter(uiState.value.query).mapNotNull { place ->
            place.location?.takeIf { it.latitude in -90.0..90.0 && it.longitude in -180.0..180.0 }?.let { location ->
                MapPlaceUiModel(
                    id = place.id,
                    photoUri = resolvedPhotos[place.id]?.uri,
                    photoAttribution = resolvedPhotos[place.id]?.toAttribution(),
                    name = place.displayName?.takeIf(String::isNotBlank) ?: place.id,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    rating = place.rating,
                    ratingsCount = place.userRatingCount,
                    categoryText = place.primaryTypeDisplayName
                        ?.takeIf(String::isNotBlank)
                        ?: place.primaryType?.takeIf(String::isNotBlank),
                    address = place.formattedAddress,
                    googleMapsUri = place.googleMapsUri,
                    distanceMeters = searchLocation?.distanceTo(location),
                    priceLevel = place.priceLevel,
                    primaryType = place.primaryType,
                    placeTypes = place.types,
                )
            }
        }
        mutableUiState.update { it.copy(places = mapPlaces) }
    }

    fun requestPhoto(placeId: String) {
        if (placeId.isBlank() || uiState.value.places.none { it.id == placeId }) return

        val reference = photoReferences[placeId] ?: return
        if (!requestedPhotoPlaceIds.add(placeId)) return

        photoQueue.addLast(PhotoRequest(placeId = placeId, reference = reference))
        startPhotoJobIfNeeded()
    }

    private fun rememberPhotoReferences(places: List<PlaceSummary>) {
        places.forEach { place ->
            if (place.id.isNotBlank()) {
                photoReferences[place.id] = place.photos.firstOrNull() ?: firstPhotoReference(place.id)
            }
        }
    }

    private fun startPhotoJobIfNeeded() {
        if (photoJob?.isActive == true) return

        photoJob = viewModelScope.launch {
            while (isActive) {
                val request = photoQueue.pollFirst() ?: break
                try {
                    when (val result = getPlacePhotoUseCase(request.reference)) {
                        is Success -> {
                            val uri = result.data.uri.takeIf(String::isNotBlank)
                            if (uri != null && isActive) {
                                resolvedPhotos[request.placeId] = result.data
                                mutableUiState.update { state ->
                                    state.copy(
                                        places = state.places.map { currentPlace ->
                                            if (currentPlace.id == request.placeId) {
                                                currentPlace.copy(
                                                    photoAttribution = result.data.toAttribution(),
                                                    photoUri = uri,
                                                )
                                            } else {
                                                currentPlace
                                            }
                                        },
                                    )
                                }
                            }
                        }

                        is Failure,
                        is Loading<*>,
                        -> Unit
                    }
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (_: Exception) {
                    // A missing photo must not hide an establishment from the map.
                }
            }
        }
    }

    private fun PlacePhoto.toAttribution(): String? =
        attributionHtml?.takeIf(String::isNotBlank)
            ?: authors.joinToString(", ") { it.name }.takeIf(String::isNotBlank)

    private data class PhotoRequest(
        val placeId: String,
        val reference: PlacePhotoReference,
    )

    private fun firstPhotoReference(placeId: String) = PlacePhotoReference(
        placeId = placeId,
        index = 0,
        width = 0,
        height = 0,
        attributionHtml = null,
        authors = emptyList(),
        googleMapsUri = null,
        flagContentUri = null,
    )

    fun onQueryChanged(query: String) {
        mutableUiState.update { it.copy(query = query) }
        renderPlaces()
    }

    fun onMapCenterChanged(center: GeoPoint) {
        currentMapCenter = center
        val reference = loadedLocation ?: searchLocation
        if (reference == null) {
            loadedLocation = center
            return
        }
        val moved = reference.distanceTo(center) > SEARCH_AREA_CHANGE_THRESHOLD_METERS
        pendingSearchLocation = center.takeIf { moved }
        mutableUiState.update { it.copy(isSearchAreaButtonVisible = moved) }
    }

    fun onSearchAreaClicked() {
        if (uiState.value.isLoading || uiState.value.isRadiusDialogVisible) return
        dialogCenter = pendingSearchLocation ?: return
        mutableUiState.update {
            it.copy(isRadiusDialogVisible = true, selectedRadiusMeters = confirmedRadius,
                customRadiusMeters = confirmedCustomRadius)
        }
    }

    fun onRadiusSelected(radiusMeters: Int?) {
        if (!uiState.value.isRadiusDialogVisible || uiState.value.isManualSearchLoading) return
        if (radiusMeters != null && radiusMeters !in FIXED_RADII_METERS) return
        mutableUiState.update { it.copy(selectedRadiusMeters = radiusMeters) }
    }

    fun onCustomRadiusChanged(radiusMeters: Int) {
        if (!uiState.value.isRadiusDialogVisible || uiState.value.isManualSearchLoading) return
        val radius = ((radiusMeters + 250) / 500 * 500).coerceIn(500, 50_000)
        mutableUiState.update { it.copy(customRadiusMeters = radius) }
    }

    fun onRadiusDismissed() {
        if (uiState.value.isManualSearchLoading) return
        dialogCenter = null
        mutableUiState.update { it.copy(isRadiusDialogVisible = false) }
    }

    fun onRadiusConfirmed() {
        val center = dialogCenter ?: return
        val state = uiState.value
        if (!state.isRadiusDialogVisible || state.isManualSearchLoading || state.isLoading) return
        confirmedRadius = state.selectedRadiusMeters
        confirmedCustomRadius = state.customRadiusMeters
        val radius = (confirmedRadius ?: confirmedCustomRadius).toDouble()
        mutableUiState.update { it.copy(isManualSearchLoading = true, isLoading = true, isError = false) }
        searchJob = viewModelScope.launch {
            val result = loadedPlacesUseCase.searchManually(center, radius)
            if (result is Success) {
                loadedLocation = center
                dialogCenter = null
            }
            mutableUiState.update {
                it.copy(isManualSearchLoading = false, isLoading = false, isError = result is Failure,
                    isRadiusDialogVisible = result !is Success)
            }
            currentMapCenter?.let(::onMapCenterChanged)
        }
    }

    fun onLocationChanged(location: GeoPoint?, bearingDegrees: Float? = null) {
        searchLocation = location?.takeIf {
            it.latitude in -90.0..90.0 && it.longitude in -180.0..180.0
        }
        mutableUiState.update { it.copy(userLocation = searchLocation?.toUiModel(bearingDegrees)) }
        renderPlaces()
        startOpeningRefresh()
        loadRouteIfPossible()
    }

    fun setRouteDestination(destination: GeoPoint?) {
        if (routeDestination == destination) return

        routeDestination = destination
        requestedRoute = null
        routeJob?.cancel()
        mutableUiState.update {
            it.copy(
                routePoints = emptyList(),
                isRouteLoading = false,
                isRouteError = false,
            )
        }
        loadRouteIfPossible()
    }

    private fun loadRouteIfPossible() {
        val destination = routeDestination ?: return
        val origin = mutableUiState.value.userLocation?.toGeoPoint() ?: return
        val routeRequest = RouteRequest(origin = origin, destination = destination)
        if (requestedRoute == routeRequest) return

        requestedRoute = routeRequest
        routeJob?.cancel()
        routeJob = viewModelScope.launch {
            mutableUiState.update {
                it.copy(
                    routePoints = emptyList(),
                    isRouteLoading = true,
                    isRouteError = false,
                )
            }
            try {
                when (val result = getRouteUseCase(origin, destination)) {
                    is Success -> mutableUiState.update {
                        it.copy(
                            routePoints = result.data.points,
                            isRouteLoading = false,
                            isRouteError = false,
                        )
                    }

                    is Failure -> mutableUiState.update {
                        it.copy(isRouteLoading = false, isRouteError = true)
                    }

                    is Loading<*> -> mutableUiState.update {
                        it.copy(isRouteLoading = true)
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                mutableUiState.update {
                    it.copy(isRouteLoading = false, isRouteError = true)
                }
            }
        }
    }

    private fun MapUserLocationUiModel.toGeoPoint() = GeoPoint(
        latitude = latitude,
        longitude = longitude,
    )

    private fun GeoPoint.toUiModel(bearingDegrees: Float? = null) = MapUserLocationUiModel(
        latitude = latitude,
        longitude = longitude,
        bearingDegrees = bearingDegrees,
    )

    private data class RouteRequest(
        val origin: GeoPoint,
        val destination: GeoPoint,
    )

    private fun GeoPoint.distanceTo(other: GeoPoint): Double {
        val latitudeDelta = Math.toRadians(other.latitude - latitude)
        val longitudeDelta = Math.toRadians(other.longitude - longitude)
        val startLatitude = Math.toRadians(latitude)
        val endLatitude = Math.toRadians(other.latitude)
        val haversine = sin(latitudeDelta / 2) * sin(latitudeDelta / 2) +
            cos(startLatitude) * cos(endLatitude) *
            sin(longitudeDelta / 2) * sin(longitudeDelta / 2)

        return EARTH_RADIUS_METERS * 2 * atan2(sqrt(haversine), sqrt(1 - haversine))
    }

    private fun handleMarkerStateFailure(throwable: Throwable) {
        if (throwable is CancellationException) throw throwable
        mutableUiState.update { it.copy(isError = true) }
    }

    private companion object {
        val FIXED_RADII_METERS = listOf(500, 1_000, 2_000, 5_000, 10_000)
        const val EARTH_RADIUS_METERS = 6_371_000.0
        const val SEARCH_AREA_CHANGE_THRESHOLD_METERS = 50.0
    }
}
