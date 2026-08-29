package com.sextou.features.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.sextou.R
import com.sextou.designsystem.R as DesignSystemR
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouCornerRadius
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTextStyles
import com.sextou.designsystem.theme.SextouTheme
import com.sextou.domain.places.model.GeoPoint
import com.sextou.features.feed.FeedTab
import com.sextou.features.feed.components.FeedBottomNavigation
import com.sextou.features.map.components.MapFloatingActions
import com.sextou.features.map.components.MapPlaceCarousel
import com.sextou.features.map.components.MapSearchAreaButton
import com.sextou.features.map.components.MapTopChrome
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private val CampoGrandeRioDeJaneiro = LatLng(-22.9068, -43.5614)
private val MapErrorShape = androidx.compose.foundation.shape.RoundedCornerShape(
    SextouCornerRadius.Surface,
)

@Composable
fun MapScreen(
    uiState: MapUiState,
    onQueryChanged: (String) -> Unit,
    onMapCenterChanged: (GeoPoint) -> Unit,
    onSearchAreaClicked: () -> Unit,
    onPlaceClicked: (String) -> Unit,
    onTabSelected: (FeedTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val firstPlace = uiState.places.firstOrNull()
    var selectedPlaceId by remember { mutableStateOf<String?>(null) }
    var selectionRequest by remember { mutableIntStateOf(0) }
    var isMapReady by remember { mutableStateOf(false) }
    var hasCenteredInitialPlace by remember { mutableStateOf(false) }
    val initialCameraTarget = uiState.userLocation?.let { location ->
        LatLng(location.latitude, location.longitude)
    } ?: firstPlace?.let { place ->
        LatLng(place.latitude, place.longitude)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            initialCameraTarget ?: CampoGrandeRioDeJaneiro,
            if (uiState.userLocation != null) 14f else 12f,
        )
    }
    val context = LocalContext.current
    val markerIcon = remember { mutableStateOf<BitmapDescriptor?>(null) }
    val mapStyleOptions = remember(context) {
        runCatching {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
        }.getOrNull()
    }
    val mapProperties = remember(mapStyleOptions) {
        MapProperties(
            isBuildingEnabled = false,
            isIndoorEnabled = false,
            isMyLocationEnabled = false,
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
            tiltGesturesEnabled = false,
            zoomControlsEnabled = false,
        )
    }
    val coroutineScope = rememberCoroutineScope()
    val centerMapOnPlace: (MapPlaceUiModel) -> Unit = { place ->
        selectedPlaceId = place.id
        coroutineScope.launch {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(place.latitude, place.longitude),
                    cameraPositionState.position.zoom,
                ),
            )
        }
    }

    LaunchedEffect(uiState.userLocation) {
        uiState.userLocation?.let { location ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude),
                    14f,
                ),
            )
        }
    }

    LaunchedEffect(firstPlace?.id, uiState.userLocation) {
        if (uiState.userLocation == null && firstPlace != null && !hasCenteredInitialPlace) {
            hasCenteredInitialPlace = true
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(firstPlace.latitude, firstPlace.longitude),
                    13f,
                ),
            )
        }
    }

    LaunchedEffect(cameraPositionState) {
        snapshotFlow { cameraPositionState.isMoving }
            .distinctUntilChanged()
            .collect { isMoving ->
                if (!isMoving &&
                    cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE
                ) {
                    val target = cameraPositionState.position.target
                    onMapCenterChanged(GeoPoint(target.latitude, target.longitude))
                }
            }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SextouColors.Background),
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings,
            onMapLoaded = {
                if (markerIcon.value == null) {
                    markerIcon.value = context.createMapMarkerIcon()
                }
                isMapReady = true
            },
        ) {
            uiState.userLocation?.let { location ->
                Circle(
                    center = LatLng(location.latitude, location.longitude),
                    radius = 50.0,
                    fillColor = SextouColors.Primary.copy(alpha = 0.16f),
                    strokeColor = SextouColors.Primary,
                    strokeWidth = 2f,
                )
            }

            uiState.places.forEach { place ->
                val markerState = remember(place.id) {
                    MarkerState(position = LatLng(place.latitude, place.longitude))
                }
                Marker(
                    state = markerState,
                    title = place.name,
                    snippet = place.rating?.let {
                        stringResource(R.string.map_rating, it)
                    },
                    contentDescription = stringResource(
                        R.string.map_marker_content_description,
                        place.name,
                    ),
                    icon = markerIcon.value,
                    onClick = {
                        centerMapOnPlace(place)
                        selectionRequest++
                        true
                    },
                )
            }
        }

        if (!isMapReady) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SextouColors.Background),
            )
        }

        MapTopChrome(
            query = uiState.query,
            onQueryChanged = onQueryChanged,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        if (uiState.isSearchAreaButtonVisible) {
            MapSearchAreaButton(
                onClick = onSearchAreaClicked,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 204.dp),
            )
        }

        MapFloatingActions(
            onRecenter = {
                uiState.userLocation?.let { location ->
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude, location.longitude),
                                14f,
                            ),
                        )
                    }
                }
            },
            modifier = Modifier.align(Alignment.TopEnd),
        )

        if (uiState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(2.dp),
                color = SextouColors.Primary,
                trackColor = androidx.compose.ui.graphics.Color.Transparent,
            )
        }

        if (uiState.isError && uiState.places.isEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(SextouSpacing.Lg),
                shape = MapErrorShape,
                color = SextouColors.SurfaceElevated.copy(alpha = 0.96f),
                border = BorderStroke(SextouDimensions.Border, SextouColors.Border),
            ) {
                Text(
                    text = stringResource(R.string.map_error),
                    modifier = Modifier.padding(SextouSpacing.Lg),
                    style = SextouTextStyles.BodyLarge,
                    color = SextouColors.TextSecondary,
                )
            }
        }

        Text(
            text = stringResource(R.string.map_provider_attribution),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = SextouSpacing.Md, bottom = 112.dp),
            style = SextouTextStyles.Metadata,
            color = SextouColors.TextSecondary,
        )

        if (uiState.places.isNotEmpty()) {
            MapPlaceCarousel(
                places = uiState.places,
                selectedPlaceId = selectedPlaceId,
                selectionRequest = selectionRequest,
                onPlaceCentered = centerMapOnPlace,
                onPlaceClicked = onPlaceClicked,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 112.dp),
            )
        }

        FeedBottomNavigation(
            selectedTab = FeedTab.MAP,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

private fun Context.createMapMarkerIcon(): BitmapDescriptor? {
    return runCatching {
        val drawable = ContextCompat.getDrawable(
            this,
            DesignSystemR.drawable.ic_sextou_map_marker_listados,
        ) ?: return null
        val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1
        val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).also { canvas ->
            drawable.setBounds(0, 0, width, height)
            drawable.draw(canvas)
        }
        BitmapDescriptorFactory.fromBitmap(bitmap)
    }.getOrNull()
}

@Preview(
    name = "Map screen",
    showBackground = true,
    showSystemUi = true,
    backgroundColor = SextouColors.BackgroundArgb,
    widthDp = 375,
    heightDp = 844,
)
@Composable
private fun MapScreenPreview() {
    SextouTheme {
        MapScreen(
            uiState = MapUiState(
                places = listOf(
                    MapPlaceUiModel(
                        id = "ao-ponto",
                        name = stringResource(R.string.map_preview_place_name),
                        latitude = CampoGrandeRioDeJaneiro.latitude,
                        longitude = CampoGrandeRioDeJaneiro.longitude,
                        rating = 4.6,
                        categoryText = stringResource(R.string.map_preview_place_category),
                        highlightText = stringResource(R.string.map_preview_place_highlight),
                        distanceMeters = 680.0,
                        priceLevel = 2,
                        imageResId = R.drawable.feed_ao_ponto,
                        isOpen = true,
                    ),
                    MapPlaceUiModel(
                        id = "bar-do-wanderley",
                        name = stringResource(R.string.feed_place_wanderley_name),
                        latitude = CampoGrandeRioDeJaneiro.latitude + 0.005,
                        longitude = CampoGrandeRioDeJaneiro.longitude + 0.005,
                        rating = 4.9,
                        categoryText = stringResource(R.string.feed_place_wanderley_category),
                        imageResId = R.drawable.feed_bar_do_wanderley,
                    ),
                ),
            ),
            onQueryChanged = {},
            onMapCenterChanged = {},
            onSearchAreaClicked = {},
            onPlaceClicked = {},
            onTabSelected = {},
        )
    }
}
