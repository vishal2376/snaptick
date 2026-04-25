package com.vishal2376.snaptick

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pins the ACRA-init gate. We refuse to initialize ACRA when the build-time
 * email destination is blank, so crash reports from forks and contributor
 * builds never silently route to a hardcoded address.
 */
class SnaptickApplicationTest {

	@Test fun shouldInitAcra_withConfiguredEmail_returnsTrue() {
		assertTrue(SnaptickApplication.shouldInitAcra("dev@example.com"))
	}

	@Test fun shouldInitAcra_withEmptyEmail_returnsFalse() {
		assertFalse(SnaptickApplication.shouldInitAcra(""))
	}

	@Test fun shouldInitAcra_withWhitespaceOnly_returnsFalse() {
		assertFalse(SnaptickApplication.shouldInitAcra("   "))
		assertFalse(SnaptickApplication.shouldInitAcra("\t\n"))
	}
}
