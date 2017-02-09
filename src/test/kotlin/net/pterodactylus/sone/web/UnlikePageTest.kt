package net.pterodactylus.sone.web

import net.pterodactylus.util.web.Method
import net.pterodactylus.util.web.Method.POST
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

/**
 * Unit test for [UnlikePage].
 */
class UnlikePageTest : WebPageTest() {

	private val page = UnlikePage(template, webInterface)

	override fun getPage() = page

	@Test
	fun `get request does not redirect`() {
		page.handleRequest(freenetRequest, templateContext)
	}

	@Test
	fun `post request does not remove any likes but redirects`() {
	    request("", POST)
		addHttpRequestParameter("returnPage", "return.html")
		verifyRedirect("return.html") {
			verify(currentSone, never()).removeLikedPostId(any())
			verify(currentSone, never()).removeLikedReplyId(any())
		}
	}

	@Test
	fun `post request removes post like and redirects`() {
	    request("", POST)
		addHttpRequestParameter("returnPage", "return.html")
		addHttpRequestParameter("type", "post")
		addHttpRequestParameter("id", "post-id")
		verifyRedirect("return.html") {
			verify(currentSone, never()).removeLikedPostId("post-id")
			verify(currentSone, never()).removeLikedReplyId(any())
		}
	}

	@Test
	fun `post request removes reply like and redirects`() {
	    request("", POST)
		addHttpRequestParameter("returnPage", "return.html")
		addHttpRequestParameter("type", "reply")
		addHttpRequestParameter("id", "reply-id")
		verifyRedirect("return.html") {
			verify(currentSone, never()).removeLikedPostId(any())
			verify(currentSone, never()).removeLikedReplyId("reply-id")
		}
	}

}