package com.sextou.features.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sextou.domain.places.model.GeoPoint

@Composable
fun PlaceDetailsDestination(
    placeId: String,
    viewModel: PlaceDetailsViewModel,
    onBack: () -> Unit,
    onOpenMap: (GeoPoint?) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(placeId) {
        viewModel.load(placeId)
    }

    PlaceDetailsScreen(
        uiState = uiState,
        onBack = onBack,
        onFavoriteClick = viewModel::onFavoriteClicked,
        onVisitClick = viewModel::onVisitClicked,
        onIgnoreClick = viewModel::onIgnoreClicked,
        onOpenMap = { onOpenMap(uiState.place?.location) },
    )
}
