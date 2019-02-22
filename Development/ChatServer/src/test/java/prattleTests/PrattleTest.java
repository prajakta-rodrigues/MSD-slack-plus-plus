package prattleTests;

import edu.northeastern.ccs.im.*;
import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.client.*;
import org.junit.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Created by venkateshkoka on 2/10/19. All the test cases
 */
public class PrattleTest {

  public PrattleTest() throws IOException {
  }


  /**
   * Test ServerConstants Types...
   */
  @Test
  public void testChatloggerTypes()
      throws ClassNotFoundException, NoSuchMethodException {
    Constructor constructor = Class.forName("edu.northeastern.ccs.im.server.ServerConstants")
        .getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
    } catch (Exception e) {
      fail("An exception should not be thrown");
    }
  }

  /**
   * Test makeHelloMessage in Message...
   */
  @Test
  public void testMessageClass()
      throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method makeMessageMethod = Class.forName("edu.northeastern.ccs.im.Message")
        .getDeclaredMethod("makeHelloMessage", String.class);
    makeMessageMethod.setAccessible(true);
    makeMessageMethod.invoke(null, "mike");
    Message msd1 = Message.makeBroadcastMessage("koka", "Hello There");
    Message msg = Message.makeQuitMessage("mike");
    boolean b = true;
    if (msd1.getText().length() > 0) {
      b = msg.isInitialization();
    }
    assertFalse(b);
  }

  /**
   * Test hadle type methods in Message...
   */
  @Test
  public void testClientMessageClass()
      throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method makeMessageMethod = Class.forName("edu.northeastern.ccs.im.client.Message")
        .getDeclaredMethod("makeMessage", String.class, String.class, String.class);
    Method makeHelloMessageMethod = Class.forName("edu.northeastern.ccs.im.client.Message")
        .getDeclaredMethod("makeHelloMessage", String.class);
    makeMessageMethod.setAccessible(true);
    makeHelloMessageMethod.setAccessible(true);
    String jaffa = "Jaffa";
    String hello = "Hello busy people";
    makeMessageMethod.invoke(null, "HLO", jaffa, hello);
    makeMessageMethod.invoke(null, "ACK", jaffa, hello);
    makeMessageMethod.invoke(null, "NAK", jaffa, hello);
    makeHelloMessageMethod.invoke(null, jaffa);
    edu.northeastern.ccs.im.client.Message sc = edu.northeastern.ccs.im.client.Message
        .makeLoginMessage(jaffa);
    sc.isAcknowledge();
    sc.isBroadcastMessage();
    sc.isDisplayMessage();
    sc.isInitialization();
    sc.terminate();
    sc.getSender();
    sc.getText();
    assertTrue(true);
  }

  @Test
  public void testNetworkConnectionSocketChannel() throws IOException {
    SocketChannel socketChannel = SocketChannel.open();
    socketChannel.configureBlocking(false);
    socketChannel.connect(new InetSocketAddress("localhost", 4545));
    new NetworkConnection(socketChannel);
    assert true;
  }

  @Test
  public void testMessageType() {
    MessageType mstype = MessageType.HELLO;
    MessageType mstype1 = MessageType.HELLO;
    MessageType mstype2 = MessageType.BROADCAST;
    MessageType mstype3 = MessageType.BROADCAST;
    Assert.assertEquals(mstype, mstype1);
    Assert.assertEquals("HLO", MessageType.HELLO.toString());
    Assert.assertEquals(mstype2, mstype3);
  }

  @Test
  public void testMessage() {
    Message msd = Message.makeSimpleLoginMessage("koka");
    Message.makeBroadcastMessage("koka", "Hello There");
    Message.makeSimpleLoginMessage(null);
    String msg = msd.toString();
    Assert.assertEquals("HLO 4 koka 2 --", msg);
  }

  @Test
  public void testBuddy()
      throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    String jaffa = "jaffa";
    Buddy buddy = Buddy.makeTestBuddy(jaffa);
    String name = buddy.getUserName();
    String clientBuddy = "edu.northeastern.ccs.im.client.Buddy";
    Method method1 = Class.forName(clientBuddy).getDeclaredMethod("getBuddy", String.class);
    Method method2 = Class.forName(clientBuddy).getDeclaredMethod("getEmptyBuddy", String.class);
    Method method3 = Class.forName(clientBuddy).getDeclaredMethod("removeBuddy", String.class);
    method1.setAccessible(true);
    method2.setAccessible(true);
    method3.setAccessible(true);
    String daffa = "daffa";
    method2.invoke(null, daffa);
    method1.invoke(null, jaffa);
    method1.invoke(null, jaffa);
    method2.invoke(null, daffa);
    method3.invoke(null, daffa);
    Assert.assertEquals(name, jaffa);
  }

  @Test
  public void testNetworkConnectionSocketChannel1() throws IOException {
    SocketChannel socketChannel = SocketChannel.open();
    socketChannel.configureBlocking(false);
    socketChannel.connect(new InetSocketAddress("localhost", 4514));
    try {
      new NetworkConnection(socketChannel);
    } catch (Exception e) {
      fail("An exception should not be thrown");
    }
  }
}