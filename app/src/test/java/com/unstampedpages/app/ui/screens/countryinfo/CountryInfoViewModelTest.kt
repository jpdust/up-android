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

    // ==================== Search Functionality Tests ====================

    @Test
    fun `initial state has empty search query`() {
        val state = viewModel.uiState.value

        assertEquals("", state.searchQuery)
        assertTrue(state.searchResults.isEmpty())
    }

    @Test
    fun `updateSearchQuery with valid query returns results`() {
        viewModel.updateSearchQuery("Japan")

        val state = viewModel.uiState.value

        assertEquals("Japan", state.searchQuery)
        assertTrue(state.searchResults.isNotEmpty())
        assertTrue(state.searchResults.any { it.name == "Japan" })
    }

    @Test
    fun `updateSearchQuery with partial match returns results`() {
        viewModel.updateSearchQuery("Uni")

        val state = viewModel.uiState.value

        assertTrue(state.searchResults.isNotEmpty())
        assertTrue(state.searchResults.any { it.name.contains("United") })
    }

    @Test
    fun `updateSearchQuery is case insensitive`() {
        viewModel.updateSearchQuery("japan")

        val state = viewModel.uiState.value

        assertTrue(state.searchResults.any { it.name == "Japan" })
    }

    @Test
    fun `updateSearchQuery with uppercase returns results`() {
        viewModel.updateSearchQuery("FRANCE")

        val state = viewModel.uiState.value

        assertTrue(state.searchResults.any { it.name == "France" })
    }

    @Test
    fun `updateSearchQuery with empty string returns no results`() {
        viewModel.updateSearchQuery("Japan")
        assertTrue(viewModel.uiState.value.searchResults.isNotEmpty())

        viewModel.updateSearchQuery("")

        val state = viewModel.uiState.value

        assertEquals("", state.searchQuery)
        assertTrue(state.searchResults.isEmpty())
    }

    @Test
    fun `updateSearchQuery with blank string returns no results`() {
        viewModel.updateSearchQuery("   ")

        val state = viewModel.uiState.value

        assertTrue(state.searchResults.isEmpty())
    }

    @Test
    fun `updateSearchQuery limits results to 5`() {
        // Search for something that would match many countries
        viewModel.updateSearchQuery("a")

        val state = viewModel.uiState.value

        assertTrue(state.searchResults.size <= 5)
    }

    @Test
    fun `updateSearchQuery with no matches returns empty list`() {
        viewModel.updateSearchQuery("xyznonexistent")

        val state = viewModel.uiState.value

        assertEquals("xyznonexistent", state.searchQuery)
        assertTrue(state.searchResults.isEmpty())
    }

    @Test
    fun `clearSearch resets search query and results`() {
        viewModel.updateSearchQuery("Japan")
        assertTrue(viewModel.uiState.value.searchResults.isNotEmpty())

        viewModel.clearSearch()

        val state = viewModel.uiState.value

        assertEquals("", state.searchQuery)
        assertTrue(state.searchResults.isEmpty())
    }

    @Test
    fun `search preserves selected country`() {
        viewModel.selectCountry("us")
        assertNotNull(viewModel.uiState.value.selectedCountry)

        viewModel.updateSearchQuery("Japan")

        assertNotNull(viewModel.uiState.value.selectedCountry)
        assertEquals("us", viewModel.uiState.value.selectedCountry?.id)
    }

    @Test
    fun `clearSearch preserves selected country`() {
        viewModel.selectCountry("us")
        viewModel.updateSearchQuery("Japan")
        viewModel.clearSearch()

        assertNotNull(viewModel.uiState.value.selectedCountry)
        assertEquals("us", viewModel.uiState.value.selectedCountry?.id)
    }

    @Test
    fun `search results contain correct country data`() {
        viewModel.updateSearchQuery("Germany")

        val germany = viewModel.uiState.value.searchResults.find { it.name == "Germany" }

        assertNotNull(germany)
        assertEquals("de", germany?.id)
        assertEquals(Continent.EUROPE, germany?.continent)
    }

    @Test
    fun `multiple searches update results correctly`() {
        viewModel.updateSearchQuery("Japan")
        assertTrue(viewModel.uiState.value.searchResults.any { it.name == "Japan" })

        viewModel.updateSearchQuery("France")
        assertFalse(viewModel.uiState.value.searchResults.any { it.name == "Japan" })
        assertTrue(viewModel.uiState.value.searchResults.any { it.name == "France" })
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
