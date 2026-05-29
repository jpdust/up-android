package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.MainActivity
import com.unstampedpages.app.data.AppConstants
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
    fun countriesScreen_pageLoad() {
        composeTestRule.countriesRobot {
            verifyScreenDisplayed()
            verifySearchBarDisplayed()
            verifyWorldMapDisplayed()
            verifyWorldMapContainerDisplayed()
            verifyMapViewSelectorDisplayed()
            verifyMapModeOptionsDisplayed()
        }
    }

    @Test
    fun countriesScreen_panMap_mapPansContinuously() {
        composeTestRule.countriesRobot {
            verifyWorldMapDisplayed()
            panMapLeft()
            verifyWorldMapDisplayed()
            panMapLeft()
            verifyWorldMapDisplayed()
            panMapRight()
            verifyWorldMapDisplayed()
            panMapRight()
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
    fun countriesScreen_zoomInMap_mapZooms() {
        composeTestRule.countriesRobot {
            verifyWorldMapDisplayed()
            zoomInMap()
            verifyWorldMapDisplayed()
            zoomInMap()
            verifyWorldMapDisplayed()
            zoomOutMap()
            verifyWorldMapDisplayed()
        }
    }

    @Test
    fun countriesScreen_Zoom_thenSelectCountry_retainsCountryInfo() {
        composeTestRule.countriesRobot {
            verifyWorldMapDisplayed()
            zoomInMap()
            // Search and select a country while zoomed
            typeInSearchBar(AppConstants.CountryName.JAPAN)
            verifySearchResultsDisplayed()
            selectSearchResult("jp")
            verifyBottomSheetDisplayed()
            verifyBottomSheetCountryName(AppConstants.CountryName.JAPAN)
            verifyAllCountryInfoDisplayed()
        }
    }

    @Test
    fun countriesScreen_selectCountry_bottomSheetLoads() {
        composeTestRule.countriesRobot {
            typeInSearchBar(AppConstants.CountryName.FRANCE)
            verifySearchResultsDisplayed()
            selectSearchResult("fr")
            verifyBottomSheetDisplayed()
            verifyBottomSheetCountryName(AppConstants.CountryName.FRANCE)
        }
    }

    @Test
    fun countriesScreen_selectCountry_displaysCountryHeader() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry(AppConstants.CountryName.GERMANY, AppConstants.CountryCode.GERMANY)
            verifyCountryHeaderDisplayed()
            verifyCountryFlagDisplayed()
            verifyBottomSheetCountryName(AppConstants.CountryName.GERMANY)
            verifyCountryContinentDisplayed()
            verifyCurrencyInfoDisplayed()
            verifySafetyLevelDisplayed()
            verifyEntryRequirementDisplayed()
            verifyPowerOutletDisplayed()
            verifyPassportValidityDisplayed()
        }
    }

    @Test
    fun countriesScreen_japanPassportValidity_displaysPlannedStay() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry(AppConstants.CountryName.JAPAN, AppConstants.CountryCode.JAPAN)
            verifyPassportValidityDisplayed()
            verifyPassportValidityValue(AppConstants.PassportValidity.PLANNED_STAY)
        }
    }

    @Test
    fun countriesScreen_germanyPassportValidity_displays3Months() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry(AppConstants.CountryName.GERMANY, AppConstants.CountryCode.GERMANY)
            verifyPassportValidityDisplayed()
            verifyPassportValidityValue(AppConstants.PassportValidity.THREE_MONTHS)
        }
    }

    @Test
    fun countriesScreen_canadaPassportValidity_displays6Months() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry(AppConstants.CountryName.CANADA, AppConstants.CountryCode.CANADA)
            verifyPassportValidityDisplayed()
            verifyPassportValidityValue(AppConstants.PassportValidity.SIX_MONTHS)
        }
    }

    @Test
    fun countriesScreen_selectCountry_displaysAllCountryInfo() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry(AppConstants.CountryName.JAPAN, AppConstants.CountryCode.JAPAN)
            verifyAllCountryInfoDisplayed()
        }
    }

    @Test
    fun countriesScreen_closeBottomSheet_sheetCloses() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry(AppConstants.CountryName.FRANCE, AppConstants.CountryCode.FRANCE)
            verifyBottomSheetDisplayed()
            closeBottomSheet()
            verifyBottomSheetNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_tapScrim_bottomSheetCloses() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry(AppConstants.CountryName.FRANCE, AppConstants.CountryCode.FRANCE)
            verifyBottomSheetDisplayed()
            closeBottomSheetByTappingScrim()
            verifyBottomSheetNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_selectMapMode_modeChanges() {
        composeTestRule.countriesRobot {
            verifyMapViewSelectorDisplayed()
            verifyDefaultMapModeSelected()
            selectSecurityRiskMapMode()
            verifySecurityRiskMapModeSelected()
            selectVisaRequirementsMapMode()
            verifyVisaRequirementsMapModeSelected()
            selectPassportValidityMapMode()
            verifyPassportValidityMapModeSelected()

            selectYellowFeverMapMode()
            verifyYellowFeverMapModeSelected()
            selectMalariaMapMode()
            verifyMalariaMapModeSelected()

            selectDefaultMapMode()
            verifyDefaultMapModeSelected()
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
            searchAndSelectCountry(AppConstants.CountryName.SUDAN, AppConstants.CountryCode.SUDAN)
            verifySafetyLevelDisplayed()
            verifyAllCountryInfoDisplayed()
        }
    }

    @Test
    fun countriesScreen_unitedStates_displaysCorrectInfoInSecurityRiskMode() {
        composeTestRule.countriesRobot {
            selectSecurityRiskMapMode()
            waitForAnimation()
            searchAndSelectCountry(AppConstants.CountryName.UNITED_STATES, AppConstants.CountryCode.UNITED_STATES)
            verifyBottomSheetDisplayed()
            verifyCurrencyInfoDisplayed()
            verifySafetyLevelValue(AppConstants.SafetyLevelDisplay.NORMAL_SECURITY_PRECAUTIONS)
            verifyAllCountryInfoDisplayed()
            verifyCurrencyConverterNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_unitedStates_displaysCorrectInfoInVisaRequirementsMode() {
        composeTestRule.countriesRobot {
            selectVisaRequirementsMapMode()
            waitForAnimation()
            searchAndSelectCountry(AppConstants.CountryName.UNITED_STATES, AppConstants.CountryCode.UNITED_STATES)
            verifyEntryRequirementDisplayed()
            verifySafetyLevelValue(AppConstants.SafetyLevelDisplay.NORMAL_SECURITY_PRECAUTIONS)
            verifyAllCountryInfoDisplayed()
        }
    }

    @Test
    fun countriesScreen_sudan_displaysCorrectInfoInVisaRequirementsMode() {
        composeTestRule.countriesRobot {
            selectVisaRequirementsMapMode()
            waitForAnimation()
            searchAndSelectCountry(AppConstants.CountryName.SUDAN, AppConstants.CountryCode.SUDAN)
            verifyEntryRequirementDisplayed()
            verifySafetyLevelValue(AppConstants.SafetyLevelDisplay.DO_NOT_TRAVEL)
            verifyAllCountryInfoDisplayed()
        }
    }

    @Test
    fun countriesScreen_japan_displaysCorrectInfoInPassportValidityMode() {
        composeTestRule.countriesRobot {
            selectPassportValidityMapMode()
            waitForAnimation()
            searchAndSelectCountry(AppConstants.CountryName.JAPAN, AppConstants.CountryCode.JAPAN)
            verifyAllCountryInfoDisplayed()
        }
    }

    @Test
    fun countriesScreen_germany_displaysCorrectInfoInPassportValidityMode() {
        composeTestRule.countriesRobot {
            selectPassportValidityMapMode()
            waitForAnimation()
            searchAndSelectCountry(AppConstants.CountryName.GERMANY, AppConstants.CountryCode.GERMANY)
            verifyAllCountryInfoDisplayed()
        }
    }

    @Test
    fun countriesScreen_nonUsdCountry_showsCurrencyConverter() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry(AppConstants.CountryName.JAPAN, AppConstants.CountryCode.JAPAN)
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
            verifyCountryInSearchResults(AppConstants.CountryName.JAPAN)
        }
    }

    @Test
    fun countriesScreen_searchForCountry_resultsContainInput() {
        composeTestRule.countriesRobot {
            typeInSearchBar("United")
            verifySearchResultsDisplayed()
            verifyCountryInSearchResults(AppConstants.CountryName.UNITED_STATES)
            verifyCountryInSearchResults(AppConstants.CountryName.UNITED_KINGDOM)
        }
    }

    @Test
    fun countriesScreen_selectSearchResult_loadsCorrectCountryInfo() {
        composeTestRule.countriesRobot {
            verifyCountryInfoFlow(AppConstants.CountryName.JAPAN, AppConstants.CountryCode.JAPAN, AppConstants.CountryName.JAPAN)
        }
    }

    @Test
    fun countriesScreen_selectSearchResult_displaysCountryName() {
        composeTestRule.countriesRobot {
            typeInSearchBar(AppConstants.CountryName.FRANCE)
            verifySearchResultsDisplayed()
            selectSearchResult(AppConstants.CountryCode.FRANCE)
            verifyBottomSheetDisplayed()
            verifyBottomSheetCountryName(AppConstants.CountryName.FRANCE)
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
            typeInSearchBar(AppConstants.CountryName.JAPAN)
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
            typeInSearchBar(AppConstants.CountryName.GERMANY)
            verifySearchResultsDisplayed()

            // Select the country
            selectSearchResult(AppConstants.CountryCode.GERMANY)
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
            typeInSearchBar(AppConstants.CountryName.SUDAN)
            verifySearchResultsDisplayed()
            selectSearchResult(AppConstants.CountryCode.SUDAN)
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
            searchAndSelectCountry(AppConstants.CountryName.JAPAN, AppConstants.CountryCode.JAPAN)
            verifyBottomSheetDisplayed()
            verifyInstructionsTextNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_bottomSheetClosed_instructionsTextReappears() {
        composeTestRule.countriesRobot {
            searchAndSelectCountry(AppConstants.CountryName.JAPAN, AppConstants.CountryCode.JAPAN)
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
            typeInSearchBar(AppConstants.CountryName.JAPAN)
            verifySearchResultsDisplayed()
            verifyInstructionsTextNotDisplayed()
        }
    }

    @Test
    fun countriesScreen_searchResultsCleared_instructionsTextReappears() {
        composeTestRule.countriesRobot {
            typeInSearchBar(AppConstants.CountryName.JAPAN)
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
            verifyCountryInSearchResults(AppConstants.CountryName.JAPAN)
            verifySearchResultContinent(AppConstants.ContinentDisplay.ASIA)
        }
    }

    @Test
    fun countriesScreen_searchResults_showMultipleCountriesWithContinents() {
        composeTestRule.countriesRobot {
            typeInSearchBar("United")
            verifySearchResultsDisplayed()
            verifyCountryInSearchResults(AppConstants.CountryName.UNITED_STATES)
            verifyCountryInSearchResults(AppConstants.CountryName.UNITED_KINGDOM)
            verifySearchResultContinent(AppConstants.ContinentDisplay.NORTH_AMERICA)
            verifySearchResultContinent(AppConstants.ContinentDisplay.EUROPE)
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

    // ==================== Direct Map Tap Tests ====================

    /**
     * Taps directly on the map canvas at the approximate Mercator coordinates for Russia
     * (54°E longitude, 62°N latitude → normX ≈ 0.65, normY ≈ 0.27).
     *
     * This exercises the `geoJsonId != null` branch of `hitTestCountry` in WorldMapCanvas.kt,
     * which is only reachable via a direct canvas tap — not via the search-bar flow.
     */
    @Test
    fun countriesScreen_tapMapPolygon_opensBottomSheet() {
        composeTestRule.countriesRobot {
            verifyWorldMapDisplayed()
            tapMapAtRelative(0.65f, 0.27f)
            verifyBottomSheetDisplayed()
        }
    }
}
