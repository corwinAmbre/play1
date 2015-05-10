import play.Logger;
import fr.corwin.play.plugins.mailgun.MailgunJobReceiver;
import fr.corwin.play.plugins.mailgun.forms.MailgunReceiveForm;

public class StubMailgunReceiver extends MailgunJobReceiver {

	@Override
	public void storeMessage(MailgunReceiveForm mail) {
		Logger.info("---- Message found: %s----", mail.url);
	}

}
