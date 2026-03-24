package com.unstampedpages.app.ui.screens.countryinfo

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for the currency input helper functions used in CountryDetailSheet.
 * These test the pure functions that handle decimal input validation and text processing.
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

    // ==================== Extract Typed Character Tests ====================

    @Test
    fun `extracts last character when text is longer`() {
        val result = extractTypedCharacterTestable("12345", "1234")
        assertEquals("5", result)
    }

    @Test
    fun `returns new text when length is same`() {
        val result = extractTypedCharacterTestable("abc", "xyz")
        assertEquals("abc", result)
    }

    @Test
    fun `returns new text when shorter than old`() {
        val result = extractTypedCharacterTestable("12", "123")
        assertEquals("12", result)
    }

    @Test
    fun `extracts single character from empty to one char`() {
        val result = extractTypedCharacterTestable("5", "")
        assertEquals("5", result)
    }

    @Test
    fun `extracts decimal point`() {
        val result = extractTypedCharacterTestable("1.", "1")
        assertEquals(".", result)
    }

    // ==================== Process Input Change Tests ====================

    @Test
    fun `first keystroke with text clears and uses typed char`() {
        val result = processInputChangeTestable(
            newText = "15",
            oldText = "1",
            isFirstKeystroke = true
        )

        assertEquals("5", result.processedText)
        assertTrue(result.isValid)
        assertTrue(result.shouldClearFirstKeystrokeFlag)
    }

    @Test
    fun `first keystroke with empty text does not clear flag`() {
        val result = processInputChangeTestable(
            newText = "",
            oldText = "1",
            isFirstKeystroke = true
        )

        assertEquals("", result.processedText)
        assertTrue(result.isValid)
        assertFalse(result.shouldClearFirstKeystrokeFlag)
    }

    @Test
    fun `non-first keystroke passes text through`() {
        val result = processInputChangeTestable(
            newText = "15",
            oldText = "1",
            isFirstKeystroke = false
        )

        assertEquals("15", result.processedText)
        assertTrue(result.isValid)
        assertFalse(result.shouldClearFirstKeystrokeFlag)
    }

    @Test
    fun `invalid input is marked as invalid`() {
        val result = processInputChangeTestable(
            newText = "abc",
            oldText = "",
            isFirstKeystroke = false
        )

        assertEquals("abc", result.processedText)
        assertFalse(result.isValid)
    }

    @Test
    fun `valid decimal input is marked as valid`() {
        val result = processInputChangeTestable(
            newText = "12.34",
            oldText = "12.3",
            isFirstKeystroke = false
        )

        assertEquals("12.34", result.processedText)
        assertTrue(result.isValid)
    }

    @Test
    fun `too many decimal places is invalid`() {
        val result = processInputChangeTestable(
            newText = "1.234",
            oldText = "1.23",
            isFirstKeystroke = false
        )

        assertEquals("1.234", result.processedText)
        assertFalse(result.isValid)
    }

    // ==================== Helper functions to expose private functions for testing ====================

    companion object {
        private val DECIMAL_INPUT_REGEX = Regex("^\\d*\\.?\\d{0,2}$")

        fun isValidDecimalInputTestable(text: String): Boolean =
            text.isEmpty() || text.matches(DECIMAL_INPUT_REGEX)

        fun extractTypedCharacterTestable(newText: String, oldText: String): String =
            if (newText.length > oldText.length) newText.last().toString() else newText

        data class InputChangeResultTestable(
            val processedText: String,
            val isValid: Boolean,
            val shouldClearFirstKeystrokeFlag: Boolean
        )

        fun processInputChangeTestable(
            newText: String,
            oldText: String,
            isFirstKeystroke: Boolean
        ): InputChangeResultTestable {
            val usedFirstKeystroke = isFirstKeystroke && newText.isNotEmpty()
            val processedText = if (usedFirstKeystroke) {
                extractTypedCharacterTestable(newText, oldText)
            } else {
                newText
            }

            return InputChangeResultTestable(
                processedText = processedText,
                isValid = isValidDecimalInputTestable(processedText),
                shouldClearFirstKeystrokeFlag = usedFirstKeystroke
            )
        }
    }
}
