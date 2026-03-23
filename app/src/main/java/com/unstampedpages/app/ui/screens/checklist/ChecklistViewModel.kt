package com.unstampedpages.app.ui.screens.checklist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unstampedpages.app.data.local.AppDatabase
import com.unstampedpages.app.data.local.entity.ChecklistItem
import com.unstampedpages.app.data.model.ChecklistCategory
import com.unstampedpages.app.data.model.ChecklistProgress
import com.unstampedpages.app.data.model.ChecklistTemplate
import com.unstampedpages.app.data.repository.ChecklistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ChecklistUiState(
    val items: List<ChecklistItem> = emptyList(),
    val groupedItems: Map<ChecklistCategory, List<ChecklistItem>> = emptyMap(),
    val progress: ChecklistProgress = ChecklistProgress(),
    val isMultiSelectMode: Boolean = false,
    val selectedItemIds: Set<Long> = emptySet(),
    val expandedCategories: Set<ChecklistCategory> = ChecklistCategory.entries.toSet(),
    val showTemplateDialog: Boolean = false,
    val showAddItemDialog: Boolean = false,
    val newItemText: String = "",
    val isLoading: Boolean = false
)

class ChecklistViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChecklistRepository

    private val _uiState = MutableStateFlow(ChecklistUiState())
    val uiState: StateFlow<ChecklistUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ChecklistRepository(database.checklistDao())

        viewModelScope.launch {
            combine(
                repository.allItemsGrouped,
                repository.totalCount,
                repository.checkedCount
            ) { items, total, checked ->
                Triple(items, total, checked)
            }.collect { (items, total, checked) ->
                val grouped = items.groupBy { ChecklistCategory.fromName(it.category) }
                _uiState.value = _uiState.value.copy(
                    items = items,
                    groupedItems = grouped,
                    progress = ChecklistProgress(checkedCount = checked, totalCount = total),
                    isLoading = false
                )
            }
        }
    }

    fun updateNewItemText(text: String) {
        _uiState.value = _uiState.value.copy(newItemText = text)
    }

    fun addItem() {
        val text = _uiState.value.newItemText.trim()
        if (text.isNotEmpty()) {
            viewModelScope.launch {
                repository.insertItem(ChecklistItem(content = text))
                _uiState.value = _uiState.value.copy(newItemText = "")
            }
        }
    }

    fun addItemWithDetails(name: String, category: ChecklistCategory, quantity: Int) {
        if (name.trim().isNotEmpty()) {
            viewModelScope.launch {
                repository.insertItem(
                    ChecklistItem(
                        content = name.trim(),
                        category = category.name,
                        quantity = quantity
                    )
                )
                _uiState.value = _uiState.value.copy(showAddItemDialog = false)
            }
        }
    }

    fun addItemFromText(text: String) {
        if (text.trim().isNotEmpty()) {
            viewModelScope.launch {
                repository.insertItem(ChecklistItem(content = text.trim()))
            }
        }
    }

    fun toggleItemChecked(item: ChecklistItem) {
        viewModelScope.launch {
            repository.toggleItemChecked(item.id, !item.isChecked)
        }
    }

    fun toggleItemPinned(item: ChecklistItem) {
        viewModelScope.launch {
            repository.toggleItemPinned(item.id, !item.isPinned)
        }
    }

    fun updateQuantity(item: ChecklistItem, quantity: Int) {
        if (quantity >= 1) {
            viewModelScope.launch {
                repository.updateQuantity(item.id, quantity)
            }
        }
    }

    fun deleteItem(item: ChecklistItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun updateItem(item: ChecklistItem, newContent: String) {
        viewModelScope.launch {
            repository.updateItem(item.copy(content = newContent))
        }
    }

    fun deleteAllItems() {
        viewModelScope.launch {
            repository.deleteAllItems()
        }
    }

    fun uncheckAllItems() {
        viewModelScope.launch {
            repository.uncheckAllItems()
        }
    }

    // Multi-select operations
    fun enterMultiSelectMode(itemId: Long) {
        _uiState.value = _uiState.value.copy(
            isMultiSelectMode = true,
            selectedItemIds = setOf(itemId)
        )
    }

    fun exitMultiSelectMode() {
        _uiState.value = _uiState.value.copy(
            isMultiSelectMode = false,
            selectedItemIds = emptySet()
        )
    }

    fun toggleItemSelection(itemId: Long) {
        val currentSelection = _uiState.value.selectedItemIds
        val newSelection = if (currentSelection.contains(itemId)) {
            currentSelection - itemId
        } else {
            currentSelection + itemId
        }

        if (newSelection.isEmpty()) {
            exitMultiSelectMode()
        } else {
            _uiState.value = _uiState.value.copy(selectedItemIds = newSelection)
        }
    }

    fun deleteSelectedItems() {
        val selectedIds = _uiState.value.selectedItemIds.toList()
        if (selectedIds.isNotEmpty()) {
            viewModelScope.launch {
                repository.deleteItemsByIds(selectedIds)
                exitMultiSelectMode()
            }
        }
    }

    // Category expansion
    fun toggleCategoryExpanded(category: ChecklistCategory) {
        val currentExpanded = _uiState.value.expandedCategories
        val newExpanded = if (currentExpanded.contains(category)) {
            currentExpanded - category
        } else {
            currentExpanded + category
        }
        _uiState.value = _uiState.value.copy(expandedCategories = newExpanded)
    }

    // Template operations
    fun showTemplateDialog() {
        _uiState.value = _uiState.value.copy(showTemplateDialog = true)
    }

    fun hideTemplateDialog() {
        _uiState.value = _uiState.value.copy(showTemplateDialog = false)
    }

    fun loadTemplate(template: ChecklistTemplate) {
        viewModelScope.launch {
            val items = template.items.map { templateItem ->
                ChecklistItem(
                    content = templateItem.name,
                    category = templateItem.category.name,
                    quantity = templateItem.quantity
                )
            }
            repository.insertItems(items)
            hideTemplateDialog()
        }
    }

    // Add item dialog
    fun showAddItemDialog() {
        _uiState.value = _uiState.value.copy(showAddItemDialog = true)
    }

    fun hideAddItemDialog() {
        _uiState.value = _uiState.value.copy(showAddItemDialog = false)
    }
}
