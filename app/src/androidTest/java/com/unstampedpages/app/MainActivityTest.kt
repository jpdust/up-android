package com.unstampedpages.app

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented integration tests for [MainActivity].
 *
 * Launches the real [MainActivity] via [createAndroidComposeRule] to exercise the full
 * application entry point: New Relic initialisation, [CountryGeometryData] async load,
 * edge-to-edge setup, and Compose content hosted inside [UnstampedPagesTheme].
 *
 * Tests are organised into four groups:
 *   1. Initial launch    — activity starts without error; home screen is the default destination
 *   2. Bottom nav bar    — all five tabs are rendered; Home is selected on launch
 *   3. Tab navigation    — tapping each tab surfaces that screen's header content
 *   4. Recreation        — simulated configuration change does not crash the app
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // -------------------------------------------------------------------------
    // 1. Initial launch
    // -------------------------------------------------------------------------

    /**
     * The most basic smoke test: if [MainActivity.onCreate] throws for any reason
     * (bad token, missing resource, crash in CountryGeometryData, etc.) this test
     * fails before reaching the assertion.
     */
    @Test
    fun mainActivity_launches_withoutCrashing() {
        composeTestRule.waitForIdle()
        // Reaching this line means onCreate completed without exception.
    }

    @Test
    fun mainActivity_homeScreen_isDisplayedOnLaunch() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Welcome, Explorer").assertIsDisplayed()
    }

    @Test
    fun mainActivity_appTitle_isDisplayedOnLaunch() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("UNSTAMPED PAGES").assertIsDisplayed()
    }

    // -------------------------------------------------------------------------
    // 2. Bottom navigation bar
    // -------------------------------------------------------------------------

    @Test
    fun mainActivity_bottomNavBar_showsHomeTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }

    @Test
    fun mainActivity_bottomNavBar_showsCountriesTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Countries").assertIsDisplayed()
    }

    @Test
    fun mainActivity_bottomNavBar_showsChecklistTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Checklist").assertIsDisplayed()
    }

    @Test
    fun mainActivity_bottomNavBar_showsTripLogTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Trip Log").assertIsDisplayed()
    }

    @Test
    fun mainActivity_bottomNavBar_showsMyStampsTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("My Stamps").assertIsDisplayed()
    }

    @Test
    fun mainActivity_homeTab_isSelectedByDefault() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Home").assertIsSelected()
    }

    @Test
    fun mainActivity_nonHomeTabs_areNotSelectedByDefault() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Countries").assertIsNotSelected()
        composeTestRule.onNodeWithText("Checklist").assertIsNotSelected()
        composeTestRule.onNodeWithText("Trip Log").assertIsNotSelected()
        composeTestRule.onNodeWithText("My Stamps").assertIsNotSelected()
    }

    // -------------------------------------------------------------------------
    // 3. Tab navigation
    // -------------------------------------------------------------------------

    @Test
    fun mainActivity_clickingCountriesTab_showsCountriesScreen() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Countries").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Tap a country to discover more").assertExists()
    }

    @Test
    fun mainActivity_clickingCountriesTab_selectsCountriesTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Countries").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Countries").assertIsSelected()
    }

    @Test
    fun mainActivity_clickingChecklistTab_showsChecklistScreen() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Checklist").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("TRAVEL CHECKLIST").assertExists()
    }

    @Test
    fun mainActivity_clickingChecklistTab_selectsChecklistTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Checklist").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Checklist").assertIsSelected()
    }

    @Test
    fun mainActivity_clickingTripLogTab_showsTripLogScreen() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Trip Log").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("TRIP LOG").assertExists()
    }

    @Test
    fun mainActivity_clickingTripLogTab_selectsTripLogTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Trip Log").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Trip Log").assertIsSelected()
    }

    @Test
    fun mainActivity_clickingMyStampsTab_showsMyStampsScreen() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("My Stamps").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("MY STAMPS").assertExists()
    }

    @Test
    fun mainActivity_clickingMyStampsTab_selectsMyStampsTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("My Stamps").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("My Stamps").assertIsSelected()
    }

    @Test
    fun mainActivity_clickingHomeTab_fromCountriesScreen_returnsToHomeScreen() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Countries").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Welcome, Explorer").assertIsDisplayed()
    }

    @Test
    fun mainActivity_clickingHomeTab_fromCountriesScreen_selectsHomeTab() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Countries").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Home").assertIsSelected()
        composeTestRule.onNodeWithText("Countries").assertIsNotSelected()
    }

    /**
     * Verifies the full navigation cycle: start → Countries → Checklist → Trip Log →
     * My Stamps → Home. Each step asserts the correct tab is selected and the previous
     * one is deselected, exercising the single-selection invariant across all five tabs.
     */
    @Test
    fun mainActivity_cyclingThroughAllTabs_selectsCorrectTabAtEachStep() {
        composeTestRule.waitForIdle()

        data class Step(val label: String, val previousLabel: String)

        listOf(
            Step("Countries", "Home"),
            Step("Checklist", "Countries"),
            Step("Trip Log", "Checklist"),
            Step("My Stamps", "Trip Log"),
            Step("Home", "My Stamps")
        ).forEach { (label, previous) ->
            composeTestRule.onNodeWithText(label).performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText(label).assertIsSelected()
            composeTestRule.onNodeWithText(previous).assertIsNotSelected()
        }
    }

    // -------------------------------------------------------------------------
    // 4. Activity recreation (simulated configuration change)
    // -------------------------------------------------------------------------

    /**
     * Simulates a configuration change (e.g. screen rotation) while on the home
     * screen.  The activity is recreated from scratch with a non-null
     * savedInstanceState, exercising the full [MainActivity.onCreate] path a
     * second time including New Relic re-initialisation.
     */
    @Test
    fun mainActivity_recreation_onHomeScreen_doesNotCrash() {
        composeTestRule.waitForIdle()

        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Welcome, Explorer").assertIsDisplayed()
    }

    @Test
    fun mainActivity_recreation_afterNavigatingToChecklist_doesNotCrash() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Checklist").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        // App remains usable after recreation regardless of which screen was active.
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }
}
