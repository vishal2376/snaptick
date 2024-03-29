package com.vishal2376.snaptick.widget.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.widget.SnaptickWidget
import com.vishal2376.snaptick.widget.SnaptickWidgetState
import com.vishal2376.snaptick.widget.model.toWidgetTask
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate

private const val LOGGER = "WIDGET_DATA_WORKER"

@HiltWorker
class WidgetTaskUpdateDataWorker @AssistedInject constructor(
	@Assisted private val context: Context,
	@Assisted private val params: WorkerParameters,
	private val taskRepository: TaskRepository,
) : CoroutineWorker(context, params) {

	override suspend fun doWork(): Result {
		// fetch today's tasks
		return withContext(Dispatchers.IO) {
			try {
				val tasks = taskRepository.getTodayTasks().first()
				val dayOfWeek = LocalDate.now().dayOfWeek.value - 1
				// get repeatable tasks
				val incompleteTasks = tasks.filter { task ->
					if (task.isRepeated) task.getRepeatWeekList().contains(dayOfWeek)
					else true
				}.filter { !it.isCompleted }.map(Task::toWidgetTask)
//				val inCompletedTasks = updatedTodayTasks.filter { !it.isCompleted }
				//update the data for the widget state
				Log.d(LOGGER, "ALL_TASKS $tasks")
				Log.d(LOGGER, "FEW_TASKS $incompleteTasks")

				SnaptickWidgetState.updateData(context, incompleteTasks)
				//update the widget
				SnaptickWidget.updateAll(context)
				// results success
				Result.success(
					workDataOf(
						WorkerConstants.WIDGET_DATA_WORKER_SUCCESS_KEY to
								WorkerConstants.WIDGET_DATA_WORKER_SUCCESS_VALUE
					)
				)
			} catch (e: Exception) {
				e.printStackTrace()
				Result.failure(
					workDataOf(WorkerConstants.WIDGET_DATA_WORKER_ERROR_KEY to e.message)
				)
			}
		}
	}

	companion object {

		private const val WORKER_NAME = "WIDGET_DATA_WORKER"

		private val updateWorker = OneTimeWorkRequestBuilder<WidgetTaskUpdateDataWorker>()
			.addTag(WorkerConstants.WIDGET_WORKER)
			.build()

		fun enqueueWorker(
			context: Context,
			policy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE
		) {
			val manager = WorkManager.getInstance(context)
			manager.enqueueUniqueWork(WORKER_NAME, policy, updateWorker)
		}

		fun cancelWorker(context: Context) {
			val manager = WorkManager.getInstance(context)
			// it will cancel the work if not in complete state
			manager.cancelWorkById(updateWorker.id)
		}
	}
}