package com.unstampedpages.app.ui.screens.countryinfo

import org.junit.Assert.*
import org.junit.Test

class MapColorModeTest {

    @Test
    fun `MapColorMode has four values`() {
        val modes = MapColorMode.entries
        assertEquals(4, modes.size)
    }

    @Test
    fun `MapColorMode DEFAULT has correct displayName`() {
        assertEquals("Default", MapColorMode.DEFAULT.displayName)
    }

    @Test
    fun `MapColorMode SECURITY_RISK has correct displayName`() {
        assertEquals("Security Risk", MapColorMode.SECURITY_RISK.displayName)
    }

    @Test
    fun `MapColorMode VISA_REQUIREMENTS has correct displayName`() {
        assertEquals("Visa Requirements", MapColorMode.VISA_REQUIREMENTS.displayName)
    }

    @Test
    fun `MapColorMode PASSPORT_VALIDITY has correct displayName`() {
        assertEquals("Passport Validity", MapColorMode.PASSPORT_VALIDITY.displayName)
    }

    @Test
    fun `MapColorMode values are in expected order`() {
        val modes = MapColorMode.entries.toList()
        assertEquals(MapColorMode.DEFAULT, modes[0])
        assertEquals(MapColorMode.SECURITY_RISK, modes[1])
        assertEquals(MapColorMode.VISA_REQUIREMENTS, modes[2])
        assertEquals(MapColorMode.PASSPORT_VALIDITY, modes[3])
    }

    @Test
    fun `MapColorMode valueOf works for DEFAULT`() {
        assertEquals(MapColorMode.DEFAULT, MapColorMode.valueOf("DEFAULT"))
    }

    @Test
    fun `MapColorMode valueOf works for SECURITY_RISK`() {
        assertEquals(MapColorMode.SECURITY_RISK, MapColorMode.valueOf("SECURITY_RISK"))
    }

    @Test
    fun `MapColorMode valueOf works for VISA_REQUIREMENTS`() {
        assertEquals(MapColorMode.VISA_REQUIREMENTS, MapColorMode.valueOf("VISA_REQUIREMENTS"))
    }

    @Test
    fun `MapColorMode valueOf works for PASSPORT_VALIDITY`() {
        assertEquals(MapColorMode.PASSPORT_VALIDITY, MapColorMode.valueOf("PASSPORT_VALIDITY"))
    }

    @Test
    fun `all displayNames are non-empty`() {
        MapColorMode.entries.forEach { mode ->
            assertTrue(
                "Display name for $mode should not be empty",
                mode.displayName.isNotEmpty()
            )
        }
    }

    @Test
    fun `all displayNames are unique`() {
        val displayNames = MapColorMode.entries.map { it.displayName }
        assertEquals(
            "All display names should be unique",
            displayNames.size,
            displayNames.distinct().size
        )
    }
}
