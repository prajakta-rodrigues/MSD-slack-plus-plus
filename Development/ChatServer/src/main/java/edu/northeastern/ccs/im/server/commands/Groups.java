package edu.northeastern.ccs.im.server.commands;

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
    return "Print out the names of each Group you are a member of";
  }
}
