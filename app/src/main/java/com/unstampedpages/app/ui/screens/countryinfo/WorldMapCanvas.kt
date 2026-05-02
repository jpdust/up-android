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
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
private data class MapLayout(
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
 * Pure hit-test function — safe to call on any thread.
 *
 * All mutable state is passed as value parameters so the caller can snapshot them on the
 * main thread and then dispatch this function to [Dispatchers.Default] without races.
 * The matrix is supplied as a pre-read 9-float array and reconstructed here to avoid
 * sharing the live [android.graphics.Matrix] object across threads.
 *
 * Returns the repo country ID (e.g. "fr") or null if no country was hit.
 */
private fun hitTestCountry(
    position: Offset,
    matrixValues: FloatArray,
    mapWidth: Float,
    mapHeight: Float,
    geometries: List<CountryGeometry>,
    countryBounds: Map<String, CountryBounds>,
    currentScale: Float
): String? {
    val inverseMatrix = android.graphics.Matrix().apply { setValues(matrixValues) }
    val screenPts = floatArrayOf(position.x, position.y)
    inverseMatrix.mapPoints(screenPts)
    val normalizedX = normalizeNormalizedX(screenPts[0] / mapWidth)
    val normalizedY = screenPts[1] / mapHeight

    // Primary: exact polygon ray-cast
    val geoJsonId = findCountryAtNormalizedPoint(normalizedX, normalizedY, geometries, countryBounds)
    if (geoJsonId != null) return geoJsonToRepoId[geoJsonId]

    // Fallback: proximity to small dot-marker countries
    val tapRadiusNorm = TAP_PROXIMITY_PX / (currentScale * mapWidth)
    var closestId: String? = null
    var closestDistSq = tapRadiusNorm * tapRadiusNorm

    for ((countryId, bounds) in countryBounds) {
        // Only consider countries rendered as dot markers at this zoom level
        val renderedPx = maxOf(
            bounds.widthNorm * mapWidth,
            bounds.heightNorm * mapHeight
        ) * currentScale
        if (renderedPx > SMALL_COUNTRY_THRESHOLD_PX) continue

        val dx = normalizedX - bounds.centroidNormX
        val dy = normalizedY - bounds.centroidNormY
        val distSq = dx * dx + dy * dy
        if (distSq < closestDistSq) {
            closestDistSq = distSq
            closestId = countryId
        }
    }
    return closestId?.let { geoJsonToRepoId[it] }
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
private class MapGestureState(
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
    var currentScale: Float = 1f
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
 * Modifier extension for map gesture handling (pan, zoom, tap)
 */
private fun Modifier.mapGestures(
    gestureState: MapGestureState,
    currentTransform: () -> TransformState,
    onTransformChange: (TransformState) -> Unit,
    onCountryTapped: (String?) -> Unit,
    colorMode: MapColorMode,
    onCompassTapped: () -> Unit,
    tapScope: CoroutineScope
): Modifier = this.pointerInput(colorMode) {
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
            if (colorMode != MapColorMode.DEFAULT && gestureState.isCompassTap(downPosition)) {
                onCompassTapped()
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

                // Cancel any in-flight hit-test from a previous rapid tap, then start
                // a new one on the Default dispatcher so the main thread (and renderer)
                // are never blocked by O(n·m) ray-casting.
                tapJob?.cancel()
                tapJob = tapScope.launch(Dispatchers.Default) {
                    val result = hitTestCountry(
                        position = downPosition,
                        matrixValues = matrixValues,
                        mapWidth = mapWidth,
                        mapHeight = mapHeight,
                        geometries = geometries,
                        countryBounds = countryBounds,
                        currentScale = currentScale
                    )
                    withContext(Dispatchers.Main) {
                        onCountryTapped(result)
                    }
                }
            } else {
                onCountryTapped(null)
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
        "6 months" -> PassportValidityColors.SixMonths
        "3 months" -> PassportValidityColors.ThreeMonths
        "Planned length of stay" -> PassportValidityColors.PlannedStay
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
    val previousModeColors: Map<String, Color>
)

private data class CountryRenderStyle(
    val normalStroke: Stroke,
    val selectedStroke: Stroke,
    val glowStyle: Stroke,
    val dotRadius: Float
)

/** Threshold in screen pixels below which a country polygon is replaced by a dot marker */
private const val SMALL_COUNTRY_THRESHOLD_PX = 8f

/** Minimum dot radius in dp for guaranteed-visible small country markers */
private const val MIN_DOT_RADIUS_DP = 3.5f

/** Tap proximity radius in screen pixels for small country hit detection */
private const val TAP_PROXIMITY_PX = 20f

/** Extra wrapped world copies to draw just outside the calculated viewport. */
private const val WRAP_COPY_PADDING = 1

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
    val minX: Float,
    val maxX: Float,
    val minY: Float,
    val maxY: Float,
    val polygonBounds: List<PolygonBounds> = emptyList()
) {
    val widthNorm: Float get() = maxX - minX
    val heightNorm: Float get() = maxY - minY
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
    for (polygon in geometry.polygons) {
        polygonBoundsList.add(computePolygonBounds(polygon))
        for (point in polygon) {
            global.addPoint(MercatorProjection.longitudeToX(point.lng), MercatorProjection.latitudeToY(point.lat))
        }
    }
    return if (global.isValid) {
        CountryBounds(
            centroidNormX = global.sumX / global.count,
            centroidNormY = global.sumY / global.count,
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
    val useTransition = params.transitionProgress < 1f
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
            val fillColor = when {
                isSelected -> MapHighlight
                useTransition -> {
                    val prev = params.previousModeColors[geometry.countryId] ?: MapLand
                    val curr = params.currentModeColors[geometry.countryId] ?: MapLand
                    lerp(prev, curr, params.transitionProgress)
                }
                else -> params.currentModeColors[geometry.countryId] ?: MapLand
            }

            val path = pathCacheHolder.getOrBuild(
                geometry = geometry,
                mapWidth = layout.mapWidth,
                mapHeight = layout.mapHeight
            )
            val isSmall = bounds != null && run {
                val w = bounds.widthNorm * layout.mapWidth * params.scale
                val h = bounds.heightNorm * layout.mapHeight * params.scale
                maxOf(w, h) < SMALL_COUNTRY_THRESHOLD_PX
            }
            val centroid = if (isSmall && bounds != null) {
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

        // Capture inverse matrix for tap detection (center copy only)
        if (wrapOffset == 0f) {
            val matrix = android.graphics.Matrix()
            @Suppress("DEPRECATION") // getMatrix(Matrix) is the non-deprecated overload
            drawContext.canvas.nativeCanvas.getMatrix(matrix)
            gestureState.matrixValid = matrix.invert(gestureState.inverseMatrix)
        }
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
    "ATA" to "aa"
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
    onCountryTapped: (countryId: String?) -> Unit,
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

    // Separate path caches per resolution — both use countryId as key, so they must
    // not share a cache or they'd collide when the same ID maps to different geometry.
    val pathCacheHiRes = remember { PathCacheHolder() }
    val pathCacheLoRes = remember { PathCacheHolder() }

    // Animation state for color mode transitions
    var previousColorMode by remember { mutableStateOf(colorMode) }
    var animationTarget by remember { mutableFloatStateOf(0f) }

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

    // Don't use remember here — animated values must trigger Canvas redraw on each frame
    val drawParams = MapDrawParams(
        geometries = drawGeometries,
        selectedCountryId = selectedCountryId,
        transitionProgress = transitionProgress,
        scale = transform.scale,
        countryBounds = drawBounds,
        currentModeColors = currentModeColors,
        previousModeColors = previousModeColors
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

            // Draw enough horizontal copies to cover the viewport at the current zoom.
            calculateHorizontalWrapOffsets(
                panX = transform.panX,
                scale = transform.scale,
                mapWidth = layout.mapWidth,
                canvasOffsetX = layout.canvasOffsetX,
                canvasWidth = layout.canvasWidth
            ).forEach { wrapOffset ->
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
                    onCompassTapped = legendConfig.onCompassTapped,
                    tapScope = tapScope
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
        // Max zoom is 200x, so scale appropriately for 5 bars
        val barCount = ((scale - 1f) / (200f - 1f) * 5f).toInt().coerceIn(1, 5)
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
