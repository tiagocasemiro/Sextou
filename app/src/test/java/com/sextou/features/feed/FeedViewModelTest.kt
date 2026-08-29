package com.sextou.features.feed

import com.sextou.R
import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.favorites.repository.FavoriteRepository
import com.sextou.domain.favorites.usecase.ObserveFavoritesUseCase
import com.sextou.domain.favorites.usecase.ToggleFavoriteUseCase
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
import com.sextou.domain.visits.repository.VisitRepository
import com.sextou.domain.visits.usecase.ObserveVisitedPlacesUseCase
import com.sextou.domain.visits.usecase.ToggleVisitedPlaceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
    fun favoriteAndVisitedActionsToggleIndependently() {
        val viewModel = feedViewModel(
            searchPlacesUseCase = FakeSearchPlacesUseCase { Success(emptyList()) },
        )

        viewModel.onFavoriteClicked("ao-ponto")
        viewModel.onVisitedClicked("ao-ponto")

        assertTrue("ao-ponto" in viewModel.uiState.value.favoritePlaceIds)
        assertTrue("ao-ponto" in viewModel.uiState.value.visitedPlaceIds)

        viewModel.onFavoriteClicked("ao-ponto")
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

        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isError)
        assertEquals(listOf("ao-ponto"), viewModel.uiState.value.places.map(FeedPlaceUiModel::id))
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

        assertEquals(listOf("ao-ponto"), viewModel.uiState.value.places.map(FeedPlaceUiModel::id))
        assertTrue(viewModel.uiState.value.isStale)
        assertEquals(R.string.feed_generic_error, viewModel.uiState.value.errorMessageResId)
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
    override suspend fun invoke(
        query: String,
        location: GeoPoint?,
        includePhotos: Boolean,
    ): Result<List<PlaceSummary>> = response(query)
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

private fun feedViewModel(
    searchPlacesUseCase: SearchPlacesUseCase,
    initialLocation: GeoPoint? = null,
    favoritePlaceIds: Set<String> = emptySet(),
    visitedPlaceIds: Set<String> = emptySet(),
): FeedViewModel {
    val favoriteRepository = FakeFavoriteRepository(favoritePlaceIds)
    val visitedRepository = FakeVisitRepository(visitedPlaceIds)
    return FeedViewModel(
        searchPlacesUseCase = searchPlacesUseCase,
        observeFavoritesUseCase = ObserveFavoritesUseCase(favoriteRepository),
        toggleFavoriteUseCase = ToggleFavoriteUseCase(favoriteRepository),
        observeVisitedPlacesUseCase = ObserveVisitedPlacesUseCase(visitedRepository),
        toggleVisitedPlaceUseCase = ToggleVisitedPlaceUseCase(visitedRepository),
        initialLocation = initialLocation,
    )
}

private class FakeFavoriteRepository(
    initialIds: Set<String>,
) : FavoriteRepository.Local {
    private val ids = MutableStateFlow(initialIds)

    override fun observeIds(): Flow<Set<String>> = ids

    override suspend fun setSelected(placeId: String, selected: Boolean): Result<Unit> {
        ids.value = if (selected) ids.value + placeId else ids.value - placeId
        return Success(Unit)
    }
}

private class FakeVisitRepository(
    initialIds: Set<String>,
) : VisitRepository.Local {
    private val ids = MutableStateFlow(initialIds)

    override fun observeIds(): Flow<Set<String>> = ids

    override suspend fun setSelected(placeId: String, selected: Boolean): Result<Unit> {
        ids.value = if (selected) ids.value + placeId else ids.value - placeId
        return Success(Unit)
    }
}

private fun place(
    id: String,
    name: String,
    location: GeoPoint? = null,
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
)
