package com.sextou.domain.places.usecase

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Loading
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.model.PlaceDetailsRequest
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.repository.PlacesRepository

class GetPlaceDetailsUseCase(
    private val repository: PlacesRepository.Remote,
    private val localRepository: PlacesRepository.Local,
) {
    suspend operator fun invoke(placeId: String): Result<PlaceDetails> {
        if (placeId.isBlank()) {
            return Failure(Error(message = INVALID_PLACE_ID_MESSAGE))
        }
        return when (val result = repository.getDetails(
            PlaceDetailsRequest(
                placeId = placeId,
                regionCode = REGION_CODE,
            ),
        )) {
            is Success -> when (val saveResult = localRepository.saveAll(listOf(result.data.toSummary()))) {
                is Success -> result
                is Failure -> Failure(saveResult.error)
                is Loading<*> -> result
            }

            is Failure -> result
            is Loading<*> -> result
        }
    }

    private fun PlaceDetails.toSummary() = PlaceSummary(
        id = id,
        displayName = displayName,
        formattedAddress = formattedAddress,
        location = location,
        primaryType = primaryType,
        primaryTypeDisplayName = primaryTypeDisplayName,
        types = types,
        businessStatus = businessStatus,
        rating = rating,
        userRatingCount = userRatingCount,
        priceLevel = priceLevel,
        googleMapsUri = googleMapsUri,
        providerAttribution = providerAttribution,
        photos = photos,
    )

    private companion object {
        const val INVALID_PLACE_ID_MESSAGE = "O identificador do estabelecimento é obrigatório."
        const val REGION_CODE = "BR"
    }
}
