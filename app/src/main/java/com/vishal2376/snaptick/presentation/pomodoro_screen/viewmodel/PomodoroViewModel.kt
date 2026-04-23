package com.vishal2376.snaptick.presentation.pomodoro_screen.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.pomodoro_screen.action.PomodoroAction
import com.vishal2376.snaptick.presentation.pomodoro_screen.events.PomodoroEvent
import com.vishal2376.snaptick.presentation.pomodoro_screen.state.PomodoroState
import com.vishal2376.snaptick.util.TaskReminderScheduler
import com.vishal2376.snaptick.util.vibrateDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PomodoroViewModel @Inject constructor(
	@ApplicationContext private val context: Context,
	private val repository: TaskRepository,
	private val reminderScheduler: TaskReminderScheduler,
	savedStateHandle: SavedStateHandle,
) : ViewModel() {

	private val _state = MutableStateFlow(PomodoroState())
	val state = _state.asStateFlow()

	private val _events = MutableSharedFlow<PomodoroEvent>(extraBufferCapacity = 1)
	val events = _events.asSharedFlow()

	private var currentTask: Task? = null
	private var tickerJob: Job? = null

	init {
		val taskId = savedStateHandle.get<Int>("id") ?: -1
		if (taskId > 0) {
			viewModelScope.launch {
				val task = repository.getTaskById(taskId) ?: return@launch
				currentTask = task
				val total = task.getDuration()
				val resuming = task.pomodoroTimer != -1
				val left = if (resuming) task.pomodoroTimer.toLong() else total
				_state.value = PomodoroState(
					taskId = task.id,
					taskTitle = task.title,
					totalTime = total,
					timeLeft = left,
					isPaused = false,
					isReset = false,
					isCompleted = left <= 0L
				)
				if (resuming) _events.emit(PomodoroEvent.ResumingPreviousSession)
				startTicker()
			}
		}
	}

	fun onAction(action: PomodoroAction) {
		when (action) {
			is PomodoroAction.TogglePause -> _state.update {
				it.copy(isPaused = !it.isPaused, isReset = false)
			}
			is PomodoroAction.Reset -> _state.update {
				it.copy(isPaused = true, isReset = true, timeLeft = it.totalTime, isCompleted = false)
			}
			is PomodoroAction.MarkCompleted -> markCompleted()
		}
	}

	private fun startTicker() {
		tickerJob?.cancel()
		tickerJob = viewModelScope.launch {
			_state
				.map { it.isPaused || it.isCompleted || it.timeLeft <= 0 }
				.distinctUntilChanged()
				.collectLatest { stopped ->
					if (stopped) return@collectLatest
					while (isActive) {
						delay(1000L)
						val cur = _state.value
						if (cur.isPaused || cur.isCompleted || cur.timeLeft <= 0) return@collectLatest
						val newLeft = cur.timeLeft - 1
						_state.update {
							it.copy(
								timeLeft = newLeft,
								isCompleted = newLeft <= 0
							)
						}
						if (newLeft <= 0) {
							vibrateDevice(context)
							_events.emit(PomodoroEvent.TimerCompleted)
						}
					}
				}
		}
	}

	private fun markCompleted() {
		val task = currentTask ?: return
		viewModelScope.launch {
			val updated = task.copy(isCompleted = true, pomodoroTimer = -1)
			repository.updateTask(updated)
			reminderScheduler.cancel(task.uuid)
			_events.emit(PomodoroEvent.TaskMarkedCompleted)
		}
	}

	@OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
	override fun onCleared() {
		super.onCleared()
		tickerJob?.cancel()
		val task = currentTask ?: return
		val s = _state.value
		val toSave = if (task.isValidPomodoroSession(s.timeLeft))
			task.copy(pomodoroTimer = s.timeLeft.toInt())
		else
			task.copy(pomodoroTimer = -1)
		GlobalScope.launch(Dispatchers.IO) { repository.updateTask(toSave) }
	}
}
