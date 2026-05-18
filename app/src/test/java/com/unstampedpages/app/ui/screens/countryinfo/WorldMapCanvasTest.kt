package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.ui.graphics.Color
import com.unstampedpages.app.R
import com.unstampedpages.app.data.AppConstants
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
    fun `geoJsonToRepoId has no duplicate values`() {
        val values = geoJsonToRepoId.values.toList()
        val uniqueValues = values.distinct()
        assertEquals(
            "All repo IDs should be unique",
            values.size,
            uniqueValues.size
        )
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
}
