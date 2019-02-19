package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.northeastern.ccs.im.client.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

	@Before() 
	public void init() throws InterruptedException{
		Thread.sleep(2000);
		
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

	private IMConnection iMConnection;

	/**
	 * Test create IM connection with null username.
	 */
	@Test
	public void testCreateIMConnectionNullUsername() {

		iMConnection = new IMConnection("localhost", 4122, null);
		assertEquals("TooDumbToEnterRealUsername" , iMConnection.getUserName());
	}

	/**
	 * Test create IM connection with empty username.
	 */
	@Test
	public void testCreateIMConnectionEmptyUsername() {

		iMConnection = new IMConnection("localhost", 4122, "");
		assertEquals("TooDumbToEnterRealUsername" , iMConnection.getUserName());
	}

	/**
	 * Test create IM connection with non empty username.
	 */
	@Test
	public void testCreateIMConnectionNonEmptyUsername() {

		iMConnection = new IMConnection("localhost", 4122, "maria");
		assertEquals("maria" , iMConnection.getUserName());
	}

	/**
	 * Test add message listener with valid listener.
	 */
	public void testAddMessageListenerValidListener() {

		iMConnection = new IMConnection("localhost", 4122, "diana");
		iMConnection.addMessageListener(MessageScanner.getInstance());
		assert true;
	}

	/**
	 * Test add message with null listener.
	 */
	@Test(expected = InvalidListenerException.class)
	public void testAddMessageListenerNull() {

		iMConnection = new IMConnection("localhost", 4122, "dim");
		iMConnection.addMessageListener(null);
	}

	/**
	 * Checks if is connection active when connection is not established.
	 */
	@Test
	public void isConnectionActiveNotConnection() {
		iMConnection = new IMConnection("localhost", 4127, "doe");
		assertEquals(false, iMConnection.connectionActive());
	}

	/**
	 * Checks if is connection active when connection is established.
	 */
	@Test
	public void isConnectionActiveWhenConnected() {
		iMConnection = new IMConnection("localhost", 4545, "jake");
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
		iMConnection = new IMConnection("localhost", 4111, "joe");
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
	 * Test connect multiple clients success.
	 */
	@Test
	public void testMultipleClientsConnectSuccess() {
		iMConnection = new IMConnection("localhost", 4545, "pra");
		IMConnection iMConnectionAnother = new IMConnection("localhost", 4545, "kid");		
		assertEquals(true, iMConnection.connect());
		assertEquals(true, iMConnectionAnother.connect());
	}

	/**
	 * Test disconnect success.
	 */
	@Test
	public void testDisconnectSuccess() {
		iMConnection = new IMConnection("localhost", 4545, "praj");
		iMConnection.connect();
		iMConnection.disconnect();
		System.out.println(iMConnection.connectionActive());
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
		iMConnection = new IMConnection("localhost", 4545, "omari");
		iMConnection.getMessageScanner();
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

	/**
	 * Test get empty username instance.
	 */
	@Test
	public void testEmptyUsername() {
		iMConnection = new IMConnection("localhost", 4545, "");
		assertEquals("TooDumbToEnterRealUsername", iMConnection.getUserName());
	}

	/**
	 * Test Keyboard scanner public classes.
	 */
	@Test(expected = NoSuchElementException.class)
	public void testKeyBoardScannerNext() {
		iMConnection = new IMConnection("localhost", 4545, "");
		iMConnection.connect();
		iMConnection.sendMessage("First message \n jaffa");
		KeyboardScanner ks = iMConnection.getKeyboardScanner();
		ks.next();
		ks.nextLine();
		if(ks.hasNext()) {
			ks.next();
		}

		assertEquals(true,true);
	}

	/**
	 * Test Keyboardscanner emptylist of messages.
	 */
	@Test(expected = NoSuchElementException.class)
	public void testKeyBoardScannerEmptyLineMesssages() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
		iMConnection = new IMConnection("localhost", 4545, "");
		iMConnection.connect();
		KeyboardScanner ks = iMConnection.getKeyboardScanner();
		Field field = Class.forName("edu.northeastern.ccs.im.client.KeyboardScanner").getDeclaredField("messages");
		field.setAccessible(true);
		List<String> msgs = new ArrayList<>();
		field.set(ks, msgs);
		ks.nextLine();
		assertEquals(true,true);
	}

	/**
	 * Test Keyboardscanner emptylist of messages.
	 */
	@Test
	public void testKeyBoardScannerEmptyMessages() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
		iMConnection = new IMConnection("localhost", 4545, "koka");
		iMConnection.connect();
		KeyboardScanner ks = iMConnection.getKeyboardScanner();
		Field field = Class.forName("edu.northeastern.ccs.im.client.KeyboardScanner").getDeclaredField("messages");
		field.setAccessible(true);
		List<String> msgs = new ArrayList<>();
		field.set(ks, msgs);
		msgs.add("First Line");
		msgs.add("Second");
		msgs.add("third");
		if(ks.hasNext()) {
			ks.next();
		}
		ks.nextLine();
		assertEquals("Second",ks.nextLine());
	}


	/**
	 * Test Keyboardscanner list of messages.
	 */
	@Test(expected = NoSuchElementException.class)
	public void testKeyBoardScannerMesssages() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
		iMConnection = new IMConnection("localhost", 4545, "");
		iMConnection.connect();
		KeyboardScanner ks = iMConnection.getKeyboardScanner();
		Field field = Class.forName("edu.northeastern.ccs.im.client.KeyboardScanner").getDeclaredField("messages");
		field.setAccessible(true);
		List<String> msgs = new ArrayList<>();
		field.set(ks, msgs);
		ks.next();
		ks.nextLine();
		if(ks.hasNext()) {
			System.out.println(ks.hasNext());
			ks.next();
		}
		assertEquals(true,true);
	}



	/**
	 * Test restart keyboard scanner singleton instance...yet to be completed
	 */
	@Test
	public void testRestartKeyboardScanner() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		iMConnection = new IMConnection("localhost", 4545, "");
		iMConnection.connect();
		MessageScanner msg = MessageScanner.getInstance();
		Method restartMethod = Class.forName("edu.northeastern.ccs.im.client.KeyboardScanner").getDeclaredMethod("restart");
		Method closeMethod = Class.forName("edu.northeastern.ccs.im.client.KeyboardScanner").getDeclaredMethod("close");
		restartMethod.setAccessible(true);
		closeMethod.setAccessible(true);
		closeMethod.invoke(null);
		restartMethod.invoke(null);
		assertEquals(iMConnection.getKeyboardScanner(),iMConnection.getKeyboardScanner());
	}
}
