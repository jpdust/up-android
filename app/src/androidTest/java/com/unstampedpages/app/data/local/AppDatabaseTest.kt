package com.unstampedpages.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.local.dao.ChecklistDao
import com.unstampedpages.app.data.local.dao.StampDao
import com.unstampedpages.app.data.local.dao.TripLogDao
import com.unstampedpages.app.data.local.entity.ChecklistItem
import com.unstampedpages.app.data.local.entity.StampItem
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
class AppDatabaseTest {

    private lateinit var database: AppDatabase
    private lateinit var checklistDao: ChecklistDao
    private lateinit var tripLogDao: TripLogDao
    private lateinit var stampDao: StampDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        checklistDao = database.checklistDao()
        tripLogDao = database.tripLogDao()
        stampDao = database.stampDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    // ==================== Database Tests ====================

    @Test
    fun database_providesChecklistDao() {
        assertNotNull(database.checklistDao())
    }

    @Test
    fun database_providesTripLogDao() {
        assertNotNull(database.tripLogDao())
    }

    @Test
    fun database_providesStampDao() {
        assertNotNull(database.stampDao())
    }

    @Test
    fun database_getDatabase_returnsSameInstance() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val instance1 = AppDatabase.getDatabase(context)
        val instance2 = AppDatabase.getDatabase(context)
        assertSame(instance1, instance2)
    }

    // ==================== ChecklistDao Tests ====================

    @Test
    fun checklistDao_insertAndGetItem() = runTest {
        val item = ChecklistItem(content = "Pack passport")
        val id = checklistDao.insertItem(item)

        val retrieved = checklistDao.getItemById(id)
        assertNotNull(retrieved)
        assertEquals("Pack passport", retrieved?.content)
        assertFalse(retrieved?.isChecked ?: true)
    }

    @Test
    fun checklistDao_getAllItems_returnsItemsInDescendingOrder() = runTest {
        val item1 = ChecklistItem(content = "Item 1", createdAt = 1000L)
        val item2 = ChecklistItem(content = "Item 2", createdAt = 2000L)
        val item3 = ChecklistItem(content = "Item 3", createdAt = 3000L)

        checklistDao.insertItem(item1)
        checklistDao.insertItem(item2)
        checklistDao.insertItem(item3)

        val items = checklistDao.getAllItems().first()
        assertEquals(3, items.size)
        assertEquals("Item 3", items[0].content)
        assertEquals("Item 2", items[1].content)
        assertEquals("Item 1", items[2].content)
    }

    @Test
    fun checklistDao_insertItems_bulkInsert() = runTest {
        val items = listOf(
            ChecklistItem(content = "Item 1"),
            ChecklistItem(content = "Item 2"),
            ChecklistItem(content = "Item 3")
        )
        checklistDao.insertItems(items)

        val allItems = checklistDao.getAllItems().first()
        assertEquals(3, allItems.size)
    }

    @Test
    fun checklistDao_updateItem() = runTest {
        val item = ChecklistItem(content = "Original content")
        val id = checklistDao.insertItem(item)

        val updatedItem = item.copy(id = id, content = "Updated content", isChecked = true)
        checklistDao.updateItem(updatedItem)

        val retrieved = checklistDao.getItemById(id)
        assertEquals("Updated content", retrieved?.content)
        assertTrue(retrieved?.isChecked ?: false)
    }

    @Test
    fun checklistDao_deleteItem() = runTest {
        val item = ChecklistItem(content = "To be deleted")
        val id = checklistDao.insertItem(item)

        val insertedItem = checklistDao.getItemById(id)
        assertNotNull(insertedItem)

        checklistDao.deleteItem(insertedItem!!)

        val deleted = checklistDao.getItemById(id)
        assertNull(deleted)
    }

    @Test
    fun checklistDao_deleteAllItems() = runTest {
        checklistDao.insertItems(listOf(
            ChecklistItem(content = "Item 1"),
            ChecklistItem(content = "Item 2"),
            ChecklistItem(content = "Item 3")
        ))

        var items = checklistDao.getAllItems().first()
        assertEquals(3, items.size)

        checklistDao.deleteAllItems()

        items = checklistDao.getAllItems().first()
        assertTrue(items.isEmpty())
    }

    @Test
    fun checklistDao_updateCheckedStatus() = runTest {
        val item = ChecklistItem(content = "Task", isChecked = false)
        val id = checklistDao.insertItem(item)

        checklistDao.updateCheckedStatus(id, true)

        val retrieved = checklistDao.getItemById(id)
        assertTrue(retrieved?.isChecked ?: false)
    }

    @Test
    fun checklistDao_getItemById_returnsNullForNonexistent() = runTest {
        val item = checklistDao.getItemById(99999L)
        assertNull(item)
    }

    @Test
    fun checklistDao_insertWithConflictReplace() = runTest {
        val item = ChecklistItem(id = 1, content = "Original")
        checklistDao.insertItem(item)

        val replacementItem = ChecklistItem(id = 1, content = "Replaced")
        checklistDao.insertItem(replacementItem)

        val retrieved = checklistDao.getItemById(1)
        assertEquals("Replaced", retrieved?.content)
    }

    // ==================== TripLogDao Tests ====================

    @Test
    fun tripLogDao_insertAndGetEntry() = runTest {
        val entry = TripLogEntry(title = "Paris Trip", content = "Amazing trip to Paris")
        val id = tripLogDao.insertEntry(entry)

        val retrieved = tripLogDao.getEntryById(id)
        assertNotNull(retrieved)
        assertEquals("Paris Trip", retrieved?.title)
        assertEquals("Amazing trip to Paris", retrieved?.content)
    }

    @Test
    fun tripLogDao_getAllEntries_returnsEntriesInDescendingDateOrder() = runTest {
        val entry1 = TripLogEntry(title = "Entry 1", content = "Content 1", date = 1000L)
        val entry2 = TripLogEntry(title = "Entry 2", content = "Content 2", date = 2000L)
        val entry3 = TripLogEntry(title = "Entry 3", content = "Content 3", date = 3000L)

        tripLogDao.insertEntry(entry1)
        tripLogDao.insertEntry(entry2)
        tripLogDao.insertEntry(entry3)

        val entries = tripLogDao.getAllEntries().first()
        assertEquals(3, entries.size)
        assertEquals("Entry 3", entries[0].title)
        assertEquals("Entry 2", entries[1].title)
        assertEquals("Entry 1", entries[2].title)
    }

    @Test
    fun tripLogDao_getEntriesByDateRange() = runTest {
        val entry1 = TripLogEntry(title = "Entry 1", content = "Content 1", date = 1000L)
        val entry2 = TripLogEntry(title = "Entry 2", content = "Content 2", date = 2000L)
        val entry3 = TripLogEntry(title = "Entry 3", content = "Content 3", date = 3000L)
        val entry4 = TripLogEntry(title = "Entry 4", content = "Content 4", date = 4000L)

        tripLogDao.insertEntry(entry1)
        tripLogDao.insertEntry(entry2)
        tripLogDao.insertEntry(entry3)
        tripLogDao.insertEntry(entry4)

        val entriesInRange = tripLogDao.getEntriesByDateRange(1500L, 3500L).first()
        assertEquals(2, entriesInRange.size)
        assertEquals("Entry 3", entriesInRange[0].title)
        assertEquals("Entry 2", entriesInRange[1].title)
    }

    @Test
    fun tripLogDao_updateEntry() = runTest {
        val entry = TripLogEntry(title = "Original", content = "Original content")
        val id = tripLogDao.insertEntry(entry)

        val updatedEntry = entry.copy(id = id, title = "Updated", content = "Updated content")
        tripLogDao.updateEntry(updatedEntry)

        val retrieved = tripLogDao.getEntryById(id)
        assertEquals("Updated", retrieved?.title)
        assertEquals("Updated content", retrieved?.content)
    }

    @Test
    fun tripLogDao_deleteEntry() = runTest {
        val entry = TripLogEntry(title = "To delete", content = "Content")
        val id = tripLogDao.insertEntry(entry)

        val insertedEntry = tripLogDao.getEntryById(id)
        assertNotNull(insertedEntry)

        tripLogDao.deleteEntry(insertedEntry!!)

        val deleted = tripLogDao.getEntryById(id)
        assertNull(deleted)
    }

    @Test
    fun tripLogDao_deleteAllEntries() = runTest {
        tripLogDao.insertEntry(TripLogEntry(title = "Entry 1", content = "Content 1"))
        tripLogDao.insertEntry(TripLogEntry(title = "Entry 2", content = "Content 2"))

        var entries = tripLogDao.getAllEntries().first()
        assertEquals(2, entries.size)

        tripLogDao.deleteAllEntries()

        entries = tripLogDao.getAllEntries().first()
        assertTrue(entries.isEmpty())
    }

    @Test
    fun tripLogDao_getEntryById_returnsNullForNonexistent() = runTest {
        val entry = tripLogDao.getEntryById(99999L)
        assertNull(entry)
    }

    @Test
    fun tripLogDao_entryWithLocation() = runTest {
        val entry = TripLogEntry(
            title = "Beach Trip",
            content = "Relaxing day",
            location = "Malibu, CA"
        )
        val id = tripLogDao.insertEntry(entry)

        val retrieved = tripLogDao.getEntryById(id)
        assertEquals("Malibu, CA", retrieved?.location)
    }

    // ==================== StampDao Tests ====================

    @Test
    fun stampDao_insertAndGetStamp() = runTest {
        val stamp = StampItem(countryCode = "US", countryName = "United States")
        stampDao.insertStamp(stamp)

        val retrieved = stampDao.getStampByCountryCode("US")
        assertNotNull(retrieved)
        assertEquals("United States", retrieved?.countryName)
    }

    @Test
    fun stampDao_getAllStamps_returnsStampsInAlphabeticalOrder() = runTest {
        val stamp1 = StampItem(countryCode = "US", countryName = "United States")
        val stamp2 = StampItem(countryCode = "FR", countryName = "France")
        val stamp3 = StampItem(countryCode = "JP", countryName = "Japan")

        stampDao.insertStamp(stamp1)
        stampDao.insertStamp(stamp2)
        stampDao.insertStamp(stamp3)

        val stamps = stampDao.getAllStamps().first()
        assertEquals(3, stamps.size)
        assertEquals("France", stamps[0].countryName)
        assertEquals("Japan", stamps[1].countryName)
        assertEquals("United States", stamps[2].countryName)
    }

    @Test
    fun stampDao_updateStamp() = runTest {
        val stamp = StampItem(countryCode = "US", countryName = "United States")
        stampDao.insertStamp(stamp)

        val updatedStamp = stamp.copy(imagePath = "/path/to/image.jpg")
        stampDao.updateStamp(updatedStamp)

        val retrieved = stampDao.getStampByCountryCode("US")
        assertEquals("/path/to/image.jpg", retrieved?.imagePath)
    }

    @Test
    fun stampDao_deleteStamp() = runTest {
        val stamp = StampItem(countryCode = "US", countryName = "United States")
        stampDao.insertStamp(stamp)

        val insertedStamp = stampDao.getStampByCountryCode("US")
        assertNotNull(insertedStamp)

        stampDao.deleteStamp(insertedStamp!!)

        val deleted = stampDao.getStampByCountryCode("US")
        assertNull(deleted)
    }

    @Test
    fun stampDao_deleteStampByCountryCode() = runTest {
        val stamp = StampItem(countryCode = "US", countryName = "United States")
        stampDao.insertStamp(stamp)

        stampDao.deleteStampByCountryCode("US")

        val deleted = stampDao.getStampByCountryCode("US")
        assertNull(deleted)
    }

    @Test
    fun stampDao_updateStampImage() = runTest {
        val stamp = StampItem(countryCode = "US", countryName = "United States")
        stampDao.insertStamp(stamp)

        stampDao.updateStampImage("US", "/new/path/image.png")

        val retrieved = stampDao.getStampByCountryCode("US")
        assertEquals("/new/path/image.png", retrieved?.imagePath)
    }

    @Test
    fun stampDao_updateStampImage_toNull() = runTest {
        val stamp = StampItem(countryCode = "US", countryName = "United States", imagePath = "/path/image.jpg")
        stampDao.insertStamp(stamp)

        stampDao.updateStampImage("US", null)

        val retrieved = stampDao.getStampByCountryCode("US")
        assertNull(retrieved?.imagePath)
    }

    @Test
    fun stampDao_getStampByCountryCode_returnsNullForNonexistent() = runTest {
        val stamp = stampDao.getStampByCountryCode("XX")
        assertNull(stamp)
    }

    @Test
    fun stampDao_insertWithConflictReplace() = runTest {
        val stamp = StampItem(countryCode = "US", countryName = "United States")
        stampDao.insertStamp(stamp)

        val replacementStamp = StampItem(countryCode = "US", countryName = "USA", imagePath = "/image.jpg")
        stampDao.insertStamp(replacementStamp)

        val retrieved = stampDao.getStampByCountryCode("US")
        assertEquals("USA", retrieved?.countryName)
        assertEquals("/image.jpg", retrieved?.imagePath)
    }

    @Test
    fun stampDao_deleteStampByCountryCode_nonexistentDoesNotThrow() = runTest {
        // Should not throw even if country code doesn't exist
        stampDao.deleteStampByCountryCode("NONEXISTENT")
        // If we reach here, the test passes
        assertTrue(true)
    }
}
