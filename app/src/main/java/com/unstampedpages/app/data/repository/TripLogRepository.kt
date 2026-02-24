package com.unstampedpages.app.data.repository

import com.unstampedpages.app.data.local.dao.TripLogDao
import com.unstampedpages.app.data.local.entity.TripLogEntry
import kotlinx.coroutines.flow.Flow

class TripLogRepository(private val tripLogDao: TripLogDao) {

    val allEntries: Flow<List<TripLogEntry>> = tripLogDao.getAllEntries()

    suspend fun getEntryById(id: Long): TripLogEntry? {
        return tripLogDao.getEntryById(id)
    }

    fun getEntriesByDateRange(startDate: Long, endDate: Long): Flow<List<TripLogEntry>> {
        return tripLogDao.getEntriesByDateRange(startDate, endDate)
    }

    suspend fun insertEntry(entry: TripLogEntry): Long {
        return tripLogDao.insertEntry(entry)
    }

    suspend fun updateEntry(entry: TripLogEntry) {
        tripLogDao.updateEntry(entry.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteEntry(entry: TripLogEntry) {
        tripLogDao.deleteEntry(entry)
    }

    suspend fun deleteAllEntries() {
        tripLogDao.deleteAllEntries()
    }
}
