package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandMessages;
import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;
import java.util.List;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.models.Notification;
import edu.northeastern.ccs.im.server.models.SlackGroup;
import edu.northeastern.ccs.im.server.models.User;

import static edu.northeastern.ccs.im.server.Prattle.getClient;
import static edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages.NOT_MODERATOR;

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
      return ErrorMessages.INCORRECT_COMMAND_PARAMETERS;
    }
    ClientRunnable currClient = getClient(senderId);
    String userHandle = currClient.getName();
    String newModHandle = params[0];
    int currChannelId = currClient.getActiveChannelId();
    SlackGroup currGroup = groupRepository.getGroupByChannelId(currChannelId);
    if (currGroup == null) {
      return ErrorMessages.NON_EXISTING_GROUP;
    }
    int groupId = currGroup.getGroupId();
    List<String> mods = userGroupRepository.getModerators(groupId);
    if (!mods.contains(userHandle)) {
      return NOT_MODERATOR;
    }
    if (!userGroupRepository.getGroupMembers(groupId).contains(newModHandle)) {
      return ErrorMessages.USER_NOT_IN_GROUP;
    }
    if (mods.contains(newModHandle)) {
      return ErrorMessages.ALREADY_MODERATOR;
    }
    User newMod = userRepository.getUserByUserName(newModHandle);
    int newModId = newMod.getUserId();
    userGroupRepository.addModerator(newModId, groupId);
    Notification modNotification = Notification
            .makeNewModeratorNotification(groupId, senderId, newModId);
    notificationRepository.addNotification(modNotification);
    return String.format(CommandMessages.SUCCESSFUL_MODERATOR_ADD, userHandle, newModHandle);
  }

  @Override
  public String description() {
    return CommandDescriptions.ADD_MODERATOR_DESCRIPTION;
  }
}
