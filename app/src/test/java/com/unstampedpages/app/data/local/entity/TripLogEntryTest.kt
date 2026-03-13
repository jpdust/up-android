package com.unstampedpages.app.data.local.entity

import org.junit.Assert.*
import org.junit.Test

class TripLogEntryTest {

    @Test
    fun `default id is zero`() {
        val entry = TripLogEntry(title = "Test", content = "Content")

        assertEquals(0L, entry.id)
    }

    @Test
    fun `title is set correctly`() {
        val entry = TripLogEntry(title = "Paris Trip", content = "Great day")

        assertEquals("Paris Trip", entry.title)
    }

    @Test
    fun `content is set correctly`() {
        val entry = TripLogEntry(title = "Test", content = "Visited the Eiffel Tower")

        assertEquals("Visited the Eiffel Tower", entry.content)
    }

    @Test
    fun `default location is null`() {
        val entry = TripLogEntry(title = "Test", content = "Content")

        assertNull(entry.location)
    }

    @Test
    fun `location can be set`() {
        val entry = TripLogEntry(title = "Test", content = "Content", location = "Paris, France")

        assertEquals("Paris, France", entry.location)
    }

    @Test
    fun `date is set to current time by default`() {
        val before = System.currentTimeMillis()
        val entry = TripLogEntry(title = "Test", content = "Content")
        val after = System.currentTimeMillis()

        assertTrue(entry.date >= before)
        assertTrue(entry.date <= after)
    }

    @Test
    fun `date can be set explicitly`() {
        val timestamp = 1234567890L
        val entry = TripLogEntry(title = "Test", content = "Content", date = timestamp)

        assertEquals(timestamp, entry.date)
    }

    @Test
    fun `createdAt is set to current time by default`() {
        val before = System.currentTimeMillis()
        val entry = TripLogEntry(title = "Test", content = "Content")
        val after = System.currentTimeMillis()

        assertTrue(entry.createdAt >= before)
        assertTrue(entry.createdAt <= after)
    }

    @Test
    fun `updatedAt is set to current time by default`() {
        val before = System.currentTimeMillis()
        val entry = TripLogEntry(title = "Test", content = "Content")
        val after = System.currentTimeMillis()

        assertTrue(entry.updatedAt >= before)
        assertTrue(entry.updatedAt <= after)
    }

    @Test
    fun `copy creates new instance with same values`() {
        val original = TripLogEntry(
            id = 1,
            title = "Trip",
            content = "Content",
            date = 1000L,
            location = "Tokyo",
            createdAt = 2000L,
            updatedAt = 3000L
        )

        val copy = original.copy()

        assertEquals(original.id, copy.id)
        assertEquals(original.title, copy.title)
        assertEquals(original.content, copy.content)
        assertEquals(original.date, copy.date)
        assertEquals(original.location, copy.location)
        assertEquals(original.createdAt, copy.createdAt)
        assertEquals(original.updatedAt, copy.updatedAt)
    }

    @Test
    fun `copy can modify title`() {
        val original = TripLogEntry(title = "Original", content = "Content")

        val modified = original.copy(title = "Modified")

        assertEquals("Modified", modified.title)
        assertEquals("Original", original.title)
    }

    @Test
    fun `copy can modify content`() {
        val original = TripLogEntry(title = "Test", content = "Original content")

        val modified = original.copy(content = "Modified content")

        assertEquals("Modified content", modified.content)
    }

    @Test
    fun `copy can set location to null`() {
        val original = TripLogEntry(title = "Test", content = "Content", location = "Paris")

        val modified = original.copy(location = null)

        assertNull(modified.location)
        assertEquals("Paris", original.location)
    }

    @Test
    fun `equals returns true for same values`() {
        val entry1 = TripLogEntry(
            id = 1, title = "Test", content = "Content",
            date = 1000L, location = "Paris", createdAt = 2000L, updatedAt = 3000L
        )
        val entry2 = TripLogEntry(
            id = 1, title = "Test", content = "Content",
            date = 1000L, location = "Paris", createdAt = 2000L, updatedAt = 3000L
        )

        assertEquals(entry1, entry2)
    }

    @Test
    fun `equals returns false for different title`() {
        val entry1 = TripLogEntry(title = "Test1", content = "Content")
        val entry2 = TripLogEntry(title = "Test2", content = "Content")

        assertNotEquals(entry1, entry2)
    }

    @Test
    fun `hashCode is consistent for equal entries`() {
        val entry1 = TripLogEntry(
            id = 1, title = "Test", content = "Content",
            date = 1000L, location = "Paris", createdAt = 2000L, updatedAt = 3000L
        )
        val entry2 = TripLogEntry(
            id = 1, title = "Test", content = "Content",
            date = 1000L, location = "Paris", createdAt = 2000L, updatedAt = 3000L
        )

        assertEquals(entry1.hashCode(), entry2.hashCode())
    }

    @Test
    fun `entry with empty title is valid`() {
        val entry = TripLogEntry(title = "", content = "Content")

        assertEquals("", entry.title)
    }

    @Test
    fun `entry with empty content is valid`() {
        val entry = TripLogEntry(title = "Test", content = "")

        assertEquals("", entry.content)
    }

    @Test
    fun `entry with long content is valid`() {
        val longContent = "A".repeat(10000)
        val entry = TripLogEntry(title = "Test", content = longContent)

        assertEquals(longContent, entry.content)
    }
}
