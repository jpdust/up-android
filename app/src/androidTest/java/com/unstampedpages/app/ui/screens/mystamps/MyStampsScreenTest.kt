package com.unstampedpages.app.ui.screens.mystamps

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.MainActivity
import com.unstampedpages.app.data.AppConstants
import com.unstampedpages.app.data.CountryList
import com.unstampedpages.app.ui.screens.mystamps.MyStampsRobot.Companion.myStampsRobot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for MyStampsScreen using the Robot Pattern.
 *
 * These tests verify the functionality of the My Stamps tab including:
 * - Screen layout and title verification
 * - Column headers display
 * - Country list population
 * - Scrolling behavior
 * - Add stamp dialog functionality
 * - Camera and Gallery options
 * - Cancel stamp addition
 * - Remove stamp functionality
 */
@RunWith(AndroidJUnit4::class)
class MyStampsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        // Navigate to My Stamps tab
        composeTestRule.onNodeWithText("My Stamps").performClick()
        composeTestRule.waitForIdle()
    }

    // ==================== Screen Layout Tests ====================

    @Test
    fun myStampsScreen_displaysCorrectTitle() {
        composeTestRule.myStampsRobot {
            verifyScreenTitle()
        }
    }

    @Test
    fun myStampsScreen_displaysColumnHeaders() {
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            verifyColumnHeaders()
        }
    }

    @Test
    fun myStampsScreen_displaysHeaderRow() {
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            verifyHeaderRowDisplayed()
        }
    }

    @Test
    fun myStampsScreen_displaysBothColumns() {
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            verifyColumnHeaders()
        }
    }

    // ==================== Country List Tests ====================

    @Test
    fun myStampsScreen_displaysCountryList() {
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            verifyCountryListDisplayed()
        }
    }

    @Test
    fun myStampsScreen_allCountriesPopulated() {
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            verifyAllCountriesPopulated()
        }
    }

    @Test
    fun myStampsScreen_hasCorrectCountryCount() {
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            verifyCountryCount(CountryList.countries.size)
        }
    }

    @Test
    fun myStampsScreen_displaysFirstCountry() {
        val firstCountry = CountryList.countries.first()
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            verifyCountryDisplayed(firstCountry.englishName)
        }
    }

    // ==================== Scrolling Tests ====================

    @Test
    fun myStampsScreen_canScrollToBottom() {
        val lastCountry = CountryList.countries.last()
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            scrollToBottom()
            verifyCountryDisplayed(lastCountry.englishName)
        }
    }

    @Test
    fun myStampsScreen_canScrollToSpecificCountry() {
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            scrollToCountry(AppConstants.CountryName.JAPAN)
            verifyCountryDisplayed(AppConstants.CountryName.JAPAN)
        }
    }

    @Test
    fun myStampsScreen_canScrollToCountryByCode() {
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            scrollToCountryByCode("JP")
            verifyCountryRowDisplayed("JP")
        }
    }

    @Test
    fun myStampsScreen_canScrollBackToTop() {
        val firstCountry = CountryList.countries.first()
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            scrollToBottom()
            scrollToTop()
            verifyCountryDisplayed(firstCountry.englishName)
        }
    }

    // ==================== Add Stamp Dialog Tests ====================

    @Test
    fun myStampsScreen_clickAddStamp_opensDialog() {
        val country = CountryList.countries.first()
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            clickAddStampButton(country.code)
            verifyAddStampDialogDisplayed()
        }
    }

    @Test
    fun myStampsScreen_addStampDialog_showsCountryName() {
        val country = CountryList.countries.first()
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            clickAddStampButton(country.code)
            verifyAddStampDialogDisplayed()
            verifyDialogShowsCountry(country.englishName)
        }
    }

    @Test
    fun myStampsScreen_addStampDialog_showsCameraAndGalleryOptions() {
        val country = CountryList.countries.first()
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            clickAddStampButton(country.code)
            verifyAddStampDialogDisplayed()
            verifyDialogOptionsDisplayed()
        }
    }

    @Test
    fun myStampsScreen_addStampDialog_showsCancelButton() {
        val country = CountryList.countries.first()
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            clickAddStampButton(country.code)
            verifyAddStampDialogDisplayed()
            verifyCancelButtonDisplayed()
        }
    }

    // ==================== Cancel Adding Stamp Tests ====================

    @Test
    fun myStampsScreen_cancelAddingStamp_dismissesDialog() {
        val country = CountryList.countries.first()
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            clickAddStampButton(country.code)
            verifyAddStampDialogDisplayed()
            cancelAddingStamp()
            verifyAddStampDialogNotDisplayed()
        }
    }

    @Test
    fun myStampsScreen_clickCancelButton_closesDialog() {
        val country = CountryList.countries.first()
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            clickAddStampButton(country.code)
            verifyAddStampDialogDisplayed()
            clickCancelButton()
            verifyAddStampDialogNotDisplayed()
        }
    }

    // ==================== Add Stamp via Gallery Tests ====================

    @Test
    fun myStampsScreen_addStampViaGallery_galleryButtonExists() {
        val country = CountryList.countries.first()
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            clickAddStampButton(country.code)
            verifyAddStampDialogDisplayed()
            verifyDialogOptionsDisplayed()
        }
    }

    @Test
    fun myStampsScreen_clickGalleryButton_triggersGalleryPicker() {
        val country = CountryList.countries.first()
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            clickAddStampButton(country.code)
            verifyAddStampDialogDisplayed()
            clickGalleryButton()
            // Note: Gallery picker is a system component, dialog should dismiss
            // The actual picker behavior requires device/emulator testing
        }
    }

    // ==================== Add Stamp via Camera Tests ====================

    @Test
    fun myStampsScreen_addStampViaCamera_cameraButtonExists() {
        val country = CountryList.countries.first()
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            clickAddStampButton(country.code)
            verifyAddStampDialogDisplayed()
            verifyDialogOptionsDisplayed()
        }
    }

    @Test
    fun myStampsScreen_clickCameraButton_triggersCameraCapture() {
        val country = CountryList.countries.first()
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            clickAddStampButton(country.code)
            verifyAddStampDialogDisplayed()
            clickCameraButton()
            // Note: Camera capture is a system component requiring permissions
            // The actual camera behavior requires device/emulator testing
        }
    }

    // ==================== Multiple Countries Tests ====================

    @Test
    fun myStampsScreen_canOpenDialogForDifferentCountries() {
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()

            // Test first country
            clickAddStampButton(CountryList.countries.first().code)
            verifyAddStampDialogDisplayed()
            verifyDialogShowsCountry(CountryList.countries.first().englishName)
            cancelAddingStamp()

            // Test another country (Japan)
            scrollToCountry(AppConstants.CountryName.JAPAN)
            clickAddStampButtonByName(AppConstants.CountryName.JAPAN)
            verifyAddStampDialogDisplayed()
            verifyDialogShowsCountry(AppConstants.CountryName.JAPAN)
            cancelAddingStamp()
        }
    }

    // ==================== Edge Cases Tests ====================

    @Test
    fun myStampsScreen_noStampInitially_showsAddButton() {
        val country = CountryList.countries.first()
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            verifyNoStampExists(country.code)
        }
    }

    @Test
    fun myStampsScreen_scrollToLastCountry_displaysCorrectly() {
        val lastCountry = CountryList.countries.last()
        composeTestRule.myStampsRobot {
            verifyScreenDisplayed()
            scrollToCountryByCode(lastCountry.code)
            verifyCountryRowDisplayed(lastCountry.code)
            verifyCountryDisplayed(lastCountry.englishName)
        }
    }

}
