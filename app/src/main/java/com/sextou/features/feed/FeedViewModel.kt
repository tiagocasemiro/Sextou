package com.sextou.features.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sextou.R
import com.sextou.domain.Failure
import com.sextou.domain.Loading
import com.sextou.domain.Success
import com.sextou.domain.favorites.usecase.ObserveFavoritesUseCase
import com.sextou.domain.favorites.usecase.ToggleFavoriteUseCase
import com.sextou.domain.places.model.BusinessStatus
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.usecase.SearchPlacesUseCase
import com.sextou.domain.visits.usecase.ObserveVisitedPlacesUseCase
import com.sextou.domain.visits.usecase.ToggleVisitedPlaceUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class FeedViewModel(
    private val searchPlacesUseCase: SearchPlacesUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val observeVisitedPlacesUseCase: ObserveVisitedPlacesUseCase,
    private val toggleVisitedPlaceUseCase: ToggleVisitedPlaceUseCase,
    initialLocation: GeoPoint? = null,
) : ViewModel() {
    private var searchLocation: GeoPoint? = initialLocation
    private var allPlaces: List<FeedPlaceUiModel> = emptyList()
    private var searchJob: Job? = null

    private val mutableUiState = MutableStateFlow(FeedUiState())

    val uiState: StateFlow<FeedUiState> = mutableUiState.asStateFlow()

    init {
        observeFavoritesUseCase()
            .onEach { favoritePlaceIds ->
                mutableUiState.update { it.copy(favoritePlaceIds = favoritePlaceIds) }
            }
            .catch { throwable ->
                if (throwable is CancellationException) throw throwable
                mutableUiState.update {
                    it.copy(actionErrorMessageResId = R.string.feed_local_error)
                }
            }
            .launchIn(viewModelScope)
        observeVisitedPlacesUseCase()
            .onEach { visitedPlaceIds ->
                mutableUiState.update { it.copy(visitedPlaceIds = visitedPlaceIds) }
            }
            .catch { throwable ->
                if (throwable is CancellationException) throw throwable
                mutableUiState.update {
                    it.copy(actionErrorMessageResId = R.string.feed_local_error)
                }
            }
            .launchIn(viewModelScope)
        loadPlaces()
    }

    fun onQueryChanged(query: String) {
        mutableUiState.update {
            it.copy(
                query = query,
                places = filterPlaces(allPlaces, query),
                isError = false,
                isStale = false,
                errorMessageResId = null,
            )
        }
        loadPlaces(query)
    }

    fun onLocationChanged(location: GeoPoint?) {
        searchLocation = location
        loadPlaces()
    }

    fun retry() {
        loadPlaces()
    }

    fun onFavoriteClicked(placeId: String) {
        val selected = placeId !in mutableUiState.value.favoritePlaceIds
        viewModelScope.launch {
            when (toggleFavoriteUseCase(placeId, selected)) {
                is Success -> mutableUiState.update { state ->
                    state.copy(
                        favoritePlaceIds = state.favoritePlaceIds.withSelection(
                            value = placeId,
                            selected = selected,
                        ),
                        actionErrorMessageResId = null,
                    )
                }

                is Failure -> mutableUiState.update {
                    it.copy(actionErrorMessageResId = R.string.feed_local_error)
                }

                is Loading<*> -> Unit
            }
        }
    }

    fun onVisitedClicked(placeId: String) {
        val selected = placeId !in mutableUiState.value.visitedPlaceIds
        viewModelScope.launch {
            when (toggleVisitedPlaceUseCase(placeId, selected)) {
                is Success -> mutableUiState.update { state ->
                    state.copy(
                        visitedPlaceIds = state.visitedPlaceIds.withSelection(
                            value = placeId,
                            selected = selected,
                        ),
                        actionErrorMessageResId = null,
                    )
                }

                is Failure -> mutableUiState.update {
                    it.copy(actionErrorMessageResId = R.string.feed_local_error)
                }

                is Loading<*> -> Unit
            }
        }
    }

    fun onTabSelected(tab: FeedTab) {
        mutableUiState.update { it.copy(selectedTab = tab) }
    }

    fun onFilterClicked() {
        mutableUiState.update { it.copy(isFilterDialogVisible = true) }
    }

    fun onFilterDialogDismissed() {
        mutableUiState.update { it.copy(isFilterDialogVisible = false) }
    }

    fun onOpenOnlyChanged(openOnly: Boolean) {
        mutableUiState.update { it.copy(openOnly = openOnly) }
    }

    private fun loadPlaces(query: String = mutableUiState.value.query) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            mutableUiState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    isStale = false,
                    errorMessageResId = null,
                )
            }

            try {
                when (val result = searchPlacesUseCase(query, searchLocation)) {
                    is Success -> {
                        val places = result.data.map { place ->
                            place.toUiModel(referenceLocation = searchLocation)
                        }
                        allPlaces = places
                        mutableUiState.update { state ->
                            state.copy(
                                places = places,
                                isLoading = false,
                                isError = false,
                                isStale = false,
                                errorMessageResId = null,
                                providerAttribution = places
                                    .firstOrNull()
                                    ?.providerAttribution,
                            )
                        }
                    }

                    is Failure -> mutableUiState.update {
                        it.copy(
                            isLoading = false,
                            isError = true,
                            isStale = it.places.isNotEmpty(),
                            errorMessageResId = R.string.feed_generic_error,
                        )
                    }

                    is Loading<*> -> mutableUiState.update {
                        it.copy(isLoading = true)
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                mutableUiState.update {
                    it.copy(
                        isLoading = false,
                        isError = true,
                        isStale = it.places.isNotEmpty(),
                        errorMessageResId = R.string.feed_generic_error,
                    )
                }
            } finally {
                if (isActive) {
                    mutableUiState.update { state ->
                        if (state.isLoading) state.copy(isLoading = false) else state
                    }
                }
            }
        }
    }

    private fun filterPlaces(
        places: List<FeedPlaceUiModel>,
        query: String,
    ): List<FeedPlaceUiModel> {
        val normalizedQuery = query.trim().lowercase()
        return if (normalizedQuery.isEmpty()) {
            places
        } else {
            places.filter { place ->
                place.searchableText.lowercase().contains(normalizedQuery)
            }
        }
    }

    private fun PlaceSummary.toUiModel(referenceLocation: GeoPoint?): FeedPlaceUiModel {
        val name = displayName?.takeIf(String::isNotBlank) ?: id
        val category = primaryTypeDisplayName?.takeIf(String::isNotBlank)
            ?: primaryType?.takeIf(String::isNotBlank)
        val searchableText = buildList {
            add(name)
            category?.let(::add)
            addAll(types)
        }.joinToString(" ").lowercase()

        return FeedPlaceUiModel(
            id = id,
            categoryText = category,
            nameText = name,
            distanceMeters = location?.let { placeLocation ->
                referenceLocation?.distanceTo(placeLocation)
            },
            rating = rating?.toFloat(),
            ratingsCount = userRatingCount,
            priceLevel = priceLevel,
            priceDescriptionResId = R.string.feed_price_level_description,
            status = businessStatus.toFeedStatus(),
            providerAttribution = providerAttribution.takeIf(String::isNotBlank),
            searchableText = searchableText,
        )
    }

    private fun BusinessStatus.toFeedStatus(): FeedPlaceStatus? = when (this) {
        // BUSINESS_STATUS only describes the business lifecycle. It does not
        // prove that the place is open at the moment of the feed request.
        BusinessStatus.OPERATIONAL -> null
        BusinessStatus.CLOSED_TEMPORARILY,
        BusinessStatus.CLOSED_PERMANENTLY,
        -> FeedPlaceStatus.CLOSED
        BusinessStatus.UNKNOWN -> null
    }

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

    private fun Set<String>.withSelection(value: String, selected: Boolean): Set<String> =
        if (selected) this + value else this - value

    private companion object {
        const val EARTH_RADIUS_METERS = 6_371_000.0
    }
}
