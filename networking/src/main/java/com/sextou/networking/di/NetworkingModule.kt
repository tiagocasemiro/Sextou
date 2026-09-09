package com.sextou.networking.di

import com.sextou.domain.places.repository.PlacesRepository
import com.sextou.domain.routes.repository.RouteRepository
import com.sextou.networking.adapter.PlacesRemoteImpl
import com.sextou.networking.adapter.RoutesRemoteImpl
import com.sextou.networking.gateway.GooglePlacesGateway
import com.sextou.networking.gateway.GoogleRoutesGateway
import com.sextou.networking.gateway.PlacesGateway
import com.sextou.networking.gateway.RoutesGateway
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun networkingModule(
    placesApiKey: String,
    mapsApiKey: String,
) = module {
    single<PlacesGateway> {
        GooglePlacesGateway(
            context = androidContext(),
            apiKey = placesApiKey,
        )
    }
    factory<PlacesRepository.Remote> {
        PlacesRemoteImpl(gateway = get())
    }
    single<RoutesGateway> {
        GoogleRoutesGateway(apiKey = mapsApiKey)
    }
    factory<RouteRepository.Remote> {
        RoutesRemoteImpl(gateway = get())
    }
}
