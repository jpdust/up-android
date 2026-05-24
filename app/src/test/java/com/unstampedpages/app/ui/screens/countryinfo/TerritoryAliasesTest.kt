package com.unstampedpages.app.ui.screens.countryinfo

import org.junit.Assert.*
import org.junit.Test
import java.util.Locale

/**
 * Unit tests for TerritoryAliases.kt — covers [getLocalizedTerritoryName] and
 * [getCombinedTerritoryAliases]. All JVM-safe: no Android runtime required.
 */
class TerritoryAliasesTest {

    // ==================== getLocalizedTerritoryName ====================

    @Test
    fun `returns null for unknown geoId`() {
        assertNull(getLocalizedTerritoryName("ZZZ"))
    }

    @Test
    fun `returns null for empty string geoId`() {
        assertNull(getLocalizedTerritoryName(""))
    }

    @Test
    fun `returns non-null for every geoId in GEO_ID_ENGLISH_FALLBACK`() {
        // The GEO_ID_ENGLISH_FALLBACK keys are the complete set of supported territory geoIds.
        // Every one must resolve to something.
        val fallbackKeys = listOf(
            "AIA", "BMU", "CYM", "FLK", "GGY", "GIB", "IMN", "IOT", "JEY",
            "MSR", "PCN", "SGS", "SHN", "TCA", "VGB",
            "ASM", "GUM", "MNP", "PRI", "VIR", "UMI",
            "GUF", "MTQ", "ATF", "BLM", "MAF", "NCL", "PYF", "SPM", "WLF",
            "ABW", "CUW", "SXM",
            "FRO", "GRL",
            "CXR", "CCK", "HMD", "NFK",
            "COK", "NIU", "TKL",
            "ALD",
            "HKG", "MAC",
            "SAH",
            "KOS"
        )
        fallbackKeys.forEach { geoId ->
            val name = getLocalizedTerritoryName(geoId, Locale.ENGLISH)
            assertNotNull("Expected non-null name for geoId=$geoId", name)
            assertTrue("Expected non-blank name for geoId=$geoId", name!!.isNotBlank())
        }
    }

    @Test
    fun `returns English fallback for CYM (Cayman Islands) in English locale`() {
        val name = getLocalizedTerritoryName("CYM", Locale.ENGLISH)
        assertNotNull(name)
        assertTrue("Expected 'Cayman' in name, got: $name", name!!.contains("Cayman", ignoreCase = true))
    }

    @Test
    fun `returns English fallback for PRI (Puerto Rico) in English locale`() {
        val name = getLocalizedTerritoryName("PRI", Locale.ENGLISH)
        assertNotNull(name)
        assertTrue("Expected 'Puerto Rico' in name, got: $name", name!!.contains("Puerto Rico", ignoreCase = true))
    }

    @Test
    fun `returns English fallback for GRL (Greenland) in English locale`() {
        val name = getLocalizedTerritoryName("GRL", Locale.ENGLISH)
        assertNotNull(name)
        assertTrue("Expected 'Greenland' in name, got: $name", name!!.contains("Greenland", ignoreCase = true))
    }

    @Test
    fun `returns English fallback for HKG (Hong Kong) in English locale`() {
        val name = getLocalizedTerritoryName("HKG", Locale.ENGLISH)
        assertNotNull(name)
        assertTrue("Expected 'Hong Kong' in name, got: $name", name!!.contains("Hong Kong", ignoreCase = true))
    }

    @Test
    fun `returns English fallback for GUF (French Guiana)`() {
        val name = getLocalizedTerritoryName("GUF", Locale.ENGLISH)
        assertNotNull(name)
        assertTrue("Expected 'French Guiana' in name, got: $name", name!!.contains("French Guiana", ignoreCase = true))
    }

    @Test
    fun `returns English fallback for MTQ (Martinique)`() {
        val name = getLocalizedTerritoryName("MTQ", Locale.ENGLISH)
        assertNotNull(name)
        assertTrue("Expected 'Martinique' in name, got: $name", name!!.contains("Martinique", ignoreCase = true))
    }

    @Test
    fun `returns English fallback for KOS (Kosovo) which has no ISO alpha-2`() {
        // KOS is not in GEO_ID_TO_ISO_ALPHA2, so it must fall through to GEO_ID_ENGLISH_FALLBACK.
        val name = getLocalizedTerritoryName("KOS", Locale.ENGLISH)
        assertNotNull(name)
        assertEquals("Kosovo", name)
    }

    @Test
    fun `returns English fallback for SAH (Western Sahara)`() {
        val name = getLocalizedTerritoryName("SAH", Locale.ENGLISH)
        assertNotNull(name)
        assertTrue("Expected 'Western Sahara' in name, got: $name", name!!.contains("Sahara", ignoreCase = true))
    }

    @Test
    fun `Spanish locale produces a non-null name for CYM`() {
        // Whether the JVM produces a Spanish translation or falls back to English,
        // the result must always be non-null and non-blank.
        val name = getLocalizedTerritoryName("CYM", Locale.forLanguageTag("es"))
        assertNotNull(name)
        assertTrue(name!!.isNotBlank())
    }

    @Test
    fun `Spanish locale produces a non-null name for GRL`() {
        val name = getLocalizedTerritoryName("GRL", Locale.forLanguageTag("es"))
        assertNotNull(name)
        assertTrue(name!!.isNotBlank())
    }

    @Test
    fun `French locale produces a non-null name for PRI`() {
        val name = getLocalizedTerritoryName("PRI", Locale.forLanguageTag("fr"))
        assertNotNull(name)
        assertTrue(name!!.isNotBlank())
    }

    @Test
    fun `result is never the raw alpha-2 code`() {
        // If Locale.getDisplayCountry returns the bare region code (e.g. "KY"), the function
        // must fall through to the English fallback instead of returning the code itself.
        listOf("AIA", "BMU", "CYM", "PRI", "GRL", "FRO", "HKG").forEach { geoId ->
            val name = getLocalizedTerritoryName(geoId, Locale.ENGLISH)
            assertNotNull(name)
            // Name should not be a 2-letter region code
            assertFalse(
                "Name for $geoId should not be a bare region code, got: $name",
                name!!.length == 2 && name == name.uppercase()
            )
        }
    }

    // ==================== getCombinedTerritoryAliases ====================

    @Test
    fun `combined aliases includes all English TERRITORY_ALIASES entries`() {
        val combined = getCombinedTerritoryAliases(Locale.ENGLISH)
        val names = combined.map { it.first }.toSet()

        // Spot-check a spread of entries from TERRITORY_ALIASES
        listOf(
            "Cayman Islands", "Puerto Rico", "French Polynesia",
            "Greenland", "Hong Kong", "Easter Island", "Azores",
            "Kosovo", "Canary Islands", "Sicily"
        ).forEach { alias ->
            assertTrue("TERRITORY_ALIASES entry '$alias' missing from combined list", names.contains(alias))
        }
    }

    @Test
    fun `combined aliases parent ids are all non-blank`() {
        getCombinedTerritoryAliases(Locale.ENGLISH).forEach { (name, parentId) ->
            assertTrue("Parent id for '$name' should be non-blank", parentId.isNotBlank())
        }
    }

    @Test
    fun `combined aliases names are all non-blank`() {
        getCombinedTerritoryAliases(Locale.ENGLISH).forEach { (name, _) ->
            assertTrue("Alias name should be non-blank", name.isNotBlank())
        }
    }

    @Test
    fun `combined aliases size is at least as large as TERRITORY_ALIASES`() {
        val combined = getCombinedTerritoryAliases(Locale.ENGLISH)
        // Localized aliases are appended on top of the English list, so combined >= English-only.
        assertTrue(combined.size >= TERRITORY_ALIASES.size)
    }

    @Test
    fun `combined aliases with Spanish locale includes at least as many entries as English`() {
        val english = getCombinedTerritoryAliases(Locale.ENGLISH)
        val spanish = getCombinedTerritoryAliases(Locale.forLanguageTag("es"))
        // Spanish JVM ICU data resolves many territory alpha-2 codes, so Spanish list should
        // be at least as large (often larger with extra localized aliases).
        assertTrue(spanish.size >= english.size)
    }

    @Test
    fun `Cayman Islands maps to ky in combined aliases`() {
        val combined = getCombinedTerritoryAliases(Locale.ENGLISH)
        val entry = combined.find { it.first == "Cayman Islands" }
        assertNotNull("'Cayman Islands' not found in combined aliases", entry)
        assertEquals("ky", entry!!.second)
    }

    @Test
    fun `Puerto Rico maps to us in combined aliases`() {
        val combined = getCombinedTerritoryAliases(Locale.ENGLISH)
        val entry = combined.find { it.first == "Puerto Rico" }
        assertNotNull("'Puerto Rico' not found in combined aliases", entry)
        assertEquals("us", entry!!.second)
    }

    @Test
    fun `Greenland maps to dk in combined aliases`() {
        val combined = getCombinedTerritoryAliases(Locale.ENGLISH)
        val entry = combined.find { it.first == "Greenland" }
        assertNotNull("'Greenland' not found in combined aliases", entry)
        assertEquals("dk", entry!!.second)
    }

    @Test
    fun `Hong Kong maps to cn in combined aliases`() {
        val combined = getCombinedTerritoryAliases(Locale.ENGLISH)
        val entry = combined.find { it.first == "Hong Kong" }
        assertNotNull("'Hong Kong' not found in combined aliases", entry)
        assertEquals("cn", entry!!.second)
    }

    @Test
    fun `French Polynesia maps to fr in combined aliases`() {
        val combined = getCombinedTerritoryAliases(Locale.ENGLISH)
        val entry = combined.find { it.first == "French Polynesia" }
        assertNotNull("'French Polynesia' not found in combined aliases", entry)
        assertEquals("fr", entry!!.second)
    }

    @Test
    fun `Easter Island maps to cl in combined aliases`() {
        val combined = getCombinedTerritoryAliases(Locale.ENGLISH)
        val entry = combined.find { it.first == "Easter Island" }
        assertNotNull("'Easter Island' not found in combined aliases", entry)
        assertEquals("cl", entry!!.second)
    }

    @Test
    fun `Martinique maps to mq in combined aliases`() {
        val combined = getCombinedTerritoryAliases(Locale.ENGLISH)
        val entry = combined.find { it.first == "Martinique" }
        assertNotNull("'Martinique' not found in combined aliases", entry)
        assertEquals("mq", entry!!.second)
    }

    @Test
    fun `French Guiana maps to fr in combined aliases`() {
        val combined = getCombinedTerritoryAliases(Locale.ENGLISH)
        val entry = combined.find { it.first == "French Guiana" }
        assertNotNull("'French Guiana' not found in combined aliases", entry)
        assertEquals("fr", entry!!.second)
    }

    @Test
    fun `Faroe Islands maps to fo in combined aliases`() {
        val combined = getCombinedTerritoryAliases(Locale.ENGLISH)
        val entry = combined.find { it.first == "Faroe Islands" }
        assertNotNull("'Faroe Islands' not found in combined aliases", entry)
        assertEquals("fo", entry!!.second)
    }

    @Test
    fun `Falkland Islands maps to fk in combined aliases`() {
        val combined = getCombinedTerritoryAliases(Locale.ENGLISH)
        val entry = combined.find { it.first == "Falkland Islands" }
        assertNotNull("'Falkland Islands' not found in combined aliases", entry)
        assertEquals("fk", entry!!.second)
    }

    @Test
    fun `Gibraltar maps to gi in combined aliases`() {
        val combined = getCombinedTerritoryAliases(Locale.ENGLISH)
        val entry = combined.find { it.first == "Gibraltar" }
        assertNotNull("'Gibraltar' not found in combined aliases", entry)
        assertEquals("gi", entry!!.second)
    }
}
