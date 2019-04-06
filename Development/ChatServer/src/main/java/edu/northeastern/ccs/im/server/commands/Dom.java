package edu.northeastern.ccs.im.server.commands;

import java.util.List;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.models.SlackGroup;

import static edu.northeastern.ccs.im.server.Prattle.getClient;
import static edu.northeastern.ccs.im.server.StringConstants.NONEXISTING_GROUP;
import static edu.northeastern.ccs.im.server.StringConstants.NOT_MODERATOR;
import static edu.northeastern.ccs.im.server.StringConstants.ONLY_MODERATOR_FAILURE;

/**
 * Removes a user's moderatorship, if applicable
 */
class Dom extends ACommand {

  /**
   * Removes a user's moderatorship
   *
   * @param ignoredParams the ignored params
   * @param senderId      the id of the user wanting to remove their moderatorship
   * @return an informative message on the result of this command.
   */
  @Override
  public String apply(String[] ignoredParams, Integer senderId) {
    ClientRunnable currClient = getClient(senderId);
    String userHandle = currClient.getName();
    int currChannelId = currClient.getActiveChannelId();
    SlackGroup currGroup = groupRepository.getGroupByChannelId(currChannelId);
    if (currGroup == null) {
      return NONEXISTING_GROUP;
    }
    int currGroupId = currGroup.getGroupId();
    List<String> mods = userGroupRepository.getModerators(currGroupId);
    if (!mods.contains(userHandle)) {
      return NOT_MODERATOR;
    }
    if (mods.size() == 1) {
      return ONLY_MODERATOR_FAILURE;
    }
    userGroupRepository.removeModerator(senderId, currGroupId);
    return userHandle + " removed themself from being a moderator of this group.";
  }

  @Override
  public String description() {
    return "Removes a user's moderatorship.";
  }
}
