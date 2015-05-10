package fr.corwin.play.plugins.mailgun;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import play.Play;

public class MailgunConfiguration {

	public static String BASE_URL = "https://api.mailgun.net/v3";
	public static String API_USER = "api";
	public static String URL_SEND_MESSAGE = "/messages";
	public static String URL_GET_EVENTS = "/events";

	public static String DOMAIN;
	public static String API_KEY;

	public static Integer EVENTS_DELAY = 30;

	public static Map<Integer, String> RESPONSES_CODE = new HashMap<Integer, String>();

	static {
		RESPONSES_CODE.put(200, "mailgun.response.success");
		RESPONSES_CODE.put(400, "mailgun.response.badrequest");
		RESPONSES_CODE.put(401, "mailgun.response.unauthorized");
		RESPONSES_CODE.put(402, "mailgun.response.fail");
		RESPONSES_CODE.put(404, "mailgun.response.notfound");
		RESPONSES_CODE.put(500, "mailgun.response.servererror");
		RESPONSES_CODE.put(502, "mailgun.response.servererror");
		RESPONSES_CODE.put(503, "mailgun.response.servererror");
		RESPONSES_CODE.put(504, "mailgun.response.servererror");

		// Override default urls - to be used for test purposes only
		BASE_URL = overrideConfiguration(BASE_URL, "mailgun.url.base", false);
		API_USER = overrideConfiguration(API_USER, "mailgun.api.user", false);
		URL_SEND_MESSAGE = overrideConfiguration(URL_SEND_MESSAGE,
				"mailgun.url.send", true);
		URL_GET_EVENTS = overrideConfiguration(URL_GET_EVENTS,
				"mailgun.url.events", true);

		// Load account data
		DOMAIN = overrideConfiguration(null, "mailgun.domain", true);
		API_KEY = Play.configuration.getProperty("mailgun.api.key");
		EVENTS_DELAY = Integer.parseInt(overrideConfiguration(
				EVENTS_DELAY.toString(), "mailgun.events.delay", false));
	}

	private static String overrideConfiguration(String initialValue,
			String key, Boolean isUrlPath) {
		String result = initialValue;
		if (StringUtils.isNotBlank(Play.configuration.getProperty(key))) {
			result = Play.configuration.getProperty(key);
			if (BooleanUtils.isTrue(isUrlPath)) {
				if (!result.startsWith("/")) {
					result = "/" + result;
				}
				if (result.endsWith("/")) {
					result = result.substring(0, result.length() - 1);
				}
			}
		}
		return result;
	}

	public static String getBaseUrl() {
		if (StringUtils.isEmpty(DOMAIN)) {
			return BASE_URL;
		}
		return BASE_URL + DOMAIN;
	}

}
