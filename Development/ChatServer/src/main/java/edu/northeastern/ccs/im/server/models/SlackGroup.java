package edu.northeastern.ccs.im.server.models;


/**
 * Class representing a SlackGroup entity corresponding to database.
 */
public class SlackGroup {

  // Name of this group. Group names are unique across the server.
  private String groupName;
  // Channel Id associated with this group. Represented the GroupChat that goes with this Group.
  private final int channelId;
  // Identifier of the group creator's user id
  private final int creatorId;
  // Unique integer identifier for this group.
  private final int groupId;

  // whether or not this group has been deleted before
  private boolean deleted;

  // the password of the group
  private String password;

  /**
   * Constructs a new group
   *
   * @param groupId   id of the group
   * @param creatorId id of the creator.
   * @param groupName name of the group.
   * @param channelId int channel
   * @param deleted   whether or not this has been 'deleted'
   * @param password  optional password to enter the group.
   */
  public SlackGroup(int groupId, int creatorId, String groupName, int channelId,
                    boolean deleted, String password) {
    this.groupId = groupId;
    this.creatorId = creatorId;
    this.groupName = groupName;
    this.channelId = channelId;
    this.deleted = deleted;
    this.password = password;
  }

  /**
   * Constructs a new group
   *
   * @param groupId   id of the group
   * @param creatorId id of the creator.
   * @param groupName name of the group.
   * @param channelId int channel
   */
  public SlackGroup(int groupId, int creatorId, String groupName, int channelId) {
    this(groupId, creatorId, groupName, channelId, false, null);
  }

  /**
   * Constructor used for inserting into groups table.
   *
   * @param creatorId The userId of the creator
   * @param groupName The name of the newly created group.
   */
  public SlackGroup(int creatorId, String groupName, String password) {
    this((groupName.hashCode() & 0xfffffff), creatorId, groupName, -1,
            false, password);
  }

  public String getGroupName() {
    return groupName;
  }

  public int getChannelId() {
    return channelId;
  }

  public int getGroupId() {
    return groupId;
  }

  public int getCreatorId() {
    return creatorId;
  }

  public String getPassword() {
    return password;
  }

  public boolean isDeleted() {
    return deleted;
  }
}
