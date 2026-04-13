package com.unstampedpages.app.ui.screens.checklist.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.unstampedpages.app.R
import com.unstampedpages.app.data.local.entity.ChecklistItem
import com.unstampedpages.app.data.model.ChecklistCategory
import com.unstampedpages.app.ui.theme.Primary
import com.unstampedpages.app.ui.theme.Secondary

/**
 * State for a category section
 */
data class CategorySectionState(
    val isExpanded: Boolean,
    val isMultiSelectMode: Boolean,
    val selectedItemIds: Set<Long>
)

/**
 * Callbacks for category section interactions
 */
data class CategorySectionCallbacks(
    val onToggleExpanded: () -> Unit,
    val onItemChecked: (ChecklistItem) -> Unit,
    val onItemDeleted: (ChecklistItem) -> Unit,
    val onItemPinned: (ChecklistItem) -> Unit,
    val onQuantityChanged: (ChecklistItem, Int) -> Unit,
    val onItemLongPress: (Long) -> Unit,
    val onItemSelected: (Long) -> Unit
)

@Composable
fun CategorySection(
    category: ChecklistCategory,
    items: List<ChecklistItem>,
    state: CategorySectionState,
    callbacks: CategorySectionCallbacks,
    modifier: Modifier = Modifier
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (state.isExpanded) 180f else 0f,
        label = "rotation"
    )

    val checkedCount = items.count { it.isChecked }
    val totalCount = items.size

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_section_${category.name.lowercase()}")
    ) {
        // Category Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { callbacks.onToggleExpanded() }
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("category_header_${category.name.lowercase()}"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = Secondary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(category.displayNameResId),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "$checkedCount/$totalCount",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = if (state.isExpanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotationAngle)
            )
        }

        // Category Items
        AnimatedVisibility(
            visible = state.isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                items.forEach { item ->
                    SwipeableChecklistItem(
                        item = item,
                        state = SwipeableItemState(
                            isMultiSelectMode = state.isMultiSelectMode,
                            isSelected = state.selectedItemIds.contains(item.id)
                        ),
                        callbacks = SwipeableItemCallbacks(
                            onCheckedChange = { callbacks.onItemChecked(item) },
                            onDelete = { callbacks.onItemDeleted(item) },
                            onPin = { callbacks.onItemPinned(item) },
                            onQuantityChange = { quantity -> callbacks.onQuantityChanged(item, quantity) },
                            onLongPress = { callbacks.onItemLongPress(item.id) },
                            onSelect = { callbacks.onItemSelected(item.id) }
                        ),
                        modifier = Modifier.testTag("checklist_item_${item.id}")
                    )
                }
            }
        }
    }
}
