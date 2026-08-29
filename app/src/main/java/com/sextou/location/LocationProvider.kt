package com.sextou.location

import com.sextou.domain.places.model.GeoPoint

interface LocationProvider {
    suspend fun getCurrentLocation(): GeoPoint?
}
