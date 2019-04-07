package edu.northeastern.ccs.im.server.commands;

import java.util.Map;

import edu.northeastern.ccs.im.server.models.User;
import edu.northeastern.ccs.im.server.utility.LanguageSupport;

/**
 * List all available COMMANDS to use.
 */
 class Help extends ACommand {

  /**
   * Lists all of the available COMMANDS.
   *
   * @param params the params
   * @param senderId the id of the sender.
   * @return the list of active users as a String.
   */
  @Override
  public String apply(String[] params, Integer senderId) {
    StringBuilder availableCOMMANDS = new StringBuilder("Available COMMANDS:");
    User user =  userRepository.getUserByUserId(senderId);
    Map<String, Command> commands = CommandFactory.getCommands().get(user.getType());

    for (Map.Entry<String, Command> command : commands.entrySet()) {
      String nextLine = "\n" + command.getKey() + " " + LanguageSupport.getInstance()
              .getLanguage("english", command.getValue().description());
      availableCOMMANDS.append(nextLine);
    }
    return availableCOMMANDS.toString();
  }

  @Override
  public String description() {
    return "Lists all of the available commands.";
  }
}
