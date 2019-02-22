package chatterTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;

import org.junit.Test;

import edu.northeastern.ccs.im.MessageType;
import edu.northeastern.ccs.im.client.Message;
import edu.northeastern.ccs.im.client.MessageScanner;

public class MessageScannerTest {

  private static final String TESTING = "testing";
  private static final String TEST_USER = "testing";

  @Test(expected = NoSuchElementException.class)
  public void testNextMessages() {
    MessageScanner messageScanner = MessageScanner.getInstance();
    while (messageScanner.hasNext()) {
      messageScanner.next();
    }
    messageScanner.next();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testRemoveMessages() {
    MessageScanner messageScanner = MessageScanner.getInstance();
    messageScanner.remove();
  }

  @Test
  public void testIsAcknowlegeMessage() {
    Message messsage = Message.makeAcknowledgeMessage(TESTING);
    assertTrue(messsage.isAcknowledge());

  }

  @Test
  public void testIsNotAcknowlegeMessage() {
    Message messsage = Message.makeQuitMessage(TESTING);
    assertFalse(messsage.isAcknowledge());

  }

  @Test
  public void testIsBroadcastMessage() {
    Message messsage = Message.makeBroadcastMessage(TEST_USER, "u");
    assertTrue(messsage.isBroadcastMessage());

  }

  @Test
  public void testIsNotBroadcastMessage() {
    Message messsage = Message.makeQuitMessage(TESTING);
    assertFalse(messsage.isBroadcastMessage());

  }

  @Test
  public void testNotBroadcastMessage() {
    edu.northeastern.ccs.im.Message messsage = edu.northeastern.ccs.im.Message.makeQuitMessage("q");
    assertFalse(messsage.isBroadcastMessage());

  }

  @Test
  public void testIsDisplayMessage() {
    Message messsage = Message.makeBroadcastMessage(TEST_USER, "u");
    assertTrue(messsage.isDisplayMessage());

  }

  @Test
  public void testIsNotDisplayMessage() {
    Message messsage = Message.makeQuitMessage(TEST_USER);
    assertFalse(messsage.isDisplayMessage());

  }

  @Test
  public void testIsInitializationMessage() {
    Message messsage = Message.makeLoginMessage("us");
    assertTrue(messsage.isInitialization());

  }

  @Test
  public void testIsNotInitializationMessage() {
    Message messsage = Message.makeQuitMessage(TEST_USER);
    assertFalse(messsage.isInitialization());

  }

  @Test
  public void testIsNotTerminationMessage() {
    Message messsage = Message.makeLoginMessage("us");
    assertFalse(messsage.terminate());

  }

  @Test
  public void testIsTerminationMessage() {
    Message messsage = Message.makeQuitMessage(TEST_USER);
    assertTrue(messsage.terminate());

  }

  @Test
  public void testToStringMessageSenderNull() {
    Message messsage = Message.makeLoginMessage(null);
    assertEquals("HLO 2 -- 2 --", messsage.toString());
  }


  @Test
  public void testMakeTypeBroadcastMessage() throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    Method makeMethod = Class.forName("edu.northeastern.ccs.im.Message")
        .getDeclaredMethod("makeMessage", String.class, String.class, String.class);
    makeMethod.setAccessible(true);
    makeMethod.invoke(null, MessageType.BROADCAST.toString(), "test1", "testText");
  }

  @Test
  public void testMakeTypeNoneMessage() throws NoSuchMethodException, ClassNotFoundException,
      IllegalAccessException, InvocationTargetException {
    Method makeMethod = Class.forName("edu.northeastern.ccs.im.Message")
        .getDeclaredMethod("makeMessage", String.class, String.class, String.class);
    makeMethod.setAccessible(true);
    assertNull(makeMethod.invoke(null, "zzz", "test1", "testText"));
  }


}
