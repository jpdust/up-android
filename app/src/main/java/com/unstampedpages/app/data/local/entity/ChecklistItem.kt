package com.unstampedpages.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist_items")
data class ChecklistItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val isChecked: Boolean = false,
    val category: String = "OTHER",
    val quantity: Int = 1,
    val isPinned: Boolean = false,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
