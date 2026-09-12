package com.sextou.domain.places.model

data class PlaceSummary(
    val id: String,
    val displayName: String?,
    val formattedAddress: String?,
    val location: GeoPoint?,
    val primaryType: String?,
    val primaryTypeDisplayName: String?,
    val types: List<String>,
    val businessStatus: BusinessStatus,
    val rating: Double?,
    val userRatingCount: Int?,
    val priceLevel: Int?,
    val googleMapsUri: String?,
    val providerAttribution: String,
    val photos: List<PlacePhotoReference> = emptyList(),
    /** Current status from the online Places response; it is not a cache value. */
    val isOpen: Boolean? = null,
    val liveMusic: PlaceAttribute = PlaceAttribute.NOT_AVAILABLE,
    val goodForChildren: PlaceAttribute = PlaceAttribute.NOT_AVAILABLE,
)
