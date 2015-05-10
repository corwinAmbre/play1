import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;
import fr.corwin.play.plugins.mailgun.forms.MailgunSendForm;

public class MailgunSendTest extends UnitTest {

	public MailgunSendForm form;

	public static final String FROM = "mailgunTest@midkemia.fr";
	public static final List<String> TO = Arrays
			.asList("sebastien.domergue@gmail.com");
	public static final List<String> CC = Arrays.asList("corwin@midkemia.fr");
	public static final String SUBJECT = "Mailgun Play 1 module test";
	public static final String HTML = "<strong>Success</strong><br/>Email sent with Play 1 Mailgun module";

	@Before
	public void before() {
		form = new MailgunSendForm();
		form.from = FROM;
		form.to = TO;
		form.cc = CC;
		form.subject = SUBJECT;
		form.html = HTML;
	}

	@Test
	public void sendTest() {
		// MailgunSender.send(form);
	}

}
