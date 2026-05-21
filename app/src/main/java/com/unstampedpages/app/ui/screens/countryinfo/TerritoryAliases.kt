package com.unstampedpages.app.ui.screens.countryinfo

/**
 * Maps GeoJSON 3-letter feature IDs for standalone territory features to their
 * human-readable display name. Used so that tapping a territory polygon on the map
 * shows the territory name rather than the parent sovereign country name.
 *
 * Only standalone GeoJSON features are listed here (separate polygons with their own
 * ISO 3166-1 alpha-3 code). Embedded territories such as Easter Island (within Chile's
 * GeoJSON) are not separately identifiable on the map and are therefore excluded.
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
