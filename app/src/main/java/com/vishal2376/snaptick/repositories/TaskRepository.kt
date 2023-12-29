package com.vishal2376.snaptick.repositories

import com.vishal2376.snaptick.data.local.TaskDao
import com.vishal2376.snaptick.domain.model.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) {
	suspend fun insertTask(task: Task) {
		dao.insertTask(task)
	}

	suspend fun deleteTask(task: Task) {
		dao.deleteTask(task)
	}

	suspend fun getTaskById(id: Int): Task {
		return dao.getTaskById(id)
	}

	fun getAllTasks(): Flow<List<Task>> {
		return dao.getAllTasks()
	}
}