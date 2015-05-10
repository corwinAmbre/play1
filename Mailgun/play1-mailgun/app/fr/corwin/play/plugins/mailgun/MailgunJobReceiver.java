package fr.corwin.play.plugins.mailgun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import play.Logger;
import play.i18n.Messages;
import play.jobs.Job;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.corwin.play.plugins.mailgun.forms.Attachment;
import fr.corwin.play.plugins.mailgun.forms.MailgunReceiveForm;

public abstract class MailgunJobReceiver extends Job<Boolean> {

	public abstract void storeMessage(MailgunReceiveForm mail);

	public Boolean doJobWithResult() {
		WSRequest request = WS.url(MailgunConfiguration.getBaseUrl()
				+ MailgunConfiguration.URL_SEND_MESSAGE);
		request.authenticate(MailgunConfiguration.API_USER,
				MailgunConfiguration.API_KEY);
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -MailgunConfiguration.EVENTS_DELAY);
		Long pastLimit = cal.getTimeInMillis();
		request.setParameter("begin", pastLimit);
		request.setParameter("ascending", true);
		request.setParameter("event", "stored");
		HttpResponse response = request.get();
		if (!response.success()) {
			Logger.error(
					"-- Error while getting Mailgun events: Error code %d - %s",
					response.getStatus(), Messages
							.get(MailgunConfiguration.RESPONSES_CODE
									.get(response.getStatus())));
			return false;
		}
		JsonObject result = response.getJson().getAsJsonObject();
		JsonArray events = result.get("items").getAsJsonArray();
		for (JsonElement event : events) {
			String url = event.getAsJsonObject().get("storage")
					.getAsJsonObject().get("url").getAsString();
			WSRequest mailRequest = WS.url(url);
			mailRequest.authenticate(MailgunConfiguration.API_USER,
					MailgunConfiguration.API_KEY);
			HttpResponse mailResponse = mailRequest.get();
			if (!mailResponse.success()) {
				Logger.error(
						"-- Error while getting Mailgun message: Error code %d - %s",
						response.getStatus(), Messages
								.get(MailgunConfiguration.RESPONSES_CODE
										.get(response.getStatus())));
				return false;
			}
			JsonObject mailJson = mailResponse.getJson().getAsJsonObject();
			MailgunReceiveForm mail = new MailgunReceiveForm();
			mail.recipients = Arrays.asList(mailJson.get("recipients")
					.getAsString().split(","));
			mail.sender = mailJson.get("sender").getAsString();
			mail.from = mailJson.get("from").getAsString();
			mail.subject = mailJson.get("subject").getAsString();
			mail.bodyPlain = mailJson.get("body-plain").getAsString();
			if (mailJson.has("stripped-text")) {
				mail.strippedText = mailJson.get("stripped-text").getAsString();
			}
			if (mailJson.has("stripped-signature")) {
				mail.strippedSignature = mailJson.get("stripped-signature")
						.getAsString();
			}
			if (mailJson.has("body-html")) {
				mail.bodyHtml = mailJson.get("body-html").getAsString();
			}
			if (mailJson.has("stripped-html")) {
				mail.strippedHtml = mailJson.get("stripped-html").getAsString();
			}
			mail.url = mailJson.get("message-url").getAsString();
			// TODO content-id-map
			// TODO message-headers
			if (mailJson.has("attachments")
					&& mailJson.get("attachments").isJsonArray()) {
				JsonArray attachments = mailJson.get("attachments")
						.getAsJsonArray();
				mail.attachments = new ArrayList<Attachment>();
				for (JsonElement element : attachments) {
					JsonObject attachment = element.getAsJsonObject();
					Attachment toAdd = new Attachment();
					toAdd.size = attachment.get("size").getAsInt();
					toAdd.url = attachment.get("url").getAsString();
					toAdd.name = attachment.get("name").getAsString();
					toAdd.contentType = attachment.get("content-type")
							.getAsString();
					mail.attachments.add(toAdd);
				}
			}
			storeMessage(mail);
		}
		return true;
	}

}
