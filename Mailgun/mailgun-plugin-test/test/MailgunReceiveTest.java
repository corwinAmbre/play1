import org.junit.Test;

import play.test.UnitTest;

public class MailgunReceiveTest extends UnitTest {

	@Test
	public void testReceive() {
		StubMailgunReceiver receiver = new StubMailgunReceiver();
		Boolean result = receiver.doJobWithResult();
		assertTrue(result);
	}

}
