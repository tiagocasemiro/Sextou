package com.sextou

import android.app.Application
import com.sextou.networking.di.networkingModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SextouApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SextouApplication)
            modules(networkingModule(BuildConfig.PLACES_API_KEY))
        }
    }
}
