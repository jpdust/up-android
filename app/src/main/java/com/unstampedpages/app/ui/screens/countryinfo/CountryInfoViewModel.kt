package com.unstampedpages.app.ui.screens.countryinfo

import androidx.lifecycle.ViewModel
import com.unstampedpages.app.data.model.Country
import com.unstampedpages.app.data.repository.CountryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CountryInfoUiState(
    val countries: List<Country> = emptyList(),
    val selectedCountry: Country? = null,
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<Country> = emptyList()
)

class CountryInfoViewModel : ViewModel() {

    private val repository = CountryRepository()

    private val _uiState = MutableStateFlow(CountryInfoUiState())
    val uiState: StateFlow<CountryInfoUiState> = _uiState.asStateFlow()

    init {
        loadCountries()
    }

    private fun loadCountries() {
        _uiState.value = CountryInfoUiState(
            countries = repository.getAllCountries(),
            isLoading = false
        )
    }

    fun selectCountry(countryId: String) {
        val country = repository.getCountryById(countryId)
        _uiState.value = _uiState.value.copy(selectedCountry = country)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedCountry = null)
    }

    fun updateSearchQuery(query: String) {
        val results = if (query.isBlank()) {
            emptyList()
        } else {
            repository.getAllCountries().filter { country ->
                country.name.contains(query, ignoreCase = true)
            }.take(5) // Limit to 5 suggestions
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
