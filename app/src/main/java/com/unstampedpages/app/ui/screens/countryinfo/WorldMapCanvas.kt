package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.unit.dp
import com.unstampedpages.app.data.model.Country
import com.unstampedpages.app.data.model.CountryGeometry
import com.unstampedpages.app.data.model.LatLng
import com.unstampedpages.app.data.model.isPointInPolygon
import com.unstampedpages.app.data.repository.CountryGeometryData
import com.unstampedpages.app.ui.theme.MapBorder
import com.unstampedpages.app.ui.theme.MapHighlight
import com.unstampedpages.app.ui.theme.MapLand
import com.unstampedpages.app.ui.theme.MapOcean
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.atan
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sinh
import kotlin.math.tan

/**
 * Color modes for the world map
 */
enum class MapColorMode(val displayName: String) {
    DEFAULT("Default"),
    SECURITY_RISK("Security Risk"),
    VISA_REQUIREMENTS("Visa Requirements")
}

/**
 * Get color for visa requirement status
 */
private fun getVisaRequirementColor(visaRequirement: String): Color {
    return when {
        visaRequirement.equals("Visa not required", ignoreCase = true) -> Color(0xFF4CAF50) // Green
        visaRequirement.equals("eVisa", ignoreCase = true) -> Color(0xFF00BCD4) // Turquoise
        visaRequirement.contains("Visa on arrival", ignoreCase = true) -> Color(0xFFFFC107) // Yellow
        visaRequirement.equals("Visa required", ignoreCase = true) -> Color(0xFF9E9E9E) // Gray
        else -> Color(0xFFFF9800) // Orange for all others
    }
}

/**
 * Mapping from GeoJSON 3-letter ISO codes to repository 2-letter codes
 */
private val geoJsonToRepoId = mapOf(
    // North America & Caribbean
    "USA" to "us", "CAN" to "ca", "MEX" to "mx", "GTM" to "gt", "CUB" to "cu",
    "HTI" to "ht", "DOM" to "do", "HND" to "hn", "NIC" to "ni", "CRI" to "cr",
    "PAN" to "pa", "JAM" to "jm", "SLV" to "sv", "BLZ" to "bz", "GRL" to "gl",
    "BHS" to "bs", "TTO" to "tt",
    // South America
    "BRA" to "br", "ARG" to "ar", "COL" to "co", "PER" to "pe", "VEN" to "ve",
    "CHL" to "cl", "ECU" to "ec", "BOL" to "bo", "PRY" to "py", "URY" to "uy",
    "GUY" to "gy", "SUR" to "sr",
    // Europe
    "GBR" to "gb", "FRA" to "fr", "DEU" to "de", "ITA" to "it", "ESP" to "es",
    "POL" to "pl", "ROU" to "ro", "NLD" to "nl", "BEL" to "be", "CZE" to "cz",
    "GRC" to "gr", "PRT" to "pt", "SWE" to "se", "HUN" to "hu", "AUT" to "at",
    "CHE" to "ch", "BGR" to "bg", "DNK" to "dk", "FIN" to "fi", "NOR" to "no",
    "IRL" to "ie", "HRV" to "hr", "SVK" to "sk", "UKR" to "ua", "RUS" to "ru",
    "TUR" to "tr", "SRB" to "rs", "LTU" to "lt", "LVA" to "lv", "EST" to "ee",
    "SVN" to "si", "ISL" to "is", "ALB" to "al", "BLR" to "by", "BIH" to "ba",
    "CYP" to "cy", "LUX" to "lu", "MDA" to "md", "MNE" to "me", "MKD" to "mk",
    // Africa
    "EGY" to "eg", "ZAF" to "za", "NGA" to "ng", "KEN" to "ke", "MAR" to "ma",
    "ETH" to "et", "TZA" to "tz", "DZA" to "dz", "SDN" to "sd", "UGA" to "ug",
    "GHA" to "gh", "MOZ" to "mz", "CIV" to "ci", "CMR" to "cm", "AGO" to "ao",
    "SEN" to "sn", "ZMB" to "zm", "ZWE" to "zw", "TUN" to "tn", "RWA" to "rw",
    "BWA" to "bw", "NAM" to "na", "LBY" to "ly", "COD" to "cd", "MDG" to "mg",
    "SOM" to "so", "ABV" to "xso", "BDI" to "bi", "BEN" to "bj", "BFA" to "bf",
    "CAF" to "cf", "COG" to "cg", "DJI" to "dj", "ERI" to "er", "GAB" to "ga",
    "GIN" to "gn", "GMB" to "gm", "GNB" to "gw", "GNQ" to "gq", "LBR" to "lr",
    "LSO" to "ls", "MLI" to "ml", "MRT" to "mr", "MWI" to "mw", "NER" to "ne",
    "SDS" to "ss", "SLE" to "sl", "SWZ" to "sz", "TCD" to "td", "TGO" to "tg",
    // Asia
    "CHN" to "cn", "JPN" to "jp", "IND" to "in", "THA" to "th", "VNM" to "vn",
    "KOR" to "kr", "IDN" to "id", "PHL" to "ph", "PAK" to "pk", "BGD" to "bd",
    "MYS" to "my", "SGP" to "sg", "MMR" to "mm", "NPL" to "np", "KHM" to "kh",
    "LAO" to "la", "LKA" to "lk", "TWN" to "tw", "HKG" to "hk", "ARE" to "ae",
    "SAU" to "sa", "ISR" to "il", "IRQ" to "iq", "IRN" to "ir", "AFG" to "af",
    "KAZ" to "kz", "UZB" to "uz", "KGZ" to "kg", "TJK" to "tj", "TKM" to "tm",
    "JOR" to "jo", "LBN" to "lb", "KWT" to "kw", "PSE" to "ps",
    "OMN" to "om", "QAT" to "qa", "BHR" to "bh", "AZE" to "az", "GEO" to "ge",
    "ARM" to "am", "MNG" to "mn", "PRK" to "kp", "BRN" to "bn", "BTN" to "bt",
    "TLS" to "tl", "SYR" to "sy", "YEM" to "ye",
    // Oceania
    "AUS" to "au", "NZL" to "nz", "PNG" to "pg", "FJI" to "fj", "SLB" to "sb",
    "VUT" to "vu", "NCL" to "nc"
)

/**
 * Mercator projection constants and calculations
 */
private object MercatorProjection {
    // Latitude limits to show all land masses including Antarctica
    const val MAX_LATITUDE = 83.0f
    const val MIN_LATITUDE = -85.0f

    // Longitude bounds
    const val MIN_LONGITUDE = -180f
    const val MAX_LONGITUDE = 180f

    // Pre-calculated Mercator Y bounds
    private val maxMercatorY = ln(tan(Math.PI / 4 + Math.toRadians(MAX_LATITUDE.toDouble()) / 2))
    private val minMercatorY = ln(tan(Math.PI / 4 + Math.toRadians(MIN_LATITUDE.toDouble()) / 2))
    private val mercatorYRange = maxMercatorY - minMercatorY

    /**
     * Convert latitude to Mercator Y coordinate (normalized 0-1)
     */
    fun latitudeToY(lat: Float): Float {
        val clampedLat = lat.coerceIn(MIN_LATITUDE, MAX_LATITUDE)
        val latRad = Math.toRadians(clampedLat.toDouble())
        val mercatorY = ln(tan(Math.PI / 4 + latRad / 2))
        return (1f - ((mercatorY - minMercatorY) / mercatorYRange).toFloat())
    }

    /**
     * Convert longitude to Mercator X coordinate (normalized 0-1)
     */
    fun longitudeToX(lng: Float): Float {
        return (lng - MIN_LONGITUDE) / (MAX_LONGITUDE - MIN_LONGITUDE)
    }

    /**
     * Convert normalized Y to latitude
     */
    fun yToLatitude(normalizedY: Float): Float {
        val mercatorY = minMercatorY + (1f - normalizedY) * mercatorYRange
        val latRad = 2 * atan(exp(mercatorY)) - Math.PI / 2
        return Math.toDegrees(latRad).toFloat()
    }

    /**
     * Convert normalized X to longitude
     */
    fun xToLongitude(normalizedX: Float): Float {
        return MIN_LONGITUDE + normalizedX * (MAX_LONGITUDE - MIN_LONGITUDE)
    }

    /**
     * Get the aspect ratio (width/height) of the Mercator projection
     */
    fun getAspectRatio(): Float {
        // Width spans 360 degrees of longitude
        // Height is the Mercator Y range
        return (360.0 / mercatorYRange / (180.0 / Math.PI)).toFloat()
    }
}

/**
 * Normalize offset to wrap around horizontally.
 * Keeps the value in the range [-0.5, 0.5) for seamless wrapping.
 */
private fun normalizeOffsetX(offset: Float): Float {
    var normalized = offset
    while (normalized >= 0.5f) normalized -= 1f
    while (normalized < -0.5f) normalized += 1f
    return normalized
}

/**
 * Realistic world map canvas with Mercator projection.
 * Scales to device width while maintaining proper aspect ratio.
 * Supports horizontal wrapping for continuous panning.
 */
@Composable
fun WorldMapCanvas(
    selectedCountryId: String?,
    onCountryTapped: (countryId: String?) -> Unit,
    colorMode: MapColorMode = MapColorMode.DEFAULT,
    countries: Map<String, Country> = emptyMap(),
    modifier: Modifier = Modifier
) {
    // Bundle transform state into a single object for atomic updates
    data class TransformState(
        val scale: Float = 1f,
        val panX: Float = 0f,
        val panY: Float = 0f
    )
    var transform by remember { mutableStateOf(TransformState()) }

    // Use rememberUpdatedState to ensure gesture handlers always have current transform
    val currentTransform by rememberUpdatedState(transform)

    // Store map layout dimensions so gesture handler uses same values as Canvas
    var mapLayoutWidth by remember { mutableStateOf(0f) }
    var mapLayoutHeight by remember { mutableStateOf(0f) }

    // Store the inverse transformation matrix for tap detection
    val inverseMatrix = remember { android.graphics.Matrix() }
    var matrixValid by remember { mutableStateOf(false) }

    val geometries = remember { CountryGeometryData.getAllGeometries() }

    // Track previous color mode for cross-dissolve animation
    var previousColorMode by remember { mutableStateOf(colorMode) }
    var animationTarget by remember { mutableStateOf(0f) }

    // When color mode changes, trigger animation
    LaunchedEffect(colorMode) {
        if (colorMode != previousColorMode) {
            animationTarget = 1f
        }
    }

    // Animate the transition progress
    val animationProgress by animateFloatAsState(
        targetValue = animationTarget,
        animationSpec = tween(durationMillis = 400),
        finishedListener = {
            // Animation complete, update previous mode and reset
            previousColorMode = colorMode
            animationTarget = 0f
        },
        label = "colorModeTransition"
    )

    // Calculate effective progress (0 = previous mode, 1 = new mode)
    val transitionProgress = if (previousColorMode == colorMode) 1f else animationProgress

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .background(MapOcean)
    ) {
        // Canvas for drawing the map
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Calculate map dimensions maintaining aspect ratio
            val mapAspectRatio = MercatorProjection.getAspectRatio()
            val canvasAspectRatio = canvasWidth / canvasHeight

            val mapWidth: Float
            val mapHeight: Float
            val canvasOffsetX: Float
            val canvasOffsetY: Float

            if (canvasAspectRatio > mapAspectRatio) {
                // Canvas is wider than map - fit to height, center horizontally
                mapHeight = canvasHeight
                mapWidth = canvasHeight * mapAspectRatio
                canvasOffsetX = (canvasWidth - mapWidth) / 2
                canvasOffsetY = 0f
            } else {
                // Canvas is taller than map - fit to width, align to top
                mapWidth = canvasWidth
                mapHeight = canvasWidth / mapAspectRatio
                canvasOffsetX = 0f
                canvasOffsetY = 0f // Top-aligned instead of centered
            }

            // Store these values for gesture handler to use
            mapLayoutWidth = mapWidth
            mapLayoutHeight = mapHeight

            // Draw the map with horizontal wrapping (draw 3 copies: left, center, right)
            val wrapOffsets = listOf(-1f, 0f, 1f)

            wrapOffsets.forEach { wrapOffset ->
                // Read current values from State objects
                val currentScale = transform.scale
                val currentPanX = transform.panX
                val currentPanY = transform.panY

                // Effective panX with wrap offset for this copy
                val effectivePanX = currentPanX + wrapOffset

                // Transform matches screenToMap exactly:
                // screenX = ((nx + panX - 0.5) * scale + 0.5) * mapW + canvasOffsetX
                withTransform({
                    // Step 3: Final position on canvas + center offset
                    translate(canvasOffsetX + 0.5f * mapWidth, canvasOffsetY + 0.5f * mapHeight)
                    // Step 2: Apply zoom
                    scale(currentScale, currentScale)
                    // Step 1: Apply pan and shift to center coordinates
                    translate((effectivePanX - 0.5f) * mapWidth, (currentPanY - 0.5f) * mapHeight)
                }) {
                    // Draw ocean background for the map area
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
                        // Look up country by converting GeoJSON ID to repo ID
                        val repoId = geoJsonToRepoId[geometry.countryId]
                        val country = repoId?.let { countries[it] }
                        drawCountryMercator(
                            geometry = geometry,
                            isSelected = isSelected,
                            colorMode = colorMode,
                            previousColorMode = previousColorMode,
                            transitionProgress = transitionProgress,
                            country = country,
                            mapWidth = mapWidth,
                            mapHeight = mapHeight
                        )
                    }

                    // Store the inverse matrix for tap detection (only for center copy)
                    if (wrapOffset == 0f) {
                        val matrix = drawContext.canvas.nativeCanvas.getMatrix()
                        matrixValid = matrix.invert(inverseMatrix)
                    }
                }
            }

            // Draw compass rose (not affected by zoom)
            drawCompassRose()

            // Draw zoom indicator
            drawZoomIndicator(transform.scale)
        }

        // Transparent overlay for gesture handling (unified tap + pan/zoom)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val downPosition = down.position
                        var totalDragDistance = 0f
                        var wasDragged = false

                        do {
                            val event = awaitPointerEvent()
                            val changes = event.changes

                            // Calculate map dimensions for pan calculations
                            val canvasWidth = size.width.toFloat()
                            val canvasHeight = size.height.toFloat()
                            val mapAspectRatio = MercatorProjection.getAspectRatio()
                            val canvasAspectRatio = canvasWidth / canvasHeight
                            val mapWidth = if (canvasAspectRatio > mapAspectRatio) {
                                canvasHeight * mapAspectRatio
                            } else {
                                canvasWidth
                            }
                            val mapHeight = if (canvasAspectRatio > mapAspectRatio) {
                                canvasHeight
                            } else {
                                canvasWidth / mapAspectRatio
                            }

                            if (changes.size > 1) {
                                // Multi-touch: zoom and pan
                                wasDragged = true
                                val zoom = event.calculateZoom()
                                val pan = event.calculatePan()

                                val current = currentTransform
                                val newScale = (current.scale * zoom).coerceIn(1f, 8f)

                                // Allow full vertical panning when zoomed to see entire map
                                val maxPanY = if (newScale <= 1f) 0f else 0.5f

                                // Pan delta: convert screen pixels to normalized map units
                                val newPanX = current.panX + pan.x / (mapWidth * newScale)
                                val newPanY = current.panY + pan.y / (mapHeight * newScale)

                                transform = TransformState(
                                    scale = newScale,
                                    panX = normalizeOffsetX(newPanX),
                                    panY = newPanY.coerceIn(-maxPanY, maxPanY)
                                )

                                changes.forEach { it.consume() }
                            } else if (changes.size == 1) {
                                val change = changes.first()
                                if (change.positionChanged()) {
                                    val panDelta = change.position - change.previousPosition
                                    val distance = panDelta.getDistance()
                                    totalDragDistance += distance

                                    // Only start panning after moving a minimum distance (tap threshold)
                                    if (totalDragDistance > 15f) {
                                        wasDragged = true

                                        val current = currentTransform
                                        // Allow full vertical panning when zoomed to see entire map
                                        val maxPanY = if (current.scale <= 1f) 0f else 0.5f

                                        // Pan delta: convert screen pixels to normalized map units
                                        val newPanX = current.panX + panDelta.x / (mapWidth * current.scale)
                                        val newPanY = current.panY + panDelta.y / (mapHeight * current.scale)

                                        transform = current.copy(
                                            panX = normalizeOffsetX(newPanX),
                                            panY = newPanY.coerceIn(-maxPanY, maxPanY)
                                        )
                                    }
                                }
                            }
                        } while (changes.any { it.pressed })

                        // If finger lifted without much movement, treat as tap
                        if (!wasDragged) {
                            // Skip if matrix hasn't been set yet
                            if (!matrixValid) {
                                return@awaitEachGesture
                            }

                            val mapWidth = mapLayoutWidth
                            val mapHeight = mapLayoutHeight

                            // Skip if Canvas hasn't drawn yet
                            if (mapWidth <= 0f || mapHeight <= 0f) {
                                return@awaitEachGesture
                            }

                            // Use the inverse matrix to convert screen coords to local coords
                            val screenPts = floatArrayOf(downPosition.x, downPosition.y)
                            inverseMatrix.mapPoints(screenPts)
                            val localX = screenPts[0]
                            val localY = screenPts[1]

                            // Convert local pixel coords to normalized (0-1) coords
                            var normalizedX = localX / mapWidth
                            val normalizedY = localY / mapHeight

                            // Wrap X for horizontal continuity
                            while (normalizedX < 0f) normalizedX += 1f
                            while (normalizedX >= 1f) normalizedX -= 1f

                            // Find the country at this position (returns GeoJSON ID like "USA")
                            val geoJsonId = findCountryAtNormalizedPoint(normalizedX, normalizedY, geometries)

                            // Convert to repository ID (like "us")
                            val repoId = geoJsonId?.let { geoJsonToRepoId[it] }

                            onCountryTapped(repoId)
                        }
                    }
                }
        )
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
    colorMode: MapColorMode,
    previousColorMode: MapColorMode,
    transitionProgress: Float,
    country: Country?,
    mapWidth: Float,
    mapHeight: Float
) {
    // Helper function to get color for a specific mode
    fun getColorForMode(mode: MapColorMode): Color {
        return when {
            isSelected -> MapHighlight
            mode == MapColorMode.SECURITY_RISK && country != null -> country.safetyLevel.color
            mode == MapColorMode.VISA_REQUIREMENTS && country != null -> getVisaRequirementColor(country.visaRequirement)
            else -> MapLand
        }
    }

    // Get colors for previous and current modes, then blend based on transition progress
    val previousColor = getColorForMode(previousColorMode)
    val currentColor = getColorForMode(colorMode)
    val fillColor = if (isSelected) MapHighlight else lerp(previousColor, currentColor, transitionProgress)
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
