package com.vishal2376.snaptick.presentation.main.viewmodel

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import app.cash.turbine.test
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.domain.model.BackupData
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.presentation.common.NavDrawerItem
import com.vishal2376.snaptick.presentation.main.action.MainAction
import com.vishal2376.snaptick.presentation.main.events.MainEvent
import com.vishal2376.snaptick.util.BackupManager
import com.vishal2376.snaptick.util.MainDispatcherRule
import com.vishal2376.snaptick.util.SettingsStoreFake
import com.vishal2376.snaptick.util.TaskRepositoryFake
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

	@get:Rule val mainRule = MainDispatcherRule()

	private lateinit var context: Context
	private lateinit var store: SettingsStoreFake
	private lateinit var backupManager: BackupManager
	private lateinit var repoFake: TaskRepositoryFake

	@Before fun setUp() {
		context = mockk(relaxed = true)
		every { context.getString(R.string.report_bug) } returns "Report a bug"
		every { context.getString(R.string.suggestions) } returns "Suggestions"
		val pm = mockk<PackageManager>(relaxed = true)
		val info = PackageInfo().apply { versionName = "1.0" }
		every { context.packageName } returns "com.test"
		every { context.packageManager } returns pm
		every { pm.getPackageInfo("com.test", 0) } returns info

		store = SettingsStoreFake()
		backupManager = mockk(relaxed = true)
		repoFake = TaskRepositoryFake()
	}

	private fun buildVm() = MainViewModel(
		context,
		store.store,
		backupManager,
		repoFake.repo,
		mockk(relaxed = true),
		mockk(relaxed = true),
	)

	@Test fun `UpdateAppTheme updates state and persists`() = runTest {
		val vm = buildVm()
		advanceUntilIdle()
		vm.onAction(MainAction.UpdateAppTheme(AppTheme.Amoled))
		advanceUntilIdle()
		assertEquals(AppTheme.Amoled, vm.state.value.theme)
		coVerify { store.store.setTheme(AppTheme.Amoled.ordinal) }
	}

	@Test fun `UpdateLanguage persists new language`() = runTest {
		val vm = buildVm()
		advanceUntilIdle()
		vm.onAction(MainAction.UpdateLanguage("fr"))
		advanceUntilIdle()
		assertEquals("fr", vm.state.value.language)
		coVerify { store.store.setLanguage("fr") }
	}

	@Test fun `OnClickNavDrawerItem REPORT_BUGS emits OpenMail event`() = runTest {
		val vm = buildVm()
		advanceUntilIdle()
		vm.events.test {
			vm.onAction(MainAction.OnClickNavDrawerItem(NavDrawerItem.REPORT_BUGS))
			val event = awaitItem()
			assertEquals(MainEvent.OpenMail("Report a bug"), event)
		}
	}

	@Test fun `CreateBackup success emits ShowToast with success text`() = runTest {
		val uri = mockk<Uri>()
		val data = BackupData(emptyList())
		coEvery { backupManager.createBackup(uri, data) } returns true

		val vm = buildVm()
		advanceUntilIdle()
		vm.events.test {
			vm.onAction(MainAction.CreateBackup(uri, data))
			val event = awaitItem()
			assertEquals(MainEvent.ShowToast("Backup created successfully"), event)
		}
	}

	@Test fun `LoadBackup null emits failure ShowToast`() = runTest {
		val uri = mockk<Uri>()
		coEvery { backupManager.loadBackup(uri) } returns null

		val vm = buildVm()
		advanceUntilIdle()
		vm.events.test {
			vm.onAction(MainAction.LoadBackup(uri))
			val event = awaitItem()
			assertEquals(MainEvent.ShowToast("Failed to restore backup"), event)
		}
	}
}
