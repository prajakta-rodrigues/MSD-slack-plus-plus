package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.CommandMessages;
import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;

public class ParentalControl extends ACommand {

  @Override
  public String description() {
    return CommandDescriptions.PARENTAL_CONTROL_DESCRIPTION;
  }

  @Override
  public String apply(String[] params, Integer senderId) {
    if(params == null) {
      return ErrorMessages.INCORRECT_COMMAND_PARAMETERS;
    }
    Boolean flag = Boolean.valueOf(params[0]);
    boolean result = userRepository.setParentalControl(senderId, flag);
    
    if (result) {
      return String.format(CommandMessages.SUCCESSFUL_PARENTAL_CONTROL, flag);
    } else {
      return ErrorMessages.FAILED_SET_PARENTAL_CONTROL;
    }

  }

}
