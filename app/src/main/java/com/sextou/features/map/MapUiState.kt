package com.sextou.features.map

import androidx.annotation.DrawableRes

data class MapPlaceUiModel(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double?,
    val categoryText: String? = null,
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
)

data class MapUiState(
    val query: String = "",
    val places: List<MapPlaceUiModel> = emptyList(),
    val userLocation: MapUserLocationUiModel? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
)
