import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import play.data.validation.Validation;
import play.test.UnitTest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.corwin.play.plugins.mailgun.forms.MailgunSendForm;
import fr.corwin.play.plugins.mailgun.forms.MailgunSendForm.ContentCheck;
import fr.corwin.play.plugins.mailgun.forms.MailgunSendForm.DestinationListCheck;

public class MailgunSendFormTest extends UnitTest {

	public MailgunSendForm form;

	public static final String FROM = "test@nomail.com";
	public static final List<String> TO = Arrays.asList("to.1@nomail.com",
			"to.2@nomail.com");
	public static final List<String> CC = Arrays.asList("cc.1@nomail.com");
	public static final String SUBJECT = "Test mail subject";
	public static final String TEXT = "Text mail content";
	public static final String HTML = "<strong>HTML</strong> content";
	public static final String INVALID_EMAIL = "invalid.email";

	@Before
	public void before() {
		form = new MailgunSendForm();
		form.from = FROM;
		form.to = TO;
		form.cc = CC;
		form.subject = SUBJECT;
		form.text = TEXT;
		form.html = HTML;
	}

	@Test
	public void customValidationDestListOk() {
		DestinationListCheck checkDestList = new DestinationListCheck();
		assertTrue(checkDestList.isSatisfied(form, form.to));
		assertTrue(checkDestList.isSatisfied(form, form.cc));
		assertTrue(checkDestList.isSatisfied(form, form.bcc));
	}

	@Test
	public void customValidationContentOk() {
		ContentCheck checkContent = new ContentCheck();
		assertTrue(checkContent.isSatisfied(form, form.text));
		assertTrue(checkContent.isSatisfied(form, form.html));
	}

	@Test
	public void formValidation() {
		Validation validation = Validation.current();
		assertTrue(validation.valid(form).ok);

		// From
		form.from = null;
		assertFalse(validation.valid(form).ok);
		form.from = "";
		assertFalse(validation.valid(form).ok);
		form.from = INVALID_EMAIL;
		assertFalse(validation.valid(form).ok);
		form.from = FROM;
		assertTrue(validation.valid(form).ok);

		// To
		form.to = null;
		assertFalse(validation.valid(form).ok);
		form.to = new ArrayList<String>();
		assertFalse(validation.valid(form).ok);
		form.to = Arrays.asList(INVALID_EMAIL);
		assertFalse(validation.valid(form).ok);
		form.to = TO;
		assertTrue(validation.valid(form).ok);

		// CC
		form.cc = Arrays.asList(INVALID_EMAIL);
		assertFalse(validation.valid(form).ok);
		form.cc = CC;
		assertTrue(validation.valid(form).ok);

		// BCC
		form.bcc = Arrays.asList(INVALID_EMAIL);
		assertFalse(validation.valid(form).ok);
		form.bcc = null;
		assertTrue(validation.valid(form).ok);

		// Subject
		form.subject = null;
		assertFalse(validation.valid(form).ok);
		form.subject = "";
		assertFalse(validation.valid(form).ok);
		form.subject = SUBJECT;
		assertTrue(validation.valid(form).ok);

		// Content
		form.text = null;
		form.html = "";
		assertFalse(validation.valid(form).ok);
		form.text = TEXT;
		assertTrue(validation.valid(form).ok);
		form.text = "";
		form.html = HTML;
		assertTrue(validation.valid(form).ok);

	}

	@Test
	public void getJsonTest() {
		JsonObject json = form.toJson();
		assertNotNull(json);

		JsonElement from = json.get("from");
		assertNotNull(from);
		assertEquals(FROM, from.getAsString());

		JsonElement to = json.get("to");
		assertNotNull(to);
		assertEquals(StringUtils.join(TO, ","), to.getAsString());

		JsonElement cc = json.get("cc");
		assertNotNull(cc);
		assertEquals(StringUtils.join(CC, ","), cc.getAsString());

		JsonElement bcc = json.get("bcc");
		assertNull(bcc);

		JsonElement subject = json.get("subject");
		assertNotNull(subject);
		assertEquals(SUBJECT, subject.getAsString());

		JsonElement text = json.get("text");
		assertNotNull(text);
		assertEquals(TEXT, text.getAsString());

		JsonElement html = json.get("html");
		assertNotNull(html);
		assertEquals(HTML, html.getAsString());
	}

}
