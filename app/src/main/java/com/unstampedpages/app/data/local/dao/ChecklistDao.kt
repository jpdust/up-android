package com.unstampedpages.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.unstampedpages.app.data.local.entity.ChecklistItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {

    @Query("SELECT * FROM checklist_items ORDER BY createdAt DESC")
    fun getAllItems(): Flow<List<ChecklistItem>>

    @Query("""
        SELECT * FROM checklist_items
        ORDER BY isPinned DESC, category ASC, isChecked ASC, sortOrder ASC, createdAt DESC
    """)
    fun getAllItemsGrouped(): Flow<List<ChecklistItem>>

    @Query("SELECT * FROM checklist_items WHERE id = :id")
    suspend fun getItemById(id: Long): ChecklistItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ChecklistItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ChecklistItem>)

    @Update
    suspend fun updateItem(item: ChecklistItem)

    @Delete
    suspend fun deleteItem(item: ChecklistItem)

    @Query("DELETE FROM checklist_items")
    suspend fun deleteAllItems()

    @Query("DELETE FROM checklist_items WHERE id IN (:ids)")
    suspend fun deleteItemsByIds(ids: List<Long>)

    @Query("UPDATE checklist_items SET isChecked = :isChecked, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateCheckedStatus(id: Long, isChecked: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE checklist_items SET isPinned = :isPinned, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updatePinnedStatus(id: Long, isPinned: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE checklist_items SET quantity = :quantity, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateQuantity(id: Long, quantity: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE checklist_items SET isChecked = 0, updatedAt = :updatedAt")
    suspend fun uncheckAllItems(updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM checklist_items")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM checklist_items WHERE isChecked = 1")
    fun getCheckedCount(): Flow<Int>
}
