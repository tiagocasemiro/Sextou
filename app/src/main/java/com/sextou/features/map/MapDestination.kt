package com.sextou.features.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sextou.domain.places.model.GeoPoint
import com.sextou.features.feed.FeedTab

@Composable
fun MapDestination(
    query: String,
    focusedPlaceId: String? = null,
    focusedLocation: GeoPoint? = null,
    viewModel: MapViewModel,
    onPlaceClicked: (String) -> Unit,
    onTabSelected: (FeedTab) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(query) {
        viewModel.load(query)
    }

    LaunchedEffect(focusedLocation) {
        viewModel.setRouteDestination(focusedLocation)
    }

    MapScreen(
        uiState = uiState,
        focusedPlaceId = focusedPlaceId,
        focusedLocation = focusedLocation,
        onQueryChanged = viewModel::onQueryChanged,
        onMapCenterChanged = viewModel::onMapCenterChanged,
        onSearchAreaClicked = viewModel::onSearchAreaClicked,
        onPlaceClicked = onPlaceClicked,
        onTabSelected = onTabSelected,
    )
}
