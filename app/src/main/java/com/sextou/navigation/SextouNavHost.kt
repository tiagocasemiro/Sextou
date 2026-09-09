package com.sextou.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sextou.features.details.PlaceDetailsDestination
import com.sextou.features.details.PlaceDetailsFallback
import com.sextou.features.details.PlaceDetailsViewModel
import com.sextou.features.feed.FeedPlaceUiModel
import com.sextou.features.feed.FeedDestination
import com.sextou.features.feed.FeedTab
import com.sextou.features.feed.FeedViewModel
import com.sextou.features.map.MapPlaceUiModel
import com.sextou.features.map.MapDestination
import com.sextou.features.map.MapViewModel
import com.sextou.domain.places.model.GeoPoint

@Composable
fun SextouNavHost(
    feedViewModel: FeedViewModel,
    mapViewModel: MapViewModel,
    placeDetailsViewModel: PlaceDetailsViewModel,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.FEED,
    ) {
        composable(AppRoutes.FEED) {
            FeedDestination(
                viewModel = feedViewModel,
                onOpenMap = { query ->
                    navController.navigate(AppRoutes.map(query))
                },
                onOpenPlace = { placeId ->
                    placeDetailsViewModel.setFallback(
                        feedViewModel.uiState.value.places
                            .firstOrNull { it.id == placeId }
                            ?.toDetailsFallback(),
                    )
                    navController.navigate(AppRoutes.placeDetails(placeId))
                },
            )
        }
        composable(
            route = AppRoutes.MAP,
            arguments = listOf(
                navArgument(AppRoutes.QUERY_ARGUMENT) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(AppRoutes.MAP_FOCUS_PLACE_ID_ARGUMENT) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(AppRoutes.MAP_FOCUS_LATITUDE_ARGUMENT) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(AppRoutes.MAP_FOCUS_LONGITUDE_ARGUMENT) {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) { backStackEntry ->
            MapDestination(
                query = backStackEntry.arguments
                    ?.getString(AppRoutes.QUERY_ARGUMENT)
                    .orEmpty(),
                focusedPlaceId = backStackEntry.arguments
                    ?.getString(AppRoutes.MAP_FOCUS_PLACE_ID_ARGUMENT)
                    ?.takeIf(String::isNotBlank),
                focusedLocation = backStackEntry.arguments.toFocusedLocation(),
                viewModel = mapViewModel,
                onPlaceClicked = { placeId ->
                    placeDetailsViewModel.setFallback(
                        mapViewModel.uiState.value.places
                            .firstOrNull { it.id == placeId }
                            ?.toDetailsFallback(),
                    )
                    navController.navigate(AppRoutes.placeDetails(placeId))
                },
                onTabSelected = { tab ->
                    when (tab) {
                        FeedTab.MAP -> Unit
                        FeedTab.FEED -> navController.popBackStack()
                        FeedTab.FAVORITES -> {
                            feedViewModel.onTabSelected(FeedTab.FAVORITES)
                            navController.popBackStack()
                        }
                    }
                },
            )
        }
        composable(
            route = AppRoutes.PLACE_DETAILS,
            arguments = listOf(
                navArgument(AppRoutes.PLACE_ID_ARGUMENT) {
                    type = NavType.StringType
                },
            ),
        ) { backStackEntry ->
            PlaceDetailsDestination(
                placeId = backStackEntry.arguments
                    ?.getString(AppRoutes.PLACE_ID_ARGUMENT)
                    .orEmpty(),
                viewModel = placeDetailsViewModel,
                onBack = navController::popBackStack,
                onOpenMap = { location ->
                    navController.navigate(
                        AppRoutes.map(
                            query = "",
                            focusedPlaceId = backStackEntry.arguments
                                ?.getString(AppRoutes.PLACE_ID_ARGUMENT),
                            focusedLocation = location,
                        ),
                    )
                },
            )
        }
    }
}

private fun FeedPlaceUiModel.toDetailsFallback() = PlaceDetailsFallback(
    id = id,
    name = nameText ?: id,
    category = categoryText,
    address = address,
    rating = rating?.toDouble(),
    ratingsCount = ratingsCount,
    priceLevel = priceLevel,
    location = location,
    googleMapsUri = googleMapsUri,
    providerAttribution = providerAttribution ?: "Google Maps",
)

private fun MapPlaceUiModel.toDetailsFallback() = PlaceDetailsFallback(
    id = id,
    name = name,
    category = categoryText,
    address = address,
    rating = rating,
    ratingsCount = ratingsCount,
    priceLevel = priceLevel,
    location = com.sextou.domain.places.model.GeoPoint(latitude, longitude),
    googleMapsUri = googleMapsUri,
    photoUri = photoUri,
    photoAttribution = photoAttribution,
)

private fun android.os.Bundle?.toFocusedLocation(): GeoPoint? {
    val latitude = this?.getString(AppRoutes.MAP_FOCUS_LATITUDE_ARGUMENT)?.toDoubleOrNull()
    val longitude = this?.getString(AppRoutes.MAP_FOCUS_LONGITUDE_ARGUMENT)?.toDoubleOrNull()
    return if (latitude != null && longitude != null) {
        GeoPoint(latitude = latitude, longitude = longitude)
    } else {
        null
    }
}
