package com.example.app.analytics.di

import com.example.app.analytics.AnalyticsManager
import com.example.app.analytics.AppAnalytics
import com.example.app.analytics.trackers.Analytics
import com.example.app.analytics.trackers.FirebaseAnalyticsTracker
import com.example.app.analytics.trackers.LogcatAnalyticsTracker
import org.koin.core.module.Module
import org.koin.dsl.module

fun analyticsModules(isDebug: Boolean): List<Module> {
    return listOf(
        module {
            single<AppAnalytics> {
                val trackers: List<Analytics> = if (isDebug) {
                    listOf(LogcatAnalyticsTracker())
                } else {
                    listOf(FirebaseAnalyticsTracker())
                }
                AnalyticsManager(trackers)
            }
        }
    )
}
