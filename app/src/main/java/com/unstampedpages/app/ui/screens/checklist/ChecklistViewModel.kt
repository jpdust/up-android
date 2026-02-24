package com.unstampedpages.app.ui.screens.checklist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unstampedpages.app.data.local.AppDatabase
import com.unstampedpages.app.data.local.entity.ChecklistItem
import com.unstampedpages.app.data.repository.ChecklistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChecklistUiState(
    val items: List<ChecklistItem> = emptyList(),
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
            repository.allItems.collect { items ->
                _uiState.value = _uiState.value.copy(items = items, isLoading = false)
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
}
