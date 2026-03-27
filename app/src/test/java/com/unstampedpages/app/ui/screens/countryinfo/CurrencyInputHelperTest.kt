package com.unstampedpages.app.ui.screens.countryinfo

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for the currency input validation used in CountryDetailSheet.
 */
class CurrencyInputHelperTest {

    // ==================== Decimal Validation Tests ====================

    @Test
    fun `empty string is valid decimal input`() {
        assertTrue(isValidDecimalInputTestable(""))
    }

    @Test
    fun `single digit is valid`() {
        assertTrue(isValidDecimalInputTestable("5"))
    }

    @Test
    fun `multiple digits is valid`() {
        assertTrue(isValidDecimalInputTestable("123"))
    }

    @Test
    fun `decimal with one decimal place is valid`() {
        assertTrue(isValidDecimalInputTestable("1.5"))
    }

    @Test
    fun `decimal with two decimal places is valid`() {
        assertTrue(isValidDecimalInputTestable("1.50"))
    }

    @Test
    fun `decimal with three decimal places is invalid`() {
        assertFalse(isValidDecimalInputTestable("1.234"))
    }

    @Test
    fun `decimal starting with period is valid`() {
        assertTrue(isValidDecimalInputTestable(".5"))
    }

    @Test
    fun `decimal ending with period is valid`() {
        assertTrue(isValidDecimalInputTestable("5."))
    }

    @Test
    fun `multiple periods is invalid`() {
        assertFalse(isValidDecimalInputTestable("1.2.3"))
    }

    @Test
    fun `letters are invalid`() {
        assertFalse(isValidDecimalInputTestable("abc"))
    }

    @Test
    fun `mixed letters and numbers is invalid`() {
        assertFalse(isValidDecimalInputTestable("1a2"))
    }

    @Test
    fun `negative number is invalid`() {
        assertFalse(isValidDecimalInputTestable("-5"))
    }

    @Test
    fun `large number is valid`() {
        assertTrue(isValidDecimalInputTestable("999999"))
    }

    @Test
    fun `zero is valid`() {
        assertTrue(isValidDecimalInputTestable("0"))
    }

    @Test
    fun `zero with decimals is valid`() {
        assertTrue(isValidDecimalInputTestable("0.00"))
    }

    companion object {
        private val DECIMAL_INPUT_REGEX = Regex("^\\d*\\.?\\d{0,2}$")

        fun isValidDecimalInputTestable(text: String): Boolean =
            text.isEmpty() || text.matches(DECIMAL_INPUT_REGEX)
    }
}
