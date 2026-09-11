package com.sextou.local.adapter

import com.sextou.domain.Failure
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.places.repository.AutomaticRefreshRepository
import com.sextou.local.database.AutomaticRefreshDao
import com.sextou.local.database.AutomaticRefreshEntity
import kotlinx.coroutines.CancellationException

class AutomaticRefreshLocalImpl(
    private val dao: AutomaticRefreshDao,
) : AutomaticRefreshRepository.Local {
    override suspend fun lastSuccessDay(): Result<Long> = safely {
        dao.lastSuccessDay() ?: 0L
    }

    override suspend fun recordSuccessDay(day: Long): Result<Unit> = safely {
        dao.recordSuccess(AutomaticRefreshEntity(successDay = day))
    }

    private suspend fun <T : Any> safely(block: suspend () -> T): Result<T> = try {
        Success(block())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (_: Exception) {
        Failure(null)
    }
}
