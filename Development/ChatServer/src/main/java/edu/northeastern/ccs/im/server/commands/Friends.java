package edu.northeastern.ccs.im.server.commands;

import java.util.List;

/**
 * Displays all of a User's friends.
 */
 class Friends extends ACommand {

  /**
   * Lists all of the active users on the server.
   *
   * @param params the params
   * @param senderId the id of the sender.
   * @return the two users being noted as friends as a String.
   */
  @Override
  public String apply(String[] params, Integer senderId) {
    List<Integer> friendIds = friendRepository.getFriendsByUserId(senderId);
    StringBuilder listOfFriends;
    if (friendIds.isEmpty()) {
      listOfFriends = new StringBuilder("You have no friends. :(");
    } else {
      listOfFriends = new StringBuilder("My friends:");
    }
    for (Integer friendId : friendIds) {
      listOfFriends.append("\n");
      listOfFriends.append(userRepository.getUserByUserId(friendId).getUserName());
    }
    return listOfFriends.toString();
  }

  @Override
  public String description() {
    return "Print out the names of all of my friends.";
  }

}
