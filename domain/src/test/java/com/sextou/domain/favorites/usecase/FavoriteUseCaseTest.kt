package com.sextou.domain.favorites.usecase

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.favorites.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FavoriteUseCaseTest {
    private lateinit var repository: RecordingFavoriteRepository

    @Before
    fun setUp() {
        repository = RecordingFavoriteRepository()
    }

    @Test
    fun toggleRejectsBlankPlaceIdBeforeCallingRepository() = runTest {
        val result = ToggleFavoriteUseCase(repository).invoke("  ", selected = true)

        assertTrue(result is Failure)
        assertEquals(null, repository.lastPlaceId)
    }

    @Test
    fun toggleForwardsSelectedStateToRepository() = runTest {
        val result = ToggleFavoriteUseCase(repository).invoke("place-1", selected = true)

        assertEquals(Success(Unit), result)
        assertEquals("place-1", repository.lastPlaceId)
        assertEquals(true, repository.lastSelected)
    }

    @Test
    fun observeDelegatesToLocalRepository() {
        val expected = flowOf(setOf("place-1"))

        repository.observedIds = expected

        assertEquals(expected, ObserveFavoritesUseCase(repository).invoke())
    }
}

private class RecordingFavoriteRepository : FavoriteRepository.Local {
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
