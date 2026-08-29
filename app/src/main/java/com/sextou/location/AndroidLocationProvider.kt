package com.sextou.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
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
    override suspend fun getCurrentLocation(): GeoPoint? {
        val cancellationTokenSource = CancellationTokenSource()
        return try {
            val currentLocation = client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token,
            ).await()

            currentLocation?.toGeoPoint()
                ?: client.lastLocation.await()?.toGeoPoint()
        } finally {
            cancellationTokenSource.cancel()
        }
    }

    private fun Location.toGeoPoint() = GeoPoint(
        latitude = latitude,
        longitude = longitude,
    )
}
