package com.unstampedpages.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.AppConstants
import com.unstampedpages.app.data.local.AppDatabase
import com.unstampedpages.app.data.local.entity.StampItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class StampRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: StampRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = StampRepository(database.stampDao())
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertStamp_returnsRowId() = runTest {
        val stamp = StampItem(countryCode = "US", countryName = AppConstants.CountryName.UNITED_STATES)

        val rowId = repository.insertStamp(stamp)

        assertTrue(rowId > 0)
    }

    @Test
    fun insertStamp_andGetByCountryCode() = runTest {
        val stamp = StampItem(countryCode = "FR", countryName = AppConstants.CountryName.FRANCE)
        repository.insertStamp(stamp)

        val retrieved = repository.getStampByCountryCode("FR")

        assertNotNull(retrieved)
        assertEquals(AppConstants.CountryName.FRANCE, retrieved?.countryName)
    }

    @Test
    fun allStamps_emitsStamps() = runTest {
        repository.insertStamp(StampItem(countryCode = "US", countryName = AppConstants.CountryName.UNITED_STATES))
        repository.insertStamp(StampItem(countryCode = "FR", countryName = AppConstants.CountryName.FRANCE))

        val stamps = repository.allStamps.first()

        assertEquals(2, stamps.size)
    }

    @Test
    fun allStamps_orderedAlphabetically() = runTest {
        repository.insertStamp(StampItem(countryCode = "US", countryName = AppConstants.CountryName.UNITED_STATES))
        repository.insertStamp(StampItem(countryCode = "AU", countryName = "Australia"))
        repository.insertStamp(StampItem(countryCode = "JP", countryName = AppConstants.CountryName.JAPAN))

        val stamps = repository.allStamps.first()

        assertEquals("Australia", stamps[0].countryName)
        assertEquals(AppConstants.CountryName.JAPAN, stamps[1].countryName)
        assertEquals(AppConstants.CountryName.UNITED_STATES, stamps[2].countryName)
    }

    @Test
    fun updateStamp_modifiesData() = runTest {
        val stamp = StampItem(countryCode = "DE", countryName = AppConstants.CountryName.GERMANY)
        repository.insertStamp(stamp)
        val insertedStamp = repository.getStampByCountryCode("DE")!!

        repository.updateStamp(insertedStamp.copy(imagePath = "/path/to/image.jpg"))

        val updated = repository.getStampByCountryCode("DE")
        assertEquals("/path/to/image.jpg", updated?.imagePath)
    }

    @Test
    fun updateStamp_updatesTimestamp() = runTest {
        val stamp = StampItem(countryCode = "IT", countryName = "Italy", updatedAt = 1000L)
        repository.insertStamp(stamp)
        val insertedStamp = repository.getStampByCountryCode("IT")!!

        Thread.sleep(10)
        repository.updateStamp(insertedStamp.copy(imagePath = "/new/path.jpg"))

        val updated = repository.getStampByCountryCode("IT")
        assertTrue(updated!!.updatedAt > insertedStamp.updatedAt)
    }

    @Test
    fun updateStampImage_changesImagePath() = runTest {
        val stamp = StampItem(countryCode = "ES", countryName = "Spain")
        repository.insertStamp(stamp)

        repository.updateStampImage("ES", "/stamps/spain.png")

        val updated = repository.getStampByCountryCode("ES")
        assertEquals("/stamps/spain.png", updated?.imagePath)
    }

    @Test
    fun updateStampImage_canSetToNull() = runTest {
        val stamp = StampItem(countryCode = "PT", countryName = "Portugal", imagePath = "/old/path.jpg")
        repository.insertStamp(stamp)

        repository.updateStampImage("PT", null)

        val updated = repository.getStampByCountryCode("PT")
        assertNull(updated?.imagePath)
    }

    @Test
    fun deleteStamp_removesFromDatabase() = runTest {
        val stamp = StampItem(countryCode = "GB", countryName = AppConstants.CountryName.UNITED_KINGDOM)
        repository.insertStamp(stamp)
        val insertedStamp = repository.getStampByCountryCode("GB")!!

        repository.deleteStamp(insertedStamp)

        val deleted = repository.getStampByCountryCode("GB")
        assertNull(deleted)
    }

    @Test
    fun deleteStampByCountryCode_removesFromDatabase() = runTest {
        val stamp = StampItem(countryCode = "CA", countryName = AppConstants.CountryName.CANADA)
        repository.insertStamp(stamp)

        repository.deleteStampByCountryCode("CA")

        val deleted = repository.getStampByCountryCode("CA")
        assertNull(deleted)
    }

    @Test
    fun getStampByCountryCode_returnsNullForNonexistent() = runTest {
        val stamp = repository.getStampByCountryCode("XX")

        assertNull(stamp)
    }

    @Test
    fun insertStamp_replacesOnConflict() = runTest {
        repository.insertStamp(StampItem(countryCode = "MX", countryName = "Mexico"))
        repository.insertStamp(StampItem(countryCode = "MX", countryName = "Mexico Updated", imagePath = "/new.jpg"))

        val stamp = repository.getStampByCountryCode("MX")

        assertEquals("Mexico Updated", stamp?.countryName)
        assertEquals("/new.jpg", stamp?.imagePath)
    }

    @Test
    fun deleteStampByCountryCode_doesNotThrowForNonexistent() = runTest {
        // Should not throw
        repository.deleteStampByCountryCode("NONEXISTENT")

        // Verify no stamps were affected
        val stamps = repository.allStamps.first()
        assertTrue(stamps.isEmpty())
    }
}
