package com.unstampedpages.app.ui.screens.checklist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.ui.screens.checklist.components.MultiSelectActionBar
import com.unstampedpages.app.ui.theme.UnstampedPagesTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for [MultiSelectActionBar].
 *
 * Tests are organised into four groups:
 *   1. Rendering        — bar and child nodes are visible
 *   2. Count label      — plural string renders the correct singular / plural form
 *   3. Cancel callback  — tapping cancel fires onCancel, not onDelete
 *   4. Delete callback  — tapping delete fires onDelete, not onCancel
 */
@RunWith(AndroidJUnit4::class)
class MultiSelectActionBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private fun launch(
        selectedCount: Int,
        onCancel: () -> Unit = {},
        onDelete: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                MultiSelectActionBar(
                    selectedCount = selectedCount,
                    onCancel = onCancel,
                    onDelete = onDelete
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    // ---------------------------------------------------------------------------
    // 1. Rendering
    // ---------------------------------------------------------------------------

    @Test
    fun multiSelectActionBar_isDisplayed() {
        launch(selectedCount = 1)

        composeTestRule.onNodeWithTag("multi_select_action_bar").assertIsDisplayed()
    }

    @Test
    fun multiSelectActionBar_cancelButton_isDisplayed() {
        launch(selectedCount = 1)

        composeTestRule.onNodeWithTag("multi_select_cancel").assertIsDisplayed()
    }

    @Test
    fun multiSelectActionBar_deleteButton_isDisplayed() {
        launch(selectedCount = 1)

        composeTestRule.onNodeWithTag("multi_select_delete").assertIsDisplayed()
    }

    @Test
    fun multiSelectActionBar_selectedCountNode_isDisplayed() {
        launch(selectedCount = 1)

        composeTestRule.onNodeWithTag("selected_count").assertIsDisplayed()
    }

    @Test
    fun multiSelectActionBar_deleteLabel_isDisplayed() {
        launch(selectedCount = 1)

        composeTestRule.onNodeWithText("Delete").assertIsDisplayed()
    }

    // ---------------------------------------------------------------------------
    // 2. Count label — plural forms
    // ---------------------------------------------------------------------------

    @Test
    fun selectedCount_one_showsSingularLabel() {
        launch(selectedCount = 1)

        composeTestRule.onNodeWithTag("selected_count").assertTextEquals("1 item selected")
    }

    @Test
    fun selectedCount_two_showsPluralLabel() {
        launch(selectedCount = 2)

        composeTestRule.onNodeWithTag("selected_count").assertTextEquals("2 items selected")
    }

    @Test
    fun selectedCount_five_showsPluralLabel() {
        launch(selectedCount = 5)

        composeTestRule.onNodeWithTag("selected_count").assertTextEquals("5 items selected")
    }

    @Test
    fun selectedCount_ten_showsPluralLabel() {
        launch(selectedCount = 10)

        composeTestRule.onNodeWithTag("selected_count").assertTextEquals("10 items selected")
    }

    // ---------------------------------------------------------------------------
    // 3. Cancel callback
    // ---------------------------------------------------------------------------

    @Test
    fun clickingCancel_invokesOnCancel() {
        var called = false
        launch(selectedCount = 3, onCancel = { called = true })

        composeTestRule.onNodeWithTag("multi_select_cancel").performClick()
        composeTestRule.waitForIdle()

        assertTrue("onCancel should be called", called)
    }

    @Test
    fun clickingCancel_doesNotInvokeOnDelete() {
        var deleteCalled = false
        launch(selectedCount = 3, onDelete = { deleteCalled = true })

        composeTestRule.onNodeWithTag("multi_select_cancel").performClick()
        composeTestRule.waitForIdle()

        assertFalse("onDelete must not be called when cancel is tapped", deleteCalled)
    }

    @Test
    fun clickingCancel_callsCallbackExactlyOnce() {
        var count = 0
        launch(selectedCount = 2, onCancel = { count++ })

        composeTestRule.onNodeWithTag("multi_select_cancel").performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, count)
    }

    // ---------------------------------------------------------------------------
    // 4. Delete callback
    // ---------------------------------------------------------------------------

    @Test
    fun clickingDelete_invokesOnDelete() {
        var called = false
        launch(selectedCount = 3, onDelete = { called = true })

        composeTestRule.onNodeWithTag("multi_select_delete").performClick()
        composeTestRule.waitForIdle()

        assertTrue("onDelete should be called", called)
    }

    @Test
    fun clickingDelete_doesNotInvokeOnCancel() {
        var cancelCalled = false
        launch(selectedCount = 3, onCancel = { cancelCalled = true })

        composeTestRule.onNodeWithTag("multi_select_delete").performClick()
        composeTestRule.waitForIdle()

        assertFalse("onCancel must not be called when delete is tapped", cancelCalled)
    }

    @Test
    fun clickingDelete_callsCallbackExactlyOnce() {
        var count = 0
        launch(selectedCount = 2, onDelete = { count++ })

        composeTestRule.onNodeWithTag("multi_select_delete").performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, count)
    }
}
