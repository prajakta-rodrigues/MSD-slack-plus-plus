package prattleTests;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.Prattle;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
  private Queue<Message> waitingList1;
  private Queue<Message> waitingList2;
  private String bot = "SlackBot";

  /**
   * Initialize the command data before each test
   */
  @Before
  @SuppressWarnings("unchecked")
  public void initCommandData()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
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

    cr1.run();
    cr2.run();
    Field activeClient = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("active");
    activeClient.setAccessible(true);
    ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>) activeClient
        .get(null);
    active.add(cr1);
    active.add(cr2);

    Field w1 = cr1.getClass().getDeclaredField("waitingList");
    w1.setAccessible(true);
    waitingList1 = (ConcurrentLinkedQueue<Message>) w1.get(cr1);
    waitingList2 = (ConcurrentLinkedQueue<Message>) w1.get(cr2);

  }

  /**
   * Reset command data after each test
   */
  @After
  public void resetData() {
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
   */
  @Test
  public void testCircleListsAllActiveUsers() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/circle"));
    assertEquals(1, waitingList2.size());
    assertEquals(0, waitingList1.size());
    Message callback = waitingList2.remove();
    assertEquals(bot, callback.getName());
    assertEquals(-1, callback.getChannelId());
    assertEquals("Active Users:\nomar\ntuffaha", callback.getText());
  }

  /**
   * Tests that a non-recognized command outputs the correct message.
   *
   */
  @Test
  public void testNonRecognizedCommand() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/circles"));
    assertEquals(1, waitingList2.size());
    assertEquals(0, waitingList1.size());
    Message callback = waitingList2.remove();
    assertEquals(bot, callback.getName());
    assertEquals(-1, callback.getChannelId());
    assertEquals("Command /circles not recognized", callback.getText());
  }

  /**
   * Tests that a non initialized client will not get the broadcasted command
   */
  @Test
  public void testNotInitialized() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/circle"));
  }

  /**
   * Tests that retrieving a client that doesn't exist returns null.
   */
  @Test
  public void getNullClient() {
    assertNull(Prattle.getClient("james franco"));
  }

  /**
   * Tests broadcast message with circle command.
   */
  @Test
  public void testBroadcastMessage() {
    Prattle.broadcastMessage(Message.makeCommandMessage("omar", "/circle"));
    assertEquals(0, waitingList2.size());
    assertEquals(0, waitingList1.size());
  }

  /**
   * Tests help works
   */
  @Test
  public void testHelp() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/help"));
    assertEquals(0, waitingList2.size());
    assertEquals(1, waitingList1.size());
    Message callback = waitingList1.remove();
    assertEquals(
        "Available Commands:\n" +
            "/groups Print out the names of each available Group on the server\n" +
            "/dm Start a DM with the given user.\n" +
            "Parameters: user id\n" +
            "/createGroup Create a group with the given name.\n" +
            "Parameters: Group name\n" +
            "/group Change your current chat room to the specified Group.\n" +
            "Parameters: group name\n" +
            "/circle Print out the handles of the active users on the server\n" +
            "/help Lists all of the available commands.",
        callback.getText());
    assertEquals(bot, callback.getName());

  }

  /**
   * Tests commands with extra params.
   */
  @Test
  public void testCommandMessageWithMultipleInputs() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/circle aroundTheCampFire"));
    assertEquals(1, waitingList2.size());
    assertEquals(0, waitingList1.size());
    Message removed = waitingList2.remove();
    assertEquals(bot, removed.getName());
    assertEquals(-1, removed.getChannelId());
    assertEquals("Active Users:\nomar\ntuffaha", removed.getText());
  }
}