package com.unstampedpages.app.ui.screens.checklist.components

import com.unstampedpages.app.data.local.entity.ChecklistItem
import org.junit.Assert.*
import org.junit.Test

class CategorySectionStateTest {

    // ==================== Default Values Tests ====================

    @Test
    fun `default CategorySectionState has isExpanded false`() {
        val state = CategorySectionState(
            isExpanded = false,
            isMultiSelectMode = false,
            selectedItemIds = emptySet()
        )

        assertFalse(state.isExpanded)
    }

    @Test
    fun `CategorySectionState can be created with isExpanded true`() {
        val state = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = false,
            selectedItemIds = emptySet()
        )

        assertTrue(state.isExpanded)
    }

    @Test
    fun `CategorySectionState can be created with isMultiSelectMode true`() {
        val state = CategorySectionState(
            isExpanded = false,
            isMultiSelectMode = true,
            selectedItemIds = emptySet()
        )

        assertTrue(state.isMultiSelectMode)
    }

    @Test
    fun `CategorySectionState can be created with selectedItemIds`() {
        val selectedIds = setOf(1L, 2L, 3L)
        val state = CategorySectionState(
            isExpanded = false,
            isMultiSelectMode = true,
            selectedItemIds = selectedIds
        )

        assertEquals(selectedIds, state.selectedItemIds)
        assertEquals(3, state.selectedItemIds.size)
    }

    // ==================== Equality Tests ====================

    @Test
    fun `CategorySectionState equality works correctly`() {
        val state1 = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = false,
            selectedItemIds = setOf(1L)
        )
        val state2 = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = false,
            selectedItemIds = setOf(1L)
        )
        val state3 = CategorySectionState(
            isExpanded = false,
            isMultiSelectMode = false,
            selectedItemIds = setOf(1L)
        )

        assertEquals(state1, state2)
        assertNotEquals(state1, state3)
    }

    @Test
    fun `CategorySectionState equality considers selectedItemIds`() {
        val state1 = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = true,
            selectedItemIds = setOf(1L, 2L)
        )
        val state2 = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = true,
            selectedItemIds = setOf(1L, 3L)
        )

        assertNotEquals(state1, state2)
    }

    // ==================== HashCode Tests ====================

    @Test
    fun `CategorySectionState hashCode is consistent`() {
        val state = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = true,
            selectedItemIds = setOf(1L, 2L)
        )
        val hash1 = state.hashCode()
        val hash2 = state.hashCode()

        assertEquals(hash1, hash2)
    }

    @Test
    fun `CategorySectionState hashCode differs for different states`() {
        val state1 = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = true,
            selectedItemIds = setOf(1L)
        )
        val state2 = CategorySectionState(
            isExpanded = false,
            isMultiSelectMode = false,
            selectedItemIds = emptySet()
        )

        assertNotEquals(state1.hashCode(), state2.hashCode())
    }

    // ==================== Copy Tests ====================

    @Test
    fun `CategorySectionState copy works correctly`() {
        val original = CategorySectionState(
            isExpanded = false,
            isMultiSelectMode = false,
            selectedItemIds = emptySet()
        )
        val copy = original.copy(isExpanded = true)

        assertFalse(original.isExpanded)
        assertTrue(copy.isExpanded)
        assertEquals(original.isMultiSelectMode, copy.isMultiSelectMode)
        assertEquals(original.selectedItemIds, copy.selectedItemIds)
    }

    @Test
    fun `CategorySectionState copy preserves unmodified fields`() {
        val original = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = true,
            selectedItemIds = setOf(1L, 2L, 3L)
        )
        val copy = original.copy(isExpanded = false)

        assertFalse(copy.isExpanded)
        assertTrue(copy.isMultiSelectMode)
        assertEquals(setOf(1L, 2L, 3L), copy.selectedItemIds)
    }

    @Test
    fun `CategorySectionState copy can update selectedItemIds`() {
        val original = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = true,
            selectedItemIds = setOf(1L)
        )
        val copy = original.copy(selectedItemIds = setOf(1L, 2L, 3L))

        assertEquals(1, original.selectedItemIds.size)
        assertEquals(3, copy.selectedItemIds.size)
    }

    // ==================== Destructuring Tests ====================

    @Test
    fun `CategorySectionState component1 returns isExpanded`() {
        val state = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = false,
            selectedItemIds = emptySet()
        )
        val (isExpanded, _, _) = state

        assertTrue(isExpanded)
    }

    @Test
    fun `CategorySectionState component2 returns isMultiSelectMode`() {
        val state = CategorySectionState(
            isExpanded = false,
            isMultiSelectMode = true,
            selectedItemIds = emptySet()
        )
        val (_, isMultiSelectMode, _) = state

        assertTrue(isMultiSelectMode)
    }

    @Test
    fun `CategorySectionState component3 returns selectedItemIds`() {
        val ids = setOf(5L, 10L, 15L)
        val state = CategorySectionState(
            isExpanded = false,
            isMultiSelectMode = false,
            selectedItemIds = ids
        )
        val (_, _, selectedItemIds) = state

        assertEquals(ids, selectedItemIds)
    }

    @Test
    fun `CategorySectionState destructuring works correctly`() {
        val state = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = true,
            selectedItemIds = setOf(1L, 2L)
        )
        val (isExpanded, isMultiSelectMode, selectedItemIds) = state

        assertTrue(isExpanded)
        assertTrue(isMultiSelectMode)
        assertEquals(setOf(1L, 2L), selectedItemIds)
    }

    // ==================== toString Tests ====================

    @Test
    fun `CategorySectionState toString contains field names`() {
        val state = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = false,
            selectedItemIds = setOf(1L)
        )
        val string = state.toString()

        assertTrue(string.contains("isExpanded"))
        assertTrue(string.contains("isMultiSelectMode"))
        assertTrue(string.contains("selectedItemIds"))
    }

    // ==================== Edge Cases ====================

    @Test
    fun `CategorySectionState with empty selectedItemIds`() {
        val state = CategorySectionState(
            isExpanded = false,
            isMultiSelectMode = true,
            selectedItemIds = emptySet()
        )

        assertTrue(state.selectedItemIds.isEmpty())
    }

    @Test
    fun `CategorySectionState with large selectedItemIds set`() {
        val largeSet = (1L..100L).toSet()
        val state = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = true,
            selectedItemIds = largeSet
        )

        assertEquals(100, state.selectedItemIds.size)
        assertTrue(state.selectedItemIds.contains(1L))
        assertTrue(state.selectedItemIds.contains(100L))
    }

    // ==================== State Transition Tests ====================

    @Test
    fun `CategorySectionState can transition from collapsed to expanded`() {
        var state = CategorySectionState(
            isExpanded = false,
            isMultiSelectMode = false,
            selectedItemIds = emptySet()
        )

        state = state.copy(isExpanded = true)

        assertTrue(state.isExpanded)
    }

    @Test
    fun `CategorySectionState can enter multi-select mode`() {
        var state = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = false,
            selectedItemIds = emptySet()
        )

        state = state.copy(isMultiSelectMode = true)

        assertTrue(state.isMultiSelectMode)
    }

    @Test
    fun `CategorySectionState can add items to selection`() {
        var state = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = true,
            selectedItemIds = setOf(1L)
        )

        state = state.copy(selectedItemIds = state.selectedItemIds + 2L)

        assertEquals(setOf(1L, 2L), state.selectedItemIds)
    }

    @Test
    fun `CategorySectionState can remove items from selection`() {
        var state = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = true,
            selectedItemIds = setOf(1L, 2L, 3L)
        )

        state = state.copy(selectedItemIds = state.selectedItemIds - 2L)

        assertEquals(setOf(1L, 3L), state.selectedItemIds)
    }

    @Test
    fun `CategorySectionState exiting multi-select clears selection`() {
        var state = CategorySectionState(
            isExpanded = true,
            isMultiSelectMode = true,
            selectedItemIds = setOf(1L, 2L, 3L)
        )

        state = state.copy(isMultiSelectMode = false, selectedItemIds = emptySet())

        assertFalse(state.isMultiSelectMode)
        assertTrue(state.selectedItemIds.isEmpty())
    }
}

class CategorySectionCallbacksTest {

    // ==================== Callback Creation Tests ====================

    @Test
    fun `CategorySectionCallbacks can be created with all callbacks`() {
        val callbacks = CategorySectionCallbacks(
            onToggleExpanded = {},
            onItemChecked = {},
            onItemDeleted = {},
            onItemPinned = {},
            onQuantityChanged = { _, _ -> },
            onItemLongPress = {},
            onItemSelected = {}
        )

        assertNotNull(callbacks)
    }

    // ==================== onToggleExpanded Tests ====================

    @Test
    fun `CategorySectionCallbacks onToggleExpanded can be invoked`() {
        var invoked = false

        val callbacks = CategorySectionCallbacks(
            onToggleExpanded = { invoked = true },
            onItemChecked = {},
            onItemDeleted = {},
            onItemPinned = {},
            onQuantityChanged = { _, _ -> },
            onItemLongPress = {},
            onItemSelected = {}
        )

        callbacks.onToggleExpanded()
        assertTrue(invoked)
    }

    @Test
    fun `CategorySectionCallbacks onToggleExpanded can be invoked multiple times`() {
        var count = 0

        val callbacks = CategorySectionCallbacks(
            onToggleExpanded = { count++ },
            onItemChecked = {},
            onItemDeleted = {},
            onItemPinned = {},
            onQuantityChanged = { _, _ -> },
            onItemLongPress = {},
            onItemSelected = {}
        )

        callbacks.onToggleExpanded()
        callbacks.onToggleExpanded()
        callbacks.onToggleExpanded()

        assertEquals(3, count)
    }

    // ==================== onItemChecked Tests ====================

    @Test
    fun `CategorySectionCallbacks onItemChecked receives correct item`() {
        var receivedItem: ChecklistItem? = null
        val testItem = ChecklistItem(
            id = 1L,
            content = "Test Item",
            isChecked = false,
            category = "OTHER"
        )

        val callbacks = CategorySectionCallbacks(
            onToggleExpanded = {},
            onItemChecked = { receivedItem = it },
            onItemDeleted = {},
            onItemPinned = {},
            onQuantityChanged = { _, _ -> },
            onItemLongPress = {},
            onItemSelected = {}
        )

        callbacks.onItemChecked(testItem)

        assertEquals(testItem, receivedItem)
        assertEquals(1L, receivedItem?.id)
        assertEquals("Test Item", receivedItem?.content)
    }

    // ==================== onItemDeleted Tests ====================

    @Test
    fun `CategorySectionCallbacks onItemDeleted receives correct item`() {
        var receivedItem: ChecklistItem? = null
        val testItem = ChecklistItem(
            id = 42L,
            content = "Delete Me",
            isChecked = true,
            category = "ELECTRONICS"
        )

        val callbacks = CategorySectionCallbacks(
            onToggleExpanded = {},
            onItemChecked = {},
            onItemDeleted = { receivedItem = it },
            onItemPinned = {},
            onQuantityChanged = { _, _ -> },
            onItemLongPress = {},
            onItemSelected = {}
        )

        callbacks.onItemDeleted(testItem)

        assertEquals(testItem, receivedItem)
        assertEquals(42L, receivedItem?.id)
    }

    // ==================== onItemPinned Tests ====================

    @Test
    fun `CategorySectionCallbacks onItemPinned receives correct item`() {
        var receivedItem: ChecklistItem? = null
        val testItem = ChecklistItem(
            id = 99L,
            content = "Pin Me",
            isChecked = false,
            isPinned = false,
            category = "DOCUMENTS"
        )

        val callbacks = CategorySectionCallbacks(
            onToggleExpanded = {},
            onItemChecked = {},
            onItemDeleted = {},
            onItemPinned = { receivedItem = it },
            onQuantityChanged = { _, _ -> },
            onItemLongPress = {},
            onItemSelected = {}
        )

        callbacks.onItemPinned(testItem)

        assertEquals(testItem, receivedItem)
        assertFalse(receivedItem!!.isPinned)
    }

    // ==================== onQuantityChanged Tests ====================

    @Test
    fun `CategorySectionCallbacks onQuantityChanged receives correct item and quantity`() {
        var receivedItem: ChecklistItem? = null
        var receivedQuantity = -1
        val testItem = ChecklistItem(
            id = 5L,
            content = "Socks",
            quantity = 2,
            category = "CLOTHING"
        )

        val callbacks = CategorySectionCallbacks(
            onToggleExpanded = {},
            onItemChecked = {},
            onItemDeleted = {},
            onItemPinned = {},
            onQuantityChanged = { item, qty ->
                receivedItem = item
                receivedQuantity = qty
            },
            onItemLongPress = {},
            onItemSelected = {}
        )

        callbacks.onQuantityChanged(testItem, 5)

        assertEquals(testItem, receivedItem)
        assertEquals(5, receivedQuantity)
    }

    @Test
    fun `CategorySectionCallbacks onQuantityChanged handles zero quantity`() {
        var receivedQuantity = -1

        val callbacks = CategorySectionCallbacks(
            onToggleExpanded = {},
            onItemChecked = {},
            onItemDeleted = {},
            onItemPinned = {},
            onQuantityChanged = { _, qty -> receivedQuantity = qty },
            onItemLongPress = {},
            onItemSelected = {}
        )

        callbacks.onQuantityChanged(
            ChecklistItem(id = 1L, content = "Test", category = "OTHER"),
            0
        )

        assertEquals(0, receivedQuantity)
    }

    @Test
    fun `CategorySectionCallbacks onQuantityChanged handles large quantity`() {
        var receivedQuantity = -1

        val callbacks = CategorySectionCallbacks(
            onToggleExpanded = {},
            onItemChecked = {},
            onItemDeleted = {},
            onItemPinned = {},
            onQuantityChanged = { _, qty -> receivedQuantity = qty },
            onItemLongPress = {},
            onItemSelected = {}
        )

        callbacks.onQuantityChanged(
            ChecklistItem(id = 1L, content = "Test", category = "OTHER"),
            999
        )

        assertEquals(999, receivedQuantity)
    }

    // ==================== onItemLongPress Tests ====================

    @Test
    fun `CategorySectionCallbacks onItemLongPress receives correct item id`() {
        var receivedId = -1L

        val callbacks = CategorySectionCallbacks(
            onToggleExpanded = {},
            onItemChecked = {},
            onItemDeleted = {},
            onItemPinned = {},
            onQuantityChanged = { _, _ -> },
            onItemLongPress = { receivedId = it },
            onItemSelected = {}
        )

        callbacks.onItemLongPress(123L)

        assertEquals(123L, receivedId)
    }

    // ==================== onItemSelected Tests ====================

    @Test
    fun `CategorySectionCallbacks onItemSelected receives correct item id`() {
        var receivedId = -1L

        val callbacks = CategorySectionCallbacks(
            onToggleExpanded = {},
            onItemChecked = {},
            onItemDeleted = {},
            onItemPinned = {},
            onQuantityChanged = { _, _ -> },
            onItemLongPress = {},
            onItemSelected = { receivedId = it }
        )

        callbacks.onItemSelected(456L)

        assertEquals(456L, receivedId)
    }

    // ==================== Callback Independence Tests ====================

    @Test
    fun `CategorySectionCallbacks callbacks are independent`() {
        var toggleCount = 0
        var checkedCount = 0
        var deletedCount = 0
        var pinnedCount = 0
        var quantitySum = 0
        var longPressCount = 0
        var selectedCount = 0

        val testItem = ChecklistItem(id = 1L, content = "Test", category = "OTHER")

        val callbacks = CategorySectionCallbacks(
            onToggleExpanded = { toggleCount++ },
            onItemChecked = { checkedCount++ },
            onItemDeleted = { deletedCount++ },
            onItemPinned = { pinnedCount++ },
            onQuantityChanged = { _, qty -> quantitySum += qty },
            onItemLongPress = { longPressCount++ },
            onItemSelected = { selectedCount++ }
        )

        callbacks.onToggleExpanded()
        callbacks.onToggleExpanded()
        callbacks.onItemChecked(testItem)
        callbacks.onItemDeleted(testItem)
        callbacks.onItemPinned(testItem)
        callbacks.onItemPinned(testItem)
        callbacks.onItemPinned(testItem)
        callbacks.onQuantityChanged(testItem, 3)
        callbacks.onQuantityChanged(testItem, 7)
        callbacks.onItemLongPress(1L)
        callbacks.onItemSelected(1L)
        callbacks.onItemSelected(2L)

        assertEquals(2, toggleCount)
        assertEquals(1, checkedCount)
        assertEquals(1, deletedCount)
        assertEquals(3, pinnedCount)
        assertEquals(10, quantitySum)
        assertEquals(1, longPressCount)
        assertEquals(2, selectedCount)
    }

    // ==================== Equality Tests ====================

    @Test
    fun `CategorySectionCallbacks with same lambdas are equal`() {
        val toggle: () -> Unit = {}
        val check: (ChecklistItem) -> Unit = {}
        val delete: (ChecklistItem) -> Unit = {}
        val pin: (ChecklistItem) -> Unit = {}
        val quantity: (ChecklistItem, Int) -> Unit = { _, _ -> }
        val longPress: (Long) -> Unit = {}
        val select: (Long) -> Unit = {}

        val callbacks1 = CategorySectionCallbacks(
            onToggleExpanded = toggle,
            onItemChecked = check,
            onItemDeleted = delete,
            onItemPinned = pin,
            onQuantityChanged = quantity,
            onItemLongPress = longPress,
            onItemSelected = select
        )

        val callbacks2 = CategorySectionCallbacks(
            onToggleExpanded = toggle,
            onItemChecked = check,
            onItemDeleted = delete,
            onItemPinned = pin,
            onQuantityChanged = quantity,
            onItemLongPress = longPress,
            onItemSelected = select
        )

        assertEquals(callbacks1, callbacks2)
    }

    @Test
    fun `CategorySectionCallbacks with different lambdas are not equal`() {
        val callbacks1 = CategorySectionCallbacks(
            onToggleExpanded = {},
            onItemChecked = {},
            onItemDeleted = {},
            onItemPinned = {},
            onQuantityChanged = { _, _ -> },
            onItemLongPress = {},
            onItemSelected = {}
        )

        val callbacks2 = CategorySectionCallbacks(
            onToggleExpanded = {},
            onItemChecked = {},
            onItemDeleted = {},
            onItemPinned = {},
            onQuantityChanged = { _, _ -> },
            onItemLongPress = {},
            onItemSelected = {}
        )

        // Different lambda instances are not equal
        assertNotEquals(callbacks1, callbacks2)
    }

    // ==================== Copy Tests ====================

    @Test
    fun `CategorySectionCallbacks copy works correctly`() {
        var originalToggleCalled = false
        var newToggleCalled = false

        val original = CategorySectionCallbacks(
            onToggleExpanded = { originalToggleCalled = true },
            onItemChecked = {},
            onItemDeleted = {},
            onItemPinned = {},
            onQuantityChanged = { _, _ -> },
            onItemLongPress = {},
            onItemSelected = {}
        )

        val copy = original.copy(onToggleExpanded = { newToggleCalled = true })

        original.onToggleExpanded()
        copy.onToggleExpanded()

        assertTrue(originalToggleCalled)
        assertTrue(newToggleCalled)
    }

    // ==================== toString Tests ====================

    @Test
    fun `CategorySectionCallbacks toString contains class name`() {
        val callbacks = CategorySectionCallbacks(
            onToggleExpanded = {},
            onItemChecked = {},
            onItemDeleted = {},
            onItemPinned = {},
            onQuantityChanged = { _, _ -> },
            onItemLongPress = {},
            onItemSelected = {}
        )

        val string = callbacks.toString()
        assertTrue(string.contains("CategorySectionCallbacks"))
    }
}
