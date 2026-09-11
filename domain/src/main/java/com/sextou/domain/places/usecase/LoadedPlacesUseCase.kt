package com.sextou.domain.places.usecase

import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.repository.AutomaticRefreshRepository
import com.sextou.domain.places.repository.PlacesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Shared for the application lifetime. Remote session versions take precedence over disk. */
class LoadedPlacesUseCase(
    remote: PlacesRepository.Remote,
    private val local: PlacesRepository.Local,
    private val automaticRefresh: AutomaticRefreshRepository.Local,
    private val currentDay: () -> Long,
    private val applicationScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
) {
    private val search = SearchPlacesUseCase(remote)
    private val automaticMutex = Mutex()
    private val dataMutex = Mutex()
    private val persistenceMutex = Mutex()
    private val markerPersistenceMutex = Mutex()
    private var automaticOperation: Deferred<Result<Unit>>? = null
    @Volatile private var lastSuccessDay = 0L
    private var savedPlaces = emptyList<PlaceSummary>()
    private val sessionPlaces = linkedMapOf<String, PlaceSummary>()
    private val mutablePlaces = MutableStateFlow<List<PlaceSummary>>(emptyList())
    private val mutableLocalFailure = MutableStateFlow(false)

    val places: StateFlow<List<PlaceSummary>> = mutablePlaces.asStateFlow()
    val hasLocalFailure: StateFlow<Boolean> = mutableLocalFailure.asStateFlow()

    init {
        applicationScope.launch(ioDispatcher) {
            local.observeAll()
                .catch { throwable ->
                    if (throwable is CancellationException) throw throwable
                    mutableLocalFailure.value = true
                }
                .collect { saved ->
                    dataMutex.withLock {
                        savedPlaces = saved
                        publish()
                    }
                }
        }
    }

    /** Call once per screen entry, after its first valid location is available. */
    suspend fun open(location: GeoPoint): Result<Unit> {
        val operation = automaticMutex.withLock {
            automaticOperation?.takeUnless { it.isCompleted } ?: applicationScope.async(
                start = CoroutineStart.LAZY,
            ) {
                refreshAutomatically(location)
            }.also { automaticOperation = it }
        }
        operation.start()
        return operation.await()
    }

    /** Explicit searches neither consume nor renew the automatic daily allowance. */
    suspend fun searchManually(location: GeoPoint, radiusMeters: Double): Result<Unit> =
        fetch(location, radiusMeters, automatic = false)

    fun filter(query: String): List<PlaceSummary> {
        val normalized = query.trim().lowercase()
        if (normalized.isEmpty()) return places.value
        return places.value.filter { place ->
            val name = place.displayName?.takeIf(String::isNotBlank) ?: place.id
            val category = place.primaryTypeDisplayName?.takeIf(String::isNotBlank)
                ?: place.primaryType?.takeIf(String::isNotBlank)
            (listOfNotNull(name, category) + place.types)
                .joinToString(" ").lowercase().contains(normalized)
        }
    }

    private suspend fun refreshAutomatically(location: GeoPoint): Result<Unit> {
        if (lastSuccessDay == currentDay()) return Success(Unit)
        when (val marker = safely { automaticRefresh.lastSuccessDay() }) {
            is Success -> if (marker.data == currentDay()) {
                lastSuccessDay = marker.data
                return Success(Unit)
            }
            else -> mutableLocalFailure.value = true
        }
        return fetch(location, AUTOMATIC_RADIUS_METERS, automatic = true)
    }

    private suspend fun fetch(
        location: GeoPoint,
        radiusMeters: Double,
        automatic: Boolean,
    ): Result<Unit> {
        val result = safely { search("", location, includePhotos = true, radiusMeters = radiusMeters) }
        if (result !is Success) return if (result is Failure) result else Failure(null)
        dataMutex.withLock {
            result.data.forEach { sessionPlaces[it.id] = it }
            publish()
        }
        if (automatic) {
            val day = currentDay()
            lastSuccessDay = day
            applicationScope.launch(ioDispatcher) {
                markerPersistenceMutex.withLock {
                    if (day == lastSuccessDay && safely { automaticRefresh.recordSuccessDay(day) } !is Success) {
                        mutableLocalFailure.value = true
                    }
                }
            }
        }
        applicationScope.launch(ioDispatcher) {
            persistenceMutex.withLock {
                if (safely { local.saveMissing(result.data) } !is Success) {
                    mutableLocalFailure.value = true
                }
            }
        }
        return Success(Unit)
    }

    private fun publish() {
        mutablePlaces.value = (sessionPlaces.values + savedPlaces).distinctBy(PlaceSummary::id)
    }

    private suspend fun <T : Any> safely(block: suspend () -> Result<T>): Result<T> = try {
        block()
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (_: Exception) {
        Failure(null)
    }

    companion object {
        const val AUTOMATIC_RADIUS_METERS = 3_000.0
    }
}
