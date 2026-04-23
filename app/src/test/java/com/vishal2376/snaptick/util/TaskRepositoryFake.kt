package com.vishal2376.snaptick.util

import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.domain.model.Task
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class TaskRepositoryFake {
	private val tasks = MutableStateFlow<List<Task>>(emptyList())
	val repo: TaskRepository = mockk(relaxed = true)

	init {
		coEvery { repo.insertTask(any()) } answers {
			val t = firstArg<Task>()
			val assigned = if (t.id == 0) t.copy(id = (tasks.value.maxOfOrNull { it.id } ?: 0) + 1) else t
			tasks.value = tasks.value + assigned
		}
		coEvery { repo.updateTask(any()) } answers {
			val t = firstArg<Task>()
			tasks.value = tasks.value.map { if (it.id == t.id) t else it }
		}
		coEvery { repo.deleteTask(any()) } answers {
			val t = firstArg<Task>()
			tasks.value = tasks.value.filterNot { it.id == t.id }
		}
		coEvery { repo.deleteAllTasks() } answers { tasks.value = emptyList() }
		coEvery { repo.getTaskById(any()) } answers {
			val id = firstArg<Int>()
			tasks.value.firstOrNull { it.id == id }
		}
		val dateSlot = slot<LocalDate>()
		every { repo.getTasksByDate(capture(dateSlot)) } answers {
			val d = dateSlot.captured
			tasks.map { list -> list.filter { it.date == d } }
		}
		every { repo.getTodayTasks() } answers {
			tasks.map { list -> list.filter { it.date == LocalDate.now() } }
		}
		every { repo.getAllTasks() } returns tasks
		every { repo.getLastRepeatedTasks() } answers {
			tasks.value.filter { it.isRepeated && it.date < LocalDate.now() }
		}
	}

	fun seed(items: List<Task>) {
		tasks.value = items
	}

	fun current(): List<Task> = tasks.value
}
