package edu.northeastern.ccs.im.server.models;

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

import static edu.northeastern.ccs.im.server.constants.StringConstants.NotificationMessages.DELETED_GROUP_TAG;
import static edu.northeastern.ccs.im.server.constants.StringConstants.NotificationMessages.GROUP_STRING;
import static edu.northeastern.ccs.im.server.constants.StringConstants.NotificationMessages.NAME_STRING;

/**
 * The Class NotificationConvertor provides methods to convert notifications their into text
 * representation.
 */
public class NotificationConvertor {

  private NotificationConvertor(){}

  /**
   * The user repository.
   */
  private static UserRepository userRepository;

  /**
   * The group repository.
   */
  private static GroupRepository groupRepository;

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(NotificationConvertor.class.getName());

  static {
    userRepository = new UserRepository(DatabaseConnection.getDataSource());
    groupRepository = new GroupRepository(DatabaseConnection.getDataSource());
  }

  /**
   * Gets the list notifications as their text representation.
   *
   * @param listNotifications the list notifications
   * @return the notifications as text
   */
  public static String getNotificationsAsText(List<Notification> listNotifications) {
    StringBuilder result = new StringBuilder();
    try {

      User user;
      SlackGroup group;
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
              updateMap(groupMessages, getGroupName(group));
            }
            break;
          case GROUP_INVITE:
            result.append(getTextForGroupInvite(notification.getAssociatedGroupId(),
                notification.getAssociatedUserId(), notification.isNew()));
            result.append("\n");
            break;
          case NEW_MODERATOR:
            result.append(getTextForNewModerator(notification.getAssociatedGroupId(),
                notification.getAssociatedUserId(), notification.isNew()));
            result.append("\n");
            break;
          case EIGHTY_SIX:
            result.append(getTextForEightySix(notification.getAssociatedGroupId(), notification.getAssociatedUserId(), notification.isNew()));
            result.append("\n");
            break;
          default:
            break;
        }
      }

      String userMsgNotifications =
          countAssociatedFromMap(userMessages, "User");
      String groupMsgNotifications =
          countAssociatedFromMap(groupMessages, "Group");

      result.append(userMsgNotifications);
      result.append(groupMsgNotifications);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      result.append("Error while fetching notifications");
    }
    return result.toString();
  }

  /**
   * Gets the text for group invite.
   *
   * @param associatedGroupId the associated group id for the notification
   * @param associatedUserId the associated user id for the notification
   * @param isNew the if the notification is new
   * @return the text for group invite
   */
  private static String getTextForGroupInvite(int associatedGroupId, int associatedUserId,
      boolean isNew) {
    User user = userRepository.getUserByUserId(associatedUserId);
    SlackGroup group = groupRepository.getGroupById(associatedGroupId);

    StringTemplate stringTemplate = NotificationType.GROUP_INVITE.getText();
    stringTemplate.removeAttribute(NAME_STRING);
    stringTemplate.removeAttribute(GROUP_STRING);
    stringTemplate.setAttribute(NAME_STRING, user.getUserName());
    stringTemplate.setAttribute(GROUP_STRING, getGroupName(group));
    StringBuilder result = new StringBuilder();
    result.append(stringTemplate.toString());
    if (isNew) {
      result.append("  " + "NEW");
    }
    return result.toString();
  }

  /**
   * Gets the text for friend request approval.
   *
   * @param associatedUserId the associated user id
   * @param isNew the is new
   * @return the text for friend request approval
   */
  private static String getTextForFriendRequestApproval(int associatedUserId, boolean isNew) {
    User user = userRepository.getUserByUserId(associatedUserId);
    StringTemplate stringTemplate = NotificationType.FRIEND_REQUEST_APPROVED.getText();
    stringTemplate.removeAttribute(NAME_STRING);
    stringTemplate.setAttribute(NAME_STRING, user.getUserName());
    StringBuilder result = new StringBuilder();
    result.append(stringTemplate.toString());
    if (isNew) {
      result.append("  " + "NEW");
    }
    return result.toString();
  }

  /**
   * Gets the text for friend request.
   *
   * @param associatedUserId the associated user id
   * @param isNew the is new
   * @return the text for friend request
   */
  private static String getTextForFriendRequest(int associatedUserId, boolean isNew) {
    User user = userRepository.getUserByUserId(associatedUserId);
    StringTemplate stringTemplate = NotificationType.FRIEND_REQUEST.getText();
    stringTemplate.removeAttribute(NAME_STRING);
    stringTemplate.setAttribute(NAME_STRING, user.getUserName());
    StringBuilder result = new StringBuilder();
    result.append(stringTemplate.toString());
    if (isNew) {
      result.append("  " + "NEW");
    }
    return result.toString();
  }

  /**
   * Gets the text for new moderator invite.
   *
   * @param associatedGroupId the associate group id
   * @param associatedUserId the associated user id
   * @param isNew the is new
   * @return the text for the new moderator notification
   */
  private static String getTextForNewModerator(int associatedGroupId, int associatedUserId,
      boolean isNew) {
    User user = userRepository.getUserByUserId(associatedUserId);
    SlackGroup group = groupRepository.getGroupById(associatedGroupId);
    StringTemplate stringTemplate = NotificationType.NEW_MODERATOR.getText();
    stringTemplate.removeAttribute(NAME_STRING);
    stringTemplate.removeAttribute(GROUP_STRING);
    stringTemplate.setAttribute(NAME_STRING, user.getUserName());
    stringTemplate.setAttribute(GROUP_STRING, getGroupName(group));
    StringBuilder result = new StringBuilder();
    result.append(stringTemplate.toString());
    if (isNew) {
      result.append("  " + "NEW");
    }
    return result.toString();
  }

  /**
   * Count associated notifications from map and given resultant string representation.
   *
   * @param associatedMessages the associated messages
   * @param senderType the sender type
   * @return the string
   */
  private static String countAssociatedFromMap(Map<String, Integer> associatedMessages,
      String senderType) {
    StringTemplate stringTemplate;
    StringBuilder result = new StringBuilder();
    String countString = "count";
    for (Entry<String, Integer> entrySet : associatedMessages.entrySet()) {
      stringTemplate = NotificationType.UNREAD_MESSAGES.getText();
      stringTemplate.removeAttribute(countString);
      stringTemplate.setAttribute(countString, entrySet.getValue());
      stringTemplate.removeAttribute(NAME_STRING);
      stringTemplate.setAttribute(NAME_STRING, senderType + " " + entrySet.getKey());
      result.append(stringTemplate.toString());
      result.append("  NEW");

      result.append("\n");
    }
    return result.toString();
  }

  /**
   * Gets the text for a notification alerting of a group termination.
   *
   * @param associatedGroupId the associate group id
   * @param associatedUserId the associated user id
   * @param isNew the is new
   * @return the text for the 86 notification
   */
  private static String getTextForEightySix(int associatedGroupId, int associatedUserId, boolean isNew) {
    User user = userRepository.getUserByUserId(associatedUserId);
    SlackGroup group = groupRepository.getGroupById(associatedGroupId);
    StringTemplate stringTemplate = NotificationType.EIGHTY_SIX.getText();
    stringTemplate.removeAttribute("name");
    stringTemplate.removeAttribute("group");
    stringTemplate.setAttribute("name", user.getUserName());
    stringTemplate.setAttribute("group", getGroupName(group));
    StringBuilder result = new StringBuilder();
    result.append(stringTemplate.toString());
    if (isNew) {
      result.append("  " + "NEW");
    }
    return result.toString();
  }

  /**
   * Update map with given userName.
   *
   * @param map the map
   * @param userName the user name
   */
  private static void updateMap(Map<String, Integer> map, String userName) {
    if (map.containsKey(userName)) {
      map.put(userName, map.get(userName) + 1);
    } else {
      map.put(userName, 1);
    }
  }

  /**
   * Adds deleted tag next to the group name if applicable.
   * @param group the Group being referenced in the notification
   * @return The Group name with a tag.
   */
  private static String getGroupName(SlackGroup group) {
    StringBuilder groupName = new StringBuilder(group.getGroupName());
    if (group.isDeleted()) {
      groupName.append(DELETED_GROUP_TAG);
    }
    return groupName.toString();
  }
}
