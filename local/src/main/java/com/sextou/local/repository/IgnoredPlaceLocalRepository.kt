package com.sextou.local.repository

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.ignored.repository.IgnoredPlaceRepository
import com.sextou.local.database.IgnoredPlaceEntity
import com.sextou.local.database.SextouDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class IgnoredPlaceLocalRepository(
    private val database: SextouDatabase,
) : IgnoredPlaceRepository.Local {
    override fun observeIds(): Flow<Set<String>> = database.ignoredPlaceDao()
        .observeIds()
        .map { ids -> ids.toSet() }

    override suspend fun setSelected(
        placeId: String,
        selected: Boolean,
    ): Result<Unit> = try {
        if (selected) {
            database.ignoredPlaceDao().insert(
                IgnoredPlaceEntity(
                    placeId = placeId,
                    selectedAt = System.currentTimeMillis(),
                ),
            )
        } else {
            database.ignoredPlaceDao().delete(placeId)
        }
        Success(Unit)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (exception: Exception) {
        Failure(
            Error(
                message = exception.message?.takeIf(String::isNotBlank)
                    ?: "Não foi possível salvar o estabelecimento ignorado.",
            ),
        )
    }
}
