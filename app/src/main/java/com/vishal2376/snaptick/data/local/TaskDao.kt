package com.vishal2376.snaptick.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vishal2376.snaptick.domain.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertTask(task: Task)

	@Delete
	suspend fun deleteTask(task: Task)

	@Update(onConflict = OnConflictStrategy.REPLACE)
	suspend fun updateTask(task: Task)

	@Query("SELECT * FROM task_table WHERE id=:id")
	suspend fun getTaskById(id: Int): Task

	@Query("SELECT * FROM task_table")
	fun getAllTasks(): Flow<List<Task>>

	@Query("SELECT * FROM task_table WHERE date = :selectedDate")
	fun getTasksByDate(selectedDate: String): Flow<List<Task>>

	@Query("SELECT * FROM task_table WHERE date = :selectedDate OR isRepeated = 1")
	fun getTodayTasks(selectedDate: String): Flow<List<Task>>

	@Query("DELETE FROM task_table")
	suspend fun deleteAllTasks()

}