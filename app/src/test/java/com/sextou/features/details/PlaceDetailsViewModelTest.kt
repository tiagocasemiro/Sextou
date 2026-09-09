package com.sextou.features.details

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.NearbySearchRequest
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.model.PlaceDetailsRequest
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlacePhotoRequest
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.model.PlaceTextSearchRequest
import com.sextou.domain.places.repository.PlacesRepository
import com.sextou.domain.places.usecase.GetPlaceDetailsUseCase
import com.sextou.domain.places.usecase.GetPlacePhotoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class PlaceDetailsViewModelTest {
    @get:Rule
    val mainDispatcherRule = DetailsMainDispatcherRule()

    @Test
    fun failedRemoteDetailsKeepThePlaceSummaryVisible() {
        val repository = FakePlacesRepository(
            detailsResult = Failure(Error(message = "Quota excedida")),
        )
        val viewModel = placeDetailsViewModel(repository)
        viewModel.setFallback(
            PlaceDetailsFallback(
                id = "place-1",
                name = "Bar do Bairro",
                category = "Bar",
                address = "Rua Principal, 10",
                rating = 4.6,
                ratingsCount = 120,
                priceLevel = 2,
                location = GeoPoint(-22.9, -43.2),
                googleMapsUri = "https://maps.google.com/?q=place-1",
            ),
        )

        viewModel.load("place-1")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isError)
        assertEquals("Bar do Bairro", state.place?.name)
        assertEquals(4.6, state.place?.rating)
        assertEquals("Rua Principal, 10", state.place?.address)
    }

    @Test
    fun failedRemoteDetailsWithoutSummaryStillExposeAnError() {
        val viewModel = placeDetailsViewModel(
            FakePlacesRepository(detailsResult = Failure(Error(message = "Indisponível"))),
        )

        viewModel.load("place-1")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isError)
        assertNull(state.place)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsMainDispatcherRule : TestWatcher() {
    private val dispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

private fun placeDetailsViewModel(repository: PlacesRepository.Remote) = PlaceDetailsViewModel(
    getPlaceDetailsUseCase = GetPlaceDetailsUseCase(repository),
    getPlacePhotoUseCase = GetPlacePhotoUseCase(repository),
)

private class FakePlacesRepository(
    private val detailsResult: Result<PlaceDetails>,
) : PlacesRepository.Remote {
    override suspend fun searchNearby(request: NearbySearchRequest): Result<List<PlaceSummary>> =
        Failure(null)

    override suspend fun searchByText(request: PlaceTextSearchRequest): Result<List<PlaceSummary>> =
        Failure(null)

    override suspend fun getDetails(request: PlaceDetailsRequest): Result<PlaceDetails> =
        detailsResult

    override suspend fun getPhoto(request: PlacePhotoRequest): Result<PlacePhoto> =
        Failure(null)
}
