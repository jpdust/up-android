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

    // ==================== Additional Coverage Tests ====================

    @Test
    fun allItemsGrouped_returnsPinnedItemsFirst() = runTest {
        repository.insertItem(ChecklistItem(content = "Unpinned", isPinned = false))
        repository.insertItem(ChecklistItem(content = "Pinned", isPinned = true))
        repository.insertItem(ChecklistItem(content = "Also Unpinned", isPinned = false))

        val items = repository.allItemsGrouped.first()

        assertEquals("Pinned", items[0].content)
    }

    @Test
    fun allItemsGrouped_sortsUncheckedBeforeChecked() = runTest {
        repository.insertItem(ChecklistItem(content = "Checked", isChecked = true, isPinned = false))
        repository.insertItem(ChecklistItem(content = "Unchecked", isChecked = false, isPinned = false))

        val items = repository.allItemsGrouped.first()

        assertFalse(items[0].isChecked)
        assertTrue(items[1].isChecked)
    }

    @Test
    fun totalCount_returnsCorrectCount() = runTest {
        repository.insertItem(ChecklistItem(content = "Item 1"))
        repository.insertItem(ChecklistItem(content = "Item 2"))
        repository.insertItem(ChecklistItem(content = "Item 3"))

        val count = repository.totalCount.first()

        assertEquals(3, count)
    }

    @Test
    fun totalCount_returnsZeroWhenEmpty() = runTest {
        val count = repository.totalCount.first()

        assertEquals(0, count)
    }

    @Test
    fun checkedCount_returnsCorrectCount() = runTest {
        repository.insertItem(ChecklistItem(content = "Checked 1", isChecked = true))
        repository.insertItem(ChecklistItem(content = "Checked 2", isChecked = true))
        repository.insertItem(ChecklistItem(content = "Unchecked", isChecked = false))

        val count = repository.checkedCount.first()

        assertEquals(2, count)
    }

    @Test
    fun checkedCount_returnsZeroWhenNoneChecked() = runTest {
        repository.insertItem(ChecklistItem(content = "Unchecked 1", isChecked = false))
        repository.insertItem(ChecklistItem(content = "Unchecked 2", isChecked = false))

        val count = repository.checkedCount.first()

        assertEquals(0, count)
    }

    @Test
    fun deleteItemsByIds_removesMultipleItems() = runTest {
        val id1 = repository.insertItem(ChecklistItem(content = "Item 1"))
        val id2 = repository.insertItem(ChecklistItem(content = "Item 2"))
        repository.insertItem(ChecklistItem(content = "Item 3"))

        repository.deleteItemsByIds(listOf(id1, id2))

        val items = repository.allItems.first()
        assertEquals(1, items.size)
        assertEquals("Item 3", items[0].content)
    }

    @Test
    fun deleteItemsByIds_withEmptyList_doesNothing() = runTest {
        repository.insertItem(ChecklistItem(content = "Item 1"))

        repository.deleteItemsByIds(emptyList())

        val items = repository.allItems.first()
        assertEquals(1, items.size)
    }

    @Test
    fun toggleItemPinned_setsToTrue() = runTest {
        val item = ChecklistItem(content = "Pin me", isPinned = false)
        val id = repository.insertItem(item)

        repository.toggleItemPinned(id, true)

        val toggled = repository.getItemById(id)
        assertTrue(toggled?.isPinned == true)
    }

    @Test
    fun toggleItemPinned_setsToFalse() = runTest {
        val item = ChecklistItem(content = "Unpin me", isPinned = true)
        val id = repository.insertItem(item)

        repository.toggleItemPinned(id, false)

        val toggled = repository.getItemById(id)
        assertFalse(toggled?.isPinned == true)
    }

    @Test
    fun updateQuantity_changesItemQuantity() = runTest {
        val item = ChecklistItem(content = "Item", quantity = 1)
        val id = repository.insertItem(item)

        repository.updateQuantity(id, 5)

        val updated = repository.getItemById(id)
        assertEquals(5, updated?.quantity)
    }

    @Test
    fun updateQuantity_canSetToOne() = runTest {
        val item = ChecklistItem(content = "Item", quantity = 10)
        val id = repository.insertItem(item)

        repository.updateQuantity(id, 1)

        val updated = repository.getItemById(id)
        assertEquals(1, updated?.quantity)
    }

    @Test
    fun uncheckAllItems_unchecksAllCheckedItems() = runTest {
        repository.insertItem(ChecklistItem(content = "Item 1", isChecked = true))
        repository.insertItem(ChecklistItem(content = "Item 2", isChecked = true))
        repository.insertItem(ChecklistItem(content = "Item 3", isChecked = false))

        repository.uncheckAllItems()

        val items = repository.allItems.first()
        items.forEach { item ->
            assertFalse("Item ${item.content} should be unchecked", item.isChecked)
        }
    }

    @Test
    fun uncheckAllItems_onEmptyDatabase_doesNotThrow() = runTest {
        // Should not throw
        repository.uncheckAllItems()

        val items = repository.allItems.first()
        assertTrue(items.isEmpty())
    }

    @Test
    fun insertItems_withEmptyList_doesNothing() = runTest {
        repository.insertItems(emptyList())

        val items = repository.allItems.first()
        assertTrue(items.isEmpty())
    }

    @Test
    fun allItemsGrouped_complexSorting() = runTest {
        // Insert items in various states
        repository.insertItem(ChecklistItem(content = "Pinned Checked", isPinned = true, isChecked = true))
        repository.insertItem(ChecklistItem(content = "Pinned Unchecked", isPinned = true, isChecked = false))
        repository.insertItem(ChecklistItem(content = "Unpinned Checked", isPinned = false, isChecked = true))
        repository.insertItem(ChecklistItem(content = "Unpinned Unchecked", isPinned = false, isChecked = false))

        val items = repository.allItemsGrouped.first()

        // Pinned items first, then within each group: unchecked before checked
        assertEquals(4, items.size)
        // First should be pinned
        assertTrue(items[0].isPinned)
        assertTrue(items[1].isPinned)
        // Last should be unpinned
        assertFalse(items[2].isPinned)
        assertFalse(items[3].isPinned)
    }

    @Test
    fun checkedCount_updatesAfterToggle() = runTest {
        val id = repository.insertItem(ChecklistItem(content = "Item", isChecked = false))

        assertEquals(0, repository.checkedCount.first())

        repository.toggleItemChecked(id, true)

        assertEquals(1, repository.checkedCount.first())
    }

    @Test
    fun totalCount_updatesAfterDelete() = runTest {
        val id = repository.insertItem(ChecklistItem(content = "Item"))

        assertEquals(1, repository.totalCount.first())

        val item = repository.getItemById(id)!!
        repository.deleteItem(item)

        assertEquals(0, repository.totalCount.first())
    }
}
