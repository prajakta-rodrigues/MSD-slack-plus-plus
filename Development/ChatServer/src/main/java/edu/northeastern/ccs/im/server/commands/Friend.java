package edu.northeastern.ccs.im.server.commands;

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
      return "No user specified";
    }

    User newFriend = userRepository.getUserByUserName(params[0]);
    User currUser = userRepository.getUserByUserId(senderId);
    String currUserHandle = currUser.getUserName();
    if (newFriend == null) {
      return "The specified user does not exist.";
    }
    Integer toFriendId = newFriend.getUserId();
    if (senderId.equals(toFriendId)) { // adding oneself as a friend
      return "You cannot be friends with yourself on this app. xD";
    }
    if (friendRepository.areFriends(senderId, toFriendId)) { // already friends
      return "You are already friends with " + params[0] + ".";
    }
    if (friendRequestRepository.hasPendingFriendRequest(senderId, toFriendId)) {
      if (friendRepository.successfullyAcceptFriendRequest(senderId, toFriendId)) {
        Notification friendRequestNotif = Notification
                .makeFriendRequestNotification(senderId, toFriendId,
                        NotificationType.FRIEND_REQUEST_APPROVED);
        notificationRepository.addNotification(friendRequestNotif);
        return currUserHandle + " and " + params[0] + " are now friends.";
      }
      return "Something went wrong and we could not accept " + params[0] + "'s friend request.";
    } else {
      if (friendRequestRepository.successfullySendFriendRequest(senderId, toFriendId)) {
        Notification friendRequestNotif = Notification
                .makeFriendRequestNotification(senderId, toFriendId, NotificationType.FRIEND_REQUEST);
        notificationRepository.addNotification(friendRequestNotif);

        return currUserHandle + " sent " + params[0] + " a friend request.";
      }
      return "You already sent " + params[0] + " a friend request.";
    }
  }

  @Override
  public String description() {
    return "Friends the user with the given handle.\nParameters: User to friend";
  }
}