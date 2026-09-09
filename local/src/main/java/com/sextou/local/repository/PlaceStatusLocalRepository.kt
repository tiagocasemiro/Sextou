package com.sextou.local.repository

import androidx.room.withTransaction
import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.places.model.PlaceStatus
import com.sextou.domain.places.repository.PlaceStatusRepository
import com.sextou.local.database.FavoriteEntity
import com.sextou.local.database.IgnoredPlaceEntity
import com.sextou.local.database.SextouDatabase
import com.sextou.local.database.VisitedPlaceEntity
import kotlinx.coroutines.CancellationException

class PlaceStatusLocalRepository(
    private val database: SextouDatabase,
) : PlaceStatusRepository.Local {
    override suspend fun setStatus(
        placeId: String,
        status: PlaceStatus?,
    ): Result<Unit> = try {
        database.withTransaction {
            database.favoriteDao().delete(placeId)
            database.visitedPlaceDao().delete(placeId)
            database.ignoredPlaceDao().delete(placeId)

            when (status) {
                PlaceStatus.FAVORITE -> database.favoriteDao().insert(
                    FavoriteEntity(
                        placeId = placeId,
                        selectedAt = System.currentTimeMillis(),
                    ),
                )

                PlaceStatus.VISITED -> database.visitedPlaceDao().insert(
                    VisitedPlaceEntity(
                        placeId = placeId,
                        selectedAt = System.currentTimeMillis(),
                    ),
                )

                PlaceStatus.IGNORED -> database.ignoredPlaceDao().insert(
                    IgnoredPlaceEntity(
                        placeId = placeId,
                        selectedAt = System.currentTimeMillis(),
                    ),
                )

                null -> Unit
            }
        }
        Success(Unit)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (exception: Exception) {
        Failure(
            Error(
                message = exception.message?.takeIf(String::isNotBlank)
                    ?: "Não foi possível salvar o status do estabelecimento.",
            ),
        )
    }
}
