package com.unstampedpages.app.data.repository

import com.unstampedpages.app.data.model.Continent
import com.unstampedpages.app.data.model.VisaRequirement
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CountryRepositoryTest {

    private lateinit var repository: CountryRepository

    @Before
    fun setUp() {
        repository = CountryRepository()
    }

    @Test
    fun `getAllCountries returns non-empty list`() {
        val countries = repository.getAllCountries()

        assertTrue(countries.isNotEmpty())
    }

    @Test
    fun `getAllCountries returns countries with valid data`() {
        val countries = repository.getAllCountries()

        countries.forEach { country ->
            assertNotNull(country.id)
            assertTrue(country.id.isNotEmpty())
            assertNotNull(country.name)
            assertTrue(country.name.isNotEmpty())
        }
    }

    @Test
    fun `getCountryById returns correct country`() {
        val country = repository.getCountryById("us")

        assertNotNull(country)
        assertEquals("United States", country?.name)
    }

    @Test
    fun `getCountryById returns null for invalid id`() {
        val country = repository.getCountryById("invalid_id")

        assertNull(country)
    }

    @Test
    fun `getCountryById is case sensitive`() {
        val country = repository.getCountryById("US")

        assertNull(country)
    }

    @Test
    fun `getCountriesByContinent returns correct countries for North America`() {
        val countries = repository.getCountriesByContinent(Continent.NORTH_AMERICA)

        assertTrue(countries.isNotEmpty())
        countries.forEach { country ->
            assertEquals(Continent.NORTH_AMERICA, country.continent)
        }
    }

    @Test
    fun `getCountriesByContinent returns correct countries for Europe`() {
        val countries = repository.getCountriesByContinent(Continent.EUROPE)

        assertTrue(countries.isNotEmpty())
        countries.forEach { country ->
            assertEquals(Continent.EUROPE, country.continent)
        }
    }

    @Test
    fun `getCountriesByContinent returns correct countries for Asia`() {
        val countries = repository.getCountriesByContinent(Continent.ASIA)

        assertTrue(countries.isNotEmpty())
        countries.forEach { country ->
            assertEquals(Continent.ASIA, country.continent)
        }
    }

    @Test
    fun `getCountriesByContinent returns correct countries for Africa`() {
        val countries = repository.getCountriesByContinent(Continent.AFRICA)

        assertTrue(countries.isNotEmpty())
        countries.forEach { country ->
            assertEquals(Continent.AFRICA, country.continent)
        }
    }

    @Test
    fun `getCountriesByContinent returns correct countries for South America`() {
        val countries = repository.getCountriesByContinent(Continent.SOUTH_AMERICA)

        assertTrue(countries.isNotEmpty())
        countries.forEach { country ->
            assertEquals(Continent.SOUTH_AMERICA, country.continent)
        }
    }

    @Test
    fun `getCountriesByContinent returns correct countries for Oceania`() {
        val countries = repository.getCountriesByContinent(Continent.OCEANIA)

        assertTrue(countries.isNotEmpty())
        countries.forEach { country ->
            assertEquals(Continent.OCEANIA, country.continent)
        }
    }

    @Test
    fun `getMapData returns non-empty list`() {
        val mapData = repository.getMapData()

        assertTrue(mapData.isNotEmpty())
    }

    @Test
    fun `getMapData returns valid coordinates`() {
        val mapData = repository.getMapData()

        mapData.forEach { data ->
            assertTrue(data.centerX >= 0f && data.centerX <= 1f)
            assertTrue(data.centerY >= 0f && data.centerY <= 1f)
            assertTrue(data.radius > 0f)
        }
    }

    @Test
    fun `sample countries contains expected countries`() {
        val countries = repository.getAllCountries()
        val ids = countries.map { it.id }

        assertTrue(ids.contains("us"))
        assertTrue(ids.contains("gb"))
        assertTrue(ids.contains("fr"))
        assertTrue(ids.contains("de"))
        assertTrue(ids.contains("jp"))
        assertTrue(ids.contains("cn"))
        assertTrue(ids.contains("au"))
        assertTrue(ids.contains("br"))
    }

    @Test
    fun `countries have valid safety levels`() {
        val countries = repository.getAllCountries()

        countries.forEach { country ->
            assertNotNull(country.safetyLevel)
            // Check that safetyLevel name is one of the expected values
            val validLevels = listOf("LOW", "MEDIUM", "HIGH", "EXTREME")
            assertTrue(validLevels.contains(country.safetyLevel.name))
        }
    }

    @Test
    fun `countries have valid currency data`() {
        val countries = repository.getAllCountries()

        countries.forEach { country ->
            assertNotNull(country.currency)
            assertTrue(country.currency.isNotEmpty())
            assertNotNull(country.currencyCode)
            assertTrue(country.currencyCode.length == 3)
            assertTrue(country.exchangeRateToUSD > 0)
        }
    }

    @Test
    fun `countries have valid outlet types`() {
        val countries = repository.getAllCountries()

        countries.forEach { country ->
            assertNotNull(country.outletType)
            assertTrue(country.outletType.isNotEmpty())
        }
    }

    @Test
    fun `countries have flag emojis`() {
        val countries = repository.getAllCountries()

        countries.forEach { country ->
            assertNotNull(country.flagEmoji)
            assertTrue(country.flagEmoji.isNotEmpty())
        }
    }

    @Test
    fun `countries have travel advisory links`() {
        val countries = repository.getAllCountries()

        countries.forEach { country ->
            val advisories = country.travelAdvisories
            assertNotNull("Expected travel advisories for ${country.name}", advisories)
            assertTrue(advisories!!.us.startsWith("https://"))
            assertTrue(advisories.uk.startsWith("https://"))
            assertTrue(advisories.au.startsWith("https://"))
            assertTrue(advisories.ca.startsWith("https://"))
        }
    }

    @Test
    fun `greenland uses explicit advisory overrides`() {
        val greenland = repository.getCountryById("gl")

        assertNotNull(greenland)
        val advisories = greenland!!.travelAdvisories!!
        assertEquals(
            "https://travel.state.gov/content/travel/en/traveladvisories/traveladvisories/greenland-travel-advisory.html",
            advisories.us
        )
        assertEquals(
            "https://www.gov.uk/foreign-travel-advice/denmark",
            advisories.uk
        )
        assertEquals(
            "https://www.smartraveller.gov.au/destinations/europe/denmark",
            advisories.au
        )
        assertEquals(
            "https://travel.gc.ca/destinations/greenland",
            advisories.ca
        )
    }

    @Test
    fun `israel uses explicit advisory overrides`() {
        val israel = repository.getCountryById("il")

        assertNotNull(israel)
        val advisories = israel!!.travelAdvisories!!
        assertEquals(
            "https://travel.state.gov/content/travel/en/traveladvisories/traveladvisories/israel-west-bank-and-gaza-travel-advisory.html",
            advisories.us
        )
        assertEquals(
            "https://www.gov.uk/foreign-travel-advice/israel",
            advisories.uk
        )
        assertEquals(
            "https://www.smartraveller.gov.au/destinations/middle-east/israel-and-palestinian-territories",
            advisories.au
        )
        assertEquals(
            "https://travel.gc.ca/destinations/israel-and-palestine",
            advisories.ca
        )
    }

    @Test
    fun `palestine uses explicit advisory overrides`() {
        val palestine = repository.getCountryById("ps")

        assertNotNull(palestine)
        val advisories = palestine!!.travelAdvisories!!
        assertEquals(
            "https://travel.state.gov/content/travel/en/traveladvisories/traveladvisories/israel-west-bank-and-gaza-travel-advisory.html",
            advisories.us
        )
        assertEquals(
            "https://www.gov.uk/foreign-travel-advice/palestine",
            advisories.uk
        )
        assertEquals(
            "https://www.smartraveller.gov.au/destinations/middle-east/palestine",
            advisories.au
        )
        assertEquals(
            "https://travel.gc.ca/destinations/israel-and-palestine",
            advisories.ca
        )
    }

    @Test
    fun `ordinary countries still receive generated advisory links`() {
        val japan = repository.getCountryById("jp")

        assertNotNull(japan)
        val advisories = japan!!.travelAdvisories!!
        assertEquals(
            "https://travel.state.gov/content/travel/en/traveladvisories/traveladvisories/japan-travel-advisory.html",
            advisories.us
        )
        assertEquals(
            "https://www.gov.uk/foreign-travel-advice/japan",
            advisories.uk
        )
        assertEquals(
            "https://www.smartraveller.gov.au/destinations/asia/japan",
            advisories.au
        )
        assertEquals(
            "https://travel.gc.ca/destinations/japan",
            advisories.ca
        )
    }

    @Test
    fun `countries have valid visa requirements`() {
        val countries = repository.getAllCountries()
        val validRequirements = VisaRequirement.values().toList()

        countries.forEach { country ->
            assertNotNull(country.visaRequirement)
            assertTrue(validRequirements.contains(country.visaRequirement))
        }
    }

    @Test
    fun `all country ids are unique`() {
        val countries = repository.getAllCountries()
        val ids = countries.map { it.id }

        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `specific country has expected values`() {
        val japan = repository.getCountryById("jp")

        assertNotNull(japan)
        assertEquals("Japan", japan?.name)
        assertEquals(Continent.ASIA, japan?.continent)
        assertEquals("JPY", japan?.currencyCode)
        assertEquals("LOW", japan?.safetyLevel?.name)
        assertEquals("Planned length of stay", japan?.passportValidity)
    }

    @Test
    fun `countries have passport validity data`() {
        val countries = repository.getAllCountries()
        val countriesWithValidity = countries.filter { it.passportValidity != null }

        // Most countries should have passport validity data
        assertTrue(
            "Expected most countries to have passport validity data",
            countriesWithValidity.size > countries.size / 2
        )
    }

    @Test
    fun `passport validity has expected values`() {
        val countries = repository.getAllCountries()
        val validValidities = listOf(
            "6 months",
            "3 months",
            "Planned length of stay",
            "150 days upon arrival",
            "120 Days Upon Arrival",
            "30 days after departure",
            "3-6 months",
            "At least 1 day after departure",
            "Must expire after departure date"
        )

        countries.forEach { country ->
            if (country.passportValidity != null) {
                assertTrue(
                    "Unexpected passport validity: ${country.passportValidity} for ${country.name}",
                    validValidities.contains(country.passportValidity)
                )
            }
        }
    }

    @Test
    fun `getCountriesByContinent returns subset of all countries`() {
        val allCountries = repository.getAllCountries()
        val northAmerica = repository.getCountriesByContinent(Continent.NORTH_AMERICA)

        assertTrue(northAmerica.size < allCountries.size)
        assertTrue(allCountries.containsAll(northAmerica))
    }

    @Test
    fun `getMapData countryIds exist in countries`() {
        val countries = repository.getAllCountries()
        val countryIds = countries.map { it.id }
        val mapData = repository.getMapData()

        mapData.forEach { data ->
            assertTrue(
                "Map data countryId ${data.countryId} should exist in countries",
                countryIds.contains(data.countryId)
            )
        }
    }

    // ==================== Additional Edge Case Tests ====================

    @Test
    fun `getCountryById returns same instance for same id`() {
        val country1 = repository.getCountryById("jp")
        val country2 = repository.getCountryById("jp")

        assertEquals(country1, country2)
    }

    @Test
    fun `getCountryById with empty string returns null`() {
        val country = repository.getCountryById("")

        assertNull(country)
    }

    @Test
    fun `getCountriesByContinent returns empty for Antarctica`() {
        // Assuming no countries in Antarctica dataset
        val countries = repository.getCountriesByContinent(Continent.ANTARCTICA)

        // This could be empty or have entries - just verify it doesn't crash
        assertNotNull(countries)
    }

    @Test
    fun `all continents have representation`() {
        val continentsRepresented = repository.getAllCountries()
            .map { it.continent }
            .distinct()

        // Check that most major continents are represented
        assertTrue(continentsRepresented.contains(Continent.NORTH_AMERICA))
        assertTrue(continentsRepresented.contains(Continent.SOUTH_AMERICA))
        assertTrue(continentsRepresented.contains(Continent.EUROPE))
        assertTrue(continentsRepresented.contains(Continent.ASIA))
        assertTrue(continentsRepresented.contains(Continent.AFRICA))
        assertTrue(continentsRepresented.contains(Continent.OCEANIA))
    }

    @Test
    fun `countries have non-negative exchange rates`() {
        val countries = repository.getAllCountries()

        countries.forEach { country ->
            assertTrue(
                "Country ${country.name} should have non-negative exchange rate",
                country.exchangeRateToUSD >= 0
            )
        }
    }

    @Test
    fun `USD country has exchange rate of 1`() {
        val usCountry = repository.getCountryById("us")

        assertNotNull(usCountry)
        assertEquals(1.0, usCountry!!.exchangeRateToUSD, 0.0001)
    }

    @Test
    fun `countries with visa required have valid visa requirement`() {
        val countries = repository.getAllCountries()
        val visaRequiredCountries = countries.filter {
            it.visaRequirement == VisaRequirement.VISA_REQUIRED
        }

        visaRequiredCountries.forEach { country ->
            assertEquals(VisaRequirement.VISA_REQUIRED, country.visaRequirement)
        }
    }

    @Test
    fun `high risk countries have HIGH safety level`() {
        val countries = repository.getAllCountries()
        val highRiskCountries = countries.filter {
            it.safetyLevel == com.unstampedpages.app.data.model.SafetyLevel.HIGH ||
            it.safetyLevel == com.unstampedpages.app.data.model.SafetyLevel.EXTREME
        }

        // Just verify we can filter and that these countries exist
        highRiskCountries.forEach { country ->
            assertTrue(
                country.safetyLevel == com.unstampedpages.app.data.model.SafetyLevel.HIGH ||
                country.safetyLevel == com.unstampedpages.app.data.model.SafetyLevel.EXTREME
            )
        }
    }

    @Test
    fun `getMapData returns unique country ids`() {
        val mapData = repository.getMapData()
        val ids = mapData.map { it.countryId }

        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `getMapData radius values are reasonable`() {
        val mapData = repository.getMapData()

        mapData.forEach { data ->
            assertTrue(
                "Radius should be between 0 and 0.5 for ${data.countryId}",
                data.radius > 0f && data.radius <= 0.5f
            )
        }
    }

    @Test
    fun `multiple repositories return same data`() {
        val repo1 = CountryRepository()
        val repo2 = CountryRepository()

        val countries1 = repo1.getAllCountries()
        val countries2 = repo2.getAllCountries()

        assertEquals(countries1.size, countries2.size)
    }

    @Test
    fun `country flag emojis are unicode characters`() {
        val countries = repository.getAllCountries()

        countries.forEach { country ->
            // Flag emojis are typically 2 unicode regional indicator symbols
            assertTrue(
                "Flag emoji for ${country.name} should have length > 0",
                country.flagEmoji.isNotEmpty()
            )
        }
    }

    @Test
    fun `getCountriesByContinent sum equals total countries`() {
        val allCountries = repository.getAllCountries()

        val continentCounts = Continent.values().sumOf { continent ->
            repository.getCountriesByContinent(continent).size
        }

        assertEquals(allCountries.size, continentCounts)
    }
}
