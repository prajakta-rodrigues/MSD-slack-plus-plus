package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;
import java.util.List;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.models.Message;
import edu.northeastern.ccs.im.server.models.SlackGroup;
import edu.northeastern.ccs.im.server.utility.FilterWords;
import edu.northeastern.ccs.im.server.constants.ServerConstants;

import static edu.northeastern.ccs.im.server.Prattle.changeClientChannel;
import static edu.northeastern.ccs.im.server.Prattle.getClient;

/**
 * Change sender's active channel to the specified Group.
 */
class Group extends ACommand {

  @Override
  public String apply(String[] params, Integer senderId) {
    List<Message> messages;
    if (params == null) {
      return ErrorMessages.INCORRECT_COMMAND_PARAMETERS;
    }
    SlackGroup targetGroup = groupRepository.getGroupByName(params[0]);
    if (targetGroup == null) {
      return ErrorMessages.NON_EXISTING_GROUP;
    }
    ClientRunnable sender = getClient(senderId);
    if (!groupRepository.groupHasMember(senderId, targetGroup.getGroupId())) {
      return ErrorMessages.CURRENT_USER_NOT_IN_GROUP;
    }
    String password = targetGroup.getPassword();
    if (password != null && params.length < 2) {
      return ErrorMessages.PASSWRD_REQURIED;
    } else if (params.length >= 2 && !params[1].equals(password)) {
      return ErrorMessages.INCORRECT_PASSWRD;
    }
    int channelId = targetGroup.getChannelId();
    changeClientChannel(channelId, sender);
    messages = messageRepository.getLatestMessagesFromChannel(channelId, ServerConstants.LATEST_MESSAGES_COUNT);
    String queuedMsg = Message.listToString(messages);
    if(userRepository.getParentalControl(senderId)) {
      queuedMsg = FilterWords.filterSwearWordsFromMessage(queuedMsg);
    }
    return String.format("Active channel set to Group %s", targetGroup.getGroupName())
            + queuedMsg;
  }

  @Override
  public String description() {
    return CommandDescriptions.GROUP_DESCRIPTION;
  }
}