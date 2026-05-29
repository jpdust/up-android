package com.unstampedpages.app.ui.screens.triplog

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.local.AppDatabase
import com.unstampedpages.app.ui.theme.UnstampedPagesTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented UI tests for [TripLogScreen].
 *
 * Tests are organised into five groups:
 *   1. Empty state       — correct empty-state copy and FAB visible; editor absent
 *   2. FAB click         — opens editor, hides FAB and empty message
 *   3. Editing state     — editor visible when isEditing=true; list/FAB absent
 *   4. Entry card        — title, content, location, and action buttons displayed
 *   5. Card interactions — Edit button / card tap open edit editor; Delete removes entry
 *
 * The singleton [AppDatabase] is wiped in [setUp] and [tearDown] to guarantee
 * each test starts from an empty, deterministic state.
 */
private const val EMPTY_STATE_TITLE = "No journal entries yet"
private const val FAB_NEW_ENTRY = "New entry"
private const val ENTRY_DUBLIN_DAY = "Day in Dublin"
private const val ENTRY_TO_BE_DELETED = "Entry to Delete"

@RunWith(AndroidJUnit4::class)
class TripLogScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var application: Application
    private lateinit var viewModel: TripLogViewModel

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

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private fun launch() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                TripLogScreen(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()
    }

    /**
     * Inserts an entry via the ViewModel and blocks until the entry is present in
     * [TripLogViewModel.uiState] and the editor has been dismissed.
     */
    private fun insertEntry(
        title: String,
        content: String,
        location: String? = null
    ) = runBlocking {
        viewModel.startNewEntry()
        viewModel.updateEditingTitle(title)
        viewModel.updateEditingContent(content)
        if (location != null) viewModel.updateEditingLocation(location)
        viewModel.saveEntry()
        withTimeout(5_000L) {
            viewModel.uiState.first {
                !it.isEditing && it.entries.any { e -> e.title == title }
            }
        }
    }

    // ---------------------------------------------------------------------------
    // 1. Empty state
    // ---------------------------------------------------------------------------

    @Test
    fun tripLogScreen_emptyState_showsEmptyTitle() {
        launch()

        composeTestRule.onNodeWithText(EMPTY_STATE_TITLE).assertIsDisplayed()
    }

    @Test
    fun tripLogScreen_emptyState_showsEmptyMessage() {
        launch()

        composeTestRule
            .onNodeWithText("Start documenting your travels!", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun tripLogScreen_emptyState_fabIsDisplayed() {
        launch()

        composeTestRule.onNodeWithContentDescription(FAB_NEW_ENTRY).assertIsDisplayed()
    }

    @Test
    fun tripLogScreen_emptyState_editorSaveButtonIsAbsent() {
        launch()

        composeTestRule.onNodeWithContentDescription("Save").assertDoesNotExist()
    }

    @Test
    fun tripLogScreen_emptyState_editorCancelButtonIsAbsent() {
        launch()

        composeTestRule.onNodeWithContentDescription("Cancel").assertDoesNotExist()
    }

    // ---------------------------------------------------------------------------
    // 2. FAB click opens editor
    // ---------------------------------------------------------------------------

    @Test
    fun tripLogScreen_clickingFab_showsNewEntryHeading() {
        launch()

        composeTestRule.onNodeWithContentDescription(FAB_NEW_ENTRY).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("New Entry").assertIsDisplayed()
    }

    @Test
    fun tripLogScreen_clickingFab_hidesFab() {
        launch()

        composeTestRule.onNodeWithContentDescription(FAB_NEW_ENTRY).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription(FAB_NEW_ENTRY).assertDoesNotExist()
    }

    @Test
    fun tripLogScreen_clickingFab_hidesEmptyTitle() {
        launch()

        composeTestRule.onNodeWithContentDescription(FAB_NEW_ENTRY).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(EMPTY_STATE_TITLE).assertDoesNotExist()
    }

    @Test
    fun tripLogScreen_clickingFab_showsSaveAndCancelButtons() {
        launch()

        composeTestRule.onNodeWithContentDescription(FAB_NEW_ENTRY).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Save").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Cancel").assertIsDisplayed()
    }

    // ---------------------------------------------------------------------------
    // 3. Editing state (set up before launch)
    // ---------------------------------------------------------------------------

    @Test
    fun tripLogScreen_editingState_showsEditor() {
        viewModel.startNewEntry()
        launch()

        composeTestRule.onNodeWithText("New Entry").assertIsDisplayed()
    }

    @Test
    fun tripLogScreen_editingState_fabIsAbsent() {
        viewModel.startNewEntry()
        launch()

        composeTestRule.onNodeWithContentDescription(FAB_NEW_ENTRY).assertDoesNotExist()
    }

    @Test
    fun tripLogScreen_editingState_emptyTitleIsAbsent() {
        viewModel.startNewEntry()
        launch()

        composeTestRule.onNodeWithText(EMPTY_STATE_TITLE).assertDoesNotExist()
    }

    @Test
    fun tripLogScreen_cancelFromEditor_returnsFabToScreen() {
        viewModel.startNewEntry()
        launch()

        composeTestRule.onNodeWithContentDescription("Cancel").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription(FAB_NEW_ENTRY).assertIsDisplayed()
    }

    @Test
    fun tripLogScreen_cancelFromEditor_hidesEditor() {
        viewModel.startNewEntry()
        launch()

        composeTestRule.onNodeWithContentDescription("Cancel").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Save").assertDoesNotExist()
    }

    // ---------------------------------------------------------------------------
    // 4. Entry card rendering
    // ---------------------------------------------------------------------------

    @Test
    fun tripLogScreen_withEntry_showsEntryTitle() {
        insertEntry("My Roman Holiday", "Visited the Colosseum")
        launch()

        composeTestRule.onNodeWithText("My Roman Holiday").assertIsDisplayed()
    }

    @Test
    fun tripLogScreen_withEntry_showsEntryContent() {
        insertEntry("Day in Paris", "Walked around the Eiffel Tower")
        launch()

        composeTestRule.onNodeWithText("Walked around the Eiffel Tower").assertIsDisplayed()
    }

    @Test
    fun tripLogScreen_withEntry_showsEditButton() {
        insertEntry("Tokyo Trip", "Amazing sushi")
        launch()

        composeTestRule.onNodeWithContentDescription("Edit").assertIsDisplayed()
    }

    @Test
    fun tripLogScreen_withEntry_showsDeleteButton() {
        insertEntry("Tokyo Trip", "Amazing sushi")
        launch()

        composeTestRule.onNodeWithContentDescription("Delete").assertIsDisplayed()
    }

    @Test
    fun tripLogScreen_withEntry_showsLocation_whenPresent() {
        insertEntry("Barcelona Trip", "Beach day", location = "Barcelona, Spain")
        launch()

        composeTestRule.onNodeWithText("Barcelona, Spain").assertIsDisplayed()
    }

    @Test
    fun tripLogScreen_withEntry_emptyStateMessageIsAbsent() {
        insertEntry("My Trip", "Some content")
        launch()

        composeTestRule.onNodeWithText(EMPTY_STATE_TITLE).assertDoesNotExist()
    }

    @Test
    fun tripLogScreen_withEntry_fabIsStillDisplayed() {
        insertEntry("My Trip", "Some content")
        launch()

        composeTestRule.onNodeWithContentDescription(FAB_NEW_ENTRY).assertIsDisplayed()
    }

    @Test
    fun tripLogScreen_withEntry_dateBadgeIsDisplayed() {
        insertEntry("Today's Adventure", "Fresh entry")
        launch()

        // The entry was just created so the date badge will say "Today"
        composeTestRule.onNodeWithText("Today").assertIsDisplayed()
    }

    // ---------------------------------------------------------------------------
    // 5. Card interactions
    // ---------------------------------------------------------------------------

    @Test
    fun tripLogScreen_clickingEditButton_showsEditEntryHeading() {
        insertEntry(ENTRY_DUBLIN_DAY, "Guinness tasting")
        launch()

        composeTestRule.onNodeWithContentDescription("Edit").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Edit Entry").assertIsDisplayed()
    }

    @Test
    fun tripLogScreen_clickingEditButton_populatesEditorWithEntryTitle() {
        insertEntry(ENTRY_DUBLIN_DAY, "Guinness tasting")
        launch()

        composeTestRule.onNodeWithContentDescription("Edit").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(ENTRY_DUBLIN_DAY).assertIsDisplayed()
    }

    @Test
    fun tripLogScreen_clickingCard_showsEditEntryHeading() {
        insertEntry("Lisbon", "Beautiful city")
        launch()

        composeTestRule.onNodeWithText("Lisbon").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Edit Entry").assertIsDisplayed()
    }

    @Test
    fun tripLogScreen_clickingDeleteButton_removesEntryFromList() {
        insertEntry(ENTRY_TO_BE_DELETED, "Temporary content")
        launch()
        composeTestRule.onNodeWithText(ENTRY_TO_BE_DELETED).assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Delete").performClick()

        runBlocking {
            withTimeout(3_000L) {
                viewModel.uiState.first { it.entries.none { e -> e.title == ENTRY_TO_BE_DELETED } }
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(ENTRY_TO_BE_DELETED).assertDoesNotExist()
    }

    @Test
    fun tripLogScreen_afterDeletingLastEntry_showsEmptyState() {
        insertEntry("Only Entry", "Will be deleted")
        launch()

        composeTestRule.onNodeWithContentDescription("Delete").performClick()

        runBlocking {
            withTimeout(3_000L) {
                viewModel.uiState.first { it.entries.isEmpty() }
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(EMPTY_STATE_TITLE).assertIsDisplayed()
    }
}
