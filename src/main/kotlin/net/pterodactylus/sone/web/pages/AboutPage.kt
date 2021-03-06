package net.pterodactylus.sone.web.pages

import net.pterodactylus.sone.main.SonePlugin.PluginHomepage
import net.pterodactylus.sone.main.SonePlugin.PluginVersion
import net.pterodactylus.sone.main.SonePlugin.PluginYear
import net.pterodactylus.sone.web.WebInterface
import net.pterodactylus.sone.web.page.FreenetRequest
import net.pterodactylus.util.template.Template
import net.pterodactylus.util.template.TemplateContext

/**
 * A [SoneTemplatePage] that stores information about Sone in the [TemplateContext].
 */
class AboutPage(template: Template, webInterface: WebInterface,
		private val pluginVersion: PluginVersion,
		private val pluginYear: PluginYear,
		private val pluginHomepage: PluginHomepage): SoneTemplatePage("about.html", template, "Page.About.Title", webInterface, false) {

	override fun handleRequest(freenetRequest: FreenetRequest, templateContext: TemplateContext) {
		templateContext["version"] = pluginVersion.version
		templateContext["year"] = pluginYear.year
		templateContext["homepage"] = pluginHomepage.homepage
	}

}
