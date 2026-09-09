package com.sextou.networking.gateway

import com.sextou.domain.places.model.GeoPoint
import com.sextou.networking.response.RoutePathResponse
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class GoogleRoutesGateway(
    private val apiKey: String,
) : RoutesGateway {
    override suspend fun computeRoute(
        origin: GeoPoint,
        destination: GeoPoint,
    ): RoutePathResponse {
        require(apiKey.isNotBlank()) {
            "Defina MAPS_API_KEY no gradle.properties local ou via -PMAPS_API_KEY."
        }

        val connection = URL(ROUTES_ENDPOINT).openConnection() as HttpURLConnection
        return try {
            connection.requestMethod = "POST"
            connection.connectTimeout = CONNECTION_TIMEOUT_MILLIS
            connection.readTimeout = READ_TIMEOUT_MILLIS
            connection.doInput = true
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("X-Goog-Api-Key", apiKey)
            connection.setRequestProperty(
                "X-Goog-FieldMask",
                "routes.polyline.encodedPolyline",
            )
            connection.setRequestProperty(
                "X-Goog-Maps-Solution-ID",
                USAGE_ATTRIBUTION_ID,
            )
            connection.outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
                writer.write(requestBody(origin, destination).toString())
            }

            val responseBody = connection.responseStream().use { stream ->
                stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            }
            if (connection.responseCode !in 200..299) {
                throw IOException(
                    "Routes API retornou HTTP ${connection.responseCode}: " +
                        responseBody.take(MAX_ERROR_BODY_LENGTH),
                )
            }

            val encodedPolyline = JSONObject(responseBody)
                .optJSONArray("routes")
                ?.optJSONObject(0)
                ?.optJSONObject("polyline")
                ?.optString("encodedPolyline")
                ?.takeIf(String::isNotBlank)
                ?: throw IOException("A Routes API não retornou uma polyline para a rota.")

            RoutePathResponse(encodedPolyline = encodedPolyline)
        } finally {
            connection.disconnect()
        }
    }

    private fun requestBody(origin: GeoPoint, destination: GeoPoint): JSONObject =
        JSONObject()
            .put("origin", waypoint(origin))
            .put("destination", waypoint(destination))
            .put("travelMode", "DRIVE")
            .put("routingPreference", "TRAFFIC_AWARE")
            .put("computeAlternativeRoutes", false)
            .put("languageCode", "pt-BR")
            .put("units", "METRIC")

    private fun waypoint(point: GeoPoint): JSONObject =
        JSONObject().put(
            "location",
            JSONObject().put(
                "latLng",
                JSONObject()
                    .put("latitude", point.latitude)
                    .put("longitude", point.longitude),
            ),
        )

    private fun HttpURLConnection.responseStream() =
        if (responseCode in 200..299) inputStream else errorStream

    private companion object {
        const val ROUTES_ENDPOINT =
            "https://routes.googleapis.com/directions/v2:computeRoutes"
        const val USAGE_ATTRIBUTION_ID = "gmp_git_agentskills_v1"
        const val CONNECTION_TIMEOUT_MILLIS = 15_000
        const val READ_TIMEOUT_MILLIS = 15_000
        const val MAX_ERROR_BODY_LENGTH = 500
    }
}
