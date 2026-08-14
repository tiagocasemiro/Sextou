package com.sextou.repository

import com.google.android.gms.common.api.ApiException
import com.sextou.domain.Error

internal fun mapPlacesError(throwable: Throwable): Error {
    val apiException = throwable as? ApiException
    return if (apiException != null) {
        Error(
            code = apiException.statusCode,
            title = "Falha ao consultar lugares",
            message = apiException.message ?: "O Google Places não conseguiu concluir a solicitação.",
        )
    } else {
        Error(
            code = generalErrorCode,
            title = "Erro inesperado",
            message = throwable.message ?: "Não foi possível consultar os lugares agora.",
        )
    }
}
