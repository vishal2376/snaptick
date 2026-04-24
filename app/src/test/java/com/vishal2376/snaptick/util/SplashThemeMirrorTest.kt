package com.vishal2376.snaptick.util

import android.content.Context
import android.content.SharedPreferences
import com.vishal2376.snaptick.presentation.common.AppTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

class SplashThemeMirrorTest {

	private fun mockContextWithPrefs(stored: Int? = null): Pair<Context, SharedPreferences.Editor> {
		val editor = mockk<SharedPreferences.Editor>(relaxed = true)
		val prefs = mockk<SharedPreferences>()
		every { prefs.getInt("theme_ordinal", AppTheme.Amoled.ordinal) } returns
			(stored ?: AppTheme.Amoled.ordinal)
		every { prefs.edit() } returns editor
		val ctx = mockk<Context>()
		every {
			ctx.getSharedPreferences("snaptick_splash_prefs", Context.MODE_PRIVATE)
		} returns prefs
		return ctx to editor
	}

	@Test fun read_defaultsToAmoled_whenPrefsEmpty() {
		val (ctx, _) = mockContextWithPrefs(stored = null)
		assertEquals(AppTheme.Amoled, SplashThemeMirror.read(ctx))
	}

	@Test fun read_returnsStoredOrdinal() {
		val (ctx, _) = mockContextWithPrefs(stored = AppTheme.Light.ordinal)
		assertEquals(AppTheme.Light, SplashThemeMirror.read(ctx))
	}

	@Test fun write_persistsOrdinalViaEditor() {
		val (ctx, editor) = mockContextWithPrefs()
		val slot = slot<Int>()
		every { editor.putInt("theme_ordinal", capture(slot)) } returns editor
		SplashThemeMirror.write(ctx, AppTheme.Dark)
		verify { editor.putInt("theme_ordinal", AppTheme.Dark.ordinal) }
		assertEquals(AppTheme.Dark.ordinal, slot.captured)
		verify { editor.apply() }
	}
}
