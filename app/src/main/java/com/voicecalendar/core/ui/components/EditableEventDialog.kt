package com.voicecalendar.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.voicecalendar.core.util.DateUtils
import com.voicecalendar.domain.model.CalendarEvent
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
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
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
                OutlinedTextField(value = editedEvent.title, onValueChange = { editedEvent = editedEvent.copy(title = it) },
                    label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = editedEvent.description, onValueChange = { editedEvent = editedEvent.copy(description = it) },
                    label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 4)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.weight(1f)) {
                        Text(editedEvent.date?.let { DateUtils.formatDate(it) } ?: "Select Date")
                    }
                    OutlinedButton(onClick = { showTimePicker = true }, modifier = Modifier.weight(1f)) {
                        Text(editedEvent.startTime?.let { DateUtils.formatTime(it) } ?: "Select Time")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = editedEvent.location, onValueChange = { editedEvent = editedEvent.copy(location = it) },
                    label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = editedEvent.reminderMinutes.toString(),
                    onValueChange = { editedEvent = editedEvent.copy(reminderMinutes = it.toIntOrNull() ?: 15) },
                    label = { Text("Reminder (minutes)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { Button(onClick = { onConfirm(editedEvent) }) { Text("Save Event") } },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
