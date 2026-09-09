package com.sextou.features.map

import androidx.annotation.DrawableRes
import com.sextou.domain.places.model.GeoPoint

data class MapPlaceUiModel(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double?,
    val ratingsCount: Int? = null,
    val categoryText: String? = null,
    val address: String? = null,
    val googleMapsUri: String? = null,
    val highlightText: String? = null,
    val distanceMeters: Double? = null,
    val priceLevel: Int? = null,
    val photoAttribution: String? = null,
    val photoUri: String? = null,
    val primaryType: String? = null,
    val placeTypes: List<String> = emptyList(),
    @param:DrawableRes val imageResId: Int? = null,
    val isOpen: Boolean? = null,
)

data class MapUserLocationUiModel(
    val latitude: Double,
    val longitude: Double,
    val bearingDegrees: Float? = null,
)

data class MapUiState(
    val query: String = "",
    val places: List<MapPlaceUiModel> = emptyList(),
    val favoritePlaceIds: Set<String> = emptySet(),
    val ignoredPlaceIds: Set<String> = emptySet(),
    val userLocation: MapUserLocationUiModel? = null,
    val routePoints: List<GeoPoint> = emptyList(),
    val isRouteLoading: Boolean = false,
    val isRouteError: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isSearchAreaButtonVisible: Boolean = false,
)
