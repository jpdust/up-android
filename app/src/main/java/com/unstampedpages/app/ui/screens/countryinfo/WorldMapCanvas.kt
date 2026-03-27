package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unstampedpages.app.data.model.Country
import com.unstampedpages.app.data.model.CountryGeometry
import com.unstampedpages.app.data.model.LatLng
import com.unstampedpages.app.data.model.VisaRequirement
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
    VISA_REQUIREMENTS("Visa Requirements"),
    PASSPORT_VALIDITY("Passport Validity")
}

/**
 * Colors for passport validity map mode
 */
private object PassportValidityColors {
    val SixMonths = Color(0xFF9E9E9E)      // Gray
    val ThreeMonths = Color(0xFF00BCD4)    // Teal
    val PlannedStay = Color(0xFF4CAF50)    // Green
    val Other = Color(0xFFFFC107)          // Yellow
}

/**
 * Transform state for map zoom and pan
 */
private data class TransformState(
    val scale: Float = 1f,
    val panX: Float = 0f,
    val panY: Float = 0f
)

/**
 * Layout dimensions for the map
 */
private data class MapLayout(
    val mapWidth: Float,
    val mapHeight: Float,
    val canvasOffsetX: Float,
    val canvasOffsetY: Float
)

/**
 * Calculate map layout dimensions based on canvas size
 */
private fun calculateMapLayout(canvasWidth: Float, canvasHeight: Float): MapLayout {
    val mapAspectRatio = MercatorProjection.getAspectRatio()
    val canvasAspectRatio = canvasWidth / canvasHeight

    return if (canvasAspectRatio > mapAspectRatio) {
        // Canvas is wider than map - fit to height, center horizontally
        val mapHeight = canvasHeight
        val mapWidth = canvasHeight * mapAspectRatio
        MapLayout(
            mapWidth = mapWidth,
            mapHeight = mapHeight,
            canvasOffsetX = (canvasWidth - mapWidth) / 2,
            canvasOffsetY = 0f
        )
    } else {
        // Canvas is taller than map - fit to width, align to top
        val mapWidth = canvasWidth
        val mapHeight = canvasWidth / mapAspectRatio
        MapLayout(
            mapWidth = mapWidth,
            mapHeight = mapHeight,
            canvasOffsetX = 0f,
            canvasOffsetY = 0f
        )
    }
}

/**
 * Calculate new transform state for multi-touch zoom/pan gesture
 */
private fun calculateMultiTouchTransform(
    current: TransformState,
    zoom: Float,
    pan: Offset,
    mapWidth: Float,
    mapHeight: Float
): TransformState {
    val newScale = (current.scale * zoom).coerceIn(1f, 25f)
    // Scale maxPanY with zoom level to allow panning to map edges at high zoom
    // At scale 1: maxPanY = 0 (no panning needed, full map visible)
    // At scale 25: maxPanY ≈ 0.98 (can pan to see top/bottom edges)
    val maxPanY = if (newScale <= 1f) 0f else (1f - 0.5f / newScale)
    val newPanX = current.panX + pan.x / (mapWidth * newScale)
    val newPanY = current.panY + pan.y / (mapHeight * newScale)

    return TransformState(
        scale = newScale,
        panX = normalizeOffsetX(newPanX),
        panY = newPanY.coerceIn(-maxPanY, maxPanY)
    )
}

/**
 * Calculate new transform state for single-touch pan gesture
 */
private fun calculateSingleTouchTransform(
    current: TransformState,
    panDelta: Offset,
    mapWidth: Float,
    mapHeight: Float
): TransformState {
    // Scale maxPanY with zoom level to allow panning to map edges at high zoom
    val maxPanY = if (current.scale <= 1f) 0f else (1f - 0.5f / current.scale)
    val newPanX = current.panX + panDelta.x / (mapWidth * current.scale)
    val newPanY = current.panY + panDelta.y / (mapHeight * current.scale)

    return current.copy(
        panX = normalizeOffsetX(newPanX),
        panY = newPanY.coerceIn(-maxPanY, maxPanY)
    )
}

/**
 * Convert screen tap position to country ID
 */
private fun screenPositionToCountryId(
    screenPosition: Offset,
    inverseMatrix: android.graphics.Matrix,
    mapWidth: Float,
    mapHeight: Float,
    geometries: List<CountryGeometry>
): String? {
    val screenPts = floatArrayOf(screenPosition.x, screenPosition.y)
    inverseMatrix.mapPoints(screenPts)

    var normalizedX = screenPts[0] / mapWidth
    val normalizedY = screenPts[1] / mapHeight

    // Wrap X for horizontal continuity
    normalizedX = normalizeNormalizedX(normalizedX)

    val geoJsonId = findCountryAtNormalizedPoint(normalizedX, normalizedY, geometries)
    return geoJsonId?.let { geoJsonToRepoId[it] }
}

/**
 * Normalize X coordinate to 0-1 range
 */
private fun normalizeNormalizedX(x: Float): Float {
    var normalized = x
    while (normalized < 0f) normalized += 1f
    while (normalized >= 1f) normalized -= 1f
    return normalized
}

/**
 * State holder for map gesture handling
 */
private class MapGestureState(
    val geometries: List<CountryGeometry>,
    val inverseMatrix: android.graphics.Matrix,
    var matrixValid: Boolean = false,
    var mapLayoutWidth: Float = 0f,
    var mapLayoutHeight: Float = 0f,
    var canvasWidth: Float = 0f,
    var canvasHeight: Float = 0f,
    var compassCenterX: Float = 0f,
    var compassCenterY: Float = 0f,
    var compassRadius: Float = 0f
)

/**
 * Process a tap gesture and return the tapped country ID
 */
private fun MapGestureState.processTap(position: Offset): String? {
    if (!matrixValid || mapLayoutWidth <= 0f || mapLayoutHeight <= 0f) {
        return null
    }
    return screenPositionToCountryId(
        screenPosition = position,
        inverseMatrix = inverseMatrix,
        mapWidth = mapLayoutWidth,
        mapHeight = mapLayoutHeight,
        geometries = geometries
    )
}

/**
 * Check if a tap position is within the compass area
 */
private fun MapGestureState.isCompassTap(position: Offset): Boolean {
    if (compassRadius <= 0f) return false
    val dx = position.x - compassCenterX
    val dy = position.y - compassCenterY
    return (dx * dx + dy * dy) <= (compassRadius * compassRadius)
}

/**
 * Result of processing a pointer event
 */
private data class GestureResult(
    val wasDragged: Boolean,
    val dragDistance: Float
)

/**
 * Handle multi-touch gesture (pinch to zoom)
 */
private fun handleMultiTouch(
    event: androidx.compose.ui.input.pointer.PointerEvent,
    layout: MapLayout,
    currentTransform: () -> TransformState,
    onTransformChange: (TransformState) -> Unit
) {
    onTransformChange(
        calculateMultiTouchTransform(
            current = currentTransform(),
            zoom = event.calculateZoom(),
            pan = event.calculatePan(),
            mapWidth = layout.mapWidth,
            mapHeight = layout.mapHeight
        )
    )
    event.changes.forEach { it.consume() }
}

/**
 * Handle single-touch gesture (pan). Returns updated drag distance.
 */
private fun handleSingleTouch(
    change: androidx.compose.ui.input.pointer.PointerInputChange,
    currentDragDistance: Float,
    layout: MapLayout,
    currentTransform: () -> TransformState,
    onTransformChange: (TransformState) -> Unit
): GestureResult {
    if (!change.positionChanged()) {
        return GestureResult(wasDragged = false, dragDistance = currentDragDistance)
    }

    val panDelta = change.position - change.previousPosition
    val newDragDistance = currentDragDistance + panDelta.getDistance()

    if (newDragDistance <= 15f) {
        return GestureResult(wasDragged = false, dragDistance = newDragDistance)
    }

    onTransformChange(
        calculateSingleTouchTransform(
            current = currentTransform(),
            panDelta = panDelta,
            mapWidth = layout.mapWidth,
            mapHeight = layout.mapHeight
        )
    )
    return GestureResult(wasDragged = true, dragDistance = newDragDistance)
}

/**
 * Modifier extension for map gesture handling (pan, zoom, tap)
 */
private fun Modifier.mapGestures(
    gestureState: MapGestureState,
    currentTransform: () -> TransformState,
    onTransformChange: (TransformState) -> Unit,
    onCountryTapped: (String?) -> Unit,
    colorMode: MapColorMode,
    onCompassTapped: () -> Unit
): Modifier = this.pointerInput(colorMode) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val downPosition = down.position
        var totalDragDistance = 0f
        var wasDragged = false
        val layout = calculateMapLayout(size.width.toFloat(), size.height.toFloat())

        do {
            val event = awaitPointerEvent()
            val changes = event.changes

            when {
                changes.size > 1 -> {
                    wasDragged = true
                    handleMultiTouch(event, layout, currentTransform, onTransformChange)
                }
                changes.size == 1 -> {
                    val result = handleSingleTouch(
                        changes.first(), totalDragDistance, layout, currentTransform, onTransformChange
                    )
                    totalDragDistance = result.dragDistance
                    wasDragged = wasDragged || result.wasDragged
                }
            }
        } while (changes.any { it.pressed })

        if (!wasDragged) {
            // Check if compass was tapped (only for non-default modes)
            if (colorMode != MapColorMode.DEFAULT && gestureState.isCompassTap(downPosition)) {
                onCompassTapped()
            } else {
                onCountryTapped(gestureState.processTap(downPosition))
            }
        }
    }
}

/**
 * Get color for visa requirement status
 */
private fun getVisaRequirementColor(visaRequirement: VisaRequirement): Color {
    return visaRequirement.color
}

/**
 * Get color for passport validity requirement
 */
private fun getPassportValidityColor(passportValidity: String?): Color {
    return when (passportValidity) {
        "6 months" -> PassportValidityColors.SixMonths
        "3 months" -> PassportValidityColors.ThreeMonths
        "Planned length of stay" -> PassportValidityColors.PlannedStay
        null -> PassportValidityColors.Other
        else -> PassportValidityColors.Other
    }
}

/**
 * Parameters for drawing the map content
 */
private data class MapDrawParams(
    val geometries: List<CountryGeometry>,
    val selectedCountryId: String?,
    val colorMode: MapColorMode,
    val previousColorMode: MapColorMode,
    val transitionProgress: Float,
    val countries: Map<String, Country>,
    val scale: Float
)

/**
 * Color transition state for animated color mode changes
 */
private data class ColorTransitionState(
    val colorMode: MapColorMode,
    val previousColorMode: MapColorMode,
    val transitionProgress: Float
)

/**
 * Drawing context with map dimensions and scale
 */
private data class CountryDrawContext(
    val mapWidth: Float,
    val mapHeight: Float,
    val scale: Float
)

/**
 * Draw a single copy of the map (used for horizontal wrapping)
 */
private fun DrawScope.drawMapCopy(
    wrapOffset: Float,
    transform: TransformState,
    layout: MapLayout,
    params: MapDrawParams,
    inverseMatrix: android.graphics.Matrix,
    onMatrixCaptured: (Boolean) -> Unit
) {
    val effectivePanX = transform.panX + wrapOffset

    withTransform({
        translate(layout.canvasOffsetX + 0.5f * layout.mapWidth, layout.canvasOffsetY + 0.5f * layout.mapHeight)
        scale(transform.scale, transform.scale)
        translate((effectivePanX - 0.5f) * layout.mapWidth, (transform.panY - 0.5f) * layout.mapHeight)
    }) {
        // Ocean background
        drawRect(
            color = MapOcean,
            topLeft = Offset.Zero,
            size = Size(layout.mapWidth, layout.mapHeight)
        )

        // Grid lines
        drawMercatorGrid(layout.mapWidth, layout.mapHeight, params.scale)

        // Countries
        val colorTransition = ColorTransitionState(
            colorMode = params.colorMode,
            previousColorMode = params.previousColorMode,
            transitionProgress = params.transitionProgress
        )
        val countryDrawContext = CountryDrawContext(
            mapWidth = layout.mapWidth,
            mapHeight = layout.mapHeight,
            scale = params.scale
        )
        params.geometries.forEach { geometry ->
            val isSelected = geometry.countryId == params.selectedCountryId
            val repoId = geoJsonToRepoId[geometry.countryId]
            val country = repoId?.let { params.countries[it] }
            drawCountryMercator(
                geometry = geometry,
                isSelected = isSelected,
                colorTransition = colorTransition,
                country = country,
                drawContext = countryDrawContext
            )
        }

        // Capture inverse matrix for tap detection (center copy only)
        if (wrapOffset == 0f) {
            val matrix = drawContext.canvas.nativeCanvas.getMatrix()
            onMatrixCaptured(matrix.invert(inverseMatrix))
        }
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
    "BHS" to "bs", "TTO" to "tt", "ATG" to "ag", "BRB" to "bb", "DMA" to "dm",
    "GRD" to "gd", "KNA" to "kn", "LCA" to "lc", "VCT" to "vc",
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
    "AND" to "ad", "LIE" to "li", "MLT" to "mt", "MCO" to "mc", "SMR" to "sm", "VAT" to "va",
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
    "CPV" to "cv", "COM" to "km", "MUS" to "mu", "STP" to "st", "SYC" to "sc",
    // Asia
    "CHN" to "cn", "JPN" to "jp", "IND" to "in", "THA" to "th", "VNM" to "vn",
    "KOR" to "kr", "IDN" to "id", "PHL" to "ph", "PAK" to "pk", "BGD" to "bd",
    "MYS" to "my", "SGP" to "sg", "MMR" to "mm", "MDV" to "mv", "NPL" to "np", "KHM" to "kh",
    "LAO" to "la", "LKA" to "lk", "TWN" to "tw", "HKG" to "hk", "ARE" to "ae",
    "SAU" to "sa", "ISR" to "il", "IRQ" to "iq", "IRN" to "ir", "AFG" to "af",
    "KAZ" to "kz", "UZB" to "uz", "KGZ" to "kg", "TJK" to "tj", "TKM" to "tm",
    "JOR" to "jo", "LBN" to "lb", "KWT" to "kw", "PSE" to "ps",
    "OMN" to "om", "QAT" to "qa", "BHR" to "bh", "AZE" to "az", "GEO" to "ge",
    "ARM" to "am", "MNG" to "mn", "PRK" to "kp", "BRN" to "bn", "BTN" to "bt",
    "TLS" to "tl", "SYR" to "sy", "YEM" to "ye",
    // Oceania
    "AUS" to "au", "NZL" to "nz", "PNG" to "pg", "FJI" to "fj", "SLB" to "sb",
    "VUT" to "vu", "NCL" to "nc", "KIR" to "ki", "MHL" to "mh", "FSM" to "fm",
    "NRU" to "nr", "PLW" to "pw", "WSM" to "ws", "TON" to "to", "TUV" to "tv"
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
    showLegend: Boolean = false,
    onCompassTapped: () -> Unit = {},
    onLegendClose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var transform by remember { mutableStateOf(TransformState()) }
    val currentTransform by rememberUpdatedState(transform)
    val geometries = remember { CountryGeometryData.getAllGeometries() }
    val gestureState = remember { MapGestureState(geometries, android.graphics.Matrix()) }

    // Animation state for color mode transitions
    var previousColorMode by remember { mutableStateOf(colorMode) }
    var animationTarget by remember { mutableStateOf(0f) }

    LaunchedEffect(colorMode) {
        if (colorMode != previousColorMode) {
            animationTarget = 1f
        }
    }

    val animationProgress by animateFloatAsState(
        targetValue = animationTarget,
        animationSpec = tween(durationMillis = 400),
        finishedListener = {
            previousColorMode = colorMode
            animationTarget = 0f  // Reset for next transition
        },
        label = "colorModeTransition"
    )

    val transitionProgress = if (previousColorMode == colorMode) 1f else animationProgress

    // Don't use remember here - animated values need to trigger Canvas redraw on each frame
    val drawParams = MapDrawParams(
        geometries = geometries,
        selectedCountryId = selectedCountryId,
        colorMode = colorMode,
        previousColorMode = previousColorMode,
        transitionProgress = transitionProgress,
        countries = countries,
        scale = transform.scale
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .background(MapOcean)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val layout = calculateMapLayout(size.width, size.height)
            gestureState.mapLayoutWidth = layout.mapWidth
            gestureState.mapLayoutHeight = layout.mapHeight
            gestureState.canvasWidth = size.width
            gestureState.canvasHeight = size.height

            // Store compass bounds for tap detection
            val roseSize = 44.dp.toPx()
            gestureState.compassCenterX = size.width - roseSize / 2 - 12.dp.toPx()
            gestureState.compassCenterY = size.height - roseSize / 2 - 12.dp.toPx()
            gestureState.compassRadius = roseSize / 2

            // Draw 3 copies for horizontal wrapping (left, center, right)
            listOf(-1f, 0f, 1f).forEach { wrapOffset ->
                drawMapCopy(
                    wrapOffset = wrapOffset,
                    transform = transform,
                    layout = layout,
                    params = drawParams,
                    inverseMatrix = gestureState.inverseMatrix,
                    onMatrixCaptured = { gestureState.matrixValid = it }
                )
            }

            drawCompassRose()
            drawZoomIndicator(transform.scale)
        }

        // Gesture handling overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .mapGestures(
                    gestureState = gestureState,
                    currentTransform = { currentTransform },
                    onTransformChange = { transform = it },
                    onCountryTapped = onCountryTapped,
                    colorMode = colorMode,
                    onCompassTapped = onCompassTapped
                )
        )

        // Map Legend
        AnimatedVisibility(
            visible = showLegend && colorMode != MapColorMode.DEFAULT,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            MapLegend(
                colorMode = colorMode,
                onClose = onLegendClose
            )
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
    colorTransition: ColorTransitionState,
    country: Country?,
    drawContext: CountryDrawContext
) {
    // Helper function to get color for a specific mode
    fun getColorForMode(mode: MapColorMode): Color {
        return when {
            isSelected -> MapHighlight
            mode == MapColorMode.SECURITY_RISK && country != null -> country.safetyLevel.color
            mode == MapColorMode.VISA_REQUIREMENTS && country != null -> getVisaRequirementColor(country.visaRequirement)
            mode == MapColorMode.PASSPORT_VALIDITY -> getPassportValidityColor(country?.passportValidity)
            else -> MapLand
        }
    }

    // Get colors for previous and current modes, then blend based on transition progress
    val previousColor = getColorForMode(colorTransition.previousColorMode)
    val currentColor = getColorForMode(colorTransition.colorMode)
    val fillColor = if (isSelected) MapHighlight else lerp(previousColor, currentColor, colorTransition.transitionProgress)
    val strokeColor = if (isSelected) MapHighlight.copy(alpha = 0.9f) else MapBorder
    // Scale border width inversely with zoom - thinner borders at higher zoom levels
    // Base width is 0.4dp, scales down to 0.1dp at max zoom (25x)
    val baseStrokeWidth = 0.4f.dp.toPx()
    val scaledStrokeWidth = (baseStrokeWidth / drawContext.scale).coerceAtLeast(0.1f.dp.toPx())
    val strokeWidth = if (isSelected) (1.5f.dp.toPx() / drawContext.scale).coerceAtLeast(0.5f.dp.toPx()) else scaledStrokeWidth
    val glowWidth = (3.dp.toPx() / drawContext.scale).coerceAtLeast(1.dp.toPx())
    val glowStyle = Stroke(width = glowWidth)

    // Filter valid polygons and draw each one
    geometry.polygons.filter { it.size >= 3 }.forEach { polygon ->
        val path = Path().apply {
            val firstPoint = latLngToMercator(polygon[0], drawContext.mapWidth, drawContext.mapHeight)
            moveTo(firstPoint.x, firstPoint.y)

            for (i in 1 until polygon.size) {
                val point = latLngToMercator(polygon[i], drawContext.mapWidth, drawContext.mapHeight)
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
            drawPath(path, MapHighlight.copy(alpha = 0.4f), style = glowStyle)
        }
    }
}

/**
 * Draw Mercator grid lines.
 */
private fun DrawScope.drawMercatorGrid(mapWidth: Float, mapHeight: Float, scale: Float) {
    val gridColor = Color(0x25FFFFFF)
    val majorGridColor = Color(0x40FFFFFF)
    // Scale grid line widths inversely with zoom
    val lineWidth = (0.3f.dp.toPx() / scale).coerceAtLeast(0.1f.dp.toPx())
    val majorLineWidth = (0.6f.dp.toPx() / scale).coerceAtLeast(0.15f.dp.toPx())

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
        // Max zoom is 25x, so scale appropriately for 5 bars
        val barCount = ((scale - 1f) / 5f).toInt().coerceIn(1, 5)
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

/**
 * Data class for legend items
 */
private data class LegendItem(
    val color: Color,
    val label: String,
    val testTag: String
)

/**
 * Get legend items for a specific map color mode
 */
private fun getLegendItems(colorMode: MapColorMode): List<LegendItem> {
    return when (colorMode) {
        MapColorMode.DEFAULT -> emptyList()
        MapColorMode.SECURITY_RISK -> listOf(
            LegendItem(Color(0xFF4CAF50), "Low Risk", "legend_item_low_risk"),
            LegendItem(Color(0xFFFFC107), "Medium Risk", "legend_item_medium_risk"),
            LegendItem(Color(0xFF8B0000), "High Risk", "legend_item_high_risk")
        )
        MapColorMode.VISA_REQUIREMENTS -> listOf(
            LegendItem(Color(0xFF4CAF50), "Visa Not Required", "legend_item_visa_not_required"),
            LegendItem(Color(0xFF00BCD4), "eVisa", "legend_item_evisa"),
            LegendItem(Color(0xFFFFC107), "Visa On Arrival", "legend_item_visa_on_arrival"),
            LegendItem(Color(0xFF9E9E9E), "Visa Required", "legend_item_visa_required"),
            LegendItem(Color(0xFF000000), "Restricted", "legend_item_restricted")
        )
        MapColorMode.PASSPORT_VALIDITY -> listOf(
            LegendItem(Color(0xFF9E9E9E), "6 Months", "legend_item_6_months"),
            LegendItem(Color(0xFF00BCD4), "3 Months", "legend_item_3_months"),
            LegendItem(Color(0xFF4CAF50), "Duration of Stay", "legend_item_duration_of_stay"),
            LegendItem(Color(0xFFFFC107), "Other", "legend_item_other")
        )
    }
}

/**
 * Map legend composable showing color meanings for the current map mode
 */
@Composable
private fun MapLegend(
    colorMode: MapColorMode,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("map_legend"),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = Color(0xE6F4E4BC), // Parchment with 90% opacity
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header with close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Map Key",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF4A2F18)
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("legend_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close legend",
                        tint = Color(0xFF4A2F18),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Legend items with cross-dissolve animation when mode changes
            AnimatedContent(
                targetState = colorMode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
                },
                label = "legendContentTransition"
            ) { mode ->
                val legendItems = getLegendItems(mode)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    legendItems.forEach { item ->
                        LegendItemView(
                            item = item,
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual legend item showing a color swatch and label
 */
@Composable
private fun LegendItemView(
    item: LegendItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Color swatch
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(item.color)
        )
        // Label
        Text(
            text = item.label,
            fontSize = 10.sp,
            color = Color(0xFF4A2F18),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
