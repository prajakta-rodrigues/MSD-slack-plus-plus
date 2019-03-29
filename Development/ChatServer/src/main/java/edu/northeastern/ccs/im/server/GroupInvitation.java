package edu.northeastern.ccs.im.server;

import java.sql.Timestamp;

public class GroupInvitation {

  private final int invitorId;
  private final int inviteeId;
  private final int groupId;
  private final Timestamp createdDate;


  public GroupInvitation(int invitorId, int inviteeId, int groupId, Timestamp createdDate) {
    super();
    this.invitorId = invitorId;
    this.inviteeId = inviteeId;
    this.groupId = groupId;
    this.createdDate = createdDate;
  }

  public int getInvitorId() {
    return invitorId;
  }

  public int getInviteeId() {
    return inviteeId;
  }

  public int getGroupId() {
    return groupId;
  }

  public Timestamp getCreatedDate() {
    return createdDate;
  }

}
