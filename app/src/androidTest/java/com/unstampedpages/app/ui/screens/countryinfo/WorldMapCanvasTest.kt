package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
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
                    onCountryTapped = { _, _ -> },
                    colorMode = MapColorMode.DEFAULT,
                    legendConfig = MapLegendConfig(showLegend = true),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Map canvas itself is still rendered in default mode
        composeTestRule.onRoot().assertIsDisplayed()
        // No legend container and no individual legend items from any mode
        composeTestRule.onNodeWithTag("map_legend").assertDoesNotExist()
        composeTestRule.onNodeWithTag("legend_item_low_risk").assertDoesNotExist()
        composeTestRule.onNodeWithTag("legend_item_visa_not_required").assertDoesNotExist()
        composeTestRule.onNodeWithTag("legend_item_6_months").assertDoesNotExist()
    }

    // ==================== drawCountryMercator — drawPath and drawCircle branches ====================
    // drawCountryMercator has four branches gated on (isSmall, isSelected):
    //   isSmall=false, isSelected=false → 2× drawPath  (fill + stroke)
    //   isSmall=false, isSelected=true  → 3× drawPath  (fill + stroke + glow)
    //   isSmall=true,  isSelected=false → 2× drawCircle (fill dot + stroke ring)
    //   isSmall=true,  isSelected=true  → 4× drawCircle (outer glow + inner glow + fill + stroke)
    //
    // Tests call drawCountryMercator directly via a minimal Canvas composable with synthetic Path
    // and CountryRenderStyle objects. No WorldMapCanvas, no CountryGeometryData, no GeoJSON parse —
    // so these tests add negligible heap pressure and cannot cause OOM in later test classes.

    private fun testRenderStyle() = CountryRenderStyle(
        normalStroke   = Stroke(width = 1f),
        selectedStroke = Stroke(width = 2f),
        glowStyle      = Stroke(width = 4f),
        dotRadius      = 4f
    )

    private fun trianglePath() = Path().apply {
        moveTo(10f, 10f); lineTo(60f, 10f); lineTo(35f, 50f); close()
    }

    @Test
    fun drawCountryMercator_drawPath_unselected_rendersWithoutCrash() {
        // isSmall=false, isSelected=false → 2× drawPath (fill + stroke)
        composeTestRule.setContent {
            UnstampedPagesTheme {
                Canvas(modifier = Modifier.size(100.dp)) {
                    drawCountryMercator(
                        path       = trianglePath(),
                        isSelected = false,
                        fillColor  = Color.Green,
                        renderStyle = testRenderStyle(),
                        isSmall    = false,
                        centroid   = Offset.Zero
                    )
                }
            }
        }
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun drawCountryMercator_drawPath_selected_rendersWithoutCrash() {
        // isSmall=false, isSelected=true → 3× drawPath (fill + stroke + glow)
        composeTestRule.setContent {
            UnstampedPagesTheme {
                Canvas(modifier = Modifier.size(100.dp)) {
                    drawCountryMercator(
                        path       = trianglePath(),
                        isSelected = true,
                        fillColor  = Color.Blue,
                        renderStyle = testRenderStyle(),
                        isSmall    = false,
                        centroid   = Offset.Zero
                    )
                }
            }
        }
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun drawCountryMercator_drawCircle_unselected_rendersWithoutCrash() {
        // isSmall=true, isSelected=false → 2× drawCircle (fill dot + stroke ring)
        composeTestRule.setContent {
            UnstampedPagesTheme {
                Canvas(modifier = Modifier.size(100.dp)) {
                    drawCountryMercator(
                        path       = trianglePath(),
                        isSelected = false,
                        fillColor  = Color.Yellow,
                        renderStyle = testRenderStyle(),
                        isSmall    = true,
                        centroid   = Offset(50f, 50f)
                    )
                }
            }
        }
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun drawCountryMercator_drawCircle_selected_rendersWithoutCrash() {
        // isSmall=true, isSelected=true → 4× drawCircle (outer glow + inner glow + fill + stroke)
        composeTestRule.setContent {
            UnstampedPagesTheme {
                Canvas(modifier = Modifier.size(100.dp)) {
                    drawCountryMercator(
                        path       = trianglePath(),
                        isSelected = true,
                        fillColor  = Color.Red,
                        renderStyle = testRenderStyle(),
                        isSmall    = true,
                        centroid   = Offset(50f, 50f)
                    )
                }
            }
        }
        composeTestRule.onRoot().assertIsDisplayed()
    }
}
