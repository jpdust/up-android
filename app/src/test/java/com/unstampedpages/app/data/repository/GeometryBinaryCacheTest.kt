package com.unstampedpages.app.data.repository

import android.content.Context
import com.unstampedpages.app.data.model.CountryGeometry
import com.unstampedpages.app.data.model.LatLng
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.Mockito
import java.io.File

class GeometryBinaryCacheTest {

    @get:Rule
    val tmp = TemporaryFolder()

    // --- helpers ---------------------------------------------------------------

    private fun cacheFile(name: String = "cache.bin"): File = tmp.newFile(name)

    private fun makeGeometry(
        id: String,
        vararg polygons: List<LatLng>
    ) = CountryGeometry(id, polygons.toList())

    private fun triangle(offsetLat: Float = 0f, offsetLng: Float = 0f) = listOf(
        LatLng(lat = 10f + offsetLat, lng = -10f + offsetLng),
        LatLng(lat = -10f + offsetLat, lng = 0f + offsetLng),
        LatLng(lat = 10f + offsetLat, lng = 10f + offsetLng)
    )

    // --- read: missing / corrupt -----------------------------------------------

    @Test
    fun `read returns null for non-existent file`() {
        val file = File(tmp.root, "does_not_exist.bin")
        assertNull(GeometryBinaryCache.readFromFile(file))
    }

    @Test
    fun `read returns null for empty file`() {
        val file = tmp.newFile("empty.bin")
        assertNull(GeometryBinaryCache.readFromFile(file))
    }

    @Test
    fun `read returns null for wrong magic`() {
        val file = cacheFile()
        java.io.DataOutputStream(file.outputStream()).use { dos ->
            dos.writeInt(0xDEADBEEF.toInt())   // wrong magic
            dos.writeInt(GeometryBinaryCache.CACHE_VERSION)
            dos.writeInt(0)
        }
        assertNull(GeometryBinaryCache.readFromFile(file))
    }

    @Test
    fun `read returns null for wrong version`() {
        val file = cacheFile()
        java.io.DataOutputStream(file.outputStream()).use { dos ->
            dos.writeInt(0x47454F4D)                          // correct magic
            dos.writeInt(GeometryBinaryCache.CACHE_VERSION + 99)  // wrong version
            dos.writeInt(0)
        }
        assertNull(GeometryBinaryCache.readFromFile(file))
    }

    @Test
    fun `read returns null for truncated data`() {
        val file = cacheFile()
        // Write header claiming 50 countries but provide no data
        java.io.DataOutputStream(file.outputStream()).use { dos ->
            dos.writeInt(0x47454F4D)
            dos.writeInt(GeometryBinaryCache.CACHE_VERSION)
            dos.writeInt(50)
            // no country data follows — truncated
        }
        assertNull(GeometryBinaryCache.readFromFile(file))
    }

    // --- round-trip: basic -----------------------------------------------------

    @Test
    fun `empty geometry list round-trips`() {
        val file = cacheFile()
        GeometryBinaryCache.writeToFile(file, emptyList())
        val result = GeometryBinaryCache.readFromFile(file)
        assertNotNull(result)
        assertTrue(result!!.isEmpty())
    }

    @Test
    fun `single country single polygon round-trips`() {
        val geometry = makeGeometry("us", triangle())
        val file = cacheFile()

        GeometryBinaryCache.writeToFile(file, listOf(geometry))
        val result = GeometryBinaryCache.readFromFile(file)!!

        assertEquals(1, result.size)
        val country = result[0]
        assertEquals("us", country.countryId)
        assertEquals(1, country.polygons.size)
        assertEquals(3, country.polygons[0].size)
        assertEquals(10f, country.polygons[0][0].lat, 0.0001f)
        assertEquals(-10f, country.polygons[0][0].lng, 0.0001f)
    }

    @Test
    fun `multiple countries multiple polygons round-trip`() {
        val geometries = listOf(
            makeGeometry("us", triangle(0f, 0f), triangle(20f, 30f)),
            makeGeometry("ca", triangle(40f, -80f)),
            makeGeometry("mx", triangle(-10f, -100f), triangle(-5f, -95f), triangle(0f, -90f))
        )
        val file = cacheFile()

        GeometryBinaryCache.writeToFile(file, geometries)
        val result = GeometryBinaryCache.readFromFile(file)!!

        assertEquals(3, result.size)
        assertEquals("us", result[0].countryId)
        assertEquals(2, result[0].polygons.size)
        assertEquals("ca", result[1].countryId)
        assertEquals(1, result[1].polygons.size)
        assertEquals("mx", result[2].countryId)
        assertEquals(3, result[2].polygons.size)
    }

    // --- round-trip: data fidelity ---------------------------------------------

    @Test
    fun `float coordinates preserved with full precision`() {
        val lat = 37.7749f
        val lng = -122.4194f
        val geometry = makeGeometry("sf", listOf(LatLng(lat, lng), LatLng(0f, 0f), LatLng(1f, 1f)))
        val file = cacheFile()

        GeometryBinaryCache.writeToFile(file, listOf(geometry))
        val result = GeometryBinaryCache.readFromFile(file)!!

        val point = result[0].polygons[0][0]
        assertEquals(lat, point.lat, 0.00001f)
        assertEquals(lng, point.lng, 0.00001f)
    }

    @Test
    fun `country id with unicode characters round-trips`() {
        // Non-ASCII id (edge case for the UTF-8 byte-length vs char-length path)
        val geometry = makeGeometry("côte", triangle())
        val file = cacheFile()

        GeometryBinaryCache.writeToFile(file, listOf(geometry))
        val result = GeometryBinaryCache.readFromFile(file)!!

        assertEquals("côte", result[0].countryId)
    }

    @Test
    fun `negative float coordinates round-trip`() {
        val polygon = listOf(
            LatLng(lat = -33.87f, lng = 151.21f),
            LatLng(lat = -37.81f, lng = 144.96f),
            LatLng(lat = -27.47f, lng = 153.03f)
        )
        val geometry = makeGeometry("au", polygon)
        val file = cacheFile()

        GeometryBinaryCache.writeToFile(file, listOf(geometry))
        val result = GeometryBinaryCache.readFromFile(file)!!

        val stored = result[0].polygons[0]
        assertEquals(-33.87f, stored[0].lat, 0.001f)
        assertEquals(151.21f, stored[0].lng, 0.001f)
        assertEquals(-37.81f, stored[1].lat, 0.001f)
        assertEquals(144.96f, stored[1].lng, 0.001f)
    }

    // --- write: atomicity ------------------------------------------------------

    @Test
    fun `write does not leave tmp file behind on success`() {
        val file = File(tmp.root, "cache.bin")
        GeometryBinaryCache.writeToFile(file, emptyList())

        val tmpFile = File(tmp.root, "cache.bin.tmp")
        assertFalse("Temp file should be gone after successful write", tmpFile.exists())
        assertTrue("Cache file should exist", file.exists())
    }

    @Test
    fun `write cleans up tmp file when rename fails`() {
        // Pre-create the destination as a directory so renameTo returns false (EISDIR).
        // The .tmp file is written successfully first, so this exercises the
        // rename-failure branch specifically — not the catch block.
        val targetName = "cache_rename_fail.bin"
        tmp.newFolder(targetName)
        val file = File(tmp.root, targetName)

        GeometryBinaryCache.writeToFile(file, emptyList())

        val tmpFile = File(tmp.root, "$targetName.tmp")
        assertFalse("Tmp file should be cleaned up after failed rename", tmpFile.exists())
    }

    @Test
    fun `write creates file in non-existent parent gracefully`() {
        // If parent doesn't exist, write should not throw — it just silently fails
        // and leaves no corrupt file. This mirrors the production catch-all.
        val file = File(tmp.root, "subdir/does/not/exist/cache.bin")
        GeometryBinaryCache.writeToFile(file, emptyList())
        // No assertion beyond "no exception thrown"
    }

    // --- idempotency -----------------------------------------------------------

    @Test
    fun `second write overwrites first`() {
        val file = cacheFile()
        GeometryBinaryCache.writeToFile(file, listOf(makeGeometry("us", triangle())))
        GeometryBinaryCache.writeToFile(file, listOf(makeGeometry("fr", triangle(10f, 10f))))

        val result = GeometryBinaryCache.readFromFile(file)!!
        assertEquals(1, result.size)
        assertEquals("fr", result[0].countryId)
    }

    @Test
    fun `read is idempotent — same result on repeated calls`() {
        val geometries = listOf(makeGeometry("jp", triangle()))
        val file = cacheFile()
        GeometryBinaryCache.writeToFile(file, geometries)

        val first = GeometryBinaryCache.readFromFile(file)!!
        val second = GeometryBinaryCache.readFromFile(file)!!

        assertEquals(first.size, second.size)
        assertEquals(first[0].countryId, second[0].countryId)
    }

    // --- context-based API ----------------------------------------------------

    private fun mockContext(): Context =
        Mockito.mock(Context::class.java).also {
            Mockito.`when`(it.filesDir).thenReturn(tmp.root)
        }

    @Test
    fun `write and read via context round-trip through hi-res file`() {
        val ctx = mockContext()
        GeometryBinaryCache.write(ctx, listOf(makeGeometry("de", triangle())))
        val result = GeometryBinaryCache.read(ctx)
        assertNotNull(result)
        assertEquals("de", result!![0].countryId)
    }

    @Test
    fun `read via context returns null when no cache exists`() {
        assertNull(GeometryBinaryCache.read(mockContext()))
    }

    @Test
    fun `writeLowRes and readLowRes via context round-trip through lo-res file`() {
        val ctx = mockContext()
        GeometryBinaryCache.writeLowRes(ctx, listOf(makeGeometry("it", triangle())))
        val result = GeometryBinaryCache.readLowRes(ctx)
        assertNotNull(result)
        assertEquals("it", result!![0].countryId)
    }

    @Test
    fun `readLowRes via context returns null when no lo-res cache exists`() {
        assertNull(GeometryBinaryCache.readLowRes(mockContext()))
    }

    @Test
    fun `write uses hi-res filename, writeLowRes uses lo-res filename`() {
        val ctx = mockContext()
        GeometryBinaryCache.write(ctx, emptyList())
        GeometryBinaryCache.writeLowRes(ctx, emptyList())
        assertTrue(File(tmp.root, "geometry_cache.bin").exists())
        assertTrue(File(tmp.root, "geometry_cache_110m.bin").exists())
    }

    @Test
    fun `hi-res and lo-res caches are independent files`() {
        val ctx = mockContext()
        // Write only to hi-res; lo-res read must return null (different file)
        GeometryBinaryCache.write(ctx, listOf(makeGeometry("fr", triangle())))
        assertNull(GeometryBinaryCache.readLowRes(ctx))
    }

    // --- deleteOrSchedule -----------------------------------------------------

    @Test
    fun `deleteOrSchedule deletes file when deletion succeeds`() {
        val file = tmp.newFile("to_delete.tmp")
        GeometryBinaryCache.deleteOrSchedule(file)
        assertFalse("File should be deleted", file.exists())
    }

    @Test
    fun `deleteOrSchedule schedules deleteOnExit when deletion fails`() {
        val subdir = tmp.newFolder("locked")
        val file = File(subdir, "locked.bin")
        file.createNewFile()
        subdir.setWritable(false)
        try {
            // Skip on systems where setWritable(false) has no effect (e.g. running as root)
            assumeTrue("Requires non-root: setWritable must prevent deletion", !file.delete())
            // file.delete() returned false — the file still exists
            GeometryBinaryCache.deleteOrSchedule(file)
            assertTrue("File must remain when delete() fails; deleteOnExit() was registered", file.exists())
        } finally {
            subdir.setWritable(true)
        }
    }

    // --- large polygon ---------------------------------------------------------

    @Test
    fun `large polygon with many points round-trips correctly`() {
        val points = (0 until 10_000).map { i ->
            LatLng(lat = (i % 180 - 90).toFloat(), lng = (i % 360 - 180).toFloat())
        }
        val geometry = makeGeometry("big", points)
        val file = cacheFile()

        GeometryBinaryCache.writeToFile(file, listOf(geometry))
        val result = GeometryBinaryCache.readFromFile(file)!!

        assertEquals(10_000, result[0].polygons[0].size)
        assertEquals(points[9_999].lat, result[0].polygons[0][9_999].lat, 0.0001f)
        assertEquals(points[9_999].lng, result[0].polygons[0][9_999].lng, 0.0001f)
    }
}
