package com.unstampedpages.app.util

import com.unstampedpages.app.data.model.LatLng
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

class GeoJsonParserTest {

    // ==================== Basic Parsing Tests ====================

    @Test
    fun `parseGeoJson with empty features returns empty list`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": []
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseGeoJson with single Polygon feature returns one CountryGeometry`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "us",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [
                                    [-120.0, 35.0],
                                    [-119.0, 35.0],
                                    [-119.0, 36.0],
                                    [-120.0, 36.0],
                                    [-120.0, 35.0]
                                ]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)

        assertEquals(1, result.size)
        assertEquals("us", result[0].countryId)
        assertEquals(1, result[0].polygons.size)
    }

    @Test
    fun `parseGeoJson with single MultiPolygon feature returns one CountryGeometry with multiple polygons`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "us",
                        "type": "Feature",
                        "geometry": {
                            "type": "MultiPolygon",
                            "coordinates": [
                                [
                                    [
                                        [-120.0, 35.0],
                                        [-119.0, 35.0],
                                        [-119.0, 36.0],
                                        [-120.0, 35.0]
                                    ]
                                ],
                                [
                                    [
                                        [-160.0, 20.0],
                                        [-159.0, 20.0],
                                        [-159.0, 21.0],
                                        [-160.0, 20.0]
                                    ]
                                ]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)

        assertEquals(1, result.size)
        assertEquals("us", result[0].countryId)
        assertEquals(2, result[0].polygons.size)
    }

    @Test
    fun `parseGeoJson with multiple features returns multiple CountryGeometry objects`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "us",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [
                                    [-120.0, 35.0],
                                    [-119.0, 35.0],
                                    [-119.0, 36.0],
                                    [-120.0, 35.0]
                                ]
                            ]
                        }
                    },
                    {
                        "id": "ca",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [
                                    [-130.0, 50.0],
                                    [-129.0, 50.0],
                                    [-129.0, 51.0],
                                    [-130.0, 50.0]
                                ]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)

        assertEquals(2, result.size)
        assertEquals("us", result[0].countryId)
        assertEquals("ca", result[1].countryId)
    }

    // ==================== Coordinate Conversion Tests ====================

    @Test
    fun `parseGeoJson correctly converts GeoJSON lng-lat to LatLng lat-lng`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "test",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [
                                    [-122.4194, 37.7749],
                                    [-122.4094, 37.7749],
                                    [-122.4094, 37.7849],
                                    [-122.4194, 37.7749]
                                ]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)
        val firstPoint = result[0].polygons[0][0]

        // GeoJSON has [lng, lat] = [-122.4194, 37.7749]
        // Should convert to LatLng(lat=37.7749, lng=-122.4194)
        assertEquals(37.7749f, firstPoint.lat, 0.0001f)
        assertEquals(-122.4194f, firstPoint.lng, 0.0001f)
    }

    @Test
    fun `parseGeoJson preserves all polygon points`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "test",
                        "type": "Feature",
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

        val result = GeoJsonParser.parseGeoJson(json)
        val points = result[0].polygons[0]

        assertEquals(5, points.size)
        assertEquals(LatLng(0.0f, 0.0f), points[0])
        assertEquals(LatLng(0.0f, 1.0f), points[1])
        assertEquals(LatLng(1.0f, 1.0f), points[2])
        assertEquals(LatLng(1.0f, 0.0f), points[3])
        assertEquals(LatLng(0.0f, 0.0f), points[4])
    }

    @Test
    fun `parseGeoJson handles negative coordinates correctly`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "test",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [
                                    [-180.0, -90.0],
                                    [180.0, 90.0],
                                    [-180.0, -90.0]
                                ]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)
        val points = result[0].polygons[0]

        assertEquals(LatLng(-90.0f, -180.0f), points[0])
        assertEquals(LatLng(90.0f, 180.0f), points[1])
    }

    @Test
    fun `parseGeoJson handles decimal coordinates with precision`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "test",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [
                                    [-73.935242, 40.730610],
                                    [-73.925242, 40.730610],
                                    [-73.935242, 40.730610]
                                ]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)
        val point = result[0].polygons[0][0]

        assertEquals(40.730610f, point.lat, 0.00001f)
        assertEquals(-73.935242f, point.lng, 0.00001f)
    }

    // ==================== Geometry Type Handling Tests ====================

    @Test
    fun `parseGeoJson skips unsupported geometry types`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "point_feature",
                        "type": "Feature",
                        "geometry": {
                            "type": "Point",
                            "coordinates": [-122.4194, 37.7749]
                        }
                    },
                    {
                        "id": "polygon_feature",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [
                                    [0.0, 0.0],
                                    [1.0, 0.0],
                                    [1.0, 1.0],
                                    [0.0, 0.0]
                                ]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)

        assertEquals(1, result.size)
        assertEquals("polygon_feature", result[0].countryId)
    }

    @Test
    fun `parseGeoJson skips LineString geometry type`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "line_feature",
                        "type": "Feature",
                        "geometry": {
                            "type": "LineString",
                            "coordinates": [
                                [-122.4194, 37.7749],
                                [-122.4094, 37.7849]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseGeoJson handles mixed valid and invalid geometry types`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "point1",
                        "type": "Feature",
                        "geometry": { "type": "Point", "coordinates": [0.0, 0.0] }
                    },
                    {
                        "id": "polygon1",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [[[0.0, 0.0], [1.0, 0.0], [0.0, 0.0]]]
                        }
                    },
                    {
                        "id": "line1",
                        "type": "Feature",
                        "geometry": { "type": "LineString", "coordinates": [[0.0, 0.0], [1.0, 1.0]] }
                    },
                    {
                        "id": "multipolygon1",
                        "type": "Feature",
                        "geometry": {
                            "type": "MultiPolygon",
                            "coordinates": [[[[0.0, 0.0], [1.0, 0.0], [0.0, 0.0]]]]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)

        assertEquals(2, result.size)
        assertEquals("polygon1", result[0].countryId)
        assertEquals("multipolygon1", result[1].countryId)
    }

    // ==================== Edge Cases Tests ====================

    @Test
    fun `parseGeoJson with Polygon having empty coordinates skips the feature`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "empty",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": []
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)

        // Streaming parser filters polygons with < 3 points; an empty outer ring
        // produces no valid polygons, so the feature is skipped entirely.
        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseGeoJson with Polygon with hole captures outer ring in polygons and hole in holes`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "with_hole",
                        "type": "Feature",
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

        val result = GeoJsonParser.parseGeoJson(json)

        assertEquals(1, result.size)
        // Outer ring ends up in polygons only
        assertEquals(1, result[0].polygons.size)
        assertEquals(5, result[0].polygons[0].size)
        assertEquals(LatLng(0.0f, 0.0f), result[0].polygons[0][0])
        // Hole ring ends up in holes (GeoJSON [lng, lat] → LatLng(lat, lng))
        assertEquals(1, result[0].holes.size)
        assertEquals(5, result[0].holes[0].size)
        assertEquals(LatLng(lat = 2.0f, lng = 2.0f), result[0].holes[0][0])
    }

    @Test
    fun `parseGeoJson with Polygon without holes has empty holes list`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "no_hole",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [
                                    [0.0, 0.0],
                                    [10.0, 0.0],
                                    [10.0, 10.0],
                                    [0.0, 0.0]
                                ]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)

        assertEquals(1, result.size)
        assertTrue("holes should be empty when polygon has no hole rings", result[0].holes.isEmpty())
    }

    @Test
    fun `parseGeoJson with Polygon with multiple holes captures each hole`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "multi_hole",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [[0.0,0.0],[20.0,0.0],[20.0,20.0],[0.0,20.0],[0.0,0.0]],
                                [[1.0,1.0],[4.0,1.0],[4.0,4.0],[1.0,4.0],[1.0,1.0]],
                                [[6.0,6.0],[9.0,6.0],[9.0,9.0],[6.0,9.0],[6.0,6.0]]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)

        assertEquals(1, result.size)
        assertEquals(1, result[0].polygons.size)
        assertEquals(2, result[0].holes.size)
    }

    @Test
    fun `parseGeoJson with MultiPolygon where sub-polygons have holes collects all holes`() {
        // MultiPolygon: two sub-polygons, each with one hole ring.
        // South Africa (ZAF) with Lesotho is an example: outer SA ring + Lesotho hole.
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "ZAF",
                        "type": "Feature",
                        "geometry": {
                            "type": "MultiPolygon",
                            "coordinates": [
                                [
                                    [[0.0,0.0],[10.0,0.0],[10.0,10.0],[0.0,10.0],[0.0,0.0]],
                                    [[3.0,3.0],[7.0,3.0],[7.0,7.0],[3.0,7.0],[3.0,3.0]]
                                ],
                                [
                                    [[20.0,20.0],[30.0,20.0],[30.0,30.0],[20.0,30.0],[20.0,20.0]],
                                    [[22.0,22.0],[28.0,22.0],[28.0,28.0],[22.0,28.0],[22.0,22.0]]
                                ]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)

        assertEquals(1, result.size)
        assertEquals(2, result[0].polygons.size)   // two outer rings
        assertEquals(2, result[0].holes.size)       // one hole per sub-polygon, flattened
    }

    @Test
    fun `parseGeoJson hole rings with fewer than 3 points are filtered out`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "tiny_hole",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [[0.0,0.0],[10.0,0.0],[10.0,10.0],[0.0,10.0],[0.0,0.0]],
                                [[1.0,1.0],[2.0,1.0]]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)

        assertEquals(1, result.size)
        assertTrue("Hole ring with < 3 points should be filtered out", result[0].holes.isEmpty())
    }

    @Test
    fun `parseGeoJson with MultiPolygon having multiple polygons preserves all`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "multi",
                        "type": "Feature",
                        "geometry": {
                            "type": "MultiPolygon",
                            "coordinates": [
                                [[[0.0, 0.0], [1.0, 0.0], [1.0, 1.0], [0.0, 0.0]]],
                                [[[10.0, 10.0], [11.0, 10.0], [11.0, 11.0], [10.0, 10.0]]],
                                [[[20.0, 20.0], [21.0, 20.0], [21.0, 21.0], [20.0, 20.0]]]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)

        assertEquals(1, result.size)
        assertEquals(3, result[0].polygons.size)
    }

    @Test
    fun `parseGeoJson maintains feature order`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "first",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [[[0.0, 0.0], [1.0, 0.0], [0.0, 0.0]]]
                        }
                    },
                    {
                        "id": "second",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [[[0.0, 0.0], [1.0, 0.0], [0.0, 0.0]]]
                        }
                    },
                    {
                        "id": "third",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [[[0.0, 0.0], [1.0, 0.0], [0.0, 0.0]]]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)

        assertEquals(3, result.size)
        assertEquals("first", result[0].countryId)
        assertEquals("second", result[1].countryId)
        assertEquals("third", result[2].countryId)
    }

    // ==================== Error Handling Tests ====================

    @Test(expected = IOException::class)
    fun `parseGeoJson throws IOException for malformed JSON`() {
        val json = "{ invalid json }"

        GeoJsonParser.parseGeoJson(json)
    }

    @Test
    fun `parseGeoJson with missing features array returns empty list`() {
        val json = """
            {
                "type": "FeatureCollection"
            }
        """.trimIndent()

        // Streaming parser skips unknown keys; no "features" key → returns empty list
        val result = GeoJsonParser.parseGeoJson(json)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseGeoJson with missing geometry skips the feature`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "test",
                        "type": "Feature"
                    }
                ]
            }
        """.trimIndent()

        // Streaming parser: geometry is null → feature is skipped → empty list
        val result = GeoJsonParser.parseGeoJson(json)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseGeoJson with missing id skips the feature`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [[[0.0, 0.0], [1.0, 0.0], [0.0, 0.0]]]
                        }
                    }
                ]
            }
        """.trimIndent()

        // Streaming parser: id is null → feature is skipped → empty list
        val result = GeoJsonParser.parseGeoJson(json)
        assertTrue(result.isEmpty())
    }

    // ==================== Real-world Scenario Tests ====================

    @Test
    fun `parseGeoJson handles country-like polygon with many points`() {
        // Simulate a simplified country border
        val coordinates = (0 until 100).map { i ->
            val angle = i * 3.6 * Math.PI / 180
            val lng = 10.0 + 5.0 * Math.cos(angle)
            val lat = 50.0 + 5.0 * Math.sin(angle)
            "[$lng, $lat]"
        }.joinToString(",")

        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "de",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [[$coordinates]]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)

        assertEquals(1, result.size)
        assertEquals("de", result[0].countryId)
        assertEquals(100, result[0].polygons[0].size)
    }

    @Test
    fun `parseGeoJson handles archipelago country with many islands`() {
        // Simulate a country with multiple islands (like Indonesia)
        val islands = (0 until 5).map { i ->
            val baseLng = 100.0 + i * 10
            val baseLat = -5.0 + i * 2
            """[
                [[$baseLng, $baseLat], [${baseLng + 1}, $baseLat], [${baseLng + 1}, ${baseLat + 1}], [$baseLng, $baseLat]]
            ]"""
        }.joinToString(",")

        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "id",
                        "type": "Feature",
                        "geometry": {
                            "type": "MultiPolygon",
                            "coordinates": [$islands]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)

        assertEquals(1, result.size)
        assertEquals("id", result[0].countryId)
        assertEquals(5, result[0].polygons.size)
    }

    @Test
    fun `parseGeoJson with integer coordinates converts to float correctly`() {
        val json = """
            {
                "type": "FeatureCollection",
                "features": [
                    {
                        "id": "test",
                        "type": "Feature",
                        "geometry": {
                            "type": "Polygon",
                            "coordinates": [
                                [
                                    [100, 50],
                                    [101, 50],
                                    [101, 51],
                                    [100, 50]
                                ]
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()

        val result = GeoJsonParser.parseGeoJson(json)
        val point = result[0].polygons[0][0]

        assertEquals(50.0f, point.lat, 0.0001f)
        assertEquals(100.0f, point.lng, 0.0001f)
    }
}
