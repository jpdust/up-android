package com.unstampedpages.app.data.repository

import com.unstampedpages.app.data.model.Continent
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
    fun `countries have valid visa requirements`() {
        val countries = repository.getAllCountries()

        countries.forEach { country ->
            assertNotNull(country.visaRequirement)
            assertTrue(country.visaRequirement.isNotEmpty())
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
}
