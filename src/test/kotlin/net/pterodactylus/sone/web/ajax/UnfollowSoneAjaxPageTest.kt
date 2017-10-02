package net.pterodactylus.sone.web.ajax

import net.pterodactylus.sone.test.mock
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.mockito.Mockito.verify

/**
 * Unit test for [UnfollowSoneAjaxPage].
 */
class UnfollowSoneAjaxPageTest : JsonPageTest("unfollowSone.ajax", pageSupplier = ::UnfollowSoneAjaxPage) {

	@Test
	fun `request without sone returns invalid-sone-id`() {
		assertThat(json.isSuccess, equalTo(false))
		assertThat(json.error, equalTo("invalid-sone-id"))
	}

	@Test
	fun `request with invalid sone returns invalid-sone-id`() {
		addRequestParameter("sone", "invalid")
		assertThat(json.isSuccess, equalTo(false))
		assertThat(json.error, equalTo("invalid-sone-id"))
	}

	@Test
	fun `request with valid sone unfollows sone`() {
		addSone(mock(), "sone-id")
		addRequestParameter("sone", "sone-id")
		assertThat(json.isSuccess, equalTo(true))
		verify(core).unfollowSone(currentSone, "sone-id")
	}

}
