package edu.northeastern.ccs.im.server.commands;

import java.util.List;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandMessages;
import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;

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
      return ErrorMessages.NO_SEARCH_TERM;
    }
    List<String> userNames = userRepository.searchUsersBySearchTerm(params[0]);
    StringBuilder listOfSimilarUsers;
    if (userNames.isEmpty()) {
      listOfSimilarUsers = new StringBuilder(CommandMessages.NO_USERS_FOUND);
    } else {
      listOfSimilarUsers = new StringBuilder(CommandMessages.USERS_FOUND);
    }
    for (String username : userNames) {
      listOfSimilarUsers.append("\n");
      listOfSimilarUsers.append(username);
    }
    return listOfSimilarUsers.toString();
  }

  @Override
  public String description() {
    return CommandDescriptions.SEARCH_DESCRIPTION;
  }

}
