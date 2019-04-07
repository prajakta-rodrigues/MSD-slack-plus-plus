package edu.northeastern.ccs.im.server.commands;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.northeastern.ccs.im.server.models.MessageHistory;
import edu.northeastern.ccs.im.server.models.User;

/**
 * Help users with privilege to wiretap other users
 */
  class WireTap extends ACommand {

  /**
   * Wiretaps conversation of particular user between given dates
   *
   * @param params include user handle, startdate and enddate
   * @param senderId the id of the user wanting to wiretap
   * @return the conversations of given user.
   */
  @Override
  public String apply(String[] params, Integer senderId) {
    if(null == params || params.length < 3) {
      return "Invalid number of parameters";
    }

    User tappedUser = userRepository.getUserByUserName(params[0]);

    if(null == tappedUser) {
      return "No user found with given user name";
    }
    Timestamp startDate;
    Timestamp endDate;
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      dateFormat.setLenient(false);
      Date parsedDate = dateFormat.parse(params[1]);
      startDate = new Timestamp(parsedDate.getTime());
      parsedDate = dateFormat.parse(params[2]);
      endDate = new Timestamp(parsedDate.getTime());
    } catch (ParseException e) {
      return "Incorrect format specified for dates";
    }
    List<MessageHistory> messages = new ArrayList<>();
    messages.addAll(messageRepository.getDirectMessageHistory(tappedUser.getUserId(), startDate, endDate));
    messages.addAll(messageRepository.getGroupMessageHistory(tappedUser.getUserId(),tappedUser.getUserName(), startDate, endDate));
    Collections.sort(messages);
    StringBuilder str = new StringBuilder("Conversation history for "+ tappedUser.getUserName() + ":\n");
    for(MessageHistory message : messages) {
      str.append(message.toString());
      str.append("\n");
    }
    return str.toString();
  }

  /*
   * Gives description of wiretap method
   * @return the description
   */
  @Override
  public String description() {
    return "Wiretap conversations of a user.Parameters : <handle> <startDate> <endDate> (Date format:mm/dd/yyyy)";
  }
}
