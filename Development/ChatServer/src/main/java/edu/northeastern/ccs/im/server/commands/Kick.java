package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.ClientRunnable;
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
      return "You have not specified a member to kick.";
    }
    ClientRunnable mod = getClient(senderId);
    SlackGroup group = groupRepository.getGroupByChannelId(mod.getActiveChannelId());
    if (group == null) {
      return "You must set a group as your active channel to kick a member.";
    }
    boolean isModerator;
    try {
      isModerator = userGroupRepository.isModerator(senderId, group.getGroupId());
    } catch (Exception e) {
      return "Error while checking if you are moderator";
    }

    if (!isModerator) {
      return "You are not the moderator of this group.";
    }

    User toKick = userRepository.getUserByUserName(params[0]);
    if (toKick == null) {
      return "user does not exist";
    }

    if (!groupRepository.groupHasMember(toKick.getUserId(), group.getGroupId())) {
      return String.format("Could not find %s as a member of this group.", params[0]);
    }
    return userGroupRepository.removeMember(group.getGroupId(), toKick.getUserId()) ?
            String.format("User %s successfully kicked from group.", toKick.getUserName()) :
            String.format("Something went wrong. Failed to kick member %s.", toKick.getUserName());
  }

  @Override
  public String description() {
    return "As the moderator of your active group, kick a member from your group.\n" +
            "Parameters: handle of the user to kick";
  }
}
