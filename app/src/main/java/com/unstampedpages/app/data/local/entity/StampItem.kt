package com.unstampedpages.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stamp_items")
data class StampItem(
    @PrimaryKey
    val countryCode: String,
    val countryName: String,
    val imagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
