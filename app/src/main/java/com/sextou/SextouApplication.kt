package com.sextou

import android.app.Application
import com.sextou.di.appModule
import com.sextou.local.di.localModule
import com.sextou.networking.di.networkingModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SextouApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SextouApplication)
            modules(
                appModule,
                localModule(),
                networkingModule(
                    placesApiKey = BuildConfig.PLACES_API_KEY,
                    mapsApiKey = BuildConfig.MAPS_API_KEY,
                ),
            )
        }
    }
}
