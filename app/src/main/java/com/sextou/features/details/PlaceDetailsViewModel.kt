package com.sextou.features.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sextou.domain.Failure
import com.sextou.domain.Loading
import com.sextou.domain.Success
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.usecase.GetPlaceDetailsUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaceDetailsViewModel(
    private val getPlaceDetailsUseCase: GetPlaceDetailsUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(PlaceDetailsUiState())
    private var loadedPlaceId: String? = null

    val uiState: StateFlow<PlaceDetailsUiState> = mutableUiState.asStateFlow()

    fun load(placeId: String) {
        if (loadedPlaceId == placeId && (mutableUiState.value.isLoading || mutableUiState.value.place != null)) {
            return
        }
        loadedPlaceId = placeId
        viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true, isError = false) }
            try {
                when (val result = getPlaceDetailsUseCase(placeId)) {
                    is Success -> mutableUiState.update {
                        it.copy(
                            isLoading = false,
                            isError = false,
                            place = result.data.toUiModel(),
                        )
                    }

                    is Failure -> mutableUiState.update {
                        it.copy(isLoading = false, isError = true, place = null)
                    }

                    is Loading<*> -> mutableUiState.update {
                        it.copy(isLoading = true)
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                mutableUiState.update {
                    it.copy(isLoading = false, isError = true, place = null)
                }
            }
        }
    }

    private fun PlaceDetails.toUiModel(): PlaceDetailsUiModel = PlaceDetailsUiModel(
        name = displayName?.takeIf(String::isNotBlank) ?: id,
        address = shortFormattedAddress ?: formattedAddress,
        phone = nationalPhoneNumber ?: internationalPhoneNumber,
        website = websiteUri,
        summary = editorialSummary?.text ?: generativeSummary?.overview,
        hours = (currentOpeningHours ?: openingHours)?.weekdayText.orEmpty(),
        rating = rating,
        ratingsCount = userRatingCount,
        providerAttribution = providerAttribution,
    )
}
