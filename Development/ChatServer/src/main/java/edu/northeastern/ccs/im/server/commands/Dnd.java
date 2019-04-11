package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandMessages;
import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;

/**
 * The Class Dnd.
 */
public class Dnd extends ACommand {

  /**
   * Gives description of DND command
   * @return description of this command
   */
  @Override
  public String description() {
    return CommandDescriptions.DND_DESCRIPTION;
  }

  /**
   * Sets the dnd mode of user
   * @param params the parameter for dnd mode: true/false
   * @return String the result of the command
   */
  @Override
  public String apply(String[] params, Integer senderId) {
    if(null == params) {
      return ErrorMessages.INCORRECT_COMMAND_PARAMETERS;
    }    
    boolean setDND = Boolean.parseBoolean(params[0]);
    if(userRepository.setDNDStatus(senderId, setDND)) {
      return String.format(CommandMessages.SUCCESSFUL_DND, setDND);
    }
    return ErrorMessages.UNSUCCESSFUL_DND;
  }

}
