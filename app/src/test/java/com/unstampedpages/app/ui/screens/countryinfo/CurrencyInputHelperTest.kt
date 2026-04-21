package com.unstampedpages.app.ui.screens.countryinfo

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for the currency input validation and first-keystroke handling
 * used in CountryDetailSheet.
 */
class CurrencyInputHelperTest {

    // ==================== First Keystroke Extraction Tests ====================

    @Test
    fun `extractNewInput when selection worked returns new text as-is`() {
        // User focused on "1.00", typed "5", selection replaced → result is "5"
        assertEquals("5", extractNewInputOnFirstKeystroke("1.00", "5"))
    }

    @Test
    fun `extractNewInput when digit appended at end extracts only new digit`() {
        // User focused on "1.00", typed "5", selection didn't replace → "1.005"
        assertEquals("5", extractNewInputOnFirstKeystroke("1.00", "1.005"))
    }

    @Test
    fun `extractNewInput when digit prepended at start extracts only new digit`() {
        // User focused on "1.00", typed "5" at start → "51.00"
        assertEquals("5", extractNewInputOnFirstKeystroke("1.00", "51.00"))
    }

    @Test
    fun `extractNewInput when multiple digits appended extracts all new digits`() {
        // User pasted "99" at end → "1.0099"
        assertEquals("99", extractNewInputOnFirstKeystroke("1.00", "1.0099"))
    }

    @Test
    fun `extractNewInput when new text is shorter returns new text`() {
        // User deleted characters (backspace)
        assertEquals("1.0", extractNewInputOnFirstKeystroke("1.00", "1.0"))
    }

    @Test
    fun `extractNewInput when new text is same length returns new text`() {
        // User replaced a character (e.g., "1.00" → "1.05")
        assertEquals("1.05", extractNewInputOnFirstKeystroke("1.00", "1.05"))
    }

    @Test
    fun `extractNewInput when old text empty returns new text`() {
        // Field was empty, user typed "5"
        assertEquals("5", extractNewInputOnFirstKeystroke("", "5"))
    }

    @Test
    fun `extractNewInput when new text empty returns empty`() {
        // User cleared the field
        assertEquals("", extractNewInputOnFirstKeystroke("1.00", ""))
    }

    @Test
    fun `extractNewInput when digit inserted in middle extracts only new digit`() {
        // User focused on "1.00", tapped in middle, typed "5" → "15.00"
        assertEquals("5", extractNewInputOnFirstKeystroke("1.00", "15.00"))
    }

    @Test
    fun `extractNewInput when digit inserted after decimal extracts only new digit`() {
        // User focused on "1.00", tapped after decimal, typed "5" → "1.500"
        assertEquals("5", extractNewInputOnFirstKeystroke("1.00", "1.500"))
    }

    @Test
    fun `extractNewInput when texts completely different returns new text`() {
        // Unusual case where strings are completely different
        // With new algorithm: finds first difference at position 0, extracts 3 chars
        assertEquals("abc", extractNewInputOnFirstKeystroke("xyz", "abcxyz"))
    }

    @Test
    fun `extractNewInput with decimal point appended`() {
        // User typed "." after "5" → "5."
        assertEquals(".", extractNewInputOnFirstKeystroke("5", "5."))
    }

    @Test
    fun `extractNewInput single digit to double digit`() {
        // User focused on "1", typed "2" → either "2" (selection worked) or "12" or "21"
        assertEquals("2", extractNewInputOnFirstKeystroke("1", "2"))
        assertEquals("2", extractNewInputOnFirstKeystroke("1", "12"))
        assertEquals("2", extractNewInputOnFirstKeystroke("1", "21"))
    }

    @Test
    fun `extractNewInput handles insertion at various positions in longer number`() {
        // Insertion at different positions in "12.34"
        assertEquals("5", extractNewInputOnFirstKeystroke("12.34", "512.34"))  // start
        assertEquals("5", extractNewInputOnFirstKeystroke("12.34", "152.34"))  // after first digit
        assertEquals("5", extractNewInputOnFirstKeystroke("12.34", "125.34"))  // before decimal
        assertEquals("5", extractNewInputOnFirstKeystroke("12.34", "12.534"))  // after decimal
        assertEquals("5", extractNewInputOnFirstKeystroke("12.34", "12.354"))  // before last digit
        assertEquals("5", extractNewInputOnFirstKeystroke("12.34", "12.345"))  // end
    }

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

        /**
         * Test-accessible version of extractNewInputOnFirstKeystroke.
         * Mirrors the logic in CountryDetailSheet.kt.
         */
        fun extractNewInputOnFirstKeystroke(oldText: String, newText: String): String {
            if (newText.length <= oldText.length) {
                return newText
            }

            // Find the inserted character(s) by comparing strings
            val addedCount = newText.length - oldText.length

            // Find insertion point by comparing characters from the start
            var insertPos = 0
            while (insertPos < oldText.length && insertPos < newText.length && oldText[insertPos] == newText[insertPos]) {
                insertPos++
            }

            // Extract the newly inserted characters
            return newText.substring(insertPos, insertPos + addedCount)
        }
    }
}
