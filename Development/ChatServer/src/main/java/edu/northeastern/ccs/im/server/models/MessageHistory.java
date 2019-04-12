package edu.northeastern.ccs.im.server.models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class MessageHistory implements Comparable<MessageHistory> {

  private final String receiverName;
  private final MessageRecipientType receiverType;
  private final String senderName;
  private final MessageRecipientType senderType;
  private final String text;
  private final Timestamp sentDate;

  public MessageHistory(String receiverName, MessageRecipientType receiver, String senderName,
      MessageRecipientType sender, String text, Timestamp sentDate) {
    super();
    this.receiverName = receiverName;
    this.senderName = senderName;
    this.text = text;
    this.sentDate = sentDate;
    this.receiverType = receiver;
    this.senderType = sender;
  }

  public String getReceiverName() {
    return receiverName;
  }

  public String getSenderName() {
    return senderName;
  }

  public String getText() {
    return text;
  }

  private Timestamp getSentDate() {
    return sentDate;
  }

  public MessageRecipientType getSender() {
    return senderType;
  }

  public MessageRecipientType getReceiver() {
    return receiverType;
  }

  @Override
  public String toString() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    StringBuilder str = new StringBuilder();
    str.append(sdf.format(sentDate));
    str.append(" ");
    str.append(senderType.getValue());
    str.append(" ");
    str.append(senderName);
    str.append(" sent ");
    str.append(receiverType.getValue());
    str.append(" ");
    str.append(receiverName);
    str.append(" : ");
    str.append(text);
    return str.toString();
  }

  @Override
  public int compareTo(MessageHistory messageHistory) {
    return this.sentDate.compareTo(messageHistory.getSentDate());
  }

}
