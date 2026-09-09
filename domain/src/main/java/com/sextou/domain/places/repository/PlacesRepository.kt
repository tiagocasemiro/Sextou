package com.sextou.domain.places.repository

import com.sextou.domain.Result
import com.sextou.domain.places.model.NearbySearchRequest
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.model.PlaceDetailsRequest
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlacePhotoRequest
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.model.PlaceTextSearchRequest
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {
    interface Remote {
        suspend fun searchNearby(request: NearbySearchRequest): Result<List<PlaceSummary>>
        suspend fun searchByText(request: PlaceTextSearchRequest): Result<List<PlaceSummary>>
        suspend fun getDetails(request: PlaceDetailsRequest): Result<PlaceDetails>
        suspend fun getPhoto(request: PlacePhotoRequest): Result<PlacePhoto>
    }

    interface Local {
        fun observeAll(): Flow<List<PlaceSummary>>
        suspend fun saveAll(places: List<PlaceSummary>): Result<Unit>
    }
}
