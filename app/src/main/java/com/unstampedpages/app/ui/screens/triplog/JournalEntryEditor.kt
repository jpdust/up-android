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
import com.unstampedpages.app.ui.theme.Primary
import com.unstampedpages.app.ui.theme.Secondary
import com.unstampedpages.app.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryEditor(
    title: String,
    content: String,
    location: String,
    date: Long,
    isNewEntry: Boolean,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onDateChange: (Long) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dismissDatePicker = { showDatePicker = false }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = if (isNewEntry) "New Entry" else "Edit Entry",
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Cancel"
                    )
                }
            },
            actions = {
                IconButton(onClick = onSave) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save",
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
                        text = DateUtils.formatFullDate(date),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Title") },
                placeholder = { Text("Give your entry a title...") },
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
                value = location,
                onValueChange = onLocationChange,
                label = { Text("Location (optional)") },
                placeholder = { Text("Where are you?") },
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
                value = content,
                onValueChange = onContentChange,
                label = { Text("Your story") },
                placeholder = { Text("Record your adventure...") },
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
                    datePickerState.selectedDateMillis?.let { onDateChange(it) }
                    dismissDatePicker()
                }) {
                    Text("OK", color = Primary)
                }
            },
            dismissButton = {
                TextButton(onClick = dismissDatePicker) {
                    Text("Cancel", color = Primary)
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
