package com.sextou.features.map.components

import android.text.Html
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sextou.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouCornerRadius
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTextStyles
import com.sextou.designsystem.component.searchbar.SextouSearchBar
import com.sextou.features.map.MapPlaceUiModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.abs

private val MapCardShape = RoundedCornerShape(SextouCornerRadius.Surface)
private val MapCardScrim = Brush.verticalGradient(
    colors = listOf(
        Color.Black.copy(alpha = 0.5f),
        Color.Transparent,
    ),
)
private val MapTopScrim = Brush.verticalGradient(
    colors = listOf(
        SextouColors.Background,
        SextouColors.Background.copy(alpha = 0.92f),
        Color.Transparent,
    ),
)
private val MapCardWidth = 278.dp
private val MapCardHeight = 222.dp

private fun LazyListLayoutInfo.mostVisibleItem(): LazyListItemInfo? {
    val viewportEnd = viewportSize.width
    if (viewportEnd <= 0) return null

    val viewportCenter = viewportEnd / 2f
    var mostVisibleItem: LazyListItemInfo? = null
    var mostVisibleSize = -1
    var closestCenterDistance = Float.POSITIVE_INFINITY

    visibleItemsInfo.forEach { item ->
        val visibleSize = (
            minOf(item.offset + item.size, viewportEnd) -
                maxOf(item.offset, 0)
            ).coerceAtLeast(0)
        val itemCenter = item.offset + item.size / 2f
        val centerDistance = abs(itemCenter - viewportCenter)

        if (visibleSize > mostVisibleSize ||
            visibleSize == mostVisibleSize && centerDistance < closestCenterDistance
        ) {
            mostVisibleItem = item
            mostVisibleSize = visibleSize
            closestCenterDistance = centerDistance
        }
    }

    return mostVisibleItem
}

private fun LazyListLayoutInfo.isCentered(item: LazyListItemInfo): Boolean {
    val viewportCenter = viewportSize.width / 2f
    val itemCenter = item.offset + item.size / 2f
    return abs(itemCenter - viewportCenter) <= 1f
}

@Composable
internal fun MapTopChrome(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(MapTopScrim),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 56.dp, end = 20.dp),
        ) {
            SextouSearchBar(
                value = query,
                onValueChange = onQueryChanged,
                placeholder = stringResource(R.string.map_search_placeholder),
                onFilterClick = {},
                filterContentDescription = stringResource(
                    R.string.map_filter_content_description,
                ),
            )
            Spacer(modifier = Modifier.height(SextouSpacing.Lg))
            MapLocationChip()
        }
    }
}

@Composable
internal fun MapSearchAreaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentDescription = stringResource(R.string.map_search_area_content_description)
    Surface(
        modifier = modifier
            .height(48.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(SextouCornerRadius.Control),
            )
            .semantics {
                this.contentDescription = contentDescription
                role = Role.Button
            },
        onClick = onClick,
        shape = RoundedCornerShape(SextouCornerRadius.Control),
        color = SextouColors.Primary,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = SextouSpacing.Lg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.map_search_area_button),
                style = SextouTextStyles.ActionButton,
                color = SextouColors.OnPrimary,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun MapLocationChip(
    modifier: Modifier = Modifier,
) {
    val locationLabel = stringResource(R.string.map_location_label)
    val locationContentDescription = stringResource(
        R.string.map_location_content_description,
        locationLabel,
    )
    Surface(
        modifier = modifier.semantics {
            contentDescription = locationContentDescription
        },
        shape = RoundedCornerShape(SextouCornerRadius.Control),
        color = SextouColors.SurfaceImage.copy(alpha = 0.8f),
        border = BorderStroke(SextouDimensions.Border, SextouColors.Border),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = SextouSpacing.Md, vertical = SextouSpacing.Sm),
            horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_feed_location),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color.Unspecified,
            )
            Text(
                text = locationLabel,
                style = SextouTextStyles.ActionButton,
                color = SextouColors.TextPrimary,
                maxLines = 1,
            )
            Icon(
                painter = painterResource(R.drawable.ic_feed_target),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
internal fun MapFloatingActions(
    onRecenter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(top = 176.dp, end = SextouSpacing.Sm),
        verticalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
    ) {
        MapFloatingActionButton(
            contentDescription = stringResource(R.string.map_recenter_content_description),
            onClick = onRecenter,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_feed_target),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Color.Unspecified,
            )
        }
        MapFloatingActionButton(
            contentDescription = stringResource(R.string.map_help_content_description),
            onClick = {},
        ) {
            Text(
                text = stringResource(R.string.map_help_symbol),
                color = SextouColors.TextSecondary,
                style = SextouTextStyles.TitleMedium,
            )
        }
    }
}

@Composable
private fun MapFloatingActionButton(
    contentDescription: String,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier
            .size(48.dp)
            .shadow(elevation = 6.dp, shape = CircleShape)
            .semantics {
                this.contentDescription = contentDescription
                role = Role.Button
            },
        onClick = onClick,
        shape = CircleShape,
        color = SextouColors.SurfaceElevated.copy(alpha = 0.92f),
        border = BorderStroke(SextouDimensions.Border, SextouColors.Border),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

@Composable
internal fun MapPlaceCarousel(
    places: List<MapPlaceUiModel>,
    selectedPlaceId: String?,
    selectionRequest: Int,
    onPlaceCentered: (MapPlaceUiModel) -> Unit,
    onPlaceClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(MapCardHeight),
    ) {
        val horizontalPadding = ((maxWidth - MapCardWidth) / 2).coerceAtLeast(0.dp)
        val currentOnPlaceCentered = rememberUpdatedState(onPlaceCentered)
        val currentSelectedPlaceId = rememberUpdatedState(selectedPlaceId)

        LaunchedEffect(selectionRequest, places) {
            if (selectionRequest > 0) {
                val selectedIndex = places.indexOfFirst { it.id == selectedPlaceId }
                if (selectedIndex >= 0) {
                    listState.animateScrollToItem(selectedIndex)
                }
            }
        }

        LaunchedEffect(listState, places) {
            var hasScrolled = false

            snapshotFlow { listState.isScrollInProgress }
                .distinctUntilChanged()
                .collect { isScrolling ->
                    if (isScrolling) {
                        hasScrolled = true
                    } else if (hasScrolled) {
                        hasScrolled = false
                        val layoutInfo = listState.layoutInfo
                        val mostVisibleItem = layoutInfo.mostVisibleItem()
                        val place = mostVisibleItem?.let { item ->
                            places.getOrNull(item.index)
                        }

                        if (mostVisibleItem != null && place != null) {
                            val isCentered = layoutInfo.isCentered(mostVisibleItem)
                            if (place.id != currentSelectedPlaceId.value || !isCentered) {
                                currentOnPlaceCentered.value(place)
                            }
                            if (!isCentered) {
                                listState.animateScrollToItem(mostVisibleItem.index)
                            }
                        }
                    }
                }
        }

        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Md + SextouSpacing.Xs),
        ) {
            items(
                items = places,
                key = { place -> place.id },
            ) { place ->
                MapPlaceCard(
                    place = place,
                    onClick = { onPlaceClicked(place.id) },
                )
            }
        }
    }
}

@Composable
private fun MapPlaceCard(
    place: MapPlaceUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardContentDescription = stringResource(
        R.string.map_card_content_description,
        place.name,
    )
    Surface(
        modifier = modifier
            .width(MapCardWidth)
            .height(MapCardHeight)
            .semantics {
                contentDescription = cardContentDescription
            },
        onClick = onClick,
        shape = MapCardShape,
        color = SextouColors.Surface,
        border = BorderStroke(SextouDimensions.Border, SextouColors.Border),
    ) {
        Column {
            MapPlaceArtwork(place = place)
            MapPlaceDetails(place = place)
        }
    }
}

@Composable
private fun MapPlaceArtwork(
    place: MapPlaceUiModel,
) {
    val photoLoadFailed = remember(place.id, place.photoUri) { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(
                RoundedCornerShape(
                    topStart = SextouCornerRadius.Surface,
                    topEnd = SextouCornerRadius.Surface,
                ),
            )
            .background(SextouColors.SurfaceImage),
    ) {
        when {
            place.photoUri != null && !photoLoadFailed.value -> {
                AsyncImage(
                    model = place.photoUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onError = { photoLoadFailed.value = true },
                )
            }

            place.imageResId != null -> {
                Image(
                    painter = painterResource(place.imageResId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            else -> {
                Icon(
                    painter = painterResource(
                        mapEstablishmentIconResource(
                            primaryType = place.primaryType,
                            types = place.placeTypes,
                        ),
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center),
                    tint = Color.Unspecified,
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MapCardScrim),
        )
        if (place.photoUri != null && !photoLoadFailed.value) {
            place.photoAttribution?.let { attribution ->
                val attributionText = attribution.toPlainText()
                if (attributionText.isNotBlank()) {
                    Text(
                        text = attributionText,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = SextouSpacing.Md, bottom = SextouSpacing.Md)
                            .background(
                                SextouColors.Scrim.copy(alpha = 0.86f),
                                RoundedCornerShape(SextouCornerRadius.Chip),
                            )
                            .padding(horizontal = SextouSpacing.Xs, vertical = 2.dp),
                        style = SextouTextStyles.Metadata.copy(
                            fontSize = 8.sp,
                            lineHeight = 10.sp,
                        ),
                        color = SextouColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        if (place.isOpen == true) {
            MapOpenBadge(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(SextouSpacing.Md),
            )
        }
        place.highlightText?.let { highlight ->
            Text(
                text = highlight,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = SextouSpacing.Md, bottom = SextouSpacing.Md)
                    .background(SextouColors.Scrim, RoundedCornerShape(SextouCornerRadius.Chip))
                    .padding(horizontal = SextouSpacing.Sm, vertical = SextouSpacing.Xs),
                style = SextouTextStyles.Metadata.copy(
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = SextouColors.Accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun String.toPlainText(): String =
    Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString().trim()

@Composable
private fun MapOpenBadge(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(SextouColors.PositiveStrong.copy(alpha = 0.9f), CircleShape)
            .padding(horizontal = 10.dp, vertical = SextouSpacing.Xs),
        horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(SextouDimensions.StatusBadgeIndicator)
                .background(SextouColors.StatusOpenIndicator, CircleShape),
        )
        Text(
            text = stringResource(R.string.map_open_now),
            style = SextouTextStyles.StatusBadge.copy(fontSize = 11.sp),
            color = SextouColors.StatusOpenContent,
            maxLines = 1,
        )
    }
}

@Composable
private fun MapPlaceDetails(
    place: MapPlaceUiModel,
) {
    val category = place.categoryText?.takeIf(String::isNotBlank)
        ?: stringResource(R.string.map_category_unknown)
    val distance = place.distanceMeters?.let { distanceMeters ->
        if (distanceMeters < 1_000.0) {
            stringResource(R.string.map_distance_meters, distanceMeters.toInt())
        } else {
            stringResource(R.string.map_distance_kilometers, distanceMeters / 1_000.0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .padding(horizontal = SextouSpacing.Md, vertical = SextouSpacing.Md),
    ) {
        Text(
            text = category.uppercase(),
            style = SextouTextStyles.Category.copy(fontSize = 10.sp, lineHeight = 15.sp),
            color = SextouColors.TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = place.name,
                modifier = Modifier.weight(1f),
                style = SextouTextStyles.CardTitle.copy(fontSize = 15.sp, lineHeight = 22.5.sp),
                color = SextouColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            place.rating?.let { rating ->
                val ratingContentDescription = stringResource(
                    R.string.map_rating_content_description,
                    rating,
                )
                Row(
                    modifier = Modifier
                        .padding(start = SextouSpacing.Sm)
                        .semantics {
                            contentDescription = ratingContentDescription
                        },
                    horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_feed_rating_star),
                        contentDescription = null,
                        modifier = Modifier.size(10.dp),
                        tint = Color.Unspecified,
                    )
                    Text(
                        text = stringResource(R.string.map_rating_value, rating),
                        style = SextouTextStyles.ActionButton.copy(fontSize = 13.sp),
                        color = SextouColors.PrimaryStrong,
                        maxLines = 1,
                    )
                }
            }
        }
        Row(
            modifier = Modifier.padding(top = SextouSpacing.Xs),
            horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            distance?.let {
                MapMetadata(
                    painter = painterResource(R.drawable.ic_feed_distance),
                    text = it,
                )
            }
            place.priceLevel?.let { priceLevel ->
                MapPriceMetadata(priceLevel = priceLevel)
            }
        }
    }
}

@Composable
private fun MapMetadata(
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
            modifier = Modifier.size(10.dp),
            tint = Color.Unspecified,
        )
        Text(
            text = text,
            style = SextouTextStyles.Metadata.copy(fontSize = 11.sp, lineHeight = 16.5.sp),
            color = SextouColors.TextSecondary,
            maxLines = 1,
        )
    }
}

@Composable
private fun MapPriceMetadata(
    priceLevel: Int,
) {
    val priceSymbol = stringResource(R.string.map_price_symbol)
    val priceSymbols = if (priceLevel <= 0) {
        stringResource(R.string.map_price_free)
    } else {
        priceSymbol.repeat(priceLevel.coerceIn(1, 4))
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = priceSymbols,
            style = SextouTextStyles.Metadata.copy(fontSize = 11.sp, lineHeight = 16.5.sp),
            color = SextouColors.Positive,
        )
        Text(
            text = stringResource(R.string.map_price_description),
            style = SextouTextStyles.Metadata.copy(fontSize = 11.sp, lineHeight = 16.5.sp),
            color = SextouColors.TextSecondary,
            maxLines = 1,
        )
    }
}
