package com.example.app.analytics.trackers

import com.example.app.analytics.events.AnalyticsEvent
import com.example.app.analytics.events.AnalyticsIdentification

internal interface Analytics {
    val name: String

    fun track(event: AnalyticsEvent)

    fun user(identification: AnalyticsIdentification)

    fun handleExceptionThrown(throwable: Throwable, event: AnalyticsEvent)
}
