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
import com.sextou.domain.places.model.BusinessStatus
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.model.PlaceDetailsRequest
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlacePhotoReference
import com.sextou.domain.places.model.PlacePhotoRequest
import com.sextou.domain.places.model.PlaceAttribute
import com.sextou.domain.places.model.PlaceAmenities
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
    fun successfulDetailsExposeApiRatingPriceDistanceAndPhoto() {
        val photoReference = PlacePhotoReference(
            placeId = "place-1",
            index = 0,
            width = 640,
            height = 320,
            attributionHtml = "Google Maps",
            authors = emptyList(),
            googleMapsUri = null,
            flagContentUri = null,
        )
        val repository = FakePlacesRepository(
            detailsResult = Success(
                samplePlaceDetails().copy(
                    priceLevel = 3,
                    photos = listOf(photoReference),
                ),
            ),
            photoResult = Success(
                PlacePhoto(
                    uri = "https://example.invalid/place-1.jpg",
                    attributionHtml = "Google Maps",
                    authors = emptyList(),
                    providerAttribution = "Google Maps",
                ),
            ),
        )
        val viewModel = placeDetailsViewModel(repository)
        viewModel.onLocationChanged(GeoPoint(0.0, 0.0))

        viewModel.load("place-1")

        val place = viewModel.uiState.value.place
        assertEquals(4.7, place?.rating)
        assertEquals(123, place?.ratingsCount)
        assertEquals(3, place?.priceLevel)
        assertEquals(0.0, place?.distanceMeters ?: -1.0, 0.0)
        assertEquals("https://example.invalid/place-1.jpg", place?.photoUri)
    }

    @Test
    fun successfulDetailsExposeApiPhoneAndWebsiteContacts() {
        val repository = FakePlacesRepository(
            detailsResult = Success(
                samplePlaceDetails().copy(
                    nationalPhoneNumber = " ",
                    internationalPhoneNumber = "+55 21 99999-9999",
                    websiteUri = "https://example.com",
                ),
            ),
        )
        val viewModel = placeDetailsViewModel(repository)

        viewModel.load("place-1")

        assertEquals("+55 21 99999-9999", viewModel.uiState.value.place?.phone)
        assertEquals("https://example.com", viewModel.uiState.value.place?.website)
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
    private val photoResult: Result<PlacePhoto> = Failure(null),
) : PlacesRepository.Remote {
    override suspend fun searchNearby(request: NearbySearchRequest): Result<List<PlaceSummary>> =
        Failure(null)

    override suspend fun searchByText(request: PlaceTextSearchRequest): Result<List<PlaceSummary>> =
        Failure(null)

    override suspend fun getDetails(request: PlaceDetailsRequest): Result<PlaceDetails> =
        detailsResult

    override suspend fun getPhoto(request: PlacePhotoRequest): Result<PlacePhoto> =
        photoResult
}

private fun samplePlaceDetails() = PlaceDetails(
    id = "place-1",
    resourceName = null,
    displayName = "Place 1",
    displayNameLanguageCode = "pt-BR",
    formattedAddress = null,
    shortFormattedAddress = null,
    adrFormattedAddress = null,
    addressComponents = emptyList(),
    postalAddress = null,
    location = GeoPoint(0.0, 0.0),
    viewport = null,
    plusCode = null,
    businessStatus = BusinessStatus.OPERATIONAL,
    primaryType = null,
    primaryTypeDisplayName = null,
    types = emptyList(),
    internationalPhoneNumber = null,
    nationalPhoneNumber = null,
    websiteUri = null,
    googleMapsUri = null,
    googleMapsLinks = null,
    iconMaskUrl = null,
    iconBackgroundColor = null,
    utcOffsetMinutes = null,
    timeZoneId = null,
    openingHours = null,
    currentOpeningHours = null,
    secondaryOpeningHours = emptyList(),
    currentSecondaryOpeningHours = emptyList(),
    priceLevel = null,
    priceRange = null,
    rating = 4.7,
    userRatingCount = 123,
    accessibility = null,
    parking = null,
    payment = null,
    amenities = PlaceAmenities(
        curbsidePickup = PlaceAttribute.UNKNOWN,
        delivery = PlaceAttribute.UNKNOWN,
        dineIn = PlaceAttribute.UNKNOWN,
        takeout = PlaceAttribute.UNKNOWN,
        reservable = PlaceAttribute.UNKNOWN,
        outdoorSeating = PlaceAttribute.UNKNOWN,
        liveMusic = PlaceAttribute.UNKNOWN,
        allowsDogs = PlaceAttribute.UNKNOWN,
        restroom = PlaceAttribute.UNKNOWN,
        goodForChildren = PlaceAttribute.UNKNOWN,
        goodForGroups = PlaceAttribute.UNKNOWN,
        goodForWatchingSports = PlaceAttribute.UNKNOWN,
        menuForChildren = PlaceAttribute.UNKNOWN,
        servesBeer = PlaceAttribute.UNKNOWN,
        servesWine = PlaceAttribute.UNKNOWN,
        servesCocktails = PlaceAttribute.UNKNOWN,
        servesCoffee = PlaceAttribute.UNKNOWN,
        servesBreakfast = PlaceAttribute.UNKNOWN,
        servesBrunch = PlaceAttribute.UNKNOWN,
        servesLunch = PlaceAttribute.UNKNOWN,
        servesDinner = PlaceAttribute.UNKNOWN,
        servesDessert = PlaceAttribute.UNKNOWN,
        servesVegetarianFood = PlaceAttribute.UNKNOWN,
    ),
    editorialSummary = null,
    generativeSummary = null,
    neighborhoodSummary = null,
    reviewSummary = null,
    reviews = emptyList(),
    photos = emptyList(),
    addressDescriptor = null,
    containingPlaces = emptyList(),
    subDestinations = emptyList(),
    attributions = emptyList(),
    providerAttribution = "Google Maps",
)
