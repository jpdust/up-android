package com.unstampedpages.app.ui.screens.checklist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.local.entity.ChecklistItem
import com.unstampedpages.app.data.model.ChecklistCategory
import com.unstampedpages.app.ui.screens.checklist.components.CategorySection
import com.unstampedpages.app.ui.screens.checklist.components.CategorySectionCallbacks
import com.unstampedpages.app.ui.screens.checklist.components.CategorySectionState
import com.unstampedpages.app.ui.theme.UnstampedPagesTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for [CategorySection].
 *
 * Tests are organised into five groups:
 *   1. Rendering       — container, header, and item-count text are always visible
 *   2. Expand icon     — contentDescription matches expanded / collapsed state
 *   3. Visibility      — items are hidden when collapsed and shown when expanded
 *   4. Header click    — tapping the header fires onToggleExpanded
 *   5. Item test tags  — each rendered item carries the expected test tag
 */
@RunWith(AndroidJUnit4::class)
class CategorySectionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private val uncheckedItem = ChecklistItem(id = 1L, content = "Laptop", isChecked = false, category = "ELECTRONICS")
    private val checkedItem   = ChecklistItem(id = 2L, content = "Charger", isChecked = true,  category = "ELECTRONICS")

    private fun defaultState(expanded: Boolean = false) = CategorySectionState(
        isExpanded = expanded,
        isMultiSelectMode = false,
        selectedItemIds = emptySet()
    )

    private fun launch(
        category: ChecklistCategory = ChecklistCategory.ELECTRONICS,
        items: List<ChecklistItem> = listOf(uncheckedItem, checkedItem),
        state: CategorySectionState = defaultState(),
        onToggleExpanded: () -> Unit = {},
        onItemChecked: (ChecklistItem) -> Unit = {},
        onItemDeleted: (ChecklistItem) -> Unit = {},
        onItemPinned: (ChecklistItem) -> Unit = {},
        onQuantityChanged: (ChecklistItem, Int) -> Unit = { _, _ -> },
        onItemLongPress: (Long) -> Unit = {},
        onItemSelected: (Long) -> Unit = {}
    ) {
        val callbacks = CategorySectionCallbacks(
            onToggleExpanded = onToggleExpanded,
            onItemChecked = onItemChecked,
            onItemDeleted = onItemDeleted,
            onItemPinned = onItemPinned,
            onQuantityChanged = onQuantityChanged,
            onItemLongPress = onItemLongPress,
            onItemSelected = onItemSelected
        )
        composeTestRule.setContent {
            UnstampedPagesTheme {
                CategorySection(
                    category = category,
                    items = items,
                    state = state,
                    callbacks = callbacks
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    // ---------------------------------------------------------------------------
    // 1. Rendering
    // ---------------------------------------------------------------------------

    @Test
    fun categorySection_container_isDisplayed() {
        launch()
        composeTestRule
            .onNodeWithTag("category_section_electronics")
            .assertIsDisplayed()
    }

    @Test
    fun categorySection_header_isDisplayed() {
        launch()
        composeTestRule
            .onNodeWithTag("category_header_electronics")
            .assertIsDisplayed()
    }

    @Test
    fun categorySection_showsPartialCheckedCount() {
        // 1 checked out of 2 items
        launch(items = listOf(uncheckedItem, checkedItem))
        composeTestRule.onNodeWithText("1/2").assertIsDisplayed()
    }

    @Test
    fun categorySection_showsZeroCheckedCount_whenNoItemChecked() {
        val items = listOf(
            ChecklistItem(id = 1L, content = "A", isChecked = false, category = "ELECTRONICS"),
            ChecklistItem(id = 2L, content = "B", isChecked = false, category = "ELECTRONICS")
        )
        launch(items = items)
        composeTestRule.onNodeWithText("0/2").assertIsDisplayed()
    }

    @Test
    fun categorySection_showsFullCheckedCount_whenAllItemsChecked() {
        val items = listOf(
            ChecklistItem(id = 1L, content = "A", isChecked = true, category = "ELECTRONICS"),
            ChecklistItem(id = 2L, content = "B", isChecked = true, category = "ELECTRONICS")
        )
        launch(items = items)
        composeTestRule.onNodeWithText("2/2").assertIsDisplayed()
    }

    @Test
    fun categorySection_showsZeroZeroCount_whenItemListIsEmpty() {
        launch(items = emptyList())
        composeTestRule.onNodeWithText("0/0").assertIsDisplayed()
    }

    @Test
    fun categorySection_showsCountOfOne_withSingleUncheckedItem() {
        launch(items = listOf(uncheckedItem))
        composeTestRule.onNodeWithText("0/1").assertIsDisplayed()
    }

    @Test
    fun categorySection_showsCountOfOne_withSingleCheckedItem() {
        launch(items = listOf(checkedItem))
        composeTestRule.onNodeWithText("1/1").assertIsDisplayed()
    }

    // ---------------------------------------------------------------------------
    // 2. Expand icon content description
    // ---------------------------------------------------------------------------

    @Test
    fun expandIcon_hasExpandDescription_whenCollapsed() {
        launch(state = defaultState(expanded = false))
        composeTestRule.onNodeWithContentDescription("Expand").assertIsDisplayed()
    }

    @Test
    fun expandIcon_hasCollapseDescription_whenExpanded() {
        launch(state = defaultState(expanded = true))
        composeTestRule.onNodeWithContentDescription("Collapse").assertIsDisplayed()
    }

    // ---------------------------------------------------------------------------
    // 3. Item visibility based on expanded state
    // ---------------------------------------------------------------------------

    @Test
    fun items_areNotInComposition_whenCollapsed() {
        launch(
            items = listOf(uncheckedItem, checkedItem),
            state = defaultState(expanded = false)
        )
        composeTestRule.onNodeWithTag("checklist_item_1").assertDoesNotExist()
        composeTestRule.onNodeWithTag("checklist_item_2").assertDoesNotExist()
    }

    @Test
    fun items_areDisplayed_whenExpanded() {
        launch(
            items = listOf(uncheckedItem, checkedItem),
            state = defaultState(expanded = true)
        )
        composeTestRule.onNodeWithTag("checklist_item_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("checklist_item_2").assertIsDisplayed()
    }

    @Test
    fun emptyItemList_collapsedSection_rendersNoItems() {
        launch(items = emptyList(), state = defaultState(expanded = false))
        // No item tags exist at all — just verifying no crash and the section renders
        composeTestRule.onNodeWithTag("category_section_electronics").assertIsDisplayed()
    }

    @Test
    fun emptyItemList_expandedSection_rendersNoItems() {
        launch(items = emptyList(), state = defaultState(expanded = true))
        composeTestRule.onNodeWithTag("category_section_electronics").assertIsDisplayed()
    }

    // ---------------------------------------------------------------------------
    // 4. Header click fires onToggleExpanded
    // ---------------------------------------------------------------------------

    @Test
    fun clickingHeader_invokesOnToggleExpanded() {
        var called = false
        launch(onToggleExpanded = { called = true })

        composeTestRule.onNodeWithTag("category_header_electronics").performClick()
        composeTestRule.waitForIdle()

        assertTrue("onToggleExpanded should have been called", called)
    }

    @Test
    fun clickingHeader_twiceFiresCallbackTwice() {
        var count = 0
        launch(onToggleExpanded = { count++ })

        composeTestRule.onNodeWithTag("category_header_electronics").performClick()
        composeTestRule.onNodeWithTag("category_header_electronics").performClick()
        composeTestRule.waitForIdle()

        assertEquals(2, count)
    }

    @Test
    fun clickingHeader_doesNotFireOtherCallbacks() {
        var checkedCalled = false
        var deletedCalled = false

        launch(
            onItemChecked = { checkedCalled = true },
            onItemDeleted = { deletedCalled = true }
        )

        composeTestRule.onNodeWithTag("category_header_electronics").performClick()
        composeTestRule.waitForIdle()

        assertTrue(!checkedCalled)
        assertTrue(!deletedCalled)
    }

    // ---------------------------------------------------------------------------
    // 5. Item test tags
    // ---------------------------------------------------------------------------

    @Test
    fun expandedSection_rendersItemsWithCorrectTestTags_forGivenIds() {
        val items = listOf(
            ChecklistItem(id = 10L, content = "Camera", category = "ELECTRONICS"),
            ChecklistItem(id = 20L, content = "Tripod", category = "ELECTRONICS")
        )
        launch(items = items, state = defaultState(expanded = true))

        composeTestRule.onNodeWithTag("checklist_item_10").assertIsDisplayed()
        composeTestRule.onNodeWithTag("checklist_item_20").assertIsDisplayed()
    }

    @Test
    fun expandedSection_withSingleItem_rendersSingleItemTag() {
        val items = listOf(ChecklistItem(id = 99L, content = "Headphones", category = "ELECTRONICS"))
        launch(items = items, state = defaultState(expanded = true))

        composeTestRule.onNodeWithTag("checklist_item_99").assertIsDisplayed()
    }

    @Test
    fun collapsedSection_itemTagsAbsent_evenWhenItemsProvided() {
        val items = listOf(
            ChecklistItem(id = 10L, content = "Camera", category = "ELECTRONICS"),
            ChecklistItem(id = 20L, content = "Tripod", category = "ELECTRONICS")
        )
        launch(items = items, state = defaultState(expanded = false))

        composeTestRule.onNodeWithTag("checklist_item_10").assertDoesNotExist()
        composeTestRule.onNodeWithTag("checklist_item_20").assertDoesNotExist()
    }

    // ---------------------------------------------------------------------------
    // 6. Category-specific test tags
    // ---------------------------------------------------------------------------

    @Test
    fun categorySection_usesLowercaseCategoryName_inTestTag_forDocuments() {
        launch(
            category = ChecklistCategory.DOCUMENTS,
            items = emptyList(),
            state = defaultState()
        )
        composeTestRule.onNodeWithTag("category_section_documents").assertIsDisplayed()
        composeTestRule.onNodeWithTag("category_header_documents").assertIsDisplayed()
    }

    @Test
    fun categorySection_usesLowercaseCategoryName_inTestTag_forClothing() {
        launch(
            category = ChecklistCategory.CLOTHING,
            items = emptyList(),
            state = defaultState()
        )
        composeTestRule.onNodeWithTag("category_section_clothing").assertIsDisplayed()
        composeTestRule.onNodeWithTag("category_header_clothing").assertIsDisplayed()
    }

    @Test
    fun categorySection_usesLowercaseCategoryName_inTestTag_forToiletries() {
        launch(
            category = ChecklistCategory.TOILETRIES,
            items = emptyList(),
            state = defaultState()
        )
        composeTestRule.onNodeWithTag("category_section_toiletries").assertIsDisplayed()
    }

    @Test
    fun categorySection_usesLowercaseCategoryName_inTestTag_forOther() {
        launch(
            category = ChecklistCategory.OTHER,
            items = emptyList(),
            state = defaultState()
        )
        composeTestRule.onNodeWithTag("category_section_other").assertIsDisplayed()
        composeTestRule.onNodeWithTag("category_header_other").assertIsDisplayed()
    }

    @Test
    fun categorySection_usesLowercaseCategoryName_inTestTag_forMedicine() {
        launch(
            category = ChecklistCategory.MEDICINE,
            items = emptyList(),
            state = defaultState()
        )
        composeTestRule.onNodeWithTag("category_section_medicine").assertIsDisplayed()
        composeTestRule.onNodeWithTag("category_header_medicine").assertIsDisplayed()
    }

    @Test
    fun categorySection_usesLowercaseCategoryName_inTestTag_forAccessories() {
        launch(
            category = ChecklistCategory.ACCESSORIES,
            items = emptyList(),
            state = defaultState()
        )
        composeTestRule.onNodeWithTag("category_section_accessories").assertIsDisplayed()
        composeTestRule.onNodeWithTag("category_header_accessories").assertIsDisplayed()
    }

    @Test
    fun categorySection_usesLowercaseCategoryName_inTestTag_forSnacks() {
        launch(
            category = ChecklistCategory.SNACKS,
            items = emptyList(),
            state = defaultState()
        )
        composeTestRule.onNodeWithTag("category_section_snacks").assertIsDisplayed()
        composeTestRule.onNodeWithTag("category_header_snacks").assertIsDisplayed()
    }

    // ---------------------------------------------------------------------------
    // 7. Item content text
    // ---------------------------------------------------------------------------

    @Test
    fun expandedSection_displaysItemContentText() {
        launch(
            items = listOf(uncheckedItem),
            state = defaultState(expanded = true)
        )
        composeTestRule.onNodeWithText("Laptop").assertIsDisplayed()
    }

    @Test
    fun expandedSection_displaysAllItemContentTexts() {
        launch(
            items = listOf(uncheckedItem, checkedItem),
            state = defaultState(expanded = true)
        )
        composeTestRule.onNodeWithText("Laptop").assertIsDisplayed()
        composeTestRule.onNodeWithText("Charger").assertIsDisplayed()
    }

    @Test
    fun collapsedSection_doesNotDisplayItemContentText() {
        launch(
            items = listOf(uncheckedItem),
            state = defaultState(expanded = false)
        )
        composeTestRule.onNodeWithText("Laptop").assertDoesNotExist()
    }

    // ---------------------------------------------------------------------------
    // 8. Check / Uncheck icon (normal mode)
    // ---------------------------------------------------------------------------

    @Test
    fun expandedSection_uncheckedItem_showsCheckContentDescription() {
        launch(
            items = listOf(uncheckedItem),
            state = defaultState(expanded = true)
        )
        composeTestRule.onNodeWithContentDescription("Check").assertIsDisplayed()
    }

    @Test
    fun expandedSection_checkedItem_showsUncheckContentDescription() {
        launch(
            items = listOf(checkedItem),
            state = defaultState(expanded = true)
        )
        composeTestRule.onNodeWithContentDescription("Uncheck").assertIsDisplayed()
    }

    // ---------------------------------------------------------------------------
    // 9. Multi-select mode checkbox rendering
    // ---------------------------------------------------------------------------

    @Test
    fun expandedSection_inMultiSelectMode_showsCheckbox() {
        launch(
            items = listOf(uncheckedItem),
            state = CategorySectionState(
                isExpanded = true,
                isMultiSelectMode = true,
                selectedItemIds = emptySet()
            )
        )
        composeTestRule.onNodeWithTag("item_checkbox").assertIsDisplayed()
    }

    @Test
    fun expandedSection_notInMultiSelectMode_doesNotShowCheckbox() {
        launch(
            items = listOf(uncheckedItem),
            state = CategorySectionState(
                isExpanded = true,
                isMultiSelectMode = false,
                selectedItemIds = emptySet()
            )
        )
        composeTestRule.onNodeWithTag("item_checkbox").assertDoesNotExist()
    }

    @Test
    fun expandedSection_multiSelectMode_multipleItems_allShowCheckboxes() {
        launch(
            items = listOf(uncheckedItem, checkedItem),
            state = CategorySectionState(
                isExpanded = true,
                isMultiSelectMode = true,
                selectedItemIds = emptySet()
            )
        )
        val checkboxes = composeTestRule.onAllNodesWithTag("item_checkbox")
        checkboxes[0].assertIsDisplayed()
        checkboxes[1].assertIsDisplayed()
    }

    // ---------------------------------------------------------------------------
    // 10. Item selection state in multi-select mode
    // ---------------------------------------------------------------------------

    @Test
    fun expandedSection_multiSelectMode_selectedItem_checkboxIsChecked() {
        launch(
            items = listOf(uncheckedItem),
            state = CategorySectionState(
                isExpanded = true,
                isMultiSelectMode = true,
                selectedItemIds = setOf(uncheckedItem.id)
            )
        )
        composeTestRule.onNodeWithTag("item_checkbox").assertIsOn()
    }

    @Test
    fun expandedSection_multiSelectMode_unselectedItem_checkboxIsNotChecked() {
        launch(
            items = listOf(uncheckedItem),
            state = CategorySectionState(
                isExpanded = true,
                isMultiSelectMode = true,
                selectedItemIds = emptySet()
            )
        )
        composeTestRule.onNodeWithTag("item_checkbox").assertIsOff()
    }

    // ---------------------------------------------------------------------------
    // 11. Quantity picker visibility
    // ---------------------------------------------------------------------------

    @Test
    fun expandedSection_itemWithQuantityGreaterThanOne_showsQuantityPicker() {
        val multiQuantityItem = ChecklistItem(
            id = 3L, content = "Socks", quantity = 3, isChecked = false, category = "ELECTRONICS"
        )
        launch(
            items = listOf(multiQuantityItem),
            state = defaultState(expanded = true)
        )
        composeTestRule.onNodeWithTag("quantity_picker_3").assertIsDisplayed()
    }

    @Test
    fun expandedSection_uncheckedItemWithQuantityOne_showsQuantityPicker() {
        val singleQuantityItem = ChecklistItem(
            id = 4L, content = "Hat", quantity = 1, isChecked = false, category = "ELECTRONICS"
        )
        launch(
            items = listOf(singleQuantityItem),
            state = defaultState(expanded = true)
        )
        composeTestRule.onNodeWithTag("quantity_picker_4").assertIsDisplayed()
    }

    @Test
    fun expandedSection_checkedItemWithQuantityOne_hidesQuantityPicker() {
        val checkedSingleItem = ChecklistItem(
            id = 5L, content = "Sunscreen", quantity = 1, isChecked = true, category = "ELECTRONICS"
        )
        launch(
            items = listOf(checkedSingleItem),
            state = defaultState(expanded = true)
        )
        composeTestRule.onNodeWithTag("quantity_picker_5").assertDoesNotExist()
    }

    // ---------------------------------------------------------------------------
    // 12. Pinned item indicator
    // ---------------------------------------------------------------------------

    @Test
    fun expandedSection_pinnedItem_showsPinnedContentDescription() {
        val pinnedItem = ChecklistItem(
            id = 6L, content = "Passport", isPinned = true, category = "ELECTRONICS"
        )
        launch(
            items = listOf(pinnedItem),
            state = defaultState(expanded = true)
        )
        composeTestRule.onNodeWithContentDescription("Pinned").assertIsDisplayed()
    }

    @Test
    fun expandedSection_unpinnedItem_doesNotShowPinnedContentDescription() {
        val unpinnedItem = ChecklistItem(
            id = 7L, content = "Visa", isPinned = false, category = "ELECTRONICS"
        )
        launch(
            items = listOf(unpinnedItem),
            state = defaultState(expanded = true)
        )
        composeTestRule.onNodeWithContentDescription("Pinned").assertDoesNotExist()
    }
}
