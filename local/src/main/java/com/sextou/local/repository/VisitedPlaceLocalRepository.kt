package com.sextou.local.repository

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.visits.repository.VisitRepository
import com.sextou.local.database.SextouDatabase
import com.sextou.local.database.VisitedPlaceEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VisitedPlaceLocalRepository(
    private val database: SextouDatabase,
) : VisitRepository.Local {
    override fun observeIds(): Flow<Set<String>> = database.visitedPlaceDao()
        .observeIds()
        .map { ids -> ids.toSet() }

    override suspend fun setSelected(
        placeId: String,
        selected: Boolean,
    ): Result<Unit> = try {
        if (selected) {
            database.visitedPlaceDao().insert(
                VisitedPlaceEntity(
                    placeId = placeId,
                    selectedAt = System.currentTimeMillis(),
                ),
            )
        } else {
            database.visitedPlaceDao().delete(placeId)
        }
        Success(Unit)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (exception: Exception) {
        Failure(
            Error(
                message = exception.message?.takeIf(String::isNotBlank)
                    ?: "Não foi possível salvar o local visitado.",
            ),
        )
    }
}
