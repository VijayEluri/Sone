/*
 * Sone - EditAlbumAjaxPage.java - Copyright © 2011–2013 David Roden
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

import net.pterodactylus.sone.data.Album;
import net.pterodactylus.sone.text.TextFilter;
import net.pterodactylus.sone.web.WebInterface;
import net.pterodactylus.sone.web.page.FreenetRequest;

import com.google.common.base.Optional;

/**
 * Page that stores a user’s album modifications.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class EditAlbumAjaxPage extends JsonPage {

	/**
	 * Creates a new edit album AJAX page.
	 *
	 * @param webInterface
	 *            The Sone web interface
	 */
	public EditAlbumAjaxPage(WebInterface webInterface) {
		super("editAlbum.ajax", webInterface);
	}

	//
	// JSONPAGE METHODS
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JsonReturnObject createJsonObject(FreenetRequest request) {
		String albumId = request.getHttpRequest().getParam("album");
		Optional<Album> album = webInterface.getCore().getAlbum(albumId);
		if (!album.isPresent()) {
			return createErrorJsonObject("invalid-album-id");
		}
		if (!album.get().getSone().isLocal()) {
			return createErrorJsonObject("not-authorized");
		}
		if ("true".equals(request.getHttpRequest().getParam("moveLeft"))) {
			album.get().moveUp();
			webInterface.getCore().touchConfiguration();
			return createSuccessJsonObject(); // TODO - fix javascript!
		}
		if ("true".equals(request.getHttpRequest().getParam("moveRight"))) {
			album.get().moveDown();
			webInterface.getCore().touchConfiguration();
			return createSuccessJsonObject(); // TODO - fix javascript!
		}
		String title = request.getHttpRequest().getParam("title").trim();
		String description = request.getHttpRequest().getParam("description").trim();
		album.get().modify().setTitle(title).setDescription(TextFilter.filter(request.getHttpRequest().getHeader("host"), description)).update();
		webInterface.getCore().touchConfiguration();
		return createSuccessJsonObject().put("albumId", album.get().getId()).put("title", album.get().getTitle()).put("description", album.get().getDescription());
	}

}
