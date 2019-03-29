package edu.northeastern.ccs.im.server;

/**
 * The Class InvitorsGroup.
 */
public class InvitorsGroup {

  /** The invitor handle. */
  private final String invitorHandle;
  
  /** The group name. */
  private final String groupName;

  /**
   * Instantiates a new invitors group.
   *
   * @param invitorHandle the invitor handle
   * @param groupName the group name
   */
  public InvitorsGroup(String invitorHandle, String groupName) {
    super();
    this.invitorHandle = invitorHandle;
    this.groupName = groupName;
  }

  /**
   * Gets the invitor handle.
   *
   * @return the invitor handle
   */
  public String getInvitorHandle() {
    return invitorHandle;
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
