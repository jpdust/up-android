package com.unstampedpages.app.ui.screens.mystamps

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unstampedpages.app.data.Country
import com.unstampedpages.app.data.CountryList
import com.unstampedpages.app.data.local.AppDatabase
import com.unstampedpages.app.data.local.entity.StampItem
import com.unstampedpages.app.data.repository.StampRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class CountryStamp(
    val country: Country,
    val imagePath: String? = null
)

data class MyStampsUiState(
    val countryStamps: List<CountryStamp> = emptyList(),
    val isLoading: Boolean = false,
    val selectedCountry: Country? = null,
    val showUploadDialog: Boolean = false
)

class MyStampsViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = application.applicationContext
    private var repository: StampRepository? = null

    private val _uiState = MutableStateFlow(MyStampsUiState())
    val uiState: StateFlow<MyStampsUiState> = _uiState.asStateFlow()

    init {
        // Initialize with countries immediately (no database)
        val initialCountryStamps = CountryList.countries.map { country ->
            CountryStamp(country = country, imagePath = null)
        }
        _uiState.value = MyStampsUiState(countryStamps = initialCountryStamps)

        // Then try to load from database
        viewModelScope.launch {
            try {
                val database = AppDatabase.getDatabase(application)
                repository = StampRepository(database.stampDao())

                repository?.allStamps
                    ?.catch { e ->
                        Log.e("MyStampsViewModel", "Error loading stamps", e)
                    }
                    ?.collect { stamps ->
                        val stampMap = stamps.associateBy { it.countryCode }
                        val countryStamps = CountryList.countries.map { country ->
                            CountryStamp(
                                country = country,
                                imagePath = stampMap[country.code]?.imagePath
                            )
                        }
                        _uiState.value = _uiState.value.copy(countryStamps = countryStamps)
                    }
            } catch (e: Exception) {
                Log.e("MyStampsViewModel", "Error initializing database", e)
            }
        }
    }

    fun showUploadDialog(country: Country) {
        _uiState.value = _uiState.value.copy(
            selectedCountry = country,
            showUploadDialog = true
        )
    }

    fun dismissUploadDialog() {
        _uiState.value = _uiState.value.copy(
            selectedCountry = null,
            showUploadDialog = false
        )
    }

    fun saveStampImage(uri: Uri) {
        val country = _uiState.value.selectedCountry ?: return

        viewModelScope.launch {
            try {
                val imagePath = copyImageToLocalStorage(uri, country.code)
                if (imagePath != null) {
                    val stamp = StampItem(
                        countryCode = country.code,
                        countryName = country.name,
                        imagePath = imagePath
                    )
                    repository?.insertStamp(stamp)
                }
            } catch (e: Exception) {
                Log.e("MyStampsViewModel", "Error saving stamp", e)
            } finally {
                dismissUploadDialog()
            }
        }
    }

    fun removeStamp(countryCode: String) {
        viewModelScope.launch {
            try {
                val stamp = repository?.getStampByCountryCode(countryCode)
                if (stamp != null) {
                    stamp.imagePath?.let { path ->
                        val file = File(path)
                        if (file.exists()) {
                            file.delete()
                        }
                    }
                    repository?.deleteStampByCountryCode(countryCode)
                }
            } catch (e: Exception) {
                Log.e("MyStampsViewModel", "Error removing stamp", e)
            }
        }
    }

    private fun copyImageToLocalStorage(uri: Uri, countryCode: String): String? {
        return try {
            val upImagesDir = File(context.filesDir, "upimages")
            if (!upImagesDir.exists()) {
                upImagesDir.mkdirs()
            }

            val fileName = "stamp_${countryCode}_${System.currentTimeMillis()}.jpg"
            val destFile = File(upImagesDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            destFile.absolutePath
        } catch (e: Exception) {
            Log.e("MyStampsViewModel", "Error copying image", e)
            null
        }
    }
}
