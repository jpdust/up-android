package com.unstampedpages.app.ui.screens.countryinfo

import java.util.Locale

// ── GeoJSON alpha-3 ID constants ──────────────────────────────────────────────
// Each ID appears in GEO_ID_TO_ISO_ALPHA2, GEO_ID_ENGLISH_FALLBACK, and
// GEO_ID_TO_TERRITORY_NAME. Centralising them here eliminates the duplicate-
// literal Sonar violations across all three maps.

private object GeoId {
    // UK overseas territories
    const val AIA = "AIA"
    const val BMU = "BMU"
    const val CYM = "CYM"
    const val FLK = "FLK"
    const val GGY = "GGY"
    const val GIB = "GIB"
    const val IMN = "IMN"
    const val IOT = "IOT"
    const val JEY = "JEY"
    const val MSR = "MSR"
    const val PCN = "PCN"
    const val SGS = "SGS"
    const val SHN = "SHN"
    const val TCA = "TCA"
    const val VGB = "VGB"
    // US territories
    const val ASM = "ASM"
    const val GUM = "GUM"
    const val MNP = "MNP"
    const val PRI = "PRI"
    const val VIR = "VIR"
    const val UMI = "UMI"
    // French territories
    const val GUF = "GUF"
    const val MTQ = "MTQ"
    const val ATF = "ATF"
    const val BLM = "BLM"
    const val MAF = "MAF"
    const val NCL = "NCL"
    const val PYF = "PYF"
    const val SPM = "SPM"
    const val WLF = "WLF"
    // Dutch territories
    const val ABW = "ABW"
    const val CUW = "CUW"
    const val SXM = "SXM"
    // Danish territories
    const val FRO = "FRO"
    const val GRL = "GRL"
    // Australian territories
    const val CXR = "CXR"
    const val CCK = "CCK"
    const val HMD = "HMD"
    const val NFK = "NFK"
    // New Zealand territories
    const val COK = "COK"
    const val NIU = "NIU"
    const val TKL = "TKL"
    // Finnish territories
    const val ALD = "ALD"
    // Chinese territories
    const val HKG = "HKG"
    const val MAC = "MAC"
}

// ── Territory display name constants ──────────────────────────────────────────
// Names that appear in GEO_ID_ENGLISH_FALLBACK, GEO_ID_TO_TERRITORY_NAME, and
// (for most) TERRITORY_ALIASES. Centralising them eliminates the duplicate-
// literal Sonar violations across those three collections.

private object TerritoryName {
    // UK overseas territories
    const val ANGUILLA = "Anguilla"
    const val BERMUDA = "Bermuda"
    const val CAYMAN_ISLANDS = "Cayman Islands"
    const val FALKLAND_ISLANDS = "Falkland Islands"
    const val GUERNSEY = "Guernsey"
    const val GIBRALTAR = "Gibraltar"
    const val ISLE_OF_MAN = "Isle of Man"
    const val BRITISH_INDIAN_OCEAN_TERRITORY = "British Indian Ocean Territory"
    const val JERSEY = "Jersey"
    const val MONTSERRAT = "Montserrat"
    const val PITCAIRN_ISLANDS = "Pitcairn Islands"
    const val SOUTH_GEORGIA_AND_SOUTH_SANDWICH_ISLANDS = "South Georgia and the South Sandwich Islands"
    const val SAINT_HELENA = "Saint Helena"
    const val TURKS_AND_CAICOS_ISLANDS = "Turks and Caicos Islands"
    const val BRITISH_VIRGIN_ISLANDS = "British Virgin Islands"
    // US territories
    const val AMERICAN_SAMOA = "American Samoa"
    const val GUAM = "Guam"
    const val NORTHERN_MARIANA_ISLANDS = "Northern Mariana Islands"
    const val PUERTO_RICO = "Puerto Rico"
    const val US_VIRGIN_ISLANDS = "US Virgin Islands"
    const val UNITED_STATES_MINOR_OUTLYING_ISLANDS = "United States Minor Outlying Islands"
    // French territories
    const val FRENCH_GUIANA = "French Guiana"
    const val MARTINIQUE = "Martinique"
    const val FRENCH_SOUTHERN_TERRITORIES = "French Southern Territories"
    const val SAINT_BARTHELEMY = "Saint Barthélemy"
    const val SAINT_MARTIN = "Saint Martin"
    const val NEW_CALEDONIA = "New Caledonia"
    const val FRENCH_POLYNESIA = "French Polynesia"
    const val SAINT_PIERRE_AND_MIQUELON = "Saint Pierre and Miquelon"
    const val WALLIS_AND_FUTUNA = "Wallis and Futuna"
    // Dutch territories
    const val ARUBA = "Aruba"
    const val CURACAO = "Curaçao"
    const val SINT_MAARTEN = "Sint Maarten"
    // Danish territories
    const val FAROE_ISLANDS = "Faroe Islands"
    const val GREENLAND = "Greenland"
    // Australian territories
    const val CHRISTMAS_ISLAND = "Christmas Island"
    const val COCOS_KEELING_ISLANDS = "Cocos (Keeling) Islands"
    const val HEARD_ISLAND_AND_MCDONALD_ISLANDS = "Heard Island and McDonald Islands"
    const val NORFOLK_ISLAND = "Norfolk Island"
    // New Zealand territories
    const val COOK_ISLANDS = "Cook Islands"
    const val NIUE = "Niue"
    const val TOKELAU = "Tokelau"
    // Finnish territories
    const val ALAND_ISLANDS = "Åland Islands"
    // Chinese territories
    const val HONG_KONG = "Hong Kong"
    const val MACAU = "Macau"
    // Other
}

/**
 * Maps GeoJSON 3-letter alpha-3 codes for standalone territory features to their
 * ISO 3166-1 alpha-2 codes. Android's [Locale] can resolve alpha-2 codes to localized
 * display names, so this is the primary path for territory name localization.
 */
private val GEO_ID_TO_ISO_ALPHA2: Map<String, String> = mapOf(
    // UK overseas territories
    // FLK and SHN are intentionally excluded: Android's locale API returns
    // "Falkland Islands (Islas Malvinas)" for FK and "St. Helena" for SH, both
    // different from the repository country name, which would produce duplicate
    // search results. getLocalizedTerritoryName falls back to GEO_ID_ENGLISH_FALLBACK.
    GeoId.AIA to "AI", GeoId.BMU to "BM", GeoId.CYM to "KY",
    GeoId.GGY to "GG", GeoId.GIB to "GI", GeoId.IMN to "IM", GeoId.IOT to "IO",
    GeoId.JEY to "JE", GeoId.MSR to "MS", GeoId.PCN to "PN", GeoId.SGS to "GS",
    GeoId.TCA to "TC", GeoId.VGB to "VG",
    // US territories
    GeoId.ASM to "AS", GeoId.GUM to "GU", GeoId.MNP to "MP", GeoId.PRI to "PR",
    GeoId.VIR to "VI", GeoId.UMI to "UM",
    // French territories
    // MAF excluded: locale API returns "St. Martin" for region MF, conflicting with the
    // "Saint Martin" alias in TERRITORY_ALIASES and producing a duplicate search result.
    GeoId.GUF to "GF", GeoId.MTQ to "MQ", GeoId.ATF to "TF", GeoId.BLM to "BL",
    GeoId.NCL to "NC", GeoId.PYF to "PF", GeoId.SPM to "PM", GeoId.WLF to "WF",
    // Dutch territories
    GeoId.ABW to "AW", GeoId.CUW to "CW", GeoId.SXM to "SX",
    // Danish territories
    GeoId.FRO to "FO", GeoId.GRL to "GL",
    // Australian territories
    GeoId.CXR to "CX", GeoId.CCK to "CC", GeoId.HMD to "HM", GeoId.NFK to "NF",
    // New Zealand territories
    GeoId.COK to "CK", GeoId.NIU to "NU", GeoId.TKL to "TK",
    // Finnish territories
    GeoId.ALD to "AX",
    // Chinese territories
    GeoId.HKG to "HK", GeoId.MAC to "MO",
)

/**
 * English fallback names for territory GeoJSON IDs whose ISO alpha-2 code may not be
 * recognized by all Android versions / locales.
 */
private val GEO_ID_ENGLISH_FALLBACK: Map<String, String> = mapOf(
    GeoId.AIA to TerritoryName.ANGUILLA,
    GeoId.BMU to TerritoryName.BERMUDA,
    GeoId.CYM to TerritoryName.CAYMAN_ISLANDS,
    GeoId.FLK to TerritoryName.FALKLAND_ISLANDS,
    GeoId.GGY to TerritoryName.GUERNSEY,
    GeoId.GIB to TerritoryName.GIBRALTAR,
    GeoId.IMN to TerritoryName.ISLE_OF_MAN,
    GeoId.IOT to TerritoryName.BRITISH_INDIAN_OCEAN_TERRITORY,
    GeoId.JEY to TerritoryName.JERSEY,
    GeoId.MSR to TerritoryName.MONTSERRAT,
    GeoId.PCN to TerritoryName.PITCAIRN_ISLANDS,
    GeoId.SGS to TerritoryName.SOUTH_GEORGIA_AND_SOUTH_SANDWICH_ISLANDS,
    GeoId.SHN to TerritoryName.SAINT_HELENA,
    GeoId.TCA to TerritoryName.TURKS_AND_CAICOS_ISLANDS,
    GeoId.VGB to TerritoryName.BRITISH_VIRGIN_ISLANDS,
    GeoId.ASM to TerritoryName.AMERICAN_SAMOA,
    GeoId.GUM to TerritoryName.GUAM,
    GeoId.MNP to TerritoryName.NORTHERN_MARIANA_ISLANDS,
    GeoId.PRI to TerritoryName.PUERTO_RICO,
    GeoId.VIR to TerritoryName.US_VIRGIN_ISLANDS,
    GeoId.UMI to TerritoryName.UNITED_STATES_MINOR_OUTLYING_ISLANDS,
    GeoId.GUF to TerritoryName.FRENCH_GUIANA,
    GeoId.MTQ to TerritoryName.MARTINIQUE,
    GeoId.ATF to TerritoryName.FRENCH_SOUTHERN_TERRITORIES,
    GeoId.BLM to TerritoryName.SAINT_BARTHELEMY,
    GeoId.MAF to TerritoryName.SAINT_MARTIN,
    GeoId.NCL to TerritoryName.NEW_CALEDONIA,
    GeoId.PYF to TerritoryName.FRENCH_POLYNESIA,
    GeoId.SPM to TerritoryName.SAINT_PIERRE_AND_MIQUELON,
    GeoId.WLF to TerritoryName.WALLIS_AND_FUTUNA,
    GeoId.ABW to TerritoryName.ARUBA,
    GeoId.CUW to TerritoryName.CURACAO,
    GeoId.SXM to TerritoryName.SINT_MAARTEN,
    GeoId.FRO to TerritoryName.FAROE_ISLANDS,
    GeoId.GRL to TerritoryName.GREENLAND,
    GeoId.CXR to TerritoryName.CHRISTMAS_ISLAND,
    GeoId.CCK to TerritoryName.COCOS_KEELING_ISLANDS,
    GeoId.HMD to TerritoryName.HEARD_ISLAND_AND_MCDONALD_ISLANDS,
    GeoId.NFK to TerritoryName.NORFOLK_ISLAND,
    GeoId.COK to TerritoryName.COOK_ISLANDS,
    GeoId.NIU to TerritoryName.NIUE,
    GeoId.TKL to TerritoryName.TOKELAU,
    GeoId.ALD to TerritoryName.ALAND_ISLANDS,
    GeoId.HKG to TerritoryName.HONG_KONG,
    GeoId.MAC to TerritoryName.MACAU,
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
internal val GEO_ID_TO_TERRITORY_NAME: Map<String, String> = GEO_ID_ENGLISH_FALLBACK

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
    TerritoryName.ANGUILLA to "gb",
    TerritoryName.BERMUDA to "bm",
    TerritoryName.BRITISH_INDIAN_OCEAN_TERRITORY to "gb",
    TerritoryName.BRITISH_VIRGIN_ISLANDS to "gb",
    TerritoryName.CAYMAN_ISLANDS to "ky",
    TerritoryName.FALKLAND_ISLANDS to "fk",
    "Malvinas" to "fk",
    TerritoryName.GIBRALTAR to "gi",
    TerritoryName.GUERNSEY to "gb",
    TerritoryName.ISLE_OF_MAN to "gb",
    TerritoryName.JERSEY to "gb",
    TerritoryName.MONTSERRAT to "gb",
    TerritoryName.PITCAIRN_ISLANDS to "gb",
    "Ascension Island" to "sh",
    "Tristan da Cunha" to "sh",
    "South Georgia" to "gb",
    "South Sandwich Islands" to "gb",
    TerritoryName.TURKS_AND_CAICOS_ISLANDS to "gb",

    // ── US territories & states often searched separately ────────────────────
    TerritoryName.AMERICAN_SAMOA to "us",
    TerritoryName.GUAM to "us",
    TerritoryName.NORTHERN_MARIANA_ISLANDS to "us",
    TerritoryName.PUERTO_RICO to "us",
    TerritoryName.US_VIRGIN_ISLANDS to "us",
    "United States Virgin Islands" to "us",
    "Hawaii" to "us",
    "Alaska" to "us",

    // ── French overseas territories & regions ────────────────────────────────
    TerritoryName.FRENCH_POLYNESIA to "fr",
    "Tahiti" to "fr",
    "Bora Bora" to "fr",
    TerritoryName.FRENCH_SOUTHERN_TERRITORIES to "fr",
    "French Guiana" to "fr",
    "Guadeloupe" to "fr",
    TerritoryName.MARTINIQUE to "mq",
    "Mayotte" to "fr",
    TerritoryName.NEW_CALEDONIA to "fr",
    "Réunion" to "fr",
    TerritoryName.SAINT_BARTHELEMY to "fr",
    TerritoryName.SAINT_MARTIN to "fr",
    TerritoryName.SAINT_PIERRE_AND_MIQUELON to "fr",
    TerritoryName.WALLIS_AND_FUTUNA to "fr",
    "Corsica" to "fr",

    // ── Dutch territories ────────────────────────────────────────────────────
    TerritoryName.ARUBA to "nl",
    "Bonaire" to "nl",
    TerritoryName.CURACAO to "nl",
    TerritoryName.SINT_MAARTEN to "nl",
    "Sint Eustatius" to "nl",
    "Saba" to "nl",

    // ── Danish territories ───────────────────────────────────────────────────
    TerritoryName.FAROE_ISLANDS to "fo",
    TerritoryName.GREENLAND to "dk",

    // ── Australian territories ───────────────────────────────────────────────
    TerritoryName.CHRISTMAS_ISLAND to "au",
    "Cocos Islands" to "au",
    "Keeling Islands" to "au",
    "Heard Island" to "au",
    "McDonald Islands" to "au",
    TerritoryName.NORFOLK_ISLAND to "au",

    // ── New Zealand territories ──────────────────────────────────────────────
    TerritoryName.COOK_ISLANDS to "nz",
    TerritoryName.NIUE to "nz",
    TerritoryName.TOKELAU to "nz",

    // ── Finnish territories ──────────────────────────────────────────────────
    TerritoryName.ALAND_ISLANDS to "fi",

    // ── Chinese territories ──────────────────────────────────────────────────
    TerritoryName.HONG_KONG to "cn",
    TerritoryName.MACAU to "mo",
    "Macao" to "mo",

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
