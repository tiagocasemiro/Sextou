package com.sextou.domain.places.model

data class NearbySearchRequest(
    val center: GeoPoint,
    val radiusMeters: Double,
    val includedTypes: Set<String> = emptySet(),
    val excludedTypes: Set<String> = emptySet(),
    val includedPrimaryTypes: Set<String> = emptySet(),
    val excludedPrimaryTypes: Set<String> = emptySet(),
    val maxResults: Int = 20,
    val rankPreference: PlaceRankPreference = PlaceRankPreference.POPULARITY,
    val regionCode: String? = null,
    val includePhotos: Boolean = false,
)

enum class PlaceRankPreference {
    DISTANCE,
    POPULARITY,
}

data class PlaceTextSearchRequest(
    val query: String,
    val locationBiasCenter: GeoPoint? = null,
    val locationBiasRadiusMeters: Double? = null,
    val includedType: String? = null,
    val strictTypeFiltering: Boolean = false,
    val openNow: Boolean = false,
    val minRating: Double? = null,
    val maxResults: Int = 20,
    val regionCode: String? = null,
    val includePhotos: Boolean = false,
)

data class PlaceDetailsRequest(
    val placeId: String,
    val regionCode: String? = null,
)

data class PlacePhotoRequest(
    val reference: PlacePhotoReference,
    val maxWidth: Int? = null,
    val maxHeight: Int? = null,
)
