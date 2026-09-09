package com.sextou.networking.response

import org.junit.Assert.assertEquals
import org.junit.Test

class RoutePathResponseTest {
    @Test
    fun `decodes the encoded route polyline into domain coordinates`() {
        val route = RoutePathResponse(
            encodedPolyline = "_p~iF~ps|U_ulLnnqC_mqNvxq`@",
        ).mapToDomain()

        assertEquals(3, route.points.size)
        assertPoint(route.points[0], 38.5, -120.2)
        assertPoint(route.points[1], 40.7, -120.95)
        assertPoint(route.points[2], 43.252, -126.453)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects an empty encoded route polyline`() {
        RoutePathResponse(encodedPolyline = "").mapToDomain()
    }

    private fun assertPoint(
        point: com.sextou.domain.places.model.GeoPoint,
        expectedLatitude: Double,
        expectedLongitude: Double,
    ) {
        assertEquals(expectedLatitude, point.latitude, COORDINATE_TOLERANCE)
        assertEquals(expectedLongitude, point.longitude, COORDINATE_TOLERANCE)
    }

    private companion object {
        const val COORDINATE_TOLERANCE = 0.0001
    }
}
