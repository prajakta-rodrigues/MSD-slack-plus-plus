package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandMessages;
import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;
import edu.northeastern.ccs.im.server.models.Notification;
import edu.northeastern.ccs.im.server.models.NotificationType;
import edu.northeastern.ccs.im.server.models.User;

/**
 * Friends a User.
 */
class Friend extends ACommand {

  /**
   * Lists all of the active users on the server.
   *
   * @param params the params
   * @param senderId the id of the sender.
   * @return the two users being noted as friends as a String.
   */
  @Override
  public String apply(String[] params, Integer senderId) {
    if (null == params) {
      return ErrorMessages.INCORRECT_COMMAND_PARAMETERS;
    }

    User newFriend = userRepository.getUserByUserName(params[0]);
    User currUser = userRepository.getUserByUserId(senderId);
    String currUserHandle = currUser.getUserName();
    if (newFriend == null) {
      return ErrorMessages.NON_EXISTING_USER;
    }
    Integer toFriendId = newFriend.getUserId();
    if (senderId.equals(toFriendId)) { // adding oneself as a friend
      return ErrorMessages.FRIEND_ONESELF_ERROR;
    }
    if (friendRepository.areFriends(senderId, toFriendId)) { // already friends
      return String.format(ErrorMessages.ALREADY_FRIENDS, params[0]);
    }
    if (friendRequestRepository.hasPendingFriendRequest(senderId, toFriendId)) {
      if (friendRepository.successfullyAcceptFriendRequest(senderId, toFriendId)) {
        Notification friendRequestNotif = Notification
            .makeFriendRequestNotification(senderId, toFriendId,
                NotificationType.FRIEND_REQUEST_APPROVED);
        notificationRepository.addNotification(friendRequestNotif);
        return String.format(CommandMessages.NEW_FRIENDS, currUserHandle, params[0]);
      }
      return ErrorMessages.GENERIC_ERROR;
    } else {
      if (friendRequestRepository.successfullySendFriendRequest(senderId, toFriendId)) {
        Notification friendRequestNotif = Notification
            .makeFriendRequestNotification(senderId, toFriendId, NotificationType.FRIEND_REQUEST);
        notificationRepository.addNotification(friendRequestNotif);

        return String.format(CommandMessages.SUCCESSFUL_FRIEND_REQUEST_SENT, currUserHandle, params[0]);
      }
      return ErrorMessages.COMMAND_ALREADY_PROCESSED;
    }
  }

  @Override
  public String description() {
    return CommandDescriptions.FRIEND_DESCRIPTION;
  }
}