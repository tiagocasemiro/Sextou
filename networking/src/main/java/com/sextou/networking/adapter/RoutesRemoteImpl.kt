package com.sextou.networking.adapter

import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.routes.model.RoutePath
import com.sextou.domain.routes.repository.RouteRepository
import com.sextou.networking.gateway.RoutesGateway
import com.sextou.repository.fetchData
import com.sextou.repository.mapRoutesError

class RoutesRemoteImpl(
    private val gateway: RoutesGateway,
) : RouteRepository.Remote {
    override suspend fun calculateRoute(
        origin: GeoPoint,
        destination: GeoPoint,
    ): Result<RoutePath> = fetchData(::mapRoutesError) {
        Success(gateway.computeRoute(origin, destination).mapToDomain())
    }
}
