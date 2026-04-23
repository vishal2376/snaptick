package com.vishal2376.snaptick.presentation.pomodoro_screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vishal2376.snaptick.presentation.pomodoro_screen.state.PomodoroState
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PomodoroScreenTest {

	@get:Rule val composeRule = createComposeRule()

	@Test fun timeLeft_rendersFormattedAsMmss() {
		composeRule.setContent {
			PomodoroScreen(
				state = PomodoroState(
					taskTitle = "Focus",
					totalTime = 1500L,
					timeLeft = 1500L,
					isPaused = true
				),
				events = emptyFlow(),
				onAction = {},
				onBack = {}
			)
		}
		composeRule.onNodeWithText("25:00").assertIsDisplayed()
		composeRule.onNodeWithText("Focus").assertIsDisplayed()
	}

	@Test fun completedState_rendersCompletedText() {
		composeRule.setContent {
			PomodoroScreen(
				state = PomodoroState(
					taskTitle = "Focus",
					totalTime = 1500L,
					timeLeft = 0L,
					isCompleted = true
				),
				events = emptyFlow(),
				onAction = {},
				onBack = {}
			)
		}
		composeRule.onNodeWithText("Completed").assertIsDisplayed()
	}
}
