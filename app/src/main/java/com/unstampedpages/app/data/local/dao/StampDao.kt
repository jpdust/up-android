package com.unstampedpages.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.unstampedpages.app.data.local.entity.StampItem
import kotlinx.coroutines.flow.Flow

@Dao
interface StampDao {

    @Query("SELECT * FROM stamp_items ORDER BY countryName ASC")
    fun getAllStamps(): Flow<List<StampItem>>

    @Query("SELECT * FROM stamp_items WHERE countryCode = :countryCode")
    suspend fun getStampByCountryCode(countryCode: String): StampItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStamp(stamp: StampItem): Long

    @Update
    suspend fun updateStamp(stamp: StampItem)

    @Delete
    suspend fun deleteStamp(stamp: StampItem)

    @Query("DELETE FROM stamp_items WHERE countryCode = :countryCode")
    suspend fun deleteStampByCountryCode(countryCode: String)

    @Query("UPDATE stamp_items SET imagePath = :imagePath, updatedAt = :updatedAt WHERE countryCode = :countryCode")
    suspend fun updateStampImage(countryCode: String, imagePath: String?, updatedAt: Long = System.currentTimeMillis())
}
