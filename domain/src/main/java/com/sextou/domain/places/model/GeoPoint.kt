package com.sextou.domain.places.model

data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
)

data class GeoBounds(
    val southWest: GeoPoint,
    val northEast: GeoPoint,
)
