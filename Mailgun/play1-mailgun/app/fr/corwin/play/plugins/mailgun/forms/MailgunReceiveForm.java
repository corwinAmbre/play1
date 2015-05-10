package fr.corwin.play.plugins.mailgun.forms;

import java.util.List;
import java.util.Map;

public class MailgunReceiveForm {

	public String url;

	public Map<String, String> headers;
	public Map<String, String> contentIdMap;

	public List<String> recipients;
	public String sender;
	public String from;
	public String subject;
	public String bodyPlain;
	public String strippedText;
	public String strippedSignature;
	public String bodyHtml;
	public String strippedHtml;
	public List<Attachment> attachments;

}
