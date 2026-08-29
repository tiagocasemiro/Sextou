package com.sextou.features.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MapDestination(
    query: String,
    viewModel: MapViewModel,
    onBack: () -> Unit,
    onPlaceClicked: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(query) {
        viewModel.load(query)
    }

    MapScreen(
        uiState = uiState,
        onBack = onBack,
        onPlaceClicked = onPlaceClicked,
    )
}
