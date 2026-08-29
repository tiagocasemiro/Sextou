package com.sextou.features.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sextou.domain.Failure
import com.sextou.domain.Loading
import com.sextou.domain.Success
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlacePhotoReference
import com.sextou.domain.places.usecase.GetPlacePhotoUseCase
import com.sextou.domain.places.usecase.SearchPlacesUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapViewModel(
    private val searchPlacesUseCase: SearchPlacesUseCase,
    private val getPlacePhotoUseCase: GetPlacePhotoUseCase,
    initialLocation: GeoPoint? = null,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(
        MapUiState(userLocation = initialLocation?.toUiModel()),
    )
    private var searchLocation: GeoPoint? = initialLocation
    private var loadedQuery: String? = null
    private var loadedLocation: GeoPoint? = null
    private var activeQuery = ""
    private var searchJob: Job? = null
    private var photoJob: Job? = null
    private var photoLoadGeneration = 0L

    val uiState: StateFlow<MapUiState> = mutableUiState.asStateFlow()

    fun load(query: String) {
        activeQuery = query
        mutableUiState.update { state ->
            if (state.query == query) state else state.copy(query = query)
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
                                        categoryText = place.primaryTypeDisplayName
                                            ?.takeIf(String::isNotBlank)
                                            ?: place.primaryType?.takeIf(String::isNotBlank),
                                        distanceMeters = searchLocation?.distanceTo(location),
                                        priceLevel = place.priceLevel,
                                        primaryType = place.primaryType,
                                        placeTypes = place.types,
                                    ),
                                    photoReference = place.photos.firstOrNull(),
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
        val placesWithPhotos = places.filter { it.photoReference != null }
        if (placesWithPhotos.isEmpty()) return

        photoJob = viewModelScope.launch {
            placesWithPhotos.forEach { place ->
                val reference = place.photoReference ?: return@forEach
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

                    is Failure,
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
        val photoReference: PlacePhotoReference?,
    )

    fun onQueryChanged(query: String) {
        load(query)
    }

    fun onLocationChanged(location: GeoPoint?) {
        if (location == searchLocation) return

        searchLocation = location
        mutableUiState.update { it.copy(userLocation = location?.toUiModel()) }
        loadedQuery?.let { load(activeQuery) }
    }

    private fun GeoPoint.toUiModel() = MapUserLocationUiModel(
        latitude = latitude,
        longitude = longitude,
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

    private companion object {
        const val EARTH_RADIUS_METERS = 6_371_000.0
    }
}
