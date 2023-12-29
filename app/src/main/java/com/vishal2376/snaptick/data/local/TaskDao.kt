package com.vishal2376.snaptick.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.vishal2376.snaptick.domain.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
	@Insert
	suspend fun insertTask(task: Task)

	@Delete
	suspend fun deleteTask(task: Task)

	@Query("SELECT * FROM task_table WHERE id=:id")
	suspend fun getTaskById(id: Int): Task

	@Query("SELECT * FROM task_table")
	fun getAllTasks(): Flow<List<Task>>
}