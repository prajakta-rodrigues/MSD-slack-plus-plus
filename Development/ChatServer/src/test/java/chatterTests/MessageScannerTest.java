package chatterTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;

import org.junit.Test;

import edu.northeastern.ccs.im.server.MessageType;
import edu.northeastern.ccs.im.client.Message;
import edu.northeastern.ccs.im.client.MessageScanner;

/**
 * The Class MessageScannerTest.
 */
public class MessageScannerTest {

  private static final String TESTING = "testing";

  private static final String TEST_USER = "testing";

  /**
   * Test next messages.
   */
  @Test(expected = NoSuchElementException.class)
  public void testNextMessages() {
    MessageScanner messageScanner = MessageScanner.getInstance();
    while (messageScanner.hasNext()) {
      messageScanner.next();
    }
    messageScanner.next();
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
    Message messsage = Message.makeAcknowledgeMessage(TESTING);
    assertTrue(messsage.isAcknowledge());

  }

  /**
   * Test is not acknowlege message.
   */
  @Test
  public void testIsNotAcknowlegeMessage() {
    Message messsage = Message.makeQuitMessage(TESTING);
    assertFalse(messsage.isAcknowledge());

  }

  /**
   * Test is broadcast message.
   */
  @Test
  public void testIsBroadcastMessage() {
    Message messsage = Message.makeBroadcastMessage(TEST_USER, "u");
    assertFalse(messsage.isCommandMessage());
    assertTrue(messsage.isBroadcastMessage());

  }

  @Test
  public void testIsCommandMessage() {
    Message message = Message.makeCommandMessage(TEST_USER, "/circle");
    assertEquals("CMD 7 testing 7 /circle", message.toString());
    assertFalse(message.isDisplayMessage());
    assertFalse(message.isBroadcastMessage());
    assertTrue(message.isCommandMessage());

  }
  /**
   * Test is not broadcast message.
   */
  @Test
  public void testIsNotBroadcastMessage() {
    Message messsage = Message.makeQuitMessage(TESTING);
    assertFalse(messsage.isBroadcastMessage());

  }

  /**
   * Test not broadcast message.
   */
  @Test
  public void testNotBroadcastMessage() {
    edu.northeastern.ccs.im.server.Message messsage = edu.northeastern.ccs.im.server.Message
        .makeQuitMessage("q");
    assertFalse(messsage.isBroadcastMessage());

  }

  /**
   * Test is display message.
   */
  @Test
  public void testIsDisplayMessage() {
    Message messsage = Message.makeBroadcastMessage(TEST_USER, "u");
    assertTrue(messsage.isDisplayMessage());

  }

  /**
   * Test is not display message.
   */
  @Test
  public void testIsNotDisplayMessage() {
    Message messsage = Message.makeQuitMessage(TEST_USER);
    assertFalse(messsage.isDisplayMessage());

  }

  /**
   * Test is initialization message.
   */
  @Test
  public void testIsInitializationMessage() {
    Message messsage = Message.makeLoginMessage("us");
    assertTrue(messsage.isInitialization());

  }

  /**
   * Test is not initialization message.
   */
  @Test
  public void testIsNotInitializationMessage() {
    Message messsage = Message.makeQuitMessage(TEST_USER);
    assertFalse(messsage.isInitialization());

  }

  /**
   * Test is not termination message.
   */
  @Test
  public void testIsNotTerminationMessage() {
    Message messsage = Message.makeLoginMessage("us");
    assertFalse(messsage.terminate());

  }

  /**
   * Test is termination message.
   */
  @Test
  public void testIsTerminationMessage() {
    Message messsage = Message.makeQuitMessage(TEST_USER);
    assertTrue(messsage.terminate());

  }

  /**
   * Test to string message sender null.
   */
  @Test
  public void testToStringMessageSenderNull() {
    Message messsage = Message.makeLoginMessage(null);
    assertEquals("HLO 2 -- 2 --", messsage.toString());
  }

  /**
   * Test to string message sender not null.
   */
  @Test
  public void testToStringMessageSenderNotNull() {
    Message messsage = Message.makeLoginMessage("test12");
    assertEquals("HLO 6 test12 2 --", messsage.toString());
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
    Method makeMethod = Class.forName("edu.northeastern.ccs.im.server.Message")
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
        .getDeclaredMethod("makeBroadcastMessage", String.class, String.class);
    makeMethod.setAccessible(true);
    makeMethod.invoke(null, "test2", "testing");
  }


  /**
   * Test make type quit message.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  @Test
  public void testMakeTypeQuitMessage() throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    Method makeMethod = Class.forName("edu.northeastern.ccs.im.server.Message")
        .getDeclaredMethod("makeMessage", String.class, String.class, String.class);
    makeMethod.setAccessible(true);
    makeMethod.invoke(null, MessageType.QUIT.toString(), "test1", "testText");
  }

  /**
   * Test make type command message for server.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  @Test
  public void testMakeTypeCommandMessage() throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    Method makeMethod = Class.forName("edu.northeastern.ccs.im.server.Message")
        .getDeclaredMethod("makeMessage", String.class, String.class, String.class);
    makeMethod.setAccessible(true);
    makeMethod.invoke(null, MessageType.COMMAND.toString(), "test1", "testText");
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
        .getDeclaredMethod("makeMessage", String.class, String.class, String.class);
    makeMethod.setAccessible(true);
    makeMethod.invoke(null, MessageType.COMMAND.toString(), "test2", "testing");
  }


  /**
   * Test make type hello message.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  @Test
  public void testMakeTypeHello() throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    Method makeMethod = Class.forName("edu.northeastern.ccs.im.server.Message")
        .getDeclaredMethod("makeMessage", String.class, String.class, String.class);
    makeMethod.setAccessible(true);
    makeMethod.invoke(null, MessageType.HELLO.toString(), "test1", "testText");
  }

  /**
   * Test make type none message.
   *
   * @throws NoSuchMethodException the no such method exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   */
  @Test
  public void testMakeTypeNoneMessage() throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    Method makeMethod = Class.forName("edu.northeastern.ccs.im.server.Message")
        .getDeclaredMethod("makeMessage", String.class, String.class, String.class);
    makeMethod.setAccessible(true);
    assertNull(makeMethod.invoke(null, "zzz", "test1", "testText"));
  }


}
