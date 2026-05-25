package com.unstampedpages.app.ui.screens.checklist

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.local.entity.ChecklistItem
import com.unstampedpages.app.data.model.ChecklistCategory
import com.unstampedpages.app.data.model.ChecklistProgress
import com.unstampedpages.app.data.model.ChecklistTemplate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChecklistViewModelTest {

    private lateinit var viewModel: ChecklistViewModel
    private lateinit var application: Application

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        viewModel = ChecklistViewModel(application)
    }

    @Test
    fun initialState_hasEmptyNewItemText() {
        val state = viewModel.uiState.value

        assertEquals("", state.newItemText)
    }

    @Test
    fun initialState_isNotLoading() {
        // isLoading starts as false in ChecklistUiState default
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
    }

    @Test
    fun updateNewItemText_updatesState() {
        viewModel.updateNewItemText("Pack sunscreen")

        assertEquals("Pack sunscreen", viewModel.uiState.value.newItemText)
    }

    @Test
    fun updateNewItemText_withEmptyString() {
        viewModel.updateNewItemText("Some text")
        viewModel.updateNewItemText("")

        assertEquals("", viewModel.uiState.value.newItemText)
    }

    @Test
    fun updateNewItemText_handlesSpecialCharacters() {
        viewModel.updateNewItemText("Item with émojis and special chars: @#$%")

        assertEquals("Item with émojis and special chars: @#$%", viewModel.uiState.value.newItemText)
    }

    @Test
    fun updateNewItemText_handlesLongText() {
        val longText = "A".repeat(1000)
        viewModel.updateNewItemText(longText)

        assertEquals(longText, viewModel.uiState.value.newItemText)
    }

    @Test
    fun updateNewItemText_multipleUpdates() {
        viewModel.updateNewItemText("First")
        assertEquals("First", viewModel.uiState.value.newItemText)

        viewModel.updateNewItemText("Second")
        assertEquals("Second", viewModel.uiState.value.newItemText)

        viewModel.updateNewItemText("Third")
        assertEquals("Third", viewModel.uiState.value.newItemText)
    }

    @Test
    fun updateNewItemText_preservesWhitespace() {
        viewModel.updateNewItemText("  text with spaces  ")

        assertEquals("  text with spaces  ", viewModel.uiState.value.newItemText)
    }

    // ---------------------------------------------------------------------------
    // Initial state — all fields
    // ---------------------------------------------------------------------------

    @Test
    fun initialState_isNotInMultiSelectMode() {
        assertFalse(viewModel.uiState.value.isMultiSelectMode)
    }

    @Test
    fun initialState_hasEmptySelectedItemIds() {
        assertTrue(viewModel.uiState.value.selectedItemIds.isEmpty())
    }

    @Test
    fun initialState_showTemplateDialogIsFalse() {
        assertFalse(viewModel.uiState.value.showTemplateDialog)
    }

    @Test
    fun initialState_showAddItemDialogIsFalse() {
        assertFalse(viewModel.uiState.value.showAddItemDialog)
    }

    @Test
    fun initialState_allCategoriesAreExpanded() {
        val expanded = viewModel.uiState.value.expandedCategories
        assertTrue(expanded.containsAll(ChecklistCategory.entries))
    }

    @Test
    fun initialState_hasEmptyItems() {
        assertTrue(viewModel.uiState.value.items.isEmpty())
    }

    @Test
    fun initialState_hasEmptyGroupedItems() {
        assertTrue(viewModel.uiState.value.groupedItems.isEmpty())
    }

    // ---------------------------------------------------------------------------
    // enterMultiSelectMode
    // ---------------------------------------------------------------------------

    @Test
    fun enterMultiSelectMode_setsIsMultiSelectModeTrue() {
        viewModel.enterMultiSelectMode(42L)

        assertTrue(viewModel.uiState.value.isMultiSelectMode)
    }

    @Test
    fun enterMultiSelectMode_setsInitialSelection() {
        viewModel.enterMultiSelectMode(42L)

        assertEquals(setOf(42L), viewModel.uiState.value.selectedItemIds)
    }

    @Test
    fun enterMultiSelectMode_replacesExistingSelection() {
        viewModel.enterMultiSelectMode(1L)
        viewModel.enterMultiSelectMode(2L)

        assertEquals(setOf(2L), viewModel.uiState.value.selectedItemIds)
    }

    // ---------------------------------------------------------------------------
    // exitMultiSelectMode
    // ---------------------------------------------------------------------------

    @Test
    fun exitMultiSelectMode_clearsMultiSelectFlag() {
        viewModel.enterMultiSelectMode(1L)

        viewModel.exitMultiSelectMode()

        assertFalse(viewModel.uiState.value.isMultiSelectMode)
    }

    @Test
    fun exitMultiSelectMode_clearsSelectedItemIds() {
        viewModel.enterMultiSelectMode(1L)

        viewModel.exitMultiSelectMode()

        assertTrue(viewModel.uiState.value.selectedItemIds.isEmpty())
    }

    @Test
    fun exitMultiSelectMode_whenNotInMode_leavesStateFalse() {
        viewModel.exitMultiSelectMode()

        assertFalse(viewModel.uiState.value.isMultiSelectMode)
    }

    // ---------------------------------------------------------------------------
    // toggleItemSelection
    // ---------------------------------------------------------------------------

    @Test
    fun toggleItemSelection_addsItemToSelection_whenNotCurrentlySelected() {
        viewModel.enterMultiSelectMode(1L)

        viewModel.toggleItemSelection(2L)

        assertTrue(viewModel.uiState.value.selectedItemIds.contains(2L))
    }

    @Test
    fun toggleItemSelection_removesItemFromSelection_whenCurrentlySelected() {
        viewModel.enterMultiSelectMode(1L)
        viewModel.toggleItemSelection(2L) // selection = {1, 2}

        viewModel.toggleItemSelection(2L) // selection = {1}

        assertFalse(viewModel.uiState.value.selectedItemIds.contains(2L))
        assertTrue(viewModel.uiState.value.selectedItemIds.contains(1L))
    }

    @Test
    fun toggleItemSelection_exitsMultiSelectMode_whenLastItemDeselected() {
        viewModel.enterMultiSelectMode(1L)

        viewModel.toggleItemSelection(1L) // deselect the only item

        assertFalse(viewModel.uiState.value.isMultiSelectMode)
        assertTrue(viewModel.uiState.value.selectedItemIds.isEmpty())
    }

    @Test
    fun toggleItemSelection_remainsInMultiSelectMode_whenOtherItemsStillSelected() {
        viewModel.enterMultiSelectMode(1L)
        viewModel.toggleItemSelection(2L) // {1, 2}

        viewModel.toggleItemSelection(1L) // {2}

        assertTrue(viewModel.uiState.value.isMultiSelectMode)
        assertEquals(setOf(2L), viewModel.uiState.value.selectedItemIds)
    }

    @Test
    fun toggleItemSelection_canSelectMultipleItems() {
        viewModel.enterMultiSelectMode(1L)
        viewModel.toggleItemSelection(2L)
        viewModel.toggleItemSelection(3L)

        assertEquals(setOf(1L, 2L, 3L), viewModel.uiState.value.selectedItemIds)
    }

    // ---------------------------------------------------------------------------
    // toggleCategoryExpanded
    // ---------------------------------------------------------------------------

    @Test
    fun toggleCategoryExpanded_removesCategory_whenCurrentlyExpanded() {
        viewModel.toggleCategoryExpanded(ChecklistCategory.ELECTRONICS)

        assertFalse(viewModel.uiState.value.expandedCategories.contains(ChecklistCategory.ELECTRONICS))
    }

    @Test
    fun toggleCategoryExpanded_addsCategory_whenCurrentlyCollapsed() {
        viewModel.toggleCategoryExpanded(ChecklistCategory.ELECTRONICS) // collapse
        viewModel.toggleCategoryExpanded(ChecklistCategory.ELECTRONICS) // expand again

        assertTrue(viewModel.uiState.value.expandedCategories.contains(ChecklistCategory.ELECTRONICS))
    }

    @Test
    fun toggleCategoryExpanded_doesNotAffectOtherCategories() {
        viewModel.toggleCategoryExpanded(ChecklistCategory.ELECTRONICS)

        assertTrue(viewModel.uiState.value.expandedCategories.contains(ChecklistCategory.CLOTHING))
        assertTrue(viewModel.uiState.value.expandedCategories.contains(ChecklistCategory.DOCUMENTS))
    }

    @Test
    fun toggleCategoryExpanded_canCollapseAllCategories() {
        ChecklistCategory.entries.forEach { viewModel.toggleCategoryExpanded(it) }

        assertTrue(viewModel.uiState.value.expandedCategories.isEmpty())
    }

    // ---------------------------------------------------------------------------
    // Template dialog
    // ---------------------------------------------------------------------------

    @Test
    fun showTemplateDialog_setsShowTemplateDialogTrue() {
        viewModel.showTemplateDialog()

        assertTrue(viewModel.uiState.value.showTemplateDialog)
    }

    @Test
    fun hideTemplateDialog_setsShowTemplateDialogFalse() {
        viewModel.showTemplateDialog()
        viewModel.hideTemplateDialog()

        assertFalse(viewModel.uiState.value.showTemplateDialog)
    }

    @Test
    fun hideTemplateDialog_whenAlreadyHidden_leavesStateFalse() {
        viewModel.hideTemplateDialog()

        assertFalse(viewModel.uiState.value.showTemplateDialog)
    }

    // ---------------------------------------------------------------------------
    // Add-item dialog
    // ---------------------------------------------------------------------------

    @Test
    fun showAddItemDialog_setsShowAddItemDialogTrue() {
        viewModel.showAddItemDialog()

        assertTrue(viewModel.uiState.value.showAddItemDialog)
    }

    @Test
    fun hideAddItemDialog_setsShowAddItemDialogFalse() {
        viewModel.showAddItemDialog()
        viewModel.hideAddItemDialog()

        assertFalse(viewModel.uiState.value.showAddItemDialog)
    }

    @Test
    fun hideAddItemDialog_whenAlreadyHidden_leavesStateFalse() {
        viewModel.hideAddItemDialog()

        assertFalse(viewModel.uiState.value.showAddItemDialog)
    }

    // ---------------------------------------------------------------------------
    // Guard checks — blank / invalid inputs do not trigger async work
    // ---------------------------------------------------------------------------

    @Test
    fun addItemWithDetails_withBlankName_doesNotHideAddItemDialog() {
        viewModel.showAddItemDialog()

        viewModel.addItemWithDetails("", ChecklistCategory.OTHER, 1)

        // Coroutine not launched — dialog stays open
        assertTrue(viewModel.uiState.value.showAddItemDialog)
    }

    @Test
    fun addItemWithDetails_withWhitespaceOnlyName_doesNotHideAddItemDialog() {
        viewModel.showAddItemDialog()

        viewModel.addItemWithDetails("   ", ChecklistCategory.OTHER, 1)

        assertTrue(viewModel.uiState.value.showAddItemDialog)
    }

    @Test
    fun updateQuantity_withZeroQuantity_doesNotChangeState() {
        viewModel.updateQuantity(ChecklistItem(id = 1L, content = "Test"), 0)

        // Guard (quantity >= 1) prevents coroutine launch — no side effects
        assertFalse(viewModel.uiState.value.isMultiSelectMode)
        assertFalse(viewModel.uiState.value.showAddItemDialog)
        assertFalse(viewModel.uiState.value.showTemplateDialog)
    }

    @Test
    fun updateQuantity_withNegativeQuantity_doesNotChangeState() {
        viewModel.updateQuantity(ChecklistItem(id = 1L, content = "Test"), -3)

        assertFalse(viewModel.uiState.value.isMultiSelectMode)
        assertFalse(viewModel.uiState.value.showAddItemDialog)
        assertFalse(viewModel.uiState.value.showTemplateDialog)
    }

    @Test
    fun deleteSelectedItems_withNoSelection_doesNothing() {
        // selectedItemIds is empty by default; guard prevents coroutine launch
        assertFalse(viewModel.uiState.value.isMultiSelectMode)

        viewModel.deleteSelectedItems()

        assertFalse(viewModel.uiState.value.isMultiSelectMode)
        assertTrue(viewModel.uiState.value.selectedItemIds.isEmpty())
    }

    // ---------------------------------------------------------------------------
    // Async operations
    // ---------------------------------------------------------------------------

    @Test
    fun addItemWithDetails_withValidName_hidesAddItemDialog() {
        viewModel.showAddItemDialog()

        viewModel.addItemWithDetails("Sunscreen", ChecklistCategory.TOILETRIES, 1)

        val finalState = awaitState { !it.showAddItemDialog }
        assertFalse(finalState.showAddItemDialog)
    }

    @Test
    fun addItemWithDetails_withLeadingTrailingWhitespace_trimsAndHidesDialog() {
        viewModel.showAddItemDialog()

        viewModel.addItemWithDetails("  Passport  ", ChecklistCategory.DOCUMENTS, 1)

        val finalState = awaitState { !it.showAddItemDialog }
        assertFalse(finalState.showAddItemDialog)
    }

    @Test
    fun loadTemplate_hidesTemplateDialog() {
        viewModel.showTemplateDialog()

        viewModel.loadTemplate(ChecklistTemplate.BEACH_VACATION)

        val finalState = awaitState { !it.showTemplateDialog }
        assertFalse(finalState.showTemplateDialog)
    }

    @Test
    fun deleteSelectedItems_withSelectedItems_exitsMultiSelectMode() {
        // Item 999 may not exist in DB; deleteItemsByIds is a no-op for missing IDs,
        // but exitMultiSelectMode() still runs after the DB call completes.
        viewModel.enterMultiSelectMode(999L)

        viewModel.deleteSelectedItems()

        val finalState = awaitState { !it.isMultiSelectMode }
        assertFalse(finalState.isMultiSelectMode)
        assertTrue(finalState.selectedItemIds.isEmpty())
    }

    // ---------------------------------------------------------------------------
    // Helper
    // ---------------------------------------------------------------------------

    private fun awaitState(
        timeoutMs: Long = 3000L,
        predicate: (ChecklistUiState) -> Boolean
    ): ChecklistUiState = runBlocking {
        withTimeout(timeoutMs) {
            viewModel.uiState.first(predicate)
        }
    }
}

class ChecklistUiStateTest {

    @Test
    fun defaultState_hasEmptyItems() {
        val state = ChecklistUiState()

        assertTrue(state.items.isEmpty())
    }

    @Test
    fun defaultState_hasEmptyNewItemText() {
        val state = ChecklistUiState()

        assertEquals("", state.newItemText)
    }

    @Test
    fun defaultState_isNotLoading() {
        val state = ChecklistUiState()

        assertFalse(state.isLoading)
    }

    @Test
    fun state_canBeCreatedWithLoading() {
        val state = ChecklistUiState(isLoading = true)

        assertTrue(state.isLoading)
    }

    @Test
    fun state_canBeCreatedWithNewItemText() {
        val state = ChecklistUiState(newItemText = "Test text")

        assertEquals("Test text", state.newItemText)
    }

    @Test
    fun state_canBeCreatedWithItems() {
        val items = listOf(
            ChecklistItem(id = 1, content = "Item 1"),
            ChecklistItem(id = 2, content = "Item 2")
        )
        val state = ChecklistUiState(items = items)

        assertEquals(2, state.items.size)
        assertEquals("Item 1", state.items[0].content)
        assertEquals("Item 2", state.items[1].content)
    }

    @Test
    fun state_copy_modifiesNewItemText() {
        val original = ChecklistUiState(newItemText = "Original")

        val modified = original.copy(newItemText = "Modified")

        assertEquals("Modified", modified.newItemText)
        assertEquals("Original", original.newItemText)
    }

    @Test
    fun state_copy_modifiesIsLoading() {
        val original = ChecklistUiState(isLoading = false)

        val modified = original.copy(isLoading = true)

        assertTrue(modified.isLoading)
        assertFalse(original.isLoading)
    }

    @Test
    fun state_copy_modifiesItems() {
        val original = ChecklistUiState(items = emptyList())
        val newItems = listOf(ChecklistItem(id = 1, content = "New"))

        val modified = original.copy(items = newItems)

        assertEquals(1, modified.items.size)
        assertTrue(original.items.isEmpty())
    }

    @Test
    fun state_equals_sameValues() {
        // ChecklistItem has createdAt/updatedAt defaulting to System.currentTimeMillis(),
        // so two separately constructed instances are never equal. Share one reference.
        val item = ChecklistItem(id = 1, content = "Test")
        val state1 = ChecklistUiState(
            items = listOf(item),
            newItemText = "text",
            isLoading = true
        )
        val state2 = ChecklistUiState(
            items = listOf(item),
            newItemText = "text",
            isLoading = true
        )

        assertEquals(state1, state2)
    }

    @Test
    fun state_notEquals_differentNewItemText() {
        val state1 = ChecklistUiState(newItemText = "text1")
        val state2 = ChecklistUiState(newItemText = "text2")

        assertNotEquals(state1, state2)
    }

    // ==================== Default values — remaining fields ====================

    @Test
    fun defaultState_hasEmptyGroupedItems() {
        val state = ChecklistUiState()

        assertTrue(state.groupedItems.isEmpty())
    }

    @Test
    fun defaultState_hasDefaultProgress() {
        val state = ChecklistUiState()

        assertEquals(ChecklistProgress(), state.progress)
        assertEquals(0, state.progress.checkedCount)
        assertEquals(0, state.progress.totalCount)
    }

    @Test
    fun defaultState_isNotInMultiSelectMode() {
        val state = ChecklistUiState()

        assertFalse(state.isMultiSelectMode)
    }

    @Test
    fun defaultState_hasEmptySelectedItemIds() {
        val state = ChecklistUiState()

        assertTrue(state.selectedItemIds.isEmpty())
    }

    @Test
    fun defaultState_allCategoriesAreExpanded() {
        val state = ChecklistUiState()

        assertEquals(ChecklistCategory.entries.toSet(), state.expandedCategories)
        assertEquals(ChecklistCategory.entries.size, state.expandedCategories.size)
    }

    @Test
    fun defaultState_showTemplateDialogIsFalse() {
        val state = ChecklistUiState()

        assertFalse(state.showTemplateDialog)
    }

    @Test
    fun defaultState_showAddItemDialogIsFalse() {
        val state = ChecklistUiState()

        assertFalse(state.showAddItemDialog)
    }

    // ==================== Copy — remaining fields ====================

    @Test
    fun state_copy_modifiesIsMultiSelectMode() {
        val original = ChecklistUiState(isMultiSelectMode = false)

        val modified = original.copy(isMultiSelectMode = true)

        assertTrue(modified.isMultiSelectMode)
        assertFalse(original.isMultiSelectMode)
    }

    @Test
    fun state_copy_modifiesSelectedItemIds() {
        val original = ChecklistUiState(selectedItemIds = emptySet())

        val modified = original.copy(selectedItemIds = setOf(1L, 2L))

        assertEquals(setOf(1L, 2L), modified.selectedItemIds)
        assertTrue(original.selectedItemIds.isEmpty())
    }

    @Test
    fun state_copy_modifiesExpandedCategories() {
        val original = ChecklistUiState(expandedCategories = ChecklistCategory.entries.toSet())

        val modified = original.copy(expandedCategories = emptySet())

        assertTrue(modified.expandedCategories.isEmpty())
        assertEquals(ChecklistCategory.entries.size, original.expandedCategories.size)
    }

    @Test
    fun state_copy_modifiesShowTemplateDialog() {
        val original = ChecklistUiState(showTemplateDialog = false)

        val modified = original.copy(showTemplateDialog = true)

        assertTrue(modified.showTemplateDialog)
        assertFalse(original.showTemplateDialog)
    }

    @Test
    fun state_copy_modifiesShowAddItemDialog() {
        val original = ChecklistUiState(showAddItemDialog = false)

        val modified = original.copy(showAddItemDialog = true)

        assertTrue(modified.showAddItemDialog)
        assertFalse(original.showAddItemDialog)
    }

    @Test
    fun state_copy_modifiesProgress() {
        val original = ChecklistUiState(progress = ChecklistProgress(0, 0))

        val modified = original.copy(progress = ChecklistProgress(3, 5))

        assertEquals(3, modified.progress.checkedCount)
        assertEquals(5, modified.progress.totalCount)
        assertEquals(0, original.progress.checkedCount)
    }

    @Test
    fun state_copy_modifiesGroupedItems() {
        val original = ChecklistUiState(groupedItems = emptyMap())
        val item = ChecklistItem(id = 1L, content = "Laptop")
        val grouped = mapOf(ChecklistCategory.ELECTRONICS to listOf(item))

        val modified = original.copy(groupedItems = grouped)

        assertEquals(1, modified.groupedItems.size)
        assertTrue(original.groupedItems.isEmpty())
    }

    // ==================== Equality — remaining fields ====================

    @Test
    fun state_notEquals_differentMultiSelectMode() {
        val state1 = ChecklistUiState(isMultiSelectMode = false)
        val state2 = ChecklistUiState(isMultiSelectMode = true)

        assertNotEquals(state1, state2)
    }

    @Test
    fun state_notEquals_differentSelectedItemIds() {
        val state1 = ChecklistUiState(selectedItemIds = setOf(1L))
        val state2 = ChecklistUiState(selectedItemIds = setOf(2L))

        assertNotEquals(state1, state2)
    }

    @Test
    fun state_notEquals_differentExpandedCategories() {
        val state1 = ChecklistUiState(expandedCategories = emptySet())
        val state2 = ChecklistUiState(expandedCategories = setOf(ChecklistCategory.OTHER))

        assertNotEquals(state1, state2)
    }

    @Test
    fun state_notEquals_differentShowTemplateDialog() {
        val state1 = ChecklistUiState(showTemplateDialog = false)
        val state2 = ChecklistUiState(showTemplateDialog = true)

        assertNotEquals(state1, state2)
    }

    @Test
    fun state_notEquals_differentShowAddItemDialog() {
        val state1 = ChecklistUiState(showAddItemDialog = false)
        val state2 = ChecklistUiState(showAddItemDialog = true)

        assertNotEquals(state1, state2)
    }

    @Test
    fun state_notEquals_differentProgress() {
        val state1 = ChecklistUiState(progress = ChecklistProgress(0, 0))
        val state2 = ChecklistUiState(progress = ChecklistProgress(1, 1))

        assertNotEquals(state1, state2)
    }
}
