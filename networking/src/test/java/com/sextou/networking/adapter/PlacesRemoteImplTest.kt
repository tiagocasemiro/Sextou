package com.sextou.networking.adapter

import com.sextou.domain.Failure
import com.sextou.domain.Success
import com.sextou.domain.places.model.NearbySearchRequest
import com.sextou.domain.places.model.PlaceDetailsRequest
import com.sextou.domain.places.model.PlacePhotoReference
import com.sextou.domain.places.model.PlacePhotoRequest
import com.sextou.domain.places.model.PlaceTextSearchRequest
import com.sextou.networking.gateway.PlacesGateway
import com.sextou.networking.response.PlaceDetailsResponse
import com.sextou.networking.response.PlacePhotoResponse
import com.sextou.networking.response.PlaceSummaryResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlacesRemoteImplTest {
    @Test
    fun `maps gateway photo response to domain without exposing infrastructure types`() = runTest {
        val gateway = FakePlacesGateway(
            photoResponse = PlacePhotoResponse(
                uri = "https://example.invalid/photo",
                attributionHtml = "Google Maps",
                authors = emptyList(),
            ),
        )
        val repository = PlacesRemoteImpl(gateway)

        val result = repository.getPhoto(
            PlacePhotoRequest(
                reference = PlacePhotoReference(
                    placeId = "place-id",
                    index = 0,
                    width = 640,
                    height = 480,
                    attributionHtml = "Google Maps",
                    authors = emptyList(),
                    googleMapsUri = null,
                    flagContentUri = null,
                ),
            ),
        )

        assertTrue(result is Success)
        assertEquals("https://example.invalid/photo", (result as Success).data.uri)
    }

    @Test
    fun `translates infrastructure exception to domain failure`() = runTest {
        val repository = PlacesRemoteImpl(FakePlacesGateway(failure = IllegalStateException("offline")))

        val result = repository.getDetails(PlaceDetailsRequest("place-id"))

        assertTrue(result is Failure)
        assertEquals("offline", (result as Failure).error?.message)
    }
}

private class FakePlacesGateway(
    private val photoResponse: PlacePhotoResponse? = null,
    private val failure: Throwable? = null,
) : PlacesGateway {
    override suspend fun searchNearby(request: NearbySearchRequest): List<PlaceSummaryResponse> =
        error("Not used")

    override suspend fun searchByText(request: PlaceTextSearchRequest): List<PlaceSummaryResponse> =
        error("Not used")

    override suspend fun getDetails(request: PlaceDetailsRequest): PlaceDetailsResponse {
        failure?.let { throw it }
        error("Not used")
    }

    override suspend fun getPhoto(request: PlacePhotoRequest): PlacePhotoResponse {
        failure?.let { throw it }
        return requireNotNull(photoResponse)
    }
}
