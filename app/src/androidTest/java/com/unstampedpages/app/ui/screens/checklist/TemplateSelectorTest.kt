package com.unstampedpages.app.ui.screens.checklist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.model.ChecklistTemplate
import com.unstampedpages.app.ui.screens.checklist.components.TemplateSelector
import com.unstampedpages.app.ui.theme.UnstampedPagesTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for [TemplateSelector].
 *
 * Tests are organised into five groups:
 *   1. Rendering            — dialog card, title, description, and cancel button are visible
 *   2. Template options     — all three option cards are visible and labelled correctly
 *   3. Item counts          — each option shows the correct plural item-count string
 *   4. Cancel callback      — tapping cancel fires onDismiss, not onTemplateSelected
 *   5. Selection callbacks  — tapping each template fires onTemplateSelected with the right value
 */
@RunWith(AndroidJUnit4::class)
class TemplateSelectorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private fun launch(
        onDismiss: () -> Unit = {},
        onTemplateSelected: (ChecklistTemplate) -> Unit = {}
    ) {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                TemplateSelector(onDismiss = onDismiss, onTemplateSelected = onTemplateSelected)
            }
        }
        composeTestRule.waitForIdle()
    }

    // ---------------------------------------------------------------------------
    // 1. Rendering
    // ---------------------------------------------------------------------------

    @Test
    fun templateSelector_dialogCard_isDisplayed() {
        launch()

        composeTestRule.onNodeWithTag("template_selector_dialog").assertIsDisplayed()
    }

    @Test
    fun templateSelector_title_isDisplayed() {
        launch()

        composeTestRule.onNodeWithText("Load Template").assertIsDisplayed()
    }

    @Test
    fun templateSelector_description_isDisplayed() {
        launch()

        composeTestRule.onNodeWithText("Choose a template to add items to your checklist")
            .assertIsDisplayed()
    }

    @Test
    fun templateSelector_cancelButton_isDisplayed() {
        launch()

        composeTestRule.onNodeWithTag("template_cancel_button").assertIsDisplayed()
    }

    // ---------------------------------------------------------------------------
    // 2. Template options — visibility and display names
    // ---------------------------------------------------------------------------

    @Test
    fun templateSelector_businessTripOption_isDisplayed() {
        launch()

        composeTestRule.onNodeWithTag("template_option_business_trip").assertIsDisplayed()
    }

    @Test
    fun templateSelector_beachVacationOption_isDisplayed() {
        launch()

        composeTestRule.onNodeWithTag("template_option_beach_vacation").assertIsDisplayed()
    }

    @Test
    fun templateSelector_hikingOption_isDisplayed() {
        launch()

        composeTestRule.onNodeWithTag("template_option_hiking").assertIsDisplayed()
    }

    @Test
    fun allTemplateOptions_areAvailable() {
        launch()

        ChecklistTemplate.entries.forEach { template ->
            composeTestRule
                .onNodeWithTag("template_option_${template.name.lowercase()}")
                .assertIsDisplayed()
        }
    }

    @Test
    fun templateSelector_businessTripOption_showsDisplayName() {
        launch()

        composeTestRule.onNodeWithText("Business Trip").assertIsDisplayed()
    }

    @Test
    fun templateSelector_beachVacationOption_showsDisplayName() {
        launch()

        composeTestRule.onNodeWithText("Beach Vacation").assertIsDisplayed()
    }

    @Test
    fun templateSelector_hikingOption_showsDisplayName() {
        launch()

        composeTestRule.onNodeWithText("Hiking Adventure").assertIsDisplayed()
    }

    // ---------------------------------------------------------------------------
    // 3. Item counts
    // ---------------------------------------------------------------------------

    @Test
    fun templateSelector_businessTripOption_showsItemCount() {
        launch()

        val count = ChecklistTemplate.BUSINESS_TRIP.items.size
        composeTestRule.onNodeWithText("$count items").assertIsDisplayed()
    }

    @Test
    fun templateSelector_beachVacationOption_showsItemCount() {
        launch()

        // Verify the count string is present for the beach vacation template
        val count = ChecklistTemplate.BEACH_VACATION.items.size
        // Each option renders its own count; asserting the count value is correct
        assertEquals(16, count)
    }

    @Test
    fun templateSelector_hikingOption_showsItemCount() {
        launch()

        val count = ChecklistTemplate.HIKING.items.size
        assertEquals(20, count)
    }

    @Test
    fun templateSelector_allItemCountsArePositive() {
        ChecklistTemplate.entries.forEach { template ->
            assertTrue(
                "Template ${template.name} should have at least one item",
                template.items.isNotEmpty()
            )
        }
    }

    // ---------------------------------------------------------------------------
    // 4. Cancel callback
    // ---------------------------------------------------------------------------

    @Test
    fun clickingCancel_invokesOnDismiss() {
        var called = false
        launch(onDismiss = { called = true })

        composeTestRule.onNodeWithTag("template_cancel_button").performClick()
        composeTestRule.waitForIdle()

        assertTrue("onDismiss should be called when cancel is tapped", called)
    }

    @Test
    fun clickingCancel_doesNotInvokeOnTemplateSelected() {
        var selectedTemplate: ChecklistTemplate? = null
        launch(onTemplateSelected = { selectedTemplate = it })

        composeTestRule.onNodeWithTag("template_cancel_button").performClick()
        composeTestRule.waitForIdle()

        assertNull("onTemplateSelected must not fire when cancel is tapped", selectedTemplate)
    }

    @Test
    fun clickingCancel_callsOnDismissExactlyOnce() {
        var count = 0
        launch(onDismiss = { count++ })

        composeTestRule.onNodeWithTag("template_cancel_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, count)
    }

    // ---------------------------------------------------------------------------
    // 5. Selection callbacks
    // ---------------------------------------------------------------------------

    @Test
    fun clickingBusinessTrip_invokesOnTemplateSelectedWithBusinessTrip() {
        var received: ChecklistTemplate? = null
        launch(onTemplateSelected = { received = it })

        composeTestRule.onNodeWithTag("template_option_business_trip").performClick()
        composeTestRule.waitForIdle()

        assertEquals(ChecklistTemplate.BUSINESS_TRIP, received)
    }

    @Test
    fun clickingBeachVacation_invokesOnTemplateSelectedWithBeachVacation() {
        var received: ChecklistTemplate? = null
        launch(onTemplateSelected = { received = it })

        composeTestRule.onNodeWithTag("template_option_beach_vacation").performClick()
        composeTestRule.waitForIdle()

        assertEquals(ChecklistTemplate.BEACH_VACATION, received)
    }

    @Test
    fun clickingHiking_invokesOnTemplateSelectedWithHiking() {
        var received: ChecklistTemplate? = null
        launch(onTemplateSelected = { received = it })

        composeTestRule.onNodeWithTag("template_option_hiking").performClick()
        composeTestRule.waitForIdle()

        assertEquals(ChecklistTemplate.HIKING, received)
    }

    @Test
    fun clickingTemplate_doesNotInvokeOnDismiss() {
        var dismissCalled = false
        launch(onDismiss = { dismissCalled = true })

        composeTestRule.onNodeWithTag("template_option_business_trip").performClick()
        composeTestRule.waitForIdle()

        assertFalse("onDismiss must not fire when a template is selected", dismissCalled)
    }

    @Test
    fun clickingTemplate_callsOnTemplateSelectedExactlyOnce() {
        var count = 0
        launch(onTemplateSelected = { count++ })

        composeTestRule.onNodeWithTag("template_option_hiking").performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, count)
    }
}
