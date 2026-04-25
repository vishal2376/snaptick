package com.vishal2376.snaptick.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Verifies [openUrl] only forwards `https://` URIs to ACTION_VIEW.
 * Defense-in-depth: nothing dynamic feeds this helper today, but if a future
 * code path does, it must NOT become a generic deeplink launcher.
 */
class UtilsTest {

	private lateinit var context: Context

	@Before fun setUp() {
		context = mockk(relaxed = true)
		mockkStatic(Uri::class)
	}

	@After fun tearDown() {
		unmockkStatic(Uri::class)
	}

	private fun stubScheme(input: String, scheme: String?) {
		every { Uri.parse(input) } returns mockk { every { this@mockk.scheme } returns scheme }
	}

	// Note: java.* and android.* unit-test stubs return null from Intent(...)
	// constructors. We can still verify startActivity is *called*; the
	// argument's content is exercised in instrumented tests.

	@Test fun `https URI launches ACTION_VIEW`() {
		stubScheme("https://example.com", "https")
		openUrl(context, "https://example.com")
		verify(exactly = 1) { context.startActivity(any<Intent>()) }
	}

	@Test fun `HTTPS uppercase scheme also accepted (case insensitive)`() {
		stubScheme("HTTPS://example.com", "HTTPS")
		openUrl(context, "HTTPS://example.com")
		verify(exactly = 1) { context.startActivity(any<Intent>()) }
	}

	@Test fun `http URI is rejected`() {
		stubScheme("http://example.com", "http")
		openUrl(context, "http://example.com")
		verify(exactly = 0) { context.startActivity(any<Intent>()) }
	}

	@Test fun `file URI is rejected`() {
		stubScheme("file:///etc/passwd", "file")
		openUrl(context, "file:///etc/passwd")
		verify(exactly = 0) { context.startActivity(any<Intent>()) }
	}

	@Test fun `intent URI is rejected`() {
		stubScheme("intent://launch#Intent;...;end", "intent")
		openUrl(context, "intent://launch#Intent;...;end")
		verify(exactly = 0) { context.startActivity(any<Intent>()) }
	}

	@Test fun `content URI is rejected`() {
		stubScheme("content://com.evil.provider/secret", "content")
		openUrl(context, "content://com.evil.provider/secret")
		verify(exactly = 0) { context.startActivity(any<Intent>()) }
	}

	@Test fun `null scheme is rejected`() {
		stubScheme("not-a-url", null)
		openUrl(context, "not-a-url")
		verify(exactly = 0) { context.startActivity(any<Intent>()) }
	}

	@Test fun `null Uri parse result returns silently`() {
		every { Uri.parse("garbage") } returns null
		openUrl(context, "garbage")
		verify(exactly = 0) { context.startActivity(any<Intent>()) }
	}
}
