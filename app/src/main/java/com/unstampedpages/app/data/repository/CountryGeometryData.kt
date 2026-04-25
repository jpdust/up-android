package com.unstampedpages.app.data.repository

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.unstampedpages.app.R
import com.unstampedpages.app.data.model.CountryGeometry
import com.unstampedpages.app.util.GeoJsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Repository for country geometry data loaded from GeoJSON.
 * Initialization is done asynchronously on an IO thread to avoid blocking the main thread
 * while parsing the high-resolution Natural Earth 10m dataset.
 */
object CountryGeometryData {

    private var geometries: List<CountryGeometry> = emptyList()

    /** Compose-observable flag: true once geometry data has been loaded. */
    val isLoaded = mutableStateOf(false)

    /**
     * Asynchronously load geometry data on an IO thread.
     * Should be called once at app startup (e.g., from MainActivity.onCreate).
     * Safe to call multiple times; subsequent calls are no-ops.
     */
    fun initializeAsync(context: Context) {
        if (isLoaded.value) return
        CoroutineScope(Dispatchers.IO).launch {
            val loaded = GeoJsonParser.parseFromResource(
                context.applicationContext,
                R.raw.world_geo
            )
            withContext(Dispatchers.Main) {
                geometries = loaded
                isLoaded.value = true
            }
        }
    }

    /**
     * Synchronous initialization — kept for instrumented tests that run on Android.
     */
    fun initialize(context: Context) {
        if (isLoaded.value) return
        geometries = GeoJsonParser.parseFromResource(
            context.applicationContext,
            R.raw.world_geo
        )
        isLoaded.value = true
    }

    fun getAllGeometries(): List<CountryGeometry> = geometries

    fun getGeometryById(countryId: String): CountryGeometry? =
        geometries.find { it.countryId == countryId }

    fun isInitialized(): Boolean = isLoaded.value
}
