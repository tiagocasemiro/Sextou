package com.sextou.domain.places.repository

import com.sextou.domain.Result
import com.sextou.domain.places.model.PlaceStatus

interface PlaceStatusRepository {
    interface Local {
        suspend fun setStatus(placeId: String, status: PlaceStatus?): Result<Unit>
    }
}
