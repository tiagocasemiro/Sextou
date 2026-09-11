package com.sextou.domain.places.usecase

import com.sextou.domain.Failure
import com.sextou.domain.Loading
import com.sextou.domain.Result
import com.sextou.domain.Success
import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.places.model.NearbySearchRequest
import com.sextou.domain.places.model.PlaceRankPreference
import com.sextou.domain.places.model.PlaceSummary
import com.sextou.domain.places.model.PlaceTextSearchRequest
import com.sextou.domain.places.repository.PlacesRepository

open class SearchPlacesUseCase(
    private val repository: PlacesRepository.Remote,
) {
    open suspend operator fun invoke(
        query: String,
        location: GeoPoint?,
        includePhotos: Boolean = false,
        radiusMeters: Double = 3_000.0,
    ): Result<List<PlaceSummary>> {
        require(radiusMeters in 500.0..50_000.0)
        location?.validate()

        val result = when {
            query.isBlank() && location != null -> {
                repository.searchNearby(
                    NearbySearchRequest(
                        center = location,
                        radiusMeters = radiusMeters,
                        includedTypes = FEED_PLACE_TYPES,
                        maxResults = MAX_RESULTS,
                        rankPreference = PlaceRankPreference.POPULARITY,
                        regionCode = REGION_CODE,
                        includePhotos = includePhotos,
                    ),
                )
            }

            query.isBlank() -> Success(emptyList())

            else -> {
                repository.searchByText(
                    PlaceTextSearchRequest(
                        query = query.trim(),
                        locationBiasCenter = location,
                        locationBiasRadiusMeters = location?.let { radiusMeters },
                        maxResults = MAX_RESULTS,
                        regionCode = REGION_CODE,
                        includePhotos = includePhotos,
                    ),
                )
            }
        }

        return result.sanitize()
    }

    private fun GeoPoint.validate() {
        require(latitude in -90.0..90.0 && longitude in -180.0..180.0) {
            "A localização deve conter latitude entre -90 e 90 e longitude entre -180 e 180."
        }
    }

    private fun Result<List<PlaceSummary>>.sanitize(): Result<List<PlaceSummary>> = when (this) {
        is Success -> Success(
            data
                .filter { it.id.isNotBlank() }
                .distinctBy(PlaceSummary::id),
        )
        is Failure -> this
        is Loading<*> -> this
    }

    private companion object {
        const val MAX_RESULTS = 20
        const val REGION_CODE = "BR"

        val FEED_PLACE_TYPES = setOf(
            "bar",
            "restaurant",
            "cafe",
            "bakery",
            "meal_takeaway",
            "night_club",
        )
    }
}
