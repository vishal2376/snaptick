package com.vishal2376.snaptick.data.repositories

import com.vishal2376.snaptick.data.calendar.CalendarSyncManager
import com.vishal2376.snaptick.data.local.TaskDao
import com.vishal2376.snaptick.domain.interactor.AppWidgetInteractor
import com.vishal2376.snaptick.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate

class TaskRepository(
	private val dao: TaskDao,
	private val interactor: AppWidgetInteractor,
	private val calendarSyncManager: CalendarSyncManager
) {
	suspend fun insertTask(task: Task): Task {
		// Sync to calendar first (if enabled) to get eventId
		val syncedTask = calendarSyncManager.syncTaskToCalendar(task)
		dao.insertTask(syncedTask)
		// Update widget when a task is added
		interactor.enqueueWidgetDataWorker()
		return syncedTask
	}

	suspend fun deleteTask(task: Task) {
		// Delete calendar event first
		calendarSyncManager.deleteCalendarEvent(task)
		dao.deleteTask(task)
		// Update widget when a task is deleted
		interactor.enqueueWidgetDataWorker()
	}

	suspend fun updateTask(task: Task): Task {
		// Sync to calendar (will update existing event or create new one)
		val syncedTask = calendarSyncManager.syncTaskToCalendar(task)
		dao.updateTask(syncedTask)
		// Update widget when a task is updated (e.g., marked complete)
		interactor.enqueueWidgetDataWorker()
		return syncedTask
	}

	suspend fun getTaskById(id: Int): Task? {
		return dao.getTaskById(id)
	}

	suspend fun deleteAllTasks() {
		// Note: We don't delete calendar events for bulk delete
		// User can manually manage calendar
		dao.deleteAllTasks()
		// Update widget when all tasks are deleted
		interactor.enqueueWidgetDataWorker()
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
			//on each emit enqueue the worker
			// thus widget get updated on any of CRUD operation
			interactor.enqueueWidgetDataWorker()
		}
	}
}