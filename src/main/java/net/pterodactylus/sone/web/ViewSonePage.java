/*
 * Sone - ViewSonePage.java - Copyright © 2010 David Roden
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.pterodactylus.sone.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.pterodactylus.sone.data.Post;
import net.pterodactylus.sone.data.Reply;
import net.pterodactylus.sone.data.Sone;
import net.pterodactylus.sone.template.SoneAccessor;
import net.pterodactylus.util.collection.ListBuilder;
import net.pterodactylus.util.collection.Pagination;
import net.pterodactylus.util.number.Numbers;
import net.pterodactylus.util.template.Template;
import net.pterodactylus.util.template.TemplateContext;

/**
 * Lets the user browser another Sone.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class ViewSonePage extends SoneTemplatePage {

	/**
	 * Creates a new “view Sone” page.
	 *
	 * @param template
	 *            The template to render
	 * @param webInterface
	 *            The Sone web interface
	 */
	public ViewSonePage(Template template, WebInterface webInterface) {
		super("viewSone.html", template, webInterface, false);
	}

	//
	// TEMPLATEPAGE METHODS
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getPageTitle(Request request) {
		String soneId = request.getHttpRequest().getParam("sone");
		Sone sone = webInterface.getCore().getSone(soneId, false);
		if ((sone != null) && (sone.getTime() > 0)) {
			String soneName = SoneAccessor.getNiceName(sone);
			return soneName + " - " + webInterface.getL10n().getString("Page.ViewSone.Title");
		}
		return webInterface.getL10n().getString("Page.ViewSone.Page.TitleWithoutSone");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processTemplate(Request request, TemplateContext templateContext) throws RedirectException {
		super.processTemplate(request, templateContext);
		String soneId = request.getHttpRequest().getParam("sone");
		Sone sone = webInterface.getCore().getSone(soneId, false);
		templateContext.set("sone", sone);
		List<Post> sonePosts = sone.getPosts();
		sonePosts.addAll(webInterface.getCore().getDirectedPosts(sone));
		Collections.sort(sonePosts, Post.TIME_COMPARATOR);
		Pagination<Post> postPagination = new Pagination<Post>(sonePosts, 10).setPage(Numbers.safeParseInteger(request.getHttpRequest().getParam("postPage"), 0));
		templateContext.set("postPagination", postPagination);
		templateContext.set("posts", postPagination.getItems());
		Set<Reply> replies = sone.getReplies();
		final Map<Post, List<Reply>> repliedPosts = new HashMap<Post, List<Reply>>();
		for (Reply reply : replies) {
			Post post = reply.getPost();
			if (repliedPosts.containsKey(post) || sone.equals(post.getSone()) || (sone.equals(post.getRecipient()))) {
				continue;
			}
			repliedPosts.put(post, webInterface.getCore().getReplies(post));
		}
		List<Post> posts = new ArrayList<Post>(repliedPosts.keySet());
		Collections.sort(posts, new Comparator<Post>() {

			@Override
			public int compare(Post leftPost, Post rightPost) {
				return (int) Math.min(Integer.MAX_VALUE, Math.max(Integer.MIN_VALUE, repliedPosts.get(rightPost).get(0).getTime() - repliedPosts.get(leftPost).get(0).getTime()));
			}

		});

		Pagination<Post> repliedPostPagination = new Pagination<Post>(posts, 10).setPage(Numbers.safeParseInteger(request.getHttpRequest().getParam("repliedPostPage"), 0));
		templateContext.set("repliedPostPagination", repliedPostPagination);
		templateContext.set("repliedPosts", repliedPostPagination.getItems());

		/* mark Sone and posts and replies as known. */
		webInterface.getCore().markSoneKnown(sone);
		for (Post post : new ListBuilder<Post>().addAll(postPagination.getItems()).addAll(repliedPostPagination.getItems()).get()) {
			if (post.getSone() != null) {
				webInterface.getCore().markPostKnown(post);
			}
			for (Reply reply : webInterface.getCore().getReplies(post)) {
				webInterface.getCore().markReplyKnown(reply);
			}
		}
	}

}
