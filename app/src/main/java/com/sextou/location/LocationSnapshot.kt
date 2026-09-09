package com.sextou.location

import com.sextou.domain.places.model.GeoPoint

data class LocationSnapshot(
    val point: GeoPoint,
    val bearingDegrees: Float? = null,
)
