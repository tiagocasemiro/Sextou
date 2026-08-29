package com.sextou.features.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sextou.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouCornerRadius
import com.sextou.designsystem.theme.SextouTextStyles
import com.sextou.designsystem.theme.SextouTheme
import com.sextou.features.feed.FeedTab

@Composable
internal fun FeedBottomNavigation(
    selectedTab: FeedTab,
    onTabSelected: (FeedTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(93.dp)
            .background(SextouColors.Surface.copy(alpha = 0.95f))
            .drawBehind {
                drawLine(
                    color = SextouColors.Border,
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx(),
                )
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 13.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            FeedBottomNavigationItem(
                selected = selectedTab == FeedTab.MAP,
                iconPainter = painterResource(
                    if (selectedTab == FeedTab.MAP) {
                        R.drawable.ic_feed_map
                    } else {
                        R.drawable.ic_feed_map_inactive
                    },
                ),
                iconWidth = if (selectedTab == FeedTab.MAP) 30.dp else 24.dp,
                iconHeight = if (selectedTab == FeedTab.MAP) 30.dp else 24.dp,
                label = stringResource(R.string.feed_navigation_map),
                labelColor = if (selectedTab == FeedTab.MAP) {
                    SextouColors.Primary
                } else {
                    SextouColors.TextSecondary
                },
                contentDescription = stringResource(
                    R.string.feed_navigation_map_content_description,
                ),
                onClick = { onTabSelected(FeedTab.MAP) },
            )
            FeedBottomNavigationItem(
                selected = selectedTab == FeedTab.FEED,
                iconPainter = painterResource(
                    if (selectedTab == FeedTab.FEED) {
                        R.drawable.ic_feed_flame_nav_selected
                    } else {
                        R.drawable.ic_feed_flame_nav
                    },
                ),
                iconWidth = if (selectedTab == FeedTab.FEED) 24.dp else 20.19.dp,
                iconHeight = if (selectedTab == FeedTab.FEED) 24.dp else 27.39.dp,
                label = stringResource(R.string.feed_navigation_feed),
                labelColor = when (selectedTab) {
                    FeedTab.MAP -> SextouColors.PrimaryStrong
                    FeedTab.FEED -> SextouColors.Primary
                    FeedTab.FAVORITES -> SextouColors.TextSecondary
                },
                contentDescription = stringResource(
                    R.string.feed_navigation_feed_content_description,
                ),
                isFeedButton = true,
                onClick = { onTabSelected(FeedTab.FEED) },
            )
            FeedBottomNavigationItem(
                selected = selectedTab == FeedTab.FAVORITES,
                iconPainter = painterResource(
                    if (selectedTab == FeedTab.FAVORITES) {
                        R.drawable.ic_feed_heart_nav_selected
                    } else {
                        R.drawable.ic_feed_heart_nav
                    },
                ),
                iconWidth = 27.77.dp,
                iconHeight = 25.dp,
                label = stringResource(R.string.feed_navigation_favorites),
                labelColor = if (selectedTab == FeedTab.FAVORITES) {
                    SextouColors.PrimaryStrong
                } else {
                    SextouColors.TextSecondary
                },
                contentDescription = stringResource(
                    R.string.feed_navigation_favorites_content_description,
                ),
                onClick = { onTabSelected(FeedTab.FAVORITES) },
            )
        }
    }
}

@Composable
private fun FeedBottomNavigationItem(
    selected: Boolean,
    iconPainter: Painter,
    iconWidth: Dp,
    iconHeight: Dp,
    label: String,
    labelColor: Color,
    contentDescription: String,
    isFeedButton: Boolean = false,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(if (isFeedButton) 64.dp else 60.dp)
            .height(56.dp)
            .then(
                if (isFeedButton) {
                    Modifier
                } else {
                    Modifier
                        .semantics {
                            this.contentDescription = contentDescription
                        }
                        .selectable(
                            selected = selected,
                            role = Role.Tab,
                            onClick = onClick,
                        )
                },
            ),
    ) {
        if (isFeedButton) {
            val feedButtonShape = RoundedCornerShape(SextouCornerRadius.Control)
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-32).dp)
                    .size(64.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = feedButtonShape,
                        clip = false,
                        ambientColor = SextouColors.Primary.copy(alpha = 0.4f),
                        spotColor = SextouColors.Primary.copy(alpha = 0.4f),
                    )
                    .clip(feedButtonShape)
                    .background(
                        color = SextouColors.Primary,
                        shape = feedButtonShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .semantics {
                            this.contentDescription = contentDescription
                        }
                        .selectable(
                            selected = selected,
                            role = Role.Tab,
                            onClick = onClick,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier.size(30.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = iconPainter,
                            contentDescription = null,
                            modifier = Modifier.size(
                                width = iconWidth,
                                height = iconHeight,
                            ),
                            tint = Color.Unspecified,
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(30.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = iconPainter,
                    contentDescription = null,
                    modifier = Modifier.size(
                        width = iconWidth,
                        height = iconHeight,
                    ),
                    tint = Color.Unspecified,
                )
            }
        }
        Text(
            text = label,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 34.dp),
            style = SextouTextStyles.NavigationLabel,
            color = labelColor,
            maxLines = 1,
        )
    }
}

@Preview(
    name = "Feed bottom navigation",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun FeedBottomNavigationPreview() {
    SextouTheme {
        FeedBottomNavigation(
            selectedTab = FeedTab.FEED,
            onTabSelected = {},
        )
    }
}
