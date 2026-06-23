package com.unstampedpages.app.ui.screens.countryinfo

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
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
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// ── JVM unit tests for CountryDetailSheet.kt ─────────────────────────────────
//
// Covered code paths:
//   • CountryDetailSheet: visible/invisible, null country, displayName branch,
//     trackAndDismiss (close button and scrim click), analytics callbacks,
//     currency-converter visibility guard (currencyCode != "USD")
//   • SafetyLevelRow: all 4 safety levels, advisories null vs non-null
//   • TravelAdvisoryChip: all 4 chip tap callbacks + openInCustomTab execution
//   • InfoRow (both overloads): entry requirement, passport validity, currency,
//     outlet type rendered from ImageVector icon and from iconContent lambda
//   • CurrencySymbolIcon: getSymbol called for non-USD currency
//   • CurrencyConverter: positive rate, zero rate, LaunchedEffect initial reset,
//     bidirectional conversion with usdValue > 0 / usdValue == 0 branches,
//     foreignValue > 0 / foreignValue == 0 branches, onUsdFocused / onForeignFocused
//   • CurrencyInputField: LaunchedEffect(value) isFocused=false and isFocused=true
//     branches, FocusInteraction.Focus (clears field, fires onFieldFocused),
//     FocusInteraction.Unfocus with empty text (restores preFocusValue), onValueChange
//     text-changed branch, isValidDecimalInput true (valid input and empty string)
//     and false (invalid) branches, IME Done KeyboardActions handler
//   • getLocalizedPassportValidity: all 5 when-arms (null, 6-month, 3-month,
//     duration, length-of-stay, stay-only keyword, unknown fallback)

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w1080dp-h2400dp-xxhdpi")
class CountryDetailSheetUnitTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── Fixture builder ───────────────────────────────────────────────────────

    private fun makeCountry(
        id: String = "jp",
        name: String = "Japan",
        safetyLevel: SafetyLevel = SafetyLevel.NORMAL_SECURITY_PRECAUTIONS,
        visaRequirement: VisaRequirement = VisaRequirement.VISA_NOT_REQUIRED,
        currency: String = "Japanese Yen",
        currencyCode: String = "JPY",
        exchangeRateToUSD: Double = 0.0067,
        outletType: String = "Type A",
        continent: Continent = Continent.ASIA,
        flagEmoji: String = "\uD83C\uDDEF\uD83C\uDDF5",
        travelAdvisories: TravelAdvisories? = TravelAdvisories(
            us = "https://travel.state.gov/jp",
            uk = "https://gov.uk/jp",
            au = "https://smartraveller.gov.au/jp",
            ca = "https://travel.gc.ca/jp"
        ),
        passportValidity: String? = "6 months required"
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
        travelAdvisories = travelAdvisories,
        passportValidity = passportValidity
    )

    private fun launchSheet(
        country: Country? = makeCountry(),
        visible: Boolean = true,
        onDismiss: () -> Unit = {},
        displayName: String? = null,
        analytics: CountrySheetAnalytics = CountrySheetAnalytics()
    ) {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = country,
                    visible = visible,
                    onDismiss = onDismiss,
                    displayName = displayName,
                    analytics = analytics
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    // ── Sheet visibility ──────────────────────────────────────────────────────

    @Test
    fun sheet_visibleTrue_showsSheetContent() {
        launchSheet(visible = true)
        composeTestRule.onNodeWithTag("country_detail_sheet").assertExists()
    }

    @Test
    fun sheet_visibleFalse_hidesSheetContent() {
        launchSheet(visible = false)
        composeTestRule.onNodeWithTag("country_detail_sheet").assertDoesNotExist()
    }

    // ── Scrim ─────────────────────────────────────────────────────────────────

    @Test
    fun scrim_visibleTrue_shown() {
        launchSheet(visible = true)
        composeTestRule.onNodeWithTag("bottom_sheet_scrim").assertExists()
    }

    @Test
    fun scrim_visibleFalse_hidden() {
        launchSheet(visible = false)
        composeTestRule.onNodeWithTag("bottom_sheet_scrim").assertDoesNotExist()
    }

    // ── Null country — `country?.let { }` branch ─────────────────────────────
    // When country is null the Surface/content is absent even if visible=true.
    // The scrim is still rendered (it sits outside the country?.let block).

    @Test
    fun nullCountry_visible_scrimShownButSheetAbsent() {
        launchSheet(country = null, visible = true)
        composeTestRule.onNodeWithTag("bottom_sheet_scrim").assertExists()
        composeTestRule.onNodeWithTag("country_detail_sheet").assertDoesNotExist()
    }

    // ── DisplayName null vs non-null (CountryHeader) ──────────────────────────

    @Test
    fun displayNameNull_showsCountryName() {
        launchSheet(displayName = null)
        composeTestRule.onNodeWithTag("country_name").assertExists()
        composeTestRule.onNodeWithText("Japan").assertExists()
    }

    @Test
    fun displayNameProvided_showsDisplayName() {
        launchSheet(displayName = "Easter Island")
        composeTestRule.onNodeWithTag("country_name").assertExists()
        composeTestRule.onNodeWithText("Easter Island").assertExists()
    }

    // ── trackAndDismiss: close button ─────────────────────────────────────────

    @Test
    fun closeButton_callsOnDismiss() {
        var dismissed = false
        launchSheet(onDismiss = { dismissed = true })
        composeTestRule.onNodeWithTag("bottom_sheet_close_button").performClick()
        composeTestRule.waitForIdle()
        assertTrue(dismissed)
    }

    @Test
    fun closeButton_firesOnDismissedAnalytics() {
        var fired = false
        launchSheet(analytics = CountrySheetAnalytics(onDismissed = { fired = true }))
        composeTestRule.onNodeWithTag("bottom_sheet_close_button").performClick()
        composeTestRule.waitForIdle()
        assertTrue(fired)
    }

    // ── trackAndDismiss: scrim click ──────────────────────────────────────────

    @Test
    fun scrimClick_callsOnDismiss() {
        var dismissed = false
        launchSheet(onDismiss = { dismissed = true })
        composeTestRule.onNodeWithTag("bottom_sheet_scrim").performClick()
        composeTestRule.waitForIdle()
        assertTrue(dismissed)
    }

    @Test
    fun scrimClick_firesOnDismissedAnalytics() {
        var fired = false
        launchSheet(analytics = CountrySheetAnalytics(onDismissed = { fired = true }))
        composeTestRule.onNodeWithTag("bottom_sheet_scrim").performClick()
        composeTestRule.waitForIdle()
        assertTrue(fired)
    }

    // ── Safety levels ─────────────────────────────────────────────────────────

    @Test
    fun safetyLevel_normalSecurityPrecautions_renders() {
        launchSheet(country = makeCountry(safetyLevel = SafetyLevel.NORMAL_SECURITY_PRECAUTIONS))
        composeTestRule.onNodeWithTag("info_safety_level_value").assertExists()
    }

    @Test
    fun safetyLevel_highDegreeCaution_renders() {
        launchSheet(country = makeCountry(safetyLevel = SafetyLevel.HIGH_DEGREE_CAUTION))
        composeTestRule.onNodeWithTag("info_safety_level_value").assertExists()
    }

    @Test
    fun safetyLevel_reconsiderTravel_renders() {
        launchSheet(country = makeCountry(safetyLevel = SafetyLevel.RECONSIDER_TRAVEL))
        composeTestRule.onNodeWithTag("info_safety_level_value").assertExists()
    }

    @Test
    fun safetyLevel_doNotTravel_renders() {
        launchSheet(country = makeCountry(safetyLevel = SafetyLevel.DO_NOT_TRAVEL))
        composeTestRule.onNodeWithTag("info_safety_level_value").assertExists()
    }

    // ── Advisory chips: presence / absence (`if (advisories != null)`) ────────

    @Test
    fun advisories_nonNull_chipsShown() {
        launchSheet(country = makeCountry(travelAdvisories = TravelAdvisories(
            us = "https://travel.state.gov",
            uk = "https://gov.uk",
            au = "https://smartraveller.gov.au",
            ca = "https://travel.gc.ca"
        )))
        composeTestRule.onNodeWithTag("advisory_chip_us").assertExists()
        composeTestRule.onNodeWithTag("advisory_chip_uk").assertExists()
        composeTestRule.onNodeWithTag("advisory_chip_au").assertExists()
        composeTestRule.onNodeWithTag("advisory_chip_ca").assertExists()
    }

    @Test
    fun advisories_null_chipsAbsent() {
        launchSheet(country = makeCountry(travelAdvisories = null))
        composeTestRule.onNodeWithTag("advisory_chip_us").assertDoesNotExist()
    }

    // ── Analytics: advisory chip taps (also exercises openInCustomTab) ────────

    @Test
    fun usChip_click_firesOnUsAdvisoryTapped() {
        var fired = false
        launchSheet(analytics = CountrySheetAnalytics(onUsAdvisoryTapped = { fired = true }))
        composeTestRule.onNodeWithTag("advisory_chip_us").performClick()
        composeTestRule.waitForIdle()
        assertTrue(fired)
    }

    @Test
    fun ukChip_click_firesOnUkAdvisoryTapped() {
        var fired = false
        launchSheet(analytics = CountrySheetAnalytics(onUkAdvisoryTapped = { fired = true }))
        composeTestRule.onNodeWithTag("advisory_chip_uk").performClick()
        composeTestRule.waitForIdle()
        assertTrue(fired)
    }

    @Test
    fun caChip_click_firesOnCaAdvisoryTapped() {
        var fired = false
        launchSheet(analytics = CountrySheetAnalytics(onCaAdvisoryTapped = { fired = true }))
        composeTestRule.onNodeWithTag("advisory_chip_ca").performClick()
        composeTestRule.waitForIdle()
        assertTrue(fired)
    }

    @Test
    fun auChip_click_firesOnAuAdvisoryTapped() {
        var fired = false
        launchSheet(analytics = CountrySheetAnalytics(onAuAdvisoryTapped = { fired = true }))
        composeTestRule.onNodeWithTag("advisory_chip_au").performClick()
        composeTestRule.waitForIdle()
        assertTrue(fired)
    }

    // ── Currency converter: USD vs non-USD guard ──────────────────────────────
    // Exercises the `if (it.currencyCode != "USD") { CurrencyConverter(...) }` branch.

    @Test
    fun currencyCode_nonUSD_converterShown() {
        launchSheet(country = makeCountry(currencyCode = "JPY", exchangeRateToUSD = 0.0067))
        composeTestRule.onNodeWithTag("currency_converter").assertExists()
    }

    @Test
    fun currencyCode_USD_converterAbsent() {
        launchSheet(country = makeCountry(
            currency = "US Dollar",
            currencyCode = "USD",
            exchangeRateToUSD = 1.0
        ))
        composeTestRule.onNodeWithTag("currency_converter").assertDoesNotExist()
    }

    // ── CurrencyConverter: exchange rate > 0 vs == 0 ─────────────────────────
    // Exercises: `val foreignPerUsd = if (exchangeRateToUSD > 0) 1.0 / ... else 0.0`

    @Test
    fun exchangeRate_positive_converterRendersFields() {
        launchSheet(country = makeCountry(currencyCode = "EUR", exchangeRateToUSD = 1.08))
        composeTestRule.onNodeWithTag("currency_input_usd").assertExists()
        composeTestRule.onNodeWithTag("currency_input_foreign").assertExists()
    }

    @Test
    fun exchangeRate_zero_converterRendersWithoutCrash() {
        // exchangeRateToUSD = 0.0 → foreignPerUsd = 0.0 (else branch)
        launchSheet(country = makeCountry(currencyCode = "EUR", exchangeRateToUSD = 0.0))
        composeTestRule.onNodeWithTag("currency_converter").assertExists()
    }

    // ── CurrencySymbolIcon ─────────────────────────────────────────────────────

    @Test
    fun currencySymbolIcon_shownForNonUsdCurrency() {
        launchSheet(country = makeCountry(currencyCode = "JPY"))
        composeTestRule.onNodeWithTag("info_currency_symbol").assertExists()
    }

    // ── Analytics: currency field focus callbacks ─────────────────────────────

    @Test
    fun usdField_focus_firesOnUsdFocused() {
        var fired = false
        launchSheet(analytics = CountrySheetAnalytics(onUsdFocused = { fired = true }))
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()
        assertTrue(fired)
    }

    @Test
    fun foreignField_focus_firesOnForeignFocused() {
        var fired = false
        launchSheet(analytics = CountrySheetAnalytics(onForeignFocused = { fired = true }))
        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.waitForIdle()
        assertTrue(fired)
    }

    // ── isValidDecimalInput branches ──────────────────────────────────────────
    // Covered via CurrencyInputField's onValueChange:
    //   true (valid):   non-empty text that matches the decimal regex
    //   false (invalid): text that doesn't match (letters, 3+ decimal places)
    //   true (empty):   empty string short-circuits via text.isEmpty()

    @Test
    fun currencyField_validTwoDecimalInput_accepted() {
        launchSheet()
        // Click to focus, then clear the semantic text before typing (mirrors the
        // androidTest focusClearedCurrencyInput helper; without it performTextInput
        // appends to whatever the field holds rather than replacing it).
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.waitForIdle()
        // isValidDecimalInput("5.25") → false.isEmpty() || "5.25".matches(REGEX) = true
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("5.25")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("5.25").assertExists()
    }

    @Test
    fun currencyField_letterInput_rejected() {
        launchSheet()
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.waitForIdle()
        // isValidDecimalInput("abc") → false || false = false → input rejected
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("abc")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("abc").assertDoesNotExist()
    }

    @Test
    fun currencyField_threeDecimalPlaces_rejected() {
        launchSheet()
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.waitForIdle()
        // isValidDecimalInput("1.234") → false || false = false → rejected
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("1.234")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("1.234").assertDoesNotExist()
    }

    @Test
    fun currencyField_clearingAllCharacters_triggersEmptyStringBranch() {
        // After typing "5" then clearing, onValueChange receives "" which triggers
        // isValidDecimalInput("") → "".isEmpty() = true (short-circuit branch).
        launchSheet()
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("5")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.waitForIdle()
        // Sheet still visible — clearing input does not crash
        composeTestRule.onNodeWithTag("country_detail_sheet").assertExists()
    }

    // ── CurrencyInputField: unfocus with empty text restores preFocusValue ────
    // FocusInteraction.Unfocus branch: `if (textFieldValue.text.isEmpty()) { restore }`

    @Test
    fun usdField_unfocusWithNoInput_restoresInitialValue() {
        // EUR at 1:1 → initial USD amount = "1"
        launchSheet(country = makeCountry(currencyCode = "EUR", exchangeRateToUSD = 1.0))
        // Focus USD field (clears it to "")
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()
        // Focus foreign field (unfocuses USD with empty text → preFocusValue restored)
        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.waitForIdle()
        // USD field should show the restored pre-focus value
        composeTestRule.onNodeWithTag("currency_input_usd").assertTextEquals("1")
    }

    // ── CurrencyInputField: LaunchedEffect(value) isFocused=true branch ───────
    // When the user types in the USD field, `usdAmount` changes → the `value` prop
    // fed to the USD CurrencyInputField changes while it IS focused → the
    // `if (!isFocused)` guard in LaunchedEffect(value) is FALSE (body skipped).
    // The foreign field simultaneously receives the new calculated amount while
    // NOT focused → its `if (!isFocused)` is TRUE (body runs, syncs display).

    @Test
    fun usdField_typing_updatesForeignFieldDisplay() {
        // Rate 1:1 so 2 USD = 2.00 foreign
        launchSheet(country = makeCountry(currencyCode = "EUR", exchangeRateToUSD = 1.0))
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("2")
        composeTestRule.waitForIdle()
        // Foreign field synced by LaunchedEffect(value) while isFocused=false
        composeTestRule.onNodeWithText("2.00").assertExists()
    }

    @Test
    fun foreignField_typing_updatesUsdFieldDisplay() {
        // Rate 1:1 so 3 foreign = 3.00 USD
        launchSheet(country = makeCountry(currencyCode = "EUR", exchangeRateToUSD = 1.0))
        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextClearance()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextInput("3")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("3.00").assertExists()
    }

    // ── CurrencyConverter: usdValue == 0 and foreignValue == 0 branches ───────
    // `if (usdValue > 0 && foreignPerUsd > 0)` with usdValue == 0 → "0.00" foreign
    // `if (foreignValue > 0 && exchangeRateToUSD > 0)` with foreignValue == 0 → "0.00" USD

    @Test
    fun usdField_zeroInput_foreignShowsZero() {
        launchSheet(country = makeCountry(currencyCode = "EUR", exchangeRateToUSD = 1.0))
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performTextClearance()
        composeTestRule.waitForIdle()
        // "0" input → usdValue = 0.0 → condition `usdValue > 0` is false → "0.00"
        composeTestRule.onNodeWithTag("currency_input_usd").performTextInput("0")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("0.00").assertExists()
    }

    @Test
    fun foreignField_zeroInput_usdShowsZero() {
        launchSheet(country = makeCountry(currencyCode = "EUR", exchangeRateToUSD = 1.0))
        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextClearance()
        composeTestRule.waitForIdle()
        // "0" input → foreignValue = 0.0 → condition `foreignValue > 0` is false → "0.00"
        composeTestRule.onNodeWithTag("currency_input_foreign").performTextInput("0")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("0.00").assertExists()
    }

    // ── IME Done action (KeyboardActions.onDone) ──────────────────────────────

    @Test
    fun usdField_imeActionDone_doesNotCrash() {
        launchSheet()
        composeTestRule.onNodeWithTag("currency_input_usd").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_usd").performImeAction()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("country_detail_sheet").assertExists()
    }

    @Test
    fun foreignField_imeActionDone_doesNotCrash() {
        launchSheet()
        composeTestRule.onNodeWithTag("currency_input_foreign").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("currency_input_foreign").performImeAction()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("country_detail_sheet").assertExists()
    }

    // ── getLocalizedPassportValidity: all when-arms ───────────────────────────

    @Test
    fun passportValidity_null_infoRowRendered() {
        // null → R.string.passport_validity_not_specified
        launchSheet(country = makeCountry(passportValidity = null))
        composeTestRule.onNodeWithTag("info_passport_validity").assertExists()
    }

    @Test
    fun passportValidity_sixMonths_infoRowRendered() {
        // contains "6 month" (ignoreCase) → R.string.passport_validity_six_months
        launchSheet(country = makeCountry(passportValidity = "6 months required"))
        composeTestRule.onNodeWithTag("info_passport_validity").assertExists()
    }

    @Test
    fun passportValidity_sixMonthsUpperCase_infoRowRendered() {
        // upper-case variant to exercise the ignoreCase flag
        launchSheet(country = makeCountry(passportValidity = "6 MONTH validity"))
        composeTestRule.onNodeWithTag("info_passport_validity").assertExists()
    }

    @Test
    fun passportValidity_threeMonths_infoRowRendered() {
        // contains "3 month" (ignoreCase) → R.string.passport_validity_three_months
        launchSheet(country = makeCountry(passportValidity = "3 month validity"))
        composeTestRule.onNodeWithTag("info_passport_validity").assertExists()
    }

    @Test
    fun passportValidity_duration_infoRowRendered() {
        // contains "duration" → R.string.passport_validity_duration_of_stay (first OR)
        launchSheet(country = makeCountry(passportValidity = "Valid for duration of visit"))
        composeTestRule.onNodeWithTag("info_passport_validity").assertExists()
    }

    @Test
    fun passportValidity_lengthOfStay_infoRowRendered() {
        // "duration" false, "length of stay" true → duration_of_stay (second OR)
        launchSheet(country = makeCountry(passportValidity = "Must be valid for length of stay"))
        composeTestRule.onNodeWithTag("info_passport_validity").assertExists()
    }

    @Test
    fun passportValidity_stayKeywordOnly_infoRowRendered() {
        // "duration" false, "length of stay" false, "stay" true → duration_of_stay (third OR)
        // "Valid for your stay" does NOT contain "length of stay", so the third OR fires.
        launchSheet(country = makeCountry(passportValidity = "Valid for your stay"))
        composeTestRule.onNodeWithTag("info_passport_validity").assertExists()
    }

    @Test
    fun passportValidity_unknownString_infoRowRendered() {
        // else branch → fallback to original string
        launchSheet(country = makeCountry(passportValidity = "Minimum 90 days"))
        composeTestRule.onNodeWithTag("info_passport_validity").assertExists()
    }

    // ── Visa requirements: all 5 VisaRequirement variants ─────────────────────
    // Exercises InfoRow(icon=ImageVector, ...) with different stringResource values.

    @Test
    fun visaRequirement_notRequired_rendersRow() {
        launchSheet(country = makeCountry(visaRequirement = VisaRequirement.VISA_NOT_REQUIRED))
        composeTestRule.onNodeWithTag("info_entry_requirement").assertExists()
    }

    @Test
    fun visaRequirement_eVisa_rendersRow() {
        launchSheet(country = makeCountry(visaRequirement = VisaRequirement.E_VISA))
        composeTestRule.onNodeWithTag("info_entry_requirement").assertExists()
    }

    @Test
    fun visaRequirement_onArrival_rendersRow() {
        launchSheet(country = makeCountry(visaRequirement = VisaRequirement.VISA_ON_ARRIVAL))
        composeTestRule.onNodeWithTag("info_entry_requirement").assertExists()
    }

    @Test
    fun visaRequirement_required_rendersRow() {
        launchSheet(country = makeCountry(visaRequirement = VisaRequirement.VISA_REQUIRED))
        composeTestRule.onNodeWithTag("info_entry_requirement").assertExists()
    }

    @Test
    fun visaRequirement_restricted_rendersRow() {
        launchSheet(country = makeCountry(visaRequirement = VisaRequirement.RESTRICTED))
        composeTestRule.onNodeWithTag("info_entry_requirement").assertExists()
    }

    // ── CountryDetailSheet default analytics parameter ────────────────────────
    // Calling CountryDetailSheet without the `analytics` argument exercises the
    // `analytics: CountrySheetAnalytics = CountrySheetAnalytics()` default branch.

    @Test
    fun sheet_defaultAnalytics_renders() {
        composeTestRule.setContent {
            UnstampedPagesTheme {
                CountryDetailSheet(
                    country = makeCountry(),
                    visible = true,
                    onDismiss = {}
                    // analytics omitted → default CountrySheetAnalytics() used
                )
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("country_detail_sheet").assertExists()
    }

    // ── InfoRow (iconContent lambda overload) — currency row ──────────────────
    // This overload is invoked for the currency row (CurrencySymbolIcon as iconContent).

    @Test
    fun currencyRow_iconContentOverload_renders() {
        launchSheet()
        composeTestRule.onNodeWithTag("info_currency").assertExists()
    }

    // ── InfoRow (ImageVector overload) — remaining rows ───────────────────────

    @Test
    fun outletTypeRow_renders() {
        launchSheet()
        composeTestRule.onNodeWithTag("info_power_outlet").assertExists()
    }

    // ── CountryHeader elements ─────────────────────────────────────────────────

    @Test
    fun countryHeader_flagEmojiShown() {
        launchSheet()
        composeTestRule.onNodeWithTag("country_flag").assertExists()
    }

    @Test
    fun countryHeader_continentShown() {
        launchSheet()
        composeTestRule.onNodeWithTag("country_continent").assertExists()
    }

    @Test
    fun countryHeader_closeButtonPresent() {
        launchSheet()
        composeTestRule.onNodeWithTag("bottom_sheet_close_button").assertExists()
    }

    // ── Custom flag override (Image vs emoji Text) ───────────────────────────

    @Test
    fun countryWithCustomFlag_somaliland_rendersImageNotEmoji() {
        launchSheet(country = makeCountry(id = "xso", name = "Somaliland", flagEmoji = "🇸🇴"))
        composeTestRule.onNode(hasTestTag("country_flag") and hasContentDescription("Flag of Somaliland"))
            .assertExists()
    }

    @Test
    fun countryWithCustomFlag_northernCyprus_rendersImageNotEmoji() {
        launchSheet(country = makeCountry(id = "xnc", name = "Northern Cyprus", flagEmoji = "🇨🇾"))
        composeTestRule.onNode(hasTestTag("country_flag") and hasContentDescription("Flag of Northern Cyprus"))
            .assertExists()
    }

    @Test
    fun countryWithCustomFlag_northernIreland_rendersImageNotEmoji() {
        launchSheet(country = makeCountry(id = "xni", name = "Northern Ireland", flagEmoji = ""))
        composeTestRule.onNode(hasTestTag("country_flag") and hasContentDescription("Flag of Northern Ireland"))
            .assertExists()
    }

    @Test
    fun countryWithoutCustomFlag_rendersEmojiText() {
        launchSheet(country = makeCountry(id = "jp", name = "Japan", flagEmoji = "🇯🇵"))
        composeTestRule.onNodeWithTag("country_flag").assertExists()
        // Emoji text node — no content description since it's a Text composable
        composeTestRule.onNode(hasTestTag("country_flag") and hasContentDescription("Flag of Japan"))
            .assertDoesNotExist()
    }
}
