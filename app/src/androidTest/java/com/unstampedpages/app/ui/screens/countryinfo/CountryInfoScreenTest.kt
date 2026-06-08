package com.unstampedpages.app.ui.screens.countryinfo

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.compose.ui.test.junit4.v2.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountryInfoScreenTest {

    @get:Rule
    val composeTestRule = AndroidComposeTestRule(
        activityRule = ActivityScenarioRule<MainActivity>(
            Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        ),
        activityProvider = { rule ->
            var activity: MainActivity? = null
            rule.scenario.onActivity { activity = it }
            activity!!
        }
    )

    @Test
    fun countryInfoScreen_setsOrientationToPortrait() {
        // Navigate to Countries tab
        composeTestRule.onNodeWithText("Countries").performClick()
        composeTestRule.waitForIdle()

        // Verify orientation is set to portrait
        assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            composeTestRule.activity.requestedOrientation
        )
    }

    @Test
    fun countryInfoScreen_navigatingAway_resetsOrientation() {
        // Navigate to Countries tab
        composeTestRule.onNodeWithText("Countries").performClick()
        composeTestRule.waitForIdle()

        // Verify orientation is portrait
        assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            composeTestRule.activity.requestedOrientation
        )

        // Navigate to a different tab (Home)
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.waitForIdle()

        // Verify orientation is reset to unspecified
        assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
            composeTestRule.activity.requestedOrientation
        )
    }

    @Test
    fun countryInfoScreen_navigateToChecklist_resetsOrientation() {
        // Navigate to Countries tab
        composeTestRule.onNodeWithText("Countries").performClick()
        composeTestRule.waitForIdle()

        // Verify orientation is portrait
        assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            composeTestRule.activity.requestedOrientation
        )

        // Navigate to Checklist tab
        composeTestRule.onNodeWithText("Checklist").performClick()
        composeTestRule.waitForIdle()

        // Verify orientation is reset
        assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
            composeTestRule.activity.requestedOrientation
        )
    }

    @Test
    fun countryInfoScreen_navigateToTripLog_resetsOrientation() {
        // Navigate to Countries tab
        composeTestRule.onNodeWithText("Countries").performClick()
        composeTestRule.waitForIdle()

        // Verify orientation is portrait
        assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            composeTestRule.activity.requestedOrientation
        )

        // Navigate to Trip Log tab
        composeTestRule.onNodeWithText("Trip Log").performClick()
        composeTestRule.waitForIdle()

        // Verify orientation is reset
        assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
            composeTestRule.activity.requestedOrientation
        )
    }

    @Test
    fun countryInfoScreen_navigateToMyStamps_resetsOrientation() {
        // Navigate to Countries tab
        composeTestRule.onNodeWithText("Countries").performClick()
        composeTestRule.waitForIdle()

        // Verify orientation is portrait
        assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            composeTestRule.activity.requestedOrientation
        )

        // Navigate to My Stamps tab
        composeTestRule.onNodeWithText("My Stamps").performClick()
        composeTestRule.waitForIdle()

        // Verify orientation is reset
        assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
            composeTestRule.activity.requestedOrientation
        )
    }

    @Test
    fun countryInfoScreen_reEntering_setsPortraitAgain() {
        // Navigate to Countries tab
        composeTestRule.onNodeWithText("Countries").performClick()
        composeTestRule.waitForIdle()

        assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            composeTestRule.activity.requestedOrientation
        )

        // Navigate away
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.waitForIdle()

        assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
            composeTestRule.activity.requestedOrientation
        )

        // Navigate back to Countries
        composeTestRule.onNodeWithText("Countries").performClick()
        composeTestRule.waitForIdle()

        // Verify orientation is portrait again
        assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            composeTestRule.activity.requestedOrientation
        )
    }

    @Test
    fun otherScreens_doNotLockOrientation() {
        // Navigate to Checklist (not Countries)
        composeTestRule.onNodeWithText("Checklist").performClick()
        composeTestRule.waitForIdle()

        // Verify orientation is not locked to portrait
        assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
            composeTestRule.activity.requestedOrientation
        )
    }
}
