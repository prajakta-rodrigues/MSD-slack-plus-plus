package edu.northeastern.ccs.im.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SlackGroup {
  /**
   * TODO: change String name to userId instead
   */
  private Set<String> moderators = Collections.synchronizedSet(new HashSet<>());
  private String groupName;
  private final int channelId;

  SlackGroup(String creatorId, String groupName, int channelId) {
    this.groupName = groupName;
    this.channelId = channelId;
    this.moderators.add(creatorId);
  }

  public String getGroupName() {
    return groupName;
  }
}
