package edu.northeastern.ccs.im.server;

public class InviteesGroup {

  private final String inviteeHandle;
  private final String groupName;

  public InviteesGroup(String inviteeHandle, String groupName) {
    super();
    this.inviteeHandle = inviteeHandle;
    this.groupName = groupName;
  }

  public String getInviteeHandle() {
    return inviteeHandle;
  }

  public String getGroupName() {
    return groupName;
  }

}
