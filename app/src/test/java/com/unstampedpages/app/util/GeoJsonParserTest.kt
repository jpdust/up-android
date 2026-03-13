package com.unstampedpages.app.util

import org.junit.Assert.*
import org.junit.Test

class GeoJsonParserTest {

    @Test
    fun `parseGeoJson parses simple polygon`() {
        val geoJson = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "type": "Feature",
                        "id": "TEST",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [
                                    [0.0, 0.0],
                                    [1.0, 0.0],
                                    [1.0, 1.0],
                                    [0.0, 1.0],
                                    [0.0, 0.0]
                                ]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(geoJson)

        assertEquals(1, result.size)
        assertEquals("TEST", result[0].countryId)
        assertEquals(1, result[0].polygons.size)
        assertEquals(5, result[0].polygons[0].size)
    }

    @Test
    fun `parseGeoJson parses polygon coordinates correctly`() {
        val geoJson = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "type": "Feature",
                        "id": "COORD_TEST",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [
                                    [-122.5, 37.5],
                                    [-122.0, 37.5],
                                    [-122.0, 38.0],
                                    [-122.5, 38.0],
                                    [-122.5, 37.5]
                                ]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(geoJson)
        val firstPoint = result[0].polygons[0][0]

        // GeoJSON is [lng, lat], LatLng stores (lat, lng)
        assertEquals(37.5f, firstPoint.lat, 0.001f)
        assertEquals(-122.5f, firstPoint.lng, 0.001f)
    }

    @Test
    fun `parseGeoJson parses MultiPolygon`() {
        val geoJson = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "type": "Feature",
                        "id": "MULTI",
                        "geometry": {
                            "type": "MultiPolygon",
                            "coordinates": [
                                [
                                    [
                                        [0.0, 0.0],
                                        [1.0, 0.0],
                                        [1.0, 1.0],
                                        [0.0, 0.0]
                                    ]
                                ],
                                [
                                    [
                                        [2.0, 2.0],
                                        [3.0, 2.0],
                                        [3.0, 3.0],
                                        [2.0, 2.0]
                                    ]
                                ]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(geoJson)

        assertEquals(1, result.size)
        assertEquals("MULTI", result[0].countryId)
        assertEquals(2, result[0].polygons.size)
    }

    @Test
    fun `parseGeoJson parses multiple features`() {
        val geoJson = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "type": "Feature",
                        "id": "COUNTRY_A",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [[[0.0, 0.0], [1.0, 0.0], [0.0, 1.0], [0.0, 0.0]]]
                        }
                    },
                    {
                        "type": "Feature",
                        "id": "COUNTRY_B",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [[[2.0, 2.0], [3.0, 2.0], [2.0, 3.0], [2.0, 2.0]]]
                        }
                    },
                    {
                        "type": "Feature",
                        "id": "COUNTRY_C",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [[[4.0, 4.0], [5.0, 4.0], [4.0, 5.0], [4.0, 4.0]]]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(geoJson)

        assertEquals(3, result.size)
        assertEquals("COUNTRY_A", result[0].countryId)
        assertEquals("COUNTRY_B", result[1].countryId)
        assertEquals("COUNTRY_C", result[2].countryId)
    }

    @Test
    fun `parseGeoJson handles polygon with holes - only uses outer ring`() {
        val geoJson = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "type": "Feature",
                        "id": "WITH_HOLE",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [
                                    [0.0, 0.0],
                                    [10.0, 0.0],
                                    [10.0, 10.0],
                                    [0.0, 10.0],
                                    [0.0, 0.0]
                                ],
                                [
                                    [2.0, 2.0],
                                    [8.0, 2.0],
                                    [8.0, 8.0],
                                    [2.0, 8.0],
                                    [2.0, 2.0]
                                ]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(geoJson)

        assertEquals(1, result.size)
        assertEquals(1, result[0].polygons.size)
        // Should only have outer ring (5 points), not the hole
        assertEquals(5, result[0].polygons[0].size)
    }

    @Test
    fun `parseGeoJson skips unknown geometry types`() {
        val geoJson = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "type": "Feature",
                        "id": "POINT_FEATURE",
                        "geometry": {
                            "type": "Point",
                            "coordinates": [0.0, 0.0]
                        }
                    },
                    {
                        "type": "Feature",
                        "id": "VALID_POLYGON",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [[[0.0, 0.0], [1.0, 0.0], [0.0, 1.0], [0.0, 0.0]]]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(geoJson)

        // Should only include the valid polygon, not the point
        assertEquals(1, result.size)
        assertEquals("VALID_POLYGON", result[0].countryId)
    }

    @Test
    fun `parseGeoJson handles empty features array`() {
        val geoJson = """
            {
                "type": "FeatureCollection",
                "features": []
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(geoJson)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseGeoJson handles empty polygon coordinates`() {
        val geoJson = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "type": "Feature",
                        "id": "EMPTY",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": []
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(geoJson)

        // Empty polygon should be skipped
        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseGeoJson preserves feature order`() {
        val geoJson = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "type": "Feature",
                        "id": "ZZZ",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [[[0.0, 0.0], [1.0, 0.0], [0.0, 1.0], [0.0, 0.0]]]
                        }
                    },
                    {
                        "type": "Feature",
                        "id": "AAA",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [[[0.0, 0.0], [1.0, 0.0], [0.0, 1.0], [0.0, 0.0]]]
                        }
                    },
                    {
                        "type": "Feature",
                        "id": "MMM",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [[[0.0, 0.0], [1.0, 0.0], [0.0, 1.0], [0.0, 0.0]]]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(geoJson)

        assertEquals("ZZZ", result[0].countryId)
        assertEquals("AAA", result[1].countryId)
        assertEquals("MMM", result[2].countryId)
    }
}
