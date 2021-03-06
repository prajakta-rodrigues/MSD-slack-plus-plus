package edu.northeastern.ccs.im.server.models;

import edu.northeastern.ccs.im.server.constants.StringConstants;
import java.util.List;

import edu.northeastern.ccs.im.server.ClientRunnable;
import edu.northeastern.ccs.im.server.Prattle;

/**
 * Each instance of this class represents a single transmission by our IM clients.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0 International
 * License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/. It
 * is based on work originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 *
 * @version 1.3
 */
public class Message {

  /**
   * The string sent when a field is null.
   */
  private static final String NULL_OUTPUT = "--";

  /**
   * The handle of the message.
   */
  private MessageType msgType;

  /**
   * The first argument used in the message. This will be the sender's identifier.
   */
  private String msgSender;

  /**
   * The second argument used in the message.
   */
  private String msgText;

  /**
   * The channel this message was sent in.
   */
  private int channelId;

  /**
   * The user id of this message's sender
   */
  private int userId;

  /**
   * Create a new message that contains actual IM text and a channelId. The type of distribution is
   * defined by the handle and we must also set the name of the message sender, message recipient,
   * and the text to send.
   *
   * @param handle Handle for the type of message being created.
   * @param srcName Name of the individual sending this message
   * @param userId id of hte sender of this message.
   * @param text Text of the instant message
   * @param channelId id of the channel this message was sent in
   */
  private Message(MessageType handle, String srcName, int userId, String text, int channelId) {
    msgType = handle;
    // Save the properly formatted identifier for the user sending the
    // message.
    msgSender = srcName;
    // Save the text of the message.
    msgText = text;
    // Save the id of the channel associated with this message
    this.channelId = channelId;
    // Save id of the sender
    this.userId = userId;
  }

  /**
   * Create a new message that contains actual IM text and a channelId. The type of distribution is
   * defined by the handle and we must also set the name of the message sender, message recipient,
   * and the text to send.
   *
   * @param handle Handle for the type of message being created.
   * @param srcName Name of the individual sending this message
   * @param text Text of the instant message
   */
  private Message(MessageType handle, String srcName, String text) {
    this(handle, srcName, -1, text, -1);
  }

  /**
   * Create a new message that contains a command sent the server that requires a single argument.
   * This message contains the given handle and the single argument.
   *
   * @param handle Handle for the type of message being created.
   * @param srcName Argument for the message; at present this is the name used to log-in to the IM
   * server.
   */
  private Message(MessageType handle, String srcName) {
    this(handle, srcName, -1, null, -1);
  }

  /**
   * For Command Message use
   *
   * @param handle handle of the message
   * @param srcName name of hte sender
   * @param senderId id of the sender
   * @param text the text of hte message
   */
  private Message(MessageType handle, String srcName, int senderId, String text) {
    this(handle, srcName, senderId, text, -1);
  }

  /**
   * Create a new message to continue the logout process.
   *
   * @param myName The name of the client that sent the quit message.
   * @return Instance of Message that specifies the process is logging out.
   */
  public static Message makeQuitMessage(String myName) {
    return new Message(MessageType.QUIT, myName, null);
  }

  /**
   * Create a new message broadcasting an announcement to the world.
   *
   * @param myName Name of the sender of this very important missive.
   * @param userId id of the sender
   * @param text Text of the message that will be sent to all users
   * @param channelId The channel that this Message was sent in.
   * @return Instance of Message that transmits text to all logged in users.
   */
  private static Message makeBroadcastMessage(String myName, int userId, String text,
      int channelId) {
    return new Message(MessageType.BROADCAST, myName, userId, text, channelId);
  }

  public static Message makeBroadcastMessage(String myName, String text) {
    return makeBroadcastMessage(myName, -1, text, -1);
  }

  /**
   * Create a new command message to interact with the application.
   *
   * @param myName Name of the sender of the sender of this command.
   * @param myId id of hte sender
   * @param text Text of the command.
   * @return Instance of Message that is a command.
   */
  public static Message makeCommandMessage(String myName, int myId, String text) {
    return new Message(MessageType.COMMAND, myName, myId, text);
  }

  /**
   * Create a new authenticate message to authenticate with the application.
   *
   * @param myName Name of the sender of the sender of this command.
   * @param text Text of the command.
   * @return Instance of Message that is a command.
   */
  public static Message makeAuthenticateMessage(String myName, String text) {
    return new Message(MessageType.AUTHENTICATE, myName, text);
  }


  /**
   * Create a new register message to register with the application.
   *
   * @param myName Name of the sender of the sender of this command.
   * @param text Text of the command.
   * @return Instance of Message that is a command.
   */
  public static Message makeRegisterMessage(String myName, String text) {
    return new Message(MessageType.REGISTER, myName, text);
  }

  /**
   * Create a new message stating the name with which the user would like to login.
   *
   * @param text Name the user wishes to use as their screen name.
   * @return Instance of Message that can be sent to the server to try and login.
   */
  protected static Message makeHelloMessage(String text) {
    return new Message(MessageType.HELLO, null, text);
  }

  /**
   * Given a handle, name and text, return the appropriate message instance or an instance from a
   * subclass of message.
   *
   * @param handle Handle of the message to be generated.
   * @param srcName Name of the originator of the message (may be null)
   * @param text Text sent in this message (may be null)
   * @return Instance of Message (or its subclasses) representing the handle, name, & text.
   */
  public static Message makeMessage(String handle, String srcName, int senderId, String text) {
    Message result = null;
    if (handle.compareTo(MessageType.QUIT.toString()) == 0) {
      result = makeQuitMessage(srcName);
    } else if (handle.compareTo(MessageType.HELLO.toString()) == 0) {
      result = makeSimpleLoginMessage(srcName);
    } else if (handle.compareTo(MessageType.BROADCAST.toString()) == 0) {
      ClientRunnable sender = Prattle.getClient(senderId);
      int activeChannelId = sender != null ? sender.getActiveChannelId() : -1;
      result = makeBroadcastMessage(srcName, senderId, text, activeChannelId);
    } else if (handle.compareTo(MessageType.COMMAND.toString()) == 0) {
      result = makeCommandMessage(srcName, senderId, text);
    } else if (handle.compareTo(MessageType.AUTHENTICATE.toString()) == 0) {
      result = makeAuthenticateMessage(srcName, text);
    } else if (handle.compareTo(MessageType.REGISTER.toString()) == 0) {
      result = makeRegisterMessage(srcName, text);
    }

    return result;
  }

  /**
   * Create a new message for the early stages when the user logs in without all the special stuff.
   *
   * @param myName Name of the user who has just logged in.
   * @return Instance of Message specifying a new friend has just logged in.
   */
  public static Message makeSimpleLoginMessage(String myName) {
    return new Message(MessageType.HELLO, myName);
  }

  /**
   * Return the name of the sender of this message.
   *
   * @return String specifying the name of the message originator.
   */
  public String getName() {
    return msgSender;
  }

  /**
   * Return the channel id associated with this message.
   *
   * @return integer channel id.
   */
  public int getChannelId() {
    return channelId;
  }

  /**
   * Return MessageType associated with this message.
   *
   * @return messagetype of message.
   */
  public MessageType getMsgType() {
    return msgType;
  }

  /**
   * Return user id associated with this message.
   *
   * @return user id of message sender.
   */
  public int getUserId() {
    return userId;
  }

  /**
   * Return the text of this message.
   *
   * @return String equal to the text sent by this message.
   */
  public String getText() {
    return msgText;
  }
  
  /**
   * Sets the text as message text
   * @param text to be set
   */
  public void setText(String text) {
    this.msgText = text; 
  }

  /**
   * Determine if this message is broadcasting text to everyone.
   *
   * @return True if the message is a broadcast message; false otherwise.
   */
  public boolean isBroadcastMessage() {
    return (msgType == MessageType.BROADCAST);
  }

  /**
   * Determine if this message is a command.
   *
   * @return True if the message is a command message; false otherwise.
   */
  public boolean isCommandMessage() {
    return (msgType == MessageType.COMMAND);
  }

  /**
   * Determine if this message is sent by a new client to log-in to the server.
   *
   * @return True if the message is an initialization message; false otherwise.
   */
  public boolean isInitialization() {
    return (msgType == MessageType.HELLO);
  }

  /**
   * Determine if this message is a message signing off from the IM server.
   *
   * @return True if the message is sent when signing off; false otherwise
   */
  public boolean terminate() {
    return (msgType == MessageType.QUIT);
  }

  /**
   * Determine if this message is a message trying to authenticate with the IM server.
   *
   * @return True if the message is sent when trying to authenticate; false otherwise
   */
  public boolean isAuthenticate() {
    return (msgType == MessageType.AUTHENTICATE);
  }

  /**
   * Determine if this message is a message trying to register with the IM server.
   *
   * @return True if the message is sent when trying to register; false otherwise
   */
  public boolean isRegister() {
    return (msgType == MessageType.REGISTER);
  }

  /**
   * Representation of this message as a String. This begins with the message handle and then
   * contains the length (as an integer) and the value of the next two arguments.
   *
   * @return Representation of this message as a String.
   */
  @Override
  public String toString() {
    String result = msgType.toString();
    if (msgSender != null) {
      result += " " + msgSender.length() + " " + msgSender;
    } else {
      result += " " + NULL_OUTPUT.length() + " " + NULL_OUTPUT;
    }
    result += " " + String.valueOf(userId).length() + " " + userId;
    if (msgText != null) {
      result += " " + msgText.length() + " " + msgText;
    } else {
      result += " " + NULL_OUTPUT.length() + " " + NULL_OUTPUT;
    }
    return result;
  }

  /**
   * Converts a list of messages into a String that can be used to display to users when a channel
   * change occurs.
   * @param messages the list of messages to parse into a string
   * @return String of all the messages.
   */
  public static String listToString(List<Message> messages) {
    StringBuilder latestMessages = new StringBuilder();
    Message msg;
    for (int i = messages.size() - 1; i >= 0; i--) {
      msg = messages.get(i);
      String nextLine = "\n" + msg.getName() + " : " + msg.getText();
      latestMessages.append(nextLine);
    }
    if (!messages.isEmpty()) {
      latestMessages.append("\n" + StringConstants.LINE_SEPARATOR);
    }
    return latestMessages.toString();
  }
}
