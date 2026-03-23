package com.unstampedpages.app.ui.screens.checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unstampedpages.app.data.model.ChecklistCategory
import com.unstampedpages.app.ui.screens.checklist.components.AddItemDialog
import com.unstampedpages.app.ui.screens.checklist.components.CategorySection
import com.unstampedpages.app.ui.screens.checklist.components.MultiSelectActionBar
import com.unstampedpages.app.ui.screens.checklist.components.ProgressHeader
import com.unstampedpages.app.ui.screens.checklist.components.TemplateSelector
import com.unstampedpages.app.ui.theme.Primary
import com.unstampedpages.app.ui.theme.Secondary

@Composable
fun ChecklistScreen(
    viewModel: ChecklistViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showOverflowMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("checklist_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Multi-select action bar or Progress header
            if (uiState.isMultiSelectMode) {
                MultiSelectActionBar(
                    selectedCount = uiState.selectedItemIds.size,
                    onCancel = { viewModel.exitMultiSelectMode() },
                    onDelete = { viewModel.deleteSelectedItems() }
                )
            } else {
                Box(modifier = Modifier.fillMaxWidth()) {
                    ProgressHeader(
                        progress = uiState.progress,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Overflow menu button
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                    ) {
                        IconButton(
                            onClick = { showOverflowMenu = true },
                            modifier = Modifier.testTag("overflow_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = Primary
                            )
                        }

                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Load Template") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ViewList,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.showTemplateDialog()
                                },
                                modifier = Modifier.testTag("menu_load_template")
                            )
                            DropdownMenuItem(
                                text = { Text("Reset List") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.uncheckAllItems()
                                },
                                modifier = Modifier.testTag("menu_reset_list")
                            )
                        }
                    }
                }
            }

            // Checklist content
            if (uiState.items.isEmpty()) {
                EmptyChecklistMessage(
                    onLoadTemplate = { viewModel.showTemplateDialog() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("checklist_content")
                ) {
                    // Render categories with items
                    ChecklistCategory.entries
                        .filter { uiState.groupedItems.containsKey(it) }
                        .sortedBy { it.sortOrder }
                        .forEach { category ->
                            val items = uiState.groupedItems[category] ?: emptyList()

                            item(key = "category_${category.name}") {
                                CategorySection(
                                    category = category,
                                    items = items,
                                    isExpanded = uiState.expandedCategories.contains(category),
                                    isMultiSelectMode = uiState.isMultiSelectMode,
                                    selectedItemIds = uiState.selectedItemIds,
                                    onToggleExpanded = { viewModel.toggleCategoryExpanded(category) },
                                    onItemChecked = { viewModel.toggleItemChecked(it) },
                                    onItemDeleted = { viewModel.deleteItem(it) },
                                    onItemPinned = { viewModel.toggleItemPinned(it) },
                                    onQuantityChanged = { item, qty -> viewModel.updateQuantity(item, qty) },
                                    onItemLongPress = { viewModel.enterMultiSelectMode(it) },
                                    onItemSelected = { viewModel.toggleItemSelection(it) }
                                )
                            }
                        }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // Floating Action Button
        if (!uiState.isMultiSelectMode) {
            FloatingActionButton(
                onClick = { viewModel.showAddItemDialog() },
                containerColor = Secondary,
                contentColor = Primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .testTag("add_item_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add item"
                )
            }
        }
    }

    // Add Item Dialog
    if (uiState.showAddItemDialog) {
        AddItemDialog(
            onDismiss = { viewModel.hideAddItemDialog() },
            onAdd = { name, category, quantity ->
                viewModel.addItemWithDetails(name, category, quantity)
            }
        )
    }

    // Template Selector Dialog
    if (uiState.showTemplateDialog) {
        TemplateSelector(
            onDismiss = { viewModel.hideTemplateDialog() },
            onTemplateSelected = { viewModel.loadTemplate(it) }
        )
    }
}

@Composable
private fun EmptyChecklistMessage(
    onLoadTemplate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Your checklist is empty",
            style = MaterialTheme.typography.titleMedium,
            color = Primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.testTag("empty_message_title")
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the + button to add items\nor load a template to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = Primary.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("empty_message_subtitle")
        )
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.material3.TextButton(
            onClick = onLoadTemplate,
            modifier = Modifier.testTag("load_template_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ViewList,
                contentDescription = null,
                tint = Secondary
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(
                text = "Load Template",
                color = Secondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
