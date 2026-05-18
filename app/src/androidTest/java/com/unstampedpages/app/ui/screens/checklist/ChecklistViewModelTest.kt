package com.unstampedpages.app.ui.screens.checklist

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.local.entity.ChecklistItem
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
}
