package com.unstampedpages.app.data.repository

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.unstampedpages.app.R
import com.unstampedpages.app.data.model.CountryGeometry
import com.unstampedpages.app.util.GeoJsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Repository for country geometry data loaded from GeoJSON.
 * Initialization is done asynchronously on an IO thread to avoid blocking the main thread
 * while parsing the high-resolution Natural Earth 10m dataset.
 *
 * Two resolutions are loaded and cached:
 * - **Hi-res (10m):** used for tap hit-testing (always) and rendering at scale ≥ [LOD_SCALE_THRESHOLD].
 * - **Lo-res (110m):** used for rendering at scale < [LOD_SCALE_THRESHOLD], where the 10m vertex
 *   count (~547k) is entirely sub-pixel and wastes GPU bandwidth.
 */
object CountryGeometryData {

    /**
     * Zoom scale at or above which the hi-res (10m) geometry is used for rendering.
     * Below this threshold the lo-res (110m) geometry is drawn instead.
     * Tap hit-testing always uses the hi-res dataset regardless of zoom level.
     */
    const val LOD_SCALE_THRESHOLD = 3f

    private var geometriesHiRes: List<CountryGeometry> = emptyList()
    private var geometriesLoRes: List<CountryGeometry> = emptyList()

    /** Compose-observable flag: true once both geometry datasets have been loaded. */
    val isLoaded = mutableStateOf(false)

    /**
     * Asynchronously load both geometry datasets on an IO thread.
     * Should be called once at app startup (e.g., from MainActivity.onCreate).
     * Safe to call multiple times; subsequent calls are no-ops.
     *
     * Each dataset is read from its binary cache if available, otherwise parsed from
     * the bundled GeoJSON and written to the cache for subsequent launches.
     */
    fun initializeAsync(context: Context) {
        if (isLoaded.value) return
        CoroutineScope(Dispatchers.IO).launch {
            val appContext = context.applicationContext

            // Parse both datasets concurrently.
            val hiResDeferred = async {
                GeometryBinaryCache.read(appContext)
                    ?: GeoJsonParser.parseFromResource(appContext, R.raw.world_geo)
                        .also { GeometryBinaryCache.write(appContext, it) }
            }
            val loResDeferred = async {
                GeometryBinaryCache.readLowRes(appContext)
                    ?: GeoJsonParser.parseFromResource(appContext, R.raw.world_geo_110m)
                        .also { GeometryBinaryCache.writeLowRes(appContext, it) }
            }

            val hiRes = hiResDeferred.await()
            val loRes = loResDeferred.await()

            withContext(Dispatchers.Main) {
                geometriesHiRes = hiRes
                geometriesLoRes = loRes
                isLoaded.value = true
            }
        }
    }

    /**
     * Synchronous initialization — kept for instrumented tests that run on Android.
     */
    fun initialize(context: Context) {
        if (isLoaded.value) return
        val appContext = context.applicationContext
        geometriesHiRes = GeoJsonParser.parseFromResource(appContext, R.raw.world_geo)
        geometriesLoRes = GeoJsonParser.parseFromResource(appContext, R.raw.world_geo_110m)
        isLoaded.value = true
    }

    /** Full-resolution (10m) geometry — use for tap hit-testing and high-zoom rendering. */
    fun getAllGeometries(): List<CountryGeometry> = geometriesHiRes

    /** Low-resolution (110m) geometry — use for rendering at scale < [LOD_SCALE_THRESHOLD]. */
    fun getLowResGeometries(): List<CountryGeometry> = geometriesLoRes

}
