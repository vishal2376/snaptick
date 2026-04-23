package com.vishal2376.snaptick.presentation.add_edit_screen.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.add_edit_screen.action.AddEditAction
import com.vishal2376.snaptick.presentation.add_edit_screen.events.AddEditEvent
import com.vishal2376.snaptick.presentation.common.Priority
import com.vishal2376.snaptick.util.MainDispatcherRule
import com.vishal2376.snaptick.util.TaskRepositoryFake
import com.vishal2376.snaptick.util.TaskReminderScheduler
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditViewModelTest {

	@get:Rule val mainRule = MainDispatcherRule()

	private lateinit var repoFake: TaskRepositoryFake
	private lateinit var scheduler: TaskReminderScheduler

	private fun buildVm(savedStateHandle: SavedStateHandle = SavedStateHandle(mapOf("id" to -1))): AddEditViewModel =
		AddEditViewModel(repoFake.repo, scheduler, savedStateHandle)

	@Before fun setUp() {
		repoFake = TaskRepositoryFake()
		scheduler = mockk(relaxed = true)
		justRun { scheduler.schedule(any()) }
		justRun { scheduler.cancel(any()) }
	}

	@Test fun `id -1 starts with blank state`() = runTest {
		val vm = buildVm()
		advanceUntilIdle()
		assertEquals(0, vm.state.value.taskId)
		assertEquals("", vm.state.value.title)
	}

	@Test fun `id loads existing task into state`() = runTest {
		val existing = Task(id = 5, uuid = "u5", title = "Ring", startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0), date = LocalDate.now())
		repoFake.seed(listOf(existing))
		val vm = buildVm(SavedStateHandle(mapOf("id" to 5)))
		advanceUntilIdle()
		assertEquals(5, vm.state.value.taskId)
		assertEquals("Ring", vm.state.value.title)
	}

	@Test fun `UpdateTitle mutates state title`() = runTest {
		val vm = buildVm()
		vm.onAction(AddEditAction.UpdateTitle("foo"))
		assertEquals("foo", vm.state.value.title)
	}

	@Test fun `UpdateAllDay true syncs endTime with startTime`() = runTest {
		val vm = buildVm()
		vm.onAction(AddEditAction.UpdateStartTime(LocalTime.of(11, 0)))
		vm.onAction(AddEditAction.UpdateAllDay(true))
		val s = vm.state.value
		assertTrue(s.isAllDay)
		assertEquals(s.startTime, s.endTime)
	}

	@Test fun `UpdateDurationMinutes sets endTime and bumps timeUpdateTick`() = runTest {
		val vm = buildVm()
		val tickBefore = vm.state.value.timeUpdateTick
		vm.onAction(AddEditAction.UpdateStartTime(LocalTime.of(9, 0)))
		vm.onAction(AddEditAction.UpdateDurationMinutes(90))
		val s = vm.state.value
		assertEquals(90L, s.duration)
		assertEquals(LocalTime.of(10, 30), s.endTime)
		assertEquals(tickBefore + 1, s.timeUpdateTick)
	}

	@Test fun `SaveTask inserts, schedules reminder, emits TaskSaved`() = runTest {
		val vm = buildVm()
		vm.onAction(AddEditAction.UpdateTitle("Run"))
		vm.onAction(AddEditAction.UpdateStartTime(LocalTime.of(9, 0)))
		vm.onAction(AddEditAction.UpdateEndTime(LocalTime.of(10, 0)))
		vm.events.test {
			vm.onAction(AddEditAction.SaveTask)
			assertEquals(AddEditEvent.TaskSaved, awaitItem())
		}
		advanceUntilIdle()
		assertEquals(1, repoFake.current().size)
		verify { scheduler.schedule(match { it.title == "Run" }) }
	}

	@Test fun `UpdateTask with reminder off cancels and emits TaskUpdated`() = runTest {
		val t = Task(id = 7, uuid = "u7", title = "X", reminder = false, startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0), date = LocalDate.now())
		repoFake.seed(listOf(t))
		val vm = buildVm(SavedStateHandle(mapOf("id" to 7)))
		advanceUntilIdle()
		vm.onAction(AddEditAction.UpdateTitle("Y"))
		vm.events.test {
			vm.onAction(AddEditAction.UpdateTask)
			assertEquals(AddEditEvent.TaskUpdated, awaitItem())
		}
		advanceUntilIdle()
		assertEquals("Y", repoFake.current().single().title)
		verify { scheduler.cancel("u7") }
	}

	@Test fun `DeleteTask removes and emits TaskDeleted`() = runTest {
		val t = Task(id = 3, uuid = "u3", title = "Del", startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0), date = LocalDate.now())
		repoFake.seed(listOf(t))
		val vm = buildVm(SavedStateHandle(mapOf("id" to 3)))
		advanceUntilIdle()
		vm.events.test {
			vm.onAction(AddEditAction.DeleteTask)
			assertEquals(AddEditEvent.TaskDeleted, awaitItem())
		}
		advanceUntilIdle()
		assertEquals(emptyList<Task>(), repoFake.current())
		verify { scheduler.cancel("u3") }
	}

	@Test fun `UpdatePriority sets state priority`() = runTest {
		val vm = buildVm()
		vm.onAction(AddEditAction.UpdatePriority(Priority.HIGH))
		assertEquals(Priority.HIGH, vm.state.value.priority)
	}
}
