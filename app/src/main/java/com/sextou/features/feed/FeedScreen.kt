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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.R
import com.sextou.designsystem.R as DesignSystemR
import com.sextou.designsystem.component.brand.SextouBrand
import com.sextou.designsystem.component.profilebutton.SextouProfileButton
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
    onProfileClicked: () -> Unit,
    onLocationClicked: () -> Unit,
    onPlaceClicked: (String) -> Unit,
    onMoreClicked: () -> Unit,
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
                onProfileClicked = onProfileClicked,
            )
            FeedContent(
                places = uiState.places,
                favoritePlaceIds = uiState.favoritePlaceIds,
                visitedPlaceIds = uiState.visitedPlaceIds,
                onLocationClicked = onLocationClicked,
                onPlaceClicked = onPlaceClicked,
                onFavoriteClicked = onFavoriteClicked,
                onVisitedClicked = onVisitedClicked,
                onMoreClicked = onMoreClicked,
                modifier = Modifier.weight(1f),
            )
        }

        FeedBottomNavigation(
            selectedTab = uiState.selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun FeedHeader(
    query: String,
    onQueryChanged: (String) -> Unit,
    onFilterClicked: () -> Unit,
    onProfileClicked: () -> Unit,
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
                SextouProfileButton(
                    contentDescription = stringResource(R.string.feed_profile_content_description),
                    onClick = onProfileClicked,
                    modifier = Modifier.align(Alignment.CenterEnd),
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
            onProfileClicked = {},
            onLocationClicked = {},
            onPlaceClicked = {},
            onMoreClicked = {},
        )
    }
}
