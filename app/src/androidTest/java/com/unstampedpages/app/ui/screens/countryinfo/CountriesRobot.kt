package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.unstampedpages.app.data.AppConstants

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

    fun verifyScreenDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("countries_screen").assertIsDisplayed()
        return this
    }

    fun verifySearchBarDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("search_bar").assertIsDisplayed()
        return this
    }

    fun verifyWorldMapDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("world_map").assertIsDisplayed()
        return this
    }

    fun verifyWorldMapContainerDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("world_map_container").assertIsDisplayed()
        return this
    }

    fun verifyMapViewSelectorDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_color_mode_selector").assertIsDisplayed()
        composeTestRule.onNodeWithText("Map View").assertIsDisplayed()
        return this
    }

    fun verifyMapModeOptionsDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithText("Default").assertIsDisplayed()
        composeTestRule.onNodeWithText("Security Risk").assertIsDisplayed()
        composeTestRule.onNodeWithText("Visa Requirements").assertIsDisplayed()
        composeTestRule.onNodeWithText("Passport Validity").assertIsDisplayed()
        composeTestRule.onNodeWithText("Yellow Fever").assertIsDisplayed()
        composeTestRule.onNodeWithText("Malaria").assertIsDisplayed()
        return this
    }

    // ==================== Search Bar Actions ====================

    fun typeInSearchBar(text: String): CountriesRobot {
        composeTestRule.onNodeWithTag("search_bar").performTextInput(text)
        composeTestRule.waitForIdle()
        return this
    }

    fun clearSearchBar(): CountriesRobot {
        composeTestRule.onNodeWithTag("search_bar").performTextClearance()
        composeTestRule.waitForIdle()
        return this
    }

    fun clickSearchClearButton(): CountriesRobot {
        composeTestRule.onNodeWithTag("search_clear_button").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    fun verifySearchClearButtonDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("search_clear_button").assertIsDisplayed()
        return this
    }

    fun verifySearchClearButtonNotDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("search_clear_button").assertDoesNotExist()
        return this
    }

    // ==================== Search Results ====================

    fun verifySearchResultsDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("search_results_dropdown").assertIsDisplayed()
        return this
    }

    fun verifySearchResultsNotDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("search_results_dropdown").assertDoesNotExist()
        return this
    }

    fun verifyCountryInSearchResults(countryName: String): CountriesRobot {
        // Territory matches whose parent country is countryName produce a subtitle node whose
        // text equals countryName exactly. Combined with the direct match's flag+name node that
        // contains countryName as a substring, there can be multiple nodes matching the query.
        // Use onAllNodesWithText + onFirst so the assertion passes as long as at least one
        // visible node contains the expected name, rather than requiring a unique match.
        composeTestRule.onAllNodesWithText(countryName, substring = true)
            .onFirst()
            .assertIsDisplayed()
        return this
    }

    fun selectSearchResult(countryId: String): CountriesRobot {
        composeTestRule.onNodeWithTag("search_result_$countryId").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    // ==================== Map Interactions ====================

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

    fun tapMapCenter(): CountriesRobot {
        composeTestRule.onNodeWithTag("world_map").performTouchInput {
            click(center)
        }
        composeTestRule.waitForIdle()
        return this
    }

    /**
     * Taps at a position on the map expressed as fractions of the map canvas size.
     * [normX] = 0.0 is the left edge, 1.0 is the right edge.
     * [normY] = 0.0 is the top edge, 1.0 is the bottom edge.
     *
     * Mercator reference points:
     *   Russia (54°E, 62°N) → normX ≈ 0.65, normY ≈ 0.27
     */
    fun tapMapAtRelative(normX: Float, normY: Float): CountriesRobot {
        composeTestRule.onNodeWithTag("world_map").performTouchInput {
            click(Offset(left + width * normX, top + height * normY))
        }
        composeTestRule.waitForIdle()
        return this
    }

    // ==================== Map Mode Selection ====================

    fun selectDefaultMapMode(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_default").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    fun selectSecurityRiskMapMode(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_security_risk").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    fun selectVisaRequirementsMapMode(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_visa_requirements").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    fun selectPassportValidityMapMode(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_passport_validity").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    fun selectYellowFeverMapMode(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_yellow_fever").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    fun selectMalariaMapMode(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_malaria").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    fun verifyDefaultMapModeSelected(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_radio_default").assertIsSelected()
        return this
    }

    fun verifySecurityRiskMapModeSelected(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_radio_security_risk").assertIsSelected()
        return this
    }

    fun verifyVisaRequirementsMapModeSelected(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_radio_visa_requirements").assertIsSelected()
        return this
    }

    fun verifyPassportValidityMapModeSelected(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_radio_passport_validity").assertIsSelected()
        return this
    }

    fun verifyYellowFeverMapModeSelected(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_radio_yellow_fever").assertIsSelected()
        return this
    }

    fun verifyMalariaMapModeSelected(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_mode_radio_malaria").assertIsSelected()
        return this
    }


    // ==================== Map Legend ====================

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

    fun verifyLegendDisplayed(): CountriesRobot {
        composeTestRule.waitUntil(timeoutMillis = 5000L) {
            composeTestRule.onAllNodesWithTag("map_legend").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("map_legend").assertIsDisplayed()
        return this
    }

    fun verifyLegendNotDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("map_legend").assertDoesNotExist()
        return this
    }

    fun verifyLegendCloseButtonDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("legend_close_button").assertIsDisplayed()
        return this
    }

    fun closeLegend(): CountriesRobot {
        composeTestRule.onNodeWithTag("legend_close_button").performClick()
        composeTestRule.waitForIdle()
        return this
    }

    fun verifySecurityRiskLegendContent(): CountriesRobot {
        composeTestRule.onNodeWithTag("legend_item_low_risk").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_medium_risk").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_high_risk").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_extreme_risk").assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.SafetyLevelDisplay.NORMAL_SECURITY_PRECAUTIONS).assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.SafetyLevelDisplay.HIGH_DEGREE_CAUTION).assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.SafetyLevelDisplay.RECONSIDER_TRAVEL).assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.SafetyLevelDisplay.DO_NOT_TRAVEL).assertIsDisplayed()
        return this
    }

    fun verifyVisaRequirementsLegendContent(): CountriesRobot {
        composeTestRule.onNodeWithTag("legend_item_visa_not_required").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_evisa").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_visa_on_arrival").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_visa_required").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_restricted").assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.VisaRequirementDisplay.VISA_NOT_REQUIRED).assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.VisaRequirementDisplay.E_VISA).assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.VisaRequirementDisplay.VISA_ON_ARRIVAL).assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.VisaRequirementDisplay.VISA_REQUIRED).assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.VisaRequirementDisplay.RESTRICTED).assertIsDisplayed()
        return this
    }

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

    fun verifyLegendTitleDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithText("Map Key").assertIsDisplayed()
        return this
    }

    // ==================== Instructions Text ====================

    fun verifyInstructionsTextDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithText("Pinch to zoom • Drag to pan").assertIsDisplayed()
        return this
    }

    fun verifyInstructionsTextNotDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithText("Pinch to zoom • Drag to pan").assertDoesNotExist()
        return this
    }

    // ==================== Search Result Item Content ====================

    fun verifySearchResultContinent(continent: String): CountriesRobot {
        composeTestRule.onNodeWithText(continent).assertIsDisplayed()
        return this
    }

    // ==================== Bottom Sheet Verification ====================

    fun verifyBottomSheetDisplayed(): CountriesRobot {
        composeTestRule.waitUntil(timeoutMillis = 5000L) {
            composeTestRule.onAllNodesWithTag("country_detail_sheet").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("country_detail_sheet").assertIsDisplayed()
        return this
    }

    fun verifyBottomSheetNotDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("country_detail_sheet").assertDoesNotExist()
        return this
    }

    fun verifyBottomSheetCountryName(countryName: String): CountriesRobot {
        composeTestRule.onNodeWithTag("country_name").assertTextEquals(countryName)
        return this
    }

    fun verifyCountryHeaderDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("country_header").assertIsDisplayed()
        return this
    }

    fun verifyCountryFlagDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("country_flag").assertIsDisplayed()
        return this
    }

    fun verifyCountryContinentDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("country_continent").assertIsDisplayed()
        return this
    }

    // ==================== Bottom Sheet Content Verification ====================

    fun verifySafetyLevelDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("info_safety_level").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Safety Level").assertIsDisplayed()
        return this
    }

    fun verifySafetyLevelValue(value: String): CountriesRobot {
        composeTestRule.onNodeWithTag("info_safety_level_value").assertTextEquals(value)
        return this
    }

    fun verifyEntryRequirementDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("info_entry_requirement").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Entry Requirement").assertIsDisplayed()
        return this
    }

    fun verifyPassportValidityDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("info_passport_validity").performScrollTo().assertIsDisplayed()
        // Note: We don't verify text "Passport Validity" separately as it also exists in the map mode selector
        return this
    }

    fun verifyPassportValidityValue(value: String): CountriesRobot {
        composeTestRule.onNodeWithTag("info_passport_validity_value").assertTextEquals(value)
        return this
    }
    fun verifyCurrencyInfoDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("info_currency").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Currency").assertIsDisplayed()
        return this
    }

    fun verifyPowerOutletDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("info_power_outlet").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Power Outlet").assertIsDisplayed()
        return this
    }

    fun verifyAllCountryInfoDisplayed(): CountriesRobot {
        verifySafetyLevelDisplayed()
        verifyEntryRequirementDisplayed()
        verifyPassportValidityDisplayed()
        verifyCurrencyInfoDisplayed()
        verifyPowerOutletDisplayed()
        return this
    }

    // ==================== Currency Converter ====================

    fun verifyCurrencyConverterDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("currency_converter").performScrollTo().assertIsDisplayed()
        return this
    }

    fun verifyCurrencyConverterNotDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithTag("currency_converter").assertDoesNotExist()
        return this
    }

    fun verifyUsdLabelDisplayed(): CountriesRobot {
        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
        return this
    }

    // ==================== Bottom Sheet Actions ====================

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

    fun verifyPageLoad(): CountriesRobot {
        verifyScreenDisplayed()
        verifySearchBarDisplayed()
        verifyWorldMapDisplayed()
        verifyMapViewSelectorDisplayed()
        return this
    }

    fun searchAndSelectCountry(searchText: String, countryId: String): CountriesRobot {
        typeInSearchBar(searchText)
        verifySearchResultsDisplayed()
        selectSearchResult(countryId)
        verifyBottomSheetDisplayed()
        return this
    }

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

    fun waitForIdle(): CountriesRobot {
        composeTestRule.waitForIdle()
        return this
    }

    fun waitForAnimation(): CountriesRobot {
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()
        return this
    }

    companion object {
        fun ComposeTestRule.countriesRobot(block: CountriesRobot.() -> Unit): CountriesRobot {
            return CountriesRobot(this).apply(block)
        }
    }
}
