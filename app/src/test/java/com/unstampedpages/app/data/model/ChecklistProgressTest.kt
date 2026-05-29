package com.unstampedpages.app.data.model

import org.junit.Assert.*
import org.junit.Test

class ChecklistProgressTest {

    // ==================== Default Values Tests ====================

    @Test
    fun `default ChecklistProgress has checkedCount 0`() {
        val progress = ChecklistProgress()
        assertEquals(0, progress.checkedCount)
    }

    @Test
    fun `default ChecklistProgress has totalCount 0`() {
        val progress = ChecklistProgress()
        assertEquals(0, progress.totalCount)
    }

    @Test
    fun `ChecklistProgress can be created with custom values`() {
        val progress = ChecklistProgress(checkedCount = 5, totalCount = 10)
        assertEquals(5, progress.checkedCount)
        assertEquals(10, progress.totalCount)
    }

    // ==================== Percentage Calculation Tests ====================

    @Test
    fun `percentage is 0 when totalCount is 0`() {
        val progress = ChecklistProgress(checkedCount = 0, totalCount = 0)
        assertEquals(0f, progress.percentage, 0.001f)
    }

    @Test
    fun `percentage is 0 when checkedCount is 0`() {
        val progress = ChecklistProgress(checkedCount = 0, totalCount = 10)
        assertEquals(0f, progress.percentage, 0.001f)
    }

    @Test
    fun `percentage is 1 when all items are checked`() {
        val progress = ChecklistProgress(checkedCount = 10, totalCount = 10)
        assertEquals(1f, progress.percentage, 0.001f)
    }

    @Test
    fun `percentage is 0_5 when half items are checked`() {
        val progress = ChecklistProgress(checkedCount = 5, totalCount = 10)
        assertEquals(0.5f, progress.percentage, 0.001f)
    }

    @Test
    fun `percentage is 0_25 when quarter items are checked`() {
        val progress = ChecklistProgress(checkedCount = 1, totalCount = 4)
        assertEquals(0.25f, progress.percentage, 0.001f)
    }

    @Test
    fun `percentage is 0_75 when three quarters items are checked`() {
        val progress = ChecklistProgress(checkedCount = 3, totalCount = 4)
        assertEquals(0.75f, progress.percentage, 0.001f)
    }

    @Test
    fun `percentage is calculated correctly for odd numbers`() {
        val progress = ChecklistProgress(checkedCount = 1, totalCount = 3)
        assertEquals(1f / 3f, progress.percentage, 0.001f)
    }

    @Test
    fun `percentage handles large numbers`() {
        val progress = ChecklistProgress(checkedCount = 500, totalCount = 1000)
        assertEquals(0.5f, progress.percentage, 0.001f)
    }

    @Test
    fun `percentage handles single item checked`() {
        val progress = ChecklistProgress(checkedCount = 1, totalCount = 1)
        assertEquals(1f, progress.percentage, 0.001f)
    }

    @Test
    fun `percentage handles single item unchecked`() {
        val progress = ChecklistProgress(checkedCount = 0, totalCount = 1)
        assertEquals(0f, progress.percentage, 0.001f)
    }

    // ==================== isComplete Tests ====================

    @Test
    fun `isComplete is false when totalCount is 0`() {
        val progress = ChecklistProgress(checkedCount = 0, totalCount = 0)
        assertFalse(progress.isComplete)
    }

    @Test
    fun `isComplete is false when checkedCount equals totalCount but totalCount is 0`() {
        val progress = ChecklistProgress(checkedCount = 0, totalCount = 0)
        assertEquals(progress.checkedCount, progress.totalCount)
        assertFalse(progress.isComplete)
    }

    @Test
    fun `isComplete is true when all items are checked`() {
        val progress = ChecklistProgress(checkedCount = 10, totalCount = 10)
        assertTrue(progress.isComplete)
    }

    @Test
    fun `isComplete is false when not all items are checked`() {
        val progress = ChecklistProgress(checkedCount = 9, totalCount = 10)
        assertFalse(progress.isComplete)
    }

    @Test
    fun `isComplete is false when no items are checked`() {
        val progress = ChecklistProgress(checkedCount = 0, totalCount = 10)
        assertFalse(progress.isComplete)
    }

    @Test
    fun `isComplete is true for single checked item`() {
        val progress = ChecklistProgress(checkedCount = 1, totalCount = 1)
        assertTrue(progress.isComplete)
    }

    @Test
    fun `isComplete is false for single unchecked item`() {
        val progress = ChecklistProgress(checkedCount = 0, totalCount = 1)
        assertFalse(progress.isComplete)
    }

    @Test
    fun `isComplete is true for large list when all checked`() {
        val progress = ChecklistProgress(checkedCount = 1000, totalCount = 1000)
        assertTrue(progress.isComplete)
    }

    // ==================== Equality Tests ====================

    @Test
    fun `ChecklistProgress equality works correctly`() {
        val progress1 = ChecklistProgress(checkedCount = 5, totalCount = 10)
        val progress2 = ChecklistProgress(checkedCount = 5, totalCount = 10)
        val progress3 = ChecklistProgress(checkedCount = 6, totalCount = 10)

        assertEquals(progress1, progress2)
        assertNotEquals(progress1, progress3)
    }

    @Test
    fun `ChecklistProgress equality considers both fields`() {
        val progress1 = ChecklistProgress(checkedCount = 5, totalCount = 10)
        val progress2 = ChecklistProgress(checkedCount = 5, totalCount = 11)

        assertNotEquals(progress1, progress2)
    }

    // ==================== HashCode Tests ====================

    @Test
    fun `ChecklistProgress hashCode is consistent`() {
        val progress = ChecklistProgress(checkedCount = 5, totalCount = 10)
        val hash1 = progress.hashCode()
        val hash2 = progress.hashCode()

        assertEquals(hash1, hash2)
    }

    @Test
    fun `ChecklistProgress hashCode differs for different values`() {
        val progress1 = ChecklistProgress(checkedCount = 5, totalCount = 10)
        val progress2 = ChecklistProgress(checkedCount = 6, totalCount = 10)

        assertNotEquals(progress1.hashCode(), progress2.hashCode())
    }

    @Test
    fun `equal ChecklistProgress objects have same hashCode`() {
        val progress1 = ChecklistProgress(checkedCount = 5, totalCount = 10)
        val progress2 = ChecklistProgress(checkedCount = 5, totalCount = 10)

        assertEquals(progress1.hashCode(), progress2.hashCode())
    }

    // ==================== Copy Tests ====================

    @Test
    fun `ChecklistProgress copy works correctly`() {
        val original = ChecklistProgress(checkedCount = 5, totalCount = 10)
        val copy = original.copy(checkedCount = 6)

        assertEquals(5, original.checkedCount)
        assertEquals(6, copy.checkedCount)
        assertEquals(original.totalCount, copy.totalCount)
    }

    @Test
    fun `ChecklistProgress copy preserves unmodified fields`() {
        val original = ChecklistProgress(checkedCount = 5, totalCount = 10)
        val copy = original.copy()

        assertEquals(original, copy)
    }

    @Test
    fun `ChecklistProgress copy can update totalCount`() {
        val original = ChecklistProgress(checkedCount = 5, totalCount = 10)
        val copy = original.copy(totalCount = 20)

        assertEquals(10, original.totalCount)
        assertEquals(20, copy.totalCount)
    }

    @Test
    fun `ChecklistProgress copy can update both fields`() {
        val original = ChecklistProgress(checkedCount = 5, totalCount = 10)
        val copy = original.copy(checkedCount = 15, totalCount = 20)

        assertEquals(5, original.checkedCount)
        assertEquals(10, original.totalCount)
        assertEquals(15, copy.checkedCount)
        assertEquals(20, copy.totalCount)
    }

    // ==================== Destructuring Tests ====================

    @Test
    fun `ChecklistProgress component1 returns checkedCount`() {
        val progress = ChecklistProgress(checkedCount = 7, totalCount = 15)
        val (checkedCount, _) = progress

        assertEquals(7, checkedCount)
    }

    @Test
    fun `ChecklistProgress component2 returns totalCount`() {
        val progress = ChecklistProgress(checkedCount = 7, totalCount = 15)
        val (_, totalCount) = progress

        assertEquals(15, totalCount)
    }

    @Test
    fun `ChecklistProgress destructuring works correctly`() {
        val progress = ChecklistProgress(checkedCount = 8, totalCount = 12)
        val (checkedCount, totalCount) = progress

        assertEquals(8, checkedCount)
        assertEquals(12, totalCount)
    }

    // ==================== toString Tests ====================

    @Test
    fun `ChecklistProgress toString contains field names`() {
        val progress = ChecklistProgress(checkedCount = 5, totalCount = 10)
        val string = progress.toString()

        assertTrue(string.contains("checkedCount"))
        assertTrue(string.contains("totalCount"))
    }

    @Test
    fun `ChecklistProgress toString contains field values`() {
        val progress = ChecklistProgress(checkedCount = 5, totalCount = 10)
        val string = progress.toString()

        assertTrue(string.contains("5"))
        assertTrue(string.contains("10"))
    }

    // ==================== Edge Cases Tests ====================

    @Test
    fun `percentage does not crash with very large numbers`() {
        val progress = ChecklistProgress(checkedCount = Int.MAX_VALUE / 2, totalCount = Int.MAX_VALUE)
        val percentage = progress.percentage
        assertTrue(percentage >= 0f && percentage <= 1f)
    }

    // ==================== Progress State Transition Tests ====================

    @Test
    fun `progress transitions from 0 to partial`() {
        var progress = ChecklistProgress(checkedCount = 0, totalCount = 10)
        assertFalse(progress.isComplete)
        assertEquals(0f, progress.percentage, 0.001f)

        progress = progress.copy(checkedCount = 5)
        assertFalse(progress.isComplete)
        assertEquals(0.5f, progress.percentage, 0.001f)
    }

    @Test
    fun `progress transitions from partial to complete`() {
        var progress = ChecklistProgress(checkedCount = 9, totalCount = 10)
        assertFalse(progress.isComplete)

        progress = progress.copy(checkedCount = 10)
        assertTrue(progress.isComplete)
        assertEquals(1f, progress.percentage, 0.001f)
    }

    @Test
    fun `progress transitions from complete to partial`() {
        var progress = ChecklistProgress(checkedCount = 10, totalCount = 10)
        assertTrue(progress.isComplete)

        progress = progress.copy(checkedCount = 9)
        assertFalse(progress.isComplete)
    }

    @Test
    fun `adding item to total changes percentage`() {
        var progress = ChecklistProgress(checkedCount = 5, totalCount = 10)
        assertEquals(0.5f, progress.percentage, 0.001f)

        progress = progress.copy(totalCount = 20)
        assertEquals(0.25f, progress.percentage, 0.001f)
    }

    // ==================== Computed Property Consistency Tests ====================

    @Test
    fun `computed properties are consistent with each other`() {
        val progress = ChecklistProgress(checkedCount = 10, totalCount = 10)

        assertTrue(progress.isComplete)
        assertEquals(1f, progress.percentage, 0.001f)
    }

    @Test
    fun `computed properties reflect empty state`() {
        val progress = ChecklistProgress()

        assertFalse(progress.isComplete)
        assertEquals(0f, progress.percentage, 0.001f)
    }
}
