package com.sextou.domain.visits.usecase

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.visits.repository.VisitRepository

class ToggleVisitedPlaceUseCase(
    private val repository: VisitRepository.Local,
) {
    suspend operator fun invoke(
        placeId: String,
        selected: Boolean,
    ): Result<Unit> {
        if (placeId.isBlank()) {
            return Failure(Error(message = INVALID_PLACE_ID_MESSAGE))
        }
        return repository.setSelected(placeId, selected)
    }

    private companion object {
        const val INVALID_PLACE_ID_MESSAGE = "O identificador do estabelecimento é obrigatório."
    }
}
