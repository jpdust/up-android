package com.unstampedpages.app.data.model

import androidx.compose.ui.geometry.Offset

/**
 * Represents a country's geographic boundary as a list of polygon coordinates.
 * Coordinates are in latitude/longitude (WGS84).
 */
data class CountryGeometry(
    val countryId: String,
    val polygons: List<List<LatLng>>
)

/**
 * Geographic coordinate in latitude/longitude.
 */
data class LatLng(
    val lat: Float,
    val lng: Float
) {
    /**
     * Convert to screen coordinates using Equirectangular projection.
     * @param width Canvas width
     * @param height Canvas height
     * @param offsetX Pan offset X
     * @param offsetY Pan offset Y
     * @param scale Zoom scale
     */
    fun toScreenCoordinates(
        width: Float,
        height: Float,
        offsetX: Float = 0f,
        offsetY: Float = 0f,
        scale: Float = 1f
    ): Offset {
        // Equirectangular projection (simple cylindrical)
        // Longitude: -180 to 180 -> 0 to width
        // Latitude: 90 to -90 -> 0 to height
        val x = ((lng + 180f) / 360f) * width * scale + offsetX
        val y = ((90f - lat) / 180f) * height * scale + offsetY
        return Offset(x, y)
    }
}

/**
 * Check if a point is inside a polygon using ray casting algorithm.
 */
fun isPointInPolygon(point: Offset, polygon: List<Offset>): Boolean {
    if (polygon.size < 3) return false

    var inside = false
    var j = polygon.size - 1

    for (i in polygon.indices) {
        val xi = polygon[i].x
        val yi = polygon[i].y
        val xj = polygon[j].x
        val yj = polygon[j].y

        if (((yi > point.y) != (yj > point.y)) &&
            (point.x < (xj - xi) * (point.y - yi) / (yj - yi) + xi)) {
            inside = !inside
        }
        j = i
    }

    return inside
}
