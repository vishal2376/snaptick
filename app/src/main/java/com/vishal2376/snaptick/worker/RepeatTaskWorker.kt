package com.vishal2376.snaptick.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.util.Constants
import java.time.LocalDate
import javax.inject.Inject

class RepeatTaskWorker @Inject constructor(
	context: Context,
	params: WorkerParameters,
	private val repository: TaskRepository
) : CoroutineWorker(context, params) {

	override suspend fun doWork(): Result {
		val taskString = inputData.getString(Constants.TASK)

		if (!taskString.isNullOrEmpty()) {
			var task = Gson().fromJson(taskString, Task::class.java)
			task = task.copy(date = LocalDate.now())
			repository.insertTask(task)
			return Result.success()
		}

		return Result.failure()
	}
}