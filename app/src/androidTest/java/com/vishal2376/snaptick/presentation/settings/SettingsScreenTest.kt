package com.vishal2376.snaptick.presentation.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vishal2376.snaptick.presentation.main.state.MainState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

	@get:Rule val composeRule = createComposeRule()

	@Test fun aboutRow_invokesOnClickAbout() {
		var aboutClicks = 0
		composeRule.setContent {
			SettingsScreen(
				appState = MainState(),
				onAction = {},
				onClickAbout = { aboutClicks++ },
				onBack = {}
			)
		}
		composeRule.onNodeWithText("About").assertIsDisplayed().performClick()
		assertTrue(aboutClicks == 1)
	}

	@Test fun rendersSettingsCategoriesHeader() {
		composeRule.setContent {
			SettingsScreen(
				appState = MainState(),
				onAction = {},
				onClickAbout = {},
				onBack = {}
			)
		}
		composeRule.onNodeWithText("Settings").assertIsDisplayed()
	}
}
