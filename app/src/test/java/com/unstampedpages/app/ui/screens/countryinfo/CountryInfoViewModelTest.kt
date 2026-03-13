package com.unstampedpages.app.ui.screens.countryinfo

import com.unstampedpages.app.data.model.Continent
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CountryInfoViewModelTest {

    private lateinit var viewModel: CountryInfoViewModel

    @Before
    fun setUp() {
        viewModel = CountryInfoViewModel()
    }

    @Test
    fun `initial state has countries loaded`() {
        val state = viewModel.uiState.value

        assertTrue(state.countries.isNotEmpty())
        assertFalse(state.isLoading)
        assertNull(state.selectedCountry)
    }

    @Test
    fun `initial state has no selected country`() {
        val state = viewModel.uiState.value

        assertNull(state.selectedCountry)
    }

    @Test
    fun `selectCountry updates selectedCountry`() {
        viewModel.selectCountry("us")

        val state = viewModel.uiState.value

        assertNotNull(state.selectedCountry)
        assertEquals("us", state.selectedCountry?.id)
        assertEquals("United States", state.selectedCountry?.name)
    }

    @Test
    fun `selectCountry with valid id sets country`() {
        viewModel.selectCountry("jp")

        val state = viewModel.uiState.value

        assertNotNull(state.selectedCountry)
        assertEquals("Japan", state.selectedCountry?.name)
    }

    @Test
    fun `selectCountry with invalid id sets null`() {
        viewModel.selectCountry("invalid_country_id")

        val state = viewModel.uiState.value

        assertNull(state.selectedCountry)
    }

    @Test
    fun `clearSelection removes selected country`() {
        viewModel.selectCountry("us")
        assertNotNull(viewModel.uiState.value.selectedCountry)

        viewModel.clearSelection()

        assertNull(viewModel.uiState.value.selectedCountry)
    }

    @Test
    fun `selecting different countries updates state`() {
        viewModel.selectCountry("us")
        assertEquals("United States", viewModel.uiState.value.selectedCountry?.name)

        viewModel.selectCountry("fr")
        assertEquals("France", viewModel.uiState.value.selectedCountry?.name)

        viewModel.selectCountry("jp")
        assertEquals("Japan", viewModel.uiState.value.selectedCountry?.name)
    }

    @Test
    fun `countries list contains expected countries`() {
        val countries = viewModel.uiState.value.countries
        val countryIds = countries.map { it.id }

        assertTrue(countryIds.contains("us"))
        assertTrue(countryIds.contains("gb"))
        assertTrue(countryIds.contains("fr"))
        assertTrue(countryIds.contains("jp"))
        assertTrue(countryIds.contains("au"))
    }

    @Test
    fun `countries list contains countries from all continents`() {
        val countries = viewModel.uiState.value.countries
        val continents = countries.map { it.continent }.distinct()

        assertTrue(continents.contains(Continent.NORTH_AMERICA))
        assertTrue(continents.contains(Continent.SOUTH_AMERICA))
        assertTrue(continents.contains(Continent.EUROPE))
        assertTrue(continents.contains(Continent.AFRICA))
        assertTrue(continents.contains(Continent.ASIA))
        assertTrue(continents.contains(Continent.OCEANIA))
    }

    @Test
    fun `selected country has all required fields`() {
        viewModel.selectCountry("us")

        val country = viewModel.uiState.value.selectedCountry!!

        assertEquals("us", country.id)
        assertEquals("United States", country.name)
        assertTrue(country.population > 0)
        assertNotNull(country.currency)
        assertNotNull(country.currencyCode)
        assertTrue(country.exchangeRateToUSD > 0)
        assertNotNull(country.outletType)
        assertNotNull(country.continent)
        assertNotNull(country.flagEmoji)
    }

    @Test
    fun `uiState preserves countries after selection`() {
        val initialCount = viewModel.uiState.value.countries.size

        viewModel.selectCountry("us")

        assertEquals(initialCount, viewModel.uiState.value.countries.size)
    }

    @Test
    fun `uiState preserves countries after clear`() {
        val initialCount = viewModel.uiState.value.countries.size

        viewModel.selectCountry("us")
        viewModel.clearSelection()

        assertEquals(initialCount, viewModel.uiState.value.countries.size)
    }
}

class CountryInfoUiStateTest {

    @Test
    fun `default state has empty countries list`() {
        val state = CountryInfoUiState()

        assertTrue(state.countries.isEmpty())
    }

    @Test
    fun `default state is not loading`() {
        val state = CountryInfoUiState()

        assertFalse(state.isLoading)
    }

    @Test
    fun `default state has no selected country`() {
        val state = CountryInfoUiState()

        assertNull(state.selectedCountry)
    }

    @Test
    fun `state can be created with loading true`() {
        val state = CountryInfoUiState(isLoading = true)

        assertTrue(state.isLoading)
    }
}
