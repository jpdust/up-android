package com.unstampedpages.app.ui.screens.countryinfo

import androidx.lifecycle.ViewModel
import com.unstampedpages.app.data.model.Continent
import com.unstampedpages.app.data.model.Country
import com.unstampedpages.app.data.repository.CountryRepository
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A search result entry — either a direct country match or a named territory/dependency.
 *
 * For direct country matches: [displayName] == [country].name, [parentCountryName] == null.
 * For territory matches: [displayName] is the territory name, [parentCountryName] is the
 * sovereign country name shown as a subtitle.
 *
 * Convenience properties [name], [id], and [continent] delegate to the underlying country so
 * that existing call sites that treat search results like Country objects continue to work.
 */
data class SearchSuggestion(
    val country: Country,
    val displayName: String,
    val parentCountryName: String? = null
) {
    val name: String get() = displayName
    val id: String get() = country.id
    val continent: Continent get() = country.continent
}

data class CountryInfoUiState(
    val countries: List<Country> = emptyList(),
    val selectedCountry: Country? = null,
    val selectedDisplayName: String? = null,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<SearchSuggestion> = emptyList()
)

class CountryInfoViewModel : ViewModel() {

    private val repository = CountryRepository()

    // Starts with the JVM/system default; the composable pushes the true app locale
    // via updateLocale() on first composition and on every subsequent locale change.
    private var currentLocale: Locale = Locale.getDefault()

    private val _uiState = MutableStateFlow(CountryInfoUiState())
    val uiState: StateFlow<CountryInfoUiState> = _uiState.asStateFlow()

    init {
        loadCountries()
    }

    /**
     * Called by the composable whenever [LocalConfiguration.current.locales[0]] changes.
     * Uses the resource-configuration locale, which correctly reflects both system locale
     * changes and Android 13+ per-app language settings (unlike [Locale.getDefault]).
     */
    fun updateLocale(locale: Locale) {
        if (locale == currentLocale) return
        currentLocale = locale
        loadCountries()
    }

    private fun loadCountries() {
        _uiState.value = CountryInfoUiState(
            countries = repository.getAllCountries(currentLocale),
            isLoading = false
        )
    }

    fun selectCountry(countryId: String, displayName: String? = null) {
        val country = repository.getCountryById(countryId, currentLocale)
        _uiState.value = _uiState.value.copy(
            selectedCountry = country,
            selectedDisplayName = displayName
        )
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(
            selectedCountry = null,
            selectedDisplayName = null
        )
    }

    fun updateSearchQuery(query: String) {
        val locale = currentLocale
        val results = if (query.isBlank()) {
            emptyList()
        } else {
            val directMatches = repository.getAllCountries(locale)
                .filter { it.name.contains(query, ignoreCase = true) }
                .map { SearchSuggestion(country = it, displayName = it.name) }

            val territoryMatches = getCombinedTerritoryAliases(locale)
                .filter { (name, _) -> name.contains(query, ignoreCase = true) }
                .mapNotNull { (name, parentId) ->
                    repository.getCountryById(parentId, locale)?.let { parent ->
                        SearchSuggestion(
                            country = parent,
                            displayName = name,
                            parentCountryName = parent.name
                        )
                    }
                }

            (directMatches + territoryMatches)
                .distinctBy { it.displayName }
                .take(5)
        }
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            searchResults = results
        )
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            searchResults = emptyList()
        )
    }
}
