package edu.northeastern.ccs.im.server.commands;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.ErrorCodes;
import edu.northeastern.ccs.im.server.models.GroupInvitation;
import edu.northeastern.ccs.im.server.models.Notification;
import edu.northeastern.ccs.im.server.models.SlackGroup;
import edu.northeastern.ccs.im.server.models.User;

import static edu.northeastern.ccs.im.server.Prattle.getClient;

/**
 * The Class SendGroupInvite sends group invite.
 */
class SendGroupInvite extends ACommand {


  @Override
  public String apply(String[] params, Integer senderId) {
    if (null == params) {
      return "No username or group given";
    }
    SlackGroup group;
    if (params.length == 2) {
      String groupName = params[1];
      group = groupRepository.getGroupByName(groupName);
    } else if (params.length == 1) {
      ClientRunnable currClient = getClient(senderId);
      if (currClient == null) {
        return "Your client is null";
      }
      int currChannelId = currClient.getActiveChannelId();
      group = groupRepository.getGroupByChannelId(currChannelId);
    } else {
      return "Command message not recogized";
    }
    if (null == group) {
      return "Group doesn't exist";
    }
    int groupId = group.getGroupId();

    boolean isModerator;

    try {
      isModerator = userGroupRepository.isModerator(senderId, groupId);
    } catch (SQLException ex) {
      return "Unable to send request";
    }

    if (!isModerator) {
      return "You are not a moderator of given group";
    }

    User user = userRepository.getUserByUserName(params[0]);

    if (null == user) {
      return "Invited user doesn't exist";
    }
    int inviteeId = user.getUserId();

    GroupInvitation groupInvitation =
            new GroupInvitation(senderId, inviteeId, groupId, Timestamp.valueOf(LocalDateTime.now()));
    boolean result = false;
    try {
      result = groupInviteRepository.add(groupInvitation);
    } catch (SQLException e) {
      if (e.getErrorCode() == ErrorCodes.MYSQL_DUPLICATE_PK) {
        return "You have already invited the user";
      }
    }
    if (result) {
      Notification notification = Notification
              .makeGroupInviteNotification(groupId, senderId, inviteeId);
      notificationRepository.addNotification(notification);
      return "Invite sent successfully";
    }
    return "Failed to send invite";
  }

  @Override
  public String description() {
    return "Send out group invite to user.\n Parameters : handle, groupName";
  }
}
