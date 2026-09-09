package com.sextou.networking.response

import com.sextou.domain.places.model.GeoPoint
import com.sextou.domain.routes.model.RoutePath
import com.sextou.repository.DomainMapperResponse

data class RoutePathResponse(
    val encodedPolyline: String,
) : DomainMapperResponse<RoutePath> {
    override fun mapToDomain(): RoutePath {
        require(encodedPolyline.isNotBlank()) { "A polyline da rota não pode ser vazia." }
        return RoutePath(points = PolylineDecoder.decode(encodedPolyline))
    }
}

internal object PolylineDecoder {
    fun decode(encodedPolyline: String): List<GeoPoint> {
        require(encodedPolyline.isNotBlank()) { "A polyline da rota não pode ser vazia." }

        val points = mutableListOf<GeoPoint>()
        var index = 0
        var latitude = 0
        var longitude = 0

        while (index < encodedPolyline.length) {
            val latitudeComponent = decodeComponent(encodedPolyline, index)
            index = latitudeComponent.nextIndex
            val longitudeComponent = decodeComponent(encodedPolyline, index)
            index = longitudeComponent.nextIndex

            latitude += latitudeComponent.toDelta()
            longitude += longitudeComponent.toDelta()
            points += GeoPoint(
                latitude = latitude / COORDINATE_SCALE,
                longitude = longitude / COORDINATE_SCALE,
            )
        }

        require(points.isNotEmpty()) { "A polyline da rota não contém pontos." }
        return points
    }

    private fun decodeComponent(encodedPolyline: String, startIndex: Int): Component {
        var index = startIndex
        var result = 0
        var shift = 0

        while (true) {
            require(index < encodedPolyline.length) { "A polyline da rota está incompleta." }
            val value = encodedPolyline[index++].code - ASCII_OFFSET
            result = result or ((value and VALUE_MASK) shl shift)
            if (value < CONTINUATION_THRESHOLD) break
            shift += SHIFT_INCREMENT
            require(shift < MAX_SHIFT) { "A polyline da rota contém um valor inválido." }
        }

        return Component(value = result, nextIndex = index)
    }

    private data class Component(
        val value: Int,
        val nextIndex: Int,
    ) {
        fun toDelta(): Int = if ((value and 1) != 0) {
            -(value shr 1)
        } else {
            value shr 1
        }
    }

    private const val ASCII_OFFSET = 63
    private const val VALUE_MASK = 0x1F
    private const val CONTINUATION_THRESHOLD = 0x20
    private const val SHIFT_INCREMENT = 5
    private const val MAX_SHIFT = 32
    private const val COORDINATE_SCALE = 100_000.0
}
