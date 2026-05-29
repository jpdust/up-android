package com.unstampedpages.app.data.local.entity

import org.junit.Assert.*
import org.junit.Test

private const val TEST_ITEM = "Test Item"

class ChecklistItemTest {

    @Test
    fun `default id is zero`() {
        val item = ChecklistItem(content = TEST_ITEM)

        assertEquals(0L, item.id)
    }

    @Test
    fun `content is set correctly`() {
        val item = ChecklistItem(content = "Pack sunscreen")

        assertEquals("Pack sunscreen", item.content)
    }

    @Test
    fun `default isChecked is false`() {
        val item = ChecklistItem(content = TEST_ITEM)

        assertFalse(item.isChecked)
    }

    @Test
    fun `isChecked can be set to true`() {
        val item = ChecklistItem(content = TEST_ITEM, isChecked = true)

        assertTrue(item.isChecked)
    }

    @Test
    fun `createdAt is set to current time by default`() {
        val before = System.currentTimeMillis()
        val item = ChecklistItem(content = TEST_ITEM)
        val after = System.currentTimeMillis()

        assertTrue(item.createdAt >= before)
        assertTrue(item.createdAt <= after)
    }

    @Test
    fun `updatedAt is set to current time by default`() {
        val before = System.currentTimeMillis()
        val item = ChecklistItem(content = TEST_ITEM)
        val after = System.currentTimeMillis()

        assertTrue(item.updatedAt >= before)
        assertTrue(item.updatedAt <= after)
    }

    @Test
    fun `createdAt can be set explicitly`() {
        val timestamp = 1234567890L
        val item = ChecklistItem(content = TEST_ITEM, createdAt = timestamp)

        assertEquals(timestamp, item.createdAt)
    }

    @Test
    fun `updatedAt can be set explicitly`() {
        val timestamp = 1234567890L
        val item = ChecklistItem(content = TEST_ITEM, updatedAt = timestamp)

        assertEquals(timestamp, item.updatedAt)
    }

    @Test
    fun `copy creates new instance with same values`() {
        val original = ChecklistItem(
            id = 1,
            content = "Original",
            isChecked = true,
            createdAt = 1000L,
            updatedAt = 2000L
        )

        val copy = original.copy()

        assertEquals(original.id, copy.id)
        assertEquals(original.content, copy.content)
        assertEquals(original.isChecked, copy.isChecked)
        assertEquals(original.createdAt, copy.createdAt)
        assertEquals(original.updatedAt, copy.updatedAt)
    }

    @Test
    fun `copy can modify content`() {
        val original = ChecklistItem(content = "Original")

        val modified = original.copy(content = "Modified")

        assertEquals("Modified", modified.content)
        assertEquals("Original", original.content)
    }

    @Test
    fun `copy can toggle isChecked`() {
        val original = ChecklistItem(content = "Test", isChecked = false)

        val toggled = original.copy(isChecked = true)

        assertTrue(toggled.isChecked)
        assertFalse(original.isChecked)
    }

    @Test
    fun `equals returns true for same values`() {
        val item1 = ChecklistItem(id = 1, content = "Test", isChecked = true, createdAt = 1000L, updatedAt = 2000L)
        val item2 = ChecklistItem(id = 1, content = "Test", isChecked = true, createdAt = 1000L, updatedAt = 2000L)

        assertEquals(item1, item2)
    }

    @Test
    fun `equals returns false for different content`() {
        val item1 = ChecklistItem(content = "Test1")
        val item2 = ChecklistItem(content = "Test2")

        assertNotEquals(item1, item2)
    }

    @Test
    fun `hashCode is consistent for equal items`() {
        val item1 = ChecklistItem(id = 1, content = "Test", isChecked = true, createdAt = 1000L, updatedAt = 2000L)
        val item2 = ChecklistItem(id = 1, content = "Test", isChecked = true, createdAt = 1000L, updatedAt = 2000L)

        assertEquals(item1.hashCode(), item2.hashCode())
    }

    @Test
    fun `item with empty content is valid`() {
        val item = ChecklistItem(content = "")

        assertEquals("", item.content)
    }

    @Test
    fun `item with long content is valid`() {
        val longContent = "A".repeat(1000)
        val item = ChecklistItem(content = longContent)

        assertEquals(longContent, item.content)
    }
}
