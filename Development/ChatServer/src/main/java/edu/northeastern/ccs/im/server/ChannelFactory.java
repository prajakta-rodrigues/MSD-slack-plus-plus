package edu.northeastern.ccs.im.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Factory class used to make Groups (and direct messages). Will likely be thinned after
 * delegating Object enforcing to database.
 */
class ChannelFactory {
  private static ChannelFactory singleton = null;
  private int channelId = -1;
  // List of used group names.
  private Set<String> takenGroupNames = Collections.synchronizedSet(new HashSet<>());

  /**
   * Get Singleton Factory
   * @return Factory instance
   */
  static ChannelFactory makeFactory() {
    if (singleton == null) {
      return new ChannelFactory();
    } else {
      return singleton;
    }
  }

  /**
   * Create a group with the given attributes.
   *
   * @param creatorId identifier of Creator of the group.
   * @param groupName name of the group.
   * @return created Group.
   */
  SlackGroup makeGroup(int creatorId, String groupName) {
    if (takenGroupNames.contains(groupName)) { throw new IllegalArgumentException("Group name already taken"); }
    this.channelId++;
    takenGroupNames.add(groupName);
    return new SlackGroup(creatorId, groupName, this.channelId);
  }
}
