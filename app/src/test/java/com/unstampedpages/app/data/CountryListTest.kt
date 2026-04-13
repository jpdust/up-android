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
            assertTrue("Country name should not be empty", country.englishName.isNotEmpty())
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
        assertEquals("United States", us?.englishName)
    }

    @Test
    fun `contains United Kingdom`() {
        val gb = CountryList.countries.find { it.code == "GB" }

        assertNotNull(gb)
        assertEquals("United Kingdom", gb?.englishName)
    }

    @Test
    fun `contains Japan`() {
        val jp = CountryList.countries.find { it.code == "JP" }

        assertNotNull(jp)
        assertEquals("Japan", jp?.englishName)
    }

    @Test
    fun `contains Australia`() {
        val au = CountryList.countries.find { it.code == "AU" }

        assertNotNull(au)
        assertEquals("Australia", au?.englishName)
    }

    @Test
    fun `contains Brazil`() {
        val br = CountryList.countries.find { it.code == "BR" }

        assertNotNull(br)
        assertEquals("Brazil", br?.englishName)
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

class CountryListItemDataClassTest {

    @Test
    fun `CountryListItem has correct code`() {
        val country = CountryListItem(code = "US", englishName = "United States")

        assertEquals("US", country.code)
    }

    @Test
    fun `CountryListItem has correct englishName`() {
        val country = CountryListItem(code = "US", englishName = "United States")

        assertEquals("United States", country.englishName)
    }

    @Test
    fun `equals returns true for same values`() {
        val country1 = CountryListItem(code = "US", englishName = "United States")
        val country2 = CountryListItem(code = "US", englishName = "United States")

        assertEquals(country1, country2)
    }

    @Test
    fun `equals returns false for different code`() {
        val country1 = CountryListItem(code = "US", englishName = "United States")
        val country2 = CountryListItem(code = "GB", englishName = "United States")

        assertNotEquals(country1, country2)
    }

    @Test
    fun `equals returns false for different name`() {
        val country1 = CountryListItem(code = "US", englishName = "United States")
        val country2 = CountryListItem(code = "US", englishName = "America")

        assertNotEquals(country1, country2)
    }

    @Test
    fun `hashCode is consistent for equal countries`() {
        val country1 = CountryListItem(code = "JP", englishName = "Japan")
        val country2 = CountryListItem(code = "JP", englishName = "Japan")

        assertEquals(country1.hashCode(), country2.hashCode())
    }

    @Test
    fun `copy creates new instance`() {
        val original = CountryListItem(code = "US", englishName = "United States")

        val copy = original.copy()

        assertEquals(original, copy)
    }

    @Test
    fun `copy can modify code`() {
        val original = CountryListItem(code = "US", englishName = "Test")

        val modified = original.copy(code = "GB")

        assertEquals("GB", modified.code)
        assertEquals("US", original.code)
    }

    @Test
    fun `copy can modify englishName`() {
        val original = CountryListItem(code = "US", englishName = "Original")

        val modified = original.copy(englishName = "Modified")

        assertEquals("Modified", modified.englishName)
        assertEquals("Original", original.englishName)
    }

    @Test
    fun `toString contains code and name`() {
        val country = CountryListItem(code = "JP", englishName = "Japan")

        val string = country.toString()

        assertTrue(string.contains("JP"))
        assertTrue(string.contains("Japan"))
    }
}
