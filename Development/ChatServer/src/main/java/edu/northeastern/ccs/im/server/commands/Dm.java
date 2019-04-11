package edu.northeastern.ccs.im.server.commands;

import java.util.List;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.constants.ServerConstants;
import edu.northeastern.ccs.im.server.models.Message;
import edu.northeastern.ccs.im.server.models.User;

import static edu.northeastern.ccs.im.server.Prattle.changeClientChannel;
import static edu.northeastern.ccs.im.server.Prattle.getClient;

/**
 * Starts a Dm.
 */
class Dm extends ACommand {

  /**
   * Starts a direct message with the specified user, if possible.
   *
   * @param params the params
   * @param senderId the id of the sender.
   * @return the list of active users as a String.
   */
  @Override
  public String apply(String[] params, Integer senderId) {
    if (params == null) {
      return "No user name provided";
    }
    User receiver = userRepository.getUserByUserName(params[0]);
    if (receiver == null) {
      return String.format("User %s not found!", params[0]);
    }
    int receiverId = receiver.getUserId();
    int existingId = dmRepository.getDMChannel(senderId, receiverId);
    int channelId = existingId > 0 ? existingId : dmRepository.createDM(senderId, receiverId);
    ClientRunnable sender = getClient(senderId);
    if (channelId < 0) {
      return "Failed to create direct message. Try again later.";
    } else if (!senderId.equals(receiverId) && !friendRepository
        .areFriends(senderId, receiverId)) {
      return "You are not friends with " + params[0]
          + ". Send them a friend request to direct message.";
    } else {
      changeClientChannel(channelId, sender);
      List<Message> messages = messageRepository
              .getLatestMessagesFromChannel(channelId, ServerConstants.LATEST_MESSAGES_COUNT);

      return String.format("You are now messaging %s", params[0]) + Message.listToString(messages);
    }
  }

  @Override
  public String description() {
    return "Start a DM with the given user.\nParameters: user id";
  }
}
