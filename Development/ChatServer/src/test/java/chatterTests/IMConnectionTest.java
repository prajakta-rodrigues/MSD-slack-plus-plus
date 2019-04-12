package chatterTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.Thread.State;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import edu.northeastern.ccs.im.client.*;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * The Class IMConnectionTest tests the methods provided IMConnection.
 */
public class IMConnectionTest {

  private String clientKeyBoardScanner = "edu.northeastern.ccs.im.client.KeyboardScanner";


  private String localhost = "localhost";

  private String messages = "messages";

  private String username = "TooDumbToEnterRealUsername";


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
 * @throws ClassNotFoundException exception
 * @throws SecurityException exception
 * @throws NoSuchFieldException exception
 * @throws IllegalAccessException exception
 * @throws IllegalArgumentException exception
   */
  @Test
  public void isConnectionActiveWhenConnected() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    iMConnection = new IMConnection(localhost, 4545, "jake");
    iMConnection.connect();
    Field socketConnection = Class.forName("edu.northeastern.ccs.im.client.IMConnection").getDeclaredField("socketConnection");
    socketConnection.setAccessible(true);
    SocketNB socketConnectionMock = new SocketNB("localhost", 4141) {
    	@Override
    	protected boolean isConnected() {
    		return true;
    	}
    };
	socketConnection.set(iMConnection, socketConnectionMock);
    assertTrue(iMConnection.connectionActive());
  }

  /**
   * Test connecting with invalid username.
   */
  @Test(expected = IllegalNameException.class)
  public void testConnectInvalidUsername() {
    iMConnection = new IMConnection(localhost, 4145, "prajakta_12");
    iMConnection.connect();

  }

  /**
   * Test connect failing with wrong port number.
   */
  @Test
  public void testConnectExceptionFail() {
    iMConnection = new IMConnection(localhost, 4111, "joe");
    assertFalse(iMConnection.connect());
  }

  /**
   * Test connect success.
   */
  @Test
  public void testConnectSuccess() {
    iMConnection = new IMConnection(localhost, 4045, "pra") {
    	@Override
    	protected boolean login() {
			return true;
    	}
    };
    assertTrue(iMConnection.connect());
  }

  /**
   * Test connect fail login.
   */
  @Test
  public void testConnectFail() {
    iMConnection = new IMConnection(localhost, 4045, "arp") {
        	@Override
        	protected boolean login() {
    			return false;
        	}
    };
    assertFalse(iMConnection.connect());
  }

  /**
   * Test disconnect success.
 * @throws ClassNotFoundException exception
 * @throws SecurityException exception
 * @throws NoSuchFieldException exception
 * @throws IllegalAccessException exception
 * @throws IllegalArgumentException exception
   */
  @Test
  public void testDisconnectSuccess() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    iMConnection = new IMConnection(localhost, 4545, "praj");
    Field socketConnection = Class.forName("edu.northeastern.ccs.im.client.IMConnection").getDeclaredField("socketConnection");
    socketConnection.setAccessible(true);
    SocketNB socketConnectionMock = new SocketNB("localhost", 4141) {
    	@Override
    	protected boolean isConnected() {
    		return true;
    	}
    	
    	@Override
    	protected void print(edu.northeastern.ccs.im.client.Message msg) {
    	
    	}
    };
	socketConnection.set(iMConnection, socketConnectionMock);
    iMConnection.disconnect();
  }

  /**
   * Test send message fail.
   */
  @Test(expected = IllegalOperationException.class)
  public void testSendMessageFail() {
    iMConnection = new IMConnection(localhost, 4545, "sean");
    iMConnection.sendMessage("hey I am testing");
  }
  
  /**
   * Test send message success.
 * @throws ClassNotFoundException exception
 * @throws SecurityException exception
 * @throws NoSuchFieldException exception
 * @throws IllegalAccessException exception
 * @throws IllegalArgumentException exception
   */
  @Test
  public void testSendMessageSuccess() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	  iMConnection = new IMConnection(localhost, 4545, "sean"){
	    	@Override
	    	public boolean connectionActive() {
	    		return true;
	    	}
	    };
	    Field socketConnection = Class.forName("edu.northeastern.ccs.im.client.IMConnection").getDeclaredField("socketConnection");
	    socketConnection.setAccessible(true);
	    SocketNB socketConnectionMock = new SocketNB("localhost", 4141) {
	    	
	    	@Override
	    	protected void print(edu.northeastern.ccs.im.client.Message msg) {
	    	
	    	}
	    };
		socketConnection.set(iMConnection, socketConnectionMock);
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
    iMConnection = new IMConnection(localhost, 4145, "omari");
    iMConnection.getMessageScanner();
  }

  /**
   * Test message scanner after connecting.
   */
  @Test
  public void testMessageScanner() {
    iMConnection = new IMConnection(localhost, 4245, "jane") {
    	@Override
    	protected boolean login() {
			return true;
    	}
    };
    iMConnection.connect();
    assertNotNull(iMConnection.getMessageScanner());
  }

  /**
   * Test get empty username instance.
   */
  @Test
  public void testEmptyUsername() {
    iMConnection = new IMConnection(localhost, 4145, "");
    assertEquals(username, iMConnection.getUserName());
  }

  /**
   * Test Keyboard scanner public classes.
 * @throws ClassNotFoundException exception
 * @throws SecurityException exception
 * @throws NoSuchFieldException exception
 * @throws IllegalAccessException exception
 * @throws IllegalArgumentException exception
   */
  @Test(expected = NoSuchElementException.class)
  public void testKeyBoardScannerNext() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    iMConnection = new IMConnection(localhost, 4145, "testSubject") {
    	@Override
    	public boolean connectionActive() {
    		return true;
    	}
    };
    iMConnection.connect();
    Field socketConnection = Class.forName("edu.northeastern.ccs.im.client.IMConnection").getDeclaredField("socketConnection");
    socketConnection.setAccessible(true);
    SocketNB socketConnectionMock = new SocketNB("localhost", 4141) {
    	
    	@Override
    	protected void print(edu.northeastern.ccs.im.client.Message msg) {
    	
    	}
    };
	socketConnection.set(iMConnection, socketConnectionMock);
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

  
	@Test
	public void testSendCommandMessage() throws NoSuchFieldException, SecurityException, ClassNotFoundException,
			IllegalArgumentException, IllegalAccessException {
		iMConnection = new IMConnection(localhost, 4145, "testUser42") {
			@Override()
			public boolean connectionActive() {
				return true;
			}
		};
		Field socketConnection = Class.forName("edu.northeastern.ccs.im.client.IMConnection")
				.getDeclaredField("socketConnection");
		socketConnection.setAccessible(true);
		SocketNB socketConnectionMock = new SocketNB("localhost", 4141) {
			@Override
			protected boolean isConnected() {
				return true;
			}

			@Override
			protected void print(edu.northeastern.ccs.im.client.Message msg) {

			}
		};
		socketConnection.set(iMConnection, socketConnectionMock);
		iMConnection.sendMessage("/help");
	}
	
	@Test
	public void testSendRegisterMessage() throws IllegalArgumentException, IllegalAccessException, 
	NoSuchFieldException, SecurityException, ClassNotFoundException {
		iMConnection = new IMConnection(localhost, 4145, "testUser42") {
			@Override()
			public boolean connectionActive() {
				return true;
			}
		};
		Field socketConnection = Class.forName("edu.northeastern.ccs.im.client.IMConnection")
				.getDeclaredField("socketConnection");
		socketConnection.setAccessible(true);
		SocketNB socketConnectionMock = new SocketNB("localhost", 4141) {
			@Override
			protected boolean isConnected() {
				return true;
			}

			@Override
			protected void print(edu.northeastern.ccs.im.client.Message msg) {

			}
		};
		socketConnection.set(iMConnection, socketConnectionMock);
		iMConnection.sendMessage(":register hu");
	}

	@Test
	public void testSendAuthenticateMessage() throws IllegalArgumentException, IllegalAccessException, 
	NoSuchFieldException, SecurityException, ClassNotFoundException {
		iMConnection = new IMConnection(localhost, 4145, "testUser42") {
			@Override()
			public boolean connectionActive() {
				return true;
			}
		};
		Field socketConnection = Class.forName("edu.northeastern.ccs.im.client.IMConnection")
				.getDeclaredField("socketConnection");
		socketConnection.setAccessible(true);
		SocketNB socketConnectionMock = new SocketNB("localhost", 4141) {
			@Override
			protected boolean isConnected() {
				return true;
			}

			@Override
			protected void print(edu.northeastern.ccs.im.client.Message msg) {

			}
		};
		socketConnection.set(iMConnection, socketConnectionMock);
		iMConnection.sendMessage(":authenticate hu");
	}
	
}
