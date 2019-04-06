package edu.northeastern.ccs.im.server.commands;

import java.sql.SQLException;

import edu.northeastern.ccs.im.server.ErrorCodes;
import edu.northeastern.ccs.im.server.models.SlackGroup;

/**
 * The Class AcceptGroupInvite for accepting group invites.
 */
class AcceptGroupInvite extends ACommand {

  @Override
  public String apply(String[] params, Integer userId) {

    if (null == params) {
      return "No group specified";
    }

    SlackGroup group = groupRepository.getGroupByName(params[0]);

    if (group == null) {
      return "Specified group doesn't exist";
    }

    boolean result = false;

    try {
      result = groupInviteRepository.acceptInvite(userId, group.getGroupId());
    } catch (SQLException e) {
      if (e.getErrorCode() == ErrorCodes.MYSQL_DUPLICATE_PK) {
        return "You are already part of the group";
      }
    }

    if (result) {
      return "Invite accepted successfully!";
    }
    return "You do not have an invite to the group";
  }

  @Override
  public String description() {
    return "Accepts group invite request. \n Parameters : groupname";
  }
}