package com.sextou.domain.places.usecase

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.places.model.PlaceStatus
import com.sextou.domain.places.repository.PlaceStatusRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SetPlaceStatusUseCaseTest {
    @Test
    fun blankPlaceIdReturnsFailureWithoutWritingStatus() = runTest {
        val repository = RecordingPlaceStatusRepository()

        val result = SetPlaceStatusUseCase(repository).invoke(" ", PlaceStatus.FAVORITE)

        assertTrue(result is Failure)
        assertTrue(repository.calls.isEmpty())
    }

    @Test
    fun writesFavoriteStatus() = runTest {
        val repository = RecordingPlaceStatusRepository()

        val result = SetPlaceStatusUseCase(repository).invoke("place-1", PlaceStatus.FAVORITE)

        assertEquals(Success(Unit), result)
        assertEquals(listOf(StatusCall("place-1", PlaceStatus.FAVORITE)), repository.calls)
    }

    @Test
    fun writesVisitedStatus() = runTest {
        val repository = RecordingPlaceStatusRepository()

        val result = SetPlaceStatusUseCase(repository).invoke("place-1", PlaceStatus.VISITED)

        assertEquals(Success(Unit), result)
        assertEquals(listOf(StatusCall("place-1", PlaceStatus.VISITED)), repository.calls)
    }

    @Test
    fun writesIgnoredStatus() = runTest {
        val repository = RecordingPlaceStatusRepository()

        val result = SetPlaceStatusUseCase(repository).invoke("place-1", PlaceStatus.IGNORED)

        assertEquals(Success(Unit), result)
        assertEquals(listOf(StatusCall("place-1", PlaceStatus.IGNORED)), repository.calls)
    }

    @Test
    fun clearsStatusWhenStatusIsNull() = runTest {
        val repository = RecordingPlaceStatusRepository()

        val result = SetPlaceStatusUseCase(repository).invoke("place-1", null)

        assertEquals(Success(Unit), result)
        assertEquals(listOf(StatusCall("place-1", null)), repository.calls)
    }

    @Test
    fun propagatesRepositoryFailure() = runTest {
        val failure = Failure(Error(message = "Banco indisponível"))
        val repository = RecordingPlaceStatusRepository(result = failure)

        val result = SetPlaceStatusUseCase(repository).invoke("place-1", PlaceStatus.FAVORITE)

        assertEquals(failure, result)
    }
}

private data class StatusCall(
    val placeId: String,
    val status: PlaceStatus?,
)

private class RecordingPlaceStatusRepository(
    private val result: Result<Unit> = Success(Unit),
) : PlaceStatusRepository.Local {
    val calls = mutableListOf<StatusCall>()

    override suspend fun setStatus(placeId: String, status: PlaceStatus?): Result<Unit> {
        calls += StatusCall(placeId, status)
        return result
    }
}
