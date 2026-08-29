package com.sextou

import android.app.Application
import com.sextou.di.appModule
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
                networkingModule(BuildConfig.PLACES_API_KEY),
            )
        }
    }
}
