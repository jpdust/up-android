package com.unstampedpages.app.data.repository

import com.unstampedpages.app.data.local.dao.StampDao
import com.unstampedpages.app.data.local.entity.StampItem
import kotlinx.coroutines.flow.Flow

class StampRepository(private val stampDao: StampDao) {

    val allStamps: Flow<List<StampItem>> = stampDao.getAllStamps()

    suspend fun getStampByCountryCode(countryCode: String): StampItem? {
        return stampDao.getStampByCountryCode(countryCode)
    }

    suspend fun insertStamp(stamp: StampItem): Long {
        return stampDao.insertStamp(stamp)
    }

    suspend fun updateStamp(stamp: StampItem) {
        stampDao.updateStamp(stamp.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun updateStampImage(countryCode: String, imagePath: String?) {
        stampDao.updateStampImage(countryCode, imagePath)
    }

    suspend fun deleteStamp(stamp: StampItem) {
        stampDao.deleteStamp(stamp)
    }

    suspend fun deleteStampByCountryCode(countryCode: String) {
        stampDao.deleteStampByCountryCode(countryCode)
    }
}
