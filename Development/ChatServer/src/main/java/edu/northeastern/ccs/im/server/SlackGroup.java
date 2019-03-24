package edu.northeastern.ccs.im.server;


/**
 * Class representing a SlackGroup entity corresponding to database. 
 */
public class SlackGroup {

//Name of this group. Group names are unique across the server.
 private String groupName;
 // Channel Id associated with this group. Represented the GroupChat that goes with this Group.
 private final int channelId;
 // Unique integer identifier for this group.
 private final int groupId;

 /**
  * Constructs a new group
  * @param groupId id of the group
  * @param groupName name of the group.
  * @param channelId int channel
  */
 public SlackGroup(int groupId, String groupName, int channelId) {
   this.groupId = groupId;
   this.groupName = groupName;
   this.channelId = channelId;
 }

 public String getGroupName() {
   return groupName;
 }

 int getChannelId() {
   return channelId;
 }

 public int getGroupId() { return groupId; }


}
