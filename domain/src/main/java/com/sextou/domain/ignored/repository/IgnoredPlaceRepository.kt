package com.sextou.domain.ignored.repository

import com.sextou.domain.Result
import kotlinx.coroutines.flow.Flow

interface IgnoredPlaceRepository {
    interface Local {
        fun observeIds(): Flow<Set<String>>

        suspend fun setSelected(
            placeId: String,
            selected: Boolean,
        ): Result<Unit>
    }
}
