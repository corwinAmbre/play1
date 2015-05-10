package fr.corwin.play.plugins.mailgun;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.jobs.Job;
import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import fr.corwin.play.plugins.mailgun.forms.MailgunSendForm;

public class MailgunSender extends Job<Boolean> {

	public static Promise<Boolean> send(@Valid MailgunSendForm form) {
		MailgunSender sender = new MailgunSender(form);
		return sender.now();
	}

	public MailgunSender(MailgunSendForm form) {
		this.form = form;
	}

	private MailgunSendForm form;

	public Boolean doJobWithResult() {
		if (form == null) {
			return false;
		}
		WSRequest request = WS.url(MailgunConfiguration.getBaseUrl()
				+ MailgunConfiguration.URL_SEND_MESSAGE);
		request.authenticate(MailgunConfiguration.API_USER,
				MailgunConfiguration.API_KEY);
		request.setParameter("from", form.from);
		request.setParameter("to", StringUtils.join(form.to, ","));
		if (form.cc != null && CollectionUtils.size(form.cc) > 0) {
			request.setParameter("cc", StringUtils.join(form.cc, ","));
		}
		if (form.bcc != null && CollectionUtils.size(form.bcc) > 0) {
			request.setParameter("bcc", StringUtils.join(form.bcc, ","));
		}
		request.setParameter("subject", form.subject);
		if (StringUtils.isNotBlank(form.text)) {
			request.setParameter("text", form.text);
		}
		if (StringUtils.isNotEmpty(form.html)) {
			request.setParameter("html", form.html);
		}
		HttpResponse response = request.post();
		if (!response.success()) {
			Logger.error(
					"-- Error while sending Mailgun message: Error code %d - %s",
					response.getStatus(), Messages
							.get(MailgunConfiguration.RESPONSES_CODE
									.get(response.getStatus())));
			return false;
		}
		return true;
	}
}
