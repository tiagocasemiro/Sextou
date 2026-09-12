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
import com.sextou.domain.places.usecase.LoadedPlacesUseCase
import com.sextou.domain.places.repository.AutomaticRefreshRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import com.sextou.domain.places.usecase.SetPlaceStatusUseCase
import com.sextou.domain.visits.repository.VisitRepository
import com.sextou.domain.visits.usecase.ObserveVisitedPlacesUseCase
import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.flow.first
import java.util.concurrent.Executors
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
    fun `publishes on main while disk save runs on IO and survives ViewModel clearing`() = runBlocking {
        Executors.newSingleThreadExecutor { Thread(it, "sextou-test-main") }.asCoroutineDispatcher().use { main ->
            Executors.newSingleThreadExecutor { Thread(it, "sextou-test-io") }.asCoroutineDispatcher().use { io ->
                Dispatchers.setMain(main)
                val release = CompletableDeferred<Unit>()
                val saved = CompletableDeferred<Long>()
                val started = CompletableDeferred<Long>()
                val scope = CoroutineScope(SupervisorJob() + io)
                val store = ViewModelStore()
                val mainThread = withContext(main) { Thread.currentThread().id }
                val ioThread = withContext(io) { Thread.currentThread().id }
                try {
                    val local = object : PlacesRepository.Local by NoOpPlacesRepository() {
                        override suspend fun saveMissing(places: List<PlaceSummary>): Result<Unit> {
                            started.complete(Thread.currentThread().id)
                            release.await()
                            saved.complete(Thread.currentThread().id)
                            return Success(Unit)
                        }
                    }
                    val loaded = LoadedPlacesUseCase(
                        FakeSearchPlacesRepository { Success(listOf(place("remote", "Remote"))) },
                        local, RefreshMarker(), { 20260911L }, scope, io,
                    )
                    val statuses = FakeStatusRepository(emptySet(), emptySet())
                    val viewModel = withContext(main) {
                        FeedViewModel(
                            loaded, GetPlacePhotoUseCase(NoOpPlacesRepository()),
                            ObserveFavoritesUseCase(FakeFavoriteRepository(statuses)),
                            ObserveVisitedPlacesUseCase(FakeVisitRepository(statuses)),
                            SetPlaceStatusUseCase(statuses), GeoPoint(0.0, 0.0),
                        ).also { store.put("feed", it); it.onScreenOpened() }
                    }
                    withTimeout(5_000) {
                        withContext(main) {
                            viewModel.uiState.first { it.places.any { place -> place.id == "remote" } }
                            assertEquals(mainThread, Thread.currentThread().id)
                            assertFalse(saved.isCompleted)
                            store.clear()
                        }
                        assertEquals(ioThread, started.await())
                        release.complete(Unit)
                        assertEquals(ioThread, saved.await())
                    }
                } finally {
                    release.complete(Unit)
                    withContext(main) { store.clear() }
                    scope.cancel()
                }
            }
        }
    }

    @Test
    fun queryFiltersPlacesByNameAndCategory() {
        val repository = FakeSearchPlacesRepository { error("Typing must not search") }
        val viewModel = feedViewModel(
            searchRepository = repository,
            savedPlaces = listOf(place("espetaria", "Espetinhos do Tonho"), place("cafe", "Ao Ponto")),
        )
        viewModel.onQueryChanged("  ESPETINHOS  ")
        assertEquals(listOf("espetaria"), viewModel.uiState.value.places.map { it.id })
        assertTrue(repository.calls.isEmpty())
    }

    @Test
    fun blankQueryRestoresAllPlaces() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
                Success(
                    listOf(
                        place(id = "ao-ponto", name = "Ao Ponto"),
                        place(id = "bar-do-ninho", name = "Bar do Ninho"),
                    ),
                )
            },
        )

        viewModel.retry()
        viewModel.onQueryChanged("bar")
        viewModel.onQueryChanged("   ")

        assertEquals(2, viewModel.uiState.value.places.size)
    }

    @Test
    fun favoriteAndVisitedActionsAreExclusive() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository { Success(emptyList()) },
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
            searchRepository = FakeSearchPlacesRepository { Success(emptyList()) },
        )

        viewModel.onTabSelected(FeedTab.FAVORITES)

        assertEquals(FeedTab.FAVORITES, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun successfulSearchPopulatesPlacesAndClearsPreviousError() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
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
            searchRepository = FakeSearchPlacesRepository { Success(remotePlaces) },
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
        val searchRepository = FakeSearchPlacesRepository {
            Success(listOf(place(id = "place-1", name = "Place 1")))
        }
        val viewModel = feedViewModel(searchRepository = searchRepository)

        viewModel.retry()

        assertTrue(searchRepository.includePhotosCalls.all { it })
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
            searchRepository = FakeSearchPlacesRepository {
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
            searchRepository = FakeSearchPlacesRepository {
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

        viewModel.onScreenOpened()
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
            searchRepository = FakeSearchPlacesRepository {
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
            searchRepository = FakeSearchPlacesRepository {
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
        val searchRepository = FakeSearchPlacesRepository {
            Success(listOf(place(id = "remote-place", name = "Remote Place")))
        }
        val viewModel = feedViewModel(
            searchRepository = searchRepository,
            initialLocation = null,
            savedPlaces = listOf(place(id = "saved-place", name = "Saved Place")),
        )

        assertEquals(
            listOf("saved-place"),
            viewModel.uiState.value.places.map(FeedPlaceUiModel::id),
        )

        viewModel.onScreenOpened()
        viewModel.onLocationChanged(GeoPoint(1.0, 1.0))
        viewModel.onLocationChanged(GeoPoint(2.0, 2.0))

        assertEquals(1, searchRepository.calls.size)
        assertEquals(
            setOf("saved-place", "remote-place"),
            viewModel.uiState.value.places.map(FeedPlaceUiModel::id).toSet(),
        )
    }

    @Test
    fun persistedSelectionsAreLoadedFromLocalRepositories() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository { Success(emptyList()) },
            favoritePlaceIds = setOf("favorite-place"),
            visitedPlaceIds = setOf("visited-place"),
        )

        assertEquals(setOf("favorite-place"), viewModel.uiState.value.favoritePlaceIds)
        assertEquals(setOf("visited-place"), viewModel.uiState.value.visitedPlaceIds)
    }

    @Test
    fun firstOpeningStartsWithEmptyDraft() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
                error("Filter interaction must not search")
            },
        )

        viewModel.onFilterClicked()

        assertEquals(emptySet<FeedFilterOption>(), viewModel.uiState.value.draftFilterOptions)
        assertTrue(viewModel.uiState.value.confirmedFilterOptions.isEmpty())
    }

    @Test
    fun openingTwicePreservesTheExistingDraft() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
                error("Filter interaction must not search")
            },
        )

        viewModel.onFilterClicked()
        viewModel.onFilterOptionChanged(FeedFilterOption.TYPE_BOTECO, true)
        viewModel.onFilterClicked()

        assertEquals(
            setOf(FeedFilterOption.TYPE_BOTECO),
            viewModel.uiState.value.draftFilterOptions,
        )
    }

    @Test
    fun selectingTwoOptionsFromTheSameGroupKeepsBothSelections() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
                error("Filter interaction must not search")
            },
        )

        viewModel.onFilterClicked()
        viewModel.onFilterOptionChanged(FeedFilterOption.TYPE_BOTECO, true)
        viewModel.onFilterOptionChanged(FeedFilterOption.TYPE_SKEWER, true)

        assertEquals(
            setOf(
                FeedFilterOption.TYPE_BOTECO,
                FeedFilterOption.TYPE_SKEWER,
            ),
            viewModel.uiState.value.draftFilterOptions,
        )
    }

    @Test
    fun unselectingOneOptionDoesNotRemoveTheOthers() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
                error("Filter interaction must not search")
            },
        )

        viewModel.onFilterClicked()
        viewModel.onFilterOptionChanged(FeedFilterOption.TYPE_BOTECO, true)
        viewModel.onFilterOptionChanged(FeedFilterOption.TYPE_SKEWER, true)
        viewModel.onFilterOptionChanged(FeedFilterOption.TYPE_BOTECO, false)

        assertEquals(
            setOf(FeedFilterOption.TYPE_SKEWER),
            viewModel.uiState.value.draftFilterOptions,
        )
    }

    @Test
    fun editingDraftDoesNotChangeConfirmedSelection() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
                error("Filter interaction must not search")
            },
        )

        viewModel.onFilterClicked()
        viewModel.onFilterOptionChanged(FeedFilterOption.TYPE_BOTECO, true)
        viewModel.onFiltersApplied()
        viewModel.onFilterClicked()
        viewModel.onFilterOptionChanged(FeedFilterOption.PRICE_LOW, true)

        assertEquals(
            setOf(FeedFilterOption.TYPE_BOTECO),
            viewModel.uiState.value.confirmedFilterOptions,
        )
        assertEquals(
            setOf(FeedFilterOption.TYPE_BOTECO, FeedFilterOption.PRICE_LOW),
            viewModel.uiState.value.draftFilterOptions,
        )
    }

    @Test
    fun applyingConfirmsDraftAndClosesThePanel() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
                error("Filter interaction must not search")
            },
        )

        viewModel.onFilterClicked()
        viewModel.onFilterOptionChanged(FeedFilterOption.CATEGORY_KARAOKE, true)
        viewModel.onFiltersApplied()

        assertEquals(
            setOf(FeedFilterOption.CATEGORY_KARAOKE),
            viewModel.uiState.value.confirmedFilterOptions,
        )
        assertNull(viewModel.uiState.value.draftFilterOptions)
    }

    @Test
    fun applyingAfterThePanelIsClosedDoesNothing() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
                error("Filter interaction must not search")
            },
        )

        viewModel.onFiltersApplied()

        assertTrue(viewModel.uiState.value.confirmedFilterOptions.isEmpty())
        assertNull(viewModel.uiState.value.draftFilterOptions)
    }

    @Test
    fun closingDiscardsDraftAndPreservesConfirmedSelection() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
                error("Filter interaction must not search")
            },
        )

        viewModel.onFilterClicked()
        viewModel.onFilterOptionChanged(FeedFilterOption.TYPE_BOTECO, true)
        viewModel.onFiltersApplied()

        viewModel.onFilterClicked()
        viewModel.onFilterOptionChanged(FeedFilterOption.PRICE_LOW, true)
        viewModel.onFilterDialogDismissed()
        viewModel.onFilterClicked()

        assertEquals(
            setOf(FeedFilterOption.TYPE_BOTECO),
            viewModel.uiState.value.draftFilterOptions,
        )
        assertEquals(
            setOf(FeedFilterOption.TYPE_BOTECO),
            viewModel.uiState.value.confirmedFilterOptions,
        )
    }

    @Test
    fun reopeningRestoresTheLastConfirmedSelection() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
                error("Filter interaction must not search")
            },
        )

        viewModel.onFilterClicked()
        viewModel.onFilterOptionChanged(FeedFilterOption.OPEN_NOW, true)
        viewModel.onFiltersApplied()
        viewModel.onFilterClicked()

        assertEquals(
            setOf(FeedFilterOption.OPEN_NOW),
            viewModel.uiState.value.draftFilterOptions,
        )
    }

    @Test
    fun applyingAnEmptyDraftClearsPreviousConfirmation() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
                error("Filter interaction must not search")
            },
        )

        viewModel.onFilterClicked()
        viewModel.onFilterOptionChanged(FeedFilterOption.OPEN_NOW, true)
        viewModel.onFiltersApplied()
        viewModel.onFilterClicked()
        viewModel.onFilterOptionChanged(FeedFilterOption.OPEN_NOW, false)
        viewModel.onFiltersApplied()

        assertTrue(viewModel.uiState.value.confirmedFilterOptions.isEmpty())
        assertNull(viewModel.uiState.value.draftFilterOptions)
    }

    @Test
    fun changingAnOptionWhileThePanelIsClosedDoesNothing() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
                error("Filter interaction must not search")
            },
        )

        viewModel.onFilterOptionChanged(FeedFilterOption.OPEN_NOW, true)

        assertTrue(viewModel.uiState.value.confirmedFilterOptions.isEmpty())
        assertNull(viewModel.uiState.value.draftFilterOptions)
    }

    @Test
    fun leavingTheScreenDiscardsDraftAndPreservesConfirmedSelection() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
                error("Filter interaction must not search")
            },
        )

        viewModel.onFilterClicked()
        viewModel.onFilterOptionChanged(FeedFilterOption.TYPE_BOTECO, true)
        viewModel.onFiltersApplied()
        viewModel.onFilterClicked()
        viewModel.onFilterOptionChanged(FeedFilterOption.PRICE_LOW, true)
        viewModel.onScreenClosed()

        assertEquals(
            setOf(FeedFilterOption.TYPE_BOTECO),
            viewModel.uiState.value.confirmedFilterOptions,
        )
        assertNull(viewModel.uiState.value.draftFilterOptions)
    }

    @Test
    fun savedPlaceEmissionsPreserveFilterSelections() {
        val localPlaces = FakePlacesLocalRepository(
            listOf(place(id = "saved-place", name = "Saved Place")),
        )
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
                error("Filter interaction must not search")
            },
            localPlacesRepository = localPlaces,
        )

        viewModel.onFilterClicked()
        viewModel.onFilterOptionChanged(FeedFilterOption.TYPE_BOTECO, true)
        viewModel.onFiltersApplied()
        localPlaces.emit(listOf(place(id = "new-place", name = "New Place")))

        assertEquals(
            setOf(FeedFilterOption.TYPE_BOTECO),
            viewModel.uiState.value.confirmedFilterOptions,
        )
        assertNull(viewModel.uiState.value.draftFilterOptions)
    }

    @Test
    fun filterInteractionsDoNotMakeRemoteCalls() {
        val searchRepository = FakeSearchPlacesRepository {
            error("Filter interaction must not search")
        }
        val viewModel = feedViewModel(searchRepository = searchRepository)

        viewModel.onFilterClicked()
        FeedFilterOption.entries.forEach { option ->
            viewModel.onFilterOptionChanged(option, true)
        }
        viewModel.onFiltersApplied()
        viewModel.onFilterClicked()
        viewModel.onFilterDialogDismissed()

        assertTrue(searchRepository.calls.isEmpty())
    }

    @Test
    fun queryChangesPreserveFilterSelectionsAndTextualResults() {
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
                error("Filter interaction must not search")
            },
            savedPlaces = listOf(
                place(id = "bar-do-ze", name = "Bar do Zé"),
                place(id = "adega", name = "Adega Central"),
            ),
        )

        viewModel.onFilterClicked()
        viewModel.onFilterOptionChanged(FeedFilterOption.TYPE_BOTECO, true)
        viewModel.onFiltersApplied()
        viewModel.onQueryChanged("adega")

        assertEquals(listOf("adega"), viewModel.uiState.value.places.map(FeedPlaceUiModel::id))
        assertEquals(
            setOf(FeedFilterOption.TYPE_BOTECO),
            viewModel.uiState.value.confirmedFilterOptions,
        )
    }

    @Test
    fun applyingFiltersDoesNotChangePlaceIds() {
        val savedPlaces = listOf(
            place(id = "place-1", name = "Place 1"),
            place(id = "place-2", name = "Place 2"),
        )
        val viewModel = feedViewModel(
            searchRepository = FakeSearchPlacesRepository {
                error("Filter interaction must not search")
            },
            savedPlaces = savedPlaces,
        )

        viewModel.onFilterClicked()
        viewModel.onFilterOptionChanged(FeedFilterOption.PRICE_MEDIUM, true)
        viewModel.onFiltersApplied()

        assertEquals(
            savedPlaces.map(PlaceSummary::id),
            viewModel.uiState.value.places.map(FeedPlaceUiModel::id),
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule : TestWatcher() {
    private val dispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler())

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        scopes.forEach { it.cancel() }
        scopes.clear()
        Dispatchers.resetMain()
    }
}

private val scopes = mutableListOf<CoroutineScope>()
private fun applicationScope() = CoroutineScope(SupervisorJob() + Dispatchers.Main).also { scopes += it }
private class RefreshMarker : AutomaticRefreshRepository.Local {
    var day = 0L
    override suspend fun lastSuccessDay(): Result<Long> = Success(day)
    override suspend fun recordSuccessDay(day: Long): Result<Unit> { this.day = day; return Success(Unit) }
}

private class FakeSearchPlacesRepository(
    private val response: (String) -> Result<List<PlaceSummary>>,
) : PlacesRepository.Remote by NoOpPlacesRepository() {
    val calls = mutableListOf<String>()
    val includePhotosCalls = mutableListOf<Boolean>()

    override suspend fun searchNearby(request: NearbySearchRequest): Result<List<PlaceSummary>> {
        calls += ""
        includePhotosCalls += request.includePhotos
        return response("")
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
    searchRepository: PlacesRepository.Remote,
    initialLocation: GeoPoint? = GeoPoint(0.0, 0.0),
    favoritePlaceIds: Set<String> = emptySet(),
    visitedPlaceIds: Set<String> = emptySet(),
    savedPlaces: List<PlaceSummary> = emptyList(),
    localPlacesRepository: FakePlacesLocalRepository? = null,
    photoResult: Result<PlacePhoto> = Failure(null),
    savePlacesUseCase: SavePlacesUseCase = SavePlacesUseCase(NoOpPlacesRepository()),
): FeedViewModel {
    val statusRepository = FakeStatusRepository(favoritePlaceIds, visitedPlaceIds)
    val favoriteRepository = FakeFavoriteRepository(statusRepository)
    val visitedRepository = FakeVisitRepository(statusRepository)
    val placesRepository = localPlacesRepository ?: FakePlacesLocalRepository(savedPlaces)
    return FeedViewModel(
        loadedPlacesUseCase = LoadedPlacesUseCase(
            remote = searchRepository,
            local = object : PlacesRepository.Local by placesRepository {
                override suspend fun saveMissing(places: List<PlaceSummary>): Result<Unit> = savePlacesUseCase(places)
            },
            automaticRefresh = RefreshMarker(), currentDay = { 20260911L },
            applicationScope = applicationScope(), ioDispatcher = Dispatchers.Main,
        ),
        getPlacePhotoUseCase = GetPlacePhotoUseCase(NoOpPlacesRepository(photoResult)),
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

    fun emit(places: List<PlaceSummary>) {
        this.places.value = places
    }

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
