package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule

/**
 * Robot Pattern implementation for testing CountryInfoScreen (Countries tab).
 *
 * This robot provides a clean API for UI tests, separating the "what" (test intent)
 * from the "how" (UI interaction details).
 *
 * Usage:
 * ```
 * countriesRobot {
 *     verifyScreenDisplayed()
 *     verifySearchBarDisplayed()
 *     verifyWorldMapDisplayed()
 *     typeInSearchBar("Japan")
 *     selectSearchResult("jp")
 *     verifyBottomSheetDisplayed()
 *     verifyCurrencyInfoDisplayed()
 * }
 * ```
 */
class CountriesRobot(private val composeTestRule: ComposeTestRule) {

    // ==================== Screen Verification ====================

    /**
     * Verifies the Countries screen is displayed
     */
    fun verifyScreenDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("countries_screen").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the search bar is displayed
     */
    fun verifySearchBarDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("search_bar").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the world map is displayed
     */
    fun verifyWorldMapDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("world_map").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the world map container is displayed
     */
    fun verifyWorldMapContainerDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("world_map_container").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the map view selector is displayed
     */
    fun verifyMapViewSelectorDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_color_mode_selector").assertIsDisplayed()
        composeTestRule.onNodeWithText("Map View").assertIsDisplayed()
        return this
    }

    /**
     * Verifies all map mode options are displayed
     */
    fun verifyMapModeOptionsDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithText("Default").assertIsDisplayed()
        composeTestRule.onNodeWithText("Security Risk").assertIsDisplayed()
        composeTestRule.onNodeWithText("Visa Requirements").assertIsDisplayed()
        composeTestRule.onNodeWithText("Passport Validity").assertIsDisplayed()
        return this
    }

    // ==================== Search Bar Actions ====================

    /**
     * Types text into the search bar
     */
    fun typeInSearchBar(text: String): CountriesRobot {
        composeTestRule.onNodeWithTag("search_bar").performTextInput(text)
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Clears the search bar text
     */
    fun clearSearchBar(): CountriesRobot {
        composeTestRule.onNodeWithTag("search_bar").performTextClearance()
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Clicks the X button to clear the search bar
     */
    fun clickSearchClearButton(): CountriesRobot {
        composeTestRule.onNodeWithTag("search_clear_button").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Verifies the clear button is displayed
     */
    fun verifySearchClearButtonDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("search_clear_button").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the clear button is NOT displayed
     */
    fun verifySearchClearButtonNotDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("search_clear_button").assertDoesNotExist()
        return this
    }

    /**
     * Verifies the search bar is empty
     */
    fun verifySearchBarEmpty(): CountriesRobot {
        composeTestRule.onNodeWithTag("search_bar").assertTextEquals("")
        return this
    }

    /**
     * Verifies the search bar contains specific text
     */
    fun verifySearchBarContains(text: String): CountriesRobot {
        composeTestRule.onNodeWithTag("search_bar").assertTextContains(text)
        return this
    }

    // ==================== Search Results ====================

    /**
     * Verifies search results dropdown is displayed
     */
    fun verifySearchResultsDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("search_results_dropdown").assertIsDisplayed()
        return this
    }

    /**
     * Verifies search results dropdown is NOT displayed
     */
    fun verifySearchResultsNotDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("search_results_dropdown").assertDoesNotExist()
        return this
    }

    /**
     * Verifies a specific country appears in search results
     */
    fun verifyCountryInSearchResults(countryName: String): CountriesRobot {
        composeTestRule.onNodeWithText(countryName, substring = true).assertIsDisplayed()
        return this
    }

    /**
     * Selects a country from search results by country ID
     */
    fun selectSearchResult(countryId: String): CountriesRobot {
        composeTestRule.onNodeWithTag("search_result_$countryId").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Selects a country from search results by clicking on the country name
     */
    fun selectSearchResultByName(countryName: String): CountriesRobot {
        composeTestRule.onNodeWithText(countryName, substring = true).performClick()
        composeTestRule.waitForIdle()
        return this
    }

    // ==================== Map Interactions ====================

    /**
     * Performs a pan gesture on the map
     */
    fun panMap(startX: Float, startY: Float, endX: Float, endY: Float): CountriesRobot {
        composeTestRule.onNodeWithTag("world_map").performTouchInput {
            swipe(
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                durationMillis = 200
            )
        }
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Pans the map to the left
     */
    fun panMapLeft(): CountriesRobot {
        composeTestRule.onNodeWithTag("world_map").performTouchInput {
            swipe(
                start = Offset(centerX + 100f, centerY),
                end = Offset(centerX - 100f, centerY),
                durationMillis = 200
            )
        }
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Pans the map to the right
     */
    fun panMapRight(): CountriesRobot {
        composeTestRule.onNodeWithTag("world_map").performTouchInput {
            swipe(
                start = Offset(centerX - 100f, centerY),
                end = Offset(centerX + 100f, centerY),
                durationMillis = 200
            )
        }
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Performs a pinch-to-zoom-in gesture on the map
     */
    fun zoomInMap(): CountriesRobot {
        composeTestRule.onNodeWithTag("world_map").performTouchInput {
            pinch(
                start0 = Offset(centerX - 50f, centerY),
                end0 = Offset(centerX - 150f, centerY),
                start1 = Offset(centerX + 50f, centerY),
                end1 = Offset(centerX + 150f, centerY),
                durationMillis = 300
            )
        }
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Performs a pinch-to-zoom-out gesture on the map
     */
    fun zoomOutMap(): CountriesRobot {
        composeTestRule.onNodeWithTag("world_map").performTouchInput {
            pinch(
                start0 = Offset(centerX - 150f, centerY),
                end0 = Offset(centerX - 50f, centerY),
                start1 = Offset(centerX + 150f, centerY),
                end1 = Offset(centerX + 50f, centerY),
                durationMillis = 300
            )
        }
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Taps at a specific position on the map
     */
    fun tapMapAt(x: Float, y: Float): CountriesRobot {
        composeTestRule.onNodeWithTag("world_map").performTouchInput {
            click(Offset(x, y))
        }
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Taps the center of the map
     */
    fun tapMapCenter(): CountriesRobot {
        composeTestRule.onNodeWithTag("world_map").performTouchInput {
            click(center)
        }
        composeTestRule.waitForIdle()
        return this
    }

    // ==================== Map Mode Selection ====================

    /**
     * Selects the Default map mode
     */
    fun selectDefaultMapMode(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_default").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Selects the Security Risk map mode
     */
    fun selectSecurityRiskMapMode(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_security_risk").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Selects the Visa Requirements map mode
     */
    fun selectVisaRequirementsMapMode(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_visa_requirements").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Selects the Passport Validity map mode
     */
    fun selectPassportValidityMapMode(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_passport_validity").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Verifies Default map mode is selected
     */
    fun verifyDefaultMapModeSelected(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_radio_default").assertIsSelected()
        return this
    }

    /**
     * Verifies Security Risk map mode is selected
     */
    fun verifySecurityRiskMapModeSelected(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_radio_security_risk").assertIsSelected()
        return this
    }

    /**
     * Verifies Visa Requirements map mode is selected
     */
    fun verifyVisaRequirementsMapModeSelected(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_radio_visa_requirements").assertIsSelected()
        return this
    }

    /**
     * Verifies Passport Validity map mode is selected
     */
    fun verifyPassportValidityMapModeSelected(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_radio_passport_validity").assertIsSelected()
        return this
    }

    // ==================== Map Legend ====================

    /**
     * Taps the compass icon in the bottom-right corner of the map.
     * The compass icon is positioned at (width - 34dp, height - 34dp) from the canvas origin.
     * Using percentage-based calculation to handle different screen densities.
     */
    fun tapCompassIcon(): CountriesRobot {
        composeTestRule.onNodeWithTag("world_map").performTouchInput {
            // Tap in the bottom-right area where the compass is located
            // Compass center is at approximately 90% from left edge and 95% from top edge
            // (accounting for typical screen dimensions and 34dp offset from corner)
            val compassX = right - (width * 0.08f).coerceAtLeast(50f)
            val compassY = bottom - (height * 0.08f).coerceAtLeast(50f)
            click(Offset(compassX, compassY))
        }
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Verifies the map legend is displayed
     */
    fun verifyLegendDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_legend").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the map legend is NOT displayed
     */
    fun verifyLegendNotDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_legend").assertDoesNotExist()
        return this
    }

    /**
     * Verifies the legend close button is displayed
     */
    fun verifyLegendCloseButtonDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("legend_close_button").assertIsDisplayed()
        return this
    }

    /**
     * Closes the map legend by clicking the close button
     */
    fun closeLegend(): CountriesRobot {
        composeTestRule.onNodeWithTag("legend_close_button").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Verifies the Security Risk legend content
     */
    fun verifySecurityRiskLegendContent(): CountriesRobot {
        composeTestRule.onNodeWithTag("legend_item_low_risk").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_medium_risk").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_high_risk").assertIsDisplayed()
        composeTestRule.onNodeWithText("Low Risk").assertIsDisplayed()
        composeTestRule.onNodeWithText("Medium Risk").assertIsDisplayed()
        composeTestRule.onNodeWithText("High Risk").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the Visa Requirements legend content
     */
    fun verifyVisaRequirementsLegendContent(): CountriesRobot {
        composeTestRule.onNodeWithTag("legend_item_visa_not_required").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_evisa").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_visa_on_arrival").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_visa_required").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_restricted").assertIsDisplayed()
        composeTestRule.onNodeWithText("Visa Not Required").assertIsDisplayed()
        composeTestRule.onNodeWithText("eVisa").assertIsDisplayed()
        composeTestRule.onNodeWithText("Visa On Arrival").assertIsDisplayed()
        composeTestRule.onNodeWithText("Visa Required").assertIsDisplayed()
        composeTestRule.onNodeWithText("Restricted").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the Passport Validity legend content
     */
    fun verifyPassportValidityLegendContent(): CountriesRobot {
        composeTestRule.onNodeWithTag("legend_item_6_months").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_3_months").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_duration_of_stay").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_other").assertIsDisplayed()
        composeTestRule.onNodeWithText("6 Months").assertIsDisplayed()
        composeTestRule.onNodeWithText("3 Months").assertIsDisplayed()
        composeTestRule.onNodeWithText("Duration of Stay").assertIsDisplayed()
        composeTestRule.onNodeWithText("Other").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the Map Key title is displayed in the legend
     */
    fun verifyLegendTitleDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithText("Map Key").assertIsDisplayed()
        return this
    }

    // ==================== Bottom Sheet Verification ====================

    /**
     * Verifies the country detail bottom sheet is displayed
     */
    fun verifyBottomSheetDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("country_detail_sheet").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the country detail bottom sheet is NOT displayed
     */
    fun verifyBottomSheetNotDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("country_detail_sheet").assertDoesNotExist()
        return this
    }

    /**
     * Verifies the bottom sheet shows the correct country name
     */
    fun verifyBottomSheetCountryName(countryName: String): CountriesRobot {
        composeTestRule.onNodeWithTag("country_name").assertTextEquals(countryName)
        return this
    }

    /**
     * Verifies the country header is displayed
     */
    fun verifyCountryHeaderDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("country_header").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the country flag is displayed
     */
    fun verifyCountryFlagDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("country_flag").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the country continent is displayed
     */
    fun verifyCountryContinentDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("country_continent").assertIsDisplayed()
        return this
    }

    // ==================== Bottom Sheet Content Verification ====================

    /**
     * Verifies the safety level info is displayed
     */
    fun verifySafetyLevelDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("info_safety_level").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Safety Level").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the safety level has a specific value
     */
    fun verifySafetyLevelValue(value: String): CountriesRobot {
        composeTestRule.onNodeWithTag("info_safety_level_value").assertTextEquals(value)
        return this
    }

    /**
     * Verifies the entry requirement info is displayed
     */
    fun verifyEntryRequirementDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("info_entry_requirement").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Entry Requirement").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the entry requirement has a specific value
     */
    fun verifyEntryRequirementValue(value: String): CountriesRobot {
        composeTestRule.onNodeWithTag("info_entry_requirement_value").assertTextEquals(value)
        return this
    }

    /**
     * Verifies the passport validity info is displayed
     */
    fun verifyPassportValidityDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("info_passport_validity").performScrollTo().assertIsDisplayed()
        // Note: We don't verify text "Passport Validity" separately as it also exists in the map mode selector
        return this
    }

    /**
     * Verifies the passport validity has a specific value
     */
    fun verifyPassportValidityValue(value: String): CountriesRobot {
        composeTestRule.onNodeWithTag("info_passport_validity_value").assertTextEquals(value)
        return this
    }

    /**
     * Verifies the currency info is displayed
     */
    fun verifyCurrencyInfoDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("info_currency").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Currency").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the currency has a specific value
     */
    fun verifyCurrencyValue(value: String): CountriesRobot {
        composeTestRule.onNodeWithTag("info_currency_value").assertTextEquals(value)
        return this
    }

    /**
     * Verifies the power outlet info is displayed
     */
    fun verifyPowerOutletDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("info_power_outlet").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Power Outlet").assertIsDisplayed()
        return this
    }

    /**
     * Verifies the power outlet has a specific value
     */
    fun verifyPowerOutletValue(value: String): CountriesRobot {
        composeTestRule.onNodeWithTag("info_power_outlet_value").assertTextEquals(value)
        return this
    }

    /**
     * Verifies all country detail info sections are displayed
     */
    fun verifyAllCountryInfoDisplayed(): CountriesRobot {
        verifySafetyLevelDisplayed()
        verifyEntryRequirementDisplayed()
        verifyPassportValidityDisplayed()
        verifyCurrencyInfoDisplayed()
        verifyPowerOutletDisplayed()
        return this
    }

    // ==================== Currency Converter ====================

    /**
     * Verifies the currency converter is displayed
     */
    fun verifyCurrencyConverterDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("currency_converter").performScrollTo().assertIsDisplayed()
        return this
    }

    /**
     * Verifies the currency converter is NOT displayed (for USD countries)
     */
    fun verifyCurrencyConverterNotDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("currency_converter").assertDoesNotExist()
        return this
    }

    /**
     * Verifies the USD label is displayed in the currency converter
     */
    fun verifyUsdLabelDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
        return this
    }

    // ==================== Bottom Sheet Actions ====================

    /**
     * Closes the bottom sheet by clicking the close button
     */
    fun closeBottomSheet(): CountriesRobot {
        composeTestRule.onNodeWithTag("bottom_sheet_close_button").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Closes the bottom sheet by clicking the scrim (dark overlay).
     * Clicks on the top portion of the scrim to avoid hitting the bottom sheet.
     */
    fun closeBottomSheetByTappingScrim(): CountriesRobot {
        composeTestRule.onNodeWithTag("bottom_sheet_scrim").performTouchInput {
            // Click near the top of the scrim, above the bottom sheet
            click(Offset(centerX, top + 50f))
        }
        composeTestRule.waitForIdle()
        return this
    }

    // ==================== Combined Verification Flows ====================

    /**
     * Verifies the page load with all main components
     */
    fun verifyPageLoad(): CountriesRobot {
        verifyScreenDisplayed()
        verifySearchBarDisplayed()
        verifyWorldMapDisplayed()
        verifyMapViewSelectorDisplayed()
        return this
    }

    /**
     * Searches for a country and selects it from results
     */
    fun searchAndSelectCountry(searchText: String, countryId: String): CountriesRobot {
        typeInSearchBar(searchText)
        verifySearchResultsDisplayed()
        selectSearchResult(countryId)
        verifyBottomSheetDisplayed()
        return this
    }

    /**
     * Verifies a complete country info flow
     */
    fun verifyCountryInfoFlow(
        searchText: String,
        countryId: String,
        countryName: String
    ): CountriesRobot {
        searchAndSelectCountry(searchText, countryId)
        verifyBottomSheetCountryName(countryName)
        verifyAllCountryInfoDisplayed()
        return this
    }

    // ==================== Utility Methods ====================

    /**
     * Waits for the UI to be idle
     */
    fun waitForIdle(): CountriesRobot {
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Waits for a condition to be true, with timeout
     */
    fun waitUntil(timeoutMillis: Long = 5000, condition: () -> Boolean): CountriesRobot {
        composeTestRule.waitUntil(timeoutMillis, condition)
        return this
    }

    /**
     * Waits for animation to complete (short delay)
     */
    fun waitForAnimation(): CountriesRobot {
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()
        return this
    }

    companion object {
        /**
         * Extension function to use the robot with a DSL-like syntax
         */
        fun ComposeTestRule.countriesRobot(block: CountriesRobot.() -> Unit): CountriesRobot {
            return CountriesRobot(this).apply(block)
        }
    }
}
