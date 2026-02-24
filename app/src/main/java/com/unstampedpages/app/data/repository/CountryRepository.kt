package com.unstampedpages.app.data.repository

import com.unstampedpages.app.data.model.Continent
import com.unstampedpages.app.data.model.Country
import com.unstampedpages.app.data.model.CountryMapData
import com.unstampedpages.app.data.model.SafetyLevel

class CountryRepository {

    fun getAllCountries(): List<Country> = sampleCountries

    fun getCountryById(id: String): Country? = sampleCountries.find { it.id == id }

    fun getCountriesByContinent(continent: Continent): List<Country> =
        sampleCountries.filter { it.continent == continent }

    fun getMapData(): List<CountryMapData> = countryMapPositions

    companion object {
        // Simplified map positions (normalized 0-1 coordinates)
        // In production, these would be actual country polygon data
        val countryMapPositions = listOf(
            // North America
            CountryMapData("us", 0.18f, 0.35f, 0.08f),
            CountryMapData("ca", 0.18f, 0.22f, 0.09f),
            CountryMapData("mx", 0.15f, 0.45f, 0.04f),

            // South America
            CountryMapData("br", 0.30f, 0.62f, 0.07f),
            CountryMapData("ar", 0.27f, 0.78f, 0.04f),
            CountryMapData("co", 0.24f, 0.52f, 0.03f),
            CountryMapData("pe", 0.22f, 0.58f, 0.03f),

            // Europe
            CountryMapData("gb", 0.46f, 0.28f, 0.02f),
            CountryMapData("fr", 0.47f, 0.33f, 0.025f),
            CountryMapData("de", 0.50f, 0.30f, 0.02f),
            CountryMapData("it", 0.51f, 0.36f, 0.02f),
            CountryMapData("es", 0.45f, 0.37f, 0.025f),

            // Africa
            CountryMapData("eg", 0.55f, 0.42f, 0.03f),
            CountryMapData("za", 0.54f, 0.72f, 0.03f),
            CountryMapData("ng", 0.50f, 0.52f, 0.03f),
            CountryMapData("ke", 0.58f, 0.55f, 0.025f),
            CountryMapData("ma", 0.45f, 0.40f, 0.025f),

            // Asia
            CountryMapData("cn", 0.75f, 0.38f, 0.07f),
            CountryMapData("jp", 0.85f, 0.38f, 0.025f),
            CountryMapData("in", 0.68f, 0.45f, 0.05f),
            CountryMapData("th", 0.73f, 0.50f, 0.025f),
            CountryMapData("vn", 0.76f, 0.48f, 0.02f),
            CountryMapData("kr", 0.82f, 0.36f, 0.015f),
            CountryMapData("id", 0.78f, 0.58f, 0.04f),

            // Oceania
            CountryMapData("au", 0.82f, 0.70f, 0.06f),
            CountryMapData("nz", 0.92f, 0.78f, 0.02f)
        )

        val sampleCountries = listOf(
            // North America
            Country(
                id = "us",
                name = "United States",
                population = 331_900_000L,
                safetyLevel = SafetyLevel.LOW,
                currency = "US Dollar",
                currencyCode = "USD",
                exchangeRateToUSD = 1.0,
                outletType = "Type A/B (120V)",
                continent = Continent.NORTH_AMERICA,
                flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
            ),
            Country(
                id = "ca",
                name = "Canada",
                population = 38_250_000L,
                safetyLevel = SafetyLevel.LOW,
                currency = "Canadian Dollar",
                currencyCode = "CAD",
                exchangeRateToUSD = 0.74,
                outletType = "Type A/B (120V)",
                continent = Continent.NORTH_AMERICA,
                flagEmoji = "\uD83C\uDDE8\uD83C\uDDE6"
            ),
            Country(
                id = "mx",
                name = "Mexico",
                population = 128_900_000L,
                safetyLevel = SafetyLevel.MEDIUM,
                currency = "Mexican Peso",
                currencyCode = "MXN",
                exchangeRateToUSD = 0.058,
                outletType = "Type A/B (127V)",
                continent = Continent.NORTH_AMERICA,
                flagEmoji = "\uD83C\uDDF2\uD83C\uDDFD"
            ),

            // South America
            Country(
                id = "br",
                name = "Brazil",
                population = 214_000_000L,
                safetyLevel = SafetyLevel.MEDIUM,
                currency = "Brazilian Real",
                currencyCode = "BRL",
                exchangeRateToUSD = 0.20,
                outletType = "Type C/N (127/220V)",
                continent = Continent.SOUTH_AMERICA,
                flagEmoji = "\uD83C\uDDE7\uD83C\uDDF7"
            ),
            Country(
                id = "ar",
                name = "Argentina",
                population = 45_810_000L,
                safetyLevel = SafetyLevel.LOW,
                currency = "Argentine Peso",
                currencyCode = "ARS",
                exchangeRateToUSD = 0.0012,
                outletType = "Type C/I (220V)",
                continent = Continent.SOUTH_AMERICA,
                flagEmoji = "\uD83C\uDDE6\uD83C\uDDF7"
            ),
            Country(
                id = "co",
                name = "Colombia",
                population = 51_870_000L,
                safetyLevel = SafetyLevel.MEDIUM,
                currency = "Colombian Peso",
                currencyCode = "COP",
                exchangeRateToUSD = 0.00025,
                outletType = "Type A/B (110V)",
                continent = Continent.SOUTH_AMERICA,
                flagEmoji = "\uD83C\uDDE8\uD83C\uDDF4"
            ),
            Country(
                id = "pe",
                name = "Peru",
                population = 33_360_000L,
                safetyLevel = SafetyLevel.MEDIUM,
                currency = "Peruvian Sol",
                currencyCode = "PEN",
                exchangeRateToUSD = 0.27,
                outletType = "Type A/B/C (220V)",
                continent = Continent.SOUTH_AMERICA,
                flagEmoji = "\uD83C\uDDF5\uD83C\uDDEA"
            ),

            // Europe
            Country(
                id = "gb",
                name = "United Kingdom",
                population = 67_220_000L,
                safetyLevel = SafetyLevel.LOW,
                currency = "British Pound",
                currencyCode = "GBP",
                exchangeRateToUSD = 1.27,
                outletType = "Type G (230V)",
                continent = Continent.EUROPE,
                flagEmoji = "\uD83C\uDDEC\uD83C\uDDE7"
            ),
            Country(
                id = "fr",
                name = "France",
                population = 67_390_000L,
                safetyLevel = SafetyLevel.LOW,
                currency = "Euro",
                currencyCode = "EUR",
                exchangeRateToUSD = 1.08,
                outletType = "Type C/E (230V)",
                continent = Continent.EUROPE,
                flagEmoji = "\uD83C\uDDEB\uD83C\uDDF7"
            ),
            Country(
                id = "de",
                name = "Germany",
                population = 83_240_000L,
                safetyLevel = SafetyLevel.LOW,
                currency = "Euro",
                currencyCode = "EUR",
                exchangeRateToUSD = 1.08,
                outletType = "Type C/F (230V)",
                continent = Continent.EUROPE,
                flagEmoji = "\uD83C\uDDE9\uD83C\uDDEA"
            ),
            Country(
                id = "it",
                name = "Italy",
                population = 59_550_000L,
                safetyLevel = SafetyLevel.LOW,
                currency = "Euro",
                currencyCode = "EUR",
                exchangeRateToUSD = 1.08,
                outletType = "Type C/F/L (230V)",
                continent = Continent.EUROPE,
                flagEmoji = "\uD83C\uDDEE\uD83C\uDDF9"
            ),
            Country(
                id = "es",
                name = "Spain",
                population = 47_420_000L,
                safetyLevel = SafetyLevel.LOW,
                currency = "Euro",
                currencyCode = "EUR",
                exchangeRateToUSD = 1.08,
                outletType = "Type C/F (230V)",
                continent = Continent.EUROPE,
                flagEmoji = "\uD83C\uDDEA\uD83C\uDDF8"
            ),

            // Africa
            Country(
                id = "eg",
                name = "Egypt",
                population = 104_260_000L,
                safetyLevel = SafetyLevel.MEDIUM,
                currency = "Egyptian Pound",
                currencyCode = "EGP",
                exchangeRateToUSD = 0.032,
                outletType = "Type C/F (220V)",
                continent = Continent.AFRICA,
                flagEmoji = "\uD83C\uDDEA\uD83C\uDDEC"
            ),
            Country(
                id = "za",
                name = "South Africa",
                population = 60_040_000L,
                safetyLevel = SafetyLevel.MEDIUM,
                currency = "South African Rand",
                currencyCode = "ZAR",
                exchangeRateToUSD = 0.053,
                outletType = "Type C/M/N (230V)",
                continent = Continent.AFRICA,
                flagEmoji = "\uD83C\uDDFF\uD83C\uDDE6"
            ),
            Country(
                id = "ng",
                name = "Nigeria",
                population = 218_540_000L,
                safetyLevel = SafetyLevel.HIGH,
                currency = "Nigerian Naira",
                currencyCode = "NGN",
                exchangeRateToUSD = 0.00065,
                outletType = "Type D/G (240V)",
                continent = Continent.AFRICA,
                flagEmoji = "\uD83C\uDDF3\uD83C\uDDEC"
            ),
            Country(
                id = "ke",
                name = "Kenya",
                population = 54_030_000L,
                safetyLevel = SafetyLevel.MEDIUM,
                currency = "Kenyan Shilling",
                currencyCode = "KES",
                exchangeRateToUSD = 0.0064,
                outletType = "Type G (240V)",
                continent = Continent.AFRICA,
                flagEmoji = "\uD83C\uDDF0\uD83C\uDDEA"
            ),
            Country(
                id = "ma",
                name = "Morocco",
                population = 37_340_000L,
                safetyLevel = SafetyLevel.LOW,
                currency = "Moroccan Dirham",
                currencyCode = "MAD",
                exchangeRateToUSD = 0.10,
                outletType = "Type C/E (220V)",
                continent = Continent.AFRICA,
                flagEmoji = "\uD83C\uDDF2\uD83C\uDDE6"
            ),

            // Asia
            Country(
                id = "cn",
                name = "China",
                population = 1_412_000_000L,
                safetyLevel = SafetyLevel.LOW,
                currency = "Chinese Yuan",
                currencyCode = "CNY",
                exchangeRateToUSD = 0.14,
                outletType = "Type A/C/I (220V)",
                continent = Continent.ASIA,
                flagEmoji = "\uD83C\uDDE8\uD83C\uDDF3"
            ),
            Country(
                id = "jp",
                name = "Japan",
                population = 125_700_000L,
                safetyLevel = SafetyLevel.LOW,
                currency = "Japanese Yen",
                currencyCode = "JPY",
                exchangeRateToUSD = 0.0067,
                outletType = "Type A/B (100V)",
                continent = Continent.ASIA,
                flagEmoji = "\uD83C\uDDEF\uD83C\uDDF5"
            ),
            Country(
                id = "in",
                name = "India",
                population = 1_417_000_000L,
                safetyLevel = SafetyLevel.MEDIUM,
                currency = "Indian Rupee",
                currencyCode = "INR",
                exchangeRateToUSD = 0.012,
                outletType = "Type C/D/M (230V)",
                continent = Continent.ASIA,
                flagEmoji = "\uD83C\uDDEE\uD83C\uDDF3"
            ),
            Country(
                id = "th",
                name = "Thailand",
                population = 71_600_000L,
                safetyLevel = SafetyLevel.LOW,
                currency = "Thai Baht",
                currencyCode = "THB",
                exchangeRateToUSD = 0.028,
                outletType = "Type A/B/C (220V)",
                continent = Continent.ASIA,
                flagEmoji = "\uD83C\uDDF9\uD83C\uDDED"
            ),
            Country(
                id = "vn",
                name = "Vietnam",
                population = 98_170_000L,
                safetyLevel = SafetyLevel.LOW,
                currency = "Vietnamese Dong",
                currencyCode = "VND",
                exchangeRateToUSD = 0.000041,
                outletType = "Type A/C/G (220V)",
                continent = Continent.ASIA,
                flagEmoji = "\uD83C\uDDFB\uD83C\uDDF3"
            ),
            Country(
                id = "kr",
                name = "South Korea",
                population = 51_740_000L,
                safetyLevel = SafetyLevel.LOW,
                currency = "South Korean Won",
                currencyCode = "KRW",
                exchangeRateToUSD = 0.00075,
                outletType = "Type C/F (220V)",
                continent = Continent.ASIA,
                flagEmoji = "\uD83C\uDDF0\uD83C\uDDF7"
            ),
            Country(
                id = "id",
                name = "Indonesia",
                population = 275_500_000L,
                safetyLevel = SafetyLevel.MEDIUM,
                currency = "Indonesian Rupiah",
                currencyCode = "IDR",
                exchangeRateToUSD = 0.000063,
                outletType = "Type C/F (230V)",
                continent = Continent.ASIA,
                flagEmoji = "\uD83C\uDDEE\uD83C\uDDE9"
            ),

            // Oceania
            Country(
                id = "au",
                name = "Australia",
                population = 26_000_000L,
                safetyLevel = SafetyLevel.LOW,
                currency = "Australian Dollar",
                currencyCode = "AUD",
                exchangeRateToUSD = 0.65,
                outletType = "Type I (230V)",
                continent = Continent.OCEANIA,
                flagEmoji = "\uD83C\uDDE6\uD83C\uDDFA"
            ),
            Country(
                id = "nz",
                name = "New Zealand",
                population = 5_120_000L,
                safetyLevel = SafetyLevel.LOW,
                currency = "New Zealand Dollar",
                currencyCode = "NZD",
                exchangeRateToUSD = 0.61,
                outletType = "Type I (230V)",
                continent = Continent.OCEANIA,
                flagEmoji = "\uD83C\uDDF3\uD83C\uDDFF"
            )
        )
    }
}
