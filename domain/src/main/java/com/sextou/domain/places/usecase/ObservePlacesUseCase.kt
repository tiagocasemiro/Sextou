package com.sextou.domain.places.usecase

import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.repository.PlacesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class ObservePlacesUseCase(
    private val repository: PlacesRepository.Local,
) {
    operator fun invoke(): Flow<List<PlaceSummary>> =
        repository.observeAll().distinctUntilChanged()
}
