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

    // ==================== Ordinal Tests ====================

    @Test
    fun `DEFAULT has ordinal 0`() {
        assertEquals(0, MapColorMode.DEFAULT.ordinal)
    }

    @Test
    fun `SECURITY_RISK has ordinal 1`() {
        assertEquals(1, MapColorMode.SECURITY_RISK.ordinal)
    }

    @Test
    fun `VISA_REQUIREMENTS has ordinal 2`() {
        assertEquals(2, MapColorMode.VISA_REQUIREMENTS.ordinal)
    }

    @Test
    fun `PASSPORT_VALIDITY has ordinal 3`() {
        assertEquals(3, MapColorMode.PASSPORT_VALIDITY.ordinal)
    }

    // ==================== Invalid valueOf Tests ====================

    @Test(expected = IllegalArgumentException::class)
    fun `valueOf with invalid name throws IllegalArgumentException`() {
        MapColorMode.valueOf("INVALID_MODE")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `valueOf with lowercase name throws IllegalArgumentException`() {
        MapColorMode.valueOf("default")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `valueOf with empty string throws IllegalArgumentException`() {
        MapColorMode.valueOf("")
    }

    // ==================== Enum Name Tests ====================

    @Test
    fun `DEFAULT name property returns DEFAULT`() {
        assertEquals("DEFAULT", MapColorMode.DEFAULT.name)
    }

    @Test
    fun `SECURITY_RISK name property returns SECURITY_RISK`() {
        assertEquals("SECURITY_RISK", MapColorMode.SECURITY_RISK.name)
    }

    @Test
    fun `VISA_REQUIREMENTS name property returns VISA_REQUIREMENTS`() {
        assertEquals("VISA_REQUIREMENTS", MapColorMode.VISA_REQUIREMENTS.name)
    }

    @Test
    fun `PASSPORT_VALIDITY name property returns PASSPORT_VALIDITY`() {
        assertEquals("PASSPORT_VALIDITY", MapColorMode.PASSPORT_VALIDITY.name)
    }

    // ==================== Display Name Content Tests ====================

    @Test
    fun `displayNames do not contain underscores`() {
        MapColorMode.entries.forEach { mode ->
            assertFalse(
                "Display name '${mode.displayName}' should not contain underscores",
                mode.displayName.contains("_")
            )
        }
    }

    @Test
    fun `displayNames start with capital letter`() {
        MapColorMode.entries.forEach { mode ->
            assertTrue(
                "Display name '${mode.displayName}' should start with capital letter",
                mode.displayName.first().isUpperCase()
            )
        }
    }

    @Test
    fun `displayNames are human readable`() {
        // Each display name should have proper spacing (no camelCase)
        MapColorMode.entries.forEach { mode ->
            val words = mode.displayName.split(" ")
            words.forEach { word ->
                assertTrue(
                    "Word '$word' in '${mode.displayName}' should start with capital",
                    word.first().isUpperCase()
                )
            }
        }
    }

    // ==================== Entries Iteration Tests ====================

    @Test
    fun `entries can be iterated with forEach`() {
        var count = 0
        MapColorMode.entries.forEach { _ -> count++ }
        assertEquals(4, count)
    }

    @Test
    fun `entries can be filtered`() {
        val nonDefaultModes = MapColorMode.entries.filter { it != MapColorMode.DEFAULT }
        assertEquals(3, nonDefaultModes.size)
        assertFalse(nonDefaultModes.contains(MapColorMode.DEFAULT))
    }

    @Test
    fun `entries can be mapped to display names`() {
        val displayNames = MapColorMode.entries.map { it.displayName }
        assertEquals(4, displayNames.size)
        assertTrue(displayNames.contains("Default"))
        assertTrue(displayNames.contains("Security Risk"))
        assertTrue(displayNames.contains("Visa Requirements"))
        assertTrue(displayNames.contains("Passport Validity"))
    }

    // ==================== Equality Tests ====================

    @Test
    fun `same enum values are equal`() {
        assertEquals(MapColorMode.DEFAULT, MapColorMode.DEFAULT)
        assertEquals(MapColorMode.SECURITY_RISK, MapColorMode.SECURITY_RISK)
    }

    @Test
    fun `different enum values are not equal`() {
        assertNotEquals(MapColorMode.DEFAULT, MapColorMode.SECURITY_RISK)
        assertNotEquals(MapColorMode.VISA_REQUIREMENTS, MapColorMode.PASSPORT_VALIDITY)
    }

    // ==================== DEFAULT Mode Special Tests ====================

    @Test
    fun `DEFAULT is first in entries`() {
        assertEquals(MapColorMode.DEFAULT, MapColorMode.entries.first())
    }

    @Test
    fun `non-DEFAULT modes count is 3`() {
        val nonDefaultCount = MapColorMode.entries.count { it != MapColorMode.DEFAULT }
        assertEquals(3, nonDefaultCount)
    }
}
