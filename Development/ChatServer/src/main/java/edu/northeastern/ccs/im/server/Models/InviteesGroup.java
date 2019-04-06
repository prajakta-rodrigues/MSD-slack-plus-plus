package edu.northeastern.ccs.im.server.Models;

/**
 * The Class InviteesGroup.
 */
public class InviteesGroup {

  /**
   * The invitee handle.
   */
  private final String inviteeHandle;

  /**
   * The group name.
   */
  private final String groupName;

  /**
   * Instantiates a new invitees group.
   *
   * @param inviteeHandle the invitee handle
   * @param groupName the group name
   */
  public InviteesGroup(String inviteeHandle, String groupName) {
    super();
    this.inviteeHandle = inviteeHandle;
    this.groupName = groupName;
  }

  /**
   * Gets the invitee handle.
   *
   * @return the invitee handle
   */
  public String getInviteeHandle() {
    return inviteeHandle;
  }

  /**
   * Gets the group name.
   *
   * @return the group name
   */
  public String getGroupName() {
    return groupName;
  }

}
