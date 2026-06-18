package com.unstampedpages.app.data

/**
 * Shared string constants used across the application and tests.
 *
 * Centralises every string literal that is referenced in more than one place so that
 * a single edit is sufficient when a value changes (e.g. a display name is renamed or
 * a passport-validity string is updated in the data layer).
 *
 * Organisation:
 *  - [CountryName]          – human-readable country names
 *  - [CountryCode]          – lowercase ISO-like IDs used in [CountryRepository]
 *  - [PassportValidity]     – data-layer strings stored on the [Country] model
 *  - [SafetyLevelDisplay]   – UI labels for [SafetyLevel] values (match R.string.safety_*)
 *  - [VisaRequirementDisplay] – UI labels for [VisaRequirement] values as shown in the legend
 *  - [ContinentDisplay]     – UI labels for [Continent] values (match R.string.continent_*)
 */
object AppConstants {

    /** Human-readable country names used in repository data and tests. */
    object CountryName {
        const val CANADA = "Canada"
        const val FRANCE = "France"
        const val GERMANY = "Germany"
        const val JAPAN = "Japan"
        const val SUDAN = "Sudan"
        const val UNITED_ARAB_EMIRATES = "United Arab Emirates"
        const val UNITED_KINGDOM = "United Kingdom"
        const val UNITED_STATES = "United States"
    }

    /**
     * Lowercase country IDs used in [com.unstampedpages.app.data.repository.CountryRepository].
     * These are passed to [CountryInfoViewModel.selectCountry] and to robot helper methods
     * such as `selectSearchResult(countryId)`.
     */
    object CountryCode {
        const val CANADA = "ca"
        const val FRANCE = "fr"
        const val GERMANY = "de"
        const val JAPAN = "jp"
        const val SUDAN = "sd"
        const val UNITED_STATES = "us"
    }

    /**
     * Passport validity strings stored in the [com.unstampedpages.app.data.model.Country] model.
     * Used in [com.unstampedpages.app.data.repository.CountryRepository] to populate data and in
     * [com.unstampedpages.app.ui.screens.countryinfo.WorldMapCanvas] to map values to colours.
     * Must match the values in `res/values/strings.xml` (passport_validity_*).
     */
    object PassportValidity {
        const val SIX_MONTHS = "6 months"
        const val THREE_MONTHS = "3 months"
        const val PLANNED_STAY = "Planned length of stay"
    }

    /**
     * UI display strings for [com.unstampedpages.app.data.model.SafetyLevel] enum values.
     * Match the English values of the R.string.safety_* string resources.
     */
    object SafetyLevelDisplay {
        const val NORMAL_SECURITY_PRECAUTIONS = "Normal Security Precautions"
        const val HIGH_DEGREE_CAUTION = "High Degree of Caution"
        const val RECONSIDER_TRAVEL = "Reconsider Travel"
        const val DO_NOT_TRAVEL = "Do Not Travel"
    }

    /**
     * UI display strings for [com.unstampedpages.app.data.model.VisaRequirement] enum values
     * as shown in the map legend.
     * Match the English values of the R.string.legend_visa_* string resources.
     */
    object VisaRequirementDisplay {
        const val VISA_NOT_REQUIRED = "Visa Not Required"
        const val E_VISA = "eVisa"
        const val VISA_ON_ARRIVAL = "Visa On Arrival"
        const val VISA_REQUIRED = "Visa Required"
        const val RESTRICTED = "Restricted"
    }

    /**
     * UI display strings for [com.unstampedpages.app.data.model.Continent] enum values.
     * Match the English values of the R.string.continent_* string resources.
     */
    object ContinentDisplay {
        const val NORTH_AMERICA = "North America"
        const val SOUTH_AMERICA = "South America"
        const val EUROPE = "Europe"
        const val AFRICA = "Africa"
        const val ASIA = "Asia"
        const val OCEANIA = "Oceania"
        const val ANTARCTICA = "Antarctica"
    }
}
