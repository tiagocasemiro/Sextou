package com.sextou.features.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sextou.R
import com.sextou.domain.Failure
import com.sextou.domain.Loading
import com.sextou.domain.Success
import com.sextou.domain.favorites.usecase.ObserveFavoritesUseCase
import com.sextou.domain.places.model.BusinessStatus
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlacePhotoReference
import com.sextou.domain.places.model.PlaceStatus
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.usecase.GetPlacePhotoUseCase
import com.sextou.domain.places.usecase.LoadedPlacesUseCase
import com.sextou.domain.places.usecase.SetPlaceStatusUseCase
import com.sextou.domain.visits.usecase.ObserveVisitedPlacesUseCase
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
import java.util.ArrayDeque
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class FeedViewModel(
    private val loadedPlacesUseCase: LoadedPlacesUseCase,
    private val getPlacePhotoUseCase: GetPlacePhotoUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val observeVisitedPlacesUseCase: ObserveVisitedPlacesUseCase,
    private val setPlaceStatusUseCase: SetPlaceStatusUseCase,
    initialLocation: GeoPoint? = null,
) : ViewModel() {
    private var searchLocation: GeoPoint? = initialLocation
    private var allPlaces: List<FeedPlaceUiModel> = emptyList()
    private var searchJob: Job? = null
    private var photoJob: Job? = null
    private val resolvedPhotos = mutableMapOf<String, PlacePhoto>()
    private val photoReferences = mutableMapOf<String, PlacePhotoReference>()
    private val requestedPhotoPlaceIds = mutableSetOf<String>()
    private val photoQueue = ArrayDeque<PhotoRequest>()
    private var initialRefreshStarted = false
    private var opening = false

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
        loadedPlacesUseCase.places
            .onEach(::onSavedPlacesChanged)
            .catch { throwable ->
                if (throwable is CancellationException) throw throwable
                mutableUiState.update {
                    it.copy(actionErrorMessageResId = R.string.feed_local_error)
                }
            }
            .launchIn(viewModelScope)
        loadedPlacesUseCase.hasLocalFailure.onEach { failed ->
            if (failed) mutableUiState.update { it.copy(actionErrorMessageResId = R.string.feed_local_error) }
        }.launchIn(viewModelScope)
    }

    fun onScreenOpened() {
        if (opening) return
        opening = true
        initialRefreshStarted = false
        startInitialRefreshIfPossible()
    }

    fun onScreenClosed() {
        opening = false
        mutableUiState.update { it.copy(draftFilterOptions = null) }
    }

    fun onQueryChanged(query: String) {
        mutableUiState.update {
            it.copy(
                query = query,
                places = filterPlaces(allPlaces, query, it.confirmedFilterOptions),
                isError = false,
                isStale = false,
                errorMessageResId = null,
            )
        }
    }

    fun onLocationChanged(location: GeoPoint?) {
        searchLocation = location?.takeIf { it.latitude in -90.0..90.0 && it.longitude in -180.0..180.0 }
        onSavedPlacesChanged(loadedPlacesUseCase.places.value)
        startInitialRefreshIfPossible()
    }

    fun retry() {
        requestedPhotoPlaceIds.clear()
        photoQueue.clear()
        photoJob?.cancel()
        photoJob = null
        mutableUiState.update { state ->
            state.copy(photoRetryToken = state.photoRetryToken + 1)
        }
        loadPlaces()
    }

    fun requestPhoto(placeId: String) {
        if (placeId.isBlank() || resolvedPhotos[placeId]?.uri?.isNotBlank() == true) return

        val reference = photoReferences[placeId] ?: return
        if (!requestedPhotoPlaceIds.add(placeId)) return

        photoQueue.addLast(PhotoRequest(placeId = placeId, reference = reference))
        startPhotoJobIfNeeded()
    }

    fun onFavoriteClicked(placeId: String) {
        val selected = placeId !in mutableUiState.value.favoritePlaceIds
        viewModelScope.launch {
            when (setPlaceStatusUseCase(placeId, PlaceStatus.FAVORITE.takeIf { selected })) {
                is Success -> mutableUiState.update { state ->
                    state.copy(
                        favoritePlaceIds = state.favoritePlaceIds.withSelection(
                            value = placeId,
                            selected = selected,
                        ),
                        visitedPlaceIds = state.visitedPlaceIds - placeId,
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
            when (setPlaceStatusUseCase(placeId, PlaceStatus.VISITED.takeIf { selected })) {
                is Success -> mutableUiState.update { state ->
                    state.copy(
                        favoritePlaceIds = state.favoritePlaceIds - placeId,
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
        mutableUiState.update { state ->
            if (state.draftFilterOptions != null) {
                state
            } else {
                state.copy(
                    draftFilterOptions = state.confirmedFilterOptions.toSet(),
                )
            }
        }
    }

    fun onFilterDialogDismissed() {
        mutableUiState.update { it.copy(draftFilterOptions = null) }
    }

    fun onFilterOptionChanged(
        option: FeedFilterOption,
        selected: Boolean,
    ) {
        mutableUiState.update { state ->
            val draft = state.draftFilterOptions
                ?: return@update state

            state.copy(
                draftFilterOptions = if (selected) {
                    draft + option
                } else {
                    draft - option
                },
            )
        }
    }

    fun onFiltersApplied() {
        mutableUiState.update { state ->
            val draft = state.draftFilterOptions
                ?: return@update state

            state.copy(
                confirmedFilterOptions = draft.toSet(),
                places = filterPlaces(allPlaces, state.query, draft),
                draftFilterOptions = null,
            )
        }
    }

    private fun loadPlaces(automatic: Boolean = false) {
        val location = searchLocation ?: return
        if (searchJob?.isActive == true) return
        searchJob = viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true, isError = false, errorMessageResId = null) }
            val result = if (automatic) loadedPlacesUseCase.open(location)
                else loadedPlacesUseCase.searchManually(location, LoadedPlacesUseCase.AUTOMATIC_RADIUS_METERS)
            mutableUiState.update {
                it.copy(
                    isLoading = false,
                    isError = result is Failure,
                    isStale = result is Failure && it.places.isNotEmpty(),
                    errorMessageResId = R.string.feed_generic_error.takeIf { result is Failure },
                )
            }
        }
    }

    private fun startInitialRefreshIfPossible() {
        if (!opening || initialRefreshStarted || searchLocation == null) return
        initialRefreshStarted = true
        loadPlaces(automatic = true)
    }

    private fun onSavedPlacesChanged(places: List<PlaceSummary>) {
        rememberPhotoReferences(places)
        val mappedPlaces = places.map { place ->
            place.toUiModel(referenceLocation = searchLocation)
        }
        allPlaces = mappedPlaces
        mutableUiState.update { state ->
            state.copy(
                places = filterPlaces(mappedPlaces, state.query, state.confirmedFilterOptions),
                providerAttribution = mappedPlaces.firstOrNull()?.providerAttribution,
            )
        }
    }

    private fun rememberPhotoReferences(places: List<PlaceSummary>) {
        places.forEach { place ->
            if (place.id.isNotBlank()) {
                photoReferences[place.id] = place.photoReference()
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
                            val photo = result.data
                            if (photo.uri.isNotBlank() && isActive) {
                                resolvedPhotos[request.placeId] = photo
                                updateResolvedPhoto(request.placeId, photo)
                            }
                        }

                        is Failure,
                        is Loading<*>,
                        -> Unit
                    }
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (_: Exception) {
                    // A missing photo must not hide an establishment from the feed.
                }
            }
        }
    }

    private fun updateResolvedPhoto(placeId: String, photo: PlacePhoto) {
        allPlaces = allPlaces.map { place ->
            if (place.id == placeId) {
                place.copy(
                    photoUri = photo.uri,
                    photoAttribution = photo.toAttribution(),
                )
            } else {
                place
            }
        }
        mutableUiState.update { state ->
            state.copy(
                places = state.places.map { place ->
                    if (place.id == placeId) {
                        place.copy(
                            photoUri = photo.uri,
                            photoAttribution = photo.toAttribution(),
                        )
                    } else {
                        place
                    }
                },
            )
        }
    }

    private fun PlacePhoto.toAttribution(): String? =
        attributionHtml?.takeIf(String::isNotBlank)
            ?: authors.joinToString(", ") { it.name }.takeIf(String::isNotBlank)

    private fun PlaceSummary.photoReference() = photos.firstOrNull() ?: PlacePhotoReference(
        placeId = id,
        index = 0,
        width = 0,
        height = 0,
        attributionHtml = null,
        authors = emptyList(),
        googleMapsUri = null,
        flagContentUri = null,
    )

    private data class PhotoRequest(
        val placeId: String,
        val reference: PlacePhotoReference,
    )

    private fun filterPlaces(
        places: List<FeedPlaceUiModel>,
        query: String,
        selectedFilterOptions: Set<FeedFilterOption>,
    ): List<FeedPlaceUiModel> {
        val ids = loadedPlacesUseCase.filter(query).mapTo(hashSetOf()) { it.id }
        return places.filter { place ->
            place.id in ids && FeedPlaceTypeFilter.matches(
                place.placeTypes,
                selectedFilterOptions,
            )
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
        val placeTypes = buildSet {
            listOfNotNull(primaryType)
                .plus(types)
                .map { it.trim().lowercase(Locale.ROOT) }
                .filter { it.isNotBlank() }
                .forEach(::add)
        }

        return FeedPlaceUiModel(
            id = id,
            categoryText = category,
            nameText = name,
            distanceMeters = location?.let { placeLocation ->
                referenceLocation?.distanceTo(placeLocation)
            },
            address = formattedAddress,
            location = location,
            googleMapsUri = googleMapsUri,
            rating = rating?.toFloat(),
            ratingsCount = userRatingCount,
            priceLevel = priceLevel,
            priceDescriptionResId = R.string.feed_price_level_description,
            status = businessStatus.toFeedStatus(),
            providerAttribution = providerAttribution.takeIf(String::isNotBlank),
            photoUri = resolvedPhotos[id]?.uri,
            photoAttribution = resolvedPhotos[id]?.toAttribution(),
            placeTypes = placeTypes,
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
