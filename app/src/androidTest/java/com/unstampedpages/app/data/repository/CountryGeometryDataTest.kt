package com.unstampedpages.app.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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

        assertTrue(CountryGeometryData.isInitialized())
    }

    @Test
    fun initialize_canBeCalledMultipleTimes() {
        CountryGeometryData.initialize(context)
        CountryGeometryData.initialize(context)

        assertTrue(CountryGeometryData.isInitialized())
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
        assertTrue("Should contain USA", countryIds.any { it.equals("USA", ignoreCase = true) || it.equals("US", ignoreCase = true) || it.equals("United States", ignoreCase = true) })
    }

    @Test
    fun getGeometryById_returnsGeometryForValidId() {
        CountryGeometryData.initialize(context)

        val geometries = CountryGeometryData.getAllGeometries()
        if (geometries.isNotEmpty()) {
            val firstId = geometries.first().countryId
            val geometry = CountryGeometryData.getGeometryById(firstId)

            assertNotNull(geometry)
            assertEquals(firstId, geometry?.countryId)
        }
    }

    @Test
    fun getGeometryById_returnsNullForInvalidId() {
        CountryGeometryData.initialize(context)

        val geometry = CountryGeometryData.getGeometryById("INVALID_COUNTRY_ID_12345")

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
}
