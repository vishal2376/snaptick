package com.vishal2376.snaptick.presentation.task_list.viewmodel

import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.task_list.action.TaskListAction
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
class TaskListViewModelTest {

	@get:Rule val mainRule = MainDispatcherRule()

	private lateinit var repoFake: TaskRepositoryFake
	private lateinit var scheduler: TaskReminderScheduler
	private lateinit var vm: TaskListViewModel

	private fun task(id: Int, completed: Boolean = false, reminder: Boolean = true) = Task(
		id = id, uuid = "u$id", title = "T$id",
		isCompleted = completed, reminder = reminder,
		startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0),
		date = LocalDate.now()
	)

	@Before fun setUp() {
		repoFake = TaskRepositoryFake()
		scheduler = mockk(relaxed = true)
		justRun { scheduler.schedule(any()) }
		justRun { scheduler.cancel(any()) }
		vm = TaskListViewModel(repoFake.repo, scheduler)
	}

	@Test fun `ToggleCompletion true updates task and cancels reminder`() = runTest {
		repoFake.seed(listOf(task(1)))
		vm.onAction(TaskListAction.ToggleCompletion(taskId = 1, isCompleted = true))
		advanceUntilIdle()
		assertTrue(repoFake.current().single().isCompleted)
		verify { scheduler.cancel("u1") }
	}

	@Test fun `ToggleCompletion false schedules reminder`() = runTest {
		repoFake.seed(listOf(task(1, completed = true)))
		vm.onAction(TaskListAction.ToggleCompletion(taskId = 1, isCompleted = false))
		advanceUntilIdle()
		verify { scheduler.schedule(match { it.id == 1 && !it.isCompleted }) }
	}

	@Test fun `SwipeTask deletes and UndoDelete restores`() = runTest {
		val t = task(1)
		repoFake.seed(listOf(t))
		vm.onAction(TaskListAction.SwipeTask(t))
		advanceUntilIdle()
		assertEquals(emptyList<Task>(), repoFake.current())
		vm.onAction(TaskListAction.UndoDelete)
		advanceUntilIdle()
		assertEquals(1, repoFake.current().size)
		verify { scheduler.cancel("u1") }
		verify { scheduler.schedule(match { it.id == 1 }) }
	}

	@Test fun `DeleteTask for missing id is no-op`() = runTest {
		repoFake.seed(emptyList())
		vm.onAction(TaskListAction.DeleteTask(taskId = 99))
		advanceUntilIdle()
		assertEquals(emptyList<Task>(), repoFake.current())
	}

	@Test fun `UndoDelete before any delete is safe`() = runTest {
		vm.onAction(TaskListAction.UndoDelete)
		advanceUntilIdle()
		assertEquals(emptyList<Task>(), repoFake.current())
	}
}
