package net.pterodactylus.sone.web.pages

import net.pterodactylus.sone.data.Sone
import net.pterodactylus.sone.test.mock
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

/**
 * Unit test for [LockSonePage].
 */
class LockSonePageTest : WebPageTest() {

	private val page = LockSonePage(template, webInterface)

	override fun getPage() = page

	@Test
	fun `page returns correct path`() {
		assertThat(page.path, equalTo("lockSone.html"))
	}

	@Test
	fun `page requires login`() {
	    assertThat(page.requiresLogin(), equalTo(false))
	}

	@Test
	fun `page returns correct title`() {
	    addTranslation("Page.LockSone.Title", "lock Sone page title")
		assertThat(page.getPageTitle(freenetRequest), equalTo("lock Sone page title"))
	}

	@Test
	fun `locking an invalid local sone redirects to return page`() {
		addHttpRequestPart("returnPage", "return.html")
		verifyRedirect("return.html") {
			verify(core, never()).lockSone(any<Sone>())
		}
	}

	@Test
	fun `locking an valid local sone locks the sone and redirects to return page`() {
		addHttpRequestPart("sone", "sone-id")
		val sone = mock<Sone>()
		addLocalSone("sone-id", sone)
		addHttpRequestPart("returnPage", "return.html")
		verifyRedirect("return.html") {
			verify(core).lockSone(sone)
		}
	}

}
