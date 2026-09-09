package com.sextou.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.sextou.domain.places.model.GeoPoint
import kotlinx.coroutines.tasks.await

class AndroidLocationProvider(context: Context) : LocationProvider {
    private val client = LocationServices.getFusedLocationProviderClient(
        context.applicationContext,
    )

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LocationSnapshot? {
        val cancellationTokenSource = CancellationTokenSource()
        return try {
            val currentLocation = client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token,
            ).await()

            currentLocation?.toLocationSnapshot()
                ?: client.lastLocation.await()?.toLocationSnapshot()
        } finally {
            cancellationTokenSource.cancel()
        }
    }

    private fun Location.toLocationSnapshot() = LocationSnapshot(
        point = GeoPoint(
            latitude = latitude,
            longitude = longitude,
        ),
        bearingDegrees = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && hasBearing()) {
            bearing.takeIf { it.isFinite() && it in 0f..360f }
        } else {
            null
        },
    )
}
