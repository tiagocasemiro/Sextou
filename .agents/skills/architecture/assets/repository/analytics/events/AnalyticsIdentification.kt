package com.example.app.analytics.events

sealed class AnalyticsIdentification(
    val user: String,
    val properties: Map<String, String>
)

class Identification(
    user: String,
    properties: Map<String, String> = emptyMap()
) : AnalyticsIdentification(user, properties)
