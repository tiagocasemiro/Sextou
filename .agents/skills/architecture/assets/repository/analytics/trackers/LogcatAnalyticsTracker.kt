package com.example.app.analytics.trackers

import android.util.Log
import com.example.app.analytics.events.AnalyticsEvent
import com.example.app.analytics.events.AnalyticsIdentification

internal class LogcatAnalyticsTracker : Analytics {
    override val name: String = NAME

    override fun track(event: AnalyticsEvent) {
        Log.d(NAME, "${event.id}: ${event.payload.toFormattedString()}")
    }

    override fun user(identification: AnalyticsIdentification) {
        Log.d(
            NAME,
            "User: ${identification.user}, properties: " +
                identification.properties.toFormattedString()
        )
    }

    override fun handleExceptionThrown(throwable: Throwable, event: AnalyticsEvent) {
        Log.e(NAME, "Event ${event.id} failed: ${throwable.message}", throwable)
    }

    private fun Map<String, String>.toFormattedString(): String {
        return entries.joinToString(separator = ", ", prefix = "[", postfix = "]") {
            "(${it.key}: ${it.value})"
        }
    }

    private companion object {
        const val NAME = "logcat-analytics"
    }
}
