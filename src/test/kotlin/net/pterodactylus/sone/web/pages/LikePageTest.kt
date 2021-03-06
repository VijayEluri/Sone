package net.pterodactylus.sone.web.pages

import net.pterodactylus.util.web.Method.POST
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions

/**
 * Unit test for [LikePage].
 */
class LikePageTest: WebPageTest(::LikePage) {

	@Test
	fun `page returns correct path`() {
	    assertThat(page.path, equalTo("like.html"))
	}

	@Test
	fun `page requires login`() {
	    assertThat(page.requiresLogin(), equalTo(true))
	}

	@Test
	fun `page returns correct title`() {
	    addTranslation("Page.Like.Title", "like page title")
		assertThat(page.getPageTitle(freenetRequest), equalTo("like page title"))
	}

	@Test
	fun `get request does not redirect`() {
		verifyNoRedirect {}
	}

	@Test
	fun `post request with post id likes post and redirects to return page`() {
		setMethod(POST)
		addHttpRequestPart("type", "post")
		addHttpRequestPart("post", "post-id")
		addHttpRequestPart("returnPage", "return.html")
		verifyRedirect("return.html") {
			verify(currentSone).addLikedPostId("post-id")
		}
	}

	@Test
	fun `post request with reply id likes post and redirects to return page`() {
		setMethod(POST)
		addHttpRequestPart("type", "reply")
		addHttpRequestPart("reply", "reply-id")
		addHttpRequestPart("returnPage", "return.html")
		verifyRedirect("return.html") {
			verify(currentSone).addLikedReplyId("reply-id")
		}
	}

	@Test
	fun `post request with invalid likes redirects to return page`() {
		setMethod(POST)
		addHttpRequestPart("type", "foo")
		addHttpRequestPart("returnPage", "return.html")
		verifyRedirect("return.html") {
			verifyNoMoreInteractions(currentSone)
		}
	}

}
