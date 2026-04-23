package com.vishal2376.snaptick.util

import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalTime

class SettingsStoreFake {
	val store: SettingsStore = mockk(relaxed = true)

	val theme = MutableStateFlow(0)
	val dynamicTheme = MutableStateFlow(false)
	val timePicker = MutableStateFlow(true)
	val timeFormat = MutableStateFlow(false)
	val language = MutableStateFlow("en")
	val sleepTime = MutableStateFlow(LocalTime.of(23, 59).toString())
	val sortTask = MutableStateFlow(0)
	val calenderView = MutableStateFlow(0)
	val lastOpened = MutableStateFlow("")
	val streak = MutableStateFlow(0)
	val showWhatsNew = MutableStateFlow(true)
	val buildVersionCode = MutableStateFlow(1)
	val swipeBehaviour = MutableStateFlow(0)

	init {
		every { store.themeKey } returns theme
		every { store.dynamicThemeKey } returns dynamicTheme
		every { store.timePickerKey } returns timePicker
		every { store.timeFormatKey } returns timeFormat
		every { store.languageKey } returns language
		every { store.sleepTimeKey } returns sleepTime
		every { store.sortTaskKey } returns sortTask
		every { store.calenderViewKey } returns calenderView
		every { store.lastOpenedKey } returns lastOpened
		every { store.streakKey } returns streak
		every { store.showWhatsNewKey } returns showWhatsNew
		every { store.buildVersionCode } returns buildVersionCode
		every { store.swipeBehaviourKey } returns swipeBehaviour

		coJustRun { store.setTheme(any()) }
		coJustRun { store.setDynamicTheme(any()) }
		coJustRun { store.setTimePicker(any()) }
		coJustRun { store.setTimeFormat(any()) }
		coJustRun { store.setLanguage(any()) }
		coJustRun { store.setSleepTime(any()) }
		coJustRun { store.setSortTask(any()) }
		coJustRun { store.setCalenderView(any()) }
		coJustRun { store.setLastOpened(any()) }
		coJustRun { store.setStreak(any()) }
		coJustRun { store.setShowWhatsNew(any()) }
		coJustRun { store.setBuildVersionCode(any()) }
		coJustRun { store.setSwipeBehaviour(any()) }
	}
}
