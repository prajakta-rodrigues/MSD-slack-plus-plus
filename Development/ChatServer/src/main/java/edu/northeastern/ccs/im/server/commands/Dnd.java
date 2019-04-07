package edu.northeastern.ccs.im.server.commands;

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
    return "Sets Do not disturb mode and no notifications will be shown. Parameters: true";
  }

  /**
   * Sets the dnd mode of user
   * @param params the parameter for dnd mode: true/false
   * @return String the result of the command
   */
  @Override
  public String apply(String[] params, Integer senderId) {
    if(null == params) {
      return "No params specified";
    }    
    boolean setDND = Boolean.parseBoolean(params[0]);
    if(userRepository.setDNDStatus(senderId, setDND)) {
      return "Set DND mode to "+ setDND;
    }
    return "Unable to set DND";
  }

}
