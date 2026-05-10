package com.unstampedpages.app.ui.screens.countryinfo

import com.unstampedpages.app.data.model.CountryGeometry
import com.unstampedpages.app.data.model.LatLng
import org.junit.Assert.*
import org.junit.Test

// ---------------------------------------------------------------------------
// Helpers shared across all classes in this file
// ---------------------------------------------------------------------------

private fun latLng(lat: Float, lng: Float) = LatLng(lat, lng)

private fun normX(lng: Float) = MercatorProjection.longitudeToX(lng)
private fun normY(lat: Float) = MercatorProjection.latitudeToY(lat)

// ---------------------------------------------------------------------------
// CountryBounds — data class and computed properties
// ---------------------------------------------------------------------------

/**
 * Direct tests for [CountryBounds].
 *
 * Covers:
 *   - Construction (explicit + default polygonBounds)
 *   - widthNorm and heightNorm computed properties
 *   - Data class contract: equals, hashCode, copy, toString
 *   - Edge cases: zero-width/height, full-range, negative (inverted) bounds
 */
class CountryBoundsTest {

    // ==================== Construction ====================

    @Test
    fun `all fields are stored as provided`() {
        val pb = PolygonBounds(0.1f, 0.4f, 0.2f, 0.8f)
        val bounds = CountryBounds(
            centroidNormX = 0.3f,
            centroidNormY = 0.6f,
            minX = 0.1f,
            maxX = 0.4f,
            minY = 0.2f,
            maxY = 0.8f,
            polygonBounds = listOf(pb)
        )

        assertEquals(0.3f,  bounds.centroidNormX, 0f)
        assertEquals(0.6f,  bounds.centroidNormY, 0f)
        assertEquals(0.1f,  bounds.minX,          0f)
        assertEquals(0.4f,  bounds.maxX,          0f)
        assertEquals(0.2f,  bounds.minY,          0f)
        assertEquals(0.8f,  bounds.maxY,          0f)
        assertEquals(listOf(pb), bounds.polygonBounds)
    }

    @Test
    fun `polygonBounds defaults to empty list`() {
        val bounds = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.1f, 0.9f)
        assertEquals(emptyList<PolygonBounds>(), bounds.polygonBounds)
    }

    @Test
    fun `polygonBounds can be supplied with multiple entries`() {
        val pb1 = PolygonBounds(0.0f, 0.3f, 0.0f, 0.3f)
        val pb2 = PolygonBounds(0.6f, 0.9f, 0.6f, 0.9f)
        val bounds = CountryBounds(0.5f, 0.5f, 0.0f, 0.9f, 0.0f, 0.9f, listOf(pb1, pb2))
        assertEquals(2, bounds.polygonBounds.size)
        assertEquals(pb1, bounds.polygonBounds[0])
        assertEquals(pb2, bounds.polygonBounds[1])
    }

    // ==================== widthNorm ====================

    @Test
    fun `widthNorm equals maxX minus minX`() {
        val bounds = CountryBounds(0.5f, 0.5f, 0.2f, 0.7f, 0.3f, 0.8f)
        assertEquals(0.5f, bounds.widthNorm, 0.0001f)
    }

    @Test
    fun `widthNorm is zero when minX equals maxX`() {
        val bounds = CountryBounds(0.5f, 0.5f, 0.4f, 0.4f, 0.0f, 1.0f)
        assertEquals(0f, bounds.widthNorm, 0f)
    }

    @Test
    fun `widthNorm is one for full longitude range`() {
        val bounds = CountryBounds(0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f)
        assertEquals(1f, bounds.widthNorm, 0.0001f)
    }

    @Test
    fun `widthNorm is negative when maxX is less than minX`() {
        // CountryBounds is a plain data class — it does not enforce minX < maxX.
        val bounds = CountryBounds(0.5f, 0.5f, 0.8f, 0.2f, 0.0f, 1.0f)
        assertTrue("Inverted X bounds produce negative widthNorm", bounds.widthNorm < 0f)
        assertEquals(-0.6f, bounds.widthNorm, 0.0001f)
    }

    @Test
    fun `widthNorm for small country is small but positive`() {
        val bounds = CountryBounds(0.5f, 0.5f, 0.499f, 0.501f, 0.3f, 0.7f)
        assertTrue(bounds.widthNorm > 0f)
        assertTrue(bounds.widthNorm < 0.01f)
    }

    @Test
    fun `widthNorm plus minX equals maxX`() {
        val minX = 0.25f
        val maxX = 0.75f
        val bounds = CountryBounds(0.5f, 0.5f, minX, maxX, 0.0f, 1.0f)
        assertEquals(maxX, bounds.minX + bounds.widthNorm, 0.0001f)
    }

    // ==================== heightNorm ====================

    @Test
    fun `heightNorm equals maxY minus minY`() {
        val bounds = CountryBounds(0.5f, 0.5f, 0.0f, 1.0f, 0.3f, 0.7f)
        assertEquals(0.4f, bounds.heightNorm, 0.0001f)
    }

    @Test
    fun `heightNorm is zero when minY equals maxY`() {
        val bounds = CountryBounds(0.5f, 0.5f, 0.0f, 1.0f, 0.5f, 0.5f)
        assertEquals(0f, bounds.heightNorm, 0f)
    }

    @Test
    fun `heightNorm is one for full latitude range`() {
        val bounds = CountryBounds(0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f)
        assertEquals(1f, bounds.heightNorm, 0.0001f)
    }

    @Test
    fun `heightNorm is negative when maxY is less than minY`() {
        val bounds = CountryBounds(0.5f, 0.5f, 0.0f, 1.0f, 0.9f, 0.1f)
        assertTrue("Inverted Y bounds produce negative heightNorm", bounds.heightNorm < 0f)
        assertEquals(-0.8f, bounds.heightNorm, 0.0001f)
    }

    @Test
    fun `heightNorm plus minY equals maxY`() {
        val minY = 0.1f
        val maxY = 0.6f
        val bounds = CountryBounds(0.5f, 0.5f, 0.0f, 1.0f, minY, maxY)
        assertEquals(maxY, bounds.minY + bounds.heightNorm, 0.0001f)
    }

    @Test
    fun `widthNorm and heightNorm are independent of centroid`() {
        val b1 = CountryBounds(0.3f, 0.4f, 0.1f, 0.6f, 0.2f, 0.7f)
        val b2 = CountryBounds(0.8f, 0.1f, 0.1f, 0.6f, 0.2f, 0.7f)
        assertEquals(b1.widthNorm,  b2.widthNorm,  0.0001f)
        assertEquals(b1.heightNorm, b2.heightNorm, 0.0001f)
    }

    // ==================== Equality ====================

    @Test
    fun `two instances with identical fields are equal`() {
        val a = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        val b = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        assertEquals(a, b)
    }

    @Test
    fun `instances differ when centroidNormX differs`() {
        val a = CountryBounds(0.4f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        val b = CountryBounds(0.6f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        assertNotEquals(a, b)
    }

    @Test
    fun `instances differ when centroidNormY differs`() {
        val a = CountryBounds(0.5f, 0.4f, 0.1f, 0.9f, 0.2f, 0.8f)
        val b = CountryBounds(0.5f, 0.6f, 0.1f, 0.9f, 0.2f, 0.8f)
        assertNotEquals(a, b)
    }

    @Test
    fun `instances differ when minX differs`() {
        val a = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        val b = CountryBounds(0.5f, 0.5f, 0.2f, 0.9f, 0.2f, 0.8f)
        assertNotEquals(a, b)
    }

    @Test
    fun `instances differ when maxX differs`() {
        val a = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        val b = CountryBounds(0.5f, 0.5f, 0.1f, 0.8f, 0.2f, 0.8f)
        assertNotEquals(a, b)
    }

    @Test
    fun `instances differ when minY differs`() {
        val a = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        val b = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.3f, 0.8f)
        assertNotEquals(a, b)
    }

    @Test
    fun `instances differ when maxY differs`() {
        val a = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        val b = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.9f)
        assertNotEquals(a, b)
    }

    @Test
    fun `instances differ when polygonBounds list differs`() {
        val pb = PolygonBounds(0.1f, 0.4f, 0.2f, 0.8f)
        val a = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f, listOf(pb))
        val b = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        assertNotEquals(a, b)
    }

    // ==================== hashCode ====================

    @Test
    fun `equal instances have equal hashCodes`() {
        val a = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        val b = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `hashCode is stable across multiple calls`() {
        val bounds = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        assertEquals(bounds.hashCode(), bounds.hashCode())
    }

    // ==================== copy ====================

    @Test
    fun `copy with no changes produces equal instance`() {
        val original = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        val copy = original.copy()
        assertEquals(original, copy)
    }

    @Test
    fun `copy changing centroidNormX produces different instance`() {
        val original = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        val modified = original.copy(centroidNormX = 0.3f)
        assertEquals(0.3f, modified.centroidNormX, 0f)
        assertEquals(original.centroidNormY, modified.centroidNormY, 0f)
        assertEquals(original.minX,          modified.minX,          0f)
        assertEquals(original.maxX,          modified.maxX,          0f)
    }

    @Test
    fun `copy changing minX recalculates widthNorm`() {
        val original = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        val modified = original.copy(minX = 0.4f)
        // widthNorm of modified = 0.9 - 0.4 = 0.5 (was 0.8)
        assertEquals(0.5f, modified.widthNorm, 0.0001f)
    }

    @Test
    fun `copy changing polygonBounds retains all other fields`() {
        val pb = PolygonBounds(0.0f, 0.5f, 0.0f, 0.5f)
        val original = CountryBounds(0.4f, 0.6f, 0.1f, 0.8f, 0.2f, 0.9f)
        val modified = original.copy(polygonBounds = listOf(pb))
        assertEquals(listOf(pb), modified.polygonBounds)
        assertEquals(original.centroidNormX, modified.centroidNormX, 0f)
        assertEquals(original.minX,          modified.minX,          0f)
        assertEquals(original.maxY,          modified.maxY,          0f)
    }

    // ==================== toString ====================

    @Test
    fun `toString contains the class name`() {
        val bounds = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        assertTrue(bounds.toString().contains("CountryBounds"))
    }

    @Test
    fun `toString contains all primary field names`() {
        val bounds = CountryBounds(0.5f, 0.5f, 0.1f, 0.9f, 0.2f, 0.8f)
        val str = bounds.toString()
        assertTrue(str.contains("centroidNormX"))
        assertTrue(str.contains("centroidNormY"))
        assertTrue(str.contains("minX"))
        assertTrue(str.contains("maxX"))
        assertTrue(str.contains("minY"))
        assertTrue(str.contains("maxY"))
    }

    // ==================== Semantic invariants (valid bounds) ====================

    @Test
    fun `widthNorm is non-negative for well-formed bounds`() {
        val bounds = CountryBounds(0.5f, 0.5f, 0.2f, 0.8f, 0.1f, 0.9f)
        assertTrue("widthNorm must be non-negative", bounds.widthNorm >= 0f)
    }

    @Test
    fun `heightNorm is non-negative for well-formed bounds`() {
        val bounds = CountryBounds(0.5f, 0.5f, 0.2f, 0.8f, 0.1f, 0.9f)
        assertTrue("heightNorm must be non-negative", bounds.heightNorm >= 0f)
    }

    @Test
    fun `centroid at exact center of symmetric bounds`() {
        // For a perfectly symmetric bbox around the origin
        val cx = normX(0f)   // 0.5
        val cy = normY(0f)
        val bounds = CountryBounds(cx, cy, cx - 0.2f, cx + 0.2f, cy - 0.1f, cy + 0.1f)
        assertEquals(cx, bounds.centroidNormX, 0.0001f)
        assertEquals(cy, bounds.centroidNormY, 0.0001f)
    }

    @Test
    fun `fallback degenerate bounds produced by computeGeometryBounds have correct fields`() {
        // computeGeometryBounds returns CountryBounds(0.5f, 0.5f, 0f, 1f, 0f, 1f)
        // for an empty geometry. Verify that degenerate instance has the correct computed props.
        val degenerate = CountryBounds(0.5f, 0.5f, 0f, 1f, 0f, 1f)
        assertEquals(1f, degenerate.widthNorm,  0f)
        assertEquals(1f, degenerate.heightNorm, 0f)
    }
}

// ---------------------------------------------------------------------------
// PolygonBounds — data class tests
// ---------------------------------------------------------------------------

/**
 * Direct tests for [PolygonBounds].
 *
 * Covers construction, field storage, equality, hashCode, copy, and toString.
 */
class PolygonBoundsTest {

    // ==================== Construction ====================

    @Test
    fun `all fields are stored as provided`() {
        val pb = PolygonBounds(0.1f, 0.6f, 0.2f, 0.7f)
        assertEquals(0.1f, pb.minX, 0f)
        assertEquals(0.6f, pb.maxX, 0f)
        assertEquals(0.2f, pb.minY, 0f)
        assertEquals(0.7f, pb.maxY, 0f)
    }

    @Test
    fun `all-zero bounds stores zeros`() {
        val pb = PolygonBounds(0f, 0f, 0f, 0f)
        assertEquals(0f, pb.minX, 0f)
        assertEquals(0f, pb.maxX, 0f)
        assertEquals(0f, pb.minY, 0f)
        assertEquals(0f, pb.maxY, 0f)
    }

    @Test
    fun `full-range fallback bounds (0,1,0,1) stores correctly`() {
        val pb = PolygonBounds(0f, 1f, 0f, 1f)
        assertEquals(0f, pb.minX, 0f)
        assertEquals(1f, pb.maxX, 0f)
        assertEquals(0f, pb.minY, 0f)
        assertEquals(1f, pb.maxY, 0f)
    }

    @Test
    fun `single-point bounds has equal min and max`() {
        val x = normX(45f)
        val y = normY(45f)
        val pb = PolygonBounds(x, x, y, y)
        assertEquals(pb.minX, pb.maxX, 0f)
        assertEquals(pb.minY, pb.maxY, 0f)
    }

    // ==================== Equality ====================

    @Test
    fun `two instances with identical fields are equal`() {
        val a = PolygonBounds(0.1f, 0.6f, 0.2f, 0.7f)
        val b = PolygonBounds(0.1f, 0.6f, 0.2f, 0.7f)
        assertEquals(a, b)
    }

    @Test
    fun `instances differ when minX differs`() {
        assertNotEquals(
            PolygonBounds(0.1f, 0.6f, 0.2f, 0.7f),
            PolygonBounds(0.2f, 0.6f, 0.2f, 0.7f)
        )
    }

    @Test
    fun `instances differ when maxX differs`() {
        assertNotEquals(
            PolygonBounds(0.1f, 0.6f, 0.2f, 0.7f),
            PolygonBounds(0.1f, 0.7f, 0.2f, 0.7f)
        )
    }

    @Test
    fun `instances differ when minY differs`() {
        assertNotEquals(
            PolygonBounds(0.1f, 0.6f, 0.2f, 0.7f),
            PolygonBounds(0.1f, 0.6f, 0.3f, 0.7f)
        )
    }

    @Test
    fun `instances differ when maxY differs`() {
        assertNotEquals(
            PolygonBounds(0.1f, 0.6f, 0.2f, 0.7f),
            PolygonBounds(0.1f, 0.6f, 0.2f, 0.8f)
        )
    }

    // ==================== hashCode ====================

    @Test
    fun `equal instances have equal hashCodes`() {
        val a = PolygonBounds(0.1f, 0.6f, 0.2f, 0.7f)
        val b = PolygonBounds(0.1f, 0.6f, 0.2f, 0.7f)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `hashCode is stable across multiple calls`() {
        val pb = PolygonBounds(0.1f, 0.6f, 0.2f, 0.7f)
        assertEquals(pb.hashCode(), pb.hashCode())
    }

    // ==================== copy ====================

    @Test
    fun `copy with no changes produces equal instance`() {
        val original = PolygonBounds(0.1f, 0.6f, 0.2f, 0.7f)
        assertEquals(original, original.copy())
    }

    @Test
    fun `copy changing minX preserves other fields`() {
        val original = PolygonBounds(0.1f, 0.6f, 0.2f, 0.7f)
        val modified = original.copy(minX = 0.3f)
        assertEquals(0.3f, modified.minX, 0f)
        assertEquals(original.maxX, modified.maxX, 0f)
        assertEquals(original.minY, modified.minY, 0f)
        assertEquals(original.maxY, modified.maxY, 0f)
    }

    @Test
    fun `copy changing maxY preserves other fields`() {
        val original = PolygonBounds(0.1f, 0.6f, 0.2f, 0.7f)
        val modified = original.copy(maxY = 0.95f)
        assertEquals(0.95f, modified.maxY, 0f)
        assertEquals(original.minX, modified.minX, 0f)
        assertEquals(original.maxX, modified.maxX, 0f)
        assertEquals(original.minY, modified.minY, 0f)
    }

    // ==================== toString ====================

    @Test
    fun `toString contains the class name`() {
        assertTrue(PolygonBounds(0.1f, 0.6f, 0.2f, 0.7f).toString().contains("PolygonBounds"))
    }

    @Test
    fun `toString contains all field names`() {
        val str = PolygonBounds(0.1f, 0.6f, 0.2f, 0.7f).toString()
        assertTrue(str.contains("minX"))
        assertTrue(str.contains("maxX"))
        assertTrue(str.contains("minY"))
        assertTrue(str.contains("maxY"))
    }
}

// ---------------------------------------------------------------------------
// computeGeometryBounds — gaps not covered in WorldMapCanvasTest
// ---------------------------------------------------------------------------

/**
 * Additional [computeGeometryBounds] tests that extend the coverage in
 * [ComputeGeometryBoundsTest] (WorldMapCanvasTest.kt).
 *
 * Covers:
 *   - centroidNormY verified precisely
 *   - widthNorm / heightNorm of the result
 *   - Three-polygon geometry
 *   - Global bounds strictly contain per-polygon bounds
 *   - Country in the northern hemisphere only
 *   - Country in the southern hemisphere only
 */
class ComputeGeometryBoundsExtendedTest {

    // Compact rectangular polygon helpers
    private fun rect(northLat: Float, southLat: Float, westLng: Float, eastLng: Float) = listOf(
        latLng(northLat, westLng), latLng(northLat, eastLng),
        latLng(southLat, eastLng), latLng(southLat, westLng),
        latLng(northLat, westLng)
    )

    // ==================== centroidNormY ====================

    @Test
    fun `centroidNormY is average of all Y values for a symmetric square`() {
        // A symmetric square centred on (0°, 0°) – Y values are symmetric around normY(0°)
        val polygon = rect(10f, -10f, -10f, 10f)
        val geometry = CountryGeometry("sq", listOf(polygon))
        val bounds = computeGeometryBounds(geometry)

        // Average of normY(10) and normY(-10) should equal normY(0) approximately
        // (exact symmetry only holds because the polygon has equal north/south extent)
        val expectedCentroidY = (normY(10f) + normY(-10f) + normY(10f) + normY(-10f) + normY(10f)) / 5f
        assertEquals(expectedCentroidY, bounds.centroidNormY, 0.001f)
    }

    @Test
    fun `centroidNormY for northern-hemisphere country is less than 0_5`() {
        // Northern hemisphere → small Y values in Mercator
        val polygon = rect(60f, 50f, 20f, 30f)
        val geometry = CountryGeometry("north", listOf(polygon))
        val bounds = computeGeometryBounds(geometry)
        assertTrue(
            "Northern-hemisphere centroid should have Y < 0.5, got ${bounds.centroidNormY}",
            bounds.centroidNormY < 0.5f
        )
    }

    @Test
    fun `centroidNormY for southern-hemisphere country is greater than 0_5`() {
        val polygon = rect(-30f, -50f, 140f, 160f)
        val geometry = CountryGeometry("south", listOf(polygon))
        val bounds = computeGeometryBounds(geometry)
        assertTrue(
            "Southern-hemisphere centroid should have Y > 0.5, got ${bounds.centroidNormY}",
            bounds.centroidNormY > 0.5f
        )
    }

    // ==================== widthNorm / heightNorm of result ====================

    @Test
    fun `widthNorm of result is positive for a real polygon`() {
        val polygon = rect(10f, -10f, -10f, 10f)
        val geometry = CountryGeometry("sq", listOf(polygon))
        val bounds = computeGeometryBounds(geometry)
        assertTrue("widthNorm must be positive for non-degenerate geometry", bounds.widthNorm > 0f)
    }

    @Test
    fun `heightNorm of result is positive for a real polygon`() {
        val polygon = rect(10f, -10f, -10f, 10f)
        val geometry = CountryGeometry("sq", listOf(polygon))
        val bounds = computeGeometryBounds(geometry)
        assertTrue("heightNorm must be positive for non-degenerate geometry", bounds.heightNorm > 0f)
    }

    @Test
    fun `widthNorm grows as polygon longitude span increases`() {
        val narrow  = rect(10f, -10f, -5f,  5f)
        val wide    = rect(10f, -10f, -40f, 40f)
        val boundsNarrow = computeGeometryBounds(CountryGeometry("n", listOf(narrow)))
        val boundsWide   = computeGeometryBounds(CountryGeometry("w", listOf(wide)))
        assertTrue(boundsWide.widthNorm > boundsNarrow.widthNorm)
    }

    @Test
    fun `heightNorm grows as polygon latitude span increases`() {
        val short = rect(5f, -5f, -10f, 10f)
        val tall  = rect(40f, -40f, -10f, 10f)
        val boundsShort = computeGeometryBounds(CountryGeometry("s", listOf(short)))
        val boundsTall  = computeGeometryBounds(CountryGeometry("t", listOf(tall)))
        assertTrue(boundsTall.heightNorm > boundsShort.heightNorm)
    }

    // ==================== Three-polygon geometry ====================

    @Test
    fun `three-polygon geometry has three entries in polygonBounds`() {
        val p1 = rect(10f, -10f, -10f, 10f)
        val p2 = rect(50f,  40f,  50f, 60f)
        val p3 = rect(-20f, -30f, 100f, 110f)
        val geometry = CountryGeometry("three", listOf(p1, p2, p3))
        val bounds = computeGeometryBounds(geometry)
        assertEquals(3, bounds.polygonBounds.size)
    }

    @Test
    fun `three-polygon global bounds span all polygons`() {
        val p1 = rect(10f, -10f, -10f, 10f)   // near origin
        val p2 = rect(50f,  40f,  50f, 60f)   // north-east
        val p3 = rect(-20f, -30f, 100f, 110f)  // south-east
        val geometry = CountryGeometry("three", listOf(p1, p2, p3))
        val bounds = computeGeometryBounds(geometry)

        // Global X must span from p1's west edge to p3's east edge
        assertTrue(bounds.minX <= normX(-10f))
        assertTrue(bounds.maxX >= normX(110f))
        // Global Y must span from p2's north edge to p3's south edge
        assertTrue(bounds.minY <= normY(50f))
        assertTrue(bounds.maxY >= normY(-30f))
    }

    // ==================== Global bounds contain each per-polygon bbox ====================

    @Test
    fun `global minX is less than or equal to each polygon minX`() {
        val p1 = rect(10f, -10f, -10f,  10f)
        val p2 = rect(50f,  40f,  50f,  60f)
        val geometry = CountryGeometry("multi", listOf(p1, p2))
        val bounds = computeGeometryBounds(geometry)
        bounds.polygonBounds.forEach { pb ->
            assertTrue("global minX ${bounds.minX} should be <= polygon minX ${pb.minX}",
                bounds.minX <= pb.minX + 1e-5f)
        }
    }

    @Test
    fun `global maxX is greater than or equal to each polygon maxX`() {
        val p1 = rect(10f, -10f, -10f,  10f)
        val p2 = rect(50f,  40f,  50f,  60f)
        val geometry = CountryGeometry("multi", listOf(p1, p2))
        val bounds = computeGeometryBounds(geometry)
        bounds.polygonBounds.forEach { pb ->
            assertTrue("global maxX ${bounds.maxX} should be >= polygon maxX ${pb.maxX}",
                bounds.maxX >= pb.maxX - 1e-5f)
        }
    }

    @Test
    fun `global minY is less than or equal to each polygon minY`() {
        val p1 = rect(10f, -10f, -10f,  10f)
        val p2 = rect(50f,  40f,  50f,  60f)
        val geometry = CountryGeometry("multi", listOf(p1, p2))
        val bounds = computeGeometryBounds(geometry)
        bounds.polygonBounds.forEach { pb ->
            assertTrue("global minY ${bounds.minY} should be <= polygon minY ${pb.minY}",
                bounds.minY <= pb.minY + 1e-5f)
        }
    }

    @Test
    fun `global maxY is greater than or equal to each polygon maxY`() {
        val p1 = rect(10f, -10f, -10f,  10f)
        val p2 = rect(50f,  40f,  50f,  60f)
        val geometry = CountryGeometry("multi", listOf(p1, p2))
        val bounds = computeGeometryBounds(geometry)
        bounds.polygonBounds.forEach { pb ->
            assertTrue("global maxY ${bounds.maxY} should be >= polygon maxY ${pb.maxY}",
                bounds.maxY >= pb.maxY - 1e-5f)
        }
    }
}

// ---------------------------------------------------------------------------
// computePolygonBounds — gaps not covered in WorldMapCanvasTest
// ---------------------------------------------------------------------------

/**
 * Additional [computePolygonBounds] tests that extend [ComputePolygonBoundsTest]
 * (WorldMapCanvasTest.kt).
 *
 * Covers:
 *   - Northern-hemisphere-only polygon
 *   - Southern-hemisphere-only polygon
 *   - Eastern-hemisphere-only polygon
 *   - Western-hemisphere-only polygon
 *   - Two-point polygon (degenerate line)
 *   - Near-antimeridian polygon (large positive and large negative longitude)
 *   - Result values are in [0, 1]
 *   - minX < maxX and minY < maxY invariants via Mercator ordering
 */
class ComputePolygonBoundsExtendedTest {

    // ==================== Hemisphere coverage ====================

    @Test
    fun `northern-hemisphere polygon has minY less than 0_5`() {
        // All latitudes positive → all Y values < 0.5 in Mercator
        val polygon = listOf(latLng(80f, 0f), latLng(60f, -20f), latLng(60f, 20f))
        val bounds = computePolygonBounds(polygon)
        assertTrue("Northern-hemisphere polygon: all Y values should be < 0.5", bounds.maxY < 0.5f)
    }

    @Test
    fun `southern-hemisphere polygon has minY greater than 0_5`() {
        val polygon = listOf(latLng(-30f, 0f), latLng(-50f, -10f), latLng(-50f, 10f))
        val bounds = computePolygonBounds(polygon)
        assertTrue("Southern-hemisphere polygon: all Y values should be > 0.5", bounds.minY > 0.5f)
    }

    @Test
    fun `eastern-hemisphere polygon has minX greater than 0_5`() {
        val polygon = listOf(latLng(10f, 90f), latLng(10f, 120f), latLng(-10f, 120f), latLng(-10f, 90f))
        val bounds = computePolygonBounds(polygon)
        assertTrue("Eastern-hemisphere polygon: all X values should be > 0.5", bounds.minX > 0.5f)
    }

    @Test
    fun `western-hemisphere polygon has maxX less than 0_5`() {
        val polygon = listOf(latLng(10f, -90f), latLng(10f, -60f), latLng(-10f, -60f), latLng(-10f, -90f))
        val bounds = computePolygonBounds(polygon)
        assertTrue("Western-hemisphere polygon: all X values should be < 0.5", bounds.maxX < 0.5f)
    }

    // ==================== Two-point polygon (degenerate line) ====================

    @Test
    fun `two-point polygon returns bounds spanning both points`() {
        val p1 = latLng(20f,  30f)
        val p2 = latLng(-20f, -30f)
        val bounds = computePolygonBounds(listOf(p1, p2))

        val expectedMinX = normX(-30f)
        val expectedMaxX = normX(30f)
        val expectedMinY = normY(20f)   // north → smaller Y
        val expectedMaxY = normY(-20f)  // south → larger Y

        assertEquals(expectedMinX, bounds.minX, 0.0001f)
        assertEquals(expectedMaxX, bounds.maxX, 0.0001f)
        assertEquals(expectedMinY, bounds.minY, 0.0001f)
        assertEquals(expectedMaxY, bounds.maxY, 0.0001f)
    }

    // ==================== Near-antimeridian polygons ====================

    @Test
    fun `polygon near positive antimeridian has maxX close to 1`() {
        val polygon = listOf(
            latLng(10f, 170f), latLng(10f, 178f),
            latLng(-10f, 178f), latLng(-10f, 170f)
        )
        val bounds = computePolygonBounds(polygon)
        assertTrue("Polygon near +180° should have maxX close to 1", bounds.maxX > 0.97f)
    }

    @Test
    fun `polygon near negative antimeridian has minX close to 0`() {
        val polygon = listOf(
            latLng(10f, -178f), latLng(10f, -170f),
            latLng(-10f, -170f), latLng(-10f, -178f)
        )
        val bounds = computePolygonBounds(polygon)
        assertTrue("Polygon near -180° should have minX close to 0", bounds.minX < 0.03f)
    }

    // ==================== Result values always in [0, 1] ====================

    @Test
    fun `bounds values are in valid normalized range for equatorial polygon`() {
        val polygon = listOf(latLng(10f, -10f), latLng(10f, 10f), latLng(-10f, 0f))
        val bounds = computePolygonBounds(polygon)
        assertTrue(bounds.minX in 0f..1f)
        assertTrue(bounds.maxX in 0f..1f)
        assertTrue(bounds.minY in 0f..1f)
        assertTrue(bounds.maxY in 0f..1f)
    }

    @Test
    fun `bounds values are in valid range for polar polygon`() {
        val polygon = listOf(latLng(80f, -10f), latLng(80f, 10f), latLng(75f, 0f))
        val bounds = computePolygonBounds(polygon)
        assertTrue(bounds.minX in 0f..1f)
        assertTrue(bounds.maxX in 0f..1f)
        assertTrue(bounds.minY in 0f..1f)
        assertTrue(bounds.maxY in 0f..1f)
    }

    // ==================== Ordering invariants ====================

    @Test
    fun `minX is strictly less than maxX for polygon with longitude spread`() {
        val polygon = listOf(latLng(0f, -30f), latLng(0f, 30f), latLng(10f, 0f))
        val bounds = computePolygonBounds(polygon)
        assertTrue(bounds.minX < bounds.maxX)
    }

    @Test
    fun `minY is strictly less than maxY for polygon with latitude spread`() {
        val polygon = listOf(latLng(30f, 0f), latLng(-30f, 0f), latLng(0f, 10f))
        val bounds = computePolygonBounds(polygon)
        assertTrue(bounds.minY < bounds.maxY)
    }

    @Test
    fun `polygon bounds are consistent with MercatorProjection output`() {
        // Build a polygon and manually compute expected bounds using MercatorProjection
        val points = listOf(latLng(45f, -90f), latLng(45f, 90f), latLng(-45f, 90f), latLng(-45f, -90f))
        val bounds = computePolygonBounds(points)

        assertEquals(normX(-90f), bounds.minX, 0.0001f)
        assertEquals(normX(90f),  bounds.maxX, 0.0001f)
        assertEquals(normY(45f),  bounds.minY, 0.0001f)  // north → small Y
        assertEquals(normY(-45f), bounds.maxY, 0.0001f)  // south → large Y
    }
}
