/*
 * Sone - GetStatusAjaxPage.java - Copyright © 2010 David Roden
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

package net.pterodactylus.sone.web.ajax;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.pterodactylus.sone.data.Post;
import net.pterodactylus.sone.data.Reply;
import net.pterodactylus.sone.data.Sone;
import net.pterodactylus.sone.template.SoneAccessor;
import net.pterodactylus.sone.web.WebInterface;
import net.pterodactylus.util.io.Closer;
import net.pterodactylus.util.json.JsonArray;
import net.pterodactylus.util.json.JsonObject;
import net.pterodactylus.util.notify.Notification;
import net.pterodactylus.util.template.Template;
import net.pterodactylus.util.template.TemplateException;

/**
 * The “get status” AJAX handler returns all information that is necessary to
 * update the web interface in real-time.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class GetStatusAjaxPage extends JsonPage {

	/** Date formatter. */
	private static final DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy, HH:mm:ss");

	/** The template to render posts. */
	private final Template postTemplate;

	/** The template to render replies. */
	private final Template replyTemplate;

	/**
	 * Creates a new “get status” AJAX handler.
	 *
	 * @param webInterface
	 *            The Sone web interface
	 * @param postTemplate
	 *            The template to render for posts
	 * @param replyTemplate
	 *            The template to render for replies
	 */
	public GetStatusAjaxPage(WebInterface webInterface, Template postTemplate, Template replyTemplate) {
		super("ajax/getStatus.ajax", webInterface);
		this.postTemplate = postTemplate;
		this.replyTemplate = replyTemplate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JsonObject createJsonObject(Request request) {
		/* load Sones. */
		boolean loadAllSones = Boolean.parseBoolean(request.getHttpRequest().getParam("loadAllSones", "true"));
		Set<Sone> sones = loadAllSones ? webInterface.getCore().getSones() : Collections.singleton(getCurrentSone(request.getToadletContext()));
		JsonArray jsonSones = new JsonArray();
		for (Sone sone : sones) {
			JsonObject jsonSone = createJsonSone(sone);
			jsonSones.add(jsonSone);
		}
		/* load notifications. */
		List<Notification> notifications = new ArrayList<Notification>(webInterface.getNotifications().getChangedNotifications());
		Set<Notification> removedNotifications = webInterface.getNotifications().getRemovedNotifications();
		Collections.sort(notifications, Notification.LAST_UPDATED_TIME_SORTER);
		JsonArray jsonNotifications = new JsonArray();
		for (Notification notification : notifications) {
			jsonNotifications.add(createJsonNotification(notification));
		}
		JsonArray jsonRemovedNotifications = new JsonArray();
		for (Notification notification : removedNotifications) {
			jsonRemovedNotifications.add(createJsonNotification(notification));
		}
		/* load new posts. */
		postTemplate.set("currentSone", getCurrentSone(request.getToadletContext()));
		Set<Post> newPosts = webInterface.getNewPosts();
		JsonArray jsonPosts = new JsonArray();
		for (Post post : newPosts) {
			jsonPosts.add(createJsonPost(post));
		}
		/* load new replies. */
		replyTemplate.set("currentSone", getCurrentSone(request.getToadletContext()));
		Set<Reply> newReplies = webInterface.getNewReplies();
		JsonArray jsonReplies = new JsonArray();
		for (Reply reply : newReplies) {
			jsonReplies.add(createJsonReply(reply));
		}
		return createSuccessJsonObject().put("sones", jsonSones).put("notifications", jsonNotifications).put("removedNotifications", jsonRemovedNotifications).put("newPosts", jsonPosts).put("newReplies", jsonReplies);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean needsFormPassword() {
		return false;
	}

	//
	// PRIVATE METHODS
	//

	/**
	 * Creates a JSON object from the given Sone.
	 *
	 * @param sone
	 *            The Sone to convert to a JSON object
	 * @return The JSON representation of the given Sone
	 */
	private JsonObject createJsonSone(Sone sone) {
		JsonObject jsonSone = new JsonObject();
		jsonSone.put("id", sone.getId());
		jsonSone.put("name", SoneAccessor.getNiceName(sone));
		jsonSone.put("local", sone.getInsertUri() != null);
		jsonSone.put("status", webInterface.getCore().getSoneStatus(sone).name());
		jsonSone.put("modified", webInterface.getCore().isModifiedSone(sone));
		jsonSone.put("locked", webInterface.getCore().isLocked(sone));
		synchronized (dateFormat) {
			jsonSone.put("lastUpdated", dateFormat.format(new Date(sone.getTime())));
		}
		jsonSone.put("age", (System.currentTimeMillis() - sone.getTime()) / 1000);
		return jsonSone;
	}

	/**
	 * Creates a JSON object from the given post. The JSON object will only
	 * contain the ID of the post, its time, and its rendered HTML code.
	 *
	 * @param post
	 *            The post to create a JSON object from
	 * @return The JSON representation of the post
	 */
	private JsonObject createJsonPost(Post post) {
		JsonObject jsonPost = new JsonObject();
		jsonPost.put("id", post.getId());
		jsonPost.put("time", post.getTime());
		StringWriter stringWriter = new StringWriter();
		postTemplate.set("post", post);
		try {
			postTemplate.render(stringWriter);
		} catch (TemplateException te1) {
			/* TODO - shouldn’t happen. */
		} finally {
			Closer.close(stringWriter);
		}
		return jsonPost.put("html", stringWriter.toString());
	}

	/**
	 * Creates a JSON object from the given reply. The JSON object will only
	 * contain the ID of the reply, the ID of its post, its time, and its
	 * rendered HTML code.
	 *
	 * @param reply
	 *            The reply to create a JSON object from
	 * @return The JSON representation of the reply
	 */
	private JsonObject createJsonReply(Reply reply) {
		JsonObject jsonPost = new JsonObject();
		jsonPost.put("postId", reply.getPost().getId());
		jsonPost.put("id", reply.getId());
		jsonPost.put("time", reply.getTime());
		StringWriter stringWriter = new StringWriter();
		replyTemplate.set("reply", reply);
		try {
			replyTemplate.render(stringWriter);
		} catch (TemplateException te1) {
			/* TODO - shouldn’t happen. */
		} finally {
			Closer.close(stringWriter);
		}
		return jsonPost.put("html", stringWriter.toString());
	}

	/**
	 * Creates a JSON object from the given notification.
	 *
	 * @param notification
	 *            The notification to create a JSON object
	 * @return The JSON object
	 */
	private static JsonObject createJsonNotification(Notification notification) {
		JsonObject jsonNotification = new JsonObject();
		jsonNotification.put("id", notification.getId());
		jsonNotification.put("text", notification.toString());
		jsonNotification.put("createdTime", notification.getCreatedTime());
		jsonNotification.put("lastUpdatedTime", notification.getLastUpdatedTime());
		jsonNotification.put("dismissable", notification.isDismissable());
		return jsonNotification;
	}

}
