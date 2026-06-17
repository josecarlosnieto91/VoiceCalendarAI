package com.voicecalendar.feature.home

import androidx.compose.foundation.layout.size
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.voicecalendar.core.ui.components.EditableEventDialog
import com.voicecalendar.core.ui.components.EventCard
import com.voicecalendar.core.ui.components.MicButton
import com.voicecalendar.core.ui.components.SoundWave
import com.voicecalendar.core.ui.components.StatusIndicator
import com.voicecalendar.core.ui.components.VoiceState
import com.voicecalendar.core.ui.theme.StatusListening

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onDismissError()
        }
    }
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onDismissSuccess()
        }
    }

    // Show edit dialog when event is extracted
    if (uiState.showEditDialog && uiState.extractedEvent != null) {
        EditableEventDialog(
            event = uiState.extractedEvent!!,
            onConfirm = viewModel::onConfirmEvent,
            onDismiss = viewModel::onDismissEditDialog
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "VoiceCalendar AI",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Main content area - centered
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Sound wave animation behind the button
                    Box(
                        modifier = Modifier.size(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SoundWave(
                            isActive = uiState.state == VoiceState.LISTENING,
                            color = StatusListening
                        )

                        // Mic button
                        MicButton(
                            state = uiState.state,
                            onToggle = viewModel::onMicToggle
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Status indicator
                    StatusIndicator(
                        state = uiState.state,
                        message = uiState.partialText
                    )

                    // Partial text during listening
                    AnimatedVisibility(
                        visible = uiState.partialText.isNotBlank() &&
                                uiState.state == VoiceState.LISTENING,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = uiState.partialText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 8.dp, start = 32.dp, end = 32.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }

            // Upcoming events section
            AnimatedVisibility(
                visible = uiState.upcomingEvents.isNotEmpty() &&
                        uiState.state != VoiceState.LISTENING,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Upcoming Events",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(uiState.upcomingEvents) { event ->
                            EventCard(event = event, onClick = { })
                        }
                    }
                }
            }
        }
    }
}
