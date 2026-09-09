package com.sextou.navigation

import android.net.Uri
import com.sextou.domain.places.model.GeoPoint

object AppRoutes {
    const val FEED = "feed"
    const val QUERY_ARGUMENT = "query"
    const val PLACE_ID_ARGUMENT = "placeId"
    const val MAP_FOCUS_PLACE_ID_ARGUMENT = "focusPlaceId"
    const val MAP_FOCUS_LATITUDE_ARGUMENT = "focusLatitude"
    const val MAP_FOCUS_LONGITUDE_ARGUMENT = "focusLongitude"
    const val MAP = "map?query={$QUERY_ARGUMENT}" +
        "&$MAP_FOCUS_PLACE_ID_ARGUMENT={$MAP_FOCUS_PLACE_ID_ARGUMENT}" +
        "&$MAP_FOCUS_LATITUDE_ARGUMENT={$MAP_FOCUS_LATITUDE_ARGUMENT}" +
        "&$MAP_FOCUS_LONGITUDE_ARGUMENT={$MAP_FOCUS_LONGITUDE_ARGUMENT}"
    const val PLACE_DETAILS = "place/{$PLACE_ID_ARGUMENT}"

    fun map(
        query: String,
        focusedPlaceId: String? = null,
        focusedLocation: GeoPoint? = null,
    ): String = "map?query=${Uri.encode(query)}" +
        "&$MAP_FOCUS_PLACE_ID_ARGUMENT=${Uri.encode(focusedPlaceId.orEmpty())}" +
        "&$MAP_FOCUS_LATITUDE_ARGUMENT=${focusedLocation?.latitude?.toString().orEmpty()}" +
        "&$MAP_FOCUS_LONGITUDE_ARGUMENT=${focusedLocation?.longitude?.toString().orEmpty()}"

    fun placeDetails(placeId: String): String = "place/${Uri.encode(placeId)}"
}
