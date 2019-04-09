package edu.northeastern.ccs.im.server.commands;

import static edu.northeastern.ccs.im.server.Prattle.getClient;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.models.Notification;
import edu.northeastern.ccs.im.server.models.SlackGroup;
import edu.northeastern.ccs.im.server.models.User;

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
      return "Incorrect command parameters";
    }
    int messageToRecall = Integer.parseInt(params[0]); // make this 0-indexed
    if (messageToRecall < 1) {
      return "Your message number must be positive";
    }
    ClientRunnable currClient = getClient(senderId);
    int currChannelId = currClient.getActiveChannelId();
    boolean success = messageRepository.recallMessage(senderId, messageToRecall - 1, currChannelId);
    if (!success) {
      return "Error: You have sent less than " + messageToRecall + " messages to this channel.";
    }
    SlackGroup group = groupRepository.getGroupByChannelId(currChannelId);
    Notification recallNotification = Notification.makeNewRecallNotification(group.getGroupId(), senderId);
    notificationRepository.addNotification(recallNotification);
    return "You have recalled message " + messageToRecall + ".";
  }

  @Override
  public String description() {
    return "Recalls a message based on the given number.\n" +
        "Parameters: the number of most recently sent message to recall.";
  }

}
