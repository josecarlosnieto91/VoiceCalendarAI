package com.voicecalendar.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.voicecalendar.domain.model.AppSettings
import com.voicecalendar.domain.model.LlmConfig
import com.voicecalendar.domain.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    settings: AppSettings,
    onSave: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var provider by remember { mutableStateOf(settings.llmConfig.provider) }
    var apiKey by remember { mutableStateOf(settings.llmConfig.apiKey) }
    var model by remember { mutableStateOf(settings.llmConfig.model) }
    var endpoint by remember { mutableStateOf(settings.llmConfig.endpointUrl) }
    var maxTokens by remember { mutableStateOf(settings.llmConfig.maxTokens.toString()) }
    var temperature by remember { mutableStateOf(settings.llmConfig.temperature.toString()) }
    var themeMode by remember { mutableStateOf(settings.themeMode.name) }
    var defaultReminder by remember { mutableStateOf(settings.defaultReminderMinutes.toString()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("AI Provider", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(value = provider, onValueChange = { provider = it },
            label = { Text("Provider") }, modifier = Modifier.fillMaxWidth())

        OutlinedTextField(value = apiKey, onValueChange = { apiKey = it },
            label = { Text("API Key") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth())

        OutlinedTextField(value = model, onValueChange = { model = it },
            label = { Text("Model") }, modifier = Modifier.fillMaxWidth())

        OutlinedTextField(value = endpoint, onValueChange = { endpoint = it },
            label = { Text("Endpoint URL") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = maxTokens, onValueChange = { maxTokens = it },
                label = { Text("Max Tokens") }, modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = temperature, onValueChange = { temperature = it },
                label = { Text("Temperature") }, modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        }

        HorizontalDivider()
        Text("Preferences", style = MaterialTheme.typography.titleMedium)

        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(value = themeMode, onValueChange = {},
                label = { Text("Theme") },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                readOnly = true)
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                ThemeMode.entries.forEach { mode ->
                    DropdownMenuItem(text = { Text(mode.name) },
                        onClick = { themeMode = mode.name; expanded = false })
                }
            }
        }

        OutlinedTextField(value = defaultReminder, onValueChange = { defaultReminder = it },
            label = { Text("Default Reminder (minutes)") }, modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            onSave(AppSettings(
                llmConfig = LlmConfig(
                    provider = provider, apiKey = apiKey, model = model,
                    endpointUrl = endpoint,
                    maxTokens = maxTokens.toIntOrNull() ?: 512,
                    temperature = temperature.toFloatOrNull() ?: 0.1f
                ),
                themeMode = ThemeMode.valueOf(themeMode),
                defaultReminderMinutes = defaultReminder.toIntOrNull() ?: 15
            ))
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Save Settings")
        }
    }
}
