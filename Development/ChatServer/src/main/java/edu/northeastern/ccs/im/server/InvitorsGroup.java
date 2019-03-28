package edu.northeastern.ccs.im.server;

public class InvitorsGroup {

  private final String invitorHandle;
  private final String groupName;

  public InvitorsGroup(String invitorHandle, String groupName) {
    super();
    this.invitorHandle = invitorHandle;
    this.groupName = groupName;
  }

  public String getInvitorHandle() {
    return invitorHandle;
  }

  public String getGroupName() {
    return groupName;
  }


}
