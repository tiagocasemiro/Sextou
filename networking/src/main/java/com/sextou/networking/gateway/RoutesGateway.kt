package com.sextou.networking.gateway

import com.sextou.domain.places.model.GeoPoint
import com.sextou.networking.response.RoutePathResponse

interface RoutesGateway {
    suspend fun computeRoute(
        origin: GeoPoint,
        destination: GeoPoint,
    ): RoutePathResponse
}
