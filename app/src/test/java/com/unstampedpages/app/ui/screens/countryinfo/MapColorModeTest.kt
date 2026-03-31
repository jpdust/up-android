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

    // ==================== Legend-Related Mode Tests ====================

    @Test
    fun `modes that should show legend are not DEFAULT`() {
        val legendModes = listOf(
            MapColorMode.SECURITY_RISK,
            MapColorMode.VISA_REQUIREMENTS,
            MapColorMode.PASSPORT_VALIDITY
        )
        legendModes.forEach { mode ->
            assertNotEquals(MapColorMode.DEFAULT, mode)
        }
    }

    @Test
    fun `DEFAULT is the only mode without colored legend`() {
        val modesWithoutLegend = MapColorMode.entries.filter { it == MapColorMode.DEFAULT }
        assertEquals(1, modesWithoutLegend.size)
        assertEquals(MapColorMode.DEFAULT, modesWithoutLegend.first())
    }

    // ==================== CompareTo Tests ====================

    @Test
    fun `DEFAULT is less than SECURITY_RISK`() {
        assertTrue(MapColorMode.DEFAULT < MapColorMode.SECURITY_RISK)
    }

    @Test
    fun `SECURITY_RISK is less than VISA_REQUIREMENTS`() {
        assertTrue(MapColorMode.SECURITY_RISK < MapColorMode.VISA_REQUIREMENTS)
    }

    @Test
    fun `VISA_REQUIREMENTS is less than PASSPORT_VALIDITY`() {
        assertTrue(MapColorMode.VISA_REQUIREMENTS < MapColorMode.PASSPORT_VALIDITY)
    }

    @Test
    fun `PASSPORT_VALIDITY is the last mode`() {
        assertEquals(MapColorMode.PASSPORT_VALIDITY, MapColorMode.entries.last())
    }

    // ==================== HashCode Tests ====================

    @Test
    fun `hashCode is consistent for same enum value`() {
        val hash1 = MapColorMode.DEFAULT.hashCode()
        val hash2 = MapColorMode.DEFAULT.hashCode()
        assertEquals(hash1, hash2)
    }

    @Test
    fun `hashCode differs between different enum values`() {
        val hashDefault = MapColorMode.DEFAULT.hashCode()
        val hashSecurity = MapColorMode.SECURITY_RISK.hashCode()
        assertNotEquals(hashDefault, hashSecurity)
    }

    // ==================== toString Tests ====================

    @Test
    fun `toString returns enum name for DEFAULT`() {
        assertEquals("DEFAULT", MapColorMode.DEFAULT.toString())
    }

    @Test
    fun `toString returns enum name for SECURITY_RISK`() {
        assertEquals("SECURITY_RISK", MapColorMode.SECURITY_RISK.toString())
    }

    @Test
    fun `toString returns enum name for VISA_REQUIREMENTS`() {
        assertEquals("VISA_REQUIREMENTS", MapColorMode.VISA_REQUIREMENTS.toString())
    }

    @Test
    fun `toString returns enum name for PASSPORT_VALIDITY`() {
        assertEquals("PASSPORT_VALIDITY", MapColorMode.PASSPORT_VALIDITY.toString())
    }

    // ==================== When Expression Coverage Tests ====================

    @Test
    fun `all modes can be used in when expression`() {
        MapColorMode.entries.forEach { mode ->
            val result = when (mode) {
                MapColorMode.DEFAULT -> "default"
                MapColorMode.SECURITY_RISK -> "security"
                MapColorMode.VISA_REQUIREMENTS -> "visa"
                MapColorMode.PASSPORT_VALIDITY -> "passport"
            }
            assertTrue(result.isNotEmpty())
        }
    }

    @Test
    fun `when expression with else is not needed`() {
        // This test verifies exhaustive when - if compilation passes, all cases are covered
        val descriptions = MapColorMode.entries.map { mode ->
            when (mode) {
                MapColorMode.DEFAULT -> "Shows default country colors"
                MapColorMode.SECURITY_RISK -> "Shows safety level colors"
                MapColorMode.VISA_REQUIREMENTS -> "Shows visa requirement colors"
                MapColorMode.PASSPORT_VALIDITY -> "Shows passport validity colors"
            }
        }
        assertEquals(4, descriptions.size)
    }

    // ==================== Display Name Length Tests ====================

    @Test
    fun `all displayNames have reasonable length`() {
        MapColorMode.entries.forEach { mode ->
            assertTrue(
                "Display name '${mode.displayName}' should be between 5 and 25 characters",
                mode.displayName.length in 5..25
            )
        }
    }

    @Test
    fun `longest displayName has 17 characters`() {
        val longest = MapColorMode.entries.maxByOrNull { it.displayName.length }
        assertEquals(17, longest?.displayName?.length)
        // Both VISA_REQUIREMENTS and PASSPORT_VALIDITY have 17 characters
        assertTrue(
            longest == MapColorMode.VISA_REQUIREMENTS || longest == MapColorMode.PASSPORT_VALIDITY
        )
    }

    @Test
    fun `shortest displayName is Default`() {
        val shortest = MapColorMode.entries.minByOrNull { it.displayName.length }
        assertEquals(MapColorMode.DEFAULT, shortest)
    }
}
