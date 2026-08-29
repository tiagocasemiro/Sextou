package com.sextou.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sextou.features.details.PlaceDetailsDestination
import com.sextou.features.details.PlaceDetailsViewModel
import com.sextou.features.feed.FeedDestination
import com.sextou.features.feed.FeedTab
import com.sextou.features.feed.FeedViewModel
import com.sextou.features.map.MapDestination
import com.sextou.features.map.MapViewModel

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
            ),
        ) { backStackEntry ->
            MapDestination(
                query = backStackEntry.arguments
                    ?.getString(AppRoutes.QUERY_ARGUMENT)
                    .orEmpty(),
                viewModel = mapViewModel,
                onPlaceClicked = { placeId ->
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
            )
        }
    }
}
