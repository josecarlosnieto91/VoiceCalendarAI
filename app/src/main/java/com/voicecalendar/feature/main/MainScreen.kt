package com.voicecalendar.feature.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voicecalendar.core.ui.components.EditableEventDialog
import com.voicecalendar.core.ui.components.EventCard
import com.voicecalendar.core.ui.components.MicButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it); viewModel.onDismissError() }
    }
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { snackbarHostState.showSnackbar(it); viewModel.onDismissSuccess() }
    }

    if (uiState.showEditDialog && uiState.extractedEvent != null) {
        EditableEventDialog(event = uiState.extractedEvent!!, onConfirm = viewModel::onConfirmEvent, onDismiss = viewModel::onDismissEditDialog)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("VoiceCalendar AI", style = MaterialTheme.typography.titleLarge) },
                actions = { IconButton(onClick = { }) { Icon(Icons.Default.Settings, "Settings") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    MicButton(isListening = uiState.isListening, onToggle = viewModel::onMicToggle)
                    Spacer(modifier = Modifier.height(16.dp))
                    AnimatedVisibility(visible = uiState.isListening, enter = fadeIn(), exit = fadeOut()) {
                        Text("Listening...", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                    }
                    AnimatedVisibility(visible = uiState.isProcessing, enter = fadeIn(), exit = fadeOut()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            Text("Processing...", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    if (uiState.partialText.isNotBlank() && uiState.isListening) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(uiState.partialText, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp))
                    }
                }
            }
            if (uiState.upcomingEvents.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Upcoming Events", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
                    items(uiState.upcomingEvents) { event -> EventCard(event = event, onClick = { }) }
                }
            }
        }
    }
}
