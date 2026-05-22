package com.unstampedpages.app.ui.screens.countryinfo

import com.unstampedpages.app.data.model.CountryGeometry
import com.unstampedpages.app.data.model.LatLng
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for the bounding-box pre-filter inside [findCountryAtNormalizedPoint].
 *
 * The key invariant: when a [CountryBounds] entry is present for a geometry and the tap
 * point falls outside that bbox, the polygon ray-cast is skipped entirely.  The critical
 * test proves this by showing that a geometry whose polygon *would* match is skipped when
 * its bbox explicitly excludes the tap point.
 */
class WorldMapTapTest {

    // --- helpers ---------------------------------------------------------------

    /**
     * Simple clockwise square polygon in lat/lng space centered on the origin
     * (lat ∈ [-10, 10], lng ∈ [-10, 10]).  The equator/prime-meridian point (0, 0) is
     * strictly inside this polygon.
     */
    private fun squareAroundOrigin(): List<LatLng> = listOf(
        LatLng(lat = 10f, lng = -10f),
        LatLng(lat = 10f, lng = 10f),
        LatLng(lat = -10f, lng = 10f),
        LatLng(lat = -10f, lng = -10f),
        LatLng(lat = 10f, lng = -10f)   // close ring
    )

    /**
     * Tight bbox that matches the square polygon above.
     * lat=10 (north) maps to a *smaller* normalized Y; lat=-10 (south) maps to a *larger*
     * normalized Y — so minY/maxY are set accordingly.
     */
    private fun tightBoundsForSquare(): CountryBounds = CountryBounds(
        centroidNormX = MercatorProjection.longitudeToX(0f),
        centroidNormY = MercatorProjection.latitudeToY(0f),
        minX = MercatorProjection.longitudeToX(-10f),
        maxX = MercatorProjection.longitudeToX(10f),
        minY = MercatorProjection.latitudeToY(10f),   // north → small Y
        maxY = MercatorProjection.latitudeToY(-10f)   // south → large Y
    )

    /**
     * Normalized tap point at the equator / prime meridian — the center of
     * [squareAroundOrigin].
     */
    private val tapX = MercatorProjection.longitudeToX(0f)   // == 0.5
    private val tapY = MercatorProjection.latitudeToY(0f)

    /**
     * A bbox positioned far from the origin tap point so the pre-filter rejects it.
     */
    private val excludingBounds = CountryBounds(
        centroidNormX = 0.85f,
        centroidNormY = 0.15f,
        minX = 0.80f,
        maxX = 0.90f,
        minY = 0.10f,
        maxY = 0.20f
    )

    // --- basic behaviour -------------------------------------------------------

    @Test
    fun `empty geometry list returns null`() {
        val result = findCountryAtNormalizedPoint(tapX, tapY, emptyList())
        assertNull(result)
    }

    @Test
    fun `point outside all polygons returns null`() {
        val geometry = CountryGeometry("test", listOf(squareAroundOrigin()))
        // tap far away from the polygon (normalized coords near 0.9, 0.1)
        val result = findCountryAtNormalizedPoint(0.9f, 0.1f, listOf(geometry))
        assertNull(result)
    }

    // --- ray-cast without bbox pre-filter ---------------------------------------

    @Test
    fun `point inside polygon found when no bounds map provided`() {
        val geometry = CountryGeometry("test", listOf(squareAroundOrigin()))
        val result = findCountryAtNormalizedPoint(tapX, tapY, listOf(geometry))
        assertEquals("test", result)
    }

    @Test
    fun `point inside polygon found when bounds map is empty`() {
        val geometry = CountryGeometry("test", listOf(squareAroundOrigin()))
        val result = findCountryAtNormalizedPoint(tapX, tapY, listOf(geometry), emptyMap())
        assertEquals("test", result)
    }

    // --- bbox pre-filter: pass-through ----------------------------------------

    @Test
    fun `point inside polygon found when tight bbox contains it`() {
        val geometry = CountryGeometry("test", listOf(squareAroundOrigin()))
        val bounds = mapOf("test" to tightBoundsForSquare())
        val result = findCountryAtNormalizedPoint(tapX, tapY, listOf(geometry), bounds)
        assertEquals("test", result)
    }

    @Test
    fun `geometry without bounds entry falls back to ray cast and is found`() {
        val geometry = CountryGeometry("test", listOf(squareAroundOrigin()))
        // bounds map exists but has no entry for "test"
        val bounds = mapOf("other" to excludingBounds)
        val result = findCountryAtNormalizedPoint(tapX, tapY, listOf(geometry), bounds)
        assertEquals("test", result)
    }

    // --- bbox pre-filter: skip -------------------------------------------------

    /**
     * CRITICAL: proves the pre-filter actually runs.
     *
     * The polygon contains the tap point (verified by the baseline assertion), but the
     * supplied bbox excludes it.  The pre-filter must skip the geometry, returning null
     * instead of the country that the ray-cast alone would have found.
     */
    @Test
    fun `bbox filter skips geometry even when polygon contains point`() {
        val geometry = CountryGeometry("test", listOf(squareAroundOrigin()))

        // Baseline: ray cast alone finds the country
        val baseline = findCountryAtNormalizedPoint(tapX, tapY, listOf(geometry))
        assertEquals("Baseline ray-cast should find country", "test", baseline)

        // With an excluding bbox: geometry is skipped → null
        val filtered = findCountryAtNormalizedPoint(
            tapX, tapY, listOf(geometry),
            countryBounds = mapOf("test" to excludingBounds)
        )
        assertNull("Pre-filter should skip geometry whose bbox excludes the tap point", filtered)
    }

    @Test
    fun `point outside bbox not matching polygon returns null`() {
        val geometry = CountryGeometry("test", listOf(squareAroundOrigin()))
        val bounds = mapOf("test" to tightBoundsForSquare())
        // tap well outside the polygon and the bbox
        val result = findCountryAtNormalizedPoint(0.9f, 0.1f, listOf(geometry), bounds)
        assertNull(result)
    }

    // --- multiple geometries ---------------------------------------------------

    @Test
    fun `first geometry skipped by bbox, second geometry found`() {
        val decoyGeometry = CountryGeometry("decoy", listOf(squareAroundOrigin()))
        val targetGeometry = CountryGeometry("target", listOf(squareAroundOrigin()))

        val bounds = mapOf(
            "decoy" to excludingBounds,          // bbox excludes tap → skipped
            "target" to tightBoundsForSquare()   // bbox includes tap → ray-cast runs → found
        )

        val result = findCountryAtNormalizedPoint(tapX, tapY, listOf(decoyGeometry, targetGeometry), bounds)
        assertEquals("target", result)
    }

    @Test
    fun `first geometry found stops iteration`() {
        val first = CountryGeometry("first", listOf(squareAroundOrigin()))
        val second = CountryGeometry("second", listOf(squareAroundOrigin()))

        val result = findCountryAtNormalizedPoint(tapX, tapY, listOf(first, second))
        assertEquals("first", result)   // stops at the first match
    }

    // --- multipolygon ----------------------------------------------------------

    @Test
    fun `multipolygon geometry found when tap is in second polygon`() {
        val outsidePolygon = listOf(
            LatLng(lat = 50f, lng = 150f),
            LatLng(lat = 50f, lng = 170f),
            LatLng(lat = 30f, lng = 170f),
            LatLng(lat = 30f, lng = 150f),
            LatLng(lat = 50f, lng = 150f)
        )
        val insidePolygon = squareAroundOrigin()
        val geometry = CountryGeometry("multi", listOf(outsidePolygon, insidePolygon))

        val result = findCountryAtNormalizedPoint(tapX, tapY, listOf(geometry))
        assertEquals("multi", result)
    }

    @Test
    fun `multipolygon geometry found when tap is in first polygon`() {
        val insidePolygon = squareAroundOrigin()
        val outsidePolygon = listOf(
            LatLng(lat = 50f, lng = 150f),
            LatLng(lat = 50f, lng = 170f),
            LatLng(lat = 30f, lng = 170f),
            LatLng(lat = 30f, lng = 150f),
            LatLng(lat = 50f, lng = 150f)
        )
        val geometry = CountryGeometry("multi", listOf(insidePolygon, outsidePolygon))

        val result = findCountryAtNormalizedPoint(tapX, tapY, listOf(geometry))
        assertEquals("multi", result)
    }

    @Test
    fun `multipolygon geometry skipped entirely when bbox excludes tap`() {
        val insidePolygon = squareAroundOrigin()
        val geometry = CountryGeometry("multi", listOf(insidePolygon, insidePolygon))

        val filtered = findCountryAtNormalizedPoint(
            tapX, tapY, listOf(geometry),
            countryBounds = mapOf("multi" to excludingBounds)
        )
        assertNull(filtered)
    }

    // --- MercatorProjection round-trip sanity ----------------------------------

    @Test
    fun `longitudeToX and xToLongitude are inverse operations`() {
        val testLngs = listOf(-180f, -90f, 0f, 90f, 180f)
        for (lng in testLngs) {
            val normalized = MercatorProjection.longitudeToX(lng)
            val recovered = MercatorProjection.xToLongitude(normalized)
            assertEquals("Round-trip failed for lng=$lng", lng, recovered, 0.001f)
        }
    }

    @Test
    fun `latitudeToY and yToLatitude are inverse operations`() {
        val testLats = listOf(-80f, -45f, 0f, 45f, 80f)
        for (lat in testLats) {
            val normalized = MercatorProjection.latitudeToY(lat)
            val recovered = MercatorProjection.yToLatitude(normalized)
            assertEquals("Round-trip failed for lat=$lat", lat, recovered, 0.01f)
        }
    }

    @Test
    fun `equator maps to approximately 0_5 normalized X for prime meridian`() {
        assertEquals(0.5f, MercatorProjection.longitudeToX(0f), 0.001f)
    }
}

// ---------------------------------------------------------------------------
// Hole-exclusion filter (Lesotho / enclosed-country scenario)
// ---------------------------------------------------------------------------

/**
 * Tests for the hole-exclusion logic in [findCountryAtNormalizedPoint].
 *
 * When a tap lands inside a matched polygon but also inside one of that geometry's
 * hole rings, the geometry is skipped (`continue@geometryLoop`) so that an enclosed
 * country whose polygon covers the same area can be found instead.
 *
 * The canonical real-world case is Lesotho (LSO) enclosed by South Africa (ZAF):
 *   - ZAF is listed first; its polygon covers the whole area including Lesotho's land.
 *   - ZAF's holes list includes Lesotho's boundary ring.
 *   - LSO is listed second with a polygon matching that boundary.
 *   - A tap inside Lesotho must return "LSO", not "ZAF".
 */
class HoleExclusionTest {

    // Outer polygon: square lat ∈ [-10, 10], lng ∈ [-10, 10]
    private val outerSquare = listOf(
        LatLng(lat = 10f, lng = -10f),
        LatLng(lat = 10f, lng = 10f),
        LatLng(lat = -10f, lng = 10f),
        LatLng(lat = -10f, lng = -10f),
        LatLng(lat = 10f, lng = -10f)
    )

    // Inner hole (and LSO polygon): square lat ∈ [-5, 5], lng ∈ [-5, 5]
    private val innerSquare = listOf(
        LatLng(lat = 5f, lng = -5f),
        LatLng(lat = 5f, lng = 5f),
        LatLng(lat = -5f, lng = 5f),
        LatLng(lat = -5f, lng = -5f),
        LatLng(lat = 5f, lng = -5f)
    )

    // Tap at origin — inside both outerSquare and innerSquare
    private val tapInsideHoleX = MercatorProjection.longitudeToX(0f)
    private val tapInsideHoleY = MercatorProjection.latitudeToY(0f)

    // Tap at lat=8, lng=0 — inside outerSquare but outside innerSquare
    private val tapOutsideHoleX = MercatorProjection.longitudeToX(0f)
    private val tapOutsideHoleY = MercatorProjection.latitudeToY(8f)

    @Test
    fun `point inside polygon hole causes geometry to be skipped — returns null`() {
        val geometry = CountryGeometry("ZAF", listOf(outerSquare), listOf(innerSquare))
        val result = findCountryAtNormalizedPoint(tapInsideHoleX, tapInsideHoleY, listOf(geometry))
        assertNull("Point inside hole should cause geometry to be skipped", result)
    }

    @Test
    fun `point inside polygon but outside hole returns the geometry`() {
        val geometry = CountryGeometry("ZAF", listOf(outerSquare), listOf(innerSquare))
        val result = findCountryAtNormalizedPoint(tapOutsideHoleX, tapOutsideHoleY, listOf(geometry))
        assertEquals("ZAF", result)
    }

    @Test
    fun `geometry with empty holes list is unaffected — point inside polygon found normally`() {
        val geometry = CountryGeometry("test", listOf(outerSquare))   // no holes
        val result = findCountryAtNormalizedPoint(tapInsideHoleX, tapInsideHoleY, listOf(geometry))
        assertEquals("test", result)
    }

    /**
     * CRITICAL — the Lesotho scenario.
     *
     * ZAF (listed first) has a hole at Lesotho's boundary.  LSO (listed second) covers
     * that same area.  A tap at the origin must return "LSO", not "ZAF".
     */
    @Test
    fun `ZAF with Lesotho hole causes fallthrough — tap returns LSO`() {
        val zaf = CountryGeometry("ZAF", listOf(outerSquare), listOf(innerSquare))
        val lso = CountryGeometry("LSO", listOf(innerSquare))   // no holes

        val result = findCountryAtNormalizedPoint(tapInsideHoleX, tapInsideHoleY, listOf(zaf, lso))
        assertEquals("Tap inside Lesotho hole should return LSO, not ZAF", "LSO", result)
    }

    @Test
    fun `tap outside hole returns ZAF even when LSO is present`() {
        val zaf = CountryGeometry("ZAF", listOf(outerSquare), listOf(innerSquare))
        val lso = CountryGeometry("LSO", listOf(innerSquare))

        val result = findCountryAtNormalizedPoint(tapOutsideHoleX, tapOutsideHoleY, listOf(zaf, lso))
        assertEquals("Tap outside Lesotho hole should return ZAF", "ZAF", result)
    }

    @Test
    fun `multiple holes — point inside any hole causes geometry to be skipped`() {
        val hole1 = listOf(
            LatLng(lat = 5f, lng = -5f), LatLng(lat = 5f, lng = -1f),
            LatLng(lat = 1f, lng = -1f), LatLng(lat = 1f, lng = -5f),
            LatLng(lat = 5f, lng = -5f)
        )
        val hole2 = listOf(
            LatLng(lat = 5f, lng = 1f), LatLng(lat = 5f, lng = 5f),
            LatLng(lat = 1f, lng = 5f), LatLng(lat = 1f, lng = 1f),
            LatLng(lat = 5f, lng = 1f)
        )
        // Tap at (lat=3, lng=3) — inside hole2 (lat [1,5], lng [1,5])
        val tapInHole2X = MercatorProjection.longitudeToX(3f)
        val tapInHole2Y = MercatorProjection.latitudeToY(3f)

        val geometry = CountryGeometry("outer", listOf(outerSquare), listOf(hole1, hole2))
        val result = findCountryAtNormalizedPoint(tapInHole2X, tapInHole2Y, listOf(geometry))
        assertNull("Point inside any hole should skip the geometry", result)
    }

    @Test
    fun `hole in first geometry causes fallthrough to second geometry which has no holes`() {
        val parent = CountryGeometry("parent", listOf(outerSquare), listOf(innerSquare))
        val child = CountryGeometry("child", listOf(innerSquare))

        val result = findCountryAtNormalizedPoint(tapInsideHoleX, tapInsideHoleY, listOf(parent, child))
        assertEquals("child", result)
    }

    @Test
    fun `hole exclusion still respects bbox pre-filter`() {
        // Geometry with a hole, but overall bbox excludes the tap point entirely.
        // The bbox filter must short-circuit before the hole check is even reached.
        val excludingBounds = CountryBounds(
            centroidNormX = 0.9f, centroidNormY = 0.1f,
            minX = 0.85f, maxX = 0.95f, minY = 0.05f, maxY = 0.15f
        )
        val geometry = CountryGeometry("ZAF", listOf(outerSquare), listOf(innerSquare))

        val result = findCountryAtNormalizedPoint(
            tapInsideHoleX, tapInsideHoleY,
            listOf(geometry),
            countryBounds = mapOf("ZAF" to excludingBounds)
        )
        assertNull("Bbox pre-filter should reject geometry before hole check runs", result)
    }
}

// ---------------------------------------------------------------------------
// Per-polygon bounding-box filter
// ---------------------------------------------------------------------------

/**
 * Tests for the second-level per-polygon bbox pre-filter added to handle countries
 * like Russia and the USA whose *overall* bbox spans nearly the full map width due
 * to outlier islands near the antimeridian.
 *
 * Setup mirrors those real-world geometries: a "mainland" polygon that contains the
 * tap point and a "far-away island" polygon whose per-polygon bbox is on the other
 * side of the map — with an overall CountryBounds that covers both.
 */
class PerPolygonBboxFilterTest {

    // Mainland polygon centred on origin — tap point (0°, 0°) is inside.
    private val mainlandPolygon = listOf(
        LatLng(lat = 10f, lng = -10f),
        LatLng(lat = 10f, lng = 10f),
        LatLng(lat = -10f, lng = 10f),
        LatLng(lat = -10f, lng = -10f),
        LatLng(lat = 10f, lng = -10f)
    )

    // "Island" far away (near the antimeridian, positive side) — tap point is not inside.
    private val farIslandPolygon = listOf(
        LatLng(lat = 60f, lng = 165f),
        LatLng(lat = 60f, lng = 175f),
        LatLng(lat = 55f, lng = 175f),
        LatLng(lat = 55f, lng = 165f),
        LatLng(lat = 60f, lng = 165f)
    )

    // Tap point: origin (0°, 0°)
    private val tapX = MercatorProjection.longitudeToX(0f)
    private val tapY = MercatorProjection.latitudeToY(0f)

    /**
     * Overall bbox that (like Russia) spans almost the full X range because it
     * includes both the mainland near X=0.5 and the far island near X=0.97.
     */
    private fun wideOverallBounds(
        polyBounds: List<PolygonBounds> = emptyList()
    ) = CountryBounds(
        centroidNormX = tapX,
        centroidNormY = tapY,
        minX = MercatorProjection.longitudeToX(-10f),    // mainland west edge
        maxX = MercatorProjection.longitudeToX(175f),    // island east edge
        minY = MercatorProjection.latitudeToY(60f),      // island north (small Y)
        maxY = MercatorProjection.latitudeToY(-10f),     // mainland south (large Y)
        polygonBounds = polyBounds
    )

    private fun mainlandPolyBounds() = PolygonBounds(
        minX = MercatorProjection.longitudeToX(-10f),
        maxX = MercatorProjection.longitudeToX(10f),
        minY = MercatorProjection.latitudeToY(10f),
        maxY = MercatorProjection.latitudeToY(-10f)
    )

    private fun islandPolyBounds() = PolygonBounds(
        minX = MercatorProjection.longitudeToX(165f),
        maxX = MercatorProjection.longitudeToX(175f),
        minY = MercatorProjection.latitudeToY(60f),
        maxY = MercatorProjection.latitudeToY(55f)
    )

    // --- baseline behaviour ----------------------------------------------------

    @Test
    fun `overall bbox passes for wide country — ray cast still finds mainland`() {
        val geometry = CountryGeometry("RUS", listOf(farIslandPolygon, mainlandPolygon))
        // Wide overall bounds, no per-polygon bounds → falls back to full ray-cast
        val result = findCountryAtNormalizedPoint(
            tapX, tapY, listOf(geometry),
            countryBounds = mapOf("RUS" to wideOverallBounds())
        )
        assertEquals("RUS", result)
    }

    // --- per-polygon filter skips non-matching polygons ------------------------

    @Test
    fun `per-polygon filter skips island polygon and finds mainland`() {
        // Island is listed first — without per-polygon filter it would be ray-cast;
        // with filter its bbox (near antimeridian) excludes the origin tap → skipped.
        val geometry = CountryGeometry("RUS", listOf(farIslandPolygon, mainlandPolygon))
        val bounds = wideOverallBounds(
            polyBounds = listOf(islandPolyBounds(), mainlandPolyBounds())
        )

        val result = findCountryAtNormalizedPoint(
            tapX, tapY, listOf(geometry),
            countryBounds = mapOf("RUS" to bounds)
        )
        assertEquals("RUS", result)
    }

    @Test
    fun `per-polygon filter skips all polygons when tap misses every individual bbox`() {
        // Tap is at origin; both polygons are far away on the right side of the map.
        val rightPolygon1 = listOf(
            LatLng(lat = 10f, lng = 150f), LatLng(lat = 10f, lng = 160f),
            LatLng(lat = -10f, lng = 160f), LatLng(lat = -10f, lng = 150f),
            LatLng(lat = 10f, lng = 150f)
        )
        val rightPolygon2 = listOf(
            LatLng(lat = 10f, lng = 165f), LatLng(lat = 10f, lng = 175f),
            LatLng(lat = -10f, lng = 175f), LatLng(lat = -10f, lng = 165f),
            LatLng(lat = 10f, lng = 165f)
        )
        val geometry = CountryGeometry("FAR", listOf(rightPolygon1, rightPolygon2))
        val bounds = CountryBounds(
            centroidNormX = 0.92f, centroidNormY = 0.5f,
            // Overall bbox spans from 0.0 to 0.97 — passes level-1 filter for origin tap
            minX = 0.0f, maxX = 0.97f, minY = 0.3f, maxY = 0.7f,
            polygonBounds = listOf(
                PolygonBounds(
                    MercatorProjection.longitudeToX(150f), MercatorProjection.longitudeToX(160f),
                    MercatorProjection.latitudeToY(10f), MercatorProjection.latitudeToY(-10f)
                ),
                PolygonBounds(
                    MercatorProjection.longitudeToX(165f), MercatorProjection.longitudeToX(175f),
                    MercatorProjection.latitudeToY(10f), MercatorProjection.latitudeToY(-10f)
                )
            )
        )

        val result = findCountryAtNormalizedPoint(
            tapX, tapY, listOf(geometry),
            countryBounds = mapOf("FAR" to bounds)
        )
        assertNull("Per-polygon filter should skip both polygons", result)
    }

    /**
     * CRITICAL: proves the per-polygon filter is the deciding factor.
     *
     * Without it: overall bbox passes → both polygons ray-cast → mainland found.
     * With a per-polygon bbox that excludes the mainland: mainland is skipped → null.
     */
    @Test
    fun `per-polygon filter blocks mainland when its individual bbox is artificially excluded`() {
        val geometry = CountryGeometry("RUS", listOf(mainlandPolygon))

        // Baseline: without per-polygon filter, mainland is found
        val withoutFilter = findCountryAtNormalizedPoint(
            tapX, tapY, listOf(geometry),
            countryBounds = mapOf("RUS" to wideOverallBounds())
        )
        assertEquals("Per-polygon filter baseline", "RUS", withoutFilter)

        // With a per-polygon bbox that excludes the origin tap, mainland is skipped
        val excludingPolyBounds = PolygonBounds(
            minX = 0.8f, maxX = 0.9f, minY = 0.1f, maxY = 0.2f
        )
        val withFilter = findCountryAtNormalizedPoint(
            tapX, tapY, listOf(geometry),
            countryBounds = mapOf("RUS" to wideOverallBounds(listOf(excludingPolyBounds)))
        )
        assertNull("Per-polygon filter should skip mainland", withFilter)
    }

    @Test
    fun `partial per-polygon list falls back to ray-cast for unlisted polygons`() {
        // geometry has 2 polygons; polygonBounds only covers the first.
        // Second polygon (mainland) has no per-polygon entry → ray-cast → found.
        val geometry = CountryGeometry("RUS", listOf(farIslandPolygon, mainlandPolygon))
        val bounds = wideOverallBounds(
            polyBounds = listOf(islandPolyBounds())  // only one entry for two polygons
        )

        val result = findCountryAtNormalizedPoint(
            tapX, tapY, listOf(geometry),
            countryBounds = mapOf("RUS" to bounds)
        )
        assertEquals("RUS", result)
    }
}
