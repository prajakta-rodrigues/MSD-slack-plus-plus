package prattleTests;

import edu.northeastern.ccs.im.server.ChatLogger;
import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.Prattle;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import edu.northeastern.ccs.im.server.Message;
import edu.northeastern.ccs.im.server.MessageType;
import edu.northeastern.ccs.im.server.NetworkConnection;
import edu.northeastern.ccs.im.client.Buddy;
import edu.northeastern.ccs.im.server.SlackGroup;

import org.mockito.Mockito;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
  public void initCommandData() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    NetworkConnection networkConnection1 = Mockito.mock(NetworkConnection.class);
    cr1 = new ClientRunnable(networkConnection1) {
    	@Override()
    	protected void checkForInitialization() {

    	}
    };
    NetworkConnection networkConnection2 = Mockito.mock(NetworkConnection.class);
    cr2 = new ClientRunnable(networkConnection2){
    	@Override()
    	protected void checkForInitialization() {

    	}
    };
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
    Field authenticate = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable").getDeclaredField("authenticated");
    authenticate.setAccessible(true);
    authenticate.set(cr1, true);
    authenticate.set(cr2, true);

    Field initialized = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable").getDeclaredField("initialized");
    initialized.setAccessible(true);
    initialized.set(cr1, true);
    initialized.set(cr2, true);

    ConcurrentLinkedQueue<ClientRunnable> active = (ConcurrentLinkedQueue<ClientRunnable>) activeClient
            .get(null);
    active.add(cr1);
    active.add(cr2);

    Field wl = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable").getDeclaredField("waitingList");
    wl.setAccessible(true);
    waitingList1 = (ConcurrentLinkedQueue<Message>)wl.get(cr1);
    waitingList2 = (ConcurrentLinkedQueue<Message>)wl.get(cr2);
  }

  /**
   * Reset command data after each test
   */
  @After
  @SuppressWarnings("unchecked")
  public void resetData() throws NoSuchFieldException, IllegalAccessException {
    Prattle.removeClient(cr1);
    Prattle.removeClient(cr2);
    Field groups = Prattle.class.getDeclaredField("groups");
    groups.setAccessible(true);
    ConcurrentLinkedQueue<SlackGroup> g = (ConcurrentLinkedQueue<SlackGroup>) groups
            .get(null);
    SlackGroup general = g.remove();
    while(!g.isEmpty()) {
      g.remove();
    }
    g.add(general);
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
    ChatLogger.warning("testing");
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

  @Test
  public void testStopServer() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	  Field isReady = Class.forName("edu.northeastern.ccs.im.server.Prattle").getDeclaredField("isReady");
	  isReady.setAccessible(true);
	  isReady.set(null, true);
	  Prattle.stopServer();
	  assertEquals(false, (boolean)isReady.get(null));
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
    Prattle.commandMessage(Message.makeCommandMessage("mike", "/circle"));
    assertEquals(0, waitingList1.size());
    assertEquals(0, waitingList2.size());
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
    assertTrue(callback.getText().contains("/groups Print out the names of each available Group on the server"));
    assertTrue(callback.getText().contains("/creategroup Create a group with the given name.\n" +
            "Parameters: Group name"));
    assertTrue(callback.getText().contains("/group Change your current chat room to the specified Group.\n" +
            "Parameters: group name"));
    assertTrue(callback.getText().contains("/circle Print out the handles of the active users on the server"));
    assertTrue(callback.getText().contains("/help Lists all of the available commands."));
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
    assertEquals(0, waitingList2.size());
    assertEquals(-1, removed.getChannelId());
    assertEquals("Active Users:\nomar\ntuffaha", removed.getText());
  }

  /**
   * Tests that users online get updated after leaving
   */
  @Test
  public void testNoneOnline() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/circle"));
    assertEquals(1, waitingList2.size());
    Message callback = waitingList2.remove();
    assertEquals(0, waitingList2.size());
    assertEquals(0, waitingList1.size());
    assertEquals("Active Users:\nomar\ntuffaha", callback.getText());
    Prattle.removeClient(cr1);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/circle"));
    assertEquals(1, waitingList2.size());
    Message removed = waitingList2.remove();
    assertEquals(0, waitingList2.size());
    assertEquals(0, waitingList1.size());
    assertEquals("Active Users:\ntuffaha", removed.getText());
  }

  @Test
  public void testMakeBroadcastMessageInheritsChannelId() {
    Message msg = Message.makeMessage("BCT", "omar", "hello world");
    assertTrue(msg.isBroadcastMessage());
    assertEquals(msg.getChannelId(), cr1.getActiveChannelId());
  }

  @Test
  public void testMakeBroadcastMessageDefaultChannelId() {
    Message msg = Message.makeMessage("BCT", "sean", "hello world");
    assertTrue(msg.isBroadcastMessage());
    assertEquals(msg.getChannelId(), -1);
  }

  @Test
  public void testCreateGroup() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/createGroup myGroup"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/groups"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("myGroup"));
    assertEquals(bot, callback.getName());
    assertTrue(callback.getText().contains("general"));
  }

  @Test
  public void testDm() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/dm tuffaha"));
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/groups"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("DM:omar-tuffaha"));
  }

  @Test
  public void testDmUserNotActive() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/dm jacobe"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/groups"));
    Message callback = waitingList2.remove();
    assertTrue(callback.getText().contains("The provided user is not active"));
  }

  @Test
  public void testDmWithoutUser() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/dm"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/groups"));
    Message callback = waitingList1.remove();
    Message callback2 = waitingList2.remove();
    assertTrue(callback2.getText().contains("No user provided to direct message."));
    assertEquals(bot, callback.getName());
    assertTrue(callback.getText().contains("general"));
  }

  @Test
  public void testDmWithoutUser2() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/dm  "));
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/groups"));
    Message callback = waitingList1.remove();
    Message callback2 = waitingList2.remove();
    assertTrue(callback2.getText().contains("No user provided to direct message."));
    assertEquals(bot, callback.getName());
    assertTrue(callback.getText().contains("general"));
  }
  @Test
  public void testDMTaken() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/dm omar"));
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/dm omar"));
    Message callback = waitingList2.remove();
    assertTrue(callback.getText().contains("DM:tuffaha-omar create"));
    callback = waitingList2.remove();
    assertTrue(callback.getText().contains("Group name already taken"));
    assertEquals(bot, callback.getName());
  }


  @Test
  public void testDmChannelAccessibility() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/dm tuffaha"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/group DM:tuffaha-tuffaha"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("You are not authorized to use this DM"));
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/group DM:tuffaha-tuffaha"));
    callback = waitingList2.remove();
    assertTrue(callback.getText().contains("DM:tuffaha-tuffaha created"));
    assertEquals(bot, callback.getName());
  }

  @Test
  public void testCreateGroupSpecialCharacters() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/createGroup !@578"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/groups"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("!@578"));
    assertTrue(callback.getText().contains("general"));
  }

  @Test
  public void testCreateGroupSpaces() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/createGroup My Group"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/groups"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("My"));
    assertFalse(callback.getText().contains("Group"));
    assertTrue(callback.getText().contains("general"));
  }

  @Test
  public void testCreateGroupNoInput() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/createGroup"));
    Message callback = waitingList2.remove();
    assertTrue(callback.getText().contains("No Group Name provided"));
    assertEquals(bot, callback.getName());
  }

  @Test
  public void testCreateGroupNameTaken() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/createGroup general"));
    Message callback = waitingList2.remove();
    assertTrue(callback.getText().contains("Group name already taken"));
    assertEquals(bot, callback.getName());
  }

  @Test
  public void testMakeCommandMessage() {
    Message msg = Message.makeMessage("CMD", "omar", "/hello");
    assertTrue(msg.isCommandMessage());
  }

  @Test
  public void testShowGroupsDefault() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/groups"));
    assertEquals(0, waitingList2.size());
    Message callback = waitingList1.remove();
    assertEquals(callback.getName(), bot);
    assertTrue(callback.getText().contains("general"));
  }

  @Test
  public void testShowMultipleGroups() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/createGroup omarGroup"));
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/createGroup TSeries"));
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/createGroup coolbeans"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/groups"));
    waitingList1.remove();
    Message callback = waitingList1.remove();
    assertEquals(bot, callback.getName());
    String text = callback.getText();
    assertTrue(text.contains("omarGroup"));
    assertTrue(text.contains("TSeries"));
    assertTrue(text.contains("coolbeans"));
    assertTrue(text.contains("general"));
  }

  @Test
  public void testChangeGroup() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/createGroup o"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/group o"));
    Message callback = waitingList1.remove();
    assertEquals("Active channel set to Group o", callback.getText());
  }

  @Test
  public void testChangeGroupNoInput() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/group"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("No Group Name provided"));
  }

  @Test
  public void testChangeGroupNotFound() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/group a"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("Group a does not exist"));
  }

  @Test
  public void testClientIdDoesntMatch() {
    Prattle.commandMessage(Message.makeCommandMessage("nonexistent", "/group general"));
    //Message callback = waitingList1.remove();
    //assertTrue(callback.getText().contains("Group a does not exist"));
  }

  @Test
  public void testMessagesReceivedGeneral() {
    Prattle.broadcastMessage(Message.makeMessage("BCT","omar", "Hey T"));
    Message callback = waitingList2.remove();
    assertEquals("omar", callback.getName());
    assertEquals("Hey T", callback.getText());
  }

  @Test
  public void testMessagesReceivedSameGroup() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/createGroup grp"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/group grp"));
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", "/group grp"));
    Prattle.broadcastMessage(Message.makeMessage("BCT","omar", "Hey T"));
    waitingList2.remove();
    Message callback = waitingList2.remove();
    assertEquals("omar", callback.getName());
    assertEquals("Hey T", callback.getText());
  }

  @Test
  public void testMessagesNotReceivedDifferentGroup() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/createGroup ogrp"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", "/group ogrp"));
    Prattle.broadcastMessage(Message.makeMessage("BCT","omar", "Hey T"));
    assertTrue(waitingList2.isEmpty());
  }

//  @SuppressWarnings("unchecked")
//  @Test
//  public void testModerator()
//      throws NoSuchFieldException, IllegalAccessException {
//    Prattle.commandMessage(Message.makeCommandMessage("omar", "/createGroup group1"));
//    Field groups = Prattle.class.getDeclaredField("groups");
//    groups.setAccessible(true);
//    ConcurrentLinkedQueue<SlackGroup> g = (ConcurrentLinkedQueue<SlackGroup>) groups
//        .get(null);
//    SlackGroup group = g.remove();
//    Message callback = waitingList1.peek();
//    System.out.println(group.getGroupName() + " " + callback);
//    while (group != null) {
//      if (group.getGroupName().equals("group1")) {
//        List<String> mods = group.getModerators();
//        String moderator1 = mods.remove(0);
//        assertEquals("omar", moderator1);
//        g.add(group);
//        break;
//      } else {
//        group = g.remove();
//      }
//    }
//  }

}