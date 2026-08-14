package com.sextou.networking.di

import com.sextou.domain.places.repository.PlacesRepository
import com.sextou.networking.adapter.PlacesRemoteImpl
import com.sextou.networking.gateway.GooglePlacesGateway
import com.sextou.networking.gateway.PlacesGateway
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun networkingModule(placesApiKey: String) = module {
    single<PlacesGateway> {
        GooglePlacesGateway(
            context = androidContext(),
            apiKey = placesApiKey,
        )
    }
    factory<PlacesRepository.Remote> {
        PlacesRemoteImpl(gateway = get())
    }
}
