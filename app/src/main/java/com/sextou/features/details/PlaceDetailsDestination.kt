package com.sextou.features.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PlaceDetailsDestination(
    placeId: String,
    viewModel: PlaceDetailsViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(placeId) {
        viewModel.load(placeId)
    }

    PlaceDetailsScreen(
        uiState = uiState,
        onBack = onBack,
    )
}
