package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.unstampedpages.app.data.model.CountryGeometry
import com.unstampedpages.app.data.model.LatLng
import com.unstampedpages.app.data.model.isPointInPolygon
import com.unstampedpages.app.data.repository.CountryGeometryData
import com.unstampedpages.app.ui.theme.MapBorder
import com.unstampedpages.app.ui.theme.MapHighlight
import com.unstampedpages.app.ui.theme.MapLand
import com.unstampedpages.app.ui.theme.MapOcean
import kotlin.math.atan
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sinh
import kotlin.math.tan

/**
 * Mercator projection constants
 */
private object MercatorProjection {
    // Standard Mercator latitude limits (cuts off polar regions)
    const val MAX_LATITUDE = 85.0f
    const val MIN_LATITUDE = -85.0f

    // Longitude bounds
    const val MIN_LONGITUDE = -180f
    const val MAX_LONGITUDE = 180f

    /**
     * Convert latitude to Mercator Y coordinate (normalized 0-1)
     */
    fun latitudeToY(lat: Float): Float {
        val clampedLat = lat.coerceIn(MIN_LATITUDE, MAX_LATITUDE)
        val latRad = Math.toRadians(clampedLat.toDouble())
        val mercatorY = ln(tan(Math.PI / 4 + latRad / 2))

        // Normalize to 0-1 range
        val maxMercatorY = ln(tan(Math.PI / 4 + Math.toRadians(MAX_LATITUDE.toDouble()) / 2))
        val minMercatorY = ln(tan(Math.PI / 4 + Math.toRadians(MIN_LATITUDE.toDouble()) / 2))

        return (1f - ((mercatorY - minMercatorY) / (maxMercatorY - minMercatorY)).toFloat())
    }

    /**
     * Convert longitude to Mercator X coordinate (normalized 0-1)
     */
    fun longitudeToX(lng: Float): Float {
        return (lng - MIN_LONGITUDE) / (MAX_LONGITUDE - MIN_LONGITUDE)
    }

    /**
     * Convert screen Y to latitude
     */
    fun yToLatitude(y: Float): Float {
        val maxMercatorY = ln(tan(Math.PI / 4 + Math.toRadians(MAX_LATITUDE.toDouble()) / 2))
        val minMercatorY = ln(tan(Math.PI / 4 + Math.toRadians(MIN_LATITUDE.toDouble()) / 2))

        val mercatorY = minMercatorY + (1f - y) * (maxMercatorY - minMercatorY)
        val latRad = 2 * atan(exp(mercatorY)) - Math.PI / 2
        return Math.toDegrees(latRad).toFloat()
    }

    /**
     * Convert screen X to longitude
     */
    fun xToLongitude(x: Float): Float {
        return MIN_LONGITUDE + x * (MAX_LONGITUDE - MIN_LONGITUDE)
    }

    /**
     * Get the aspect ratio of the Mercator projection
     */
    fun getAspectRatio(): Float {
        val maxMercatorY = ln(tan(Math.PI / 4 + Math.toRadians(MAX_LATITUDE.toDouble()) / 2))
        val minMercatorY = ln(tan(Math.PI / 4 + Math.toRadians(MIN_LATITUDE.toDouble()) / 2))
        val mercatorHeight = (maxMercatorY - minMercatorY).toFloat()
        val mercatorWidth = (MAX_LONGITUDE - MIN_LONGITUDE) / 180f * Math.PI.toFloat()
        return mercatorWidth / mercatorHeight
    }
}

/**
 * Realistic world map canvas with Mercator projection.
 * Scales to device width while maintaining proper aspect ratio.
 */
@Composable
fun WorldMapCanvas(
    selectedCountryId: String?,
    onCountryTapped: (countryId: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val geometries = remember { CountryGeometryData.getAllGeometries() }

    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(1f, 8f)

        // Update scale
        scale = newScale

        // Update offset with bounds checking
        val maxOffsetX = (newScale - 1f) * 0.5f
        val maxOffsetY = (newScale - 1f) * 0.5f

        offsetX = (offsetX + panChange.x / 1000f).coerceIn(-maxOffsetX, maxOffsetX)
        offsetY = (offsetY + panChange.y / 1000f).coerceIn(-maxOffsetY, maxOffsetY)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MapOcean)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .transformable(state = transformState)
                .pointerInput(geometries, scale, offsetX, offsetY) {
                    detectTapGestures { tapOffset ->
                        // Convert tap position to normalized map coordinates
                        val mapWidth = size.width.toFloat()
                        val mapHeight = size.height.toFloat()

                        // Reverse the transformation to get map coordinates
                        val normalizedX = (tapOffset.x / mapWidth - 0.5f) / scale + 0.5f - offsetX
                        val normalizedY = (tapOffset.y / mapHeight - 0.5f) / scale + 0.5f - offsetY

                        // Find which country was tapped
                        val tappedCountry = findCountryAtNormalizedPoint(
                            normalizedX = normalizedX,
                            normalizedY = normalizedY,
                            geometries = geometries
                        )
                        onCountryTapped(tappedCountry)
                    }
                }
        ) {
            val mapWidth = size.width
            val mapHeight = size.height

            // Apply zoom and pan transformation
            withTransform({
                // Center the transformation
                translate(mapWidth / 2, mapHeight / 2)
                scale(scale, scale)
                translate(-mapWidth / 2, -mapHeight / 2)
                translate(offsetX * mapWidth, offsetY * mapHeight)
            }) {
                // Draw ocean background (already set on Box)
                drawRect(
                    color = MapOcean,
                    topLeft = Offset.Zero,
                    size = Size(mapWidth, mapHeight)
                )

                // Draw grid lines
                drawMercatorGrid(mapWidth, mapHeight)

                // Draw all countries
                geometries.forEach { geometry ->
                    val isSelected = geometry.countryId == selectedCountryId
                    drawCountryMercator(
                        geometry = geometry,
                        isSelected = isSelected,
                        mapWidth = mapWidth,
                        mapHeight = mapHeight
                    )
                }
            }

            // Draw compass rose (not affected by zoom)
            drawCompassRose()

            // Draw zoom indicator
            drawZoomIndicator(scale)
        }
    }
}

/**
 * Find which country contains the given normalized point.
 */
private fun findCountryAtNormalizedPoint(
    normalizedX: Float,
    normalizedY: Float,
    geometries: List<CountryGeometry>
): String? {
    // Convert normalized coordinates back to lat/lng
    val lng = MercatorProjection.xToLongitude(normalizedX)
    val lat = MercatorProjection.yToLatitude(normalizedY)

    for (geometry in geometries) {
        for (polygon in geometry.polygons) {
            if (isPointInLatLngPolygon(lat, lng, polygon)) {
                return geometry.countryId
            }
        }
    }
    return null
}

/**
 * Check if a lat/lng point is inside a polygon using ray casting.
 */
private fun isPointInLatLngPolygon(lat: Float, lng: Float, polygon: List<LatLng>): Boolean {
    if (polygon.size < 3) return false

    var inside = false
    var j = polygon.size - 1

    for (i in polygon.indices) {
        val xi = polygon[i].lng
        val yi = polygon[i].lat
        val xj = polygon[j].lng
        val yj = polygon[j].lat

        if (((yi > lat) != (yj > lat)) &&
            (lng < (xj - xi) * (lat - yi) / (yj - yi) + xi)) {
            inside = !inside
        }
        j = i
    }

    return inside
}

/**
 * Convert LatLng to screen position using Mercator projection.
 */
private fun latLngToMercator(latLng: LatLng, mapWidth: Float, mapHeight: Float): Offset {
    val x = MercatorProjection.longitudeToX(latLng.lng) * mapWidth
    val y = MercatorProjection.latitudeToY(latLng.lat) * mapHeight
    return Offset(x, y)
}

/**
 * Draw a country using Mercator projection.
 */
private fun DrawScope.drawCountryMercator(
    geometry: CountryGeometry,
    isSelected: Boolean,
    mapWidth: Float,
    mapHeight: Float
) {
    val fillColor = if (isSelected) MapHighlight else MapLand
    val strokeColor = if (isSelected) MapHighlight.copy(alpha = 0.9f) else MapBorder
    val strokeWidth = if (isSelected) 2.dp.toPx() else 0.8f.dp.toPx()

    geometry.polygons.forEach { polygon ->
        if (polygon.size >= 3) {
            val path = Path().apply {
                val firstPoint = latLngToMercator(polygon[0], mapWidth, mapHeight)
                moveTo(firstPoint.x, firstPoint.y)

                for (i in 1 until polygon.size) {
                    val point = latLngToMercator(polygon[i], mapWidth, mapHeight)
                    lineTo(point.x, point.y)
                }
                close()
            }

            // Draw fill
            drawPath(path, fillColor, style = Fill)

            // Draw border
            drawPath(path, strokeColor, style = Stroke(width = strokeWidth))

            // Draw glow effect if selected
            if (isSelected) {
                drawPath(
                    path,
                    MapHighlight.copy(alpha = 0.4f),
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        }
    }
}

/**
 * Draw Mercator grid lines.
 */
private fun DrawScope.drawMercatorGrid(mapWidth: Float, mapHeight: Float) {
    val gridColor = Color(0x25FFFFFF)
    val majorGridColor = Color(0x40FFFFFF)
    val lineWidth = 0.5f.dp.toPx()
    val majorLineWidth = 1f.dp.toPx()

    // Latitude lines (every 20 degrees)
    val latitudes = listOf(-80f, -60f, -40f, -20f, 0f, 20f, 40f, 60f, 80f)
    latitudes.forEach { lat ->
        val y = MercatorProjection.latitudeToY(lat) * mapHeight
        val isMajor = lat == 0f

        drawLine(
            color = if (isMajor) majorGridColor else gridColor,
            start = Offset(0f, y),
            end = Offset(mapWidth, y),
            strokeWidth = if (isMajor) majorLineWidth else lineWidth
        )
    }

    // Longitude lines (every 30 degrees)
    val longitudes = listOf(-180f, -150f, -120f, -90f, -60f, -30f, 0f, 30f, 60f, 90f, 120f, 150f, 180f)
    longitudes.forEach { lng ->
        val x = MercatorProjection.longitudeToX(lng) * mapWidth
        val isMajor = lng == 0f

        drawLine(
            color = if (isMajor) majorGridColor else gridColor,
            start = Offset(x, 0f),
            end = Offset(x, mapHeight),
            strokeWidth = if (isMajor) majorLineWidth else lineWidth
        )
    }
}

/**
 * Draw compass rose.
 */
private fun DrawScope.drawCompassRose() {
    val roseSize = 44.dp.toPx()
    val centerX = size.width - roseSize / 2 - 12.dp.toPx()
    val centerY = size.height - roseSize / 2 - 12.dp.toPx()
    val radius = roseSize / 2

    // Background
    drawCircle(
        color = Color(0xE0F4E4BC),
        radius = radius,
        center = Offset(centerX, centerY)
    )

    drawCircle(
        color = MapBorder,
        radius = radius,
        center = Offset(centerX, centerY),
        style = Stroke(width = 1.5f.dp.toPx())
    )

    // North pointer
    val northPath = Path().apply {
        moveTo(centerX, centerY - radius + 5.dp.toPx())
        lineTo(centerX - 4.dp.toPx(), centerY)
        lineTo(centerX, centerY - 3.dp.toPx())
        close()
    }
    drawPath(northPath, Color(0xFF8B2500))

    // South pointer
    val southPath = Path().apply {
        moveTo(centerX, centerY + radius - 5.dp.toPx())
        lineTo(centerX + 4.dp.toPx(), centerY)
        lineTo(centerX, centerY + 3.dp.toPx())
        close()
    }
    drawPath(southPath, MapBorder)

    // E/W line
    drawLine(
        color = MapBorder,
        start = Offset(centerX - radius + 6.dp.toPx(), centerY),
        end = Offset(centerX + radius - 6.dp.toPx(), centerY),
        strokeWidth = 1.5f.dp.toPx()
    )

    // Center
    drawCircle(
        color = MapBorder,
        radius = 2.dp.toPx(),
        center = Offset(centerX, centerY)
    )
}

/**
 * Draw zoom level indicator.
 */
private fun DrawScope.drawZoomIndicator(scale: Float) {
    if (scale > 1.1f) {
        val x = 12.dp.toPx()
        val y = size.height - 12.dp.toPx()

        // Zoom indicator bars (more bars = more zoom)
        val barCount = ((scale - 1f) / 1.5f).toInt().coerceIn(1, 5)
        val barWidth = 4.dp.toPx()
        val barSpacing = 3.dp.toPx()
        val maxBarHeight = 16.dp.toPx()

        for (i in 0 until barCount) {
            val barHeight = maxBarHeight * (i + 1) / 5f
            drawRoundRect(
                color = Color(0xCCFFFFFF),
                topLeft = Offset(x + i * (barWidth + barSpacing), y - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
            )
        }
    }
}
