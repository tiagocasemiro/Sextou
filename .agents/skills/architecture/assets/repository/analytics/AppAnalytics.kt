package com.example.app.analytics

import com.example.app.analytics.events.AnalyticsEvent
import com.example.app.analytics.events.AnalyticsIdentification

interface AppAnalytics {
    fun track(event: AnalyticsEvent)

    fun user(identification: AnalyticsIdentification)
}
