package prattleTests;


import edu.northeastern.ccs.im.server.utility.LanguageSupport;
import org.junit.After;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.northeastern.ccs.im.client.Buddy;
import edu.northeastern.ccs.im.server.ChatLogger;
import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.Message;
import edu.northeastern.ccs.im.server.MessageType;
import edu.northeastern.ccs.im.server.NetworkConnection;
import edu.northeastern.ccs.im.server.Notification;
import edu.northeastern.ccs.im.server.NotificationType;
import edu.northeastern.ccs.im.server.Prattle;
import edu.northeastern.ccs.im.server.SlackGroup;
import edu.northeastern.ccs.im.server.User;
import edu.northeastern.ccs.im.server.repositories.DirectMessageRepository;
import edu.northeastern.ccs.im.server.repositories.GroupRepository;
import edu.northeastern.ccs.im.server.repositories.NotificationRepository;
import edu.northeastern.ccs.im.server.repositories.UserRepository;

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
  private String bot = "Slackbot";
  private DirectMessageRepository dmRepository;
  private UserRepository userRepository;

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
    Field authenticatedClient = Class.forName("edu.northeastern.ccs.im.server.Prattle")
            .getDeclaredField("authenticated");
    authenticatedClient.setAccessible(true);

    Field cm = Class.forName("edu.northeastern.ccs.im.server.Prattle")
            .getDeclaredField("channelMembers");
    cm.setAccessible(true);

    Field authenticate = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
            .getDeclaredField("authenticated");
    authenticate.setAccessible(true);
    authenticate.set(cr1, true);
    authenticate.set(cr2, true);
    Field initialized = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
            .getDeclaredField("initialized");
    initialized.setAccessible(true);
    initialized.set(cr1, true);
    initialized.set(cr2, true);
    Field userId = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
            .getDeclaredField("userId");
    userId.setAccessible(true);
    userId.set(cr1, 2);
    userId.set(cr2, 1);

    Map<Integer, ClientRunnable> authenticated = (Hashtable<Integer, ClientRunnable>) authenticatedClient
            .get(null);
    authenticated.put(2, cr1);
    authenticated.put(1, cr2);

    Map<Integer, Set<ClientRunnable>> channelMembers = (Hashtable<Integer, Set<ClientRunnable>>) cm
            .get(null);
    channelMembers.get(1).add(cr1);
    channelMembers.get(1).add(cr2);

    Field wl = Class.forName("edu.northeastern.ccs.im.server.ClientRunnable")
            .getDeclaredField("waitingList");
    wl.setAccessible(true);
    waitingList1 = (ConcurrentLinkedQueue<Message>)wl.get(cr1);
    waitingList2 = (ConcurrentLinkedQueue<Message>)wl.get(cr2);

    GroupRepository groupRepository = new MockGroupRepository();

    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
            .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);

    dmRepository = Mockito.mock(DirectMessageRepository.class);
    userRepository = Mockito.mock(UserRepository.class);

    User omar = new User(2, "omar", "password");

    Field ur = Class.forName("edu.northeastern.ccs.im.server.Prattle")
            .getDeclaredField("userRepository");
    ur.setAccessible(true);
    ur.set(null, userRepository);

    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(omar);

    Field dmr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
            .getDeclaredField("dmRepository");
    dmr.setAccessible(true);
    dmr.set(null, dmRepository);

    Mockito.when(dmRepository.createDM(Mockito.anyInt(), Mockito.anyInt())).thenReturn(5);
    Mockito.when(dmRepository.getDMChannel(Mockito.anyInt(), Mockito.anyInt())).thenReturn(10);
  }

  /** Mock Group Repository class for testing purposes. */
  private class MockGroupRepository extends GroupRepository {
    Map<String, SlackGroup> mockDb;

    MockGroupRepository() {
      mockDb = new HashMap<>();

      mockDb.put("general", new SlackGroup(1, -1, "general", 1));
    }

    @Override
    public boolean addGroup(SlackGroup toAdd) {
      if (mockDb.containsKey(toAdd.getGroupName())) {
        return false;
      } else {
        mockDb.put(toAdd.getGroupName(), toAdd);
        return true;
      }
    }

    @Override
    public SlackGroup getGroupById(int id) {
      SlackGroup ans = null;
      for (SlackGroup group : mockDb.values()) {
        ans = group.getGroupId() == id ? group : ans;
      }
      return ans;
    }

    @Override
    public SlackGroup getGroupByName(String groupName) {
      return mockDb.getOrDefault(groupName, null);
    }

    @Override
    public boolean groupHasMember(int id, int groupId) {
      return true;
    }

    @Override
    public String groupsHavingMember(int id) {
      StringBuilder ans = new StringBuilder();
      for (SlackGroup group : mockDb.values()) {
        ans.append(group.getGroupName());
        ans.append("\n");
      }
      return ans.toString();
    }

  }

  @AfterClass
  public static void terminate() {
    Prattle.stopServer();
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
            .getDeclaredMethod("makeMessage", String.class, String.class, int.class, String.class);
    Method makeHelloMessageMethod = Class.forName("edu.northeastern.ccs.im.client.Message")
            .getDeclaredMethod("makeHelloMessage", String.class);
    makeMessageMethod.setAccessible(true);
    makeHelloMessageMethod.setAccessible(true);
    makeMessageMethod.invoke(null, "HLO", jaffa, -1, hello);
    makeMessageMethod.invoke(null, "ACK", jaffa, -1, hello);
    makeMessageMethod.invoke(null, "NAK", jaffa, -1, hello);
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
    assertEquals("HLO 4 koka 2 -1 2 --", msg);
    assertEquals("BCT 4 koka 2 -1 11 Hello There", msg1);
    assertEquals("HLO 2 -- 2 -1 2 --", msg2);
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
    assertFalse((boolean) isReady.get(null));
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
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/circle"));
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
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1,  "/circles"));
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
    Prattle.commandMessage(Message.makeCommandMessage("mike", -1,"/circle"));
    assertEquals(0, waitingList1.size());
    assertEquals(0, waitingList2.size());
  }

  /**
   * Tests that retrieving a client that doesn't exist returns null.
   */
  @Test
  public void getNullClient() {
    assertNull(Prattle.getClient(-10));
  }

  /**
   * Tests help works
   */
  @Test
  public void testHelp() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/help"));
    assertEquals(0, waitingList2.size());
    assertEquals(1, waitingList1.size());
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("/groups Print out the names of each Group you are a member of\n"));
    assertEquals(bot, callback.getName());
  }



  /**
   * Tests commands with extra params.
   */
  @Test
  public void testCommandMessageWithMultipleInputs() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1,  "/circle aroundTheCampFire"));
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
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/circle"));
    assertEquals(1, waitingList2.size());
    Message callback = waitingList2.remove();
    assertEquals(0, waitingList2.size());
    assertEquals(0, waitingList1.size());
    assertEquals("Active Users:\nomar\ntuffaha", callback.getText());
    Prattle.removeClient(cr1);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/circle"));
    assertEquals(1, waitingList2.size());
    Message removed = waitingList2.remove();
    assertEquals(0, waitingList2.size());
    assertEquals(0, waitingList1.size());
    assertEquals("Active Users:\ntuffaha", removed.getText());
  }

  @Test
  public void testMakeBroadcastMessageInheritsChannelId() {
    Message msg = Message.makeMessage("BCT", "omar", 2, "hello world");
    assertTrue(msg.isBroadcastMessage());
    assertEquals(msg.getChannelId(), cr1.getActiveChannelId());
  }

  @Test
  public void testMakeBroadcastMessageDefaultChannelId() {
    Message msg = Message.makeMessage("BCT", "sean", 3,"hello world");
    assertTrue(msg.isBroadcastMessage());
    assertEquals(msg.getChannelId(), -1);
  }

  @Test
  public void testCreateGroup() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1,  "/createGroup myGroup"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/groups"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("myGroup"));
    assertEquals(bot, callback.getName());
    assertTrue(callback.getText().contains("general"));
  }

  @Test
  public void testCreateGroupSpecialCharacters() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup !@578"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/groups"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("!@578"));
    assertTrue(callback.getText().contains("general"));
  }

  @Test
  public void testCreateGroupSpaces() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup My Group"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/groups"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("My"));
    assertFalse(callback.getText().contains("Group"));
    assertTrue(callback.getText().contains("general"));
  }

  @Test
  public void testCreateGroupNoInput() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup"));
    Message callback = waitingList2.remove();
    assertTrue(callback.getText().contains("No Group Name provided"));
    assertEquals(bot, callback.getName());
  }

  @Test
  public void testCreateGroupNameTaken() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup general"));
    Message callback = waitingList2.remove();
    assertTrue(callback.getText().contains("A group with this name already exists"));
    assertEquals(bot, callback.getName());
  }

  @Test
  public void testMakeCommandMessage() {
    Message msg = Message.makeMessage("CMD", "omar", 2, "/hello");
    assertTrue(msg.isCommandMessage());
  }

  @Test
  public void testShowGroupsDefault() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/groups"));
    assertEquals(0, waitingList2.size());
    Message callback = waitingList1.remove();
    assertEquals(callback.getName(), bot);
    assertTrue(callback.getText().contains("general"));
  }

  @Test
  public void testShowMultipleGroups() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/createGroup omarGroup"));
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup TSeries"));
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup coolbeans"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/groups"));
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
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup o"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group o"));
    Message callback = waitingList1.remove();
    assertEquals("Active channel set to Group o", callback.getText());
  }

  @Test
  public void testChangeGroupNoInput() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("No Group Name provided"));
  }

  @Test
  public void testChangeGroupNotFound() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group a"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("Group a does not exist"));
  }

  @Test
  public void testClientIdDoesntMatch() {
    Prattle.commandMessage(Message.makeCommandMessage("nonexistent", -1,"/group general"));
    //Message callback = waitingList1.remove();
    //assertTrue(callback.getText().contains("Group a does not exist"));
  }

  @Test
  public void testMessagesReceivedGeneral() {
    Prattle.broadcastMessage(Message.makeMessage("BCT","omar", 2, "Hey T"));
    Message callback = waitingList2.remove();
    assertEquals("omar",  callback.getName());
    assertEquals("Hey T", callback.getText());
  }

  @Test
  public void testMessagesReceivedSameGroup() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/createGroup grp"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group grp"));
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/group grp"));
    Prattle.broadcastMessage(Message.makeMessage("BCT","omar", 2, "Hey T"));
    waitingList2.remove();
    Message callback = waitingList2.remove();
    assertEquals("omar", callback.getName());
    assertEquals("Hey T", callback.getText());
  }

  @Test
  public void testMessagesNotReceivedDifferentGroup() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/createGroup ogrp"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group ogrp"));
    Prattle.broadcastMessage(Message.makeMessage("BCT","omar", 2, "Hey T"));
    assertTrue(waitingList2.isEmpty());
  }
  
  @Test
  public void testNotificationCommandNullList() throws NoSuchFieldException, SecurityException, 
  ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    Field notificationRepoField = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("notificationRepository");
    notificationRepoField.setAccessible(true);
    NotificationRepository notificationRepo = Mockito.mock(NotificationRepository.class);
    notificationRepoField.set(null, notificationRepo);
    Mockito.when(notificationRepo.getAllNotificationsByReceiverId(1)).thenReturn(null);
    assertTrue(waitingList2.isEmpty());
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/notification"));
    Message callback = waitingList2.remove();
    assertEquals("No notifications to show" , callback.getText());

  }
  
  @Test
  public void testNotificationCommandEmptyList() throws NoSuchFieldException, SecurityException, 
  ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    Field notificationRepoField = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("notificationRepository");
    notificationRepoField.setAccessible(true);
    NotificationRepository notificationRepo = Mockito.mock(NotificationRepository.class);
    notificationRepoField.set(null, notificationRepo);
    List<Notification> list = new ArrayList<>();
    Mockito.when(notificationRepo.getAllNotificationsByReceiverId(1)).thenReturn(list );
    assertTrue(waitingList2.isEmpty());
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/notification"));
    Message callback = waitingList2.remove();
    assertEquals("No notifications to show" , callback.getText());

  }
  
  @Test
  public void testNotificationCommandNotificationList() throws NoSuchFieldException, SecurityException, 
  ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    Field notificationRepoField = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("notificationRepository");
    notificationRepoField.setAccessible(true);
    NotificationRepository notificationRepo = Mockito.mock(NotificationRepository.class);
    notificationRepoField.set(null, notificationRepo);
    
    Field userRepoField = Class.forName("edu.northeastern.ccs.im.server.NotificationConvertor")
        .getDeclaredField("userRepository");
    userRepoField.setAccessible(true);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    userRepoField.set(null, userRepository);
    User user = new User(2, "testY", "pwd");
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);
    
    List<Notification> list = new ArrayList<>();
    Notification e = new Notification();
    e.setId(1);
    e.setAssociatedGroupId(1);
    e.setAssociatedUserId(2);
    e.setNew(true);
    e.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
    e.setRecieverId(1);
    e.setType(NotificationType.FRIEND_REQUEST);
    list.add(e);
    Mockito.when(notificationRepo.getAllNotificationsByReceiverId(1)).thenReturn(list );
    assertTrue(waitingList2.isEmpty());
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/notification"));
    Message callback = waitingList2.remove();
    assertEquals("Notifications:\n" + 
        "testY has sent you a friend request.  NEW\n", callback.getText());

  }
  @Test
  public void testMustBeMemberToSetGroup() throws IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
    GroupRepository groupRepository = Mockito.mock(MockGroupRepository.class);

    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
            .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);

    Mockito.when(groupRepository.groupHasMember(Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(false);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(new SlackGroup(1, "special_group"));

    ((MockGroupRepository) groupRepository).mockDb = new HashMap<>();

    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group special_group"));
    Message callback = waitingList1.remove();
    assertEquals("You are not a member of this group", callback.getText());
  }

  @Test
  public void testDMUserNotFound() {
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(null);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/dm koka"));
    Message callback = waitingList2.remove();
    assertEquals("User koka not found!", callback.getText());
    assertEquals(1, cr2.getActiveChannelId());
  }

  @Test
  public void testDMExistingDM() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/dm omar"));
    Message callback = waitingList2.remove();
    assertEquals("You are now messaging omar", callback.getText());
    assertEquals(10, cr2.getActiveChannelId());
  }

  @Test
  public void testDMnewDM() {
    Mockito.when(dmRepository.getDMChannel(Mockito.anyInt(), Mockito.anyInt())).thenReturn(-1);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/dm omar"));
    Message callback = waitingList2.remove();
    assertEquals("You are now messaging omar", callback.getText());
    assertEquals(5, cr2.getActiveChannelId());
  }

  @Test
  public void testDMqueryFail() {
    Mockito.when(dmRepository.getDMChannel(Mockito.anyInt(), Mockito.anyInt())).thenReturn(-1);
    Mockito.when(dmRepository.createDM(Mockito.anyInt(), Mockito.anyInt())).thenReturn(-1);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/dm omar"));
    Message callback = waitingList2.remove();
    assertEquals("Failed to create direct message. Try again later.", callback.getText());
    assertEquals(1, cr2.getActiveChannelId());
  }

  @Test
  public void testDMBetweenUsers() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/dm omar"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/dm tuffaha"));
    assertEquals(10, cr1.getActiveChannelId());
    assertEquals(10, cr2.getActiveChannelId());
  }
}