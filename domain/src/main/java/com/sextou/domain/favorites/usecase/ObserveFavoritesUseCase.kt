package com.sextou.domain.favorites.usecase

import com.sextou.domain.favorites.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoritesUseCase(
    private val repository: FavoriteRepository.Local,
) {
    operator fun invoke(): Flow<Set<String>> = repository.observeIds()
}
