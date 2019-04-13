package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandMessages;
import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;
import java.sql.SQLException;

import edu.northeastern.ccs.im.server.constants.ErrorCodes;
import edu.northeastern.ccs.im.server.models.SlackGroup;

/**
 * The Class AcceptGroupInvite for accepting group invites.
 */
class AcceptGroupInvite extends ACommand {

  @Override
  public String apply(String[] params, Integer userId) {

    if (null == params) {
      return ErrorMessages.INCORRECT_COMMAND_PARAMETERS;
    }

    SlackGroup group = groupRepository.getGroupByName(params[0]);

    if (group == null) {
      return ErrorMessages.NON_EXISTING_GROUP;
    }
    String password = group.getPassword();

    boolean result = false;

    try {
      result = groupInviteRepository.acceptInvite(userId, group.getGroupId());
    } catch (SQLException e) {
      if (e.getErrorCode() == ErrorCodes.MYSQL_DUPLICATE_PK) {
        return ErrorMessages.ALREADY_IN_GROUP;
      }
    }

    if (result) {
      StringBuilder ans = new StringBuilder(CommandMessages.SUCCESSFUL_INVITE_ACCEPT);
      if (password != null) {
        ans.append(CommandMessages.JOIN_WITH_PASS);
        ans.append(password);
      }
      return ans.toString();
    }
    return ErrorMessages.NO_INVITE;
  }

  @Override
  public String description() {
    return CommandDescriptions.ACCEPT_GROUP_INVITE_DESCRIPTION;
  }
}