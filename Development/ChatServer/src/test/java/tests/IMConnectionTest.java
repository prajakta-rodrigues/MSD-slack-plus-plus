package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.northeastern.ccs.im.client.IMConnection;
import edu.northeastern.ccs.im.client.IllegalNameException;
import edu.northeastern.ccs.im.client.IllegalOperationException;
import edu.northeastern.ccs.im.client.InvalidListenerException;
import edu.northeastern.ccs.im.client.MessageScanner;
import edu.northeastern.ccs.im.server.Prattle;

/**
 * The Class IMConnectionTest tests the methods provided IMConnection.
 */
public class IMConnectionTest {

	/** The executor. */
	private static ExecutorService executor;

	/**
	 * Setup server.
	 */
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

	/**
	 * Kill server.
	 *
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@AfterClass()
	public static void killSetup() throws InterruptedException {
		Prattle.stopServer();
		if (null != executor) {
			executor.shutdownNow();
		}
	}

	/** The i M connection. */
	private IMConnection iMConnection;

	/**
	 * Test create IM connection with null username.
	 */
	@Test
	public void testCreateIMConnectionNullUsername() {

		iMConnection = new IMConnection("localhost", 4122, null);
		iMConnection.addMessageListener(MessageScanner.getInstance());
		assert true;
	}

	/**
	 * Test create IM connection with empty username.
	 */
	@Test
	public void testCreateIMConnectionEmptyUsername() {

		iMConnection = new IMConnection("localhost", 4122, "");
		iMConnection.addMessageListener(MessageScanner.getInstance());
		assert true;
	}

	/**
	 * Test create IM connection with non empty username.
	 */
	@Test
	public void testCreateIMConnectionNonEmptyUsername() {

		iMConnection = new IMConnection("localhost", 4122, "prajakta");
		assert true;
	}

	/**
	 * Test add message listener with valid listener.
	 */
	public void testAddMessageListenerValidListener() {

		iMConnection = new IMConnection("localhost", 4122, "prajakta");
		iMConnection.addMessageListener(MessageScanner.getInstance());
		assert true;
	}

	/**
	 * Test add message with null listener.
	 */
	@Test(expected = InvalidListenerException.class)
	public void testAddMessageListenerNull() {

		iMConnection = new IMConnection("localhost", 4122, "prajakta");
		iMConnection.addMessageListener(null);
	}

	/**
	 * Checks if is connection active when connection is not established.
	 */
	@Test
	public void isConnectionActiveNotConnection() {
		iMConnection = new IMConnection("localhost", 4127, "pra");
		assertEquals(false, iMConnection.connectionActive());
	}

	/**
	 * Checks if is connection active when connection is established.
	 */
	@Test
	public void isConnectionActiveWhenConnected() {
		iMConnection = new IMConnection("localhost", 4545, "pra");
		iMConnection.connect();
		assertEquals(true, iMConnection.connectionActive());
	}

	/**
	 * Test connecting with invalid username.
	 *
	 * @throws Exception the exception while trying to connect
	 *             
	 */
	@Test(expected = IllegalNameException.class)
	public void testConnectInvalidUsername() throws Exception {
		iMConnection = new IMConnection("localhost", 4545, "prajakta_12");
		iMConnection.connect();

	}

	/**
	 * Test connect failing with wrong port number.
	 */
	@Test
	public void testConnectFail() {
		iMConnection = new IMConnection("localhost", 4111, "omar");
		assertFalse(iMConnection.connect());
	}

	/**
	 * Test connect success.
	 */
	@Test
	public void testConnectSuccess() {
		iMConnection = new IMConnection("localhost", 4545, "pra");
		assertEquals(true, iMConnection.connect());
	}

	/**
	 * Test disconnect success.
	 */
	@Test
	public void testDisconnectSuccess() {
		iMConnection = new IMConnection("localhost", 4545, "pra");
		iMConnection.connect();
		iMConnection.disconnect();
		assert true;
	}

	/**
	 * Test send message.
	 */
	@Test
	public void testSendMessage() {
		iMConnection = new IMConnection("localhost", 4545, "sean");
		iMConnection.connect();
		iMConnection.sendMessage("hey I am testing");
		assert true;
	}

	/**
	 * Test sending message failed without making connection.
	 */
	@Test(expected = IllegalOperationException.class)
	public void testSendMessageFailed() {
		iMConnection = new IMConnection("localhost", 4545, "koka");
		iMConnection.sendMessage("hey I am testing");
	}
	
	/**
	 * Test get keyboard scanner singleton instance.
	 */
	@Test
	public void testGetKeyboardScanner() {
		iMConnection = new IMConnection("localhost", 4545, "omar");
		assertEquals(iMConnection.getKeyboardScanner(),iMConnection.getKeyboardScanner());
	}
	
	
	/**
	 * Test message scanner exception before connecting.
	 */
	@Test(expected  = IllegalOperationException.class)
	public void testMessageScannerException() {
		iMConnection = new IMConnection("localhost", 4545, "omar");
		assertEquals(null , iMConnection.getMessageScanner());
	}

	/**
	 * Test message scanner after connecting.
	 */
	@Test
	public void testMessageScanner() {
		iMConnection = new IMConnection("localhost", 4545, "jane");
		iMConnection.connect();
		assertEquals(true , iMConnection.getMessageScanner() instanceof MessageScanner);
	}
}
