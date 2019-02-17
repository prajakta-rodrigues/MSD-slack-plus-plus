package tests;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.northeastern.ccs.im.client.IMConnection;
import edu.northeastern.ccs.im.client.IllegalNameException;
import edu.northeastern.ccs.im.client.InvalidListenerException;
import edu.northeastern.ccs.im.client.MessageScanner;
import edu.northeastern.ccs.im.server.Prattle;

@RunWith(PowerMockRunner.class)
@PrepareForTest(IMConnection.class)
public class IMConnectionTest {

	public static ExecutorService executor;

	@BeforeClass()
	public static void setup() {
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            public void run() {
				String[] args = {};
				Prattle.main(args);
				
            }
        });

	}

	@AfterClass()
	public static void killSetup() throws InterruptedException {
		executor.shutdownNow();
	}

	private IMConnection iMConnection;

	@Test
	public void testCreateIMConnectionNullUsername() {

		iMConnection = new IMConnection("localhost", 4122, null);
		iMConnection.addMessageListener(MessageScanner.getInstance());
		assert true;
	}

	@Test
	public void testCreateIMConnectionEmptyUsername() {

		iMConnection = new IMConnection("localhost", 4122, "");
		iMConnection.addMessageListener(MessageScanner.getInstance());
		assert true;
	}

	@Test
	public void testCreateIMConnectionNonEmptyUsername() {

		iMConnection = new IMConnection("localhost", 4122, "prajakta");
		assert true;
	}

	public void testAddMessageListenerValidListener() {

		iMConnection = new IMConnection("localhost", 4122, "prajakta");
		iMConnection.addMessageListener(MessageScanner.getInstance());
		assert true;
	}

	@Test(expected = InvalidListenerException.class)
	public void testAddMessageListenerNull() {

		iMConnection = new IMConnection("localhost", 4122, "prajakta");
		iMConnection.addMessageListener(null);
	}

	@Test(expected = IllegalNameException.class)
	public void testConnectInvalidUsername() throws Exception {
		IMConnection iMConnectionSpy = PowerMockito.spy(new IMConnection("localhost", 4123, "prajakta_12"));
		PowerMockito.doReturn(true).when(iMConnectionSpy, "login");
		assertEquals(true, iMConnectionSpy.connect());
	}

	@Test
	public void isConnectionActiveNullSocket() {
		iMConnection = new IMConnection("localhost", 4127, "pra");
		assertEquals(false, iMConnection.connectionActive());
	}

	@Test
	public void isConnectionctive() {
		iMConnection = new IMConnection("localhost", 4545, "pra");
		iMConnection.connect();
		assertEquals(true, iMConnection.connectionActive());
	}

	@Test
	public void testConnectSuccess() {
		iMConnection = new IMConnection("localhost", 4545, "pra");
		assertEquals(true, iMConnection.connect());
	}

}
