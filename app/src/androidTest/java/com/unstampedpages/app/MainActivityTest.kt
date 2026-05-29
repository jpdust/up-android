package com.unstampedpages.app

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

private const val WELCOME_TEXT = "Welcome, Explorer"
private const val TRIP_LOG_TAB = "Trip Log"
private const val MY_STAMPS_TAB = "My Stamps"

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
    fun mainActivityLaunchesWithoutCrashing() {
        composeTestRule.waitForIdle()
        // Reaching this line means onCreate completed without exception.
    }

    @Test
    fun mainActivityHomeScreenIsDisplayedOnLaunch() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(WELCOME_TEXT).assertIsDisplayed()
    }

    @Test
    fun mainActivityAppTitleIsDisplayedOnLaunch() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("UNSTAMPED PAGES").assertIsDisplayed()
    }

    // -------------------------------------------------------------------------
    // 2. Bottom navigation bar
    // -------------------------------------------------------------------------

    @Test
    fun mainActivityBottomNavBarShowsHomeTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }

    @Test
    fun mainActivityBottomNavBarShowsCountriesTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Countries").assertIsDisplayed()
    }

    @Test
    fun mainActivityBottomNavBarShowsChecklistTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Checklist").assertIsDisplayed()
    }

    @Test
    fun mainActivityBottomNavBarShowsTripLogTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(TRIP_LOG_TAB).assertIsDisplayed()
    }

    @Test
    fun mainActivityBottomNavBarShowsMyStampsTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(MY_STAMPS_TAB).assertIsDisplayed()
    }

    @Test
    fun mainActivityHomeTabIsSelectedByDefault() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Home").assertIsSelected()
    }

    @Test
    fun mainActivityNonHomeTabsAreNotSelectedByDefault() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Countries").assertIsNotSelected()
        composeTestRule.onNodeWithText("Checklist").assertIsNotSelected()
        composeTestRule.onNodeWithText(TRIP_LOG_TAB).assertIsNotSelected()
        composeTestRule.onNodeWithText(MY_STAMPS_TAB).assertIsNotSelected()
    }

    // -------------------------------------------------------------------------
    // 3. Tab navigation
    // -------------------------------------------------------------------------

    @Test
    fun mainActivityClickingCountriesTabShowsCountriesScreen() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Countries").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Tap a country to discover more").assertExists()
    }

    @Test
    fun mainActivityClickingCountriesTabSelectsCountriesTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Countries").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Countries").assertIsSelected()
    }

    @Test
    fun mainActivityClickingChecklistTabShowsChecklistScreen() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Checklist").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("TRAVEL CHECKLIST").assertExists()
    }

    @Test
    fun mainActivityClickingChecklistTabSelectsChecklistTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Checklist").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Checklist").assertIsSelected()
    }

    @Test
    fun mainActivityClickingTripLogTabShowsTripLogScreen() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(TRIP_LOG_TAB).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("TRIP LOG").assertExists()
    }

    @Test
    fun mainActivityClickingTripLogTabSelectsTripLogTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(TRIP_LOG_TAB).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(TRIP_LOG_TAB).assertIsSelected()
    }

    @Test
    fun mainActivityClickingMyStampsTabShowsMyStampsScreen() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(MY_STAMPS_TAB).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("MY STAMPS").assertExists()
    }

    @Test
    fun mainActivityClickingMyStampsTabSelectsMyStampsTab() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(MY_STAMPS_TAB).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(MY_STAMPS_TAB).assertIsSelected()
    }

    @Test
    fun mainActivityClickingHomeTabFromCountriesScreenReturnsToHomeScreen() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Countries").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(WELCOME_TEXT).assertIsDisplayed()
    }

    @Test
    fun mainActivityClickingHomeTabFromCountriesScreenSelectsHomeTab() {
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
    fun mainActivityCyclingThroughAllTabsSelectsCorrectTabAtEachStep() {
        composeTestRule.waitForIdle()

        data class Step(val label: String, val previousLabel: String)

        listOf(
            Step("Countries", "Home"),
            Step("Checklist", "Countries"),
            Step(TRIP_LOG_TAB, "Checklist"),
            Step(MY_STAMPS_TAB, TRIP_LOG_TAB),
            Step("Home", MY_STAMPS_TAB)
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
    fun mainActivityRecreationOnHomeScreenDoesNotCrash() {
        composeTestRule.waitForIdle()

        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(WELCOME_TEXT).assertIsDisplayed()
    }

    @Test
    fun mainActivityRecreationAfterNavigatingToChecklistDoesNotCrash() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Checklist").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        // App remains usable after recreation regardless of which screen was active.
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }
}
