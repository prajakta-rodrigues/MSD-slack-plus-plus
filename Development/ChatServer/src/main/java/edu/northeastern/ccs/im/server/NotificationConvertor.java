package edu.northeastern.ccs.im.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.stringtemplate.StringTemplate;
import edu.northeastern.ccs.im.server.repositories.GroupRepository;
import edu.northeastern.ccs.im.server.repositories.UserRepository;
import edu.northeastern.ccs.im.server.utility.DatabaseConnection;

public class NotificationConvertor {

  private static UserRepository userRepository;
  private static GroupRepository groupRepository;
  static final Logger LOGGER = Logger.getLogger(NotificationConvertor.class.getName());

  static {
    userRepository = new UserRepository(DatabaseConnection.getDataSource());
    groupRepository = new GroupRepository(DatabaseConnection.getDataSource());
  }
  
  public static String getNotificationsAsText(List<Notification> listNotifications) {
    StringBuilder result = new StringBuilder();
    try {

      User user = null;
      SlackGroup group = null;
      Map<String, Integer> userMessages = new HashMap<>();
      Map<String, Integer> groupMessages = new HashMap<>();
      for (Notification notification : listNotifications) {
        switch (notification.getType()) {
          case FRIEND_REQUEST:
            result.append(getTextForFriendRequest(notification.getAssociatedUserId(), 
                notification.isNew()));
            result.append("\n");
            break;
          case FRIEND_REQUEST_APPROVED:
            result.append(getTextForFriendRequestApproval(notification.getAssociatedUserId(), 
                notification.isNew()));
            result.append("\n");
            break;
          case UNREAD_MESSAGES:
            if (0 != notification.getAssociatedUserId() && notification.isNew()) {
              user = userRepository.getUserByUserId(notification.getAssociatedUserId());
              updateMap(userMessages, user.getUserName());
            } else if (0 != notification.getAssociatedGroupId() && notification.isNew()) {
              group = groupRepository.getGroupById(notification.getAssociatedGroupId());
              updateMap(groupMessages, group.getGroupName());
            }
            break;
          default:
            break;
        }
      }

      String userMsgNotifications =
          countAssociatedFromMap(userMessages, true, NotificationType.UNREAD_MESSAGES, "User");
      String groupMsgNotifications =
          countAssociatedFromMap(groupMessages, true, NotificationType.UNREAD_MESSAGES, "Group");

      result.append(userMsgNotifications);
      result.append(groupMsgNotifications);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      result.append("Error while fetching notifications");
    }
    return result.toString();
  }

  private static String getTextForFriendRequestApproval(int associatedUserId, boolean isNew) {
    User user = userRepository.getUserByUserId(associatedUserId);
    StringTemplate stringTemplate = NotificationType.FRIEND_REQUEST_APPROVED.getText();
    stringTemplate.removeAttribute("name");
    stringTemplate.setAttribute("name", user.getUserName());
    StringBuilder result = new StringBuilder();
    result.append(stringTemplate.toString());
    if (isNew) {
      result.append("  " + "NEW");
    }
    return result.toString();
  }

  private static String getTextForFriendRequest(int associatedUserId, boolean isNew) {
    User user = userRepository.getUserByUserId(associatedUserId);
    StringTemplate stringTemplate = NotificationType.FRIEND_REQUEST.getText();
    stringTemplate.removeAttribute("name");
    stringTemplate.setAttribute("name", user.getUserName());
    StringBuilder result = new StringBuilder();
    result .append(stringTemplate.toString());
    if (isNew) {
      result.append("  " + "NEW");
    }
    return result.toString();
  }

  private static String countAssociatedFromMap(Map<String, Integer> associatedMessages,
      boolean isNew, NotificationType unreadMessages, String senderType) {
    StringTemplate stringTemplate = null;
    StringBuilder result = new StringBuilder();
    for (Entry<String, Integer> entrySet : associatedMessages.entrySet()) {
      stringTemplate = unreadMessages.getText();
      stringTemplate.removeAttribute("count");
      stringTemplate.setAttribute("count", entrySet.getValue());
      stringTemplate.removeAttribute("name");
      stringTemplate.setAttribute("name", senderType + " " + entrySet.getKey());
      if (isNew) {
        result.append(stringTemplate.toString() + "  " + "NEW");
      }
      result.append("\n");
    }
    return result.toString();
  }

  private static void updateMap(Map<String, Integer> map, String userName) {
    if (map.containsKey(userName)) {
      map.put(userName, map.get(userName) + 1);
    } else {
      map.put(userName, 1);
    }
  }

}
