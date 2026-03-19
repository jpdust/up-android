package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.model.Continent
import com.unstampedpages.app.data.model.Country
import com.unstampedpages.app.data.model.SafetyLevel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountryDetailSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createCountry(
        id: String = "test",
        name: String = "Test Country",
        currencyCode: String = "USD",
        currency: String = "US Dollar",
        exchangeRateToUSD: Double = 1.0,
        visaRequirement: String = "Visa not required"
    ) = Country(
        id = id,
        name = name,
        safetyLevel = SafetyLevel.LOW,
        visaRequirement = visaRequirement,
        currency = currency,
        currencyCode = currencyCode,
        exchangeRateToUSD = exchangeRateToUSD,
        outletType = "Type A/B (120V)",
        continent = Continent.NORTH_AMERICA,
        flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
    )

    @Test
    fun countryDetailSheet_usdCountry_hidesExchangeRate() {
        val usdCountry = createCountry(
            id = "us",
            name = "United States",
            currencyCode = "USD",
            currency = "US Dollar",
            exchangeRateToUSD = 1.0
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = usdCountry,
                visible = true,
                onDismiss = {}
            )
        }

        // Verify currency is displayed
        composeTestRule.onNodeWithText("US Dollar (USD)").assertIsDisplayed()

        // Verify exchange rate text is NOT displayed for USD countries
        composeTestRule.onNodeWithText("1 USD =").assertDoesNotExist()
    }

    @Test
    fun countryDetailSheet_nonUsdCountry_showsExchangeRate() {
        val euroCountry = createCountry(
            id = "fr",
            name = "France",
            currencyCode = "EUR",
            currency = "Euro",
            exchangeRateToUSD = 1.08
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = euroCountry,
                visible = true,
                onDismiss = {}
            )
        }

        // Verify currency is displayed
        composeTestRule.onNodeWithText("Euro (EUR)").assertIsDisplayed()

        // Verify exchange rate IS displayed for non-USD countries
        composeTestRule.onNodeWithText("1 USD =").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_japaneseYen_showsExchangeRate() {
        val jpyCountry = createCountry(
            id = "jp",
            name = "Japan",
            currencyCode = "JPY",
            currency = "Japanese Yen",
            exchangeRateToUSD = 0.0067
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = jpyCountry,
                visible = true,
                onDismiss = {}
            )
        }

        // Verify currency is displayed
        composeTestRule.onNodeWithText("Japanese Yen (JPY)").assertIsDisplayed()

        // Verify exchange rate IS displayed
        composeTestRule.onNodeWithText("1 USD =").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_panamaUsd_hidesExchangeRate() {
        // Panama uses USD as its currency
        val panamaCountry = createCountry(
            id = "pa",
            name = "Panama",
            currencyCode = "USD",
            currency = "US Dollar",
            exchangeRateToUSD = 1.0
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = panamaCountry,
                visible = true,
                onDismiss = {}
            )
        }

        // Verify currency is displayed
        composeTestRule.onNodeWithText("US Dollar (USD)").assertIsDisplayed()

        // Verify exchange rate is NOT displayed for Panama (uses USD)
        composeTestRule.onNodeWithText("1 USD =").assertDoesNotExist()
    }

    @Test
    fun countryDetailSheet_ecuadorUsd_hidesExchangeRate() {
        // Ecuador uses USD as its currency
        val ecuadorCountry = createCountry(
            id = "ec",
            name = "Ecuador",
            currencyCode = "USD",
            currency = "US Dollar",
            exchangeRateToUSD = 1.0
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = ecuadorCountry,
                visible = true,
                onDismiss = {}
            )
        }

        // Verify exchange rate is NOT displayed for Ecuador (uses USD)
        composeTestRule.onNodeWithText("1 USD =").assertDoesNotExist()
    }

    @Test
    fun countryDetailSheet_britishPound_showsExchangeRate() {
        val gbpCountry = createCountry(
            id = "gb",
            name = "United Kingdom",
            currencyCode = "GBP",
            currency = "British Pound",
            exchangeRateToUSD = 1.27
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = gbpCountry,
                visible = true,
                onDismiss = {}
            )
        }

        // Verify currency is displayed
        composeTestRule.onNodeWithText("British Pound (GBP)").assertIsDisplayed()

        // Verify exchange rate IS displayed
        composeTestRule.onNodeWithText("1 USD =").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_notVisible_hidesContent() {
        val country = createCountry()

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = false,
                onDismiss = {}
            )
        }

        // When not visible, country name should not be displayed
        composeTestRule.onNodeWithText("Test Country").assertDoesNotExist()
    }

    @Test
    fun countryDetailSheet_visible_showsCountryName() {
        val country = createCountry(name = "Test Country")

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        // When visible, country name should be displayed
        composeTestRule.onNodeWithText("Test Country").assertIsDisplayed()
    }
}
