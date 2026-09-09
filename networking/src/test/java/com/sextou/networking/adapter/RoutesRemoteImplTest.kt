package com.sextou.networking.adapter

import com.sextou.domain.Failure
import com.sextou.domain.Success
import com.sextou.domain.places.model.GeoPoint
import com.sextou.networking.gateway.RoutesGateway
import com.sextou.networking.response.RoutePathResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RoutesRemoteImplTest {
    @Test
    fun `maps the gateway polyline to a domain route`() = runTest {
        val repository = RoutesRemoteImpl(
            FakeRoutesGateway(
                response = RoutePathResponse("_p~iF~ps|U_ulLnnqC_mqNvxq`@"),
            ),
        )

        val result = repository.calculateRoute(
            origin = GeoPoint(-22.9, -43.2),
            destination = GeoPoint(-22.91, -43.21),
        )

        assertTrue(result is Success)
        assertEquals(3, (result as Success).data.points.size)
    }

    @Test
    fun `translates gateway failures to a domain failure`() = runTest {
        val repository = RoutesRemoteImpl(
            FakeRoutesGateway(failure = IllegalStateException("route unavailable")),
        )

        val result = repository.calculateRoute(
            origin = GeoPoint(-22.9, -43.2),
            destination = GeoPoint(-22.91, -43.21),
        )

        assertTrue(result is Failure)
        assertEquals("route unavailable", (result as Failure).error?.message)
    }
}

private class FakeRoutesGateway(
    private val response: RoutePathResponse? = null,
    private val failure: Throwable? = null,
) : RoutesGateway {
    override suspend fun computeRoute(
        origin: GeoPoint,
        destination: GeoPoint,
    ): RoutePathResponse {
        failure?.let { throw it }
        return requireNotNull(response)
    }
}
