package com.sextou.domain.places.repository

import com.sextou.domain.Result

interface AutomaticRefreshRepository {
    interface Local {
        /** Civil date encoded as yyyyMMdd; zero means no successful automatic refresh. */
        suspend fun lastSuccessDay(): Result<Long>
        suspend fun recordSuccessDay(day: Long): Result<Unit>
    }
}
