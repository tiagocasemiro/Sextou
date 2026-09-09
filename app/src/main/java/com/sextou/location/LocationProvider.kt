package com.sextou.location

interface LocationProvider {
    suspend fun getCurrentLocation(): LocationSnapshot?
}
