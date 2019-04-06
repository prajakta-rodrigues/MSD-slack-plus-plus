package prattleTests;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import edu.northeastern.ccs.im.server.Notification;
import edu.northeastern.ccs.im.server.NotificationConvertor;
import edu.northeastern.ccs.im.server.NotificationType;
import edu.northeastern.ccs.im.server.SlackGroup;
import edu.northeastern.ccs.im.server.User;
import edu.northeastern.ccs.im.server.repositories.GroupRepository;
import edu.northeastern.ccs.im.server.repositories.UserRepository;

/**
 * The Class NotificationConvertorTest.
 */
public class NotificationConvertorTest {


  /**
   * Test get notifications as text no new unread messages.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testGetNotificationsAsTextNoNewUnreadMessages() throws NoSuchFieldException,
      SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    Field userRepoField = Class.forName("edu.northeastern.ccs.im.server.NotificationConvertor")
        .getDeclaredField("userRepository");
    userRepoField.setAccessible(true);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    userRepoField.set(null, userRepository);
    User user = new User(2, "testY", "pwd");
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);

    Field groupRepoField = Class.forName("edu.northeastern.ccs.im.server.NotificationConvertor")
        .getDeclaredField("groupRepository");
    groupRepoField.setAccessible(true);
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    groupRepoField.set(null, groupRepository);

    List<Notification> listNotifications = new ArrayList<>();
    Notification n;
    n = new Notification();
    n.setId(1);
    n.setRecieverId(1);
    n.setType(NotificationType.FRIEND_REQUEST_APPROVED);
    n.setAssociatedUserId(2);
    listNotifications.add(n);

    for (int i = 0; i < 3; i++) {
      n = new Notification();
      n.setId(i + 1);
      n.setRecieverId(1);
      n.setAssociatedUserId(2);
      n.setType(NotificationType.UNREAD_MESSAGES);
      listNotifications.add(n);
    }

    for (int i = 0; i < 3; i++) {
      n = new Notification();
      n.setId(i + 1);
      n.setRecieverId(1);
      n.setAssociatedGroupId(4);
      n.setType(NotificationType.UNREAD_MESSAGES);
      listNotifications.add(n);
    }
    assertEquals("testY has accepted your friend request.\n",
        NotificationConvertor.getNotificationsAsText(listNotifications));
  }

  /**
   * Test get notifications as text new unread messages.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testGetNotificationsAsTextNewUnreadMessages() throws NoSuchFieldException,
      SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    Field userRepoField = Class.forName("edu.northeastern.ccs.im.server.NotificationConvertor")
        .getDeclaredField("userRepository");
    userRepoField.setAccessible(true);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    userRepoField.set(null, userRepository);
    User user = new User(2, "testY", "pwd");
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);

    Field groupRepoField = Class.forName("edu.northeastern.ccs.im.server.NotificationConvertor")
        .getDeclaredField("groupRepository");
    groupRepoField.setAccessible(true);
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    groupRepoField.set(null, groupRepository);
    SlackGroup group = new SlackGroup(1, 1, "gou", 3);
    Mockito.when(groupRepository.getGroupById(Mockito.anyInt())).thenReturn(group);

    List<Notification> listNotifications = new ArrayList<>();
    Notification n;
    n = new Notification();
    n.setId(1);
    n.setRecieverId(1);
    n.setType(NotificationType.FRIEND_REQUEST_APPROVED);
    n.setAssociatedUserId(2);
    n.setNew(true);
    listNotifications.add(n);

    for (int i = 0; i < 3; i++) {
      n = new Notification();
      n.setId(i + 1);
      n.setRecieverId(1);
      n.setAssociatedUserId(2);
      n.setNew(true);
      n.setType(NotificationType.UNREAD_MESSAGES);
      listNotifications.add(n);
    }

    for (int i = 0; i < 3; i++) {
      n = new Notification();
      n.setId(i + 1);
      n.setRecieverId(1);
      n.setAssociatedGroupId(4);
      n.setNew(true);
      n.setType(NotificationType.UNREAD_MESSAGES);
      listNotifications.add(n);
    }
    assertEquals(
        "testY has accepted your friend request.  NEW\n"
            + "You have 3 unread messages from User testY  NEW\n"
            + "You have 3 unread messages from Group gou  NEW\n",
        NotificationConvertor.getNotificationsAsText(listNotifications));
  }

  /**
   * Test get notifications as text exception.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws SecurityException the security exception
   * @throws ClassNotFoundException the class not found exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Test
  public void testGetNotificationsAsTextException() throws NoSuchFieldException, SecurityException,
      ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    Field userRepoField = Class.forName("edu.northeastern.ccs.im.server.NotificationConvertor")
        .getDeclaredField("userRepository");
    userRepoField.setAccessible(true);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    userRepoField.set(null, userRepository);
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt()))
        .thenThrow(new IllegalArgumentException());
    List<Notification> listNotifications = new ArrayList<>();
    Notification n;
    n = new Notification();
    n.setId(1);
    n.setRecieverId(1);
    n.setType(NotificationType.FRIEND_REQUEST_APPROVED);
    n.setAssociatedUserId(2);
    listNotifications.add(n);
    assertEquals("Error while fetching notifications",
        NotificationConvertor.getNotificationsAsText(listNotifications));

  }


  @Test
  public void testGetNotificationsAsTextNewGroupInviteMessages() throws NoSuchFieldException,
      SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    Field userRepoField = Class.forName("edu.northeastern.ccs.im.server.NotificationConvertor")
        .getDeclaredField("userRepository");
    userRepoField.setAccessible(true);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    userRepoField.set(null, userRepository);
    User user = new User(2, "testY", "pwd");
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);

    Field groupRepoField = Class.forName("edu.northeastern.ccs.im.server.NotificationConvertor")
        .getDeclaredField("groupRepository");
    groupRepoField.setAccessible(true);
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    groupRepoField.set(null, groupRepository);

    List<Notification> listNotifications = new ArrayList<>();
    Notification n;
    n = new Notification();
    n.setId(1);
    n.setRecieverId(1);
    n.setType(NotificationType.GROUP_INVITE);
    n.setAssociatedUserId(2);
    n.setAssociatedGroupId(3);
    n.setNew(true);

    listNotifications.add(n);
    SlackGroup group = new SlackGroup(1, 1, "gpp", 1);
    Mockito.when(groupRepository.getGroupById(Mockito.anyInt())).thenReturn(group);

    assertEquals("You have been invited to group gpp by moderator testY  NEW\n"
        , NotificationConvertor.getNotificationsAsText(listNotifications));
  }


  @Test
  public void testGetNotificationsAsTextNotNewGroupInviteMessages() throws NoSuchFieldException,
      SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    Field userRepoField = Class.forName("edu.northeastern.ccs.im.server.NotificationConvertor")
        .getDeclaredField("userRepository");
    userRepoField.setAccessible(true);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    userRepoField.set(null, userRepository);
    User user = new User(2, "testY", "pwd");
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);

    Field groupRepoField = Class.forName("edu.northeastern.ccs.im.server.NotificationConvertor")
        .getDeclaredField("groupRepository");
    groupRepoField.setAccessible(true);
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    groupRepoField.set(null, groupRepository);

    List<Notification> listNotifications = new ArrayList<>();
    Notification n;
    n = new Notification();
    n.setId(1);
    n.setRecieverId(1);
    n.setType(NotificationType.GROUP_INVITE);
    n.setAssociatedUserId(2);
    n.setAssociatedGroupId(3);
    n.setNew(false);

    listNotifications.add(n);
    SlackGroup group = new SlackGroup(1, 1, "gpp", 1);
    Mockito.when(groupRepository.getGroupById(Mockito.anyInt())).thenReturn(group);

    assertEquals("You have been invited to group gpp by moderator testY\n"
        , NotificationConvertor.getNotificationsAsText(listNotifications));
  }

  @Test
  public void testGetTextForNewModerator() throws NoSuchFieldException,
      SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    Field userRepoField = Class.forName("edu.northeastern.ccs.im.server.NotificationConvertor")
        .getDeclaredField("userRepository");
    userRepoField.setAccessible(true);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    userRepoField.set(null, userRepository);
    User user = new User(2, "abcd", "efgh");
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);

    Field groupRepoField = Class.forName("edu.northeastern.ccs.im.server.NotificationConvertor")
        .getDeclaredField("groupRepository");
    groupRepoField.setAccessible(true);
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    groupRepoField.set(null, groupRepository);

    List<Notification> listNotifications = new ArrayList<>();
    Notification n;
    n = new Notification();
    n.setId(1);
    n.setRecieverId(1);
    n.setType(NotificationType.NEW_MODERATOR);
    n.setAssociatedUserId(2);
    n.setAssociatedGroupId(3);
    n.setNew(true);

    listNotifications.add(n);
    SlackGroup group = new SlackGroup(1, 1, "groupABCD", 100);
    Mockito.when(groupRepository.getGroupById(Mockito.anyInt())).thenReturn(group);

    assertEquals("abcd added you as a moderator for group groupABCD  NEW\n"
        , NotificationConvertor.getNotificationsAsText(listNotifications));
  }

  @Test
  public void testGetTextForModNotNew() throws NoSuchFieldException,
      SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    Field userRepoField = Class.forName("edu.northeastern.ccs.im.server.NotificationConvertor")
        .getDeclaredField("userRepository");
    userRepoField.setAccessible(true);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    userRepoField.set(null, userRepository);
    User user = new User(2, "mike", "pass");
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);

    Field groupRepoField = Class.forName("edu.northeastern.ccs.im.server.NotificationConvertor")
        .getDeclaredField("groupRepository");
    groupRepoField.setAccessible(true);
    GroupRepository groupRepository = Mockito.mock(GroupRepository.class);
    groupRepoField.set(null, groupRepository);

    List<Notification> listNotifications = new ArrayList<>();
    Notification n;
    n = new Notification();
    n.setId(1);
    n.setRecieverId(1);
    n.setType(NotificationType.NEW_MODERATOR);
    n.setAssociatedUserId(2);
    n.setAssociatedGroupId(3);
    n.setNew(false);

    listNotifications.add(n);
    SlackGroup group = new SlackGroup(1, 1, "helloSir", 100);
    Mockito.when(groupRepository.getGroupById(Mockito.anyInt())).thenReturn(group);

    assertEquals("mike added you as a moderator for group helloSir\n"
        , NotificationConvertor.getNotificationsAsText(listNotifications));
  }

  @Test
  public void testGetTextForFriendRequestNotNew() throws NoSuchFieldException,
      SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
    Field userRepoField = Class.forName("edu.northeastern.ccs.im.server.NotificationConvertor")
        .getDeclaredField("userRepository");
    userRepoField.setAccessible(true);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    userRepoField.set(null, userRepository);
    User user = new User(2, "mike", "pass");
    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(user);

    List<Notification> listNotifications = new ArrayList<>();
    Notification n;
    n = new Notification();
    n.setId(1);
    n.setRecieverId(1);
    n.setType(NotificationType.FRIEND_REQUEST);
    n.setAssociatedUserId(2);
    n.setAssociatedGroupId(3);
    n.setNew(false);

    listNotifications.add(n);

    assertEquals("mike has sent you a friend request.\n"
        , NotificationConvertor.getNotificationsAsText(listNotifications));
  }
}
