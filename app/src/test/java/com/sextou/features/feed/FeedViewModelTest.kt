package com.sextou.features.feed

import com.sextou.R
import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.favorites.repository.FavoriteRepository
import com.sextou.domain.favorites.usecase.ObserveFavoritesUseCase
import com.sextou.domain.places.model.BusinessStatus
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.NearbySearchRequest
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.model.PlaceDetailsRequest
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlacePhotoReference
import com.sextou.domain.places.model.PlacePhotoRequest
import com.sextou.domain.places.model.PlaceStatus
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.model.PlaceTextSearchRequest
import com.sextou.domain.places.repository.PlaceStatusRepository
import com.sextou.domain.places.repository.PlacesRepository
import com.sextou.domain.places.usecase.ObservePlacesUseCase
import com.sextou.domain.places.usecase.GetPlacePhotoUseCase
import com.sextou.domain.places.usecase.SavePlacesUseCase
import com.sextou.domain.places.usecase.SearchPlacesUseCase
import com.sextou.domain.places.usecase.SetPlaceStatusUseCase
import com.sextou.domain.visits.repository.VisitRepository
import com.sextou.domain.visits.usecase.ObserveVisitedPlacesUseCase
import kotlinx.coroutines.CompletableDeferred
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
class FeedViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun queryFiltersPlacesByNameAndCategory() {
        val viewModel = feedViewModel(
            searchPlacesUseCase = FakeSearchPlacesUseCase { query ->
                if (query.contains("espetinhos")) {
                    Success(listOf(place(id = "espetaria-do-tonho", name = "Espetaria do Tonho")))
                } else {
                    Success(listOf(place(id = "ao-ponto", name = "Ao Ponto")))
                }
            },
        )

        viewModel.onQueryChanged("espetinhos")

        assertEquals(
            listOf("espetaria-do-tonho"),
            viewModel.uiState.value.places.map(FeedPlaceUiModel::id),
        )
    }

    @Test
    fun blankQueryRestoresAllPlaces() {
        val viewModel = feedViewModel(
            searchPlacesUseCase = FakeSearchPlacesUseCase {
                Success(
                    listOf(
                        place(id = "ao-ponto", name = "Ao Ponto"),
                        place(id = "bar-do-ninho", name = "Bar do Ninho"),
                    ),
                )
            },
        )

        viewModel.onQueryChanged("bar")
        viewModel.onQueryChanged("   ")

        assertEquals(2, viewModel.uiState.value.places.size)
    }

    @Test
    fun favoriteAndVisitedActionsAreExclusive() {
        val viewModel = feedViewModel(
            searchPlacesUseCase = FakeSearchPlacesUseCase { Success(emptyList()) },
        )

        viewModel.onFavoriteClicked("ao-ponto")
        viewModel.onVisitedClicked("ao-ponto")

        assertTrue("ao-ponto" in viewModel.uiState.value.visitedPlaceIds)
        assertTrue(viewModel.uiState.value.favoritePlaceIds.isEmpty())

        viewModel.onVisitedClicked("ao-ponto")

        assertTrue(viewModel.uiState.value.favoritePlaceIds.isEmpty())
        assertTrue(viewModel.uiState.value.visitedPlaceIds.isEmpty())
    }

    @Test
    fun tabSelectionIsReflectedInUiState() {
        val viewModel = feedViewModel(
            searchPlacesUseCase = FakeSearchPlacesUseCase { Success(emptyList()) },
        )

        viewModel.onTabSelected(FeedTab.FAVORITES)

        assertEquals(FeedTab.FAVORITES, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun successfulSearchPopulatesPlacesAndClearsPreviousError() {
        val viewModel = feedViewModel(
            searchPlacesUseCase = FakeSearchPlacesUseCase {
                Success(listOf(place(id = "ao-ponto", name = "Ao Ponto")))
            },
        )

        viewModel.retry()

        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isError)
        assertEquals(listOf("ao-ponto"), viewModel.uiState.value.places.map(FeedPlaceUiModel::id))
    }

    @Test
    fun `publishes every remote place before background persistence finishes`() {
        val remotePlaces = listOf(
            place(id = "place-1", name = "Place 1"),
            place(id = "place-2", name = "Place 2"),
            place(id = "place-3", name = "Place 3"),
        )
        val savePlacesUseCase = BlockingSavePlacesUseCase()
        val viewModel = feedViewModel(
            searchPlacesUseCase = FakeSearchPlacesUseCase { Success(remotePlaces) },
            savePlacesUseCase = savePlacesUseCase,
        )

        viewModel.retry()

        assertEquals(
            remotePlaces.map(PlaceSummary::id),
            viewModel.uiState.value.places.map(FeedPlaceUiModel::id),
        )
        assertTrue(savePlacesUseCase.started.isCompleted)
        assertEquals(remotePlaces, savePlacesUseCase.receivedPlaces)
        assertFalse(savePlacesUseCase.release.isCompleted)

        savePlacesUseCase.release.complete(Unit)
    }

    @Test
    fun feedRequestsPhotoMetadataFromThePlacesApi() {
        val searchPlacesUseCase = FakeSearchPlacesUseCase {
            Success(listOf(place(id = "place-1", name = "Place 1")))
        }
        val viewModel = feedViewModel(searchPlacesUseCase = searchPlacesUseCase)

        viewModel.retry()

        assertTrue(searchPlacesUseCase.includePhotosCalls.all { it })
    }

    @Test
    fun successfulPhotoResolutionIsPublishedInTheFeedPlace() {
        val reference = PlacePhotoReference(
            placeId = "place-1",
            index = 0,
            width = 640,
            height = 320,
            attributionHtml = "<a>Google Maps</a>",
            authors = emptyList(),
            googleMapsUri = null,
            flagContentUri = null,
        )
        val viewModel = feedViewModel(
            searchPlacesUseCase = FakeSearchPlacesUseCase {
                Success(listOf(place(id = "place-1", name = "Place 1", photos = listOf(reference))))
            },
            photoResult = Success(
                PlacePhoto(
                    uri = "https://example.invalid/place-1.jpg",
                    attributionHtml = "<a>Google Maps</a>",
                    authors = emptyList(),
                    providerAttribution = "Google Maps",
                ),
            ),
        )

        viewModel.retry()
        viewModel.requestPhoto("place-1")

        assertEquals(
            "https://example.invalid/place-1.jpg",
            viewModel.uiState.value.places.single().photoUri,
        )
    }

    @Test
    fun remotePlaceMappingDoesNotInventMissingMetadata() {
        val viewModel = feedViewModel(
            searchPlacesUseCase = FakeSearchPlacesUseCase {
                Success(
                    listOf(
                        place(
                            id = "place-1",
                            name = "Place 1",
                            location = GeoPoint(0.0, 0.0),
                        ),
                    ),
                )
            },
            initialLocation = GeoPoint(0.0, 0.0),
        )

        val mappedPlace = viewModel.uiState.value.places.single()

        assertEquals(0.0, mappedPlace.distanceMeters ?: -1.0, 0.0)
        assertEquals("Google Maps", mappedPlace.providerAttribution)
        assertNull(mappedPlace.status)
        assertNull(mappedPlace.rating)
        assertNull(mappedPlace.ratingsCount)
        assertNull(mappedPlace.priceLevel)
    }

    @Test
    fun failedSearchExposesDomainErrorWithoutKeepingLoadingState() {
        val viewModel = feedViewModel(
            searchPlacesUseCase = FakeSearchPlacesUseCase {
                Failure(Error(message = "Serviço indisponível"))
            },
        )

        viewModel.retry()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.isError)
        assertEquals(R.string.feed_generic_error, viewModel.uiState.value.errorMessageResId)
        assertFalse(viewModel.uiState.value.isStale)
    }

    @Test
    fun failedRefreshKeepsPreviousPlacesAndMarksResultsAsStale() {
        var calls = 0
        val viewModel = feedViewModel(
            searchPlacesUseCase = FakeSearchPlacesUseCase {
                if (calls++ == 0) {
                    Success(listOf(place(id = "ao-ponto", name = "Ao Ponto")))
                } else {
                    Failure(Error(message = "Serviço indisponível"))
                }
            },
        )

        viewModel.retry()
        viewModel.retry()

        assertEquals(listOf("ao-ponto"), viewModel.uiState.value.places.map(FeedPlaceUiModel::id))
        assertTrue(viewModel.uiState.value.isStale)
        assertEquals(R.string.feed_generic_error, viewModel.uiState.value.errorMessageResId)
    }

    @Test
    fun savedPlacesAreShownBeforeOneAutomaticRefreshAfterLocationArrives() {
        val searchPlacesUseCase = FakeSearchPlacesUseCase {
            Success(listOf(place(id = "remote-place", name = "Remote Place")))
        }
        val viewModel = feedViewModel(
            searchPlacesUseCase = searchPlacesUseCase,
            savedPlaces = listOf(place(id = "saved-place", name = "Saved Place")),
        )

        assertEquals(
            listOf("saved-place"),
            viewModel.uiState.value.places.map(FeedPlaceUiModel::id),
        )

        viewModel.onLocationChanged(GeoPoint(1.0, 1.0))
        viewModel.onLocationChanged(GeoPoint(2.0, 2.0))

        assertEquals(1, searchPlacesUseCase.calls.size)
        assertEquals(
            setOf("saved-place", "remote-place"),
            viewModel.uiState.value.places.map(FeedPlaceUiModel::id).toSet(),
        )
    }

    @Test
    fun persistedSelectionsAreLoadedFromLocalRepositories() {
        val viewModel = feedViewModel(
            searchPlacesUseCase = FakeSearchPlacesUseCase { Success(emptyList()) },
            favoritePlaceIds = setOf("favorite-place"),
            visitedPlaceIds = setOf("visited-place"),
        )

        assertEquals(setOf("favorite-place"), viewModel.uiState.value.favoritePlaceIds)
        assertEquals(setOf("visited-place"), viewModel.uiState.value.visitedPlaceIds)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule : TestWatcher() {
    private val dispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

private class FakeSearchPlacesUseCase(
    private val response: (String) -> Result<List<PlaceSummary>>,
) : SearchPlacesUseCase(NoOpPlacesRepository()) {
    val calls = mutableListOf<String>()
    val includePhotosCalls = mutableListOf<Boolean>()

    override suspend fun invoke(
        query: String,
        location: GeoPoint?,
        includePhotos: Boolean,
    ): Result<List<PlaceSummary>> {
        calls += query
        includePhotosCalls += includePhotos
        return response(query)
    }
}

private class BlockingSavePlacesUseCase : SavePlacesUseCase(NoOpPlacesRepository()) {
    val started = CompletableDeferred<Unit>()
    val release = CompletableDeferred<Unit>()
    var receivedPlaces: List<PlaceSummary> = emptyList()

    override suspend fun invoke(places: List<PlaceSummary>): Result<Unit> {
        receivedPlaces = places
        started.complete(Unit)
        release.await()
        return Success(Unit)
    }
}

private class NoOpPlacesRepository(
    private val photoResult: Result<PlacePhoto> = Failure(null),
) : PlacesRepository.Remote, PlacesRepository.Local {
    override suspend fun searchNearby(request: NearbySearchRequest): Result<List<PlaceSummary>> =
        Success(emptyList())

    override suspend fun searchByText(request: PlaceTextSearchRequest): Result<List<PlaceSummary>> =
        Success(emptyList())

    override suspend fun getDetails(request: PlaceDetailsRequest): Result<PlaceDetails> =
        error("Not used")

    override suspend fun getPhoto(request: PlacePhotoRequest): Result<PlacePhoto> =
        photoResult

    override fun observeAll(): Flow<List<PlaceSummary>> = flowOf(emptyList())

    override suspend fun saveAll(places: List<PlaceSummary>): Result<Unit> = Success(Unit)

    override suspend fun saveMissing(places: List<PlaceSummary>): Result<Unit> = Success(Unit)
}

private fun feedViewModel(
    searchPlacesUseCase: SearchPlacesUseCase,
    initialLocation: GeoPoint? = null,
    favoritePlaceIds: Set<String> = emptySet(),
    visitedPlaceIds: Set<String> = emptySet(),
    savedPlaces: List<PlaceSummary> = emptyList(),
    photoResult: Result<PlacePhoto> = Failure(null),
    savePlacesUseCase: SavePlacesUseCase = SavePlacesUseCase(NoOpPlacesRepository()),
): FeedViewModel {
    val statusRepository = FakeStatusRepository(favoritePlaceIds, visitedPlaceIds)
    val favoriteRepository = FakeFavoriteRepository(statusRepository)
    val visitedRepository = FakeVisitRepository(statusRepository)
    val placesRepository = FakePlacesLocalRepository(savedPlaces)
    return FeedViewModel(
        searchPlacesUseCase = searchPlacesUseCase,
        getPlacePhotoUseCase = GetPlacePhotoUseCase(NoOpPlacesRepository(photoResult)),
        savePlacesUseCase = savePlacesUseCase,
        observePlacesUseCase = ObservePlacesUseCase(placesRepository),
        observeFavoritesUseCase = ObserveFavoritesUseCase(favoriteRepository),
        observeVisitedPlacesUseCase = ObserveVisitedPlacesUseCase(visitedRepository),
        setPlaceStatusUseCase = SetPlaceStatusUseCase(statusRepository),
        initialLocation = initialLocation,
    )
}

private class FakePlacesLocalRepository(
    initialPlaces: List<PlaceSummary>,
) : PlacesRepository.Local {
    private val places = MutableStateFlow(initialPlaces)

    override fun observeAll(): Flow<List<PlaceSummary>> = places

    override suspend fun saveAll(places: List<PlaceSummary>): Result<Unit> = Success(Unit)

    override suspend fun saveMissing(places: List<PlaceSummary>): Result<Unit> = Success(Unit)
}

private class FakeFavoriteRepository(
    private val statusRepository: FakeStatusRepository,
) : FavoriteRepository.Local {
    override fun observeIds(): Flow<Set<String>> = statusRepository.favoriteIds

    override suspend fun setSelected(placeId: String, selected: Boolean): Result<Unit> {
        return statusRepository.setStatus(
            placeId,
            PlaceStatus.FAVORITE.takeIf { selected },
        )
    }
}

private class FakeVisitRepository(
    private val statusRepository: FakeStatusRepository,
) : VisitRepository.Local {
    override fun observeIds(): Flow<Set<String>> = statusRepository.visitedIds

    override suspend fun setSelected(placeId: String, selected: Boolean): Result<Unit> {
        return statusRepository.setStatus(
            placeId,
            PlaceStatus.VISITED.takeIf { selected },
        )
    }
}

private class FakeStatusRepository(
    favoritePlaceIds: Set<String>,
    visitedPlaceIds: Set<String>,
) : PlaceStatusRepository.Local {
    val favoriteIds = MutableStateFlow(favoritePlaceIds)
    val visitedIds = MutableStateFlow(visitedPlaceIds)

    override suspend fun setStatus(placeId: String, status: PlaceStatus?): Result<Unit> {
        favoriteIds.value = favoriteIds.value - placeId
        visitedIds.value = visitedIds.value - placeId
        when (status) {
            PlaceStatus.FAVORITE -> favoriteIds.value += placeId
            PlaceStatus.VISITED -> visitedIds.value += placeId
            PlaceStatus.IGNORED,
            null,
            -> Unit
        }
        return Success(Unit)
    }
}

private fun place(
    id: String,
    name: String,
    location: GeoPoint? = null,
    photos: List<PlacePhotoReference> = emptyList(),
) = PlaceSummary(
    id = id,
    displayName = name,
    formattedAddress = null,
    location = location,
    primaryType = "bar",
    primaryTypeDisplayName = "Bar",
    types = listOf("bar"),
    businessStatus = BusinessStatus.OPERATIONAL,
    rating = null,
    userRatingCount = null,
    priceLevel = null,
    googleMapsUri = null,
    providerAttribution = "Google Maps",
    photos = photos,
)
