package fr.corwin.play.plugins.mailgun.forms;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Email;
import play.data.validation.Required;

import com.google.gson.JsonObject;

public class MailgunSendForm {

	@Required
	@Email
	public String from;

	@Required
	@CheckWith(DestinationListCheck.class)
	public List<String> to;
	@CheckWith(DestinationListCheck.class)
	public List<String> cc;
	@CheckWith(DestinationListCheck.class)
	public List<String> bcc;

	@Required
	public String subject;
	@CheckWith(ContentCheck.class)
	public String text;
	@CheckWith(ContentCheck.class)
	public String html;

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("from", from);
		json.addProperty("to", StringUtils.join(to, ","));
		json.addProperty("subject", subject);
		if (cc != null && CollectionUtils.size(cc) > 0) {
			json.addProperty("cc", StringUtils.join(cc, ","));
		}
		if (bcc != null && CollectionUtils.size(bcc) > 0) {
			json.addProperty("bcc", StringUtils.join(bcc, ","));
		}
		if (StringUtils.isNotEmpty(text)) {
			json.addProperty("text", text);
		}
		if (StringUtils.isNotEmpty("html")) {
			json.addProperty("html", html);
		}
		return json;
	}

	public static class ContentCheck extends Check {
		@Override
		public boolean isSatisfied(Object validatedObject, Object text) {
			if (!(validatedObject instanceof MailgunSendForm)) {
				return false;
			}
			MailgunSendForm form = (MailgunSendForm) validatedObject;
			if (StringUtils.isBlank(form.text)
					&& StringUtils.isBlank(form.html)) {
				setMessage("validation.mailugn.missingcontent");
				return false;
			}
			return true;

		}
	}

	public static class DestinationListCheck extends Check {

		@Override
		public boolean isSatisfied(Object validatedObject, Object destList) {
			if (destList != null) {
				if (!(destList instanceof List)) {
					return false;
				}
				String emailPattern = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
						+ "[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
						+ "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
				Pattern emailRegex = Pattern.compile(emailPattern);
				List<String> emails = (List<String>) destList;
				for (String email : emails) {
					if (!emailRegex.matcher(email).matches()) {
						setMessage("validation.email", email);
						return false;
					}
				}
			}
			return true;
		}
	}

	public String validate() {
		if (StringUtils.isBlank(text) && StringUtils.isBlank(html)) {
			return "Email content is empty";
		}
		return null;
	}

}
