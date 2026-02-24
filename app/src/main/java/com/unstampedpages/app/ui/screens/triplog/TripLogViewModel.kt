package com.unstampedpages.app.ui.screens.triplog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unstampedpages.app.data.local.AppDatabase
import com.unstampedpages.app.data.local.entity.TripLogEntry
import com.unstampedpages.app.data.repository.TripLogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TripLogUiState(
    val entries: List<TripLogEntry> = emptyList(),
    val selectedEntry: TripLogEntry? = null,
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val editingTitle: String = "",
    val editingContent: String = "",
    val editingLocation: String = "",
    val editingDate: Long = System.currentTimeMillis()
)

class TripLogViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripLogRepository

    private val _uiState = MutableStateFlow(TripLogUiState())
    val uiState: StateFlow<TripLogUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TripLogRepository(database.tripLogDao())

        viewModelScope.launch {
            repository.allEntries.collect { entries ->
                _uiState.value = _uiState.value.copy(entries = entries, isLoading = false)
            }
        }
    }

    fun startNewEntry() {
        _uiState.value = _uiState.value.copy(
            isEditing = true,
            selectedEntry = null,
            editingTitle = "",
            editingContent = "",
            editingLocation = "",
            editingDate = System.currentTimeMillis()
        )
    }

    fun editEntry(entry: TripLogEntry) {
        _uiState.value = _uiState.value.copy(
            isEditing = true,
            selectedEntry = entry,
            editingTitle = entry.title,
            editingContent = entry.content,
            editingLocation = entry.location ?: "",
            editingDate = entry.date
        )
    }

    fun updateEditingTitle(title: String) {
        _uiState.value = _uiState.value.copy(editingTitle = title)
    }

    fun updateEditingContent(content: String) {
        _uiState.value = _uiState.value.copy(editingContent = content)
    }

    fun updateEditingLocation(location: String) {
        _uiState.value = _uiState.value.copy(editingLocation = location)
    }

    fun updateEditingDate(date: Long) {
        _uiState.value = _uiState.value.copy(editingDate = date)
    }

    fun saveEntry() {
        val state = _uiState.value
        if (state.editingTitle.isBlank() && state.editingContent.isBlank()) {
            cancelEditing()
            return
        }

        viewModelScope.launch {
            val entry = state.selectedEntry?.copy(
                title = state.editingTitle.ifBlank { "Untitled Entry" },
                content = state.editingContent,
                location = state.editingLocation.ifBlank { null },
                date = state.editingDate
            ) ?: TripLogEntry(
                title = state.editingTitle.ifBlank { "Untitled Entry" },
                content = state.editingContent,
                location = state.editingLocation.ifBlank { null },
                date = state.editingDate
            )

            if (state.selectedEntry != null) {
                repository.updateEntry(entry)
            } else {
                repository.insertEntry(entry)
            }

            cancelEditing()
        }
    }

    fun cancelEditing() {
        _uiState.value = _uiState.value.copy(
            isEditing = false,
            selectedEntry = null,
            editingTitle = "",
            editingContent = "",
            editingLocation = "",
            editingDate = System.currentTimeMillis()
        )
    }

    fun deleteEntry(entry: TripLogEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    fun viewEntry(entry: TripLogEntry) {
        _uiState.value = _uiState.value.copy(selectedEntry = entry)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedEntry = null)
    }
}
