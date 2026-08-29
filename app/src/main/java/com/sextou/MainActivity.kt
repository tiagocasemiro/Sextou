package com.sextou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sextou.designsystem.theme.SextouTheme
import com.sextou.features.details.PlaceDetailsViewModel
import com.sextou.features.feed.FeedViewModel
import com.sextou.features.map.MapViewModel
import com.sextou.navigation.SextouNavHost
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val feedViewModel: FeedViewModel by viewModel()
    private val mapViewModel: MapViewModel by viewModel()
    private val placeDetailsViewModel: PlaceDetailsViewModel by viewModel()

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
}
