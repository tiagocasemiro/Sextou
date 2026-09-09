package com.sextou.domain.routes.repository

import com.sextou.domain.Result
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.routes.model.RoutePath

interface RouteRepository {
    interface Remote {
        suspend fun calculateRoute(
            origin: GeoPoint,
            destination: GeoPoint,
        ): Result<RoutePath>
    }
}
