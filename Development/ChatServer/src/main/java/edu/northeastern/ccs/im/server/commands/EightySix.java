package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.Prattle;
import edu.northeastern.ccs.im.server.constants.ServerConstants;
import edu.northeastern.ccs.im.server.constants.StringConstants;
import edu.northeastern.ccs.im.server.models.Message;
import edu.northeastern.ccs.im.server.models.SlackGroup;

import static edu.northeastern.ccs.im.server.Prattle.getClient;

class EightySix extends ACommand {

  @Override
  public String apply(String[] params, Integer senderId) {
    ClientRunnable sender = getClient(senderId);
    String modName = userRepository.getUserByUserId(senderId).getUserName();
    int channelId = sender.getActiveChannelId();
    SlackGroup group = groupRepository.getGroupByChannelId(channelId);
    int groupId = group.getGroupId();
    String groupName = group.getGroupName();
    if (!userGroupRepository.isModerator(senderId, groupId)) {
      return StringConstants.ErrorMessages.NOT_MODERATOR;
    }
    if (!groupRepository.deleteGroup(groupId)) {
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
