package com.sextou.domain.ignored.usecase

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.ignored.repository.IgnoredPlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IgnoredUseCaseTest {
    private lateinit var repository: RecordingIgnoredPlaceRepository

    @Before
    fun setUp() {
        repository = RecordingIgnoredPlaceRepository()
    }

    @Test
    fun `rejects a blank place id`() = runTest {
        val result = ToggleIgnoredPlaceUseCase(repository)(placeId = " ", selected = true)

        assertEquals(
            Failure(Error(message = "O identificador do estabelecimento é obrigatório.")),
            result,
        )
        assertTrue(repository.selectedCalls.isEmpty())
    }

    @Test
    fun `delegates the selected state`() = runTest {
        val result = ToggleIgnoredPlaceUseCase(repository)(placeId = "place-1", selected = true)

        assertEquals(Success(Unit), result)
        assertEquals(listOf("place-1" to true), repository.selectedCalls)
    }

    @Test
    fun `exposes the repository ids flow`() = runTest {
        repository.ids.value = setOf("place-1")

        assertEquals(setOf("place-1"), ObserveIgnoredPlacesUseCase(repository)().first())
    }
}

private class RecordingIgnoredPlaceRepository : IgnoredPlaceRepository.Local {
    val ids = MutableStateFlow<Set<String>>(emptySet())
    val selectedCalls = mutableListOf<Pair<String, Boolean>>()

    override fun observeIds(): Flow<Set<String>> = ids

    override suspend fun setSelected(placeId: String, selected: Boolean): Result<Unit> {
        selectedCalls += placeId to selected
        ids.value = if (selected) ids.value + placeId else ids.value - placeId
        return Success(Unit)
    }
}
