package com.sextou.features.feed

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue

@Composable
fun FeedDestination(
    viewModel: FeedViewModel,
    onOpenMap: (String) -> Unit,
    onOpenPlace: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(viewModel) {
        viewModel.onScreenOpened()
        onDispose { viewModel.onScreenClosed() }
    }

    FeedScreen(
        uiState = uiState,
        onQueryChanged = viewModel::onQueryChanged,
        onFavoriteClicked = viewModel::onFavoriteClicked,
        onVisitedClicked = viewModel::onVisitedClicked,
        onTabSelected = { tab ->
            if (tab == FeedTab.MAP) {
                onOpenMap(uiState.query)
            } else {
                viewModel.onTabSelected(tab)
            }
        },
        onFilterClicked = viewModel::onFilterClicked,
        onFilterDialogDismissed = viewModel::onFilterDialogDismissed,
        onFilterOptionChanged = viewModel::onFilterOptionChanged,
        onFiltersApplied = viewModel::onFiltersApplied,
        onPlaceClicked = onOpenPlace,
        onPhotoRequested = viewModel::requestPhoto,
        onRetry = viewModel::retry,
    )
}
