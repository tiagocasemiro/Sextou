package com.example.app.analytics

import android.util.Log
import com.example.app.analytics.events.AnalyticsEvent
import com.example.app.analytics.events.AnalyticsIdentification
import com.example.app.analytics.trackers.Analytics

internal class AnalyticsManager(
    private val trackers: List<Analytics>
) : AppAnalytics {

    override fun track(event: AnalyticsEvent) {
        trackers.forEach { tracker ->
            try {
                tracker.track(event)
            } catch (throwable: Throwable) {
                Log.e(TAG, "Analytics ${tracker.name} failed: ${throwable.message}", throwable)
                try {
                    tracker.handleExceptionThrown(throwable, event)
                } catch (handlerThrowable: Throwable) {
                    Log.e(
                        TAG,
                        "Analytics ${tracker.name} error handler failed",
                        handlerThrowable
                    )
                }
            }
        }
    }

    override fun user(identification: AnalyticsIdentification) {
        trackers.forEach { tracker ->
            try {
                tracker.user(identification)
            } catch (throwable: Throwable) {
                Log.e(TAG, "Analytics ${tracker.name} failed: ${throwable.message}", throwable)
            }
        }
    }

    private companion object {
        const val TAG = "AnalyticsManager"
    }
}
