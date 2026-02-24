package com.unstampedpages.app.data.repository

import com.unstampedpages.app.data.local.dao.ChecklistDao
import com.unstampedpages.app.data.local.entity.ChecklistItem
import kotlinx.coroutines.flow.Flow

class ChecklistRepository(private val checklistDao: ChecklistDao) {

    val allItems: Flow<List<ChecklistItem>> = checklistDao.getAllItems()

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

    suspend fun toggleItemChecked(id: Long, isChecked: Boolean) {
        checklistDao.updateCheckedStatus(id, isChecked)
    }

    suspend fun deleteAllItems() {
        checklistDao.deleteAllItems()
    }
}
