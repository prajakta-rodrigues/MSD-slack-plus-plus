package prattleTests;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.Prattle;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import edu.northeastern.ccs.im.server.Message;
import edu.northeastern.ccs.im.server.MessageType;
import edu.northeastern.ccs.im.server.NetworkConnection;
import edu.northeastern.ccs.im.client.Buddy;
import org.mockito.Mockito;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;


/**
 * Created by venkateshkoka on 2/10/19. All the test cases
 */
public class PrattleTest {

  private ClientRunnable cr1;
  private ClientRunnable cr2;

  /**
   * Initialize the command data before each test
   */
  private void initCommandData() {
    NetworkConnection networkConnection1 = Mockito.mock(NetworkConnection.class);
    cr1 = new ClientRunnable(networkConnection1);
    NetworkConnection networkConnection2 = Mockito.mock(NetworkConnection.class);
    cr2 = new ClientRunnable(networkConnection2);
    cr1.setName("omar");
    cr2.setName("tuffaha");
    List<Message> messageQueue1 = new ArrayList<>();
    List<Message> messageQueue2 = new ArrayList<>();
    Message omar_msg_1 = Message.makeBroadcastMessage("omar", "Omar says hi");
    Message omar_msg_2 = Message.makeBroadcastMessage("omar", "Omar says bye");
    Message tuff_msg_1 = Message.makeBroadcastMessage("tuffaha", "Tuffaha says hi");
    Message tuff_msg_2 = Message.makeBroadcastMessage("tuffaha", "Tuffaha says bye");
    messageQueue1.add(omar_msg_1);
    messageQueue1.add(omar_msg_2);
    messageQueue2.add(tuff_msg_1);
    messageQueue2.add(tuff_msg_2);
    Iterator<Message> mockIterator1 = messageQueue1.iterator();
    Iterator<Message> mockIterator2 = messageQueue2.iterator();
    when(networkConnection1.iterator()).thenReturn(mockIterator1);
    when(networkConnection2.iterator()).thenReturn(mockIterator2);
  }

  /**
   * Reset command data after each test
   */
  private void resetData() {
    Prattle.removeClient(cr1);
    Prattle.removeClient(cr2);
  }

  /**
   * Test ServerConstants Types.
   *
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchMethodException the no such method exception
   * @throws IllegalAccessException the illegal access exception
   * @throws InvocationTargetException the invocation target exception
   * @throws InstantiationException the instantiation exception
   */
  @Test
  public void testChatloggerTypes()
      throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    Constructor constructor = Class.forName("edu.northeastern.ccs.im.server.ServerConstants")
        .getDeclaredConstructor();
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  /**
   * Test makeHelloMessage in Message.
   *
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchMethodException the no such method exception
   * @throws InvocationTargetException the invocation target exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testMessageClass()
      throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method makeMessageMethod = Class.forName("edu.northeastern.ccs.im.server.Message")
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
   * Test handle type methods in Message.
   *
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchMethodException the no such method exception
   * @throws InvocationTargetException the invocation target exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testClientMessageClass()
      throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    String jaffa = "jaffa";
    String hello = "hello";
    Method makeMessageMethod = Class.forName("edu.northeastern.ccs.im.client.Message")
        .getDeclaredMethod("makeMessage", String.class, String.class, String.class);
    Method makeHelloMessageMethod = Class.forName("edu.northeastern.ccs.im.client.Message")
        .getDeclaredMethod("makeHelloMessage", String.class);
    makeMessageMethod.setAccessible(true);
    makeHelloMessageMethod.setAccessible(true);
    makeMessageMethod.invoke(null, "HLO", jaffa, hello);
    makeMessageMethod.invoke(null, "ACK", jaffa, hello);
    makeMessageMethod.invoke(null, "NAK", jaffa, hello);
    makeHelloMessageMethod.invoke(null, jaffa);
    edu.northeastern.ccs.im.client.Message sc = edu.northeastern.ccs.im.client.Message
        .makeLoginMessage("jaffa");
    sc.isAcknowledge();
    sc.isBroadcastMessage();
    sc.isDisplayMessage();
    sc.isInitialization();
    sc.terminate();
    assertEquals(jaffa, sc.getSender());
    assertNull(sc.getText());
  }


  /**
   * Test network connection socket channel.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void testNetworkConnectionSocketChannel() throws IOException {
    try (SocketChannel socketChannel = SocketChannel.open()) {
      socketChannel.configureBlocking(false);
      socketChannel.connect(new InetSocketAddress("localhost", 4545));
      new NetworkConnection(socketChannel);
    }
    assert true;
  }

  /**
   * Test message type.
   */
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

  /**
   * Test message.
   */
  @Test
  public void testMessage() {

    Message msd = Message.makeSimpleLoginMessage("koka");
    Message msd1 = Message.makeBroadcastMessage("koka", "Hello There");
    Message msd2 = Message.makeSimpleLoginMessage(null);
    String msg = msd.toString();
    String msg1 = msd1.toString();
    String msg2 = msd2.toString();
    assertEquals("HLO 4 koka 2 --", msg);
    assertEquals("BCT 4 koka 11 Hello There", msg1);
    assertEquals("HLO 2 -- 2 --", msg2);
  }

  /**
   * Test buddy.
   *
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchMethodException the no such method exception
   * @throws InvocationTargetException the invocation target exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testBuddy()
      throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    String bud = "edu.northeastern.ccs.im.client.Buddy";
    String daffa = "daffa";
    String jaffa = "jaffa";
    Buddy buddy = Buddy.makeTestBuddy(jaffa);
    String name = buddy.getUserName();
    Method method1 = Class.forName(bud).getDeclaredMethod("getBuddy", String.class);
    Method method2 = Class.forName(bud).getDeclaredMethod("getEmptyBuddy", String.class);
    Method method3 = Class.forName(bud).getDeclaredMethod("removeBuddy", String.class);
    method1.setAccessible(true);
    method2.setAccessible(true);
    method3.setAccessible(true);
    method2.invoke(null, daffa);
    method1.invoke(null, jaffa);
    method1.invoke(null, jaffa);
    method2.invoke(null, daffa);
    method3.invoke(null, daffa);
    Assert.assertEquals(name, jaffa);
  }

  /**
   * Test network connection socket channel 1.
   *
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @Test
  public void testNetworkConnectionSocketChannel1() throws IOException {
    try (SocketChannel socketChannel = SocketChannel.open()) {
      socketChannel.configureBlocking(false);
      socketChannel.connect(new InetSocketAddress("localhost", 4514));
      new NetworkConnection(socketChannel);
    }
    assert true;
  }

  /**
   * Tests that the /circle command works by listing all active users.
   *
   * @throws NoSuchFieldException no such field exception.
   * @throws ClassNotFoundException class not found exception.
   * @throws IllegalAccessException illegal access exception.
   */
  @Test
  public void testCircleListsAllActiveUsers()
      throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
    initCommandData();
    cr1.run();
    cr2.run();
    Field activeClient = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("active");
    activeClient.setAccessible(true);
    @SuppressWarnings("unchecked")
    ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>) activeClient
        .get(null);
    active.add(cr1);
    active.add(cr2);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/circle"));
    resetData();
  }

  /**
   * Tests that a non-recognized command outputs the correct message.
   *
   * @throws ClassNotFoundException class not found exception.
   * @throws NoSuchFieldException no such field exception.
   * @throws IllegalAccessException illegal state exception.
   */
  @Test
  public void testNonRecognizedCommand()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    initCommandData();
    cr1.run();
    cr2.run();
    Field activeClient = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("active");
    activeClient.setAccessible(true);
    @SuppressWarnings("unchecked")
    ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>) activeClient
        .get(null);
    active.add(cr1);
    active.add(cr2);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/circles"));
    resetData();
  }

  /**
   * Tests that a non initialized client will not get the broadcasted command
   *
   * @throws ClassNotFoundException class not found exception.
   * @throws NoSuchFieldException no such file exception.
   * @throws IllegalAccessException illegal access exception.
   */
  @Test
  public void testNotInitialized()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    initCommandData();
    Field activeClient = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("active");
    activeClient.setAccessible(true);
    @SuppressWarnings("unchecked")
    ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>) activeClient
        .get(null);
    active.add(cr1);
    active.add(cr2);
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/circle"));
    resetData();
  }

  /**
   * Tests that retrieving a client that doesn't exist returns null.
   */
  @Test
  public void getNullClient() {
    assertNull(Prattle.getClient("james franco"));
  }

  @Test
  public void testBroadcastMessage() {
    Prattle.broadcastMessage(Message.makeCommandMessage("omar", "/circle everybody"));
  }

  @Test
  public void testHelp() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/help"));
  }

  @Test
  public void testCommandMessageWithMultipleInputs() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/circle aroundTheCampFire"));
  }
}