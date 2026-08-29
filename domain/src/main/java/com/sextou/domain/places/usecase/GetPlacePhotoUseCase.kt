package com.sextou.domain.places.usecase

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlacePhotoReference
import com.sextou.domain.places.model.PlacePhotoRequest
import com.sextou.domain.places.repository.PlacesRepository

class GetPlacePhotoUseCase(
    private val repository: PlacesRepository.Remote,
) {
    suspend operator fun invoke(reference: PlacePhotoReference): Result<PlacePhoto> {
        if (reference.placeId.isBlank()) {
            return Failure(Error(message = INVALID_PLACE_ID_MESSAGE))
        }
        if (reference.index < 0) {
            return Failure(Error(message = INVALID_PHOTO_INDEX_MESSAGE))
        }

        return repository.getPhoto(
            PlacePhotoRequest(
                reference = reference,
                maxWidth = PHOTO_MAX_WIDTH,
                maxHeight = PHOTO_MAX_HEIGHT,
            ),
        )
    }

    private companion object {
        const val INVALID_PLACE_ID_MESSAGE = "O identificador do estabelecimento é obrigatório."
        const val INVALID_PHOTO_INDEX_MESSAGE = "O índice da foto não pode ser negativo."
        const val PHOTO_MAX_WIDTH = 640
        const val PHOTO_MAX_HEIGHT = 320
    }
}
