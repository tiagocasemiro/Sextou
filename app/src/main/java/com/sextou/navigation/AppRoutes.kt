package com.sextou.navigation

import android.net.Uri

object AppRoutes {
    const val FEED = "feed"
    const val QUERY_ARGUMENT = "query"
    const val PLACE_ID_ARGUMENT = "placeId"
    const val MAP = "map?query={$QUERY_ARGUMENT}"
    const val PLACE_DETAILS = "place/{$PLACE_ID_ARGUMENT}"

    fun map(query: String): String = "map?query=${Uri.encode(query)}"

    fun placeDetails(placeId: String): String = "place/${Uri.encode(placeId)}"
}
