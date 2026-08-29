package com.sextou.features.map

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
import com.sextou.domain.places.usecase.SearchPlacesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {
    @get:Rule
    val mainDispatcherRule = MapMainDispatcherRule()

    @Test
    fun `loads map places using the current user location`() {
        val location = GeoPoint(latitude = -22.9, longitude = -43.2)
        val searchPlacesUseCase = RecordingSearchPlacesUseCase(
            result = Success(listOf(place(id = "place-1", location = GeoPoint(-22.91, -43.21)))),
        )
        val viewModel = MapViewModel(searchPlacesUseCase)

        viewModel.onLocationChanged(location)
        viewModel.load(query = "")

        assertEquals(location, searchPlacesUseCase.calls.single().location)
        assertEquals(
            MapUserLocationUiModel(latitude = -22.9, longitude = -43.2),
            viewModel.uiState.value.userLocation,
        )
        assertEquals(listOf("place-1"), viewModel.uiState.value.places.map(MapPlaceUiModel::id))
    }

    @Test
    fun `reloads the active map query when the user location changes`() {
        val firstLocation = GeoPoint(latitude = -22.9, longitude = -43.2)
        val secondLocation = GeoPoint(latitude = -22.91, longitude = -43.21)
        val searchPlacesUseCase = RecordingSearchPlacesUseCase()
        val viewModel = MapViewModel(searchPlacesUseCase)

        viewModel.load(query = "bar")
        viewModel.onLocationChanged(firstLocation)
        viewModel.onLocationChanged(secondLocation)

        assertEquals(
            listOf(null, firstLocation, secondLocation),
            searchPlacesUseCase.calls.map(LocationSearchCall::location),
        )
        assertEquals("bar", searchPlacesUseCase.calls.last().query)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MapMainDispatcherRule : TestWatcher() {
    private val dispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

private data class LocationSearchCall(
    val query: String,
    val location: GeoPoint?,
)

private class RecordingSearchPlacesUseCase(
    private val result: Result<List<PlaceSummary>> = Success(emptyList()),
) : SearchPlacesUseCase(NoOpPlacesRepository()) {
    val calls = mutableListOf<LocationSearchCall>()

    override suspend fun invoke(
        query: String,
        location: GeoPoint?,
    ): Result<List<PlaceSummary>> {
        calls += LocationSearchCall(query = query, location = location)
        return result
    }
}

private class NoOpPlacesRepository : PlacesRepository.Remote {
    override suspend fun searchNearby(request: NearbySearchRequest): Result<List<PlaceSummary>> =
        Success(emptyList())

    override suspend fun searchByText(request: PlaceTextSearchRequest): Result<List<PlaceSummary>> =
        Success(emptyList())

    override suspend fun getDetails(request: PlaceDetailsRequest): Result<PlaceDetails> =
        error("Not used")

    override suspend fun getPhoto(request: PlacePhotoRequest): Result<PlacePhoto> =
        error("Not used")
}

private fun place(
    id: String,
    location: GeoPoint,
) = PlaceSummary(
    id = id,
    displayName = "Place $id",
    formattedAddress = null,
    location = location,
    primaryType = "bar",
    primaryTypeDisplayName = "Bar",
    types = listOf("bar"),
    businessStatus = BusinessStatus.OPERATIONAL,
    rating = 4.5,
    userRatingCount = 10,
    priceLevel = 2,
    googleMapsUri = null,
    providerAttribution = "Google Maps",
)
