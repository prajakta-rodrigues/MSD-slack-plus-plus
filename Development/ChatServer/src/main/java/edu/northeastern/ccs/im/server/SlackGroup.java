package edu.northeastern.ccs.im.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class representing a SlackGroup.  This class is subject to a lot of change once the database
 * is integrated.
 */
public class SlackGroup {
  /**
   * TODO: change String name to userId instead
   * Collection of moderators of this Group
   */
  private Set<String> moderators = Collections.synchronizedSet(new HashSet<>());
  // Name of this group. Group names are unique across the server.
  private String groupName;
  // Channel Id associated with this group. Represented the GroupChat that goes with this Group.
  private final int channelId;

  /**
   * Constructs a group for the first time, instantiating the creator, the name of the group, and
   * the channelId. Meant to be constructed using the ChannelFactory to enforce validity of
   * attributes.
   * @param creatorId Identifier of the User who created this Group
   * @param groupName name of the group
   * @param channelId integer channel Id
   */
  SlackGroup(String creatorId, String groupName, int channelId) {
    this.groupName = groupName;
    this.channelId = channelId;
    this.moderators.add(creatorId);
  }

  public String getGroupName() {
    return groupName;
  }

  public int getChannelId() {
    return channelId;
  }
}
