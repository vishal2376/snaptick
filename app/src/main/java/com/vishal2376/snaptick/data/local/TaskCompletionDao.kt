package com.vishal2376.snaptick.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskCompletionDao {

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insert(completion: TaskCompletion)

	@Query("DELETE FROM task_completions WHERE uuid = :uuid AND date = :date")
	suspend fun delete(uuid: String, date: String)

	@Query("DELETE FROM task_completions WHERE uuid = :uuid")
	suspend fun deleteAllForTask(uuid: String)

	@Query("SELECT EXISTS(SELECT 1 FROM task_completions WHERE uuid = :uuid AND date = :date)")
	suspend fun isCompleted(uuid: String, date: String): Boolean

	@Query("SELECT uuid FROM task_completions WHERE date = :date")
	fun completedUuidsOn(date: String): Flow<List<String>>
}
