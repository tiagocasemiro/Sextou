package com.sextou.domain.routes.model

import com.sextou.domain.places.model.GeoPoint

data class RoutePath(
    val points: List<GeoPoint>,
)
