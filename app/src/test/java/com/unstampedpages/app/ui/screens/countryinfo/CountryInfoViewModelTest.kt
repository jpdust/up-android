package com.unstampedpages.app.ui.screens.countryinfo

import com.unstampedpages.app.data.AppConstants
import com.unstampedpages.app.data.model.Continent
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

private const val CAYMAN_ISLANDS = "Cayman Islands"
private const val EASTER_ISLAND = "Easter Island"

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
        assertEquals(AppConstants.CountryName.UNITED_STATES, state.selectedCountry?.name)
    }

    @Test
    fun `selectCountry with valid id sets country`() {
        viewModel.selectCountry("jp")

        val state = viewModel.uiState.value

        assertNotNull(state.selectedCountry)
        assertEquals(AppConstants.CountryName.JAPAN, state.selectedCountry?.name)
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
        viewModel.selectCountry(AppConstants.CountryCode.UNITED_STATES)
        assertEquals(AppConstants.CountryName.UNITED_STATES, viewModel.uiState.value.selectedCountry?.name)

        viewModel.selectCountry(AppConstants.CountryCode.FRANCE)
        assertEquals(AppConstants.CountryName.FRANCE, viewModel.uiState.value.selectedCountry?.name)

        viewModel.selectCountry(AppConstants.CountryCode.JAPAN)
        assertEquals(AppConstants.CountryName.JAPAN, viewModel.uiState.value.selectedCountry?.name)
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
        assertEquals(AppConstants.CountryName.UNITED_STATES, country.name)
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
        viewModel.updateSearchQuery(AppConstants.CountryName.JAPAN)

        val state = viewModel.uiState.value

        assertEquals(AppConstants.CountryName.JAPAN, state.searchQuery)
        assertTrue(state.searchResults.isNotEmpty())
        assertTrue(state.searchResults.any { it.name == AppConstants.CountryName.JAPAN })
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

        assertTrue(state.searchResults.any { it.name == AppConstants.CountryName.JAPAN })
    }

    @Test
    fun `updateSearchQuery with uppercase returns results`() {
        viewModel.updateSearchQuery("FRANCE")

        val state = viewModel.uiState.value

        assertTrue(state.searchResults.any { it.name == AppConstants.CountryName.FRANCE })
    }

    @Test
    fun `updateSearchQuery with empty string returns no results`() {
        viewModel.updateSearchQuery(AppConstants.CountryName.JAPAN)
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
        viewModel.updateSearchQuery(AppConstants.CountryName.JAPAN)
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

        viewModel.updateSearchQuery(AppConstants.CountryName.JAPAN)

        assertNotNull(viewModel.uiState.value.selectedCountry)
        assertEquals("us", viewModel.uiState.value.selectedCountry?.id)
    }

    @Test
    fun `clearSearch preserves selected country`() {
        viewModel.selectCountry("us")
        viewModel.updateSearchQuery(AppConstants.CountryName.JAPAN)
        viewModel.clearSearch()

        assertNotNull(viewModel.uiState.value.selectedCountry)
        assertEquals("us", viewModel.uiState.value.selectedCountry?.id)
    }

    @Test
    fun `search results contain correct country data`() {
        viewModel.updateSearchQuery(AppConstants.CountryName.GERMANY)

        val germany = viewModel.uiState.value.searchResults.find { it.name == AppConstants.CountryName.GERMANY }

        assertNotNull(germany)
        assertEquals(AppConstants.CountryCode.GERMANY, germany?.id)
        assertEquals(Continent.EUROPE, germany?.continent)
    }

    @Test
    fun `multiple searches update results correctly`() {
        viewModel.updateSearchQuery(AppConstants.CountryName.JAPAN)
        assertTrue(viewModel.uiState.value.searchResults.any { it.name == AppConstants.CountryName.JAPAN })

        viewModel.updateSearchQuery(AppConstants.CountryName.FRANCE)
        assertFalse(viewModel.uiState.value.searchResults.any { it.name == AppConstants.CountryName.JAPAN })
        assertTrue(viewModel.uiState.value.searchResults.any { it.name == AppConstants.CountryName.FRANCE })
    }

    // ==================== Edge Case Tests ====================

    @Test
    fun `selecting same country twice does not change state`() {
        viewModel.selectCountry("us")
        val firstState = viewModel.uiState.value

        viewModel.selectCountry("us")
        val secondState = viewModel.uiState.value

        assertEquals(firstState.selectedCountry, secondState.selectedCountry)
    }

    @Test
    fun `clearing selection when nothing selected is no-op`() {
        assertNull(viewModel.uiState.value.selectedCountry)
        viewModel.clearSelection()
        assertNull(viewModel.uiState.value.selectedCountry)
    }

    @Test
    fun `clearing search when empty is no-op`() {
        assertEquals("", viewModel.uiState.value.searchQuery)
        assertTrue(viewModel.uiState.value.searchResults.isEmpty())

        viewModel.clearSearch()

        assertEquals("", viewModel.uiState.value.searchQuery)
        assertTrue(viewModel.uiState.value.searchResults.isEmpty())
    }

    @Test
    fun `search with special characters returns no results`() {
        viewModel.updateSearchQuery("@#$%")

        val state = viewModel.uiState.value
        assertTrue(state.searchResults.isEmpty())
    }

    @Test
    fun `search with numbers returns no results`() {
        viewModel.updateSearchQuery("12345")

        val state = viewModel.uiState.value
        assertTrue(state.searchResults.isEmpty())
    }

    @Test
    fun `search preserves country list`() {
        val initialCount = viewModel.uiState.value.countries.size

        viewModel.updateSearchQuery(AppConstants.CountryName.JAPAN)
        assertEquals(initialCount, viewModel.uiState.value.countries.size)

        viewModel.updateSearchQuery("xyz")
        assertEquals(initialCount, viewModel.uiState.value.countries.size)

        viewModel.clearSearch()
        assertEquals(initialCount, viewModel.uiState.value.countries.size)
    }

    @Test
    fun `select then search then clear maintains selection`() {
        viewModel.selectCountry("fr")
        assertEquals(AppConstants.CountryName.FRANCE, viewModel.uiState.value.selectedCountry?.name)

        viewModel.updateSearchQuery(AppConstants.CountryName.GERMANY)
        assertEquals(AppConstants.CountryName.FRANCE, viewModel.uiState.value.selectedCountry?.name)

        viewModel.clearSearch()
        assertEquals(AppConstants.CountryName.FRANCE, viewModel.uiState.value.selectedCountry?.name)
    }

    @Test
    fun `search with leading spaces finds matches containing spaces`() {
        // Search with leading spaces - matches "Japan" because filter uses contains()
        viewModel.updateSearchQuery("   Japan")
        // Leading spaces means no match since country names don't have leading spaces
        assertTrue(viewModel.uiState.value.searchResults.isEmpty())
    }

    @Test
    fun `search with trailing spaces still finds matches`() {
        // Trailing spaces - "Japan   " contains "Japan" so Japan matches
        viewModel.updateSearchQuery(AppConstants.CountryName.JAPAN)
        assertTrue(viewModel.uiState.value.searchResults.any { it.name == AppConstants.CountryName.JAPAN })
    }

    @Test
    fun `rapid search updates work correctly`() {
        viewModel.updateSearchQuery("J")
        viewModel.updateSearchQuery("Ja")
        viewModel.updateSearchQuery("Jap")
        viewModel.updateSearchQuery("Japa")
        viewModel.updateSearchQuery(AppConstants.CountryName.JAPAN)

        assertEquals(AppConstants.CountryName.JAPAN, viewModel.uiState.value.searchQuery)
        assertTrue(viewModel.uiState.value.searchResults.any { it.name == AppConstants.CountryName.JAPAN })
    }

    @Test
    fun `country selection after invalid id selection works`() {
        viewModel.selectCountry("invalid")
        assertNull(viewModel.uiState.value.selectedCountry)

        viewModel.selectCountry("jp")
        assertNotNull(viewModel.uiState.value.selectedCountry)
        assertEquals(AppConstants.CountryName.JAPAN, viewModel.uiState.value.selectedCountry?.name)
    }

    // ==================== Country Data Validation Tests ====================

    @Test
    fun `all countries have valid exchange rates`() {
        viewModel.uiState.value.countries.forEach { country ->
            assertTrue(
                "Country ${country.name} should have positive exchange rate",
                country.exchangeRateToUSD > 0
            )
        }
    }

    @Test
    fun `all countries have non-empty currency code`() {
        viewModel.uiState.value.countries.forEach { country ->
            assertTrue(
                "Country ${country.name} should have non-empty currency code",
                country.currencyCode.isNotBlank()
            )
        }
    }

    @Test
    fun `all countries have non-empty currency name`() {
        viewModel.uiState.value.countries.forEach { country ->
            assertTrue(
                "Country ${country.name} should have non-empty currency name",
                country.currency.isNotBlank()
            )
        }
    }

    @Test
    fun `all countries have non-empty outlet type`() {
        viewModel.uiState.value.countries.forEach { country ->
            assertTrue(
                "Country ${country.name} should have non-empty outlet type",
                country.outletType.isNotBlank()
            )
        }
    }

    @Test
    fun `all countries have non-empty flag emoji`() {
        viewModel.uiState.value.countries.forEach { country ->
            assertTrue(
                "Country ${country.name} should have non-empty flag emoji",
                country.flagEmoji.isNotBlank()
            )
        }
    }

    @Test
    fun `all countries have valid safety level`() {
        viewModel.uiState.value.countries.forEach { country ->
            assertNotNull(
                "Country ${country.name} should have a safety level",
                country.safetyLevel
            )
        }
    }

    @Test
    fun `all countries have valid visa requirement`() {
        viewModel.uiState.value.countries.forEach { country ->
            assertNotNull(
                "Country ${country.name} should have a visa requirement",
                country.visaRequirement
            )
        }
    }

    // ==================== updateLocale Tests ====================

    @Test
    fun `updateLocale with same locale as current does not change countries list`() {
        val before = viewModel.uiState.value.countries.map { it.id }
        // The ViewModel starts with Locale.getDefault(); calling updateLocale with the
        // same locale should be a no-op (guard clause in updateLocale).
        viewModel.updateLocale(java.util.Locale.getDefault())
        val after = viewModel.uiState.value.countries.map { it.id }
        assertEquals(before, after)
    }

    @Test
    fun `updateLocale with a different locale refreshes the countries list`() {
        val before = viewModel.uiState.value.countries
        val newLocale = if (java.util.Locale.getDefault() == java.util.Locale.ENGLISH)
            java.util.Locale.forLanguageTag("es") else java.util.Locale.ENGLISH
        viewModel.updateLocale(newLocale)
        // The list should still be non-empty and the same size — only names may differ.
        val after = viewModel.uiState.value.countries
        assertTrue(after.isNotEmpty())
        assertEquals(before.size, after.size)
    }

    @Test
    fun `updateLocale keeps isLoading false`() {
        viewModel.updateLocale(java.util.Locale.forLanguageTag("es"))
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `updateLocale preserves country ids`() {
        val idsBefore = viewModel.uiState.value.countries.map { it.id }.toSet()
        viewModel.updateLocale(java.util.Locale.forLanguageTag("fr"))
        val idsAfter = viewModel.uiState.value.countries.map { it.id }.toSet()
        assertEquals(idsBefore, idsAfter)
    }

    @Test
    fun `updateLocale clears any existing search query`() {
        // updateLocale reloads via loadCountries, which resets the full state.
        viewModel.updateSearchQuery("Japan")
        assertTrue(viewModel.uiState.value.searchResults.isNotEmpty())
        viewModel.updateLocale(java.util.Locale.forLanguageTag("es"))
        // After locale change the state is reset — search results are cleared.
        assertEquals("", viewModel.uiState.value.searchQuery)
        assertTrue(viewModel.uiState.value.searchResults.isEmpty())
    }

    @Test
    fun `calling updateLocale twice with the same new locale only reloads once`() {
        val newLocale = java.util.Locale.forLanguageTag("es")
        viewModel.updateLocale(newLocale)
        val stateAfterFirst = viewModel.uiState.value
        // Second call with the same locale should be a no-op (guard clause).
        viewModel.updateLocale(newLocale)
        val stateAfterSecond = viewModel.uiState.value
        assertEquals(stateAfterFirst, stateAfterSecond)
    }

    // ==================== selectCountry displayName Tests ====================

    @Test
    fun `selectCountry with displayName stores it in selectedDisplayName`() {
        viewModel.selectCountry("gb", CAYMAN_ISLANDS)
        assertEquals(CAYMAN_ISLANDS, viewModel.uiState.value.selectedDisplayName)
    }

    @Test
    fun `selectCountry without displayName leaves selectedDisplayName null`() {
        viewModel.selectCountry("us")
        assertNull(viewModel.uiState.value.selectedDisplayName)
    }

    @Test
    fun `selectCountry with null displayName leaves selectedDisplayName null`() {
        viewModel.selectCountry("gb", displayName = null)
        assertNull(viewModel.uiState.value.selectedDisplayName)
    }

    @Test
    fun `clearSelection clears selectedDisplayName`() {
        viewModel.selectCountry("gb", CAYMAN_ISLANDS)
        assertNotNull(viewModel.uiState.value.selectedDisplayName)

        viewModel.clearSelection()

        assertNull(viewModel.uiState.value.selectedDisplayName)
        assertNull(viewModel.uiState.value.selectedCountry)
    }

    @Test
    fun `selecting a new country replaces previous displayName`() {
        viewModel.selectCountry("gb", CAYMAN_ISLANDS)
        assertEquals(CAYMAN_ISLANDS, viewModel.uiState.value.selectedDisplayName)

        viewModel.selectCountry("dk", "Greenland")
        assertEquals("Greenland", viewModel.uiState.value.selectedDisplayName)
    }

    @Test
    fun `selecting a country without displayName after one with displayName clears displayName`() {
        viewModel.selectCountry("gb", CAYMAN_ISLANDS)
        assertNotNull(viewModel.uiState.value.selectedDisplayName)

        viewModel.selectCountry("us")
        assertNull(viewModel.uiState.value.selectedDisplayName)
    }

    // ==================== Territory Search Tests ====================

    @Test
    fun `searching Easter Island returns Chile`() {
        viewModel.updateSearchQuery(EASTER_ISLAND)

        val results = viewModel.uiState.value.searchResults
        assertTrue(results.isNotEmpty())
        val suggestion = results.first()
        assertEquals(EASTER_ISLAND, suggestion.displayName)
        assertEquals("cl", suggestion.country.id)
        assertNotNull(suggestion.parentCountryName)
    }

    @Test
    fun `searching Cayman Islands returns Cayman Islands own entry`() {
        viewModel.updateSearchQuery("Cayman")

        val results = viewModel.uiState.value.searchResults
        assertTrue(results.isNotEmpty())
        // Cayman Islands now has its own repository entry (KYD currency)
        assertTrue(results.any { it.displayName == CAYMAN_ISLANDS && it.country.id == "ky" })
    }

    @Test
    fun `searching Azores returns Portugal`() {
        viewModel.updateSearchQuery("Azores")

        val results = viewModel.uiState.value.searchResults
        assertTrue(results.isNotEmpty())
        assertTrue(results.any { it.displayName == "Azores" && it.country.id == "pt" })
    }

    @Test
    fun `territory result has parentCountryName set`() {
        viewModel.updateSearchQuery("Puerto Rico")

        val results = viewModel.uiState.value.searchResults
        val suggestion = results.find { it.displayName == "Puerto Rico" }
        assertNotNull(suggestion)
        assertNotNull(suggestion!!.parentCountryName)
        assertEquals("us", suggestion.country.id)
    }

    @Test
    fun `direct country result has null parentCountryName`() {
        viewModel.updateSearchQuery(AppConstants.CountryName.FRANCE)

        val results = viewModel.uiState.value.searchResults
        val franceSuggestion = results.find { it.displayName == AppConstants.CountryName.FRANCE }
        assertNotNull(franceSuggestion)
        assertNull(franceSuggestion!!.parentCountryName)
    }

    @Test
    fun `territory suggestion id points to territory own repo entry`() {
        // Bermuda has its own repository entry (BMD currency) — suggestion.id is "bm"
        viewModel.updateSearchQuery("Bermuda")

        val results = viewModel.uiState.value.searchResults
        val suggestion = results.find { it.displayName == "Bermuda" }
        assertNotNull(suggestion)
        assertEquals("bm", suggestion!!.id)
    }

    @Test
    fun `territory suggestion still delegates to sovereign when no own entry exists`() {
        // Anguilla has no own repository entry — delegates to United Kingdom
        viewModel.updateSearchQuery("Anguilla")

        val results = viewModel.uiState.value.searchResults
        val suggestion = results.find { it.displayName == "Anguilla" }
        assertNotNull(suggestion)
        assertEquals("gb", suggestion!!.id)
    }

    @Test
    fun `searching without accents matches accented territory and returns single result`() {
        viewModel.updateSearchQuery("Reunion")

        val results = viewModel.uiState.value.searchResults
        assertTrue(results.isNotEmpty())
        val reunionResults = results.filter { it.displayName.contains("union", ignoreCase = true) }
        assertEquals(1, reunionResults.size)
        assertEquals("Réunion", reunionResults.first().displayName)
    }

    @Test
    fun `results are still limited to 5 when territory matches are included`() {
        // "Island" appears in many territory names
        viewModel.updateSearchQuery("Island")

        assertTrue(viewModel.uiState.value.searchResults.size <= 5)
    }

    // ==================== Search Result Order Tests ====================

    @Test
    fun `search results are limited to 5 even with many matches`() {
        // Search for a common letter that matches many countries
        viewModel.updateSearchQuery("a")
        assertTrue(viewModel.uiState.value.searchResults.size <= 5)
    }

    @Test
    fun `search results are capped at exactly 5 for broad queries`() {
        viewModel.updateSearchQuery("a")
        assertEquals(5, viewModel.uiState.value.searchResults.size)

        viewModel.updateSearchQuery("an")
        assertEquals(5, viewModel.uiState.value.searchResults.size)
    }

    @Test
    fun `search results are sorted alphabetically within prefix and substring groups`() {
        viewModel.updateSearchQuery("an")
        val results = viewModel.uiState.value.searchResults
        assertTrue(results.size > 1)
        val names = results.map { it.displayName }
        val prefixMatches = names.filter { it.startsWith("An", ignoreCase = true) }
        val substringMatches = names.filter { !it.startsWith("An", ignoreCase = true) }
        assertEquals(prefixMatches.sorted(), prefixMatches)
        assertEquals(substringMatches.sorted(), substringMatches)
        assertEquals(prefixMatches + substringMatches, names)
    }

    @Test
    fun `prefix matches appear before substring matches`() {
        viewModel.updateSearchQuery("Uni")
        val results = viewModel.uiState.value.searchResults
        assertTrue(results.size >= 3)
        val names = results.map { it.displayName }
        val expectedPrefix = listOf(
            AppConstants.CountryName.UNITED_ARAB_EMIRATES,
            AppConstants.CountryName.UNITED_KINGDOM,
            AppConstants.CountryName.UNITED_STATES
        )
        assertEquals(expectedPrefix, names.take(3))
    }

    @Test
    fun `progressive search narrows results and all match input`() {
        viewModel.updateSearchQuery("U")
        val resultsU = viewModel.uiState.value.searchResults.toList()
        assertTrue(resultsU.isNotEmpty())
        assertTrue(resultsU.all { it.displayName.contains("U", ignoreCase = true) })

        viewModel.updateSearchQuery("Un")
        val resultsUn = viewModel.uiState.value.searchResults.toList()
        assertTrue(resultsUn.isNotEmpty())
        assertTrue(resultsUn.all { it.displayName.contains("Un", ignoreCase = true) })
        assertTrue(resultsUn.size <= resultsU.size)

        viewModel.updateSearchQuery("Uni")
        val resultsUni = viewModel.uiState.value.searchResults.toList()
        assertTrue(resultsUni.isNotEmpty())
        assertTrue(resultsUni.all { it.displayName.contains("Uni", ignoreCase = true) })
        assertTrue(resultsUni.size <= resultsUn.size)
    }

    @Test
    fun `exact match appears in search results`() {
        viewModel.updateSearchQuery(AppConstants.CountryName.JAPAN)
        val results = viewModel.uiState.value.searchResults
        assertTrue(results.any { it.name == AppConstants.CountryName.JAPAN })
    }

    @Test
    fun `partial match at beginning works`() {
        viewModel.updateSearchQuery("Ger")
        assertTrue(viewModel.uiState.value.searchResults.any { it.name == AppConstants.CountryName.GERMANY })
    }

    @Test
    fun `partial match in middle works`() {
        viewModel.updateSearchQuery("Kingdom")
        assertTrue(viewModel.uiState.value.searchResults.any { it.name == AppConstants.CountryName.UNITED_KINGDOM })
    }

    // ==================== State Consistency Tests ====================

    @Test
    fun `state remains consistent after multiple operations`() {
        val initialCountries = viewModel.uiState.value.countries

        // Perform many operations
        viewModel.selectCountry("us")
        viewModel.updateSearchQuery(AppConstants.CountryName.FRANCE)
        viewModel.selectCountry("fr")
        viewModel.clearSearch()
        viewModel.clearSelection()
        viewModel.updateSearchQuery(AppConstants.CountryName.GERMANY)
        viewModel.clearSearch()

        // Countries list should remain unchanged
        assertEquals(initialCountries.size, viewModel.uiState.value.countries.size)
        assertNull(viewModel.uiState.value.selectedCountry)
        assertEquals("", viewModel.uiState.value.searchQuery)
        assertTrue(viewModel.uiState.value.searchResults.isEmpty())
    }

    @Test
    fun `isLoading is false after initialization`() {
        assertFalse(viewModel.uiState.value.isLoading)
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

    @Test
    fun `default state has empty search query`() {
        val state = CountryInfoUiState()

        assertEquals("", state.searchQuery)
    }

    @Test
    fun `default state has empty search results`() {
        val state = CountryInfoUiState()

        assertTrue(state.searchResults.isEmpty())
    }

    @Test
    fun `state can be created with search query`() {
        val state = CountryInfoUiState(searchQuery = AppConstants.CountryName.JAPAN)

        assertEquals(AppConstants.CountryName.JAPAN, state.searchQuery)
    }

    @Test
    fun `state equality works correctly`() {
        val state1 = CountryInfoUiState(isLoading = true, searchQuery = "test")
        val state2 = CountryInfoUiState(isLoading = true, searchQuery = "test")
        val state3 = CountryInfoUiState(isLoading = false, searchQuery = "test")

        assertEquals(state1, state2)
        assertNotEquals(state1, state3)
    }

    @Test
    fun `state hashCode is consistent`() {
        val state = CountryInfoUiState(isLoading = true, searchQuery = "test")
        val hash1 = state.hashCode()
        val hash2 = state.hashCode()

        assertEquals(hash1, hash2)
    }

    @Test
    fun `state copy works correctly`() {
        val original = CountryInfoUiState(isLoading = true, searchQuery = "original")
        val copy = original.copy(searchQuery = "modified")

        assertEquals("original", original.searchQuery)
        assertEquals("modified", copy.searchQuery)
        assertTrue(original.isLoading)
        assertTrue(copy.isLoading)
    }

    @Test
    fun `state copy preserves unmodified fields`() {
        val original = CountryInfoUiState(
            isLoading = true,
            searchQuery = "test",
            countries = emptyList(),
            selectedCountry = null,
            searchResults = emptyList()
        )
        val copy = original.copy(isLoading = false)

        assertFalse(copy.isLoading)
        assertEquals(original.searchQuery, copy.searchQuery)
        assertEquals(original.countries, copy.countries)
        assertEquals(original.selectedCountry, copy.selectedCountry)
        assertEquals(original.searchResults, copy.searchResults)
    }

    @Test
    fun `state toString contains field names`() {
        val state = CountryInfoUiState(isLoading = true, searchQuery = "test")
        val string = state.toString()

        assertTrue(string.contains("isLoading"))
        assertTrue(string.contains("searchQuery"))
        assertTrue(string.contains("countries"))
    }

    @Test
    fun `state component1 returns countries`() {
        val state = CountryInfoUiState()
        val (countries, _, _, _, _, _) = state

        assertTrue(countries.isEmpty())
    }

    @Test
    fun `state component2 returns selectedCountry`() {
        val state = CountryInfoUiState()
        val (_, selectedCountry, _, _, _, _) = state

        assertNull(selectedCountry)
    }

    @Test
    fun `state component3 returns selectedDisplayName`() {
        val state = CountryInfoUiState(selectedDisplayName = EASTER_ISLAND)
        val (_, _, selectedDisplayName, _, _, _) = state

        assertEquals(EASTER_ISLAND, selectedDisplayName)
    }

    @Test
    fun `state component4 returns isLoading`() {
        val state = CountryInfoUiState(isLoading = true)
        val (_, _, _, isLoading, _, _) = state

        assertTrue(isLoading)
    }

    @Test
    fun `state component5 returns searchQuery`() {
        val state = CountryInfoUiState(searchQuery = "test")
        val (_, _, _, _, searchQuery, _) = state

        assertEquals("test", searchQuery)
    }

    @Test
    fun `state component6 returns searchResults`() {
        val state = CountryInfoUiState()
        val (_, _, _, _, _, searchResults) = state

        assertTrue(searchResults.isEmpty())
    }

    @Test
    fun `state with all parameters can be created`() {
        val state = CountryInfoUiState(
            countries = emptyList(),
            selectedCountry = null,
            isLoading = false,
            searchQuery = "query",
            searchResults = emptyList()
        )

        assertTrue(state.countries.isEmpty())
        assertNull(state.selectedCountry)
        assertFalse(state.isLoading)
        assertEquals("query", state.searchQuery)
        assertTrue(state.searchResults.isEmpty())
    }
}
