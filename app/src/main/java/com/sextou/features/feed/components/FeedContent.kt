package com.sextou.features.feed.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sextou.R
import com.sextou.designsystem.component.sectionheader.SextouSectionHeader
import com.sextou.designsystem.component.statusbadge.SextouStatus
import com.sextou.designsystem.component.statusbadge.SextouStatusBadge
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouCornerRadius
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTextStyles
import com.sextou.designsystem.theme.SextouTheme
import com.sextou.features.feed.FeedPlaceStatus
import com.sextou.features.feed.FeedPlaceUiModel
import com.sextou.features.feed.FeedUiState
import com.sextou.features.feed.preview
import kotlin.math.roundToInt

private val FeedCardShape = RoundedCornerShape(SextouCornerRadius.Surface)
private val FeedImageShape = RoundedCornerShape(
    topStart = SextouCornerRadius.Surface,
    topEnd = SextouCornerRadius.Surface,
)
private val FeedImageScrim = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        SextouColors.Scrim,
    ),
)

@Composable
internal fun FeedContent(
    places: List<FeedPlaceUiModel>,
    favoritePlaceIds: Set<String>,
    visitedPlaceIds: Set<String>,
    isLoading: Boolean,
    isError: Boolean,
    isStale: Boolean,
    @androidx.annotation.StringRes errorMessageResId: Int?,
    @androidx.annotation.StringRes actionErrorMessageResId: Int?,
    isFavoritesTab: Boolean,
    providerAttribution: String?,
    onPlaceClicked: (String) -> Unit,
    onFavoriteClicked: (String) -> Unit,
    onVisitedClicked: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 112.dp),
    ) {
        item(key = "section") {
            SextouSectionHeader(
                text = stringResource(R.string.feed_section_title),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .padding(
                        start = SextouSpacing.Md + SextouSpacing.Sm,
                        top = SextouSpacing.Md,
                        end = SextouSpacing.Md + SextouSpacing.Sm,
                    ),
            )
        }

        if (isLoading && places.isNotEmpty()) {
            item(key = "refreshing") {
                androidx.compose.material3.LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SextouSpacing.Md + SextouSpacing.Sm),
                    color = SextouColors.Primary,
                )
            }
        }

        if (isStale && places.isNotEmpty()) {
            item(key = "stale-error") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = SextouSpacing.Md + SextouSpacing.Sm,
                            vertical = SextouSpacing.Md,
                        ),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = stringResource(R.string.feed_stale_results),
                        style = SextouTextStyles.Metadata,
                        color = SextouColors.TextSecondary,
                    )
                    TextButton(onClick = onRetry) {
                        Text(text = stringResource(R.string.feed_retry))
                    }
                }
            }
        }

        actionErrorMessageResId?.let { errorResId ->
            item(key = "local-error") {
                Text(
                    text = stringResource(errorResId),
                    modifier = Modifier.padding(
                        horizontal = SextouSpacing.Md + SextouSpacing.Sm,
                        vertical = SextouSpacing.Sm,
                    ),
                    style = SextouTextStyles.Metadata,
                    color = SextouColors.Error,
                )
            }
        }

        when {
            isLoading && places.isEmpty() -> item(key = "loading") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SextouSpacing.Xxl),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = SextouColors.Primary)
                }
            }

            isError && places.isEmpty() -> item(key = "error") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = SextouSpacing.Md + SextouSpacing.Sm,
                            vertical = SextouSpacing.Xl,
                        ),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = errorMessageResId?.let { stringResource(it) }
                            ?: stringResource(R.string.feed_generic_error),
                        style = SextouTextStyles.BodyLarge,
                        color = SextouColors.TextSecondary,
                    )
                    TextButton(onClick = onRetry) {
                        Text(text = stringResource(R.string.feed_retry))
                    }
                }
            }

            places.isEmpty() -> item(key = "empty") {
                Text(
                    text = stringResource(
                        if (isFavoritesTab) {
                            R.string.feed_empty_favorites
                        } else {
                            R.string.feed_empty_search
                        },
                    ),
                    modifier = Modifier.padding(
                        horizontal = SextouSpacing.Md + SextouSpacing.Sm,
                        vertical = SextouSpacing.Xl,
                    ),
                    style = SextouTextStyles.BodyLarge,
                    color = SextouColors.TextSecondary,
                )
            }

            else -> {
                items(
                    items = places,
                    key = { place -> place.id },
                ) { place ->
                    FeedPlaceCard(
                        place = place,
                        isFavorite = place.id in favoritePlaceIds,
                        isVisited = place.id in visitedPlaceIds,
                        onClick = { onPlaceClicked(place.id) },
                        onFavoriteClick = { onFavoriteClicked(place.id) },
                        onVisitedClick = { onVisitedClicked(place.id) },
                        modifier = Modifier
                            .padding(
                                start = SextouSpacing.Md + SextouSpacing.Sm,
                                top = SextouSpacing.Lg,
                                end = SextouSpacing.Md + SextouSpacing.Sm,
                            ),
                    )
                }
                providerAttribution?.let { attribution ->
                    item(key = "provider-attribution") {
                        Text(
                            text = stringResource(
                                R.string.feed_provider_attribution,
                                attribution,
                            ),
                            modifier = Modifier.padding(
                                start = SextouSpacing.Md + SextouSpacing.Sm,
                                top = SextouSpacing.Md,
                                end = SextouSpacing.Md + SextouSpacing.Sm,
                            ),
                            style = SextouTextStyles.Metadata,
                            color = SextouColors.TextSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedPlaceCard(
    place: FeedPlaceUiModel,
    isFavorite: Boolean,
    isVisited: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onVisitedClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val name = place.nameText
        ?: place.nameResId.takeIf { it != 0 }?.let { stringResource(it) }
        ?: place.id
    val cardContentDescription = stringResource(
        R.string.feed_card_content_description,
        name,
    )
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = cardContentDescription
            },
        shape = FeedCardShape,
        color = SextouColors.Surface,
        border = BorderStroke(SextouDimensions.Border, SextouColors.Border),
        tonalElevation = SextouDimensions.CardElevation,
    ) {
        Column {
            FeedPlaceArtwork(
                place = place,
                name = name,
                isFavorite = isFavorite,
                isVisited = isVisited,
                onFavoriteClick = onFavoriteClick,
                onVisitedClick = onVisitedClick,
            )
            FeedPlaceDetails(
                place = place,
                name = name,
            )
        }
    }
}

@Composable
private fun FeedPlaceArtwork(
    place: FeedPlaceUiModel,
    name: String,
    isFavorite: Boolean,
    isVisited: Boolean,
    onFavoriteClick: () -> Unit,
    onVisitedClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(176.dp)
            .clip(FeedImageShape)
            .background(SextouColors.SurfaceImage),
    ) {
        if (place.imageResId != null) {
            androidx.compose.foundation.Image(
                painter = painterResource(place.imageResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            )
        } else {
            FeedPlacePlaceholder(
                place = place,
                name = name,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FeedImageScrim),
        )
        place.status?.let { status ->
            SextouStatusBadge(
                status = when (status) {
                    FeedPlaceStatus.OPEN -> SextouStatus.OPEN
                    FeedPlaceStatus.CLOSED -> SextouStatus.CLOSED
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(SextouSpacing.Md),
            )
        }
        val highlight = place.highlightText
            ?: place.highlightResId.takeIf { it != 0 }?.let { stringResource(it) }
        highlight?.let {
            FeedPlaceHighlight(
                text = it,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(SextouSpacing.Md),
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(SextouSpacing.Md),
            horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Sm),
        ) {
            FeedCardActionButton(
                painter = painterResource(R.drawable.ic_feed_card_star),
                contentDescription = stringResource(
                    if (isFavorite) {
                        R.string.feed_favorite_remove_content_description
                    } else {
                        R.string.feed_favorite_content_description
                    },
                    name,
                ),
                selected = isFavorite,
                onClick = onFavoriteClick,
            )
            FeedCardActionButton(
                painter = painterResource(R.drawable.ic_feed_card_bookmark),
                contentDescription = stringResource(
                    if (isVisited) {
                        R.string.feed_visit_remove_content_description
                    } else {
                        R.string.feed_visit_content_description
                    },
                    name,
                ),
                selected = isVisited,
                onClick = onVisitedClick,
            )
        }
    }
}

@Composable
private fun FeedPlacePlaceholder(
    place: FeedPlaceUiModel,
    name: String,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SextouColors.SurfaceImage),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SextouSpacing.Sm),
        ) {
            place.placeholderEmojiResId?.let { emojiResId ->
                Text(
                    text = stringResource(emojiResId),
                    color = SextouColors.TextPrimary.copy(alpha = 0.3f),
                    fontSize = 48.sp,
                    lineHeight = 48.sp,
                )
            }
            Text(
                text = name,
                style = SextouTextStyles.Metadata,
                color = SextouColors.TextSecondary.copy(alpha = 0.6f),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun FeedPlaceHighlight(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier
            .background(
                color = SextouColors.Scrim,
                shape = RoundedCornerShape(SextouCornerRadius.Chip),
            )
            .padding(horizontal = SextouSpacing.Sm, vertical = SextouSpacing.Xs),
        style = SextouTextStyles.Metadata.copy(fontWeight = FontWeight.SemiBold),
        color = SextouColors.Accent,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun FeedCardActionButton(
    painter: Painter,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(SextouCornerRadius.Full)
    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .semantics {
                this.contentDescription = contentDescription
                role = Role.Button
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = if (selected) {
                        SextouColors.Primary
                    } else {
                        SextouColors.Scrim.copy(alpha = 0.5f)
                    },
                    shape = shape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
private fun FeedPlaceDetails(
    place: FeedPlaceUiModel,
    name: String,
) {
    val category = place.categoryText
        ?: place.categoryResId.takeIf { it != 0 }?.let { stringResource(it) }
        ?: stringResource(R.string.feed_place_category_unknown)
    val distance = when {
        place.distanceMeters != null && place.distanceMeters < 1_000.0 -> {
            stringResource(
                R.string.feed_distance_meters,
                place.distanceMeters.roundToInt(),
            )
        }

        place.distanceMeters != null -> {
            stringResource(
                R.string.feed_distance_kilometers,
                place.distanceMeters / 1_000.0,
            )
        }

        place.distanceResId != 0 -> stringResource(place.distanceResId)
        else -> null
    }
    val hours = place.hoursText
        ?: place.hoursResId.takeIf { it != 0 }?.let { stringResource(it) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(89.dp)
            .padding(horizontal = SextouSpacing.Lg, vertical = SextouSpacing.Md),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(39.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = category.uppercase(),
                    style = SextouTextStyles.Category,
                    color = SextouColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = name,
                    style = SextouTextStyles.CardTitle,
                    color = SextouColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            FeedRating(
                place = place,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .padding(top = SextouSpacing.Sm + SextouSpacing.Xs),
            horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
            verticalAlignment = Alignment.Top,
        ) {
            distance?.let {
                FeedMetadata(
                    painter = painterResource(R.drawable.ic_feed_distance),
                    text = it,
                )
            }
            place.priceLevel?.let {
                FeedPriceMetadata(place = place)
            }
            hours?.let {
                FeedMetadata(
                    painter = painterResource(R.drawable.ic_feed_clock),
                    text = it,
                )
            }
        }
    }
}

@Composable
private fun FeedRating(
    place: FeedPlaceUiModel,
) {
    if (place.rating == null && place.ratingsCount == null) return

    val ratingCount = place.ratingsCount?.let {
        androidx.compose.ui.res.pluralStringResource(
            R.plurals.feed_rating_reviews_count,
            it,
            it,
        )
    }
    val ratingContentDescription = when {
        place.rating != null && ratingCount != null -> stringResource(
            R.string.feed_rating_content_description,
            place.rating,
            ratingCount,
        )

        place.rating != null -> stringResource(
            R.string.feed_rating_only_content_description,
            place.rating,
        )

        else -> stringResource(
            R.string.feed_rating_count_only_content_description,
            ratingCount.orEmpty(),
        )
    }
    Row(
        modifier = Modifier.semantics {
            contentDescription = ratingContentDescription
        },
        horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_feed_rating_star),
            contentDescription = null,
            modifier = Modifier.size(SextouSpacing.Sm + SextouSpacing.Xs),
            tint = Color.Unspecified,
        )
        place.rating?.let {
            Text(
                text = stringResource(R.string.feed_rating_value, it),
                style = SextouTextStyles.ActionButton,
                color = SextouColors.PrimaryStrong,
                maxLines = 1,
            )
        }
        place.ratingsCount?.let {
            Text(
                text = stringResource(R.string.feed_rating_count, it),
                style = SextouTextStyles.Metadata,
                color = SextouColors.TextSecondary,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun FeedPriceMetadata(place: FeedPlaceUiModel) {
    val priceLevel = place.priceLevel ?: return
    val priceDescription = place.priceDescriptionResId
        .takeIf { it != 0 }
        ?.let { stringResource(it) }
        ?: stringResource(R.string.feed_price_level_description)
    val priceSymbols = if (priceLevel <= 0) {
        stringResource(R.string.feed_price_free)
    } else {
        stringResource(R.string.feed_price_symbol).repeat(priceLevel.coerceAtMost(4))
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_feed_price),
            contentDescription = null,
            modifier = Modifier.size(SextouSpacing.Sm + SextouSpacing.Xs),
            tint = Color.Unspecified,
        )
        Text(
            text = priceSymbols,
            style = SextouTextStyles.Metadata.copy(fontWeight = FontWeight.Bold),
            color = SextouColors.Positive,
            maxLines = 1,
        )
        Text(
            text = priceDescription,
            style = SextouTextStyles.Metadata,
            color = SextouColors.TextSecondary,
            maxLines = 1,
        )
    }
}

@Composable
private fun FeedMetadata(
    painter: Painter,
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(SextouSpacing.Sm + SextouSpacing.Xs),
            tint = Color.Unspecified,
        )
        Text(
            text = text,
            style = SextouTextStyles.Metadata,
            color = SextouColors.TextSecondary,
            maxLines = 1,
        )
    }
}

@Preview(
    name = "Feed place card",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun FeedPlaceCardPreview() {
    SextouTheme {
        FeedPlaceCard(
            place = FeedUiState.preview().places.first(),
            isFavorite = false,
            isVisited = false,
            onClick = {},
            onFavoriteClick = {},
            onVisitedClick = {},
            modifier = Modifier.padding(SextouSpacing.Lg),
        )
    }
}
