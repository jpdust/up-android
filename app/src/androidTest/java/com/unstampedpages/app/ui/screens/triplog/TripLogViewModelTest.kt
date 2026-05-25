package com.unstampedpages.app.ui.screens.triplog

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.local.AppDatabase
import com.unstampedpages.app.data.local.entity.TripLogEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
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
        runBlocking { AppDatabase.getDatabase(application).tripLogDao().deleteAllEntries() }
        viewModel = TripLogViewModel(application)
    }

    @After
    fun tearDown() {
        runBlocking { AppDatabase.getDatabase(application).tripLogDao().deleteAllEntries() }
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

    // ---------------------------------------------------------------------------
    // editEntry — synchronous state mutations
    // ---------------------------------------------------------------------------

    @Test
    fun editEntry_setsIsEditingTrue() {
        val entry = TripLogEntry(id = 1L, title = "My Trip", content = "Content")

        viewModel.editEntry(entry)

        assertTrue(viewModel.uiState.value.isEditing)
    }

    @Test
    fun editEntry_setsSelectedEntry() {
        val entry = TripLogEntry(id = 1L, title = "My Trip", content = "Content")

        viewModel.editEntry(entry)

        assertEquals(entry, viewModel.uiState.value.selectedEntry)
    }

    @Test
    fun editEntry_copiesEntryTitle() {
        val entry = TripLogEntry(id = 1L, title = "Roman Holiday", content = "Content")

        viewModel.editEntry(entry)

        assertEquals("Roman Holiday", viewModel.uiState.value.editingTitle)
    }

    @Test
    fun editEntry_copiesEntryContent() {
        val entry = TripLogEntry(id = 1L, title = "Trip", content = "Great journey!")

        viewModel.editEntry(entry)

        assertEquals("Great journey!", viewModel.uiState.value.editingContent)
    }

    @Test
    fun editEntry_copiesEntryLocation_whenPresent() {
        val entry = TripLogEntry(id = 1L, title = "Trip", content = "Content", location = "Rome")

        viewModel.editEntry(entry)

        assertEquals("Rome", viewModel.uiState.value.editingLocation)
    }

    @Test
    fun editEntry_setsEmptyLocation_whenNull() {
        val entry = TripLogEntry(id = 1L, title = "Trip", content = "Content", location = null)

        viewModel.editEntry(entry)

        assertEquals("", viewModel.uiState.value.editingLocation)
    }

    @Test
    fun editEntry_copiesEntryDate() {
        val date = 1_700_000_000_000L
        val entry = TripLogEntry(id = 1L, title = "Trip", content = "Content", date = date)

        viewModel.editEntry(entry)

        assertEquals(date, viewModel.uiState.value.editingDate)
    }

    // ---------------------------------------------------------------------------
    // saveEntry — async paths
    // ---------------------------------------------------------------------------

    @Test
    fun saveEntry_withValidTitleAndContent_cancelsEditing() {
        viewModel.startNewEntry()
        viewModel.updateEditingTitle("My Journey")
        viewModel.updateEditingContent("A wonderful trip")

        viewModel.saveEntry()

        val finalState = awaitState { !it.isEditing }
        assertFalse(finalState.isEditing)
        assertNull(finalState.selectedEntry)
    }

    @Test
    fun saveEntry_withBlankTitle_andValidContent_usesUntitledEntryTitle() {
        viewModel.startNewEntry()
        viewModel.updateEditingTitle("   ")
        viewModel.updateEditingContent("Content without a title")

        viewModel.saveEntry()

        val finalState = awaitState { it.entries.isNotEmpty() }
        assertTrue(finalState.entries.any { it.title == "Untitled Entry" })
    }

    @Test
    fun saveEntry_withBlankLocation_storesNullLocation() {
        viewModel.startNewEntry()
        viewModel.updateEditingTitle("No Location")
        viewModel.updateEditingContent("Content")
        viewModel.updateEditingLocation("   ")

        viewModel.saveEntry()

        val finalState = awaitState { it.entries.isNotEmpty() }
        val saved = finalState.entries.first { it.title == "No Location" }
        assertNull(saved.location)
    }

    @Test
    fun saveEntry_forExistingEntry_updatesRatherThanInserts() {
        // Insert via repository then re-use the entry reference for editing
        viewModel.startNewEntry()
        viewModel.updateEditingTitle("Original Title")
        viewModel.updateEditingContent("Original content")
        viewModel.saveEntry()
        val stateWithEntry = awaitState { it.entries.isNotEmpty() && !it.isEditing }
        val originalEntry = stateWithEntry.entries.first()

        viewModel.editEntry(originalEntry)
        viewModel.updateEditingTitle("Updated Title")
        viewModel.saveEntry()

        val finalState = awaitState { it.entries.any { e -> e.title == "Updated Title" } }
        assertEquals(1, finalState.entries.size) // still one entry, not two
        assertEquals("Updated Title", finalState.entries.first().title)
    }

    // ---------------------------------------------------------------------------
    // deleteEntry — async path
    // ---------------------------------------------------------------------------

    @Test
    fun deleteEntry_removesEntryFromState() {
        viewModel.startNewEntry()
        viewModel.updateEditingTitle("To Delete")
        viewModel.updateEditingContent("Content")
        viewModel.saveEntry()
        val stateWithEntry = awaitState { it.entries.isNotEmpty() && !it.isEditing }
        val entry = stateWithEntry.entries.first { it.title == "To Delete" }

        viewModel.deleteEntry(entry)

        val finalState = awaitState { it.entries.none { e -> e.title == "To Delete" } }
        assertTrue(finalState.entries.none { it.title == "To Delete" })
    }

    // ---------------------------------------------------------------------------
    // Helper
    // ---------------------------------------------------------------------------

    private fun awaitState(
        timeoutMs: Long = 5_000L,
        predicate: (TripLogUiState) -> Boolean
    ): TripLogUiState = runBlocking {
        withTimeout(timeoutMs) {
            viewModel.uiState.first(predicate)
        }
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

    // ==================== Default values — remaining fields ====================

    @Test
    fun defaultState_editingDate_isNonZero() {
        val state = TripLogUiState()

        assertTrue("editingDate should be a valid timestamp", state.editingDate > 0L)
    }

    // ==================== Copy — remaining fields ====================

    @Test
    fun state_copy_modifiesEditingContent() {
        val original = TripLogUiState(editingContent = "Original content")

        val modified = original.copy(editingContent = "Modified content")

        assertEquals("Modified content", modified.editingContent)
        assertEquals("Original content", original.editingContent)
    }

    @Test
    fun state_copy_modifiesEditingLocation() {
        val original = TripLogUiState(editingLocation = "Paris")

        val modified = original.copy(editingLocation = "Tokyo")

        assertEquals("Tokyo", modified.editingLocation)
        assertEquals("Paris", original.editingLocation)
    }

    @Test
    fun state_copy_modifiesEditingDate() {
        val original = TripLogUiState(editingDate = 1_000L)

        val modified = original.copy(editingDate = 2_000L)

        assertEquals(2_000L, modified.editingDate)
        assertEquals(1_000L, original.editingDate)
    }

    @Test
    fun state_copy_modifiesIsLoading() {
        val original = TripLogUiState(isLoading = false)

        val modified = original.copy(isLoading = true)

        assertTrue(modified.isLoading)
        assertFalse(original.isLoading)
    }

    @Test
    fun state_copy_modifiesSelectedEntry() {
        val entry = TripLogEntry(id = 1L, title = "Entry", content = "Content")
        val original = TripLogUiState(selectedEntry = null)

        val modified = original.copy(selectedEntry = entry)

        assertNotNull(modified.selectedEntry)
        assertNull(original.selectedEntry)
    }

    @Test
    fun state_copy_modifiesEntries() {
        val original = TripLogUiState(entries = emptyList())
        val entry = TripLogEntry(id = 1L, title = "Trip", content = "Notes")

        val modified = original.copy(entries = listOf(entry))

        assertEquals(1, modified.entries.size)
        assertTrue(original.entries.isEmpty())
    }

    // ==================== Equality ====================

    @Test
    fun state_equals_sameValues() {
        val entry = TripLogEntry(id = 1L, title = "Trip", content = "Notes")
        val state1 = TripLogUiState(
            entries = listOf(entry),
            isEditing = true,
            editingTitle = "Title",
            editingDate = 1_000L
        )
        val state2 = TripLogUiState(
            entries = listOf(entry),
            isEditing = true,
            editingTitle = "Title",
            editingDate = 1_000L
        )

        assertEquals(state1, state2)
    }

    @Test
    fun state_notEquals_differentIsEditing() {
        val state1 = TripLogUiState(isEditing = false)
        val state2 = TripLogUiState(isEditing = true)

        assertNotEquals(state1, state2)
    }

    @Test
    fun state_notEquals_differentIsLoading() {
        val state1 = TripLogUiState(isLoading = false)
        val state2 = TripLogUiState(isLoading = true)

        assertNotEquals(state1, state2)
    }

    @Test
    fun state_notEquals_differentEditingContent() {
        val state1 = TripLogUiState(editingContent = "A")
        val state2 = TripLogUiState(editingContent = "B")

        assertNotEquals(state1, state2)
    }

    @Test
    fun state_notEquals_differentEditingLocation() {
        val state1 = TripLogUiState(editingLocation = "Paris")
        val state2 = TripLogUiState(editingLocation = "Tokyo")

        assertNotEquals(state1, state2)
    }

    @Test
    fun state_notEquals_differentEditingDate() {
        val state1 = TripLogUiState(editingDate = 1_000L)
        val state2 = TripLogUiState(editingDate = 2_000L)

        assertNotEquals(state1, state2)
    }

    @Test
    fun state_notEquals_differentSelectedEntry() {
        val entry = TripLogEntry(id = 1L, title = "Trip", content = "Notes")
        val state1 = TripLogUiState(selectedEntry = null)
        val state2 = TripLogUiState(selectedEntry = entry)

        assertNotEquals(state1, state2)
    }
}
