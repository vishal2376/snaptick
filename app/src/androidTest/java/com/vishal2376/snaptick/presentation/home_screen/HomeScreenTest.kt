package com.vishal2376.snaptick.presentation.home_screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.main.state.MainState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

	@get:Rule val composeRule = createComposeRule()

	private fun task(id: Int, title: String) = Task(
		id = id, uuid = "u$id", title = title,
		startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0),
		date = LocalDate.now()
	)

	@Test fun rendersTaskTitles() {
		composeRule.setContent {
			HomeScreen(
				tasks = listOf(task(1, "Stand up"), task(2, "Lunch")),
				appState = MainState(),
				onAction = {},
				onTaskAction = {},
				onNavigate = {},
				onBackupData = {},
				onRestoreData = {}
			)
		}
		composeRule.onNodeWithText("Stand up").assertIsDisplayed()
		composeRule.onNodeWithText("Lunch").assertIsDisplayed()
	}
}
