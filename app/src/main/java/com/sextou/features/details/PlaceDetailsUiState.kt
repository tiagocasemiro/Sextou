package com.sextou.features.details

data class PlaceDetailsUiModel(
    val name: String,
    val address: String?,
    val phone: String?,
    val website: String?,
    val summary: String?,
    val hours: List<String>,
    val rating: Double?,
    val ratingsCount: Int?,
    val providerAttribution: String,
)

data class PlaceDetailsUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val place: PlaceDetailsUiModel? = null,
)
