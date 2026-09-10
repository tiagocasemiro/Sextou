package com.sextou.features.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sextou.domain.Failure
import com.sextou.domain.Loading
import com.sextou.domain.Success
import com.sextou.domain.favorites.usecase.ObserveFavoritesUseCase
import com.sextou.domain.ignored.usecase.ObserveIgnoredPlacesUseCase
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.model.PlaceOpeningHours
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlaceStatus
import com.sextou.domain.places.usecase.GetPlaceDetailsUseCase
import com.sextou.domain.places.usecase.GetPlacePhotoUseCase
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
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class PlaceDetailsViewModel(
    private val getPlaceDetailsUseCase: GetPlaceDetailsUseCase,
    private val getPlacePhotoUseCase: GetPlacePhotoUseCase,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val observeVisitedPlacesUseCase: ObserveVisitedPlacesUseCase,
    private val observeIgnoredPlacesUseCase: ObserveIgnoredPlacesUseCase,
    private val setPlaceStatusUseCase: SetPlaceStatusUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(PlaceDetailsUiState())
    private var activePlaceId: String? = null
    private var favoritePlaceIds: Set<String> = emptySet()
    private var visitedPlaceIds: Set<String> = emptySet()
    private var ignoredPlaceIds: Set<String> = emptySet()
    private var searchLocation: GeoPoint? = null
    private var loadedPlaceId: String? = null
    private var pendingFallback: PlaceDetailsFallback? = null
    private var loadJob: Job? = null

    val uiState: StateFlow<PlaceDetailsUiState> = mutableUiState.asStateFlow()

    fun onLocationChanged(location: GeoPoint?) {
        if (searchLocation == location) return

        searchLocation = location
        mutableUiState.update { state ->
            val place = state.place ?: return@update state
            val distanceMeters = location?.let { referenceLocation ->
                place.location?.let { placeLocation ->
                    referenceLocation.distanceTo(placeLocation)
                }
            }
            state.copy(
                place = place.copy(distanceMeters = distanceMeters ?: place.distanceMeters),
            )
        }
    }

    init {
        observeFavoritesUseCase()
            .onEach { placeIds ->
                favoritePlaceIds = placeIds
                updateSelectionState()
            }
            .catch { throwable -> ignoreSelectionObservationFailure(throwable) }
            .launchIn(viewModelScope)
        observeVisitedPlacesUseCase()
            .onEach { placeIds ->
                visitedPlaceIds = placeIds
                updateSelectionState()
            }
            .catch { throwable -> ignoreSelectionObservationFailure(throwable) }
            .launchIn(viewModelScope)
        observeIgnoredPlacesUseCase()
            .onEach { placeIds ->
                ignoredPlaceIds = placeIds
                updateSelectionState()
            }
            .catch { throwable -> ignoreSelectionObservationFailure(throwable) }
            .launchIn(viewModelScope)
    }

    fun setFallback(fallback: PlaceDetailsFallback?) {
        pendingFallback = fallback
        if (fallback != null && loadedPlaceId != fallback.id) {
            mutableUiState.update {
                it.copy(
                    isLoading = false,
                    isError = false,
                    place = fallback.toUiModel(),
                )
            }
        }
    }

    fun load(placeId: String) {
        activePlaceId = placeId
        updateSelectionState()
        val fallback = pendingFallback?.takeIf { it.id == placeId }
        pendingFallback = null
        if (loadedPlaceId == placeId && (mutableUiState.value.isLoading || mutableUiState.value.place != null)) {
            return
        }
        loadedPlaceId = placeId
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val fallbackUiModel = fallback?.toUiModel()
            mutableUiState.update {
                it.copy(
                    isLoading = true,
                    isError = false,
                    place = fallbackUiModel,
                )
            }
            try {
                when (val result = getPlaceDetailsUseCase(placeId)) {
                    is Success -> mutableUiState.update {
                        it.copy(
                            isLoading = false,
                            isError = false,
                            place = result.data.toUiModel().mergeWithFallback(fallbackUiModel),
                        )
                    }.also {
                        result.data.photos.firstOrNull()?.let { reference ->
                            loadPhoto(reference)
                        }
                    }

                    is Failure -> mutableUiState.update {
                        it.copy(
                            isLoading = false,
                            isError = fallbackUiModel == null,
                            place = fallbackUiModel,
                        )
                    }

                    is Loading<*> -> mutableUiState.update {
                        it.copy(isLoading = true)
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                mutableUiState.update {
                    it.copy(
                        isLoading = false,
                        isError = fallbackUiModel == null,
                        place = fallbackUiModel,
                    )
                }
            }
        }
    }

    fun onFavoriteClicked() {
        onStatusClicked(PlaceStatus.FAVORITE)
    }

    fun onVisitClicked() {
        onStatusClicked(PlaceStatus.VISITED)
    }

    fun onIgnoreClicked() {
        onStatusClicked(PlaceStatus.IGNORED)
    }

    private fun PlaceDetails.toUiModel(): PlaceDetailsUiModel {
        val availableHours = currentOpeningHours ?: openingHours
        return PlaceDetailsUiModel(
            name = displayName?.takeIf(String::isNotBlank) ?: id,
            category = primaryTypeDisplayName?.takeIf(String::isNotBlank)
                ?: primaryType?.takeIf(String::isNotBlank),
            address = shortFormattedAddress ?: formattedAddress,
            phone = nationalPhoneNumber?.takeIf(String::isNotBlank)
                ?: internationalPhoneNumber?.takeIf(String::isNotBlank),
            website = websiteUri,
            summary = editorialSummary?.text ?: generativeSummary?.overview,
            hours = availableHours?.weekdayText.orEmpty(),
            hoursSummary = availableHours?.weekdayText?.firstOrNull(),
            hoursSchedule = availableHours?.toUiModel(),
            rating = rating,
            ratingsCount = userRatingCount,
            providerAttribution = providerAttribution,
            location = location,
            priceLevel = priceLevel,
            distanceMeters = location?.let { placeLocation ->
                searchLocation?.distanceTo(placeLocation)
            },
            photoCount = photos.size,
            menuUri = websiteUri ?: googleMapsUri,
        )
    }

    private fun PlaceDetailsFallback.toUiModel() = PlaceDetailsUiModel(
        name = name,
        category = category,
        address = address,
        phone = null,
        website = null,
        summary = null,
        hours = emptyList(),
        rating = rating,
        ratingsCount = ratingsCount,
        providerAttribution = providerAttribution,
        location = location,
        priceLevel = priceLevel,
        distanceMeters = location?.let { placeLocation ->
            searchLocation?.distanceTo(placeLocation)
        } ?: distanceMeters,
        photoUri = photoUri,
        photoAttribution = photoAttribution,
        photoCount = if (photoUri != null) 1 else 0,
        menuUri = googleMapsUri,
    )

    private fun PlaceDetailsUiModel.mergeWithFallback(
        fallback: PlaceDetailsUiModel?,
    ): PlaceDetailsUiModel = if (fallback == null) {
        this
    } else {
        copy(
            location = location ?: fallback.location,
            distanceMeters = distanceMeters ?: fallback.distanceMeters,
            photoUri = photoUri ?: fallback.photoUri,
            photoAttribution = photoAttribution ?: fallback.photoAttribution,
            photoCount = photoCount.takeIf { it > 0 } ?: fallback.photoCount,
        )
    }

    private fun PlaceOpeningHours.toUiModel(): PlaceDetailsHoursScheduleUiModel? {
        val rows = weekdayText
            .mapNotNull { text -> text.toHoursRow() }
            .take(MAX_VISIBLE_HOURS_ROWS)
        return rows.takeIf { it.isNotEmpty() }?.let { visibleRows ->
            PlaceDetailsHoursScheduleUiModel(rows = visibleRows)
        }
    }

    private fun String.toHoursRow(): PlaceDetailsHoursRowUiModel? {
        val separatorIndex = indexOf(':')
        if (separatorIndex <= 0) return null

        val day = substring(0, separatorIndex).trim()
        val time = substring(separatorIndex + 1).trim()
        if (day.isBlank() || time.isBlank()) return null

        return PlaceDetailsHoursRowUiModel(
            day = day,
            time = time,
            status = if (time.isClosedHours()) {
                PlaceDetailsHoursRowStatus.UNAVAILABLE
            } else {
                PlaceDetailsHoursRowStatus.HIDDEN
            },
        )
    }

    private fun String.isClosedHours(): Boolean =
        contains("fechado", ignoreCase = true) || contains("closed", ignoreCase = true)

    private suspend fun loadPhoto(reference: com.sextou.domain.places.model.PlacePhotoReference) {
        val photo = try {
            when (val result = getPlacePhotoUseCase(reference)) {
                is Success -> result.data
                is Failure,
                is Loading<*>,
                -> null
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            null
        }

        val uri = photo?.uri?.takeIf(String::isNotBlank) ?: return
        mutableUiState.update { state ->
            state.copy(
                place = state.place?.copy(
                    photoUri = uri,
                    photoAttribution = photo.toAttribution(),
                ),
            )
        }
    }

    private fun PlacePhoto.toAttribution(): String? =
        attributionHtml?.takeIf(String::isNotBlank)
            ?: authors.joinToString(", ") { it.name }.takeIf(String::isNotBlank)

    private fun onStatusClicked(status: PlaceStatus) {
        val placeId = activePlaceId ?: return
        val isSelected = when (status) {
            PlaceStatus.FAVORITE -> placeId in favoritePlaceIds
            PlaceStatus.VISITED -> placeId in visitedPlaceIds
            PlaceStatus.IGNORED -> placeId in ignoredPlaceIds
        }
        val nextStatus = status.takeUnless { isSelected }
        viewModelScope.launch {
            try {
                when (setPlaceStatusUseCase(placeId, nextStatus)) {
                    is Success -> {
                        favoritePlaceIds = favoritePlaceIds.withSelection(
                            placeId = placeId,
                            selected = nextStatus == PlaceStatus.FAVORITE,
                        )
                        visitedPlaceIds = visitedPlaceIds.withSelection(
                            placeId = placeId,
                            selected = nextStatus == PlaceStatus.VISITED,
                        )
                        ignoredPlaceIds = ignoredPlaceIds.withSelection(
                            placeId = placeId,
                            selected = nextStatus == PlaceStatus.IGNORED,
                        )
                        updateSelectionState()
                    }

                    is Failure,
                    is Loading<*>,
                    -> Unit
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                // The local use cases return Failure for expected persistence errors.
            }
        }
    }

    private fun updateSelectionState() {
        val placeId = activePlaceId
        mutableUiState.update { state ->
            state.copy(
                isFavorite = placeId != null && placeId in favoritePlaceIds,
                isVisited = placeId != null && placeId in visitedPlaceIds,
                isIgnored = placeId != null && placeId in ignoredPlaceIds,
            )
        }
    }

    private fun ignoreSelectionObservationFailure(throwable: Throwable) {
        if (throwable is CancellationException) throw throwable
    }

    private fun Set<String>.withSelection(placeId: String?, selected: Boolean): Set<String> {
        if (placeId == null) return this
        return if (selected) this + placeId else this - placeId
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

    private companion object {
        const val MAX_VISIBLE_HOURS_ROWS = 3
        const val EARTH_RADIUS_METERS = 6_371_000.0
    }
}
