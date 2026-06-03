package com.unstampedpages.app.ui.screens.checklist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.model.ChecklistCategory
import com.unstampedpages.app.ui.screens.checklist.components.AddItemDialog
import com.unstampedpages.app.ui.theme.UnstampedPagesTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for [AddItemDialog].
 *
 * Tests are organised into five groups:
 *   1. Rendering        — dialog, fields, and buttons are visible on launch
 *   2. Add-button state — disabled when item name is blank; enabled after typing
 *   3. Cancel callback  — tapping cancel fires onDismiss, not onAdd
 *   4. Add callback     — tapping add fires onAdd with the correct name, category, and quantity
 *   5. Category picker  — selecting a different category is reflected in the onAdd payload
 */
@RunWith(AndroidJUnit4::class)
class AddItemDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private fun launch(onDismiss: () -> Unit = {}, onAdd: (String, ChecklistCategory, Int) -> Unit = { _, _, _ -> }) {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                AddItemDialog(onDismiss = onDismiss, onAdd = onAdd)
            }
        }
        composeTestRule.waitForIdle()
    }

    // ---------------------------------------------------------------------------
    // 1. Rendering
    // ---------------------------------------------------------------------------

    @Test
    fun addItemDialog_dialogCard_isDisplayed() {
        launch()

        composeTestRule.onNodeWithTag("add_item_dialog").assertIsDisplayed()
    }

    @Test
    fun addItemDialog_title_isDisplayed() {
        launch()

        composeTestRule.onNodeWithText("Add Item").assertIsDisplayed()
    }

    @Test
    fun addItemDialog_itemNameInput_isDisplayed() {
        launch()

        composeTestRule.onNodeWithTag("item_name_input").assertIsDisplayed()
    }

    @Test
    fun addItemDialog_categoryDropdown_isDisplayed() {
        launch()

        composeTestRule.onNodeWithTag("category_dropdown").assertIsDisplayed()
    }

    @Test
    fun addItemDialog_quantityPicker_isDisplayed() {
        launch()

        composeTestRule.onNodeWithTag("dialog_quantity_picker").assertIsDisplayed()
    }

    @Test
    fun addItemDialog_cancelButton_isDisplayed() {
        launch()

        composeTestRule.onNodeWithTag("cancel_button").assertIsDisplayed()
    }

    @Test
    fun addItemDialog_addButton_isDisplayed() {
        launch()

        composeTestRule.onNodeWithTag("add_button").assertIsDisplayed()
    }

    @Test
    fun addItemDialog_quantityLabel_isDisplayed() {
        launch()

        composeTestRule.onNodeWithText("Quantity").assertIsDisplayed()
    }

    // ---------------------------------------------------------------------------
    // 2. Add-button enabled / disabled state
    // ---------------------------------------------------------------------------

    @Test
    fun addButton_isDisabledWhenItemNameIsEmpty() {
        launch()

        composeTestRule.onNodeWithTag("add_button").assertIsNotEnabled()
    }

    @Test
    fun addButton_isEnabledAfterTypingName() {
        launch()

        composeTestRule.onNodeWithTag("item_name_input").performTextInput("Sunscreen")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("add_button").assertIsEnabled()
    }

    @Test
    fun addButton_isDisabledForWhitespaceOnlyInput() {
        launch()

        composeTestRule.onNodeWithTag("item_name_input").performTextInput("   ")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("add_button").assertIsNotEnabled()
    }

    // ---------------------------------------------------------------------------
    // 3. Cancel callback
    // ---------------------------------------------------------------------------

    @Test
    fun clickingCancel_invokesOnDismiss() {
        var called = false
        launch(onDismiss = { called = true })

        composeTestRule.onNodeWithTag("cancel_button").performClick()
        composeTestRule.waitForIdle()

        assertTrue("onDismiss should be invoked", called)
    }

    @Test
    fun clickingCancel_doesNotInvokeOnAdd() {
        var addCalled = false
        launch(onAdd = { _, _, _ -> addCalled = true })

        composeTestRule.onNodeWithTag("cancel_button").performClick()
        composeTestRule.waitForIdle()

        assertFalse("onAdd must not be called when cancel is tapped", addCalled)
    }

    @Test
    fun clickingCancel_callsOnDismissExactlyOnce() {
        var count = 0
        launch(onDismiss = { count++ })

        composeTestRule.onNodeWithTag("cancel_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, count)
    }

    // ---------------------------------------------------------------------------
    // 4. Add callback — name, default category, and quantity
    // ---------------------------------------------------------------------------

    @Test
    fun clickingAdd_invokesOnAddWithEnteredName() {
        var receivedName = ""
        launch(onAdd = { name, _, _ -> receivedName = name })

        composeTestRule.onNodeWithTag("item_name_input").performTextInput("Passport")
        composeTestRule.onNodeWithTag("add_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals("Passport", receivedName)
    }

    @Test
    fun clickingAdd_invokesOnAddWithDefaultCategoryOther() {
        var receivedCategory: ChecklistCategory? = null
        launch(onAdd = { _, category, _ -> receivedCategory = category })

        composeTestRule.onNodeWithTag("item_name_input").performTextInput("Adapter")
        composeTestRule.onNodeWithTag("add_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals(ChecklistCategory.OTHER, receivedCategory)
    }

    @Test
    fun clickingAdd_invokesOnAddWithDefaultQuantityOne() {
        var receivedQuantity = -1
        launch(onAdd = { _, _, qty -> receivedQuantity = qty })

        composeTestRule.onNodeWithTag("item_name_input").performTextInput("Charger")
        composeTestRule.onNodeWithTag("add_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, receivedQuantity)
    }

    @Test
    fun clickingAdd_callsOnAddExactlyOnce() {
        var count = 0
        launch(onAdd = { _, _, _ -> count++ })

        composeTestRule.onNodeWithTag("item_name_input").performTextInput("Hat")
        composeTestRule.onNodeWithTag("add_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, count)
    }

    @Test
    fun afterIncreasingQuantity_addPassesUpdatedQuantity() {
        var receivedQuantity = -1
        launch(onAdd = { _, _, qty -> receivedQuantity = qty })

        composeTestRule.onNodeWithTag("item_name_input").performTextInput("Socks")
        // Tap increase twice inside the dialog's embedded QuantityPicker
        composeTestRule.onNodeWithTag("quantity_increase").performClick()
        composeTestRule.onNodeWithTag("quantity_increase").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("add_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals(3, receivedQuantity)
    }

    // ---------------------------------------------------------------------------
    // 5. Category picker
    // ---------------------------------------------------------------------------

    @Test
    fun selectingElectronicsCategory_passesElectronicsThroughOnAdd() {
        var receivedCategory: ChecklistCategory? = null
        launch(onAdd = { _, cat, _ -> receivedCategory = cat })

        // Open the dropdown and pick Electronics
        composeTestRule.onNodeWithTag("category_dropdown").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("category_option_electronics").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("item_name_input").performTextInput("Laptop")
        composeTestRule.onNodeWithTag("add_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals(ChecklistCategory.ELECTRONICS, receivedCategory)
    }

    @Test
    fun selectingClothingCategory_passesClothingThroughOnAdd() {
        var receivedCategory: ChecklistCategory? = null
        launch(onAdd = { _, cat, _ -> receivedCategory = cat })

        composeTestRule.onNodeWithTag("category_dropdown").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("category_option_clothing").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("item_name_input").performTextInput("T-shirt")
        composeTestRule.onNodeWithTag("add_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals(ChecklistCategory.CLOTHING, receivedCategory)
    }

    @Test
    fun allCategoryOptions_areAvailableInDropdown() {
        launch()

        composeTestRule.onNodeWithTag("category_dropdown").performClick()
        composeTestRule.waitForIdle()

        ChecklistCategory.entries.forEach { category ->
            composeTestRule
                .onNodeWithTag("category_option_${category.name.lowercase()}")
                .assertIsDisplayed()
        }
    }
}
