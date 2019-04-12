package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;

/**
 * List all groups on the server.
 */
class Groups extends ACommand {

  @Override
  public String apply(String[] param, Integer senderId) {
    return groupRepository.groupsHavingMember(senderId);
  }

  @Override
  public String description() {
    return CommandDescriptions.GROUPS_DESCRIPTION;
  }
}
