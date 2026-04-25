package com.vishal2376.snaptick.presentation.common

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.vishal2376.snaptick.ui.theme.SnaptickTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Pins the security-critical confirm dialog. Wrong wiring here means a
 * misclick or hostile document provider could silently destroy a user's
 * tasks, so each surface area gets its own assertion.
 */
class BackupRestoreConfirmDialogTest {

	@get:Rule val composeRule = createComposeRule()

	@Test fun renders_taskCount_and_warning_copy() {
		composeRule.setContent {
			SnaptickTheme {
				BackupRestoreConfirmDialog(
					taskCount = 42,
					droppedCount = 0,
					onConfirm = {},
					onDismiss = {},
				)
			}
		}

		composeRule.onNodeWithText("Restore backup?").assertIsDisplayed()
		composeRule.onNodeWithText("Restore 42").assertIsDisplayed()
		composeRule.onNodeWithText("Cancel").assertIsDisplayed()
		// Warning copy must mention that this is destructive.
		composeRule.onNodeWithText(
			"This will replace every task currently in Snaptick with " +
				"42 tasks from the selected backup file. This cannot be undone.",
		).assertIsDisplayed()
	}

	@Test fun renders_dropped_count_when_nonzero() {
		composeRule.setContent {
			SnaptickTheme {
				BackupRestoreConfirmDialog(
					taskCount = 10,
					droppedCount = 3,
					onConfirm = {},
					onDismiss = {},
				)
			}
		}

		composeRule.onNodeWithText(
			"This will replace every task currently in Snaptick with " +
				"10 tasks from the selected backup file (3 skipped). This cannot be undone.",
		).assertIsDisplayed()
	}

	@Test fun confirm_button_invokes_onConfirm() {
		var confirmed = 0
		var dismissed = 0
		composeRule.setContent {
			SnaptickTheme {
				BackupRestoreConfirmDialog(
					taskCount = 5,
					droppedCount = 0,
					onConfirm = { confirmed++ },
					onDismiss = { dismissed++ },
				)
			}
		}

		composeRule.onNodeWithText("Restore 5").performClick()

		assertEquals(1, confirmed)
		assertEquals(0, dismissed)
	}

	@Test fun cancel_button_invokes_onDismiss() {
		var confirmed = 0
		var dismissed = 0
		composeRule.setContent {
			SnaptickTheme {
				BackupRestoreConfirmDialog(
					taskCount = 5,
					droppedCount = 0,
					onConfirm = { confirmed++ },
					onDismiss = { dismissed++ },
				)
			}
		}

		composeRule.onNodeWithText("Cancel").performClick()

		assertEquals(0, confirmed)
		assertTrue("dismiss should fire at least once", dismissed >= 1)
	}

	@Test fun singular_task_count_uses_singular_copy() {
		composeRule.setContent {
			SnaptickTheme {
				BackupRestoreConfirmDialog(
					taskCount = 1,
					droppedCount = 0,
					onConfirm = {},
					onDismiss = {},
				)
			}
		}

		composeRule.onNodeWithText(
			"This will replace every task currently in Snaptick with " +
				"1 task from the selected backup file. This cannot be undone.",
		).assertIsDisplayed()
	}
}
