package com.sextou.di

import com.sextou.domain.favorites.usecase.ObserveFavoritesUseCase
import com.sextou.domain.favorites.usecase.ToggleFavoriteUseCase
import com.sextou.domain.places.usecase.GetPlaceDetailsUseCase
import com.sextou.domain.places.usecase.SearchPlacesUseCase
import com.sextou.domain.visits.usecase.ObserveVisitedPlacesUseCase
import com.sextou.domain.visits.usecase.ToggleVisitedPlaceUseCase
import com.sextou.features.details.PlaceDetailsViewModel
import com.sextou.features.feed.FeedViewModel
import com.sextou.features.map.MapViewModel
import com.sextou.location.AndroidLocationProvider
import com.sextou.location.LocationProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<LocationProvider> {
        AndroidLocationProvider(context = androidContext())
    }
    factory { SearchPlacesUseCase(repository = get()) }
    factory { GetPlaceDetailsUseCase(repository = get()) }
    factory { ObserveFavoritesUseCase(repository = get()) }
    factory { ToggleFavoriteUseCase(repository = get()) }
    factory { ObserveVisitedPlacesUseCase(repository = get()) }
    factory { ToggleVisitedPlaceUseCase(repository = get()) }
    viewModel {
        FeedViewModel(
            searchPlacesUseCase = get(),
            observeFavoritesUseCase = get(),
            toggleFavoriteUseCase = get(),
            observeVisitedPlacesUseCase = get(),
            toggleVisitedPlaceUseCase = get(),
        )
    }
    viewModel { MapViewModel(searchPlacesUseCase = get()) }
    viewModel { PlaceDetailsViewModel(getPlaceDetailsUseCase = get()) }
}
