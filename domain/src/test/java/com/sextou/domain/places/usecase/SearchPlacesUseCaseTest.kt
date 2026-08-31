package com.sextou.domain.places.usecase

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Loading
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.places.model.BusinessStatus
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.NearbySearchRequest
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.model.PlaceDetailsRequest
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlacePhotoRequest
import com.sextou.domain.places.model.PlaceRankPreference
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
    fun `uses only 3 km and 6 km radii with popularity when query is blank and location exists`() = runTest {
        useCase(
            query = "   ",
            location = GeoPoint(latitude = -22.9, longitude = -43.2),
        )

        assertEquals(
            listOf(
                3_000.0,
                6_000.0,
            ),
            repository.nearbyRequests.map(NearbySearchRequest::radiusMeters),
        )
        repository.nearbyRequests.forEach { request ->
            assertEquals(GeoPoint(latitude = -22.9, longitude = -43.2), request.center)
            assertEquals(20, request.maxResults)
            assertEquals(PlaceRankPreference.POPULARITY, request.rankPreference)
            assertEquals("BR", request.regionCode)
            assertEquals(false, request.includePhotos)
            assertTrue("bar" in request.includedTypes)
            assertTrue("restaurant" in request.includedTypes)
        }
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
        assertEquals(800.0, request?.locationBiasRadiusMeters)
        assertEquals(false, request?.includePhotos)
        assertTrue(repository.nearbyRequests.isEmpty())
    }

    @Test
    fun `includes photo metadata when explicitly requested for nearby search`() = runTest {
        useCase(
            query = "",
            location = GeoPoint(latitude = -22.9, longitude = -43.2),
            includePhotos = true,
        )

        assertEquals(2, repository.nearbyRequests.size)
        assertTrue(repository.nearbyRequests.all(NearbySearchRequest::includePhotos))
    }

    @Test
    fun `merges successful nearby searches and removes duplicated place ids`() = runTest {
        repository.nearbyResults = listOf(
            Success(listOf(place(id = "inner"), place(id = "duplicate"))),
            Success(listOf(place(id = "duplicate"), place(id = "outer"))),
        )

        val result = useCase(query = "", location = GeoPoint(0.0, 0.0))

        assertEquals(
            listOf("inner", "duplicate", "outer"),
            (result as Success).data.map(PlaceSummary::id),
        )
        assertEquals(2, repository.nearbyRequests.size)
    }

    @Test
    fun `preserves a nearby search failure and does not start later radii`() = runTest {
        val expected = Failure(
            Error(
                code = 503,
                title = "Unavailable",
                message = "Try again",
            ),
        )
        repository.nearbyResults = listOf(
            Success(emptyList()),
            expected,
        )

        val result = useCase(query = "", location = GeoPoint(0.0, 0.0))

        assertEquals(expected, result)
        assertEquals(2, repository.nearbyRequests.size)
    }

    @Test
    fun `preserves nearby loading state and does not start later radii`() = runTest {
        repository.nearbyResults = listOf(
            Success(emptyList()),
            Loading(emptyList<PlaceSummary>()),
        )

        val result = useCase(query = "", location = GeoPoint(0.0, 0.0))

        assertEquals(Loading(emptyList<PlaceSummary>()), result)
        assertEquals(2, repository.nearbyRequests.size)
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
    fun `does not search before location is available for a blank query`() = runTest {
        val result = useCase(query = "", location = null)

        assertEquals(Success(emptyList<PlaceSummary>()), result)
        assertNull(repository.textRequest)
        assertTrue(repository.nearbyRequests.isEmpty())
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
    val nearbyRequests = mutableListOf<NearbySearchRequest>()
    var textRequest: PlaceTextSearchRequest? = null
    var nearbyResult: Result<List<PlaceSummary>> = Success(emptyList())
    var nearbyResults: List<Result<List<PlaceSummary>>> = emptyList()
    var textResult: Result<List<PlaceSummary>> = Success(emptyList())

    override suspend fun searchNearby(request: NearbySearchRequest): Result<List<PlaceSummary>> {
        nearbyRequests += request
        return nearbyResults.getOrNull(nearbyRequests.lastIndex) ?: nearbyResult
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
