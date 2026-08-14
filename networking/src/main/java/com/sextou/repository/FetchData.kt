package com.sextou.repository

import com.sextou.domain.Error
import com.sextou.domain.Failure
import com.sextou.domain.Result
import java.net.ConnectException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal const val connectErrorCode = 166
internal const val generalErrorCode = 266

suspend fun <T : Any> fetchData(
    errorMapper: (Throwable) -> Error = ::defaultError,
    dataProvider: suspend () -> Result<T>,
): Result<T> = withContext(Dispatchers.IO) {
    try {
        dataProvider()
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (connection: ConnectException) {
        Failure(
            Error(
                code = connectErrorCode,
                title = "Falha de conexão",
                message = "Verifique sua conexão com a internet e tente novamente.",
            ),
        )
    } catch (exception: Exception) {
        Failure(errorMapper(exception))
    }
}

private fun defaultError(throwable: Throwable) = Error(
    code = generalErrorCode,
    title = "Erro inesperado",
    message = throwable.message ?: "Tente novamente mais tarde.",
)
