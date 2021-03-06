package net.pterodactylus.sone.web.pages

import net.pterodactylus.sone.utils.isPOST
import net.pterodactylus.sone.utils.parameters
import net.pterodactylus.sone.web.WebInterface
import net.pterodactylus.sone.web.page.FreenetRequest
import net.pterodactylus.util.template.Template
import net.pterodactylus.util.template.TemplateContext

/**
 * This page lets the user unlock a [net.pterodactylus.sone.data.Sone] to allow its insertion.
 */
class UnlockSonePage(template: Template, webInterface: WebInterface):
		SoneTemplatePage("unlockSone.html", template, "Page.UnlockSone.Title", webInterface, false) {

	override fun handleRequest(freenetRequest: FreenetRequest, templateContext: TemplateContext) {
		if (freenetRequest.isPOST) {
			freenetRequest.parameters["sone", 44]
					.let(webInterface.core::getLocalSone)
					?.also(webInterface.core::unlockSone)
			throw RedirectException(freenetRequest.parameters["returnPage", 256])
		}
	}

}
