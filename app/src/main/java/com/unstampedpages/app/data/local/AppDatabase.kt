package com.unstampedpages.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.unstampedpages.app.data.local.dao.ChecklistDao
import com.unstampedpages.app.data.local.dao.StampDao
import com.unstampedpages.app.data.local.dao.TripLogDao
import com.unstampedpages.app.data.local.entity.ChecklistItem
import com.unstampedpages.app.data.local.entity.StampItem
import com.unstampedpages.app.data.local.entity.TripLogEntry

@Database(
    entities = [ChecklistItem::class, TripLogEntry::class, StampItem::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun checklistDao(): ChecklistDao
    abstract fun tripLogDao(): TripLogDao
    abstract fun stampDao(): StampDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "unstamped_pages.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
