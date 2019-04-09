package edu.northeastern.ccs.im.server.commands;

import java.sql.SQLException;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.Prattle;
import edu.northeastern.ccs.im.server.constants.ServerConstants;
import edu.northeastern.ccs.im.server.constants.StringConstants;
import edu.northeastern.ccs.im.server.models.Message;
import edu.northeastern.ccs.im.server.models.SlackGroup;

import static edu.northeastern.ccs.im.server.Prattle.getClient;

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
      return StringConstants.ErrorMessages.GENERIC_ERROR;
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
    for (ClientRunnable client : Prattle.getChannelClients(channelId)) {
      client.setActiveChannelId(ServerConstants.GENERAL_ID);
      client.enqueueMessage(
              Message.makeBroadcastMessage(ServerConstants.SLACKBOT,
                      String.format(StringConstants.CommandMessages.EIGHTYSIX_NOTIFICATION,
                              groupName, modName)));
    }
    return StringConstants.CommandMessages.EIGHTYSIX_SUCCESS;
  }

  @Override
  public String description() {
    return StringConstants.CommandDescriptions.EIGHTYSIX_DESCRIPTION;
  }
}
