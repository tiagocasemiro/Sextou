package com.sextou.domain.ignored.usecase

import com.sextou.domain.ignored.repository.IgnoredPlaceRepository
import kotlinx.coroutines.flow.Flow

class ObserveIgnoredPlacesUseCase(
    private val repository: IgnoredPlaceRepository.Local,
) {
    operator fun invoke(): Flow<Set<String>> = repository.observeIds()
}
