package com.voicecalendar.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.voicecalendar.core.util.DateUtils
import com.voicecalendar.domain.model.CalendarEvent
import com.voicecalendar.domain.model.EventCategory
import com.voicecalendar.domain.model.EventPriority
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableEventDialog(
    event: CalendarEvent,
    onConfirm: (CalendarEvent) -> Unit,
    onDismiss: () -> Unit
) {
    var editedEvent by remember { mutableStateOf(event) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = event.date
                ?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        editedEvent = editedEvent.copy(
                            date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        )
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        val startHour = event.startTime?.hour ?: 9
        val startMinute = event.startTime?.minute ?: 0
        val timeState = rememberTimePickerState(initialHour = startHour, initialMinute = startMinute, is24Hour = true)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time") },
            text = { TimePicker(state = timeState) },
            confirmButton = {
                TextButton(onClick = {
                    editedEvent = editedEvent.copy(startTime = LocalTime.of(timeState.hour, timeState.minute))
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancel") } }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Review Event") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                // Title
                OutlinedTextField(value = editedEvent.title, onValueChange = { editedEvent = editedEvent.copy(title = it) },
                    label = { Text("Title") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(8.dp))

                // Date & Time row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.weight(1f)) {
                        Text(editedEvent.date?.let { DateUtils.formatDate(it) } ?: "Select Date")
                    }
                    OutlinedButton(onClick = { showTimePicker = true }, modifier = Modifier.weight(1f)) {
                        Text(editedEvent.startTime?.let { DateUtils.formatTime(it) } ?: "Select Time")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Duration
                OutlinedTextField(
                    value = editedEvent.durationMinutes.toString(),
                    onValueChange = { editedEvent = editedEvent.copy(durationMinutes = it.toIntOrNull()?.coerceIn(15, 1440) ?: 60) },
                    label = { Text("Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Location
                OutlinedTextField(value = editedEvent.location, onValueChange = { editedEvent = editedEvent.copy(location = it) },
                    label = { Text("Location") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(8.dp))

                // Category dropdown
                ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
                    OutlinedTextField(
                        value = editedEvent.category.displayName,
                        onValueChange = {},
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) },
                        readOnly = true
                    )
                    ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        EventCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.displayName) },
                                onClick = {
                                    editedEvent = editedEvent.copy(category = cat)
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Priority dropdown
                ExposedDropdownMenuBox(expanded = priorityExpanded, onExpandedChange = { priorityExpanded = it }) {
                    OutlinedTextField(
                        value = editedEvent.priority.displayName,
                        onValueChange = {},
                        label = { Text("Priority") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(priorityExpanded) },
                        readOnly = true
                    )
                    ExposedDropdownMenu(expanded = priorityExpanded, onDismissRequest = { priorityExpanded = false }) {
                        EventPriority.entries.forEach { pri ->
                            DropdownMenuItem(
                                text = { Text(pri.displayName) },
                                onClick = {
                                    editedEvent = editedEvent.copy(priority = pri)
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Reminders
                OutlinedTextField(
                    value = editedEvent.reminders.joinToString(", "),
                    onValueChange = {
                        editedEvent = editedEvent.copy(
                            reminders = it.split(",").mapNotNull { s -> s.trim().toIntOrNull() }
                        )
                    },
                    label = { Text("Reminders (minutes before, comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Show smart reminder info
                if (editedEvent.reminders.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⏰ ${com.voicecalendar.domain.service.SmartReminderEngine.describeReminders(editedEvent.reminders).joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = { Button(onClick = { onConfirm(editedEvent) }) { Text("Save Event") } },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
