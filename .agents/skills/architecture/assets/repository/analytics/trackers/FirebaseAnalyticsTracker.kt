package com.example.app.analytics.trackers

import android.os.Bundle
import android.util.Log
import com.example.app.analytics.events.AnalyticsEvent
import com.example.app.analytics.events.AnalyticsIdentification
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

internal class FirebaseAnalyticsTracker : Analytics {
    private val firebase = Firebase.analytics

    override val name: String = NAME

    override fun track(event: AnalyticsEvent) {
        firebase.logEvent(event.id, event.payload.toBundle())
    }

    override fun user(identification: AnalyticsIdentification) {
        firebase.setUserId(identification.user)
        identification.properties.forEach { (key, value) ->
            firebase.setUserProperty(key, value)
        }
    }

    override fun handleExceptionThrown(throwable: Throwable, event: AnalyticsEvent) {
        Log.e(NAME, "Event ${event.id} failed: ${throwable.message}", throwable)
    }

    private fun Map<String, String>.toBundle(): Bundle {
        return Bundle().apply {
            forEach { (key, value) -> putString(key, value) }
        }
    }

    private companion object {
        const val NAME = "firebase-analytics"
    }
}
