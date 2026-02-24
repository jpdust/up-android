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
    val isLoading: Boolean = false
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
}
