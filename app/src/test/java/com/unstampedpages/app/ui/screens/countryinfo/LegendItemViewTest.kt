package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.unstampedpages.app.R
import com.unstampedpages.app.ui.theme.UnstampedPagesTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LegendItemViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun launch(item: LegendItem) {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                LegendItemView(item = item, modifier = Modifier)
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun legendItem_displaysNormalSecurityLabel() {
        launch(LegendItem(Color.Green, R.string.legend_low_risk, "legend_item_low_risk"))
        composeTestRule.onNode(hasText("Normal Security", substring = true)).assertExists()
    }

    @Test
    fun legendItem_displaysDoNotTravelLabel() {
        launch(LegendItem(Color.Red, R.string.legend_extreme_risk, "legend_item_extreme_risk"))
        composeTestRule.onNode(hasText("Do Not Travel", substring = true)).assertExists()
    }

    @Test
    fun legendItem_displaysVisaNotRequiredLabel() {
        launch(LegendItem(Color.Cyan, R.string.legend_visa_not_required, "legend_item_visa_not_required"))
        composeTestRule.onNode(hasText("Not Required", substring = true)).assertExists()
    }

    @Test
    fun legendItem_displaysDurationOfStayLabel() {
        launch(LegendItem(Color.Green, R.string.legend_duration_of_stay, "legend_item_duration"))
        composeTestRule.onNode(hasText("Duration", substring = true)).assertExists()
    }
}
