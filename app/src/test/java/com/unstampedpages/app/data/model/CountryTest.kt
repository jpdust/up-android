package com.unstampedpages.app.data.model

import org.junit.Assert.*
import org.junit.Test

class CountryTest {

    @Test
    fun `Country data class holds correct values`() {
        val country = Country(
            id = "us",
            name = "United States",
            safetyLevel = SafetyLevel.LOW,
            visaRequirement = VisaRequirement.RESTRICTED,
            currency = "US Dollar",
            currencyCode = "USD",
            exchangeRateToUSD = 1.0,
            outletType = "Type A/B",
            continent = Continent.NORTH_AMERICA,
            flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
        )

        assertEquals("us", country.id)
        assertEquals("United States", country.name)
        assertEquals(SafetyLevel.LOW, country.safetyLevel)
        assertEquals(VisaRequirement.RESTRICTED, country.visaRequirement)
        assertEquals("US Dollar", country.currency)
        assertEquals("USD", country.currencyCode)
        assertEquals(1.0, country.exchangeRateToUSD, 0.001)
        assertEquals("Type A/B", country.outletType)
        assertEquals(Continent.NORTH_AMERICA, country.continent)
        assertNull(country.imageUrl)
        assertNull(country.passportValidity)
    }

    @Test
    fun `Country with imageUrl`() {
        val country = Country(
            id = "fr",
            name = "France",
            safetyLevel = SafetyLevel.LOW,
            visaRequirement = VisaRequirement.VISA_NOT_REQUIRED,
            currency = "Euro",
            currencyCode = "EUR",
            exchangeRateToUSD = 1.1,
            outletType = "Type C/E",
            continent = Continent.EUROPE,
            flagEmoji = "\uD83C\uDDEB\uD83C\uDDF7",
            imageUrl = "https://example.com/france.jpg"
        )

        assertEquals("https://example.com/france.jpg", country.imageUrl)
    }

    @Test
    fun `Country with passportValidity`() {
        val country = Country(
            id = "jp",
            name = "Japan",
            safetyLevel = SafetyLevel.LOW,
            visaRequirement = VisaRequirement.VISA_NOT_REQUIRED,
            currency = "Yen",
            currencyCode = "JPY",
            exchangeRateToUSD = 0.0067,
            outletType = "Type A/B",
            continent = Continent.ASIA,
            flagEmoji = "\uD83C\uDDEF\uD83C\uDDF5",
            passportValidity = "Planned length of stay"
        )

        assertEquals("Planned length of stay", country.passportValidity)
    }

    @Test
    fun `Country copy creates new instance with modified values`() {
        val original = Country(
            id = "jp",
            name = "Japan",
            safetyLevel = SafetyLevel.LOW,
            visaRequirement = VisaRequirement.VISA_NOT_REQUIRED,
            currency = "Yen",
            currencyCode = "JPY",
            exchangeRateToUSD = 0.0067,
            outletType = "Type A/B",
            continent = Continent.ASIA,
            flagEmoji = "\uD83C\uDDEF\uD83C\uDDF5"
        )

        val copy = original.copy(name = "Nippon")

        assertEquals("Japan", original.name)
        assertEquals("Nippon", copy.name)
        assertEquals(original.id, copy.id)
    }

    @Test
    fun `Country equals and hashCode work correctly`() {
        val country1 = Country(
            id = "de",
            name = "Germany",
            safetyLevel = SafetyLevel.LOW,
            visaRequirement = VisaRequirement.VISA_NOT_REQUIRED,
            currency = "Euro",
            currencyCode = "EUR",
            exchangeRateToUSD = 1.1,
            outletType = "Type C/F",
            continent = Continent.EUROPE,
            flagEmoji = "\uD83C\uDDE9\uD83C\uDDEA"
        )

        val country2 = Country(
            id = "de",
            name = "Germany",
            safetyLevel = SafetyLevel.LOW,
            visaRequirement = VisaRequirement.VISA_NOT_REQUIRED,
            currency = "Euro",
            currencyCode = "EUR",
            exchangeRateToUSD = 1.1,
            outletType = "Type C/F",
            continent = Continent.EUROPE,
            flagEmoji = "\uD83C\uDDE9\uD83C\uDDEA"
        )

        assertEquals(country1, country2)
        assertEquals(country1.hashCode(), country2.hashCode())
    }
}

class SafetyLevelTest {

    @Test
    fun `SafetyLevel LOW has correct displayName`() {
        assertEquals("Low Risk", SafetyLevel.LOW.displayName)
    }

    @Test
    fun `SafetyLevel MEDIUM has correct displayName`() {
        assertEquals("Medium Risk", SafetyLevel.MEDIUM.displayName)
    }

    @Test
    fun `SafetyLevel HIGH has correct displayName`() {
        assertEquals("High Risk", SafetyLevel.HIGH.displayName)
    }

    @Test
    fun `SafetyLevel EXTREME has correct displayName`() {
        assertEquals("Extreme Risk", SafetyLevel.EXTREME.displayName)
    }

    @Test
    fun `SafetyLevel values returns all levels`() {
        val values = SafetyLevel.values()
        assertEquals(4, values.size)
        assertTrue(values.contains(SafetyLevel.LOW))
        assertTrue(values.contains(SafetyLevel.MEDIUM))
        assertTrue(values.contains(SafetyLevel.HIGH))
        assertTrue(values.contains(SafetyLevel.EXTREME))
    }

    @Test
    fun `SafetyLevel valueOf returns correct enum`() {
        assertEquals(SafetyLevel.LOW, SafetyLevel.valueOf("LOW"))
        assertEquals(SafetyLevel.MEDIUM, SafetyLevel.valueOf("MEDIUM"))
        assertEquals(SafetyLevel.HIGH, SafetyLevel.valueOf("HIGH"))
        assertEquals(SafetyLevel.EXTREME, SafetyLevel.valueOf("EXTREME"))
    }
}

class VisaRequirementTest {

    @Test
    fun `VisaRequirement NOT_REQUIRED has correct displayName`() {
        assertEquals("Visa not required", VisaRequirement.VISA_NOT_REQUIRED.displayName)
    }

    @Test
    fun `VisaRequirement EVISA has correct displayName`() {
        assertEquals("eVisa", VisaRequirement.EVISA.displayName)
    }

    @Test
    fun `VisaRequirement ON_ARRIVAL has correct displayName`() {
        assertEquals("Visa on arrival", VisaRequirement.VISA_ON_ARRIVAL.displayName)
    }

    @Test
    fun `VisaRequirement REQUIRED has correct displayName`() {
        assertEquals("Visa required", VisaRequirement.VISA_REQUIRED.displayName)
    }

    @Test
    fun `VisaRequirement OTHER has correct displayName`() {
        assertEquals("Restricted", VisaRequirement.RESTRICTED.displayName)
    }

    @Test
    fun `VisaRequirement values returns all requirements`() {
        val values = VisaRequirement.values()
        assertEquals(5, values.size)
        assertTrue(values.contains(VisaRequirement.VISA_NOT_REQUIRED))
        assertTrue(values.contains(VisaRequirement.EVISA))
        assertTrue(values.contains(VisaRequirement.VISA_ON_ARRIVAL))
        assertTrue(values.contains(VisaRequirement.VISA_REQUIRED))
        assertTrue(values.contains(VisaRequirement.RESTRICTED))
    }

    @Test
    fun `VisaRequirement valueOf returns correct enum`() {
        assertEquals(VisaRequirement.VISA_NOT_REQUIRED, VisaRequirement.valueOf("VISA_NOT_REQUIRED"))
        assertEquals(VisaRequirement.EVISA, VisaRequirement.valueOf("EVISA"))
        assertEquals(VisaRequirement.VISA_ON_ARRIVAL, VisaRequirement.valueOf("VISA_ON_ARRIVAL"))
        assertEquals(VisaRequirement.VISA_REQUIRED, VisaRequirement.valueOf("VISA_REQUIRED"))
        assertEquals(VisaRequirement.RESTRICTED, VisaRequirement.valueOf("RESTRICTED"))
    }
}

class ContinentTest {

    @Test
    fun `Continent NORTH_AMERICA has correct displayName`() {
        assertEquals("North America", Continent.NORTH_AMERICA.displayName)
    }

    @Test
    fun `Continent SOUTH_AMERICA has correct displayName`() {
        assertEquals("South America", Continent.SOUTH_AMERICA.displayName)
    }

    @Test
    fun `Continent EUROPE has correct displayName`() {
        assertEquals("Europe", Continent.EUROPE.displayName)
    }

    @Test
    fun `Continent AFRICA has correct displayName`() {
        assertEquals("Africa", Continent.AFRICA.displayName)
    }

    @Test
    fun `Continent ASIA has correct displayName`() {
        assertEquals("Asia", Continent.ASIA.displayName)
    }

    @Test
    fun `Continent OCEANIA has correct displayName`() {
        assertEquals("Oceania", Continent.OCEANIA.displayName)
    }

    @Test
    fun `Continent ANTARCTICA has correct displayName`() {
        assertEquals("Antarctica", Continent.ANTARCTICA.displayName)
    }

    @Test
    fun `Continent values returns all continents`() {
        val values = Continent.values()
        assertEquals(7, values.size)
    }
}

class CountryMapDataTest {

    @Test
    fun `CountryMapData holds correct values`() {
        val mapData = CountryMapData(
            countryId = "us",
            centerX = 100f,
            centerY = 200f,
            radius = 50f
        )

        assertEquals("us", mapData.countryId)
        assertEquals(100f, mapData.centerX, 0.001f)
        assertEquals(200f, mapData.centerY, 0.001f)
        assertEquals(50f, mapData.radius, 0.001f)
    }

    @Test
    fun `CountryMapData copy works correctly`() {
        val original = CountryMapData("fr", 50f, 75f, 25f)
        val copy = original.copy(radius = 30f)

        assertEquals(25f, original.radius, 0.001f)
        assertEquals(30f, copy.radius, 0.001f)
    }
}
