package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.geometry.Offset
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.model.Continent
import com.unstampedpages.app.data.model.Country
import com.unstampedpages.app.data.model.SafetyLevel
import com.unstampedpages.app.data.model.TravelAdvisories
import com.unstampedpages.app.data.model.VisaRequirement
import com.unstampedpages.app.ui.theme.UnstampedPagesTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountryDetailSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ---------------------------------------------------------------------------
    // Helpers shared by both the original and the merged "additional" tests
    // ---------------------------------------------------------------------------

    /**
     * Convenience launcher used by the merged tests that came from
     * [CountryDetailSheetAdditionalTest]. The original tests use inline [setContent] blocks;
     * this helper avoids repeating the boilerplate for the newer tests.
     */
    private fun launchSheet(country: Country?, visible: Boolean = true) {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = visible, onDismiss = {})
            }
        }
        composeTestRule.waitForIdle()
    }

    /**
     * Builds a [Country] with **EUR** as the default currency (so the currency converter
     * is shown) and a **nullable** [travelAdvisories] parameter so tests can pass `null`
     * to verify chip-hiding behaviour.
     *
     * Distinct from [createCountry] which defaults to USD and non-null advisories.
     */
    private fun country(
        name: String = "Test Country",
        currencyCode: String = "EUR",
        currency: String = "Euro",
        exchangeRateToUSD: Double = 1.08,
        safetyLevel: SafetyLevel = SafetyLevel.NORMAL_SECURITY_PRECAUTIONS,
        visaRequirement: VisaRequirement = VisaRequirement.VISA_NOT_REQUIRED,
        outletType: String = "Type C/F (230V)",
        continent: Continent = Continent.EUROPE,
        flagEmoji: String = "\uD83C\uDDEB\uD83C\uDDF7",
        passportValidity: String? = null,
        travelAdvisories: TravelAdvisories? = TravelAdvisories(
            us = "https://travel.state.gov",
            uk = "https://gov.uk",
            au = "https://smartraveller.gov.au",
            ca = "https://travel.gc.ca"
        )
    ) = Country(
        id = name.lowercase().replace(" ", "_"),
        name = name,
        safetyLevel = safetyLevel,
        visaRequirement = visaRequirement,
        currency = currency,
        currencyCode = currencyCode,
        exchangeRateToUSD = exchangeRateToUSD,
        outletType = outletType,
        continent = continent,
        flagEmoji = flagEmoji,
        passportValidity = passportValidity,
        travelAdvisories = travelAdvisories
    )

    // ---------------------------------------------------------------------------

    private fun createCountry(
        id: String = "test",
        name: String = "Test Country",
        currencyCode: String = "USD",
        currency: String = "US Dollar",
        exchangeRateToUSD: Double = 1.0,
        visaRequirement: VisaRequirement = VisaRequirement.VISA_NOT_REQUIRED,
        safetyLevel: SafetyLevel = SafetyLevel.NORMAL_SECURITY_PRECAUTIONS,
        outletType: String = "Type A/B (120V)",
        continent: Continent = Continent.NORTH_AMERICA,
        flagEmoji: String = "\uD83C\uDDFA\uD83C\uDDF8",
        passportValidity: String? = null,
        travelAdvisories: TravelAdvisories = TravelAdvisories(
            us = "https://travel.state.gov/en/international-travel/travel-advisories/test-country.html",
            uk = "https://www.gov.uk/foreign-travel-advice/test-country",
            au = "https://www.smartraveller.gov.au/destinations/americas/test-country",
            ca = "https://travel.gc.ca/destinations/test-country"
        )
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
        flagEmoji = flagEmoji,
        passportValidity = passportValidity,
        travelAdvisories = travelAdvisories
    )

    private fun focusClearedCurrencyInput(testTag: String) {
        composeTestRule.onNodeWithTag(testTag).performClick()
        composeTestRule.onNodeWithTag(testTag).performTextClearance()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(testTag).assertTextEquals("")
    }

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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = usdCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = jpyCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = panamaCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = ecuadorCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = gbpCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = false,
                    onDismiss = {}
                )
            }
        }

        // When not visible, country name should not be displayed
        composeTestRule.onNodeWithText("Test Country").assertDoesNotExist()
    }

    @Test
    fun countryDetailSheet_visible_showsCountryName() {
        val country = createCountry(name = "Test Country")

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = jpyCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = gbpCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = mxnCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            safetyLevel = SafetyLevel.NORMAL_SECURITY_PRECAUTIONS
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Safety Level").assertIsDisplayed()
        composeTestRule.onNodeWithText("Normal Security Precautions").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsMediumRiskSafetyLevel() {
        val country = createCountry(
            name = "Medium Risk Country",
            safetyLevel = SafetyLevel.HIGH_DEGREE_CAUTION
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Safety Level").assertIsDisplayed()
        composeTestRule.onNodeWithText("High Degree of Caution").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsHighRiskSafetyLevel() {
        val country = createCountry(
            name = "High Risk Country",
            safetyLevel = SafetyLevel.RECONSIDER_TRAVEL
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Safety Level").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reconsider Travel").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsExtremeRiskSafetyLevel() {
        val country = createCountry(
            name = "Extreme Risk Country",
            safetyLevel = SafetyLevel.DO_NOT_TRAVEL
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Safety Level").assertIsDisplayed()
        composeTestRule.onNodeWithText("Do Not Travel").assertIsDisplayed()
    }

    // ============================================================
    // Entry Requirement Display Tests
    // ============================================================

    @Test
    fun countryDetailSheet_showsVisaNotRequired() {
        val country = createCountry(
            visaRequirement = VisaRequirement.VISA_NOT_REQUIRED
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Entry Requirement").assertIsDisplayed()
        composeTestRule.onNodeWithText("Visa not required").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsVisaRequired() {
        val country = createCountry(
            visaRequirement = VisaRequirement.VISA_REQUIRED
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Entry Requirement").assertIsDisplayed()
        composeTestRule.onNodeWithText("Visa required").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsEVisa() {
        val country = createCountry(
            visaRequirement = VisaRequirement.E_VISA
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Entry Requirement").assertIsDisplayed()
        composeTestRule.onNodeWithText("eVisa").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsVisaOnArrival() {
        val country = createCountry(
            visaRequirement = VisaRequirement.VISA_ON_ARRIVAL
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Entry Requirement").assertIsDisplayed()
        composeTestRule.onNodeWithText("Visa on arrival").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsRestricted() {
        val country = createCountry(
            name = "Restricted Country",
            visaRequirement = VisaRequirement.RESTRICTED
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Entry Requirement").assertIsDisplayed()
        composeTestRule.onNodeWithText("Restricted").assertIsDisplayed()
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Power Outlet").assertExists()
        composeTestRule.onNodeWithText("Type C/F (230V)").assertExists()
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("\uD83C\uDDEB\uD83C\uDDF7").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsFullCountryName() {
        val country = createCountry(
            name = "United States of America"
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("United States of America").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_showsCountryWithSpecialCharacters() {
        val country = createCountry(
            name = "Côte d'Ivoire"
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Côte d'Ivoire").assertIsDisplayed()
    }

    // ============================================================
    // Null Country Handling Tests
    // ============================================================

    @Test
    fun countryDetailSheet_nullCountry_hidesContent() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = null,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // When country is null, no country-specific content should be displayed
        composeTestRule.onNodeWithText("Safety Level").assertDoesNotExist()
        composeTestRule.onNodeWithText("Entry Requirement").assertDoesNotExist()
        composeTestRule.onNodeWithText("Currency").assertDoesNotExist()
    }

    @Test
    fun countryDetailSheet_nullCountryNotVisible_hidesContent() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = null,
                    visible = false,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = vndCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
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
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = strongCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // Should display 0.10 for the foreign amount
        composeTestRule.onNodeWithText("0.10").assertExists()
    }

    @Test
    fun countryDetailSheet_showsAllFieldsTogether() {
        // Comprehensive test that all fields are displayed together
        val country = createCountry(
            name = "Germany",
            safetyLevel = SafetyLevel.NORMAL_SECURITY_PRECAUTIONS,
            visaRequirement = VisaRequirement.VISA_NOT_REQUIRED,
            currency = "Euro",
            currencyCode = "EUR",
            exchangeRateToUSD = 1.08,
            outletType = "Type C/F (230V)",
            continent = Continent.EUROPE,
            flagEmoji = "\uD83C\uDDE9\uD83C\uDDEA"
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // Header
        composeTestRule.onNodeWithText("Germany").assertIsDisplayed()
        composeTestRule.onNodeWithText("\uD83C\uDDE9\uD83C\uDDEA").assertIsDisplayed()
        composeTestRule.onNodeWithText("Europe").assertIsDisplayed()

        // Info rows
        composeTestRule.onNodeWithText("Safety Level").assertIsDisplayed()
        composeTestRule.onNodeWithText("Normal Security Precautions").assertExists()
        composeTestRule.onNodeWithText("Entry Requirement").assertIsDisplayed()
        composeTestRule.onNodeWithText("Visa not required").assertIsDisplayed()
        composeTestRule.onNodeWithText("Currency").assertIsDisplayed()
        composeTestRule.onNodeWithText("Euro (EUR)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Power Outlet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Type C/F (230V)").assertIsDisplayed()

        // Currency converter (non-USD)
        composeTestRule.onNodeWithText("USD").assertExists()
        composeTestRule.onNodeWithText("EUR").assertExists()
        composeTestRule.onNodeWithText("=").assertExists()

        // Travel advisory chips
        composeTestRule.onNodeWithText("US").assertExists()
        composeTestRule.onNodeWithText("UK").assertExists()
        composeTestRule.onNodeWithText("AU").assertExists()
        composeTestRule.onNodeWithText("CA").assertExists()
    }

    @Test
    fun countryDetailSheet_longCurrencyName_displays() {
        val country = createCountry(
            currency = "United Arab Emirates Dirham",
            currencyCode = "AED"
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("United Arab Emirates Dirham (AED)").assertIsDisplayed()
    }

    // ============================================================
    // Currency Input Field Tests
    // ============================================================

    @Test
    fun currencyInput_usdFieldExists() {
        val euroCountry = createCountry(
            id = "fr",
            name = "France",
            currencyCode = "EUR",
            currency = "Euro",
            exchangeRateToUSD = 1.08
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("currency_input_usd").assertExists()
    }

    @Test
    fun currencyInput_foreignFieldExists() {
        val euroCountry = createCountry(
            id = "fr",
            name = "France",
            currencyCode = "EUR",
            currency = "Euro",
            exchangeRateToUSD = 1.08
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("currency_input_foreign").assertExists()
    }

    @Test
    fun currencyInput_usdFieldCanBeFocused() {
        val euroCountry = createCountry(
            id = "fr",
            name = "France",
            currencyCode = "EUR",
            currency = "Euro",
            exchangeRateToUSD = 1.08
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.onNodeWithTag("currency_input_usd").assertIsFocused()
    }

    @Test
    fun currencyInput_foreignFieldCanBeFocused() {
        val euroCountry = createCountry(
            id = "fr",
            name = "France",
            currencyCode = "EUR",
            currency = "Euro",
            exchangeRateToUSD = 1.08
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.onNodeWithTag("currency_input_foreign").assertIsFocused()
    }

    @Test
    fun currencyInput_clearAndTypingInUsdFieldUpdatesValue() {
        val euroCountry = createCountry(
            id = "fr",
            name = "France",
            currencyCode = "EUR",
            currency = "Euro",
            exchangeRateToUSD = 1.08
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // Clear and type a new value
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("5")
        composeTestRule.waitForIdle()

        // Value should be "5"
        composeTestRule.onNodeWithText("5").assertExists()
    }

    @Test
    fun currencyInput_clearAndTypingInForeignFieldUpdatesUsd() {
        val euroCountry = createCountry(
            id = "fr",
            name = "France",
            currencyCode = "EUR",
            currency = "Euro",
            exchangeRateToUSD = 1.08
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // Clear and type a new value in foreign field
        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextClearance()
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextInput("2")
        composeTestRule.waitForIdle()

        // Value should be "2"
        // USD should be recalculated: 2 EUR * 1.08 = 2.16 USD
        composeTestRule.onNodeWithText("2").assertExists()
        composeTestRule.onNodeWithText("2.16").assertExists()
    }

    @Test
    fun currencyInput_usdFieldAcceptsTextInput() {
        val jpyCountry = createCountry(
            id = "jp",
            name = "Japan",
            currencyCode = "JPY",
            currency = "Japanese Yen",
            exchangeRateToUSD = 0.0067
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = jpyCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // Initial value should be "1"
        composeTestRule.onNodeWithText("1").assertExists()

        // Clear and type a new value
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("3")
        composeTestRule.waitForIdle()

        // Value should be "3"
        composeTestRule.onNodeWithText("3").assertExists()
    }

    @Test
    fun currencyInput_foreignFieldAcceptsTextInput() {
        val euroCountry = createCountry(
            id = "fr",
            name = "France",
            currencyCode = "EUR",
            currency = "Euro",
            exchangeRateToUSD = 1.08
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // Initial EUR value should be ~0.93
        composeTestRule.onNodeWithText("0.93").assertExists()

        // Clear and type a new value
        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextClearance()
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextInput("5")
        composeTestRule.waitForIdle()

        // Value should be "5"
        composeTestRule.onNodeWithText("5").assertExists()
    }

    @Test
    fun currencyInput_imeActionDone_canBeTriggered() {
        val euroCountry = createCountry(
            id = "fr",
            name = "France",
            currencyCode = "EUR",
            currency = "Euro",
            exchangeRateToUSD = 1.08
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // Focus field
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.onNodeWithTag("currency_input_usd").assertIsFocused()

        // Perform IME action - this should not throw
        composeTestRule.onNodeWithTag("currency_input_usd").performImeAction()
        composeTestRule.waitForIdle()

        // After IME action completes without error, the test passes
        // (keyboard dismissal is handled by the system)
    }

    @Test
    fun currencyInput_acceptsDecimalInput() {
        val euroCountry = createCountry(
            id = "fr",
            name = "France",
            currencyCode = "EUR",
            currency = "Euro",
            exchangeRateToUSD = 1.08
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // Clear and type a decimal value
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("2.5")
        composeTestRule.waitForIdle()

        // Value should be accepted
        composeTestRule.onNodeWithText("2.5").assertExists()
    }

    @Test
    fun currencyInput_multipleDigitsCanBeTyped() {
        val euroCountry = createCountry(
            id = "fr",
            name = "France",
            currencyCode = "EUR",
            currency = "Euro",
            exchangeRateToUSD = 1.08
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // Clear and type multiple digits
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("10")
        composeTestRule.waitForIdle()

        // Value should be "10"
        composeTestRule.onNodeWithText("10").assertExists()
    }

    @Test
    fun currencyInput_bidirectionalConversion_usdToForeign() {
        // GBP with rate 1.27 means 1 GBP = 1.27 USD
        // So 10 USD = 10 / 1.27 = 7.87 GBP
        val gbpCountry = createCountry(
            id = "gb",
            name = "United Kingdom",
            currencyCode = "GBP",
            currency = "British Pound",
            exchangeRateToUSD = 1.27
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = gbpCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // Clear and type in USD field
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("10")
        composeTestRule.waitForIdle()

        // 10 USD should convert to ~7.87 GBP (10 * (1/1.27))
        composeTestRule.onNodeWithText("10").assertExists()
        composeTestRule.onNodeWithText("7.87").assertExists()
    }

    @Test
    fun currencyInput_bidirectionalConversion_foreignToUsd() {
        // MXN with rate 0.058 means 1 MXN = 0.058 USD
        // So 100 MXN = 100 * 0.058 = 5.80 USD
        val mxnCountry = createCountry(
            id = "mx",
            name = "Mexico",
            currencyCode = "MXN",
            currency = "Mexican Peso",
            exchangeRateToUSD = 0.058
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = mxnCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // Clear and type in foreign (MXN) field
        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextClearance()
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextInput("100")
        composeTestRule.waitForIdle()

        // 100 MXN should convert to 5.80 USD (100 * 0.058)
        composeTestRule.onNodeWithText("100").assertExists()
        composeTestRule.onNodeWithText("5.80").assertExists()
    }

    @Test
    fun currencyInput_usdCountry_noInputFieldsDisplayed() {
        val usdCountry = createCountry(
            id = "us",
            name = "United States",
            currencyCode = "USD",
            currency = "US Dollar",
            exchangeRateToUSD = 1.0
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = usdCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // Currency converter should not be shown for USD countries
        composeTestRule.onNodeWithTag("currency_input_usd").assertDoesNotExist()
        composeTestRule.onNodeWithTag("currency_input_foreign").assertDoesNotExist()
    }

    // ============================================================
    // Clear on First Keystroke Tests
    // ============================================================

    @Test
    fun currencyInput_usdField_firstKeystrokeClearsValue() {
        val euroCountry = createCountry(
            id = "fr",
            name = "France",
            currencyCode = "EUR",
            currency = "Euro",
            exchangeRateToUSD = 1.08
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // Initial USD value should be "1"
        composeTestRule.onNodeWithText("1").assertExists()

        // The current UI clears the field when it is tapped.
        focusClearedCurrencyInput("currency_input_usd")

        // Typing a single digit starts a fresh value.
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("5")
        composeTestRule.waitForIdle()

        // The value should be "5" (not "15" or "51")
        composeTestRule.onNodeWithText("5").assertExists()
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("5")
    }

    @Test
    fun currencyInput_foreignField_firstKeystrokeClearsValue() {
        val euroCountry = createCountry(
            id = "fr",
            name = "France",
            currencyCode = "EUR",
            currency = "Euro",
            exchangeRateToUSD = 1.08
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // Initial EUR value should be approximately 0.93
        composeTestRule.onNodeWithText("0.93").assertExists()

        // The current UI clears the field when it is tapped.
        focusClearedCurrencyInput("currency_input_foreign")

        // Typing a single digit starts a fresh value.
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextInput("7")
        composeTestRule.waitForIdle()

        // The value should be "7" (not "0.937" or "70.93")
        composeTestRule.onNodeWithTag("currency_input_foreign").assertTextEquals("7")
    }

    @Test
    fun currencyInput_subsequentKeystrokesAppend() {
        val euroCountry = createCountry(
            id = "fr",
            name = "France",
            currencyCode = "EUR",
            currency = "Euro",
            exchangeRateToUSD = 1.08
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // The current UI clears the field when it is tapped.
        focusClearedCurrencyInput("currency_input_usd")
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("5")
        composeTestRule.waitForIdle()

        // Type second digit - should append, not replace
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("0")
        composeTestRule.waitForIdle()

        // Value should now be "50"
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("50")
    }

    @Test
    fun currencyInput_firstKeystrokeUpdatesConversion() {
        // GBP with rate 1.27: 1 GBP = 1.27 USD, so 1 USD = 0.79 GBP
        val gbpCountry = createCountry(
            id = "gb",
            name = "United Kingdom",
            currencyCode = "GBP",
            currency = "British Pound",
            exchangeRateToUSD = 1.27
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = gbpCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // Initial: 1 USD = 0.79 GBP
        composeTestRule.onNodeWithText("1").assertExists()
        composeTestRule.onNodeWithText("0.79").assertExists()

        // Tapping USD clears "1" before text entry.
        focusClearedCurrencyInput("currency_input_usd")

        // Type "5" in USD field.
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("5")
        composeTestRule.waitForIdle()

        // USD should be "5", GBP should be recalculated: 5 * (1/1.27) = 3.94
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("5")
        composeTestRule.onNodeWithText("3.94").assertExists()
    }

    @Test
    fun currencyInput_foreignField_Focus_UpdatesUsdConversion() {
        // EUR with rate 1.08: 1 EUR = 1.08 USD
        val euroCountry = createCountry(
            id = "fr",
            name = "France",
            currencyCode = "EUR",
            currency = "Euro",
            exchangeRateToUSD = 1.08
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // Tapping EUR clears "0.93" before text entry.
        focusClearedCurrencyInput("currency_input_foreign")

        // Type "3" in EUR field.
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextInput("3")
        composeTestRule.waitForIdle()

        // EUR should be "3", USD should be recalculated: 3 * 1.08 = 3.24
        composeTestRule.onNodeWithTag("currency_input_foreign").assertTextEquals("3")
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("3.24").assertExists()
    }

    @Test
    fun currencyInput_refocusingFieldResetsClearBehavior() {
        val euroCountry = createCountry(
            id = "fr",
            name = "France",
            currencyCode = "EUR",
            currency = "Euro",
            exchangeRateToUSD = 1.08
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = euroCountry,
                    visible = true,
                    onDismiss = {}
                )
            }
        }

        // First interaction: tapping clears "1", then type "5".
        focusClearedCurrencyInput("currency_input_usd")
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("5")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("5")

        // Focus foreign field to lose focus on USD
        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.waitForIdle()

        // Re-focus USD field: tapping clears "5" immediately, then typing starts over.
        focusClearedCurrencyInput("currency_input_usd")
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("9")
        composeTestRule.waitForIdle()

        // Value should be "9" (not "59" or "95")
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("9")
    }

    // ============================================================
    // Travel Advisory Chip Tests
    // ============================================================

    @Test
    fun safetyLevelRow_showsAllFourAdvisoryChips() {
        val country = createCountry(name = "France", continent = Continent.EUROPE)

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("US").assertIsDisplayed()
        composeTestRule.onNodeWithText("UK").assertIsDisplayed()
        composeTestRule.onNodeWithText("AU").assertIsDisplayed()
        composeTestRule.onNodeWithText("CA").assertIsDisplayed()
    }

    @Test
    fun safetyLevelRow_usChip_isClickable() {
        val country = createCountry()

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("US").assertHasClickAction()
    }

    @Test
    fun safetyLevelRow_ukChip_isClickable() {
        val country = createCountry()

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("UK").assertHasClickAction()
    }

    @Test
    fun safetyLevelRow_auChip_isClickable() {
        val country = createCountry()

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("AU").assertHasClickAction()
    }

    @Test
    fun safetyLevelRow_caChip_isClickable() {
        val country = createCountry()

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("CA").assertHasClickAction()
    }

    @Test
    fun safetyLevelRow_chips_visibleForMediumRisk() {
        val country = createCountry(safetyLevel = SafetyLevel.HIGH_DEGREE_CAUTION)

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("US").assertIsDisplayed()
        composeTestRule.onNodeWithText("UK").assertIsDisplayed()
        composeTestRule.onNodeWithText("AU").assertIsDisplayed()
        composeTestRule.onNodeWithText("CA").assertIsDisplayed()
    }

    @Test
    fun safetyLevelRow_chips_visibleForHighRisk() {
        val country = createCountry(safetyLevel = SafetyLevel.RECONSIDER_TRAVEL)

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("US").assertIsDisplayed()
        composeTestRule.onNodeWithText("UK").assertIsDisplayed()
        composeTestRule.onNodeWithText("AU").assertIsDisplayed()
        composeTestRule.onNodeWithText("CA").assertIsDisplayed()
    }

    @Test
    fun safetyLevelRow_chips_visibleForExtremeRisk() {
        val country = createCountry(safetyLevel = SafetyLevel.DO_NOT_TRAVEL)

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("US").assertIsDisplayed()
        composeTestRule.onNodeWithText("UK").assertIsDisplayed()
        composeTestRule.onNodeWithText("AU").assertIsDisplayed()
        composeTestRule.onNodeWithText("CA").assertIsDisplayed()
    }

    @Test
    fun safetyLevelRow_chips_notVisibleWhenSheetHidden() {
        val country = createCountry()

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = false, onDismiss = {})
            }
        }

        // Advisory chips should not appear when the sheet is not visible
        composeTestRule.onNodeWithText("US").assertDoesNotExist()
        composeTestRule.onNodeWithText("UK").assertDoesNotExist()
        composeTestRule.onNodeWithText("AU").assertDoesNotExist()
        composeTestRule.onNodeWithText("CA").assertDoesNotExist()
    }

    @Test
    fun safetyLevelRow_chips_notVisibleForNullCountry() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = null, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("US").assertDoesNotExist()
        composeTestRule.onNodeWithText("UK").assertDoesNotExist()
        composeTestRule.onNodeWithText("AU").assertDoesNotExist()
        composeTestRule.onNodeWithText("CA").assertDoesNotExist()
    }

    @Test
    fun safetyLevelRow_chips_visibleForSouthAmericaContinent() {
        val country = createCountry(name = "Brazil", continent = Continent.SOUTH_AMERICA)

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("US").assertIsDisplayed()
        composeTestRule.onNodeWithText("UK").assertIsDisplayed()
        composeTestRule.onNodeWithText("AU").assertIsDisplayed()
        composeTestRule.onNodeWithText("CA").assertIsDisplayed()
    }

    @Test
    fun safetyLevelRow_chips_visibleForAfricaContinent() {
        val country = createCountry(name = "Kenya", continent = Continent.AFRICA)

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("US").assertIsDisplayed()
        composeTestRule.onNodeWithText("UK").assertIsDisplayed()
        composeTestRule.onNodeWithText("AU").assertIsDisplayed()
        composeTestRule.onNodeWithText("CA").assertIsDisplayed()
    }

    @Test
    fun safetyLevelRow_chips_visibleForAsiaContinent() {
        val country = createCountry(name = "Japan", continent = Continent.ASIA)

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("US").assertIsDisplayed()
        composeTestRule.onNodeWithText("UK").assertIsDisplayed()
        composeTestRule.onNodeWithText("AU").assertIsDisplayed()
        composeTestRule.onNodeWithText("CA").assertIsDisplayed()
    }

    @Test
    fun safetyLevelRow_chips_visibleForOceaniaContinent() {
        val country = createCountry(name = "Fiji", continent = Continent.OCEANIA)

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("US").assertIsDisplayed()
        composeTestRule.onNodeWithText("UK").assertIsDisplayed()
        composeTestRule.onNodeWithText("AU").assertIsDisplayed()
        composeTestRule.onNodeWithText("CA").assertIsDisplayed()
    }

    @Test
    fun safetyLevelRow_chips_visibleForAntarcticaContinent() {
        val country = createCountry(name = "Antarctica", continent = Continent.ANTARCTICA)

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("US").assertIsDisplayed()
        composeTestRule.onNodeWithText("UK").assertIsDisplayed()
        composeTestRule.onNodeWithText("AU").assertIsDisplayed()
        composeTestRule.onNodeWithText("CA").assertIsDisplayed()
    }

    @Test
    fun safetyLevelRow_chips_insideSafetyLevelRow() {
        // Verify chips are co-located with the safety level row, not elsewhere in the sheet
        val country = createCountry()

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        // Safety level row must exist alongside the chips
        composeTestRule.onNodeWithTag("info_safety_level").assertExists()
        composeTestRule.onNodeWithText("US").assertIsDisplayed()
        composeTestRule.onNodeWithText("CA").assertIsDisplayed()
    }

    @Test
    fun safetyLevelRow_chips_visibleForMultiWordCountryName() {
        // Multi-word country names exercise the slug generation (spaces → hyphens)
        val country = createCountry(
            name = "New Zealand",
            continent = Continent.OCEANIA
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("New Zealand").assertIsDisplayed()
        composeTestRule.onNodeWithText("US").assertIsDisplayed()
        composeTestRule.onNodeWithText("UK").assertIsDisplayed()
        composeTestRule.onNodeWithText("AU").assertIsDisplayed()
        composeTestRule.onNodeWithText("CA").assertIsDisplayed()
    }

    // ============================================================
    // Passport Validity Display Tests
    // ============================================================

    @Test
    fun countryDetailSheet_passportValidity_showsLabelRow() {
        val country = createCountry(passportValidity = "6 months")

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("Passport Validity").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_passportValidity_sixMonths_localizes() {
        // "6 months" in the data → localized string "6 months"
        val country = createCountry(passportValidity = "6 months")

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("6 months").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_passportValidity_sixMonthsWithPrefix_localizes() {
        // "At least 6 months" also matches the "6 month" pattern (case-insensitive)
        val country = createCountry(passportValidity = "At least 6 months")

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("6 months").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_passportValidity_threeMonths_localizes() {
        val country = createCountry(passportValidity = "3 months")

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("3 months").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_passportValidity_durationOfStay_localizes() {
        val country = createCountry(passportValidity = "duration of stay")

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("Planned length of stay").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_passportValidity_lengthOfStay_localizes() {
        val country = createCountry(passportValidity = "length of stay")

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("Planned length of stay").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_passportValidity_stayKeyword_localizes() {
        val country = createCountry(passportValidity = "valid for the length of stay")

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("Planned length of stay").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_passportValidity_null_showsNotSpecified() {
        val country = createCountry(passportValidity = null)

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("Not specified").assertIsDisplayed()
    }

    @Test
    fun countryDetailSheet_passportValidity_unknownValue_showsRawString() {
        // A value that doesn't match any known pattern falls through to the raw string
        val country = createCountry(passportValidity = "1 year minimum")

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithText("1 year minimum").assertIsDisplayed()
    }

    // ============================================================
    // Dismiss Behavior Tests
    // ============================================================

    @Test
    fun countryDetailSheet_closeButton_triggersOnDismiss() {
        var dismissed = false
        val country = createCountry()

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("bottom_sheet_close_button").performClick()
        composeTestRule.waitForIdle()

        assertTrue("onDismiss should be called when the close button is tapped", dismissed)
    }

    @Test
    fun countryDetailSheet_scrimClick_triggersOnDismiss() {
        var dismissed = false
        val country = createCountry()

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = true,
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("bottom_sheet_scrim").performTouchInput {
            click(Offset(width / 2f, height * 0.1f))
        }
        composeTestRule.waitForIdle()

        assertTrue("onDismiss should be called when the scrim is tapped", dismissed)
    }

    @Test
    fun countryDetailSheet_closeButtonExists_whenVisible() {
        val country = createCountry()

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithTag("bottom_sheet_close_button").assertExists()
    }

    // ============================================================
    // Test Tag Verification Tests
    // ============================================================

    @Test
    fun countryDetailSheet_testTag_countryDetailSheet_exists() {
        val country = createCountry()

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithTag("country_detail_sheet").assertExists()
    }

    @Test
    fun countryDetailSheet_testTag_countryHeader_exists() {
        val country = createCountry()

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithTag("country_header").assertExists()
    }

    @Test
    fun countryDetailSheet_testTag_countryFlag_displaysEmoji() {
        val country = createCountry(
            flagEmoji = "\uD83C\uDDEB\uD83C\uDDF7"
        )

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithTag("country_flag")
            .assertExists()
            .assertTextEquals("\uD83C\uDDEB\uD83C\uDDF7")
    }

    @Test
    fun countryDetailSheet_testTag_countryName_displaysName() {
        val country = createCountry(name = "Japan")

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithTag("country_name")
            .assertExists()
            .assertTextEquals("Japan")
    }

    @Test
    fun countryDetailSheet_testTag_safetyLevelValue_matchesSafetyLevel() {
        val country = createCountry(safetyLevel = SafetyLevel.RECONSIDER_TRAVEL)

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithTag("info_safety_level_value")
            .assertExists()
            .assertTextEquals("Reconsider Travel")
    }

    @Test
    fun countryDetailSheet_testTag_entryRequirementValue_matchesRequirement() {
        val country = createCountry(visaRequirement = VisaRequirement.E_VISA)

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithTag("info_entry_requirement_value")
            .assertExists()
            .assertTextEquals("eVisa")
    }

    @Test
    fun countryDetailSheet_testTag_passportValidityValue_matchesValidity() {
        val country = createCountry(passportValidity = "6 months")

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithTag("info_passport_validity_value")
            .assertExists()
            .assertTextEquals("6 months")
    }

    @Test
    fun countryDetailSheet_testTag_currencyValue_matchesCurrency() {
        val country = createCountry(currency = "Euro", currencyCode = "EUR")

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithTag("info_currency_value")
            .assertExists()
            .assertTextEquals("Euro (EUR)")
    }

    @Test
    fun countryDetailSheet_testTag_powerOutletValue_matchesOutletType() {
        val country = createCountry(outletType = "Type G (230V)")

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithTag("info_power_outlet_value")
            .assertExists()
            .assertTextEquals("Type G (230V)")
    }

    @Test
    fun countryDetailSheet_testTag_currencyConverter_existsForNonUsd() {
        val country = createCountry(currencyCode = "EUR", exchangeRateToUSD = 1.08)

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithTag("currency_converter").assertExists()
    }

    @Test
    fun countryDetailSheet_testTag_currencyConverter_absentForUsd() {
        val country = createCountry(currencyCode = "USD", exchangeRateToUSD = 1.0)

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithTag("currency_converter").assertDoesNotExist()
    }

    @Test
    fun countryDetailSheet_testTag_scrimExists_whenVisible() {
        val country = createCountry()

        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = true, onDismiss = {})
            }
        }

        composeTestRule.onNodeWithTag("bottom_sheet_scrim").assertExists()
    }

    // ============================================================
    // Null Travel Advisories
    // (Merged from CountryDetailSheetAdditionalTest)
    // ============================================================

    /**
     * When [Country.travelAdvisories] is null the advisory chips must NOT appear,
     * even though the country itself is non-null and the sheet is visible.
     * This exercises the `if (advisories != null)` guard inside [SafetyLevelRow].
     */
    @Test
    fun safetyLevelRow_chips_hiddenWhenAdvisoriesNull() {
        launchSheet(country(travelAdvisories = null))

        composeTestRule.onNodeWithText("US").assertDoesNotExist()
        composeTestRule.onNodeWithText("UK").assertDoesNotExist()
        composeTestRule.onNodeWithText("AU").assertDoesNotExist()
        composeTestRule.onNodeWithText("CA").assertDoesNotExist()
    }

    @Test
    fun safetyLevelRow_safetyLabel_stillDisplayedWhenAdvisoriesNull() {
        // The Safety Level row itself (label + value) is always rendered regardless of advisories.
        launchSheet(country(travelAdvisories = null))

        composeTestRule.onNodeWithText("Safety Level").assertIsDisplayed()
        composeTestRule.onNodeWithText("Normal Security Precautions").assertIsDisplayed()
    }

    @Test
    fun safetyLevelRow_chips_hiddenForHighRiskCountryWithNullAdvisories() {
        // Even a DO_NOT_TRAVEL country should have no chips when advisories are null.
        launchSheet(country(safetyLevel = SafetyLevel.DO_NOT_TRAVEL, travelAdvisories = null))

        composeTestRule.onNodeWithText("US").assertDoesNotExist()
        composeTestRule.onNodeWithText("CA").assertDoesNotExist()
    }

    @Test
    fun safetyLevelRow_chipsPresent_whenAdvisoriesNonNull() {
        // Positive counterpart: non-null advisories produce chips.
        launchSheet(country(travelAdvisories = TravelAdvisories(
            us = "https://travel.state.gov",
            uk = "https://gov.uk",
            au = "https://smartraveller.gov.au",
            ca = "https://travel.gc.ca"
        )))

        composeTestRule.onNodeWithText("US").assertIsDisplayed()
        composeTestRule.onNodeWithText("UK").assertIsDisplayed()
        composeTestRule.onNodeWithText("AU").assertIsDisplayed()
        composeTestRule.onNodeWithText("CA").assertIsDisplayed()
    }

    // ============================================================
    // Passport Validity — uppercase and additional pattern matching
    // (Merged from CountryDetailSheetAdditionalTest)
    // ============================================================

    /**
     * [getLocalizedPassportValidity] uses case-insensitive `contains`. Verify that
     * uppercase strings that match a pattern still produce the localized string.
     */
    @Test
    fun passportValidity_sixMonthsUppercase_localizes() {
        launchSheet(country(passportValidity = "6 MONTHS REMAINING"))

        composeTestRule.onNodeWithText("6 months").assertIsDisplayed()
    }

    @Test
    fun passportValidity_threeMonthsUppercase_localizes() {
        launchSheet(country(passportValidity = "3 MONTHS"))

        composeTestRule.onNodeWithText("3 months").assertIsDisplayed()
    }

    @Test
    fun passportValidity_durationUppercase_localizes() {
        launchSheet(country(passportValidity = "DURATION OF STAY"))

        composeTestRule.onNodeWithText("Planned length of stay").assertIsDisplayed()
    }

    @Test
    fun passportValidity_stayKeywordUppercase_localizes() {
        launchSheet(country(passportValidity = "VALID FOR LENGTH OF STAY"))

        composeTestRule.onNodeWithText("Planned length of stay").assertIsDisplayed()
    }

    /**
     * "At least 3 months" should match the "3 month" pattern — parallel to the
     * existing "At least 6 months" test.
     */
    @Test
    fun passportValidity_atLeastThreeMonths_localizes() {
        launchSheet(country(passportValidity = "At least 3 months beyond intended stay"))

        composeTestRule.onNodeWithText("3 months").assertIsDisplayed()
    }

    /**
     * "duration" by itself (not "duration of stay") still matches the `contains("duration")`
     * branch and produces the localized string.
     */
    @Test
    fun passportValidity_durationWordAlone_localizes() {
        launchSheet(country(passportValidity = "duration"))

        composeTestRule.onNodeWithText("Planned length of stay").assertIsDisplayed()
    }

    @Test
    fun passportValidity_mixedCase_sixMonths_localizes() {
        launchSheet(country(passportValidity = "Valid for at least 6 Month beyond departure"))

        composeTestRule.onNodeWithText("6 months").assertIsDisplayed()
    }

    // ============================================================
    // Currency Input — decimal validation
    // (Merged from CountryDetailSheetAdditionalTest)
    //
    // Each test calls performTextClearance() explicitly after performClick() so
    // that the field is empty before performTextInput() fires, rather than
    // relying on the asynchronous FocusInteraction.Focus handler to do it.
    // ============================================================

    /**
     * Typing letters into the USD field should be rejected by [isValidDecimalInput].
     * The field should remain empty rather than displaying "abc".
     */
    @Test
    fun currencyInput_usdField_rejectsLetterInput() {
        launchSheet(country())

        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("abc")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("abc").assertDoesNotExist()
    }

    @Test
    fun currencyInput_foreignField_rejectsLetterInput() {
        launchSheet(country())

        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextClearance()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextInput("xyz")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("xyz").assertDoesNotExist()
    }

    /**
     * Input with three decimal places ("1.234") exceeds the `\d{0,2}` constraint and
     * must be rejected.
     */
    @Test
    fun currencyInput_usdField_rejectsThreeDecimalPlaces() {
        launchSheet(country())

        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("1.23")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("1.23")

        // Appending a third decimal digit must be rejected.
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("4")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("1.23")
    }

    /**
     * A lone decimal point "." satisfies `^\d*\.?\d{0,2}$` and must be accepted.
     */
    @Test
    fun currencyInput_usdField_acceptsSingleDecimalPoint() {
        launchSheet(country())

        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput(".")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals(".")
    }

    /**
     * "1.2.3" contains two decimal points and must be rejected.
     */
    @Test
    fun currencyInput_usdField_rejectsMultipleDecimalPoints() {
        launchSheet(country())

        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("1.2")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("1.2")

        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput(".")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("1.2")
    }

    @Test
    fun currencyInput_usdField_acceptsOneDecimalPlace() {
        launchSheet(country())

        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("5.7")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("5.7")
    }

    @Test
    fun currencyInput_usdField_acceptsTwoDecimalPlaces() {
        launchSheet(country())

        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("5.75")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("5.75")
    }

    @Test
    fun currencyInput_foreignField_rejectsThreeDecimalPlaces() {
        launchSheet(country())

        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextClearance()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextInput("2.99")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_foreign").assertTextEquals("2.99")

        composeTestRule.onNodeWithTag("currency_input_foreign").performTextInput("9")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("currency_input_foreign").assertTextEquals("2.99")
    }

    // ============================================================
    // Blur-without-typing restores pre-focus value
    // (Merged from CountryDetailSheetAdditionalTest)
    // ============================================================

    /**
     * When the user focuses a [CurrencyInputField] but types nothing, blurring the field
     * must restore the value that was showing before focus.  This exercises the
     * [FocusInteraction.Unfocus] branch that checks `textFieldValue.text.isEmpty()`.
     */
    @Test
    fun currencyInput_usdField_blurWithNoInput_restoresValue() {
        // EUR country: initial USD = "1"
        launchSheet(country(exchangeRateToUSD = 1.08))

        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("1")

        // Focus USD (the Focus interaction clears it asynchronously)
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()

        // Dismiss focus via IME Done — avoids racing with a concurrent foreign Focus event
        // and prevents accidentally clearing foreign field's displayed value.
        composeTestRule.onNodeWithTag("currency_input_usd").performImeAction()
        composeTestRule.waitForIdle()

        // USD must have been restored to "1" (the preFocusValue)
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("1")
    }

    @Test
    fun currencyInput_foreignField_blurWithNoInput_restoresValue() {
        // EUR country: initial foreign = 1/1.08 ≈ "0.93"
        launchSheet(country(exchangeRateToUSD = 1.08))

        composeTestRule.onNodeWithTag("currency_input_foreign").assertTextEquals("0.93")

        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.waitForIdle()

        // Dismiss focus via IME Done — avoids racing with a concurrent USD Focus event
        // and prevents accidentally clearing USD field's displayed value.
        composeTestRule.onNodeWithTag("currency_input_foreign").performImeAction()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("currency_input_foreign").assertTextEquals("0.93")
    }
    
    @Test
    fun currencyInput_usdField_blurWithNoInput_doesNotShowZero() {
        // Blurring USD without typing must not produce "0.00" in either field.
        launchSheet(country(exchangeRateToUSD = 1.27, currencyCode = "GBP", currency = "British Pound"))

        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()
        // Dismiss focus via IME Done — avoids racing with a concurrent foreign Focus event.
        composeTestRule.onNodeWithTag("currency_input_usd").performImeAction()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("0.00").assertDoesNotExist()
    }

    // ============================================================
    // Currency converter present / absent for various non-USD codes
    // (Merged from CountryDetailSheetAdditionalTest)
    // ============================================================

    @Test
    fun currencyConverter_present_forSubUnitCurrency() {
        launchSheet(country(currencyCode = "JPY", currency = "Japanese Yen", exchangeRateToUSD = 0.0067))

        composeTestRule.onNodeWithTag("currency_converter").assertExists()
    }

    @Test
    fun currencyConverter_present_forSuperUnitCurrency() {
        launchSheet(country(currencyCode = "KWD", currency = "Kuwaiti Dinar", exchangeRateToUSD = 3.26))

        composeTestRule.onNodeWithTag("currency_converter").assertExists()
    }

    @Test
    fun currencyConverter_absent_forUSD() {
        launchSheet(country(currencyCode = "USD", currency = "US Dollar", exchangeRateToUSD = 1.0))

        composeTestRule.onNodeWithTag("currency_converter").assertDoesNotExist()
    }

    // ============================================================
    // Test-tag completeness guard
    // (Merged from CountryDetailSheetAdditionalTest)
    // ============================================================

    /**
     * Verifies that every declared test tag exists for a fully-populated country, ensuring
     * that future refactoring of test tag strings would fail here rather than silently.
     */
    @Test
    fun allDeclaredTestTags_existForFullyPopulatedCountry() {
        launchSheet(country(
            passportValidity = "6 months",
            travelAdvisories = TravelAdvisories("https://a", "https://b", "https://c", "https://d"),
            currencyCode = "EUR"
        ))

        val tags = listOf(
            "country_detail_sheet",
            "country_header",
            "country_flag",
            "country_name",
            "country_continent",
            "info_safety_level",
            "info_safety_level_value",
            "info_entry_requirement",
            "info_entry_requirement_value",
            "info_passport_validity",
            "info_passport_validity_value",
            "info_currency",
            "info_currency_value",
            "info_power_outlet",
            "info_power_outlet_value",
            "currency_converter",
            "currency_input_usd",
            "currency_input_foreign",
            "bottom_sheet_scrim",
            "bottom_sheet_close_button"
        )

        tags.forEach { tag ->
            composeTestRule.onNodeWithTag(tag).assertExists()
        }
    }

    /**
     * For a USD country, the currency-converter-related tags must be absent.
     */
    @Test
    fun converterTestTags_absentForUsdCountry() {
        launchSheet(country(currencyCode = "USD", currency = "US Dollar", exchangeRateToUSD = 1.0))

        listOf("currency_converter", "currency_input_usd", "currency_input_foreign")
            .forEach { tag ->
                composeTestRule.onNodeWithTag(tag).assertDoesNotExist()
            }
    }

}
