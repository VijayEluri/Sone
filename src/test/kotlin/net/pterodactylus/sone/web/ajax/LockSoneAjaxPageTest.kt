package net.pterodactylus.sone.web.ajax

import net.pterodactylus.sone.data.Sone
import net.pterodactylus.sone.test.mock
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.mockito.Mockito.verify

/**
 * Unit test for [LockSoneAjaxPage].
 */
class LockSoneAjaxPageTest : JsonPageTest("lockSone.ajax", requiresLogin = false, pageSupplier = ::LockSoneAjaxPage) {

	@Test
	fun `request without valid sone results in invalid-sone-id`() {
		assertThatJsonFailed("invalid-sone-id")
	}

	@Test
	fun `request with valid sone id results in locked sone`() {
		val sone = mock<Sone>()
		addLocalSone(sone, "sone-id")
		addRequestParameter("sone", "sone-id")
		assertThatJsonIsSuccessful()
		verify(core).lockSone(sone)
	}

}
