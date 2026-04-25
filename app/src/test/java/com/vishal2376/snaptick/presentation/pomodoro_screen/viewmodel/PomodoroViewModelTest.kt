package com.vishal2376.snaptick.presentation.pomodoro_screen.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.presentation.pomodoro_screen.action.PomodoroAction
import com.vishal2376.snaptick.presentation.pomodoro_screen.events.PomodoroEvent
import com.vishal2376.snaptick.util.MainDispatcherRule
import com.vishal2376.snaptick.util.TaskRepositoryFake
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class PomodoroViewModelTest {

	@get:Rule val mainRule = MainDispatcherRule()

	private lateinit var context: Context
	private lateinit var repoFake: TaskRepositoryFake

	private fun task(pomodoroTimer: Int = -1) = Task(
		id = 1, uuid = "u1", title = "Focus",
		startTime = LocalTime.of(9, 0), endTime = LocalTime.of(9, 30),
		pomodoroTimer = pomodoroTimer, date = LocalDate.now()
	)

	@Before fun setUp() {
		context = mockk(relaxed = true)
		mockkStatic("com.vishal2376.snaptick.util.UtilsKt")
		every { com.vishal2376.snaptick.util.vibrateDevice(any(), any(), any()) } returns Unit
		repoFake = TaskRepositoryFake()
	}

	@After fun tearDown() { unmockkStatic("com.vishal2376.snaptick.util.UtilsKt") }

	private fun buildVm(id: Int = 1) = PomodoroViewModel(
		context, repoFake.repo, SavedStateHandle(mapOf("id" to id))
	)

	// Let init load DB + spin ticker loop a negligible amount.
	private suspend fun kotlinx.coroutines.test.TestScope.letInitSettle() {
		runCurrent()
		advanceTimeBy(50)
		runCurrent()
	}

	@Test(timeout = 5_000) fun `loads task with fresh timer`() = runTest(timeout = 5.seconds) {
		repoFake.seed(listOf(task()))
		val vm = buildVm()
		letInitSettle()
		val s = vm.state.value
		assertEquals(1800L, s.totalTime)
		assertEquals(1800L, s.timeLeft)
		assertFalse(s.isCompleted)
	}

	@Test(timeout = 5_000) fun `resumes previous session when pomodoroTimer set`() = runTest(timeout = 5.seconds) {
		repoFake.seed(listOf(task(pomodoroTimer = 600)))
		val vm = buildVm()
		vm.events.test {
			letInitSettle()
			assertEquals(PomodoroEvent.ResumingPreviousSession, awaitItem())
		}
		assertEquals(600L, vm.state.value.timeLeft)
	}

	@Test(timeout = 5_000) fun `ticker decrements timeLeft after 1 second`() = runTest(timeout = 5.seconds) {
		repoFake.seed(listOf(task()))
		val vm = buildVm()
		letInitSettle()
		val before = vm.state.value.timeLeft
		advanceTimeBy(1100)
		runCurrent()
		assertEquals(before - 1, vm.state.value.timeLeft)
	}

	@Test(timeout = 5_000) fun `TogglePause stops the ticker`() = runTest(timeout = 5.seconds) {
		repoFake.seed(listOf(task()))
		val vm = buildVm()
		letInitSettle()
		vm.onAction(PomodoroAction.TogglePause)
		runCurrent()
		val paused = vm.state.value.timeLeft
		advanceTimeBy(3000)
		runCurrent()
		assertEquals(paused, vm.state.value.timeLeft)
		assertTrue(vm.state.value.isPaused)
	}

	@Test(timeout = 5_000) fun `Reset restores timeLeft to totalTime and pauses`() = runTest(timeout = 5.seconds) {
		repoFake.seed(listOf(task()))
		val vm = buildVm()
		letInitSettle()
		advanceTimeBy(5000)
		runCurrent()
		vm.onAction(PomodoroAction.Reset)
		runCurrent()
		val s = vm.state.value
		assertTrue(s.isPaused)
		assertEquals(s.totalTime, s.timeLeft)
		assertFalse(s.isCompleted)
	}

	@Test(timeout = 5_000) fun `MarkCompleted updates repo and emits TaskMarkedCompleted`() = runTest(timeout = 5.seconds) {
		repoFake.seed(listOf(task()))
		val vm = buildVm()
		letInitSettle()
		vm.events.test {
			vm.onAction(PomodoroAction.MarkCompleted)
			runCurrent()
			assertEquals(PomodoroEvent.TaskMarkedCompleted, awaitItem())
		}
		assertTrue(repoFake.current().single().isCompleted)
	}
}
