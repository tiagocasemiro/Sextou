package com.sextou.networking.adapter

import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.places.model.NearbySearchRequest
import com.sextou.domain.places.model.PlaceDetails
import com.sextou.domain.places.model.PlaceDetailsRequest
import com.sextou.domain.places.model.PlacePhoto
import com.sextou.domain.places.model.PlacePhotoRequest
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.model.PlaceTextSearchRequest
import com.sextou.domain.places.repository.PlacesRepository
import com.sextou.networking.gateway.PlacesGateway
import com.sextou.repository.fetchData
import com.sextou.repository.mapPlacesError

class PlacesRemoteImpl(
    private val gateway: PlacesGateway,
) : PlacesRepository.Remote {
    override suspend fun searchNearby(request: NearbySearchRequest): Result<List<PlaceSummary>> =
        fetchData(::mapPlacesError) {
            Success(gateway.searchNearby(request).map { it.mapToDomain() })
        }

    override suspend fun searchByText(request: PlaceTextSearchRequest): Result<List<PlaceSummary>> =
        fetchData(::mapPlacesError) {
            Success(gateway.searchByText(request).map { it.mapToDomain() })
        }

    override suspend fun getDetails(request: PlaceDetailsRequest): Result<PlaceDetails> =
        fetchData(::mapPlacesError) {
            Success(gateway.getDetails(request).mapToDomain())
        }

    override suspend fun getPhoto(request: PlacePhotoRequest): Result<PlacePhoto> =
        fetchData(::mapPlacesError) {
            Success(gateway.getPhoto(request).mapToDomain())
        }
}
