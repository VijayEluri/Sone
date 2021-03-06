package net.pterodactylus.sone.notify;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import net.pterodactylus.sone.data.Post;
import net.pterodactylus.sone.data.PostReply;
import net.pterodactylus.sone.data.Sone;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

/**
 * Filter that checks a {@link PostReply} for visibility.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
@Singleton
public class ReplyVisibilityFilter {

	private final PostVisibilityFilter postVisibilityFilter;

	@Inject
	public ReplyVisibilityFilter(@Nonnull PostVisibilityFilter postVisibilityFilter) {
		this.postVisibilityFilter = postVisibilityFilter;
	}

	/**
	 * Checks whether a reply is visible to the given Sone. A reply is not
	 * considered visible if one of the following statements is true:
	 * <ul>
	 * <li>The reply does not have a post.</li>
	 * <li>The reply’s post {@link PostVisibilityFilter#isPostVisible(Sone, Post) is not visible}.</li>
	 * <li>The reply’s {@link PostReply#getTime() time} is in the future.</li>
	 * </ul>
	 * If none of these statements is true the reply is considered visible.
	 *
	 * @param sone
	 * 		The Sone that checks for a post’s visibility (may be
	 * 		{@code null} to skip Sone-specific checks, such as trust)
	 * @param reply
	 * 		The reply to check for visibility
	 * @return {@code true} if the reply is considered visible, {@code false}
	 * otherwise
	 */
	boolean isReplyVisible(@Nullable Sone sone, @Nonnull PostReply reply) {
		checkNotNull(reply, "reply must not be null");
		Optional<Post> post = reply.getPost();
		if (!post.isPresent()) {
			return false;
		}
		if (!postVisibilityFilter.isPostVisible(sone, post.get())) {
			return false;
		}
		return reply.getTime() <= System.currentTimeMillis();
	}

	@Nonnull
	public Predicate<PostReply> isVisible(@Nullable final Sone currentSone) {
		return new Predicate<PostReply>() {
			@Override
			public boolean apply(@Nullable PostReply postReply) {
				return (postReply != null) && isReplyVisible(currentSone, postReply);
			}
		};
	}

}
