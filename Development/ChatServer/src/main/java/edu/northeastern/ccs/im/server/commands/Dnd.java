package edu.northeastern.ccs.im.server.commands;

public class Dnd extends ACommand {

  @Override
  public String description() {
    return "Sets Do not disturb mode and no notifications will be shown. Parameters: true";
  }

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
