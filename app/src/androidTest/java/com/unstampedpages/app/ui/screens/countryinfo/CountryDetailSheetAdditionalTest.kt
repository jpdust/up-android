package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.model.Continent
import com.unstampedpages.app.data.model.Country
import com.unstampedpages.app.data.model.SafetyLevel
import com.unstampedpages.app.data.model.TravelAdvisories
import com.unstampedpages.app.data.model.VisaRequirement
import com.unstampedpages.app.ui.theme.UnstampedPagesTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Additional instrumented tests for [CountryDetailSheet] that cover gaps not addressed
 * in [CountryDetailSheetTest]:
 *
 *   1. Null travel advisories  — advisory chips hidden when [Country.travelAdvisories] is null
 *                                (as opposed to when the entire country is null).
 *   2. Passport validity edge cases — case-insensitive pattern matching and "At least 3 months".
 *   3. Currency input validation — [isValidDecimalInput] guard: letters, extra decimal places,
 *                                  multiple dots, and the lone "." edge case are all handled.
 *   4. Empty-field blur restore — [CurrencyInputField] restores the pre-focus value when focus
 *                                 is lost without typing anything.
 */
@RunWith(AndroidJUnit4::class)
class CountryDetailSheetAdditionalTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

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

    private fun launchSheet(country: Country?, visible: Boolean = true) {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(country = country, visible = visible, onDismiss = {})
            }
        }
        composeTestRule.waitForIdle()
    }

    // ---------------------------------------------------------------------------
    // 1. Null travel advisories
    // ---------------------------------------------------------------------------

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

    // ---------------------------------------------------------------------------
    // 2. Passport validity — case-insensitive matching and "At least 3 months"
    // ---------------------------------------------------------------------------

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

    // ---------------------------------------------------------------------------
    // 3. Currency input decimal validation
    // ---------------------------------------------------------------------------

    /**
     * Typing letters into the USD field should be rejected by [isValidDecimalInput].
     * The field should remain at its cleared-empty state rather than displaying "abc".
     */
    @Test
    fun currencyInput_usdField_rejectsLetterInput() {
        launchSheet(country())

        // Focus the USD field (which clears it)
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()
        // Type letters — should be rejected
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("abc")
        composeTestRule.waitForIdle()

        // "abc" must not appear anywhere
        composeTestRule.onNodeWithText("abc").assertDoesNotExist()
    }

    @Test
    fun currencyInput_foreignField_rejectsLetterInput() {
        launchSheet(country())

        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
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
        composeTestRule.waitForIdle()
        // Type a value then try to add a third decimal
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("1.23")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("1.23")

        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("4")
        composeTestRule.waitForIdle()

        // "1.234" is invalid — field should still show "1.23"
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("1.23")
    }

    /**
     * A lone decimal point "." satisfies `^\d*\.?\d{0,2}$` (zero digits before,
     * one dot, zero digits after) and must be accepted.
     */
    @Test
    fun currencyInput_usdField_acceptsSingleDecimalPoint() {
        launchSheet(country())

        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput(".")
        composeTestRule.waitForIdle()

        // "." is a valid intermediate value
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals(".")
    }

    /**
     * "1.2.3" contains two decimal points and must be rejected.
     */
    @Test
    fun currencyInput_usdField_rejectsMultipleDecimalPoints() {
        launchSheet(country())

        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("1.2")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("1.2")

        // Typing a second dot should be rejected
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput(".")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("1.2")
    }

    @Test
    fun currencyInput_usdField_acceptsOneDecimalPlace() {
        launchSheet(country())

        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("5.7")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("5.7")
    }

    @Test
    fun currencyInput_usdField_acceptsTwoDecimalPlaces() {
        launchSheet(country())

        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("5.75")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("5.75")
    }

    @Test
    fun currencyInput_foreignField_rejectsThreeDecimalPlaces() {
        launchSheet(country())

        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextInput("2.99")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_foreign").assertTextEquals("2.99")

        composeTestRule.onNodeWithTag("currency_input_foreign").performTextInput("9")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("currency_input_foreign").assertTextEquals("2.99")
    }

    // ---------------------------------------------------------------------------
    // 4. Empty-field blur: restore pre-focus value
    // ---------------------------------------------------------------------------

    /**
     * When the user focuses a [CurrencyInputField] but types nothing, blurring the field
     * must restore the value that was showing before focus.  This exercises the
     * `FocusInteraction.Unfocus` branch that checks `textFieldValue.text.isEmpty()`.
     */
    @Test
    fun currencyInput_usdField_blurWithNoInput_restoresValue() {
        // EUR country: initial USD = "1"
        launchSheet(country(exchangeRateToUSD = 1.08))

        // Verify "1" is displayed initially
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("1")

        // Focus USD (clears it)
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()

        // Immediately shift focus to the foreign field without typing anything
        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.waitForIdle()

        // USD field must have been restored to "1" (the preFocusValue)
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("1")
    }

    @Test
    fun currencyInput_foreignField_blurWithNoInput_restoresValue() {
        // EUR country: initial foreign = 1/1.08 ≈ "0.93"
        launchSheet(country(exchangeRateToUSD = 1.08))

        composeTestRule.onNodeWithTag("currency_input_foreign").assertTextEquals("0.93")

        // Focus foreign field (clears it)
        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.waitForIdle()

        // Immediately shift focus to USD without typing
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()

        // Foreign field must restore to "0.93"
        composeTestRule.onNodeWithTag("currency_input_foreign").assertTextEquals("0.93")
    }

    @Test
    fun currencyInput_foreignField_blurWithNoInput_doesNotUpdateUsd() {
        // Blurring without typing must not propagate a 0.00 conversion to USD.
        launchSheet(country(exchangeRateToUSD = 1.08))

        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("1")

        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.waitForIdle()

        // Shift focus back to USD without typing in the foreign field
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()

        // USD must still show its original value, not "0.00"
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("1")
    }

    @Test
    fun currencyInput_usdField_blurWithNoInput_doesNotShowZero() {
        // Blurring USD without typing must not produce "0.00" in either field.
        launchSheet(country(exchangeRateToUSD = 1.27, currencyCode = "GBP", currency = "British Pound"))

        // Focus then immediately blur to foreign field
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.waitForIdle()

        // Neither field should show "0.00"
        composeTestRule.onNodeWithText("0.00").assertDoesNotExist()
    }

    // ---------------------------------------------------------------------------
    // 5. Currency converter present / absent for various non-USD codes
    // ---------------------------------------------------------------------------

    /**
     * Sanity-guard: every non-USD currency code produces a converter, regardless of
     * whether the rate is < 1 or > 1.
     */
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

    // ---------------------------------------------------------------------------
    // 6. Test-tag completeness guard
    // ---------------------------------------------------------------------------

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
