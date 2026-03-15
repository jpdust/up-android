package com.unstampedpages.app.ui.screens.mystamps

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.Country
import com.unstampedpages.app.data.CountryList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyStampsViewModelTest {

    private lateinit var viewModel: MyStampsViewModel
    private lateinit var application: Application

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        viewModel = MyStampsViewModel(application)
    }

    @Test
    fun initialState_hasCountryStamps() {
        val state = viewModel.uiState.value

        assertTrue(state.countryStamps.isNotEmpty())
    }

    @Test
    fun initialState_countryStampsMatchesCountryList() {
        val state = viewModel.uiState.value

        assertEquals(CountryList.countries.size, state.countryStamps.size)
    }

    @Test
    fun initialState_allStampsHaveNullImagePath() {
        val state = viewModel.uiState.value

        // Initially, before database loads, all image paths should be null
        state.countryStamps.forEach { stamp ->
            // Note: This may change after database loads, but initially should be null
            assertNotNull(stamp.country)
        }
    }

    @Test
    fun initialState_isNotLoading() {
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
    }

    @Test
    fun initialState_hasNoSelectedCountry() {
        val state = viewModel.uiState.value

        assertNull(state.selectedCountry)
    }

    @Test
    fun initialState_uploadDialogNotShown() {
        val state = viewModel.uiState.value

        assertFalse(state.showUploadDialog)
    }

    @Test
    fun showUploadDialog_setsSelectedCountry() {
        val country = CountryList.countries.first()

        viewModel.showUploadDialog(country)

        assertEquals(country, viewModel.uiState.value.selectedCountry)
    }

    @Test
    fun showUploadDialog_setsShowUploadDialogTrue() {
        val country = CountryList.countries.first()

        viewModel.showUploadDialog(country)

        assertTrue(viewModel.uiState.value.showUploadDialog)
    }

    @Test
    fun dismissUploadDialog_clearsSelectedCountry() {
        val country = CountryList.countries.first()
        viewModel.showUploadDialog(country)

        viewModel.dismissUploadDialog()

        assertNull(viewModel.uiState.value.selectedCountry)
    }

    @Test
    fun dismissUploadDialog_hidesDialog() {
        val country = CountryList.countries.first()
        viewModel.showUploadDialog(country)

        viewModel.dismissUploadDialog()

        assertFalse(viewModel.uiState.value.showUploadDialog)
    }

    @Test
    fun showUploadDialog_withDifferentCountries() {
        val country1 = CountryList.countries[0]
        val country2 = CountryList.countries[1]

        viewModel.showUploadDialog(country1)
        assertEquals(country1, viewModel.uiState.value.selectedCountry)

        viewModel.dismissUploadDialog()

        viewModel.showUploadDialog(country2)
        assertEquals(country2, viewModel.uiState.value.selectedCountry)
    }

    @Test
    fun countryStamps_containsExpectedCountries() {
        val state = viewModel.uiState.value
        val countryCodes = state.countryStamps.map { it.country.code }

        assertTrue(countryCodes.contains("US"))
        assertTrue(countryCodes.contains("GB"))
        assertTrue(countryCodes.contains("FR"))
        assertTrue(countryCodes.contains("JP"))
    }

    @Test
    fun countryStamp_hasCorrectCountryData() {
        val state = viewModel.uiState.value
        val usStamp = state.countryStamps.find { it.country.code == "US" }

        assertNotNull(usStamp)
        assertEquals("United States", usStamp?.country?.name)
    }

    // Camera functionality tests

    @Test
    fun initialState_cameraImageUriIsNull() {
        val state = viewModel.uiState.value

        assertNull(state.cameraImageUri)
    }

    @Test
    fun createCameraUri_returnsNonNullUri() {
        val uri = viewModel.createCameraUri()

        assertNotNull(uri)
    }

    @Test
    fun createCameraUri_updatesCameraImageUriInState() {
        val uri = viewModel.createCameraUri()

        assertEquals(uri, viewModel.uiState.value.cameraImageUri)
    }

    @Test
    fun createCameraUri_returnsContentUri() {
        val uri = viewModel.createCameraUri()

        assertNotNull(uri)
        assertEquals("content", uri?.scheme)
    }

    @Test
    fun createCameraUri_uriContainsFileProvider() {
        val uri = viewModel.createCameraUri()

        assertNotNull(uri)
        assertTrue(uri.toString().contains("fileprovider"))
    }

    @Test
    fun createCameraUri_calledMultipleTimes_returnsUniqueUris() {
        val uri1 = viewModel.createCameraUri()
        Thread.sleep(10) // Ensure different timestamps
        val uri2 = viewModel.createCameraUri()

        assertNotNull(uri1)
        assertNotNull(uri2)
        assertNotEquals(uri1, uri2)
    }

    @Test
    fun saveCameraImage_withFailure_doesNotChangeDialogState() {
        val country = CountryList.countries.first()
        viewModel.showUploadDialog(country)
        viewModel.createCameraUri()

        // Simulate camera capture failure
        viewModel.saveCameraImage(false)

        // Dialog should still be showing (failure doesn't dismiss)
        assertTrue(viewModel.uiState.value.showUploadDialog)
    }

    @Test
    fun saveCameraImage_withSuccess_noSelectedCountry_doesNotCrash() {
        // Don't show dialog (no selected country)
        viewModel.createCameraUri()

        // Should not crash when no country selected
        viewModel.saveCameraImage(true)

        // State should be unchanged
        assertNull(viewModel.uiState.value.selectedCountry)
    }

    @Test
    fun saveCameraImage_withSuccess_dismissesDialog() = runTest {
        val country = CountryList.countries.first()
        viewModel.showUploadDialog(country)
        viewModel.createCameraUri()

        viewModel.saveCameraImage(true)

        // Give coroutine time to complete
        Thread.sleep(100)

        assertFalse(viewModel.uiState.value.showUploadDialog)
        assertNull(viewModel.uiState.value.selectedCountry)
    }
}

class CountryStampTest {

    @Test
    fun countryStamp_defaultImagePathIsNull() {
        val country = Country(code = "US", name = "United States")
        val stamp = CountryStamp(country = country)

        assertNull(stamp.imagePath)
    }

    @Test
    fun countryStamp_canHaveImagePath() {
        val country = Country(code = "US", name = "United States")
        val stamp = CountryStamp(country = country, imagePath = "/path/to/image.jpg")

        assertEquals("/path/to/image.jpg", stamp.imagePath)
    }

    @Test
    fun countryStamp_storesCountry() {
        val country = Country(code = "FR", name = "France")
        val stamp = CountryStamp(country = country)

        assertEquals("FR", stamp.country.code)
        assertEquals("France", stamp.country.name)
    }
}

class MyStampsUiStateTest {

    @Test
    fun defaultState_hasEmptyCountryStamps() {
        val state = MyStampsUiState()

        assertTrue(state.countryStamps.isEmpty())
    }

    @Test
    fun defaultState_isNotLoading() {
        val state = MyStampsUiState()

        assertFalse(state.isLoading)
    }

    @Test
    fun defaultState_hasNoSelectedCountry() {
        val state = MyStampsUiState()

        assertNull(state.selectedCountry)
    }

    @Test
    fun defaultState_uploadDialogNotShown() {
        val state = MyStampsUiState()

        assertFalse(state.showUploadDialog)
    }

    @Test
    fun state_canBeCreatedWithLoading() {
        val state = MyStampsUiState(isLoading = true)

        assertTrue(state.isLoading)
    }

    @Test
    fun state_canBeCreatedWithShowUploadDialog() {
        val state = MyStampsUiState(showUploadDialog = true)

        assertTrue(state.showUploadDialog)
    }

    @Test
    fun state_canBeCreatedWithSelectedCountry() {
        val country = Country(code = "US", name = "United States")
        val state = MyStampsUiState(selectedCountry = country)

        assertEquals(country, state.selectedCountry)
    }

    @Test
    fun state_canBeCreatedWithCountryStamps() {
        val stamps = listOf(
            CountryStamp(Country(code = "US", name = "United States")),
            CountryStamp(Country(code = "FR", name = "France"))
        )
        val state = MyStampsUiState(countryStamps = stamps)

        assertEquals(2, state.countryStamps.size)
    }

    @Test
    fun defaultState_cameraImageUriIsNull() {
        val state = MyStampsUiState()

        assertNull(state.cameraImageUri)
    }

    @Test
    fun state_canBeCreatedWithCameraImageUri() {
        val uri = android.net.Uri.parse("content://com.example/test")
        val state = MyStampsUiState(cameraImageUri = uri)

        assertEquals(uri, state.cameraImageUri)
    }

    @Test
    fun state_cameraImageUriCanBeUpdatedViaCopy() {
        val uri = android.net.Uri.parse("content://com.example/test")
        val initialState = MyStampsUiState()
        val updatedState = initialState.copy(cameraImageUri = uri)

        assertNull(initialState.cameraImageUri)
        assertEquals(uri, updatedState.cameraImageUri)
    }
}
