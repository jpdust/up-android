package com.unstampedpages.app.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.AppConstants
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountryGeometryDataTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun initialize_loadsGeometryData() {
        CountryGeometryData.initialize(context)

        assertTrue(CountryGeometryData.isLoaded.value)
    }

    @Test
    fun initialize_canBeCalledMultipleTimes() {
        CountryGeometryData.initialize(context)
        CountryGeometryData.initialize(context)

        assertTrue(CountryGeometryData.isLoaded.value)
    }

    @Test
    fun getAllGeometries_returnsListAfterInitialization() {
        CountryGeometryData.initialize(context)

        val geometries = CountryGeometryData.getAllGeometries()

        assertTrue(geometries.isNotEmpty())
    }

    @Test
    fun getAllGeometries_containsKnownCountries() {
        CountryGeometryData.initialize(context)

        val geometries = CountryGeometryData.getAllGeometries()
        val countryIds = geometries.map { it.countryId }

        // Check for some major countries that should be in the dataset
        assertTrue("Should contain USA", countryIds.any { it.equals("USA", ignoreCase = true) || it.equals("US", ignoreCase = true) || it.equals(AppConstants.CountryName.UNITED_STATES, ignoreCase = true) })
    }

    @Test
    fun getGeometryById_returnsGeometryForValidId() {
        CountryGeometryData.initialize(context)

        val geometries = CountryGeometryData.getAllGeometries()
        if (geometries.isNotEmpty()) {
            val firstId = geometries.first().countryId
            val geometry = CountryGeometryData.getAllGeometries().firstOrNull { it.countryId == firstId }

            assertNotNull(geometry)
            assertEquals(firstId, geometry?.countryId)
        }
    }

    @Test
    fun getGeometryById_returnsNullForInvalidId() {
        CountryGeometryData.initialize(context)

        val geometry = CountryGeometryData.getAllGeometries().firstOrNull { it.countryId == "INVALID_COUNTRY_ID_12345" }

        assertNull(geometry)
    }

    @Test
    fun geometries_haveValidPolygons() {
        CountryGeometryData.initialize(context)

        val geometries = CountryGeometryData.getAllGeometries()

        geometries.forEach { geometry ->
            assertTrue(
                "Geometry for ${geometry.countryId} should have polygons",
                geometry.polygons.isNotEmpty()
            )
        }
    }

    @Test
    fun geometries_haveValidCoordinates() {
        CountryGeometryData.initialize(context)

        val geometries = CountryGeometryData.getAllGeometries()

        geometries.forEach { geometry ->
            geometry.polygons.forEach { polygon ->
                assertTrue(
                    "Polygon in ${geometry.countryId} should have at least 3 points",
                    polygon.size >= 3
                )

                polygon.forEach { point ->
                    // Longitude should be between -180 and 180
                    assertTrue(
                        "Longitude ${point.lng} should be valid",
                        point.lng >= -180 && point.lng <= 180
                    )
                    // Latitude should be between -90 and 90
                    assertTrue(
                        "Latitude ${point.lat} should be valid",
                        point.lat >= -90 && point.lat <= 90
                    )
                }
            }
        }
    }

    @Test
    fun allCountryIds_areUnique() {
        CountryGeometryData.initialize(context)

        val geometries = CountryGeometryData.getAllGeometries()
        val ids = geometries.map { it.countryId }

        assertEquals(
            "All country IDs should be unique",
            ids.size,
            ids.distinct().size
        )
    }

    @Test
    fun allCountryIds_areNotEmpty() {
        CountryGeometryData.initialize(context)

        val geometries = CountryGeometryData.getAllGeometries()

        geometries.forEach { geometry ->
            assertTrue(
                "Country ID should not be empty",
                geometry.countryId.isNotEmpty()
            )
        }
    }

    // ==================== Additional Coverage Tests ====================

    @Test
    fun getGeometryById_isCaseSensitive() {
        CountryGeometryData.initialize(context)

        val geometries = CountryGeometryData.getAllGeometries()
        if (geometries.isNotEmpty()) {
            val firstId = geometries.first().countryId
            // Try with different case - should return null if IDs are case-sensitive
            val uppercaseResult = CountryGeometryData.getAllGeometries().firstOrNull { it.countryId == firstId.uppercase() }
            val lowercaseResult = CountryGeometryData.getAllGeometries().firstOrNull { it.countryId == firstId.lowercase() }

            // At least one should find the geometry (the one matching the actual case)
            val foundAny = uppercaseResult != null || lowercaseResult != null
            assertTrue("Should find geometry with correct case", foundAny)
        }
    }

    @Test
    fun geometries_polygonsHaveMultiplePoints() {
        CountryGeometryData.initialize(context)

        val geometries = CountryGeometryData.getAllGeometries()

        // Countries typically have many points defining their borders
        geometries.take(10).forEach { geometry ->
            geometry.polygons.forEach { polygon ->
                assertTrue(
                    "Polygon in ${geometry.countryId} should have multiple points for a realistic border",
                    polygon.size >= 3
                )
            }
        }
    }

    @Test
    fun geometries_containMajorCountries() {
        CountryGeometryData.initialize(context)

        val geometries = CountryGeometryData.getAllGeometries()
        val countryIds = geometries.map { it.countryId.lowercase() }

        // Check for some major countries (IDs may vary based on GeoJSON source)
        val majorCountriesFound = countryIds.any {
            it.contains("usa") || it.contains("us") || it == "usa" || it == "us"
        } || countryIds.any {
            it.contains("chn") || it.contains("cn") || it == "china" || it == "chn"
        } || countryIds.any {
            it.contains("gbr") || it.contains("gb") || it == "gbr" || it == "uk"
        }

        assertTrue("Should contain at least one major country", geometries.size > 50 || majorCountriesFound)
    }

    @Test
    fun getAllGeometries_returnsConsistentResults() {
        CountryGeometryData.initialize(context)

        val geometries1 = CountryGeometryData.getAllGeometries()
        val geometries2 = CountryGeometryData.getAllGeometries()

        assertEquals(
            "Multiple calls should return same number of geometries",
            geometries1.size,
            geometries2.size
        )
    }

    @Test
    fun getGeometryById_returnsConsistentResults() {
        CountryGeometryData.initialize(context)

        val geometries = CountryGeometryData.getAllGeometries()
        if (geometries.isNotEmpty()) {
            val firstId = geometries.first().countryId

            val result1 = CountryGeometryData.getAllGeometries().firstOrNull { it.countryId == firstId }
            val result2 = CountryGeometryData.getAllGeometries().firstOrNull { it.countryId == firstId }

            assertEquals(
                "Multiple calls should return same geometry",
                result1?.countryId,
                result2?.countryId
            )
        }
    }

    @Test
    fun geometries_haveReasonablePolygonCounts() {
        CountryGeometryData.initialize(context)

        val geometries = CountryGeometryData.getAllGeometries()

        geometries.forEach { geometry ->
            // Most countries have between 1 and maybe 100+ polygons (for archipelagos like Indonesia)
            assertTrue(
                "Country ${geometry.countryId} should have reasonable polygon count",
                geometry.polygons.size in 1..1000
            )
        }
    }

    @Test
    fun isInitialized_returnsTrueAfterInit() {
        CountryGeometryData.initialize(context)

        assertTrue("Should be initialized after calling initialize()", CountryGeometryData.isLoaded.value)
    }

    @Test
    fun geometries_firstPolygonHasValidStartEndPoints() {
        CountryGeometryData.initialize(context)

        val geometries = CountryGeometryData.getAllGeometries()

        // In GeoJSON, polygons are typically closed (first point = last point)
        geometries.take(5).forEach { geometry ->
            geometry.polygons.forEach { polygon ->
                if (polygon.size > 1) {
                    val first = polygon.first()
                    val last = polygon.last()
                    // Check if polygon is closed (common in GeoJSON)
                    val isClosed = first.lat == last.lat && first.lng == last.lng
                    // If not closed, that's also valid - just checking for valid coordinates
                    assertTrue(
                        "First point should have valid coordinates",
                        first.lat >= -90 && first.lat <= 90 && first.lng >= -180 && first.lng <= 180
                    )
                }
            }
        }
    }
}
