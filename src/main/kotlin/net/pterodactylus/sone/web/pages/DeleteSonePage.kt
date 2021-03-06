package net.pterodactylus.sone.web.pages

import net.pterodactylus.sone.data.Sone
import net.pterodactylus.sone.utils.isPOST
import net.pterodactylus.sone.web.WebInterface
import net.pterodactylus.sone.web.page.FreenetRequest
import net.pterodactylus.util.template.Template
import net.pterodactylus.util.template.TemplateContext

/**
 * Lets the user delete a Sone. Of course the Sone is not really deleted from
 * Freenet; merely all references to it are removed from the local plugin
 * installation.
 */
class DeleteSonePage(template: Template, webInterface: WebInterface):
		LoggedInPage("deleteSone.html", template, "Page.DeleteSone.Title", webInterface) {

	override fun handleRequest(freenetRequest: FreenetRequest, currentSone: Sone, templateContext: TemplateContext) {
		if (freenetRequest.isPOST) {
			if (freenetRequest.httpRequest.isPartSet("deleteSone")) {
				webInterface.core.deleteSone(currentSone)
			}
			throw RedirectException("index.html")
		}
	}

}
