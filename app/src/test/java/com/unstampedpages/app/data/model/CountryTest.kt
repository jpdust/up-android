package com.unstampedpages.app.data.model

import androidx.compose.ui.graphics.Color
import com.unstampedpages.app.R
import com.unstampedpages.app.data.AppConstants
import org.junit.Assert.*
import org.junit.Test

class CountryTest {

    @Test
    fun `Country data class holds correct values`() {
        val country = Country(
            id = AppConstants.CountryCode.UNITED_STATES,
            name = AppConstants.CountryName.UNITED_STATES,
            safetyLevel = SafetyLevel.NORMAL_SECURITY_PRECAUTIONS,
            visaRequirement = VisaRequirement.RESTRICTED,
            currency = "US Dollar",
            currencyCode = "USD",
            exchangeRateToUSD = 1.0,
            outletType = "Type A/B",
            continent = Continent.NORTH_AMERICA,
            flagEmoji = "\uD83C\uDDFA\uD83C\uDDF8"
        )

        assertEquals("us", country.id)
        assertEquals(AppConstants.CountryName.UNITED_STATES, country.name)
        assertEquals(SafetyLevel.NORMAL_SECURITY_PRECAUTIONS, country.safetyLevel)
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
            id = AppConstants.CountryCode.FRANCE,
            name = AppConstants.CountryName.FRANCE,
            safetyLevel = SafetyLevel.NORMAL_SECURITY_PRECAUTIONS,
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
            id = AppConstants.CountryCode.JAPAN,
            name = AppConstants.CountryName.JAPAN,
            safetyLevel = SafetyLevel.NORMAL_SECURITY_PRECAUTIONS,
            visaRequirement = VisaRequirement.VISA_NOT_REQUIRED,
            currency = "Yen",
            currencyCode = "JPY",
            exchangeRateToUSD = 0.0067,
            outletType = "Type A/B",
            continent = Continent.ASIA,
            flagEmoji = "\uD83C\uDDEF\uD83C\uDDF5",
            passportValidity = AppConstants.PassportValidity.PLANNED_STAY
        )

        assertEquals(AppConstants.PassportValidity.PLANNED_STAY, country.passportValidity)
    }

    @Test
    fun `Country copy creates new instance with modified values`() {
        val original = Country(
            id = AppConstants.CountryCode.JAPAN,
            name = AppConstants.CountryName.JAPAN,
            safetyLevel = SafetyLevel.NORMAL_SECURITY_PRECAUTIONS,
            visaRequirement = VisaRequirement.VISA_NOT_REQUIRED,
            currency = "Yen",
            currencyCode = "JPY",
            exchangeRateToUSD = 0.0067,
            outletType = "Type A/B",
            continent = Continent.ASIA,
            flagEmoji = "\uD83C\uDDEF\uD83C\uDDF5"
        )

        val copy = original.copy(name = "Nippon")

        assertEquals(AppConstants.CountryName.JAPAN, original.name)
        assertEquals("Nippon", copy.name)
        assertEquals(original.id, copy.id)
    }

    @Test
    fun `Country equals and hashCode work correctly`() {
        val country1 = Country(
            id = AppConstants.CountryCode.GERMANY,
            name = AppConstants.CountryName.GERMANY,
            safetyLevel = SafetyLevel.NORMAL_SECURITY_PRECAUTIONS,
            visaRequirement = VisaRequirement.VISA_NOT_REQUIRED,
            currency = "Euro",
            currencyCode = "EUR",
            exchangeRateToUSD = 1.1,
            outletType = "Type C/F",
            continent = Continent.EUROPE,
            flagEmoji = "\uD83C\uDDE9\uD83C\uDDEA"
        )

        val country2 = Country(
            id = AppConstants.CountryCode.GERMANY,
            name = AppConstants.CountryName.GERMANY,
            safetyLevel = SafetyLevel.NORMAL_SECURITY_PRECAUTIONS,
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
    fun `SafetyLevel LOW has correct displayNameResId`() {
        assertEquals(R.string.safety_low_risk, SafetyLevel.NORMAL_SECURITY_PRECAUTIONS.displayNameResId)
    }

    @Test
    fun `SafetyLevel MEDIUM has correct displayNameResId`() {
        assertEquals(R.string.safety_medium_risk, SafetyLevel.HIGH_DEGREE_CAUTION.displayNameResId)
    }

    @Test
    fun `SafetyLevel HIGH has correct displayNameResId`() {
        assertEquals(R.string.safety_high_risk, SafetyLevel.RECONSIDER_TRAVEL.displayNameResId)
    }

    @Test
    fun `SafetyLevel EXTREME has correct displayNameResId`() {
        assertEquals(R.string.safety_extreme_risk, SafetyLevel.DO_NOT_TRAVEL.displayNameResId)
    }

    @Test
    fun `SafetyLevel HIGH uses orange advisory color`() {
        assertEquals(Color(0xFFFF9800), SafetyLevel.RECONSIDER_TRAVEL.color)
    }

    @Test
    fun `SafetyLevel EXTREME uses red advisory color`() {
        assertEquals(Color(0xFFE53935), SafetyLevel.DO_NOT_TRAVEL.color)
    }

    @Test
    fun `SafetyLevel values returns all levels`() {
        val values = SafetyLevel.values()
        assertEquals(4, values.size)
        assertTrue(values.contains(SafetyLevel.NORMAL_SECURITY_PRECAUTIONS))
        assertTrue(values.contains(SafetyLevel.HIGH_DEGREE_CAUTION))
        assertTrue(values.contains(SafetyLevel.RECONSIDER_TRAVEL))
        assertTrue(values.contains(SafetyLevel.DO_NOT_TRAVEL))
    }

    @Test
    fun `SafetyLevel valueOf returns correct enum`() {
        assertEquals(SafetyLevel.NORMAL_SECURITY_PRECAUTIONS, SafetyLevel.valueOf("NORMAL_SECURITY_PRECAUTIONS"))
        assertEquals(SafetyLevel.HIGH_DEGREE_CAUTION, SafetyLevel.valueOf("HIGH_DEGREE_CAUTION"))
        assertEquals(SafetyLevel.RECONSIDER_TRAVEL, SafetyLevel.valueOf("RECONSIDER_TRAVEL"))
        assertEquals(SafetyLevel.DO_NOT_TRAVEL, SafetyLevel.valueOf("DO_NOT_TRAVEL"))
    }
}

class VisaRequirementTest {

    @Test
    fun `VisaRequirement NOT_REQUIRED has correct displayNameResId`() {
        assertEquals(R.string.visa_not_required, VisaRequirement.VISA_NOT_REQUIRED.displayNameResId)
    }

    @Test
    fun `VisaRequirement EVISA has correct displayNameResId`() {
        assertEquals(R.string.visa_evisa, VisaRequirement.E_VISA.displayNameResId)
    }

    @Test
    fun `VisaRequirement ON_ARRIVAL has correct displayNameResId`() {
        assertEquals(R.string.visa_on_arrival, VisaRequirement.VISA_ON_ARRIVAL.displayNameResId)
    }

    @Test
    fun `VisaRequirement REQUIRED has correct displayNameResId`() {
        assertEquals(R.string.visa_required, VisaRequirement.VISA_REQUIRED.displayNameResId)
    }

    @Test
    fun `VisaRequirement RESTRICTED has correct displayNameResId`() {
        assertEquals(R.string.visa_restricted, VisaRequirement.RESTRICTED.displayNameResId)
    }

    @Test
    fun `VisaRequirement values returns all requirements`() {
        val values = VisaRequirement.values()
        assertEquals(5, values.size)
        assertTrue(values.contains(VisaRequirement.VISA_NOT_REQUIRED))
        assertTrue(values.contains(VisaRequirement.E_VISA))
        assertTrue(values.contains(VisaRequirement.VISA_ON_ARRIVAL))
        assertTrue(values.contains(VisaRequirement.VISA_REQUIRED))
        assertTrue(values.contains(VisaRequirement.RESTRICTED))
    }

    @Test
    fun `VisaRequirement valueOf returns correct enum`() {
        assertEquals(VisaRequirement.VISA_NOT_REQUIRED, VisaRequirement.valueOf("VISA_NOT_REQUIRED"))
        assertEquals(VisaRequirement.E_VISA, VisaRequirement.valueOf("E_VISA"))
        assertEquals(VisaRequirement.VISA_ON_ARRIVAL, VisaRequirement.valueOf("VISA_ON_ARRIVAL"))
        assertEquals(VisaRequirement.VISA_REQUIRED, VisaRequirement.valueOf("VISA_REQUIRED"))
        assertEquals(VisaRequirement.RESTRICTED, VisaRequirement.valueOf("RESTRICTED"))
    }
}

class ContinentTest {

    @Test
    fun `Continent NORTH_AMERICA has correct displayNameResId`() {
        assertEquals(R.string.continent_north_america, Continent.NORTH_AMERICA.displayNameResId)
    }

    @Test
    fun `Continent SOUTH_AMERICA has correct displayNameResId`() {
        assertEquals(R.string.continent_south_america, Continent.SOUTH_AMERICA.displayNameResId)
    }

    @Test
    fun `Continent EUROPE has correct displayNameResId`() {
        assertEquals(R.string.continent_europe, Continent.EUROPE.displayNameResId)
    }

    @Test
    fun `Continent AFRICA has correct displayNameResId`() {
        assertEquals(R.string.continent_africa, Continent.AFRICA.displayNameResId)
    }

    @Test
    fun `Continent ASIA has correct displayNameResId`() {
        assertEquals(R.string.continent_asia, Continent.ASIA.displayNameResId)
    }

    @Test
    fun `Continent OCEANIA has correct displayNameResId`() {
        assertEquals(R.string.continent_oceania, Continent.OCEANIA.displayNameResId)
    }

    @Test
    fun `Continent ANTARCTICA has correct displayNameResId`() {
        assertEquals(R.string.continent_antarctica, Continent.ANTARCTICA.displayNameResId)
    }

    @Test
    fun `Continent values returns all continents`() {
        val values = Continent.values()
        assertEquals(7, values.size)
    }
}
