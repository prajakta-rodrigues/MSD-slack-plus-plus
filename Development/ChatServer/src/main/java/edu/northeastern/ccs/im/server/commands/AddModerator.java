package edu.northeastern.ccs.im.server.commands;

import java.util.List;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.models.Notification;
import edu.northeastern.ccs.im.server.models.SlackGroup;
import edu.northeastern.ccs.im.server.models.User;

import static edu.northeastern.ccs.im.server.Prattle.getClient;
import static edu.northeastern.ccs.im.server.StringConstants.NONEXISTING_GROUP;
import static edu.northeastern.ccs.im.server.StringConstants.NOT_MODERATOR;

/**
 * Adds a moderator to a group.
 */
 class AddModerator extends ACommand {

  /**
   * Adds a moderator to a group
   *
   * @param params the user being added as a moderator
   * @param senderId the user trying to add a moderator
   * @return an informative message on the result of this command.
   */
  @Override
  public String apply(String[] params, Integer senderId) {
    if (params == null) {
      return "Invalid command parameters.";
    }
    ClientRunnable currClient = getClient(senderId);
    String userHandle = currClient.getName();
    String newModHandle = params[0];
    int currChannelId = currClient.getActiveChannelId();
    SlackGroup currGroup = groupRepository.getGroupByChannelId(currChannelId);
    if (currGroup == null) {
      return NONEXISTING_GROUP;
    }
    int groupId = currGroup.getGroupId();
    List<String> mods = userGroupRepository.getModerators(groupId);
    if (!mods.contains(userHandle)) {
      return NOT_MODERATOR;
    }
    if (!userGroupRepository.getGroupMembers(groupId).contains(newModHandle)) {
      return "The desired user is not part of the group. Send them an invite first.";
    }
    if (mods.contains(newModHandle)) {
      return "The desired user is already a moderator";
    }
    User newMod = userRepository.getUserByUserName(newModHandle);
    int newModId = newMod.getUserId();
    userGroupRepository.addModerator(newModId, groupId);
    Notification modNotification = Notification
            .makeNewModeratorNotification(groupId, senderId, newModId);
    notificationRepository.addNotification(modNotification);
    return userHandle + " added " + newModHandle + " as a moderator of this group.";
  }

  @Override
  public String description() {
    return "Adds the given user as a moderator.\nParameters: User to add as a moderator.";
  }
}
