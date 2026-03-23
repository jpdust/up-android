package com.unstampedpages.app.ui.screens.checklist.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.unstampedpages.app.data.local.entity.ChecklistItem
import com.unstampedpages.app.ui.theme.Accent
import com.unstampedpages.app.ui.theme.Primary
import com.unstampedpages.app.ui.theme.Secondary
import com.unstampedpages.app.ui.theme.SecondaryLight

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SwipeableChecklistItem(
    item: ChecklistItem,
    isMultiSelectMode: Boolean,
    isSelected: Boolean,
    onCheckedChange: () -> Unit,
    onDelete: () -> Unit,
    onPin: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onLongPress: () -> Unit,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current

    val dismissState = rememberDismissState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                DismissValue.DismissedToStart -> {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onDelete()
                    true
                }
                DismissValue.DismissedToEnd -> {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onPin()
                    false // Don't dismiss, just toggle pin
                }
                DismissValue.Default -> false
            }
        }
    )

    val directions = if (isMultiSelectMode) {
        emptySet()
    } else {
        setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart)
    }

    SwipeToDismiss(
        state = dismissState,
        background = {
            SwipeBackground(
                dismissDirection = dismissState.dismissDirection,
                isPinned = item.isPinned
            )
        },
        dismissContent = {
            ChecklistItemContent(
                item = item,
                isMultiSelectMode = isMultiSelectMode,
                isSelected = isSelected,
                onCheckedChange = onCheckedChange,
                onQuantityChange = onQuantityChange,
                onLongPress = onLongPress,
                onSelect = onSelect
            )
        },
        modifier = modifier.padding(vertical = 4.dp),
        directions = directions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(
    dismissDirection: DismissDirection?,
    isPinned: Boolean
) {
    val color by animateColorAsState(
        targetValue = when (dismissDirection) {
            DismissDirection.StartToEnd -> if (isPinned) Primary.copy(alpha = 0.3f) else Secondary
            DismissDirection.EndToStart -> Accent
            null -> Color.Transparent
        },
        label = "background"
    )

    val alignment = when (dismissDirection) {
        DismissDirection.StartToEnd -> Alignment.CenterStart
        DismissDirection.EndToStart -> Alignment.CenterEnd
        null -> Alignment.Center
    }

    val icon = when (dismissDirection) {
        DismissDirection.StartToEnd -> Icons.Default.PushPin
        DismissDirection.EndToStart -> Icons.Default.Delete
        null -> null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color, RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChecklistItemContent(
    item: ChecklistItem,
    isMultiSelectMode: Boolean,
    isSelected: Boolean,
    onCheckedChange: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onLongPress: () -> Unit,
    onSelect: () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val visualState = computeVisualState(isSelected, item.isChecked)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .combinedClickable(
                onClick = createClickHandler(isMultiSelectMode, onSelect, onCheckedChange, hapticFeedback),
                onLongClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongPress()
                }
            ),
        colors = CardDefaults.cardColors(containerColor = visualState.containerColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SelectionIndicator(
                isMultiSelectMode = isMultiSelectMode,
                isSelected = isSelected,
                isChecked = item.isChecked,
                onCheckedChange = onCheckedChange,
                onSelect = onSelect
            )

            PinIndicator(isPinned = item.isPinned)

            Text(
                text = item.content,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .weight(1f)
                    .alpha(visualState.textAlpha),
                textDecoration = visualState.textDecoration,
                color = Primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            QuantitySection(
                item = item,
                isMultiSelectMode = isMultiSelectMode,
                onQuantityChange = onQuantityChange
            )
        }
    }
}

private data class ChecklistItemVisualState(
    val containerColor: Color,
    val textAlpha: Float,
    val textDecoration: TextDecoration
)

@Composable
private fun computeVisualState(isSelected: Boolean, isChecked: Boolean): ChecklistItemVisualState {
    val containerColor = when {
        isSelected -> Secondary.copy(alpha = 0.2f)
        isChecked -> SecondaryLight.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface
    }
    val textAlpha = if (isChecked) 0.6f else 1f
    val textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
    return ChecklistItemVisualState(containerColor, textAlpha, textDecoration)
}

private fun createClickHandler(
    isMultiSelectMode: Boolean,
    onSelect: () -> Unit,
    onCheckedChange: () -> Unit,
    hapticFeedback: androidx.compose.ui.hapticfeedback.HapticFeedback
): () -> Unit = {
    if (isMultiSelectMode) {
        onSelect()
    } else {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        onCheckedChange()
    }
}

@Composable
private fun SelectionIndicator(
    isMultiSelectMode: Boolean,
    isSelected: Boolean,
    isChecked: Boolean,
    onCheckedChange: () -> Unit,
    onSelect: () -> Unit
) {
    if (isMultiSelectMode) {
        MultiSelectCheckbox(isSelected = isSelected, onSelect = onSelect)
    } else {
        CheckToggleButton(isChecked = isChecked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun MultiSelectCheckbox(isSelected: Boolean, onSelect: () -> Unit) {
    Checkbox(
        checked = isSelected,
        onCheckedChange = { onSelect() },
        colors = CheckboxDefaults.colors(
            checkedColor = Secondary,
            uncheckedColor = Primary.copy(alpha = 0.5f)
        ),
        modifier = Modifier.testTag("item_checkbox")
    )
}

@Composable
private fun CheckToggleButton(isChecked: Boolean, onCheckedChange: () -> Unit) {
    val icon = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked
    val contentDescription = if (isChecked) "Uncheck" else "Check"
    val tint = if (isChecked) Secondary else Primary.copy(alpha = 0.5f)

    IconButton(onClick = onCheckedChange, modifier = Modifier.size(48.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun PinIndicator(isPinned: Boolean) {
    if (!isPinned) return
    Icon(
        imageVector = Icons.Default.PushPin,
        contentDescription = "Pinned",
        tint = Secondary,
        modifier = Modifier.size(16.dp)
    )
    Spacer(modifier = Modifier.width(4.dp))
}

@Composable
private fun QuantitySection(
    item: ChecklistItem,
    isMultiSelectMode: Boolean,
    onQuantityChange: (Int) -> Unit
) {
    val showQuantityPicker = item.quantity > 1 || !item.isChecked
    if (!showQuantityPicker) return

    QuantityPicker(
        quantity = item.quantity,
        onQuantityChange = onQuantityChange,
        enabled = !isMultiSelectMode && !item.isChecked,
        modifier = Modifier.testTag("quantity_picker_${item.id}")
    )
}
