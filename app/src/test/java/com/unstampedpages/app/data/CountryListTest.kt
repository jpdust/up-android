package com.unstampedpages.app.data

import org.junit.Assert.*
import org.junit.Test

class CountryListTest {

    @Test
    fun `countries list is not empty`() {
        assertTrue(CountryList.countries.isNotEmpty())
    }

    @Test
    fun `countries list has expected size`() {
        // CountryList has 195 countries
        assertTrue(CountryList.countries.size >= 190)
    }

    @Test
    fun `all countries have non-empty code`() {
        CountryList.countries.forEach { country ->
            assertTrue("Country code should not be empty", country.code.isNotEmpty())
        }
    }

    @Test
    fun `all countries have non-empty name`() {
        CountryList.countries.forEach { country ->
            assertTrue("Country name should not be empty", country.name.isNotEmpty())
        }
    }

    @Test
    fun `all country codes are unique`() {
        val codes = CountryList.countries.map { it.code }

        assertEquals(codes.size, codes.distinct().size)
    }

    @Test
    fun `all country codes are uppercase`() {
        CountryList.countries.forEach { country ->
            assertEquals(
                "Country code ${country.code} should be uppercase",
                country.code.uppercase(),
                country.code
            )
        }
    }

    @Test
    fun `all country codes are 2 characters`() {
        CountryList.countries.forEach { country ->
            assertEquals(
                "Country code ${country.code} should be 2 characters",
                2,
                country.code.length
            )
        }
    }

    @Test
    fun `contains United States`() {
        val us = CountryList.countries.find { it.code == "US" }

        assertNotNull(us)
        assertEquals("United States", us?.name)
    }

    @Test
    fun `contains United Kingdom`() {
        val gb = CountryList.countries.find { it.code == "GB" }

        assertNotNull(gb)
        assertEquals("United Kingdom", gb?.name)
    }

    @Test
    fun `contains Japan`() {
        val jp = CountryList.countries.find { it.code == "JP" }

        assertNotNull(jp)
        assertEquals("Japan", jp?.name)
    }

    @Test
    fun `contains Australia`() {
        val au = CountryList.countries.find { it.code == "AU" }

        assertNotNull(au)
        assertEquals("Australia", au?.name)
    }

    @Test
    fun `contains Brazil`() {
        val br = CountryList.countries.find { it.code == "BR" }

        assertNotNull(br)
        assertEquals("Brazil", br?.name)
    }

    @Test
    fun `contains countries from all regions`() {
        val codes = CountryList.countries.map { it.code }

        // North America
        assertTrue(codes.contains("US"))
        assertTrue(codes.contains("CA"))
        assertTrue(codes.contains("MX"))

        // Europe
        assertTrue(codes.contains("GB"))
        assertTrue(codes.contains("FR"))
        assertTrue(codes.contains("DE"))

        // Asia
        assertTrue(codes.contains("JP"))
        assertTrue(codes.contains("CN"))
        assertTrue(codes.contains("IN"))

        // Africa
        assertTrue(codes.contains("ZA"))
        assertTrue(codes.contains("NG"))
        assertTrue(codes.contains("EG"))

        // South America
        assertTrue(codes.contains("BR"))
        assertTrue(codes.contains("AR"))

        // Oceania
        assertTrue(codes.contains("AU"))
        assertTrue(codes.contains("NZ"))
    }
}

class CountryDataClassTest {

    @Test
    fun `Country has correct code`() {
        val country = Country(code = "US", name = "United States")

        assertEquals("US", country.code)
    }

    @Test
    fun `Country has correct name`() {
        val country = Country(code = "US", name = "United States")

        assertEquals("United States", country.name)
    }

    @Test
    fun `equals returns true for same values`() {
        val country1 = Country(code = "US", name = "United States")
        val country2 = Country(code = "US", name = "United States")

        assertEquals(country1, country2)
    }

    @Test
    fun `equals returns false for different code`() {
        val country1 = Country(code = "US", name = "United States")
        val country2 = Country(code = "GB", name = "United States")

        assertNotEquals(country1, country2)
    }

    @Test
    fun `equals returns false for different name`() {
        val country1 = Country(code = "US", name = "United States")
        val country2 = Country(code = "US", name = "America")

        assertNotEquals(country1, country2)
    }

    @Test
    fun `hashCode is consistent for equal countries`() {
        val country1 = Country(code = "JP", name = "Japan")
        val country2 = Country(code = "JP", name = "Japan")

        assertEquals(country1.hashCode(), country2.hashCode())
    }

    @Test
    fun `copy creates new instance`() {
        val original = Country(code = "US", name = "United States")

        val copy = original.copy()

        assertEquals(original, copy)
    }

    @Test
    fun `copy can modify code`() {
        val original = Country(code = "US", name = "Test")

        val modified = original.copy(code = "GB")

        assertEquals("GB", modified.code)
        assertEquals("US", original.code)
    }

    @Test
    fun `copy can modify name`() {
        val original = Country(code = "US", name = "Original")

        val modified = original.copy(name = "Modified")

        assertEquals("Modified", modified.name)
        assertEquals("Original", original.name)
    }

    @Test
    fun `toString contains code and name`() {
        val country = Country(code = "JP", name = "Japan")

        val string = country.toString()

        assertTrue(string.contains("JP"))
        assertTrue(string.contains("Japan"))
    }
}
