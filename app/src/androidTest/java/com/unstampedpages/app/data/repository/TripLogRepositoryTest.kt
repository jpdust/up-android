package com.unstampedpages.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.local.AppDatabase
import com.unstampedpages.app.data.local.entity.TripLogEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TripLogRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: TripLogRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = TripLogRepository(database.tripLogDao())
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertEntry_returnsId() = runTest {
        val entry = TripLogEntry(title = "Paris Day 1", content = "Visited Eiffel Tower")

        val id = repository.insertEntry(entry)

        assertTrue(id > 0)
    }

    @Test
    fun insertEntry_andGetById() = runTest {
        val entry = TripLogEntry(title = "Tokyo Trip", content = "Amazing sushi")
        val id = repository.insertEntry(entry)

        val retrieved = repository.getEntryById(id)

        assertNotNull(retrieved)
        assertEquals("Tokyo Trip", retrieved?.title)
        assertEquals("Amazing sushi", retrieved?.content)
    }

    @Test
    fun allEntries_emitsEntries() = runTest {
        repository.insertEntry(TripLogEntry(title = "Entry 1", content = "Content 1"))
        repository.insertEntry(TripLogEntry(title = "Entry 2", content = "Content 2"))

        val entries = repository.allEntries.first()

        assertEquals(2, entries.size)
    }

    @Test
    fun getEntriesByDateRange_filtersCorrectly() = runTest {
        repository.insertEntry(TripLogEntry(title = "Early", content = "...", date = 1000L))
        repository.insertEntry(TripLogEntry(title = "Middle", content = "...", date = 2000L))
        repository.insertEntry(TripLogEntry(title = "Late", content = "...", date = 3000L))

        val inRange = repository.getEntriesByDateRange(1500L, 2500L).first()

        assertEquals(1, inRange.size)
        assertEquals("Middle", inRange[0].title)
    }

    @Test
    fun updateEntry_modifiesContent() = runTest {
        val entry = TripLogEntry(title = "Original Title", content = "Original content")
        val id = repository.insertEntry(entry)
        val insertedEntry = repository.getEntryById(id)!!

        repository.updateEntry(insertedEntry.copy(title = "Updated Title", content = "Updated content"))

        val updated = repository.getEntryById(id)
        assertEquals("Updated Title", updated?.title)
        assertEquals("Updated content", updated?.content)
    }

    @Test
    fun updateEntry_updatesTimestamp() = runTest {
        val entry = TripLogEntry(title = "Test", content = "...", updatedAt = 1000L)
        val id = repository.insertEntry(entry)
        val insertedEntry = repository.getEntryById(id)!!

        Thread.sleep(10)
        repository.updateEntry(insertedEntry.copy(content = "Modified"))

        val updated = repository.getEntryById(id)
        assertTrue(updated!!.updatedAt > insertedEntry.updatedAt)
    }

    @Test
    fun deleteEntry_removesFromDatabase() = runTest {
        val entry = TripLogEntry(title = "To delete", content = "...")
        val id = repository.insertEntry(entry)
        val insertedEntry = repository.getEntryById(id)!!

        repository.deleteEntry(insertedEntry)

        val deleted = repository.getEntryById(id)
        assertNull(deleted)
    }

    @Test
    fun deleteAllEntries_clearsDatabase() = runTest {
        repository.insertEntry(TripLogEntry(title = "Entry 1", content = "..."))
        repository.insertEntry(TripLogEntry(title = "Entry 2", content = "..."))

        repository.deleteAllEntries()

        val entries = repository.allEntries.first()
        assertTrue(entries.isEmpty())
    }

    @Test
    fun getEntryById_returnsNullForNonexistent() = runTest {
        val entry = repository.getEntryById(99999L)

        assertNull(entry)
    }

    @Test
    fun insertEntry_withLocation() = runTest {
        val entry = TripLogEntry(
            title = "Beach Day",
            content = "Relaxing",
            location = "Malibu, CA"
        )
        val id = repository.insertEntry(entry)

        val retrieved = repository.getEntryById(id)

        assertEquals("Malibu, CA", retrieved?.location)
    }

    @Test
    fun allEntries_orderedByDateDescending() = runTest {
        repository.insertEntry(TripLogEntry(title = "Old", content = "...", date = 1000L))
        repository.insertEntry(TripLogEntry(title = "New", content = "...", date = 3000L))
        repository.insertEntry(TripLogEntry(title = "Mid", content = "...", date = 2000L))

        val entries = repository.allEntries.first()

        assertEquals("New", entries[0].title)
        assertEquals("Mid", entries[1].title)
        assertEquals("Old", entries[2].title)
    }
}
