package com.unstampedpages.app.data.local.entity

import org.junit.Assert.*
import org.junit.Test

class StampItemTest {

    @Test
    fun `countryCode is set correctly`() {
        val stamp = StampItem(countryCode = "US", countryName = "United States")

        assertEquals("US", stamp.countryCode)
    }

    @Test
    fun `countryName is set correctly`() {
        val stamp = StampItem(countryCode = "US", countryName = "United States")

        assertEquals("United States", stamp.countryName)
    }

    @Test
    fun `default imagePath is null`() {
        val stamp = StampItem(countryCode = "US", countryName = "United States")

        assertNull(stamp.imagePath)
    }

    @Test
    fun `imagePath can be set`() {
        val stamp = StampItem(
            countryCode = "US",
            countryName = "United States",
            imagePath = "/path/to/stamp.jpg"
        )

        assertEquals("/path/to/stamp.jpg", stamp.imagePath)
    }

    @Test
    fun `createdAt is set to current time by default`() {
        val before = System.currentTimeMillis()
        val stamp = StampItem(countryCode = "US", countryName = "United States")
        val after = System.currentTimeMillis()

        assertTrue(stamp.createdAt >= before)
        assertTrue(stamp.createdAt <= after)
    }

    @Test
    fun `updatedAt is set to current time by default`() {
        val before = System.currentTimeMillis()
        val stamp = StampItem(countryCode = "US", countryName = "United States")
        val after = System.currentTimeMillis()

        assertTrue(stamp.updatedAt >= before)
        assertTrue(stamp.updatedAt <= after)
    }

    @Test
    fun `createdAt can be set explicitly`() {
        val timestamp = 1234567890L
        val stamp = StampItem(
            countryCode = "US",
            countryName = "United States",
            createdAt = timestamp
        )

        assertEquals(timestamp, stamp.createdAt)
    }

    @Test
    fun `updatedAt can be set explicitly`() {
        val timestamp = 1234567890L
        val stamp = StampItem(
            countryCode = "US",
            countryName = "United States",
            updatedAt = timestamp
        )

        assertEquals(timestamp, stamp.updatedAt)
    }

    @Test
    fun `copy creates new instance with same values`() {
        val original = StampItem(
            countryCode = "US",
            countryName = "United States",
            imagePath = "/path/to/image.jpg",
            createdAt = 1000L,
            updatedAt = 2000L
        )

        val copy = original.copy()

        assertEquals(original.countryCode, copy.countryCode)
        assertEquals(original.countryName, copy.countryName)
        assertEquals(original.imagePath, copy.imagePath)
        assertEquals(original.createdAt, copy.createdAt)
        assertEquals(original.updatedAt, copy.updatedAt)
    }

    @Test
    fun `copy can modify imagePath`() {
        val original = StampItem(countryCode = "US", countryName = "United States")

        val modified = original.copy(imagePath = "/new/path.jpg")

        assertEquals("/new/path.jpg", modified.imagePath)
        assertNull(original.imagePath)
    }

    @Test
    fun `copy can set imagePath to null`() {
        val original = StampItem(
            countryCode = "US",
            countryName = "United States",
            imagePath = "/path/to/image.jpg"
        )

        val modified = original.copy(imagePath = null)

        assertNull(modified.imagePath)
        assertEquals("/path/to/image.jpg", original.imagePath)
    }

    @Test
    fun `equals returns true for same values`() {
        val stamp1 = StampItem(
            countryCode = "US",
            countryName = "United States",
            imagePath = "/path.jpg",
            createdAt = 1000L,
            updatedAt = 2000L
        )
        val stamp2 = StampItem(
            countryCode = "US",
            countryName = "United States",
            imagePath = "/path.jpg",
            createdAt = 1000L,
            updatedAt = 2000L
        )

        assertEquals(stamp1, stamp2)
    }

    @Test
    fun `equals returns false for different countryCode`() {
        val stamp1 = StampItem(countryCode = "US", countryName = "United States")
        val stamp2 = StampItem(countryCode = "GB", countryName = "United States")

        assertNotEquals(stamp1, stamp2)
    }

    @Test
    fun `hashCode is consistent for equal stamps`() {
        val stamp1 = StampItem(
            countryCode = "US",
            countryName = "United States",
            imagePath = "/path.jpg",
            createdAt = 1000L,
            updatedAt = 2000L
        )
        val stamp2 = StampItem(
            countryCode = "US",
            countryName = "United States",
            imagePath = "/path.jpg",
            createdAt = 1000L,
            updatedAt = 2000L
        )

        assertEquals(stamp1.hashCode(), stamp2.hashCode())
    }

    @Test
    fun `countryCode is case sensitive`() {
        val stamp1 = StampItem(countryCode = "US", countryName = "United States")
        val stamp2 = StampItem(countryCode = "us", countryName = "United States")

        assertNotEquals(stamp1, stamp2)
    }

    @Test
    fun `stamp with special characters in path is valid`() {
        val path = "/files/upimages/stamp_US_1234567890.jpg"
        val stamp = StampItem(
            countryCode = "US",
            countryName = "United States",
            imagePath = path
        )

        assertEquals(path, stamp.imagePath)
    }
}
