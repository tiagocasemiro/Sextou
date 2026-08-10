package com.example.app.repository

import com.example.app.domain.Error
import com.example.app.domain.Failure
import com.example.app.domain.Result
import java.net.ConnectException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal const val connectErrorCode = 166
internal const val generalErrorCode = 266

suspend fun <T : Any> fetchData(dataProvider: suspend () -> Result<T>): Result<T> {
    return withContext(Dispatchers.IO) {
        try {
            dataProvider()
        } catch (e: ConnectException) {
            e.printStackTrace()
            Failure(
                    Error(
                            code = connectErrorCode,
                            title = "Falha de conexão",
                            message =
                                    "Por favor verifique sua conexão com a internet e tente novamente."
                    )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Failure(
                    Error(
                            code = generalErrorCode,
                            title = "Erro inesperado",
                            message =
                                    "Houve um erro inesperado em nossos sistemas, por favor tente novamente mais tarde."
                    )
            )
        }
    }
}
