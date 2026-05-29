package com.unstampedpages.app.ui.screens.checklist.components

import org.junit.Assert.*
import org.junit.Test

/**
 * Comprehensive unit tests for SwipeableChecklistItem.kt components.
 *
 * Note: Composable functions require instrumented tests with Compose UI testing.
 * These tests cover the data classes and any testable logic.
 */
class SwipeableChecklistItemTest {

    // ==================== SwipeableItemState Tests ====================

    @Test
    fun `SwipeableItemState default values are correct`() {
        val state = SwipeableItemState(
            isMultiSelectMode = false,
            isSelected = false
        )

        assertFalse(state.isMultiSelectMode)
        assertFalse(state.isSelected)
    }

    @Test
    fun `SwipeableItemState can be created with multiSelectMode true`() {
        val state = SwipeableItemState(
            isMultiSelectMode = true,
            isSelected = false
        )

        assertTrue(state.isMultiSelectMode)
        assertFalse(state.isSelected)
    }

    @Test
    fun `SwipeableItemState can be created with isSelected true`() {
        val state = SwipeableItemState(
            isMultiSelectMode = false,
            isSelected = true
        )

        assertFalse(state.isMultiSelectMode)
        assertTrue(state.isSelected)
    }

    @Test
    fun `SwipeableItemState can be created with both flags true`() {
        val state = SwipeableItemState(
            isMultiSelectMode = true,
            isSelected = true
        )

        assertTrue(state.isMultiSelectMode)
        assertTrue(state.isSelected)
    }

    @Test
    fun `SwipeableItemState equality works correctly`() {
        val state1 = SwipeableItemState(isMultiSelectMode = true, isSelected = false)
        val state2 = SwipeableItemState(isMultiSelectMode = true, isSelected = false)
        val state3 = SwipeableItemState(isMultiSelectMode = false, isSelected = false)

        assertEquals(state1, state2)
        assertNotEquals(state1, state3)
    }

    @Test
    fun `SwipeableItemState hashCode is consistent`() {
        val state = SwipeableItemState(isMultiSelectMode = true, isSelected = true)
        val hash1 = state.hashCode()
        val hash2 = state.hashCode()

        assertEquals(hash1, hash2)
    }

    @Test
    fun `SwipeableItemState hashCode differs for different states`() {
        val state1 = SwipeableItemState(isMultiSelectMode = true, isSelected = true)
        val state2 = SwipeableItemState(isMultiSelectMode = false, isSelected = false)

        assertNotEquals(state1.hashCode(), state2.hashCode())
    }

    @Test
    fun `SwipeableItemState copy works correctly`() {
        val original = SwipeableItemState(isMultiSelectMode = true, isSelected = false)
        val copy = original.copy(isSelected = true)

        assertTrue(original.isMultiSelectMode)
        assertFalse(original.isSelected)
        assertTrue(copy.isMultiSelectMode)
        assertTrue(copy.isSelected)
    }

    @Test
    fun `SwipeableItemState copy preserves unchanged values`() {
        val original = SwipeableItemState(isMultiSelectMode = true, isSelected = true)
        val copy = original.copy()

        assertEquals(original, copy)
    }

    @Test
    fun `SwipeableItemState toString contains field values`() {
        val state = SwipeableItemState(isMultiSelectMode = true, isSelected = false)
        val string = state.toString()

        assertTrue(string.contains("isMultiSelectMode"))
        assertTrue(string.contains("isSelected"))
        assertTrue(string.contains("true"))
        assertTrue(string.contains("false"))
    }

    @Test
    fun `SwipeableItemState component1 returns isMultiSelectMode`() {
        val state = SwipeableItemState(isMultiSelectMode = true, isSelected = false)
        val (multiSelect, _) = state

        assertTrue(multiSelect)
    }

    @Test
    fun `SwipeableItemState component2 returns isSelected`() {
        val state = SwipeableItemState(isMultiSelectMode = false, isSelected = true)
        val (_, selected) = state

        assertTrue(selected)
    }

    @Test
    fun `SwipeableItemState destructuring works correctly`() {
        val state = SwipeableItemState(isMultiSelectMode = true, isSelected = true)
        val (multiSelect, selected) = state

        assertTrue(multiSelect)
        assertTrue(selected)
    }

    // ==================== SwipeableItemCallbacks Tests ====================

    @Test
    fun `SwipeableItemCallbacks can be created with all callbacks`() {
        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = {},
            onDelete = {},
            onPin = {},
            onQuantityChange = {},
            onLongPress = {},
            onSelect = {}
        )

        assertNotNull(callbacks)
    }

    @Test
    fun `SwipeableItemCallbacks onCheckedChange can be invoked`() {
        var invoked = false

        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = { invoked = true },
            onDelete = {},
            onPin = {},
            onQuantityChange = {},
            onLongPress = {},
            onSelect = {}
        )

        callbacks.onCheckedChange()
        assertTrue(invoked)
    }

    @Test
    fun `SwipeableItemCallbacks onDelete can be invoked`() {
        var invoked = false

        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = {},
            onDelete = { invoked = true },
            onPin = {},
            onQuantityChange = {},
            onLongPress = {},
            onSelect = {}
        )

        callbacks.onDelete()
        assertTrue(invoked)
    }

    @Test
    fun `SwipeableItemCallbacks onPin can be invoked`() {
        var invoked = false

        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = {},
            onDelete = {},
            onPin = { invoked = true },
            onQuantityChange = {},
            onLongPress = {},
            onSelect = {}
        )

        callbacks.onPin()
        assertTrue(invoked)
    }

    @Test
    fun `SwipeableItemCallbacks onQuantityChange receives correct value`() {
        var receivedValue = -1

        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = {},
            onDelete = {},
            onPin = {},
            onQuantityChange = { receivedValue = it },
            onLongPress = {},
            onSelect = {}
        )

        callbacks.onQuantityChange(5)
        assertEquals(5, receivedValue)
    }

    @Test
    fun `SwipeableItemCallbacks onQuantityChange handles zero`() {
        var receivedValue = -1

        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = {},
            onDelete = {},
            onPin = {},
            onQuantityChange = { receivedValue = it },
            onLongPress = {},
            onSelect = {}
        )

        callbacks.onQuantityChange(0)
        assertEquals(0, receivedValue)
    }

    @Test
    fun `SwipeableItemCallbacks onQuantityChange handles negative values`() {
        var receivedValue = 0

        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = {},
            onDelete = {},
            onPin = {},
            onQuantityChange = { receivedValue = it },
            onLongPress = {},
            onSelect = {}
        )

        callbacks.onQuantityChange(-1)
        assertEquals(-1, receivedValue)
    }

    @Test
    fun `SwipeableItemCallbacks onQuantityChange handles large values`() {
        var receivedValue = 0

        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = {},
            onDelete = {},
            onPin = {},
            onQuantityChange = { receivedValue = it },
            onLongPress = {},
            onSelect = {}
        )

        callbacks.onQuantityChange(Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, receivedValue)
    }

    @Test
    fun `SwipeableItemCallbacks onLongPress can be invoked`() {
        var invoked = false

        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = {},
            onDelete = {},
            onPin = {},
            onQuantityChange = {},
            onLongPress = { invoked = true },
            onSelect = {}
        )

        callbacks.onLongPress()
        assertTrue(invoked)
    }

    @Test
    fun `SwipeableItemCallbacks onSelect can be invoked`() {
        var invoked = false

        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = {},
            onDelete = {},
            onPin = {},
            onQuantityChange = {},
            onLongPress = {},
            onSelect = { invoked = true }
        )

        callbacks.onSelect()
        assertTrue(invoked)
    }

    @Test
    fun `SwipeableItemCallbacks callbacks are independent`() {
        var checkedCount = 0
        var deleteCount = 0
        var pinCount = 0
        var quantitySum = 0
        var longPressCount = 0
        var selectCount = 0

        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = { checkedCount++ },
            onDelete = { deleteCount++ },
            onPin = { pinCount++ },
            onQuantityChange = { quantitySum += it },
            onLongPress = { longPressCount++ },
            onSelect = { selectCount++ }
        )

        callbacks.onCheckedChange()
        callbacks.onCheckedChange()
        callbacks.onDelete()
        callbacks.onPin()
        callbacks.onPin()
        callbacks.onPin()
        callbacks.onQuantityChange(3)
        callbacks.onQuantityChange(7)
        callbacks.onLongPress()
        callbacks.onSelect()
        callbacks.onSelect()

        assertEquals(2, checkedCount)
        assertEquals(1, deleteCount)
        assertEquals(3, pinCount)
        assertEquals(10, quantitySum)
        assertEquals(1, longPressCount)
        assertEquals(2, selectCount)
    }

    @Test
    fun `SwipeableItemCallbacks equality works correctly`() {
        val lambda1: () -> Unit = {}
        val lambda2: (Int) -> Unit = {}

        val callbacks1 = SwipeableItemCallbacks(
            onCheckedChange = lambda1,
            onDelete = lambda1,
            onPin = lambda1,
            onQuantityChange = lambda2,
            onLongPress = lambda1,
            onSelect = lambda1
        )

        val callbacks2 = SwipeableItemCallbacks(
            onCheckedChange = lambda1,
            onDelete = lambda1,
            onPin = lambda1,
            onQuantityChange = lambda2,
            onLongPress = lambda1,
            onSelect = lambda1
        )

        // Same lambdas should be equal
        assertEquals(callbacks1, callbacks2)
    }

    @Test
    fun `SwipeableItemCallbacks with different lambdas are not equal`() {
        val callbacks1 = SwipeableItemCallbacks(
            onCheckedChange = {},
            onDelete = {},
            onPin = {},
            onQuantityChange = {},
            onLongPress = {},
            onSelect = {}
        )

        val callbacks2 = SwipeableItemCallbacks(
            onCheckedChange = {},
            onDelete = {},
            onPin = {},
            onQuantityChange = {},
            onLongPress = {},
            onSelect = {}
        )

        // Different lambda instances are not equal
        assertNotEquals(callbacks1, callbacks2)
    }

    @Test
    fun `SwipeableItemCallbacks copy works correctly`() {
        var originalDeleteCalled = false
        var newDeleteCalled = false

        val original = SwipeableItemCallbacks(
            onCheckedChange = {},
            onDelete = { originalDeleteCalled = true },
            onPin = {},
            onQuantityChange = {},
            onLongPress = {},
            onSelect = {}
        )

        val copy = original.copy(onDelete = { newDeleteCalled = true })

        original.onDelete()
        copy.onDelete()

        assertTrue(originalDeleteCalled)
        assertTrue(newDeleteCalled)
    }

    @Test
    fun `SwipeableItemCallbacks toString contains function references`() {
        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = {},
            onDelete = {},
            onPin = {},
            onQuantityChange = {},
            onLongPress = {},
            onSelect = {}
        )

        val string = callbacks.toString()
        assertTrue(string.contains("SwipeableItemCallbacks"))
    }

    // ==================== Integration Tests ====================

    @Test
    fun `SwipeableItemState and SwipeableItemCallbacks can be used together`() {
        val state = SwipeableItemState(
            isMultiSelectMode = true,
            isSelected = false
        )

        var wasSelected = false

        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = {},
            onDelete = {},
            onPin = {},
            onQuantityChange = {},
            onLongPress = {},
            onSelect = { wasSelected = true }
        )

        // Simulate multi-select mode behavior
        if (state.isMultiSelectMode) {
            callbacks.onSelect()
        }

        assertTrue(wasSelected)
    }

    @Test
    fun `SwipeableItemState controls callback behavior - normal mode`() {
        val state = SwipeableItemState(
            isMultiSelectMode = false,
            isSelected = false
        )

        var checkedChanged = false

        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = { checkedChanged = true },
            onDelete = {},
            onPin = {},
            onQuantityChange = {},
            onLongPress = {},
            onSelect = {}
        )

        // Simulate normal mode behavior
        if (!state.isMultiSelectMode) {
            callbacks.onCheckedChange()
        }

        assertTrue(checkedChanged)
    }

    @Test
    fun `multiple state transitions work correctly`() {
        var state = SwipeableItemState(isMultiSelectMode = false, isSelected = false)

        // Enter multi-select mode
        state = state.copy(isMultiSelectMode = true)
        assertTrue(state.isMultiSelectMode)
        assertFalse(state.isSelected)

        // Select item
        state = state.copy(isSelected = true)
        assertTrue(state.isMultiSelectMode)
        assertTrue(state.isSelected)

        // Deselect item
        state = state.copy(isSelected = false)
        assertTrue(state.isMultiSelectMode)
        assertFalse(state.isSelected)

        // Exit multi-select mode
        state = state.copy(isMultiSelectMode = false)
        assertFalse(state.isMultiSelectMode)
        assertFalse(state.isSelected)
    }

    // ==================== Edge Case Tests ====================

    @Test
    fun `SwipeableItemCallbacks can handle exceptions in callbacks`() {
        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = { throw RuntimeException("Test exception") },
            onDelete = {},
            onPin = {},
            onQuantityChange = {},
            onLongPress = {},
            onSelect = {}
        )

        var exceptionThrown = false
        try {
            callbacks.onCheckedChange()
        } catch (e: RuntimeException) {
            exceptionThrown = true
            assertEquals("Test exception", e.message)
        }

        assertTrue(exceptionThrown)
    }

    @Test
    fun `SwipeableItemCallbacks can be chained`() {
        var step1 = false
        var step2 = false
        var step3 = false

        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = {
                step1 = true
            },
            onDelete = {
                step2 = true
            },
            onPin = {
                step3 = true
            },
            onQuantityChange = {},
            onLongPress = {},
            onSelect = {}
        )

        // Simulate a sequence of actions
        callbacks.onCheckedChange()
        assertTrue(step1)
        assertFalse(step2)

        callbacks.onDelete()
        assertTrue(step2)
        assertFalse(step3)

        callbacks.onPin()
        assertTrue(step3)
    }

    @Test
    fun `SwipeableItemState all possible combinations`() {
        val allCombinations = listOf(
            SwipeableItemState(isMultiSelectMode = false, isSelected = false),
            SwipeableItemState(isMultiSelectMode = false, isSelected = true),
            SwipeableItemState(isMultiSelectMode = true, isSelected = false),
            SwipeableItemState(isMultiSelectMode = true, isSelected = true)
        )

        assertEquals(4, allCombinations.size)
        assertEquals(4, allCombinations.distinct().size) // All should be unique
    }

    @Test
    fun `SwipeableItemCallbacks onQuantityChange called multiple times accumulates correctly`() {
        val quantities = mutableListOf<Int>()

        val callbacks = SwipeableItemCallbacks(
            onCheckedChange = {},
            onDelete = {},
            onPin = {},
            onQuantityChange = { quantities.add(it) },
            onLongPress = {},
            onSelect = {}
        )

        callbacks.onQuantityChange(1)
        callbacks.onQuantityChange(2)
        callbacks.onQuantityChange(3)
        callbacks.onQuantityChange(2)
        callbacks.onQuantityChange(1)

        assertEquals(listOf(1, 2, 3, 2, 1), quantities)
    }
}
