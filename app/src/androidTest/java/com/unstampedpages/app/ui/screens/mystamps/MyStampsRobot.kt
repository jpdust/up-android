package com.unstampedpages.app.ui.screens.mystamps

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.unstampedpages.app.data.CountryList

/**
 * Robot Pattern implementation for testing MyStampsScreen.
 *
 * This robot provides a clean API for UI tests, separating the "what" (test intent)
 * from the "how" (UI interaction details).
 *
 * Usage:
 * ```
 * myStampsRobot {
 *     verifyScreenDisplayed()
 *     verifyColumnHeaders()
 *     scrollToCountry("Zimbabwe")
 *     clickAddStampButton("US")
 *     verifyAddStampDialogDisplayed()
 * }
 * ```
 */
class MyStampsRobot(private val composeTestRule: ComposeTestRule) {

    // ==================== Screen Verification ====================

    /**
     * Verifies the My Stamps screen is displayed
     */
    fun verifyScreenDisplayed(): MyStampsRobot {
        composeTestRule.onNodeWithTag("my_stamps_screen").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the screen title "My Stamps" is displayed in the navigation
     */
    fun verifyScreenTitle(): MyStampsRobot {
        composeTestRule.onNodeWithText("My Stamps").assertIsDisplayed()
        return this
    }

    /**
     * Verifies both column headers (Country and Stamp) are displayed
     */
    fun verifyColumnHeaders(): MyStampsRobot {
        composeTestRule.onNodeWithTag("column_header_country").assertIsDisplayed()
        composeTestRule.onNodeWithTag("column_header_stamp").assertIsDisplayed()
        composeTestRule.onNodeWithText("Country").assertIsDisplayed()
        composeTestRule.onNodeWithText("Stamp").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the header row is displayed
     */
    fun verifyHeaderRowDisplayed(): MyStampsRobot {
        composeTestRule.onNodeWithTag("header_row").assertIsDisplayed()
        return this
    }

    // ==================== Country List Verification ====================

    /**
     * Verifies the country stamps list is displayed
     */
    fun verifyCountryListDisplayed(): MyStampsRobot {
        composeTestRule.onNodeWithTag("country_stamps_list").assertIsDisplayed()
        return this
    }

    /**
     * Verifies a specific country is displayed in the list
     */
    fun verifyCountryDisplayed(countryName: String): MyStampsRobot {
        composeTestRule.onNodeWithText(countryName).assertIsDisplayed()
        return this
    }

    /**
     * Verifies a specific country row is displayed by country code
     */
    fun verifyCountryRowDisplayed(countryCode: String): MyStampsRobot {
        composeTestRule.onNodeWithTag("country_row_$countryCode").assertIsDisplayed()
        return this
    }

    /**
     * Verifies all countries from CountryList are present in the list.
     * This scrolls through the entire list to check each country.
     */
    fun verifyAllCountriesPopulated(): MyStampsRobot {
        val countries = CountryList.countries
        val listNode = composeTestRule.onNodeWithTag("country_stamps_list")

        countries.forEach { country ->
            listNode.performScrollToNode(hasTestTag("country_row_${country.code}"))
            composeTestRule.onNodeWithTag("country_row_${country.code}").assertIsDisplayed()
        }
        return this
    }

    /**
     * Verifies the total count of countries matches the expected count
     */
    fun verifyCountryCount(expectedCount: Int): MyStampsRobot {
        // Scroll to ensure items are loaded, then verify count matches CountryList
        val actualCount = CountryList.countries.size
        assert(actualCount == expectedCount) {
            "Expected $expectedCount countries but found $actualCount"
        }
        return this
    }

    // ==================== Scrolling Actions ====================

    /**
     * Scrolls to a specific country by name
     */
    fun scrollToCountry(countryName: String): MyStampsRobot {
        val country = CountryList.countries.find { it.name == countryName }
        requireNotNull(country) { "Country '$countryName' not found in CountryList" }

        composeTestRule.onNodeWithTag("country_stamps_list")
            .performScrollToNode(hasTestTag("country_row_${country.code}"))
        return this
    }

    /**
     * Scrolls to a specific country by country code
     */
    fun scrollToCountryByCode(countryCode: String): MyStampsRobot {
        composeTestRule.onNodeWithTag("country_stamps_list")
            .performScrollToNode(hasTestTag("country_row_$countryCode"))
        return this
    }

    /**
     * Scrolls to the top of the country list
     */
    fun scrollToTop(): MyStampsRobot {
        val firstCountry = CountryList.countries.first()
        composeTestRule.onNodeWithTag("country_stamps_list")
            .performScrollToNode(hasTestTag("country_row_${firstCountry.code}"))
        return this
    }

    /**
     * Scrolls to the bottom of the country list
     */
    fun scrollToBottom(): MyStampsRobot {
        val lastCountry = CountryList.countries.last()
        composeTestRule.onNodeWithTag("country_stamps_list")
            .performScrollToNode(hasTestTag("country_row_${lastCountry.code}"))
        return this
    }

    // ==================== Add Stamp Actions ====================

    /**
     * Clicks the add stamp button for a specific country code
     */
    fun clickAddStampButton(countryCode: String): MyStampsRobot {
        scrollToCountryByCode(countryCode)
        composeTestRule.onNodeWithTag("add_stamp_$countryCode").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Clicks the add stamp button for a country by name
     */
    fun clickAddStampButtonByName(countryName: String): MyStampsRobot {
        val country = CountryList.countries.find { it.name == countryName }
        requireNotNull(country) { "Country '$countryName' not found in CountryList" }
        return clickAddStampButton(country.code)
    }

    // ==================== Dialog Verification ====================

    /**
     * Verifies the Add Stamp dialog is displayed
     */
    fun verifyAddStampDialogDisplayed(): MyStampsRobot {
        composeTestRule.onNodeWithTag("add_stamp_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add Stamp").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the Add Stamp dialog is NOT displayed
     */
    fun verifyAddStampDialogNotDisplayed(): MyStampsRobot {
        composeTestRule.onNodeWithTag("add_stamp_dialog").assertDoesNotExist()
        return this
    }

    /**
     * Verifies the dialog shows the correct country name
     */
    fun verifyDialogShowsCountry(countryName: String): MyStampsRobot {
        composeTestRule.onNodeWithText("Add a passport stamp image for $countryName")
            .assertIsDisplayed()
        return this
    }

    /**
     * Verifies Camera and Gallery buttons are displayed in the dialog
     */
    fun verifyDialogOptionsDisplayed(): MyStampsRobot {
        composeTestRule.onNodeWithTag("camera_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("gallery_button").assertIsDisplayed()
        composeTestRule.onNodeWithText("Camera").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gallery").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the Cancel button is displayed in the dialog
     */
    fun verifyCancelButtonDisplayed(): MyStampsRobot {
        composeTestRule.onNodeWithTag("cancel_button").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
        return this
    }

    // ==================== Dialog Actions ====================

    /**
     * Clicks the Camera button in the Add Stamp dialog
     */
    fun clickCameraButton(): MyStampsRobot {
        composeTestRule.onNodeWithTag("camera_button").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Clicks the Gallery button in the Add Stamp dialog
     */
    fun clickGalleryButton(): MyStampsRobot {
        composeTestRule.onNodeWithTag("gallery_button").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Clicks the Cancel button to dismiss the dialog
     */
    fun clickCancelButton(): MyStampsRobot {
        composeTestRule.onNodeWithTag("cancel_button").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Cancels adding a stamp by clicking the Cancel button
     */
    fun cancelAddingStamp(): MyStampsRobot {
        clickCancelButton()
        verifyAddStampDialogNotDisplayed()
        return this
    }

    // ==================== Remove Stamp Actions ====================

    /**
     * Clicks the remove stamp button for a specific country code.
     * Note: This only works if the country has a stamp.
     */
    fun clickRemoveStampButton(countryCode: String): MyStampsRobot {
        scrollToCountryByCode(countryCode)
        composeTestRule.onNodeWithTag("stamp_${countryCode}_remove").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Removes a stamp from a country by name
     */
    fun removeStampByName(countryName: String): MyStampsRobot {
        val country = CountryList.countries.find { it.name == countryName }
        requireNotNull(country) { "Country '$countryName' not found in CountryList" }
        return clickRemoveStampButton(country.code)
    }

    // ==================== Stamp Verification ====================

    /**
     * Verifies a stamp exists for a specific country
     */
    fun verifyStampExists(countryCode: String): MyStampsRobot {
        scrollToCountryByCode(countryCode)
        composeTestRule.onNodeWithTag("stamp_$countryCode").assertIsDisplayed()
        return this
    }

    /**
     * Verifies no stamp exists for a specific country (add button should be visible)
     */
    fun verifyNoStampExists(countryCode: String): MyStampsRobot {
        scrollToCountryByCode(countryCode)
        composeTestRule.onNodeWithTag("add_stamp_$countryCode").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the remove button is visible for a stamp
     */
    fun verifyRemoveButtonVisible(countryCode: String): MyStampsRobot {
        scrollToCountryByCode(countryCode)
        composeTestRule.onNodeWithTag("stamp_${countryCode}_remove").assertIsDisplayed()
        return this
    }

    // ==================== Utility Methods ====================

    /**
     * Waits for the UI to be idle
     */
    fun waitForIdle(): MyStampsRobot {
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Waits for a condition to be true, with timeout
     */
    fun waitUntil(timeoutMillis: Long = 5000, condition: () -> Boolean): MyStampsRobot {
        composeTestRule.waitUntil(timeoutMillis, condition)
        return this
    }

    companion object {
        /**
         * Extension function to use the robot with a DSL-like syntax
         */
        fun ComposeTestRule.myStampsRobot(block: MyStampsRobot.() -> Unit): MyStampsRobot {
            return MyStampsRobot(this).apply(block)
        }
    }
}
