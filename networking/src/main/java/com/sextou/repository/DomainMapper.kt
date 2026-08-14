package com.sextou.repository

interface DomainMapperResponse<T : Any> {
    fun mapToDomain(): T
}
