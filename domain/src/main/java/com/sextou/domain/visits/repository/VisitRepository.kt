package com.sextou.domain.visits.repository

import com.sextou.domain.Result
import kotlinx.coroutines.flow.Flow

interface VisitRepository {
    interface Local {
        fun observeIds(): Flow<Set<String>>

        suspend fun setSelected(
            placeId: String,
            selected: Boolean,
        ): Result<Unit>
    }
}
