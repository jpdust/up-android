package com.unstampedpages.app.ui.screens.checklist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.ui.screens.checklist.components.QuantityPicker
import com.unstampedpages.app.ui.theme.UnstampedPagesTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for [QuantityPicker].
 *
 * Tests are organised into five groups:
 *   1. Display          — quantity label renders the correct text
 *   2. Enabled state    — decrease/increase buttons are present and respond when enabled = true
 *   3. Boundary         — decrease button is disabled at quantity = 1
 *   4. Callbacks        — tapping buttons delivers the correct new value
 *   5. Disabled mode    — control buttons are absent when enabled = false
 */
@RunWith(AndroidJUnit4::class)
class QuantityPickerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private fun launch(quantity: Int, enabled: Boolean = true, onQuantityChange: (Int) -> Unit = {}) {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                QuantityPicker(
                    quantity = quantity,
                    onQuantityChange = onQuantityChange,
                    enabled = enabled
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    /** Launches a stateful QuantityPicker that drives itself; returns a reference to read state. */
    private fun launchStateful(initial: Int): () -> Int {
        var current = initial
        composeTestRule.setContent {
            UnstampedPagesTheme {
                var qty by remember { mutableIntStateOf(initial) }
                current = qty
                QuantityPicker(
                    quantity = qty,
                    onQuantityChange = {
                        qty = it
                        current = it
                    },
                    enabled = true
                )
            }
        }
        composeTestRule.waitForIdle()
        return { current }
    }

    // ---------------------------------------------------------------------------
    // 1. Display
    // ---------------------------------------------------------------------------

    @Test
    fun quantityPicker_showsX1ForQuantityOne() {
        launch(quantity = 1)

        composeTestRule.onNodeWithTag("quantity_value").assertTextEquals("x1")
    }

    @Test
    fun quantityPicker_showsX5ForQuantityFive() {
        launch(quantity = 5)

        composeTestRule.onNodeWithTag("quantity_value").assertTextEquals("x5")
    }

    @Test
    fun quantityPicker_showsX99ForLargeQuantity() {
        launch(quantity = 99)

        composeTestRule.onNodeWithTag("quantity_value").assertTextEquals("x99")
    }

    @Test
    fun quantityPicker_quantityValueNode_isDisplayed() {
        launch(quantity = 3)

        composeTestRule.onNodeWithTag("quantity_value").assertIsDisplayed()
    }

    // ---------------------------------------------------------------------------
    // 2. Enabled state — buttons are present
    // ---------------------------------------------------------------------------

    @Test
    fun quantityPicker_enabled_decreaseButtonIsDisplayed() {
        launch(quantity = 2)

        composeTestRule.onNodeWithTag("quantity_decrease").assertIsDisplayed()
    }

    @Test
    fun quantityPicker_enabled_increaseButtonIsDisplayed() {
        launch(quantity = 1)

        composeTestRule.onNodeWithTag("quantity_increase").assertIsDisplayed()
    }

    @Test
    fun quantityPicker_enabled_increaseButtonIsEnabled() {
        launch(quantity = 1)

        composeTestRule.onNodeWithTag("quantity_increase").assertIsEnabled()
    }

    // ---------------------------------------------------------------------------
    // 3. Boundary — decrease disabled at quantity = 1
    // ---------------------------------------------------------------------------

    @Test
    fun quantityPicker_quantityOne_decreaseButtonIsDisabled() {
        launch(quantity = 1)

        composeTestRule.onNodeWithTag("quantity_decrease").assertIsNotEnabled()
    }

    @Test
    fun quantityPicker_quantityTwo_decreaseButtonIsEnabled() {
        launch(quantity = 2)

        composeTestRule.onNodeWithTag("quantity_decrease").assertIsEnabled()
    }

    // ---------------------------------------------------------------------------
    // 4. Callbacks
    // ---------------------------------------------------------------------------

    @Test
    fun clickingIncrease_callsOnQuantityChangeWithIncrementedValue() {
        var received = -1
        launch(quantity = 3, onQuantityChange = { received = it })

        composeTestRule.onNodeWithTag("quantity_increase").performClick()
        composeTestRule.waitForIdle()

        assertEquals(4, received)
    }

    @Test
    fun clickingDecrease_callsOnQuantityChangeWithDecrementedValue() {
        var received = -1
        launch(quantity = 3, onQuantityChange = { received = it })

        composeTestRule.onNodeWithTag("quantity_decrease").performClick()
        composeTestRule.waitForIdle()

        assertEquals(2, received)
    }

    @Test
    fun clickingDecrease_atQuantityOne_doesNotCallOnQuantityChange() {
        var called = false
        launch(quantity = 1, onQuantityChange = { called = true })

        composeTestRule.onNodeWithTag("quantity_decrease").performClick()
        composeTestRule.waitForIdle()

        assertEquals(false, called)
    }

    @Test
    fun clickingIncrease_callsCallbackExactlyOnce() {
        var count = 0
        launch(quantity = 1, onQuantityChange = { count++ })

        composeTestRule.onNodeWithTag("quantity_increase").performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, count)
    }

    @Test
    fun multipleIncrements_accumulateCorrectly() {
        val getValue = launchStateful(initial = 1)

        repeat(4) {
            composeTestRule.onNodeWithTag("quantity_increase").performClick()
            composeTestRule.waitForIdle()
        }

        assertEquals(5, getValue())
    }

    @Test
    fun incrementThenDecrement_returnsToOriginalValue() {
        val getValue = launchStateful(initial = 3)

        composeTestRule.onNodeWithTag("quantity_increase").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("quantity_decrease").performClick()
        composeTestRule.waitForIdle()

        assertEquals(3, getValue())
    }

    // ---------------------------------------------------------------------------
    // 5. Disabled mode
    // ---------------------------------------------------------------------------

    @Test
    fun quantityPicker_disabled_decreaseButtonDoesNotExist() {
        launch(quantity = 5, enabled = false)

        composeTestRule.onNodeWithTag("quantity_decrease").assertDoesNotExist()
    }

    @Test
    fun quantityPicker_disabled_increaseButtonDoesNotExist() {
        launch(quantity = 5, enabled = false)

        composeTestRule.onNodeWithTag("quantity_increase").assertDoesNotExist()
    }

    @Test
    fun quantityPicker_disabled_quantityValueIsStillDisplayed() {
        launch(quantity = 7, enabled = false)

        composeTestRule.onNodeWithTag("quantity_value")
            .assertIsDisplayed()
            .assertTextEquals("x7")
    }
}
