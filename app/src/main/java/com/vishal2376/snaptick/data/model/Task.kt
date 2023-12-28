package com.vishal2376.snaptick.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
	@PrimaryKey val id: Int? = 0,
	val title: String,
	val isCompleted: Boolean,
	val startTime: String,
	val endTime: String,
)
