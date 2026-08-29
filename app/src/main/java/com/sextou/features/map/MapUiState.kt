package com.sextou.features.map

data class MapPlaceUiModel(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double?,
)

data class MapUiState(
    val places: List<MapPlaceUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
)
