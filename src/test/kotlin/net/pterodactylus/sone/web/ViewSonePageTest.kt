package net.pterodactylus.sone.web

import net.pterodactylus.sone.data.Post
import net.pterodactylus.sone.data.PostReply
import net.pterodactylus.sone.data.Profile
import net.pterodactylus.sone.data.Sone
import net.pterodactylus.sone.test.asOptional
import net.pterodactylus.sone.test.mock
import net.pterodactylus.sone.test.whenever
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.Before
import org.junit.Test

/**
 * Unit test for [ViewSonePage].
 */
class ViewSonePageTest : WebPageTest() {

	init {
		whenever(currentSone.id).thenReturn("sone-id")
	}

	private val page = ViewSonePage(template, webInterface)
	private val post1 = createPost("post1", "First Post.", 1000, currentSone)
	private val post2 = createPost("post2", "Second Post.", 2000, currentSone)
	private val foreignPost1 = createPost("foreign-post1", "First Foreign Post.", 1000, mock<Sone>())
	private val foreignPost2 = createPost("foreign-post2", "Second Foreign Post.", 2000, mock<Sone>())
	private val directed1 = createPost("post3", "First directed.", 1500, mock<Sone>(), recipient = currentSone)
	private val directed2 = createPost("post4", "Second directed.", 2500, mock<Sone>(), recipient = currentSone)

	override fun getPage() = page

	@Before
	fun setup() {
		whenever(currentSone.posts).thenReturn(mutableListOf(post2, post1))
		whenever(core.getDirectedPosts("sone-id")).thenReturn(setOf(directed1, directed2))
		core.preferences.postsPerPage = 2
	}

	@Test
	fun `get request without sone parameter stores null in template context`() {
		page.handleRequest(freenetRequest, templateContext)
		assertThat(templateContext["sone"], nullValue())
		assertThat(templateContext["soneId"], equalTo<Any>(""))
	}

	@Test
	fun `get request with invalid sone parameter stores null in template context`() {
		addHttpRequestParameter("sone", "invalid-sone-id")
		page.handleRequest(freenetRequest, templateContext)
		assertThat(templateContext["sone"], nullValue())
		assertThat(templateContext["soneId"], equalTo<Any>("invalid-sone-id"))
	}

	@Test
	fun `get request with valid sone parameter stores sone in template context`() {
		whenever(currentSone.posts).thenReturn(mutableListOf())
		whenever(core.getDirectedPosts("sone-id")).thenReturn(emptyList())
		addHttpRequestParameter("sone", "sone-id")
		addSone("sone-id", currentSone)
		page.handleRequest(freenetRequest, templateContext)
		assertThat(templateContext["sone"], equalTo<Any>(currentSone))
		assertThat(templateContext["soneId"], equalTo<Any>("sone-id"))
	}

	private fun createPost(id: String, text: String, time: Long, sender: Sone? = null, recipient: Sone? = null) = mock<Post>().apply {
		whenever(this.id).thenReturn(id)
		sender?.run { whenever(this@apply.sone).thenReturn(this) }
		val recipientId = recipient?.id
		whenever(this.recipientId).thenReturn(recipientId.asOptional())
		whenever(this.recipient).thenReturn(recipient.asOptional())
		whenever(this.time).thenReturn(time)
		whenever(this.text).thenReturn(text)
	}

	@Test
	@Suppress("UNCHECKED_CAST")
	fun `request with valid sone stores posts and directed posts in template context`() {
		addSone("sone-id", currentSone)
		addHttpRequestParameter("sone", "sone-id")
		page.handleRequest(freenetRequest, templateContext)
		assertThat(templateContext["posts"] as Iterable<Post>, contains(directed2, post2))
	}

	@Test
	@Suppress("UNCHECKED_CAST")
	fun `second page of posts is shown correctly`() {
		addSone("sone-id", currentSone)
		addHttpRequestParameter("sone", "sone-id")
		addHttpRequestParameter("postPage", "1")
		page.handleRequest(freenetRequest, templateContext)
		assertThat(templateContext["posts"] as Iterable<Post>, contains(directed1, post1))
	}

	private fun createReply(text: String, time: Long, post: Post?) = mock<PostReply>().apply {
		whenever(this.text).thenReturn(text)
		whenever(this.time).thenReturn(time)
		whenever(this.post).thenReturn(post.asOptional())
	}

	@Test
	@Suppress("UNCHECKED_CAST")
	fun `replies are shown correctly`() {
	    val reply1 = createReply("First Reply", 1500, foreignPost1)
		val reply2 = createReply("Second Reply", 2500, foreignPost2)
		val reply3 = createReply("Third Reply", 1750, post1)
		val reply4 = createReply("Fourth Reply", 2250, post2)
	    val reply5 = createReply("Fifth Reply", 1600, post1)
	    val reply6 = createReply("Sixth Reply", 2100, directed1)
	    val reply7 = createReply("Seventh Reply", 2200, null)
	    val reply8 = createReply("Eigth Reply", 2300, foreignPost1)
		whenever(currentSone.replies).thenReturn(setOf(reply1, reply2, reply3, reply4, reply5, reply6, reply7, reply8))
		whenever(core.getReplies("post1")).thenReturn(listOf(reply3, reply5))
		whenever(core.getReplies("post2")).thenReturn(listOf(reply4))
		whenever(core.getReplies("foreign-post1")).thenReturn(listOf(reply1))
		whenever(core.getReplies("foreign-post2")).thenReturn(listOf(reply2))
		whenever(core.getReplies("post3")).thenReturn(listOf(reply6))
		addSone("sone-id", currentSone)
		addHttpRequestParameter("sone", "sone-id")
		page.handleRequest(freenetRequest, templateContext)
		assertThat(templateContext["repliedPosts"] as Iterable<Post>, contains(foreignPost2,  foreignPost1))
	}

	@Test
	fun `page title is default for request without parameters`() {
	    assertThat(page.getPageTitle(freenetRequest), equalTo("Page.ViewSone.Page.TitleWithoutSone"))
	}

	@Test
	fun `page title is default for request with invalid sone parameters`() {
		addHttpRequestParameter("sone", "invalid-sone-id")
	    assertThat(page.getPageTitle(freenetRequest), equalTo("Page.ViewSone.Page.TitleWithoutSone"))
	}

	@Test
	fun `page title contains sone name for request with sone parameters`() {
		addHttpRequestParameter("sone", "sone-id")
		addSone("sone-id", currentSone)
		whenever(currentSone.profile).thenReturn(Profile(currentSone).apply {
			firstName = "First"
			middleName = "M."
			lastName = "Last"
		})
	    assertThat(page.getPageTitle(freenetRequest), equalTo("First M. Last - Page.ViewSone.Title"))
	}

	@Test
	fun `page is link-excepted`() {
	    assertThat(page.isLinkExcepted(null), equalTo(true))
	}

}