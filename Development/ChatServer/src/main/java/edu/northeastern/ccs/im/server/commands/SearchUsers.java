package edu.northeastern.ccs.im.server.commands;

import java.util.List;

/**
 * To search users whose handle names starting with search term
 */
public class SearchUsers extends ACommand{
  /**
   * Lists all of the active users on the server.
   *
   * @param params the params
   * @param senderId the id of the sender.
   * @return the two users being noted as friends as a String.
   */
  @Override
  public String apply(String[] params, Integer senderId) {
    if(params==null){
      return "Please enter a search term to find similar usernames";
    }
    List<String> userNames = userRepository.searchUsersBySearchTerm(params[0]);
    StringBuilder listOfSimilarUsers;
    if (userNames.isEmpty()) {
      listOfSimilarUsers = new StringBuilder("No users found");
    } else {
      listOfSimilarUsers = new StringBuilder("Users with similar names are:");
    }
    for (String username : userNames) {
      listOfSimilarUsers.append("\n");
      listOfSimilarUsers.append(username);
    }
    return listOfSimilarUsers.toString();
  }

  @Override
  public String description() {
    return "Search for users starting with similar search term.\nParameters: search term";
  }

}
