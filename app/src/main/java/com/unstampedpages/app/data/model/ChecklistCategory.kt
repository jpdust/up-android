package com.unstampedpages.app.data.model

import androidx.annotation.StringRes
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
import com.unstampedpages.app.R

/**
 * Categories for organizing checklist items.
 * Each category has a display name resource and an icon for visual identification.
 */
enum class ChecklistCategory(
    @param:StringRes val displayNameResId: Int,
    val icon: ImageVector,
    val sortOrder: Int
) {
    ELECTRONICS(
        displayNameResId = R.string.category_electronics,
        icon = Icons.Default.Devices,
        sortOrder = 0
    ),
    TOILETRIES(
        displayNameResId = R.string.category_toiletries,
        icon = Icons.Default.Soap,
        sortOrder = 1
    ),
    CLOTHING(
        displayNameResId = R.string.category_clothing,
        icon = Icons.Default.Checkroom,
        sortOrder = 2
    ),
    DOCUMENTS(
        displayNameResId = R.string.category_documents,
        icon = Icons.Default.Description,
        sortOrder = 3
    ),
    MEDICINE(
        displayNameResId = R.string.category_medicine,
        icon = Icons.Default.LocalPharmacy,
        sortOrder = 4
    ),
    ACCESSORIES(
        displayNameResId = R.string.category_accessories,
        icon = Icons.Default.Watch,
        sortOrder = 5
    ),
    SNACKS(
        displayNameResId = R.string.category_snacks,
        icon = Icons.Default.Restaurant,
        sortOrder = 6
    ),
    OTHER(
        displayNameResId = R.string.category_other,
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
