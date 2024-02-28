package com.vishal2376.snaptick.worker

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.vishal2376.snaptick.data.local.TaskDatabase
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.util.Constants
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class RepeatTaskWorker(val context: Context, params: WorkerParameters) :
	CoroutineWorker(context, params) {

	override suspend fun doWork(): Result {
		try {
			val taskId = inputData.getInt(Constants.TASK_ID, -1)
			val dayOfWeek = LocalDate.now().dayOfWeek.value - 1 // because mon-1,sun-7

			if (taskId != -1) {
				val db = Room.databaseBuilder(
					applicationContext,
					TaskDatabase::class.java,
					"local_db"
				).build()

				val repository = TaskRepository(db.taskDao())
				
				val repeatedTaskList = repository.getRepeatedTasks()
				repeatedTaskList.collect { taskList ->
					taskList.forEach { task ->
						//repeat days of week
						val repeatWeekDays = task.getRepeatWeekList()
						if (task.reminder && repeatWeekDays.contains(dayOfWeek)) {
							//calculate delay
							val startTimeSec = task.startTime.toSecondOfDay()
							val currentTimeSec = LocalTime.now().toSecondOfDay()
							val delaySec = startTimeSec - currentTimeSec

							if (delaySec > 0) {
								// cancel old notification
								WorkManager.getInstance(applicationContext)
									.cancelAllWorkByTag(task.uuid)

								// new notification request
								val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
									.setInitialDelay(delaySec.toLong(), TimeUnit.SECONDS)
									.addTag(task.uuid)
									.build()
								WorkManager.getInstance(applicationContext).enqueue(workRequest)
							}
						}
					}
				}

				db.close()
				return Result.success()
			}
		} catch (e: Exception) {
			Log.e("@@@", "doWork: Error : ${e.message}")
		}
		return Result.failure()
	}
}