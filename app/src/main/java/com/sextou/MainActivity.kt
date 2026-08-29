package com.sextou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sextou.designsystem.theme.SextouTheme
import com.sextou.features.feed.FeedDestination
import com.sextou.features.feed.FeedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val feedViewModel: FeedViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SextouTheme {
                FeedDestination(viewModel = feedViewModel)
            }
        }
    }
}
