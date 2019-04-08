package prattleTests;

import com.google.cloud.ServiceOptions;
import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import edu.northeastern.ccs.im.server.repositories.FriendRepository;
import edu.northeastern.ccs.im.server.repositories.FriendRequestRepository;
import edu.northeastern.ccs.im.server.repositories.UserGroupRepository;

import edu.northeastern.ccs.im.server.utility.TranslationSupport;
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
import java.sql.SQLException;
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
import edu.northeastern.ccs.im.server.ErrorCodes;
import edu.northeastern.ccs.im.server.GroupInvitation;
import edu.northeastern.ccs.im.server.InviteesGroup;
import edu.northeastern.ccs.im.server.InvitorsGroup;
import edu.northeastern.ccs.im.server.Message;
import edu.northeastern.ccs.im.server.MessageType;
import edu.northeastern.ccs.im.server.NetworkConnection;
import edu.northeastern.ccs.im.server.Notification;
import edu.northeastern.ccs.im.server.NotificationType;
import edu.northeastern.ccs.im.server.Prattle;
import edu.northeastern.ccs.im.server.SlackGroup;
import edu.northeastern.ccs.im.server.User;
import edu.northeastern.ccs.im.server.repositories.DirectMessageRepository;
import edu.northeastern.ccs.im.server.repositories.GroupInviteRepository;
import edu.northeastern.ccs.im.server.repositories.GroupRepository;
import edu.northeastern.ccs.im.server.repositories.NotificationRepository;
import edu.northeastern.ccs.im.server.repositories.UserRepository;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
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
  private GroupRepository groupRepository;
  private GroupInviteRepository groupInviteRepository;
  private NotificationRepository notificationRepository;
  
  private FriendRequestRepository friendRequestRepository;
  private FriendRepository friendRepository;
  private UserGroupRepository userGroupRepository;
  private User omar;
  private User mark;

  /**
   * Initialize the command data before each test
   */
  @Before
  @SuppressWarnings("unchecked")
  public void initCommandData()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    NetworkConnection networkConnection1 = Mockito.mock(NetworkConnection.class);
    cr1 = new ClientRunnable(networkConnection1) {
      @Override()
      protected void checkForInitialization() {

      }
    };
    NetworkConnection networkConnection2 = Mockito.mock(NetworkConnection.class);
    cr2 = new ClientRunnable(networkConnection2) {
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
    waitingList1 = (ConcurrentLinkedQueue<Message>) wl.get(cr1);
    waitingList2 = (ConcurrentLinkedQueue<Message>) wl.get(cr2);

    GroupRepository groupRepository = new MockGroupRepository();

    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);

    dmRepository = Mockito.mock(DirectMessageRepository.class);
    userRepository = Mockito.mock(UserRepository.class);
    friendRequestRepository = Mockito.mock(FriendRequestRepository.class);
    userGroupRepository = Mockito.mock(UserGroupRepository.class);
    friendRepository = Mockito.mock(FriendRepository.class);

    omar = new User(1, "omar", "password");
    mark = new User(2, "mark", "password");

    Field ur = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("userRepository");
    ur.setAccessible(true);
    ur.set(null, userRepository);

    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(omar);

    Field dmr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("dmRepository");
    dmr.setAccessible(true);
    dmr.set(null, dmRepository);
    
    userGroupRepository = Mockito.mock(UserGroupRepository.class);
    
    Field userGroupRepo =
        Class.forName("edu.northeastern.ccs.im.server.Prattle").getDeclaredField("userGroupRepository");
    userGroupRepo.setAccessible(true);
    userGroupRepo.set(null, userGroupRepository);
    
    Field frr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("friendRequestRepository");
    frr.setAccessible(true);
    frr.set(null, friendRequestRepository);

    Field fr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("friendRepository");
    fr.setAccessible(true);
    fr.set(null, friendRepository);

    Field ugr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("userGroupRepository");
    ugr.setAccessible(true);
    ugr.set(null, userGroupRepository);

    Mockito.when(dmRepository.createDM(Mockito.anyInt(), Mockito.anyInt())).thenReturn(5);
    Mockito.when(dmRepository.getDMChannel(Mockito.anyInt(), Mockito.anyInt())).thenReturn(10);
    
    groupInviteRepository = Mockito.mock(GroupInviteRepository.class);
    Field fieldInviteRepo =
        Class.forName("edu.northeastern.ccs.im.server.Prattle").getDeclaredField("groupInviteRepository");
    fieldInviteRepo.setAccessible(true);
    fieldInviteRepo.set(null, groupInviteRepository);
    
    notificationRepository = Mockito.mock(NotificationRepository.class);
    Field fieldNotificationRepository =
        Class.forName("edu.northeastern.ccs.im.server.Prattle").getDeclaredField("notificationRepository");
    fieldNotificationRepository.setAccessible(true);
    fieldNotificationRepository.set(null, notificationRepository);
    
    
  }

  /**
   * Mock Group Repository class for testing purposes.
   */
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
  public void testStopServer()
      throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    Field isReady = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("isReady");
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
   */
  @Test
  public void testNonRecognizedCommand() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/circles"));
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
    Prattle.commandMessage(Message.makeCommandMessage("mike", -1, "/circle"));
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
    assertTrue(callback.getText()
        .contains("/groups Print out the names of each Group you are a member of\n"));
    assertEquals(bot, callback.getName());
  }

  /**
   * Tests commands with extra params.
   */
  @Test
  public void testCommandMessageWithMultipleInputs() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/circle aroundTheCampFire"));
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
    Message msg = Message.makeMessage("BCT", "sean", 3, "hello world");
    assertTrue(msg.isBroadcastMessage());
    assertEquals(msg.getChannelId(), -1);
  }

  @Test
  public void testCreateGroup() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup myGroup"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/groups"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("myGroup"));
    assertEquals(bot, callback.getName());
    assertTrue(callback.getText().contains("general"));
  }
  @Test
  public void testCreateGroupSuccess() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup o"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group o"));
    Message callback = waitingList1.remove();
    assertTrue( callback.getText().contains("Active channel set to Group o"));
  }

  @Test
  public void testCreateGroupFails()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    Mockito.when(groupRepository.addGroup(any())).thenReturn(false);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup myGroup"));
    Message callback = waitingList2.remove();
    assertEquals("Something went wrong and your group was not created.", callback.getText());
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
    assertTrue(callback.getText().contains("Active channel set to Group o"));
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
    Prattle.commandMessage(Message.makeCommandMessage("nonexistent", -1, "/group general"));
    //Message callback = waitingList1.remove();
    //assertTrue(callback.getText().contains("Group a does not exist"));
  }

  @Test
  public void testMessagesReceivedGeneral() {
    Prattle.broadcastMessage(Message.makeMessage("BCT", "omar", 2, "Hey T"));
    Message callback = waitingList2.remove();
    assertEquals("omar", callback.getName());
    assertEquals("Hey T", callback.getText());
  }

  @Test
  public void testMessagesReceivedSameGroup() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/createGroup grp"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group grp"));
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/group grp"));
    Prattle.broadcastMessage(Message.makeMessage("BCT", "omar", 2, "Hey T"));
    waitingList2.remove();
    Message callback = waitingList2.remove();
    assertEquals("omar", callback.getName());
    assertEquals("Hey T", callback.getText());
  }

  @Test
  public void testMessagesNotReceivedDifferentGroup() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/createGroup ogrp"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group ogrp"));
    Prattle.broadcastMessage(Message.makeMessage("BCT", "omar", 2, "Hey T"));
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
    assertEquals("No notifications to show", callback.getText());

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
    Mockito.when(notificationRepo.getAllNotificationsByReceiverId(1)).thenReturn(list);
    assertTrue(waitingList2.isEmpty());
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/notification"));
    Message callback = waitingList2.remove();
    assertEquals("No notifications to show", callback.getText());

  }

  @Test
  public void testNotificationCommandNotificationList()
      throws NoSuchFieldException, SecurityException,
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
    Mockito.when(notificationRepo.getAllNotificationsByReceiverId(1)).thenReturn(list);
    assertTrue(waitingList2.isEmpty());
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/notification"));
    Message callback = waitingList2.remove();
    assertEquals("Notifications:\n" +
        "testY has sent you a friend request.  NEW\n", callback.getText());

  }

  @Test
  public void testMustBeMemberToSetGroup()
      throws IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
    GroupRepository groupRepository = Mockito.mock(MockGroupRepository.class);

    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);

    Mockito.when(groupRepository.groupHasMember(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(false);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString()))
        .thenReturn(new SlackGroup(1, "special_group"));

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
    Mockito.when(friendRepository.areFriends(1, 2)).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/dm omar"));
    Message callback = waitingList2.remove();
    assertEquals("You are now messaging omar", callback.getText());
    assertEquals(10, cr2.getActiveChannelId());
  }

  @Test
  public void testDMnewDM() {
    Mockito.when(dmRepository.getDMChannel(Mockito.anyInt(), Mockito.anyInt())).thenReturn(-1);
    Mockito.when(friendRepository.areFriends(1, 2)).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/dm omar"));
    Message callback = waitingList2.remove();
    assertEquals("You are now messaging omar", callback.getText());
    assertEquals(5, cr2.getActiveChannelId());
  }

  /**
   * Tests that you cannot dm someone who is not your friend.
   */
  @Test
  public void testDmNotFriend() {
    Mockito.when(dmRepository.getDMChannel(Mockito.anyInt(), Mockito.anyInt())).thenReturn(-1);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 2, "/dm omar"));
    Message callback = waitingList1.remove();
    assertEquals("You are not friends with omar. Send them a friend request to direct message.",
        callback.getText());
    assertEquals(1, cr2.getActiveChannelId());
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
    Mockito.when(friendRepository.areFriends(1, 2)).thenReturn(true);
    Mockito.when(friendRepository.areFriends(2, 1)).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/dm omar"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/dm tuffaha"));
    assertEquals(10, cr1.getActiveChannelId());
    assertEquals(10, cr2.getActiveChannelId());
  }
  
  @Test
  public void testSendGroupInviteNoParams() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/invite"));
    Message callback = waitingList2.remove();
    assertEquals("No username or group given" , callback.getText());
  }
  
  @Test
  public void testSendGroupInviteCurrentGroupNotModerator() throws SQLException, NoSuchFieldException, 
  SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field groupRep =
        Class.forName("edu.northeastern.ccs.im.server.Prattle").getDeclaredField("groupRepository");
    groupRep.setAccessible(true);
    groupRep.set(null, groupRepository);
    User user = new User(1, "rita", "pwd");
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(user );
    SlackGroup group = new SlackGroup(1 , 1 , "testgp" , 1);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(group);
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(group);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(), Mockito.anyInt())).thenReturn(false);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/invite rita"));
    Message callback = waitingList2.remove();
    assertEquals("You are not a moderator of given group" , callback.getText());
  }
  
  @Test
  public void testSendGroupInviteCurrentGroupSuccess() throws NoSuchFieldException, SecurityException, 
  ClassNotFoundException, IllegalArgumentException, IllegalAccessException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field groupRep =
        Class.forName("edu.northeastern.ccs.im.server.Prattle").getDeclaredField("groupRepository");
    groupRep.setAccessible(true);
    groupRep.set(null, groupRepository);
    User user = new User(1, "rita", "pwd");
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(user );
    SlackGroup group = new SlackGroup(1 , 1 , "testgp" , 1);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(group);
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(group);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
    Mockito.when(groupInviteRepository.add(Mockito.any(GroupInvitation.class))).thenReturn(true);
    Mockito.when(notificationRepository.addNotification(Mockito.any(Notification.class))).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/invite rita"));
    Message callback = waitingList2.remove();
    assertEquals("Invite sent successfully" , callback.getText());
    
  }

  @Test
  public void testSendGroupInviteCurrentGroupFail() throws NoSuchFieldException, SecurityException, 
  ClassNotFoundException, IllegalArgumentException, IllegalAccessException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field groupRep =
        Class.forName("edu.northeastern.ccs.im.server.Prattle").getDeclaredField("groupRepository");
    groupRep.setAccessible(true);
    groupRep.set(null, groupRepository);
    User user = new User(1, "rita", "pwd");
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(user );
    SlackGroup group = new SlackGroup(1 , 1 , "testgp" , 1);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(group);
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(group);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
    Mockito.when(groupInviteRepository.add(Mockito.any(GroupInvitation.class))).thenReturn(false);
    Mockito.when(notificationRepository.addNotification(Mockito.any(Notification.class))).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/invite rita"));
    Message callback = waitingList2.remove();
    assertEquals("Failed to send invite" , callback.getText());
    
  }
  
  @Test
  public void testAcceptInviteNoParams() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/accept"));
    Message callback = waitingList2.remove();
    assertEquals("No group specified" , callback.getText());

  }
  
  @Test
  public void testAcceptInviteGroupDoesntExist() throws NoSuchFieldException, SecurityException, 
  ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field groupRep =
        Class.forName("edu.northeastern.ccs.im.server.Prattle").getDeclaredField("groupRepository");
    groupRep.setAccessible(true);
    groupRep.set(null, groupRepository);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(null);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/accept gp"));
    Message callback = waitingList2.remove();
    assertEquals("Specified group doesn't exist" , callback.getText());

  }
  
  @Test
  public void testAcceptInviteGroupSuccess() throws NoSuchFieldException, SecurityException, 
  ClassNotFoundException, IllegalArgumentException, IllegalAccessException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field groupRep =
        Class.forName("edu.northeastern.ccs.im.server.Prattle").getDeclaredField("groupRepository");
    groupRep.setAccessible(true);
    groupRep.set(null, groupRepository);
    SlackGroup group = new SlackGroup(1 , 1 , "testgp" , 1);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(group);
    Mockito.when(groupInviteRepository.acceptInvite(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/accept gp"));
    Message callback = waitingList2.remove();
    assertEquals("Invite accepted successfully!" , callback.getText());

  }
  
  @Test
  public void testAcceptInviteGroupError() throws NoSuchFieldException, SecurityException, 
  ClassNotFoundException, IllegalArgumentException, IllegalAccessException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field groupRep =
        Class.forName("edu.northeastern.ccs.im.server.Prattle").getDeclaredField("groupRepository");
    groupRep.setAccessible(true);
    groupRep.set(null, groupRepository);
    SlackGroup group = new SlackGroup(1 , 1 , "testgp" , 1);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(group);
    Mockito.when(groupInviteRepository.acceptInvite(Mockito.anyInt(), Mockito.anyInt())).thenReturn(false);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/accept gp"));
    Message callback = waitingList2.remove();
    assertEquals("You do not have an invite to the group" , callback.getText());

  }
  
  @Test
  public void testAcceptInviteGroupSQLException() throws NoSuchFieldException, SecurityException, 
  ClassNotFoundException, IllegalArgumentException, IllegalAccessException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field groupRep =
        Class.forName("edu.northeastern.ccs.im.server.Prattle").getDeclaredField("groupRepository");
    groupRep.setAccessible(true);
    groupRep.set(null, groupRepository);
    SlackGroup group = new SlackGroup(1 , 1 , "testgp" , 1);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(group);
    Mockito.when(groupInviteRepository.acceptInvite(Mockito.anyInt(), Mockito.anyInt())).thenThrow(new SQLException());
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/accept gp"));
    Message callback = waitingList2.remove();
    assertEquals("You do not have an invite to the group" , callback.getText());

  }
  
  @Test
  public void testAcceptInviteGroupAlreadyInGroup() throws NoSuchFieldException, SecurityException, 
  ClassNotFoundException, IllegalArgumentException, IllegalAccessException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field groupRep =
        Class.forName("edu.northeastern.ccs.im.server.Prattle").getDeclaredField("groupRepository");
    groupRep.setAccessible(true);
    groupRep.set(null, groupRepository);
    SlackGroup group = new SlackGroup(1 , 1 , "testgp" , 1);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(group);
    SQLException e = Mockito.mock(SQLException.class);
    Mockito.when(e.getErrorCode()).thenReturn(ErrorCodes.MYSQL_DUPLICATE_PK);
    Mockito.when(groupInviteRepository.acceptInvite(Mockito.anyInt(), Mockito.anyInt())).thenThrow(e);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/accept gp"));
    Message callback = waitingList2.remove();
    assertEquals("You are already part of the group" , callback.getText());

  }
  
  @Test
  public void testSentInvites() {
    List<InviteesGroup> list = new ArrayList<>();
    list.add(new InviteesGroup("tim", "tom"));
    Mockito.when(groupInviteRepository.getGroupInvitationsByInvitorId(Mockito.anyInt())).thenReturn(list );
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/sentinvites"));
    Message callback = waitingList2.remove();
    assertEquals("Invitations sent:\n" + 
        "Invite sent to user tim for group tom\n" , callback.getText());
  }
  
  @Test
  public void testSentInvitesNoInvites() {
    List<InviteesGroup> list = new ArrayList<>();
    Mockito.when(groupInviteRepository.getGroupInvitationsByInvitorId(Mockito.anyInt())).thenReturn(list );
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/sentinvites"));
    Message callback = waitingList2.remove();
    assertEquals("Invitations sent:\n", callback.getText());
  }
  
  @Test
  public void testMyInvites() {
    List<InvitorsGroup> list = new ArrayList<>();
    list.add(new InvitorsGroup("tim", "tom"));
    Mockito.when(groupInviteRepository.getGroupInvitationsByInviteeId(Mockito.anyInt())).thenReturn(list );
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/invites"));
    Message callback = waitingList2.remove();
    assertEquals("Invitations:\n" + 
        "Moderator tim invites you to join group tom\n" , callback.getText());
  }
  
  @Test
  public void testNoMyInvites() {
    List<InvitorsGroup> list = new ArrayList<>();
    Mockito.when(groupInviteRepository.getGroupInvitationsByInviteeId(Mockito.anyInt())).thenReturn(list );
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/invites"));
    Message callback = waitingList2.remove();
    assertEquals("Invitations:\n" , callback.getText());
  }
  
  /**
   * Tests that sending a friend request to a null doesn't work
   */
  @Test
  public void testFriendCommandNullFriend() {
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(null);
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(omar);
    Prattle.commandMessage(Message.makeCommandMessage("josh", 1, "/friend jake"));
    Message callback = waitingList2.remove();
    assertEquals("The specified user does not exist.", callback.getText());
  }


  /**
   * Tests that sending a friend request doesn't work if they're already friends
   */
  @Test
  public void testFriendAreFriends() {
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(mark);
    Mockito.when(friendRepository.areFriends(anyInt(), anyInt())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("mark", 2, "/friend omar"));
    Message callback = waitingList1.remove();
    assertEquals("You are already friends with omar.", callback.getText());
  }

  /**
   * Tests that sending a friend request accepts it if there already is one sent to them by that
   * user
   */
  @Test
  public void testFriendHasPendingFriendRequest() {
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(mark);
    Mockito.when(friendRepository.areFriends(anyInt(), anyInt())).thenReturn(false);
    Mockito.when(friendRequestRepository.hasPendingFriendRequest(anyInt(), anyInt()))
        .thenReturn(true);
    Mockito.when(friendRepository.successfullyAcceptFriendRequest(anyInt(), anyInt()))
        .thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("mark", 2, "/friend omar"));
    Message callback = waitingList1.remove();
    assertEquals("mark and omar are now friends.", callback.getText());
  }

  /**
   * Tests the accepting friend request failure condition.
   */
  @Test
  public void testFriendHasPendingFriendRequestFailure() {
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(mark);
    Mockito.when(friendRepository.areFriends(anyInt(), anyInt())).thenReturn(false);
    Mockito.when(friendRequestRepository.hasPendingFriendRequest(anyInt(), anyInt()))
        .thenReturn(true);
    Mockito.when(friendRequestRepository.successfullySendFriendRequest(anyInt(), anyInt()))
        .thenReturn(false);
    Prattle.commandMessage(Message.makeCommandMessage("mark", 2, "/friend omar"));
    Message callback = waitingList1.remove();
    assertEquals("Something went wrong and we could not accept omar's friend request.",
        callback.getText());
  }

  /**
   * Tests that sending a friend request sends it if there already isn't one from that user
   */
  @Test
  public void testFriendDoesntHasPendingFriendRequest() {
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(mark);
    Mockito.when(friendRepository.areFriends(anyInt(), anyInt())).thenReturn(false);
    Mockito.when(friendRequestRepository.hasPendingFriendRequest(anyInt(), anyInt()))
        .thenReturn(false);
    Mockito.when(friendRequestRepository.successfullySendFriendRequest(anyInt(), anyInt()))
        .thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("mark", 2, "/friend omar"));
    Message callback = waitingList1.remove();
    assertEquals("mark sent omar a friend request.", callback.getText());
  }

  /**
   * Tests that the friends commands shows a list of friends
   */
  @Test
  public void testFriends() {
    List<Integer> friendIds = new ArrayList<>();
    friendIds.add(1);
    Mockito.when(friendRepository.getFriendsByUserId(anyInt())).thenReturn(friendIds);
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(omar);
    Prattle.commandMessage(Message.makeCommandMessage("mark", 2, "/friends"));
    Message callback = waitingList1.remove();
    assertEquals("My friends:\nomar", callback.getText());
  }

  /**
   * Tests that no friends shows a boom roasted message.
   */
  @Test
  public void testNoFriends() {
    Prattle.commandMessage(Message.makeCommandMessage("mark", 2, "/friends"));
    Message callback = waitingList1.remove();
    assertEquals("You have no friends. :(", callback.getText());
  }

  /**
   * Tests that the groupMembers command list all group members
   */
  @Test
  public void testGroupMembersCommand()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup helloGroup = new SlackGroup(100, 2, "hello", 100);
    Mockito.when(groupRepository.addGroup(any())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("mark", 2, "/createGroup hello"));
    Mockito.when(groupRepository.getGroupByName(anyString())).thenReturn(helloGroup);
    Mockito.when(groupRepository.groupHasMember(anyInt(), anyInt())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("mark", 2, "/group hello"));
    Mockito.when(groupRepository.getGroupByChannelId(anyInt())).thenReturn(helloGroup);
    List<String> mods = new ArrayList<>();
    mods.add("omar");
    mods.add("mark");
    Mockito.when(userGroupRepository.getModerators(anyInt())).thenReturn(mods);
    List<String> groupMembers = new ArrayList<>();
    groupMembers.add("omar");
    groupMembers.add("Sandy");
    groupMembers.add("mark");
    Mockito.when(userGroupRepository.getGroupMembers(anyInt())).thenReturn(groupMembers);
    Prattle.commandMessage(Message.makeCommandMessage("mark", 2, "/groupMembers"));
    Message callback = waitingList1.remove();
    assertEquals("Group hello created", callback.getText());
    callback = waitingList1.remove();
    assertEquals("Active channel set to Group hello", callback.getText());
    callback = waitingList1.remove();
    assertEquals("Group Members:\n*omar\nSandy\n*mark", callback.getText());
  }

  /**
   * Tests that the groupMembers with a null group
   */
  @Test
  public void testGroupMembersCommand2()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    Prattle.commandMessage(Message.makeCommandMessage("mark", 2, "/groupMembers"));
    Message callback = waitingList1.remove();
    assertEquals("Your group is non-existent.", callback.getText());
  }


  /**
   * Tests that removing a group member who is not in the group
   */
  @Test
  public void testRemoveUserWithoutName() {
    Prattle.commandMessage(Message.makeCommandMessage("josh", 1, "/kick"));
    Message callback = waitingList2.remove();
    assertEquals("You have not specified a member to kick.", callback.getText());
  }

  /**
   * Tests that removing a group member when a group doesn't exist
   */
  @Test
  public void testRemoveUserWithoutGroup() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
            .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(null);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/kick clobb"));
    Message callback = waitingList2.remove();
    assertEquals("You must set a group as your active channel to kick a member.", callback.getText());
  }

  /**
   * Tests that removing a group member when a user is not moderator.
   */
  @Test
  public void testRemoveUserWhenNotModerator() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
            .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup slackGroup = new SlackGroup(0,"koka");
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(slackGroup);
    List<String> moderators = new ArrayList<>();
    moderators.add("pmar");
    Mockito.when(userGroupRepository.getModerators(Mockito.anyInt())).thenReturn(moderators);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/kick clobb"));
    Message callback = waitingList2.remove();
    assertEquals("You are not the moderator of this group.", callback.getText());
  }

  /**
   * Tests exception while removing a group member.
   */
  @Test
  public void testRemoveUserException() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
            .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup slackGroup = new SlackGroup(0,"koka");
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(slackGroup);
    Mockito.when(userGroupRepository.getModerators(Mockito.anyInt())).thenThrow(new IllegalArgumentException());
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/kick clobb"));
    Message callback = waitingList2.remove();
    assertEquals("You are not the moderator of this group.", callback.getText());
  }

  /**
   * Tests User null exception while removing a group member.
   */
  @Test
  public void testUserByUsernameNullException() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, SQLException {
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
            .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup slackGroup = new SlackGroup(0,"koka");
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(slackGroup);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(),Mockito.anyInt())).thenReturn(true);
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(null);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/kick clobb"));
    Message callback = waitingList2.remove();
    assertEquals("user does not exist", callback.getText());
  }

  /**
   * Tests  user not in Group while removing a group member.
   */
  @Test
  public void testUserNotInGroup() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, SQLException {
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
            .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup slackGroup = new SlackGroup(0,"koka");
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(slackGroup);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(),Mockito.anyInt())).thenReturn(true);
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(omar);
    Mockito.when(groupRepository.groupHasMember(Mockito.anyInt(),Mockito.anyInt())).thenReturn(false);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/kick clobb"));
    Message callback = waitingList2.remove();
    assertEquals("Could not find clobb as a member of this group.", callback.getText());
  }

  /**
   * Tests  user removed from group.
   */
  @Test
  public void testUserRemovedFromGroup() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, SQLException {
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
            .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup slackGroup = new SlackGroup(0,"koka");
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(slackGroup);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(),Mockito.anyInt())).thenReturn(true);
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(omar);
    Mockito.when(groupRepository.groupHasMember(Mockito.anyInt(),Mockito.anyInt())).thenReturn(true);
    Mockito.when(userGroupRepository.removeMember(Mockito.anyInt(),Mockito.anyInt())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/kick clobb"));
    Message callback = waitingList2.remove();
    assertEquals("User omar successfully kicked from group.", callback.getText());
  }

  /**
   * Tests  user removed from group exception.
   */
  @Test
  public void testUserRemovedFromGroupException() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, SQLException {
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
            .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup slackGroup = new SlackGroup(0,"koka");
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(slackGroup);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(),Mockito.anyInt())).thenReturn(true);
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(omar);
    Mockito.when(groupRepository.groupHasMember(Mockito.anyInt(),Mockito.anyInt())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/kick clobb"));
    Message callback = waitingList2.remove();
    assertEquals("Something went wrong. Failed to kick member omar.", callback.getText());
  }

  /**
   * Tests  /lang command.
   */
  @Test
  public void testAvailablesLanguagesToTranslate() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    TranslationSupport translationSupport = Mockito.mock(TranslationSupport.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
            .getDeclaredField("translationSupport");
    gr.setAccessible(true);
    gr.set(null,translationSupport);
    String languagesSupported = "spanish,german";
    when(translationSupport.getAllLanguagesSupported()).thenReturn(languagesSupported);
    Prattle.commandMessage(Message.makeCommandMessage("josh", 1, "/lang"));
    Message callback = waitingList2.remove();
    assertEquals("spanish,german", callback.getText());
  }

  /**
   * Tests translate option when no target language is provided.
   */
  @Test
  public void testNoTargetLanguageToTranslate() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    Prattle.commandMessage(Message.makeCommandMessage("josh", 1, "/translate"));
    Message callback = waitingList2.remove();
    assertEquals("You have to enter a language", callback.getText());
  }

  /**
   * Tests translate option when target language is not supported.
   */
  @Test
  public void testTargetLanguageNotSupportedToTranslate() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    Prattle.commandMessage(Message.makeCommandMessage("josh", 1, "/translate biscuit"));
    Message callback = waitingList2.remove();
    assertEquals("You have to enter a valid language or code. " +
            "check /lang command to find the supported languages", callback.getText());
  }

  /**
   * Tests translate command when no text is given to translate.
   */
  @Test
  public void testNoTextProvidedToTranslate() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    TranslationSupport translationSupport = Mockito.mock(TranslationSupport.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
            .getDeclaredField("translationSupport");
    gr.setAccessible(true);
    gr.set(null,translationSupport);
    String translatedText = "Hola";
    Mockito.when(translationSupport.isLanguageSupported(Mockito.anyString())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("josh", 1, "/translate spanish"));
    Message callback = waitingList2.remove();
    assertEquals("You have to enter some text to translate", callback.getText());
  }

  /**
   * Test's translate command.
   */
  @Test
  public void testTranslate() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    TranslationSupport translationSupport = Mockito.mock(TranslationSupport.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
            .getDeclaredField("translationSupport");
    gr.setAccessible(true);
    gr.set(null,translationSupport);
    String translatedText = "Hola";
    Mockito.when(translationSupport.isLanguageSupported(Mockito.anyString())).thenReturn(true);
    when(translationSupport.translateTextToGivenLanguage(Mockito.anyString(),Mockito.anyString()))
            .thenReturn(translatedText);
    Prattle.commandMessage(Message.makeCommandMessage("josh", 1, "/translate spanish hello"));
    Message callback = waitingList2.remove();
    assertEquals("Hola", callback.getText());
  }

}