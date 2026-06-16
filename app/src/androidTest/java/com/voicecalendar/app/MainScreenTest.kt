package com.voicecalendar.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.voicecalendar.core.ui.components.StatusIndicator
import com.voicecalendar.core.ui.components.VoiceState
import com.voicecalendar.core.ui.theme.VoiceCalendarTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun statusIndicator_showsIdleText() {
        composeTestRule.setContent {
            VoiceCalendarTheme {
                StatusIndicator(state = VoiceState.IDLE)
            }
        }

        composeTestRule.onNodeWithText("Tap to speak").assertIsDisplayed()
    }

    @Test
    fun statusIndicator_showsListeningText() {
        composeTestRule.setContent {
            VoiceCalendarTheme {
                StatusIndicator(state = VoiceState.LISTENING)
            }
        }

        composeTestRule.onNodeWithText("Listening...").assertIsDisplayed()
    }

    @Test
    fun statusIndicator_showsProcessingText() {
        composeTestRule.setContent {
            VoiceCalendarTheme {
                StatusIndicator(state = VoiceState.PROCESSING)
            }
        }

        composeTestRule.onNodeWithText("Processing...").assertIsDisplayed()
    }

    @Test
    fun statusIndicator_showsErrorText() {
        composeTestRule.setContent {
            VoiceCalendarTheme {
                StatusIndicator(state = VoiceState.ERROR, message = "Test error")
            }
        }

        composeTestRule.onNodeWithText("Test error").assertIsDisplayed()
    }

    @Test
    fun statusIndicator_showsCompletedText() {
        composeTestRule.setContent {
            VoiceCalendarTheme {
                StatusIndicator(state = VoiceState.COMPLETED)
            }
        }

        composeTestRule.onNodeWithText("Event saved!").assertIsDisplayed()
    }

    @Test
    fun statusIndicator_showsSavingText() {
        composeTestRule.setContent {
            VoiceCalendarTheme {
                StatusIndicator(state = VoiceState.SAVING)
            }
        }

        composeTestRule.onNodeWithText("Saving...").assertIsDisplayed()
    }
}
