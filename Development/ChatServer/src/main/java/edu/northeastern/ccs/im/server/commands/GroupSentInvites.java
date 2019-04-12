package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandMessages;
import java.util.List;

import edu.northeastern.ccs.im.server.models.InviteesGroup;

/**
 * The Class GroupSentInvites for checking all sent invitations.
 */
class GroupSentInvites extends ACommand {

  @Override
  public String apply(String[] params, Integer senderId) {
    List<InviteesGroup> listInvites =
        groupInviteRepository.getGroupInvitationsByInvitorId(senderId);
    StringBuilder result = new StringBuilder();
    result.append("Invitations sent:\n");
    for (InviteesGroup invite : listInvites) {
      result.append(String
          .format(CommandMessages.INVITES_SENT, invite.getInviteeHandle(), invite.getGroupName()));
      result.append("\n");
    }
    return result.toString();
  }

  @Override
  public String description() {
    return CommandDescriptions.GROUP_SENT_INVITES_DESCRIPTIONS;
  }
}
