package com.sextou.di

import com.sextou.features.feed.FeedViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { FeedViewModel() }
}
