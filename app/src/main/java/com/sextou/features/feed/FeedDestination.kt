package com.sextou.features.feed

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue

@Composable
fun FeedDestination(
    viewModel: FeedViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FeedScreen(
        uiState = uiState,
        onQueryChanged = viewModel::onQueryChanged,
        onFavoriteClicked = viewModel::onFavoriteClicked,
        onVisitedClicked = viewModel::onVisitedClicked,
        onTabSelected = viewModel::onTabSelected,
        onFilterClicked = {},
        onProfileClicked = {},
        onLocationClicked = {},
        onPlaceClicked = {},
        onMoreClicked = {},
    )
}
