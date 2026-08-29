package com.sextou.domain.places.usecase

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.model.PlaceDetailsRequest
import com.sextou.domain.places.repository.PlacesRepository

class GetPlaceDetailsUseCase(
    private val repository: PlacesRepository.Remote,
) {
    suspend operator fun invoke(placeId: String): Result<PlaceDetails> {
        if (placeId.isBlank()) {
            return Failure(Error(message = INVALID_PLACE_ID_MESSAGE))
        }
        return repository.getDetails(
            PlaceDetailsRequest(
                placeId = placeId,
                regionCode = REGION_CODE,
            ),
        )
    }

    private companion object {
        const val INVALID_PLACE_ID_MESSAGE = "O identificador do estabelecimento é obrigatório."
        const val REGION_CODE = "BR"
    }
}
