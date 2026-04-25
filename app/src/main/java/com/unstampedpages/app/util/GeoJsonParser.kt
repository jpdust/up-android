package com.unstampedpages.app.util

import android.content.Context
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.unstampedpages.app.data.model.CountryGeometry
import com.unstampedpages.app.data.model.LatLng
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringReader

/**
 * Streaming GeoJSON parser using Gson's JsonReader.
 * Processes the file token-by-token without loading the entire JSON into memory,
 * which is critical for the high-resolution 10m Natural Earth dataset.
 */
object GeoJsonParser {

    fun parseFromResource(context: Context, resourceId: Int): List<CountryGeometry> {
        val stream = context.resources.openRawResource(resourceId)
        return stream.use { parseStream(InputStreamReader(it, Charsets.UTF_8)) }
    }

    /** String-based entry point kept for unit tests. */
    fun parseGeoJson(jsonString: String): List<CountryGeometry> {
        return parseStream(StringReader(jsonString))
    }

    // ---- streaming implementation ----

    private fun parseStream(reader: Reader): List<CountryGeometry> {
        val geometries = mutableListOf<CountryGeometry>()
        val jr = JsonReader(reader)
        jr.use {
            jr.beginObject()
            while (jr.hasNext()) {
                if (jr.nextName() == "features") {
                    jr.beginArray()
                    while (jr.hasNext()) {
                        parseFeature(jr)?.let { geometries.add(it) }
                    }
                    jr.endArray()
                } else {
                    jr.skipValue()
                }
            }
            jr.endObject()
        }
        return geometries
    }

    private fun parseFeature(jr: JsonReader): CountryGeometry? {
        var id: String? = null
        var polygons: List<List<LatLng>>? = null

        jr.beginObject()
        while (jr.hasNext()) {
            when (jr.nextName()) {
                "id" -> id = jr.nextString()
                "geometry" -> polygons = parseGeometry(jr)
                else -> jr.skipValue()
            }
        }
        jr.endObject()

        val finalId = id ?: return null
        val finalPolygons = polygons?.filter { it.size >= 3 } ?: return null
        if (finalPolygons.isEmpty()) return null
        return CountryGeometry(finalId, finalPolygons)
    }

    private fun parseGeometry(jr: JsonReader): List<List<LatLng>>? {
        if (jr.peek() == JsonToken.NULL) { jr.nextNull(); return null }

        var type: String? = null
        var polygons: List<List<LatLng>>? = null

        jr.beginObject()
        while (jr.hasNext()) {
            when (jr.nextName()) {
                "type" -> type = jr.nextString()
                "coordinates" -> {
                    polygons = when (type) {
                        "Polygon" -> listOf(readPolygonCoords(jr))
                        "MultiPolygon" -> readMultiPolygonCoords(jr)
                        else -> { jr.skipValue(); null }
                    }
                }
                else -> jr.skipValue()
            }
        }
        jr.endObject()
        return polygons
    }

    /** Reads a Polygon coordinates array: [[lng,lat],...] (outer ring only). */
    private fun readPolygonCoords(jr: JsonReader): List<LatLng> {
        var outerRing = emptyList<LatLng>()
        var ringIndex = 0
        jr.beginArray()
        while (jr.hasNext()) {
            val ring = readRing(jr)
            if (ringIndex == 0) outerRing = ring   // only outer ring
            else { /* skip holes */ }
            ringIndex++
        }
        jr.endArray()
        return outerRing
    }

    /** Reads a MultiPolygon coordinates array: [[[lng,lat],...], ...] */
    private fun readMultiPolygonCoords(jr: JsonReader): List<List<LatLng>> {
        val polygons = mutableListOf<List<LatLng>>()
        jr.beginArray()
        while (jr.hasNext()) {
            polygons.add(readPolygonCoords(jr))
        }
        jr.endArray()
        return polygons
    }

    /** Reads one ring: [[lng,lat], [lng,lat], ...] */
    private fun readRing(jr: JsonReader): List<LatLng> {
        val points = mutableListOf<LatLng>()
        jr.beginArray()
        while (jr.hasNext()) {
            jr.beginArray()
            val lng = jr.nextDouble().toFloat()
            val lat = jr.nextDouble().toFloat()
            // skip any extra dimensions (e.g., altitude)
            while (jr.hasNext()) jr.skipValue()
            jr.endArray()
            points.add(LatLng(lat, lng))
        }
        jr.endArray()
        return points
    }
}
