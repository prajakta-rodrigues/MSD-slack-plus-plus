package edu.northeastern.ccs.im.server.models;

/**
 * Enumeration for the different types of messages.
 *
 * @author Maria Jump
 */
public enum MessageType {
  /**
   * Message sent by the user attempting to login using a specified username.
   */
  HELLO("HLO"),
  /**
   * Message sent by the user to start the logging out process and sent by the server once the
   * logout process completes.
   */
  QUIT("BYE"),
  /**
   * Message whose contents is broadcast to all connected users.
   */
  BROADCAST("BCT"),
  /**
   * Message whose intention is to allow the user to interact with the system
   */
  COMMAND("CMD"),

  /**
   * Message whose intention is to authenticate with the system
   */
  AUTHENTICATE("AUT"),

  /**
   * Message whose intention is to register with the system
   */
  REGISTER("REG");

  /**
   * Store the short name of this message type.
   */
  private String abbreviation;

  /**
   * Define the message type and specify its short name.
   *
   * @param abbrev Short name of this message type, as a String.
   */
  MessageType(String abbrev) {
    abbreviation = abbrev;
  }

  /**
   * Return a representation of this Message as a String.
   *
   * @return Three letter abbreviation for this type of message.
   */
  @Override
  public String toString() {
    return abbreviation;
  }
}
