package com.unstampedpages.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.unstampedpages.app.data.local.entity.TripLogEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface TripLogDao {

    @Query("SELECT * FROM trip_log_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<TripLogEntry>>

    @Query("SELECT * FROM trip_log_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): TripLogEntry?

    @Query("SELECT * FROM trip_log_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getEntriesByDateRange(startDate: Long, endDate: Long): Flow<List<TripLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: TripLogEntry): Long

    @Update
    suspend fun updateEntry(entry: TripLogEntry)

    @Delete
    suspend fun deleteEntry(entry: TripLogEntry)

    @Query("DELETE FROM trip_log_entries")
    suspend fun deleteAllEntries()
}
