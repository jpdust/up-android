package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
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
        visaRequirement: String = "Visa not required",
        safetyLevel: SafetyLevel = SafetyLevel.LOW,
        outletType: String = "Type A/B (120V)",
        continent: Continent = Continent.NORTH_AMERICA,
        flagEmoji: String = "\uD83C\uDDFA\uD83C\uDDF8"
    ) = Country(
        id = id,
        name = name,
        safetyLevel = safetyLevel,
        visaRequirement = visaRequirement,
        currency = currency,
        currencyCode = currencyCode,
        exchangeRateToUSD = exchangeRateToUSD,
        outletType = outletType,
        continent = continent,
        flagEmoji = flagEmoji
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
    fun countryDetailSheet_nonUsdCountry_showsCurrencyConverter() {
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

        // Verify currency converter IS displayed for non-USD countries
        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
        composeTestRule.onNodeWithText("EUR").assertIsDisplayed()
        composeTestRule.onNodeWithText("=").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_japaneseYen_showsCurrencyConverter() {
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

        // Verify currency converter IS displayed
        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
        composeTestRule.onNodeWithText("JPY").assertIsDisplayed()
        composeTestRule.onNodeWithText("=").assertIsDisplayed()
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
    fun countryDetailSheet_britishPound_showsCurrencyConverter() {
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

        // Verify currency converter IS displayed
        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
        composeTestRule.onNodeWithText("GBP").assertIsDisplayed()
        composeTestRule.onNodeWithText("=").assertIsDisplayed()
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

    // ============================================================
    // Currency Converter Calculation Tests
    // ============================================================

    @Test
    fun currencyConverter_euro_initialValueCalculation() {
        // EUR with exchangeRateToUSD = 1.08 means 1 EUR = 1.08 USD
        // So 1 USD = 1/1.08 = 0.93 EUR (approximately)
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

        // Initial USD amount should be "1"
        composeTestRule.onNodeWithText("1").assertExists()
        // Initial EUR amount should be approximately 0.93 (1/1.08)
        composeTestRule.onNodeWithText("0.93").assertExists()
    }

    @Test
    fun currencyConverter_japaneseYen_initialValueCalculation() {
        // JPY with exchangeRateToUSD = 0.0067 means 1 JPY = 0.0067 USD
        // So 1 USD = 1/0.0067 = 149.25 JPY (approximately)
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

        // Initial USD amount should be "1"
        composeTestRule.onNodeWithText("1").assertExists()
        // Initial JPY amount should be approximately 149.25 (1/0.0067)
        composeTestRule.onNodeWithText("149.25").assertExists()
    }

    @Test
    fun currencyConverter_britishPound_initialValueCalculation() {
        // GBP with exchangeRateToUSD = 1.27 means 1 GBP = 1.27 USD
        // So 1 USD = 1/1.27 = 0.79 GBP (approximately)
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

        // Initial USD amount should be "1"
        composeTestRule.onNodeWithText("1").assertExists()
        // Initial GBP amount should be approximately 0.79 (1/1.27)
        composeTestRule.onNodeWithText("0.79").assertExists()
    }

    @Test
    fun currencyConverter_mexicanPeso_initialValueCalculation() {
        // MXN with exchangeRateToUSD = 0.058 means 1 MXN = 0.058 USD
        // So 1 USD = 1/0.058 = 17.24 MXN (approximately)
        val mxnCountry = createCountry(
            id = "mx",
            name = "Mexico",
            currencyCode = "MXN",
            currency = "Mexican Peso",
            exchangeRateToUSD = 0.058
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = mxnCountry,
                visible = true,
                onDismiss = {}
            )
        }

        // Initial USD amount should be "1"
        composeTestRule.onNodeWithText("1").assertExists()
        // Initial MXN amount should be approximately 17.24 (1/0.058)
        composeTestRule.onNodeWithText("17.24").assertExists()
    }

    @Test
    fun currencyConverter_showsCurrencyCode() {
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

        // Currency codes should be displayed
        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
        composeTestRule.onNodeWithText("EUR").assertIsDisplayed()
    }

    @Test
    fun currencyConverter_showsEqualsSign() {
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

        // Equals sign should be displayed
        composeTestRule.onNodeWithText("=").assertIsDisplayed()
    }

    // ============================================================
    // Safety Level Display Tests
    // ============================================================

    @Test
    fun countryDetailSheet_showsLowRiskSafetyLevel() {
        val country = createCountry(
            name = "Safe Country",
            safetyLevel = SafetyLevel.LOW
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Safety Level").assertIsDisplayed()
        composeTestRule.onNodeWithText("Low Risk").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsMediumRiskSafetyLevel() {
        val country = createCountry(
            name = "Medium Risk Country",
            safetyLevel = SafetyLevel.MEDIUM
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Safety Level").assertIsDisplayed()
        composeTestRule.onNodeWithText("Medium Risk").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsHighRiskSafetyLevel() {
        val country = createCountry(
            name = "High Risk Country",
            safetyLevel = SafetyLevel.HIGH
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Safety Level").assertIsDisplayed()
        composeTestRule.onNodeWithText("High Risk").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsExtremeRiskSafetyLevel() {
        val country = createCountry(
            name = "Extreme Risk Country",
            safetyLevel = SafetyLevel.EXTREME
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Safety Level").assertIsDisplayed()
        composeTestRule.onNodeWithText("Extreme Risk").assertIsDisplayed()
    }

    // ============================================================
    // Entry Requirement Display Tests
    // ============================================================

    @Test
    fun countryDetailSheet_showsVisaNotRequired() {
        val country = createCountry(
            visaRequirement = "Visa not required"
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Entry Requirement").assertIsDisplayed()
        composeTestRule.onNodeWithText("Visa not required").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsVisaRequired() {
        val country = createCountry(
            visaRequirement = "Visa required"
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Entry Requirement").assertIsDisplayed()
        composeTestRule.onNodeWithText("Visa required").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsEVisa() {
        val country = createCountry(
            visaRequirement = "eVisa"
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Entry Requirement").assertIsDisplayed()
        composeTestRule.onNodeWithText("eVisa").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsVisaOnArrival() {
        val country = createCountry(
            visaRequirement = "Visa on arrival"
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Entry Requirement").assertIsDisplayed()
        composeTestRule.onNodeWithText("Visa on arrival").assertIsDisplayed()
    }

    // ============================================================
    // Outlet Type Display Tests
    // ============================================================

    @Test
    fun countryDetailSheet_showsOutletTypeA() {
        val country = createCountry(
            outletType = "Type A/B (120V)"
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Power Outlet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Type A/B (120V)").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsOutletTypeG() {
        val country = createCountry(
            outletType = "Type G (230V)"
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Power Outlet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Type G (230V)").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsOutletTypeC() {
        val country = createCountry(
            outletType = "Type C/F (230V)"
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Power Outlet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Type C/F (230V)").assertIsDisplayed()
    }

    // ============================================================
    // Continent Display Tests
    // ============================================================

    @Test
    fun countryDetailSheet_showsNorthAmericaContinent() {
        val country = createCountry(
            name = "United States",
            continent = Continent.NORTH_AMERICA
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("North America").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsSouthAmericaContinent() {
        val country = createCountry(
            name = "Brazil",
            continent = Continent.SOUTH_AMERICA
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("South America").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsEuropeContinent() {
        val country = createCountry(
            name = "France",
            continent = Continent.EUROPE
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Europe").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsAfricaContinent() {
        val country = createCountry(
            name = "Kenya",
            continent = Continent.AFRICA
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Africa").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsAsiaContinent() {
        val country = createCountry(
            name = "Japan",
            continent = Continent.ASIA
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Asia").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsOceaniaContinent() {
        val country = createCountry(
            name = "Australia",
            continent = Continent.OCEANIA
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Oceania").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsAntarcticaContinent() {
        val country = createCountry(
            name = "Antarctic Research Station",
            continent = Continent.ANTARCTICA
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        // Verify the country name is shown
        composeTestRule.onNodeWithText("Antarctic Research Station").assertIsDisplayed()
        // Verify the continent is shown
        composeTestRule.onNodeWithText("Antarctica").assertIsDisplayed()
    }

    // ============================================================
    // Header Content Tests
    // ============================================================

    @Test
    fun countryDetailSheet_showsFlagEmoji() {
        val country = createCountry(
            name = "France",
            flagEmoji = "\uD83C\uDDEB\uD83C\uDDF7" // French flag
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("\uD83C\uDDEB\uD83C\uDDF7").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsFullCountryName() {
        val country = createCountry(
            name = "United States of America"
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("United States of America").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsCountryWithSpecialCharacters() {
        val country = createCountry(
            name = "Côte d'Ivoire"
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Côte d'Ivoire").assertIsDisplayed()
    }

    // ============================================================
    // Null Country Handling Tests
    // ============================================================

    @Test
    fun countryDetailSheet_nullCountry_hidesContent() {
        composeTestRule.setContent {
            CountryDetailSheet(
                country = null,
                visible = true,
                onDismiss = {}
            )
        }

        // When country is null, no country-specific content should be displayed
        composeTestRule.onNodeWithText("Safety Level").assertDoesNotExist()
        composeTestRule.onNodeWithText("Entry Requirement").assertDoesNotExist()
        composeTestRule.onNodeWithText("Currency").assertDoesNotExist()
    }

    @Test
    fun countryDetailSheet_nullCountryNotVisible_hidesContent() {
        composeTestRule.setContent {
            CountryDetailSheet(
                country = null,
                visible = false,
                onDismiss = {}
            )
        }

        // No content should be displayed
        composeTestRule.onNodeWithText("Safety Level").assertDoesNotExist()
    }

    // ============================================================
    // Edge Case Tests
    // ============================================================

    @Test
    fun currencyConverter_zeroExchangeRate_handlesGracefully() {
        // Edge case: exchange rate is 0
        val country = createCountry(
            currencyCode = "XXX",
            currency = "Unknown Currency",
            exchangeRateToUSD = 0.0
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        // Should display 0.00 for the foreign amount when exchange rate is 0
        composeTestRule.onNodeWithText("0.00").assertExists()
    }

    @Test
    fun currencyConverter_verySmallExchangeRate_displaysLargeAmount() {
        // Very small exchange rate (like Vietnamese Dong)
        // 1 VND = 0.00004 USD, so 1 USD = 25,000 VND
        val vndCountry = createCountry(
            id = "vn",
            name = "Vietnam",
            currencyCode = "VND",
            currency = "Vietnamese Dong",
            exchangeRateToUSD = 0.00004
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = vndCountry,
                visible = true,
                onDismiss = {}
            )
        }

        // Should display a large number for the foreign amount
        // 1 / 0.00004 = 25000
        composeTestRule.onNodeWithText("25000.00").assertExists()
    }

    @Test
    fun currencyConverter_veryLargeExchangeRate_displaysSmallAmount() {
        // Very large exchange rate (hypothetical strong currency)
        // 1 XXX = 10 USD, so 1 USD = 0.10 XXX
        val strongCountry = createCountry(
            id = "xx",
            name = "Strong Country",
            currencyCode = "XXX",
            currency = "Strong Currency",
            exchangeRateToUSD = 10.0
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = strongCountry,
                visible = true,
                onDismiss = {}
            )
        }

        // Should display 0.10 for the foreign amount
        composeTestRule.onNodeWithText("0.10").assertExists()
    }

    @Test
    fun countryDetailSheet_showsAllFieldsTogether() {
        // Comprehensive test that all fields are displayed together
        val country = createCountry(
            name = "Germany",
            safetyLevel = SafetyLevel.LOW,
            visaRequirement = "Visa not required",
            currency = "Euro",
            currencyCode = "EUR",
            exchangeRateToUSD = 1.08,
            outletType = "Type C/F (230V)",
            continent = Continent.EUROPE,
            flagEmoji = "\uD83C\uDDE9\uD83C\uDDEA"
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        // Header
        composeTestRule.onNodeWithText("Germany").assertIsDisplayed()
        composeTestRule.onNodeWithText("\uD83C\uDDE9\uD83C\uDDEA").assertIsDisplayed()
        composeTestRule.onNodeWithText("Europe").assertIsDisplayed()

        // Info rows
        composeTestRule.onNodeWithText("Safety Level").assertIsDisplayed()
        composeTestRule.onNodeWithText("Low Risk").assertIsDisplayed()
        composeTestRule.onNodeWithText("Entry Requirement").assertIsDisplayed()
        composeTestRule.onNodeWithText("Visa not required").assertIsDisplayed()
        composeTestRule.onNodeWithText("Currency").assertIsDisplayed()
        composeTestRule.onNodeWithText("Euro (EUR)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Power Outlet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Type C/F (230V)").assertIsDisplayed()

        // Currency converter (non-USD)
        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
        composeTestRule.onNodeWithText("EUR").assertIsDisplayed()
        composeTestRule.onNodeWithText("=").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_longCurrencyName_displays() {
        val country = createCountry(
            currency = "United Arab Emirates Dirham",
            currencyCode = "AED"
        )

        composeTestRule.setContent {
            CountryDetailSheet(
                country = country,
                visible = true,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("United Arab Emirates Dirham (AED)").assertIsDisplayed()
    }
}
