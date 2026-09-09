package com.sextou.domain.places.usecase

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.places.model.PlaceStatus
import com.sextou.domain.places.repository.PlaceStatusRepository

class SetPlaceStatusUseCase(
    private val repository: PlaceStatusRepository.Local,
) {
    suspend operator fun invoke(
        placeId: String,
        status: PlaceStatus?,
    ): Result<Unit> {
        if (placeId.isBlank()) {
            return Failure(Error(message = INVALID_PLACE_ID_MESSAGE))
        }
        return repository.setStatus(placeId, status)
    }

    private companion object {
        const val INVALID_PLACE_ID_MESSAGE = "O identificador do estabelecimento é obrigatório."
    }
}
