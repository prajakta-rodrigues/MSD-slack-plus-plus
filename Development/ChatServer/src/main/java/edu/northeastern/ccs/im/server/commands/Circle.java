package edu.northeastern.ccs.im.server.commands;

import java.util.List;

import edu.northeastern.ccs.im.server.ClientRunnable;

import static edu.northeastern.ccs.im.server.Prattle.getAuthenticatedClients;

/**
 * List all active friend on the server.
 */
class Circle extends ACommand {

  /**
   * Lists all of the active friends on the server.
   *
   * @param params the params
   * @param senderId the id of the sender.
   * @return the list of active friends as a String.
   */
  @Override
  public String apply(String[] params, Integer senderId) {
    List<Integer> friendIds = friendRepository.getFriendsByUserId(senderId);
    StringBuilder activeFriends = new StringBuilder("Active Friends:");
    for (ClientRunnable activeUser : getAuthenticatedClients()) {
      int activeUserId = activeUser.getUserId();
      if (friendIds.contains(activeUserId)) {
        activeFriends.append("\n");
        activeFriends.append(activeUser.getName());
      }
    }
    String activeFriendsList = activeFriends.toString();
    if (activeFriendsList.equals("Active Friends:")) {
      activeFriendsList = "No friends are active.";
    }
    return activeFriendsList;
  }

  @Override
  public String description() {
    return "Print out the handles of the active users on the server";
  }
}
