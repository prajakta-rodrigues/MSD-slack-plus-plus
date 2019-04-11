package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandMessages;
import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;
import edu.northeastern.ccs.im.server.models.SlackGroup;

/**
 * Create a Group with the given name.
 */
class CreateGroup extends ACommand {

  @Override
  public String apply(String[] params, Integer senderId) {
    if (params == null) {
      return ErrorMessages.INCORRECT_COMMAND_PARAMETERS;
    }
    if (groupRepository.getGroupByName(params[0]) != null) {
      return ErrorMessages.GROUP_TAKEN;
    }
    String password = params.length < 2 ? null : params[1];
    if (groupRepository.addGroup(new SlackGroup(senderId, params[0], password))) {
      return String.format(CommandMessages.SUCCESSFUL_GROUP_CREATED, params[0]);
    } else {
      return ErrorMessages.GENERIC_ERROR;
    }
  }

  @Override
  public String description() {
    return CommandDescriptions.CREATE_GROUP_DESCRIPTION;
  }
}
