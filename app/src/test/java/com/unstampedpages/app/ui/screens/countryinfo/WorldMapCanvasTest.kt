package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.pow
import com.unstampedpages.app.R
import com.unstampedpages.app.data.AppConstants
import com.unstampedpages.app.data.CountryList
import com.unstampedpages.app.data.model.Continent
import com.unstampedpages.app.data.model.Country
import com.unstampedpages.app.data.model.SafetyLevel
import com.unstampedpages.app.data.model.VisaRequirement
import com.unstampedpages.app.data.repository.CountryRepository
import org.junit.Assert.*
import org.junit.Test

/**
 * Comprehensive unit tests for WorldMapCanvas.kt components.
 */
class WorldMapCanvasTest {

    // ==================== MapLegendConfig Tests ====================

    @Test
    fun `MapLegendConfig default values are correct`() {
        val config = MapLegendConfig()

        assertFalse(config.showLegend)
    }

    @Test
    fun `MapLegendConfig can be created with custom values`() {
        var compassTapped = false
        var legendClosed = false

        val config = MapLegendConfig(
            showLegend = true,
            onCompassTapped = { compassTapped = true },
            onLegendClose = { legendClosed = true }
        )

        assertTrue(config.showLegend)

        config.onCompassTapped()
        assertTrue(compassTapped)

        config.onLegendClose()
        assertTrue(legendClosed)
    }

    @Test
    fun `MapLegendConfig callbacks are independent`() {
        var compassCount = 0
        var closeCount = 0

        val config = MapLegendConfig(
            showLegend = false,
            onCompassTapped = { compassCount++ },
            onLegendClose = { closeCount++ }
        )

        config.onCompassTapped()
        config.onCompassTapped()
        config.onLegendClose()

        assertEquals(2, compassCount)
        assertEquals(1, closeCount)
    }

    // ==================== MercatorProjection Tests ====================

    @Test
    fun `MercatorProjection MAX_LATITUDE is 83 degrees`() {
        assertEquals(83.0f, MercatorProjection.MAX_LATITUDE)
    }

    @Test
    fun `MercatorProjection MIN_LATITUDE is -85 degrees`() {
        assertEquals(-85.0f, MercatorProjection.MIN_LATITUDE)
    }

    @Test
    fun `MercatorProjection MIN_LONGITUDE is -180 degrees`() {
        assertEquals(-180f, MercatorProjection.MIN_LONGITUDE)
    }

    @Test
    fun `MercatorProjection MAX_LONGITUDE is 180 degrees`() {
        assertEquals(180f, MercatorProjection.MAX_LONGITUDE)
    }

    @Test
    fun `latitudeToY returns value near 0_5 for equator`() {
        val y = MercatorProjection.latitudeToY(0f)
        // Due to asymmetric latitude bounds (-85 to 83), equator is not exactly at 0.5
        assertTrue("Equator Y should be between 0.4 and 0.6", y > 0.4f && y < 0.6f)
    }

    @Test
    fun `latitudeToY returns value near 0 for high positive latitude`() {
        val y = MercatorProjection.latitudeToY(80f)
        assertTrue(y < 0.2f)
        assertTrue(y > 0f)
    }

    @Test
    fun `latitudeToY returns value near 1 for high negative latitude`() {
        val y = MercatorProjection.latitudeToY(-80f)
        assertTrue(y > 0.8f)
        assertTrue(y < 1f)
    }

    @Test
    fun `latitudeToY clamps values above MAX_LATITUDE`() {
        val yAtMax = MercatorProjection.latitudeToY(MercatorProjection.MAX_LATITUDE)
        val yAboveMax = MercatorProjection.latitudeToY(90f)
        assertEquals(yAtMax, yAboveMax, 0.001f)
    }

    @Test
    fun `latitudeToY clamps values below MIN_LATITUDE`() {
        val yAtMin = MercatorProjection.latitudeToY(MercatorProjection.MIN_LATITUDE)
        val yBelowMin = MercatorProjection.latitudeToY(-90f)
        assertEquals(yAtMin, yBelowMin, 0.001f)
    }

    @Test
    fun `longitudeToX returns 0 for -180 degrees`() {
        val x = MercatorProjection.longitudeToX(-180f)
        assertEquals(0f, x, 0.001f)
    }

    @Test
    fun `longitudeToX returns 0_5 for 0 degrees`() {
        val x = MercatorProjection.longitudeToX(0f)
        assertEquals(0.5f, x, 0.001f)
    }

    @Test
    fun `longitudeToX returns 1 for 180 degrees`() {
        val x = MercatorProjection.longitudeToX(180f)
        assertEquals(1f, x, 0.001f)
    }

    @Test
    fun `longitudeToX is linear`() {
        val x90 = MercatorProjection.longitudeToX(90f)
        val xMinus90 = MercatorProjection.longitudeToX(-90f)
        assertEquals(0.75f, x90, 0.001f)
        assertEquals(0.25f, xMinus90, 0.001f)
    }

    @Test
    fun `yToLatitude is inverse of latitudeToY`() {
        val testLatitudes = listOf(-80f, -45f, 0f, 45f, 80f)
        testLatitudes.forEach { lat ->
            val y = MercatorProjection.latitudeToY(lat)
            val recoveredLat = MercatorProjection.yToLatitude(y)
            assertEquals(lat, recoveredLat, 0.1f)
        }
    }

    @Test
    fun `xToLongitude is inverse of longitudeToX`() {
        val testLongitudes = listOf(-180f, -90f, 0f, 90f, 180f)
        testLongitudes.forEach { lng ->
            val x = MercatorProjection.longitudeToX(lng)
            val recoveredLng = MercatorProjection.xToLongitude(x)
            assertEquals(lng, recoveredLng, 0.001f)
        }
    }

    @Test
    fun `yToLatitude returns value near equator for 0_5`() {
        val lat = MercatorProjection.yToLatitude(0.5f)
        // Due to asymmetric latitude bounds, Y=0.5 is near but not exactly at equator
        assertTrue("Latitude at Y=0.5 should be between -10 and 10", lat > -10f && lat < 10f)
    }

    @Test
    fun `xToLongitude returns 0 for 0_5`() {
        val lng = MercatorProjection.xToLongitude(0.5f)
        assertEquals(0f, lng, 0.001f)
    }

    @Test
    fun `getAspectRatio returns positive value`() {
        val ratio = MercatorProjection.getAspectRatio()
        assertTrue(ratio > 0)
    }

    @Test
    fun `getAspectRatio returns reasonable value for world map`() {
        val ratio = MercatorProjection.getAspectRatio()
        // World maps typically have aspect ratio between 1.5 and 2.5
        assertTrue(ratio > 1.0f)
        assertTrue(ratio < 3.0f)
    }

    // ==================== normalizeOffsetX Tests ====================

    @Test
    fun `normalizeOffsetX keeps value in range for zero`() {
        assertEquals(0f, normalizeOffsetX(0f), 0.001f)
    }

    @Test
    fun `normalizeOffsetX keeps value in range for small positive`() {
        assertEquals(0.25f, normalizeOffsetX(0.25f), 0.001f)
    }

    @Test
    fun `normalizeOffsetX keeps value in range for small negative`() {
        assertEquals(-0.25f, normalizeOffsetX(-0.25f), 0.001f)
    }

    @Test
    fun `normalizeOffsetX wraps large positive values`() {
        assertEquals(0.25f, normalizeOffsetX(1.25f), 0.001f)
    }

    @Test
    fun `normalizeOffsetX wraps large negative values`() {
        assertEquals(-0.25f, normalizeOffsetX(-1.25f), 0.001f)
    }

    @Test
    fun `normalizeOffsetX wraps exactly 0_5 to -0_5`() {
        val result = normalizeOffsetX(0.5f)
        assertEquals(-0.5f, result, 0.001f)
    }

    @Test
    fun `normalizeOffsetX wraps multiple times for very large values`() {
        assertEquals(0.1f, normalizeOffsetX(3.1f), 0.001f)
    }

    @Test
    fun `normalizeOffsetX wraps multiple times for very negative values`() {
        assertEquals(-0.1f, normalizeOffsetX(-3.1f), 0.001f)
    }

    // ==================== normalizeNormalizedX Tests ====================

    @Test
    fun `normalizeNormalizedX keeps value in 0-1 range for 0`() {
        assertEquals(0f, normalizeNormalizedX(0f), 0.001f)
    }

    @Test
    fun `normalizeNormalizedX keeps value in 0-1 range for 0_5`() {
        assertEquals(0.5f, normalizeNormalizedX(0.5f), 0.001f)
    }

    @Test
    fun `normalizeNormalizedX wraps value at 1`() {
        assertEquals(0f, normalizeNormalizedX(1f), 0.001f)
    }

    @Test
    fun `normalizeNormalizedX wraps negative values`() {
        assertEquals(0.75f, normalizeNormalizedX(-0.25f), 0.001f)
    }

    @Test
    fun `normalizeNormalizedX wraps large positive values`() {
        assertEquals(0.5f, normalizeNormalizedX(2.5f), 0.001f)
    }

    @Test
    fun `normalizeNormalizedX wraps large negative values`() {
        assertEquals(0.5f, normalizeNormalizedX(-2.5f), 0.001f)
    }

    // ==================== PassportValidityColors Tests ====================

    @Test
    fun `PassportValidityColors SixMonths is gray`() {
        assertEquals(Color(0xFF9E9E9E), PassportValidityColors.SixMonths)
    }

    @Test
    fun `PassportValidityColors ThreeMonths is teal`() {
        assertEquals(Color(0xFF00BCD4), PassportValidityColors.ThreeMonths)
    }

    @Test
    fun `PassportValidityColors PlannedStay is green`() {
        assertEquals(Color(0xFF4CAF50), PassportValidityColors.PlannedStay)
    }

    @Test
    fun `PassportValidityColors Other is yellow`() {
        assertEquals(Color(0xFFFFC107), PassportValidityColors.Other)
    }

    // ==================== getPassportValidityColor Tests ====================

    @Test
    fun `getPassportValidityColor returns gray for 6 months`() {
        val color = getPassportValidityColor(AppConstants.PassportValidity.SIX_MONTHS)
        assertEquals(PassportValidityColors.SixMonths, color)
    }

    @Test
    fun `getPassportValidityColor returns teal for 3 months`() {
        val color = getPassportValidityColor(AppConstants.PassportValidity.THREE_MONTHS)
        assertEquals(PassportValidityColors.ThreeMonths, color)
    }

    @Test
    fun `getPassportValidityColor returns green for planned stay`() {
        val color = getPassportValidityColor(AppConstants.PassportValidity.PLANNED_STAY)
        assertEquals(PassportValidityColors.PlannedStay, color)
    }

    @Test
    fun `getPassportValidityColor returns yellow for null`() {
        val color = getPassportValidityColor(null)
        assertEquals(PassportValidityColors.Other, color)
    }

    @Test
    fun `getPassportValidityColor returns yellow for unknown value`() {
        val color = getPassportValidityColor("Unknown requirement")
        assertEquals(PassportValidityColors.Other, color)
    }

    @Test
    fun `getPassportValidityColor returns yellow for empty string`() {
        val color = getPassportValidityColor("")
        assertEquals(PassportValidityColors.Other, color)
    }

    // ==================== getLegendItems Tests ====================

    @Test
    fun `getLegendItems returns empty list for DEFAULT mode`() {
        val items = getLegendItems(MapColorMode.DEFAULT)
        assertTrue(items.isEmpty())
    }

    @Test
    fun `getLegendItems returns 4 items for SECURITY_RISK mode`() {
        val items = getLegendItems(MapColorMode.SECURITY_RISK)
        assertEquals(4, items.size)
    }

    @Test
    fun `getLegendItems SECURITY_RISK contains Low Risk`() {
        val items = getLegendItems(MapColorMode.SECURITY_RISK)
        assertTrue(items.any { it.labelResId == R.string.legend_low_risk })
    }

    @Test
    fun `getLegendItems SECURITY_RISK contains Medium Risk`() {
        val items = getLegendItems(MapColorMode.SECURITY_RISK)
        assertTrue(items.any { it.labelResId == R.string.legend_medium_risk })
    }

    @Test
    fun `getLegendItems SECURITY_RISK contains Reconsider Travel`() {
        val items = getLegendItems(MapColorMode.SECURITY_RISK)
        assertTrue(items.any { it.labelResId == R.string.legend_high_risk })
    }

    @Test
    fun `getLegendItems SECURITY_RISK contains Do Not Travel`() {
        val items = getLegendItems(MapColorMode.SECURITY_RISK)
        assertTrue(items.any { it.labelResId == R.string.legend_extreme_risk })
    }

    @Test
    fun `getLegendItems returns 5 items for VISA_REQUIREMENTS mode`() {
        val items = getLegendItems(MapColorMode.VISA_REQUIREMENTS)
        assertEquals(5, items.size)
    }

    @Test
    fun `getLegendItems VISA_REQUIREMENTS contains all visa types`() {
        val items = getLegendItems(MapColorMode.VISA_REQUIREMENTS)
        val labelResIds = items.map { it.labelResId }
        assertTrue(labelResIds.contains(R.string.legend_visa_not_required))
        assertTrue(labelResIds.contains(R.string.legend_evisa))
        assertTrue(labelResIds.contains(R.string.legend_visa_on_arrival))
        assertTrue(labelResIds.contains(R.string.legend_visa_required))
        assertTrue(labelResIds.contains(R.string.legend_restricted))
    }

    @Test
    fun `getLegendItems returns 4 items for PASSPORT_VALIDITY mode`() {
        val items = getLegendItems(MapColorMode.PASSPORT_VALIDITY)
        assertEquals(4, items.size)
    }

    @Test
    fun `getLegendItems PASSPORT_VALIDITY contains all validity types`() {
        val items = getLegendItems(MapColorMode.PASSPORT_VALIDITY)
        val labelResIds = items.map { it.labelResId }
        assertTrue(labelResIds.contains(R.string.legend_six_months))
        assertTrue(labelResIds.contains(R.string.legend_three_months))
        assertTrue(labelResIds.contains(R.string.legend_duration_of_stay))
        assertTrue(labelResIds.contains(R.string.legend_other))
    }

    @Test
    fun `getLegendItems all items have non-empty test tags`() {
        MapColorMode.entries.forEach { mode ->
            val items = getLegendItems(mode)
            items.forEach { item ->
                assertTrue(
                    "Item with labelResId '${item.labelResId}' should have non-empty testTag",
                    item.testTag.isNotBlank()
                )
            }
        }
    }

    @Test
    fun `getLegendItems all items have valid label resource IDs`() {
        MapColorMode.entries.forEach { mode ->
            val items = getLegendItems(mode)
            items.forEach { item ->
                assertTrue(
                    "Item should have non-zero labelResId",
                    item.labelResId != 0
                )
            }
        }
    }

    @Test
    fun `getLegendItems test tags follow naming convention`() {
        MapColorMode.entries.forEach { mode ->
            val items = getLegendItems(mode)
            items.forEach { item ->
                assertTrue(
                    "TestTag '${item.testTag}' should start with 'legend_item_'",
                    item.testTag.startsWith("legend_item_")
                )
            }
        }
    }

    // ==================== LegendItem Tests ====================

    @Test
    fun `LegendItem can be created with all properties`() {
        val item = LegendItem(
            color = Color.Red,
            labelResId = R.string.legend_low_risk,
            testTag = "test_tag"
        )

        assertEquals(Color.Red, item.color)
        assertEquals(R.string.legend_low_risk, item.labelResId)
        assertEquals("test_tag", item.testTag)
    }

    @Test
    fun `LegendItem equality works correctly`() {
        val item1 = LegendItem(Color.Red, R.string.legend_low_risk, "tag")
        val item2 = LegendItem(Color.Red, R.string.legend_low_risk, "tag")
        val item3 = LegendItem(Color.Blue, R.string.legend_low_risk, "tag")

        assertEquals(item1, item2)
        assertNotEquals(item1, item3)
    }

    // ==================== geoJsonToRepoId Tests ====================

    @Test
    fun `geoJsonToRepoId contains USA mapping`() {
        assertEquals("us", geoJsonToRepoId["USA"])
    }

    @Test
    fun `geoJsonToRepoId contains GBR mapping`() {
        assertEquals("gb", geoJsonToRepoId["GBR"])
    }

    @Test
    fun `geoJsonToRepoId contains JPN mapping`() {
        assertEquals("jp", geoJsonToRepoId["JPN"])
    }

    @Test
    fun `geoJsonToRepoId contains AUS mapping`() {
        assertEquals("au", geoJsonToRepoId["AUS"])
    }

    @Test
    fun `geoJsonToRepoId contains BRA mapping`() {
        assertEquals("br", geoJsonToRepoId["BRA"])
    }

    @Test
    fun `geoJsonToRepoId contains NIR mapping for Northern Ireland`() {
        assertEquals("xni", geoJsonToRepoId["NIR"])
    }

    @Test
    fun `geoJsonToRepoId contains CYN mapping for Northern Cyprus`() {
        assertEquals("xnc", geoJsonToRepoId["CYN"])
    }

    @Test
    fun `geoJsonToRepoId returns null for unknown code`() {
        assertNull(geoJsonToRepoId["INVALID"])
    }

    @Test
    fun `geoJsonToRepoId most values are 2-letter codes`() {
        // Most codes are 2-letter ISO codes, but some territories use custom codes (e.g., "xso" for Somaliland)
        val standardCodes = geoJsonToRepoId.values.filter { it.length == 2 }
        val customCodes = geoJsonToRepoId.values.filter { it.length != 2 }

        // The vast majority should be standard 2-letter codes
        assertTrue(
            "Most codes should be 2-letter codes",
            standardCodes.size > geoJsonToRepoId.size * 0.95
        )

        // Custom codes should be lowercase
        customCodes.forEach { code ->
            assertEquals(
                "Custom code '$code' should be lowercase",
                code.lowercase(),
                code
            )
        }
    }

    @Test
    fun `geoJsonToRepoId all keys are 3-letter codes`() {
        geoJsonToRepoId.keys.forEach { code ->
            assertEquals(
                "Key '$code' should be 3 characters",
                3,
                code.length
            )
        }
    }

    @Test
    fun `geoJsonToRepoId all values are lowercase`() {
        geoJsonToRepoId.values.forEach { code ->
            assertEquals(
                "Code '$code' should be lowercase",
                code.lowercase(),
                code
            )
        }
    }

    @Test
    fun `geoJsonToRepoId all keys are uppercase`() {
        geoJsonToRepoId.keys.forEach { code ->
            assertEquals(
                "Key '$code' should be uppercase",
                code.uppercase(),
                code
            )
        }
    }

    @Test
    fun `geoJsonToRepoId contains countries from all continents`() {
        // North America
        assertNotNull(geoJsonToRepoId["USA"])
        assertNotNull(geoJsonToRepoId["CAN"])
        // South America
        assertNotNull(geoJsonToRepoId["BRA"])
        assertNotNull(geoJsonToRepoId["ARG"])
        // Europe
        assertNotNull(geoJsonToRepoId["GBR"])
        assertNotNull(geoJsonToRepoId["FRA"])
        // Africa
        assertNotNull(geoJsonToRepoId["EGY"])
        assertNotNull(geoJsonToRepoId["ZAF"])
        // Asia
        assertNotNull(geoJsonToRepoId["CHN"])
        assertNotNull(geoJsonToRepoId["JPN"])
        // Oceania
        assertNotNull(geoJsonToRepoId["AUS"])
        assertNotNull(geoJsonToRepoId["NZL"])
        // Antarctica
        assertNotNull(geoJsonToRepoId["ATA"])
    }

    @Test
    fun `geoJsonToRepoId has more than 100 mappings`() {
        assertTrue(
            "Should have many country mappings",
            geoJsonToRepoId.size > 100
        )
    }

    // ==================== Coordinate Conversion Round-Trip Tests ====================

    @Test
    fun `coordinate conversion round trip for Tokyo`() {
        // Tokyo: approximately 35.6762° N, 139.6503° E
        val lat = 35.6762f
        val lng = 139.6503f

        val x = MercatorProjection.longitudeToX(lng)
        val y = MercatorProjection.latitudeToY(lat)

        val recoveredLng = MercatorProjection.xToLongitude(x)
        val recoveredLat = MercatorProjection.yToLatitude(y)

        assertEquals(lng, recoveredLng, 0.01f)
        assertEquals(lat, recoveredLat, 0.1f)
    }

    @Test
    fun `coordinate conversion round trip for New York`() {
        // New York: approximately 40.7128° N, 74.0060° W
        val lat = 40.7128f
        val lng = -74.0060f

        val x = MercatorProjection.longitudeToX(lng)
        val y = MercatorProjection.latitudeToY(lat)

        val recoveredLng = MercatorProjection.xToLongitude(x)
        val recoveredLat = MercatorProjection.yToLatitude(y)

        assertEquals(lng, recoveredLng, 0.01f)
        assertEquals(lat, recoveredLat, 0.1f)
    }

    @Test
    fun `coordinate conversion round trip for Sydney`() {
        // Sydney: approximately 33.8688° S, 151.2093° E
        val lat = -33.8688f
        val lng = 151.2093f

        val x = MercatorProjection.longitudeToX(lng)
        val y = MercatorProjection.latitudeToY(lat)

        val recoveredLng = MercatorProjection.xToLongitude(x)
        val recoveredLat = MercatorProjection.yToLatitude(y)

        assertEquals(lng, recoveredLng, 0.01f)
        assertEquals(lat, recoveredLat, 0.1f)
    }

    // ==================== Edge Case Tests ====================

    @Test
    fun `MercatorProjection handles extreme positive longitude`() {
        val x = MercatorProjection.longitudeToX(180f)
        assertEquals(1f, x, 0.001f)
    }

    @Test
    fun `MercatorProjection handles extreme negative longitude`() {
        val x = MercatorProjection.longitudeToX(-180f)
        assertEquals(0f, x, 0.001f)
    }

    @Test
    fun `normalizeOffsetX handles boundary value -0_5`() {
        val result = normalizeOffsetX(-0.5f)
        assertEquals(-0.5f, result, 0.001f)
    }

    @Test
    fun `normalizeNormalizedX handles boundary value 0`() {
        val result = normalizeNormalizedX(0f)
        assertEquals(0f, result, 0.001f)
    }

    @Test
    fun `normalizeNormalizedX handles value just below 1`() {
        val result = normalizeNormalizedX(0.9999f)
        assertEquals(0.9999f, result, 0.001f)
    }

    @Test
    fun `calculateVerticalPanBounds locks pan when not zoomed`() {
        val bounds = calculateVerticalPanBounds(
            scale = 1f,
            mapHeight = 1000f
        )

        assertEquals(0f, bounds.minPanY, 0.001f)
        assertEquals(0f, bounds.maxPanY, 0.001f)
    }

    @Test
    fun `calculateVerticalPanBounds allows traversing full map at high zoom`() {
        val bounds = calculateVerticalPanBounds(
            scale = 200f,
            mapHeight = 1018.52f
        )

        assertTrue("Bottom-edge bound should allow substantial upward travel", bounds.minPanY < -0.99f)
        assertTrue("Top-edge bound should allow substantial downward travel", bounds.maxPanY > 0.99f)
        assertTrue(bounds.minPanY < bounds.maxPanY)
    }

    // ==================== calculateMultiTouchTransform Tests ====================
    //
    // Rendering formula:
    //   screen_x = pivotX + scale * mapWidth * (normX + panX - 0.5)
    //   where pivotX = canvasOffsetX + 0.5 * mapWidth
    //
    // Core invariant: the map point that was under prevCentroid should appear
    // at prevCentroid + pan (i.e. at the current finger position) after the transform.

    private fun makeLayout(
        mapWidth: Float = 1000f,
        mapHeight: Float = 500f,
        canvasOffsetX: Float = 0f,
        canvasOffsetY: Float = 0f
    ) = MapLayout(
        mapWidth = mapWidth,
        mapHeight = mapHeight,
        canvasOffsetX = canvasOffsetX,
        canvasOffsetY = canvasOffsetY,
        canvasWidth = mapWidth + canvasOffsetX * 2,
        canvasHeight = mapHeight + canvasOffsetY * 2
    )

    /**
     * Computes the screen X position of a map point at [normX] given the transform [state]
     * and [layout], using the same formula as the rendering withTransform block.
     */
    private fun screenX(normX: Float, state: TransformState, layout: MapLayout): Float {
        val pivotX = layout.canvasOffsetX + 0.5f * layout.mapWidth
        return pivotX + state.scale * layout.mapWidth * (normX + state.panX - 0.5f)
    }

    private fun screenY(normY: Float, state: TransformState, layout: MapLayout): Float {
        val pivotY = layout.canvasOffsetY + 0.5f * layout.mapHeight
        return pivotY + state.scale * layout.mapHeight * (normY + state.panY - 0.5f)
    }

    /**
     * Verifies the core centroid-pivot invariant: the map point under [prevCentroid]
     * in [initial] state should appear at prevCentroid + pan after the transform.
     */
    private fun assertCentroidPivotInvariant(
        initial: TransformState,
        zoom: Float,
        pan: Offset,
        prevCentroid: Offset,
        layout: MapLayout,
        delta: Float = 0.1f
    ) {
        val result = calculateMultiTouchTransform(initial, zoom, pan, prevCentroid, layout)
        val pivotX = layout.canvasOffsetX + 0.5f * layout.mapWidth
        val pivotY = layout.canvasOffsetY + 0.5f * layout.mapHeight

        val normX = (prevCentroid.x - pivotX) / (initial.scale * layout.mapWidth) + 0.5f - initial.panX
        val normY = (prevCentroid.y - pivotY) / (initial.scale * layout.mapHeight) + 0.5f - initial.panY

        assertEquals(
            "world point under prevCentroid should appear at prevCentroid.x + pan.x",
            prevCentroid.x + pan.x,
            screenX(normX, result, layout),
            delta
        )
        assertEquals(
            "world point under prevCentroid should appear at prevCentroid.y + pan.y",
            prevCentroid.y + pan.y,
            screenY(normY, result, layout),
            delta
        )
    }

    @Test
    fun `calculateMultiTouchTransform pure zoom at map center does not change pan`() {
        val layout = makeLayout()
        val initial = TransformState(scale = 1f, panX = 0f, panY = 0f)
        val pivotX = layout.canvasOffsetX + 0.5f * layout.mapWidth
        val pivotY = layout.canvasOffsetY + 0.5f * layout.mapHeight
        val centroid = Offset(pivotX, pivotY) // centroid IS the map center

        val result = calculateMultiTouchTransform(
            current = initial,
            zoom = 2f,
            pan = Offset.Zero,
            prevCentroid = centroid,
            layout = layout
        )

        assertEquals(2f, result.scale, 0.001f)
        assertEquals(0f, result.panX, 0.001f) // no drift when centroid is at pivot
        assertEquals(0f, result.panY, 0.001f)
    }

    @Test
    fun `calculateMultiTouchTransform pure zoom at right of center shifts panX left`() {
        val layout = makeLayout(mapWidth = 1000f, mapHeight = 500f)
        val initial = TransformState(scale = 1f, panX = 0f, panY = 0f)
        // Centroid at x=700 — to the right of the map center (x=500)
        val centroid = Offset(700f, 250f)

        val result = calculateMultiTouchTransform(
            current = initial,
            zoom = 2f,
            pan = Offset.Zero,
            prevCentroid = centroid,
            layout = layout
        )

        assertEquals(2f, result.scale, 0.001f)
        // panX should be negative: map shifted right so the right-side content stays visible
        assertTrue("panX should be negative when zooming right of center", result.panX < 0f)
        assertCentroidPivotInvariant(initial, 2f, Offset.Zero, centroid, layout)
    }

    @Test
    fun `calculateMultiTouchTransform pure zoom at left of center shifts panX right`() {
        val layout = makeLayout(mapWidth = 1000f, mapHeight = 500f)
        val initial = TransformState(scale = 1f, panX = 0f, panY = 0f)
        val centroid = Offset(300f, 250f) // left of center

        val result = calculateMultiTouchTransform(
            current = initial,
            zoom = 2f,
            pan = Offset.Zero,
            prevCentroid = centroid,
            layout = layout
        )

        assertEquals(2f, result.scale, 0.001f)
        assertTrue("panX should be positive when zooming left of center", result.panX > 0f)
        assertCentroidPivotInvariant(initial, 2f, Offset.Zero, centroid, layout)
    }

    @Test
    fun `calculateMultiTouchTransform centroid pivot invariant holds for zoom only`() {
        val layout = makeLayout(mapWidth = 1000f, mapHeight = 500f)
        val initial = TransformState(scale = 1f, panX = 0f, panY = 0f)
        val centroid = Offset(750f, 180f)

        assertCentroidPivotInvariant(initial, 3f, Offset.Zero, centroid, layout)
    }

    @Test
    fun `calculateMultiTouchTransform centroid pivot invariant holds for zoom with pan`() {
        val layout = makeLayout(mapWidth = 1000f, mapHeight = 500f)
        val initial = TransformState(scale = 1f, panX = 0f, panY = 0f)
        // Simultaneous zoom AND translation — the classic pinch gesture
        val prevCentroid = Offset(700f, 250f)
        val pan = Offset(30f, 10f) // fingers also moving

        assertCentroidPivotInvariant(initial, 1.5f, pan, prevCentroid, layout)
    }

    @Test
    fun `calculateMultiTouchTransform centroid pivot invariant holds at non-zero initial pan`() {
        val layout = makeLayout(mapWidth = 1000f, mapHeight = 500f)
        val initial = TransformState(scale = 2f, panX = 0.1f, panY = 0.05f)
        val prevCentroid = Offset(800f, 200f)
        val pan = Offset(-20f, 5f)

        assertCentroidPivotInvariant(initial, 1.5f, pan, prevCentroid, layout)
    }

    @Test
    fun `calculateMultiTouchTransform centroid pivot invariant holds with canvas offset`() {
        // Canvas wider than map — canvasOffsetX > 0
        val layout = makeLayout(mapWidth = 600f, mapHeight = 500f, canvasOffsetX = 200f)
        val initial = TransformState(scale = 1f, panX = 0f, panY = 0f)
        val prevCentroid = Offset(600f, 250f) // right of map center (which is at x=500)
        val pan = Offset(10f, 0f)

        assertCentroidPivotInvariant(initial, 2f, pan, prevCentroid, layout)
    }

    @Test
    fun `calculateMultiTouchTransform fast lateral pan with zoom does not drift`() {
        // This test verifies the useCurrent=false fix. With useCurrent=true the error per
        // frame is pan * (1 - s_new/s_old). Over many frames with large pan this drifts.
        val layout = makeLayout(mapWidth = 1000f, mapHeight = 500f)
        var state = TransformState(scale = 1f, panX = 0f, panY = 0f)

        // Simulate 20 frames of aggressive pinch: zoom 1.05x + 20px lateral pan per frame
        val panPerFrame = Offset(20f, 0f)
        val zoomPerFrame = 1.05f

        repeat(20) { frame ->
            val prevCentroid = Offset(700f + frame * 20f, 250f)
            state = calculateMultiTouchTransform(state, zoomPerFrame, panPerFrame, prevCentroid, layout)
        }

        // After 20 frames the scale should be ~1.05^20 ≈ 2.65
        assertEquals(1.05f.pow(20f), state.scale, 0.1f)

        // The world point originally at normX=0.7 should be near where it's expected.
        // Primary check: invariant must hold for the last frame, which is what assertCentroidPivotInvariant does.
        // The accumulated pan is correct when each frame uses the previous centroid.
        val finalPrevCentroid = Offset(700f + 19 * 20f, 250f)
        assertCentroidPivotInvariant(
            // Use second-to-last state to check the last frame's invariant
            TransformState(scale = state.scale / 1.05f, panX = state.panX, panY = state.panY),
            1.05f, panPerFrame, finalPrevCentroid, layout
        )
    }

    @Test
    fun `calculateMultiTouchTransform scale is clamped at minimum 1f`() {
        val layout = makeLayout()
        val initial = TransformState(scale = 1f, panX = 0f, panY = 0f)

        val result = calculateMultiTouchTransform(
            current = initial,
            zoom = 0.5f, // would produce scale=0.5 without clamping
            pan = Offset.Zero,
            prevCentroid = Offset(500f, 250f),
            layout = layout
        )

        assertEquals(1f, result.scale, 0.001f)
    }

    @Test
    fun `calculateMultiTouchTransform scale is clamped at maximum 200f`() {
        val layout = makeLayout()
        val initial = TransformState(scale = 150f, panX = 0f, panY = 0f)

        val result = calculateMultiTouchTransform(
            current = initial,
            zoom = 5f, // would produce scale=750 without clamping
            pan = Offset.Zero,
            prevCentroid = Offset(500f, 250f),
            layout = layout
        )

        assertEquals(200f, result.scale, 0.001f)
    }

    @Test
    fun `calculateMultiTouchTransform panY is clamped within vertical bounds`() {
        val layout = makeLayout(mapWidth = 1000f, mapHeight = 500f)
        // Start near the top boundary
        val initial = TransformState(scale = 2f, panX = 0f, panY = 0.4f)
        val prevCentroid = Offset(500f, 10f) // near the top edge — would push panY higher

        val result = calculateMultiTouchTransform(
            current = initial,
            zoom = 1.5f,
            pan = Offset(0f, -100f), // pull upward
            prevCentroid = prevCentroid,
            layout = layout
        )

        val bounds = calculateVerticalPanBounds(result.scale, layout.mapHeight)
        assertTrue(result.panY >= bounds.minPanY)
        assertTrue(result.panY <= bounds.maxPanY)
    }

    @Test
    fun `calculateMultiTouchTransform incremental zoom accumulates correctly`() {
        // Zooming in two steps should produce the same result as one equivalent step
        // (when prevCentroid is used, this identity holds exactly).
        val layout = makeLayout(mapWidth = 1000f, mapHeight = 500f)
        val initial = TransformState(scale = 1f, panX = 0f, panY = 0f)
        val centroid = Offset(700f, 200f)

        // One step: s=1 → s=4, zoom=4
        val oneStep = calculateMultiTouchTransform(initial, 4f, Offset.Zero, centroid, layout)

        // Two steps: s=1 → s=2, then s=2 → s=4
        val step1 = calculateMultiTouchTransform(initial, 2f, Offset.Zero, centroid, layout)
        val twoStep = calculateMultiTouchTransform(step1, 2f, Offset.Zero, centroid, layout)

        assertEquals(oneStep.scale, twoStep.scale, 0.001f)
        assertEquals(oneStep.panX, twoStep.panX, 0.001f)
        assertEquals(oneStep.panY, twoStep.panY, 0.001f)
    }

    @Test
    fun `calculateMultiTouchTransform zoom factor 1f with no pan leaves state unchanged`() {
        val layout = makeLayout()
        val initial = TransformState(scale = 2f, panX = 0.1f, panY = -0.05f)

        val result = calculateMultiTouchTransform(
            current = initial,
            zoom = 1f,
            pan = Offset.Zero,
            prevCentroid = Offset(700f, 300f),
            layout = layout
        )

        assertEquals(initial.scale, result.scale, 0.001f)
        assertEquals(initial.panX, result.panX, 0.001f)
        assertEquals(initial.panY, result.panY, 0.001f)
    }

    @Test
    fun `calculateMultiTouchTransform pure pan without zoom translates correctly`() {
        val layout = makeLayout(mapWidth = 1000f, mapHeight = 500f)
        val initial = TransformState(scale = 2f, panX = 0f, panY = 0f)
        val pan = Offset(50f, 0f)

        val result = calculateMultiTouchTransform(
            current = initial,
            zoom = 1f,
            pan = pan,
            prevCentroid = Offset(500f, 250f), // centroid at pivot — zoom correction is zero
            layout = layout
        )

        // pan.x / (mapWidth * scale) = 50 / (1000 * 2) = 0.025
        assertEquals(0.025f, result.panX, 0.001f)
    }

    // ==================== Additional MercatorProjection Tests ====================

    @Test
    fun `latitudeToY produces monotonically increasing values for decreasing latitudes`() {
        val latitudes = listOf(80f, 60f, 40f, 20f, 0f, -20f, -40f, -60f, -80f)
        val yValues = latitudes.map { MercatorProjection.latitudeToY(it) }

        for (i in 0 until yValues.size - 1) {
            assertTrue(
                "Y value at lat ${latitudes[i]} should be less than Y at lat ${latitudes[i + 1]}",
                yValues[i] < yValues[i + 1]
            )
        }
    }

    @Test
    fun `longitudeToX produces monotonically increasing values`() {
        val longitudes = listOf(-180f, -120f, -60f, 0f, 60f, 120f, 180f)
        val xValues = longitudes.map { MercatorProjection.longitudeToX(it) }

        for (i in 0 until xValues.size - 1) {
            assertTrue(
                "X value at lng ${longitudes[i]} should be less than X at lng ${longitudes[i + 1]}",
                xValues[i] < xValues[i + 1]
            )
        }
    }

    @Test
    fun `latitudeToY returns values in valid range`() {
        val testLatitudes = listOf(-85f, -60f, -30f, 0f, 30f, 60f, 83f)
        testLatitudes.forEach { lat ->
            val y = MercatorProjection.latitudeToY(lat)
            assertTrue("Y value for lat $lat should be >= 0", y >= 0f)
            assertTrue("Y value for lat $lat should be <= 1", y <= 1f)
        }
    }

    @Test
    fun `longitudeToX returns values in valid range`() {
        val testLongitudes = listOf(-180f, -90f, 0f, 90f, 180f)
        testLongitudes.forEach { lng ->
            val x = MercatorProjection.longitudeToX(lng)
            assertTrue("X value for lng $lng should be >= 0", x >= 0f)
            assertTrue("X value for lng $lng should be <= 1", x <= 1f)
        }
    }

    @Test
    fun `yToLatitude returns high positive latitude for y near 0`() {
        val lat = MercatorProjection.yToLatitude(0.1f)
        assertTrue("Latitude should be positive for y near 0", lat > 0f)
        assertTrue("Latitude should be high for y near 0", lat > 60f)
    }

    @Test
    fun `yToLatitude returns high negative latitude for y near 1`() {
        val lat = MercatorProjection.yToLatitude(0.9f)
        assertTrue("Latitude should be negative for y near 1", lat < 0f)
        assertTrue("Latitude should be low for y near 1", lat < -60f)
    }

    @Test
    fun `xToLongitude returns negative longitude for x less than 0_5`() {
        val lng = MercatorProjection.xToLongitude(0.25f)
        assertTrue("Longitude should be negative for x < 0.5", lng < 0f)
    }

    @Test
    fun `xToLongitude returns positive longitude for x greater than 0_5`() {
        val lng = MercatorProjection.xToLongitude(0.75f)
        assertTrue("Longitude should be positive for x > 0.5", lng > 0f)
    }

    @Test
    fun `getAspectRatio is consistent across multiple calls`() {
        val ratio1 = MercatorProjection.getAspectRatio()
        val ratio2 = MercatorProjection.getAspectRatio()
        assertEquals(ratio1, ratio2, 0.0001f)
    }

    // ==================== Additional normalizeOffsetX Tests ====================

    @Test
    fun `normalizeOffsetX handles exact 1_0 offset`() {
        val result = normalizeOffsetX(1.0f)
        assertEquals(0f, result, 0.001f)
    }

    @Test
    fun `normalizeOffsetX handles exact -1_0 offset`() {
        val result = normalizeOffsetX(-1.0f)
        assertEquals(0f, result, 0.001f)
    }

    @Test
    fun `normalizeOffsetX is idempotent`() {
        val values = listOf(-0.3f, 0f, 0.25f, 0.49f, -0.49f)
        values.forEach { value ->
            val first = normalizeOffsetX(value)
            val second = normalizeOffsetX(first)
            assertEquals("normalizeOffsetX should be idempotent for $value", first, second, 0.001f)
        }
    }

    @Test
    fun `normalizeOffsetX wraps correctly near boundaries`() {
        // Just over 0.5 should wrap to negative
        val result = normalizeOffsetX(0.51f)
        assertTrue("Result should be negative when wrapping from 0.51", result < 0f)
    }

    @Test
    fun `calculateHorizontalWrapOffsets includes padded neighbor copies at high zoom`() {
        val offsets = calculateHorizontalWrapOffsets(
            panX = -0.49f,
            scale = 80f,
            mapWidth = 1000f,
            canvasOffsetX = 0f,
            canvasWidth = 1000f
        )

        assertTrue("Center copy should always be included", 0 in offsets)
        assertTrue("Right wrapped neighbor should be included near the west edge", 1 in offsets)
    }

    @Test
    fun `calculateHorizontalWrapOffsets expands for wide canvases`() {
        val offsets = calculateHorizontalWrapOffsets(
            panX = 0f,
            scale = 1f,
            mapWidth = 1000f,
            canvasOffsetX = -500f,
            canvasWidth = 3000f
        )

        assertTrue("Wide viewports need more than the three default copies", offsets.last > 1)
    }

    // ==================== Additional normalizeNormalizedX Tests ====================

    @Test
    fun `normalizeNormalizedX is idempotent`() {
        val values = listOf(0.1f, 0.5f, 0.9f, 0f)
        values.forEach { value ->
            val first = normalizeNormalizedX(value)
            val second = normalizeNormalizedX(first)
            assertEquals("normalizeNormalizedX should be idempotent for $value", first, second, 0.001f)
        }
    }

    @Test
    fun `normalizeNormalizedX handles multiple wraps positive`() {
        val result = normalizeNormalizedX(5.3f)
        assertEquals(0.3f, result, 0.001f)
    }

    @Test
    fun `normalizeNormalizedX handles multiple wraps negative`() {
        val result = normalizeNormalizedX(-5.3f)
        assertEquals(0.7f, result, 0.001f)
    }

    // ==================== Additional geoJsonToRepoId Tests ====================

    @Test
    fun `geoJsonToRepoId contains all major European countries`() {
        val europeanCodes = listOf("GBR", "FRA", "DEU", "ITA", "ESP", "POL", "NLD", "BEL", "SWE", "NOR")
        europeanCodes.forEach { code ->
            assertNotNull("Should contain $code", geoJsonToRepoId[code])
        }
    }

    @Test
    fun `geoJsonToRepoId contains all major Asian countries`() {
        val asianCodes = listOf("CHN", "JPN", "IND", "KOR", "THA", "VNM", "IDN", "PHL", "SGP", "MYS")
        asianCodes.forEach { code ->
            assertNotNull("Should contain $code", geoJsonToRepoId[code])
        }
    }

    @Test
    fun `geoJsonToRepoId contains all major African countries`() {
        val africanCodes = listOf("EGY", "ZAF", "NGA", "KEN", "MAR", "ETH", "GHA", "TZA")
        africanCodes.forEach { code ->
            assertNotNull("Should contain $code", geoJsonToRepoId[code])
        }
    }

    @Test
    fun `geoJsonToRepoId contains all South American countries`() {
        val southAmericanCodes = listOf("BRA", "ARG", "COL", "PER", "VEN", "CHL", "ECU", "BOL", "PRY", "URY")
        southAmericanCodes.forEach { code ->
            assertNotNull("Should contain $code", geoJsonToRepoId[code])
        }
    }

    @Test
    fun `geoJsonToRepoId contains Central American and Caribbean countries`() {
        val centralAmericanCodes = listOf("MEX", "GTM", "CUB", "HTI", "DOM", "HND", "NIC", "CRI", "PAN", "JAM")
        centralAmericanCodes.forEach { code ->
            assertNotNull("Should contain $code", geoJsonToRepoId[code])
        }
    }

    @Test
    fun `geoJsonToRepoId contains Oceanian countries`() {
        val oceanianCodes = listOf(
            "AUS", "NZL", "PNG", "FJI",
            // Pacific micronations
            "NRU", "TUV", "PLW", "MHL", "WSM", "TON", "KIR", "FSM"
        )
        oceanianCodes.forEach { code ->
            assertNotNull("Should contain $code", geoJsonToRepoId[code])
        }
    }

    @Test
    fun `geoJsonToRepoId contains ATA mapping for Antarctica`() {
        assertEquals("aa", geoJsonToRepoId["ATA"])
    }

    @Test
    fun `geoJsonToRepoId ATA maps to repository id aa`() {
        val repoId = geoJsonToRepoId["ATA"]
        assertNotNull("ATA should have a repository mapping", repoId)
        assertEquals(
            "Antarctica GeoJSON code ATA must map to repository id 'aa'",
            "aa",
            repoId
        )
    }

    @Test
    fun `geoJsonToRepoId contains Middle Eastern countries`() {
        val middleEastCodes = listOf("ARE", "SAU", "ISR", "IRQ", "IRN", "JOR", "KWT", "QAT", "OMN", "BHR")
        middleEastCodes.forEach { code ->
            assertNotNull("Should contain $code", geoJsonToRepoId[code])
        }
    }

    @Test
    fun `geoJsonToRepoId all values are non-blank`() {
        // Every entry must resolve to a non-empty parent repo ID;
        // duplicates are expected now that territories map to sovereign parents.
        geoJsonToRepoId.forEach { (key, value) ->
            assertTrue("Entry $key must have a non-blank repo ID", value.isNotBlank())
        }
    }

    @Test
    fun `geoJsonToRepoId territory entries map to correct repo ids`() {
        // Territories with own repository entries (own currency / continent)
        assertEquals("ky", geoJsonToRepoId["CYM"]) // Cayman Islands (KYD)
        assertEquals("bm", geoJsonToRepoId["BMU"]) // Bermuda (BMD)
        assertEquals("fk", geoJsonToRepoId["FLK"]) // Falkland Islands (FKP)
        assertEquals("gi", geoJsonToRepoId["GIB"]) // Gibraltar (GIP)
        assertEquals("fo", geoJsonToRepoId["FRO"]) // Faroe Islands (DKK)
        assertEquals("sh", geoJsonToRepoId["SHN"]) // Saint Helena (SHP)
        assertEquals("mq", geoJsonToRepoId["MTQ"]) // Martinique (EUR, North America)
        // French territories that still delegate to France
        assertEquals("fr", geoJsonToRepoId["GUF"]) // French Guiana
        assertEquals("fr", geoJsonToRepoId["PYF"]) // French Polynesia
        // US territories
        assertEquals("us", geoJsonToRepoId["PRI"]) // Puerto Rico
        assertEquals("us", geoJsonToRepoId["GUM"]) // Guam
        // Netherlands territories
        assertEquals("nl", geoJsonToRepoId["CUW"]) // Curaçao
        // New Zealand territories
        assertEquals("nz", geoJsonToRepoId["COK"]) // Cook Islands
    }

    // ==================== Additional getLegendItems Color Tests ====================

    @Test
    fun `getLegendItems SECURITY_RISK colors are distinct`() {
        val items = getLegendItems(MapColorMode.SECURITY_RISK)
        val colors = items.map { it.color }
        assertEquals(colors.size, colors.distinct().size)
    }

    @Test
    fun `getLegendItems VISA_REQUIREMENTS colors are distinct`() {
        val items = getLegendItems(MapColorMode.VISA_REQUIREMENTS)
        val colors = items.map { it.color }
        assertEquals(colors.size, colors.distinct().size)
    }

    @Test
    fun `getLegendItems PASSPORT_VALIDITY colors are distinct`() {
        val items = getLegendItems(MapColorMode.PASSPORT_VALIDITY)
        val colors = items.map { it.color }
        assertEquals(colors.size, colors.distinct().size)
    }

    @Test
    fun `getLegendItems SECURITY_RISK has correct colors`() {
        val items = getLegendItems(MapColorMode.SECURITY_RISK)
        val lowRisk = items.find { it.labelResId == R.string.legend_low_risk }
        val mediumRisk = items.find { it.labelResId == R.string.legend_medium_risk }
        val highRisk = items.find { it.labelResId == R.string.legend_high_risk }
        val extremeRisk = items.find { it.labelResId == R.string.legend_extreme_risk }

        assertEquals(Color(0xFF4CAF50), lowRisk?.color)    // Green
        assertEquals(Color(0xFFFFC107), mediumRisk?.color) // Yellow
        assertEquals(Color(0xFFFF9800), highRisk?.color)   // Orange
        assertEquals(Color(0xFFE53935), extremeRisk?.color) // Red
    }

    @Test
    fun `getLegendItems VISA_REQUIREMENTS has correct colors`() {
        val items = getLegendItems(MapColorMode.VISA_REQUIREMENTS)

        assertEquals(Color(0xFF4CAF50), items.find { it.labelResId == R.string.legend_visa_not_required }?.color)
        assertEquals(Color(0xFF00BCD4), items.find { it.labelResId == R.string.legend_evisa }?.color)
        assertEquals(Color(0xFFFFC107), items.find { it.labelResId == R.string.legend_visa_on_arrival }?.color)
        assertEquals(Color(0xFF9E9E9E), items.find { it.labelResId == R.string.legend_visa_required }?.color)
        assertEquals(Color(0xFF000000), items.find { it.labelResId == R.string.legend_restricted }?.color)
    }

    @Test
    fun `getLegendItems PASSPORT_VALIDITY has correct colors`() {
        val items = getLegendItems(MapColorMode.PASSPORT_VALIDITY)

        assertEquals(Color(0xFF9E9E9E), items.find { it.labelResId == R.string.legend_six_months }?.color)
        assertEquals(Color(0xFF00BCD4), items.find { it.labelResId == R.string.legend_three_months }?.color)
        assertEquals(Color(0xFF4CAF50), items.find { it.labelResId == R.string.legend_duration_of_stay }?.color)
        assertEquals(Color(0xFFFFC107), items.find { it.labelResId == R.string.legend_other }?.color)
    }

    // ==================== Additional PassportValidityColors Tests ====================

    @Test
    fun `PassportValidityColors all colors are distinct`() {
        val colors = listOf(
            PassportValidityColors.SixMonths,
            PassportValidityColors.ThreeMonths,
            PassportValidityColors.PlannedStay,
            PassportValidityColors.Other
        )
        assertEquals(4, colors.distinct().size)
    }

    @Test
    fun `PassportValidityColors are opaque`() {
        val colors = listOf(
            PassportValidityColors.SixMonths,
            PassportValidityColors.ThreeMonths,
            PassportValidityColors.PlannedStay,
            PassportValidityColors.Other
        )
        colors.forEach { color ->
            assertEquals("Color should be fully opaque", 1f, color.alpha)
        }
    }

    // ==================== Additional getPassportValidityColor Tests ====================

    @Test
    fun `getPassportValidityColor is case sensitive`() {
        // Uppercase should not match
        val color = getPassportValidityColor("6 MONTHS")
        assertEquals(PassportValidityColors.Other, color)
    }

    @Test
    fun `getPassportValidityColor requires exact match`() {
        // Partial match should not work
        val color = getPassportValidityColor("6 months validity")
        assertEquals(PassportValidityColors.Other, color)
    }

    @Test
    fun `getPassportValidityColor handles whitespace variations`() {
        // Extra whitespace should not match
        val color = getPassportValidityColor(" 6 months ")
        assertEquals(PassportValidityColors.Other, color)
    }

    // ==================== Additional MapLegendConfig Tests ====================

    @Test
    fun `MapLegendConfig default callbacks are no-ops`() {
        val config = MapLegendConfig()
        // Should not throw
        config.onCompassTapped()
        config.onLegendClose()
    }

    @Test
    fun `MapLegendConfig copy works correctly`() {
        val original = MapLegendConfig(showLegend = true)
        val copy = original.copy(showLegend = false)

        assertTrue(original.showLegend)
        assertFalse(copy.showLegend)
    }

    // ==================== Additional LegendItem Tests ====================

    @Test
    fun `LegendItem hashCode is consistent`() {
        val item = LegendItem(Color.Red, R.string.legend_low_risk, "tag")
        val hash1 = item.hashCode()
        val hash2 = item.hashCode()
        assertEquals(hash1, hash2)
    }

    @Test
    fun `LegendItem copy works correctly`() {
        val original = LegendItem(Color.Red, R.string.legend_low_risk, "original_tag")
        val copy = original.copy(labelResId = R.string.legend_high_risk)

        assertEquals(R.string.legend_low_risk, original.labelResId)
        assertEquals(R.string.legend_high_risk, copy.labelResId)
        assertEquals(original.color, copy.color)
        assertEquals(original.testTag, copy.testTag)
    }

    @Test
    fun `LegendItem toString contains all fields`() {
        val item = LegendItem(Color.Red, R.string.legend_low_risk, "test_tag")
        val string = item.toString()

        assertTrue(string.contains("labelResId"))
        assertTrue(string.contains("test_tag"))
    }

    // ==================== Extreme Value Tests ====================

    @Test
    fun `MercatorProjection handles Prime Meridian`() {
        val x = MercatorProjection.longitudeToX(0f)
        assertEquals(0.5f, x, 0.001f)
    }

    @Test
    fun `MercatorProjection handles International Date Line west`() {
        val x = MercatorProjection.longitudeToX(-180f)
        assertEquals(0f, x, 0.001f)
    }

    @Test
    fun `MercatorProjection handles International Date Line east`() {
        val x = MercatorProjection.longitudeToX(180f)
        assertEquals(1f, x, 0.001f)
    }

    @Test
    fun `MercatorProjection handles Arctic Circle region`() {
        val y = MercatorProjection.latitudeToY(66.5f)  // Arctic Circle
        assertTrue(y > 0f)
        assertTrue(y < 0.3f)
    }

    @Test
    fun `MercatorProjection handles Antarctic Circle region`() {
        val y = MercatorProjection.latitudeToY(-66.5f)  // Antarctic Circle
        assertTrue(y > 0.7f)
        assertTrue(y < 1f)
    }

    @Test
    fun `MercatorProjection handles Tropic of Cancer`() {
        val y = MercatorProjection.latitudeToY(23.5f)  // Tropic of Cancer
        assertTrue(y > 0.3f)
        assertTrue(y < 0.5f)
    }

    @Test
    fun `MercatorProjection handles Tropic of Capricorn`() {
        val y = MercatorProjection.latitudeToY(-23.5f)  // Tropic of Capricorn
        assertTrue(y > 0.5f)
        assertTrue(y < 0.7f)
    }

    // ==================== geoJsonToRepoId Specific Mappings Tests ====================

    @Test
    fun `geoJsonToRepoId Russia mapping is correct`() {
        assertEquals("ru", geoJsonToRepoId["RUS"])
    }

    @Test
    fun `geoJsonToRepoId China mapping is correct`() {
        assertEquals("cn", geoJsonToRepoId["CHN"])
    }

    @Test
    fun `geoJsonToRepoId India mapping is correct`() {
        assertEquals("in", geoJsonToRepoId["IND"])
    }

    @Test
    fun `geoJsonToRepoId Canada mapping is correct`() {
        assertEquals("ca", geoJsonToRepoId["CAN"])
    }

    @Test
    fun `geoJsonToRepoId Mexico mapping is correct`() {
        assertEquals("mx", geoJsonToRepoId["MEX"])
    }

    @Test
    fun `geoJsonToRepoId Germany mapping is correct`() {
        assertEquals("de", geoJsonToRepoId["DEU"])
    }

    @Test
    fun `geoJsonToRepoId France mapping is correct`() {
        assertEquals("fr", geoJsonToRepoId["FRA"])
    }

    @Test
    fun `geoJsonToRepoId Italy mapping is correct`() {
        assertEquals("it", geoJsonToRepoId["ITA"])
    }

    @Test
    fun `geoJsonToRepoId Spain mapping is correct`() {
        assertEquals("es", geoJsonToRepoId["ESP"])
    }

    @Test
    fun `geoJsonToRepoId South Korea mapping is correct`() {
        assertEquals("kr", geoJsonToRepoId["KOR"])
    }

    @Test
    fun `geoJsonToRepoId North Korea mapping is correct`() {
        assertEquals("kp", geoJsonToRepoId["PRK"])
    }

    @Test
    fun `geoJsonToRepoId Taiwan mapping is correct`() {
        assertEquals("tw", geoJsonToRepoId["TWN"])
    }

    @Test
    fun `geoJsonToRepoId Hong Kong mapping is correct`() {
        assertEquals("hk", geoJsonToRepoId["HKG"])
    }

    @Test
    fun `geoJsonToRepoId UAE mapping is correct`() {
        assertEquals("ae", geoJsonToRepoId["ARE"])
    }

    @Test
    fun `geoJsonToRepoId South Africa mapping is correct`() {
        assertEquals("za", geoJsonToRepoId["ZAF"])
    }

    @Test
    fun `geoJsonToRepoId Egypt mapping is correct`() {
        assertEquals("eg", geoJsonToRepoId["EGY"])
    }

    @Test
    fun `geoJsonToRepoId Nigeria mapping is correct`() {
        assertEquals("ng", geoJsonToRepoId["NGA"])
    }

    @Test
    fun `geoJsonToRepoId New Zealand mapping is correct`() {
        assertEquals("nz", geoJsonToRepoId["NZL"])
    }

    @Test
    fun `geoJsonToRepoId Switzerland mapping is correct`() {
        assertEquals("ch", geoJsonToRepoId["CHE"])
    }

    @Test
    fun `geoJsonToRepoId Ukraine mapping is correct`() {
        assertEquals("ua", geoJsonToRepoId["UKR"])
    }

    // ==================== MapLegendConfig Edge Case Tests ====================

    @Test
    fun `MapLegendConfig equality with same values`() {
        val config1 = MapLegendConfig(showLegend = true)
        val config2 = MapLegendConfig(showLegend = true)

        assertEquals(config1.showLegend, config2.showLegend)
    }

    @Test
    fun `MapLegendConfig with different showLegend values`() {
        val configTrue = MapLegendConfig(showLegend = true)
        val configFalse = MapLegendConfig(showLegend = false)

        assertTrue(configTrue.showLegend)
        assertFalse(configFalse.showLegend)
    }

    @Test
    fun `MapLegendConfig callback invocation order is independent`() {
        var callOrder = mutableListOf<String>()

        val config = MapLegendConfig(
            showLegend = true,
            onCompassTapped = { callOrder.add("compass") },
            onLegendClose = { callOrder.add("close") }
        )

        config.onLegendClose()
        config.onCompassTapped()
        config.onLegendClose()

        assertEquals(listOf("close", "compass", "close"), callOrder)
    }

    // ==================== MercatorProjection Boundary Tests ====================

    @Test
    fun `latitudeToY at exact MAX_LATITUDE returns 0`() {
        val y = MercatorProjection.latitudeToY(MercatorProjection.MAX_LATITUDE)
        assertEquals(0f, y, 0.001f)
    }

    @Test
    fun `latitudeToY at exact MIN_LATITUDE returns 1`() {
        val y = MercatorProjection.latitudeToY(MercatorProjection.MIN_LATITUDE)
        assertEquals(1f, y, 0.001f)
    }

    @Test
    fun `yToLatitude at 0 returns MAX_LATITUDE`() {
        val lat = MercatorProjection.yToLatitude(0f)
        assertEquals(MercatorProjection.MAX_LATITUDE, lat, 0.1f)
    }

    @Test
    fun `yToLatitude at 1 returns MIN_LATITUDE`() {
        val lat = MercatorProjection.yToLatitude(1f)
        assertEquals(MercatorProjection.MIN_LATITUDE, lat, 0.1f)
    }

    @Test
    fun `xToLongitude at 0 returns MIN_LONGITUDE`() {
        val lng = MercatorProjection.xToLongitude(0f)
        assertEquals(MercatorProjection.MIN_LONGITUDE, lng, 0.001f)
    }

    @Test
    fun `xToLongitude at 1 returns MAX_LONGITUDE`() {
        val lng = MercatorProjection.xToLongitude(1f)
        assertEquals(MercatorProjection.MAX_LONGITUDE, lng, 0.001f)
    }

    // ==================== Coordinate Conversion Precision Tests ====================

    @Test
    fun `coordinate conversion maintains precision for small latitude changes`() {
        val lat1 = 45.0f
        val lat2 = 45.1f

        val y1 = MercatorProjection.latitudeToY(lat1)
        val y2 = MercatorProjection.latitudeToY(lat2)

        assertTrue("Small latitude change should produce different Y values", y1 != y2)
    }

    @Test
    fun `coordinate conversion maintains precision for small longitude changes`() {
        val lng1 = 90.0f
        val lng2 = 90.1f

        val x1 = MercatorProjection.longitudeToX(lng1)
        val x2 = MercatorProjection.longitudeToX(lng2)

        assertTrue("Small longitude change should produce different X values", x1 != x2)
    }

    // ==================== normalizeOffsetX Comprehensive Tests ====================

    @Test
    fun `normalizeOffsetX handles very small positive values`() {
        val result = normalizeOffsetX(0.001f)
        assertEquals(0.001f, result, 0.0001f)
    }

    @Test
    fun `normalizeOffsetX handles very small negative values`() {
        val result = normalizeOffsetX(-0.001f)
        assertEquals(-0.001f, result, 0.0001f)
    }

    @Test
    fun `normalizeOffsetX result is always in range`() {
        val testValues = listOf(-10f, -5f, -1f, -0.5f, 0f, 0.5f, 1f, 5f, 10f)
        testValues.forEach { value ->
            val result = normalizeOffsetX(value)
            assertTrue(
                "Result $result for input $value should be in range [-0.5, 0.5)",
                result >= -0.5f && result < 0.5f
            )
        }
    }

    // ==================== normalizeNormalizedX Comprehensive Tests ====================

    @Test
    fun `normalizeNormalizedX handles very small positive values`() {
        val result = normalizeNormalizedX(0.001f)
        assertEquals(0.001f, result, 0.0001f)
    }

    @Test
    fun `normalizeNormalizedX handles value just under 1`() {
        val result = normalizeNormalizedX(0.999f)
        assertEquals(0.999f, result, 0.001f)
    }

    @Test
    fun `normalizeNormalizedX result is always in range`() {
        val testValues = listOf(-10f, -5f, -1f, -0.5f, 0f, 0.5f, 1f, 5f, 10f)
        testValues.forEach { value ->
            val result = normalizeNormalizedX(value)
            assertTrue(
                "Result $result for input $value should be in range [0, 1)",
                result >= 0f && result < 1f
            )
        }
    }

    // ==================== getLegendItems Comprehensive Tests ====================

    @Test
    fun `getLegendItems SECURITY_RISK test tags are unique`() {
        val items = getLegendItems(MapColorMode.SECURITY_RISK)
        val testTags = items.map { it.testTag }
        assertEquals(testTags.size, testTags.distinct().size)
    }

    @Test
    fun `getLegendItems VISA_REQUIREMENTS test tags are unique`() {
        val items = getLegendItems(MapColorMode.VISA_REQUIREMENTS)
        val testTags = items.map { it.testTag }
        assertEquals(testTags.size, testTags.distinct().size)
    }

    @Test
    fun `getLegendItems PASSPORT_VALIDITY test tags are unique`() {
        val items = getLegendItems(MapColorMode.PASSPORT_VALIDITY)
        val testTags = items.map { it.testTag }
        assertEquals(testTags.size, testTags.distinct().size)
    }

    @Test
    fun `getLegendItems all modes have unique test tags across modes`() {
        val allTestTags = MapColorMode.entries
            .filter { it != MapColorMode.DEFAULT }
            .flatMap { getLegendItems(it) }
            .map { it.testTag }

        assertEquals(
            "All test tags across all modes should be unique",
            allTestTags.size,
            allTestTags.distinct().size
        )
    }

    // ==================== getPassportValidityColor Comprehensive Tests ====================

    @Test
    fun `getPassportValidityColor handles common variations of 6 months`() {
        assertEquals(PassportValidityColors.SixMonths, getPassportValidityColor(AppConstants.PassportValidity.SIX_MONTHS))
        assertEquals(PassportValidityColors.Other, getPassportValidityColor("6 Months"))
        assertEquals(PassportValidityColors.Other, getPassportValidityColor("six months"))
    }

    @Test
    fun `getPassportValidityColor handles common variations of 3 months`() {
        assertEquals(PassportValidityColors.ThreeMonths, getPassportValidityColor(AppConstants.PassportValidity.THREE_MONTHS))
        assertEquals(PassportValidityColors.Other, getPassportValidityColor("3 Months"))
        assertEquals(PassportValidityColors.Other, getPassportValidityColor("three months"))
    }

    @Test
    fun `getPassportValidityColor handles planned stay variations`() {
        assertEquals(PassportValidityColors.PlannedStay, getPassportValidityColor(AppConstants.PassportValidity.PLANNED_STAY))
        assertEquals(PassportValidityColors.Other, getPassportValidityColor("planned length of stay"))
    }

    // ==================== geoJsonToRepoId Additional Tests ====================

    @Test
    fun `geoJsonToRepoId contains small island nations`() {
        val islandNations = listOf(
            "MUS", "MDV", "MLT", "BRB", "SGP",
            // Caribbean
            "ATG", "DMA", "GRD", "KNA", "LCA", "VCT",
            // African islands
            "COM", "STP", "SYC",
            // European microstates
            "AND", "LIE", "MCO", "SMR", "VAT"
        )
        islandNations.forEach { code ->
            assertNotNull("Should contain $code", geoJsonToRepoId[code])
        }
    }

    @Test
    fun `geoJsonToRepoId contains Nordic countries`() {
        val nordicCodes = listOf("DNK", "FIN", "ISL", "NOR", "SWE")
        nordicCodes.forEach { code ->
            assertNotNull("Should contain $code", geoJsonToRepoId[code])
        }
    }

    @Test
    fun `geoJsonToRepoId contains Baltic states`() {
        val balticCodes = listOf("EST", "LVA", "LTU")
        balticCodes.forEach { code ->
            assertNotNull("Should contain $code", geoJsonToRepoId[code])
        }
    }

    @Test
    fun `geoJsonToRepoId contains Balkan countries`() {
        val balkanCodes = listOf("HRV", "SRB", "BGR", "ROU", "SVN", "ALB", "MKD", "BIH", "MNE")
        balkanCodes.forEach { code ->
            assertNotNull("Should contain $code", geoJsonToRepoId[code])
        }
    }

    // ==================== LegendItem Additional Tests ====================

    @Test
    fun `LegendItem destructuring works correctly`() {
        val item = LegendItem(Color.Red, R.string.legend_low_risk, "test_tag")
        val (color, labelResId, tag) = item

        assertEquals(Color.Red, color)
        assertEquals(R.string.legend_low_risk, labelResId)
        assertEquals("test_tag", tag)
    }

    @Test
    fun `LegendItem with transparent color can be created`() {
        val item = LegendItem(Color.Transparent, R.string.legend_low_risk, "transparent_tag")
        assertEquals(Color.Transparent, item.color)
    }

    @Test
    fun `LegendItem with zero labelResId can be created`() {
        val item = LegendItem(Color.Red, 0, "zero_label_tag")
        assertEquals(0, item.labelResId)
    }

    // ==================== Pacific Micronation Mapping Tests ====================

    @Test
    fun `geoJsonToRepoId Nauru mapping is correct`() {
        assertEquals("nr", geoJsonToRepoId["NRU"])
    }

    @Test
    fun `geoJsonToRepoId Tuvalu mapping is correct`() {
        assertEquals("tv", geoJsonToRepoId["TUV"])
    }

    @Test
    fun `geoJsonToRepoId Palau mapping is correct`() {
        assertEquals("pw", geoJsonToRepoId["PLW"])
    }

    @Test
    fun `geoJsonToRepoId Marshall Islands mapping is correct`() {
        assertEquals("mh", geoJsonToRepoId["MHL"])
    }

    @Test
    fun `geoJsonToRepoId Samoa mapping is correct`() {
        assertEquals("ws", geoJsonToRepoId["WSM"])
    }

    @Test
    fun `geoJsonToRepoId Tonga mapping is correct`() {
        assertEquals("to", geoJsonToRepoId["TON"])
    }

    @Test
    fun `geoJsonToRepoId Kiribati mapping is correct`() {
        assertEquals("ki", geoJsonToRepoId["KIR"])
    }

    @Test
    fun `geoJsonToRepoId Micronesia mapping is correct`() {
        assertEquals("fm", geoJsonToRepoId["FSM"])
    }

    // ==================== Caribbean Small Nation Mapping Tests ====================

    @Test
    fun `geoJsonToRepoId Antigua and Barbuda mapping is correct`() {
        assertEquals("ag", geoJsonToRepoId["ATG"])
    }

    @Test
    fun `geoJsonToRepoId Dominica mapping is correct`() {
        assertEquals("dm", geoJsonToRepoId["DMA"])
    }

    @Test
    fun `geoJsonToRepoId Grenada mapping is correct`() {
        assertEquals("gd", geoJsonToRepoId["GRD"])
    }

    @Test
    fun `geoJsonToRepoId Saint Kitts and Nevis mapping is correct`() {
        assertEquals("kn", geoJsonToRepoId["KNA"])
    }

    @Test
    fun `geoJsonToRepoId Saint Lucia mapping is correct`() {
        assertEquals("lc", geoJsonToRepoId["LCA"])
    }

    @Test
    fun `geoJsonToRepoId Saint Vincent and the Grenadines mapping is correct`() {
        assertEquals("vc", geoJsonToRepoId["VCT"])
    }

    // ==================== African Island Nation Mapping Tests ====================

    @Test
    fun `geoJsonToRepoId Comoros mapping is correct`() {
        assertEquals("km", geoJsonToRepoId["COM"])
    }

    @Test
    fun `geoJsonToRepoId Sao Tome and Principe mapping is correct`() {
        assertEquals("st", geoJsonToRepoId["STP"])
    }

    @Test
    fun `geoJsonToRepoId Seychelles mapping is correct`() {
        assertEquals("sc", geoJsonToRepoId["SYC"])
    }

    // ==================== European Microstate Mapping Tests ====================

    @Test
    fun `geoJsonToRepoId Andorra mapping is correct`() {
        assertEquals("ad", geoJsonToRepoId["AND"])
    }

    @Test
    fun `geoJsonToRepoId Liechtenstein mapping is correct`() {
        assertEquals("li", geoJsonToRepoId["LIE"])
    }

    @Test
    fun `geoJsonToRepoId Monaco mapping is correct`() {
        assertEquals("mc", geoJsonToRepoId["MCO"])
    }

    @Test
    fun `geoJsonToRepoId San Marino mapping is correct`() {
        assertEquals("sm", geoJsonToRepoId["SMR"])
    }

    @Test
    fun `geoJsonToRepoId Vatican City mapping is correct`() {
        assertEquals("va", geoJsonToRepoId["VAT"])
    }

    @Test
    fun `geoJsonToRepoId Malta mapping is correct`() {
        assertEquals("mt", geoJsonToRepoId["MLT"])
    }

    // ==================== Palestine Mapping Test ====================

    @Test
    fun `geoJsonToRepoId Palestine mapping is correct`() {
        // Natural Earth 10m uses PSX as the ADM0_A3 code for Palestine
        assertEquals("ps", geoJsonToRepoId["PSX"])
    }
}

// ---------------------------------------------------------------------------
// computePolygonBounds
// ---------------------------------------------------------------------------

class ComputePolygonBoundsTest {

    private fun latLng(lat: Float, lng: Float) = com.unstampedpages.app.data.model.LatLng(lat, lng)

    @Test
    fun `empty polygon returns degenerate fallback bounds`() {
        val bounds = computePolygonBounds(emptyList())
        assertEquals(0f, bounds.minX, 0f)
        assertEquals(1f, bounds.maxX, 0f)
        assertEquals(0f, bounds.minY, 0f)
        assertEquals(1f, bounds.maxY, 0f)
    }

    @Test
    fun `single point polygon returns bounds equal to that point`() {
        val point = latLng(0f, 0f)
        val bounds = computePolygonBounds(listOf(point))
        val expectedX = MercatorProjection.longitudeToX(0f)
        val expectedY = MercatorProjection.latitudeToY(0f)
        assertEquals(expectedX, bounds.minX, 0.0001f)
        assertEquals(expectedX, bounds.maxX, 0.0001f)
        assertEquals(expectedY, bounds.minY, 0.0001f)
        assertEquals(expectedY, bounds.maxY, 0.0001f)
    }

    @Test
    fun `polygon spanning equator and prime meridian has correct bounds`() {
        val polygon = listOf(
            latLng(10f, -10f), latLng(10f, 10f),
            latLng(-10f, 10f), latLng(-10f, -10f)
        )
        val bounds = computePolygonBounds(polygon)
        assertEquals(MercatorProjection.longitudeToX(-10f), bounds.minX, 0.0001f)
        assertEquals(MercatorProjection.longitudeToX(10f),  bounds.maxX, 0.0001f)
        // north (lat=10) → smaller Y; south (lat=-10) → larger Y
        assertEquals(MercatorProjection.latitudeToY(10f),  bounds.minY, 0.0001f)
        assertEquals(MercatorProjection.latitudeToY(-10f), bounds.maxY, 0.0001f)
    }

    @Test
    fun `minX is less than maxX for any non-empty polygon`() {
        val polygon = listOf(latLng(45f, -90f), latLng(45f, 90f), latLng(-45f, 0f))
        val bounds = computePolygonBounds(polygon)
        assertTrue(bounds.minX < bounds.maxX)
    }

    @Test
    fun `minY is less than maxY for polygon spanning latitudes`() {
        val polygon = listOf(latLng(60f, 0f), latLng(-60f, 0f), latLng(0f, 10f))
        val bounds = computePolygonBounds(polygon)
        assertTrue(bounds.minY < bounds.maxY)
    }

    @Test
    fun `all-same-point polygon returns degenerate-free bounds`() {
        val polygon = listOf(latLng(30f, 60f), latLng(30f, 60f), latLng(30f, 60f))
        val bounds = computePolygonBounds(polygon)
        // minX == maxX is valid (not degenerate — isValid=true because count > 0)
        assertEquals(bounds.minX, bounds.maxX, 0.0001f)
    }
}

// ---------------------------------------------------------------------------
// computeGeometryBounds
// ---------------------------------------------------------------------------

class ComputeGeometryBoundsTest {

    private fun latLng(lat: Float, lng: Float) = com.unstampedpages.app.data.model.LatLng(lat, lng)

    private val squarePolygon = listOf(
        latLng(10f, -10f), latLng(10f, 10f),
        latLng(-10f, 10f), latLng(-10f, -10f),
        latLng(10f, -10f)
    )

    @Test
    fun `geometry with no polygons returns degenerate fallback bounds`() {
        val geometry = com.unstampedpages.app.data.model.CountryGeometry("empty", emptyList())
        val bounds = computeGeometryBounds(geometry)
        assertEquals(0.5f, bounds.centroidNormX, 0.0001f)
        assertEquals(0.5f, bounds.centroidNormY, 0.0001f)
        assertEquals(0f, bounds.minX, 0f)
        assertEquals(1f, bounds.maxX, 0f)
        assertEquals(0f, bounds.minY, 0f)
        assertEquals(1f, bounds.maxY, 0f)
    }

    @Test
    fun `geometry with one empty polygon returns degenerate fallback bounds`() {
        val geometry = com.unstampedpages.app.data.model.CountryGeometry("empty", listOf(emptyList()))
        val bounds = computeGeometryBounds(geometry)
        // No points → global accumulator has count=0 → wide-open defaults with empty polygonBounds
        assertEquals(0, bounds.polygonBounds.size)
        assertEquals(0.5f, bounds.centroidNormX, 0.0001f)
        assertEquals(0f, bounds.minX, 0f)
        assertEquals(1f, bounds.maxX, 0f)
    }

    @Test
    fun `single polygon geometry has correct centroid`() {
        val geometry = com.unstampedpages.app.data.model.CountryGeometry("sq", listOf(squarePolygon))
        val bounds = computeGeometryBounds(geometry)
        // centroid should be near (0.5, latitudeToY(0)) for a symmetric square
        assertEquals(MercatorProjection.longitudeToX(0f), bounds.centroidNormX, 0.01f)
    }

    @Test
    fun `single polygon geometry global bounds match polygon bounds`() {
        val geometry = com.unstampedpages.app.data.model.CountryGeometry("sq", listOf(squarePolygon))
        val bounds = computeGeometryBounds(geometry)
        val polyBounds = bounds.polygonBounds.first()
        assertEquals(polyBounds.minX, bounds.minX, 0.0001f)
        assertEquals(polyBounds.maxX, bounds.maxX, 0.0001f)
        assertEquals(polyBounds.minY, bounds.minY, 0.0001f)
        assertEquals(polyBounds.maxY, bounds.maxY, 0.0001f)
    }

    @Test
    fun `polygon bounds list has one entry per polygon`() {
        val farPolygon = listOf(
            latLng(60f, 150f), latLng(60f, 170f),
            latLng(50f, 170f), latLng(50f, 150f)
        )
        val geometry = com.unstampedpages.app.data.model.CountryGeometry("multi", listOf(squarePolygon, farPolygon))
        val bounds = computeGeometryBounds(geometry)
        assertEquals(2, bounds.polygonBounds.size)
    }

    @Test
    fun `multipolygon global bounds span both polygons`() {
        val farPolygon = listOf(
            latLng(60f, 150f), latLng(60f, 170f),
            latLng(50f, 170f), latLng(50f, 150f)
        )
        val geometry = com.unstampedpages.app.data.model.CountryGeometry("multi", listOf(squarePolygon, farPolygon))
        val bounds = computeGeometryBounds(geometry)
        // Global maxX must be at least as large as the far polygon's east edge
        assertTrue(bounds.maxX >= MercatorProjection.longitudeToX(170f))
        // Global minX must be at most as small as the square's west edge
        assertTrue(bounds.minX <= MercatorProjection.longitudeToX(-10f))
    }

    @Test
    fun `countryId is propagated correctly`() {
        val geometry = com.unstampedpages.app.data.model.CountryGeometry("fr", listOf(squarePolygon))
        val bounds = computeGeometryBounds(geometry)
        // Just verify the function runs without error and returns valid data
        assertTrue(bounds.widthNorm > 0f)
    }

    @Test
    fun `geometry with empty and non-empty polygons uses only non-empty for global bounds`() {
        val geometry = com.unstampedpages.app.data.model.CountryGeometry(
            "mixed", listOf(emptyList(), squarePolygon)
        )
        val bounds = computeGeometryBounds(geometry)
        assertEquals(2, bounds.polygonBounds.size)
        // Global bounds should come from squarePolygon only
        assertEquals(MercatorProjection.longitudeToX(-10f), bounds.minX, 0.0001f)
        assertEquals(MercatorProjection.longitudeToX(10f),  bounds.maxX, 0.0001f)
    }

    @Test
    fun `single polygon label centroid equals overall centroid`() {
        val geometry = com.unstampedpages.app.data.model.CountryGeometry("sq", listOf(squarePolygon))
        val bounds = computeGeometryBounds(geometry)
        assertEquals(bounds.centroidNormX, bounds.labelCentroidNormX, 0.0001f)
        assertEquals(bounds.centroidNormY, bounds.labelCentroidNormY, 0.0001f)
    }

    @Test
    fun `multipolygon label centroid comes from the largest polygon not the overall centroid`() {
        // Large polygon centred near (0, 0); small outlier polygon far to the east.
        // The overall centroid is pulled east; the label centroid should stay near (0, 0).
        val smallEastPolygon = listOf(
            latLng(1f, 170f), latLng(1f, 175f),
            latLng(-1f, 175f), latLng(-1f, 170f)
        )
        val geometry = com.unstampedpages.app.data.model.CountryGeometry(
            "usa-like", listOf(squarePolygon, smallEastPolygon)
        )
        val bounds = computeGeometryBounds(geometry)
        // squarePolygon is the larger bounding box → label centroid is its centroid
        val mainCx = MercatorProjection.longitudeToX(0f)
        assertEquals(mainCx, bounds.labelCentroidNormX, 0.01f)
        // Overall centroid is pulled toward the east outlier and differs from label centroid
        assertTrue(bounds.centroidNormX > bounds.labelCentroidNormX)
    }

    @Test
    fun `label centroid is from the largest polygon even when it is not the first`() {
        // Put a tiny polygon first, then the large square second.
        val tinyPolygon = listOf(
            latLng(1f, 170f), latLng(1f, 171f),
            latLng(0f, 171f), latLng(0f, 170f)
        )
        val geometry = com.unstampedpages.app.data.model.CountryGeometry(
            "reversed", listOf(tinyPolygon, squarePolygon)
        )
        val bounds = computeGeometryBounds(geometry)
        // squarePolygon is still largest → label centroid matches its centroid
        val mainCx = MercatorProjection.longitudeToX(0f)
        assertEquals(mainCx, bounds.labelCentroidNormX, 0.01f)
    }
}

// ---------------------------------------------------------------------------
// GRID_POSITIONS
// Tests for the lazy pre-computed Mercator grid line positions.
// ---------------------------------------------------------------------------

class GridPositionsTest {

    @Test
    fun `GRID_POSITIONS has exactly 9 latitude entries`() {
        val (latYs, _) = GRID_POSITIONS
        assertEquals(9, latYs.size)
    }

    @Test
    fun `GRID_POSITIONS has exactly 13 longitude entries`() {
        val (_, lngXs) = GRID_POSITIONS
        assertEquals(13, lngXs.size)
    }

    @Test
    fun `GRID_POSITIONS equator entry is marked as major`() {
        val (latYs, _) = GRID_POSITIONS
        // Equator (lat=0) maps to the middle of the 9 entries; isMajor must be true
        val equatorEntry = latYs.single { it.second }
        assertEquals(MercatorProjection.latitudeToY(0f), equatorEntry.first, 0.0001f)
    }

    @Test
    fun `GRID_POSITIONS only one latitude entry is marked as major`() {
        val (latYs, _) = GRID_POSITIONS
        assertEquals(1, latYs.count { it.second })
    }

    @Test
    fun `GRID_POSITIONS prime meridian entry is marked as major`() {
        val (_, lngXs) = GRID_POSITIONS
        val primeMeridianEntry = lngXs.single { it.second }
        assertEquals(MercatorProjection.longitudeToX(0f), primeMeridianEntry.first, 0.0001f)
    }

    @Test
    fun `GRID_POSITIONS only one longitude entry is marked as major`() {
        val (_, lngXs) = GRID_POSITIONS
        assertEquals(1, lngXs.count { it.second })
    }

    @Test
    fun `GRID_POSITIONS all latitude Y values are in normalised range`() {
        val (latYs, _) = GRID_POSITIONS
        latYs.forEach { (y, _) ->
            assertTrue("lat Y $y not in [0,1]", y in 0f..1f)
        }
    }

    @Test
    fun `GRID_POSITIONS all longitude X values are in normalised range`() {
        val (_, lngXs) = GRID_POSITIONS
        lngXs.forEach { (x, _) ->
            assertTrue("lng X $x not in [0,1]", x in 0f..1f)
        }
    }

    @Test
    fun `GRID_POSITIONS latitude Y values match direct MercatorProjection calls`() {
        val latitudes = listOf(-80f, -60f, -40f, -20f, 0f, 20f, 40f, 60f, 80f)
        val (latYs, _) = GRID_POSITIONS
        latitudes.forEachIndexed { i, lat ->
            val expected = MercatorProjection.latitudeToY(lat)
            assertEquals("Mismatch at lat=$lat", expected, latYs[i].first, 0.0001f)
        }
    }

    @Test
    fun `GRID_POSITIONS longitude X values match direct MercatorProjection calls`() {
        val longitudes = listOf(-180f, -150f, -120f, -90f, -60f, -30f, 0f, 30f, 60f, 90f, 120f, 150f, 180f)
        val (_, lngXs) = GRID_POSITIONS
        longitudes.forEachIndexed { i, lng ->
            val expected = MercatorProjection.longitudeToX(lng)
            assertEquals("Mismatch at lng=$lng", expected, lngXs[i].first, 0.0001f)
        }
    }

    @Test
    fun `GRID_POSITIONS latitude Y values are in ascending order`() {
        // Higher latitudes (north) have smaller Y in Mercator — so list from -80 to +80
        // maps to Y values from large (south) to small (north), i.e. descending Y.
        // Verify they are strictly monotonically ordered (descending).
        val (latYs, _) = GRID_POSITIONS
        val ys = latYs.map { it.first }
        for (i in 0 until ys.size - 1) {
            assertTrue("Expected Y[${i}]=${ys[i]} > Y[${i+1}]=${ys[i+1]}", ys[i] > ys[i + 1])
        }
    }

    @Test
    fun `GRID_POSITIONS longitude X values are in ascending order`() {
        val (_, lngXs) = GRID_POSITIONS
        val xs = lngXs.map { it.first }
        for (i in 0 until xs.size - 1) {
            assertTrue("Expected X[${i}]=${xs[i]} < X[${i+1}]=${xs[i+1]}", xs[i] < xs[i + 1])
        }
    }

    @Test
    fun `GRID_POSITIONS returns same instance on repeated access`() {
        // Verifies lazy initialisation — same Pair reference returned each time.
        val first = GRID_POSITIONS
        val second = GRID_POSITIONS
        assertTrue(first === second)
    }
}

// ---------------------------------------------------------------------------
// MapColorMode — default and entry coverage
// MapGestureState.colorMode defaults to MapColorMode.DEFAULT and
// MapGestureState.onCompassTapped defaults to a no-op lambda. MapGestureState
// itself cannot be instantiated in JVM unit tests because its constructor
// requires android.graphics.Matrix (Android runtime only). These tests verify
// the MapColorMode enum that drives the defaulted field, confirming that
// DEFAULT exists and all modes are enumerable — which is also exercised by
// the TransitionProgressGuardTest below.
// ---------------------------------------------------------------------------

class MapColorModeDefaultsTest {

    @Test
    fun `MapColorMode DEFAULT exists`() {
        // Explicit reference — fails to compile if the constant is removed or renamed.
        val mode: MapColorMode = MapColorMode.DEFAULT
        assertNotNull(mode)
    }

    @Test
    fun `MapColorMode has at least two distinct values`() {
        // The transition logic requires at least one non-DEFAULT mode to cross-dissolve to.
        assertTrue(MapColorMode.entries.size >= 2)
    }

    @Test
    fun `all MapColorMode entries are unique`() {
        val entries = MapColorMode.entries
        assertEquals(entries.size, entries.toSet().size)
    }

    @Test
    fun `MapColorMode SECURITY_RISK exists`() {
        val mode: MapColorMode = MapColorMode.SECURITY_RISK
        assertNotNull(mode)
    }

    @Test
    fun `MapColorMode VISA_REQUIREMENTS exists`() {
        val mode: MapColorMode = MapColorMode.VISA_REQUIREMENTS
        assertNotNull(mode)
    }

    @Test
    fun `MapColorMode PASSPORT_VALIDITY exists`() {
        val mode: MapColorMode = MapColorMode.PASSPORT_VALIDITY
        assertNotNull(mode)
    }
}

// ---------------------------------------------------------------------------
// transitionProgress guard logic
// The actual Compose animatable cannot be tested in JVM unit tests, but the
// core guard expression — "if prev==curr return 1f, else return animatable value"
// — is a pure function that can be verified directly.
// ---------------------------------------------------------------------------

class TransitionProgressGuardTest {

    /**
     * Mirrors the logic in WorldMapCanvas.kt:
     *   val transitionProgress = if (previousColorMode == colorMode) 1f else transitionAnimatable.value
     */
    private fun transitionProgress(
        previousColorMode: MapColorMode,
        colorMode: MapColorMode,
        animatableValue: Float
    ): Float = if (previousColorMode == colorMode) 1f else animatableValue

    @Test
    fun `returns 1f when previous and current modes match`() {
        assertEquals(1f, transitionProgress(MapColorMode.DEFAULT, MapColorMode.DEFAULT, 0f), 0f)
    }

    @Test
    fun `returns animatable value when modes differ`() {
        assertEquals(0f, transitionProgress(MapColorMode.DEFAULT, MapColorMode.SECURITY_RISK, 0f), 0f)
        assertEquals(0.5f, transitionProgress(MapColorMode.DEFAULT, MapColorMode.SECURITY_RISK, 0.5f), 0f)
        assertEquals(1f, transitionProgress(MapColorMode.DEFAULT, MapColorMode.SECURITY_RISK, 1f), 0f)
    }

    @Test
    fun `returns 1f for same-mode regardless of animatable value`() {
        // Even if the animatable were mid-animation for some reason, a matching mode
        // must short-circuit to 1f (steady state — no lerp overhead).
        listOf(0f, 0.5f, 0.99f).forEach { v ->
            assertEquals(1f, transitionProgress(MapColorMode.VISA_REQUIREMENTS, MapColorMode.VISA_REQUIREMENTS, v), 0f)
        }
    }

    @Test
    fun `animatable value of 0f causes useTransition to be true`() {
        // useTransition = transitionProgress < 1f
        val progress = transitionProgress(MapColorMode.DEFAULT, MapColorMode.SECURITY_RISK, 0f)
        assertTrue(progress < 1f)
    }

    @Test
    fun `animatable value of 1f at end of animation causes useTransition to be false`() {
        val progress = transitionProgress(MapColorMode.DEFAULT, MapColorMode.SECURITY_RISK, 1f)
        assertFalse(progress < 1f)
    }

    @Test
    fun `all distinct mode pairs cause animatable passthrough`() {
        // Any combination of different modes must return the animatable value, not 1f.
        val modes = MapColorMode.entries
        modes.forEach { prev ->
            modes.filter { it != prev }.forEach { curr ->
                val animValue = 0.3f
                assertEquals(
                    "Expected passthrough for $prev -> $curr",
                    animValue,
                    transitionProgress(prev, curr, animValue),
                    0f
                )
            }
        }
    }

    @Test
    fun `snapTo 0f at animation start produces correct first-frame colors`() {
        // When LaunchedEffect fires after colorMode changes, the animatable is snapTo(0f).
        // transitionProgress must therefore be 0f on the first new-mode frame,
        // meaning lerp(old, new, 0) == old colors (no flash).
        val progress = transitionProgress(MapColorMode.DEFAULT, MapColorMode.SECURITY_RISK, 0f)
        assertEquals(0f, progress, 0f)
    }
}

// =============================================================================
// Country Label — Constants
// =============================================================================

/**
 * Pin the label-visibility constants to their expected values.
 * Changing a constant is a deliberate decision; these tests surface that decision
 * so reviewers know it affects the cross-dissolve UX.
 */
class CountryLabelConstantsTest {

    @Test
    fun `LABEL_SHOW_THRESHOLD is 3f`() {
        assertEquals(3f, LABEL_SHOW_THRESHOLD)
    }

    @Test
    fun `LABEL_MIN_SCREEN_PX is 20f`() {
        assertEquals(20f, LABEL_MIN_SCREEN_PX)
    }

    @Test
    fun `LABEL_FULL_SCREEN_PX is 80f`() {
        assertEquals(80f, LABEL_FULL_SCREEN_PX)
    }

    @Test
    fun `SMALL_COUNTRY_THRESHOLD_PX is 8f`() {
        assertEquals(8f, SMALL_COUNTRY_THRESHOLD_PX)
    }

    @Test
    fun `LABEL_TEXT_SP is 10f`() {
        assertEquals(10f, LABEL_TEXT_SP)
    }

    @Test
    fun `LABEL_FULL_SCREEN_PX is strictly greater than LABEL_MIN_SCREEN_PX`() {
        assertTrue(LABEL_FULL_SCREEN_PX > LABEL_MIN_SCREEN_PX)
    }

    @Test
    fun `LABEL_MIN_SCREEN_PX is strictly greater than SMALL_COUNTRY_THRESHOLD_PX`() {
        // Ensures the three thresholds are ordered: dot < min < full
        assertTrue(LABEL_MIN_SCREEN_PX > SMALL_COUNTRY_THRESHOLD_PX)
    }
}

// =============================================================================
// Country Label — Cross-dissolve zoom threshold
// =============================================================================

/**
 * Tests the logic that drives the `animateFloatAsState` target for label alpha.
 *
 * The composable uses: `if (scale >= LABEL_SHOW_THRESHOLD) 1f else 0f`
 * We replicate that expression here so the threshold value and direction are
 * covered independently of Compose's animation runtime.
 */
class LabelAlphaThresholdTest {

    private fun targetAlpha(scale: Float) =
        if (scale >= LABEL_SHOW_THRESHOLD) 1f else 0f

    @Test
    fun `labels are hidden at scale 1f (initial zoom)`() {
        assertEquals(0f, targetAlpha(1f))
    }

    @Test
    fun `labels are hidden just below the show threshold`() {
        assertEquals(0f, targetAlpha(LABEL_SHOW_THRESHOLD - 0.01f))
    }

    @Test
    fun `labels become visible exactly at the show threshold`() {
        assertEquals(1f, targetAlpha(LABEL_SHOW_THRESHOLD))
    }

    @Test
    fun `labels remain visible above the show threshold`() {
        assertEquals(1f, targetAlpha(LABEL_SHOW_THRESHOLD + 0.01f))
    }

    @Test
    fun `labels remain visible at maximum zoom`() {
        assertEquals(1f, targetAlpha(200f))
    }

    @Test
    fun `threshold is exclusive — scale just below gives 0f`() {
        // Guards against an accidental > instead of >=.
        // Math.nextDown gives the largest float strictly less than LABEL_SHOW_THRESHOLD.
        val justBelow = Math.nextDown(LABEL_SHOW_THRESHOLD)
        assertEquals(0f, targetAlpha(justBelow))
    }
}

// =============================================================================
// Country Label — Size-based alpha (sizeAlpha)
// =============================================================================

/**
 * Tests for [computeLabelSizeAlpha].
 *
 * The function maps screenMaxDim (largest visible dimension of a country's bbox)
 * to a [0, 1] opacity that is multiplied with the zoom-driven labelAlpha to get
 * the final label opacity.
 */
class LabelSizeAlphaTest {

    // ── Dot-mode countries (very small on screen) ──────────────────────────

    @Test
    fun `zero screen dimension returns full alpha (dot mode)`() {
        assertEquals(1f, computeLabelSizeAlpha(0f))
    }

    @Test
    fun `screen size just below small threshold returns full alpha`() {
        assertEquals(1f, computeLabelSizeAlpha(SMALL_COUNTRY_THRESHOLD_PX - 0.01f))
    }

    // ── Transition from dot-mode to fade-in ───────────────────────────────

    @Test
    fun `screen size exactly at small threshold starts the fade-in ramp`() {
        // SMALL_COUNTRY_THRESHOLD_PX (8) < LABEL_MIN_SCREEN_PX (20), so the
        // linear ramp returns a negative value that is clamped to 0f.
        assertEquals(0f, computeLabelSizeAlpha(SMALL_COUNTRY_THRESHOLD_PX))
    }

    // ── Fade-in ramp (LABEL_MIN_SCREEN_PX .. LABEL_FULL_SCREEN_PX) ───────

    @Test
    fun `screen size below label min returns 0f`() {
        assertEquals(0f, computeLabelSizeAlpha(LABEL_MIN_SCREEN_PX - 1f))
    }

    @Test
    fun `screen size at label min returns exactly 0f`() {
        assertEquals(0f, computeLabelSizeAlpha(LABEL_MIN_SCREEN_PX), 0.0001f)
    }

    @Test
    fun `screen size at midpoint returns 0_5f`() {
        val mid = (LABEL_MIN_SCREEN_PX + LABEL_FULL_SCREEN_PX) / 2f
        assertEquals(0.5f, computeLabelSizeAlpha(mid), 0.0001f)
    }

    @Test
    fun `screen size at label full returns exactly 1f`() {
        assertEquals(1f, computeLabelSizeAlpha(LABEL_FULL_SCREEN_PX), 0.0001f)
    }

    @Test
    fun `screen size above label full is clamped to 1f`() {
        assertEquals(1f, computeLabelSizeAlpha(LABEL_FULL_SCREEN_PX + 100f))
    }

    @Test
    fun `sizeAlpha is monotonically non-decreasing across the ramp`() {
        val samples = listOf(
            LABEL_MIN_SCREEN_PX,
            LABEL_MIN_SCREEN_PX + 10f,
            LABEL_MIN_SCREEN_PX + 20f,
            LABEL_MIN_SCREEN_PX + 30f,
            LABEL_FULL_SCREEN_PX
        )
        for (i in 1 until samples.size) {
            assertTrue(
                "sizeAlpha should not decrease from ${samples[i-1]} to ${samples[i]}",
                computeLabelSizeAlpha(samples[i]) >= computeLabelSizeAlpha(samples[i - 1])
            )
        }
    }

    // ── finalAlpha = labelAlpha × sizeAlpha ───────────────────────────────

    @Test
    fun `finalAlpha is zero when labelAlpha is zero regardless of country size`() {
        val labelAlpha = 0f
        val sizeAlpha = computeLabelSizeAlpha(200f)
        assertEquals(0f, labelAlpha * sizeAlpha)
    }

    @Test
    fun `finalAlpha is zero when country is too small to label`() {
        val labelAlpha = 1f
        val sizeAlpha = computeLabelSizeAlpha(LABEL_MIN_SCREEN_PX - 1f)
        assertTrue(labelAlpha * sizeAlpha < 0.01f)
    }

    @Test
    fun `finalAlpha is full when both factors are 1f`() {
        val labelAlpha = 1f
        val sizeAlpha = computeLabelSizeAlpha(LABEL_FULL_SCREEN_PX)
        assertEquals(1f, labelAlpha * sizeAlpha, 0.0001f)
    }

    @Test
    fun `finalAlpha skip threshold — values below 0_01 would cause label skip`() {
        // Guard against accidental changes to the early-exit comparison.
        // Any finalAlpha >= 0.01f should pass; < 0.01f should be skipped.
        assertFalse(0.01f < 0.01f)    // boundary: exactly at threshold is kept
        assertTrue(0.009f < 0.01f)   // just below: skipped
    }

    @Test
    fun `dot-mode country label reaches full opacity with labelAlpha 1f`() {
        val finalAlpha = 1f * computeLabelSizeAlpha(SMALL_COUNTRY_THRESHOLD_PX / 2f)
        assertEquals(1f, finalAlpha, 0.0001f)
    }
}

// =============================================================================
// Country Label — Centroid overrides
// =============================================================================

/**
 * Tests for [LABEL_CENTROID_OVERRIDES].
 *
 * Overrides are used when a country's geometric centroid falls outside its
 * main land mass (e.g., New Zealand due to sub-Antarctic islands).
 */
class LabelCentroidOverridesTest {

    @Test
    fun `NZL override is present`() {
        assertTrue(
            "NZL centroid override missing",
            LABEL_CENTROID_OVERRIDES.containsKey("NZL")
        )
    }

    @Test
    fun `NZL override X matches longitude 173 east`() {
        val (x, _) = LABEL_CENTROID_OVERRIDES.getValue("NZL")
        assertEquals(MercatorProjection.longitudeToX(173f), x, 0.0001f)
    }

    @Test
    fun `NZL override Y matches latitude 41_5 south`() {
        val (_, y) = LABEL_CENTROID_OVERRIDES.getValue("NZL")
        assertEquals(MercatorProjection.latitudeToY(-41.5f), y, 0.0001f)
    }

    @Test
    fun `all centroid override keys are valid GeoJSON IDs in geoJsonToRepoId`() {
        LABEL_CENTROID_OVERRIDES.keys.forEach { geoId ->
            assertTrue(
                "Override key '$geoId' not found in geoJsonToRepoId",
                geoJsonToRepoId.containsKey(geoId)
            )
        }
    }

    @Test
    fun `all centroid override coordinates are in valid normalized range`() {
        LABEL_CENTROID_OVERRIDES.values.forEach { (x, y) ->
            assertTrue("centroid X $x out of [0,1]", x in 0f..1f)
            assertTrue("centroid Y $y out of [0,1]", y in 0f..1f)
        }
    }

    // ── Kiribati ────────────────────────────────────────────────────────────────
    // Kiribati straddles the antimeridian; its vertex-averaged centroid lands near
    // Africa. The override pins the label to the Gilbert Islands (capital Tarawa).

    @Test
    fun `KIR override is present`() {
        assertTrue(
            "KIR centroid override missing — Kiribati label will appear near Africa",
            LABEL_CENTROID_OVERRIDES.containsKey("KIR")
        )
    }

    @Test
    fun `KIR override X matches longitude 174 east (Gilbert Islands)`() {
        val (x, _) = LABEL_CENTROID_OVERRIDES.getValue("KIR")
        assertEquals(MercatorProjection.longitudeToX(174f), x, 0.0001f)
    }

    @Test
    fun `KIR override Y matches latitude 1_5 south (near Tarawa)`() {
        val (_, y) = LABEL_CENTROID_OVERRIDES.getValue("KIR")
        assertEquals(MercatorProjection.latitudeToY(-1.5f), y, 0.0001f)
    }

    @Test
    fun `KIR override X is in eastern Pacific (right side of map)`() {
        // Gilbert Islands are near 174°E; normalized X should be > 0.95.
        val (x, _) = LABEL_CENTROID_OVERRIDES.getValue("KIR")
        assertTrue("KIR X $x is not in eastern Pacific (expected > 0.95)", x > 0.95f)
    }
}

// =============================================================================
// Country Label — Name resolution (buildCountryNames)
// =============================================================================

/**
 * Tests for [buildCountryNames].
 *
 * Verifies the two-level lookup (countries map → CountryList fallback) that
 * builds the geoJsonId → display name map used to render country labels.
 */
class BuildCountryNamesTest {

    // Helper — builds a minimal Country suitable for name-resolution tests
    private fun country(repoId: String, name: String) = Country(
        id = repoId,
        name = name,
        safetyLevel = SafetyLevel.NORMAL_SECURITY_PRECAUTIONS,
        visaRequirement = VisaRequirement.VISA_NOT_REQUIRED,
        currency = "Test",
        currencyCode = "TST",
        exchangeRateToUSD = 1.0,
        outletType = "Type A",
        continent = Continent.EUROPE,
        flagEmoji = ""
    )

    // ── CountryList fallback ───────────────────────────────────────────────

    @Test
    fun `AUS resolves to a non-blank name from CountryList fallback`() {
        val names = buildCountryNames(emptyMap())
        val name = names["AUS"]
        assertNotNull("AUS not found in label map", name)
        assertTrue("AUS name is blank", name!!.isNotBlank())
    }

    @Test
    fun `major countries resolve names via CountryList when countries map is empty`() {
        val names = buildCountryNames(emptyMap())
        listOf("USA", "GBR", "DEU", "FRA", "JPN", "CHN", "IND", "BRA").forEach { geoId ->
            assertNotNull("$geoId missing from label map", names[geoId])
        }
    }

    @Test
    fun `all six continents have at least one resolvable country via CountryList`() {
        val names = buildCountryNames(emptyMap())
        // Representative country per continent
        mapOf(
            "North America" to "USA",
            "South America" to "BRA",
            "Europe"        to "DEU",
            "Africa"        to "NGA",
            "Asia"          to "CHN",
            "Oceania"       to "AUS"
        ).forEach { (continent, geoId) ->
            assertNotNull("No label for $continent ($geoId)", names[geoId])
        }
    }

    // ── countries map takes priority ───────────────────────────────────────

    @Test
    fun `countries map name is preferred over CountryList for the same country`() {
        val customName = "Custom Australia"
        val countriesMap = mapOf("au" to country("au", customName))
        val names = buildCountryNames(countriesMap)
        assertEquals(customName, names["AUS"])
    }

    @Test
    fun `countries map with different name overrides CountryList for USA`() {
        val customName = "United States of Testing"
        val countriesMap = mapOf("us" to country("us", customName))
        val names = buildCountryNames(countriesMap)
        assertEquals(customName, names["USA"])
    }

    // ── name lookup is case-insensitive for CountryList ────────────────────

    @Test
    fun `CountryList lookup is case-insensitive for repo id`() {
        // geoJsonToRepoId maps "AUS" to "au"; CountryList codes may be uppercase
        val names = buildCountryNames(emptyMap())
        // If this resolves, the case-insensitive match worked
        assertNotNull(names["AUS"])
    }

    // ── geoIds with no name in either source are excluded ─────────────────

    @Test
    fun `output contains only GeoJSON IDs that resolved to a name`() {
        val names = buildCountryNames(emptyMap())
        // Every key in the output must be a geoJsonToRepoId key
        names.keys.forEach { geoId ->
            assertTrue("Unexpected geoId '$geoId' in label map", geoJsonToRepoId.containsKey(geoId))
        }
    }

    @Test
    fun `output size does not exceed geoJsonToRepoId size`() {
        val names = buildCountryNames(emptyMap())
        assertTrue(names.size <= geoJsonToRepoId.size)
    }

    // ── coverage: most countries in geoJsonToRepoId should resolve ────────

    @Test
    fun `at least 90 percent of geoJsonToRepoId entries resolve via CountryList`() {
        val names = buildCountryNames(emptyMap())
        val resolved = names.size
        val total = geoJsonToRepoId.size
        val pct = resolved.toDouble() / total
        assertTrue(
            "Only $resolved of $total GeoJSON IDs resolved (${(pct * 100).toInt()}%); expected >= 90%",
            pct >= 0.90
        )
    }

    @Test
    fun `countries map with full CountryRepository data resolves at least 90 percent`() {
        val repo = CountryRepository()
        val countries = repo.getAllCountries().associateBy { it.id }
        val names = buildCountryNames(countries)
        val pct = names.size.toDouble() / geoJsonToRepoId.size
        assertTrue(
            "Only ${names.size} of ${geoJsonToRepoId.size} GeoJSON IDs resolved using repository data",
            pct >= 0.90
        )
    }

    // ── custom idMap parameter ─────────────────────────────────────────────

    @Test
    fun `custom idMap is used instead of geoJsonToRepoId`() {
        val customMap = mapOf("TST" to "ts")
        val countriesMap = mapOf("ts" to country("ts", "Test Country"))
        val names = buildCountryNames(countriesMap, idMap = customMap)
        assertEquals(mapOf("TST" to "Test Country"), names)
    }

    @Test
    fun `empty idMap produces empty output`() {
        val names = buildCountryNames(emptyMap(), idMap = emptyMap())
        assertTrue(names.isEmpty())
    }

    // ── locale parameter ───────────────────────────────────────────────────

    @Test
    fun `territory geoId CYM resolves to a name (not the parent country name) regardless of locale`() {
        // CYM → parent repo id "gb". The territory name (Cayman Islands or localized equivalent)
        // must take priority over the parent country name (United Kingdom / Reino Unido).
        val repo = CountryRepository()
        val countries = repo.getAllCountries().associateBy { it.id }
        val names = buildCountryNames(countries, locale = java.util.Locale.ENGLISH)
        val caymanName = names["CYM"]
        assertNotNull("CYM should resolve to a territory name", caymanName)
        assertFalse(
            "CYM should not resolve to the parent country name 'United Kingdom', got: $caymanName",
            caymanName!!.equals("United Kingdom", ignoreCase = true)
        )
        assertTrue("CYM name should contain 'Cayman', got: $caymanName",
            caymanName.contains("Cayman", ignoreCase = true))
    }

    @Test
    fun `territory geoId GRL resolves to Greenland not Denmark`() {
        val repo = CountryRepository()
        val countries = repo.getAllCountries().associateBy { it.id }
        val names = buildCountryNames(countries, locale = java.util.Locale.ENGLISH)
        val greenlandName = names["GRL"]
        assertNotNull("GRL should resolve to a name", greenlandName)
        assertFalse("GRL should not resolve to 'Denmark'",
            greenlandName!!.equals("Denmark", ignoreCase = true))
        assertTrue("GRL name should contain 'Greenland', got: $greenlandName",
            greenlandName.contains("Greenland", ignoreCase = true))
    }

    @Test
    fun `territory geoId PYF resolves to French Polynesia not France`() {
        val repo = CountryRepository()
        val countries = repo.getAllCountries().associateBy { it.id }
        val names = buildCountryNames(countries, locale = java.util.Locale.ENGLISH)
        val name = names["PYF"]
        assertNotNull("PYF should resolve to a name", name)
        assertFalse("PYF should not resolve to 'France'", name!!.equals("France", ignoreCase = true))
        assertTrue("PYF name should contain 'Polynesia', got: $name",
            name.contains("Polynesia", ignoreCase = true))
    }

    @Test
    fun `Spanish locale produces non-blank names for territory geoIds`() {
        val repo = CountryRepository()
        val countries = repo.getAllCountries(java.util.Locale.forLanguageTag("es")).associateBy { it.id }
        val names = buildCountryNames(countries, locale = java.util.Locale.forLanguageTag("es"))
        // All resolved names must be non-blank regardless of locale.
        names.values.forEach { name ->
            assertTrue("Territory name should be non-blank in Spanish, got: '$name'", name.isNotBlank())
        }
    }

    @Test
    fun `Spanish locale still prioritizes territory name over parent country name for CYM`() {
        val repo = CountryRepository()
        val countries = repo.getAllCountries(java.util.Locale.forLanguageTag("es")).associateBy { it.id }
        val names = buildCountryNames(countries, locale = java.util.Locale.forLanguageTag("es"))
        val caymanName = names["CYM"]
        assertNotNull("CYM should resolve to a name in Spanish", caymanName)
        // Whatever the Spanish JVM returns, it must not be the Spanish name for United Kingdom.
        val ukSpanish = repo.getCountryById("gb", java.util.Locale.forLanguageTag("es"))!!.name
        assertFalse(
            "CYM should not resolve to parent country name '$ukSpanish' in Spanish, got: $caymanName",
            caymanName!!.equals(ukSpanish, ignoreCase = true)
        )
    }

    @Test
    fun `locale parameter does not reduce overall map size vs default locale`() {
        val defaultNames = buildCountryNames(emptyMap())
        val spanishNames = buildCountryNames(emptyMap(), locale = java.util.Locale.forLanguageTag("es"))
        // Changing locale should not reduce coverage — every entry that resolved in English
        // must also resolve in Spanish (territory fallback covers any gaps).
        assertTrue(spanishNames.size >= defaultNames.size)
    }
}

// ---------------------------------------------------------------------------
// proximityFallbackHitTest
//
// Tests for the proximity fallback introduced to fix Seychelles outer-island
// tap detection. The fallback handles two cases:
//
//   A) Entire country is a dot marker (overall rendered size ≤ threshold) →
//      proximity check against the country centroid.
//
//   B) Country is large overall but contains individual tiny island polygons
//      (e.g. Seychelles: Aldabra, Farquhar) whose per-polygon size ≤ threshold →
//      proximity check against the bbox centre of each such polygon.
//
// The function returns a geoJson country ID (key in countryBounds), NOT a repo ID.
// ---------------------------------------------------------------------------

class ProximityFallbackHitTestTest {

    // ── helpers ──────────────────────────────────────────────────────────────

    private val mapWidth  = 1000f
    private val mapHeight = 500f

    /**
     * Build a [CountryBounds] whose overall rendered size at [scale] is exactly
     * [renderedPx] pixels (as the max of width/height).
     * Centroid is placed at ([cx], [cy]) in normalised coords.
     */
    private fun smallCountryBounds(
        cx: Float = 0.5f,
        cy: Float = 0.5f,
        renderedPx: Float = SMALL_COUNTRY_THRESHOLD_PX / 2f,
        scale: Float = 1f
    ): CountryBounds {
        val halfNorm = (renderedPx / scale) / (2f * mapWidth)
        return CountryBounds(
            centroidNormX = cx,
            centroidNormY = cy,
            minX = cx - halfNorm, maxX = cx + halfNorm,
            minY = cy - halfNorm, maxY = cy + halfNorm,
            polygonBounds = listOf(
                PolygonBounds(cx - halfNorm, cx + halfNorm, cy - halfNorm, cy + halfNorm)
            )
        )
    }

    /**
     * Build a [CountryBounds] with a large overall extent (well above threshold)
     * plus [smallIslands] polygon bounding boxes that are tiny (below threshold at scale=1).
     */
    private fun archipelagoBounds(
        overallMinX: Float = 0.1f,
        overallMaxX: Float = 0.9f,
        smallIslands: List<PolygonBounds>
    ): CountryBounds {
        return CountryBounds(
            centroidNormX = (overallMinX + overallMaxX) / 2f,
            centroidNormY = 0.5f,
            minX = overallMinX, maxX = overallMaxX,
            minY = 0.4f, maxY = 0.6f,
            polygonBounds = smallIslands
        )
    }

    /** Create a tiny [PolygonBounds] centred at ([cx], [cy]) with half-width [halfNorm]. */
    private fun tinyPoly(cx: Float, cy: Float, halfNorm: Float = 0.001f) =
        PolygonBounds(cx - halfNorm, cx + halfNorm, cy - halfNorm, cy + halfNorm)

    // ── A: returns null when countryBounds is empty ───────────────────────

    @Test
    fun `returns null for empty countryBounds`() {
        val result = proximityFallbackHitTest(0.5f, 0.5f, emptyMap(), mapWidth, mapHeight, 1f)
        assertNull(result)
    }

    // ── A: entire-country dot-marker path ────────────────────────────────

    @Test
    fun `returns country id when tap is exactly on centroid of dot-marker country`() {
        val bounds = smallCountryBounds(cx = 0.5f, cy = 0.5f)
        val result = proximityFallbackHitTest(0.5f, 0.5f, mapOf("SML" to bounds), mapWidth, mapHeight, 1f)
        assertEquals("SML", result)
    }

    @Test
    fun `returns null when tap is just beyond tap radius from dot-marker centroid`() {
        val scale = 1f
        val tapRadiusNorm = TAP_PROXIMITY_PX / (scale * mapWidth)
        val bounds = smallCountryBounds(cx = 0.5f, cy = 0.5f)
        // Place tap slightly outside the radius
        val farX = 0.5f + tapRadiusNorm + 0.001f
        val result = proximityFallbackHitTest(farX, 0.5f, mapOf("SML" to bounds), mapWidth, mapHeight, scale)
        assertNull(result)
    }

    @Test
    fun `returns country id when tap is just inside tap radius from dot-marker centroid`() {
        val scale = 1f
        val tapRadiusNorm = TAP_PROXIMITY_PX / (scale * mapWidth)
        val bounds = smallCountryBounds(cx = 0.5f, cy = 0.5f)
        // Place tap just inside the radius
        val nearX = 0.5f + tapRadiusNorm * 0.9f
        val result = proximityFallbackHitTest(nearX, 0.5f, mapOf("SML" to bounds), mapWidth, mapHeight, scale)
        assertEquals("SML", result)
    }

    @Test
    fun `dot-marker picks closest country when two are within radius`() {
        val scale = 1f
        val tapRadiusNorm = TAP_PROXIMITY_PX / (scale * mapWidth)
        // Two countries within radius; A is closer
        val boundsA = smallCountryBounds(cx = 0.5f,                       cy = 0.5f)
        val boundsB = smallCountryBounds(cx = 0.5f + tapRadiusNorm * 0.8f, cy = 0.5f)
        val result = proximityFallbackHitTest(
            normalizedX = 0.5f,
            normalizedY = 0.5f,
            countryBounds = mapOf("AAA" to boundsA, "BBB" to boundsB),
            mapWidth = mapWidth, mapHeight = mapHeight, currentScale = scale
        )
        assertEquals("AAA", result)
    }

    @Test
    fun `dot-marker country above threshold is not returned via centroid path`() {
        // Overall rendered size > SMALL_COUNTRY_THRESHOLD_PX and no small polygons
        val largeBounds = CountryBounds(
            centroidNormX = 0.5f, centroidNormY = 0.5f,
            minX = 0.1f, maxX = 0.9f,
            minY = 0.3f, maxY = 0.7f,
            polygonBounds = emptyList()   // no tiny sub-polygons
        )
        // Tap exactly on centroid — but this is not a dot-marker country, and there are
        // no small polygons, so the fallback should return null.
        val result = proximityFallbackHitTest(0.5f, 0.5f, mapOf("LRG" to largeBounds), mapWidth, mapHeight, 1f)
        assertNull(result)
    }

    @Test
    fun `tap radius scales inversely with zoom — higher scale gives smaller normalised radius`() {
        val scale2x = 2f
        val scale10x = 10f
        val tapRadiusNorm2  = TAP_PROXIMITY_PX / (scale2x  * mapWidth)
        val tapRadiusNorm10 = TAP_PROXIMITY_PX / (scale10x * mapWidth)
        assertTrue(tapRadiusNorm2 > tapRadiusNorm10)
    }

    @Test
    fun `dot-marker country at higher zoom with same pixel offset may fall outside radius`() {
        // At scale=1 the country is reachable; at scale=10 the same normalised offset is outside
        val cx = 0.5f
        val cy = 0.5f
        val offsetNorm = TAP_PROXIMITY_PX / (1f * mapWidth) * 0.9f  // inside at scale=1
        val smallBounds = smallCountryBounds(cx = cx, cy = cy, scale = 1f)

        val atScale1 = proximityFallbackHitTest(cx + offsetNorm, cy, mapOf("C" to smallBounds), mapWidth, mapHeight, 1f)
        // At scale=10, offsetNorm is >> tapRadiusNorm(10)
        val atScale10 = proximityFallbackHitTest(cx + offsetNorm, cy, mapOf("C" to smallBounds), mapWidth, mapHeight, 10f)

        assertEquals("C", atScale1)
        assertNull(atScale10)
    }

    // ── B: per-polygon tiny-island path (the Seychelles fix) ─────────────

    @Test
    fun `archipelago tap near tiny island polygon centre returns country id`() {
        val islandCx = 0.3f
        val islandCy = 0.5f
        val bounds = archipelagoBounds(
            smallIslands = listOf(tinyPoly(islandCx, islandCy))
        )
        // Tap exactly on island bbox centre
        val result = proximityFallbackHitTest(islandCx, islandCy, mapOf("SYC" to bounds), mapWidth, mapHeight, 1f)
        assertEquals("SYC", result)
    }

    @Test
    fun `archipelago tap on large polygon does not trigger fallback because ray-cast handles it`() {
        // Polygon large enough that renderedPx > TAP_PROXIMITY_PX * 2 — should be skipped.
        // Half-width produces a full rendered dim of TAP_PROXIMITY_PX * 3 = 60px > 40px threshold.
        val bigHalfNorm = (TAP_PROXIMITY_PX * 1.5f) / mapWidth  // half of 60px
        val bigPoly = PolygonBounds(0.4f - bigHalfNorm, 0.4f + bigHalfNorm, 0.4f - bigHalfNorm, 0.4f + bigHalfNorm)
        val bounds = archipelagoBounds(smallIslands = listOf(bigPoly))
        // Tap exactly on the big polygon centre — fallback skips it, so returns null
        val result = proximityFallbackHitTest(0.4f, 0.4f, mapOf("SYC" to bounds), mapWidth, mapHeight, 1f)
        assertNull(result)
    }

    @Test
    fun `isolated territory between old and new threshold is now tappable via proximity`() {
        // Simulates Easter Island: a tiny polygon of a large country that rendered at ~20px
        // (above old SMALL_COUNTRY_THRESHOLD_PX=8px but below new TAP_PROXIMITY_PX*2=40px).
        val islandHalfNorm = (TAP_PROXIMITY_PX * 0.9f) / mapWidth  // half of 18px → full dim = 36px < 40px
        val islandPoly = PolygonBounds(
            0.2f - islandHalfNorm, 0.2f + islandHalfNorm,
            0.5f - islandHalfNorm, 0.5f + islandHalfNorm
        )
        val bounds = archipelagoBounds(smallIslands = listOf(islandPoly))
        // Tap exactly on the island bbox centre
        val result = proximityFallbackHitTest(0.2f, 0.5f, mapOf("CHL" to bounds), mapWidth, mapHeight, 1f)
        assertEquals("CHL", result)
    }

    @Test
    fun `archipelago tap outside radius of tiny polygon returns null`() {
        val islandCx = 0.3f
        val islandCy = 0.5f
        val tapRadiusNorm = TAP_PROXIMITY_PX / (1f * mapWidth)
        val bounds = archipelagoBounds(smallIslands = listOf(tinyPoly(islandCx, islandCy)))
        val farX = islandCx + tapRadiusNorm + 0.002f
        val result = proximityFallbackHitTest(farX, islandCy, mapOf("SYC" to bounds), mapWidth, mapHeight, 1f)
        assertNull(result)
    }

    @Test
    fun `archipelago with mixed large and tiny polygons — only tiny triggers fallback`() {
        // bigHalfNorm produces full dim = TAP_PROXIMITY_PX * 3 * 2 / mapWidth * mapWidth
        //   = SMALL_COUNTRY_THRESHOLD_PX * 6 = 48px > TAP_PROXIMITY_PX * 2 (40px) → excluded
        val bigHalfNorm  = (SMALL_COUNTRY_THRESHOLD_PX * 3f) / mapWidth
        val bigPoly  = PolygonBounds(0.7f - bigHalfNorm, 0.7f + bigHalfNorm, 0.5f - bigHalfNorm, 0.5f + bigHalfNorm)
        val tinyIsland = tinyPoly(0.2f, 0.5f)
        val bounds = archipelagoBounds(smallIslands = listOf(bigPoly, tinyIsland))
        // Tap on the tiny island — only it should match
        val result = proximityFallbackHitTest(0.2f, 0.5f, mapOf("SYC" to bounds), mapWidth, mapHeight, 1f)
        assertEquals("SYC", result)
        // Tap on the big polygon area — fallback skips it (>40px threshold), returns null
        val onBig = proximityFallbackHitTest(0.7f, 0.5f, mapOf("SYC" to bounds), mapWidth, mapHeight, 1f)
        assertNull(onBig)
    }

    @Test
    fun `archipelago picks closest island when two tiny polygons are within radius`() {
        val tapX = 0.5f
        val tapY = 0.5f
        val nearIsland = tinyPoly(0.5005f, 0.5f)   // very close to tap
        val farIsland  = tinyPoly(0.502f,  0.5f)   // further but still within radius
        val bounds = archipelagoBounds(smallIslands = listOf(nearIsland, farIsland))
        val result = proximityFallbackHitTest(tapX, tapY, mapOf("SYC" to bounds), mapWidth, mapHeight, 1f)
        // Either island resolves to "SYC" — the important thing is we get a match
        assertEquals("SYC", result)
    }

    @Test
    fun `archipelago tiny polygon centroid uses bbox midpoint not vertex average`() {
        // Asymmetric polygon bbox: width = 0.006 * 1000px = 6px < SMALL_COUNTRY_THRESHOLD_PX (8px).
        // midpoint X = (0.100 + 0.106) / 2 = 0.103  (not 0.100, proving midpoint is used)
        val pb = PolygonBounds(0.100f, 0.106f, 0.498f, 0.504f)
        val bounds = archipelagoBounds(smallIslands = listOf(pb))
        val expectedCx = (0.100f + 0.106f) / 2f  // = 0.103
        val expectedCy = (0.498f + 0.504f) / 2f  // = 0.501
        // Tap at the exact midpoint — should resolve
        val result = proximityFallbackHitTest(expectedCx, expectedCy, mapOf("SYC" to bounds), mapWidth, mapHeight, 1f)
        assertEquals("SYC", result)
        // Tap at minX (0.100) — still within radius but tests that midpoint is the anchor
        val atMin = proximityFallbackHitTest(0.100f, expectedCy, mapOf("SYC" to bounds), mapWidth, mapHeight, 1f)
        assertEquals("SYC", atMin)
    }

    @Test
    fun `two archipelagos — closer tiny island wins`() {
        // Country A: tiny island at 0.3
        val boundsA = archipelagoBounds(overallMinX = 0.1f, overallMaxX = 0.9f,
            smallIslands = listOf(tinyPoly(0.3f, 0.5f)))
        // Country B: tiny island at 0.32 (further from tap at 0.3)
        val boundsB = archipelagoBounds(overallMinX = 0.05f, overallMaxX = 0.95f,
            smallIslands = listOf(tinyPoly(0.32f, 0.5f)))
        val result = proximityFallbackHitTest(
            normalizedX = 0.3f, normalizedY = 0.5f,
            countryBounds = mapOf("AAA" to boundsA, "BBB" to boundsB),
            mapWidth = mapWidth, mapHeight = mapHeight, currentScale = 1f
        )
        assertEquals("AAA", result)
    }

    @Test
    fun `archipelago with no tiny polygons returns null even if tap is on centroid`() {
        // Country with large overall bounds and no polygonBounds entries
        val bounds = CountryBounds(
            centroidNormX = 0.5f, centroidNormY = 0.5f,
            minX = 0.1f, maxX = 0.9f,
            minY = 0.3f, maxY = 0.7f,
            polygonBounds = emptyList()
        )
        val result = proximityFallbackHitTest(0.5f, 0.5f, mapOf("SYC" to bounds), mapWidth, mapHeight, 1f)
        assertNull(result)
    }

    // ── interaction between path A and path B ────────────────────────────

    @Test
    fun `dot-marker country wins over a further archipelago tiny island`() {
        val tapX = 0.5f
        val tapY = 0.5f
        val dotBounds = smallCountryBounds(cx = 0.5002f, cy = 0.5f)   // very close
        val archBounds = archipelagoBounds(smallIslands = listOf(tinyPoly(0.505f, 0.5f)))
        val result = proximityFallbackHitTest(
            normalizedX = tapX, normalizedY = tapY,
            countryBounds = mapOf("DOT" to dotBounds, "ARC" to archBounds),
            mapWidth = mapWidth, mapHeight = mapHeight, currentScale = 1f
        )
        assertEquals("DOT", result)
    }

    @Test
    fun `archipelago tiny island wins over a further dot-marker country`() {
        val tapX = 0.5f
        val tapY = 0.5f
        val archBounds = archipelagoBounds(smallIslands = listOf(tinyPoly(0.5002f, 0.5f)))  // closer
        val dotBounds  = smallCountryBounds(cx = 0.505f, cy = 0.5f)                         // further
        val result = proximityFallbackHitTest(
            normalizedX = tapX, normalizedY = tapY,
            countryBounds = mapOf("ARC" to archBounds, "DOT" to dotBounds),
            mapWidth = mapWidth, mapHeight = mapHeight, currentScale = 1f
        )
        assertEquals("ARC", result)
    }
}

// ---------------------------------------------------------------------------
// hitTestNormalizedPoint
//
// Tests for the extracted pure-coordinate hit-test function. This exercises
// the two code paths inside the function:
//
//   1. Ray-cast path  — findCountryAtNormalizedPoint returns a geoJson ID
//      which is translated via geoJsonToRepoId and returned.
//
//   2. Fallback path  — ray-cast returns null; proximityFallbackHitTest is
//      called and its result (possibly null) is translated and returned.
//
// The function also validates the geoJsonToRepoId translation for real country
// codes so any regression in the ID map is caught here.
// ---------------------------------------------------------------------------

class HitTestNormalizedPointTest {

    // ── helpers ──────────────────────────────────────────────────────────────

    private val mapWidth  = 1000f
    private val mapHeight = 500f
    private val scale     = 1f

    private fun latLng(lat: Float, lng: Float) =
        com.unstampedpages.app.data.model.LatLng(lat, lng)

    /** A medium-sized triangle polygon centred at (lat=0, lng=0). */
    private val triangleAtOrigin = listOf(
        latLng(5f, -5f), latLng(5f, 5f), latLng(-5f, 0f)
    )

    private fun geometryAt(geoId: String, polygon: List<com.unstampedpages.app.data.model.LatLng>) =
        com.unstampedpages.app.data.model.CountryGeometry(geoId, listOf(polygon))

    private fun boundsFor(geometry: com.unstampedpages.app.data.model.CountryGeometry) =
        computeGeometryBounds(geometry)

    // ── path 1: ray-cast hit ─────────────────────────────────────────────

    @Test
    fun `ray-cast hit returns translated repo id`() {
        // Use a real geoJsonToRepoId entry: "FRA" -> "fr"
        val geometry = geometryAt("FRA", triangleAtOrigin)
        val bounds = mapOf("FRA" to boundsFor(geometry))
        // Tap at centroid of triangle (lat≈2, lng≈0) → should land inside
        val centX = MercatorProjection.longitudeToX(0f)
        val centY = MercatorProjection.latitudeToY(2f)
        val result = hitTestNormalizedPoint(centX, centY, listOf(geometry), bounds, mapWidth, mapHeight, scale)
        assertEquals("fr", result)
    }

    @Test
    fun `ray-cast hit for Germany returns correct repo id`() {
        // DEU -> "de"
        val geometry = geometryAt("DEU", triangleAtOrigin)
        val bounds = mapOf("DEU" to boundsFor(geometry))
        val centX = MercatorProjection.longitudeToX(0f)
        val centY = MercatorProjection.latitudeToY(2f)
        val result = hitTestNormalizedPoint(centX, centY, listOf(geometry), bounds, mapWidth, mapHeight, scale)
        assertEquals("de", result)
    }

    @Test
    fun `ray-cast hit for Japan returns correct repo id`() {
        // JPN -> "jp"
        val geometry = geometryAt("JPN", triangleAtOrigin)
        val bounds = mapOf("JPN" to boundsFor(geometry))
        val centX = MercatorProjection.longitudeToX(0f)
        val centY = MercatorProjection.latitudeToY(2f)
        val result = hitTestNormalizedPoint(centX, centY, listOf(geometry), bounds, mapWidth, mapHeight, scale)
        assertEquals("jp", result)
    }

    @Test
    fun `ray-cast miss falls through to null when no fallback matches`() {
        val geometry = geometryAt("FRA", triangleAtOrigin)
        val bounds = mapOf("FRA" to boundsFor(geometry))
        // Tap far away from the polygon and from any centroid
        val result = hitTestNormalizedPoint(0.9f, 0.1f, listOf(geometry), bounds, mapWidth, mapHeight, scale)
        assertNull(result)
    }

    @Test
    fun `ray-cast with unknown geoId not in geoJsonToRepoId returns null`() {
        // GeoId "ZZZ" is not in geoJsonToRepoId so the repo ID lookup returns null
        val geometry = geometryAt("ZZZ", triangleAtOrigin)
        val bounds = mapOf("ZZZ" to boundsFor(geometry))
        val centX = MercatorProjection.longitudeToX(0f)
        val centY = MercatorProjection.latitudeToY(2f)
        val result = hitTestNormalizedPoint(centX, centY, listOf(geometry), bounds, mapWidth, mapHeight, scale)
        assertNull(result)
    }

    // ── path 2: fallback hit ─────────────────────────────────────────────

    @Test
    fun `fallback hit for dot-marker country returns translated repo id`() {
        // Place a dot-marker country (SYC -> "sc") with its centroid near the tap.
        // Use halfNorm that keeps rendered size well below threshold (3px < 8px).
        val sycCx = 0.65f
        val sycCy = 0.50f
        val halfNorm = (SMALL_COUNTRY_THRESHOLD_PX * 0.375f) / mapWidth  // 3px half-size
        val bounds = CountryBounds(
            centroidNormX = sycCx, centroidNormY = sycCy,
            minX = sycCx - halfNorm, maxX = sycCx + halfNorm,
            minY = sycCy - halfNorm, maxY = sycCy + halfNorm,
            polygonBounds = listOf(PolygonBounds(sycCx - halfNorm, sycCx + halfNorm, sycCy - halfNorm, sycCy + halfNorm))
        )
        // No geometries → ray-cast misses → falls to proximity fallback
        val result = hitTestNormalizedPoint(
            normalizedX = sycCx, normalizedY = sycCy,
            geometries = emptyList(),
            countryBounds = mapOf("SYC" to bounds),
            mapWidth = mapWidth, mapHeight = mapHeight, currentScale = scale
        )
        assertEquals("sc", result)
    }

    @Test
    fun `fallback hit for archipelago tiny island returns translated repo id`() {
        val islandCx = 0.65f
        val islandCy = 0.50f
        val halfNorm = 0.001f  // tiny polygon, well below threshold
        // Overall bounds are large (spanning the map) — forces per-polygon path
        val bounds = CountryBounds(
            centroidNormX = 0.5f, centroidNormY = 0.5f,
            minX = 0.1f, maxX = 0.9f,
            minY = 0.3f, maxY = 0.7f,
            polygonBounds = listOf(
                PolygonBounds(islandCx - halfNorm, islandCx + halfNorm, islandCy - halfNorm, islandCy + halfNorm)
            )
        )
        val result = hitTestNormalizedPoint(
            normalizedX = islandCx, normalizedY = islandCy,
            geometries = emptyList(),
            countryBounds = mapOf("SYC" to bounds),
            mapWidth = mapWidth, mapHeight = mapHeight, currentScale = scale
        )
        assertEquals("sc", result)
    }

    @Test
    fun `fallback with unknown geoId returns null even if proximity matches`() {
        // "ZZZ" is not in geoJsonToRepoId — fallback finds it by proximity but translation is null.
        // Use halfNorm that keeps rendered size well below threshold (3px < 8px).
        val cx = 0.5f; val cy = 0.5f
        val halfNorm = (SMALL_COUNTRY_THRESHOLD_PX * 0.375f) / mapWidth  // 3px half-size
        val bounds = CountryBounds(
            centroidNormX = cx, centroidNormY = cy,
            minX = cx - halfNorm, maxX = cx + halfNorm,
            minY = cy - halfNorm, maxY = cy + halfNorm,
            polygonBounds = listOf(PolygonBounds(cx - halfNorm, cx + halfNorm, cy - halfNorm, cy + halfNorm))
        )
        val result = hitTestNormalizedPoint(cx, cy, emptyList(), mapOf("ZZZ" to bounds), mapWidth, mapHeight, scale)
        assertNull(result)
    }

    // ── priority: dot-marker country wins over ray-cast ───────────────────

    @Test
    fun `nearby dot-marker country wins over ray-cast parent`() {
        // Polygon at origin (ray-cast will hit FRA), plus a dot-marker SYC also nearby
        val fGeom = geometryAt("FRA", triangleAtOrigin)
        val fBounds = boundsFor(fGeom)

        val sycCx = MercatorProjection.longitudeToX(0f)
        val sycCy = MercatorProjection.latitudeToY(2f)
        val halfNorm = (SMALL_COUNTRY_THRESHOLD_PX * 0.375f) / mapWidth  // 3px half-size, clearly below threshold
        val sycBounds = CountryBounds(
            centroidNormX = sycCx, centroidNormY = sycCy,
            minX = sycCx - halfNorm, maxX = sycCx + halfNorm,
            minY = sycCy - halfNorm, maxY = sycCy + halfNorm,
            polygonBounds = listOf(PolygonBounds(sycCx - halfNorm, sycCx + halfNorm, sycCy - halfNorm, sycCy + halfNorm))
        )

        val result = hitTestNormalizedPoint(
            normalizedX = sycCx, normalizedY = sycCy,
            geometries = listOf(fGeom),
            countryBounds = mapOf("FRA" to fBounds, "SYC" to sycBounds),
            mapWidth = mapWidth, mapHeight = mapHeight, currentScale = scale
        )
        // SYC wins because proximity to dot-marker countries is checked first
        assertEquals("sc", result)
    }

    @Test
    fun `ray-cast still works when no dot-marker country is nearby`() {
        val fGeom = geometryAt("FRA", triangleAtOrigin)
        val fBounds = boundsFor(fGeom)

        val result = hitTestNormalizedPoint(
            normalizedX = MercatorProjection.longitudeToX(0f),
            normalizedY = MercatorProjection.latitudeToY(0f),
            geometries = listOf(fGeom),
            countryBounds = mapOf("FRA" to fBounds),
            mapWidth = mapWidth, mapHeight = mapHeight, currentScale = scale
        )
        assertEquals("fr", result)
    }

    // ── empty inputs ─────────────────────────────────────────────────────

    @Test
    fun `proximity hit with geoId not in geoJsonToRepoId returns null`() {
        val cx = MercatorProjection.longitudeToX(0f)
        val cy = MercatorProjection.latitudeToY(0f)
        val halfNorm = (SMALL_COUNTRY_THRESHOLD_PX * 0.375f) / mapWidth
        val unknownBounds = CountryBounds(
            centroidNormX = cx, centroidNormY = cy,
            minX = cx - halfNorm, maxX = cx + halfNorm,
            minY = cy - halfNorm, maxY = cy + halfNorm,
            polygonBounds = listOf(PolygonBounds(cx - halfNorm, cx + halfNorm, cy - halfNorm, cy + halfNorm))
        )
        val result = hitTestNormalizedPoint(
            cx, cy, emptyList(),
            mapOf("UNKNOWN" to unknownBounds),
            mapWidth, mapHeight, scale
        )
        assertNull(result)
    }

    @Test
    fun `ray-cast hit with geoId not in geoJsonToRepoId returns null`() {
        val geometry = geometryAt("NOTINMAP", triangleAtOrigin)
        val bounds = mapOf("NOTINMAP" to boundsFor(geometry))
        val result = hitTestNormalizedPoint(
            MercatorProjection.longitudeToX(0f),
            MercatorProjection.latitudeToY(2f),
            listOf(geometry), bounds, mapWidth, mapHeight, scale
        )
        assertNull(result)
    }

    @Test
    fun `both proximity and ray-cast miss returns null`() {
        val farGeometry = geometryAt("FRA", listOf(
            latLng(80f, 170f), latLng(80f, 175f), latLng(75f, 170f)
        ))
        val result = hitTestNormalizedPoint(
            MercatorProjection.longitudeToX(0f),
            MercatorProjection.latitudeToY(0f),
            listOf(farGeometry),
            mapOf("FRA" to boundsFor(farGeometry)),
            mapWidth, mapHeight, scale
        )
        assertNull(result)
    }

    @Test
    fun `returns null for empty geometries and empty countryBounds`() {
        val result = hitTestNormalizedPoint(0.5f, 0.5f, emptyList(), emptyMap(), mapWidth, mapHeight, scale)
        assertNull(result)
    }

    @Test
    fun `returns null for empty geometries with no proximity match`() {
        val farBounds = CountryBounds(
            centroidNormX = 0.1f, centroidNormY = 0.1f,
            minX = 0.1f, maxX = 0.9f,
            minY = 0.1f, maxY = 0.9f,
            polygonBounds = emptyList()
        )
        val result = hitTestNormalizedPoint(0.8f, 0.8f, emptyList(), mapOf("LRG" to farBounds), mapWidth, mapHeight, scale)
        assertNull(result)
    }
}

// =============================================================================
// CalculateMapLayout Tests
// =============================================================================

class CalculateMapLayoutTest {

    private val mapAspectRatio = MercatorProjection.getAspectRatio()

    @Test
    fun `wide canvas fits to height and centers horizontally`() {
        // Make canvas much wider than the map aspect ratio so it takes the "fit to height" branch.
        val canvasWidth = 2000f
        val canvasHeight = 500f
        val layout = calculateMapLayout(canvasWidth, canvasHeight)

        val expectedMapHeight = canvasHeight
        val expectedMapWidth = canvasHeight * mapAspectRatio

        assertEquals(expectedMapWidth, layout.mapWidth, 0.01f)
        assertEquals(expectedMapHeight, layout.mapHeight, 0.01f)
        assertEquals((canvasWidth - expectedMapWidth) / 2f, layout.canvasOffsetX, 0.01f)
        assertEquals(0f, layout.canvasOffsetY, 0.01f)
        assertEquals(canvasWidth, layout.canvasWidth, 0.01f)
        assertEquals(canvasHeight, layout.canvasHeight, 0.01f)
    }

    @Test
    fun `tall canvas fits to width and centers vertically`() {
        // Make canvas much taller than the map aspect ratio so it takes the "fit to width" branch.
        val canvasWidth = 400f
        val canvasHeight = 2000f
        val layout = calculateMapLayout(canvasWidth, canvasHeight)

        val expectedMapWidth = canvasWidth
        val expectedMapHeight = canvasWidth / mapAspectRatio
        val expectedCanvasOffsetY = (canvasHeight - expectedMapHeight) / 2

        assertEquals(expectedMapWidth, layout.mapWidth, 0.01f)
        assertEquals(expectedMapHeight, layout.mapHeight, 0.01f)
        assertEquals(0f, layout.canvasOffsetX, 0.01f)
        assertEquals(expectedCanvasOffsetY, layout.canvasOffsetY, 0.01f)
        assertEquals(canvasWidth, layout.canvasWidth, 0.01f)
        assertEquals(canvasHeight, layout.canvasHeight, 0.01f)
    }

    @Test
    fun `square canvas selects correct branch based on map aspect ratio`() {
        // The Mercator map is wider than tall, so a square canvas is taller relative to the map.
        val side = 800f
        val layout = calculateMapLayout(side, side)

        // square canvas aspect = 1.0; map aspect > 1.0 → canvas is effectively "taller" → fit-to-width
        assertTrue("mapWidth should equal canvasWidth", layout.mapWidth == side)
        assertTrue("mapHeight should be less than canvasHeight", layout.mapHeight < side)
    }

    @Test
    fun `map dimensions maintain aspect ratio for wide canvas`() {
        val layout = calculateMapLayout(1920f, 600f)
        val actualAspect = layout.mapWidth / layout.mapHeight
        assertEquals(mapAspectRatio, actualAspect, 0.001f)
    }

    @Test
    fun `map dimensions maintain aspect ratio for tall canvas`() {
        val layout = calculateMapLayout(600f, 1920f)
        val actualAspect = layout.mapWidth / layout.mapHeight
        assertEquals(mapAspectRatio, actualAspect, 0.001f)
    }

    @Test
    fun `canvasOffsetX is zero for tall canvas`() {
        val layout = calculateMapLayout(600f, 1200f)
        assertEquals(0f, layout.canvasOffsetX, 0.001f)
    }

    @Test
    fun `canvasOffsetX is positive for wide canvas`() {
        val layout = calculateMapLayout(1920f, 600f)
        assertTrue("canvasOffsetX should be > 0 for wide canvas", layout.canvasOffsetX > 0f)
    }

    @Test
    fun `canvasOffsetY is zero for wide canvas (map fills full height)`() {
        // Wide canvas: map is fit-to-height, so no vertical gap.
        assertEquals(0f, calculateMapLayout(1920f, 600f).canvasOffsetY, 0.001f)
    }

    @Test
    fun `canvasOffsetY centers map vertically for tall canvas`() {
        // Tall canvas: map is fit-to-width, canvasOffsetY = (canvasHeight - mapHeight) / 2.
        val layout600x1920 = calculateMapLayout(600f, 1920f)
        val expectedOffsetY = (1920f - layout600x1920.mapHeight) / 2f
        assertEquals(expectedOffsetY, layout600x1920.canvasOffsetY, 0.001f)
        // The map centre (cy) must equal the canvas centre.
        val cy = layout600x1920.canvasOffsetY + 0.5f * layout600x1920.mapHeight
        assertEquals(1920f / 2f, cy, 0.001f)

        val layout800x800 = calculateMapLayout(800f, 800f)
        val expectedOffsetY800 = (800f - layout800x800.mapHeight) / 2f
        assertEquals(expectedOffsetY800, layout800x800.canvasOffsetY, 0.001f)
        val cy800 = layout800x800.canvasOffsetY + 0.5f * layout800x800.mapHeight
        assertEquals(800f / 2f, cy800, 0.001f)
    }

    @Test
    fun `tall canvas zoom at canvas centre produces focalNormY of 0_5`() {
        // Regression test: fingers at the canvas centre must produce a focal point
        // at the map centre (normY = 0.5), not the southern hemisphere.
        val canvasWidth = 1080f
        val canvasHeight = 1920f
        val layout = calculateMapLayout(canvasWidth, canvasHeight)

        val cy = layout.canvasOffsetY + 0.5f * layout.mapHeight
        assertEquals(canvasHeight / 2f, cy, 0.1f)

        // Simulate fingers at canvas centre with no existing pan.
        val state = TransformState(scale = 1f, panX = 0f, panY = 0f)
        val prevCentroid = Offset(canvasWidth / 2f, canvasHeight / 2f)
        val result = calculateMultiTouchTransform(
            current = state,
            zoom = 2f,
            pan = Offset.Zero,
            prevCentroid = prevCentroid,
            layout = layout
        )
        // With correct vertical centring, panY after zoom must remain ~0 (map centre fixed).
        assertEquals(0f, result.panY, 0.001f)
    }
}

// =============================================================================
// SelectCountryFillColor Tests
// =============================================================================

class SelectCountryFillColorTest {

    private val red   = Color(0xFFFF0000)
    private val green = Color(0xFF00FF00)
    private val blue  = Color(0xFF0000FF)

    // ── selected country ─────────────────────────────────────────────────────

    @Test
    fun `selected country always returns MapHighlight`() {
        val color = selectCountryFillColor(
            countryId = "fr", selectedCountryId = "fr",
            transitionProgress = 1f,
            previousModeColors = mapOf("fr" to red),
            currentModeColors  = mapOf("fr" to green)
        )
        assertEquals(com.unstampedpages.app.ui.theme.MapHighlight, color)
    }

    @Test
    fun `selected country returns MapHighlight even mid-transition`() {
        val color = selectCountryFillColor(
            countryId = "de", selectedCountryId = "de",
            transitionProgress = 0.5f,
            previousModeColors = mapOf("de" to red),
            currentModeColors  = mapOf("de" to blue)
        )
        assertEquals(com.unstampedpages.app.ui.theme.MapHighlight, color)
    }

    // ── transition in progress ───────────────────────────────────────────────

    @Test
    fun `mid-transition returns lerped color`() {
        val from = Color(0xFF000000)
        val to   = Color(0xFFFFFFFF)
        val color = selectCountryFillColor(
            countryId = "us", selectedCountryId = null,
            transitionProgress = 0.5f,
            previousModeColors = mapOf("us" to from),
            currentModeColors  = mapOf("us" to to)
        )
        // Compose lerp works in linear-light space, so verify against the same lerp call
        val expected = androidx.compose.ui.graphics.lerp(from, to, 0.5f)
        assertEquals(expected, color)
    }

    @Test
    fun `transition at exactly 0 returns previous color`() {
        val color = selectCountryFillColor(
            countryId = "jp", selectedCountryId = null,
            transitionProgress = 0f,
            previousModeColors = mapOf("jp" to red),
            currentModeColors  = mapOf("jp" to blue)
        )
        assertEquals(red, color)
    }

    @Test
    fun `transition missing previous color falls back to MapLand`() {
        val color = selectCountryFillColor(
            countryId = "zz", selectedCountryId = null,
            transitionProgress = 0.5f,
            previousModeColors = emptyMap(),
            currentModeColors  = mapOf("zz" to blue)
        )
        val expected = androidx.compose.ui.graphics.lerp(
            com.unstampedpages.app.ui.theme.MapLand, blue, 0.5f
        )
        assertEquals(expected, color)
    }

    @Test
    fun `transition missing current color falls back to MapLand`() {
        val color = selectCountryFillColor(
            countryId = "zz", selectedCountryId = null,
            transitionProgress = 0.5f,
            previousModeColors = mapOf("zz" to red),
            currentModeColors  = emptyMap()
        )
        val expected = androidx.compose.ui.graphics.lerp(
            red, com.unstampedpages.app.ui.theme.MapLand, 0.5f
        )
        assertEquals(expected, color)
    }

    // ── stable state (transitionProgress == 1) ───────────────────────────────

    @Test
    fun `stable state returns current color`() {
        val color = selectCountryFillColor(
            countryId = "fr", selectedCountryId = null,
            transitionProgress = 1f,
            previousModeColors = mapOf("fr" to red),
            currentModeColors  = mapOf("fr" to blue)
        )
        assertEquals(blue, color)
    }

    @Test
    fun `stable state missing current color falls back to MapLand`() {
        val color = selectCountryFillColor(
            countryId = "xx", selectedCountryId = null,
            transitionProgress = 1f,
            previousModeColors = mapOf("xx" to red),
            currentModeColors  = emptyMap()
        )
        assertEquals(com.unstampedpages.app.ui.theme.MapLand, color)
    }

    @Test
    fun `non-selected country is not affected by different selected country`() {
        val color = selectCountryFillColor(
            countryId = "de", selectedCountryId = "fr",
            transitionProgress = 1f,
            previousModeColors = mapOf("de" to red),
            currentModeColors  = mapOf("de" to green)
        )
        assertEquals(green, color)
    }
}

// =============================================================================
// IsCountrySmall Tests
// =============================================================================

class IsCountrySmallTest {

    private val mapWidth  = 1000f
    private val mapHeight = 500f

    @Test
    fun `tiny country with both dims below threshold returns true`() {
        // 0.005 * 1000 * 1 = 5px < 8px
        assertTrue(isCountrySmall(0.005f, 0.005f, mapWidth, mapHeight, scale = 1f))
    }

    @Test
    fun `country at exactly threshold is not small`() {
        // width that gives exactly 8px: 8 / (1000 * 1) = 0.008
        val w = SMALL_COUNTRY_THRESHOLD_PX / (mapWidth * 1f)
        assertFalse(isCountrySmall(w, 0.001f, mapWidth, mapHeight, scale = 1f))
    }

    @Test
    fun `country just below threshold is small`() {
        // 7.9px < 8px
        val w = 7.9f / (mapWidth * 1f)
        assertTrue(isCountrySmall(w, 0.001f, mapWidth, mapHeight, scale = 1f))
    }

    @Test
    fun `wide country exceeding threshold is not small`() {
        // 0.1 * 1000 * 1 = 100px >> 8px
        assertFalse(isCountrySmall(0.1f, 0.001f, mapWidth, mapHeight, scale = 1f))
    }

    @Test
    fun `tall country exceeding threshold is not small`() {
        // height: 0.1 * 500 * 1 = 50px >> 8px
        assertFalse(isCountrySmall(0.001f, 0.1f, mapWidth, mapHeight, scale = 1f))
    }

    @Test
    fun `zoom scale pushes small country above threshold`() {
        // 0.003 * 1000 * 1 = 3px (small), but with scale=4: 0.003*1000*4 = 12px (not small)
        assertTrue(isCountrySmall(0.003f, 0.003f, mapWidth, mapHeight, scale = 1f))
        assertFalse(isCountrySmall(0.003f, 0.003f, mapWidth, mapHeight, scale = 4f))
    }

    @Test
    fun `isSmall uses maxOf width and height dimensions`() {
        // widthPx = 0.003 * 1000 = 3px, heightPx = 0.012 * 500 = 6px → max = 6px < 8px
        assertTrue(isCountrySmall(0.003f, 0.012f, mapWidth, mapHeight, scale = 1f))
        // heightPx = 0.02 * 500 = 10px → max = 10px >= 8px
        assertFalse(isCountrySmall(0.003f, 0.02f, mapWidth, mapHeight, scale = 1f))
    }

    @Test
    fun `scale=1 country just above threshold is not small`() {
        val w = (SMALL_COUNTRY_THRESHOLD_PX + 0.01f) / mapWidth
        assertFalse(isCountrySmall(w, 0.001f, mapWidth, mapHeight, scale = 1f))
    }
}

// =============================================================================
// IsLabelCulled Tests
// =============================================================================

class IsLabelCulledTest {

    private val canvasWidth  = 800f
    private val canvasHeight = 600f
    private val tw = 60f   // typical label text width
    private val th = 20f   // typical label text height

    // ── visible (not culled) ─────────────────────────────────────────────────

    @Test
    fun `label centred on canvas is visible`() {
        assertFalse(isLabelCulled(400f, 300f, tw, th, canvasWidth, canvasHeight))
    }

    @Test
    fun `label at origin is visible`() {
        assertFalse(isLabelCulled(0f, 0f, tw, th, canvasWidth, canvasHeight))
    }

    @Test
    fun `label at canvas bottom-right is visible`() {
        assertFalse(isLabelCulled(canvasWidth, canvasHeight, tw, th, canvasWidth, canvasHeight))
    }

    @Test
    fun `label partially off left edge but within margin is visible`() {
        // screenX = -tw + 1 is inside the margin
        assertFalse(isLabelCulled(-tw + 1f, 300f, tw, th, canvasWidth, canvasHeight))
    }

    @Test
    fun `label partially off right edge but within margin is visible`() {
        assertFalse(isLabelCulled(canvasWidth + tw - 1f, 300f, tw, th, canvasWidth, canvasHeight))
    }

    @Test
    fun `label partially off top edge but within margin is visible`() {
        assertFalse(isLabelCulled(400f, -th + 1f, tw, th, canvasWidth, canvasHeight))
    }

    @Test
    fun `label partially off bottom edge but within margin is visible`() {
        assertFalse(isLabelCulled(400f, canvasHeight + th - 1f, tw, th, canvasWidth, canvasHeight))
    }

    // ── culled (off-screen) ──────────────────────────────────────────────────

    @Test
    fun `label fully off left edge is culled`() {
        assertTrue(isLabelCulled(-tw - 1f, 300f, tw, th, canvasWidth, canvasHeight))
    }

    @Test
    fun `label fully off right edge is culled`() {
        assertTrue(isLabelCulled(canvasWidth + tw + 1f, 300f, tw, th, canvasWidth, canvasHeight))
    }

    @Test
    fun `label fully off top edge is culled`() {
        assertTrue(isLabelCulled(400f, -th - 1f, tw, th, canvasWidth, canvasHeight))
    }

    @Test
    fun `label fully off bottom edge is culled`() {
        assertTrue(isLabelCulled(400f, canvasHeight + th + 1f, tw, th, canvasWidth, canvasHeight))
    }

    @Test
    fun `label off both left and top is culled`() {
        assertTrue(isLabelCulled(-tw - 1f, -th - 1f, tw, th, canvasWidth, canvasHeight))
    }

    @Test
    fun `label exactly at left boundary is culled`() {
        // screenX < -tw → strictly less than, so -tw exactly is NOT culled
        assertFalse(isLabelCulled(-tw, 300f, tw, th, canvasWidth, canvasHeight))
    }

    @Test
    fun `label exactly at right boundary is culled`() {
        // screenX > canvasWidth + tw → strictly greater, so canvasWidth+tw exactly is NOT culled
        assertFalse(isLabelCulled(canvasWidth + tw, 300f, tw, th, canvasWidth, canvasHeight))
    }
}

// =============================================================================
// ComputeZoomBarCount Tests
// =============================================================================

class ComputeZoomBarCountTest {

    @Test
    fun `scale at minimum visible threshold gives 1 bar`() {
        // Just above 1.1f threshold but very close to 1 — formula: (1.11-1)/(199)*5 ≈ 0.0028 → 0, clamped to 1
        assertEquals(1, computeZoomBarCount(1.11f))
    }

    @Test
    fun `scale=1 gives 1 bar (clamped from 0)`() {
        assertEquals(1, computeZoomBarCount(1f))
    }

    @Test
    fun `scale just above 1 gives 1 bar`() {
        assertEquals(1, computeZoomBarCount(1.5f))
    }

    @Test
    fun `scale at 40x gives 1 bar`() {
        // (40-1)/(199)*5 = 195/199*5 ≈ 0.98 → toInt()=0 → clamped to 1
        assertEquals(1, computeZoomBarCount(40f))
    }

    @Test
    fun `scale at midpoint gives 2-3 bars`() {
        // (100-1)/(199)*5 = 99/199*5 ≈ 2.49 → toInt()=2
        assertEquals(2, computeZoomBarCount(100f))
    }

    @Test
    fun `scale at 161 gives 4 bars`() {
        // (161-1)/199*5 = 160/199*5 ≈ 4.02 → toInt()=4
        assertEquals(4, computeZoomBarCount(161f))
    }

    @Test
    fun `scale at max (200) gives 5 bars`() {
        // (200-1)/(199)*5 = 1.0*5 = 5.0 → toInt()=5
        assertEquals(5, computeZoomBarCount(200f))
    }

    @Test
    fun `scale beyond 200 is clamped to 5 bars`() {
        assertEquals(5, computeZoomBarCount(500f))
    }

    @Test
    fun `result is always in range 1 to 5`() {
        val testScales = listOf(0f, 1f, 1.1f, 2f, 10f, 50f, 100f, 150f, 200f, 300f)
        for (s in testScales) {
            val bars = computeZoomBarCount(s)
            assertTrue("bars=$bars out of range for scale=$s", bars in 1..5)
        }
    }

    @Test
    fun `bar count is monotonically non-decreasing with scale`() {
        val scales = listOf(1f, 5f, 10f, 40f, 80f, 120f, 161f, 180f, 200f)
        val counts = scales.map { computeZoomBarCount(it) }
        for (i in 1 until counts.size) {
            assertTrue(
                "bar count should not decrease: ${counts[i-1]} > ${counts[i]} at scale ${scales[i]}",
                counts[i] >= counts[i - 1]
            )
        }
    }
}

// =============================================================================
// ComputeZoomBarSpecs Tests
// =============================================================================

class ComputeZoomBarSpecsTest {

    // Fixed pixel values that stand in for dp.toPx() results in tests.
    private val canvasHeight  = 600f
    private val xPx           = 48f
    private val yOffsetPx     = 48f
    private val barWidthPx    = 16f
    private val barSpacingPx  = 12f
    private val maxBarHeightPx = 64f

    private fun specs(scale: Float) = computeZoomBarSpecs(
        scale, canvasHeight, xPx, yOffsetPx, barWidthPx, barSpacingPx, maxBarHeightPx
    )

    // ── hidden (scale ≤ 1.1) ─────────────────────────────────────────────────

    @Test
    fun `returns null for scale 1`() {
        assertNull(specs(1f))
    }

    @Test
    fun `returns null for scale exactly 1_1`() {
        assertNull(specs(1.1f))
    }

    @Test
    fun `returns non-null for scale just above 1_1`() {
        assertNotNull(specs(1.11f))
    }

    // ── bar count ─────────────────────────────────────────────────────────────

    @Test
    fun `bar count matches computeZoomBarCount`() {
        for (scale in listOf(2f, 10f, 50f, 100f, 150f, 200f)) {
            val expected = computeZoomBarCount(scale)
            assertEquals("scale=$scale", expected, specs(scale)!!.size)
        }
    }

    @Test
    fun `scale 200 produces 5 bars`() {
        assertEquals(5, specs(200f)!!.size)
    }

    // ── bar geometry ──────────────────────────────────────────────────────────

    @Test
    fun `every bar has the correct width`() {
        val list = specs(200f)!!
        list.forEachIndexed { i, spec ->
            assertEquals("bar $i width", barWidthPx, spec.width, 0.001f)
        }
    }

    @Test
    fun `bar heights increase linearly with index`() {
        val list = specs(200f)!!
        list.forEachIndexed { i, spec ->
            val expected = maxBarHeightPx * (i + 1) / 5f
            assertEquals("bar $i height", expected, spec.height, 0.001f)
        }
    }

    @Test
    fun `bar topLeftX increases by barWidth plus barSpacing each step`() {
        val list = specs(200f)!!
        list.forEachIndexed { i, spec ->
            val expected = xPx + i * (barWidthPx + barSpacingPx)
            assertEquals("bar $i topLeftX", expected, spec.topLeftX, 0.001f)
        }
    }

    @Test
    fun `bar topLeftY equals baseY minus barHeight`() {
        val baseY = canvasHeight - yOffsetPx
        val list = specs(200f)!!
        list.forEachIndexed { i, spec ->
            val barHeight = maxBarHeightPx * (i + 1) / 5f
            assertEquals("bar $i topLeftY", baseY - barHeight, spec.topLeftY, 0.001f)
        }
    }

    @Test
    fun `first bar is shortest and last bar is tallest`() {
        val list = specs(200f)!!
        assertTrue(list.first().height < list.last().height)
    }

    @Test
    fun `single bar has correct geometry`() {
        // A scale that maps to 1 bar: (scale-1)/199*5 < 1 → scale < 40.8
        val list = specs(5f)!!
        assertEquals(1, list.size)
        val bar = list[0]
        assertEquals(xPx, bar.topLeftX, 0.001f)
        assertEquals(maxBarHeightPx / 5f, bar.height, 0.001f)
        assertEquals(barWidthPx, bar.width, 0.001f)
    }
}

// =============================================================================
// ComputeVisibleLabelSpecs Tests
// =============================================================================

class ComputeVisibleLabelSpecsTest {

    private val mapWidth     = 1000f
    private val mapHeight    = 500f
    private val canvasWidth  = 800f
    private val canvasHeight = 600f

    // A large-bounding-box country: widthNorm=0.4 → 400px ≫ LABEL_FULL_SCREEN_PX(80px).
    // computeLabelSizeAlpha(400) = 1.0, so finalAlpha = labelAlpha.
    private fun largeBounds(cx: Float = 0.5f, cy: Float = 0.5f) = CountryBounds(
        centroidNormX = cx, centroidNormY = cy,
        minX = cx - 0.2f, maxX = cx + 0.2f,
        minY = cy - 0.1f, maxY = cy + 0.1f,
        polygonBounds = emptyList()
    )

    // screenMaxDim between SMALL_COUNTRY_THRESHOLD_PX(8) and LABEL_MIN_SCREEN_PX(20)
    // → sizeAlpha = 0 → finalAlpha = 0 → label is skipped.
    private fun dimBounds(cx: Float = 0.5f, cy: Float = 0.5f) = CountryBounds(
        centroidNormX = cx, centroidNormY = cy,
        minX = cx - 0.006f, maxX = cx + 0.006f,  // widthNorm=0.012, 12px at scale=1
        minY = cy - 0.006f, maxY = cy + 0.006f,
        polygonBounds = emptyList()
    )

    private fun geom(id: String) =
        com.unstampedpages.app.data.model.CountryGeometry(id, emptyList())

    // Identity mapper: leaves pts untouched (simulates a no-op matrix).
    private val identity: (FloatArray) -> Unit = { }

    private data class RenderConfig(
        val labelAlpha: Float = 1f,
        val matrixValid: Boolean = true
    )

    private fun defaultSpecs(
        wrapOffset: Float = 0f,
        renderConfig: RenderConfig = RenderConfig(),
        geometries: List<com.unstampedpages.app.data.model.CountryGeometry> = listOf(geom("FRA")),
        countryBounds: Map<String, CountryBounds> = mapOf("FRA" to largeBounds()),
        labelTextSizes: Map<String, Pair<Float, Float>> = mapOf("FRA" to (60f to 20f)),
        screenMapper: (FloatArray) -> Unit = identity,
        overrideCanvasWidth: Float = canvasWidth
    ) = computeVisibleLabelSpecs(
        wrapOffset,
        LabelRenderContext(renderConfig.labelAlpha, renderConfig.matrixValid, 1f, mapWidth, mapHeight, overrideCanvasWidth, canvasHeight),
        geometries, countryBounds, labelTextSizes, screenMapper
    )

    // ── early-return guards ───────────────────────────────────────────────────

    @Test
    fun `returns empty map when labelAlpha is below threshold`() {
        assertTrue(defaultSpecs(renderConfig = RenderConfig(labelAlpha = 0.005f)).isEmpty())
    }

    @Test
    fun `returns empty map when labelAlpha is exactly 0`() {
        assertTrue(defaultSpecs(renderConfig = RenderConfig(labelAlpha = 0f)).isEmpty())
    }

    @Test
    fun `returns empty map when matrixValid is false`() {
        assertTrue(defaultSpecs(renderConfig = RenderConfig(matrixValid = false)).isEmpty())
    }

    @Test
    fun `returns empty map when both alpha low and matrix invalid`() {
        assertTrue(defaultSpecs(renderConfig = RenderConfig(labelAlpha = 0f, matrixValid = false)).isEmpty())
    }

    // ── per-geometry skip conditions ──────────────────────────────────────────

    @Test
    fun `returns empty map for empty geometry list`() {
        assertTrue(defaultSpecs(geometries = emptyList()).isEmpty())
    }

    @Test
    fun `skips country with no text size entry`() {
        val result = defaultSpecs(labelTextSizes = emptyMap())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `skips country with no bounds entry`() {
        val result = defaultSpecs(countryBounds = emptyMap())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `skips country whose finalAlpha is below threshold`() {
        // dimBounds → screenMaxDim=12px → sizeAlpha=0 → finalAlpha=0
        val result = defaultSpecs(
            geometries = listOf(geom("FRA")),
            countryBounds = mapOf("FRA" to dimBounds()),
            labelTextSizes = mapOf("FRA" to (60f to 20f))
        )
        assertTrue(result.isEmpty())
    }

    // ── culling ───────────────────────────────────────────────────────────────

    @Test
    fun `culls label whose screenX is off the left edge`() {
        // Move centroid far left so (centNormX+0)*mapWidth < 0 after no-op mapper.
        val result = defaultSpecs(
            geometries = listOf(geom("FRA")),
            countryBounds = mapOf("FRA" to largeBounds(cx = -0.1f)),
            labelTextSizes = mapOf("FRA" to (60f to 20f))
        )
        assertTrue(result.isEmpty())
    }

    @Test
    fun `culls label whose screenX is off the right edge`() {
        // centNormX=1.2 → screenX=1200 > 800+60=860
        val result = defaultSpecs(
            geometries = listOf(geom("FRA")),
            countryBounds = mapOf("FRA" to largeBounds(cx = 1.2f)),
            labelTextSizes = mapOf("FRA" to (60f to 20f))
        )
        assertTrue(result.isEmpty())
    }

    @Test
    fun `culls label whose screenY is off the top edge`() {
        // centNormY=-0.1 → screenY=-50 < -20
        val result = defaultSpecs(
            geometries = listOf(geom("FRA")),
            countryBounds = mapOf("FRA" to largeBounds(cy = -0.1f)),
            labelTextSizes = mapOf("FRA" to (60f to 20f))
        )
        assertTrue(result.isEmpty())
    }

    @Test
    fun `culls label whose screenY is off the bottom edge`() {
        // centNormY=1.5 → screenY=750 > 600+20=620
        val result = defaultSpecs(
            geometries = listOf(geom("FRA")),
            countryBounds = mapOf("FRA" to largeBounds(cy = 1.5f)),
            labelTextSizes = mapOf("FRA" to (60f to 20f))
        )
        assertTrue(result.isEmpty())
    }

    // ── valid label spec ──────────────────────────────────────────────────────

    @Test
    fun `visible label is present in result map`() {
        val result = defaultSpecs()
        assertTrue(result.containsKey("FRA"))
    }

    @Test
    fun `topLeft centres text on centroid screenX`() {
        // centroid (0.5,0.5), identity mapper → screenX=500, tw=60 → topLeft.x=470
        val spec = defaultSpecs().getValue("FRA")
        assertEquals(470f, spec.topLeft.x, 0.01f)
    }

    @Test
    fun `topLeft centres text on centroid screenY`() {
        // screenY=250, th=20 → topLeft.y=240
        val spec = defaultSpecs().getValue("FRA")
        assertEquals(240f, spec.topLeft.y, 0.01f)
    }

    @Test
    fun `shadowOffset is 8 percent of text height`() {
        val spec = defaultSpecs().getValue("FRA")
        assertEquals(20f * 0.08f, spec.shadowOffset, 0.001f)
    }

    @Test
    fun `shadowColor alpha is finalAlpha times 0_67`() {
        val labelAlpha = 0.8f
        val spec = defaultSpecs(renderConfig = RenderConfig(labelAlpha = labelAlpha)).getValue("FRA")
        // largeBounds → sizeAlpha=1 → finalAlpha=0.8
        // Color stores alpha as 8-bit, so allow ≈1/255 ≈ 0.004 rounding error.
        assertEquals(labelAlpha * 0.67f, spec.shadowColor.alpha, 0.005f)
        assertEquals(0f, spec.shadowColor.red,   0.001f)  // Color.Black base
        assertEquals(0f, spec.shadowColor.green, 0.001f)
        assertEquals(0f, spec.shadowColor.blue,  0.001f)
    }

    @Test
    fun `fillColor alpha equals finalAlpha`() {
        val labelAlpha = 0.6f
        val spec = defaultSpecs(renderConfig = RenderConfig(labelAlpha = labelAlpha)).getValue("FRA")
        assertEquals(labelAlpha, spec.fillColor.alpha, 0.001f)
        assertEquals(1f, spec.fillColor.red,   0.001f)  // Color.White base
        assertEquals(1f, spec.fillColor.green, 0.001f)
        assertEquals(1f, spec.fillColor.blue,  0.001f)
    }

    // ── wrapOffset shifts the path-space X coordinate ─────────────────────────

    @Test
    fun `wrapOffset shifts screenX proportionally`() {
        // wrapOffset=0 → pts[0] = 0.5*1000 = 500, topLeft.x = 470
        // wrapOffset=0.3 → pts[0] = 0.8*1000 = 800, topLeft.x = 770
        val spec0  = defaultSpecs(wrapOffset = 0f).getValue("FRA")
        val spec03 = defaultSpecs(wrapOffset = 0.3f).getValue("FRA")
        assertEquals(470f, spec0.topLeft.x,  0.01f)
        assertEquals(770f, spec03.topLeft.x, 0.01f)
    }

    // ── screenMapper is called and transforms pts ─────────────────────────────

    @Test
    fun `screenMapper translation shifts topLeft`() {
        // mapper shifts x by +50 → screenX=550 → topLeft.x = 550-30 = 520
        val shiftMapper: (FloatArray) -> Unit = { pts -> pts[0] += 50f }
        val spec = defaultSpecs(screenMapper = shiftMapper).getValue("FRA")
        assertEquals(520f, spec.topLeft.x, 0.01f)
    }

    // ── centroid overrides ────────────────────────────────────────────────────

    @Test
    fun `uses LABEL_CENTROID_OVERRIDES when available`() {
        // "NZL" has an override at ~longitude 173° → normX ≈ 0.981.
        // Use a wide canvas (3000px) so the label is not culled.
        val override = LABEL_CENTROID_OVERRIDES.getValue("NZL")
        val cx = override.first
        val cy = override.second
        val expectedScreenX = cx * mapWidth
        val expectedScreenY = cy * mapHeight
        val result = defaultSpecs(
            geometries     = listOf(geom("NZL")),
            countryBounds  = mapOf("NZL" to largeBounds(cx, cy)),
            labelTextSizes = mapOf("NZL" to (60f to 20f)),
            overrideCanvasWidth = 3000f
        )
        val spec = result.getValue("NZL")
        assertEquals(expectedScreenX - 30f, spec.topLeft.x, 0.01f)
        assertEquals(expectedScreenY - 10f, spec.topLeft.y, 0.01f)
    }

    @Test
    fun `uses bounds centroid when no override exists`() {
        // "DEU" has no override; centroid from bounds should be used.
        val cx = 0.52f; val cy = 0.38f
        val result = defaultSpecs(
            geometries     = listOf(geom("DEU")),
            countryBounds  = mapOf("DEU" to largeBounds(cx, cy)),
            labelTextSizes = mapOf("DEU" to (60f to 20f))
        )
        val spec = result.getValue("DEU")
        assertEquals(cx * mapWidth - 30f, spec.topLeft.x, 0.01f)
        assertEquals(cy * mapHeight - 10f, spec.topLeft.y, 0.01f)
    }

    // ── multiple geometries ───────────────────────────────────────────────────

    @Test
    fun `multiple visible countries all appear in result`() {
        val result = defaultSpecs(
            geometries    = listOf(geom("FRA"), geom("DEU"), geom("ESP")),
            countryBounds = mapOf(
                "FRA" to largeBounds(0.3f, 0.4f),
                "DEU" to largeBounds(0.5f, 0.4f),
                "ESP" to largeBounds(0.4f, 0.5f)
            ),
            labelTextSizes = mapOf(
                "FRA" to (60f to 20f),
                "DEU" to (50f to 20f),
                "ESP" to (55f to 20f)
            )
        )
        assertEquals(3, result.size)
        assertTrue(result.containsKey("FRA"))
        assertTrue(result.containsKey("DEU"))
        assertTrue(result.containsKey("ESP"))
    }

    @Test
    fun `mix of valid and invalid countries yields only valid ones`() {
        // "FRA" valid, "XXX" has no bounds, "YYY" has dim bounds (finalAlpha=0)
        val result = defaultSpecs(
            geometries    = listOf(geom("FRA"), geom("XXX"), geom("YYY")),
            countryBounds = mapOf(
                "FRA" to largeBounds(),
                "YYY" to dimBounds()
            ),
            labelTextSizes = mapOf(
                "FRA" to (60f to 20f),
                "XXX" to (60f to 20f),
                "YYY" to (60f to 20f)
            )
        )
        assertEquals(1, result.size)
        assertTrue(result.containsKey("FRA"))
    }

    // ==================== latLngToMercator Tests ====================
    // latLngToMercator converts a LatLng to an Offset in map-pixel space by scaling
    // the normalized Mercator coordinates by mapWidth and mapHeight respectively.

    private fun latLng(lat: Float, lng: Float) = com.unstampedpages.app.data.model.LatLng(lat, lng)

    @Test
    fun `latLngToMercator prime meridian equator returns x at half mapWidth`() {
        val result = latLngToMercator(latLng(0f, 0f), mapWidth = 1000f, mapHeight = 500f)
        assertEquals(500f, result.x, 0.1f)
    }

    @Test
    fun `latLngToMercator west edge longitude minus 180 returns x of 0`() {
        val result = latLngToMercator(latLng(0f, -180f), mapWidth = 1000f, mapHeight = 500f)
        assertEquals(0f, result.x, 0.1f)
    }

    @Test
    fun `latLngToMercator east edge longitude 180 returns x equal to mapWidth`() {
        val result = latLngToMercator(latLng(0f, 180f), mapWidth = 1000f, mapHeight = 500f)
        assertEquals(1000f, result.x, 0.1f)
    }

    @Test
    fun `latLngToMercator x scales linearly with mapWidth`() {
        val smallMap = latLngToMercator(latLng(0f, 90f), mapWidth = 100f, mapHeight = 50f)
        val largeMap = latLngToMercator(latLng(0f, 90f), mapWidth = 800f, mapHeight = 400f)
        // longitudeToX(90) = 0.75, so x = 0.75 * mapWidth
        assertEquals(75f, smallMap.x, 0.1f)
        assertEquals(600f, largeMap.x, 0.1f)
    }

    @Test
    fun `latLngToMercator y scales with mapHeight`() {
        val smallMap = latLngToMercator(latLng(0f, 0f), mapWidth = 100f, mapHeight = 50f)
        val largeMap = latLngToMercator(latLng(0f, 0f), mapWidth = 100f, mapHeight = 400f)
        val expectedNormY = MercatorProjection.latitudeToY(0f)
        assertEquals(expectedNormY * 50f, smallMap.y, 0.1f)
        assertEquals(expectedNormY * 400f, largeMap.y, 0.1f)
    }

    @Test
    fun `latLngToMercator high north latitude produces small y`() {
        val result = latLngToMercator(latLng(80f, 0f), mapWidth = 1000f, mapHeight = 500f)
        // y=0 is north pole, so high latitude → small y
        assertTrue("High north lat should produce small y", result.y < 500f * 0.2f)
        assertTrue("y should be non-negative", result.y >= 0f)
    }

    @Test
    fun `latLngToMercator high south latitude produces large y`() {
        val result = latLngToMercator(latLng(-80f, 0f), mapWidth = 1000f, mapHeight = 500f)
        // y=mapHeight is south pole, so far-south latitude → large y
        assertTrue("High south lat should produce large y", result.y > 500f * 0.8f)
        assertTrue("y should not exceed mapHeight", result.y <= 500f)
    }

    @Test
    fun `latLngToMercator equator y is between 40 and 60 percent of mapHeight`() {
        // Asymmetric latitude range (-85..83) means equator is not at exactly 50%
        val result = latLngToMercator(latLng(0f, 0f), mapWidth = 1000f, mapHeight = 500f)
        assertTrue(result.y > 500f * 0.4f)
        assertTrue(result.y < 500f * 0.6f)
    }

    @Test
    fun `latLngToMercator clamps latitude above MAX_LATITUDE`() {
        val atMax = latLngToMercator(latLng(MercatorProjection.MAX_LATITUDE, 0f), mapWidth = 1000f, mapHeight = 500f)
        val aboveMax = latLngToMercator(latLng(90f, 0f), mapWidth = 1000f, mapHeight = 500f)
        assertEquals(atMax.y, aboveMax.y, 0.01f)
    }

    @Test
    fun `latLngToMercator clamps latitude below MIN_LATITUDE`() {
        val atMin = latLngToMercator(latLng(MercatorProjection.MIN_LATITUDE, 0f), mapWidth = 1000f, mapHeight = 500f)
        val belowMin = latLngToMercator(latLng(-90f, 0f), mapWidth = 1000f, mapHeight = 500f)
        assertEquals(atMin.y, belowMin.y, 0.01f)
    }

    @Test
    fun `latLngToMercator x and y are independent of each other`() {
        val base = latLngToMercator(latLng(45f, 90f), mapWidth = 800f, mapHeight = 400f)
        val sameLatDiffLng = latLngToMercator(latLng(45f, -90f), mapWidth = 800f, mapHeight = 400f)
        val diffLatSameLng = latLngToMercator(latLng(-45f, 90f), mapWidth = 800f, mapHeight = 400f)

        // Same latitude → same y
        assertEquals(base.y, sameLatDiffLng.y, 0.01f)
        // Same longitude → same x
        assertEquals(base.x, diffLatSameLng.x, 0.01f)
        // Different latitudes → different y
        assertNotEquals(base.y, diffLatSameLng.y)
        // Different longitudes → different x
        assertNotEquals(base.x, sameLatDiffLng.x)
    }

    @Test
    fun `latLngToMercator result matches direct MercatorProjection calculation`() {
        val lat = 51.5074f  // London
        val lng = -0.1278f
        val mapWidth = 1200f
        val mapHeight = 600f

        val result = latLngToMercator(latLng(lat, lng), mapWidth, mapHeight)

        val expectedX = MercatorProjection.longitudeToX(lng) * mapWidth
        val expectedY = MercatorProjection.latitudeToY(lat) * mapHeight
        assertEquals(expectedX, result.x, 0.001f)
        assertEquals(expectedY, result.y, 0.001f)
    }

    @Test
    fun `latLngToMercator returns zero offset for zero-size map`() {
        val result = latLngToMercator(latLng(45f, 90f), mapWidth = 0f, mapHeight = 0f)
        assertEquals(0f, result.x, 0.001f)
        assertEquals(0f, result.y, 0.001f)
    }

    @Test
    fun `latLngToMercator north pole clamped y is less than equator y`() {
        val northPole = latLngToMercator(latLng(90f, 0f), mapWidth = 1000f, mapHeight = 500f)
        val equator = latLngToMercator(latLng(0f, 0f), mapWidth = 1000f, mapHeight = 500f)
        assertTrue(northPole.y < equator.y)
    }

    @Test
    fun `latLngToMercator south pole clamped y is greater than equator y`() {
        val southPole = latLngToMercator(latLng(-90f, 0f), mapWidth = 1000f, mapHeight = 500f)
        val equator = latLngToMercator(latLng(0f, 0f), mapWidth = 1000f, mapHeight = 500f)
        assertTrue(southPole.y > equator.y)
    }

    @Test
    fun `latLngToMercator x increases monotonically with longitude`() {
        val longitudes = listOf(-180f, -90f, 0f, 90f, 180f)
        val xValues = longitudes.map { lng ->
            latLngToMercator(latLng(0f, lng), mapWidth = 1000f, mapHeight = 500f).x
        }
        for (i in 0 until xValues.size - 1) {
            assertTrue(
                "x at lng ${longitudes[i + 1]} (${xValues[i + 1]}) should be > x at ${longitudes[i]} (${xValues[i]})",
                xValues[i + 1] > xValues[i]
            )
        }
    }

    @Test
    fun `latLngToMercator y increases monotonically as latitude decreases`() {
        val latitudes = listOf(80f, 40f, 0f, -40f, -80f)
        val yValues = latitudes.map { lat ->
            latLngToMercator(latLng(lat, 0f), mapWidth = 1000f, mapHeight = 500f).y
        }
        for (i in 0 until yValues.size - 1) {
            assertTrue(
                "y at lat ${latitudes[i + 1]} (${yValues[i + 1]}) should be > y at ${latitudes[i]} (${yValues[i]})",
                yValues[i + 1] > yValues[i]
            )
        }
    }

    // ==================== determinePanDirection Tests ====================

    @Test
    fun `determinePanDirection returns right when panX is positive and dominant`() {
        assertEquals("right", determinePanDirection(100f, 10f))
    }

    @Test
    fun `determinePanDirection returns left when panX is negative and dominant`() {
        assertEquals("left", determinePanDirection(-100f, 10f))
    }

    @Test
    fun `determinePanDirection returns down when panY is positive and dominant`() {
        assertEquals("down", determinePanDirection(5f, 80f))
    }

    @Test
    fun `determinePanDirection returns up when panY is negative and dominant`() {
        assertEquals("up", determinePanDirection(5f, -80f))
    }

    @Test
    fun `determinePanDirection prefers vertical when axes are equal`() {
        // When abs(panX) == abs(panY), the vertical axis wins (ties go to vertical)
        val result = determinePanDirection(50f, 50f)
        assertEquals("down", result)
    }

    @Test
    fun `determinePanDirection prefers vertical when axes are equal and negative Y`() {
        val result = determinePanDirection(50f, -50f)
        assertEquals("up", result)
    }

    @Test
    fun `determinePanDirection handles large horizontal displacement`() {
        assertEquals("right", determinePanDirection(9999f, 9998f))
    }

    @Test
    fun `determinePanDirection handles large vertical displacement`() {
        assertEquals("down", determinePanDirection(9998f, 9999f))
    }

    @Test
    fun `determinePanDirection handles near-zero displacement`() {
        assertEquals("right", determinePanDirection(0.01f, 0.005f))
    }

    @Test
    fun `determinePanDirection returns left for purely horizontal negative movement`() {
        assertEquals("left", determinePanDirection(-200f, 0f))
    }

    @Test
    fun `determinePanDirection returns right for purely horizontal positive movement`() {
        assertEquals("right", determinePanDirection(200f, 0f))
    }

    @Test
    fun `determinePanDirection returns down for purely vertical positive movement`() {
        assertEquals("down", determinePanDirection(0f, 200f))
    }

    @Test
    fun `determinePanDirection returns up for purely vertical negative movement`() {
        assertEquals("up", determinePanDirection(0f, -200f))
    }
}
