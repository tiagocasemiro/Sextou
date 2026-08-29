package com.sextou.features.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.sextou.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouTextStyles
import com.sextou.designsystem.theme.SextouTheme

private val CampoGrandeRioDeJaneiro = LatLng(-22.9068, -43.5614)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    uiState: MapUiState,
    onBack: () -> Unit,
    onPlaceClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val firstPlace = uiState.places.firstOrNull()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(CampoGrandeRioDeJaneiro, 12f)
    }

    LaunchedEffect(firstPlace?.id) {
        firstPlace?.let { place ->
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(place.latitude, place.longitude),
                    14f,
                ),
            )
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.map_title)) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = stringResource(R.string.map_back))
                    }
                },
            )
        },
        containerColor = SextouColors.Background,
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
            ) {
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
                        onClick = {
                            onPlaceClicked(place.id)
                            true
                        },
                    )
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    color = SextouColors.Primary,
                )
            }

            if (uiState.isError && uiState.places.isEmpty()) {
                Text(
                    text = stringResource(R.string.map_error),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(24.dp),
                    style = SextouTextStyles.BodyLarge,
                    color = SextouColors.TextSecondary,
                )
            }

            Text(
                text = stringResource(R.string.map_provider_attribution),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                style = SextouTextStyles.Metadata,
                color = SextouColors.TextSecondary,
            )
        }
    }
}
