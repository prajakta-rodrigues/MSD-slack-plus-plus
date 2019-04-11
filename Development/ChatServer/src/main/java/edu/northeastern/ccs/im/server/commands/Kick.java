package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandMessages;
import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;
import edu.northeastern.ccs.im.server.models.SlackGroup;
import edu.northeastern.ccs.im.server.models.User;

import static edu.northeastern.ccs.im.server.Prattle.getClient;

/**
 * Kick a member from a group.
 */
class Kick extends ACommand {

  /**
   * removes users from the group.
   *
   * @param params the params
   * @param senderId the id of the sender.
   * @return the used removed form group as string.
   */
  @Override
  public String apply(String[] params, Integer senderId) {
    if (params == null) {
      return ErrorMessages.INCORRECT_COMMAND_PARAMETERS;
    }
    ClientRunnable mod = getClient(senderId);
    SlackGroup group = groupRepository.getGroupByChannelId(mod.getActiveChannelId());
    if (group == null) {
      return ErrorMessages.NON_EXISTING_GROUP;
    }
    boolean isModerator;
    try {
      isModerator = userGroupRepository.isModerator(senderId, group.getGroupId());
    } catch (Exception e) {
      return ErrorMessages.GENERIC_ERROR;
    }

    if (!isModerator) {
      return ErrorMessages.NOT_MODERATOR;
    }

    User toKick = userRepository.getUserByUserName(params[0]);
    if (toKick == null) {
      return ErrorMessages.NON_EXISTING_USER;
    }

    if (!groupRepository.groupHasMember(toKick.getUserId(), group.getGroupId())) {
      return ErrorMessages.USER_NOT_IN_GROUP;
    }
    return userGroupRepository.removeMember(group.getGroupId(), toKick.getUserId()) ?
        CommandMessages.SUCCESSFUL_KICK : ErrorMessages.UNSUCCESSFUL_KICK;
  }

  @Override
  public String description() {
    return CommandDescriptions.KICK_DESCRIPTION;
  }
}
