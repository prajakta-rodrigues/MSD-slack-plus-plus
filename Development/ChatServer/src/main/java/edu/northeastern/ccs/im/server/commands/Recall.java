package edu.northeastern.ccs.im.server.commands;

import static edu.northeastern.ccs.im.server.Prattle.getClient;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandMessages;
import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;

/**
 * Command to recall a sent message
 */
public class Recall extends ACommand {

  /**
   * Recalls the desired sent message based on a 1-index message history
   *
   * @param params the params
   * @param senderId the id of the sender.
   * @return the result of this recall attempt.
   */
  @Override
  public String apply(String[] params, Integer senderId) {
    if (params == null) {
      return ErrorMessages.INCORRECT_COMMAND_PARAMETERS;
    }
    int messageToRecall = Integer.parseInt(params[0]); // make this 0-indexed
    if (messageToRecall < 1) {
      return ErrorMessages.NON_POSITIVE_MESSAGE_NUMBER;
    }
    ClientRunnable currClient = getClient(senderId);
    int currChannelId = currClient.getActiveChannelId();
    boolean success = messageRepository.recallMessage(senderId, messageToRecall - 1, currChannelId);
    if (!success) {
      return ErrorMessages.NOT_ENOUGH_MESSAGES;
    }
    return CommandMessages.SUCCESSFUL_RECALL;
  }

  @Override
  public String description() {
    return CommandDescriptions.RECALL_DESCRIPTION;
  }

}
