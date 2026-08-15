package com.example.app.repository

interface DomainMapperResponse<T : Any> {
    fun mapToDomain(): T
}
