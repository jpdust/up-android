package com.unstampedpages.app.data.model

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.*
import org.junit.Test

class CountryGeometryTest {

    @Test
    fun `CountryGeometry holds correct values`() {
        val polygon = listOf(
            LatLng(0f, 0f),
            LatLng(1f, 0f),
            LatLng(1f, 1f),
            LatLng(0f, 1f)
        )
        val geometry = CountryGeometry(
            countryId = "test",
            polygons = listOf(polygon)
        )

        assertEquals("test", geometry.countryId)
        assertEquals(1, geometry.polygons.size)
        assertEquals(4, geometry.polygons[0].size)
    }

    @Test
    fun `CountryGeometry with multiple polygons`() {
        val polygon1 = listOf(LatLng(0f, 0f), LatLng(1f, 0f), LatLng(0f, 1f))
        val polygon2 = listOf(LatLng(2f, 2f), LatLng(3f, 2f), LatLng(2f, 3f))

        val geometry = CountryGeometry(
            countryId = "multi",
            polygons = listOf(polygon1, polygon2)
        )

        assertEquals(2, geometry.polygons.size)
    }
}

class LatLngTest {

    @Test
    fun `LatLng holds correct values`() {
        val latLng = LatLng(lat = 45.5f, lng = -122.6f)

        assertEquals(45.5f, latLng.lat, 0.001f)
        assertEquals(-122.6f, latLng.lng, 0.001f)
    }

    @Test
    fun `LatLng toScreenCoordinates at origin`() {
        val latLng = LatLng(lat = 0f, lng = -180f)
        val offset = latLng.toScreenCoordinates(width = 360f, height = 180f)

        assertEquals(0f, offset.x, 0.001f)
        assertEquals(90f, offset.y, 0.001f)
    }

    @Test
    fun `LatLng toScreenCoordinates at center`() {
        val latLng = LatLng(lat = 0f, lng = 0f)
        val offset = latLng.toScreenCoordinates(width = 360f, height = 180f)

        assertEquals(180f, offset.x, 0.001f)
        assertEquals(90f, offset.y, 0.001f)
    }

    @Test
    fun `LatLng toScreenCoordinates at top right`() {
        val latLng = LatLng(lat = 90f, lng = 180f)
        val offset = latLng.toScreenCoordinates(width = 360f, height = 180f)

        assertEquals(360f, offset.x, 0.001f)
        assertEquals(0f, offset.y, 0.001f)
    }

    @Test
    fun `LatLng toScreenCoordinates at bottom left`() {
        val latLng = LatLng(lat = -90f, lng = -180f)
        val offset = latLng.toScreenCoordinates(width = 360f, height = 180f)

        assertEquals(0f, offset.x, 0.001f)
        assertEquals(180f, offset.y, 0.001f)
    }

    @Test
    fun `LatLng toScreenCoordinates with scale`() {
        val latLng = LatLng(lat = 0f, lng = 0f)
        val offset = latLng.toScreenCoordinates(
            width = 360f,
            height = 180f,
            scale = 2f
        )

        assertEquals(360f, offset.x, 0.001f)
        assertEquals(180f, offset.y, 0.001f)
    }

    @Test
    fun `LatLng toScreenCoordinates with offset`() {
        val latLng = LatLng(lat = 0f, lng = 0f)
        val offset = latLng.toScreenCoordinates(
            width = 360f,
            height = 180f,
            offsetX = 10f,
            offsetY = 20f
        )

        assertEquals(190f, offset.x, 0.001f)
        assertEquals(110f, offset.y, 0.001f)
    }

    @Test
    fun `LatLng toScreenCoordinates with scale and offset`() {
        val latLng = LatLng(lat = 0f, lng = 0f)
        val offset = latLng.toScreenCoordinates(
            width = 360f,
            height = 180f,
            offsetX = 10f,
            offsetY = 20f,
            scale = 2f
        )

        assertEquals(370f, offset.x, 0.001f)
        assertEquals(200f, offset.y, 0.001f)
    }

    @Test
    fun `LatLng equals and hashCode`() {
        val latLng1 = LatLng(45.0f, -122.0f)
        val latLng2 = LatLng(45.0f, -122.0f)

        assertEquals(latLng1, latLng2)
        assertEquals(latLng1.hashCode(), latLng2.hashCode())
    }

    @Test
    fun `LatLng copy works correctly`() {
        val original = LatLng(45.0f, -122.0f)
        val copy = original.copy(lat = 46.0f)

        assertEquals(45.0f, original.lat, 0.001f)
        assertEquals(46.0f, copy.lat, 0.001f)
        assertEquals(original.lng, copy.lng, 0.001f)
    }
}

class IsPointInPolygonTest {

    @Test
    fun `isPointInPolygon returns true for point inside square`() {
        val polygon = listOf(
            Offset(0f, 0f),
            Offset(10f, 0f),
            Offset(10f, 10f),
            Offset(0f, 10f)
        )
        val point = Offset(5f, 5f)

        assertTrue(isPointInPolygon(point, polygon))
    }

    @Test
    fun `isPointInPolygon returns false for point outside square`() {
        val polygon = listOf(
            Offset(0f, 0f),
            Offset(10f, 0f),
            Offset(10f, 10f),
            Offset(0f, 10f)
        )
        val point = Offset(15f, 5f)

        assertFalse(isPointInPolygon(point, polygon))
    }

    @Test
    fun `isPointInPolygon returns true for point inside triangle`() {
        val polygon = listOf(
            Offset(0f, 0f),
            Offset(10f, 0f),
            Offset(5f, 10f)
        )
        val point = Offset(5f, 3f)

        assertTrue(isPointInPolygon(point, polygon))
    }

    @Test
    fun `isPointInPolygon returns false for point outside triangle`() {
        val polygon = listOf(
            Offset(0f, 0f),
            Offset(10f, 0f),
            Offset(5f, 10f)
        )
        val point = Offset(0f, 10f)

        assertFalse(isPointInPolygon(point, polygon))
    }

    @Test
    fun `isPointInPolygon returns false for polygon with less than 3 points`() {
        val polygon = listOf(
            Offset(0f, 0f),
            Offset(10f, 0f)
        )
        val point = Offset(5f, 0f)

        assertFalse(isPointInPolygon(point, polygon))
    }

    @Test
    fun `isPointInPolygon returns false for empty polygon`() {
        val polygon = emptyList<Offset>()
        val point = Offset(5f, 5f)

        assertFalse(isPointInPolygon(point, polygon))
    }

    @Test
    fun `isPointInPolygon handles complex polygon`() {
        // L-shaped polygon
        val polygon = listOf(
            Offset(0f, 0f),
            Offset(10f, 0f),
            Offset(10f, 5f),
            Offset(5f, 5f),
            Offset(5f, 10f),
            Offset(0f, 10f)
        )

        // Point inside the L
        assertTrue(isPointInPolygon(Offset(2f, 2f), polygon))
        assertTrue(isPointInPolygon(Offset(2f, 7f), polygon))

        // Point outside the L (in the cut-out)
        assertFalse(isPointInPolygon(Offset(7f, 7f), polygon))
    }

    @Test
    fun `isPointInPolygon with negative coordinates`() {
        val polygon = listOf(
            Offset(-10f, -10f),
            Offset(10f, -10f),
            Offset(10f, 10f),
            Offset(-10f, 10f)
        )

        assertTrue(isPointInPolygon(Offset(0f, 0f), polygon))
        assertTrue(isPointInPolygon(Offset(-5f, -5f), polygon))
        assertFalse(isPointInPolygon(Offset(-15f, 0f), polygon))
    }
}
