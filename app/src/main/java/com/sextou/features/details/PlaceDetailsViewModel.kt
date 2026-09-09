package com.sextou.features.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sextou.domain.Failure
import com.sextou.domain.Loading
import com.sextou.domain.Success
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.model.PlaceOpeningHours
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.usecase.GetPlaceDetailsUseCase
import com.sextou.domain.places.usecase.GetPlacePhotoUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaceDetailsViewModel(
    private val getPlaceDetailsUseCase: GetPlaceDetailsUseCase,
    private val getPlacePhotoUseCase: GetPlacePhotoUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(PlaceDetailsUiState())
    private var loadedPlaceId: String? = null
    private var pendingFallback: PlaceDetailsFallback? = null
    private var loadJob: Job? = null

    val uiState: StateFlow<PlaceDetailsUiState> = mutableUiState.asStateFlow()

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
                            place = result.data.toUiModel(),
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

    private fun PlaceDetails.toUiModel(): PlaceDetailsUiModel {
        val availableHours = currentOpeningHours ?: openingHours
        return PlaceDetailsUiModel(
            name = displayName?.takeIf(String::isNotBlank) ?: id,
            category = primaryTypeDisplayName?.takeIf(String::isNotBlank)
                ?: primaryType?.takeIf(String::isNotBlank),
            address = shortFormattedAddress ?: formattedAddress,
            phone = nationalPhoneNumber ?: internationalPhoneNumber,
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
        photoUri = photoUri,
        photoAttribution = photoAttribution,
        photoCount = if (photoUri != null) 1 else 0,
        menuUri = googleMapsUri,
    )

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
        authors.joinToString(", ") { it.name }.takeIf(String::isNotBlank)

    private companion object {
        const val MAX_VISIBLE_HOURS_ROWS = 3
    }
}
