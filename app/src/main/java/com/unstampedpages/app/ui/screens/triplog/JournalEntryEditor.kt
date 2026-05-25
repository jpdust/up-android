package com.unstampedpages.app.ui.screens.triplog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.unstampedpages.app.R
import com.unstampedpages.app.ui.theme.Primary
import com.unstampedpages.app.ui.theme.Secondary
import com.unstampedpages.app.util.DateUtils

/**
 * State for the journal entry editor
 */
data class JournalEntryState(
    val title: String,
    val content: String,
    val location: String,
    val date: Long,
    val isNewEntry: Boolean
)

/**
 * Callbacks for journal entry editor interactions
 */
data class JournalEntryCallbacks(
    val onTitleChange: (String) -> Unit,
    val onContentChange: (String) -> Unit,
    val onLocationChange: (String) -> Unit,
    val onDateChange: (Long) -> Unit,
    val onSave: () -> Unit,
    val onCancel: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryEditor(
    state: JournalEntryState,
    callbacks: JournalEntryCallbacks
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dismissDatePicker = { showDatePicker = false }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = state.date)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = stringResource(if (state.isNewEntry) R.string.trip_log_new_entry else R.string.trip_log_edit_entry),
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = callbacks.onCancel) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_cancel)
                    )
                }
            },
            actions = {
                IconButton(onClick = callbacks.onSave) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.cd_save),
                        tint = Secondary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Date selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { showDatePicker = true }) {
                    Text(
                        text = DateUtils.formatFullDate(state.date),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            OutlinedTextField(
                value = state.title,
                onValueChange = callbacks.onTitleChange,
                label = { Text(stringResource(R.string.trip_log_title_label)) },
                placeholder = { Text(stringResource(R.string.trip_log_title_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                colors = textFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Location
            OutlinedTextField(
                value = state.location,
                onValueChange = callbacks.onLocationChange,
                label = { Text(stringResource(R.string.trip_log_location_label)) },
                placeholder = { Text(stringResource(R.string.trip_log_location_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Secondary
                    )
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                colors = textFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content
            OutlinedTextField(
                value = state.content,
                onValueChange = callbacks.onContentChange,
                label = { Text(stringResource(R.string.trip_log_story_label)) },
                placeholder = { Text(stringResource(R.string.trip_log_story_placeholder)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                colors = textFieldColors()
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = dismissDatePicker,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { callbacks.onDateChange(it) }
                    dismissDatePicker()
                }) {
                    Text(stringResource(R.string.action_ok), color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = dismissDatePicker) {
                    Text(stringResource(R.string.checklist_cancel), color = MaterialTheme.colorScheme.primary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Secondary,
    unfocusedBorderColor = Primary.copy(alpha = 0.3f),
    focusedLabelColor = Secondary,
    unfocusedLabelColor = Primary.copy(alpha = 0.6f),
    cursorColor = Secondary
)
