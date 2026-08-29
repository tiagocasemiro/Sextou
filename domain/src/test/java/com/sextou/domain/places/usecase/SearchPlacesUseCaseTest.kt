package com.sextou.domain.places.usecase

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.places.model.BusinessStatus
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.NearbySearchRequest
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.model.PlaceDetailsRequest
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlacePhotoRequest
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.model.PlaceTextSearchRequest
import com.sextou.domain.places.repository.PlacesRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchPlacesUseCaseTest {
    private lateinit var repository: RecordingPlacesRepository
    private lateinit var useCase: SearchPlacesUseCase

    @Before
    fun setUp() {
        repository = RecordingPlacesRepository()
        useCase = SearchPlacesUseCase(repository)
    }

    @Test
    fun `uses nearby search and feed criteria when query is blank and location exists`() = runTest {
        useCase(
            query = "   ",
            location = GeoPoint(latitude = -22.9, longitude = -43.2),
        )

        val request = repository.nearbyRequest
        assertNotNull(request)
        assertEquals(5_000.0, request?.radiusMeters)
        assertEquals(20, request?.maxResults)
        assertEquals("BR", request?.regionCode)
        assertEquals(false, request?.includePhotos)
        assertTrue("bar" in request!!.includedTypes)
        assertTrue("restaurant" in request.includedTypes)
        assertNull(repository.textRequest)
    }

    @Test
    fun `uses text search with a trimmed query and location bias`() = runTest {
        useCase(
            query = "  espetinho  ",
            location = GeoPoint(latitude = -22.9, longitude = -43.2),
        )

        val request = repository.textRequest
        assertNotNull(request)
        assertEquals("espetinho", request?.query)
        assertEquals(GeoPoint(-22.9, -43.2), request?.locationBiasCenter)
        assertEquals(5_000.0, request?.locationBiasRadiusMeters)
        assertEquals(false, request?.includePhotos)
        assertNull(repository.nearbyRequest)
    }

    @Test
    fun `includes photo metadata when explicitly requested for nearby search`() = runTest {
        useCase(
            query = "",
            location = GeoPoint(latitude = -22.9, longitude = -43.2),
            includePhotos = true,
        )

        assertEquals(true, repository.nearbyRequest?.includePhotos)
    }

    @Test
    fun `includes photo metadata when explicitly requested for text search`() = runTest {
        useCase(
            query = "bar",
            location = null,
            includePhotos = true,
        )

        assertEquals(true, repository.textRequest?.includePhotos)
    }

    @Test
    fun `falls back to a non-empty text search when location is unavailable`() = runTest {
        useCase(query = "", location = null)

        val request = repository.textRequest
        assertNotNull(request)
        assertTrue(request!!.query.isNotBlank())
        assertNull(request.locationBiasCenter)
        assertNull(request.locationBiasRadiusMeters)
        assertNull(repository.nearbyRequest)
    }

    @Test
    fun `preserves repository failures`() = runTest {
        val expected = Failure(
            Error(
                code = 503,
                title = "Unavailable",
                message = "Try again",
            ),
        )
        repository.textResult = expected

        val result = useCase(query = "bar", location = null)

        assertEquals(expected, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects coordinates outside the geographic bounds`() = runTest {
        useCase(
            query = "",
            location = GeoPoint(latitude = 91.0, longitude = 0.0),
        )
    }

    @Test
    fun `removes empty and duplicated place ids from successful results`() = runTest {
        repository.nearbyResult = Success(
            listOf(
                place(id = ""),
                place(id = "place-1"),
                place(id = "place-1"),
            ),
        )

        val result = useCase(query = "", location = GeoPoint(0.0, 0.0))

        assertEquals(
            listOf("place-1"),
            (result as Success).data.map(PlaceSummary::id),
        )
    }
}

private class RecordingPlacesRepository : PlacesRepository.Remote {
    var nearbyRequest: NearbySearchRequest? = null
    var textRequest: PlaceTextSearchRequest? = null
    var nearbyResult: Result<List<PlaceSummary>> = Success(emptyList())
    var textResult: Result<List<PlaceSummary>> = Success(emptyList())

    override suspend fun searchNearby(request: NearbySearchRequest): Result<List<PlaceSummary>> {
        nearbyRequest = request
        return nearbyResult
    }

    override suspend fun searchByText(request: PlaceTextSearchRequest): Result<List<PlaceSummary>> {
        textRequest = request
        return textResult
    }

    override suspend fun getDetails(request: PlaceDetailsRequest): Result<PlaceDetails> =
        error("Not used")

    override suspend fun getPhoto(request: PlacePhotoRequest): Result<PlacePhoto> =
        error("Not used")
}

private fun place(id: String) = PlaceSummary(
    id = id,
    displayName = "Place $id",
    formattedAddress = null,
    location = null,
    primaryType = "bar",
    primaryTypeDisplayName = "Bar",
    types = listOf("bar"),
    businessStatus = BusinessStatus.OPERATIONAL,
    rating = null,
    userRatingCount = null,
    priceLevel = null,
    googleMapsUri = null,
    providerAttribution = "Google Maps",
)
