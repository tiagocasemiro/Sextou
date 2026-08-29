package com.sextou.features.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.R
import com.sextou.designsystem.R as DesignSystemR
import com.sextou.designsystem.component.brand.SextouBrand
import com.sextou.designsystem.component.searchbar.SextouSearchBar
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTheme
import com.sextou.features.feed.components.FeedBottomNavigation
import com.sextou.features.feed.components.FeedContent

@Composable
fun FeedScreen(
    uiState: FeedUiState,
    onQueryChanged: (String) -> Unit,
    onFavoriteClicked: (String) -> Unit,
    onVisitedClicked: (String) -> Unit,
    onTabSelected: (FeedTab) -> Unit,
    onFilterClicked: () -> Unit,
    onFilterDialogDismissed: () -> Unit,
    onOpenOnlyChanged: (Boolean) -> Unit,
    onPlaceClicked: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SextouColors.Background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            FeedHeader(
                query = uiState.query,
                onQueryChanged = onQueryChanged,
                onFilterClicked = onFilterClicked,
            )
            FeedContent(
                places = uiState.visiblePlaces,
                favoritePlaceIds = uiState.favoritePlaceIds,
                visitedPlaceIds = uiState.visitedPlaceIds,
                isLoading = uiState.isLoading,
                isError = uiState.isError,
                isStale = uiState.isStale,
                errorMessageResId = uiState.errorMessageResId,
                actionErrorMessageResId = uiState.actionErrorMessageResId,
                isFavoritesTab = uiState.selectedTab == FeedTab.FAVORITES,
                providerAttribution = uiState.providerAttribution,
                onPlaceClicked = onPlaceClicked,
                onFavoriteClicked = onFavoriteClicked,
                onVisitedClicked = onVisitedClicked,
                onRetry = onRetry,
                modifier = Modifier.weight(1f),
            )
        }

        FeedBottomNavigation(
            selectedTab = uiState.selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        if (uiState.isFilterDialogVisible) {
            FeedFilterDialog(
                openOnly = uiState.openOnly,
                onOpenOnlyChanged = onOpenOnlyChanged,
                onDismiss = onFilterDialogDismissed,
            )
        }
    }
}

@Composable
private fun FeedHeader(
    query: String,
    onQueryChanged: (String) -> Unit,
    onFilterClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SextouColors.Background,
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = SextouSpacing.Md + SextouSpacing.Sm,
                        top = SextouSpacing.Md,
                        end = SextouSpacing.Md + SextouSpacing.Sm,
                    ),
            ) {
                SextouBrand(
                    iconPainter = painterResource(DesignSystemR.drawable.ic_sextou_chopp),
                    title = stringResource(R.string.feed_brand_title),
                    subtitle = stringResource(R.string.feed_brand_subtitle),
                    iconContentDescription = stringResource(
                        R.string.feed_brand_icon_content_description,
                    ),
                )
            }

            SextouSearchBar(
                value = query,
                onValueChange = onQueryChanged,
                placeholder = stringResource(R.string.feed_search_placeholder),
                onFilterClick = onFilterClicked,
                filterContentDescription = stringResource(R.string.feed_filter_content_description),
                modifier = Modifier.padding(
                    start = SextouSpacing.Md + SextouSpacing.Sm,
                    top = SextouSpacing.Md,
                    end = SextouSpacing.Md + SextouSpacing.Sm,
                    bottom = SextouSpacing.Md,
                ),
            )
        }
    }
}

@Composable
private fun FeedFilterDialog(
    openOnly: Boolean,
    onOpenOnlyChanged: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.feed_filter_title))
        },
        text = {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.feed_filter_open_only))
                    Text(text = stringResource(R.string.feed_filter_open_only_description))
                }
                Switch(
                    checked = openOnly,
                    onCheckedChange = onOpenOnlyChanged,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.feed_close))
            }
        },
    )
}

@Preview(
    name = "Feed screen",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
    widthDp = 390,
    heightDp = 844,
)
@Composable
private fun FeedScreenPreview() {
    SextouTheme {
        FeedScreen(
            uiState = FeedUiState.preview(),
            onQueryChanged = {},
            onFavoriteClicked = {},
            onVisitedClicked = {},
            onTabSelected = {},
            onFilterClicked = {},
            onFilterDialogDismissed = {},
            onOpenOnlyChanged = {},
            onPlaceClicked = {},
            onRetry = {},
        )
    }
}
