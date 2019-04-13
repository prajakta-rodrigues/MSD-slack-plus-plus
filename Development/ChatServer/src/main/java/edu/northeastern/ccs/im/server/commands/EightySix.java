package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;
import java.sql.SQLException;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.Prattle;
import edu.northeastern.ccs.im.server.constants.ServerConstants;
import edu.northeastern.ccs.im.server.constants.StringConstants;
import edu.northeastern.ccs.im.server.models.Message;
import edu.northeastern.ccs.im.server.models.SlackGroup;

import static edu.northeastern.ccs.im.server.Prattle.getClient;
import static edu.northeastern.ccs.im.server.constants.ServerConstants.GENERAL_ID;

/**
 * Destroys a SlackGroup and moves all active members to general.
 */
class EightySix extends ACommand {

  @Override
  public String apply(String[] params, Integer senderId) {
    ClientRunnable sender = getClient(senderId);
    String modName = userRepository.getUserByUserId(senderId).getUserName();
    int channelId = sender.getActiveChannelId();
    SlackGroup group = groupRepository.getGroupByChannelId(channelId);
    if (group == null) {
      return ErrorMessages.NON_EXISTING_GROUP;
    }
    int groupId = group.getGroupId();
    String groupName = group.getGroupName();
    try {
      if (!userGroupRepository.isModerator(senderId, groupId)) {
        return StringConstants.ErrorMessages.NOT_MODERATOR;
      }
    } catch (SQLException e) {
      return StringConstants.ErrorMessages.GENERIC_ERROR;
    }
    if (!groupRepository.deleteGroup(senderId, groupId)) {
      return StringConstants.ErrorMessages.GENERIC_ERROR;
    }
    Prattle.changeClientChannel(GENERAL_ID, sender);
    for (ClientRunnable client : Prattle.getChannelClients(channelId)) {
      Prattle.changeClientChannel(GENERAL_ID, client);
      client.enqueueMessage(
              Message.makeBroadcastMessage(ServerConstants.SLACKBOT,
                      String.format(StringConstants.CommandMessages.EIGHTY_SIX_NOTIFICATION,
                              groupName, modName)));
    } 
    return StringConstants.CommandMessages.EIGHTY_SIX_SUCCESS;
  }

  @Override
  public String description() {
    return StringConstants.CommandDescriptions.EIGHTY_SIX_DESCRIPTION;
  }
}
