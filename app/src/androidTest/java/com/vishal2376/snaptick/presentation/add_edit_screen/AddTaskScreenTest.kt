package com.vishal2376.snaptick.presentation.add_edit_screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vishal2376.snaptick.presentation.add_edit_screen.action.AddEditAction
import com.vishal2376.snaptick.presentation.add_edit_screen.state.AddEditState
import com.vishal2376.snaptick.presentation.main.state.MainState
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddTaskScreenTest {

	@get:Rule val composeRule = createComposeRule()

	@Test fun titleInput_dispatchesUpdateTitle() {
		val dispatched = mutableListOf<AddEditAction>()
		composeRule.setContent {
			AddTaskScreen(
				state = AddEditState(),
				events = emptyFlow(),
				appState = MainState(),
				onAction = { dispatched += it },
				onMainAction = {},
				onBack = {}
			)
		}
		composeRule.onNodeWithText("What would you like to do ?").assertIsDisplayed()
		composeRule.onNodeWithText("What would you like to do ?").performTextInput("Run")
		assertTrue(dispatched.any { it is AddEditAction.UpdateTitle && it.title == "Run" })
	}

	@Test fun saveButton_dispatchesSaveTask_whenTitlePresent() {
		val dispatched = mutableListOf<AddEditAction>()
		composeRule.setContent {
			AddTaskScreen(
				state = AddEditState(title = "Morning Run"),
				events = emptyFlow(),
				appState = MainState(),
				onAction = { dispatched += it },
				onMainAction = {},
				onBack = {}
			)
		}
		// two nodes have "Add Task": top-bar title + button. Pick the clickable one.
		composeRule.onAllNodesWithText("Add Task").filter(hasClickAction())[0].performClick()
		assertTrue(dispatched.any { it === AddEditAction.SaveTask })
	}
}
