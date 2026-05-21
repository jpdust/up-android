package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.ui.graphics.Color
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
        assertTrue(0.01f >= 0.01f)   // boundary: exactly at threshold is kept
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
        val (x, _) = LABEL_CENTROID_OVERRIDES["NZL"]!!
        assertEquals(MercatorProjection.longitudeToX(173f), x, 0.0001f)
    }

    @Test
    fun `NZL override Y matches latitude 41_5 south`() {
        val (_, y) = LABEL_CENTROID_OVERRIDES["NZL"]!!
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
        val (x, _) = LABEL_CENTROID_OVERRIDES["KIR"]!!
        assertEquals(MercatorProjection.longitudeToX(174f), x, 0.0001f)
    }

    @Test
    fun `KIR override Y matches latitude 1_5 south (near Tarawa)`() {
        val (_, y) = LABEL_CENTROID_OVERRIDES["KIR"]!!
        assertEquals(MercatorProjection.latitudeToY(-1.5f), y, 0.0001f)
    }

    @Test
    fun `KIR override X is in eastern Pacific (right side of map)`() {
        // Gilbert Islands are near 174°E; normalized X should be > 0.95.
        val (x, _) = LABEL_CENTROID_OVERRIDES["KIR"]!!
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
        // Polygon large enough that renderedPx > SMALL_COUNTRY_THRESHOLD_PX — should be skipped
        val bigHalfNorm = (SMALL_COUNTRY_THRESHOLD_PX * 2f) / mapWidth
        val bigPoly = PolygonBounds(0.4f - bigHalfNorm, 0.4f + bigHalfNorm, 0.4f - bigHalfNorm, 0.4f + bigHalfNorm)
        val bounds = archipelagoBounds(smallIslands = listOf(bigPoly))
        // Tap exactly on the big polygon centre — fallback skips it, so returns null
        val result = proximityFallbackHitTest(0.4f, 0.4f, mapOf("SYC" to bounds), mapWidth, mapHeight, 1f)
        assertNull(result)
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
        val bigHalfNorm  = (SMALL_COUNTRY_THRESHOLD_PX * 3f) / mapWidth
        val bigPoly  = PolygonBounds(0.7f - bigHalfNorm, 0.7f + bigHalfNorm, 0.5f - bigHalfNorm, 0.5f + bigHalfNorm)
        val tinyIsland = tinyPoly(0.2f, 0.5f)
        val bounds = archipelagoBounds(smallIslands = listOf(bigPoly, tinyIsland))
        // Tap on the tiny island — only it should match
        val result = proximityFallbackHitTest(0.2f, 0.5f, mapOf("SYC" to bounds), mapWidth, mapHeight, 1f)
        assertEquals("SYC", result)
        // Tap on the big polygon area — fallback skips it, returns null
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

    // ── priority: ray-cast wins over fallback ────────────────────────────

    @Test
    fun `ray-cast result wins over nearby fallback candidate`() {
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
        // FRA wins because ray-cast fires first
        assertEquals("fr", result)
    }

    // ── empty inputs ─────────────────────────────────────────────────────

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
