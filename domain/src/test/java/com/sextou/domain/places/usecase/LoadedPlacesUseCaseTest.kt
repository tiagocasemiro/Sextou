package com.sextou.domain.places.usecase

import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.places.model.*
import com.sextou.domain.places.repository.AutomaticRefreshRepository
import com.sextou.domain.places.repository.PlacesRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test
import kotlin.test.*
import kotlin.coroutines.ContinuationInterceptor

@OptIn(ExperimentalCoroutinesApi::class)
class LoadedPlacesUseCaseTest {
    private lateinit var remote: Remote
    private lateinit var local: Local
    private lateinit var marker: Marker
    private var day = 20260911L
    private val location = GeoPoint(-22.9, -43.2)

    @Before
    fun beforeEach() {
        remote = Remote()
        local = Local()
        marker = Marker()
        day = 20260911L
    }

    private fun TestScope.subject(scope: CoroutineScope = backgroundScope) = LoadedPlacesUseCase(
        remote, local, marker, { day }, scope, StandardTestDispatcher(testScheduler),
    )

    @Test
    fun `observes saved places immediately without requiring location or network`() = runTest {
        local.data.value = listOf(place("saved"))
        val subject = subject()
        runCurrent()
        assertEquals(local.data.value, subject.places.value)
        assertTrue(remote.requests.isEmpty())
    }

    @Test
    fun `empty successful automatic response consumes the day`() = runTest {
        val subject = subject()
        assertEquals(Success(Unit), subject.open(location))
        subject.open(location)
        assertEquals(1, remote.requests.size)
        assertEquals(3_000.0, remote.requests.single().radiusMeters)
        assertTrue(remote.requests.single().includePhotos)
        runCurrent()
        assertEquals(day, marker.day)
    }

    @Test
    fun `concurrent openings share one request and one failure`() = runTest {
        remote.gate = CompletableDeferred()
        remote.result = Failure(null)
        val subject = subject()
        val feed = async { subject.open(location) }
        val map = async { subject.open(location) }
        runCurrent()
        assertEquals(1, remote.requests.size)
        remote.gate!!.complete(Unit)
        assertIs<Failure>(feed.await())
        assertIs<Failure>(map.await())
        runCurrent()
        assertEquals(1, remote.requests.size)
        subject.open(location)
        assertEquals(2, remote.requests.size)
    }

    @Test
    fun `persisted daily marker survives new use case and next day permits refresh`() = runTest {
        subject().open(location)
        runCurrent()
        val restarted = subject()
        restarted.open(location)
        assertEquals(1, remote.requests.size)
        day++
        restarted.open(location)
        assertEquals(2, remote.requests.size)
    }

    @Test
    fun `manual searches use every fixed radius and slider endpoints independently of marker`() = runTest {
        val subject = subject()
        val radii = listOf(500.0, 1_000.0, 2_000.0, 5_000.0, 10_000.0, 50_000.0)
        radii.forEach { subject.searchManually(location, it) }
        runCurrent()
        assertEquals(0L, marker.day)
        assertEquals(radii, remote.requests.map { it.radiusMeters })
        subject.open(location)
        subject.searchManually(location, 500.0)
        subject.open(location)
        assertEquals(radii.size + 2, remote.requests.size)
    }

    @Test
    fun `late local emissions cannot erase or downgrade remote session versions`() = runTest {
        val old = place("same", "old")
        local.data.value = listOf(old, place("local"))
        remote.result = Success(listOf(old.copy(displayName = "new"), place("remote")))
        val subject = subject()
        runCurrent()
        subject.open(location)
        local.data.value = listOf(old)
        runCurrent()
        assertEquals(listOf("same", "remote"), subject.places.value.map { it.id })
        assertEquals("new", subject.places.value.first().displayName)
    }

    @Test
    fun `combines local and remote by ID without radius filtering`() = runTest {
        local.data.value = listOf(place("local").copy(location = GeoPoint(80.0, 80.0)))
        remote.result = Success(listOf(place("remote")))
        val subject = subject()
        runCurrent()
        subject.open(location)
        assertEquals(setOf("local", "remote"), subject.places.value.map { it.id }.toSet())
    }

    @Test
    fun `publishes before saving and caller cancellation does not cancel persistence`() = runTest {
        local.gate = CompletableDeferred()
        remote.result = Success(listOf(place("remote")))
        val subject = subject()
        val screen = async { subject.open(location) }
        runCurrent()
        assertEquals(listOf("remote"), subject.places.value.map { it.id })
        assertFalse(local.saved)
        screen.cancel()
        local.gate!!.complete(Unit)
        runCurrent()
        assertTrue(local.saved)
        assertEquals(day, marker.day)
    }

    @Test
    fun `closing all waiting screens does not abort shared automatic operation`() = runTest {
        remote.gate = CompletableDeferred()
        val subject = subject()
        val screen = async { subject.open(location) }
        runCurrent()
        screen.cancel()
        remote.gate!!.complete(Unit)
        runCurrent()
        subject.open(location)
        assertEquals(1, remote.requests.size)
    }

    @Test
    fun `returns remote data to presentation while saving on the injected IO dispatcher`() = runTest {
        val presentation = StandardTestDispatcher(testScheduler, "presentation")
        val io = StandardTestDispatcher(testScheduler, "io")
        val application = CoroutineScope(backgroundScope.coroutineContext + presentation)
        val subject = LoadedPlacesUseCase(remote, local, marker, { day }, application, io)
        remote.result = Success(listOf(place("remote")))
        local.gate = CompletableDeferred()

        val screen = async(presentation) {
            assertEquals(Success(Unit), subject.open(location))
            assertSame(presentation, currentCoroutineContext()[ContinuationInterceptor])
            assertEquals(listOf("remote"), subject.places.value.map { it.id })
        }
        runCurrent()

        assertTrue(screen.isCompleted)
        screen.await()
        assertSame(io, local.saveDispatcher)
        assertFalse(local.saved)
        local.gate!!.complete(Unit)
        runCurrent()
        assertTrue(local.saved)
    }

    @Test
    fun `manual results are available before database save finishes`() = runTest {
        local.gate = CompletableDeferred()
        remote.result = Success(listOf(place("manual")))
        val subject = subject()

        assertEquals(Success(Unit), subject.searchManually(location, 2_000.0))
        assertEquals(listOf("manual"), subject.places.value.map { it.id })
        assertFalse(local.saved)
        local.gate!!.complete(Unit)
        runCurrent()
        assertTrue(local.saved)
        assertEquals(0L, marker.day)
    }

    @Test
    fun `failed persistence is signaled and does not remove remote data or daily success`() = runTest {
        local.fail = true
        remote.result = Success(listOf(place("remote")))
        val subject = subject()
        subject.open(location)
        runCurrent()
        assertTrue(subject.hasLocalFailure.value)
        assertEquals("remote", subject.places.value.single().id)
        assertEquals(day, marker.day)
        subject.open(location)
        assertEquals(1, remote.requests.size)
    }

    @Test
    fun `marker failure retains in memory daily block while establishments still save`() = runTest {
        marker.failWrite = true
        val subject = subject()
        subject.open(location)
        runCurrent()
        subject.open(location)
        assertEquals(1, remote.requests.size)
        assertTrue(local.saved)
        assertTrue(subject.hasLocalFailure.value)
        subject().open(location)
        assertEquals(2, remote.requests.size)
    }

    @Test
    fun `pending marker write still blocks repeated automatic openings`() = runTest {
        marker.gate = CompletableDeferred()
        val subject = subject()
        subject.open(location)
        subject.open(location)
        assertEquals(1, remote.requests.size)
        assertEquals(0L, marker.day)
        marker.gate!!.complete(Unit)
        runCurrent()
        assertEquals(day, marker.day)
    }

    @Test
    fun `failed marker read signals failure but permits remote refresh`() = runTest {
        marker.failRead = true
        val subject = subject()
        subject.open(location)
        runCurrent()
        assertEquals(1, remote.requests.size)
        assertTrue(subject.hasLocalFailure.value)
    }

    @Test
    fun `filters names categories types and whitespace without requesting remote data`() = runTest {
        local.data.value = listOf(
            place("one", "Tonho").copy(primaryTypeDisplayName = "Espetinhos", types = listOf("meal_takeaway")),
            place("two", "Café"),
        )
        val subject = subject()
        runCurrent()
        listOf(" TONHO ", "espetinhos", "MEAL_TAKEAWAY").forEach { query ->
            assertEquals(listOf("one"), subject.filter(query).map { it.id })
        }
        assertEquals(2, subject.filter("  ").size)
        assertTrue(subject.filter("absent").isEmpty())
        assertTrue(remote.requests.isEmpty())
    }

    @Test
    fun `rejects invalid coordinates and radii without contacting remote`() = runTest {
        val subject = subject()
        assertIs<Failure>(subject.searchManually(location, 499.0))
        assertIs<Failure>(subject.searchManually(location, 50_001.0))
        assertIs<Failure>(subject.searchManually(GeoPoint(Double.NaN, 0.0), 3_000.0))
        assertTrue(remote.requests.isEmpty())
    }

    private class Remote : PlacesRepository.Remote {
        val requests = mutableListOf<NearbySearchRequest>()
        var result: Result<List<PlaceSummary>> = Success(emptyList())
        var gate: CompletableDeferred<Unit>? = null
        override suspend fun searchNearby(request: NearbySearchRequest): Result<List<PlaceSummary>> {
            requests += request
            gate?.await()
            return result
        }
        override suspend fun searchByText(request: PlaceTextSearchRequest): Result<List<PlaceSummary>> = error("Unexpected text search")
        override suspend fun getDetails(request: PlaceDetailsRequest): Result<PlaceDetails> = error("Unused")
        override suspend fun getPhoto(request: PlacePhotoRequest): Result<PlacePhoto> = error("Unused")
    }

    private class Local : PlacesRepository.Local {
        val data = MutableStateFlow<List<PlaceSummary>>(emptyList())
        var gate: CompletableDeferred<Unit>? = null
        var saved = false
        var fail = false
        var saveDispatcher: ContinuationInterceptor? = null
        override fun observeAll() = data
        override suspend fun saveAll(places: List<PlaceSummary>): Result<Unit> = error("Must preserve existing rows")
        override suspend fun saveMissing(places: List<PlaceSummary>): Result<Unit> {
            saveDispatcher = currentCoroutineContext()[ContinuationInterceptor]
            gate?.await()
            if (fail) throw IllegalStateException("Disk failure")
            saved = true
            return Success(Unit)
        }
    }

    private class Marker : AutomaticRefreshRepository.Local {
        var day = 0L
        var gate: CompletableDeferred<Unit>? = null
        var failRead = false
        var failWrite = false
        override suspend fun lastSuccessDay(): Result<Long> = if (failRead) Failure(null) else Success(day)
        override suspend fun recordSuccessDay(day: Long): Result<Unit> {
            gate?.await()
            if (failWrite) return Failure(null)
            this.day = day
            return Success(Unit)
        }
    }

    private fun place(id: String, name: String = id) = PlaceSummary(
        id, name, null, location, "bar", "Bar", listOf("bar"), BusinessStatus.UNKNOWN,
        null, null, null, null, "Google Maps",
    )
}
