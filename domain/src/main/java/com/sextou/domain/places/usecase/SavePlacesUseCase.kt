package com.sextou.domain.places.usecase

import com.sextou.domain.Result
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.repository.PlacesRepository

open class SavePlacesUseCase(
    private val repository: PlacesRepository.Local,
) {
    open suspend operator fun invoke(places: List<PlaceSummary>): Result<Unit> =
        repository.saveMissing(places)
}
