package com.unstampedpages.app.ui.screens.countryinfo

import java.util.Locale

/**
 * Maps GeoJSON 3-letter alpha-3 codes for standalone territory features to their
 * ISO 3166-1 alpha-2 codes. Android's [Locale] can resolve alpha-2 codes to localized
 * display names, so this is the primary path for territory name localization.
 */
private val GEO_ID_TO_ISO_ALPHA2: Map<String, String> = mapOf(
    // UK overseas territories
    "AIA" to "AI", "BMU" to "BM", "CYM" to "KY", "FLK" to "FK",
    "GGY" to "GG", "GIB" to "GI", "IMN" to "IM", "IOT" to "IO",
    "JEY" to "JE", "MSR" to "MS", "PCN" to "PN", "SGS" to "GS",
    "SHN" to "SH", "TCA" to "TC", "VGB" to "VG",
    // US territories
    "ASM" to "AS", "GUM" to "GU", "MNP" to "MP", "PRI" to "PR",
    "VIR" to "VI", "UMI" to "UM",
    // French territories
    "ATF" to "TF", "BLM" to "BL", "MAF" to "MF", "NCL" to "NC",
    "PYF" to "PF", "SPM" to "PM", "WLF" to "WF",
    // Dutch territories
    "ABW" to "AW", "CUW" to "CW", "SXM" to "SX",
    // Danish territories
    "FRO" to "FO", "GRL" to "GL",
    // Australian territories
    "CXR" to "CX", "CCK" to "CC", "HMD" to "HM", "NFK" to "NF",
    // New Zealand territories
    "COK" to "CK", "NIU" to "NU", "TKL" to "TK",
    // Finnish territories
    "ALD" to "AX",
    // Chinese territories
    "HKG" to "HK", "MAC" to "MO",
    // Other
    "SAH" to "EH",
)

/**
 * English fallback names for territory GeoJSON IDs whose ISO alpha-2 code may not be
 * recognized by all Android versions / locales. Also covers Kosovo (XK) which is not an
 * official ISO 3166-1 entry.
 */
private val GEO_ID_ENGLISH_FALLBACK: Map<String, String> = mapOf(
    "AIA" to "Anguilla",
    "BMU" to "Bermuda",
    "CYM" to "Cayman Islands",
    "FLK" to "Falkland Islands",
    "GGY" to "Guernsey",
    "GIB" to "Gibraltar",
    "IMN" to "Isle of Man",
    "IOT" to "British Indian Ocean Territory",
    "JEY" to "Jersey",
    "MSR" to "Montserrat",
    "PCN" to "Pitcairn Islands",
    "SGS" to "South Georgia and the South Sandwich Islands",
    "SHN" to "Saint Helena",
    "TCA" to "Turks and Caicos Islands",
    "VGB" to "British Virgin Islands",
    "ASM" to "American Samoa",
    "GUM" to "Guam",
    "MNP" to "Northern Mariana Islands",
    "PRI" to "Puerto Rico",
    "VIR" to "US Virgin Islands",
    "UMI" to "United States Minor Outlying Islands",
    "ATF" to "French Southern Territories",
    "BLM" to "Saint Barthélemy",
    "MAF" to "Saint Martin",
    "NCL" to "New Caledonia",
    "PYF" to "French Polynesia",
    "SPM" to "Saint Pierre and Miquelon",
    "WLF" to "Wallis and Futuna",
    "ABW" to "Aruba",
    "CUW" to "Curaçao",
    "SXM" to "Sint Maarten",
    "FRO" to "Faroe Islands",
    "GRL" to "Greenland",
    "CXR" to "Christmas Island",
    "CCK" to "Cocos (Keeling) Islands",
    "HMD" to "Heard Island and McDonald Islands",
    "NFK" to "Norfolk Island",
    "COK" to "Cook Islands",
    "NIU" to "Niue",
    "TKL" to "Tokelau",
    "ALD" to "Åland Islands",
    "HKG" to "Hong Kong",
    "MAC" to "Macau",
    "SAH" to "Western Sahara",
    "KOS" to "Kosovo",
)

/**
 * Returns the localized display name for a standalone territory GeoJSON feature, or null
 * if [geoId] is not a known territory. Uses Android's built-in [Locale] data where possible,
 * falling back to the English name.
 */
internal fun getLocalizedTerritoryName(
    geoId: String,
    locale: Locale = Locale.getDefault()
): String? {
    val alpha2 = GEO_ID_TO_ISO_ALPHA2[geoId]
    if (alpha2 != null) {
        val localized = Locale.Builder().setRegion(alpha2).build().getDisplayCountry(locale)
        if (localized.isNotBlank() && localized != alpha2) return localized
    }
    return GEO_ID_ENGLISH_FALLBACK[geoId]
}

/**
 * Maps GeoJSON 3-letter feature IDs for standalone territory features to their
 * human-readable display name. Used so that tapping a territory polygon on the map
 * shows the territory name rather than the parent sovereign country name.
 *
 * Only standalone GeoJSON features are listed here (separate polygons with their own
 * ISO 3166-1 alpha-3 code). Embedded territories such as Easter Island (within Chile's
 * GeoJSON) are not separately identifiable on the map and are therefore excluded.
 *
 * @deprecated Use [getLocalizedTerritoryName] for locale-aware resolution.
 */
internal val GEO_ID_TO_TERRITORY_NAME: Map<String, String> = mapOf(
    // UK overseas territories
    "AIA" to "Anguilla",
    "BMU" to "Bermuda",
    "CYM" to "Cayman Islands",
    "FLK" to "Falkland Islands",
    "GGY" to "Guernsey",
    "GIB" to "Gibraltar",
    "IMN" to "Isle of Man",
    "IOT" to "British Indian Ocean Territory",
    "JEY" to "Jersey",
    "MSR" to "Montserrat",
    "PCN" to "Pitcairn Islands",
    "SGS" to "South Georgia and the South Sandwich Islands",
    "SHN" to "Saint Helena",
    "TCA" to "Turks and Caicos Islands",
    "VGB" to "British Virgin Islands",
    // US territories
    "ASM" to "American Samoa",
    "GUM" to "Guam",
    "MNP" to "Northern Mariana Islands",
    "PRI" to "Puerto Rico",
    "VIR" to "US Virgin Islands",
    "UMI" to "United States Minor Outlying Islands",
    // French territories
    "ATF" to "French Southern Territories",
    "BLM" to "Saint Barthélemy",
    "MAF" to "Saint Martin",
    "NCL" to "New Caledonia",
    "PYF" to "French Polynesia",
    "SPM" to "Saint Pierre and Miquelon",
    "WLF" to "Wallis and Futuna",
    // Dutch territories
    "ABW" to "Aruba",
    "CUW" to "Curaçao",
    "SXM" to "Sint Maarten",
    // Danish territories
    "FRO" to "Faroe Islands",
    "GRL" to "Greenland",
    // Australian territories
    "CXR" to "Christmas Island",
    "CCK" to "Cocos (Keeling) Islands",
    "HMD" to "Heard Island and McDonald Islands",
    "NFK" to "Norfolk Island",
    // New Zealand territories
    "COK" to "Cook Islands",
    "NIU" to "Niue",
    "TKL" to "Tokelau",
    // Finnish territories
    "ALD" to "Åland Islands",
    // Chinese territories
    "HKG" to "Hong Kong",
    "MAC" to "Macau",
    // Other
    "SAH" to "Western Sahara",
    "KOS" to "Kosovo",
)

/**
 * Maps territory / dependency display names to their parent sovereign country's repo ID.
 * Used to make territories searchable — selecting one opens the parent country's detail sheet.
 *
 * Covers two categories:
 *  1. Standalone GeoJSON features that map to a parent country (e.g. Cayman Islands → gb)
 *  2. Named regions that are geographic sub-units of a parent country (e.g. Azores → pt)
 */
internal val TERRITORY_ALIASES: List<Pair<String, String>> = listOf(

    // ── UK overseas territories ───────────────────────────────────────────────
    "Anguilla" to "gb",
    "Bermuda" to "gb",
    "British Indian Ocean Territory" to "gb",
    "British Virgin Islands" to "gb",
    "Cayman Islands" to "gb",
    "Falkland Islands" to "gb",
    "Malvinas" to "gb",
    "Gibraltar" to "gb",
    "Guernsey" to "gb",
    "Isle of Man" to "gb",
    "Jersey" to "gb",
    "Montserrat" to "gb",
    "Pitcairn Islands" to "gb",
    "Saint Helena" to "gb",
    "Ascension Island" to "gb",
    "Tristan da Cunha" to "gb",
    "South Georgia" to "gb",
    "South Sandwich Islands" to "gb",
    "Turks and Caicos Islands" to "gb",

    // ── US territories & states often searched separately ────────────────────
    "American Samoa" to "us",
    "Guam" to "us",
    "Northern Mariana Islands" to "us",
    "Puerto Rico" to "us",
    "US Virgin Islands" to "us",
    "United States Virgin Islands" to "us",
    "Hawaii" to "us",
    "Alaska" to "us",

    // ── French overseas territories & regions ────────────────────────────────
    "French Polynesia" to "fr",
    "Tahiti" to "fr",
    "Bora Bora" to "fr",
    "French Southern Territories" to "fr",
    "French Guiana" to "fr",
    "Guadeloupe" to "fr",
    "Martinique" to "fr",
    "Mayotte" to "fr",
    "New Caledonia" to "fr",
    "Réunion" to "fr",
    "Reunion" to "fr",
    "Saint Barthélemy" to "fr",
    "Saint Barthelemy" to "fr",
    "Saint Martin" to "fr",
    "Saint Pierre and Miquelon" to "fr",
    "Wallis and Futuna" to "fr",
    "Corsica" to "fr",

    // ── Dutch territories ────────────────────────────────────────────────────
    "Aruba" to "nl",
    "Bonaire" to "nl",
    "Curaçao" to "nl",
    "Curacao" to "nl",
    "Sint Maarten" to "nl",
    "Sint Eustatius" to "nl",
    "Saba" to "nl",

    // ── Danish territories ───────────────────────────────────────────────────
    "Faroe Islands" to "dk",
    "Greenland" to "dk",

    // ── Australian territories ───────────────────────────────────────────────
    "Christmas Island" to "au",
    "Cocos Islands" to "au",
    "Keeling Islands" to "au",
    "Heard Island" to "au",
    "McDonald Islands" to "au",
    "Norfolk Island" to "au",

    // ── New Zealand territories ──────────────────────────────────────────────
    "Cook Islands" to "nz",
    "Niue" to "nz",
    "Tokelau" to "nz",

    // ── Finnish territories ──────────────────────────────────────────────────
    "Åland Islands" to "fi",
    "Aland Islands" to "fi",

    // ── Chinese territories ──────────────────────────────────────────────────
    "Hong Kong" to "cn",
    "Macau" to "cn",
    "Macao" to "cn",

    // ── Chilean territories ──────────────────────────────────────────────────
    "Easter Island" to "cl",
    "Rapa Nui" to "cl",
    "Juan Fernández Islands" to "cl",

    // ── Portuguese territories ───────────────────────────────────────────────
    "Azores" to "pt",
    "Madeira" to "pt",

    // ── Spanish territories & autonomous communities ──────────────────────────
    "Balearic Islands" to "es",
    "Mallorca" to "es",
    "Majorca" to "es",
    "Ibiza" to "es",
    "Menorca" to "es",
    "Canary Islands" to "es",
    "Gran Canaria" to "es",
    "Tenerife" to "es",
    "Lanzarote" to "es",
    "Ceuta" to "es",
    "Melilla" to "es",

    // ── Italian islands ──────────────────────────────────────────────────────
    "Sardinia" to "it",
    "Sicily" to "it",

    // ── Norwegian territories ────────────────────────────────────────────────
    "Svalbard" to "no",
    "Jan Mayen" to "no",

    // ── Other territories / administered regions ─────────────────────────────
    "Western Sahara" to "ma",
    "Kosovo" to "rs",
    "Kaliningrad" to "ru",
    "Sakhalin" to "ru",
    "Andaman Islands" to "in",
    "Nicobar Islands" to "in",
)

/**
 * Returns a combined list of search aliases for territories in both English and the given
 * [locale]. Combining both ensures that users can search in either language (e.g. "Cayman
 * Islands" or "Islas Caimán" both find the UK parent country).
 *
 * The localized aliases are derived from [GEO_ID_TO_ISO_ALPHA2] for standalone GeoJSON
 * territory features; embedded territories (Azores, Easter Island, etc.) remain English-only
 * because they have no independent alpha-2 ISO code.
 */
internal fun getCombinedTerritoryAliases(
    locale: Locale = Locale.getDefault()
): List<Pair<String, String>> {
    // Resolve geoJsonToRepoId lazily here to avoid a circular-init dependency in the file.
    val localizedAliases = GEO_ID_TO_ISO_ALPHA2.entries.mapNotNull { (geoId, alpha2) ->
        val parentId = geoJsonToRepoId[geoId] ?: return@mapNotNull null
        val localized = Locale.Builder().setRegion(alpha2).build().getDisplayCountry(locale)
        if (localized.isBlank() || localized == alpha2) return@mapNotNull null
        localized to parentId
    }
    return TERRITORY_ALIASES + localizedAliases
}
