package com.sextou.domain.visits.usecase

import com.sextou.domain.visits.repository.VisitRepository
import kotlinx.coroutines.flow.Flow

class ObserveVisitedPlacesUseCase(
    private val repository: VisitRepository.Local,
) {
    operator fun invoke(): Flow<Set<String>> = repository.observeIds()
}
