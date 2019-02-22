package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.lang.Thread.State;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import edu.northeastern.ccs.im.ChatLogger;
import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.client.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.ClientTimer;
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
	public void init() throws InterruptedException {
		Thread.sleep(3000);

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
		assertEquals("TooDumbToEnterRealUsername", iMConnection.getUserName());
	}

	/**
	 * Test create IM connection with empty username.
	 */
	@Test
	public void testCreateIMConnectionEmptyUsername() {

		iMConnection = new IMConnection("localhost", 4122, "");
		assertEquals("TooDumbToEnterRealUsername", iMConnection.getUserName());
	}

	/**
	 * Test create IM connection with non empty username.
	 */
	@Test
	public void testCreateIMConnectionNonEmptyUsername() {

		iMConnection = new IMConnection("localhost", 4122, "maria");
		assertEquals("maria", iMConnection.getUserName());
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
	 * @throws Exception
	 *             the exception while trying to connect
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
		iMConnection = new IMConnection("localhost", 4545, "arp");
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
	 * Test send longer than byte buffer limit message.
	 */
	@Test
	public void testSendLongMessage() {
		iMConnection = new IMConnection("localhost", 4545, "testUser5");
		iMConnection.connect();
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < 1421; i++) {
			str.append("hey I am testinguyyyyyyyyyyyyyyyyyuuuuuuuyyyyyyyyyyyy ");
		}
		iMConnection.sendMessage(str.toString());
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
		assertEquals(iMConnection.getKeyboardScanner(), iMConnection.getKeyboardScanner());
	}

	/**
	 * Test message scanner exception before connecting.
	 */
	@Test(expected = IllegalOperationException.class)
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
		assertEquals(true, iMConnection.getMessageScanner() instanceof MessageScanner);
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
		iMConnection = new IMConnection("localhost", 4545, "testSubject");
		iMConnection.connect();
		iMConnection.sendMessage("First message \n jaffa");
		KeyboardScanner ks = iMConnection.getKeyboardScanner();
		ks.next();
		ks.nextLine();
		if (ks.hasNext()) {
			ks.next();
		}
		ks.next();
	}

	/**
	 * Test Keyboardscanner emptylist of messages.
	 */
	@Test(expected = NoSuchElementException.class)
	public void testKeyBoardScannerEmptyLineMesssages()
			throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
		iMConnection = new IMConnection("localhost", 4545, "testSubject1");
		iMConnection.connect();
		KeyboardScanner ks = iMConnection.getKeyboardScanner();
		Field field = Class.forName("edu.northeastern.ccs.im.client.KeyboardScanner").getDeclaredField("messages");
		field.setAccessible(true);
		List<String> msgs = new ArrayList<>();
		field.set(ks, msgs);
		ks.nextLine();
		assertEquals(true, true);
	}

	/**
	 * Test Keyboardscanner emptylist of messages.
	 */
	@Test
	public void testKeyBoardScannerEmptyMessages()
			throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
		iMConnection = new IMConnection("localhost", 4545, "testSubject2");
		iMConnection.connect();
		KeyboardScanner ks = iMConnection.getKeyboardScanner();
		Field field = Class.forName("edu.northeastern.ccs.im.client.KeyboardScanner").getDeclaredField("messages");
		field.setAccessible(true);
		List<String> msgs = new ArrayList<>();
		field.set(ks, msgs);
		msgs.add("First Line");
		msgs.add("Second");
		msgs.add("third");
		if (ks.hasNext()) {
			ks.next();
		}
		ks.nextLine();
		assertEquals("Second", ks.nextLine());
	}

	/**
	 * Test Keyboardscanner list of messages.
	 */
	@Test(expected = NoSuchElementException.class)
	public void testKeyBoardScannerMesssages()
			throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
		iMConnection = new IMConnection("localhost", 4545, "testSubject3");
		iMConnection.connect();
		ChatLogger.warning("testing");
		KeyboardScanner ks = iMConnection.getKeyboardScanner();
		Field field = Class.forName("edu.northeastern.ccs.im.client.KeyboardScanner").getDeclaredField("messages");
		field.setAccessible(true);
		List<String> msgs = new ArrayList<>();
		field.set(ks, msgs);
		ks.next();
		ks.nextLine();
		if (ks.hasNext()) {
			ks.next();
		}
	}

	/**
	 * Test restart keyboard scanner singleton instance...yet to be completed
	 * 
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	@Test
	public void testRestartKeyboardScanner() throws ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException, IllegalAccessException, NoSuchFieldException, SecurityException {
		iMConnection = new IMConnection("localhost", 4545, "testSubject4");
		iMConnection.connect();
		Method restartMethod = Class.forName("edu.northeastern.ccs.im.client.KeyboardScanner")
				.getDeclaredMethod("restart");
		Method closeMethod = Class.forName("edu.northeastern.ccs.im.client.KeyboardScanner").getDeclaredMethod("close");
		KeyboardScanner keyboardScanner = iMConnection.getKeyboardScanner();
		restartMethod.setAccessible(true);
		closeMethod.setAccessible(true);
		Field scanner = Class.forName("edu.northeastern.ccs.im.client.KeyboardScanner").getDeclaredField("producer");
		scanner.setAccessible(true);
		Thread producer = Mockito.mock(Thread.class);
		scanner.set(keyboardScanner, producer);
		Mockito.when(producer.getState()).thenReturn(State.TERMINATED);
		Field field = Class.forName("edu.northeastern.ccs.im.client.KeyboardScanner").getDeclaredField("messages");
		field.setAccessible(true);
		List<String> msgs = new ArrayList<>();
		field.set(keyboardScanner, msgs);
		msgs.add("First Line");
		msgs.add("Second");
		msgs.add("third");
		if (keyboardScanner.hasNext()) {
			keyboardScanner.next();
		}
		closeMethod.invoke(keyboardScanner);
		restartMethod.invoke(keyboardScanner);
	}

	@Test
	public void testCloseKeyBoardInstanceNull() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		iMConnection = new IMConnection("localhost", 4545, "testSubject51");
		Method closeMethod = Class.forName("edu.northeastern.ccs.im.client.KeyboardScanner").getDeclaredMethod("close");
		closeMethod.setAccessible(true);
		closeMethod.invoke(null);
		closeMethod.invoke(null);

	}

	@Test
	public void testRemoveClient() throws IOException {
		NetworkConnection networkConnection = Mockito.mock(NetworkConnection.class);
		Prattle.removeClient(new ClientRunnable(networkConnection));
		assert true;

	}
	
	@Test
	public void testClientTimeout() throws NoSuchFieldException, SecurityException, ClassNotFoundException,
			IllegalArgumentException, IllegalAccessException {
		iMConnection = new IMConnection("localhost", 4545, "testingUser");
		iMConnection.connect();
		Field activeClient = Class.forName("edu.northeastern.ccs.im.server.Prattle").getDeclaredField("active");
		activeClient.setAccessible(true);
		ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>) activeClient.get(null);
		ClientRunnable clientRunnable = active.peek();

		Field field = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable").getDeclaredField("timer");
		field.setAccessible(true);
		ClientTimer clientTimer = Mockito.mock(ClientTimer.class);
		field.set(clientRunnable, clientTimer);
		Mockito.when(clientTimer.isBehind()).thenReturn(true);

	}

	@Test
	public void loggerTest() throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<ChatLogger> constructor = ChatLogger.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		try {
			constructor.newInstance();
			assert false;
		} catch (Exception e) {
			assert true;
		}

	}

	@Test
	public void testClientUserId() throws NoSuchFieldException, SecurityException, ClassNotFoundException,
			IllegalArgumentException, IllegalAccessException {
		iMConnection = new IMConnection("localhost", 4545, "testingUser1");
		iMConnection.connect();
		Field activeClient = Class.forName("edu.northeastern.ccs.im.server.Prattle").getDeclaredField("active");
		activeClient.setAccessible(true);
		ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>) activeClient.get(null);
		ClientRunnable clientRunnable = active.peek();

		Field field = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable").getDeclaredField("userId");
		field.setAccessible(true);
		field.set(clientRunnable, 123);
		assertEquals(123, clientRunnable.getUserId());
		iMConnection.disconnect();
	}

	@Test
	public void testClientUserNameNull() throws NoSuchFieldException, SecurityException, ClassNotFoundException,
			IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		iMConnection = new IMConnection("localhost", 4545, "testingUser1");
		iMConnection.connect();
		Field activeClient = Class.forName("edu.northeastern.ccs.im.server.Prattle").getDeclaredField("active");
		activeClient.setAccessible(true);
		ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>) activeClient.get(null);
		ClientRunnable clientRunnable = active.peek();
		Method setUserNameMethod = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
				.getDeclaredMethod("setUserName", String.class);
		setUserNameMethod.setAccessible(true);
		setUserNameMethod.invoke(clientRunnable, new Object[] { null });
		iMConnection.disconnect();
	}

	@Test
	public void handleOutgoingMessages() {
		iMConnection = new IMConnection("localhost", 4545, "testUser22");
		iMConnection.connect();
		IMConnection iMConnectionTwo = new IMConnection("localhost", 4545, "testUser21");
		iMConnectionTwo.connect();
		iMConnection.sendMessage("hi");
		iMConnectionTwo.sendMessage("hey");
	}

	@Test
	public void handleExitMessages() {
		iMConnection = new IMConnection("localhost", 4545, "testUser22");
		iMConnection.connect();
		IMConnection iMConnectionTwo = new IMConnection("localhost", 4545, "testUser21");
		iMConnectionTwo.connect();
		iMConnection.sendMessage("/quit");
	}

	@Test(expected = IllegalStateException.class)
	public void testServerIllegalStateException() {
		String[] args = {};
		Prattle.main(args);
	}

	
	
	@Test
	public void testClientRunnableNameNull() {
		NetworkConnection networkConnection = Mockito.mock(NetworkConnection.class);
		ClientRunnable clientRunnable = new ClientRunnable(networkConnection);
		Iterator<Message> value = Mockito.mock(Iterator.class);
		Mockito.when(networkConnection.iterator()).thenReturn(value);
		Mockito.when(value.hasNext()).thenReturn(true);
		Message message = Message.makeSimpleLoginMessage(null);
		Mockito.when(value.next()).thenReturn(message);
		clientRunnable.run();
	}

	@Test
	public void testStartIMConnectionSocketNB() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SocketNB socketNB = new SocketNB("localhost", 4545);
		Method connectedMethod = Class.forName("edu.northeastern.ccs.im.client.SocketNB")
				.getDeclaredMethod("startIMConnection");
		connectedMethod.setAccessible(true);

		connectedMethod.invoke(socketNB);
	}

	@Test
	public void testSocketNB() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SocketNB socketNB = new SocketNB("", 0);
		Method connectedMethod = Class.forName("edu.northeastern.ccs.im.client.SocketNB")
				.getDeclaredMethod("isConnected");
		connectedMethod.setAccessible(true);
		assertEquals(false, connectedMethod.invoke(socketNB));
	}

	@Test
	public void testScanForMessagesWorker() throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
		iMConnection = new IMConnection("localhost", 4545, "testUser42");
		Constructor<ScanForMessagesWorker> constructor;
		constructor = ScanForMessagesWorker.class.getDeclaredConstructor(IMConnection.class, SocketNB.class);
		constructor.setAccessible(true);
		ScanForMessagesWorker scanForMessagesWorker = constructor.newInstance(iMConnection,
				new SocketNB("localhost", 4545));
		Method processMethod = Class.forName("edu.northeastern.ccs.im.client.ScanForMessagesWorker")
				.getDeclaredMethod("process", List.class);
		processMethod.setAccessible(true);
		List<edu.northeastern.ccs.im.client.Message> msg = new ArrayList<>();
		msg.add(edu.northeastern.ccs.im.client.Message.makeAcknowledgeMessage("testUser42"));
		msg.add(edu.northeastern.ccs.im.client.Message.makeNoAcknowledgeMessage());
		msg.add(edu.northeastern.ccs.im.client.Message.makeLoginMessage("testUser42"));
		processMethod.invoke(scanForMessagesWorker, msg);
	}

	@Test
	public void testSocketNBReadArgumentEmptyBuffer() throws NoSuchMethodException, SecurityException,
			ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SocketNB socketNB = new SocketNB("locahost", 4545);
		Method readMethod = Class.forName("edu.northeastern.ccs.im.client.SocketNB").getDeclaredMethod("readArgument",
				CharBuffer.class);
		readMethod.setAccessible(true);
		CharBuffer charBuf = CharBuffer.allocate(1024);
		charBuf.put("1hey test this");
		charBuf.position(12);
		try {
			readMethod.invoke(socketNB, charBuf);
			assert false;
		} catch (Exception e) {
			assert true;
		}
	}

	@Test
	public void testSocketNBReadArgument() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SocketNB socketNB = new SocketNB("locahost", 4545);
		Method readMethod = Class.forName("edu.northeastern.ccs.im.client.SocketNB").getDeclaredMethod("readArgument",
				CharBuffer.class);
		readMethod.setAccessible(true);
		CharBuffer charBuf = CharBuffer.allocate(1024);
		charBuf.put("1hey test this00");
		charBuf.position(14);
		assertEquals(null, readMethod.invoke(socketNB, charBuf));
	}

	@Test
	public void testMessageChecks() throws NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ClientRunnable clientRunnable = new ClientRunnable(null); 
		clientRunnable.setName("usr1");
		Method msgChecksMethod = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable").getDeclaredMethod("messageChecks",
				edu.northeastern.ccs.im.Message.class);
		msgChecksMethod.setAccessible(true);
		msgChecksMethod.invoke(clientRunnable, Message.makeBroadcastMessage("usr", "hey"));
		
	}
	
	@Test
	public void testSocketNBprint() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SocketNB socketNB = new SocketNB("locahost", 4545);
		Method readMethod = Class.forName("edu.northeastern.ccs.im.client.SocketNB").getDeclaredMethod("print",
				edu.northeastern.ccs.im.client.Message.class);
		readMethod.setAccessible(true);
		try {
			readMethod.invoke(socketNB, edu.northeastern.ccs.im.client.Message.makeAcknowledgeMessage("rita"));
			assert false;
		} catch (Exception e) {
			assert true;
		}
	}
	
	

}
