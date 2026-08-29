package com.sextou.domain.places.usecase

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.places.model.NearbySearchRequest
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.model.PlaceDetailsRequest
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlacePhotoReference
import com.sextou.domain.places.model.PlacePhotoRequest
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.model.PlaceTextSearchRequest
import com.sextou.domain.places.repository.PlacesRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPlacePhotoUseCaseTest {
    private lateinit var repository: PhotoRecordingPlacesRepository
    private lateinit var reference: PlacePhotoReference

    @Before
    fun setUp() {
        repository = PhotoRecordingPlacesRepository()
        reference = PlacePhotoReference(
            placeId = "place-1",
            index = 0,
            width = 640,
            height = 480,
            attributionHtml = null,
            authors = emptyList(),
            googleMapsUri = null,
            flagContentUri = null,
        )
    }

    @Test
    fun blankPlaceIdReturnsFailureWithoutCallingRemoteRepository() = runTest {
        val result = GetPlacePhotoUseCase(repository).invoke(reference.copy(placeId = " "))

        assertTrue(result is Failure)
        assertEquals(null, repository.lastRequest)
    }

    @Test
    fun negativePhotoIndexReturnsFailureWithoutCallingRemoteRepository() = runTest {
        val result = GetPlacePhotoUseCase(repository).invoke(reference.copy(index = -1))

        assertTrue(result is Failure)
        assertEquals(null, repository.lastRequest)
    }

    @Test
    fun validReferenceUsesCardDimensionsAndReturnsPhoto() = runTest {
        val expected = samplePhoto()
        repository.photoResult = Success(expected)

        val result = GetPlacePhotoUseCase(repository).invoke(reference)

        assertEquals(Success(expected), result)
        assertEquals(
            PlacePhotoRequest(reference = reference, maxWidth = 640, maxHeight = 320),
            repository.lastRequest,
        )
    }

    @Test
    fun preservesRemotePhotoFailure() = runTest {
        val expected = Failure(Error(code = 503, message = "offline"))
        repository.photoResult = expected

        val result = GetPlacePhotoUseCase(repository).invoke(reference)

        assertEquals(expected, result)
    }
}

private class PhotoRecordingPlacesRepository : PlacesRepository.Remote {
    var lastRequest: PlacePhotoRequest? = null
    var photoResult: Result<PlacePhoto> = Failure(null)

    override suspend fun searchNearby(request: NearbySearchRequest): Result<List<PlaceSummary>> =
        Success(emptyList())

    override suspend fun searchByText(request: PlaceTextSearchRequest): Result<List<PlaceSummary>> =
        Success(emptyList())

    override suspend fun getDetails(request: PlaceDetailsRequest): Result<PlaceDetails> =
        error("Not used")

    override suspend fun getPhoto(request: PlacePhotoRequest): Result<PlacePhoto> {
        lastRequest = request
        return photoResult
    }
}

private fun samplePhoto() = PlacePhoto(
    uri = "https://example.invalid/photo.jpg",
    attributionHtml = null,
    authors = emptyList(),
    providerAttribution = "Google Maps",
)
