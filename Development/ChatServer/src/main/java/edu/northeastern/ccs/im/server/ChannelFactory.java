package edu.northeastern.ccs.im.server;

public class ChannelFactory {
  private int channelId = 0;

  public SlackGroup makeGroup(String creatorId, String groupName) {
    this.channelId++;
    return new SlackGroup(creatorId, groupName, this.channelId);
  }
}
