package com.unstampedpages.app.ui.screens.triplog

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.local.entity.TripLogEntry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TripLogViewModelTest {

    private lateinit var viewModel: TripLogViewModel
    private lateinit var application: Application

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        viewModel = TripLogViewModel(application)
    }

    @Test
    fun initialState_isNotEditing() {
        val state = viewModel.uiState.value

        assertFalse(state.isEditing)
    }

    @Test
    fun initialState_hasNoSelectedEntry() {
        val state = viewModel.uiState.value

        assertNull(state.selectedEntry)
    }

    @Test
    fun initialState_hasEmptyEditingFields() {
        val state = viewModel.uiState.value

        assertEquals("", state.editingTitle)
        assertEquals("", state.editingContent)
        assertEquals("", state.editingLocation)
    }

    @Test
    fun startNewEntry_setsEditingToTrue() {
        viewModel.startNewEntry()

        assertTrue(viewModel.uiState.value.isEditing)
    }

    @Test
    fun startNewEntry_clearsSelectedEntry() {
        viewModel.startNewEntry()

        assertNull(viewModel.uiState.value.selectedEntry)
    }

    @Test
    fun startNewEntry_clearsEditingFields() {
        viewModel.updateEditingTitle("Some title")
        viewModel.updateEditingContent("Some content")

        viewModel.startNewEntry()

        val state = viewModel.uiState.value
        assertEquals("", state.editingTitle)
        assertEquals("", state.editingContent)
        assertEquals("", state.editingLocation)
    }

    @Test
    fun updateEditingTitle_updatesState() {
        viewModel.updateEditingTitle("My Trip")

        assertEquals("My Trip", viewModel.uiState.value.editingTitle)
    }

    @Test
    fun updateEditingContent_updatesState() {
        viewModel.updateEditingContent("Great adventure!")

        assertEquals("Great adventure!", viewModel.uiState.value.editingContent)
    }

    @Test
    fun updateEditingLocation_updatesState() {
        viewModel.updateEditingLocation("Paris, France")

        assertEquals("Paris, France", viewModel.uiState.value.editingLocation)
    }

    @Test
    fun updateEditingDate_updatesState() {
        val date = 1234567890L

        viewModel.updateEditingDate(date)

        assertEquals(date, viewModel.uiState.value.editingDate)
    }

    @Test
    fun cancelEditing_setsEditingToFalse() {
        viewModel.startNewEntry()
        assertTrue(viewModel.uiState.value.isEditing)

        viewModel.cancelEditing()

        assertFalse(viewModel.uiState.value.isEditing)
    }

    @Test
    fun cancelEditing_clearsSelectedEntry() {
        viewModel.startNewEntry()

        viewModel.cancelEditing()

        assertNull(viewModel.uiState.value.selectedEntry)
    }

    @Test
    fun cancelEditing_clearsEditingFields() {
        viewModel.startNewEntry()
        viewModel.updateEditingTitle("Title")
        viewModel.updateEditingContent("Content")
        viewModel.updateEditingLocation("Location")

        viewModel.cancelEditing()

        val state = viewModel.uiState.value
        assertEquals("", state.editingTitle)
        assertEquals("", state.editingContent)
        assertEquals("", state.editingLocation)
    }

    @Test
    fun clearSelection_removesSelectedEntry() {
        viewModel.startNewEntry()
        viewModel.cancelEditing()

        viewModel.clearSelection()

        assertNull(viewModel.uiState.value.selectedEntry)
    }

    @Test
    fun saveEntry_withEmptyTitleAndContent_cancelsEditing() {
        viewModel.startNewEntry()
        viewModel.updateEditingTitle("")
        viewModel.updateEditingContent("")

        viewModel.saveEntry()

        // saveEntry calls cancelEditing synchronously when both are blank
        assertFalse(viewModel.uiState.value.isEditing)
    }

    @Test
    fun saveEntry_withBlankTitleAndContent_cancelsEditing() {
        viewModel.startNewEntry()
        viewModel.updateEditingTitle("   ")
        viewModel.updateEditingContent("   ")

        viewModel.saveEntry()

        // saveEntry calls cancelEditing synchronously when both are blank
        assertFalse(viewModel.uiState.value.isEditing)
    }

    @Test
    fun updateEditingTitle_handlesSpecialCharacters() {
        viewModel.updateEditingTitle("Trip with émojis and @#$%")

        assertEquals("Trip with émojis and @#$%", viewModel.uiState.value.editingTitle)
    }

    @Test
    fun updateEditingContent_handlesLongText() {
        val longText = "A".repeat(5000)
        viewModel.updateEditingContent(longText)

        assertEquals(longText, viewModel.uiState.value.editingContent)
    }

    @Test
    fun updateEditingLocation_preservesWhitespace() {
        viewModel.updateEditingLocation("  Paris, France  ")

        assertEquals("  Paris, France  ", viewModel.uiState.value.editingLocation)
    }
}

class TripLogUiStateTest {

    @Test
    fun defaultState_hasEmptyEntries() {
        val state = TripLogUiState()

        assertTrue(state.entries.isEmpty())
    }

    @Test
    fun defaultState_hasNoSelectedEntry() {
        val state = TripLogUiState()

        assertNull(state.selectedEntry)
    }

    @Test
    fun defaultState_isNotEditing() {
        val state = TripLogUiState()

        assertFalse(state.isEditing)
    }

    @Test
    fun defaultState_isNotLoading() {
        val state = TripLogUiState()

        assertFalse(state.isLoading)
    }

    @Test
    fun defaultState_hasEmptyEditingTitle() {
        val state = TripLogUiState()

        assertEquals("", state.editingTitle)
    }

    @Test
    fun defaultState_hasEmptyEditingContent() {
        val state = TripLogUiState()

        assertEquals("", state.editingContent)
    }

    @Test
    fun defaultState_hasEmptyEditingLocation() {
        val state = TripLogUiState()

        assertEquals("", state.editingLocation)
    }

    @Test
    fun state_canBeCreatedWithIsLoading() {
        val state = TripLogUiState(isLoading = true)

        assertTrue(state.isLoading)
    }

    @Test
    fun state_canBeCreatedWithIsEditing() {
        val state = TripLogUiState(isEditing = true)

        assertTrue(state.isEditing)
    }

    @Test
    fun state_canBeCreatedWithEditingFields() {
        val state = TripLogUiState(
            editingTitle = "Test Title",
            editingContent = "Test Content",
            editingLocation = "Test Location"
        )

        assertEquals("Test Title", state.editingTitle)
        assertEquals("Test Content", state.editingContent)
        assertEquals("Test Location", state.editingLocation)
    }

    @Test
    fun state_canBeCreatedWithEntries() {
        val entries = listOf(
            TripLogEntry(id = 1, title = "Trip 1", content = "Content 1"),
            TripLogEntry(id = 2, title = "Trip 2", content = "Content 2")
        )
        val state = TripLogUiState(entries = entries)

        assertEquals(2, state.entries.size)
    }

    @Test
    fun state_canBeCreatedWithSelectedEntry() {
        val entry = TripLogEntry(id = 1, title = "Selected", content = "Entry")
        val state = TripLogUiState(selectedEntry = entry)

        assertNotNull(state.selectedEntry)
        assertEquals("Selected", state.selectedEntry?.title)
    }

    @Test
    fun state_copy_modifiesIsEditing() {
        val original = TripLogUiState(isEditing = false)

        val modified = original.copy(isEditing = true)

        assertTrue(modified.isEditing)
        assertFalse(original.isEditing)
    }

    @Test
    fun state_copy_modifiesEditingTitle() {
        val original = TripLogUiState(editingTitle = "Original")

        val modified = original.copy(editingTitle = "Modified")

        assertEquals("Modified", modified.editingTitle)
        assertEquals("Original", original.editingTitle)
    }
}
