package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.MainActivity
import com.unstampedpages.app.ui.screens.countryinfo.CountriesRobot.Companion.countriesRobot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountriesScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        // Navigate to Countries tab
        composeTestRule.onNodeWithText("Countries").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun countriesScreen_pageLoad_displaysSearchBar() {
        composeTestRule.countriesRobot {
            verifyScreenDisplayed()
            verifySearchBarDisplayed()
        }
    }

    @Test
    fun countriesScreen_pageLoad_displaysWorldMap() {
        composeTestRule.countriesRobot {
            verifyScreenDisplayed()
            verifyWorldMapDisplayed()
            verifyWorldMapContainerDisplayed()
        }
    }

    @Test
    fun countriesScreen_pageLoad_displaysMapViewSelector() {
        composeTestRule.countriesRobot {
            verifyScreenDisplayed()
            verifyMapViewSelectorDisplayed()
            verifyMapModeOptionsDisplayed()
        }
    }

    @Test
    fun countriesScreen_pageLoad_allMainComponentsDisplayed() {
        composeTestRule.countriesRobot {
            verifyPageLoad()
        }
    }

    @Test
    fun countriesScreen_panMapLeft_mapPansContinuously() {
        composeTestRule.countriesRobot {
            verifyWorldMapDisplayed()
            panMapLeft()
            verifyWorldMapDisplayed()
            panMapLeft()
            verifyWorldMapDisplayed()
        }
    }

    @Test
    fun countriesScreen_panMapRight_mapPansContinuously() {
        composeTestRule.countriesRobot {
            verifyWorldMapDisplayed()
            panMapRight()
            verifyWorldMapDisplayed()
            panMapRight()
            verifyWorldMapDisplayed()
        }
    }

    @Test
    fun countriesScreen_panMapContinuously_wrapsAround() {
        composeTestRule.countriesRobot {
            verifyWorldMapDisplayed()
            // Pan multiple times to test continuous wrapping
            repeat(5) {
                panMapLeft()
                waitForIdle()
            }
            verifyWorldMapDisplayed()
        }
    }

    @Test
    fun countriesScreen_zoomInMap_mapZoomsIn() {
        composeTestRule.countriesRobot {
            verifyWorldMapDisplayed()
            zoomInMap()
            verifyWorldMapDisplayed()
        }
    }

    @Test
    fun countriesScreen_zoomOutMap_mapZoomsOut() {
        composeTestRule.countriesRobot {
            verifyWorldMapDisplayed()
            zoomInMap()
            zoomOutMap()
            verifyWorldMapDisplayed()
        }
    }

    @Test
    fun countriesScreen_zoomIn_thenSelectCountry_retainsCountryInfo() {
        composeTestRule.countriesRobot {
            verifyWorldMapDisplayed()
            zoomInMap()
            // Search and select a country while zoomed
            typeInSearchBar("Japan")
            verifySearchResultsDisplayed()
            selectSearchResult("jp")
            verifyBottomSheetDisplayed()
            verifyAllCountryInfoDisplayed()
        }
    }

    @Test
    fun countriesScreen_selectCountry_bottomSheetLoads() {
        composeTestRule.countriesRobot {
            typeInSearchBar("France")
            verifySearchResultsDisplayed()
            selectSearchResult("fr")
            verifyBottomSheetDisplayed()
        }
    }

    @Test
    fun countriesScreen_selectCountry_displaysCountryHeader() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry("Germany", "de")
            verifyCountryHeaderDisplayed()
            verifyCountryFlagDisplayed()
            verifyBottomSheetCountryName("Germany")
            verifyCountryContinentDisplayed()
        }
    }

    @Test
    fun countriesScreen_selectCountry_displaysCurrencyInfo() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry("Japan", "jp")
            verifyCurrencyInfoDisplayed()
        }
    }

    @Test
    fun countriesScreen_selectCountry_displaysSafetyRisk() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry("Japan", "jp")
            verifySafetyLevelDisplayed()
        }
    }

    @Test
    fun countriesScreen_selectCountry_displaysEntryRequirement() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry("Japan", "jp")
            verifyEntryRequirementDisplayed()
        }
    }

    @Test
    fun countriesScreen_selectCountry_displaysPowerOutlet() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry("Japan", "jp")
            verifyPowerOutletDisplayed()
        }
    }

    @Test
    fun countriesScreen_selectCountry_displaysPassportValidity() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry("Japan", "jp")
            verifyPassportValidityDisplayed()
        }
    }

    @Test
    fun countriesScreen_japanPassportValidity_displaysPlannedStay() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry("Japan", "jp")
            verifyPassportValidityDisplayed()
            verifyPassportValidityValue("Planned length of stay")
        }
    }

    @Test
    fun countriesScreen_germanyPassportValidity_displays3Months() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry("Germany", "de")
            verifyPassportValidityDisplayed()
            verifyPassportValidityValue("3 months")
        }
    }

    @Test
    fun countriesScreen_canadaPassportValidity_displays6Months() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry("Canada", "ca")
            verifyPassportValidityDisplayed()
            verifyPassportValidityValue("6 months")
        }
    }

    @Test
    fun countriesScreen_selectCountry_displaysAllCountryInfo() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry("Japan", "jp")
            verifyAllCountryInfoDisplayed()
        }
    }

    @Test
    fun countriesScreen_closeBottomSheet_sheetCloses() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry("France", "fr")
            verifyBottomSheetDisplayed()
            closeBottomSheet()
            verifyBottomSheetNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_tapScrim_bottomSheetCloses() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry("France", "fr")
            verifyBottomSheetDisplayed()
            closeBottomSheetByTappingScrim()
            verifyBottomSheetNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_defaultMapModeSelected_byDefault() {
        composeTestRule.countriesRobot {
            verifyMapViewSelectorDisplayed()
            verifyDefaultMapModeSelected()
        }
    }

    @Test
    fun countriesScreen_selectSecurityRiskMode_modeChanges() {
        composeTestRule.countriesRobot {
            verifyMapViewSelectorDisplayed()
            selectSecurityRiskMapMode()
            verifySecurityRiskMapModeSelected()
        }
    }

    @Test
    fun countriesScreen_selectVisaRequirementsMode_modeChanges() {
        composeTestRule.countriesRobot {
            verifyMapViewSelectorDisplayed()
            selectVisaRequirementsMapMode()
            verifyVisaRequirementsMapModeSelected()
        }
    }

    @Test
    fun countriesScreen_selectPassportValidityMode_modeChanges() {
        composeTestRule.countriesRobot {
            verifyMapViewSelectorDisplayed()
            selectPassportValidityMapMode()
            verifyPassportValidityMapModeSelected()
        }
    }

    @Test
    fun countriesScreen_toggleBetweenModes_allModesWork() {
        composeTestRule.countriesRobot {
            verifyMapViewSelectorDisplayed()

            // Default -> Security Risk
            selectSecurityRiskMapMode()
            verifySecurityRiskMapModeSelected()
            waitForAnimation()

            // Security Risk -> Visa Requirements
            selectVisaRequirementsMapMode()
            verifyVisaRequirementsMapModeSelected()
            waitForAnimation()

            // Visa Requirements -> Passport Validity
            selectPassportValidityMapMode()
            verifyPassportValidityMapModeSelected()
            waitForAnimation()

            // Passport Validity -> Default
            selectDefaultMapMode()
            verifyDefaultMapModeSelected()
            waitForAnimation()
        }
    }

    @Test
    fun countriesScreen_toggleMapModes_withAnimation() {
        composeTestRule.countriesRobot {
            verifyMapViewSelectorDisplayed()

            selectSecurityRiskMapMode()
            waitForAnimation()
            verifySecurityRiskMapModeSelected()

            selectVisaRequirementsMapMode()
            waitForAnimation()
            verifyVisaRequirementsMapModeSelected()
        }
    }

    @Test
    fun countriesScreen_toggleMapModes_whileZoomedIn() {
        composeTestRule.countriesRobot {
            verifyWorldMapDisplayed()
            zoomInMap()

            selectSecurityRiskMapMode()
            waitForAnimation()
            verifySecurityRiskMapModeSelected()

            selectVisaRequirementsMapMode()
            waitForAnimation()
            verifyVisaRequirementsMapModeSelected()

            selectPassportValidityMapMode()
            waitForAnimation()
            verifyPassportValidityMapModeSelected()
        }
    }

    @Test
    fun countriesScreen_sudan_displaysCorrectInfoInSecurityRiskMode() {
        composeTestRule.countriesRobot {
            selectSecurityRiskMapMode()
            waitForAnimation()
            searchAndSelectCountry("Sudan", "sd")
            verifySafetyLevelDisplayed()
            verifyAllCountryInfoDisplayed()
        }
    }

    @Test
    fun countriesScreen_unitedStates_displaysCorrectInfoInSecurityRiskMode() {
        composeTestRule.countriesRobot {
            selectSecurityRiskMapMode()
            waitForAnimation()
            searchAndSelectCountry("United States", "us")
            verifySafetyLevelDisplayed()
            verifyAllCountryInfoDisplayed()
        }
    }

    @Test
    fun countriesScreen_sudan_displaysCorrectInfoInVisaRequirementsMode() {
        composeTestRule.countriesRobot {
            selectVisaRequirementsMapMode()
            waitForAnimation()
            searchAndSelectCountry("Sudan", "sd")
            verifyEntryRequirementDisplayed()
            verifyAllCountryInfoDisplayed()
        }
    }

    @Test
    fun countriesScreen_unitedStates_displaysCorrectInfoInVisaRequirementsMode() {
        composeTestRule.countriesRobot {
            selectVisaRequirementsMapMode()
            waitForAnimation()
            searchAndSelectCountry("United States", "us")
            verifyEntryRequirementDisplayed()
            verifyAllCountryInfoDisplayed()
        }
    }

    @Test
    fun countriesScreen_japan_displaysCorrectInfoInPassportValidityMode() {
        composeTestRule.countriesRobot {
            selectPassportValidityMapMode()
            waitForAnimation()
            searchAndSelectCountry("Japan", "jp")
            verifyAllCountryInfoDisplayed()
        }
    }

    @Test
    fun countriesScreen_germany_displaysCorrectInfoInPassportValidityMode() {
        composeTestRule.countriesRobot {
            selectPassportValidityMapMode()
            waitForAnimation()
            searchAndSelectCountry("Germany", "de")
            verifyAllCountryInfoDisplayed()
        }
    }

    @Test
    fun countriesScreen_usdCountry_doesNotShowCurrencyConverter() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry("United States", "us")
            verifyBottomSheetDisplayed()
            verifyCurrencyInfoDisplayed()
            verifyCurrencyConverterNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_nonUsdCountry_showsCurrencyConverter() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry("Japan", "jp")
            verifyBottomSheetDisplayed()
            verifyCurrencyInfoDisplayed()
            verifyCurrencyConverterDisplayed()
            verifyUsdLabelDisplayed()
        }
    }

    @Test
    fun countriesScreen_typeInSearchBar_showsResults() {
        composeTestRule.countriesRobot {
            typeInSearchBar("Jap")
            verifySearchResultsDisplayed()
            verifyCountryInSearchResults("Japan")
        }
    }

    @Test
    fun countriesScreen_searchForCountry_resultsContainInput() {
        composeTestRule.countriesRobot {
            typeInSearchBar("United")
            verifySearchResultsDisplayed()
            verifyCountryInSearchResults("United States")
            verifyCountryInSearchResults("United Kingdom")
        }
    }

    @Test
    fun countriesScreen_selectSearchResult_loadsCorrectCountryInfo() {
        composeTestRule.countriesRobot {
            verifyCountryInfoFlow("Japan", "jp", "Japan")
        }
    }

    @Test
    fun countriesScreen_selectSearchResult_displaysCountryName() {
        composeTestRule.countriesRobot {
            typeInSearchBar("France")
            verifySearchResultsDisplayed()
            selectSearchResult("fr")
            verifyBottomSheetDisplayed()
            verifyBottomSheetCountryName("France")
        }
    }

    @Test
    fun countriesScreen_typeInSearchBar_showsClearButton() {
        composeTestRule.countriesRobot {
            typeInSearchBar("Test")
            verifySearchClearButtonDisplayed()
        }
    }

    @Test
    fun countriesScreen_clickClearButton_clearsSearchBar() {
        composeTestRule.countriesRobot {
            typeInSearchBar("Japan")
            verifySearchClearButtonDisplayed()
            clickSearchClearButton()
            verifySearchClearButtonNotDisplayed()
            verifySearchResultsNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_emptySearchBar_noClearButton() {
        composeTestRule.countriesRobot {
            verifySearchBarDisplayed()
            verifySearchClearButtonNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_completeUserFlow() {
        composeTestRule.countriesRobot {
            // Verify page load
            verifyPageLoad()

            // Search for a country
            typeInSearchBar("Germany")
            verifySearchResultsDisplayed()

            // Select the country
            selectSearchResult("de")
            verifyBottomSheetDisplayed()

            // Verify all country info
            verifyAllCountryInfoDisplayed()
            verifyCurrencyConverterDisplayed()

            // Close the bottom sheet
            closeBottomSheet()
            verifyBottomSheetNotDisplayed()

            // Change map mode
            selectSecurityRiskMapMode()
            verifySecurityRiskMapModeSelected()

            // Pan and zoom the map
            panMapLeft()
            zoomInMap()

            // Search for another country
            typeInSearchBar("Sudan")
            verifySearchResultsDisplayed()
            selectSearchResult("sd")
            verifyBottomSheetDisplayed()
            verifyAllCountryInfoDisplayed()
        }
    }

    // ==================== Map Legend Tests ====================

    @Test
    fun countriesScreen_defaultMode_tapCompass_legendNotDisplayed() {
        composeTestRule.countriesRobot {
            verifyPageLoad()
            verifyDefaultMapModeSelected()
            tapCompassIcon()
            verifyLegendNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_securityRiskMode_tapCompass_legendDisplayed() {
        composeTestRule.countriesRobot {
            verifyPageLoad()
            selectSecurityRiskMapMode()
            waitForAnimation()
            tapCompassIcon()
            verifyLegendDisplayed()
            verifyLegendTitleDisplayed()
            verifyLegendCloseButtonDisplayed()
        }
    }

    @Test
    fun countriesScreen_securityRiskMode_legendShowsCorrectContent() {
        composeTestRule.countriesRobot {
            selectSecurityRiskMapMode()
            waitForAnimation()
            tapCompassIcon()
            verifyLegendDisplayed()
            verifySecurityRiskLegendContent()
        }
    }

    @Test
    fun countriesScreen_visaRequirementsMode_tapCompass_legendDisplayed() {
        composeTestRule.countriesRobot {
            verifyPageLoad()
            selectVisaRequirementsMapMode()
            waitForAnimation()
            tapCompassIcon()
            verifyLegendDisplayed()
            verifyLegendTitleDisplayed()
            verifyLegendCloseButtonDisplayed()
        }
    }

    @Test
    fun countriesScreen_visaRequirementsMode_legendShowsCorrectContent() {
        composeTestRule.countriesRobot {
            selectVisaRequirementsMapMode()
            waitForAnimation()
            tapCompassIcon()
            verifyLegendDisplayed()
            verifyVisaRequirementsLegendContent()
        }
    }

    @Test
    fun countriesScreen_passportValidityMode_tapCompass_legendDisplayed() {
        composeTestRule.countriesRobot {
            verifyPageLoad()
            selectPassportValidityMapMode()
            waitForAnimation()
            tapCompassIcon()
            verifyLegendDisplayed()
            verifyLegendTitleDisplayed()
            verifyLegendCloseButtonDisplayed()
        }
    }

    @Test
    fun countriesScreen_passportValidityMode_legendShowsCorrectContent() {
        composeTestRule.countriesRobot {
            selectPassportValidityMapMode()
            waitForAnimation()
            tapCompassIcon()
            verifyLegendDisplayed()
            verifyPassportValidityLegendContent()
        }
    }

    @Test
    fun countriesScreen_closeLegend_legendCloses() {
        composeTestRule.countriesRobot {
            selectSecurityRiskMapMode()
            waitForAnimation()
            tapCompassIcon()
            verifyLegendDisplayed()
            closeLegend()
            waitForAnimation()
            verifyLegendNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_legendOpenClose_multipleTimesWorks() {
        composeTestRule.countriesRobot {
            selectSecurityRiskMapMode()
            waitForAnimation()

            // Open and close multiple times
            tapCompassIcon()
            verifyLegendDisplayed()
            closeLegend()
            waitForAnimation()
            verifyLegendNotDisplayed()

            tapCompassIcon()
            verifyLegendDisplayed()
            closeLegend()
            waitForAnimation()
            verifyLegendNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_switchToDefaultMode_legendClosesAutomatically() {
        composeTestRule.countriesRobot {
            selectSecurityRiskMapMode()
            waitForAnimation()
            tapCompassIcon()
            verifyLegendDisplayed()

            // Switch to default mode - legend should close
            selectDefaultMapMode()
            waitForAnimation()
            verifyLegendNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_switchFromDefaultToNonDefault_legendReopens() {
        composeTestRule.countriesRobot {
            // Enable legend on Security Risk mode
            selectSecurityRiskMapMode()
            waitForAnimation()
            tapCompassIcon()
            verifyLegendDisplayed()

            // Switch to default mode - legend should close
            selectDefaultMapMode()
            waitForAnimation()
            verifyLegendNotDisplayed()

            // Switch back to non-default mode - legend should reopen
            selectVisaRequirementsMapMode()
            waitForAnimation()
            verifyLegendDisplayed()
            verifyVisaRequirementsLegendContent()
        }
    }

    @Test
    fun countriesScreen_legendDisabled_switchingModesDoesNotShowLegend() {
        composeTestRule.countriesRobot {
            // Don't tap compass - legend is disabled
            selectSecurityRiskMapMode()
            waitForAnimation()
            verifyLegendNotDisplayed()

            selectVisaRequirementsMapMode()
            waitForAnimation()
            verifyLegendNotDisplayed()

            selectPassportValidityMapMode()
            waitForAnimation()
            verifyLegendNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_switchBetweenNonDefaultModes_legendStaysOpen() {
        composeTestRule.countriesRobot {
            selectSecurityRiskMapMode()
            waitForAnimation()
            tapCompassIcon()
            verifyLegendDisplayed()
            verifySecurityRiskLegendContent()

            // Switch to visa requirements mode - legend should update
            selectVisaRequirementsMapMode()
            waitForAnimation()
            verifyLegendDisplayed()
            verifyVisaRequirementsLegendContent()
        }
    }

    @Test
    fun countriesScreen_zoomWhileLegendOpen_legendRemainsDisplayed() {
        composeTestRule.countriesRobot {
            selectSecurityRiskMapMode()
            waitForAnimation()
            tapCompassIcon()
            verifyLegendDisplayed()

            // Zoom the map
            zoomInMap()
            verifyLegendDisplayed()
        }
    }

    // ==================== Instructions Text Visibility Tests ====================

    @Test
    fun countriesScreen_pageLoad_instructionsTextDisplayed() {
        composeTestRule.countriesRobot {
            verifyPageLoad()
            verifyInstructionsTextDisplayed()
        }
    }

    @Test
    fun countriesScreen_bottomSheetOpen_instructionsTextHidden() {
        composeTestRule.countriesRobot {
            verifyInstructionsTextDisplayed()
            searchAndSelectCountry("Japan", "jp")
            verifyBottomSheetDisplayed()
            verifyInstructionsTextNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_bottomSheetClosed_instructionsTextReappears() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry("Japan", "jp")
            verifyBottomSheetDisplayed()
            verifyInstructionsTextNotDisplayed()
            closeBottomSheet()
            verifyBottomSheetNotDisplayed()
            verifyInstructionsTextDisplayed()
        }
    }

    @Test
    fun countriesScreen_searchResultsShowing_instructionsTextHidden() {
        composeTestRule.countriesRobot {
            verifyInstructionsTextDisplayed()
            typeInSearchBar("Japan")
            verifySearchResultsDisplayed()
            verifyInstructionsTextNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_searchResultsCleared_instructionsTextReappears() {
        composeTestRule.countriesRobot {
            typeInSearchBar("Japan")
            verifySearchResultsDisplayed()
            verifyInstructionsTextNotDisplayed()
            clickSearchClearButton()
            verifySearchResultsNotDisplayed()
            verifyInstructionsTextDisplayed()
        }
    }

    @Test
    fun countriesScreen_legendShowing_instructionsTextHidden() {
        composeTestRule.countriesRobot {
            verifyInstructionsTextDisplayed()
            selectSecurityRiskMapMode()
            waitForAnimation()
            tapCompassIcon()
            verifyLegendDisplayed()
            verifyInstructionsTextNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_legendClosed_instructionsTextReappears() {
        composeTestRule.countriesRobot {
            selectSecurityRiskMapMode()
            waitForAnimation()
            tapCompassIcon()
            verifyLegendDisplayed()
            verifyInstructionsTextNotDisplayed()
            closeLegend()
            waitForAnimation()
            verifyLegendNotDisplayed()
            verifyInstructionsTextDisplayed()
        }
    }

    // ==================== Search Result Item Content Tests ====================

    @Test
    fun countriesScreen_searchResults_showCountryWithContinent() {
        composeTestRule.countriesRobot {
            typeInSearchBar("Jap")
            verifySearchResultsDisplayed()
            verifyCountryInSearchResults("Japan")
            verifySearchResultContinent("Asia")
        }
    }

    @Test
    fun countriesScreen_searchResults_showMultipleCountriesWithContinents() {
        composeTestRule.countriesRobot {
            typeInSearchBar("United")
            verifySearchResultsDisplayed()
            verifyCountryInSearchResults("United States")
            verifyCountryInSearchResults("United Kingdom")
            verifySearchResultContinent("North America")
            verifySearchResultContinent("Europe")
        }
    }

    // ==================== Map Mode Radio Button Direct Click Tests ====================

    @Test
    fun countriesScreen_clickSecurityRiskRadioButton_modeChanges() {
        composeTestRule.countriesRobot {
            verifyDefaultMapModeSelected()
        }
        // Click the radio button directly
        composeTestRule.onNodeWithTag("map_mode_radio_security_risk").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.countriesRobot {
            verifySecurityRiskMapModeSelected()
        }
    }

    @Test
    fun countriesScreen_clickVisaRequirementsRadioButton_modeChanges() {
        composeTestRule.countriesRobot {
            verifyDefaultMapModeSelected()
        }
        composeTestRule.onNodeWithTag("map_mode_radio_visa_requirements").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.countriesRobot {
            verifyVisaRequirementsMapModeSelected()
        }
    }

    @Test
    fun countriesScreen_clickPassportValidityRadioButton_modeChanges() {
        composeTestRule.countriesRobot {
            verifyDefaultMapModeSelected()
        }
        composeTestRule.onNodeWithTag("map_mode_radio_passport_validity").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.countriesRobot {
            verifyPassportValidityMapModeSelected()
        }
    }

    @Test
    fun countriesScreen_clickDefaultRadioButton_modeChanges() {
        composeTestRule.countriesRobot {
            selectSecurityRiskMapMode()
            verifySecurityRiskMapModeSelected()
        }
        composeTestRule.onNodeWithTag("map_mode_radio_default").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.countriesRobot {
            verifyDefaultMapModeSelected()
        }
    }
}
