package com.vishal2376.snaptick.data.repositories

import android.content.Context
import com.vishal2376.snaptick.data.local.TaskDao
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.widget.worker.WidgetUpdateWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate

class TaskRepository(
	private val dao: TaskDao,
	private val context: Context
) {
	suspend fun insertTask(task: Task) {
		dao.insertTask(task)
		WidgetUpdateWorker.enqueueWorker(context)
	}

	suspend fun deleteTask(task: Task) {
		dao.deleteTask(task)
		WidgetUpdateWorker.enqueueWorker(context)
	}

	suspend fun updateTask(task: Task) {
		dao.updateTask(task)
		WidgetUpdateWorker.enqueueWorker(context)
	}

	suspend fun getTaskById(id: Int): Task? {
		return dao.getTaskById(id)
	}

	suspend fun deleteAllTasks() {
		dao.deleteAllTasks()
		WidgetUpdateWorker.enqueueWorker(context)
	}

	fun getTasksByDate(selectedDate: LocalDate): Flow<List<Task>> {
		return dao.getTasksByDate(selectedDate.toString())
	}

	fun getTodayTasks(): Flow<List<Task>> {
		return dao.getTasksByDate(LocalDate.now().toString())
	}

	fun getLastRepeatedTasks(): List<Task> {
		val today = LocalDate.now().toString()
		return dao.getLastRepeatedTasks(today)
	}

	fun getAllTasks(): Flow<List<Task>> {
		return dao.getAllTasks().onEach {
			WidgetUpdateWorker.enqueueWorker(context)
		}
	}
}
