package com.sextou.features.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.sextou.R
import com.sextou.designsystem.R as DesignSystemR
import com.sextou.designsystem.component.button.SextouButton
import com.sextou.designsystem.component.button.SextouButtonDefaults
import com.sextou.designsystem.component.quickaction.SextouQuickAction
import com.sextou.designsystem.component.quickaction.SextouQuickActionDefaults
import com.sextou.designsystem.component.statusbadge.SextouStatus
import com.sextou.designsystem.component.statusbadge.SextouStatusBadge
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouCornerRadius
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTextStyles
import com.sextou.designsystem.theme.SextouTheme
import com.sextou.domain.places.model.GeoPoint

private object PlaceDetailsLayout {
    val HeroHeight = 320.dp
    val HeroControl = 40.dp
    val HeroControlIcon = 16.dp
    val ContentTopOverlap = 16.dp
    val ContentPadding = 24.dp
    val BottomBarHeight = 97.dp
    val BottomActionHeight = 56.dp
    val MapHeight = 128.dp
    val CardRadius = 16.dp
    val HoursCardHeight = 168.dp
    val HoursHorizontalPadding = 16.dp
    val HoursTopPadding = 16.dp
    val HoursBottomPadding = 7.dp
    val HoursRowHeight = 24.dp
    val HoursDayWidth = 80.dp
    val HoursTimeWidth = 110.dp
    val HoursStateWidth = 78.dp
    const val HoursVisibleRows = 3
    val RatingCardHeight = 126.dp
    val MovementCardHeight = 210.dp
    val MenuImage = 64.dp
    val MenuCardHeight = 90.dp
    val MenuImageRadius = 12.dp
    val MovementBarMaxHeight = 67.dp
}

private val PlaceDetailsShellShape = RoundedCornerShape(
    topStart = SextouCornerRadius.Surface,
    topEnd = SextouCornerRadius.Surface,
)
private val PlaceDetailsCardShape = RoundedCornerShape(PlaceDetailsLayout.CardRadius)
private val PlaceDetailsHeroScrim = Brush.verticalGradient(
    colors = listOf(
        SextouColors.Background.copy(alpha = 0.4f),
        Color.Transparent,
        SextouColors.Background,
    ),
)
private val PlaceDetailsTitleStyle = SextouTextStyles.HeadlineMedium.copy(
    fontSize = 24.sp,
    lineHeight = 30.sp,
)
private val PlaceDetailsSectionTitleStyle = SextouTextStyles.TitleMedium.copy(
    fontSize = 16.sp,
    lineHeight = 24.sp,
)
private val PlaceDetailsMetaStyle = SextouTextStyles.BodyLarge.copy(
    fontSize = 14.sp,
    lineHeight = 20.sp,
)
private val PlaceDetailsBodyStyle = SextouTextStyles.BodyLarge.copy(
    fontSize = 14.sp,
    lineHeight = 22.75.sp,
)
private val PlaceDetailsSmallStyle = SextouTextStyles.Metadata.copy(
    fontSize = 10.sp,
    lineHeight = 15.sp,
)
private val PlaceDetailsHoursHeadingStyle = SextouTextStyles.TitleMedium.copy(
    fontSize = 16.sp,
    lineHeight = 24.sp,
    fontWeight = FontWeight.Bold,
)
private val PlaceDetailsHoursStatusStyle = SextouTextStyles.Metadata.copy(
    fontSize = 12.sp,
    lineHeight = 16.sp,
    fontWeight = FontWeight.Medium,
)
private val PlaceDetailsHoursDayStyle = SextouTextStyles.BodyLarge.copy(
    fontSize = 14.sp,
    lineHeight = 20.sp,
    fontWeight = FontWeight.Bold,
)
private val PlaceDetailsHoursTimeStyle = SextouTextStyles.BodyLarge.copy(
    fontSize = 14.sp,
    lineHeight = 20.sp,
)
private val PlaceDetailsMenuTitleStyle = SextouTextStyles.TitleMedium.copy(
    fontSize = 14.sp,
    lineHeight = 20.sp,
)

@Composable
fun PlaceDetailsScreen(
    uiState: PlaceDetailsUiState,
    onBack: () -> Unit,
    onFavoriteClick: () -> Unit = {},
    onVisitClick: () -> Unit = {},
    onIgnoreClick: () -> Unit = {},
    onOpenMap: () -> Unit = {},
    onOpenMenu: () -> Unit = {},
    onShare: () -> Unit = {},
    onMore: () -> Unit = {},
    onContact: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SextouColors.Background),
    ) {
        val details = uiState.place
        when {
            details != null -> {
                PlaceDetailsScrollContent(
                    details = details,
                    isFavorite = uiState.isFavorite,
                    isVisited = uiState.isVisited,
                    isIgnored = uiState.isIgnored,
                    onBack = onBack,
                    onShare = onShare,
                    onMore = onMore,
                    onFavoriteClick = onFavoriteClick,
                    onVisitClick = onVisitClick,
                    onIgnoreClick = onIgnoreClick,
                    onOpenMap = onOpenMap,
                    onOpenMenu = onOpenMenu,
                )
                PlaceDetailsBottomBar(
                    onOpenMap = onOpenMap,
                    onContact = onContact,
                )
                if (uiState.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(SextouDimensions.Border)
                            .align(Alignment.TopCenter),
                        color = SextouColors.Primary,
                        trackColor = Color.Transparent,
                    )
                }
            }

            uiState.isLoading -> PlaceDetailsLoadingContent()
            else -> PlaceDetailsErrorContent(onBack = onBack)
        }
    }
}

@Composable
private fun PlaceDetailsScrollContent(
    details: PlaceDetailsUiModel,
    isFavorite: Boolean,
    isVisited: Boolean,
    isIgnored: Boolean,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onMore: () -> Unit,
    onFavoriteClick: () -> Unit,
    onVisitClick: () -> Unit,
    onIgnoreClick: () -> Unit,
    onOpenMap: () -> Unit,
    onOpenMenu: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = PlaceDetailsLayout.BottomBarHeight),
    ) {
        item {
            PlaceDetailsHero(
                details = details,
                onBack = onBack,
                onShare = onShare,
                onMore = onMore,
            )
        }
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = -PlaceDetailsLayout.ContentTopOverlap),
                shape = PlaceDetailsShellShape,
                color = SextouColors.Background,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = PlaceDetailsLayout.ContentPadding,
                            top = PlaceDetailsLayout.ContentPadding,
                            end = PlaceDetailsLayout.ContentPadding,
                            bottom = PlaceDetailsLayout.BottomBarHeight + PlaceDetailsLayout.ContentPadding,
                        ),
                    verticalArrangement = Arrangement.spacedBy(SextouSpacing.Lg),
                ) {
                    PlaceDetailsOverview(details = details)
                    if (details.hasMetadata()) {
                        PlaceDetailsMetadata(details = details)
                    }
                    PlaceDetailsQuickActions(
                        isFavorite = isFavorite,
                        isVisited = isVisited,
                        isIgnored = isIgnored,
                        onFavoriteClick = onFavoriteClick,
                        onVisitClick = onVisitClick,
                        onIgnoreClick = onIgnoreClick,
                    )
                    PlaceDetailsRatingCard(
                        rating = details.rating,
                        ratingsCount = details.ratingsCount,
                    )
                    details.movement?.let { movement ->
                        PlaceDetailsMovementCard(movement = movement)
                    }
                    details.location?.let { location ->
                        PlaceDetailsLocation(
                            details = details,
                            location = location,
                            onOpenMap = onOpenMap,
                        )
                    }
                    details.hoursSchedule?.let { schedule ->
                        PlaceDetailsHoursCard(schedule = schedule)
                    }
                    details.summary?.takeIf(String::isNotBlank)?.let { summary ->
                        PlaceDetailsAbout(summary = summary)
                    }
                    if (details.menuItems.isNotEmpty()) {
                        PlaceDetailsMenu(
                            details = details,
                            onOpenMenu = onOpenMenu,
                        )
                    }
                    details.providerAttribution
                        .takeIf(String::isNotBlank)
                        ?.let { attribution ->
                            Text(
                                text = stringResource(
                                    R.string.details_provider_attribution,
                                    attribution,
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
private fun PlaceDetailsHero(
    details: PlaceDetailsUiModel,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onMore: () -> Unit,
) {
    var photoLoadFailed by remember(details.photoUri) { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(PlaceDetailsLayout.HeroHeight),
    ) {
        when {
            details.photoUri != null && !photoLoadFailed -> {
                AsyncImage(
                    model = details.photoUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onError = { photoLoadFailed = true },
                )
            }

            details.imageResId != null -> {
                Image(
                    painter = painterResource(details.imageResId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SextouColors.SurfaceImage),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PlaceDetailsHeroScrim),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = PlaceDetailsLayout.ContentPadding,
                    top = PlaceDetailsLayout.ContentPadding,
                    end = PlaceDetailsLayout.ContentPadding,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            DetailsIconButton(
                painter = painterResource(R.drawable.details_action_back),
                contentDescription = stringResource(R.string.details_back_content_description),
                onClick = onBack,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Sm)) {
                DetailsIconButton(
                    painter = painterResource(R.drawable.details_action_share),
                    contentDescription = stringResource(R.string.details_share_content_description),
                    onClick = onShare,
                )
                DetailsIconButton(
                    painter = painterResource(R.drawable.details_action_more),
                    contentDescription = stringResource(R.string.details_more_content_description),
                    onClick = onMore,
                )
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(
                    start = PlaceDetailsLayout.ContentPadding,
                    end = PlaceDetailsLayout.ContentPadding,
                    bottom = PlaceDetailsLayout.ContentPadding,
                ),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            PlaceDetailsPageIndicators(count = details.photoCount)
            if (details.isOpen == true) {
                SextouStatusBadge(status = SextouStatus.OPEN)
            }
        }
        details.photoAttribution?.takeIf(String::isNotBlank)?.let { attribution ->
            Text(
                text = attribution,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        start = PlaceDetailsLayout.ContentPadding,
                        bottom = 4.dp,
                    )
                    .background(
                        color = SextouColors.Scrim,
                        shape = RoundedCornerShape(SextouCornerRadius.Chip),
                    )
                    .padding(horizontal = SextouSpacing.Xs, vertical = 2.dp),
                style = PlaceDetailsSmallStyle,
                color = SextouColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun DetailsIconButton(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .size(PlaceDetailsLayout.HeroControl)
            .semantics {
                this.contentDescription = contentDescription
            },
        onClick = onClick,
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.5f),
        contentColor = Color.White,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(PlaceDetailsLayout.HeroControlIcon),
            )
        }
    }
}

@Composable
private fun PlaceDetailsPageIndicators(count: Int) {
    val indicatorCount = count.coerceIn(1, 5)
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(indicatorCount) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (index == 0) {
                            SextouColors.Primary
                        } else {
                            Color.White.copy(alpha = 0.3f)
                        },
                        shape = CircleShape,
                    ),
            )
        }
    }
}

@Composable
private fun PlaceDetailsOverview(details: PlaceDetailsUiModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SextouSpacing.Xs),
    ) {
        details.category?.takeIf(String::isNotBlank)?.let { category ->
            Text(
                text = category,
                style = SextouTextStyles.Category,
                color = SextouColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = details.name,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = SextouSpacing.Sm),
                style = PlaceDetailsTitleStyle,
                color = SextouColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (details.rating != null || details.ratingsCount != null) {
                PlaceDetailsRatingBadge(
                    rating = details.rating,
                    ratingsCount = details.ratingsCount,
                )
            }
        }
    }
}

@Composable
private fun PlaceDetailsRatingBadge(
    rating: Double?,
    ratingsCount: Int?,
) {
    Surface(
        shape = RoundedCornerShape(SextouCornerRadius.Base),
        color = SextouColors.SurfaceElevated,
        border = BorderStroke(SextouDimensions.Border, SextouColors.Border),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = SextouSpacing.Xs, vertical = SextouSpacing.Xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Xs),
        ) {
            Image(
                painter = painterResource(R.drawable.details_rating_star),
                contentDescription = null,
                modifier = Modifier.size(11.dp, 10.dp),
            )
            rating?.let {
                Text(
                    text = stringResource(R.string.details_rating_value, it),
                    style = PlaceDetailsMetaStyle.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    ),
                    color = SextouColors.PrimaryStrong,
                )
            }
            ratingsCount?.let {
                Text(
                    text = stringResource(R.string.details_rating_count_short, it),
                    style = SextouTextStyles.Metadata,
                    color = SextouColors.TextSecondary,
                )
            }
        }
    }
}

@Composable
private fun PlaceDetailsMetadata(details: PlaceDetailsUiModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Lg),
    ) {
        details.distanceText?.let { distance ->
            DetailsMetadataItem(
                icon = painterResource(R.drawable.details_meta_distance),
                text = distance,
            )
        }
        details.priceLevel?.let { level ->
            val price = stringResource(R.string.details_price_symbol).repeat(level.coerceIn(1, 4))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Xs),
            ) {
                Text(
                    text = price,
                    style = PlaceDetailsMetaStyle.copy(fontWeight = FontWeight.Bold),
                    color = SextouColors.Positive,
                )
                Text(
                    text = stringResource(priceDescription(level)),
                    style = PlaceDetailsMetaStyle,
                    color = SextouColors.TextSecondary,
                )
            }
        }
        details.hoursSummary?.let { hours ->
            DetailsMetadataItem(
                icon = painterResource(R.drawable.details_meta_clock),
                text = hours,
            )
        }
    }
}

@Composable
private fun DetailsMetadataItem(
    icon: Painter,
    text: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Xs),
    ) {
        Image(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
        )
        Text(
            text = text,
            style = PlaceDetailsMetaStyle,
            color = SextouColors.TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun PlaceDetailsQuickActions(
    isFavorite: Boolean,
    isVisited: Boolean,
    isIgnored: Boolean,
    onFavoriteClick: () -> Unit,
    onVisitClick: () -> Unit,
    onIgnoreClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        SextouQuickAction(
            action = SextouQuickActionDefaults.Action.FAVORITAR,
            selected = isFavorite,
            onClick = onFavoriteClick,
        )
        SextouQuickAction(
            action = SextouQuickActionDefaults.Action.VISITAR,
            selected = isVisited,
            onClick = onVisitClick,
        )
        SextouQuickAction(
            action = SextouQuickActionDefaults.Action.IGNORAR,
            selected = isIgnored,
            onClick = onIgnoreClick,
        )
    }
}

@Composable
private fun PlaceDetailsRatingCard(
    rating: Double?,
    ratingsCount: Int?,
) {
    val normalizedRating = rating?.coerceIn(0.0, 5.0)
    val ratingDescription = normalizedRating?.let {
        stringResource(R.string.details_google_rating_content_description, it)
    } ?: stringResource(R.string.details_google_rating_unavailable_content_description)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(PlaceDetailsLayout.RatingCardHeight),
        shape = PlaceDetailsCardShape,
        color = SextouColors.Surface,
        border = BorderStroke(SextouDimensions.Border, SextouColors.Border),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SextouSpacing.Lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
        ) {
            Text(
                text = stringResource(R.string.details_google_rating_prompt),
                style = SextouTextStyles.SectionTitle,
                color = SextouColors.TextPrimary,
            )
            Row(
                modifier = Modifier.semantics {
                    contentDescription = ratingDescription
                },
                horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(5) { index ->
                    PlaceDetailsRatingStar(
                        fillFraction = normalizedRating
                            ?.minus(index)
                            ?.coerceIn(0.0, 1.0)
                            ?.toFloat()
                            ?: 0f,
                    )
                }
                normalizedRating?.let {
                    Text(
                        text = stringResource(R.string.details_rating_value, it),
                        style = PlaceDetailsMetaStyle.copy(fontWeight = FontWeight.Bold),
                        color = SextouColors.PrimaryStrong,
                    )
                }
                ratingsCount?.let {
                    Text(
                        text = stringResource(R.string.details_rating_count_short, it),
                        style = PlaceDetailsSmallStyle,
                        color = SextouColors.TextSecondary,
                    )
                }
            }
            Text(
                text = if (normalizedRating == null) {
                    stringResource(R.string.details_google_rating_unavailable)
                } else {
                    stringResource(R.string.details_google_rating_hint)
                },
                style = PlaceDetailsSmallStyle,
                color = SextouColors.TextSecondary,
            )
        }
    }
}

@Composable
private fun PlaceDetailsRatingStar(fillFraction: Float) {
    Box(
        modifier = Modifier
            .width(27.dp)
            .height(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_feed_card_star),
            contentDescription = null,
            tint = SextouColors.TextSecondary,
            modifier = Modifier.size(27.dp, 24.dp),
        )
        if (fillFraction > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fillFraction)
                    .clip(RoundedCornerShape(0.dp)),
                contentAlignment = Alignment.CenterStart,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_feed_rating_star),
                    contentDescription = null,
                    tint = SextouColors.PrimaryStrong,
                    modifier = Modifier.size(27.dp, 24.dp),
                )
            }
        }
    }
}

@Composable
private fun PlaceDetailsMovementCard(movement: PlaceDetailsMovementUiModel) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(PlaceDetailsLayout.MovementCardHeight),
        shape = PlaceDetailsCardShape,
        color = SextouColors.Surface,
        border = BorderStroke(SextouDimensions.Border, SextouColors.Border),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SextouSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Sm),
                ) {
                    Image(
                        painter = painterResource(DesignSystemR.drawable.ic_sextou_flame),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp, 14.dp),
                    )
                    Text(
                        text = stringResource(R.string.details_movement_title),
                        style = SextouTextStyles.LabelSmall.copy(fontWeight = FontWeight.Bold),
                        color = SextouColors.TextPrimary,
                        maxLines = 2,
                    )
                }
                Surface(
                    modifier = Modifier.width(121.dp),
                    shape = RoundedCornerShape(SextouCornerRadius.Small),
                    color = SextouColors.PositiveStrong.copy(alpha = 0.16f),
                ) {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = SextouSpacing.Sm,
                            vertical = SextouSpacing.Sm,
                        ),
                        verticalArrangement = Arrangement.spacedBy(SextouSpacing.Xs),
                    ) {
                        Text(
                            text = movement.label,
                            style = PlaceDetailsSmallStyle,
                            color = SextouColors.Positive,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        movement.percentage?.let { percentage ->
                            Text(
                                text = stringResource(
                                    R.string.details_movement_percentage,
                                    percentage,
                                ),
                                style = PlaceDetailsSmallStyle,
                                color = SextouColors.Positive,
                            )
                        }
                    }
                }
            }
            PlaceDetailsMovementChart(bars = movement.bars)
        }
    }
}

@Composable
private fun PlaceDetailsMovementChart(bars: List<PlaceDetailsMovementBarUiModel>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = SextouDimensions.Border),
            horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Sm),
            verticalAlignment = Alignment.Bottom,
        ) {
            bars.forEach { bar ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(
                                (PlaceDetailsLayout.MovementBarMaxHeight.value *
                                    bar.value.coerceIn(0, 100) / 100f).dp,
                            )
                            .background(
                                color = if (bar.highlighted) {
                                    SextouColors.Primary
                                } else {
                                    SextouColors.SurfaceElevated
                                },
                            ),
                    )
                    Spacer(modifier = Modifier.height(SextouSpacing.Sm))
                    Text(
                        text = bar.label,
                        style = PlaceDetailsSmallStyle,
                        color = SextouColors.TextSecondary,
                        maxLines = 1,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(SextouDimensions.Border)
                .background(SextouColors.OutlineVariant),
        )
    }
}

@Composable
private fun PlaceDetailsLocation(
    details: PlaceDetailsUiModel,
    location: GeoPoint,
    onOpenMap: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            val openMapDescription = stringResource(
                R.string.details_open_map_content_description,
            )
            Text(
                text = stringResource(R.string.details_location),
                style = PlaceDetailsSectionTitleStyle,
                color = SextouColors.TextPrimary,
            )
            SextouButton(
                label = stringResource(R.string.details_open_map),
                onClick = onOpenMap,
                modifier = Modifier.semantics {
                    this.contentDescription = openMapDescription
                },
                size = SextouButtonDefaults.Size.Small,
                style = SextouButtonDefaults.ghostStyle(),
            )
        }
        DetailsMap(location = location)
        details.address?.takeIf(String::isNotBlank)?.let { address ->
            Row(
                modifier = Modifier.padding(top = SextouSpacing.Xs),
                horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Sm),
                verticalAlignment = Alignment.Top,
            ) {
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = address,
                    style = PlaceDetailsMetaStyle,
                    color = SextouColors.TextSecondary,
                )
            }
        }
    }
}

@Composable
private fun DetailsMap(location: GeoPoint) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val mapStyleOptions = remember(context) {
        runCatching {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
        }.getOrNull()
    }
    val mapProperties = remember(mapStyleOptions) {
        MapProperties(
            isBuildingEnabled = false,
            isIndoorEnabled = false,
            isTrafficEnabled = false,
            mapStyleOptions = mapStyleOptions,
        )
    }
    val mapUiSettings = remember {
        MapUiSettings(
            compassEnabled = false,
            indoorLevelPickerEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false,
            rotationGesturesEnabled = false,
            scrollGesturesEnabled = false,
            tiltGesturesEnabled = false,
            zoomControlsEnabled = false,
            zoomGesturesEnabled = false,
        )
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(location.latitude, location.longitude),
            15f,
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(PlaceDetailsLayout.MapHeight)
            .clip(PlaceDetailsCardShape)
            .background(SextouColors.SurfaceImage),
    ) {
        if (isPreview) {
            Image(
                painter = painterResource(R.drawable.details_map_preview),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SextouColors.Background.copy(alpha = 0.42f)),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SextouColors.Primary.copy(alpha = 0.1f)),
        )
        Image(
            painter = painterResource(R.drawable.details_map_marker),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.Center),
        )
    }
}

@Composable
private fun PlaceDetailsHoursCard(schedule: PlaceDetailsHoursScheduleUiModel) {
    val rows = schedule.rows.take(PlaceDetailsLayout.HoursVisibleRows)
    if (rows.isEmpty()) return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(PlaceDetailsLayout.HoursCardHeight),
        shape = RoundedCornerShape(SextouCornerRadius.Medium),
        color = SextouColors.SurfaceContainer,
        border = BorderStroke(SextouDimensions.Border, SextouColors.Border),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = PlaceDetailsLayout.HoursHorizontalPadding,
                    top = PlaceDetailsLayout.HoursTopPadding,
                    end = PlaceDetailsLayout.HoursHorizontalPadding,
                    bottom = PlaceDetailsLayout.HoursBottomPadding,
                ),
            verticalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PlaceDetailsLayout.HoursRowHeight),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Sm),
                ) {
                    Image(
                        painter = painterResource(R.drawable.details_meta_clock),
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                    )
                    Text(
                        text = stringResource(R.string.details_hours),
                        style = PlaceDetailsHoursHeadingStyle,
                        color = SextouColors.TextPrimary,
                    )
                }
                schedule.status?.let { status ->
                    Text(
                        text = hoursHeaderStatus(status),
                        style = PlaceDetailsHoursStatusStyle,
                        color = if (status.isOpen) {
                            SextouColors.Positive
                        } else {
                            SextouColors.TextSecondary
                        },
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SextouDimensions.Border)
                    .background(SextouColors.OutlineVariant),
            )
            rows.forEach { row ->
                PlaceDetailsHoursRow(row = row)
            }
        }
    }
}

@Composable
private fun PlaceDetailsHoursRow(row: PlaceDetailsHoursRowUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(PlaceDetailsLayout.HoursRowHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = row.day,
            modifier = Modifier.width(PlaceDetailsLayout.HoursDayWidth),
            style = PlaceDetailsHoursDayStyle,
            color = SextouColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = row.time,
            modifier = Modifier.width(PlaceDetailsLayout.HoursTimeWidth),
            style = PlaceDetailsHoursTimeStyle,
            color = SextouColors.TextSecondary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        when (row.status) {
            PlaceDetailsHoursRowStatus.OPEN -> {
                Text(
                    text = stringResource(R.string.details_hours_open),
                    modifier = Modifier.width(PlaceDetailsLayout.HoursStateWidth),
                    style = PlaceDetailsHoursStatusStyle,
                    color = SextouColors.Positive,
                    textAlign = TextAlign.End,
                )
            }

            PlaceDetailsHoursRowStatus.UNAVAILABLE -> {
                Text(
                    text = stringResource(R.string.details_hours_unavailable),
                    modifier = Modifier.width(PlaceDetailsLayout.HoursStateWidth),
                    style = PlaceDetailsHoursStatusStyle,
                    color = SextouColors.TextSecondary,
                    textAlign = TextAlign.End,
                )
            }

            PlaceDetailsHoursRowStatus.HIDDEN -> {
                Spacer(modifier = Modifier.width(PlaceDetailsLayout.HoursStateWidth))
            }
        }
    }
}

@Composable
private fun hoursHeaderStatus(status: PlaceDetailsHoursStatusUiModel): String =
    when {
        status.isOpen && status.closingTime != null -> stringResource(
            R.string.details_hours_open_until,
            status.closingTime,
        )

        status.isOpen -> stringResource(R.string.details_hours_open)
        else -> stringResource(R.string.details_hours_closed_now)
    }

@Composable
private fun PlaceDetailsAbout(summary: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SextouSpacing.Sm),
    ) {
        Text(
            text = stringResource(R.string.details_about),
            style = PlaceDetailsSectionTitleStyle,
            color = SextouColors.TextPrimary,
        )
        Text(
            text = summary,
            style = PlaceDetailsBodyStyle,
            color = SextouColors.TextSecondary,
        )
    }
}

@Composable
private fun PlaceDetailsMenu(
    details: PlaceDetailsUiModel,
    onOpenMenu: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.details_menu),
                style = PlaceDetailsSectionTitleStyle,
                color = SextouColors.TextPrimary,
            )
            details.menuUri?.let {
                val menuDescription = stringResource(
                    R.string.details_menu_open_content_description,
                )
                SextouButton(
                    label = stringResource(R.string.details_menu_open),
                    onClick = onOpenMenu,
                    modifier = Modifier.semantics {
                        this.contentDescription = menuDescription
                    },
                    size = SextouButtonDefaults.Size.Small,
                    style = SextouButtonDefaults.ghostStyle(),
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(SextouSpacing.Md)) {
            details.menuItems.forEach { item ->
                PlaceDetailsMenuItem(item = item)
            }
        }
    }
}

@Composable
private fun PlaceDetailsMenuItem(item: PlaceDetailsMenuItemUiModel) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(PlaceDetailsLayout.MenuCardHeight),
        shape = PlaceDetailsCardShape,
        color = SextouColors.Surface,
        border = BorderStroke(SextouDimensions.Border, SextouColors.Border),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SextouSpacing.Md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Lg),
        ) {
            Box(
                modifier = Modifier
                    .size(PlaceDetailsLayout.MenuImage)
                    .clip(RoundedCornerShape(PlaceDetailsLayout.MenuImageRadius))
                    .background(SextouColors.SurfaceImage),
            ) {
                item.imageResId?.let { imageResId ->
                    Image(
                        painter = painterResource(imageResId),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SextouCornerRadius.Small),
            ) {
                Text(
                    text = item.name,
                    style = PlaceDetailsMenuTitleStyle,
                    color = SextouColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.description,
                    style = SextouTextStyles.Metadata,
                    color = SextouColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = item.price,
                style = PlaceDetailsMetaStyle.copy(fontWeight = FontWeight.Bold),
                color = SextouColors.PrimaryStrong,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun BoxScope.PlaceDetailsBottomBar(
    onOpenMap: () -> Unit,
    onContact: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(PlaceDetailsLayout.BottomBarHeight)
            .align(Alignment.BottomCenter),
        color = SextouColors.Background.copy(alpha = 0.94f),
        border = BorderStroke(SextouDimensions.Border, SextouColors.Border),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = PlaceDetailsLayout.ContentPadding,
                    top = SextouSpacing.Lg,
                    end = PlaceDetailsLayout.ContentPadding,
                    bottom = SextouSpacing.Lg,
                ),
            horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
        ) {
            DetailsBottomAction(
                modifier = Modifier.weight(1f),
                painter = painterResource(R.drawable.details_bottom_directions),
                label = stringResource(R.string.details_directions),
                contentDescription = stringResource(R.string.details_directions_content_description),
                onClick = onOpenMap,
                primary = false,
            )
            DetailsBottomAction(
                modifier = Modifier.weight(1f),
                painter = painterResource(R.drawable.details_bottom_contact),
                label = stringResource(R.string.details_contact),
                contentDescription = stringResource(R.string.details_contact_content_description),
                onClick = onContact,
                primary = true,
            )
        }
    }
}

@Composable
private fun DetailsBottomAction(
    modifier: Modifier,
    painter: Painter,
    label: String,
    contentDescription: String,
    onClick: () -> Unit,
    primary: Boolean,
) {
    Surface(
        modifier = modifier
            .height(PlaceDetailsLayout.BottomActionHeight)
            .semantics {
                this.contentDescription = contentDescription
            },
        onClick = onClick,
        shape = PlaceDetailsCardShape,
        color = if (primary) SextouColors.Primary else Color.Transparent,
        contentColor = if (primary) SextouColors.OnPrimary else SextouColors.TextPrimary,
        border = if (primary) null else BorderStroke(
            SextouDimensions.Border,
            Color.White.copy(alpha = 0.1f),
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = label,
                modifier = Modifier.padding(end = SextouSpacing.Sm),
                style = SextouTextStyles.ActionButton,
                color = if (primary) SextouColors.OnPrimary else SextouColors.TextPrimary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun PlaceDetailsLoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = SextouColors.Primary)
    }
}

@Composable
private fun PlaceDetailsErrorContent(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PlaceDetailsLayout.ContentPadding),
        verticalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
    ) {
        DetailsIconButton(
            painter = painterResource(R.drawable.details_action_back),
            contentDescription = stringResource(R.string.details_back_content_description),
            onClick = onBack,
        )
        Text(
            text = stringResource(R.string.details_error),
            modifier = Modifier.padding(top = SextouSpacing.Xl),
            style = SextouTextStyles.BodyLarge,
            color = SextouColors.TextSecondary,
        )
    }
}

private fun PlaceDetailsUiModel.hasMetadata(): Boolean =
    distanceText != null || priceLevel != null || hoursSummary != null

private fun priceDescription(priceLevel: Int): Int = when (priceLevel) {
    1 -> R.string.details_price_cheap
    2 -> R.string.details_price_bargain
    3 -> R.string.details_price_moderate
    else -> R.string.details_price_expensive
}

@Preview(
    name = "Place details screen",
    showBackground = true,
    showSystemUi = true,
    backgroundColor = SextouColors.BackgroundArgb,
    widthDp = 375,
    heightDp = 844,
)
@Composable
private fun PlaceDetailsScreenPreview() {
    SextouTheme {
        PlaceDetailsScreen(
            uiState = PlaceDetailsUiState(
                place = PlaceDetailsUiModel(
                    name = stringResource(R.string.feed_place_ao_ponto_name),
                    category = stringResource(R.string.feed_place_ao_ponto_category),
                    address = stringResource(R.string.details_preview_address),
                    phone = null,
                    website = null,
                    summary = stringResource(R.string.details_preview_summary),
                    hours = emptyList(),
                    hoursSummary = stringResource(R.string.details_preview_hours),
                    hoursSchedule = PlaceDetailsHoursScheduleUiModel(
                        status = PlaceDetailsHoursStatusUiModel(
                            isOpen = true,
                            closingTime = stringResource(R.string.details_preview_hours_closing_time),
                        ),
                        rows = listOf(
                            PlaceDetailsHoursRowUiModel(
                                day = stringResource(R.string.details_preview_hours_today),
                                time = stringResource(R.string.details_preview_hours_today_range),
                                status = PlaceDetailsHoursRowStatus.OPEN,
                            ),
                            PlaceDetailsHoursRowUiModel(
                                day = stringResource(R.string.details_preview_hours_friday),
                                time = stringResource(R.string.details_preview_hours_friday_range),
                                status = PlaceDetailsHoursRowStatus.OPEN,
                            ),
                            PlaceDetailsHoursRowUiModel(
                                day = stringResource(R.string.details_preview_hours_sunday),
                                time = stringResource(R.string.details_preview_hours_sunday_range),
                                status = PlaceDetailsHoursRowStatus.UNAVAILABLE,
                            ),
                        ),
                    ),
                    distanceText = stringResource(R.string.feed_place_ao_ponto_distance),
                    rating = 4.6,
                    ratingsCount = 156,
                    providerAttribution = stringResource(R.string.details_preview_provider),
                    location = GeoPoint(-22.9839, -43.2046),
                    priceLevel = 2,
                    isOpen = true,
                    photoCount = 3,
                    menuUri = "preview",
                    imageResId = R.drawable.feed_ao_ponto,
                    movement = PlaceDetailsMovementUiModel(
                        label = stringResource(R.string.details_preview_movement_label),
                        percentage = 35,
                        bars = listOf(
                            PlaceDetailsMovementBarUiModel(
                                label = stringResource(R.string.details_preview_movement_12h),
                                value = 12,
                            ),
                            PlaceDetailsMovementBarUiModel(
                                label = stringResource(R.string.details_preview_movement_14h),
                                value = 25,
                            ),
                            PlaceDetailsMovementBarUiModel(
                                label = stringResource(R.string.details_preview_movement_16h),
                                value = 18,
                            ),
                            PlaceDetailsMovementBarUiModel(
                                label = stringResource(R.string.details_preview_movement_18h),
                                value = 45,
                            ),
                            PlaceDetailsMovementBarUiModel(
                                label = stringResource(R.string.details_preview_movement_20h),
                                value = 67,
                                highlighted = true,
                            ),
                            PlaceDetailsMovementBarUiModel(
                                label = stringResource(R.string.details_preview_movement_22h),
                                value = 28,
                            ),
                            PlaceDetailsMovementBarUiModel(
                                label = stringResource(R.string.details_preview_movement_00h),
                                value = 12,
                            ),
                        ),
                    ),
                    menuItems = listOf(
                        PlaceDetailsMenuItemUiModel(
                            name = stringResource(R.string.details_preview_menu_steak_name),
                            description = stringResource(R.string.details_preview_menu_steak_description),
                            price = stringResource(R.string.details_preview_menu_steak_price),
                            imageResId = R.drawable.details_menu_steak,
                        ),
                        PlaceDetailsMenuItemUiModel(
                            name = stringResource(R.string.details_preview_menu_beer_name),
                            description = stringResource(R.string.details_preview_menu_beer_description),
                            price = stringResource(R.string.details_preview_menu_beer_price),
                            imageResId = R.drawable.details_menu_brahma,
                        ),
                    ),
                ),
            ),
            onBack = {},
        )
    }
}
