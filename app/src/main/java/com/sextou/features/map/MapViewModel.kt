package com.sextou.features.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sextou.domain.Failure
import com.sextou.domain.Loading
import com.sextou.domain.Success
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.usecase.SearchPlacesUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    private val searchPlacesUseCase: SearchPlacesUseCase,
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

    val uiState: StateFlow<MapUiState> = mutableUiState.asStateFlow()

    fun load(query: String) {
        activeQuery = query
        if (loadedQuery == query &&
            loadedLocation == searchLocation &&
            (mutableUiState.value.isLoading || mutableUiState.value.places.isNotEmpty())
        ) {
            return
        }
        loadedQuery = query
        loadedLocation = searchLocation
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true, isError = false) }
            try {
                when (val result = searchPlacesUseCase(query, location = searchLocation)) {
                    is Success -> mutableUiState.update {
                        it.copy(
                            places = result.data.mapNotNull { place ->
                                place.location?.let { location ->
                                    MapPlaceUiModel(
                                        id = place.id,
                                        name = place.displayName?.takeIf(String::isNotBlank) ?: place.id,
                                        latitude = location.latitude,
                                        longitude = location.longitude,
                                        rating = place.rating,
                                    )
                                }
                            },
                            isLoading = false,
                            isError = false,
                        )
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
}
