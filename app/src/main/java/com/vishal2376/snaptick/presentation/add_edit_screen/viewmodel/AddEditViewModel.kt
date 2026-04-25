package com.vishal2376.snaptick.presentation.add_edit_screen.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.presentation.add_edit_screen.action.AddEditAction
import com.vishal2376.snaptick.presentation.add_edit_screen.events.AddEditEvent
import com.vishal2376.snaptick.presentation.add_edit_screen.state.AddEditState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
	private val repository: TaskRepository,
	savedStateHandle: SavedStateHandle,
) : ViewModel() {

	private val _state = MutableStateFlow(AddEditState())
	val state = _state.asStateFlow()

	private val _events = MutableSharedFlow<AddEditEvent>(extraBufferCapacity = 1)
	val events = _events.asSharedFlow()

	init {
		val taskId: Int = savedStateHandle.get<Int>("id") ?: -1
		if (taskId > 0) {
			viewModelScope.launch {
				repository.getTaskById(taskId)?.let { task ->
					_state.value = AddEditState.fromTask(task)
				}
			}
		}
	}

	fun onAction(action: AddEditAction) {
		when (action) {
			is AddEditAction.UpdateTitle -> _state.update { it.copy(title = action.title) }
			is AddEditAction.UpdateStartTime -> _state.update { it.copy(startTime = action.time) }
			is AddEditAction.UpdateEndTime -> _state.update { it.copy(endTime = action.time) }
			is AddEditAction.UpdateDate -> _state.update { it.copy(date = action.date) }
			is AddEditAction.UpdateReminder -> _state.update { it.copy(reminder = action.enabled) }
			is AddEditAction.UpdateAllDay -> _state.update {
				it.copy(isAllDay = action.enabled, endTime = if (action.enabled) it.startTime else it.endTime)
			}
			is AddEditAction.UpdateRepeated -> _state.update { it.copy(isRepeated = action.enabled) }
			is AddEditAction.UpdateRepeatWeekDays -> _state.update { it.copy(repeatWeekdays = action.weekDays) }
			is AddEditAction.UpdatePriority -> _state.update { it.copy(priority = action.priority) }
			is AddEditAction.UpdateDurationMinutes -> _state.update {
				it.copy(
					duration = action.minutes,
					endTime = it.startTime.plusMinutes(action.minutes),
					timeUpdateTick = it.timeUpdateTick + 1
				)
			}
			is AddEditAction.ResetPomodoroTimer -> _state.update { it.copy(pomodoroTimer = -1) }
			is AddEditAction.SaveTask -> saveTask()
			is AddEditAction.UpdateTask -> updateTask()
			is AddEditAction.DeleteTask -> deleteTask()
		}
	}

	private fun saveTask() {
		viewModelScope.launch {
			val task = _state.value.toTask()
			repository.insertTask(task)
			_events.emit(AddEditEvent.TaskSaved)
		}
	}

	private fun updateTask() {
		viewModelScope.launch {
			val task = _state.value.toTask()
			repository.updateTask(task)
			_events.emit(AddEditEvent.TaskUpdated)
		}
	}

	private fun deleteTask() {
		viewModelScope.launch {
			val task = _state.value.toTask()
			repository.deleteTask(task)
			_events.emit(AddEditEvent.TaskDeleted)
		}
	}
}
