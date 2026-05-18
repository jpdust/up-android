package com.unstampedpages.app.ui.screens.mystamps

import com.unstampedpages.app.data.AppConstants
import com.unstampedpages.app.data.CountryListItem
import org.junit.Assert.*
import org.junit.Test

class MyStampsUiStateTest {

    @Test
    fun `default state has empty country stamps list`() {
        val state = MyStampsUiState()

        assertTrue(state.countryStamps.isEmpty())
    }

    @Test
    fun `default state is not loading`() {
        val state = MyStampsUiState()

        assertFalse(state.isLoading)
    }

    @Test
    fun `default state has no selected country`() {
        val state = MyStampsUiState()

        assertNull(state.selectedCountry)
    }

    @Test
    fun `default state does not show upload dialog`() {
        val state = MyStampsUiState()

        assertFalse(state.showUploadDialog)
    }

    @Test
    fun `default state has no camera image uri`() {
        val state = MyStampsUiState()

        assertNull(state.cameraImageUri)
    }

    @Test
    fun `state can be created with loading true`() {
        val state = MyStampsUiState(isLoading = true)

        assertTrue(state.isLoading)
    }

    @Test
    fun `state can be created with showUploadDialog true`() {
        val state = MyStampsUiState(showUploadDialog = true)

        assertTrue(state.showUploadDialog)
    }

    @Test
    fun `state can be created with selected country`() {
        val country = CountryListItem(code = "US", englishName = AppConstants.CountryName.UNITED_STATES)
        val state = MyStampsUiState(selectedCountry = country)

        assertNotNull(state.selectedCountry)
        assertEquals("US", state.selectedCountry?.code)
        assertEquals(AppConstants.CountryName.UNITED_STATES, state.selectedCountry?.englishName)
    }

    @Test
    fun `state can be created with country stamps`() {
        val country = CountryListItem(code = "JP", englishName = AppConstants.CountryName.JAPAN)
        val stamp = CountryStamp(country = country, imagePath = "/path/to/image.jpg")
        val state = MyStampsUiState(countryStamps = listOf(stamp))

        assertEquals(1, state.countryStamps.size)
        assertEquals(AppConstants.CountryName.JAPAN, state.countryStamps[0].country.englishName)
        assertEquals("/path/to/image.jpg", state.countryStamps[0].imagePath)
    }

    @Test
    fun `state copy preserves unchanged fields`() {
        val country = CountryListItem(code = "US", englishName = AppConstants.CountryName.UNITED_STATES)
        val stamp = CountryStamp(country = country)
        val originalState = MyStampsUiState(
            countryStamps = listOf(stamp),
            isLoading = false,
            selectedCountry = country
        )

        val newState = originalState.copy(showUploadDialog = true)

        assertEquals(originalState.countryStamps, newState.countryStamps)
        assertEquals(originalState.isLoading, newState.isLoading)
        assertEquals(originalState.selectedCountry, newState.selectedCountry)
        assertTrue(newState.showUploadDialog)
    }

    @Test
    fun `state copy can update multiple fields`() {
        val state = MyStampsUiState()
        val country = CountryListItem(code = "FR", englishName = AppConstants.CountryName.FRANCE)

        val newState = state.copy(
            selectedCountry = country,
            showUploadDialog = true,
            isLoading = true
        )

        assertEquals(country, newState.selectedCountry)
        assertTrue(newState.showUploadDialog)
        assertTrue(newState.isLoading)
    }

    @Test
    fun `state equality works correctly`() {
        val state1 = MyStampsUiState(isLoading = true)
        val state2 = MyStampsUiState(isLoading = true)
        val state3 = MyStampsUiState(isLoading = false)

        assertEquals(state1, state2)
        assertNotEquals(state1, state3)
    }
}

class CountryStampTest {

    @Test
    fun `country stamp can be created with country only`() {
        val country = CountryListItem(code = "US", englishName = AppConstants.CountryName.UNITED_STATES)
        val stamp = CountryStamp(country = country)

        assertEquals(country, stamp.country)
        assertNull(stamp.imagePath)
    }

    @Test
    fun `country stamp can be created with image path`() {
        val country = CountryListItem(code = "JP", englishName = AppConstants.CountryName.JAPAN)
        val stamp = CountryStamp(country = country, imagePath = "/path/to/stamp.jpg")

        assertEquals(country, stamp.country)
        assertEquals("/path/to/stamp.jpg", stamp.imagePath)
    }

    @Test
    fun `country stamp with null image path`() {
        val country = CountryListItem(code = "DE", englishName = AppConstants.CountryName.GERMANY)
        val stamp = CountryStamp(country = country, imagePath = null)

        assertNull(stamp.imagePath)
    }

    @Test
    fun `country stamp equality with same data`() {
        val country = CountryListItem(code = "FR", englishName = AppConstants.CountryName.FRANCE)
        val stamp1 = CountryStamp(country = country, imagePath = "/path/image.jpg")
        val stamp2 = CountryStamp(country = country, imagePath = "/path/image.jpg")

        assertEquals(stamp1, stamp2)
    }

    @Test
    fun `country stamp inequality with different image paths`() {
        val country = CountryListItem(code = "GB", englishName = AppConstants.CountryName.UNITED_KINGDOM)
        val stamp1 = CountryStamp(country = country, imagePath = "/path/image1.jpg")
        val stamp2 = CountryStamp(country = country, imagePath = "/path/image2.jpg")

        assertNotEquals(stamp1, stamp2)
    }

    @Test
    fun `country stamp inequality with different countries`() {
        val country1 = CountryListItem(code = "US", englishName = AppConstants.CountryName.UNITED_STATES)
        val country2 = CountryListItem(code = "CA", englishName = AppConstants.CountryName.CANADA)
        val stamp1 = CountryStamp(country = country1)
        val stamp2 = CountryStamp(country = country2)

        assertNotEquals(stamp1, stamp2)
    }

    @Test
    fun `country stamp copy works correctly`() {
        val country = CountryListItem(code = "AU", englishName = "Australia")
        val originalStamp = CountryStamp(country = country, imagePath = null)

        val updatedStamp = originalStamp.copy(imagePath = "/new/path.jpg")

        assertEquals(country, updatedStamp.country)
        assertEquals("/new/path.jpg", updatedStamp.imagePath)
        assertNull(originalStamp.imagePath) // Original unchanged
    }

    @Test
    fun `country stamp hashCode consistency`() {
        val country = CountryListItem(code = "MX", englishName = "Mexico")
        val stamp1 = CountryStamp(country = country, imagePath = "/path.jpg")
        val stamp2 = CountryStamp(country = country, imagePath = "/path.jpg")

        assertEquals(stamp1.hashCode(), stamp2.hashCode())
    }

    @Test
    fun `country stamp toString contains country info`() {
        val country = CountryListItem(code = "BR", englishName = "Brazil")
        val stamp = CountryStamp(country = country, imagePath = "/path.jpg")

        val stringRep = stamp.toString()

        assertTrue(stringRep.contains("Brazil") || stringRep.contains("BR"))
    }
}
