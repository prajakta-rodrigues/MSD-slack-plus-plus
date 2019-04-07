package edu.northeastern.ccs.im.server.models;

import java.sql.Timestamp;

/**
 * class for group invitations
 */
public class GroupInvitation {

  private final int invitorId;
  private final int inviteeId;
  private final int groupId;
  private final Timestamp createdDate;


  /**
   * Constructs an instance of Group Invitation.
   *
   * @param invitorId the inviter's id
   * @param inviteeId the invitee's id
   * @param groupId the group being invited to
   * @param createdDate the created date
   */
  public GroupInvitation(int invitorId, int inviteeId, int groupId, Timestamp createdDate) {
    super();
    this.invitorId = invitorId;
    this.inviteeId = inviteeId;
    this.groupId = groupId;
    this.createdDate = createdDate;
  }

  /**
   * @return the inviter id
   */
  public int getInvitorId() {
    return invitorId;
  }

  /**
   * @return the invitee id
   */
  public int getInviteeId() {
    return inviteeId;
  }

  /**
   * @return the group id
   */
  public int getGroupId() {
    return groupId;
  }

  /**
   * @return the created date
   */
  public Timestamp getCreatedDate() {
    return createdDate;
  }

}
