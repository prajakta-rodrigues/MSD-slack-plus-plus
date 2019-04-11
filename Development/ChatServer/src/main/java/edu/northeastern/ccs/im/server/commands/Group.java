package edu.northeastern.ccs.im.server.commands;

import java.util.List;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.models.Message;
import edu.northeastern.ccs.im.server.models.SlackGroup;
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
      return "No Group Name provided";
    }
    SlackGroup targetGroup = groupRepository.getGroupByName(params[0]);
    if (targetGroup == null) {
      return String.format("Group %s does not exist", params[0]);
    }
    ClientRunnable sender = getClient(senderId);
    if (!groupRepository.groupHasMember(senderId, targetGroup.getGroupId())) {
      return "You are not a member of this group";
    }
    String password = targetGroup.getPassword();
    if (password != null && params.length < 2) {
      return "This group requires a password";
    } else if (params.length >= 2 && !params[1].equals(password)) {
      return "You have input the incorrect password";
    }
    int channelId = targetGroup.getChannelId();
    changeClientChannel(channelId, sender);
    messages = messageRepository
            .getLatestMessagesFromChannel(channelId, ServerConstants.LATEST_MESSAGES_COUNT);
    StringBuilder latestMessages =
            new StringBuilder(
                    String.format("Active channel set to Group %s", targetGroup.getGroupName()));
    for (Message msg : messages) {
      String nextLine = "\n" + msg.getName() + " : " + msg.getText();
      latestMessages.append(nextLine);
    }
    if (!messages.isEmpty()) {
      latestMessages.append("\n" + "-------------------------");
    }
    return latestMessages.toString();
  }

  @Override
  public String description() {
    return "Change your current chat room to the specified Group.\nParameters: group name, " +
            "(if locked) password";
  }
}