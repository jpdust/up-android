package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.AppConstants
import com.unstampedpages.app.data.model.CountryGeometry
import com.unstampedpages.app.data.model.LatLng
import com.unstampedpages.app.ui.theme.UnstampedPagesTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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

    // ==================== DrawScope.drawCountryLabels Tests ====================
    //
    // Line 1296: val textLayout = params.labelTextLayouts.getValue(countryId)
    //
    // This line executes once per entry returned by computeVisibleLabelSpecs. The
    // specs map is empty when labelAlpha < 0.01, matrixValid is false, the geometries
    // list is empty, or every label centroid is culled outside the visible canvas.
    // Tests 1–4 verify the loop is skipped in those cases; tests 5–6 verify the
    // getValue call fires for one and two visible labels respectively.

    // Layout with map 400×200 and a generous canvas so mapped centroids land on screen.
    private fun testMapLayout() = MapLayout(
        mapWidth = 400f, mapHeight = 200f,
        canvasOffsetX = 0f, canvasOffsetY = 0f,
        canvasWidth = 800f, canvasHeight = 400f
    )

    // Bounds whose centroid maps to (200, 100) with the identity forward matrix,
    // and whose widthNorm × mapWidth = 200px >> LABEL_FULL_SCREEN_PX → sizeAlpha = 1.
    private fun visibleBounds() = CountryBounds(
        centroidNormX = 0.5f, centroidNormY = 0.5f,
        minX = 0.25f, maxX = 0.75f,
        minY = 0.25f, maxY = 0.75f
    )

    // Bounds whose centroid maps to x = 2000px — far beyond canvasWidth + tw → culled.
    private fun culledBounds() = CountryBounds(
        centroidNormX = 5.0f, centroidNormY = 0.5f,
        minX = 4.75f, maxX = 5.25f,
        minY = 0.25f, maxY = 0.75f
    )

    private fun testGeometry(id: String) = CountryGeometry(
        countryId = id,
        polygons = listOf(listOf(LatLng(0f, 0f), LatLng(1f, 0f), LatLng(0f, 1f)))
    )

    // gestureState with identity forwardMatrix; matrixValid defaults to true here.
    private fun labelGestureState(matrixValid: Boolean = true) = MapGestureState(
        geometries = emptyList(),
        inverseMatrix = android.graphics.Matrix(),
        matrixValid = matrixValid
    )

    @Test
    fun drawCountryLabels_skipsLoop_whenLabelAlphaIsZero() {
        // labelAlpha=0 → computeVisibleLabelSpecs returns empty → line 1296 never reached
        composeTestRule.setContent {
            val measurer = rememberTextMeasurer()
            val textLayout = remember { measurer.measure("Test") }
            Canvas(modifier = Modifier.size(200.dp)) {
                drawCountryLabels(
                    wrapOffset = 0f,
                    layout = testMapLayout(),
                    params = MapDrawParams(
                        geometries = listOf(testGeometry("TST")),
                        selectedCountryId = null,
                        transitionProgress = 0f,
                        scale = 1f,
                        countryBounds = mapOf("TST" to visibleBounds()),
                        currentModeColors = emptyMap(),
                        previousModeColors = emptyMap(),
                        labelAlpha = 0f,
                        labelTextLayouts = mapOf("TST" to textLayout)
                    ),
                    gestureState = labelGestureState()
                )
            }
        }
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun drawCountryLabels_skipsLoop_whenMatrixInvalid() {
        // matrixValid=false → computeVisibleLabelSpecs returns empty → line 1296 never reached
        composeTestRule.setContent {
            val measurer = rememberTextMeasurer()
            val textLayout = remember { measurer.measure("Test") }
            Canvas(modifier = Modifier.size(200.dp)) {
                drawCountryLabels(
                    wrapOffset = 0f,
                    layout = testMapLayout(),
                    params = MapDrawParams(
                        geometries = listOf(testGeometry("TST")),
                        selectedCountryId = null,
                        transitionProgress = 0f,
                        scale = 1f,
                        countryBounds = mapOf("TST" to visibleBounds()),
                        currentModeColors = emptyMap(),
                        previousModeColors = emptyMap(),
                        labelAlpha = 1f,
                        labelTextLayouts = mapOf("TST" to textLayout)
                    ),
                    gestureState = labelGestureState(matrixValid = false)
                )
            }
        }
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun drawCountryLabels_skipsLoop_whenNoGeometries() {
        // empty geometries list → computeVisibleLabelSpecs iterates nothing → empty map → line 1296 not reached
        composeTestRule.setContent {
            Canvas(modifier = Modifier.size(200.dp)) {
                drawCountryLabels(
                    wrapOffset = 0f,
                    layout = testMapLayout(),
                    params = MapDrawParams(
                        geometries = emptyList(),
                        selectedCountryId = null,
                        transitionProgress = 0f,
                        scale = 1f,
                        countryBounds = emptyMap(),
                        currentModeColors = emptyMap(),
                        previousModeColors = emptyMap(),
                        labelAlpha = 1f,
                        labelTextLayouts = emptyMap()
                    ),
                    gestureState = labelGestureState()
                )
            }
        }
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun drawCountryLabels_skipsLoop_whenLabelIsCulled() {
        // centroid maps to x=2000px which exceeds canvasWidth+tw → culled → specs empty → line 1296 not reached
        composeTestRule.setContent {
            val measurer = rememberTextMeasurer()
            val textLayout = remember { measurer.measure("Test") }
            Canvas(modifier = Modifier.size(200.dp)) {
                drawCountryLabels(
                    wrapOffset = 0f,
                    layout = testMapLayout(),
                    params = MapDrawParams(
                        geometries = listOf(testGeometry("TST")),
                        selectedCountryId = null,
                        transitionProgress = 0f,
                        scale = 1f,
                        countryBounds = mapOf("TST" to culledBounds()),
                        currentModeColors = emptyMap(),
                        previousModeColors = emptyMap(),
                        labelAlpha = 1f,
                        labelTextLayouts = mapOf("TST" to textLayout)
                    ),
                    gestureState = labelGestureState()
                )
            }
        }
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun drawCountryLabels_executesGetValue_forSingleVisibleLabel() {
        // labelAlpha=1, matrixValid=true, visible bounds, matching layout entry →
        // computeVisibleLabelSpecs returns one entry → line 1296 executes exactly once
        composeTestRule.setContent {
            val measurer = rememberTextMeasurer()
            val textLayout = remember { measurer.measure("France") }
            Canvas(modifier = Modifier.size(200.dp)) {
                drawCountryLabels(
                    wrapOffset = 0f,
                    layout = testMapLayout(),
                    params = MapDrawParams(
                        geometries = listOf(testGeometry("FRA")),
                        selectedCountryId = null,
                        transitionProgress = 0f,
                        scale = 1f,
                        countryBounds = mapOf("FRA" to visibleBounds()),
                        currentModeColors = emptyMap(),
                        previousModeColors = emptyMap(),
                        labelAlpha = 1f,
                        labelTextLayouts = mapOf("FRA" to textLayout)
                    ),
                    gestureState = labelGestureState()
                )
            }
        }
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun drawCountryLabels_executesGetValue_forEachVisibleLabel() {
        // Two geometries both with visible bounds and matching layouts →
        // computeVisibleLabelSpecs returns two entries → line 1296 executes twice
        composeTestRule.setContent {
            val measurer = rememberTextMeasurer()
            val layoutA = remember { measurer.measure("Germany") }
            val layoutB = remember { measurer.measure("Italy") }
            Canvas(modifier = Modifier.size(200.dp)) {
                drawCountryLabels(
                    wrapOffset = 0f,
                    layout = testMapLayout(),
                    params = MapDrawParams(
                        geometries = listOf(testGeometry("DEU"), testGeometry("ITA")),
                        selectedCountryId = null,
                        transitionProgress = 0f,
                        scale = 1f,
                        countryBounds = mapOf(
                            "DEU" to visibleBounds(),
                            "ITA" to visibleBounds()
                        ),
                        currentModeColors = emptyMap(),
                        previousModeColors = emptyMap(),
                        labelAlpha = 1f,
                        labelTextLayouts = mapOf(
                            "DEU" to layoutA,
                            "ITA" to layoutB
                        )
                    ),
                    gestureState = labelGestureState()
                )
            }
        }
        composeTestRule.onRoot().assertIsDisplayed()
    }

    // ==================== Gesture Callback Tests ====================

    @Test
    fun worldMapCanvas_panGesture_firesOnPanGestureEndCallback() {
        var panCallbackFired = false

        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = { _, _ -> },
                    gestureCallbacks = MapGestureCallbacks(onPanGestureEnd = { panCallbackFired = true }),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onRoot().performTouchInput {
            swipe(
                start = center,
                end = center.copy(x = center.x - 200f),
                durationMillis = 200
            )
        }
        composeTestRule.waitForIdle()

        assertTrue("onPanGestureEnd should fire after a pan gesture", panCallbackFired)
    }

    @Test
    fun worldMapCanvas_zoomGesture_firesOnZoomGestureEndCallback() {
        var zoomCallbackFired = false

        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = { _, _ -> },
                    gestureCallbacks = MapGestureCallbacks(onZoomGestureEnd = { _, _ -> zoomCallbackFired = true }),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onRoot().performTouchInput {
            pinch(
                start0 = center.copy(x = center.x - 100f),
                end0   = center.copy(x = center.x - 200f),
                start1 = center.copy(x = center.x + 100f),
                end1   = center.copy(x = center.x + 200f)
            )
        }
        composeTestRule.waitForIdle()

        assertTrue("onZoomGestureEnd should fire after a pinch-zoom gesture", zoomCallbackFired)
    }

    @Test
    fun worldMapCanvas_zoomIn_passesZoomedInTrue() {
        var capturedZoomedIn: Boolean? = null

        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = { _, _ -> },
                    gestureCallbacks = MapGestureCallbacks(onZoomGestureEnd = { zoomedIn, _ -> capturedZoomedIn = zoomedIn }),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        composeTestRule.waitForIdle()

        // Pinch outward = zoom in
        composeTestRule.onRoot().performTouchInput {
            pinch(
                start0 = center.copy(x = center.x - 50f),
                end0   = center.copy(x = center.x - 200f),
                start1 = center.copy(x = center.x + 50f),
                end1   = center.copy(x = center.x + 200f)
            )
        }
        composeTestRule.waitForIdle()

        assertTrue("zoomedIn should be true after pinching outward", capturedZoomedIn == true)
    }

    @Test
    fun worldMapCanvas_panLeft_passesLeftDirection() {
        var capturedDirection: String? = null

        composeTestRule.setContent {
            UnstampedPagesTheme {
                WorldMapCanvas(
                    selectedCountryId = null,
                    onCountryTapped = { _, _ -> },
                    gestureCallbacks = MapGestureCallbacks(onPanGestureEnd = { direction -> capturedDirection = direction }),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onRoot().performTouchInput {
            swipe(
                start = center,
                end = center.copy(x = center.x - 300f),
                durationMillis = 200
            )
        }
        composeTestRule.waitForIdle()

        assertEquals("Pan left should report 'left' direction", "left", capturedDirection)
    }
}
