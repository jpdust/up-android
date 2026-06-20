package com.unstampedpages.app.ui.screens.countryinfo

import com.unstampedpages.app.R
import org.junit.Assert.*
import org.junit.Test

class CustomFlagProviderTest {

    @Test
    fun `getFlagDrawable returns drawable for Somaliland`() {
        assertEquals(R.drawable.flag_xso, CustomFlagProvider.getFlagDrawable("xso"))
    }

    @Test
    fun `getFlagDrawable returns drawable for Northern Cyprus`() {
        assertEquals(R.drawable.flag_xnc, CustomFlagProvider.getFlagDrawable("xnc"))
    }

    @Test
    fun `getFlagDrawable returns drawable for Northern Ireland`() {
        assertEquals(R.drawable.flag_xni, CustomFlagProvider.getFlagDrawable("xni"))
    }

    @Test
    fun `getFlagDrawable returns null for country without override`() {
        assertNull(CustomFlagProvider.getFlagDrawable("us"))
    }

    @Test
    fun `getFlagDrawable returns null for empty string`() {
        assertNull(CustomFlagProvider.getFlagDrawable(""))
    }

    @Test
    fun `getFlagDrawable returns null for unknown id`() {
        assertNull(CustomFlagProvider.getFlagDrawable("zzz"))
    }

    @Test
    fun `hasCustomFlag returns true for Somaliland`() {
        assertTrue(CustomFlagProvider.hasCustomFlag("xso"))
    }

    @Test
    fun `hasCustomFlag returns true for Northern Cyprus`() {
        assertTrue(CustomFlagProvider.hasCustomFlag("xnc"))
    }

    @Test
    fun `hasCustomFlag returns true for Northern Ireland`() {
        assertTrue(CustomFlagProvider.hasCustomFlag("xni"))
    }

    @Test
    fun `hasCustomFlag returns false for country without override`() {
        assertFalse(CustomFlagProvider.hasCustomFlag("us"))
    }

    @Test
    fun `hasCustomFlag returns false for empty string`() {
        assertFalse(CustomFlagProvider.hasCustomFlag(""))
    }

    @Test
    fun `hasCustomFlag is consistent with getFlagDrawable`() {
        val countryCodes = listOf("xnc", "xni", "xso", "us", "gb", "fr", "jp", "", "zzz")
        for (code in countryCodes) {
            assertEquals(
                "hasCustomFlag and getFlagDrawable should agree for '$code'",
                CustomFlagProvider.hasCustomFlag(code),
                CustomFlagProvider.getFlagDrawable(code) != null
            )
        }
    }
}
