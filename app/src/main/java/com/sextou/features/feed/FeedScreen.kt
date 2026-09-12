package com.sextou.features.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.R
import com.sextou.designsystem.R as DesignSystemR
import com.sextou.designsystem.component.brand.SextouBrand
import com.sextou.designsystem.component.filtersheet.SextouFilterSheet
import com.sextou.designsystem.component.filtersheet.SextouFilterSheetDefaults
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
    onFilterOptionChanged: (FeedFilterOption, Boolean) -> Unit,
    onFiltersApplied: () -> Unit,
    onPlaceClicked: (String) -> Unit,
    onPhotoRequested: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SextouColors.Background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            FeedHeader(
                query = uiState.query,
                onQueryChanged = onQueryChanged,
                onFilterClicked = {
                    focusManager.clearFocus(force = true)
                    keyboardController?.hide()
                    onFilterClicked()
                },
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
                photoRetryToken = uiState.photoRetryToken,
                onPhotoRequested = onPhotoRequested,
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

        uiState.draftFilterOptions?.let { draft ->
            SextouFilterSheet(
                title = stringResource(R.string.feed_filter_title),
                groups = feedFilterGroups(),
                selectedIds = draft.mapTo(mutableSetOf()) { it.name },
                applyLabel = stringResource(R.string.feed_filter_apply),
                closeContentDescription = stringResource(
                    R.string.feed_filter_close_description,
                ),
                onSelectionChanged = { id, selected ->
                    FeedFilterOption.entries
                        .firstOrNull { it.name == id }
                        ?.let { option ->
                            onFilterOptionChanged(option, selected)
                        }
                },
                onApply = onFiltersApplied,
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
private fun feedFilterGroups(): List<SextouFilterSheetDefaults.GroupData> = listOf(
    SextouFilterSheetDefaults.GroupData(
        id = "types",
        title = stringResource(R.string.feed_filter_type_title),
        layout = SextouFilterSheetDefaults.GroupLayout.CHIPS,
        options = listOf(
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.TYPE_BOTECO.name,
                label = stringResource(R.string.feed_filter_type_boteco),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.TYPE_SKEWER.name,
                label = stringResource(R.string.feed_filter_type_skewer),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.TYPE_WINE_STORE.name,
                label = stringResource(R.string.feed_filter_type_wine_store),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.TYPE_KARAOKE.name,
                label = stringResource(R.string.feed_filter_type_karaoke),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.TYPE_FOOD_TRAILER.name,
                label = stringResource(R.string.feed_filter_type_food_trailer),
            ),
        ),
    ),
    SextouFilterSheetDefaults.GroupData(
        id = "prices",
        title = stringResource(R.string.feed_filter_price_title),
        layout = SextouFilterSheetDefaults.GroupLayout.PRICE_CARDS,
        options = listOf(
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.PRICE_LOW.name,
                label = stringResource(R.string.feed_filter_price_low_symbol),
                supportingText = stringResource(R.string.feed_filter_price_low_description),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.PRICE_MEDIUM.name,
                label = stringResource(R.string.feed_filter_price_medium_symbol),
                supportingText = stringResource(R.string.feed_filter_price_medium_description),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.PRICE_HIGH.name,
                label = stringResource(R.string.feed_filter_price_high_symbol),
                supportingText = stringResource(R.string.feed_filter_price_high_description),
            ),
        ),
    ),
    SextouFilterSheetDefaults.GroupData(
        id = "other",
        title = stringResource(R.string.feed_filter_other_title),
        layout = SextouFilterSheetDefaults.GroupLayout.SWITCHES,
        options = listOf(
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.OPEN_NOW.name,
                label = stringResource(R.string.feed_filter_open_now),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.KIDS_SPACE.name,
                label = stringResource(R.string.feed_filter_kids_space),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.LIVE_MUSIC.name,
                label = stringResource(R.string.feed_filter_live_music),
            ),
        ),
    ),
    SextouFilterSheetDefaults.GroupData(
        id = "categories",
        title = stringResource(R.string.feed_filter_category_title),
        layout = SextouFilterSheetDefaults.GroupLayout.CHIPS,
        options = listOf(
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.CATEGORY_KARAOKE.name,
                label = stringResource(R.string.feed_filter_category_karaoke),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.CATEGORY_KIDS.name,
                label = stringResource(R.string.feed_filter_category_kids),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.CATEGORY_STREET_FOOD.name,
                label = stringResource(R.string.feed_filter_category_street_food),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.CATEGORY_WINE_STORE.name,
                label = stringResource(R.string.feed_filter_category_wine_store),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.CATEGORY_LIVE_MUSIC.name,
                label = stringResource(R.string.feed_filter_category_live_music),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = FeedFilterOption.CATEGORY_24_HOURS.name,
                label = stringResource(R.string.feed_filter_category_24_hours),
            ),
        ),
    ),
)

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
            onFilterOptionChanged = { _, _ -> },
            onFiltersApplied = {},
            onPlaceClicked = {},
            onPhotoRequested = {},
            onRetry = {},
        )
    }
}
