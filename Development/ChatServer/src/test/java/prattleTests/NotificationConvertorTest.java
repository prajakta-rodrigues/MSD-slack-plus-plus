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

}
