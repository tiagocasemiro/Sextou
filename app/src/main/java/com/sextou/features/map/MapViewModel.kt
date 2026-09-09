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
import com.sextou.domain.places.usecase.GetPlacePhotoUseCase
import com.sextou.domain.places.usecase.SearchPlacesUseCase
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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapViewModel(
    private val searchPlacesUseCase: SearchPlacesUseCase,
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
    private var loadedQuery: String? = null
    private var loadedLocation: GeoPoint? = null
    private var activeQuery = ""
    private var pendingSearchLocation: GeoPoint? = null
    private var searchJob: Job? = null
    private var photoJob: Job? = null
    private var routeJob: Job? = null
    private var photoLoadGeneration = 0L
    private var routeDestination: GeoPoint? = null
    private var requestedRoute: RouteRequest? = null

    val uiState: StateFlow<MapUiState> = mutableUiState.asStateFlow()

    init {
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
        activeQuery = query
        pendingSearchLocation = null
        mutableUiState.update { state ->
            state.copy(
                query = query,
                isSearchAreaButtonVisible = false,
            )
        }
        if (loadedQuery == query &&
            loadedLocation == searchLocation &&
            (mutableUiState.value.isLoading || mutableUiState.value.places.isNotEmpty())
        ) {
            return
        }
        loadedQuery = query
        loadedLocation = searchLocation
        searchJob?.cancel()
        photoJob?.cancel()
        val generation = ++photoLoadGeneration
        searchJob = viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true, isError = false) }
            try {
                when (
                    val result = searchPlacesUseCase(
                        query,
                        location = searchLocation,
                        includePhotos = true,
                    )
                ) {
                    is Success -> {
                        val mappedPlaces = result.data.mapNotNull { place ->
                            place.location?.let { location ->
                                MapPlaceMapping(
                                    place = MapPlaceUiModel(
                                        id = place.id,
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
                                    ),
                                    photoReference = place.photos.firstOrNull()
                                        ?: firstPhotoReference(place.id),
                                )
                            }
                        }
                        val mapPlaces = mappedPlaces.map(MapPlaceMapping::place)
                        mutableUiState.update {
                            it.copy(
                                places = mapPlaces,
                                isLoading = false,
                                isError = false,
                            )
                        }
                        loadPhotoUris(mappedPlaces, generation)
                    }

                    is Failure -> mutableUiState.update {
                        it.copy(isLoading = false, isError = true)
                    }

                    is Loading<*> -> mutableUiState.update {
                        it.copy(isLoading = true)
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                mutableUiState.update {
                    it.copy(isLoading = false, isError = true)
                }
            }
        }
    }

    private fun loadPhotoUris(places: List<MapPlaceMapping>, generation: Long) {
        if (places.isEmpty()) return

        photoJob = viewModelScope.launch {
            places.forEach { place ->
                val reference = place.photoReference
                if (!isActive || generation != photoLoadGeneration) return@launch

                when (val result = getPlacePhotoUseCase(reference)) {
                    is Success -> {
                        val uri = result.data.uri.takeIf(String::isNotBlank)
                        if (uri != null && isActive && generation == photoLoadGeneration) {
                            mutableUiState.update { state ->
                                state.copy(
                                    places = state.places.map { currentPlace ->
                                        if (currentPlace.id == place.place.id) {
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

                    is Failure -> Unit
                    is Loading<*>,
                    -> Unit
                }
            }
        }
    }

    private fun PlacePhoto.toAttribution(): String? =
        attributionHtml?.takeIf(String::isNotBlank)
            ?: authors.joinToString(", ") { it.name }.takeIf(String::isNotBlank)

    private data class MapPlaceMapping(
        val place: MapPlaceUiModel,
        val photoReference: PlacePhotoReference,
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
        load(query)
    }

    fun onMapCenterChanged(center: GeoPoint) {
        val referenceLocation = loadedLocation ?: searchLocation
        val movedToAnotherArea = referenceLocation == null ||
            referenceLocation.distanceTo(center) > SEARCH_AREA_CHANGE_THRESHOLD_METERS

        pendingSearchLocation = center.takeIf { movedToAnotherArea }
        mutableUiState.update {
            it.copy(isSearchAreaButtonVisible = movedToAnotherArea)
        }
    }

    fun onSearchAreaClicked() {
        val center = pendingSearchLocation ?: return

        pendingSearchLocation = null
        searchLocation = center
        mutableUiState.update { it.copy(isSearchAreaButtonVisible = false) }
        load(activeQuery)
    }

    fun onLocationChanged(
        location: GeoPoint?,
        bearingDegrees: Float? = null,
    ) {
        val locationChanged = location != searchLocation
        val currentUserLocation = mutableUiState.value.userLocation
        val bearingChanged = currentUserLocation?.bearingDegrees != bearingDegrees
        if (!locationChanged && !bearingChanged) return

        if (locationChanged) {
            pendingSearchLocation = null
            searchLocation = location
        }
        mutableUiState.update {
            it.copy(userLocation = location?.toUiModel(bearingDegrees))
        }
        if (locationChanged) {
            loadedQuery?.let { load(activeQuery) }
        }
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
        const val EARTH_RADIUS_METERS = 6_371_000.0
        const val SEARCH_AREA_CHANGE_THRESHOLD_METERS = 50.0
    }
}
