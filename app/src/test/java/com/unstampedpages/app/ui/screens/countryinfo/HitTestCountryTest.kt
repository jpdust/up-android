package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.ui.geometry.Offset
import com.unstampedpages.app.data.model.CountryGeometry
import com.unstampedpages.app.data.model.LatLng
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HitTestCountryTest {

    private val mapWidth = 1000f
    private val mapHeight = 500f

    private fun identityMatrixValues(): FloatArray {
        val m = android.graphics.Matrix()
        val values = FloatArray(9)
        m.getValues(values)
        return values
    }

    private fun makeSnapshot(
        geometries: List<CountryGeometry> = emptyList(),
        countryBounds: Map<String, CountryBounds> = emptyMap(),
        scale: Float = 1f
    ): HitTestSnapshot = HitTestSnapshot(
        matrixValues = identityMatrixValues(),
        mapWidth = mapWidth,
        mapHeight = mapHeight,
        geometries = geometries,
        countryBounds = countryBounds,
        currentScale = scale
    )

    private fun polygonAtLatLng(
        latCenter: Float,
        lngCenter: Float,
        halfSize: Float = 5f
    ): List<LatLng> = listOf(
        LatLng(latCenter + halfSize, lngCenter - halfSize),
        LatLng(latCenter + halfSize, lngCenter + halfSize),
        LatLng(latCenter - halfSize, lngCenter + halfSize),
        LatLng(latCenter - halfSize, lngCenter - halfSize),
        LatLng(latCenter + halfSize, lngCenter - halfSize)
    )

    private fun tapPositionFor(lat: Float, lng: Float): Offset {
        val x = MercatorProjection.longitudeToX(lng) * mapWidth
        val y = MercatorProjection.latitudeToY(lat) * mapHeight
        return Offset(x, y)
    }

    // ── Path 1: proximity hit ────────────────────────────────────────────

    @Test
    fun `proximity hit returns repo id and territory name`() {
        val cx = MercatorProjection.longitudeToX(55f)
        val cy = MercatorProjection.latitudeToY(-4f)
        val halfNorm = (SMALL_COUNTRY_THRESHOLD_PX * 0.375f) / mapWidth
        val sycBounds = CountryBounds(
            centroidNormX = cx, centroidNormY = cy,
            minX = cx - halfNorm, maxX = cx + halfNorm,
            minY = cy - halfNorm, maxY = cy + halfNorm,
            polygonBounds = listOf(PolygonBounds(cx - halfNorm, cx + halfNorm, cy - halfNorm, cy + halfNorm))
        )

        val snapshot = makeSnapshot(countryBounds = mapOf("SYC" to sycBounds))
        val tap = Offset(cx * mapWidth, cy * mapHeight)
        val (repoId, territoryName) = hitTestCountry(tap, snapshot, Locale.ENGLISH)

        assertEquals("sc", repoId)
        assertNull(territoryName)
    }

    // ── Path 2: ray-cast hit ─────────────────────────────────────────────

    @Test
    fun `ray-cast hit returns repo id for sovereign country`() {
        val polygon = polygonAtLatLng(48f, 2f)
        val geometry = CountryGeometry("FRA", listOf(polygon))
        val bounds = computeGeometryBounds(geometry)

        val snapshot = makeSnapshot(
            geometries = listOf(geometry),
            countryBounds = mapOf("FRA" to bounds)
        )
        val tap = tapPositionFor(48f, 2f)
        val (repoId, territoryName) = hitTestCountry(tap, snapshot, Locale.ENGLISH)

        assertEquals("fr", repoId)
        assertNull(territoryName)
    }

    @Test
    fun `ray-cast hit returns territory display name for known territory`() {
        val polygon = polygonAtLatLng(-22f, -159f)
        val geometry = CountryGeometry("COK", listOf(polygon))
        val bounds = computeGeometryBounds(geometry)

        val snapshot = makeSnapshot(
            geometries = listOf(geometry),
            countryBounds = mapOf("COK" to bounds)
        )
        val tap = tapPositionFor(-22f, -159f)
        val (repoId, territoryName) = hitTestCountry(tap, snapshot, Locale.ENGLISH)

        assertEquals("nz", repoId)
        assertNotNull(territoryName)
    }

    // ── Path 3: no hit ───────────────────────────────────────────────────

    @Test
    fun `tap in empty ocean returns null pair`() {
        val polygon = polygonAtLatLng(48f, 2f)
        val geometry = CountryGeometry("FRA", listOf(polygon))
        val bounds = computeGeometryBounds(geometry)

        val snapshot = makeSnapshot(
            geometries = listOf(geometry),
            countryBounds = mapOf("FRA" to bounds)
        )
        val tap = tapPositionFor(-40f, -120f)
        val (repoId, territoryName) = hitTestCountry(tap, snapshot, Locale.ENGLISH)

        assertNull(repoId)
        assertNull(territoryName)
    }

    @Test
    fun `empty snapshot returns null pair`() {
        val snapshot = makeSnapshot()
        val tap = Offset(500f, 250f)
        val (repoId, territoryName) = hitTestCountry(tap, snapshot, Locale.ENGLISH)

        assertNull(repoId)
        assertNull(territoryName)
    }

    // ── Priority: proximity wins over ray-cast ───────────────────────────

    @Test
    fun `dot-marker country wins over parent polygon`() {
        val polygon = polygonAtLatLng(0f, 0f, halfSize = 20f)
        val parentGeom = CountryGeometry("FRA", listOf(polygon))
        val parentBounds = computeGeometryBounds(parentGeom)

        val dotCx = MercatorProjection.longitudeToX(0f)
        val dotCy = MercatorProjection.latitudeToY(0f)
        val halfNorm = (SMALL_COUNTRY_THRESHOLD_PX * 0.375f) / mapWidth
        val dotBounds = CountryBounds(
            centroidNormX = dotCx, centroidNormY = dotCy,
            minX = dotCx - halfNorm, maxX = dotCx + halfNorm,
            minY = dotCy - halfNorm, maxY = dotCy + halfNorm,
            polygonBounds = listOf(PolygonBounds(dotCx - halfNorm, dotCx + halfNorm, dotCy - halfNorm, dotCy + halfNorm))
        )

        val snapshot = makeSnapshot(
            geometries = listOf(parentGeom),
            countryBounds = mapOf("FRA" to parentBounds, "VAT" to dotBounds)
        )
        val tap = Offset(dotCx * mapWidth, dotCy * mapHeight)
        val (repoId, _) = hitTestCountry(tap, snapshot, Locale.ENGLISH)

        assertEquals("va", repoId)
    }

    // ── Matrix transformation ────────────────────────────────────────────

    @Test
    fun `scaled matrix adjusts tap coordinates correctly`() {
        val polygon = polygonAtLatLng(0f, 0f, halfSize = 10f)
        val geometry = CountryGeometry("FRA", listOf(polygon))
        val bounds = computeGeometryBounds(geometry)

        val scaledMatrix = android.graphics.Matrix()
        scaledMatrix.setScale(1f, 1f)
        val values = FloatArray(9)
        scaledMatrix.getValues(values)

        val snapshot = HitTestSnapshot(
            matrixValues = values,
            mapWidth = mapWidth,
            mapHeight = mapHeight,
            geometries = listOf(geometry),
            countryBounds = mapOf("FRA" to bounds),
            currentScale = 1f
        )
        val tap = tapPositionFor(0f, 0f)
        val (repoId, _) = hitTestCountry(tap, snapshot, Locale.ENGLISH)

        assertEquals("fr", repoId)
    }
}
