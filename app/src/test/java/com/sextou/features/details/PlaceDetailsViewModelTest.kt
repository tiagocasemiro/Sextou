package com.sextou.features.details

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.favorites.repository.FavoriteRepository
import com.sextou.domain.favorites.usecase.ObserveFavoritesUseCase
import com.sextou.domain.ignored.repository.IgnoredPlaceRepository
import com.sextou.domain.ignored.usecase.ObserveIgnoredPlacesUseCase
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.NearbySearchRequest
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.model.PlaceDetailsRequest
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlacePhotoRequest
import com.sextou.domain.places.model.PlaceStatus
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.model.PlaceTextSearchRequest
import com.sextou.domain.places.repository.PlaceStatusRepository
import com.sextou.domain.places.repository.PlacesRepository
import com.sextou.domain.places.usecase.GetPlaceDetailsUseCase
import com.sextou.domain.places.usecase.GetPlacePhotoUseCase
import com.sextou.domain.places.usecase.SetPlaceStatusUseCase
import com.sextou.domain.visits.repository.VisitRepository
import com.sextou.domain.visits.usecase.ObserveVisitedPlacesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
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

    @Test
    fun statusSelectionsAreLoadedFromTheLocalRepositories() {
        val viewModel = placeDetailsViewModel(
            repository = FakePlacesRepository(detailsResult = Failure(null)),
            statusStore = TestStatusStore(visitedPlaceIds = setOf("place-1")),
        )

        viewModel.load("place-1")

        assertFalse(viewModel.uiState.value.isFavorite)
        assertTrue(viewModel.uiState.value.isVisited)
        assertFalse(viewModel.uiState.value.isIgnored)
    }

    @Test
    fun selectingAStatusPersistsItAndClearsTheOtherStatuses() {
        val statusStore = TestStatusStore(visitedPlaceIds = setOf("place-1"))
        val viewModel = placeDetailsViewModel(
            repository = FakePlacesRepository(detailsResult = Failure(null)),
            statusStore = statusStore,
        )
        viewModel.load("place-1")

        viewModel.onFavoriteClicked()

        assertEquals(listOf(StatusCall("place-1", PlaceStatus.FAVORITE)), statusStore.calls)
        assertTrue(viewModel.uiState.value.isFavorite)
        assertFalse(viewModel.uiState.value.isVisited)
        assertFalse(viewModel.uiState.value.isIgnored)
        assertEquals(setOf("place-1"), statusStore.favoritePlaceIds.value)
        assertEquals(emptySet<String>(), statusStore.visitedPlaceIds.value)

        viewModel.onFavoriteClicked()

        assertEquals(
            listOf(
                StatusCall("place-1", PlaceStatus.FAVORITE),
                StatusCall("place-1", null),
            ),
            statusStore.calls,
        )
        assertFalse(viewModel.uiState.value.isFavorite)
        assertFalse(viewModel.uiState.value.isVisited)
        assertFalse(viewModel.uiState.value.isIgnored)
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

private fun placeDetailsViewModel(
    repository: PlacesRepository.Remote,
    statusStore: TestStatusStore = TestStatusStore(),
) = PlaceDetailsViewModel(
    getPlaceDetailsUseCase = GetPlaceDetailsUseCase(repository, EmptyPlacesLocalRepository()),
    getPlacePhotoUseCase = GetPlacePhotoUseCase(repository),
    observeFavoritesUseCase = ObserveFavoritesUseCase(TestFavoriteRepository(statusStore)),
    observeVisitedPlacesUseCase = ObserveVisitedPlacesUseCase(TestVisitRepository(statusStore)),
    observeIgnoredPlacesUseCase = ObserveIgnoredPlacesUseCase(TestIgnoredPlaceRepository(statusStore)),
    setPlaceStatusUseCase = SetPlaceStatusUseCase(statusStore),
)

private class EmptyPlacesLocalRepository : PlacesRepository.Local {
    override fun observeAll() = flowOf(emptyList<PlaceSummary>())

    override suspend fun saveAll(places: List<PlaceSummary>): Result<Unit> = Success(Unit)
}

private class EmptyPlaceStatusRepository : com.sextou.domain.places.repository.PlaceStatusRepository.Local {
    override suspend fun setStatus(
        placeId: String,
        status: com.sextou.domain.places.model.PlaceStatus?,
    ): Result<Unit> = Success(Unit)
}

private data class StatusCall(
    val placeId: String,
    val status: PlaceStatus?,
)

private class TestStatusStore(
    favoritePlaceIds: Set<String> = emptySet(),
    visitedPlaceIds: Set<String> = emptySet(),
    ignoredPlaceIds: Set<String> = emptySet(),
) : PlaceStatusRepository.Local {
    val favoritePlaceIds = MutableStateFlow(favoritePlaceIds)
    val visitedPlaceIds = MutableStateFlow(visitedPlaceIds)
    val ignoredPlaceIds = MutableStateFlow(ignoredPlaceIds)
    val calls = mutableListOf<StatusCall>()

    override suspend fun setStatus(placeId: String, status: PlaceStatus?): Result<Unit> {
        calls += StatusCall(placeId, status)
        favoritePlaceIds.value = favoritePlaceIds.value - placeId
        visitedPlaceIds.value = visitedPlaceIds.value - placeId
        ignoredPlaceIds.value = ignoredPlaceIds.value - placeId
        when (status) {
            PlaceStatus.FAVORITE -> favoritePlaceIds.value += placeId
            PlaceStatus.VISITED -> visitedPlaceIds.value += placeId
            PlaceStatus.IGNORED -> ignoredPlaceIds.value += placeId
            null -> Unit
        }
        return Success(Unit)
    }
}

private class TestFavoriteRepository(
    private val store: TestStatusStore,
) : FavoriteRepository.Local {
    override fun observeIds(): Flow<Set<String>> = store.favoritePlaceIds

    override suspend fun setSelected(placeId: String, selected: Boolean): Result<Unit> =
        store.setStatus(placeId, PlaceStatus.FAVORITE.takeIf { selected })
}

private class TestVisitRepository(
    private val store: TestStatusStore,
) : VisitRepository.Local {
    override fun observeIds(): Flow<Set<String>> = store.visitedPlaceIds

    override suspend fun setSelected(placeId: String, selected: Boolean): Result<Unit> =
        store.setStatus(placeId, PlaceStatus.VISITED.takeIf { selected })
}

private class TestIgnoredPlaceRepository(
    private val store: TestStatusStore,
) : IgnoredPlaceRepository.Local {
    override fun observeIds(): Flow<Set<String>> = store.ignoredPlaceIds

    override suspend fun setSelected(placeId: String, selected: Boolean): Result<Unit> =
        store.setStatus(placeId, PlaceStatus.IGNORED.takeIf { selected })
}

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
