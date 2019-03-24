package edu.northeastern.ccs.im.server;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;

public enum NotificationType {

  FRIEND_REQUEST(new StringTemplate("$name$ has sent you a friend request.",DefaultTemplateLexer.class)), 
  FRIEND_REQUEST_APPROVED(new StringTemplate("$name$ has accepted your friend request.",DefaultTemplateLexer.class)), 
  UNREAD_MESSAGES(new StringTemplate("You have $count$ unread messages from $name$",DefaultTemplateLexer.class));

  private final StringTemplate text;

  public StringTemplate getText() {
    return text;
  }

  private NotificationType(StringTemplate text) {
    this.text = text;
  }
  
}
