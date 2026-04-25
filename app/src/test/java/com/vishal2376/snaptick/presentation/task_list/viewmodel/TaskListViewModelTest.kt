package com.vishal2376.snaptick.presentation.task_list.viewmodel

import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.task_list.action.TaskListAction
import com.vishal2376.snaptick.util.MainDispatcherRule
import com.vishal2376.snaptick.util.TaskRepositoryFake
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
	private lateinit var vm: TaskListViewModel

	private fun task(
		id: Int,
		completed: Boolean = false,
		reminder: Boolean = true,
		repeated: Boolean = false,
	) = Task(
		id = id, uuid = "u$id", title = "T$id",
		isCompleted = completed, reminder = reminder, isRepeated = repeated,
		repeatWeekdays = if (repeated) "0,1,2,3,4,5,6" else "",
		startTime = LocalTime.of(9, 0), endTime = LocalTime.of(10, 0),
		date = LocalDate.now()
	)

	@Before fun setUp() {
		repoFake = TaskRepositoryFake()
		vm = TaskListViewModel(repoFake.repo)
	}

	@Test fun `ToggleCompletion on one-off updates isCompleted via updateTask`() = runTest {
		repoFake.seed(listOf(task(1)))
		vm.onAction(TaskListAction.ToggleCompletion(taskId = 1, isCompleted = true))
		advanceUntilIdle()
		assertTrue(repoFake.current().single().isCompleted)
	}

	@Test fun `ToggleCompletion on repeat task writes per-date completion not isCompleted`() = runTest {
		repoFake.seed(listOf(task(1, repeated = true)))
		vm.onAction(TaskListAction.ToggleCompletion(taskId = 1, isCompleted = true))
		advanceUntilIdle()
		// Template's isCompleted MUST stay false; per-date flag goes elsewhere.
		assertFalse(repoFake.current().single().isCompleted)
		coVerify { repoFake.repo.markCompletedForDate("u1", LocalDate.now()) }
	}

	@Test fun `ToggleCompletion off on repeat clears the date completion`() = runTest {
		repoFake.seed(listOf(task(1, repeated = true)))
		vm.onAction(TaskListAction.ToggleCompletion(taskId = 1, isCompleted = false))
		advanceUntilIdle()
		coVerify { repoFake.repo.unmarkCompletedForDate("u1", LocalDate.now()) }
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
