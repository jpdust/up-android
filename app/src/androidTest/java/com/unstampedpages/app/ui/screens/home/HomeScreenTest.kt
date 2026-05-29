package com.unstampedpages.app.ui.screens.home

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.ui.theme.UnstampedPagesTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for [HomeScreen].
 *
 * Tests are organized into eight groups:
 *   1. Header content  — title, tagline, welcome card, quote
 *   2. Feature cards   — all four cards render with their titles and descriptions
 *   3. Clickability    — each card node has a click action
 *   4. Navigation      — each card's click fires the correct callback
 *   5. Independence    — clicking one card does not fire other cards' callbacks
 *   6. Invocation count — each click fires the callback exactly once
 *   7. Default args    — HomeScreen renders and clicks without crashing when no
 *                        callbacks are supplied (default lambdas)
 *   8. Multiple clicks — callback accumulates correctly across repeated taps
 *
 * Note: HomeScreen uses a vertically-scrollable Column whose total content height
 * exceeds a typical emulator viewport. Items below the fold use assertExists()
 * rather than assertIsDisplayed() (which checks on-screen visibility), and
 * performScrollTo() is called before performClick() on off-screen card nodes.
 */
private const val CARD_EXPLORE_COUNTRIES = "Explore Countries"
private const val CARD_TRAVEL_CHECKLIST = "Travel Checklist"
private const val CARD_TRIP_JOURNAL = "Trip Journal"
private const val CARD_MY_PASSPORT_STAMPS = "My Passport Stamps"
private const val MSG_CHECKLIST_NO_FIRE = "Checklist callback should not have been invoked"
private const val MSG_TRIP_LOG_NO_FIRE = "Trip log callback should not have been invoked"
private const val MSG_STAMPS_NO_FIRE = "Stamps callback should not have been invoked"
private const val MSG_COUNTRIES_NO_FIRE = "Countries callback should not have been invoked"

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    /**
     * Sets content as [HomeScreen] wrapped in the app theme.
     * Each callback parameter defaults to a no-op lambda so tests only supply
     * what they care about.
     */
    private fun launchHomeScreen(
        onNavigateToCountries: () -> Unit = {},
        onNavigateToChecklist: () -> Unit = {},
        onNavigateToTripLog: () -> Unit = {},
        onNavigateToMyStamps: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                HomeScreen(
                    onNavigateToCountries = onNavigateToCountries,
                    onNavigateToChecklist = onNavigateToChecklist,
                    onNavigateToTripLog = onNavigateToTripLog,
                    onNavigateToMyStamps = onNavigateToMyStamps
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    // ---------------------------------------------------------------------------
    // 1. Header content
    // ---------------------------------------------------------------------------

    @Test
    fun homeScreen_appTitle_isDisplayed() {
        launchHomeScreen()

        composeTestRule.onNodeWithText("UNSTAMPED PAGES").assertIsDisplayed()
    }

    @Test
    fun homeScreen_appTagline_isDisplayed() {
        launchHomeScreen()

        composeTestRule.onNodeWithText("Your Adventure Awaits").assertIsDisplayed()
    }

    @Test
    fun homeScreen_welcomeHeading_isDisplayed() {
        launchHomeScreen()

        composeTestRule.onNodeWithText("Welcome, Explorer").assertIsDisplayed()
    }

    @Test
    fun homeScreen_description_isDisplayed() {
        launchHomeScreen()

        composeTestRule
            .onNodeWithText(
                "Document your journeys across the globe. Discover new countries, " +
                    "track your travels, and never forget what to pack."
            )
            .assertExists()
    }

    /**
     * The quote body is matched as a substring to avoid fragility around curly-quote
     * characters in the resource string. Uses assertExists() because the quote card
     * may be below the initial viewport of the scrollable Column.
     */
    @Test
    fun homeScreen_quote_isDisplayed() {
        launchHomeScreen()

        composeTestRule
            .onNodeWithText(
                "The world is a book, and those who do not travel read only one page.",
                substring = true
            )
            .assertExists()
    }

    @Test
    fun homeScreen_quoteAuthor_isDisplayed() {
        launchHomeScreen()

        composeTestRule.onNodeWithText("— Saint Augustine").assertExists()
    }

    // ---------------------------------------------------------------------------
    // 2. Feature cards — rendering
    // ---------------------------------------------------------------------------

    @Test
    fun homeScreen_exploreCountriesCard_titleIsDisplayed() {
        launchHomeScreen()

        composeTestRule.onNodeWithText(CARD_EXPLORE_COUNTRIES).assertIsDisplayed()
    }

    @Test
    fun homeScreen_exploreCountriesCard_descriptionIsDisplayed() {
        launchHomeScreen()

        composeTestRule
            .onNodeWithText(
                "Tap the globe to discover country info, safety levels, and travel tips."
            )
            .assertExists()
    }

    @Test
    fun homeScreen_travelChecklistCard_titleIsDisplayed() {
        launchHomeScreen()

        composeTestRule.onNodeWithText(CARD_TRAVEL_CHECKLIST).assertExists()
    }

    @Test
    fun homeScreen_travelChecklistCard_descriptionIsDisplayed() {
        launchHomeScreen()

        composeTestRule
            .onNodeWithText(
                "Never forget essentials. Keep track of what to bring on your adventure."
            )
            .assertExists()
    }

    @Test
    fun homeScreen_tripJournalCard_titleIsDisplayed() {
        launchHomeScreen()

        composeTestRule.onNodeWithText(CARD_TRIP_JOURNAL).assertExists()
    }

    @Test
    fun homeScreen_tripJournalCard_descriptionIsDisplayed() {
        launchHomeScreen()

        composeTestRule
            .onNodeWithText("Record your memories. Document each day of your journey.")
            .assertExists()
    }

    @Test
    fun homeScreen_myPassportStampsCard_titleIsDisplayed() {
        launchHomeScreen()

        composeTestRule.onNodeWithText(CARD_MY_PASSPORT_STAMPS).assertExists()
    }

    @Test
    fun homeScreen_myPassportStampsCard_descriptionIsDisplayed() {
        launchHomeScreen()

        composeTestRule
            .onNodeWithText(
                "Upload pictures of your passport stamps to each country you visit " +
                    "as a digital record."
            )
            .assertExists()
    }

    /** Verifies all four feature card titles are present in the composition. */
    @Test
    fun homeScreen_allFourFeatureCards_areDisplayed() {
        launchHomeScreen()

        listOf(
            CARD_EXPLORE_COUNTRIES,
            CARD_TRAVEL_CHECKLIST,
            CARD_TRIP_JOURNAL,
            CARD_MY_PASSPORT_STAMPS
        ).forEach { title ->
            composeTestRule.onNodeWithText(title).assertExists()
        }
    }

    // ---------------------------------------------------------------------------
    // 3. Feature cards — clickability
    // ---------------------------------------------------------------------------

    @Test
    fun homeScreen_exploreCountriesCard_hasClickAction() {
        launchHomeScreen()

        composeTestRule.onNodeWithText(CARD_EXPLORE_COUNTRIES).assertHasClickAction()
    }

    @Test
    fun homeScreen_travelChecklistCard_hasClickAction() {
        launchHomeScreen()

        composeTestRule.onNodeWithText(CARD_TRAVEL_CHECKLIST)
            .performScrollTo()
            .assertHasClickAction()
    }

    @Test
    fun homeScreen_tripJournalCard_hasClickAction() {
        launchHomeScreen()

        composeTestRule.onNodeWithText(CARD_TRIP_JOURNAL)
            .performScrollTo()
            .assertHasClickAction()
    }

    @Test
    fun homeScreen_myPassportStampsCard_hasClickAction() {
        launchHomeScreen()

        composeTestRule.onNodeWithText(CARD_MY_PASSPORT_STAMPS)
            .performScrollTo()
            .assertHasClickAction()
    }

    // ---------------------------------------------------------------------------
    // 4. Navigation — each card fires the correct callback
    // ---------------------------------------------------------------------------

    @Test
    fun clickingExploreCountries_invokesOnNavigateToCountries() {
        var called = false
        launchHomeScreen(onNavigateToCountries = { called = true })

        composeTestRule.onNodeWithText(CARD_EXPLORE_COUNTRIES).performClick()
        composeTestRule.waitForIdle()

        assertTrue("onNavigateToCountries should be called", called)
    }

    @Test
    fun clickingTravelChecklist_invokesOnNavigateToChecklist() {
        var called = false
        launchHomeScreen(onNavigateToChecklist = { called = true })

        composeTestRule.onNodeWithText(CARD_TRAVEL_CHECKLIST)
            .performScrollTo()
            .performClick()
        composeTestRule.waitForIdle()

        assertTrue("onNavigateToChecklist should be called", called)
    }

    @Test
    fun clickingTripJournal_invokesOnNavigateToTripLog() {
        var called = false
        launchHomeScreen(onNavigateToTripLog = { called = true })

        composeTestRule.onNodeWithText(CARD_TRIP_JOURNAL)
            .performScrollTo()
            .performClick()
        composeTestRule.waitForIdle()

        assertTrue("onNavigateToTripLog should be called", called)
    }

    @Test
    fun clickingMyPassportStamps_invokesOnNavigateToMyStamps() {
        var called = false
        launchHomeScreen(onNavigateToMyStamps = { called = true })

        composeTestRule.onNodeWithText(CARD_MY_PASSPORT_STAMPS)
            .performScrollTo()
            .performClick()
        composeTestRule.waitForIdle()

        assertTrue("onNavigateToMyStamps should be called", called)
    }

    // ---------------------------------------------------------------------------
    // 5. Callback independence — clicking one card does not trigger other callbacks
    // ---------------------------------------------------------------------------

    @Test
    fun clickingExploreCountries_doesNotInvokeOtherCallbacks() {
        var checklistCalled = false
        var tripLogCalled = false
        var stampsCalled = false

        launchHomeScreen(
            onNavigateToCountries = {},
            onNavigateToChecklist = { checklistCalled = true },
            onNavigateToTripLog = { tripLogCalled = true },
            onNavigateToMyStamps = { stampsCalled = true }
        )

        composeTestRule.onNodeWithText(CARD_EXPLORE_COUNTRIES).performClick()
        composeTestRule.waitForIdle()

        assertFalse(MSG_CHECKLIST_NO_FIRE, checklistCalled)
        assertFalse(MSG_TRIP_LOG_NO_FIRE, tripLogCalled)
        assertFalse(MSG_STAMPS_NO_FIRE, stampsCalled)
    }

    @Test
    fun clickingTravelChecklist_doesNotInvokeOtherCallbacks() {
        var countriesCalled = false
        var tripLogCalled = false
        var stampsCalled = false

        launchHomeScreen(
            onNavigateToCountries = { countriesCalled = true },
            onNavigateToChecklist = {},
            onNavigateToTripLog = { tripLogCalled = true },
            onNavigateToMyStamps = { stampsCalled = true }
        )

        composeTestRule.onNodeWithText(CARD_TRAVEL_CHECKLIST)
            .performScrollTo()
            .performClick()
        composeTestRule.waitForIdle()

        assertFalse(MSG_COUNTRIES_NO_FIRE, countriesCalled)
        assertFalse(MSG_TRIP_LOG_NO_FIRE, tripLogCalled)
        assertFalse(MSG_STAMPS_NO_FIRE, stampsCalled)
    }

    @Test
    fun clickingTripJournal_doesNotInvokeOtherCallbacks() {
        var countriesCalled = false
        var checklistCalled = false
        var stampsCalled = false

        launchHomeScreen(
            onNavigateToCountries = { countriesCalled = true },
            onNavigateToChecklist = { checklistCalled = true },
            onNavigateToTripLog = {},
            onNavigateToMyStamps = { stampsCalled = true }
        )

        composeTestRule.onNodeWithText(CARD_TRIP_JOURNAL)
            .performScrollTo()
            .performClick()
        composeTestRule.waitForIdle()

        assertFalse(MSG_COUNTRIES_NO_FIRE, countriesCalled)
        assertFalse(MSG_CHECKLIST_NO_FIRE, checklistCalled)
        assertFalse(MSG_STAMPS_NO_FIRE, stampsCalled)
    }

    @Test
    fun clickingMyPassportStamps_doesNotInvokeOtherCallbacks() {
        var countriesCalled = false
        var checklistCalled = false
        var tripLogCalled = false

        launchHomeScreen(
            onNavigateToCountries = { countriesCalled = true },
            onNavigateToChecklist = { checklistCalled = true },
            onNavigateToTripLog = { tripLogCalled = true },
            onNavigateToMyStamps = {}
        )

        composeTestRule.onNodeWithText(CARD_MY_PASSPORT_STAMPS)
            .performScrollTo()
            .performClick()
        composeTestRule.waitForIdle()

        assertFalse(MSG_COUNTRIES_NO_FIRE, countriesCalled)
        assertFalse(MSG_CHECKLIST_NO_FIRE, checklistCalled)
        assertFalse(MSG_TRIP_LOG_NO_FIRE, tripLogCalled)
    }

    // ---------------------------------------------------------------------------
    // 6. Callback invocation count — each click fires the callback exactly once
    // ---------------------------------------------------------------------------

    @Test
    fun clickingExploreCountries_callsCallbackExactlyOnce() {
        var count = 0
        launchHomeScreen(onNavigateToCountries = { count++ })

        composeTestRule.onNodeWithText(CARD_EXPLORE_COUNTRIES).performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, count)
    }

    @Test
    fun clickingTravelChecklist_callsCallbackExactlyOnce() {
        var count = 0
        launchHomeScreen(onNavigateToChecklist = { count++ })

        composeTestRule.onNodeWithText(CARD_TRAVEL_CHECKLIST)
            .performScrollTo()
            .performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, count)
    }

    @Test
    fun clickingTripJournal_callsCallbackExactlyOnce() {
        var count = 0
        launchHomeScreen(onNavigateToTripLog = { count++ })

        composeTestRule.onNodeWithText(CARD_TRIP_JOURNAL)
            .performScrollTo()
            .performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, count)
    }

    @Test
    fun clickingMyPassportStamps_callsCallbackExactlyOnce() {
        var count = 0
        launchHomeScreen(onNavigateToMyStamps = { count++ })

        composeTestRule.onNodeWithText(CARD_MY_PASSPORT_STAMPS)
            .performScrollTo()
            .performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, count)
    }

    // ---------------------------------------------------------------------------
    // 7. Default arguments — no crash when callbacks are omitted
    // ---------------------------------------------------------------------------

    /**
     * [HomeScreen] supplies default no-op lambdas for all navigation callbacks.
     * Launching and clicking without providing any callbacks must not throw.
     */
    @Test
    fun homeScreen_rendersWithDefaultCallbacks_withoutCrash() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                HomeScreen() // all callbacks default to {}
            }
        }
        composeTestRule.waitForIdle()

        // If we reach this assertion the composition succeeded
        composeTestRule.onNodeWithText("Welcome, Explorer").assertIsDisplayed()
    }

    @Test
    fun clickingExploreCountries_withDefaultCallback_doesNotCrash() {
        composeTestRule.setContent {
            UnstampedPagesTheme { HomeScreen() }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(CARD_EXPLORE_COUNTRIES).performClick()
        composeTestRule.waitForIdle()
        // No exception → test passes
    }

    @Test
    fun clickingTravelChecklist_withDefaultCallback_doesNotCrash() {
        composeTestRule.setContent {
            UnstampedPagesTheme { HomeScreen() }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(CARD_TRAVEL_CHECKLIST)
            .performScrollTo()
            .performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun clickingTripJournal_withDefaultCallback_doesNotCrash() {
        composeTestRule.setContent {
            UnstampedPagesTheme { HomeScreen() }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(CARD_TRIP_JOURNAL)
            .performScrollTo()
            .performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun clickingMyPassportStamps_withDefaultCallback_doesNotCrash() {
        composeTestRule.setContent {
            UnstampedPagesTheme { HomeScreen() }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(CARD_MY_PASSPORT_STAMPS)
            .performScrollTo()
            .performClick()
        composeTestRule.waitForIdle()
    }

    // ---------------------------------------------------------------------------
    // 8. Multiple sequential clicks — callback accumulates correctly
    // ---------------------------------------------------------------------------

    @Test
    fun exploringCountries_multipleTimes_callsCallbackEachTime() {
        var count = 0
        launchHomeScreen(onNavigateToCountries = { count++ })

        repeat(3) {
            composeTestRule.onNodeWithText(CARD_EXPLORE_COUNTRIES).performClick()
            composeTestRule.waitForIdle()
        }

        assertEquals("Callback should fire once per tap", 3, count)
    }
}
