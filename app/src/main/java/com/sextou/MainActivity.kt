package com.sextou

import android.Manifest
import android.os.Bundle
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.sextou.designsystem.theme.SextouTheme
import com.sextou.domain.places.model.GeoPoint
import com.sextou.features.details.PlaceDetailsViewModel
import com.sextou.features.feed.FeedViewModel
import com.sextou.features.map.MapViewModel
import com.sextou.location.LocationProvider
import com.sextou.navigation.SextouNavHost
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val feedViewModel: FeedViewModel by viewModel()
    private val mapViewModel: MapViewModel by viewModel()
    private val placeDetailsViewModel: PlaceDetailsViewModel by viewModel()
    private val locationProvider: LocationProvider by inject()

    private var hasRequestedLocationPermission = false
    private var locationJob: Job? = null

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        if (permissions.values.any { it }) {
            refreshLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SextouTheme {
                SextouNavHost(
                    feedViewModel = feedViewModel,
                    mapViewModel = mapViewModel,
                    placeDetailsViewModel = placeDetailsViewModel,
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        requestLocationOrRefresh()
    }

    private fun requestLocationOrRefresh() {
        if (hasLocationPermission()) {
            refreshLocation()
        } else if (!hasRequestedLocationPermission) {
            hasRequestedLocationPermission = true
            locationPermissionLauncher.launch(LOCATION_PERMISSIONS)
        }
    }

    private fun hasLocationPermission(): Boolean = LOCATION_PERMISSIONS.any { permission ->
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun refreshLocation() {
        locationJob?.cancel()
        locationJob = lifecycleScope.launch {
            try {
                locationProvider.getCurrentLocation()?.let(::publishLocation)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: SecurityException) {
                // Permission can be revoked while the location task is running.
            } catch (_: Exception) {
                // The feed and map keep their non-location fallback when GPS is unavailable.
            }
        }
    }

    private fun publishLocation(location: GeoPoint) {
        feedViewModel.onLocationChanged(location)
        mapViewModel.onLocationChanged(location)
    }

    private companion object {
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }
}
