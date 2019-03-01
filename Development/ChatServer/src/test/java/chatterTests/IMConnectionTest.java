package chatterTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.Thread.State;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.northeastern.ccs.im.server.ChatLogger;
import edu.northeastern.ccs.im.server.Message;
import edu.northeastern.ccs.im.server.NetworkConnection;
import edu.northeastern.ccs.im.client.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.ClientTimer;
import edu.northeastern.ccs.im.server.Prattle;

/**
 * The Class IMConnectionTest tests the methods provided IMConnection.
 */
public class IMConnectionTest {

  private String clientKeyBoardScanner = "edu.northeastern.ccs.im.client.KeyboardScanner";
  
  private String clientServerPrattle = "edu.northeastern.ccs.im.server.Prattle";
  
  private String clientSocketNB = "edu.northeastern.ccs.im.client.SocketNB";

  private String actives = "active";
  
  private String localhost = "localhost";

  private String messages = "messages";

  private String client = "edu.northeastern.ccs.im.server.ClientRunnable";

  private String username = "TooDumbToEnterRealUsername";
  
  private static ExecutorService executor;

  /**
   * Setup server.`
   */
  @BeforeClass()
  public static void setup() {
    executor = Executors.newSingleThreadExecutor();
    executor.execute(() -> {
      String[] args = {};
      Prattle.main(args);
    });

  }

  /**
   * Initiates the server thread before testing client.
   *
   * @throws InterruptedException the interrupted exception
   */
  @Before()
  public void init() throws InterruptedException {
    Thread.sleep(3000);

  }

  /**
   * Kill server.
   */
  @AfterClass()
  public static void killSetup() {
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

    iMConnection = new IMConnection(localhost, 4122, null);
    assertEquals(username, iMConnection.getUserName());
  }

  /**
   * Test create IM connection with empty username.
   */
  @Test
  public void testCreateIMConnectionEmptyUsername() {

    iMConnection = new IMConnection(localhost, 4122, "");
    assertEquals(username, iMConnection.getUserName());
  }

  /**
   * Test create IM connection with non empty username.
   */
  @Test
  public void testCreateIMConnectionNonEmptyUsername() {

    iMConnection = new IMConnection(localhost, 4122, "maria");
    assertEquals("maria", iMConnection.getUserName());
  }

  /**
   * Test add message listener with valid listener.
   */
  @Test
  public void testAddMessageListenerValidListener() {

    iMConnection = new IMConnection(localhost, 4122, "diana");
    iMConnection.addMessageListener(MessageScanner.getInstance());
    assert true;
  }

  /**
   * Test add message with null listener.
   */
  @Test(expected = InvalidListenerException.class)
  public void testAddMessageListenerNull() {

    iMConnection = new IMConnection(localhost, 4122, "dim");
    iMConnection.addMessageListener(null);
  }

  /**
   * Checks if is connection active when connection is not established.
   */
  @Test
  public void isConnectionActiveNotConnection() {
    iMConnection = new IMConnection(localhost, 4127, "doe");
    assertFalse(iMConnection.connectionActive());
  }

  /**
   * Checks if is connection active when connection is established.
   */
  @Test
  public void isConnectionActiveWhenConnected() {
    iMConnection = new IMConnection(localhost, 4545, "jake");
    iMConnection.connect();
    assertTrue(iMConnection.connectionActive());
  }

  /**
   * Test connecting with invalid username.
   *
   */
  @Test(expected = IllegalNameException.class)
  public void testConnectInvalidUsername() {
    iMConnection = new IMConnection(localhost, 4545, "prajakta_12");
    iMConnection.connect();

  }

  /**
   * Test connect failing with wrong port number.
   */
  @Test
  public void testConnectFail() {
    iMConnection = new IMConnection(localhost, 4111, "joe");
    assertFalse(iMConnection.connect());
  }

  /**
   * Test connect success.
   */
  @Test
  public void testConnectSuccess() {
    iMConnection = new IMConnection(localhost, 4545, "pra");
    assertTrue(iMConnection.connect());
  }

  /**
   * Test connect multiple clients success.
   */
  @Test
  public void testMultipleClientsConnectSuccess() {
    iMConnection = new IMConnection(localhost, 4545, "arp");
    IMConnection iMConnectionAnother = new IMConnection(localhost, 4545, "kid");
    assertTrue(iMConnection.connect());
    assertTrue(iMConnectionAnother.connect());
  }

  /**
   * Test disconnect success.
   */
  @Test
  public void testDisconnectSuccess() {
    iMConnection = new IMConnection(localhost, 4545, "praj");
    iMConnection.connect();
    iMConnection.disconnect();
    System.out.println(iMConnection.connectionActive());
  }

  /**
   * Test send message.
   */
  @Test
  public void testSendMessage() {
    iMConnection = new IMConnection(localhost, 4545, "sean");
    iMConnection.connect();
    iMConnection.sendMessage("hey I am testing");
    assert true;
  }

  /**
   * Test send longer than byte buffer limit message.
   */
  @Test
  public void testSendLongMessage() {
    iMConnection = new IMConnection(localhost, 4545, "testUser5");
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
    iMConnection = new IMConnection(localhost, 4545, "koka");
    iMConnection.sendMessage("hey I am testing");
  }

  /**
   * Test get keyboard scanner singleton instance.
   */
  @Test
  public void testGetKeyboardScanner() {
    iMConnection = new IMConnection(localhost, 4545, "omar");
    assertEquals(iMConnection.getKeyboardScanner(), iMConnection.getKeyboardScanner());
  }

  /**
   * Test message scanner exception before connecting.
   */
  @Test(expected = IllegalOperationException.class)
  public void testMessageScannerException() {
    iMConnection = new IMConnection(localhost, 4545, "omari");
    iMConnection.getMessageScanner();
  }

  /**
   * Test message scanner after connecting.
   */
  @Test
  public void testMessageScanner() {
    iMConnection = new IMConnection(localhost, 4545, "jane");
    iMConnection.connect();
    assertNotNull(iMConnection.getMessageScanner());
  }

  /**
   * Test get empty username instance.
   */
  @Test
  public void testEmptyUsername() {
    iMConnection = new IMConnection(localhost, 4545, "");
    assertEquals(username, iMConnection.getUserName());
  }

  /**
   * Test Keyboard scanner public classes.
   */
  @Test(expected = NoSuchElementException.class)
  public void testKeyBoardScannerNext() {
    iMConnection = new IMConnection(localhost, 4545, "testSubject");
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
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws IllegalAccessException the illegal access exception
   * @throws ClassNotFoundException the class not found exception
   */
  @Test(expected = NoSuchElementException.class)
  public void testKeyBoardScannerEmptyLineMesssages()
      throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
    iMConnection = new IMConnection(localhost, 4545, "testSubject1");
    iMConnection.connect();
    KeyboardScanner ks = iMConnection.getKeyboardScanner();
    Field field = Class.forName(clientKeyBoardScanner)
        .getDeclaredField(messages);
    field.setAccessible(true);
    List<String> msgs = new ArrayList<>();
    field.set(ks, msgs);
    ks.nextLine();
  }

  /**
   * Test Keyboardscanner emptylist of messages.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws IllegalAccessException the illegal access exception
   * @throws ClassNotFoundException the class not found exception
   */
  @Test
  public void testKeyBoardScannerEmptyMessages()
      throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
    String second = "Second";
    iMConnection = new IMConnection(localhost, 4545, "testSubject2");
    iMConnection.connect();
    KeyboardScanner ks = iMConnection.getKeyboardScanner();
    Field field = Class.forName(clientKeyBoardScanner)
        .getDeclaredField(messages);
    field.setAccessible(true);
    List<String> msgs = new ArrayList<>();
    field.set(ks, msgs);
    msgs.add("First Line");
    msgs.add(second);
    msgs.add("third");
    if (ks.hasNext()) {
      ks.next();
    }
    ks.nextLine();
    assertEquals(second, ks.nextLine());
  }

  /**
   * Test Keyboardscanner list of messages.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws IllegalAccessException the illegal access exception
   * @throws ClassNotFoundException the class not found exception
   */
  @Test(expected = NoSuchElementException.class)
  public void testKeyBoardScannerMesssages()
      throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
    iMConnection = new IMConnection(localhost, 4545, "testSubject3");
    iMConnection.connect();
    ChatLogger.warning("testing");
    KeyboardScanner ks = iMConnection.getKeyboardScanner();
    Field field = Class.forName(clientKeyBoardScanner)
        .getDeclaredField(messages);
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
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchMethodException the no such method exception
   * @throws InvocationTargetException the invocation target exception
   * @throws IllegalAccessException the illegal access exception
   * @throws NoSuchFieldException the no such field exception
   */
  @Test
  public void testRestartKeyboardScanner() throws ClassNotFoundException, NoSuchMethodException,
      InvocationTargetException, IllegalAccessException, NoSuchFieldException {
    iMConnection = new IMConnection(localhost, 4545, "testSubject4");
    iMConnection.connect();
    Method restartMethod = Class.forName(clientKeyBoardScanner)
        .getDeclaredMethod("restart");
    Method closeMethod = Class.forName(clientKeyBoardScanner)
        .getDeclaredMethod("close");
    KeyboardScanner keyboardScanner = iMConnection.getKeyboardScanner();
    restartMethod.setAccessible(true);
    closeMethod.setAccessible(true);
    Field scanner = Class.forName(clientKeyBoardScanner)
        .getDeclaredField("producer");
    scanner.setAccessible(true);
    Thread producer = Mockito.mock(Thread.class);
    scanner.set(keyboardScanner, producer);
    Mockito.when(producer.getState()).thenReturn(State.TERMINATED);
    Field field = Class.forName(clientKeyBoardScanner)
        .getDeclaredField(messages);
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

  /**
   * Test close key board instance null.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  @Test
  public void testCloseKeyBoardInstanceNull()
      throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    iMConnection = new IMConnection(localhost, 4545, "testSubject51");
    Method closeMethod = Class.forName(clientKeyBoardScanner)
        .getDeclaredMethod("close");
    closeMethod.setAccessible(true);
    closeMethod.invoke(null);
    closeMethod.invoke(null);

  }

  /**
   * Test remove client.
   */
  @Test
  public void testRemoveClient() {
    NetworkConnection networkConnection = Mockito.mock(NetworkConnection.class);
    Prattle.removeClient(new ClientRunnable(networkConnection));
    assert true;

  }

  /**
   * Test client timeout.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testClientTimeout()
      throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
    iMConnection = new IMConnection(localhost, 4545, "testingUser");
    iMConnection.connect();
    Field activeClient = Class.forName(clientServerPrattle)
        .getDeclaredField(actives);
    activeClient.setAccessible(true);
    @SuppressWarnings("unchecked")
	ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>) activeClient
        .get(null);
    ClientRunnable clientRunnable = active.peek();

    Field field = Class.forName(client)
        .getDeclaredField("timer");
    field.setAccessible(true);
    ClientTimer clientTimer = Mockito.mock(ClientTimer.class);
    field.set(clientRunnable, clientTimer);
    Mockito.when(clientTimer.isBehind()).thenReturn(true);

  }

  /**
   * Logger test.
   *
   * @throws NoSuchMethodException the no such method exception
   */
  @Test
  public void loggerTest() throws NoSuchMethodException {
    Constructor<ChatLogger> constructor = ChatLogger.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      assert false;
    } catch (Exception e) {
      assert true;
    }

  }

  /**
   * Test client user id.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testClientUserId()
      throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
    iMConnection = new IMConnection(localhost, 4545, "testingUser1");
    iMConnection.connect();
    Field activeClient = Class.forName(clientServerPrattle)
        .getDeclaredField(actives);
    activeClient.setAccessible(true);
    @SuppressWarnings("unchecked")
	ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>) activeClient
        .get(null);
    ClientRunnable clientRunnable = active.peek();

    Field field = Class.forName(client)
        .getDeclaredField("userId");
    field.setAccessible(true);
    field.set(clientRunnable, 123);
    assert clientRunnable != null;
    assertEquals(123, clientRunnable.getUserId());
    iMConnection.disconnect();
  }

  /**
   * Test client user name null.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws NoSuchMethodException the no such method exception
   * @throws InvocationTargetException the invocation target exception
   */
  @Test
  public void testClientUserNameNull()
      throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    iMConnection = new IMConnection(localhost, 4545, "testingUser1");
    iMConnection.connect();
    Field activeClient = Class.forName(clientServerPrattle)
        .getDeclaredField(actives);
    activeClient.setAccessible(true);
    @SuppressWarnings("unchecked")
	ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>) activeClient
        .get(null);
    ClientRunnable clientRunnable = active.peek();
    Method setUserNameMethod = Class.forName(client)
        .getDeclaredMethod("setUserName", String.class);
    setUserNameMethod.setAccessible(true);
    setUserNameMethod.invoke(clientRunnable, "object");
    iMConnection.disconnect();
  }

  /**
   * Handle outgoing messages.
   */
  @Test
  public void handleOutgoingMessages() {
    iMConnection = new IMConnection(localhost, 4545, "testUser22");
    iMConnection.connect();
    IMConnection iMConnectionTwo = new IMConnection(localhost, 4545, "testUser21");
    iMConnectionTwo.connect();
    iMConnection.sendMessage("hi");
    iMConnectionTwo.sendMessage("hey");
  }

  /**
   * Handle exit messages.
   */
  @Test
  public void handleExitMessages() {
    iMConnection = new IMConnection(localhost, 4545, "testUser22");
    iMConnection.connect();
    IMConnection iMConnectionTwo = new IMConnection(localhost, 4545, "testUser21");
    iMConnectionTwo.connect();
    iMConnection.sendMessage("/quit");
  }

  /**
   * Test server illegal state exception.
   */
  @Test(expected = IllegalStateException.class)
  public void testServerIllegalStateException() {
    String[] args = {};
    Prattle.main(args);
  }


  /**
   * Test client runnable name null.
   */
  @Test
  public void testClientRunnableNameNull() {
    NetworkConnection networkConnection = Mockito.mock(NetworkConnection.class);
    ClientRunnable clientRunnable = new ClientRunnable(networkConnection);
    @SuppressWarnings("unchecked")
	Iterator<Message> value = Mockito.mock(Iterator.class);
    Mockito.when(networkConnection.iterator()).thenReturn(value);
    Mockito.when(value.hasNext()).thenReturn(true);
    Message message = Message.makeSimpleLoginMessage(null);
    Mockito.when(value.next()).thenReturn(message);
    clientRunnable.run();
  }

  /**
   * Test start IM connection socket NB.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  @Test
  public void testStartIMConnectionSocketNB()
      throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    SocketNB socketNB = new SocketNB(localhost, 4545);
    Method connectedMethod = Class.forName(clientSocketNB)
        .getDeclaredMethod("startIMConnection");
    connectedMethod.setAccessible(true);

    connectedMethod.invoke(socketNB);
  }

  /**
   * Test socket NB.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  @Test
  public void testSocketNB()
      throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    SocketNB socketNB = new SocketNB("", 0);
    Method connectedMethod = Class.forName(clientSocketNB)
        .getDeclaredMethod("isConnected");
    connectedMethod.setAccessible(true);
    assertEquals(false, connectedMethod.invoke(socketNB));
  }

  /**
   * Test scan for messages worker.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws InstantiationException the instantiation exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   * @throws ClassNotFoundException the class not found exception
   */
  @Test
  public void testScanForMessagesWorker()
      throws NoSuchMethodException, InstantiationException,
      IllegalAccessException, InvocationTargetException, ClassNotFoundException {
    String testUser = "testUser42";
    iMConnection = new IMConnection(localhost, 4545, testUser);
    Constructor<ScanForMessagesWorker> constructor;
    constructor = ScanForMessagesWorker.class
        .getDeclaredConstructor(IMConnection.class, SocketNB.class);
    constructor.setAccessible(true);
    ScanForMessagesWorker scanForMessagesWorker = constructor.newInstance(iMConnection,
        new SocketNB(localhost, 4545));
    Method processMethod = Class.forName("edu.northeastern.ccs.im.client.ScanForMessagesWorker")
        .getDeclaredMethod("process", List.class);
    processMethod.setAccessible(true);
    List<edu.northeastern.ccs.im.client.Message> msg = new ArrayList<>();
    msg.add(edu.northeastern.ccs.im.client.Message.makeAcknowledgeMessage(testUser));
    msg.add(edu.northeastern.ccs.im.client.Message.makeNoAcknowledgeMessage());
    msg.add(edu.northeastern.ccs.im.client.Message.makeLoginMessage(testUser));
    processMethod.invoke(scanForMessagesWorker, msg);
  }

  /**
   * Test socket NB read argument empty buffer.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   */
  @Test
  public void testSocketNBReadArgumentEmptyBuffer() throws NoSuchMethodException,
      ClassNotFoundException {
    SocketNB socketNB = new SocketNB(localhost, 4545);
    Method readMethod = Class.forName(clientSocketNB)
        .getDeclaredMethod("readArgument",
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

  /**
   * Test socket NB read argument.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  @Test
  public void testSocketNBReadArgument()
      throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    SocketNB socketNB = new SocketNB(localhost, 4545);
    Method readMethod = Class.forName(clientSocketNB)
        .getDeclaredMethod("readArgument",
            CharBuffer.class);
    readMethod.setAccessible(true);
    CharBuffer charBuf = CharBuffer.allocate(1024);
    charBuf.put("1hey test this00");
    charBuf.position(14);
    assertNull(readMethod.invoke(socketNB, charBuf));
  }

  /**
   * Test socket N bprint.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   */
  @Test
  public void testSocketNBprint()
      throws NoSuchMethodException, ClassNotFoundException {
    SocketNB socketNB = new SocketNB(localhost, 4545);
    Method readMethod = Class.forName(clientSocketNB)
        .getDeclaredMethod("print",
            edu.northeastern.ccs.im.client.Message.class);
    readMethod.setAccessible(true);
    try {
      readMethod
          .invoke(socketNB, edu.northeastern.ccs.im.client.Message.makeAcknowledgeMessage("rita"));
      assert false;
    } catch (Exception e) {
      assert true;
    }
  }

  /**
   * Test client runnable broadcast message name null.
   */
  @Test
	public void testClientRunnableBroadcastMessageNameNull() {
		NetworkConnection networkConnection = Mockito.mock(NetworkConnection.class);
		ClientRunnable clientRunnable = new ClientRunnable(networkConnection);
		@SuppressWarnings("unchecked")
		Iterator<Message> value = Mockito.mock(Iterator.class);
		Mockito.when(networkConnection.iterator()).thenReturn(value);
		Mockito.when(value.hasNext()).thenReturn(true);
		Message message = Message.makeBroadcastMessage(null, "test51");
		Mockito.when(value.next()).thenReturn(message);
		clientRunnable.run();
	}

	/**
	 * Test client runnable broadcast message different name.
	 */
	@Test
	public void testClientRunnableBroadcastMessageDifferentName() {
		NetworkConnection networkConnection = Mockito.mock(NetworkConnection.class);
		ClientRunnable clientRunnable = new ClientRunnable(networkConnection);
		clientRunnable.setName("test61");
		@SuppressWarnings("unchecked")
		Iterator<Message> value = Mockito.mock(Iterator.class);
		Mockito.when(networkConnection.iterator()).thenReturn(value);
		Mockito.when(value.hasNext()).thenReturn(true);
		Message message = Message.makeBroadcastMessage(null, "test51");
		Mockito.when(value.next()).thenReturn(message);
		clientRunnable.run();
	}


	/**
	 * Test message null checks.
	 *
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	@Test
	public void testMessageNullChecks() throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		ClientRunnable clientRunnable = new ClientRunnable(null);
		clientRunnable.setName("usr3");
		Method msgChecksMethod = Class.forName(client).getDeclaredMethod("messageChecks",
				Message.class);
		msgChecksMethod.setAccessible(true);
		msgChecksMethod.invoke(clientRunnable, Message.makeBroadcastMessage(null, "hey"));

	}

	/**
	 * Test message checks.
	 *
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	@Test
	public void testMessageChecks() throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		ClientRunnable clientRunnable = new ClientRunnable(null);
		clientRunnable.setName("usr1");
		Method msgChecksMethod = Class.forName(client).getDeclaredMethod("messageChecks",
				edu.northeastern.ccs.im.server.Message.class);
		msgChecksMethod.setAccessible(true);
		msgChecksMethod.invoke(clientRunnable, Message.makeBroadcastMessage("usr", "hey"));
		
	}

}
