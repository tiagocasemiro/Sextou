package com.sextou.domain.places.usecase

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.places.model.BusinessStatus
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.repository.PlacesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SavePlacesUseCaseTest {
    @Test
    fun `saves every place through the missing-only local operation`() = runTest {
        val repository = RecordingPlacesLocalRepository()
        val places = listOf(place("place-1"), place("place-2"))

        val result = SavePlacesUseCase(repository)(places)

        assertEquals(Success(Unit), result)
        assertEquals(places, repository.savedPlaces)
    }

    @Test
    fun `returns local persistence failures without changing the remote result flow`() = runTest {
        val expected = Failure(Error(message = "database unavailable"))
        val repository = RecordingPlacesLocalRepository(saveResult = expected)

        val result = SavePlacesUseCase(repository)(listOf(place("place-1")))

        assertEquals(expected, result)
    }
}

private class RecordingPlacesLocalRepository(
    private val saveResult: Result<Unit> = Success(Unit),
) : PlacesRepository.Local {
    var savedPlaces: List<PlaceSummary> = emptyList()

    override fun observeAll(): Flow<List<PlaceSummary>> = emptyFlow()

    override suspend fun saveAll(places: List<PlaceSummary>): Result<Unit> = Success(Unit)

    override suspend fun saveMissing(places: List<PlaceSummary>): Result<Unit> {
        savedPlaces = places
        return saveResult
    }
}

private fun place(id: String) = PlaceSummary(
    id = id,
    displayName = "Place $id",
    formattedAddress = null,
    location = null,
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
