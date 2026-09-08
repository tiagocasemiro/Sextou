package com.sextou.features.details

import androidx.annotation.DrawableRes
import com.sextou.domain.places.model.GeoPoint

data class PlaceDetailsUiModel(
    val name: String,
    val category: String? = null,
    val address: String?,
    val phone: String?,
    val website: String?,
    val summary: String?,
    val hours: List<String>,
    val hoursSummary: String? = null,
    val distanceText: String? = null,
    val rating: Double?,
    val ratingsCount: Int?,
    val providerAttribution: String,
    val location: GeoPoint? = null,
    val priceLevel: Int? = null,
    val isOpen: Boolean? = null,
    val photoUri: String? = null,
    val photoAttribution: String? = null,
    val photoCount: Int = 0,
    val menuUri: String? = null,
    @param:DrawableRes val imageResId: Int? = null,
    val movement: PlaceDetailsMovementUiModel? = null,
    val menuItems: List<PlaceDetailsMenuItemUiModel> = emptyList(),
)

data class PlaceDetailsMovementUiModel(
    val label: String,
    val percentage: Int?,
    val bars: List<PlaceDetailsMovementBarUiModel>,
)

data class PlaceDetailsMovementBarUiModel(
    val label: String,
    val value: Int,
    val highlighted: Boolean = false,
)

data class PlaceDetailsMenuItemUiModel(
    val name: String,
    val description: String,
    val price: String,
    @param:DrawableRes val imageResId: Int? = null,
)

data class PlaceDetailsUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val place: PlaceDetailsUiModel? = null,
)
