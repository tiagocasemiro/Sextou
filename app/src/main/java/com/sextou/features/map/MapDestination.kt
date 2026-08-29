package com.sextou.features.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sextou.features.feed.FeedTab

@Composable
fun MapDestination(
    query: String,
    viewModel: MapViewModel,
    onPlaceClicked: (String) -> Unit,
    onTabSelected: (FeedTab) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(query) {
        viewModel.load(query)
    }

    MapScreen(
        uiState = uiState,
        onQueryChanged = viewModel::onQueryChanged,
        onMapCenterChanged = viewModel::onMapCenterChanged,
        onSearchAreaClicked = viewModel::onSearchAreaClicked,
        onPlaceClicked = onPlaceClicked,
        onTabSelected = onTabSelected,
    )
}
