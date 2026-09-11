package com.sextou.features.map

import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.favorites.repository.FavoriteRepository
import com.sextou.domain.favorites.usecase.ObserveFavoritesUseCase
import com.sextou.domain.ignored.repository.IgnoredPlaceRepository
import com.sextou.domain.ignored.usecase.ObserveIgnoredPlacesUseCase
import com.sextou.domain.places.model.BusinessStatus
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.NearbySearchRequest
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.model.PlaceDetailsRequest
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlacePhotoReference
import com.sextou.domain.places.model.PlacePhotoRequest
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.model.PlaceTextSearchRequest
import com.sextou.domain.places.repository.PlacesRepository
import com.sextou.domain.places.usecase.GetPlacePhotoUseCase
import com.sextou.domain.places.usecase.SavePlacesUseCase
import com.sextou.domain.places.usecase.LoadedPlacesUseCase
import com.sextou.domain.places.repository.AutomaticRefreshRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import com.sextou.domain.routes.model.RoutePath
import com.sextou.domain.routes.repository.RouteRepository
import com.sextou.domain.routes.usecase.GetRouteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.junit.Before
import com.sextou.domain.Failure
import kotlinx.coroutines.CompletableDeferred
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {
    private lateinit var radiusRepository: RecordingSearchPlacesRepository

    @Before
    fun beforeEach() { radiusRepository = RecordingSearchPlacesRepository() }

    private fun radiusViewModel() = mapViewModel(
        searchRepository = radiusRepository,
        initialLocation = GeoPoint(0.0, 0.0),
    )

    @Test
    fun `typing map movement and provisional radius changes do not call remote`() {
        val viewModel = radiusViewModel()
        viewModel.onQueryChanged("bar")
        viewModel.onMapCenterChanged(GeoPoint(0.01, 0.01))
        viewModel.onSearchAreaClicked()
        viewModel.onRadiusSelected(500)
        viewModel.onCustomRadiusChanged(50_000)
        assertTrue(radiusRepository.calls.isEmpty())
    }

    @Test
    fun `first dialog uses custom three km and cancel discards draft`() {
        val viewModel = radiusViewModel()
        viewModel.onMapCenterChanged(GeoPoint(0.01, 0.01))
        viewModel.onSearchAreaClicked()
        assertNull(viewModel.uiState.value.selectedRadiusMeters)
        assertEquals(3_000, viewModel.uiState.value.customRadiusMeters)
        viewModel.onRadiusSelected(500)
        viewModel.onRadiusDismissed()
        viewModel.onSearchAreaClicked()
        assertNull(viewModel.uiState.value.selectedRadiusMeters)
        assertEquals(3_000, viewModel.uiState.value.customRadiusMeters)
        assertTrue(radiusRepository.calls.isEmpty())
    }

    @Test
    fun `confirmation uses captured center and restores last confirmed selection`() {
        val viewModel = radiusViewModel()
        val captured = GeoPoint(0.01, 0.01)
        viewModel.onMapCenterChanged(captured)
        viewModel.onSearchAreaClicked()
        viewModel.onRadiusSelected(5_000)
        viewModel.onMapCenterChanged(GeoPoint(0.02, 0.02))
        viewModel.onRadiusConfirmed()
        assertEquals(captured, radiusRepository.calls.single().location)
        assertEquals(5_000.0, radiusRepository.calls.single().radiusMeters, 0.0)
        viewModel.onSearchAreaClicked()
        assertEquals(5_000, viewModel.uiState.value.selectedRadiusMeters)
    }

    @Test
    fun `manual failure retains same area for retry and duplicate confirmation is blocked`() {
        radiusRepository.result = Failure(null)
        radiusRepository.gate = CompletableDeferred()
        val viewModel = radiusViewModel()
        val center = GeoPoint(0.01, 0.01)
        viewModel.onMapCenterChanged(center)
        viewModel.onSearchAreaClicked()
        viewModel.onRadiusConfirmed()
        viewModel.onRadiusConfirmed()
        assertEquals(1, radiusRepository.calls.size)
        assertTrue(viewModel.uiState.value.isManualSearchLoading)
        radiusRepository.gate!!.complete(Unit)
        assertTrue(viewModel.uiState.value.isRadiusDialogVisible)
        assertTrue(viewModel.uiState.value.isSearchAreaButtonVisible)
        assertTrue(viewModel.uiState.value.isError)
        viewModel.onRadiusConfirmed()
        assertEquals(listOf(center, center), radiusRepository.calls.map { it.location })
    }

    @Test
    fun `custom slider confirms minimum and maximum values in meters`() {
        val viewModel = radiusViewModel()
        listOf(500, 50_000).forEachIndexed { index, radius ->
            viewModel.onMapCenterChanged(GeoPoint(0.01 * (index + 1), 0.01))
            viewModel.onSearchAreaClicked()
            viewModel.onCustomRadiusChanged(radius)
            viewModel.onRadiusConfirmed()
        }
        assertEquals(listOf(500.0, 50_000.0), radiusRepository.calls.map { it.radiusMeters })
    }

    @Test
    fun `map action requires more than fifty meters and success resets reference`() {
        val viewModel = radiusViewModel()
        viewModel.onMapCenterChanged(GeoPoint(0.0001, 0.0))
        assertFalse(viewModel.uiState.value.isSearchAreaButtonVisible)
        viewModel.onMapCenterChanged(GeoPoint(0.001, 0.0))
        assertTrue(viewModel.uiState.value.isSearchAreaButtonVisible)
        viewModel.onSearchAreaClicked()
        viewModel.onRadiusConfirmed()
        assertFalse(viewModel.uiState.value.isSearchAreaButtonVisible)
    }

    @Test
    fun `repeated entry notification does not retry failed automatic load until next entry`() {
        radiusRepository.result = Failure(null)
        val viewModel = radiusViewModel()
        viewModel.onScreenOpened()
        viewModel.onScreenOpened()
        viewModel.onLocationChanged(GeoPoint(1.0, 1.0))
        assertEquals(1, radiusRepository.calls.size)
        viewModel.onScreenClosed()
        viewModel.onScreenOpened()
        assertEquals(2, radiusRepository.calls.size)
    }

    @Test
    fun `map excludes invalid coordinates and filters locally`() {
        radiusRepository.result = Success(listOf(
            place("valid", GeoPoint(0.0, 0.0)),
            place("invalid", GeoPoint(Double.NaN, 0.0)),
        ))
        val viewModel = radiusViewModel()
        viewModel.onScreenOpened()
        assertEquals(listOf("valid"), viewModel.uiState.value.places.map { it.id })
        viewModel.onQueryChanged("absent")
        assertTrue(viewModel.uiState.value.places.isEmpty())
        viewModel.onQueryChanged("")
        assertEquals(listOf("valid"), viewModel.uiState.value.places.map { it.id })
        assertEquals(1, radiusRepository.calls.size)
    }

    @get:Rule
    val mainDispatcherRule = MapMainDispatcherRule()

    @Test
    fun `loads map places using the current user location`() {
        val location = GeoPoint(latitude = -22.9, longitude = -43.2)
        val searchRepository = RecordingSearchPlacesRepository(
            result = Success(listOf(place(id = "place-1", location = GeoPoint(-22.91, -43.21)))),
        )
        val viewModel = mapViewModel(
            searchRepository = searchRepository,
            getPlacePhotoUseCase = GetPlacePhotoUseCase(NoOpPlacesRepository()),
            savePlacesUseCase = SavePlacesUseCase(NoOpPlacesRepository()),
            getRouteUseCase = GetRouteUseCase(NoOpRouteRepository()),
            observeFavoritesUseCase = ObserveFavoritesUseCase(EmptyFavoriteRepository()),
            observeIgnoredPlacesUseCase = ObserveIgnoredPlacesUseCase(EmptyIgnoredPlaceRepository()),
        )

        viewModel.onLocationChanged(location)
        viewModel.onLocationChanged(GeoPoint(-22.9, -43.2))
        viewModel.load(query = "")

        assertEquals(location, searchRepository.calls.single().location)
        assertEquals(true, searchRepository.calls.single().includePhotos)
        assertEquals(
            MapUserLocationUiModel(latitude = -22.9, longitude = -43.2),
            viewModel.uiState.value.userLocation,
        )
        assertEquals(listOf("place-1"), viewModel.uiState.value.places.map(MapPlaceUiModel::id))
    }

    @Test
    fun `loads persisted favorite and ignored ids for marker rendering`() {
        val viewModel = mapViewModel(
            searchRepository = RecordingSearchPlacesRepository(),
            getPlacePhotoUseCase = GetPlacePhotoUseCase(NoOpPlacesRepository()),
            savePlacesUseCase = SavePlacesUseCase(NoOpPlacesRepository()),
            getRouteUseCase = GetRouteUseCase(NoOpRouteRepository()),
            observeFavoritesUseCase = ObserveFavoritesUseCase(
                EmptyFavoriteRepository(setOf("favorite-place")),
            ),
            observeIgnoredPlacesUseCase = ObserveIgnoredPlacesUseCase(
                EmptyIgnoredPlaceRepository(setOf("ignored-place")),
            ),
        )

        assertEquals(setOf("favorite-place"), viewModel.uiState.value.favoritePlaceIds)
        assertEquals(setOf("ignored-place"), viewModel.uiState.value.ignoredPlaceIds)
    }

    @Test
    fun `loads a route from the user location to the focused establishment`() {
        val origin = GeoPoint(-22.9, -43.2)
        val destination = GeoPoint(-22.91, -43.21)
        val routeRepository = RecordingRouteRepository()
        val viewModel = mapViewModel(
            searchRepository = RecordingSearchPlacesRepository(),
            getPlacePhotoUseCase = GetPlacePhotoUseCase(NoOpPlacesRepository()),
            savePlacesUseCase = SavePlacesUseCase(NoOpPlacesRepository()),
            getRouteUseCase = GetRouteUseCase(routeRepository),
            observeFavoritesUseCase = ObserveFavoritesUseCase(EmptyFavoriteRepository()),
            observeIgnoredPlacesUseCase = ObserveIgnoredPlacesUseCase(EmptyIgnoredPlaceRepository()),
        )

        viewModel.onLocationChanged(origin, bearingDegrees = 135f)
        viewModel.setRouteDestination(destination)

        assertEquals(origin to destination, routeRepository.lastRequest)
        assertEquals(listOf(origin, destination), viewModel.uiState.value.routePoints)
        assertEquals(135f, viewModel.uiState.value.userLocation?.bearingDegrees)
        assertFalse(viewModel.uiState.value.isRouteLoading)
        assertFalse(viewModel.uiState.value.isRouteError)
    }

    @Test
    fun `waits for location then ignores further GPS and query changes`() {
        val firstLocation = GeoPoint(latitude = -22.9, longitude = -43.2)
        val secondLocation = GeoPoint(latitude = -22.91, longitude = -43.21)
        val searchRepository = RecordingSearchPlacesRepository()
        val viewModel = mapViewModel(
            searchRepository = searchRepository,
            getPlacePhotoUseCase = GetPlacePhotoUseCase(NoOpPlacesRepository()),
            savePlacesUseCase = SavePlacesUseCase(NoOpPlacesRepository()),
            getRouteUseCase = GetRouteUseCase(NoOpRouteRepository()),
            observeFavoritesUseCase = ObserveFavoritesUseCase(EmptyFavoriteRepository()),
            observeIgnoredPlacesUseCase = ObserveIgnoredPlacesUseCase(EmptyIgnoredPlaceRepository()),
        )

        viewModel.load(query = "bar")
        viewModel.onLocationChanged(firstLocation)
        viewModel.onLocationChanged(secondLocation)

        assertEquals(
            listOf(firstLocation),
            searchRepository.calls.map(LocationSearchCall::location),
        )
        assertEquals("", searchRepository.calls.last().query)
    }

    @Test
    fun `shows the search area action after the map moves to another area`() {
        val location = GeoPoint(latitude = -22.9, longitude = -43.2)
        val viewModel = mapViewModel(
            searchRepository = RecordingSearchPlacesRepository(),
            getPlacePhotoUseCase = GetPlacePhotoUseCase(NoOpPlacesRepository()),
            savePlacesUseCase = SavePlacesUseCase(NoOpPlacesRepository()),
            getRouteUseCase = GetRouteUseCase(NoOpRouteRepository()),
            observeFavoritesUseCase = ObserveFavoritesUseCase(EmptyFavoriteRepository()),
            observeIgnoredPlacesUseCase = ObserveIgnoredPlacesUseCase(EmptyIgnoredPlaceRepository()),
            initialLocation = location,
        )

        viewModel.onMapCenterChanged(GeoPoint(latitude = -22.901, longitude = -43.201))

        assertTrue(viewModel.uiState.value.isSearchAreaButtonVisible)
    }

    @Test
    fun `searches the new map center when the search area action is clicked`() {
        val initialLocation = GeoPoint(latitude = -22.9, longitude = -43.2)
        val mapCenter = GeoPoint(latitude = -22.91, longitude = -43.21)
        val searchRepository = RecordingSearchPlacesRepository(
            result = Success(listOf(place(id = "place-1", location = mapCenter))),
        )
        val viewModel = mapViewModel(
            searchRepository = searchRepository,
            getPlacePhotoUseCase = GetPlacePhotoUseCase(NoOpPlacesRepository()),
            savePlacesUseCase = SavePlacesUseCase(NoOpPlacesRepository()),
            getRouteUseCase = GetRouteUseCase(NoOpRouteRepository()),
            observeFavoritesUseCase = ObserveFavoritesUseCase(EmptyFavoriteRepository()),
            observeIgnoredPlacesUseCase = ObserveIgnoredPlacesUseCase(EmptyIgnoredPlaceRepository()),
            initialLocation = initialLocation,
        )

        viewModel.onLocationChanged(GeoPoint(-22.9, -43.2))
        viewModel.load(query = "")
        viewModel.onMapCenterChanged(mapCenter)
        viewModel.onSearchAreaClicked()
        viewModel.onRadiusConfirmed()

        assertEquals(
            listOf(initialLocation, mapCenter),
            searchRepository.calls.map(LocationSearchCall::location),
        )
        assertFalse(viewModel.uiState.value.isSearchAreaButtonVisible)
    }

    @Test
    fun `loads the resolved photo uri for a place returned with photo metadata`() {
        val reference = PlacePhotoReference(
            placeId = "place-1",
            index = 0,
            width = 1_200,
            height = 800,
            attributionHtml = null,
            authors = emptyList(),
            googleMapsUri = null,
            flagContentUri = null,
        )
        val photoRepository = NoOpPlacesRepository().apply {
            photoResult = Success(
                PlacePhoto(
                    uri = "https://example.invalid/place-1.jpg",
                    attributionHtml = "<a href=\"https://example.invalid/author\">Foto do autor</a>",
                    authors = emptyList(),
                    providerAttribution = "Google Maps",
                ),
            )
        }
        val searchRepository = RecordingSearchPlacesRepository(
            result = Success(
                listOf(
                    place(
                        id = "place-1",
                        location = GeoPoint(-22.91, -43.21),
                        photos = listOf(reference),
                    ),
                ),
            ),
        )
        val viewModel = mapViewModel(
            searchRepository = searchRepository,
            getPlacePhotoUseCase = GetPlacePhotoUseCase(photoRepository),
            savePlacesUseCase = SavePlacesUseCase(NoOpPlacesRepository()),
            getRouteUseCase = GetRouteUseCase(NoOpRouteRepository()),
            observeFavoritesUseCase = ObserveFavoritesUseCase(EmptyFavoriteRepository()),
            observeIgnoredPlacesUseCase = ObserveIgnoredPlacesUseCase(EmptyIgnoredPlaceRepository()),
        )

        viewModel.onLocationChanged(GeoPoint(-22.9, -43.2))
        viewModel.load(query = "")
        viewModel.requestPhoto("place-1")

        viewModel.onQueryChanged("absent")
        viewModel.onQueryChanged("")
        assertEquals(
            "https://example.invalid/place-1.jpg",
            viewModel.uiState.value.places.single().photoUri,
        )
        assertEquals(
            "<a href=\"https://example.invalid/author\">Foto do autor</a>",
            viewModel.uiState.value.places.single().photoAttribution,
        )
        assertEquals(
            PlacePhotoRequest(reference = reference, maxWidth = 640, maxHeight = 320),
            photoRepository.lastPhotoRequest,
        )
    }

    @Test
    fun `loads the first photo when nearby result has no photo metadata`() {
        val expectedReference = PlacePhotoReference(
            placeId = "place-1",
            index = 0,
            width = 0,
            height = 0,
            attributionHtml = null,
            authors = emptyList(),
            googleMapsUri = null,
            flagContentUri = null,
        )
        val photoRepository = NoOpPlacesRepository().apply {
            photoResult = Success(
                PlacePhoto(
                    uri = "https://example.invalid/place-1.jpg",
                    attributionHtml = null,
                    authors = emptyList(),
                    providerAttribution = "Google Maps",
                ),
            )
        }
        val viewModel = mapViewModel(
            searchRepository = RecordingSearchPlacesRepository(
                result = Success(
                    listOf(place(id = "place-1", location = GeoPoint(-22.91, -43.21))),
                ),
            ),
            getPlacePhotoUseCase = GetPlacePhotoUseCase(photoRepository),
            savePlacesUseCase = SavePlacesUseCase(NoOpPlacesRepository()),
            getRouteUseCase = GetRouteUseCase(NoOpRouteRepository()),
            observeFavoritesUseCase = ObserveFavoritesUseCase(EmptyFavoriteRepository()),
            observeIgnoredPlacesUseCase = ObserveIgnoredPlacesUseCase(EmptyIgnoredPlaceRepository()),
        )

        viewModel.onLocationChanged(GeoPoint(-22.9, -43.2))
        viewModel.load(query = "")
        viewModel.requestPhoto("place-1")

        viewModel.onQueryChanged("absent")
        viewModel.onQueryChanged("")
        assertEquals(
            "https://example.invalid/place-1.jpg",
            viewModel.uiState.value.places.single().photoUri,
        )
        assertEquals(
            PlacePhotoRequest(reference = expectedReference, maxWidth = 640, maxHeight = 320),
            photoRepository.lastPhotoRequest,
        )
    }

    @Test
    fun `keeps photo uri empty when place has no available photo`() {
        val viewModel = mapViewModel(
            searchRepository = RecordingSearchPlacesRepository(
                result = Success(
                    listOf(place(id = "place-1", location = GeoPoint(-22.91, -43.21))),
                ),
            ),
            getPlacePhotoUseCase = GetPlacePhotoUseCase(NoOpPlacesRepository()),
            savePlacesUseCase = SavePlacesUseCase(NoOpPlacesRepository()),
            getRouteUseCase = GetRouteUseCase(NoOpRouteRepository()),
            observeFavoritesUseCase = ObserveFavoritesUseCase(EmptyFavoriteRepository()),
            observeIgnoredPlacesUseCase = ObserveIgnoredPlacesUseCase(EmptyIgnoredPlaceRepository()),
        )

        viewModel.onLocationChanged(GeoPoint(-22.9, -43.2))
        viewModel.load(query = "")

        assertNull(viewModel.uiState.value.places.single().photoUri)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MapMainDispatcherRule : TestWatcher() {
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

private data class LocationSearchCall(
    val query: String,
    val location: GeoPoint?,
    val includePhotos: Boolean,
    val radiusMeters: Double,
)

private class RecordingSearchPlacesRepository(
    var result: Result<List<PlaceSummary>> = Success(emptyList()),
) : PlacesRepository.Remote by NoOpPlacesRepository() {
    val calls = mutableListOf<LocationSearchCall>()
    var gate: CompletableDeferred<Unit>? = null

    override suspend fun searchNearby(request: NearbySearchRequest): Result<List<PlaceSummary>> {
        calls += LocationSearchCall(query = "", location = request.center, includePhotos = request.includePhotos, radiusMeters = request.radiusMeters)
        gate?.await()
        return result
    }
}

private class NoOpPlacesRepository : PlacesRepository.Remote, PlacesRepository.Local {
    var lastPhotoRequest: PlacePhotoRequest? = null
    var photoResult: Result<PlacePhoto> = Success(
        PlacePhoto(
            uri = "",
            attributionHtml = null,
            authors = emptyList(),
            providerAttribution = "Google Maps",
        ),
    )

    override suspend fun searchNearby(request: NearbySearchRequest): Result<List<PlaceSummary>> =
        Success(emptyList())

    override suspend fun searchByText(request: PlaceTextSearchRequest): Result<List<PlaceSummary>> =
        Success(emptyList())

    override suspend fun getDetails(request: PlaceDetailsRequest): Result<PlaceDetails> =
        error("Not used")

    override suspend fun getPhoto(request: PlacePhotoRequest): Result<PlacePhoto> {
        lastPhotoRequest = request
        return photoResult
    }

    override fun observeAll() = flowOf(emptyList<PlaceSummary>())

    override suspend fun saveAll(places: List<PlaceSummary>): Result<Unit> = Success(Unit)

    override suspend fun saveMissing(places: List<PlaceSummary>): Result<Unit> = Success(Unit)
}

private class NoOpRouteRepository : RouteRepository.Remote {
    override suspend fun calculateRoute(
        origin: GeoPoint,
        destination: GeoPoint,
    ): Result<RoutePath> = Success(RoutePath(listOf(origin, destination)))
}

private class RecordingRouteRepository : RouteRepository.Remote {
    var lastRequest: Pair<GeoPoint, GeoPoint>? = null

    override suspend fun calculateRoute(
        origin: GeoPoint,
        destination: GeoPoint,
    ): Result<RoutePath> {
        lastRequest = origin to destination
        return Success(RoutePath(listOf(origin, destination)))
    }
}

private class EmptyFavoriteRepository(
    private val ids: Set<String> = emptySet(),
) : FavoriteRepository.Local {
    override fun observeIds() = flowOf(ids)

    override suspend fun setSelected(
        placeId: String,
        selected: Boolean,
    ): Result<Unit> = Success(Unit)
}

private class EmptyIgnoredPlaceRepository(
    private val ids: Set<String> = emptySet(),
) : IgnoredPlaceRepository.Local {
    override fun observeIds() = flowOf(ids)

    override suspend fun setSelected(
        placeId: String,
        selected: Boolean,
    ): Result<Unit> = Success(Unit)
}

private fun place(
    id: String,
    location: GeoPoint,
    photos: List<PlacePhotoReference> = emptyList(),
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
    photos = photos,
)

private val scopes = mutableListOf<CoroutineScope>()
private fun mapViewModel(
    searchRepository: PlacesRepository.Remote,
    getPlacePhotoUseCase: GetPlacePhotoUseCase = GetPlacePhotoUseCase(NoOpPlacesRepository()),
    savePlacesUseCase: SavePlacesUseCase = SavePlacesUseCase(NoOpPlacesRepository()),
    getRouteUseCase: GetRouteUseCase = GetRouteUseCase(NoOpRouteRepository()),
    observeFavoritesUseCase: ObserveFavoritesUseCase = ObserveFavoritesUseCase(EmptyFavoriteRepository()),
    observeIgnoredPlacesUseCase: ObserveIgnoredPlacesUseCase = ObserveIgnoredPlacesUseCase(EmptyIgnoredPlaceRepository()),
    initialLocation: GeoPoint? = null,
): MapViewModel {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main).also { scopes += it }
    return MapViewModel(
        loadedPlacesUseCase = LoadedPlacesUseCase(
            searchRepository, NoOpPlacesRepository(), object : AutomaticRefreshRepository.Local {
                var day = 0L
                override suspend fun lastSuccessDay(): Result<Long> = Success(day)
                override suspend fun recordSuccessDay(day: Long): Result<Unit> { this.day = day; return Success(Unit) }
            }, { 20260911L }, scope, Dispatchers.Main,
        ),
        initialLocation = initialLocation,
        getPlacePhotoUseCase = getPlacePhotoUseCase,
        getRouteUseCase = getRouteUseCase,
        observeFavoritesUseCase = observeFavoritesUseCase,
        observeIgnoredPlacesUseCase = observeIgnoredPlacesUseCase,
    )
}
