package edu.northeastern.ccs.im.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ChannelFactory {
  private int channelId = -1;
  private Set<String> takenGroupNames = Collections.synchronizedSet(new HashSet<>());

  public SlackGroup makeGroup(String creatorId, String groupName) {
    if (takenGroupNames.contains(groupName)) { throw new IllegalArgumentException("Group name already taken"); }
    this.channelId++;
    takenGroupNames.add(creatorId);
    return new SlackGroup(creatorId, groupName, this.channelId);
  }
}
