package com.vishal2376.snaptick.presentation.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

/**
 * Stage-2 confirmation for the backup restore flow. Renders only when
 * `MainState.pendingRestore` is non-null. Shows the parsed task count plus
 * any rows that were dropped due to malformed dates, and forces the user to
 * acknowledge that confirming will wipe their existing tasks.
 *
 * Wiring lives in `AppNavigation`; the dialog itself is a thin
 * [AlertDialog] so the rest of the app's UI shell stays untouched.
 */
@Composable
fun BackupRestoreConfirmDialog(
	taskCount: Int,
	droppedCount: Int,
	onConfirm: () -> Unit,
	onDismiss: () -> Unit,
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(onClick = onConfirm) {
				Text(
					text = "Restore $taskCount",
					color = MaterialTheme.colorScheme.primary,
				)
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(
					text = "Cancel",
					color = MaterialTheme.colorScheme.onPrimaryContainer,
				)
			}
		},
		title = {
			Text(text = "Restore backup?")
		},
		text = {
			val dropNote = if (droppedCount > 0) " ($droppedCount skipped)" else ""
			Text(
				text = "This will replace every task currently in Snaptick with " +
					"$taskCount task${if (taskCount == 1) "" else "s"} from the " +
					"selected backup file$dropNote. This cannot be undone.",
			)
		},
	)
}
