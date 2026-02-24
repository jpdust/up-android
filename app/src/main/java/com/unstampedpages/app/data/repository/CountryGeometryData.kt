package com.unstampedpages.app.data.repository

import android.content.Context
import com.unstampedpages.app.R
import com.unstampedpages.app.data.model.CountryGeometry
import com.unstampedpages.app.util.GeoJsonParser

/**
 * Repository for country geometry data loaded from GeoJSON.
 * Provides accurate country borders for the world map.
 */
object CountryGeometryData {

    private var geometries: List<CountryGeometry>? = null

    /**
     * Initialize with application context to load GeoJSON data.
     * Should be called once at app startup.
     */
    fun initialize(context: Context) {
        if (geometries == null) {
            geometries = GeoJsonParser.parseFromResource(
                context.applicationContext,
                R.raw.world_geo
            )
        }
    }

    /**
     * Get all country geometries.
     * Returns empty list if not initialized.
     */
    fun getAllGeometries(): List<CountryGeometry> {
        return geometries ?: emptyList()
    }

    /**
     * Get geometry for a specific country by ID.
     */
    fun getGeometryById(countryId: String): CountryGeometry? {
        return geometries?.find { it.countryId == countryId }
    }

    /**
     * Check if data has been loaded.
     */
    fun isInitialized(): Boolean = geometries != null
}
