package net.pterodactylus.sone.web.ajax

import net.pterodactylus.sone.utils.ifTrue
import net.pterodactylus.sone.utils.let
import net.pterodactylus.sone.utils.parameters
import net.pterodactylus.sone.web.WebInterface
import net.pterodactylus.sone.web.page.FreenetRequest
import javax.inject.Inject

/**
 * AJAX page that lets the user dismiss a notification.
 */
class DismissNotificationAjaxPage @Inject constructor(webInterface: WebInterface) :
		JsonPage("dismissNotification.ajax", webInterface) {

	override val requiresLogin = false

	override fun createJsonObject(request: FreenetRequest): JsonReturnObject =
			request.parameters["notification"]!!
					.let(webInterface::getNotification)
					.let { notification ->
						notification.isDismissable.ifTrue {
							createSuccessJsonObject().also {
								notification.dismiss()
							}
						} ?: createErrorJsonObject("not-dismissable")
					} ?: createErrorJsonObject("invalid-notification-id")

}
