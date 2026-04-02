package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.ui.graphics.Color
import com.unstampedpages.app.R
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
        val color = getPassportValidityColor("6 months")
        assertEquals(PassportValidityColors.SixMonths, color)
    }

    @Test
    fun `getPassportValidityColor returns teal for 3 months`() {
        val color = getPassportValidityColor("3 months")
        assertEquals(PassportValidityColors.ThreeMonths, color)
    }

    @Test
    fun `getPassportValidityColor returns green for planned stay`() {
        val color = getPassportValidityColor("Planned length of stay")
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
    fun `getLegendItems returns 3 items for SECURITY_RISK mode`() {
        val items = getLegendItems(MapColorMode.SECURITY_RISK)
        assertEquals(3, items.size)
    }

    @Test
    fun `getLegendItems SECURITY_RISK contains expected labelResIds`() {
        val items = getLegendItems(MapColorMode.SECURITY_RISK)
        val labelResIds = items.map { it.labelResId }
        assertTrue(labelResIds.contains(R.string.legend_low_risk))
        assertTrue(labelResIds.contains(R.string.legend_medium_risk))
        assertTrue(labelResIds.contains(R.string.legend_high_risk))
    }

    @Test
    fun `getLegendItems returns 5 items for VISA_REQUIREMENTS mode`() {
        val items = getLegendItems(MapColorMode.VISA_REQUIREMENTS)
        assertEquals(5, items.size)
    }

    @Test
    fun `getLegendItems VISA_REQUIREMENTS contains all visa type labelResIds`() {
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
    fun `getLegendItems PASSPORT_VALIDITY contains all validity type labelResIds`() {
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
                    "Item should have non-empty testTag",
                    item.testTag.isNotBlank()
                )
            }
        }
    }

    @Test
    fun `getLegendItems all items have non-zero labelResIds`() {
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

    // ==================== Additional getLegendItems Tests ====================

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

        assertEquals(Color(0xFF4CAF50), lowRisk?.color)    // Green
        assertEquals(Color(0xFFFFC107), mediumRisk?.color) // Yellow
        assertEquals(Color(0xFF8B0000), highRisk?.color)   // Dark Red
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

    // ==================== Additional geoJsonToRepoId Tests ====================

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
    fun `geoJsonToRepoId has no duplicate values`() {
        val values = geoJsonToRepoId.values.toList()
        val uniqueValues = values.distinct()
        assertEquals(
            "All repo IDs should be unique",
            values.size,
            uniqueValues.size
        )
    }

    // ==================== getLegendItems Test Tag Uniqueness ====================

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
}
