package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.models.SlackGroup;

/**
 * Create a Group with the given name.
 */
class CreateGroup extends ACommand {

  @Override
  public String apply(String[] params, Integer senderId) {
    if (params == null) {
      return "No Group Name provided";
    }
    if (groupRepository.getGroupByName(params[0]) != null) {
      return "A group with this name already exists";
    }
    String password = params.length < 2 ? null : params[1];
    if (groupRepository.addGroup(new SlackGroup(senderId, params[0], password))) {
      return String.format("Group %s created", params[0]);
    } else {
      return "Something went wrong and your group was not created.";
    }
  }

  @Override
  public String description() {
    return "Create a group with the given name.\nParameters: Group name, (optional) password";
  }
}
