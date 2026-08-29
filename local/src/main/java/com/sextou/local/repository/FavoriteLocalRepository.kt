package com.sextou.local.repository

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.favorites.repository.FavoriteRepository
import com.sextou.local.database.FavoriteEntity
import com.sextou.local.database.SextouDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteLocalRepository(
    private val database: SextouDatabase,
) : FavoriteRepository.Local {
    override fun observeIds(): Flow<Set<String>> = database.favoriteDao()
        .observeIds()
        .map { ids -> ids.toSet() }

    override suspend fun setSelected(
        placeId: String,
        selected: Boolean,
    ): Result<Unit> = try {
        if (selected) {
            database.favoriteDao().insert(
                FavoriteEntity(
                    placeId = placeId,
                    selectedAt = System.currentTimeMillis(),
                ),
            )
        } else {
            database.favoriteDao().delete(placeId)
        }
        Success(Unit)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (exception: Exception) {
        Failure(
            Error(
                message = exception.message?.takeIf(String::isNotBlank)
                    ?: "Não foi possível salvar o favorito.",
            ),
        )
    }
}
