package edu.northeastern.ccs.im.server.commands;

import java.util.List;

import edu.northeastern.ccs.im.server.ClientRunnable;

import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandMessages;
import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;
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
      return ErrorMessages.INCORRECT_COMMAND_PARAMETERS;
    }
    User receiver = userRepository.getUserByUserName(params[0]);
    if (receiver == null) {
      return ErrorMessages.NON_EXISTING_USER;
    }
    int receiverId = receiver.getUserId();

    if (!senderId.equals(receiverId) && !friendRepository
        .areFriends(senderId, receiverId)) {
      return String.format(ErrorMessages.NOT_FRIENDS, params[0]);
    }
    int channelId = dmRepository.getDMChannel(senderId, receiverId);
    if (channelId < 0) {
      channelId = dmRepository.createDM(senderId, receiverId);
    }  
    if (channelId < 0) {
      return ErrorMessages.GENERIC_ERROR;
    }
    ClientRunnable sender = getClient(senderId);
    changeClientChannel(channelId, sender);
    List<Message> messages = messageRepository
            .getLatestMessagesFromChannel(channelId, ServerConstants.LATEST_MESSAGES_COUNT);
    return String.format(CommandMessages.SUCCESSFUL_DM, params[0]) + Message.listToString(messages);
  }

  @Override
  public String description() {
    return CommandDescriptions.DM_DESCRIPTION;
  }
}
