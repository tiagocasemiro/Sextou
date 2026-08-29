package com.sextou.domain.favorites.repository

import com.sextou.domain.Result
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    interface Local {
        fun observeIds(): Flow<Set<String>>

        suspend fun setSelected(
            placeId: String,
            selected: Boolean,
        ): Result<Unit>
    }
}
