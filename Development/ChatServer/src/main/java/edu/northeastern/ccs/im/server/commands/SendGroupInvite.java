package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandMessages;
import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.constants.ErrorCodes;
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
      return ErrorMessages.INCORRECT_COMMAND_PARAMETERS;
    }
    SlackGroup group;
    if (params.length == 2) {
      String groupName = params[1];
      group = groupRepository.getGroupByName(groupName);
    } else if (params.length == 1) {
      ClientRunnable currClient = getClient(senderId);
      int currChannelId = currClient.getActiveChannelId();
      group = groupRepository.getGroupByChannelId(currChannelId);
    } else {
      return ErrorMessages.COMMAND_NOT_RECOGNIZED;
    }
    if (null == group) {
      return ErrorMessages.NON_EXISTING_GROUP;
    }
    int groupId = group.getGroupId();

    boolean isModerator;

    try {
      isModerator = userGroupRepository.isModerator(senderId, groupId);
    } catch (SQLException ex) {
      return ErrorMessages.GENERIC_ERROR;
    }

    if (!isModerator) {
      return ErrorMessages.NOT_MODERATOR;
    }

    User user = userRepository.getUserByUserName(params[0]);

    if (null == user) {
      return ErrorMessages.NON_EXISTING_USER;
    }
    int inviteeId = user.getUserId();

    GroupInvitation groupInvitation =
            new GroupInvitation(senderId, inviteeId, groupId, Timestamp.valueOf(LocalDateTime.now()));
    boolean result = false;
    try {
      result = groupInviteRepository.add(groupInvitation);
    } catch (SQLException e) {
      if (e.getErrorCode() == ErrorCodes.MYSQL_DUPLICATE_PK) {
        return ErrorMessages.COMMAND_ALREADY_PROCESSED;
      }
    }
    if (result) {
      Notification notification = Notification
              .makeGroupInviteNotification(groupId, senderId, inviteeId);
      notificationRepository.addNotification(notification);
      return CommandMessages.SUCCESSFUL_INVITE;
    }
    return ErrorMessages.UNSUCCESSFUL_INVITE;
  }

  @Override
  public String description() {
    return CommandDescriptions.SEND_GROUP_INVITE_DESCRIPTION;
  }
}
