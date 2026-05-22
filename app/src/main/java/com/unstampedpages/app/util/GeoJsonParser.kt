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
        var geometryResult: Pair<List<List<LatLng>>, List<List<LatLng>>>? = null

        jr.beginObject()
        while (jr.hasNext()) {
            when (jr.nextName()) {
                "id" -> id = jr.nextString()
                "geometry" -> geometryResult = parseGeometry(jr)
                else -> jr.skipValue()
            }
        }
        jr.endObject()

        val finalId = id ?: return null
        val (polygons, holes) = geometryResult ?: return null
        val finalPolygons = polygons.filter { it.size >= 3 }
        if (finalPolygons.isEmpty()) return null
        return CountryGeometry(finalId, finalPolygons, holes.filter { it.size >= 3 })
    }

    /**
     * Returns (outerRings, holeRings) for the geometry object currently being read.
     */
    private fun parseGeometry(
        jr: JsonReader
    ): Pair<List<List<LatLng>>, List<List<LatLng>>>? {
        if (jr.peek() == JsonToken.NULL) { jr.nextNull(); return null }

        var type: String? = null
        var result: Pair<List<List<LatLng>>, List<List<LatLng>>>? = null

        jr.beginObject()
        while (jr.hasNext()) {
            when (jr.nextName()) {
                "type" -> type = jr.nextString()
                "coordinates" -> {
                    result = when (type) {
                        "Polygon" -> {
                            val (outer, holes) = readPolygon(jr)
                            listOf(outer) to holes
                        }
                        "MultiPolygon" -> readMultiPolygon(jr)
                        else -> { jr.skipValue(); null }
                    }
                }
                else -> jr.skipValue()
            }
        }
        jr.endObject()
        return result
    }

    /**
     * Reads one Polygon: `[[outerRing], [hole], ...]`.
     * Returns the outer ring and all hole rings.
     */
    private fun readPolygon(jr: JsonReader): Pair<List<LatLng>, List<List<LatLng>>> {
        var outerRing = emptyList<LatLng>()
        val holes = mutableListOf<List<LatLng>>()
        var ringIndex = 0
        jr.beginArray()
        while (jr.hasNext()) {
            val ring = readRing(jr)
            if (ringIndex == 0) outerRing = ring else holes.add(ring)
            ringIndex++
        }
        jr.endArray()
        return outerRing to holes
    }

    /**
     * Reads a MultiPolygon: `[Polygon, Polygon, ...]`.
     * Returns all outer rings and all hole rings (flattened across polygons).
     */
    private fun readMultiPolygon(jr: JsonReader): Pair<List<List<LatLng>>, List<List<LatLng>>> {
        val outerRings = mutableListOf<List<LatLng>>()
        val allHoles = mutableListOf<List<LatLng>>()
        jr.beginArray()
        while (jr.hasNext()) {
            val (outer, holes) = readPolygon(jr)
            outerRings.add(outer)
            allHoles.addAll(holes)
        }
        jr.endArray()
        return outerRings to allHoles
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
