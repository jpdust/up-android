package com.unstampedpages.app.ui.screens.countryinfo

import com.unstampedpages.app.data.AppConstants
import com.unstampedpages.app.data.CountryList
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
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.annotation.StringRes
import com.unstampedpages.app.R
import com.unstampedpages.app.data.model.Country
import com.unstampedpages.app.data.model.CountryGeometry
import com.unstampedpages.app.data.model.LatLng
import com.unstampedpages.app.data.model.VisaRequirement
import com.unstampedpages.app.data.repository.CountryGeometryData
import com.unstampedpages.app.ui.theme.MapBorder
import com.unstampedpages.app.ui.theme.MapHighlight
import com.unstampedpages.app.ui.theme.MapLand
import com.unstampedpages.app.ui.theme.MapOcean
import androidx.compose.ui.graphics.nativeCanvas
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.atan
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.tan

/**
 * Color modes for the world map
 */
enum class MapColorMode(@StringRes val displayNameResId: Int) {
    DEFAULT(R.string.map_mode_default),
    SECURITY_RISK(R.string.map_mode_security_risk),
    VISA_REQUIREMENTS(R.string.map_mode_visa_requirements),
    PASSPORT_VALIDITY(R.string.map_mode_passport_validity),
    YELLOW_FEVER(R.string.map_mode_yellow_fever),
    MALARIA(R.string.map_mode_malaria)
}

/**
 * Legend state and callbacks for the map
 */
data class MapLegendConfig(
    val showLegend: Boolean = false,
    val onCompassTapped: () -> Unit = {},
    val onLegendClose: () -> Unit = {}
)

/**
 * Colors for passport validity map mode
 */
internal object PassportValidityColors {
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

internal data class VerticalPanBounds(
    val minPanY: Float,
    val maxPanY: Float
)

/**
 * Layout dimensions for the map
 */
internal data class MapLayout(
    val mapWidth: Float,
    val mapHeight: Float,
    val canvasOffsetX: Float,
    val canvasOffsetY: Float,
    val canvasWidth: Float,
    val canvasHeight: Float
)

/**
 * Calculate map layout dimensions based on canvas size
 */
internal fun calculateMapLayout(canvasWidth: Float, canvasHeight: Float): MapLayout {
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
            canvasOffsetY = 0f,
            canvasWidth = canvasWidth,
            canvasHeight = canvasHeight
        )
    } else {
        // Canvas is taller than map - fit to width, align to top
        val mapWidth = canvasWidth
        val mapHeight = canvasWidth / mapAspectRatio
        MapLayout(
            mapWidth = mapWidth,
            mapHeight = mapHeight,
            canvasOffsetX = 0f,
            canvasOffsetY = 0f,
            canvasWidth = canvasWidth,
            canvasHeight = canvasHeight
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
    val newScale = (current.scale * zoom).coerceIn(1f, 200f)
    val verticalPanBounds = calculateVerticalPanBounds(
        scale = newScale,
        mapHeight = mapHeight
    )
    val newPanX = current.panX + pan.x / (mapWidth * newScale)
    val newPanY = current.panY + pan.y / (mapHeight * newScale)

    return TransformState(
        scale = newScale,
        panX = normalizeOffsetX(newPanX),
        panY = newPanY.coerceIn(verticalPanBounds.minPanY, verticalPanBounds.maxPanY)
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
    val verticalPanBounds = calculateVerticalPanBounds(
        scale = current.scale,
        mapHeight = mapHeight
    )
    val newPanX = current.panX + panDelta.x / (mapWidth * current.scale)
    val newPanY = current.panY + panDelta.y / (mapHeight * current.scale)

    return current.copy(
        panX = normalizeOffsetX(newPanX),
        panY = newPanY.coerceIn(verticalPanBounds.minPanY, verticalPanBounds.maxPanY)
    )
}

internal fun calculateVerticalPanBounds(
    scale: Float,
    mapHeight: Float
): VerticalPanBounds {
    if (scale <= 1f || mapHeight <= 0f) {
        return VerticalPanBounds(minPanY = 0f, maxPanY = 0f)
    }

    // Once zoomed in, allow traversing the full projection instead of keeping the map tightly framed.
    // With wrap rendering restored, a wider symmetric clamp is safer than edge-fitting math that can
    // stop the user early on tall screens.
    val maxPanDistance = 1f - 0.5f / scale

    return VerticalPanBounds(
        minPanY = -maxPanDistance,
        maxPanY = maxPanDistance
    )
}

/**
 * Proximity fallback for hit-testing.
 *
 * Returns the geoJson country ID (e.g. "SYC") of the closest candidate, or null.
 * Two kinds of candidates are considered:
 *
 *  1. Entire countries whose overall rendered size is ≤ [SMALL_COUNTRY_THRESHOLD_PX] —
 *     these are drawn as dot markers, so proximity to the country centroid is used.
 *
 *  2. Individual island polygons within larger countries (e.g. Seychelles outer islands:
 *     Aldabra, Farquhar, Amirantes) whose per-polygon rendered size is ≤ the threshold.
 *     The bbox centre of each such polygon is used as a stand-in centroid.
 *
 * The function returns the closest match within [TAP_PROXIMITY_PX] of the tap point,
 * expressed in normalised [0,1] map coordinates.
 */
/**
 * Returns the squared distance from ([normalizedX], [normalizedY]) to the nearest tiny island
 * polygon (rendered width/height below [SMALL_COUNTRY_THRESHOLD_PX]) in [polygonBounds],
 * or [Float.MAX_VALUE] if no such polygon exists.
 *
 * Extracted from [proximityFallbackHitTest] to keep that function's cognitive complexity
 * within the allowed limit.
 */
private fun closestIslandDistSq(
    normalizedX: Float,
    normalizedY: Float,
    polygonBounds: List<PolygonBounds>,
    mapWidth: Float,
    mapHeight: Float,
    currentScale: Float
): Float {
    // Use 2× the tap-proximity radius as the "too small to ray-cast reliably" threshold.
    // A polygon ≤ (2 * TAP_PROXIMITY_PX) wide fits inside one finger-tap diameter, so a
    // tap anywhere on it is guaranteed to be within TAP_PROXIMITY_PX of the bbox centre.
    // This is intentionally larger than SMALL_COUNTRY_THRESHOLD_PX (which drives rendering)
    // so that isolated territories like Easter Island (Chile) are tappable at moderate zoom.
    val islandTapThresholdPx = TAP_PROXIMITY_PX * 2f
    var minDistSq = Float.MAX_VALUE
    for (pb in polygonBounds) {
        val pbRenderedPx = maxOf(
            (pb.maxX - pb.minX) * mapWidth,
            (pb.maxY - pb.minY) * mapHeight
        ) * currentScale
        if (pbRenderedPx > islandTapThresholdPx) continue
        val dx = normalizedX - (pb.minX + pb.maxX) / 2f
        val dy = normalizedY - (pb.minY + pb.maxY) / 2f
        val d = dx * dx + dy * dy
        if (d < minDistSq) minDistSq = d
    }
    return minDistSq
}

internal fun proximityFallbackHitTest(
    normalizedX: Float,
    normalizedY: Float,
    countryBounds: Map<String, CountryBounds>,
    mapWidth: Float,
    mapHeight: Float,
    currentScale: Float
): String? {
    val tapRadiusNorm = TAP_PROXIMITY_PX / (currentScale * mapWidth)
    var closestId: String? = null
    var closestDistSq = tapRadiusNorm * tapRadiusNorm

    for ((countryId, bounds) in countryBounds) {
        val overallRenderedPx = maxOf(
            bounds.widthNorm * mapWidth,
            bounds.heightNorm * mapHeight
        ) * currentScale

        // Dot-marker country: compare distance to its centroid.
        // Archipelago country: find the nearest tiny island polygon bbox centre.
        val candidateDistSq = if (overallRenderedPx <= SMALL_COUNTRY_THRESHOLD_PX) {
            val dx = normalizedX - bounds.centroidNormX
            val dy = normalizedY - bounds.centroidNormY
            dx * dx + dy * dy
        } else {
            closestIslandDistSq(normalizedX, normalizedY, bounds.polygonBounds, mapWidth, mapHeight, currentScale)
        }

        if (candidateDistSq < closestDistSq) {
            closestDistSq = candidateDistSq
            closestId = countryId
        }
    }
    return closestId
}

/**
 * Immutable snapshot of the map state required for a single hit-test dispatch.
 *
 * All six fields are captured on the main thread before the work is sent to
 * [Dispatchers.Default], ensuring no shared mutable state crosses thread boundaries.
 * [matrixValues] is a freshly copied 9-float array so the live
 * [android.graphics.Matrix] is never shared across threads.
 */
private class HitTestSnapshot(
    val matrixValues: FloatArray,
    val mapWidth: Float,
    val mapHeight: Float,
    val geometries: List<CountryGeometry>,
    val countryBounds: Map<String, CountryBounds>,
    val currentScale: Float,
)

/**
 * Pure hit-test function — safe to call on any thread.
 *
 * All mutable state is supplied via [snapshot], which is captured on the main thread
 * before dispatch to [Dispatchers.Default]. Returns the repo country ID (e.g. "fr") and
 * an optional territory display name (e.g. "Cayman Islands") if the tapped feature is a
 * known standalone territory. Both values are null when no country was hit.
 */
private fun hitTestCountry(
    position: Offset,
    snapshot: HitTestSnapshot,
    locale: java.util.Locale = java.util.Locale.getDefault()
): Pair<String?, String?> {
    val pts = floatArrayOf(position.x, position.y)
    android.graphics.Matrix().apply { setValues(snapshot.matrixValues) }.mapPoints(pts)
    val normX = normalizeNormalizedX(pts[0] / snapshot.mapWidth)
    val normY = pts[1] / snapshot.mapHeight

    // Primary: exact polygon ray-cast — captures geoJsonId for territory name lookup.
    val geoJsonId = findCountryAtNormalizedPoint(normX, normY, snapshot.geometries, snapshot.countryBounds)
    if (geoJsonId != null) {
        return geoJsonToRepoId[geoJsonId] to getLocalizedTerritoryName(geoJsonId, locale)
    }

    // Fallback: proximity to small dot-marker countries and tiny island polygons.
    val fallbackId = proximityFallbackHitTest(
        normX, normY, snapshot.countryBounds, snapshot.mapWidth, snapshot.mapHeight, snapshot.currentScale
    )
    return (fallbackId?.let { geoJsonToRepoId[it] }) to (fallbackId?.let { getLocalizedTerritoryName(it, locale) })
}

/**
 * Pure coordinate-space hit test — no Android matrix code, safe to call on any thread
 * and exercisable from JVM unit tests.
 *
 * Given Mercator-normalised coordinates already mapped out of screen space, runs:
 *  1. Ray-cast against every polygon in [geometries] (exact hit).
 *  2. [proximityFallbackHitTest] for small dot-marker countries and tiny island
 *     polygons within larger countries (e.g. Seychelles outer islands).
 *
 * Returns the repo country ID (e.g. "sc") or null.
 */
internal fun hitTestNormalizedPoint(
    normalizedX: Float,
    normalizedY: Float,
    geometries: List<CountryGeometry>,
    countryBounds: Map<String, CountryBounds>,
    mapWidth: Float,
    mapHeight: Float,
    currentScale: Float
): String? {
    // Primary: exact polygon ray-cast
    val geoJsonId = findCountryAtNormalizedPoint(normalizedX, normalizedY, geometries, countryBounds)
    if (geoJsonId != null) return geoJsonToRepoId[geoJsonId]

    // Fallback: proximity to small dot-marker countries and small island polygons
    val fallbackId = proximityFallbackHitTest(normalizedX, normalizedY, countryBounds, mapWidth, mapHeight, currentScale)
    return fallbackId?.let { geoJsonToRepoId[it] }
}

/**
 * Normalize X coordinate to 0-1 range
 */
internal fun normalizeNormalizedX(x: Float): Float {
    var normalized = x
    while (normalized < 0f) normalized += 1f
    while (normalized >= 1f) normalized -= 1f
    return normalized
}

/**
 * State holder for map gesture handling
 */
internal class MapGestureState(
    var geometries: List<CountryGeometry>,
    val inverseMatrix: android.graphics.Matrix,
    var matrixValid: Boolean = false,
    var mapLayoutWidth: Float = 0f,
    var mapLayoutHeight: Float = 0f,
    var canvasWidth: Float = 0f,
    var canvasHeight: Float = 0f,
    var compassCenterX: Float = 0f,
    var compassCenterY: Float = 0f,
    var compassRadius: Float = 0f,
    var countryBounds: Map<String, CountryBounds> = emptyMap(),
    var currentScale: Float = 1f,
    // Stored here so mapGestures can use pointerInput(Unit) and avoid restarting
    // the gesture coroutine on every color mode toggle or locale change.
    var colorMode: MapColorMode = MapColorMode.DEFAULT,
    var onCompassTapped: () -> Unit = {},
    var currentLocale: java.util.Locale = java.util.Locale.getDefault(),
    // Forward rendering matrix (captured from nativeCanvas inside withTransform for wrapOffset=0).
    // Used by drawCountryLabels to position labels in the same coordinate space as the polygons,
    // bypassing any analytic formula that might mis-model the scale pivot or compose order.
    val forwardMatrix: android.graphics.Matrix = android.graphics.Matrix()
)

/**
 * Check if a tap position is within the compass area
 */
private fun MapGestureState.isCompassTap(position: Offset): Boolean {
    if (compassRadius <= 0f) return false
    val dx = position.x - compassCenterX
    val dy = position.y - compassCenterY
    return (dx * dx + dy * dy) <= (compassRadius * compassRadius)
}

private fun MapGestureState.isReadyForHitTest(): Boolean =
    matrixValid && mapLayoutWidth > 0f && mapLayoutHeight > 0f

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
 * Coroutine configuration for async tap hit-testing.
 * Groups the scope and dispatchers so [mapGestures] stays within the parameter limit.
 */
private data class TapConfig(
    val scope: CoroutineScope,
    val computeDispatcher: CoroutineDispatcher = Dispatchers.Default,
    val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
)

/**
 * Modifier extension for map gesture handling (pan, zoom, tap).
 *
 * Keyed on [Unit] so the coroutine is never restarted when the color mode changes.
 * Color mode and compass callback are read from [gestureState] which is updated
 * each recomposition — no restart needed to pick up the new values.
 */
private fun Modifier.mapGestures(
    gestureState: MapGestureState,
    currentTransform: () -> TransformState,
    onTransformChange: (TransformState) -> Unit,
    onCountryTapped: (String?, String?) -> Unit,
    tapConfig: TapConfig
): Modifier = this.pointerInput(Unit) {
    var tapJob: Job? = null
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val downPosition = down.position
        var totalDragDistance = 0f
        var wasDragged = false
        val canvasHeight = size.height.toFloat()
        val layout = calculateMapLayout(size.width.toFloat(), canvasHeight)

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
            if (gestureState.colorMode != MapColorMode.DEFAULT && gestureState.isCompassTap(downPosition)) {
                gestureState.onCompassTapped()
            } else if (gestureState.isReadyForHitTest()) {
                // Snapshot all mutable state on the main thread before dispatching.
                // android.graphics.Matrix is not thread-safe — copy its 9 float values.
                // List<CountryGeometry> and Map<String, CountryBounds> are immutable and
                // safe to pass across threads by reference.
                val matrixValues = FloatArray(9).also { gestureState.inverseMatrix.getValues(it) }
                val mapWidth = gestureState.mapLayoutWidth
                val mapHeight = gestureState.mapLayoutHeight
                val geometries = gestureState.geometries
                val countryBounds = gestureState.countryBounds
                val currentScale = gestureState.currentScale
                val locale = gestureState.currentLocale

                // Cancel any in-flight hit-test from a previous rapid tap, then start
                // a new one on the Default dispatcher so the main thread (and renderer)
                // are never blocked by O(n·m) ray-casting.
                tapJob?.cancel()
                tapJob = tapConfig.scope.launch(tapConfig.computeDispatcher) {
                    val snapshot = HitTestSnapshot(
                        matrixValues = matrixValues,
                        mapWidth = mapWidth,
                        mapHeight = mapHeight,
                        geometries = geometries,
                        countryBounds = countryBounds,
                        currentScale = currentScale,
                    )
                    val (repoId, territoryName) = hitTestCountry(
                        position = downPosition,
                        snapshot = snapshot,
                        locale = locale
                    )
                    withContext(tapConfig.mainDispatcher) {
                        onCountryTapped(repoId, territoryName)
                    }
                }
            } else {
                onCountryTapped(null, null)
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
internal fun getPassportValidityColor(passportValidity: String?): Color {
    return when (passportValidity) {
        AppConstants.PassportValidity.SIX_MONTHS -> PassportValidityColors.SixMonths
        AppConstants.PassportValidity.THREE_MONTHS -> PassportValidityColors.ThreeMonths
        AppConstants.PassportValidity.PLANNED_STAY -> PassportValidityColors.PlannedStay
        null -> PassportValidityColors.Other
        else -> PassportValidityColors.Other
    }
}

/**
 * Pre-compute fill colors for every country geometry for a given mode.
 * Called at most once per unique (mode, countries) pair and cached via remember().
 * Eliminates per-country per-frame branching in the hot draw path.
 */
private fun computeModeColors(
    mode: MapColorMode,
    countries: Map<String, Country>
): Map<String, Color> = buildMap(geoJsonToRepoId.size) {
    geoJsonToRepoId.forEach { (geoJsonId, repoId) ->
        val country = countries[repoId]
        put(geoJsonId, when {
            mode == MapColorMode.SECURITY_RISK && country != null -> country.safetyLevel.color
            mode == MapColorMode.VISA_REQUIREMENTS && country != null -> getVisaRequirementColor(country.visaRequirement)
            mode == MapColorMode.PASSPORT_VALIDITY -> getPassportValidityColor(country?.passportValidity)
            mode == MapColorMode.YELLOW_FEVER && country != null && country.yellowFeverRequired -> Color(0xFFFFEB3B)
            mode == MapColorMode.MALARIA && country != null && country.malariaRisk -> Color(0xFFE53935)
            else -> MapLand
        })
    }
}

/**
 * Parameters for drawing the map content.
 * Color data is excluded — it is passed as pre-computed maps to avoid
 * per-country per-frame recalculation.
 */
private data class MapDrawParams(
    val geometries: List<CountryGeometry>,
    val selectedCountryId: String?,
    val transitionProgress: Float,
    val scale: Float,
    val countryBounds: Map<String, CountryBounds>,
    val currentModeColors: Map<String, Color>,
    val previousModeColors: Map<String, Color>,
    /** geoJsonId (3-letter) → display name, pre-computed once per countries-map change. */
    val countryNames: Map<String, String> = emptyMap(),
    /** Animated alpha for country name labels; 0 = hidden, 1 = fully visible. */
    val labelAlpha: Float = 0f,
    /** Pre-measured text layouts for country name labels. Measured once per name-set change. */
    val labelTextLayouts: Map<String, TextLayoutResult> = emptyMap()
)

private data class CountryRenderStyle(
    val normalStroke: Stroke,
    val selectedStroke: Stroke,
    val glowStyle: Stroke,
    val dotRadius: Float
)

/** Threshold in screen pixels below which a country polygon is replaced by a dot marker */
internal const val SMALL_COUNTRY_THRESHOLD_PX = 8f

/** Minimum dot radius in dp for guaranteed-visible small country markers */
private const val MIN_DOT_RADIUS_DP = 3.5f

/** Tap proximity radius in screen pixels for small country hit detection */
internal const val TAP_PROXIMITY_PX = 20f

/** Extra wrapped world copies to draw just outside the calculated viewport. */
private const val WRAP_COPY_PADDING = 1

// ── Country label constants ───────────────────────────────────────────────────
/** Scale at which country name labels begin cross-dissolving into view. */
internal const val LABEL_SHOW_THRESHOLD = 3f

/**
 * Country label font size in sp, calibrated at [LABEL_SHOW_THRESHOLD].
 * Text is drawn in map space, so glyphs grow proportionally with further zoom.
 */
internal const val LABEL_TEXT_SP = 10f

/**
 * Minimum screen-space width (px) a polygon country must span before its label
 * starts to appear. Countries narrower than this are skipped at the current zoom.
 */
internal const val LABEL_MIN_SCREEN_PX = 20f

/**
 * Screen-space width (px) at which a polygon country label reaches full opacity.
 * Labels fade linearly from 0 at [LABEL_MIN_SCREEN_PX] to 1 here.
 */
internal const val LABEL_FULL_SCREEN_PX = 80f


/**
 * Per-country centroid overrides for labelling.
 *
 * The geometric centroid of some countries falls outside their main land mass — e.g. New Zealand
 * (centroid lands near the Cook Strait between the two main islands) or countries with many
 * scattered islands. Values are normalised Mercator (X, Y) in [0,1].
 *
 * Keys are GeoJSON 3-letter ISO codes (the same key used in [geoJsonToRepoId]).
 */
internal val LABEL_CENTROID_OVERRIDES: Map<String, Pair<Float, Float>> by lazy {
    mapOf(
        // NZ: geometric centroid drifts south due to outlier sub-Antarctic islands;
        // override to sit visually between the North and South Islands.
        "NZL" to (MercatorProjection.longitudeToX(173.0f) to MercatorProjection.latitudeToY(-41.5f)),

        // Kiribati: territory straddles the antimeridian — Gilbert Islands (~174°E),
        // Phoenix Islands (~172°W) and Line Islands (~157°W) sit on opposite sides of 180°.
        // Vertex-averaging the normalised X coordinates yields a midpoint near Africa.
        // Override to the Gilbert Islands (capital Tarawa, ~174°E / 1.5°S), the main
        // populated group, so the label appears in the central Pacific.
        "KIR" to (MercatorProjection.longitudeToX(174.0f) to MercatorProjection.latitudeToY(-1.5f))
    )
}

/**
 * Compute the size-based opacity factor for a country label.
 *
 * Countries whose bounding box is smaller than [SMALL_COUNTRY_THRESHOLD_PX] are rendered
 * as dot markers; their labels should always be fully opaque (sizeAlpha = 1f) so the name
 * appears as soon as the global labelAlpha rises above zero.
 *
 * For larger countries the label fades in linearly between [LABEL_MIN_SCREEN_PX] and
 * [LABEL_FULL_SCREEN_PX]:
 *   - < [LABEL_MIN_SCREEN_PX]  → 0f (country still too small to label)
 *   - in range               → linear interpolation clamped to [0, 1]
 *   - ≥ [LABEL_FULL_SCREEN_PX] → 1f (fully opaque)
 *
 * @param screenMaxDim The larger of the country's screen-space width and height in pixels.
 */
internal fun computeLabelSizeAlpha(screenMaxDim: Float): Float {
    if (screenMaxDim < SMALL_COUNTRY_THRESHOLD_PX) return 1f
    return ((screenMaxDim - LABEL_MIN_SCREEN_PX) / (LABEL_FULL_SCREEN_PX - LABEL_MIN_SCREEN_PX))
        .coerceIn(0f, 1f)
}

/**
 * Build the geoJsonId → display name map used for country labels.
 *
 * Lookup order:
 * 1. [countries] keyed by 2-letter repo id (preferred — shorter common names).
 * 2. [CountryList] fallback for geometry entries that have no repository counterpart.
 *
 * GeoJson IDs with no name in either source are omitted from the result.
 *
 * Extracted as a standalone function so it can be unit-tested independently of the
 * Compose composable that calls it.
 */
internal fun buildCountryNames(
    countries: Map<String, com.unstampedpages.app.data.model.Country>,
    idMap: Map<String, String> = geoJsonToRepoId,
    locale: java.util.Locale = java.util.Locale.getDefault()
): Map<String, String> = buildMap {
    idMap.forEach { (geoId, repoId) ->
        // Territories resolve to their own localized name first.
        // Sovereign countries use the name already on the Country object (localized by the
        // repository), falling back to CountryList for any GeoJSON features not in the repo.
        val name = getLocalizedTerritoryName(geoId, locale)
            ?: countries[repoId]?.name
            ?: CountryList.countries.find { it.code.equals(repoId, ignoreCase = true) }
                ?.getLocalizedName(locale)
        if (name != null) put(geoId, name)
    }
}

/**
 * Pre-computed Mercator Y and X positions for grid lines, in normalised [0,1] coordinates.
 * Computed once at first use — eliminates ln/tan/atan transcendental calls on every draw frame.
 * Each pair is (normalisedPosition, isMajorLine).
 */
internal val GRID_POSITIONS: Pair<List<Pair<Float, Boolean>>, List<Pair<Float, Boolean>>> by lazy {
    val latYs = listOf(-80f, -60f, -40f, -20f, 0f, 20f, 40f, 60f, 80f)
        .map { lat -> MercatorProjection.latitudeToY(lat) to (lat == 0f) }
    val lngXs = listOf(-180f, -150f, -120f, -90f, -60f, -30f, 0f, 30f, 60f, 90f, 120f, 150f, 180f)
        .map { lng -> MercatorProjection.longitudeToX(lng) to (lng == 0f) }
    latYs to lngXs
}

/**
 * LOD hysteresis thresholds.
 *
 * The lo-res (110m) dataset switches to hi-res (10m) only when [scale ≥ LOD_HI_THRESHOLD].
 * Once in hi-res, it reverts to lo-res only when [scale < LOD_LO_THRESHOLD].
 * [LOD_LO_THRESHOLD] is set to 1f (the minimum possible scale) so the revert is effectively
 * unreachable in practice: once the user has zoomed past [LOD_HI_THRESHOLD], hi-res persists
 * for the session. This prevents the 63 countries present only in the 10m dataset from
 * disappearing when the user zooms back out.
 */
private const val LOD_HI_THRESHOLD = 4f
private const val LOD_LO_THRESHOLD = 1f

/**
 * Normalised [0,1] bounding box for a single polygon ring, used as a second-level
 * pre-filter inside [findCountryAtNormalizedPoint] before the O(n) ray-cast.
 */
internal data class PolygonBounds(
    val minX: Float,
    val maxX: Float,
    val minY: Float,
    val maxY: Float
)

/**
 * Pre-computed Mercator bounding box and centroid for a country, in normalised [0,1] coordinates.
 * Stores actual min/max bounds so viewport culling can use exact edge comparisons rather than
 * centroid ± halfWidth (which is incorrect when the centroid isn't at the bbox center).
 *
 * [polygonBounds] holds one entry per polygon in [CountryGeometry.polygons] (same order).
 * Countries such as Russia and the USA have overall bboxes that span nearly the full map
 * width due to outlier islands near the antimeridian.  The per-polygon bounds let
 * [findCountryAtNormalizedPoint] skip individual polygons cheaply before committing to
 * the expensive vertex-by-vertex ray-cast.
 */
internal data class CountryBounds(
    val centroidNormX: Float,
    val centroidNormY: Float,
    /** Centroid of the largest polygon by bounding-box area; used for label placement. */
    val labelCentroidNormX: Float,
    val labelCentroidNormY: Float,
    val minX: Float,
    val maxX: Float,
    val minY: Float,
    val maxY: Float,
    val polygonBounds: List<PolygonBounds> = emptyList()
) {
    val widthNorm: Float get() = maxX - minX
    val heightNorm: Float get() = maxY - minY

    /**
     * Convenience constructor for single-landmass cases (tests, degenerate geometries)
     * where the label centroid equals the overall centroid.
     */
    constructor(
        centroidNormX: Float,
        centroidNormY: Float,
        minX: Float,
        maxX: Float,
        minY: Float,
        maxY: Float,
        polygonBounds: List<PolygonBounds> = emptyList()
    ) : this(
        centroidNormX, centroidNormY,
        centroidNormX, centroidNormY,
        minX, maxX, minY, maxY,
        polygonBounds
    )
}

/**
 * Lazily builds and caches Compose Path objects for country polygons on demand.
 * Paths are expressed in map-pixel space (0..mapWidth × 0..mapHeight) and are
 * compatible with withTransform-based zoom/pan, so the same cache is valid for
 * all three horizontal wrap copies and for any zoom level.
 * The cache is rebuilt only when the map size changes.
 */
private class PathCacheHolder {
    private var builtForWidth: Float = 0f
    private var builtForHeight: Float = 0f
    private val cache = mutableMapOf<String, Path>()

    fun getOrBuild(
        geometry: CountryGeometry,
        mapWidth: Float,
        mapHeight: Float
    ): Path {
        if (builtForWidth != mapWidth || builtForHeight != mapHeight) {
            builtForWidth = mapWidth
            builtForHeight = mapHeight
            cache.clear()
        }

        return cache.getOrPut(geometry.countryId) {
            Path().apply {
                for (polygon in geometry.polygons) {
                    if (polygon.size < 3) continue
                    val first = latLngToMercator(polygon[0], mapWidth, mapHeight)
                    moveTo(first.x, first.y)
                    for (i in 1 until polygon.size) {
                        val pt = latLngToMercator(polygon[i], mapWidth, mapHeight)
                        lineTo(pt.x, pt.y)
                    }
                    close()
                }
            }
        }
    }
}

/**
 * Mutable accumulator for a Mercator bounding box and centroid sum.
 * Centralises the four min/max comparisons so callers stay branch-free.
 */
private class BoundsAccumulator {
    var minX = Float.MAX_VALUE;  var maxX = -Float.MAX_VALUE
    var minY = Float.MAX_VALUE;  var maxY = -Float.MAX_VALUE
    var sumX = 0f;               var sumY = 0f;  var count = 0

    fun addPoint(x: Float, y: Float) {
        if (x < minX) minX = x
        if (x > maxX) maxX = x
        if (y < minY) minY = y
        if (y > maxY) maxY = y
        sumX += x;  sumY += y;  count++
    }

    val isValid: Boolean get() = count > 0
}

/**
 * Compute the Mercator bounding box for a single polygon ring.
 * Returns [PolygonBounds(0,1,0,1)] for empty or degenerate polygons (never culls them).
 */
internal fun computePolygonBounds(polygon: List<LatLng>): PolygonBounds {
    val acc = BoundsAccumulator()
    for (point in polygon) {
        acc.addPoint(MercatorProjection.longitudeToX(point.lng), MercatorProjection.latitudeToY(point.lat))
    }
    return if (acc.isValid) PolygonBounds(acc.minX, acc.maxX, acc.minY, acc.maxY)
    else PolygonBounds(0f, 1f, 0f, 1f)
}

/**
 * Compute [CountryBounds] for a single [CountryGeometry].
 * Iterates polygons once: builds per-polygon bounds and accumulates the global bbox/centroid
 * in the same pass. Returns wide-open defaults when the geometry contains no points.
 */
internal fun computeGeometryBounds(geometry: CountryGeometry): CountryBounds {
    val polygonBoundsList = mutableListOf<PolygonBounds>()
    val global = BoundsAccumulator()
    var largestArea = -1f
    var largestPolygonIndex = 0
    for ((index, polygon) in geometry.polygons.withIndex()) {
        val pb = computePolygonBounds(polygon)
        polygonBoundsList.add(pb)
        val area = (pb.maxX - pb.minX) * (pb.maxY - pb.minY)
        if (area > largestArea) {
            largestArea = area
            largestPolygonIndex = index
        }
        for (point in polygon) {
            global.addPoint(MercatorProjection.longitudeToX(point.lng), MercatorProjection.latitudeToY(point.lat))
        }
    }
    return if (global.isValid) {
        // Compute label centroid from the largest polygon so multi-part countries
        // (e.g. USA, France, NZL) place their label on the main landmass rather
        // than at the vertex-weighted mean of all territories combined.
        val labelAcc = BoundsAccumulator()
        for (point in geometry.polygons[largestPolygonIndex]) {
            labelAcc.addPoint(MercatorProjection.longitudeToX(point.lng), MercatorProjection.latitudeToY(point.lat))
        }
        CountryBounds(
            centroidNormX = global.sumX / global.count,
            centroidNormY = global.sumY / global.count,
            labelCentroidNormX = labelAcc.sumX / labelAcc.count,
            labelCentroidNormY = labelAcc.sumY / labelAcc.count,
            minX = global.minX, maxX = global.maxX,
            minY = global.minY, maxY = global.maxY,
            polygonBounds = polygonBoundsList
        )
    } else {
        // Wide defaults so a degenerate geometry is never incorrectly culled
        CountryBounds(0.5f, 0.5f, 0f, 1f, 0f, 1f)
    }
}

/**
 * Pre-compute bounds for all country geometries once at startup.
 * Produces both an overall per-geometry bbox (fast first-level cull) and a
 * per-polygon bbox list (second-level cull that handles countries like Russia
 * and the USA whose overall bbox spans nearly the full map width due to
 * outlier islands near the antimeridian).
 */
private fun computeAllCountryBounds(geometries: List<CountryGeometry>): Map<String, CountryBounds> =
    geometries.associate { geometry -> geometry.countryId to computeGeometryBounds(geometry) }

internal fun calculateHorizontalWrapOffsets(
    panX: Float,
    scale: Float,
    mapWidth: Float,
    canvasOffsetX: Float,
    canvasWidth: Float
): IntRange {
    if (scale <= 0f || mapWidth <= 0f || canvasWidth <= 0f) return 0..0

    val leftCanvasNorm = -canvasOffsetX / mapWidth
    val rightCanvasNorm = (canvasWidth - canvasOffsetX) / mapWidth
    val minVisibleWrap = ((leftCanvasNorm - 0.5f) / scale) - panX - 0.5f
    val maxVisibleWrap = ((rightCanvasNorm - 0.5f) / scale) - panX + 0.5f

    val padding = if (scale > 1f) WRAP_COPY_PADDING else 0
    val first = floor(minVisibleWrap).toInt() - padding
    val last = ceil(maxVisibleWrap).toInt() + padding

    return minOf(first, 0)..maxOf(last, 0)
}

/**
 * Select the fill colour for one country polygon.
 * Extracted so the colour-selection logic can be unit-tested independently of
 * the [DrawScope] that calls it.
 */
internal fun selectCountryFillColor(
    countryId: String,
    selectedCountryId: String?,
    transitionProgress: Float,
    previousModeColors: Map<String, Color>,
    currentModeColors: Map<String, Color>
): Color = when {
    countryId == selectedCountryId -> MapHighlight
    transitionProgress < 1f -> lerp(
        previousModeColors[countryId] ?: MapLand,
        currentModeColors[countryId] ?: MapLand,
        transitionProgress
    )
    else -> currentModeColors[countryId] ?: MapLand
}

/**
 * Return true if a country's largest rendered dimension is below the dot-marker threshold.
 * Extracted so the size-test logic can be unit-tested independently of [DrawScope].
 */
internal fun isCountrySmall(
    widthNorm: Float,
    heightNorm: Float,
    mapWidth: Float,
    mapHeight: Float,
    scale: Float
): Boolean = maxOf(widthNorm * mapWidth * scale, heightNorm * mapHeight * scale) < SMALL_COUNTRY_THRESHOLD_PX

/**
 * Draw a single copy of the map (used for horizontal wrapping).
 * The caller chooses enough horizontal copies to cover the viewport, including
 * padded neighbors to avoid precision gaps while panning at high zoom.
 */
private fun DrawScope.drawMapCopy(
    wrapOffset: Float,
    transform: TransformState,
    layout: MapLayout,
    params: MapDrawParams,
    pathCacheHolder: PathCacheHolder,
    renderStyle: CountryRenderStyle,
    gestureState: MapGestureState
) {
    val effectivePanX = transform.panX + wrapOffset
    val s = transform.scale

    withTransform({
        translate(layout.canvasOffsetX + 0.5f * layout.mapWidth, layout.canvasOffsetY + 0.5f * layout.mapHeight)
        scale(s, s)
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
        params.geometries.forEach { geometry ->
            val bounds = params.countryBounds[geometry.countryId]
            val isSelected = geometry.countryId == params.selectedCountryId
            val fillColor = selectCountryFillColor(
                geometry.countryId, params.selectedCountryId, params.transitionProgress,
                params.previousModeColors, params.currentModeColors
            )

            val path = pathCacheHolder.getOrBuild(
                geometry = geometry,
                mapWidth = layout.mapWidth,
                mapHeight = layout.mapHeight
            )
            val isSmall = bounds != null &&
                isCountrySmall(bounds.widthNorm, bounds.heightNorm, layout.mapWidth, layout.mapHeight, params.scale)
            val centroid = if (isSmall) {
                Offset(bounds.centroidNormX * layout.mapWidth, bounds.centroidNormY * layout.mapHeight)
            } else {
                Offset.Zero
            }
            drawCountryMercator(
                path = path,
                isSelected = isSelected,
                fillColor = fillColor,
                renderStyle = renderStyle,
                isSmall = isSmall,
                centroid = centroid
            )
        }

        // Capture rendering matrices for tap detection and label positioning (center copy only).
        // forwardMatrix is saved before inversion so drawCountryLabels can use the same
        // coordinate mapping as the polygon paths — avoiding any analytic formula error.
        if (wrapOffset == 0f) {
            val matrix = android.graphics.Matrix()
            @Suppress("DEPRECATION") // getMatrix(Matrix) is the non-deprecated overload
            drawContext.canvas.nativeCanvas.getMatrix(matrix)
            gestureState.forwardMatrix.set(matrix)
            gestureState.matrixValid = matrix.invert(gestureState.inverseMatrix)
        }
    }
}

/**
 * Draw country name labels for one horizontal wrap copy in canvas-local (screen) space.
 *
 * Why forward matrix instead of an analytic formula:
 *   Compose's DrawTransform.scale(s, s) defaults to pivoting around the canvas *center*,
 *   not the origin. Any analytic formula must account for this pivot and for the exact
 *   matrix-composition order (pre- vs post-concat). Getting either detail wrong produces
 *   a systematic offset that is hard to detect without running the app. The forward
 *   rendering matrix captured from nativeCanvas.getMatrix() inside the withTransform
 *   embeds ALL of these details automatically — using it to position labels guarantees
 *   they land in exactly the same coordinate space as the polygon paths.
 *
 * Handling wrap copies:
 *   The forward matrix is captured for wrapOffset = 0. For copy w, the effective
 *   transform is identical to copy 0 except that the path X coordinates are shifted
 *   by w × mapWidth (because effectivePanX = panX + w shifts the T2 translate by
 *   w × mapWidth in path space). Therefore:
 *     screenPos_w = forwardMatrix * (centNormX + w, centNormY) * (mapWidth, mapHeight)
 *
 * Labels are drawn after all map copies so they always render on top of country fills.
 */

/**
 * Return true when a label whose centre maps to ([screenX], [screenY]) and whose text
 * bounding box is [tw] × [th] pixels falls entirely outside the visible canvas.
 * Extracted so the culling predicate can be unit-tested without a [DrawScope].
 */
internal fun isLabelCulled(
    screenX: Float,
    screenY: Float,
    tw: Float,
    th: Float,
    canvasWidth: Float,
    canvasHeight: Float
): Boolean = screenX < -tw || screenX > canvasWidth + tw || screenY < -th || screenY > canvasHeight + th

/**
 * Drawing parameters computed for one visible country label.
 * All values are in screen-pixel space; no framework types are required.
 */
internal data class LabelDrawSpec(
    val topLeft: Offset,
    val shadowOffset: Float,
    val shadowColor: Color,
    val fillColor: Color
)

/**
 * Rendering context that groups the scalar canvas/gesture values needed by
 * [computeVisibleLabelSpecs], keeping its parameter count within Sonar limits.
 */
internal data class LabelRenderContext(
    val labelAlpha: Float,
    val matrixValid: Boolean,
    val scale: Float,
    val mapWidth: Float,
    val mapHeight: Float,
    val canvasWidth: Float,
    val canvasHeight: Float
)

/**
 * Compute [LabelDrawSpec] for every label that should be rendered in one horizontal
 * wrap copy, without referencing [DrawScope], [Density], or Android framework types.
 *
 * [labelTextSizes] maps geoJsonId → (width_px, height_px), derived by the caller from
 * [TextLayoutResult.size] before calling this function.
 * [screenMapper] wraps [android.graphics.Matrix.mapPoints] so the Matrix itself stays
 * in the [DrawScope] caller.
 *
 * Returns an empty map when [LabelRenderContext.labelAlpha] < 0.01 or
 * [LabelRenderContext.matrixValid] is false, matching the early-return logic in
 * [drawCountryLabels].
 */
internal fun computeVisibleLabelSpecs(
    wrapOffset: Float,
    context: LabelRenderContext,
    geometries: List<CountryGeometry>,
    countryBounds: Map<String, CountryBounds>,
    labelTextSizes: Map<String, Pair<Float, Float>>,
    screenMapper: (FloatArray) -> Unit
): Map<String, LabelDrawSpec> {
    val (labelAlpha, matrixValid, scale, mapWidth, mapHeight, canvasWidth, canvasHeight) = context
    if (labelAlpha < 0.01f || !matrixValid) return emptyMap()

    return buildMap {
        for (geometry in geometries) {
            val (tw, th) = labelTextSizes[geometry.countryId] ?: continue
            val bounds   = countryBounds[geometry.countryId]  ?: continue

            val screenMaxDim = maxOf(
                bounds.widthNorm  * mapWidth  * scale,
                bounds.heightNorm * mapHeight * scale
            )
            val finalAlpha = labelAlpha * computeLabelSizeAlpha(screenMaxDim)
            if (finalAlpha < 0.01f) continue

            val (centNormX, centNormY) = LABEL_CENTROID_OVERRIDES[geometry.countryId]
                ?: (bounds.labelCentroidNormX to bounds.labelCentroidNormY)

            // Map the centroid from path space to screen space via the captured forward matrix.
            // Adding wrapOffset to centNormX shifts the point one full map width in X,
            // matching what the withTransform does for each horizontal wrap copy.
            val pts = floatArrayOf(
                (centNormX + wrapOffset) * mapWidth,
                centNormY * mapHeight
            )
            screenMapper(pts)

            if (isLabelCulled(pts[0], pts[1], tw, th, canvasWidth, canvasHeight)) continue

            put(geometry.countryId, LabelDrawSpec(
                topLeft      = Offset(pts[0] - tw / 2f, pts[1] - th / 2f),
                shadowOffset = th * 0.08f,
                shadowColor  = Color.Black.copy(alpha = finalAlpha * 0.67f),
                fillColor    = Color.White.copy(alpha = finalAlpha)
            ))
        }
    }
}

private fun DrawScope.drawCountryLabels(
    wrapOffset: Float,
    layout: MapLayout,
    params: MapDrawParams,
    gestureState: MapGestureState
) {
    val textSizes = params.labelTextLayouts.mapValues { (_, tl) ->
        tl.size.width.toFloat() to tl.size.height.toFloat()
    }
    val ctx = LabelRenderContext(
        labelAlpha  = params.labelAlpha,
        matrixValid = gestureState.matrixValid,
        scale       = params.scale,
        mapWidth    = layout.mapWidth,
        mapHeight   = layout.mapHeight,
        canvasWidth = layout.canvasWidth,
        canvasHeight = layout.canvasHeight
    )
    val specs = computeVisibleLabelSpecs(
        wrapOffset    = wrapOffset,
        context       = ctx,
        geometries    = params.geometries,
        countryBounds = params.countryBounds,
        labelTextSizes = textSizes,
        screenMapper  = { pts -> gestureState.forwardMatrix.mapPoints(pts) }
    )
    for ((countryId, spec) in specs) {
        val textLayout = params.labelTextLayouts[countryId] ?: continue
        drawText(textLayout, color = spec.shadowColor, topLeft = spec.topLeft + Offset(0f,              -spec.shadowOffset))
        drawText(textLayout, color = spec.shadowColor, topLeft = spec.topLeft + Offset(0f,              +spec.shadowOffset))
        drawText(textLayout, color = spec.shadowColor, topLeft = spec.topLeft + Offset(-spec.shadowOffset, 0f))
        drawText(textLayout, color = spec.shadowColor, topLeft = spec.topLeft + Offset(+spec.shadowOffset, 0f))
        drawText(textLayout, color = spec.fillColor,   topLeft = spec.topLeft)
    }
}

/**
 * Mapping from GeoJSON 3-letter ISO codes to repository 2-letter codes
 */
internal val geoJsonToRepoId = mapOf(
    // North America & Caribbean
    "USA" to "us", "CAN" to "ca", "MEX" to "mx", "GTM" to "gt", "CUB" to "cu",
    "HTI" to "ht", "DOM" to "do", "HND" to "hn", "NIC" to "ni", "CRI" to "cr",
    "PAN" to "pa", "JAM" to "jm", "SLV" to "sv", "BLZ" to "bz", "GRL" to "gl",
    "BHS" to "bs", "TTO" to "tt", "ATG" to "ag", "BRB" to "bb", "DMA" to "dm",
    "GRD" to "gd", "KNA" to "kn", "LCA" to "lc", "VCT" to "vc", "ABW" to "aw",
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
    "SOM" to "so", "SOL" to "xso", "BDI" to "bi", "BEN" to "bj", "BFA" to "bf",
    "CAF" to "cf", "COG" to "cg", "DJI" to "dj", "ERI" to "er", "GAB" to "ga",
    "GIN" to "gn", "GMB" to "gm", "GNB" to "gw", "GNQ" to "gq", "LBR" to "lr",
    "LSO" to "ls", "MLI" to "ml", "MRT" to "mr", "MWI" to "mw", "NER" to "ne",
    "SDS" to "ss", "SLE" to "sl", "SWZ" to "sz", "TCD" to "td", "TGO" to "tg",
    "CPV" to "cv", "COM" to "km", "MUS" to "mu", "STP" to "st", "SYC" to "sc",
    // Asia — NE 10m uses PSX for Palestine's ADM0_A3 (ISO_A3=PSE)
    "CHN" to "cn", "JPN" to "jp", "IND" to "in", "THA" to "th", "VNM" to "vn",
    "KOR" to "kr", "IDN" to "id", "PHL" to "ph", "PAK" to "pk", "BGD" to "bd",
    "MYS" to "my", "SGP" to "sg", "MMR" to "mm", "MDV" to "mv", "NPL" to "np", "KHM" to "kh",
    "LAO" to "la", "LKA" to "lk", "TWN" to "tw", "HKG" to "hk", "ARE" to "ae",
    "SAU" to "sa", "ISR" to "il", "IRQ" to "iq", "IRN" to "ir", "AFG" to "af",
    "KAZ" to "kz", "UZB" to "uz", "KGZ" to "kg", "TJK" to "tj", "TKM" to "tm",
    "JOR" to "jo", "LBN" to "lb", "KWT" to "kw", "PSX" to "ps",
    "OMN" to "om", "QAT" to "qa", "BHR" to "bh", "AZE" to "az", "GEO" to "ge",
    "ARM" to "am", "MNG" to "mn", "PRK" to "kp", "BRN" to "bn", "BTN" to "bt",
    "TLS" to "tl", "SYR" to "sy", "YEM" to "ye",
    // Oceania
    "AUS" to "au", "NZL" to "nz", "PNG" to "pg", "FJI" to "fj", "SLB" to "sb",
    "VUT" to "vu", "NCL" to "nc", "KIR" to "ki", "MHL" to "mh", "FSM" to "fm",
    "NRU" to "nr", "PLW" to "pw", "WSM" to "ws", "TON" to "to", "TUV" to "tv",
    // Antarctica
    "ATA" to "aa",
    // Territories & Dependencies — separate GeoJSON features not mapped above.
    // Each resolves to the administering sovereign country so map taps open
    // that country's bottom sheet.
    // UK overseas territories
    "AIA" to "gb", "BMU" to "gb", "CYM" to "gb", "FLK" to "gb", "GGY" to "gb",
    "GIB" to "gb", "IMN" to "gb", "IOT" to "gb", "JEY" to "gb", "MSR" to "gb",
    "PCN" to "gb", "SGS" to "gb", "SHN" to "gb", "TCA" to "gb", "VGB" to "gb",
    // US territories
    "ASM" to "us", "GUM" to "us", "MNP" to "us", "PRI" to "us", "VIR" to "us",
    "UMI" to "us",
    // French territories (NCL already mapped above)
    "ATF" to "fr", "BLM" to "fr", "MAF" to "fr", "PYF" to "fr", "SPM" to "fr",
    "WLF" to "fr",
    // Netherlands territories (ABW already mapped above as Aruba)
    "CUW" to "nl", "SXM" to "nl",
    // Danish territories (GRL already mapped above)
    "FRO" to "dk",
    // Australian territories
    "HMD" to "au", "NFK" to "au",
    // New Zealand territories
    "COK" to "nz", "NIU" to "nz",
    // Finnish territories
    "ALD" to "fi",
    // Chinese territories (HKG already mapped above)
    "MAC" to "cn",
    // Other
    "SAH" to "ma",  // Western Sahara — administered by Morocco (disputed)
    "KOS" to "rs"   // Kosovo — administered by Serbia (disputed)
)

/**
 * Mercator projection constants and calculations
 */
internal object MercatorProjection {
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
internal fun normalizeOffsetX(offset: Float): Float {
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
    onCountryTapped: (countryId: String?, displayName: String?) -> Unit,
    modifier: Modifier = Modifier,
    colorMode: MapColorMode = MapColorMode.DEFAULT,
    countries: Map<String, Country> = emptyMap(),
    legendConfig: MapLegendConfig = MapLegendConfig()
) {
    val tapScope = rememberCoroutineScope()
    var transform by remember { mutableStateOf(TransformState()) }
    val currentTransform by rememberUpdatedState(transform)

    // Re-read both geometry datasets whenever the async load completes.
    val isLoaded by CountryGeometryData.isLoaded
    val geometriesHiRes = remember(isLoaded) { CountryGeometryData.getAllGeometries() }
    val geometriesLoRes = remember(isLoaded) { CountryGeometryData.getLowResGeometries() }
    val boundsHiRes = remember(geometriesHiRes) { computeAllCountryBounds(geometriesHiRes) }
    val boundsLoRes = remember(geometriesLoRes) { computeAllCountryBounds(geometriesLoRes) }

    // Tap hit-testing always uses hi-res geometry for accuracy regardless of zoom.
    val gestureState = remember { MapGestureState(geometriesHiRes, android.graphics.Matrix()) }
    gestureState.geometries = geometriesHiRes
    gestureState.countryBounds = boundsHiRes
    // Pre-compute locale once so both gestureState and countryNames share the same snapshot.
    val currentLocale = LocalConfiguration.current.locales[0]
    // Keep colorMode, compass callback, and locale current so mapGestures (keyed on Unit) never
    // needs to restart its coroutine when any of these change — reads happen at tap time.
    gestureState.colorMode = colorMode
    gestureState.onCompassTapped = legendConfig.onCompassTapped
    gestureState.currentLocale = currentLocale

    // Separate path caches per resolution — both use countryId as key, so they must
    // not share a cache or they'd collide when the same ID maps to different geometry.
    val pathCacheHiRes = remember { PathCacheHolder() }
    val pathCacheLoRes = remember { PathCacheHolder() }

    // Animation state for color mode transitions.
    //
    // Why Animatable instead of animateFloatAsState:
    //   animateFloatAsState required resetting targetValue to 0f inside finishedListener,
    //   which triggered a spurious 400 ms reverse animation after every toggle.
    //
    // Why initialized at 0f (not 1f):
    //   LaunchedEffect runs one frame after colorMode first changes. If the animatable
    //   starts at 1f, that first frame has transitionProgress=1 → useTransition=false →
    //   the new colors render immediately (visible flash) before the cross-dissolve begins.
    //   Starting at 0f means the first frame correctly shows the OLD colors (lerp at 0),
    //   and the LaunchedEffect then animates 0→1 for the smooth dissolve.
    //
    // Why snapTo(0f) at the end of each animation:
    //   Resets the animatable so the NEXT toggle's first frame also starts at 0.
    var previousColorMode by remember { mutableStateOf(colorMode) }
    val transitionAnimatable = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(colorMode) {
        if (colorMode != previousColorMode) {
            transitionAnimatable.snapTo(0f)
            transitionAnimatable.animateTo(1f, tween(durationMillis = 400))
            previousColorMode = colorMode
            transitionAnimatable.snapTo(0f)  // Ready for the next toggle; no animation
        }
    }

    // When no transition is active (previous == current), short-circuit to 1f so the
    // draw path skips the per-country lerp entirely.
    val transitionProgress = if (previousColorMode == colorMode) 1f else transitionAnimatable.value

    // Pre-compute fill colors for all countries — O(240) work, cached until mode or countries change.
    // Eliminates per-country per-frame branching in the hot draw path.
    val currentModeColors = remember(colorMode, countries) { computeModeColors(colorMode, countries) }
    val previousModeColors = remember(previousColorMode, countries) { computeModeColors(previousColorMode, countries) }

    // LOD selection with hysteresis — prevents flickering when scale oscillates near a
    // single cutoff value (common during combined zoom+pan gestures).
    // Switch lo→hi at scale ≥ LOD_HI_THRESHOLD; hi→lo only at scale < LOD_LO_THRESHOLD.
    // Tap hit-testing always uses hi-res regardless of LOD (gestureState is fixed above).
    var isHiRes by remember { mutableStateOf(false) }
    isHiRes = when {
        transform.scale >= LOD_HI_THRESHOLD -> true
        transform.scale <  LOD_LO_THRESHOLD -> false
        else                                -> isHiRes  // hold current LOD in the dead-band
    }
    val drawGeometries  = if (isHiRes) geometriesHiRes else geometriesLoRes
    val drawBounds      = if (isHiRes) boundsHiRes     else boundsLoRes
    val activePathCache = if (isHiRes) pathCacheHiRes  else pathCacheLoRes

    // Pre-compute geoJsonId → display name once per countries-map or locale change.
    val countryNames = remember(countries, currentLocale) { buildCountryNames(countries, locale = currentLocale) }

    // Animate labels in/out as a cross-dissolve keyed on whether scale has crossed the
    // show threshold. animateFloatAsState drives Canvas redraws automatically.
    val labelAlpha by animateFloatAsState(
        targetValue = if (transform.scale >= LABEL_SHOW_THRESHOLD) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "countryLabelAlpha"
    )

    // Measure each country name once per name-set change.
    // TextMeasurer has an internal cache, but pre-measuring here avoids any per-frame
    // measurement inside the draw loop.
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(fontSize = LABEL_TEXT_SP.sp, fontWeight = FontWeight.Bold)
    val labelTextLayouts = remember(countryNames) {
        countryNames.mapValues { (_, name) -> textMeasurer.measure(text = name, style = labelStyle) }
    }

    // Don't use remember here — animated values must trigger Canvas redraw on each frame
    val drawParams = MapDrawParams(
        geometries = drawGeometries,
        selectedCountryId = selectedCountryId,
        transitionProgress = transitionProgress,
        scale = transform.scale,
        countryBounds = drawBounds,
        currentModeColors = currentModeColors,
        previousModeColors = previousModeColors,
        countryNames = countryNames,
        labelAlpha = labelAlpha,
        labelTextLayouts = labelTextLayouts
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

            gestureState.currentScale = transform.scale

            // Store compass bounds for tap detection
            val roseSize = 44.dp.toPx()
            gestureState.compassCenterX = size.width - roseSize / 2 - 12.dp.toPx()
            gestureState.compassCenterY = size.height - roseSize / 2 - 12.dp.toPx()
            gestureState.compassRadius = roseSize / 2

            // Pre-compute stroke objects once per frame — 3 allocations instead of 720+
            val scale = transform.scale
            val baseStrokeWidth = 0.4f.dp.toPx()
            val renderStyle = CountryRenderStyle(
                normalStroke = Stroke(width = (baseStrokeWidth / scale).coerceAtLeast(0.1f.dp.toPx())),
                selectedStroke = Stroke(width = (1.5f.dp.toPx() / scale).coerceAtLeast(0.5f.dp.toPx())),
                glowStyle = Stroke(width = (3.dp.toPx() / scale).coerceAtLeast(1.dp.toPx())),
                dotRadius = (MIN_DOT_RADIUS_DP.dp.toPx() / scale).coerceAtLeast(1f)
            )

            // Cache wrap offsets — used for both map geometry and label drawing.
            val wrapOffsets = calculateHorizontalWrapOffsets(
                panX = transform.panX,
                scale = transform.scale,
                mapWidth = layout.mapWidth,
                canvasOffsetX = layout.canvasOffsetX,
                canvasWidth = layout.canvasWidth
            )

            // Draw enough horizontal copies to cover the viewport at the current zoom.
            wrapOffsets.forEach { wrapOffset ->
                drawMapCopy(
                    wrapOffset = wrapOffset.toFloat(),
                    transform = transform,
                    layout = layout,
                    params = drawParams,
                    pathCacheHolder = activePathCache,
                    renderStyle = renderStyle,
                    gestureState = gestureState
                )
            }

            // Labels drawn after all map copies so they render on top of every country fill.
            // Positions are derived from the forward rendering matrix (see drawCountryLabels).
            wrapOffsets.forEach { wrapOffset ->
                drawCountryLabels(
                    wrapOffset = wrapOffset.toFloat(),
                    layout = layout,
                    params = drawParams,
                    gestureState = gestureState
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
                    tapConfig = TapConfig(scope = tapScope)
                )
        )

        // Map Legend
        AnimatedVisibility(
            visible = legendConfig.showLegend && colorMode != MapColorMode.DEFAULT,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            MapLegend(
                colorMode = colorMode,
                onClose = legendConfig.onLegendClose
            )
        }
    }
}

/**
 * Find which country contains the given normalized point.
 *
 * Two-level bounding-box pre-filter eliminates ray-casting for the vast majority
 * of geometries on every tap:
 *
 *   1. **Per-geometry bbox** — skip the whole country if the overall bbox misses.
 *   2. **Per-polygon bbox** — within a geometry that passes level 1, skip individual
 *      polygons whose own bbox misses.  This is critical for Russia (214 polygons,
 *      overall bbox = 100% map width) and the USA (344 polygons, overall bbox ≈ 100%
 *      map width): without level 2 every tap on earth ray-casts all 558 of their
 *      polygons; with level 2 a tap in Europe skips all of them in ~3 µs.
 */
internal fun findCountryAtNormalizedPoint(
    normalizedX: Float,
    normalizedY: Float,
    geometries: List<CountryGeometry>,
    countryBounds: Map<String, CountryBounds> = emptyMap()
): String? {
    val lng = MercatorProjection.xToLongitude(normalizedX)
    val lat = MercatorProjection.yToLatitude(normalizedY)

    for (geometry in geometries) {
        val bounds = countryBounds[geometry.countryId]

        // Level 1: skip entire geometry if overall bbox misses
        if (bounds != null &&
            (normalizedX < bounds.minX || normalizedX > bounds.maxX ||
             normalizedY < bounds.minY || normalizedY > bounds.maxY)) {
            continue
        }

        val polyBoundsList = bounds?.polygonBounds
        for ((idx, polygon) in geometry.polygons.withIndex()) {
            // Level 2: skip individual polygon if its own bbox misses
            val pb = polyBoundsList?.getOrNull(idx)
            if (pb != null &&
                (normalizedX < pb.minX || normalizedX > pb.maxX ||
                 normalizedY < pb.minY || normalizedY > pb.maxY)) {
                continue
            }
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
 * Draw a country using pre-built cached paths.
 * Fill color and stroke objects are pre-computed by the caller — no per-country allocations.
 * [isSmall] and [centroid] are pre-computed by the caller from bounds, mapWidth/Height, and scale.
 */
private fun DrawScope.drawCountryMercator(
    path: Path,
    isSelected: Boolean,
    fillColor: Color,
    renderStyle: CountryRenderStyle,
    isSmall: Boolean,
    centroid: Offset
) {
    val strokeColor = if (isSelected) MapHighlight.copy(alpha = 0.9f) else MapBorder

    if (isSmall) {
        // Draw a guaranteed-visible dot marker at the centroid
        if (isSelected) {
            drawCircle(MapHighlight.copy(alpha = 0.35f), renderStyle.dotRadius * 4f, centroid)
        }
        drawCircle(fillColor, renderStyle.dotRadius, centroid)
        drawCircle(strokeColor, renderStyle.dotRadius, centroid, style = renderStyle.normalStroke)
    } else {
        drawPath(path, fillColor, style = Fill)
        drawPath(path, strokeColor, style = if (isSelected) renderStyle.selectedStroke else renderStyle.normalStroke)
        if (isSelected) {
            drawPath(path, MapHighlight.copy(alpha = 0.4f), style = renderStyle.glowStyle)
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

    val (latYs, lngXs) = GRID_POSITIONS

    // Latitude lines (every 20 degrees) — positions pre-computed, no transcendental calls per frame
    latYs.forEach { (normY, isMajor) ->
        val y = normY * mapHeight
        drawLine(
            color = if (isMajor) majorGridColor else gridColor,
            start = Offset(0f, y),
            end = Offset(mapWidth, y),
            strokeWidth = if (isMajor) majorLineWidth else lineWidth
        )
    }

    // Longitude lines (every 30 degrees) — positions pre-computed
    lngXs.forEach { (normX, isMajor) ->
        val x = normX * mapWidth
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
 * Compute how many zoom-indicator bars to show for the given [scale].
 * Maps the range (1, 200] linearly onto [1, 5].
 * Extracted so the bar-count formula can be unit-tested without a [DrawScope].
 */
internal fun computeZoomBarCount(scale: Float): Int =
    ((scale - 1f) / (200f - 1f) * 5f).toInt().coerceIn(1, 5)

/**
 * Screen-space geometry for one zoom-indicator bar.
 */
internal data class ZoomBarSpec(
    val topLeftX: Float,
    val topLeftY: Float,
    val width: Float,
    val height: Float
)

/**
 * Compute the list of [ZoomBarSpec]s to draw for the zoom indicator, given
 * already-resolved pixel sizes from the caller's [DrawScope]/[Density] context.
 *
 * Returns null when [scale] ≤ 1.1 (the indicator is hidden at 1× zoom).
 * Extracted so bar geometry can be verified in JVM unit tests without a [DrawScope].
 */
internal fun computeZoomBarSpecs(
    scale: Float,
    canvasHeight: Float,
    xPx: Float,
    yOffsetPx: Float,
    barWidthPx: Float,
    barSpacingPx: Float,
    maxBarHeightPx: Float
): List<ZoomBarSpec>? {
    if (scale <= 1.1f) return null
    val barCount = computeZoomBarCount(scale)
    val baseY    = canvasHeight - yOffsetPx
    return List(barCount) { i ->
        val barHeight = maxBarHeightPx * (i + 1) / 5f
        ZoomBarSpec(
            topLeftX = xPx + i * (barWidthPx + barSpacingPx),
            topLeftY = baseY - barHeight,
            width    = barWidthPx,
            height   = barHeight
        )
    }
}

/**
 * Draw zoom level indicator.
 */
private fun DrawScope.drawZoomIndicator(scale: Float) {
    val specs = computeZoomBarSpecs(
        scale, size.height,
        xPx          = 12.dp.toPx(),
        yOffsetPx    = 12.dp.toPx(),
        barWidthPx   = 4.dp.toPx(),
        barSpacingPx = 3.dp.toPx(),
        maxBarHeightPx = 16.dp.toPx()
    ) ?: return
    val cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
    for (spec in specs) {
        drawRoundRect(
            color        = Color(0xCCFFFFFF),
            topLeft      = Offset(spec.topLeftX, spec.topLeftY),
            size         = Size(spec.width, spec.height),
            cornerRadius = cornerRadius
        )
    }
}

/**
 * Data class for legend items
 */
internal data class LegendItem(
    val color: Color,
    @androidx.annotation.StringRes val labelResId: Int,
    val testTag: String
)

/**
 * Get legend items for a specific map color mode
 */
internal fun getLegendItems(colorMode: MapColorMode): List<LegendItem> {
    return when (colorMode) {
        MapColorMode.DEFAULT -> emptyList()
        MapColorMode.SECURITY_RISK -> listOf(
            LegendItem(Color(0xFF4CAF50), R.string.legend_low_risk, "legend_item_low_risk"),
            LegendItem(Color(0xFFFFC107), R.string.legend_medium_risk, "legend_item_medium_risk"),
            LegendItem(Color(0xFFFF9800), R.string.legend_high_risk, "legend_item_high_risk"),
            LegendItem(Color(0xFFE53935), R.string.legend_extreme_risk, "legend_item_extreme_risk")
        )
        MapColorMode.VISA_REQUIREMENTS -> listOf(
            LegendItem(Color(0xFF4CAF50), R.string.legend_visa_not_required, "legend_item_visa_not_required"),
            LegendItem(Color(0xFF00BCD4), R.string.legend_evisa, "legend_item_evisa"),
            LegendItem(Color(0xFFFFC107), R.string.legend_visa_on_arrival, "legend_item_visa_on_arrival"),
            LegendItem(Color(0xFF9E9E9E), R.string.legend_visa_required, "legend_item_visa_required"),
            LegendItem(Color(0xFF000000), R.string.legend_restricted, "legend_item_restricted")
        )
        MapColorMode.PASSPORT_VALIDITY -> listOf(
            LegendItem(Color(0xFF9E9E9E), R.string.legend_six_months, "legend_item_6_months"),
            LegendItem(Color(0xFF00BCD4), R.string.legend_three_months, "legend_item_3_months"),
            LegendItem(Color(0xFF4CAF50), R.string.legend_duration_of_stay, "legend_item_duration_of_stay"),
            LegendItem(Color(0xFFFFC107), R.string.legend_other, "legend_item_other")
        )
        MapColorMode.YELLOW_FEVER -> listOf(
            LegendItem(Color(0xFFFFEB3B), R.string.legend_yellow_fever_required, "legend_item_yellow_fever")
        )
        MapColorMode.MALARIA -> listOf(
            LegendItem(Color(0xFFE53935), R.string.legend_malaria_risk, "legend_item_malaria")
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
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
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
                    text = stringResource(R.string.country_map_key),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("legend_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cd_close_legend),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
                if (mode == MapColorMode.SECURITY_RISK) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        legendItems.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                rowItems.forEach { item ->
                                    LegendItemView(
                                        item = item,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag(item.testTag)
                                    )
                                }
                            }
                        }
                    }
                } else {
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
            text = stringResource(item.labelResId),
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
