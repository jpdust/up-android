package com.unstampedpages.app.data.repository

import android.content.Context
import com.unstampedpages.app.data.model.CountryGeometry
import com.unstampedpages.app.data.model.LatLng
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Reads and writes a compact binary cache of parsed [CountryGeometry] data.
 *
 * On first launch the app parses the 11 MB GeoJSON file and serialises the result
 * here.  Subsequent launches read the binary cache directly — eliminating the
 * JSON tokenisation cost and cutting load time significantly.
 *
 * Cache invalidation is handled by [CACHE_VERSION]: bump the constant whenever
 * the GeoJSON source is updated or the binary format changes.  The header check
 * in [readFromFile] will reject any file written by an older version and the
 * caller falls back to a fresh JSON parse.
 *
 * Binary format (all multi-byte values big-endian via [DataOutputStream]):
 * ```
 * [4]  magic  = 0x47454F4D ("GEOM")
 * [4]  version (Int)
 * [4]  country count (Int)
 * per country:
 *   [2]  id byte length (unsigned short)
 *   [n]  id bytes (UTF-8)
 *   [4]  polygon count (Int)
 *   per polygon:
 *     [4]  point count (Int)
 *     per point:
 *       [4]  lat (Float)
 *       [4]  lng (Float)
 * ```
 */
internal object GeometryBinaryCache {

    private const val MAGIC = 0x47454F4D
    internal const val CACHE_VERSION = 1
    private const val CACHE_FILE         = "geometry_cache.bin"
    private const val CACHE_FILE_LOW_RES = "geometry_cache_110m.bin"

    // ------------------------------------------------------------------
    // Public context-based API
    // ------------------------------------------------------------------

    fun read(context: Context): List<CountryGeometry>? =
        readFromFile(cacheFile(context))

    fun write(context: Context, geometries: List<CountryGeometry>) =
        writeToFile(cacheFile(context), geometries)

    fun readLowRes(context: Context): List<CountryGeometry>? =
        readFromFile(lowResCacheFile(context))

    fun writeLowRes(context: Context, geometries: List<CountryGeometry>) =
        writeToFile(lowResCacheFile(context), geometries)

    private fun cacheFile(context: Context) = File(context.filesDir, CACHE_FILE)
    private fun lowResCacheFile(context: Context) = File(context.filesDir, CACHE_FILE_LOW_RES)

    // ------------------------------------------------------------------
    // Internal file-based API (exposed for unit tests)
    // ------------------------------------------------------------------

    /**
     * Reads geometry from [file].  Returns null if the file doesn't exist,
     * fails the header check, or is corrupt in any way — the caller should
     * fall back to the JSON parser.
     */
    internal fun readFromFile(file: File): List<CountryGeometry>? {
        if (!file.exists()) return null
        return try {
            DataInputStream(BufferedInputStream(FileInputStream(file))).use { dis ->
                if (dis.readInt() != MAGIC) return null
                if (dis.readInt() != CACHE_VERSION) return null
                val countryCount = dis.readInt()
                List(countryCount) {
                    val idLen = dis.readUnsignedShort()
                    val idBytes = ByteArray(idLen)
                    dis.readFully(idBytes)
                    val countryId = String(idBytes, Charsets.UTF_8)
                    val polygonCount = dis.readInt()
                    val polygons = List(polygonCount) {
                        val pointCount = dis.readInt()
                        List(pointCount) {
                            LatLng(lat = dis.readFloat(), lng = dis.readFloat())
                        }
                    }
                    CountryGeometry(countryId, polygons)
                }
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Serialises [geometries] to [file].
     *
     * Writes to a sibling `.tmp` file first and renames on success so that a
     * crash or OOM mid-write never leaves a corrupt cache in place.
     */
    internal fun writeToFile(file: File, geometries: List<CountryGeometry>) {
        val tmp = File(file.parent, "${file.name}.tmp")
        try {
            DataOutputStream(BufferedOutputStream(FileOutputStream(tmp))).use { dos ->
                writeGeometries(dos, geometries)
            }
            if (!tmp.renameTo(file)) deleteOrSchedule(tmp)
        } catch (_: Exception) {
            deleteOrSchedule(tmp)
            deleteOrSchedule(file)
        }
    }

    /**
     * Tries to delete [file] immediately; schedules deletion at JVM exit if that fails.
     * Used to clean up tmp and target files after a failed atomic write.
     */
    internal fun deleteOrSchedule(file: File) {
        if (!file.delete()) file.deleteOnExit()
    }

    private fun writeGeometries(dos: DataOutputStream, geometries: List<CountryGeometry>) {
        dos.writeInt(MAGIC)
        dos.writeInt(CACHE_VERSION)
        dos.writeInt(geometries.size)
        for (country in geometries) {
            val idBytes = country.countryId.toByteArray(Charsets.UTF_8)
            dos.writeShort(idBytes.size)
            dos.write(idBytes)
            dos.writeInt(country.polygons.size)
            for (polygon in country.polygons) {
                dos.writeInt(polygon.size)
                for (point in polygon) {
                    dos.writeFloat(point.lat)
                    dos.writeFloat(point.lng)
                }
            }
        }
    }
}
