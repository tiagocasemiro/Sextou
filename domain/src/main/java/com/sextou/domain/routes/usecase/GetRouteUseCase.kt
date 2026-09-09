package com.sextou.domain.routes.usecase

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.routes.model.RoutePath
import com.sextou.domain.routes.repository.RouteRepository

class GetRouteUseCase(
    private val repository: RouteRepository.Remote,
) {
    suspend operator fun invoke(
        origin: GeoPoint,
        destination: GeoPoint,
    ): Result<RoutePath> {
        if (!origin.isValid()) {
            return Failure(Error(message = INVALID_ORIGIN_MESSAGE))
        }
        if (!destination.isValid()) {
            return Failure(Error(message = INVALID_DESTINATION_MESSAGE))
        }
        if (origin == destination) {
            return Success(RoutePath(points = listOf(origin, destination)))
        }
        return repository.calculateRoute(origin, destination)
    }

    private fun GeoPoint.isValid(): Boolean =
        latitude in -90.0..90.0 && longitude in -180.0..180.0

    private companion object {
        const val INVALID_ORIGIN_MESSAGE = "A localização atual não é válida."
        const val INVALID_DESTINATION_MESSAGE = "A localização do estabelecimento não é válida."
    }
}
