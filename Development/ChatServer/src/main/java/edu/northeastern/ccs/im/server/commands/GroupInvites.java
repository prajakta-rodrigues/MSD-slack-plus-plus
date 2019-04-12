package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandMessages;
import java.util.List;

import edu.northeastern.ccs.im.server.models.InvitorsGroup;

/**
 * The Class GroupInvites for checking invitations received.
 */
class GroupInvites extends ACommand {

  @Override
  public String apply(String[] params, Integer senderId) {
    List<InvitorsGroup> listInvites =
            groupInviteRepository.getGroupInvitationsByInviteeId(senderId);
    StringBuilder result = new StringBuilder();
    result.append("Invitations:\n");
    for (InvitorsGroup invite : listInvites) {
      result.append(String.format(CommandMessages.GROUP_INVITES,
              invite.getInvitorHandle(), invite.getGroupName()));
      result.append("\n");
    }
    return result.toString();
  }

  @Override
  public String description() {
    return CommandDescriptions.GROUP_INVITES_DESCRIPTION;
  }

}
