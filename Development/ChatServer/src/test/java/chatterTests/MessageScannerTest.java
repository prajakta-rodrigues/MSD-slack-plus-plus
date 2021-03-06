package chatterTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Test;
import org.mockito.Mockito;
import edu.northeastern.ccs.im.client.Message;
import edu.northeastern.ccs.im.client.MessageScanner;

/**
 * The Class MessageScannerTest.
 */
public class MessageScannerTest {

  /** The Constant TESTING. */
  private static final String TESTING = "testing";

  /** The Constant TEST_USER. */
  private static final String TEST_USER = "testing";

  /**
   * Test next messages.
   */
  @Test(expected = NoSuchElementException.class)
  public void testNextMessagesException() {
    MessageScanner messageScanner = MessageScanner.getInstance();
    while (messageScanner.hasNext()) {
      messageScanner.next();
    }
    messageScanner.next();
  }



  @Test
  public void testHasNextMessages() {
    MessageScanner messageScanner = MessageScanner.getInstance();
    Iterator<Message> it = Mockito.mock(Iterator.class);
    Mockito.when(it.hasNext()).thenReturn(true).thenReturn(false);
    Message mesg = Message.makeLoginMessage("heyya");
    Mockito.when(it.next()).thenReturn(mesg);
    messageScanner.messagesReceived(it);
    assertTrue(messageScanner.hasNext());
    while (messageScanner.hasNext()) {
      assertNotNull(messageScanner.next());
    }

  }

  /**
   * Test remove messages.
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testRemoveMessages() {
    MessageScanner messageScanner = MessageScanner.getInstance();
    messageScanner.remove();
  }

  /**
   * Test is acknowlege message.
   */
  @Test
  public void testIsAcknowlegeMessage() {
    Message message = Message.makeAcknowledgeMessage(TESTING);
    assertTrue(message.isAcknowledge());
  }

  /**
   * Test is not acknowlege message.
   */
  @Test
  public void testIsNotAcknowlegeMessage() {
    Message message = Message.makeQuitMessage(TESTING);
    assertFalse(message.isAcknowledge());
  }

  /**
   * Test is broadcast message.
   */
  @Test
  public void testIsBroadcastMessage() {
    Message message = Message.makeBroadcastMessage(TEST_USER, -1,"u");
    assertFalse(message.isCommandMessage());
    assertTrue(message.isBroadcastMessage());

  }

  /**
   * Tests that command messages are command messages and get printed correctly.
   */
  @Test
  public void testCommandMessage() {
    Message message = Message.makeCommandMessage(TEST_USER, -1, "/circle");
    edu.northeastern.ccs.im.server.models.Message message2 =
        edu.northeastern.ccs.im.server.models.Message.makeCommandMessage(TEST_USER, -1, "/quit");
    assertEquals("CMD 7 testing 2 -1 7 /circle", message.toString());
    assertFalse(message.isDisplayMessage());
    assertFalse(message.isBroadcastMessage());
    assertTrue(message.isCommandMessage());

    assertEquals("CMD 7 testing 2 -1 5 /quit", message2.toString());
    assertFalse(message2.isBroadcastMessage());
    assertTrue(message2.isCommandMessage());
    assertEquals(-1, message2.getChannelId());
  }

  /**
   * Test is not broadcast message.
   */
  @Test
  public void testIsNotBroadcastMessage() {
    Message message = Message.makeQuitMessage(TESTING);
    assertFalse(message.isBroadcastMessage());
  }

  /**
   * Test is not command message.
   */
  @Test
  public void testIsNotCommandMessage() {
    edu.northeastern.ccs.im.server.models.Message message =
        edu.northeastern.ccs.im.server.models.Message.makeQuitMessage(TEST_USER);
    assertFalse(message.isCommandMessage());
  }

  /**
   * Test not broadcast message.
   */
  @Test
  public void testNotBroadcastMessage() {
    edu.northeastern.ccs.im.server.models.Message message =
        edu.northeastern.ccs.im.server.models.Message.makeQuitMessage("q");
    assertFalse(message.isBroadcastMessage());

  }

  /**
   * Test is display message.
   */
  @Test
  public void testIsDisplayMessage() {
    Message message = Message.makeBroadcastMessage(TEST_USER, -1,"u");
    assertTrue(message.isDisplayMessage());

  }

  /**
   * Test is not display message.
   */
  @Test
  public void testIsNotDisplayMessage() {
    Message message = Message.makeQuitMessage(TEST_USER);
    assertFalse(message.isDisplayMessage());

  }

  /**
   * Test is initialization message.
   */
  @Test
  public void testIsInitializationMessage() {
    Message message = Message.makeLoginMessage("us");
    assertTrue(message.isInitialization());

  }

  /**
   * Test is not initialization message.
   */
  @Test
  public void testIsNotInitializationMessage() {
    Message message = Message.makeQuitMessage(TEST_USER);
    assertFalse(message.isInitialization());

  }

  /**
   * Test is not termination message.
   */
  @Test
  public void testIsNotTerminationMessage() {
    Message message = Message.makeLoginMessage("us");
    assertFalse(message.terminate());

  }

  /**
   * Test is termination message.
   */
  @Test
  public void testIsTerminationMessage() {
    Message message = Message.makeQuitMessage(TEST_USER);
    assertTrue(message.terminate());

  }

  /**
   * Test to string message sender null.
   */
  @Test
  public void testToStringMessageSenderNull() {
    Message message = Message.makeLoginMessage(null);
    assertEquals("HLO 2 -- 2 -1 2 --", message.toString());
  }

  /**
   * Test to string message sender not null.
   */
  @Test
  public void testToStringMessageSenderNotNull() {
    Message message = Message.makeLoginMessage("test12");
    assertEquals("HLO 6 test12 2 -1 2 --", message.toString());
  }

  /**
   * Test make type broadcast message for server.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  @Test
  public void testMakeTypeBroadcastMessage() throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    Method makeMethod = Class.forName("edu.northeastern.ccs.im.server.models.Message")
        .getDeclaredMethod("makeBroadcastMessage", String.class, String.class);
    makeMethod.setAccessible(true);
    makeMethod.invoke(null, "test1", "testText");
  }

  /**
   * Test make type broadcast message for client.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  @Test
  public void testMakeTypeBroadcastMessage2() throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    Method makeMethod = Class.forName("edu.northeastern.ccs.im.client.Message")
        .getDeclaredMethod("makeBroadcastMessage", String.class, int.class, String.class);
    makeMethod.setAccessible(true);
    makeMethod.invoke(null, "test2", 2, "testing");
  }

  /**
   * Test make type quit message for client.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  @Test
  public void testMakeTypeQuitMessage2() throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    Method makeMethod = Class.forName("edu.northeastern.ccs.im.client.Message")
        .getDeclaredMethod("makeMessage", String.class, String.class, int.class, String.class);
    makeMethod.setAccessible(true);
    makeMethod.invoke(null, "BYE", "tester", -1, "testingText");
  }

  /**
   * Test make type command message for client.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  @Test
  public void testMakeTypeCommandMessage2() throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    Method makeMethod = Class.forName("edu.northeastern.ccs.im.client.Message")
        .getDeclaredMethod("makeMessage", String.class, String.class, int.class, String.class);
    makeMethod.setAccessible(true);
    makeMethod.invoke(null, "CMD", "test2", 2, "testing");
  }

  /**
   * Test make type none message client.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  @Test
  public void testMakeTypeNoneMessageClient() throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    Method makeMethod = Class.forName("edu.northeastern.ccs.im.client.Message")
        .getDeclaredMethod("makeMessage", String.class, String.class, int.class, String.class);
    makeMethod.setAccessible(true);
    assertNull(makeMethod.invoke(null, "zzz", "test1", 1, "testText"));
  }

  /**
   * Test make message broadcast.
   */
  @Test
  public void testMakeMessageBroadcast() {
    Message.makeMessage("BCT", "test", -1,"text");
  }

  /**
   * Test make auth message.
   */
  @Test
  public void testMakeAuthMessage() {
    Message msg = Message.makeAuthenticateMessage("this", "user");
    assertEquals("user", msg.getText());
  }

  /**
   * Test make register message.
   */
  @Test
  public void testMakeRegisterMessage() {
    Message msg = Message.makeRegisterMessage("this", "user");
    assertEquals("user", msg.getText());
  }

  /**
   * Make auth message.
   */
  @Test
  public void makeAuthMessage() {
    Message msg = Message.makeMessage("AUT", "test", -1,"test1");
    assertEquals("test", msg.getSender());
    assertTrue(msg.isAuthenticateMessage());
    assertFalse(msg.isRegisterMessage());
  }

  /**
   * Make register message.
   */
  @Test
  public void makeRegisterMessage() {
    Message msg = Message.makeMessage("REG", "test", -1,"test1");
    assertEquals("test", msg.getSender());
    assertTrue(msg.isRegisterMessage());
    assertFalse(msg.isAuthenticateMessage());
  }
}
