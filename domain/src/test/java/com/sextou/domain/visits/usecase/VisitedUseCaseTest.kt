package com.sextou.domain.visits.usecase

import com.sextou.domain.Result
import com.sextou.domain.Failure
import com.sextou.domain.Success
import com.sextou.domain.visits.repository.VisitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class VisitedUseCaseTest {
    private lateinit var repository: RecordingVisitRepository

    @Before
    fun setUp() {
        repository = RecordingVisitRepository()
    }

    @Test
    fun toggleRejectsBlankPlaceIdBeforeCallingRepository() = runTest {
        val result = ToggleVisitedPlaceUseCase(repository).invoke("", selected = true)

        assertTrue(result is Failure)
        assertEquals(null, repository.lastPlaceId)
    }

    @Test
    fun toggleForwardsUnselectedStateToRepository() = runTest {
        val result = ToggleVisitedPlaceUseCase(repository).invoke("place-2", selected = false)

        assertEquals(Success(Unit), result)
        assertEquals("place-2", repository.lastPlaceId)
        assertEquals(false, repository.lastSelected)
    }

    @Test
    fun observeDelegatesToLocalRepository() {
        val expected = flowOf(setOf("place-2"))
        repository.observedIds = expected

        assertEquals(expected, ObserveVisitedPlacesUseCase(repository).invoke())
    }
}

private class RecordingVisitRepository : VisitRepository.Local {
    var lastPlaceId: String? = null
    var lastSelected: Boolean? = null
    var observedIds: Flow<Set<String>> = flowOf(emptySet())

    override fun observeIds(): Flow<Set<String>> = observedIds

    override suspend fun setSelected(placeId: String, selected: Boolean): Result<Unit> {
        lastPlaceId = placeId
        lastSelected = selected
        return Success(Unit)
    }
}
