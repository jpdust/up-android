package com.unstampedpages.app.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Soap
import androidx.compose.material.icons.filled.Watch
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Categories for organizing checklist items.
 * Each category has a display name and an icon for visual identification.
 */
enum class ChecklistCategory(
    val displayName: String,
    val icon: ImageVector,
    val sortOrder: Int
) {
    ELECTRONICS(
        displayName = "Electronics",
        icon = Icons.Default.Devices,
        sortOrder = 0
    ),
    TOILETRIES(
        displayName = "Toiletries",
        icon = Icons.Default.Soap,
        sortOrder = 1
    ),
    CLOTHING(
        displayName = "Clothing",
        icon = Icons.Default.Checkroom,
        sortOrder = 2
    ),
    DOCUMENTS(
        displayName = "Documents",
        icon = Icons.Default.Description,
        sortOrder = 3
    ),
    MEDICINE(
        displayName = "Medicine",
        icon = Icons.Default.LocalPharmacy,
        sortOrder = 4
    ),
    ACCESSORIES(
        displayName = "Accessories",
        icon = Icons.Default.Watch,
        sortOrder = 5
    ),
    SNACKS(
        displayName = "Snacks",
        icon = Icons.Default.Restaurant,
        sortOrder = 6
    ),
    OTHER(
        displayName = "Other",
        icon = Icons.Default.MoreHoriz,
        sortOrder = 7
    );

    companion object {
        /**
         * Get a category by its name, defaulting to OTHER if not found.
         */
        fun fromName(name: String): ChecklistCategory {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: OTHER
        }
    }
}
