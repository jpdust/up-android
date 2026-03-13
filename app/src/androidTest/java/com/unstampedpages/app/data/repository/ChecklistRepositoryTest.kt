package com.unstampedpages.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unstampedpages.app.data.local.AppDatabase
import com.unstampedpages.app.data.local.entity.ChecklistItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ChecklistRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: ChecklistRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = ChecklistRepository(database.checklistDao())
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertItem_returnsId() = runTest {
        val item = ChecklistItem(content = "Pack sunscreen")

        val id = repository.insertItem(item)

        assertTrue(id > 0)
    }

    @Test
    fun insertItem_andGetById() = runTest {
        val item = ChecklistItem(content = "Book flight")
        val id = repository.insertItem(item)

        val retrieved = repository.getItemById(id)

        assertNotNull(retrieved)
        assertEquals("Book flight", retrieved?.content)
    }

    @Test
    fun allItems_emitsItems() = runTest {
        repository.insertItem(ChecklistItem(content = "Item 1"))
        repository.insertItem(ChecklistItem(content = "Item 2"))

        val items = repository.allItems.first()

        assertEquals(2, items.size)
    }

    @Test
    fun insertItems_bulkInsert() = runTest {
        val items = listOf(
            ChecklistItem(content = "Item A"),
            ChecklistItem(content = "Item B"),
            ChecklistItem(content = "Item C")
        )

        repository.insertItems(items)

        val allItems = repository.allItems.first()
        assertEquals(3, allItems.size)
    }

    @Test
    fun updateItem_modifiesContent() = runTest {
        val item = ChecklistItem(content = "Original")
        val id = repository.insertItem(item)
        val insertedItem = repository.getItemById(id)!!

        repository.updateItem(insertedItem.copy(content = "Updated"))

        val updated = repository.getItemById(id)
        assertEquals("Updated", updated?.content)
    }

    @Test
    fun updateItem_updatesTimestamp() = runTest {
        val item = ChecklistItem(content = "Test", updatedAt = 1000L)
        val id = repository.insertItem(item)
        val insertedItem = repository.getItemById(id)!!

        Thread.sleep(10) // Ensure time passes
        repository.updateItem(insertedItem.copy(content = "Modified"))

        val updated = repository.getItemById(id)
        assertTrue(updated!!.updatedAt > insertedItem.updatedAt)
    }

    @Test
    fun deleteItem_removesFromDatabase() = runTest {
        val item = ChecklistItem(content = "To delete")
        val id = repository.insertItem(item)
        val insertedItem = repository.getItemById(id)!!

        repository.deleteItem(insertedItem)

        val deleted = repository.getItemById(id)
        assertNull(deleted)
    }

    @Test
    fun toggleItemChecked_changesStatus() = runTest {
        val item = ChecklistItem(content = "Toggle me", isChecked = false)
        val id = repository.insertItem(item)

        repository.toggleItemChecked(id, true)

        val toggled = repository.getItemById(id)
        assertTrue(toggled?.isChecked == true)
    }

    @Test
    fun toggleItemChecked_canToggleBackToFalse() = runTest {
        val item = ChecklistItem(content = "Toggle twice", isChecked = true)
        val id = repository.insertItem(item)

        repository.toggleItemChecked(id, false)

        val toggled = repository.getItemById(id)
        assertFalse(toggled?.isChecked == true)
    }

    @Test
    fun deleteAllItems_clearsDatabase() = runTest {
        repository.insertItem(ChecklistItem(content = "Item 1"))
        repository.insertItem(ChecklistItem(content = "Item 2"))
        repository.insertItem(ChecklistItem(content = "Item 3"))

        repository.deleteAllItems()

        val items = repository.allItems.first()
        assertTrue(items.isEmpty())
    }

    @Test
    fun getItemById_returnsNullForNonexistent() = runTest {
        val item = repository.getItemById(99999L)

        assertNull(item)
    }
}
