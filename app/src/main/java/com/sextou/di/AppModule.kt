package com.sextou.di

import com.sextou.domain.places.usecase.LoadedPlacesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import java.util.Calendar
import com.sextou.domain.favorites.usecase.ObserveFavoritesUseCase
import com.sextou.domain.favorites.usecase.ToggleFavoriteUseCase
import com.sextou.domain.ignored.usecase.ObserveIgnoredPlacesUseCase
import com.sextou.domain.ignored.usecase.ToggleIgnoredPlaceUseCase
import com.sextou.domain.places.usecase.GetPlaceDetailsUseCase
import com.sextou.domain.places.usecase.GetPlacePhotoUseCase
import com.sextou.domain.places.usecase.SetPlaceStatusUseCase
import com.sextou.domain.routes.usecase.GetRouteUseCase
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
    single(named("placesApplicationScope")) { CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate) }
    single {
        LoadedPlacesUseCase(
            remote = get(), local = get(), automaticRefresh = get(),
            currentDay = {
                val calendar = Calendar.getInstance()
                calendar.get(Calendar.YEAR) * 10_000L +
                    (calendar.get(Calendar.MONTH) + 1) * 100L + calendar.get(Calendar.DAY_OF_MONTH)
            },
            applicationScope = get(named("placesApplicationScope")),
            ioDispatcher = Dispatchers.IO,
        )
    }
    single<LocationProvider> {
        AndroidLocationProvider(context = androidContext())
    }
    factory { GetPlaceDetailsUseCase(repository = get(), localRepository = get()) }
    factory { GetPlacePhotoUseCase(repository = get()) }
    factory { GetRouteUseCase(repository = get()) }
    factory { SetPlaceStatusUseCase(repository = get()) }
    factory { ObserveFavoritesUseCase(repository = get()) }
    factory { ToggleFavoriteUseCase(repository = get()) }
    factory { ObserveIgnoredPlacesUseCase(repository = get()) }
    factory { ToggleIgnoredPlaceUseCase(repository = get()) }
    factory { ObserveVisitedPlacesUseCase(repository = get()) }
    factory { ToggleVisitedPlaceUseCase(repository = get()) }
    viewModel {
        FeedViewModel(
            loadedPlacesUseCase = get(),
            getPlacePhotoUseCase = get(),
            observeFavoritesUseCase = get(),
            observeVisitedPlacesUseCase = get(),
            setPlaceStatusUseCase = get(),
        )
    }
    viewModel {
        MapViewModel(
            loadedPlacesUseCase = get(),
            getPlacePhotoUseCase = get(),
            getRouteUseCase = get(),
            observeFavoritesUseCase = get(),
            observeIgnoredPlacesUseCase = get(),
        )
    }
    viewModel {
        PlaceDetailsViewModel(
            getPlaceDetailsUseCase = get(),
            getPlacePhotoUseCase = get(),
            observeFavoritesUseCase = get(),
            observeVisitedPlacesUseCase = get(),
            observeIgnoredPlacesUseCase = get(),
            setPlaceStatusUseCase = get(),
        )
    }
}
