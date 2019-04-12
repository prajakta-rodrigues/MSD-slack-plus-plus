package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;
import java.util.List;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.models.SlackGroup;

import static edu.northeastern.ccs.im.server.Prattle.getClient;

/**
 * List all the group members in a group.
 */
class GroupMembers extends ACommand {

  /**
   * Lists all the group members in a group.
   *
   * @param params the params
   * @param senderId the id of the sender.
   * @return the list of active users as a String.
   */
  @Override
  public String apply(String[] params, Integer senderId) {
    ClientRunnable currClient = getClient(senderId);
    int currChannelId = currClient.getActiveChannelId();
    SlackGroup currGroup = groupRepository.getGroupByChannelId(currChannelId);
    if (currGroup == null) {
      return ErrorMessages.NON_EXISTING_GROUP;
    }
    List<String> mods = userGroupRepository.getModerators(currGroup.getGroupId());
    List<String> queriedMembers = userGroupRepository.getGroupMembers(currGroup.getGroupId());
    StringBuilder groupMembers = new StringBuilder("Group Members:");
    for (String member : queriedMembers) {
      groupMembers.append("\n");
      if (mods.contains(member)) {
        groupMembers.append("*");
      }
      groupMembers.append(member);
    }
    return groupMembers.toString();
  }

  @Override
  public String description() {
    return CommandDescriptions.GROUP_MEMBERS_DESCRIPTION;
  }
}