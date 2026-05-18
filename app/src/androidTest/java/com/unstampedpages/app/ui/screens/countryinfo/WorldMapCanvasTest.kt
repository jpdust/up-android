package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.AppConstants
import com.unstampedpages.app.ui.theme.UnstampedPagesTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorldMapCanvasTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ==================== Legend Visibility Tests ====================

    @Test
    fun worldMapCanvas_legendNotShown_whenShowLegendFalse() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.SECURITY_RISK,
                    legendConfig = MapLegendConfig(showLegend = false),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("map_legend").assertDoesNotExist()
    }

    @Test
    fun worldMapCanvas_legendShown_whenShowLegendTrueAndNonDefaultMode() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.SECURITY_RISK,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("map_legend").assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_legendNotShown_whenShowLegendTrueButDefaultMode() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.DEFAULT,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("map_legend").assertDoesNotExist()
    }

    // ==================== Security Risk Legend Content Tests ====================

    @Test
    fun worldMapCanvas_securityRiskLegend_showsNormalSecurityPrecautions() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.SECURITY_RISK,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_item_low_risk").assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.SafetyLevelDisplay.NORMAL_SECURITY_PRECAUTIONS).assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_securityRiskLegend_showsHighDegreeOfCaution() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.SECURITY_RISK,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_item_medium_risk").assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.SafetyLevelDisplay.HIGH_DEGREE_CAUTION).assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_securityRiskLegend_showsReconsiderTravel() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.SECURITY_RISK,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_item_high_risk").assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.SafetyLevelDisplay.RECONSIDER_TRAVEL).assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_securityRiskLegend_showsDoNotTravel() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.SECURITY_RISK,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_item_extreme_risk").assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.SafetyLevelDisplay.DO_NOT_TRAVEL).assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_securityRiskLegend_showsAllFourItems() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.SECURITY_RISK,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_item_low_risk").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_medium_risk").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_high_risk").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_extreme_risk").assertIsDisplayed()
    }

    // ==================== Visa Requirements Legend Content Tests ====================

    @Test
    fun worldMapCanvas_visaRequirementsLegend_showsVisaNotRequired() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.VISA_REQUIREMENTS,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_item_visa_not_required").assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.VisaRequirementDisplay.VISA_NOT_REQUIRED).assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_visaRequirementsLegend_showsEvisa() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.VISA_REQUIREMENTS,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_item_evisa").assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.VisaRequirementDisplay.E_VISA).assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_visaRequirementsLegend_showsVisaOnArrival() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.VISA_REQUIREMENTS,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_item_visa_on_arrival").assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.VisaRequirementDisplay.VISA_ON_ARRIVAL).assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_visaRequirementsLegend_showsVisaRequired() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.VISA_REQUIREMENTS,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_item_visa_required").assertIsDisplayed()
        composeTestRule.onNodeWithText(AppConstants.VisaRequirementDisplay.VISA_REQUIRED).assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_visaRequirementsLegend_showsRestricted() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.VISA_REQUIREMENTS,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_item_restricted").assertIsDisplayed()
        composeTestRule.onNodeWithText("Restricted").assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_visaRequirementsLegend_showsAllFiveItems() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.VISA_REQUIREMENTS,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_item_visa_not_required").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_evisa").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_visa_on_arrival").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_visa_required").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_restricted").assertIsDisplayed()
    }

    // ==================== Passport Validity Legend Content Tests ====================

    @Test
    fun worldMapCanvas_passportValidityLegend_shows6Months() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.PASSPORT_VALIDITY,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_item_6_months").assertIsDisplayed()
        composeTestRule.onNodeWithText("6 Months").assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_passportValidityLegend_shows3Months() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.PASSPORT_VALIDITY,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_item_3_months").assertIsDisplayed()
        composeTestRule.onNodeWithText("3 Months").assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_passportValidityLegend_showsDurationOfStay() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.PASSPORT_VALIDITY,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_item_duration_of_stay").assertIsDisplayed()
        composeTestRule.onNodeWithText("Duration of Stay").assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_passportValidityLegend_showsOther() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.PASSPORT_VALIDITY,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_item_other").assertIsDisplayed()
        composeTestRule.onNodeWithText("Other").assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_passportValidityLegend_showsAllFourItems() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.PASSPORT_VALIDITY,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_item_6_months").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_3_months").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_duration_of_stay").assertIsDisplayed()
        composeTestRule.onNodeWithTag("legend_item_other").assertIsDisplayed()
    }

    // ==================== Legend UI Elements Tests ====================

    @Test
    fun worldMapCanvas_legend_showsMapKeyTitle() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.SECURITY_RISK,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithText("Map Key").assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_legend_showsCloseButton() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.SECURITY_RISK,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_close_button").assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_legend_closeButtonCallsOnLegendClose() {
        var closeCalled = false

        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.SECURITY_RISK,
                    legendConfig = MapLegendConfig(
                        showLegend = true,
                        onLegendClose = { closeCalled = true }
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        composeTestRule.onNodeWithTag("legend_close_button").performClick()

        assert(closeCalled) { "onLegendClose should be called when close button is clicked" }
    }

    // ==================== Legend Content Changes Tests ====================

    @Test
    fun worldMapCanvas_securityRiskMode_doesNotShowVisaItems() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.SECURITY_RISK,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Should show security risk items
        composeTestRule.onNodeWithTag("legend_item_low_risk").assertIsDisplayed()

        // Should NOT show visa items
        composeTestRule.onNodeWithTag("legend_item_visa_not_required").assertDoesNotExist()
        composeTestRule.onNodeWithTag("legend_item_evisa").assertDoesNotExist()
    }

    @Test
    fun worldMapCanvas_visaMode_doesNotShowSecurityItems() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.VISA_REQUIREMENTS,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Should show visa items
        composeTestRule.onNodeWithTag("legend_item_visa_not_required").assertIsDisplayed()

        // Should NOT show security risk items
        composeTestRule.onNodeWithTag("legend_item_low_risk").assertDoesNotExist()
        composeTestRule.onNodeWithTag("legend_item_medium_risk").assertDoesNotExist()
    }

    @Test
    fun worldMapCanvas_passportValidityMode_doesNotShowOtherItems() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.PASSPORT_VALIDITY,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Should show passport validity items
        composeTestRule.onNodeWithTag("legend_item_6_months").assertIsDisplayed()

        // Should NOT show other mode items
        composeTestRule.onNodeWithTag("legend_item_low_risk").assertDoesNotExist()
        composeTestRule.onNodeWithTag("legend_item_visa_not_required").assertDoesNotExist()
    }

    // ==================== Map Display Tests ====================

    @Test
    fun worldMapCanvas_displaysWorldMap() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // The canvas should be displayed (it fills the modifier)
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun worldMapCanvas_defaultMode_noLegendItems() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = {},
                    colorMode = MapColorMode.DEFAULT,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Legend should not be displayed in default mode
        composeTestRule.onNodeWithTag("map_legend").assertDoesNotExist()
    }
}
