package com.example.app.analytics.events

sealed class AnalyticsEvent(
    val id: String,
    val payload: Map<String, String> = emptyMap()
)

class Event(
    category: String,
    action: String,
    label: String,
    value: String? = null
) : AnalyticsEvent(
    id = "${category}_${action}_${label}",
    payload = eventPayload(category, action, label, value)
)

private fun eventPayload(
    category: String,
    action: String,
    label: String,
    value: String?
): Map<String, String> {
    val payload = mutableMapOf(
        "Category" to category,
        "Action" to action,
        "Label" to label
    )
    value?.let { payload["Value"] = it }
    return payload
}
