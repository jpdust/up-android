package com.unstampedpages.app.ui.screens.triplog

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.AnnotatedString
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.ui.theme.UnstampedPagesTheme
import com.unstampedpages.app.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for [JournalEntryEditor].
 *
 * Tests are organised into five groups:
 *   1. Title bar         — correct heading for new vs. edit entries; toolbar buttons visible
 *   2. Field content     — title, location, and story fields display the provided state
 *   3. Date display      — formatted date appears in the editor
 *   4. Save/Cancel       — tapping the toolbar icons fires the expected callbacks
 *   5. Field callbacks   — typing in each field invokes the correct onChange callback
 */
@RunWith(AndroidJUnit4::class)
class JournalEntryEditorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    /** A fixed epoch for deterministic date formatting in tests (2024-01-15 UTC). */
    private val testDateMillis = 1705276800000L

    private fun defaultState(
        title: String = "Test Title",
        content: String = "Test content",
        location: String = "Paris",
        date: Long = testDateMillis,
        isNewEntry: Boolean = true
    ) = JournalEntryState(
        title = title,
        content = content,
        location = location,
        date = date,
        isNewEntry = isNewEntry
    )

    private fun launch(
        state: JournalEntryState = defaultState(),
        onTitleChange: (String) -> Unit = {},
        onContentChange: (String) -> Unit = {},
        onLocationChange: (String) -> Unit = {},
        onDateChange: (Long) -> Unit = {},
        onSave: () -> Unit = {},
        onCancel: () -> Unit = {}
    ) {
        val callbacks = JournalEntryCallbacks(
            onTitleChange = onTitleChange,
            onContentChange = onContentChange,
            onLocationChange = onLocationChange,
            onDateChange = onDateChange,
            onSave = onSave,
            onCancel = onCancel
        )
        composeTestRule.setContent {
            UnstampedPagesTheme {
                JournalEntryEditor(state = state, callbacks = callbacks)
            }
        }
        composeTestRule.waitForIdle()
    }

    // ---------------------------------------------------------------------------
    // 1. Title bar
    // ---------------------------------------------------------------------------

    @Test
    fun journalEntryEditor_newEntry_showsNewEntryTitle() {
        launch(state = defaultState(isNewEntry = true))

        composeTestRule.onNodeWithText("New Entry").assertIsDisplayed()
    }

    @Test
    fun journalEntryEditor_editEntry_showsEditEntryTitle() {
        launch(state = defaultState(isNewEntry = false))

        composeTestRule.onNodeWithText("Edit Entry").assertIsDisplayed()
    }

    @Test
    fun journalEntryEditor_cancelIcon_isDisplayed() {
        launch()

        composeTestRule.onNodeWithContentDescription("Cancel").assertIsDisplayed()
    }

    @Test
    fun journalEntryEditor_saveIcon_isDisplayed() {
        launch()

        composeTestRule.onNodeWithContentDescription("Save").assertIsDisplayed()
    }

    // ---------------------------------------------------------------------------
    // 2. Field content
    // ---------------------------------------------------------------------------

    @Test
    fun journalEntryEditor_titleField_showsStateTitle() {
        launch(state = defaultState(title = "My Adventure"))

        composeTestRule.onNodeWithText("My Adventure").assertIsDisplayed()
    }

    @Test
    fun journalEntryEditor_locationField_showsStateLocation() {
        launch(state = defaultState(location = "Tokyo"))

        composeTestRule.onNodeWithText("Tokyo").assertIsDisplayed()
    }

    @Test
    fun journalEntryEditor_contentField_showsStateContent() {
        launch(state = defaultState(content = "What a day it was"))

        composeTestRule.onNodeWithText("What a day it was").assertIsDisplayed()
    }

    @Test
    fun journalEntryEditor_titleLabel_isDisplayed() {
        launch()

        composeTestRule.onNodeWithText("Title").assertIsDisplayed()
    }

    @Test
    fun journalEntryEditor_locationLabel_isDisplayed() {
        launch()

        composeTestRule.onNodeWithText("Location (optional)").assertIsDisplayed()
    }

    @Test
    fun journalEntryEditor_storyLabel_isDisplayed() {
        launch()

        composeTestRule.onNodeWithText("Your story").assertIsDisplayed()
    }

    // ---------------------------------------------------------------------------
    // 3. Date display
    // ---------------------------------------------------------------------------

    @Test
    fun journalEntryEditor_formattedDate_isDisplayed() {
        launch(state = defaultState(date = testDateMillis))

        val formatted = DateUtils.formatFullDate(testDateMillis)
        composeTestRule.onNodeWithText(formatted).assertIsDisplayed()
    }

    // ---------------------------------------------------------------------------
    // 4. Save / Cancel callbacks
    // ---------------------------------------------------------------------------

    @Test
    fun clickingSaveIcon_invokesOnSave() {
        var called = false
        launch(onSave = { called = true })

        composeTestRule.onNodeWithContentDescription("Save").performClick()
        composeTestRule.waitForIdle()

        assertTrue("onSave should be called", called)
    }

    @Test
    fun clickingCancelIcon_invokesOnCancel() {
        var called = false
        launch(onCancel = { called = true })

        composeTestRule.onNodeWithContentDescription("Cancel").performClick()
        composeTestRule.waitForIdle()

        assertTrue("onCancel should be called", called)
    }

    @Test
    fun clickingSaveIcon_doesNotInvokeOnCancel() {
        var cancelCalled = false
        launch(onCancel = { cancelCalled = true })

        composeTestRule.onNodeWithContentDescription("Save").performClick()
        composeTestRule.waitForIdle()

        assertEquals(false, cancelCalled)
    }

    @Test
    fun clickingCancelIcon_doesNotInvokeOnSave() {
        var saveCalled = false
        launch(onSave = { saveCalled = true })

        composeTestRule.onNodeWithContentDescription("Cancel").performClick()
        composeTestRule.waitForIdle()

        assertEquals(false, saveCalled)
    }

    @Test
    fun clickingSaveIcon_callsCallbackExactlyOnce() {
        var count = 0
        launch(onSave = { count++ })

        composeTestRule.onNodeWithContentDescription("Save").performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, count)
    }

    @Test
    fun clickingCancelIcon_callsCallbackExactlyOnce() {
        var count = 0
        launch(onCancel = { count++ })

        composeTestRule.onNodeWithContentDescription("Cancel").performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, count)
    }

    // ---------------------------------------------------------------------------
    // 5. Field callbacks
    // ---------------------------------------------------------------------------

    @Test
    fun typingInTitleField_invokesOnTitleChange() {
        var received = ""
        launch(
            state = defaultState(title = ""),
            onTitleChange = { received = it }
        )

        // In Compose 1.11 performTextInput routes through InsertTextAtCursor + a focus
        // step that can break the onValueChange chain for controlled text fields.
        // Calling SetText directly on the merged BasicTextField node (found by its label
        // text which is merged into the node's semantics) invokes the action handler
        // that wires straight to onValueChange — no IME or focus involvement.
        composeTestRule.onNodeWithText("Title")
            .performSemanticsAction(SemanticsActions.SetText) { it(AnnotatedString("Rome")) }
        composeTestRule.waitForIdle()

        assertTrue("onTitleChange should have received non-empty text", received.isNotEmpty())
    }

    @Test
    fun typingInLocationField_invokesOnLocationChange() {
        var received = ""
        launch(
            state = defaultState(location = ""),
            onLocationChange = { received = it }
        )

        composeTestRule.onNodeWithText("Location (optional)")
            .performSemanticsAction(SemanticsActions.SetText) { it(AnnotatedString("Berlin")) }
        composeTestRule.waitForIdle()

        assertTrue("onLocationChange should have received non-empty text", received.isNotEmpty())
    }

    @Test
    fun typingInContentField_invokesOnContentChange() {
        var received = ""
        launch(
            state = defaultState(content = ""),
            onContentChange = { received = it }
        )

        composeTestRule.onNodeWithText("Your story")
            .performSemanticsAction(SemanticsActions.SetText) { it(AnnotatedString("Today was incredible.")) }
        composeTestRule.waitForIdle()

        assertTrue("onContentChange should have received non-empty text", received.isNotEmpty())
    }
}
