package prattleTests;

import edu.northeastern.ccs.im.server.constants.StringConstants;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandMessages;
import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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

import javax.sql.DataSource;

import edu.northeastern.ccs.im.client.Buddy;
import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.NetworkConnection;
import edu.northeastern.ccs.im.server.Prattle;
import edu.northeastern.ccs.im.server.constants.ErrorCodes;
import edu.northeastern.ccs.im.server.models.GroupInvitation;
import edu.northeastern.ccs.im.server.models.InviteesGroup;
import edu.northeastern.ccs.im.server.models.InvitorsGroup;
import edu.northeastern.ccs.im.server.models.Message;
import edu.northeastern.ccs.im.server.models.MessageHistory;
import edu.northeastern.ccs.im.server.models.MessageRecipientType;
import edu.northeastern.ccs.im.server.models.MessageType;
import edu.northeastern.ccs.im.server.models.Notification;
import edu.northeastern.ccs.im.server.models.NotificationType;
import edu.northeastern.ccs.im.server.models.SlackGroup;
import edu.northeastern.ccs.im.server.models.User;
import edu.northeastern.ccs.im.server.models.UserType;
import edu.northeastern.ccs.im.server.repositories.DirectMessageRepository;
import edu.northeastern.ccs.im.server.repositories.FriendRepository;
import edu.northeastern.ccs.im.server.repositories.FriendRequestRepository;
import edu.northeastern.ccs.im.server.repositories.GroupInviteRepository;
import edu.northeastern.ccs.im.server.repositories.GroupRepository;
import edu.northeastern.ccs.im.server.repositories.MessageRepository;
import edu.northeastern.ccs.im.server.repositories.NotificationRepository;
import edu.northeastern.ccs.im.server.repositories.UserGroupRepository;
import edu.northeastern.ccs.im.server.repositories.UserRepository;
import edu.northeastern.ccs.im.server.utility.ChatLogger;
import edu.northeastern.ccs.im.server.utility.TranslationSupport;

import static edu.northeastern.ccs.im.server.constants.StringConstants.CommandMessages.EIGHTY_SIX_NOTIFICATION;
import static edu.northeastern.ccs.im.server.constants.StringConstants.CommandMessages.EIGHTY_SIX_SUCCESS;
import static edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages.GENERIC_ERROR;
import static edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages.NOT_MODERATOR;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


/**
 * Created by venkateshkoka on 2/10/19. All the test cases
 */
public class PrattleTest {

  /**
   * The cr 1.
   */
  private ClientRunnable cr1;

  /**
   * The cr 2.
   */
  private ClientRunnable cr2;

  /**
   * The waiting list 1.
   */
  private Queue<Message> waitingList1;

  /**
   * The waiting list 2.
   */
  private Queue<Message> waitingList2;

  /**
   * The bot.
   */
  private String bot = "Slackbot";

  /**
   * The dm repository.
   */
  private DirectMessageRepository dmRepository;

  /**
   * The user repository.
   */
  private UserRepository userRepository;

  /**
   * The group repository.
   */
  private GroupRepository groupRepository;

  /**
   * The group invite repository.
   */
  private GroupInviteRepository groupInviteRepository;

  /**
   * The notification repository.
   */
  private NotificationRepository notificationRepository;

  /**
   * The message repository.
   */
  private MessageRepository messageRepository;

  /**
   * The friend request repository.
   */
  private FriendRequestRepository friendRequestRepository;

  /**
   * The friend repository.
   */
  private FriendRepository friendRepository;

  /**
   * The user group repository.
   */
  private UserGroupRepository userGroupRepository;

  /**
   * The omar.
   */
  private User omar;

  /**
   * The mark.
   */
  private User mark;

  /**
   * Kill server.
   */
  @BeforeClass
  public static void killServer() {
    Prattle.stopServer();
  }

  /**
   * Initialize the command data before each test.
   *
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchFieldException the no such field exception
   * @throws IllegalAccessException the illegal access exception
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

    omar = new User(1, "omar", "password", UserType.GENERAL);
    mark = new User(2, "mark", "password", UserType.GENERAL);

    groupRepository = new MockGroupRepository();

    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);

    dmRepository = Mockito.mock(DirectMessageRepository.class);
    userRepository = Mockito.mock(UserRepository.class);
    friendRequestRepository = Mockito.mock(FriendRequestRepository.class);
    userGroupRepository = Mockito.mock(UserGroupRepository.class);
    friendRepository = Mockito.mock(FriendRepository.class);

    Field ur = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("userRepository");
    ur.setAccessible(true);
    ur.set(null, userRepository);

    Field commandUr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("userRepository");
    commandUr.setAccessible(true);
    commandUr.set(null, userRepository);

    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(omar);

    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(omar);

    Field dmr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("dmRepository");
    dmr.setAccessible(true);
    dmr.set(null, dmRepository);

    userGroupRepository = Mockito.mock(UserGroupRepository.class);

    Field userGroupRepo =
        Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
            .getDeclaredField("userGroupRepository");
    userGroupRepo.setAccessible(true);
    userGroupRepo.set(null, userGroupRepository);

    Field frr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("friendRequestRepository");
    frr.setAccessible(true);
    frr.set(null, friendRequestRepository);

    Field fr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("friendRepository");
    fr.setAccessible(true);
    fr.set(null, friendRepository);

    Field ugr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("userGroupRepository");
    ugr.setAccessible(true);
    ugr.set(null, userGroupRepository);

    Mockito.when(dmRepository.createDM(Mockito.anyInt(), Mockito.anyInt())).thenReturn(5);
    Mockito.when(dmRepository.getDMChannel(Mockito.anyInt(), Mockito.anyInt())).thenReturn(10);

    groupInviteRepository = Mockito.mock(GroupInviteRepository.class);
    Field fieldInviteRepo =
        Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
            .getDeclaredField("groupInviteRepository");
    fieldInviteRepo.setAccessible(true);
    fieldInviteRepo.set(null, groupInviteRepository);

    notificationRepository = Mockito.mock(NotificationRepository.class);
    Field fieldNotificationRepository =
        Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
            .getDeclaredField("notificationRepository");
    fieldNotificationRepository.setAccessible(true);
    fieldNotificationRepository.set(null, notificationRepository);

    messageRepository = Mockito.mock(MessageRepository.class);
    Field fieldMessageRepository =
        Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
            .getDeclaredField("messageRepository");
    fieldMessageRepository.setAccessible(true);
    fieldMessageRepository.set(null, messageRepository);
  }

  /**
   * Mock Group Repository class for testing purposes.
   */
  private class MockGroupRepository extends GroupRepository {

    /**
     * The mock db.
     */
    Map<String, SlackGroup> mockDb;

    /**
     * Instantiates a new mock group repository.
     */
    MockGroupRepository() {
      super(Mockito.mock(DataSource.class));
      mockDb = new HashMap<>();

      mockDb.put("general", new SlackGroup(1, -1, "general", 1));
      mockDb.put("group2", new SlackGroup(2, -1, "group2", 2));
      mockDb.put("groupWithPass", new SlackGroup(3, -1, "groupWithPass", 3, false, "password"));
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.server.repositories.GroupRepository#addGroup(edu.northeastern.ccs.im.server.models.SlackGroup)
     */
    @Override
    public boolean addGroup(SlackGroup toAdd) {
      if (mockDb.containsKey(toAdd.getGroupName())) {
        return false;
      } else {
        mockDb.put(toAdd.getGroupName(), toAdd);
        return true;
      }
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.server.repositories.GroupRepository#getGroupById(int)
     */
    @Override
    public SlackGroup getGroupById(int id) {
      SlackGroup ans = null;
      for (SlackGroup group : mockDb.values()) {
        ans = group.getGroupId() == id ? group : ans;
      }
      return ans;
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.server.repositories.GroupRepository#getGroupByName(java.lang.String)
     */
    @Override
    public SlackGroup getGroupByName(String groupName) {
      return mockDb.getOrDefault(groupName, null);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.server.repositories.GroupRepository#groupHasMember(int, int)
     */
    @Override
    public boolean groupHasMember(int id, int groupId) {
      return true;
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.server.repositories.GroupRepository#groupsHavingMember(int)
     */
    @Override
    public String groupsHavingMember(int id) {
      StringBuilder ans = new StringBuilder();
      for (SlackGroup group : mockDb.values()) {
        ans.append(group.getGroupName());
        ans.append("\n");
      }
      return ans.toString();
    }

    @Override
    public boolean deleteGroup(int moderatorId, int groupId) {
      return true;
    }

    @Override
    public SlackGroup getGroupByChannelId(int channelId) {
      for (SlackGroup group : mockDb.values()) {
        if (group.getChannelId() == channelId) {
          return group;
        }
      }
      return null;
    }
  }

  /**
   * Terminate.
   */
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
    Constructor constructor = Class
        .forName("edu.northeastern.ccs.im.server.constants.ServerConstants")
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
    Method makeMessageMethod = Class.forName("edu.northeastern.ccs.im.server.models.Message")
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

  /**
   * Test stop server.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   */
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
  }

  /**
   * Tests that the /circle command works by listing all active users.
   */
  @Test
  public void testCircleListsAllActiveUsers() {
    List<Integer> friendIds = new ArrayList<>();
    friendIds.add(2);
    Mockito.when(friendRepository.getFriendsByUserId(anyInt())).thenReturn(friendIds);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/circle"));
    assertEquals(1, waitingList2.size());
    assertEquals(0, waitingList1.size());
    Message callback = waitingList2.remove();
    assertEquals(bot, callback.getName());
    assertEquals(-1, callback.getChannelId());
    assertEquals("Active Friends:\nomar", callback.getText());
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
    assertEquals(ErrorMessages.COMMAND_NOT_RECOGNIZED, callback.getText());
  }

  /**
   * Tests that a non initialized client will not get the broadcasted command.
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
   * Tests help works.
   */
  @Test
  public void testHelp() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/help"));
    assertEquals(0, waitingList2.size());
    assertEquals(1, waitingList1.size());
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains(CommandDescriptions.GROUPS_DESCRIPTION));
    assertEquals(bot, callback.getName());
  }

  /**
   * Tests commands with extra params.
   */
  @Test
  public void testCommandMessageWithMultipleInputs() {
    List<Integer> friendIds = new ArrayList<>();
    friendIds.add(2);
    Mockito.when(friendRepository.getFriendsByUserId(anyInt())).thenReturn(friendIds);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/circle aroundTheCampFire"));
    assertEquals(1, waitingList2.size());
    assertEquals(0, waitingList1.size());
    Message removed = waitingList2.remove();
    assertEquals(bot, removed.getName());
    assertEquals(0, waitingList2.size());
    assertEquals(-1, removed.getChannelId());
    assertEquals("Active Friends:\nomar", removed.getText());
  }

  /**
   * Tests that users online get updated after leaving.
   */
  @Test
  public void testNoneOnline() {
    List<Integer> friendIds = new ArrayList<>();
    friendIds.add(2);
    Mockito.when(friendRepository.getFriendsByUserId(anyInt())).thenReturn(friendIds);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/circle"));
    assertEquals(1, waitingList2.size());
    Message callback = waitingList2.remove();
    assertEquals(0, waitingList2.size());
    assertEquals(0, waitingList1.size());
    assertEquals("Active Friends:\nomar", callback.getText());
    Prattle.removeClient(cr1);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/circle"));
    assertEquals(1, waitingList2.size());
    Message removed = waitingList2.remove();
    assertEquals(0, waitingList2.size());
    assertEquals(0, waitingList1.size());
    assertEquals(ErrorMessages.NO_ACTIVE_FRIENDS, removed.getText());
  }

  /**
   * Test make broadcast message inherits channel id.
   */
  @Test
  public void testMakeBroadcastMessageInheritsChannelId() {
    Message msg = Message.makeMessage("BCT", "omar", 2, "hello world");
    assertTrue(msg.isBroadcastMessage());
    assertEquals(msg.getChannelId(), cr1.getActiveChannelId());
  }

  /**
   * Test make broadcast message default channel id.
   */
  @Test
  public void testMakeBroadcastMessageDefaultChannelId() {
    Message msg = Message.makeMessage("BCT", "sean", 3, "hello world");
    assertTrue(msg.isBroadcastMessage());
    assertEquals(msg.getChannelId(), -1);
  }

  /**
   * Test create group.
   */
  @Test
  public void testCreateGroup() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup myGroup"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/groups"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("myGroup"));
    assertEquals(bot, callback.getName());
    assertTrue(callback.getText().contains("general"));
  }

  /**
   * Test create group success.
   */
  @Test
  public void testCreateGroupSuccess() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup o"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group o"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("Active channel set to Group o"));
  }

  /**
   * Test create group fails.
   *
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchFieldException the no such field exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testCreateGroupFails()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    Mockito.when(groupRepository.addGroup(any())).thenReturn(false);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup myGroup"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.GENERIC_ERROR, callback.getText());
  }

  /**
   * Test create group special characters.
   */
  @Test
  public void testCreateGroupSpecialCharacters() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup !@578"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/groups"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("!@578"));
    assertTrue(callback.getText().contains("general"));
  }

  /**
   * Test create group spaces.
   */
  @Test
  public void testCreateGroupSpaces() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup My Group"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/groups"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("My"));
    assertFalse(callback.getText().contains("Group"));
    assertTrue(callback.getText().contains("general"));
  }

  /**
   * Test create group no input.
   */
  @Test
  public void testCreateGroupNoInput() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup"));
    Message callback = waitingList2.remove();
    assertTrue(callback.getText().contains(ErrorMessages.INCORRECT_COMMAND_PARAMETERS));
    assertEquals(bot, callback.getName());
  }

  /**
   * Test create group name taken.
   */
  @Test
  public void testCreateGroupNameTaken() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup general"));
    Message callback = waitingList2.remove();
    assertTrue(callback.getText().contains("A group with this name already exists"));
    assertEquals(bot, callback.getName());
  }

  /**
   * Test make command message.
   */
  @Test
  public void testMakeCommandMessage() {
    Message msg = Message.makeMessage("CMD", "omar", 2, "/hello");
    assertTrue(msg.isCommandMessage());
  }

  /**
   * Test show groups default.
   */
  @Test
  public void testShowGroupsDefault() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/groups"));
    assertEquals(0, waitingList2.size());
    Message callback = waitingList1.remove();
    assertEquals(callback.getName(), bot);
    assertTrue(callback.getText().contains("general"));
  }

  /**
   * Test show multiple groups.
   */
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

  /**
   * Test change group.
   */
  @Test
  public void testChangeGroup() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/createGroup o"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group o"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("Active channel set to Group o"));
  }

  /**
   * Test change group no input.
   */
  @Test
  public void testChangeGroupNoInput() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains(ErrorMessages.INCORRECT_COMMAND_PARAMETERS));
  }

  /**
   * Test change group not found.
   */
  @Test
  public void testChangeGroupNotFound() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group a"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains(ErrorMessages.NON_EXISTING_GROUP));
  }

  /**
   * Test client id doesnt match.
   */
  @Test
  public void testClientIdDoesntMatch() {
    Prattle.commandMessage(Message.makeCommandMessage("nonexistent", -1, "/group general"));
    //Message callback = waitingList1.remove();
    //assertTrue(callback.getText().contains("Group a does not exist"));
  }

  /**
   * Test messages received general.
   */
  @Test
  public void testMessagesReceivedGeneral() {
    Prattle.broadcastMessage(Message.makeMessage("BCT", "omar", 2, "Hey T"));
    Message callback = waitingList2.remove();
    assertEquals("omar", callback.getName());
    assertEquals("Hey T", callback.getText());
  }

  /**
   * Test messages received same group.
   */
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

  /**
   * Test messages not received different group.
   */
  @Test
  public void testMessagesNotReceivedDifferentGroup() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/createGroup ogrp"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group ogrp"));
    Prattle.broadcastMessage(Message.makeMessage("BCT", "omar", 2, "Hey T"));
    assertTrue(waitingList2.isEmpty());
  }

  /**
   * Test notification command null list.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testNotificationCommandNullList() throws NoSuchFieldException, SecurityException,
      ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    Field notificationRepoField = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("notificationRepository");
    notificationRepoField.setAccessible(true);
    NotificationRepository notificationRepo = Mockito.mock(NotificationRepository.class);
    notificationRepoField.set(null, notificationRepo);
    Mockito.when(notificationRepo.getAllNotificationsByReceiverId(1)).thenReturn(null);
    assertTrue(waitingList2.isEmpty());
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/notification"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NO_NOTIFICATIONS, callback.getText());

  }

  /**
   * Test notification command empty list.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testNotificationCommandEmptyList() throws NoSuchFieldException, SecurityException,
      ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    Field notificationRepoField = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("notificationRepository");
    notificationRepoField.setAccessible(true);
    NotificationRepository notificationRepo = Mockito.mock(NotificationRepository.class);
    notificationRepoField.set(null, notificationRepo);
    List<Notification> list = new ArrayList<>();
    Mockito.when(notificationRepo.getAllNotificationsByReceiverId(1)).thenReturn(list);
    assertTrue(waitingList2.isEmpty());
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/notification"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NO_NOTIFICATIONS, callback.getText());

  }

  /**
   * Test notification command notification list.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testNotificationCommandNotificationList()
      throws NoSuchFieldException, SecurityException,
      ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    Field notificationRepoField = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("notificationRepository");
    notificationRepoField.setAccessible(true);
    NotificationRepository notificationRepo = Mockito.mock(NotificationRepository.class);
    notificationRepoField.set(null, notificationRepo);

    Field userRepoField = Class
        .forName("edu.northeastern.ccs.im.server.models.NotificationConvertor")
        .getDeclaredField("userRepository");
    userRepoField.setAccessible(true);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    userRepoField.set(null, userRepository);
    User user = new User(2, "testY", "pwd", UserType.GENERAL);
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

  /**
   * Test must be member to set group.
   *
   * @throws IllegalAccessException the illegal access exception
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchFieldException the no such field exception
   */
  @Test
  public void testMustBeMemberToSetGroup()
      throws IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
    groupRepository = Mockito.mock(MockGroupRepository.class);

    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);

    Mockito.when(groupRepository.groupHasMember(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(false);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString()))
        .thenReturn(new SlackGroup(1, "special_group", null));

    ((MockGroupRepository) groupRepository).mockDb = new HashMap<>();

    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group special_group"));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.CURRENT_USER_NOT_IN_GROUP, callback.getText());
  }

  /**
   * Test DM user not found.
   */
  @Test
  public void testDMUserNotFound() {
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(null);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/dm koka"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NON_EXISTING_USER, callback.getText());
    assertEquals(1, cr2.getActiveChannelId());
  }

  /**
   * Test DM existing DM.
   */
  @Test
  public void testDMExistingDM() {
    Mockito.when(friendRepository.areFriends(1, 2)).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/dm omar"));
    Message callback = waitingList2.remove();
    assertEquals(String.format(CommandMessages.SUCCESSFUL_DM, "omar"), callback.getText());
    assertEquals(10, cr2.getActiveChannelId());
  }

  /**
   * Test D mnew DM.
   */
  @Test
  public void testDMnewDM() {
    Mockito.when(dmRepository.getDMChannel(Mockito.anyInt(), Mockito.anyInt())).thenReturn(-1);
    Mockito.when(friendRepository.areFriends(1, 2)).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/dm omar"));
    Message callback = waitingList2.remove();
    assertEquals(String.format(CommandMessages.SUCCESSFUL_DM, "omar"), callback.getText());
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
    assertEquals(String.format(ErrorMessages.NOT_FRIENDS, "omar"), callback.getText());
    assertEquals(1, cr2.getActiveChannelId());
  }

  /**
   * Test D mquery fail.
   */
  @Test
  public void testDMqueryFail() {
    Mockito.when(dmRepository.getDMChannel(Mockito.anyInt(), Mockito.anyInt())).thenReturn(-1);
    Mockito.when(dmRepository.createDM(Mockito.anyInt(), Mockito.anyInt())).thenReturn(-1);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/dm omar"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.GENERIC_ERROR, callback.getText());
    assertEquals(1, cr2.getActiveChannelId());
  }

  /**
   * Test DM between users.
   */
  @Test
  public void testDMBetweenUsers() {
    Mockito.when(friendRepository.areFriends(1, 2)).thenReturn(true);
    Mockito.when(friendRepository.areFriends(2, 1)).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/dm omar"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/dm tuffaha"));
    assertEquals(10, cr1.getActiveChannelId());
    assertEquals(10, cr2.getActiveChannelId());
  }

  /**
   * Test send group invite no params.
   */
  @Test
  public void testSendGroupInviteNoParams() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/invite"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.INCORRECT_COMMAND_PARAMETERS, callback.getText());
  }

  /**
   * Test send group invite current group not moderator.
   *
   * @throws SQLException the SQL exception
   * @throws NoSuchFieldException the no such field exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testSendGroupInviteCurrentGroupNotModerator()
      throws SQLException, NoSuchFieldException,
      SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field groupRep =
        Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
            .getDeclaredField("groupRepository");
    groupRep.setAccessible(true);
    groupRep.set(null, groupRepository);
    User user = new User(1, "rita", "pwd", UserType.GENERAL);
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(user);
    SlackGroup group = new SlackGroup(1, 1, "testgp", 1);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(group);
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(group);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(false);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/invite rita"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NOT_MODERATOR, callback.getText());
  }

  /**
   * Test send group invite current group success.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   * @throws SQLException the SQL exception
   */
  @Test
  public void testSendGroupInviteCurrentGroupSuccess()
      throws NoSuchFieldException, SecurityException,
      ClassNotFoundException, IllegalArgumentException, IllegalAccessException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field groupRep =
        Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
            .getDeclaredField("groupRepository");
    groupRep.setAccessible(true);
    groupRep.set(null, groupRepository);
    User user = new User(1, "rita", "pwd", UserType.GENERAL);
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(user);
    SlackGroup group = new SlackGroup(1, 1, "testgp", 1);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(group);
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(group);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Mockito.when(groupInviteRepository.add(Mockito.any(GroupInvitation.class))).thenReturn(true);
    Mockito.when(notificationRepository.addNotification(Mockito.any(Notification.class)))
        .thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/invite rita"));
    Message callback = waitingList2.remove();
    assertEquals(CommandMessages.SUCCESSFUL_INVITE, callback.getText());
  }

  /**
   * Test send group invite current group fail.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   * @throws SQLException the SQL exception
   */
  @Test
  public void testSendGroupInviteCurrentGroupFail() throws NoSuchFieldException, SecurityException,
      ClassNotFoundException, IllegalArgumentException, IllegalAccessException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field groupRep =
        Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
            .getDeclaredField("groupRepository");
    groupRep.setAccessible(true);
    groupRep.set(null, groupRepository);
    User user = new User(1, "rita", "pwd", UserType.GENERAL);
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(user);
    SlackGroup group = new SlackGroup(1, 1, "testgp", 1);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(group);
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(group);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Mockito.when(groupInviteRepository.add(Mockito.any(GroupInvitation.class))).thenReturn(false);
    Mockito.when(notificationRepository.addNotification(Mockito.any(Notification.class)))
        .thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/invite rita"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.UNSUCCESSFUL_INVITE, callback.getText());

  }

  /**
   * Test accept invite no params.
   */
  @Test
  public void testAcceptInviteNoParams() {
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/accept"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.INCORRECT_COMMAND_PARAMETERS, callback.getText());

  }

  /**
   * Test accept invite group doesnt exist.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testAcceptInviteGroupDoesntExist() throws NoSuchFieldException, SecurityException,
      ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field groupRep =
        Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
            .getDeclaredField("groupRepository");
    groupRep.setAccessible(true);
    groupRep.set(null, groupRepository);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(null);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/accept gp"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NON_EXISTING_GROUP, callback.getText());

  }

  /**
   * Test accept invite group success.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   * @throws SQLException the SQL exception
   */
  @Test
  public void testAcceptInviteGroupSuccess() throws NoSuchFieldException, SecurityException,
      ClassNotFoundException, IllegalArgumentException, IllegalAccessException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field groupRep =
        Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
            .getDeclaredField("groupRepository");
    groupRep.setAccessible(true);
    groupRep.set(null, groupRepository);
    SlackGroup group = new SlackGroup(1, 1, "testgp", 1);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(group);
    Mockito.when(groupInviteRepository.acceptInvite(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/accept gp"));
    Message callback = waitingList2.remove();
    assertEquals(CommandMessages.SUCCESSFUL_INVITE_ACCEPT, callback.getText());
  }

  /**
   * Test accept invite group error.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   * @throws SQLException the SQL exception
   */
  @Test
  public void testAcceptInviteGroupError() throws NoSuchFieldException, SecurityException,
      ClassNotFoundException, IllegalArgumentException, IllegalAccessException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field groupRep =
        Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
            .getDeclaredField("groupRepository");
    groupRep.setAccessible(true);
    groupRep.set(null, groupRepository);
    SlackGroup group = new SlackGroup(1, 1, "testgp", 1);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(group);
    Mockito.when(groupInviteRepository.acceptInvite(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(false);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/accept gp"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NO_INVITE, callback.getText());

  }

  /**
   * Test accept invite group SQL exception.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   * @throws SQLException the SQL exception
   */
  @Test
  public void testAcceptInviteGroupSQLException() throws NoSuchFieldException, SecurityException,
      ClassNotFoundException, IllegalArgumentException, IllegalAccessException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field groupRep =
        Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
            .getDeclaredField("groupRepository");
    groupRep.setAccessible(true);
    groupRep.set(null, groupRepository);
    SlackGroup group = new SlackGroup(1, 1, "testgp", 1);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(group);
    Mockito.when(groupInviteRepository.acceptInvite(Mockito.anyInt(), Mockito.anyInt()))
        .thenThrow(new SQLException());
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/accept gp"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NO_INVITE, callback.getText());

  }

  /**
   * Test accept invite group already in group.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   * @throws SQLException the SQL exception
   */
  @Test
  public void testAcceptInviteGroupAlreadyInGroup() throws NoSuchFieldException, SecurityException,
      ClassNotFoundException, IllegalArgumentException, IllegalAccessException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field groupRep =
        Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
            .getDeclaredField("groupRepository");
    groupRep.setAccessible(true);
    groupRep.set(null, groupRepository);
    SlackGroup group = new SlackGroup(1, 1, "testgp", 1);
    Mockito.when(groupRepository.getGroupByName(Mockito.anyString())).thenReturn(group);
    SQLException e = Mockito.mock(SQLException.class);
    Mockito.when(e.getErrorCode()).thenReturn(ErrorCodes.MYSQL_DUPLICATE_PK);
    Mockito.when(groupInviteRepository.acceptInvite(Mockito.anyInt(), Mockito.anyInt()))
        .thenThrow(e);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/accept gp"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.ALREADY_IN_GROUP, callback.getText());

  }

  /**
   * Test sent invites.
   */
  @Test
  public void testSentInvites() {
    List<InviteesGroup> list = new ArrayList<>();
    list.add(new InviteesGroup("tim", "tom"));
    Mockito.when(groupInviteRepository.getGroupInvitationsByInvitorId(Mockito.anyInt()))
        .thenReturn(list);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/sentinvites"));
    Message callback = waitingList2.remove();
    assertEquals("Invitations sent:\nInvite sent to user tim for group tom.\n",
        callback.getText());
  }

  /**
   * Test sent invites no invites.
   */
  @Test
  public void testSentInvitesNoInvites() {
    List<InviteesGroup> list = new ArrayList<>();
    Mockito.when(groupInviteRepository.getGroupInvitationsByInvitorId(Mockito.anyInt()))
        .thenReturn(list);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/sentinvites"));
    Message callback = waitingList2.remove();
    assertEquals("Invitations sent:\n", callback.getText());
  }

  /**
   * Test my invites.
   */
  @Test
  public void testMyInvites() {
    List<InvitorsGroup> list = new ArrayList<>();
    list.add(new InvitorsGroup("tim", "tom"));
    Mockito.when(groupInviteRepository.getGroupInvitationsByInviteeId(Mockito.anyInt()))
        .thenReturn(list);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/invites"));
    Message callback = waitingList2.remove();
    assertEquals("Invitations:\nModerator tim invites you to join group tom.\n",
        callback.getText());
  }

  /**
   * Test no my invites.
   */
  @Test
  public void testNoMyInvites() {
    List<InvitorsGroup> list = new ArrayList<>();
    Mockito.when(groupInviteRepository.getGroupInvitationsByInviteeId(Mockito.anyInt()))
        .thenReturn(list);
    Prattle.commandMessage(Message.makeCommandMessage("tuffaha", 1, "/invites"));
    Message callback = waitingList2.remove();
    assertEquals("Invitations:\n", callback.getText());
  }

  /**
   * Tests that sending a friend request to a null doesn't work.
   */
  @Test
  public void testFriendCommandNullFriend() {
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(null);
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(omar);
    Prattle.commandMessage(Message.makeCommandMessage("josh", 1, "/friend jake"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NON_EXISTING_USER, callback.getText());
  }

  /**
   * Tests that sending a friend request to yourself fails
   */
  @Test
  public void testFriendCommandFriendYourself() {
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(omar);
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(omar);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/friend omar"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.FRIEND_ONESELF_ERROR, callback.getText());
  }

  /**
   * Tests that sending a friend rfequest to a null doesn't work.
   */
  @Test
  public void testFriendCommandNullFriend2() {
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(null);
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(omar);
    Prattle.commandMessage(Message.makeCommandMessage("michael", 1, "/friend"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.INCORRECT_COMMAND_PARAMETERS, callback.getText());
  }


  /**
   * Tests that sending a friend request doesn't work if they're already friends.
   */
  @Test
  public void testFriendAreFriends() {
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(mark);
    Mockito.when(friendRepository.areFriends(anyInt(), anyInt())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("mark", 2, "/friend omar"));
    Message callback = waitingList1.remove();
    assertEquals(String.format(ErrorMessages.ALREADY_FRIENDS, "omar"), callback.getText());
  }

  /**
   * Tests that sending a friend request accepts it if there already is one sent to them by that
   * user.
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
    assertEquals(ErrorMessages.GENERIC_ERROR, callback.getText());
  }

  /**
   * Tests the sending a friend request fails if you already sent one
   */
  @Test
  public void testFriendDoesntWorkTwice() {
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(mark);
    Mockito.when(friendRepository.areFriends(anyInt(), anyInt())).thenReturn(false);
    Mockito.when(friendRequestRepository.hasPendingFriendRequest(anyInt(), anyInt()))
        .thenReturn(false);
    Mockito.when(friendRequestRepository.successfullySendFriendRequest(anyInt(), anyInt()))
        .thenReturn(false);
    Prattle.commandMessage(Message.makeCommandMessage("mark", 2, "/friend omar"));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.COMMAND_ALREADY_PROCESSED, callback.getText());
  }

  /**
   * Tests that sending a friend request sends it if there already isn't one from that user.
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
   * Tests that the friends commands shows a list of friends.
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
   * Tests that the groupMembers command list all group members.
   *
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchFieldException the no such field exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testGroupMembersCommand()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
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
    assertEquals(String.format(CommandMessages.SUCCESSFUL_GROUP_CREATED, "hello"),
        callback.getText());
    callback = waitingList1.remove();
    assertEquals(String.format(StringConstants.ACTIVE_CHANNEL_SET, "hello"), callback.getText());
    callback = waitingList1.remove();
    assertEquals("Group Members:\n*omar\nSandy\n*mark", callback.getText());
  }

  /**
   * Tests that the groupMembers with a null group.
   *
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchFieldException the no such field exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testGroupMembersCommand2()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    Prattle.commandMessage(Message.makeCommandMessage("mark", 2, "/groupMembers"));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.NON_EXISTING_GROUP, callback.getText());
  }


  /**
   * Tests that removing a group member who is not in the group.
   */
  @Test
  public void testRemoveUserWithoutName() {
    Prattle.commandMessage(Message.makeCommandMessage("josh", 1, "/kick"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.INCORRECT_COMMAND_PARAMETERS, callback.getText());
  }

  /**
   * Tests that removing a group member when a group doesn't exist.
   *
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchFieldException the no such field exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testRemoveUserWithoutGroup()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(null);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/kick clobb"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NON_EXISTING_GROUP, callback.getText());
  }

  /**
   * Tests that removing a group member when a user is not moderator.
   *
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchFieldException the no such field exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testRemoveUserWhenNotModerator()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup slackGroup = new SlackGroup(0, "koka", null);
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(slackGroup);
    List<String> moderators = new ArrayList<>();
    moderators.add("pmar");
    Mockito.when(userGroupRepository.getModerators(Mockito.anyInt())).thenReturn(moderators);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/kick clobb"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NOT_MODERATOR, callback.getText());
  }

  /**
   * Tests exception while removing a group member.
   *
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchFieldException the no such field exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testRemoveUserException()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup slackGroup = new SlackGroup(0, "koka", null);
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(slackGroup);
    Mockito.when(userGroupRepository.getModerators(Mockito.anyInt()))
        .thenThrow(new IllegalArgumentException());
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/kick clobb"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NOT_MODERATOR, callback.getText());
  }

  /**
   * Tests User null exception while removing a group member.
   *
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchFieldException the no such field exception
   * @throws IllegalAccessException the illegal access exception
   * @throws SQLException the SQL exception
   */
  @Test
  public void testUserByUsernameNullException()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup slackGroup = new SlackGroup(0, "koka", null);
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(slackGroup);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(null);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/kick clobb"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NON_EXISTING_USER, callback.getText());
  }

  /**
   * Tests  user not in Group while removing a group member.
   *
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchFieldException the no such field exception
   * @throws IllegalAccessException the illegal access exception
   * @throws SQLException the SQL exception
   */
  @Test
  public void testUserNotInGroup()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup slackGroup = new SlackGroup(0, "koka", null);
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(slackGroup);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(omar);
    Mockito.when(groupRepository.groupHasMember(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(false);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/kick clobb"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.USER_NOT_IN_GROUP, callback.getText());
  }

  /**
   * Tests  user removed from group.
   *
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchFieldException the no such field exception
   * @throws IllegalAccessException the illegal access exception
   * @throws SQLException the SQL exception
   */
  @Test
  public void testUserRemovedFromGroup()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup slackGroup = new SlackGroup(0, "koka", null);
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(slackGroup);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(omar);
    Mockito.when(groupRepository.groupHasMember(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Mockito.when(userGroupRepository.removeMember(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/kick clobb"));
    Message callback = waitingList2.remove();
    assertEquals(CommandMessages.SUCCESSFUL_KICK, callback.getText());
  }

  /**
   * Tests  user removed from group exception.
   *
   * @throws ClassNotFoundException the class not found exception
   * @throws NoSuchFieldException the no such field exception
   * @throws IllegalAccessException the illegal access exception
   * @throws SQLException the SQL exception
   */
  @Test
  public void testUserRemovedFromGroupException()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup slackGroup = new SlackGroup(0, "koka", null);
    Mockito.when(groupRepository.getGroupByChannelId(Mockito.anyInt())).thenReturn(slackGroup);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(omar);
    Mockito.when(groupRepository.groupHasMember(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/kick clobb"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.UNSUCCESSFUL_KICK, callback.getText());
  }

  /**
   * Tests that added a moderator works.
   *
   * @throws ClassNotFoundException class not found exception
   * @throws NoSuchFieldException no such field exception
   * @throws IllegalAccessException illegal access exception
   */
  @Test
  public void testAddModerator()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup helloGroup = new SlackGroup(100, 2, "hello", 100);
    Mockito.when(groupRepository.addGroup(any())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/createGroup hello"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group hello"));
    Mockito.when(groupRepository.getGroupByChannelId(anyInt())).thenReturn(helloGroup);
    List<String> mods = new ArrayList<>();
    mods.add("omar");
    List<String> members = new ArrayList<>();
    members.add("omar");
    members.add("Chicken");
    Mockito.when(userGroupRepository.getModerators(Mockito.anyInt())).thenReturn(mods);
    Mockito.when(userGroupRepository.getGroupMembers(Mockito.anyInt())).thenReturn(members);
    User chicken = new User(5, "Chicken", "password", UserType.GENERAL);
    Mockito.when(userRepository.getUserByUserName("Chicken")).thenReturn(chicken);
    Mockito.when(groupRepository.groupHasMember(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/addModerator Chicken"));
    waitingList1.remove();
    waitingList1.remove();
    Message callback = waitingList1.remove();
    assertEquals("omar added Chicken as a moderator of this group.", callback.getText());
  }

  /**
   * Tests that added a moderator fails if they are already a moderator.
   *
   * @throws ClassNotFoundException class not found exception
   * @throws NoSuchFieldException no such field exception
   * @throws IllegalAccessException illegal access exception
   */
  @Test
  public void testAddModeratorDuplicateMod()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup helloGroup = new SlackGroup(100, 2, "hello", 100);
    Mockito.when(groupRepository.addGroup(any())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/createGroup hello"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group hello"));
    Mockito.when(groupRepository.getGroupByChannelId(anyInt())).thenReturn(helloGroup);
    List<String> mods = new ArrayList<>();
    mods.add("omar");
    mods.add("cowgirl");
    List<String> members = new ArrayList<>();
    members.add("omar");
    members.add("cowgirl");
    Mockito.when(userGroupRepository.getModerators(Mockito.anyInt())).thenReturn(mods);
    Mockito.when(userGroupRepository.getGroupMembers(Mockito.anyInt())).thenReturn(members);
    User cowgirl = new User(5, "cowgirl", "password", UserType.GENERAL);
    Mockito.when(userRepository.getUserByUserName("cowgirl")).thenReturn(cowgirl);
    Mockito.when(groupRepository.groupHasMember(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/addModerator cowgirl"));
    waitingList1.remove();
    waitingList1.remove();
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.ALREADY_MODERATOR, callback.getText());
  }

  /**
   * Tests that added a moderator fails if the desired moderator isn't in the group first.
   *
   * @throws ClassNotFoundException class not found exception
   * @throws NoSuchFieldException no such field exception
   * @throws IllegalAccessException illegal access exception
   */
  @Test
  public void testAddModerator2()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup hiGroup = new SlackGroup(900, 2, "hi", 100);
    Mockito.when(groupRepository.addGroup(any())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/createGroup hi"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group hi"));
    Mockito.when(groupRepository.getGroupByChannelId(anyInt())).thenReturn(hiGroup);
    List<String> mods = new ArrayList<>();
    mods.add("omar");
    List<String> members = new ArrayList<>();
    members.add("omar");
    Mockito.when(userGroupRepository.getModerators(Mockito.anyInt())).thenReturn(mods);
    Mockito.when(userGroupRepository.getGroupMembers(Mockito.anyInt())).thenReturn(members);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/addModerator milk"));
    waitingList1.remove();
    waitingList1.remove();
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.USER_NOT_IN_GROUP, callback.getText());
  }

  /**
   * Tests that added a moderator fails if the sender isn't a moderator.
   *
   * @throws ClassNotFoundException class not found exception
   * @throws NoSuchFieldException no such field exception
   * @throws IllegalAccessException illegal access exception
   */
  @Test
  public void testAddModeratorNotMod()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup hiGroup = new SlackGroup(900, 2, "hi", 100);
    Mockito.when(groupRepository.addGroup(any())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/createGroup hi"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group hi"));
    Mockito.when(groupRepository.getGroupByChannelId(anyInt())).thenReturn(hiGroup);
    List<String> mods = new ArrayList<>();
    mods.add("not_Omar");
    Mockito.when(userGroupRepository.getModerators(Mockito.anyInt())).thenReturn(mods);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/addModerator cheese"));
    waitingList1.remove();
    waitingList1.remove();
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.NOT_MODERATOR, callback.getText());
  }

  /**
   * Tests that added a moderator fails with a null group.
   *
   * @throws ClassNotFoundException class not found exception
   * @throws NoSuchFieldException no such field exception
   * @throws IllegalAccessException illegal access exception
   */
  @Test
  public void testAddModeratorNullGroup()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    List<String> mods = new ArrayList<>();
    mods.add("omar");
    List<String> members = new ArrayList<>();
    members.add("omar");
    Mockito.when(userGroupRepository.getModerators(Mockito.anyInt())).thenReturn(mods);
    Mockito.when(userGroupRepository.getGroupMembers(Mockito.anyInt())).thenReturn(members);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/addModerator milk"));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.NON_EXISTING_GROUP, callback.getText());
  }


  /**
   * Tests that added a moderator fails with no params.
   *
   * @throws ClassNotFoundException class not found exception
   * @throws NoSuchFieldException no such field exception
   * @throws IllegalAccessException illegal access exception
   */
  @Test
  public void testAddModeratorNoParams()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    List<String> mods = new ArrayList<>();
    mods.add("omar");
    List<String> members = new ArrayList<>();
    members.add("omar");
    Mockito.when(userGroupRepository.getModerators(Mockito.anyInt())).thenReturn(mods);
    Mockito.when(userGroupRepository.getGroupMembers(Mockito.anyInt())).thenReturn(members);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/addModerator"));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.INCORRECT_COMMAND_PARAMETERS, callback.getText());
  }

  /**
   * Tests that dm fails with a null group.
   *
   * @throws ClassNotFoundException class not found exception
   * @throws NoSuchFieldException no such field exception
   * @throws IllegalAccessException illegal access exception
   */
  @Test
  public void testDomNoGroup()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    List<String> mods = new ArrayList<>();
    mods.add("omar");
    Mockito.when(userGroupRepository.getModerators(Mockito.anyInt())).thenReturn(mods);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/dom"));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.NON_EXISTING_GROUP, callback.getText());
  }

  /**
   * Tests that added a dom fails if the sender isn't a moderator.
   *
   * @throws ClassNotFoundException class not found exception
   * @throws NoSuchFieldException no such field exception
   * @throws IllegalAccessException illegal access exception
   */
  @Test
  public void testDomFailsWhenNotMod()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup supGroup = new SlackGroup(900, 2, "sup", 100);
    Mockito.when(groupRepository.addGroup(any())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/createGroup sup"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group sup"));
    Mockito.when(groupRepository.getGroupByChannelId(anyInt())).thenReturn(supGroup);
    List<String> mods = new ArrayList<>();
    mods.add("not_omar");
    Mockito.when(userGroupRepository.getModerators(Mockito.anyInt())).thenReturn(mods);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/dom"));
    waitingList1.remove();
    waitingList1.remove();
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.NOT_MODERATOR, callback.getText());
  }

  /**
   * Tests that dom fails if there isn't another moderator.
   *
   * @throws ClassNotFoundException class not found exception
   * @throws NoSuchFieldException no such field exception
   * @throws IllegalAccessException illegal access exception
   */
  @Test
  public void testDomFailsWithNoOtherModerator()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup helloGroup = new SlackGroup(100, 2, "hello", 100);
    Mockito.when(groupRepository.addGroup(any())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/createGroup hello"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group hello"));
    Mockito.when(groupRepository.getGroupByChannelId(anyInt())).thenReturn(helloGroup);
    List<String> mods = new ArrayList<>();
    mods.add("omar");
    Mockito.when(userGroupRepository.getModerators(Mockito.anyInt())).thenReturn(mods);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/dom"));
    waitingList1.remove();
    waitingList1.remove();
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.ONLY_MODERATOR_FAILURE, callback.getText());
  }

  /**
   * Tests that dom works.
   *
   * @throws ClassNotFoundException class not found exception
   * @throws NoSuchFieldException no such field exception
   * @throws IllegalAccessException illegal access exception
   */
  @Test
  public void testDomWorks()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    SlackGroup helloGroup = new SlackGroup(100, 2, "hello", 100);
    Mockito.when(groupRepository.addGroup(any())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/createGroup hello"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group hello"));
    Mockito.when(groupRepository.getGroupByChannelId(anyInt())).thenReturn(helloGroup);
    List<String> mods = new ArrayList<>();
    mods.add("omar");
    mods.add("cowboy");
    Mockito.when(userGroupRepository.getModerators(Mockito.anyInt())).thenReturn(mods);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/dom "));
    waitingList1.remove();
    waitingList1.remove();
    Message callback = waitingList1.remove();
    assertEquals(String.format(CommandMessages.SUCCESSFUL_DOM, "omar"), callback.getText());
  }

  /**
   * Test command message null user.
   */
  @Test
  public void testCommandMessageNullUser() {
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(null);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/help"));
    Message callback = waitingList1.remove();
    assertEquals("User not recognized", callback.getText());
  }

  /**
   * Test command message user null user type.
   */
  @Test
  public void testCommandMessageUserNullUserType() {
    User user = new User(2, "omar", "pwd", null);
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/help"));
    Message callback = waitingList1.remove();
    assertEquals("User not recognized", callback.getText());
  }

  /**
   * Test wire tap general user.
   */
  @Test
  public void testWireTapGeneralUser() {
    User user = new User(2, "omar", "pwd", UserType.GENERAL);
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/wiretap "));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.COMMAND_NOT_RECOGNIZED, callback.getText());
  }


  /**
   * Test wire tap no params.
   */
  @Test
  public void testWireTapNoParams() {
    User user = new User(2, "omar", "pwd", UserType.GOVERNMENT);
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/wiretap"));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.INCORRECT_COMMAND_PARAMETERS, callback.getText());
  }


  /**
   * Test wire tap less params.
   */
  @Test
  public void testWireTapLessParams() {
    User user = new User(2, "omar", "pwd", UserType.GOVERNMENT);
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/wiretap h"));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.INCORRECT_COMMAND_PARAMETERS, callback.getText());
  }

  /**
   * Test wire tap user not found.
   */
  @Test
  public void testWireTapUserNotFound() {
    User user = new User(2, "omar", "pwd", UserType.GOVERNMENT);
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(null);
    Prattle.commandMessage(
        Message.makeCommandMessage("omar", 2, "/wiretap hiye 2018-04-21 2018-04-21"));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.NON_EXISTING_USER, callback.getText());
  }

  /**
   * Test wire tap user invalid date format.
   */
  @Test
  public void testWireTapUserInvalidDateFormat() {
    User user = new User(2, "omar", "pwd", UserType.GOVERNMENT);
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString()))
        .thenReturn(new User(1, "ian", "pwd", UserType.GENERAL));
    Prattle.commandMessage(
        Message.makeCommandMessage("omar", 2, "/wiretap hiye 2018-14-21 2018-04-21"));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.INCORRECT_DATE_FORMAT, callback.getText());
  }

  /**
   * Test wire tap user.
   */
  @Test
  public void testWireTapUser() {
    User user = new User(2, "omar", "pwd", UserType.GOVERNMENT);
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString()))
        .thenReturn(new User(1, "ian", "pwd", UserType.GENERAL));
    List<MessageHistory> messageHistory = new ArrayList<>();
    messageHistory.add(new MessageHistory("us1", MessageRecipientType.USER, "us2",
        MessageRecipientType.GROUP, "hey", Timestamp.valueOf("2018-04-21 00:00:00")));
    Mockito.when(messageRepository.getDirectMessageHistory(Mockito.anyInt(),
        Mockito.any(Timestamp.class), Mockito.any(Timestamp.class))).thenReturn(messageHistory);
    Mockito.when(messageRepository.getGroupMessageHistory(Mockito.anyInt(), Mockito.anyString(),
        Mockito.any(Timestamp.class), Mockito.any(Timestamp.class))).thenReturn(messageHistory);
    Prattle.commandMessage(
        Message.makeCommandMessage("omar", 2, "/wiretap ian 2018-04-21 2018-04-21"));
    Message callback = waitingList1.remove();
    assertEquals("Conversation history for ian:\n" +
        "2018-04-21 00:00:00 Group us2 sent User us1 : hey\n" +
        "2018-04-21 00:00:00 Group us2 sent User us1 : hey\n", callback.getText());
  }

  /**
   * Test no commands for user.
   */
  @Test
  public void testNoCommandsForUser() {
    User user = new User(2, "omar", "pwd", UserType.SYSTEM);
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);
    Prattle.commandMessage(
        Message.makeCommandMessage("omar", 2, "/help"));
    Message callback = waitingList1.remove();
    assertEquals("No commands available", callback.getText());
  }

  /**
   * Test help govt user.
   */
  @Test
  public void testHelpGovtUser() {
    User user = new User(2, "omar", "pwd", UserType.GOVERNMENT);
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/help"));
    Message callback = waitingList1.remove();
    assertEquals("Available COMMANDS:\n/wiretap Wiretap conversations of a user. Parameters: <handle> <startDate> <endDate> "
            + "(Date format:mm/dd/yyyy).\n" + "/help Lists all of the available commands.",
        callback.getText());
  }

  /**
   * Test dnd sucess.
   */
  @Test
  public void testDndSucess() {
    when(userRepository.setDNDStatus(anyInt(), Mockito.anyBoolean())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/dnd true"));
    Message callback = waitingList1.remove();
    assertEquals(String.format(CommandMessages.SUCCESSFUL_DND, "true"), callback.getText());
  }

  /**
   * Test dnd fail.
   */
  @Test
  public void testDndFail() {
    when(userRepository.setDNDStatus(anyInt(), Mockito.anyBoolean())).thenReturn(false);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/dnd true"));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.UNSUCCESSFUL_DND, callback.getText());
  }

  /**
   * Test dnd invalid params.
   */
  @Test
  public void testDndInvalidParams() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/dnd"));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.INCORRECT_COMMAND_PARAMETERS, callback.getText());
  }

  /**
   * Tests recall message
   */
  @Test
  public void testRecall() {
    Mockito.when(messageRepository.recallMessage(anyInt(), anyInt(), anyInt())).thenReturn(true);
    Prattle.commandMessage(Message.makeBroadcastMessage("omar", "hello"));
    Prattle.commandMessage(Message.makeBroadcastMessage("omar", "goodbye"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/recall 2"));
    Message callback = waitingList2.remove();
    assertEquals(CommandMessages.SUCCESSFUL_RECALL, callback.getText());
  }

  /**
   * Tests recall message with no given message number
   */
  @Test
  public void testRecallNoMessage() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/recall"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.INCORRECT_COMMAND_PARAMETERS, callback.getText());
  }

  /**
   * Tests recall message with a negative message number
   */
  @Test
  public void testRecallNegativeMessageNumber() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/recall -8"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NON_POSITIVE_MESSAGE_NUMBER, callback.getText());
  }


  /**
   * Tests recall message with a zero message number
   */
  @Test
  public void testRecallZeroMessageNumber() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/recall 0"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NON_POSITIVE_MESSAGE_NUMBER, callback.getText());
  }

  /**
   * Tests recall message with a nonexistent message
   */
  @Test
  public void testRecallMessageNumberIsTooHigh() {
    Mockito.when(messageRepository.recallMessage(anyInt(), anyInt(), anyInt())).thenReturn(false);
    Prattle.commandMessage(Message.makeBroadcastMessage("omar", "boop"));
    Prattle.commandMessage(Message.makeBroadcastMessage("omar", "bap"));
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/recall 19"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NOT_ENOUGH_MESSAGES, callback.getText());
  }

  /**
   * Tests kick throws an exception while checking for a moderator
   */
  @Test
  public void testKickException() throws SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    SlackGroup group = new SlackGroup(1, "hiGroup", null);
    Mockito.when(groupRepository.getGroupByChannelId(anyInt())).thenReturn(group);
    Mockito.when(userGroupRepository.isModerator(anyInt(), anyInt())).thenThrow(new SQLException());
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/kick sean"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.GENERIC_ERROR, callback.getText());
  }

  /**
   * Tests inviting to a group with a given group name, which is null
   */
  @Test
  public void testGroupInviteParamsLength2AndNullGroup() {
    groupRepository = Mockito.mock(GroupRepository.class);
    SlackGroup group = new SlackGroup(1, "hiGroup", null);
    Mockito.when(groupRepository.getGroupByName(anyString())).thenReturn(group);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/invite sean hiGroup"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NON_EXISTING_GROUP, callback.getText());
  }

  /**
   * Tests inviting to a group with too many params
   */
  @Test
  public void testGroupInviteParamsLength3() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/invite sean oops threeParams"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.COMMAND_NOT_RECOGNIZED, callback.getText());
  }

  /**
   * Tests inviting to a group without being a moderator
   */
  @Test
  public void testGroupInviteFailedModeratorCheck() throws SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    SlackGroup group = new SlackGroup(1, "byeGroup", null);
    Mockito.when(groupRepository.getGroupByChannelId(anyInt())).thenReturn(group);
    Mockito.when(userGroupRepository.isModerator(anyInt(), anyInt())).thenThrow(new SQLException());
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/invite koka"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.GENERIC_ERROR, callback.getText());
  }

  /**
   * Tests inviting to a group where the desired user is null
   */
  @Test
  public void testGroupInviteNullUser() throws SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    SlackGroup group = new SlackGroup(1, "heyGroup", null);
    Mockito.when(groupRepository.getGroupByChannelId(anyInt())).thenReturn(group);
    Mockito.when(userGroupRepository.isModerator(anyInt(), anyInt())).thenReturn(true);
    Mockito.when(userRepository.getUserByUserName(anyString())).thenReturn(null);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/invite arya"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.NON_EXISTING_USER, callback.getText());
  }

  /**
   * Tests inviting to a group fails when they're already invited
   */
  @Test
  public void testGroupInviteDuplicateInvite() throws SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    SlackGroup group = new SlackGroup(1, "sweetGroup", null);
    Mockito.when(groupRepository.getGroupByChannelId(anyInt())).thenReturn(group);
    Mockito.when(userGroupRepository.isModerator(anyInt(), anyInt())).thenReturn(true);
    Mockito.when(userRepository.getUserByUserName(anyString())).thenReturn(omar);
    SQLException e = Mockito.mock(SQLException.class);
    Mockito.when(e.getErrorCode()).thenReturn(ErrorCodes.MYSQL_DUPLICATE_PK);
    Mockito.when(groupInviteRepository.add(any())).thenThrow(e);
    Prattle.commandMessage(Message.makeCommandMessage("koka", 1, "/invite emjed"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.COMMAND_ALREADY_PROCESSED, callback.getText());
  }

  /**
   * Tests inviting to a group fails when an execption is thrown
   */
  @Test
  public void testGroupInviteException() throws SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    SlackGroup group = new SlackGroup(1, "tomatoes", null);
    Mockito.when(groupRepository.getGroupByChannelId(anyInt())).thenReturn(group);
    Mockito.when(userGroupRepository.isModerator(anyInt(), anyInt())).thenReturn(true);
    Mockito.when(userRepository.getUserByUserName(anyString())).thenReturn(omar);
    Mockito.when(groupInviteRepository.add(any())).thenThrow(new SQLException());
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/invite mister"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.UNSUCCESSFUL_INVITE, callback.getText());
  }

  /**
   * Tests that dm fails without a given user
   */
  @Test
  public void testDmNullParam() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/dm"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.INCORRECT_COMMAND_PARAMETERS, callback.getText());
  }

  /**
   * Tests get latest messages from channel
   */
  @Test
  public void testGroupMessagesQueue() {
    groupRepository = Mockito.mock(GroupRepository.class);
    SlackGroup group = new SlackGroup(1, "misterGroup", null);
    Mockito.when(groupRepository.getGroupByName(anyString())).thenReturn(group);
    Mockito.when(groupRepository.groupHasMember(anyInt(), anyInt())).thenReturn(true);
    List<Message> listOfMessages = new ArrayList<>();
    Message m1 = Mockito.mock(Message.class);
    Mockito.when(m1.getText()).thenReturn("text");
    Mockito.when(m1.getName()).thenReturn("omar");
    listOfMessages.add(m1);
    Mockito.when(messageRepository.getLatestMessagesFromChannel(anyInt(), anyInt()))
        .thenReturn(listOfMessages);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/group general"));
    Message callback = waitingList2.remove();
    assertEquals("Active channel set to Group general\nomar : text\n-------------------------",
        callback.getText());
  }

  /**
   * Tests that message history works
   */
  @Test
  public void testMessageHistory() {
    MessageHistory mh = new MessageHistory("receiver", MessageRecipientType.USER, "sender",
        MessageRecipientType.GROUP, "text", Timestamp.valueOf(LocalDateTime.now()));
    assertEquals("receiver", mh.getReceiverName());
    assertEquals("User", mh.getReceiver().getValue());
    assertEquals("sender", mh.getSenderName());
    assertEquals("Group", mh.getSender().getValue());
    assertEquals("text", mh.getText());
  }

  @Test
  public void testSearchUsersSuccess() {
    List<String> userNames = new ArrayList<>();
    userNames.add("poker");
    when(userRepository.searchUsersBySearchTerm(anyString())).thenReturn(userNames);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/search p"));
    Message callback = waitingList1.remove();
    assertEquals("Users with similar names are:\npoker" , callback.getText());
  }

  @Test
  public void testSearchUsersInvalidParams() {
    List<String> userNames = new ArrayList<>();
    userNames.add("poker");
    when(userRepository.searchUsersBySearchTerm(anyString())).thenReturn(userNames);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/search"));
    Message callback = waitingList1.remove();
    assertEquals("Please enter a search term to find similar usernames" , callback.getText());
  }

  @Test
  public void testSearchUsersNoUsersFound() {
    List<String> userNames = new ArrayList<>();
    when(userRepository.searchUsersBySearchTerm(anyString())).thenReturn(userNames);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/search p"));
    Message callback = waitingList1.remove();
    assertEquals("No users found" , callback.getText());
  }
  

  @Test
  public void testEightySixSuccess() throws SQLException {
    Prattle.changeClientChannel(1, cr2);
    Prattle.changeClientChannel(2, cr1);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/86"));
    Message callback1 = waitingList1.remove();
    Message callback2 = waitingList1.remove();
    assertEquals(String.format(EIGHTY_SIX_NOTIFICATION, "group2", "omar"), callback1.getText());
    assertEquals(EIGHTY_SIX_SUCCESS, callback2.getText());
    assertEquals(1, cr2.getActiveChannelId());
    assertEquals(0, waitingList2.size());
  }

  @Test
  public void testEightySixSuccessHandlesMembers() throws SQLException {
    Prattle.changeClientChannel(2, cr2);
    Prattle.changeClientChannel(2, cr1);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/86"));
    Message callback = waitingList2.remove();
    assertEquals(String.format(EIGHTY_SIX_NOTIFICATION, "group2", "omar"), callback.getText());
    assertEquals(1, cr2.getActiveChannelId());
    assertEquals(1, cr1.getActiveChannelId());
  }

  @Test
  public void testEightySixNotModerator() throws SQLException {
    Prattle.changeClientChannel(2, cr1);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(false);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/86"));
    Message callback = waitingList1.remove();
    assertEquals(NOT_MODERATOR, callback.getText());
    assertEquals(2, cr1.getActiveChannelId());
  }

  @Test
  public void testEightySixExceptionModerator() throws SQLException {
    Prattle.changeClientChannel(2, cr1);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(), Mockito.anyInt()))
        .thenThrow(new SQLException());
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/86"));
    Message callback = waitingList1.remove();
    assertEquals(GENERIC_ERROR, callback.getText());
    assertEquals(2, cr1.getActiveChannelId());
  }

  @Test
  public void testNullGroup() throws SQLException {
    cr1.setActiveChannelId(-1);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/86"));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.NON_EXISTING_GROUP, callback.getText());
  }

  @Test
  public void testFailedDelete()
      throws IllegalAccessException, ClassNotFoundException, NoSuchFieldException, SQLException {
    groupRepository = Mockito.mock(GroupRepository.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("groupRepository");
    gr.setAccessible(true);
    gr.set(null, groupRepository);
    Mockito.when(groupRepository.deleteGroup(Mockito.anyInt(), Mockito.anyInt())).thenReturn(false);
    Mockito.when(userGroupRepository.isModerator(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/86"));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.NON_EXISTING_GROUP, callback.getText());
  }

  /**
   * Tests  /lang command.
   */
  @Test
  public void testAvailablesLanguagesToTranslate()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    TranslationSupport translationSupport = Mockito.mock(TranslationSupport.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.Languages")
        .getDeclaredField("translationSupport");
    gr.setAccessible(true);
    gr.set(null, translationSupport);
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
  public void testNoTargetLanguageToTranslate() {
    Prattle.commandMessage(Message.makeCommandMessage("josh", 1, "/translate"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.INCORRECT_COMMAND_PARAMETERS, callback.getText());
  }

  /**
   * Tests translate command when no text is given to translate.
   */
  @Test
  public void testNoTextProvidedToTranslate()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    TranslationSupport translationSupport = Mockito.mock(TranslationSupport.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.Translate")
        .getDeclaredField("translationSupport");
    gr.setAccessible(true);
    gr.set(null, translationSupport);
    Mockito.when(translationSupport.isLanguageSupported(Mockito.anyString())).thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("josh", 1, "/translate spanish"));
    Message callback = waitingList2.remove();
    assertEquals(ErrorMessages.INCORRECT_COMMAND_PARAMETERS, callback.getText());
  }

  /**
   * Test's translate command.
   */
  @Test
  public void testTranslate()
      throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    TranslationSupport translationSupport = Mockito.mock(TranslationSupport.class);
    Field gr = Class.forName("edu.northeastern.ccs.im.server.commands.Translate")
        .getDeclaredField("translationSupport");
    gr.setAccessible(true);
    gr.set(null, translationSupport);
    String translatedText = "Hola";
    Mockito.when(translationSupport.isLanguageSupported(Mockito.anyString())).thenReturn(true);
    when(translationSupport.translateTextToGivenLanguage(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(translatedText);
    Prattle.commandMessage(Message.makeCommandMessage("josh", 1, "/translate spanish hello"));
    Message callback = waitingList2.remove();
    assertEquals("Hola", callback.getText());
  }

  @Test
  public void testAcceptLockedGroupInvite() throws SQLException {
    Mockito.when(groupInviteRepository.acceptInvite(Mockito.anyInt(), Mockito.anyInt()))
        .thenReturn(true);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/accept groupWithPass"));
    Message callback = waitingList1.remove();
    assertTrue(callback.getText().contains("Join group with password: password"));
  }

  @Test
  public void testCreateLockedGroup() {
    SlackGroup group = new SlackGroup(4, -1, "aGroup", 1, false, "pass");
    groupRepository.addGroup(group);
    assertNotNull(groupRepository.getGroupByName("aGroup").getPassword());
  }

  @Test
  public void testJoinLockedGroupSuccess() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group groupWithPass password"));
    assertEquals(3, cr1.getActiveChannelId());
  }

  @Test
  public void testJoinLockedGroupNoPass() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group groupWithPass"));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.PASSWRD_REQURIED, callback.getText());
    assertEquals(1, cr1.getActiveChannelId());
  }

  @Test
  public void testJoinLockedGroupWrongPass() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "/group groupWithPass pasword"));
    Message callback = waitingList1.remove();
    assertEquals(ErrorMessages.INCORRECT_PASSWRD, callback.getText());
    assertEquals(1, cr1.getActiveChannelId());
  }
}
