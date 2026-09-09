package com.sextou.domain.routes.usecase

import com.sextou.domain.Failure
import com.sextou.domain.Success
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.routes.model.RoutePath
import com.sextou.domain.routes.repository.RouteRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetRouteUseCaseTest {
    @Test
    fun `calculates a route through the remote repository`() = runTest {
        val origin = GeoPoint(-22.9, -43.2)
        val destination = GeoPoint(-22.91, -43.21)
        val expected = RoutePath(listOf(origin, destination))
        val repository = FakeRouteRepository(Success(expected))

        val result = GetRouteUseCase(repository)(origin, destination)

        assertEquals(Success(expected), result)
        assertEquals(origin to destination, repository.lastRequest)
    }

    @Test
    fun `does not call remote repository for invalid coordinates`() = runTest {
        val repository = FakeRouteRepository(Success(RoutePath(emptyList())))

        val result = GetRouteUseCase(repository)(
            origin = GeoPoint(91.0, -43.2),
            destination = GeoPoint(-22.91, -43.21),
        )

        assertTrue(result is Failure)
        assertEquals(null, repository.lastRequest)
    }

    @Test
    fun `returns a local path when origin and destination are the same`() = runTest {
        val point = GeoPoint(-22.9, -43.2)
        val repository = FakeRouteRepository(Success(RoutePath(emptyList())))

        val result = GetRouteUseCase(repository)(point, point)

        assertEquals(Success(RoutePath(listOf(point, point))), result)
        assertEquals(null, repository.lastRequest)
    }
}

private class FakeRouteRepository(
    private val result: com.sextou.domain.Result<RoutePath>,
) : RouteRepository.Remote {
    var lastRequest: Pair<GeoPoint, GeoPoint>? = null

    override suspend fun calculateRoute(
        origin: GeoPoint,
        destination: GeoPoint,
    ): com.sextou.domain.Result<RoutePath> {
        lastRequest = origin to destination
        return result
    }
}
