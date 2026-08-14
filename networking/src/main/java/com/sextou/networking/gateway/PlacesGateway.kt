package com.sextou.networking.gateway

import com.sextou.domain.places.model.NearbySearchRequest
import com.sextou.domain.places.model.PlaceDetailsRequest
import com.sextou.domain.places.model.PlacePhotoRequest
import com.sextou.domain.places.model.PlaceTextSearchRequest
import com.sextou.networking.response.PlaceDetailsResponse
import com.sextou.networking.response.PlacePhotoResponse
import com.sextou.networking.response.PlaceSummaryResponse

interface PlacesGateway {
    suspend fun searchNearby(request: NearbySearchRequest): List<PlaceSummaryResponse>
    suspend fun searchByText(request: PlaceTextSearchRequest): List<PlaceSummaryResponse>
    suspend fun getDetails(request: PlaceDetailsRequest): PlaceDetailsResponse
    suspend fun getPhoto(request: PlacePhotoRequest): PlacePhotoResponse
}
