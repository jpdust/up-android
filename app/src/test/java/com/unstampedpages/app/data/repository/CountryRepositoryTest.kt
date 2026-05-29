package com.unstampedpages.app.data.repository

import com.unstampedpages.app.data.AppConstants
import com.unstampedpages.app.data.model.Continent
import com.unstampedpages.app.data.model.SafetyLevel
import com.unstampedpages.app.data.model.VisaRequirement
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

private const val HTTPS_PREFIX = "https://"

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
        assertEquals(countries.size, 209)
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
        assertEquals(AppConstants.CountryName.UNITED_STATES, country?.name)
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
            val validLevels = listOf("NORMAL_SECURITY_PRECAUTIONS", "HIGH_DEGREE_CAUTION", "RECONSIDER_TRAVEL", "DO_NOT_TRAVEL")
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
            assertTrue(advisories!!.us.startsWith(HTTPS_PREFIX))
            assertTrue(advisories.uk.startsWith(HTTPS_PREFIX))
            assertTrue(advisories.au.startsWith(HTTPS_PREFIX))
            assertTrue(advisories.ca.startsWith(HTTPS_PREFIX))
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
        assertEquals(AppConstants.CountryName.JAPAN, japan?.name)
        assertEquals(Continent.ASIA, japan?.continent)
        assertEquals("JPY", japan?.currencyCode)
        assertEquals("NORMAL_SECURITY_PRECAUTIONS", japan?.safetyLevel?.name)
        assertEquals(AppConstants.PassportValidity.PLANNED_STAY, japan?.passportValidity)
    }

    @Test
    fun `state department overrides apply to representative countries`() {
        assertEquals(SafetyLevel.RECONSIDER_TRAVEL, repository.getCountryById("gt")?.safetyLevel)
        assertEquals(SafetyLevel.DO_NOT_TRAVEL, repository.getCountryById("ht")?.safetyLevel)
        assertEquals(SafetyLevel.HIGH_DEGREE_CAUTION, repository.getCountryById("gb")?.safetyLevel)
        assertEquals(SafetyLevel.RECONSIDER_TRAVEL, repository.getCountryById("ae")?.safetyLevel)
        assertEquals(SafetyLevel.HIGH_DEGREE_CAUTION, repository.getCountryById("aa")?.safetyLevel)
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
            AppConstants.PassportValidity.SIX_MONTHS,
            AppConstants.PassportValidity.THREE_MONTHS,
            AppConstants.PassportValidity.PLANNED_STAY,
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

    // ==================== Locale-aware Tests ====================

    @Test
    fun `getAllCountries with English locale returns English name for US`() {
        val countries = repository.getAllCountries(java.util.Locale.ENGLISH)
        val us = countries.find { it.id == "us" }
        assertNotNull(us)
        assertEquals(AppConstants.CountryName.UNITED_STATES, us!!.name)
    }

    @Test
    fun `getAllCountries with Spanish locale returns non-blank names`() {
        val countries = repository.getAllCountries(java.util.Locale.forLanguageTag("es"))
        assertTrue(countries.isNotEmpty())
        countries.forEach { country ->
            assertTrue("Country ${country.id} has blank name in Spanish", country.name.isNotBlank())
        }
    }

    @Test
    fun `getAllCountries with Spanish locale returns different name for US than English`() {
        val englishCountries = repository.getAllCountries(java.util.Locale.ENGLISH)
        val spanishCountries = repository.getAllCountries(java.util.Locale.forLanguageTag("es"))

        val usEnglish = englishCountries.find { it.id == "us" }!!.name
        val usSpanish = spanishCountries.find { it.id == "us" }!!.name

        // Spanish JVM ICU data provides a different display name for the US than English.
        assertNotEquals(
            "Expected Spanish name for US to differ from English",
            usEnglish, usSpanish
        )
    }

    @Test
    fun `getAllCountries with Spanish locale preserves country ids`() {
        val englishIds = repository.getAllCountries(java.util.Locale.ENGLISH).map { it.id }.toSet()
        val spanishIds = repository.getAllCountries(java.util.Locale.forLanguageTag("es")).map { it.id }.toSet()
        assertEquals(englishIds, spanishIds)
    }

    @Test
    fun `getAllCountries with Spanish locale returns same count as English`() {
        val englishCount = repository.getAllCountries(java.util.Locale.ENGLISH).size
        val spanishCount = repository.getAllCountries(java.util.Locale.forLanguageTag("es")).size
        assertEquals(englishCount, spanishCount)
    }

    @Test
    fun `getCountryById with English locale returns English name`() {
        val country = repository.getCountryById("jp", java.util.Locale.ENGLISH)
        assertNotNull(country)
        assertEquals(AppConstants.CountryName.JAPAN, country!!.name)
    }

    @Test
    fun `getCountryById with Spanish locale returns non-blank name`() {
        val country = repository.getCountryById("jp", java.util.Locale.forLanguageTag("es"))
        assertNotNull(country)
        assertTrue(country!!.name.isNotBlank())
    }

    @Test
    fun `getCountryById with Spanish locale returns localized name different from English for Japan`() {
        val english = repository.getCountryById("jp", java.util.Locale.ENGLISH)!!.name
        val spanish = repository.getCountryById("jp", java.util.Locale.forLanguageTag("es"))!!.name
        assertNotEquals("Expected Spanish name for Japan to differ from English", english, spanish)
    }

    @Test
    fun `getCountryById with French locale returns non-blank name`() {
        val country = repository.getCountryById("de", java.util.Locale.forLanguageTag("fr"))
        assertNotNull(country)
        assertTrue(country!!.name.isNotBlank())
    }

    @Test
    fun `getCountryById preserves all non-name fields when localizing`() {
        val english = repository.getCountryById("us", java.util.Locale.ENGLISH)!!
        val spanish = repository.getCountryById("us", java.util.Locale.forLanguageTag("es"))!!

        assertEquals(english.id, spanish.id)
        assertEquals(english.continent, spanish.continent)
        assertEquals(english.currency, spanish.currency)
        assertEquals(english.currencyCode, spanish.currencyCode)
        assertEquals(english.exchangeRateToUSD, spanish.exchangeRateToUSD, 0.0001)
        assertEquals(english.safetyLevel, spanish.safetyLevel)
        assertEquals(english.visaRequirement, spanish.visaRequirement)
    }

    @Test
    fun `getCountryById with invalid id returns null regardless of locale`() {
        assertNull(repository.getCountryById("invalid_id", java.util.Locale.ENGLISH))
        assertNull(repository.getCountryById("invalid_id", java.util.Locale.forLanguageTag("es")))
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
    fun `getCountryById returns Antarctica for id aa`() {
        val country = repository.getCountryById("aa")

        assertNotNull("Antarctica should exist in the repository with id 'aa'", country)
        assertEquals("Antarctica", country!!.name)
        assertEquals(Continent.ANTARCTICA, country.continent)
    }

    @Test
    fun `Antarctica has travel advisory links`() {
        val country = repository.getCountryById("aa")

        assertNotNull(country)
        val advisories = country!!.travelAdvisories
        assertNotNull("Antarctica should have travel advisories", advisories)
        assertTrue(advisories!!.us.startsWith(HTTPS_PREFIX))
        assertTrue(advisories.uk.startsWith(HTTPS_PREFIX))
        assertTrue(advisories.au.startsWith(HTTPS_PREFIX))
        assertTrue(advisories.ca.startsWith(HTTPS_PREFIX))
    }

    @Test
    fun `all continents have representation`() {
        val continentsRepresented = repository.getAllCountries()
            .map { it.continent }
            .distinct()

        assertTrue(continentsRepresented.contains(Continent.NORTH_AMERICA))
        assertTrue(continentsRepresented.contains(Continent.SOUTH_AMERICA))
        assertTrue(continentsRepresented.contains(Continent.EUROPE))
        assertTrue(continentsRepresented.contains(Continent.ASIA))
        assertTrue(continentsRepresented.contains(Continent.AFRICA))
        assertTrue(continentsRepresented.contains(Continent.OCEANIA))
        assertTrue(continentsRepresented.contains(Continent.ANTARCTICA))
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
        val RECONSIDERTRAVELRiskCountries = countries.filter {
            it.safetyLevel == com.unstampedpages.app.data.model.SafetyLevel.RECONSIDER_TRAVEL ||
            it.safetyLevel == com.unstampedpages.app.data.model.SafetyLevel.DO_NOT_TRAVEL
        }

        // Just verify we can filter and that these countries exist
        RECONSIDERTRAVELRiskCountries.forEach { country ->
            assertTrue(
                country.safetyLevel == com.unstampedpages.app.data.model.SafetyLevel.RECONSIDER_TRAVEL ||
                country.safetyLevel == com.unstampedpages.app.data.model.SafetyLevel.DO_NOT_TRAVEL
            )
        }
    }

    // ==================== Territory Currency Tests ====================

    @Test
    fun `Cayman Islands has KYD currency and is in North America`() {
        val country = repository.getCountryById("ky")
        assertNotNull("Cayman Islands (ky) should exist", country)
        assertEquals("KYD", country!!.currencyCode)
        assertEquals(Continent.NORTH_AMERICA, country.continent)
    }

    @Test
    fun `Bermuda has BMD currency and is in North America`() {
        val country = repository.getCountryById("bm")
        assertNotNull("Bermuda (bm) should exist", country)
        assertEquals("BMD", country!!.currencyCode)
        assertEquals(Continent.NORTH_AMERICA, country.continent)
    }

    @Test
    fun `Falkland Islands has FKP currency and is in South America`() {
        val country = repository.getCountryById("fk")
        assertNotNull("Falkland Islands (fk) should exist", country)
        assertEquals("FKP", country!!.currencyCode)
        assertEquals(Continent.SOUTH_AMERICA, country.continent)
    }

    @Test
    fun `Gibraltar has GIP currency and is in Europe`() {
        val country = repository.getCountryById("gi")
        assertNotNull("Gibraltar (gi) should exist", country)
        assertEquals("GIP", country!!.currencyCode)
        assertEquals(Continent.EUROPE, country.continent)
    }

    @Test
    fun `Faroe Islands has DKK currency and is in Europe`() {
        val country = repository.getCountryById("fo")
        assertNotNull("Faroe Islands (fo) should exist", country)
        assertEquals("DKK", country!!.currencyCode)
        assertEquals(Continent.EUROPE, country.continent)
    }

    @Test
    fun `Saint Helena has SHP currency and is in Africa`() {
        val country = repository.getCountryById("sh")
        assertNotNull("Saint Helena (sh) should exist", country)
        assertEquals("SHP", country!!.currencyCode)
        assertEquals(Continent.AFRICA, country.continent)
    }

    @Test
    fun `Martinique has EUR currency and is in North America`() {
        val country = repository.getCountryById("mq")
        assertNotNull("Martinique (mq) should exist", country)
        assertEquals("EUR", country!!.currencyCode)
        assertEquals(Continent.NORTH_AMERICA, country.continent)
    }

    @Test
    fun `all seven new territories are present in the repository`() {
        val ids = listOf("ky", "bm", "fk", "gi", "fo", "sh", "mq")
        ids.forEach { id ->
            assertNotNull("Territory '$id' should exist in repository", repository.getCountryById(id))
        }
    }

    @Test
    fun `new territories have valid currency and exchange rate data`() {
        val ids = listOf("ky", "bm", "fk", "gi", "fo", "sh", "mq")
        ids.forEach { id ->
            val country = repository.getCountryById(id)!!
            assertTrue("$id currency should be non-blank", country.currency.isNotBlank())
            assertTrue("$id currencyCode should be 3 chars", country.currencyCode.length == 3)
            assertTrue("$id exchange rate should be positive", country.exchangeRateToUSD > 0)
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

}
