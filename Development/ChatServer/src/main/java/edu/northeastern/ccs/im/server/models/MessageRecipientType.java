package edu.northeastern.ccs.im.server.models;

public enum MessageRecipientType {
  USER("User"), GROUP("Group");

  private final String value;

  MessageRecipientType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
