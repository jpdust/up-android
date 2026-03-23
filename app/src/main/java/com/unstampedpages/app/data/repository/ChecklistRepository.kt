package com.unstampedpages.app.data.repository

import com.unstampedpages.app.data.local.dao.ChecklistDao
import com.unstampedpages.app.data.local.entity.ChecklistItem
import kotlinx.coroutines.flow.Flow

class ChecklistRepository(private val checklistDao: ChecklistDao) {

    val allItems: Flow<List<ChecklistItem>> = checklistDao.getAllItems()

    val allItemsGrouped: Flow<List<ChecklistItem>> = checklistDao.getAllItemsGrouped()

    val totalCount: Flow<Int> = checklistDao.getTotalCount()

    val checkedCount: Flow<Int> = checklistDao.getCheckedCount()

    suspend fun getItemById(id: Long): ChecklistItem? {
        return checklistDao.getItemById(id)
    }

    suspend fun insertItem(item: ChecklistItem): Long {
        return checklistDao.insertItem(item)
    }

    suspend fun insertItems(items: List<ChecklistItem>) {
        checklistDao.insertItems(items)
    }

    suspend fun updateItem(item: ChecklistItem) {
        checklistDao.updateItem(item.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteItem(item: ChecklistItem) {
        checklistDao.deleteItem(item)
    }

    suspend fun deleteItemsByIds(ids: List<Long>) {
        checklistDao.deleteItemsByIds(ids)
    }

    suspend fun toggleItemChecked(id: Long, isChecked: Boolean) {
        checklistDao.updateCheckedStatus(id, isChecked)
    }

    suspend fun toggleItemPinned(id: Long, isPinned: Boolean) {
        checklistDao.updatePinnedStatus(id, isPinned)
    }

    suspend fun updateQuantity(id: Long, quantity: Int) {
        checklistDao.updateQuantity(id, quantity)
    }

    suspend fun uncheckAllItems() {
        checklistDao.uncheckAllItems()
    }

    suspend fun deleteAllItems() {
        checklistDao.deleteAllItems()
    }
}
