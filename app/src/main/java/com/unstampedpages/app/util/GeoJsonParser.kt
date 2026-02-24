package com.unstampedpages.app.util

import android.content.Context
import com.unstampedpages.app.data.model.CountryGeometry
import com.unstampedpages.app.data.model.LatLng
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

object GeoJsonParser {

    fun parseFromResource(context: Context, resourceId: Int): List<CountryGeometry> {
        val inputStream = context.resources.openRawResource(resourceId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val jsonString = reader.use { it.readText() }
        return parseGeoJson(jsonString)
    }

    fun parseGeoJson(jsonString: String): List<CountryGeometry> {
        val geometries = mutableListOf<CountryGeometry>()
        val json = JSONObject(jsonString)
        val features = json.getJSONArray("features")

        for (i in 0 until features.length()) {
            val feature = features.getJSONObject(i)
            val id = feature.getString("id")
            val geometry = feature.getJSONObject("geometry")
            val type = geometry.getString("type")

            val polygons = when (type) {
                "Polygon" -> {
                    listOf(parsePolygon(geometry.getJSONArray("coordinates")))
                }
                "MultiPolygon" -> {
                    parseMultiPolygon(geometry.getJSONArray("coordinates"))
                }
                else -> emptyList()
            }

            if (polygons.isNotEmpty()) {
                geometries.add(CountryGeometry(id, polygons))
            }
        }

        return geometries
    }

    /**
     * Parse a GeoJSON Polygon's coordinates.
     * GeoJSON Polygon format: [outer_ring, hole1, hole2, ...] where each ring is [[lng, lat], ...]
     * We only use the outer ring (index 0) and ignore holes.
     */
    private fun parsePolygon(coordinates: JSONArray): List<LatLng> {
        if (coordinates.length() == 0) return emptyList()

        // Get the outer ring (first element)
        val outerRing = coordinates.getJSONArray(0)
        return parseRing(outerRing)
    }

    /**
     * Parse a ring (array of coordinate pairs).
     */
    private fun parseRing(ring: JSONArray): List<LatLng> {
        val points = mutableListOf<LatLng>()
        for (i in 0 until ring.length()) {
            val point = ring.getJSONArray(i)
            val lng = point.getDouble(0).toFloat()
            val lat = point.getDouble(1).toFloat()
            points.add(LatLng(lat, lng))
        }
        return points
    }

    /**
     * Parse a GeoJSON MultiPolygon's coordinates.
     * GeoJSON MultiPolygon format: [polygon1, polygon2, ...] where each polygon is [outer_ring, holes...]
     */
    private fun parseMultiPolygon(coordinates: JSONArray): List<List<LatLng>> {
        val polygons = mutableListOf<List<LatLng>>()
        for (i in 0 until coordinates.length()) {
            val polygonCoords = coordinates.getJSONArray(i)
            val outerRing = parsePolygon(polygonCoords)
            if (outerRing.isNotEmpty()) {
                polygons.add(outerRing)
            }
        }
        return polygons
    }
}
