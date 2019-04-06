package edu.northeastern.ccs.im.server.Models;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;

/**
 * The Enum NotificationType.
 */
public enum NotificationType {

  /**
   * The friend request.
   */
  FRIEND_REQUEST(
      new StringTemplate("$name$ has sent you a friend request.", DefaultTemplateLexer.class)),

  /**
   * The friend request approved.
   */
  FRIEND_REQUEST_APPROVED(
      new StringTemplate("$name$ has accepted your friend request.", DefaultTemplateLexer.class)),

  /**
   * The unread messages.
   */
  UNREAD_MESSAGES(new StringTemplate("You have $count$ unread messages from $name$",
      DefaultTemplateLexer.class)),

  /*Group invite*/
  GROUP_INVITE(new StringTemplate("You have been invited to group $group$ by moderator $name$",
      DefaultTemplateLexer.class)),

  /**
   * New moderator
   */
  NEW_MODERATOR(new StringTemplate("$name$ added you as a moderator for group $group$",
      DefaultTemplateLexer .class));

  /**
   * The text.
   */
  private final StringTemplate text;

  /**
   * Gets the text.
   *
   * @return the text
   */
  public StringTemplate getText() {
    return text;
  }

  /**
   * Instantiates a new notification type.
   *
   * @param text the text
   */
  NotificationType(StringTemplate text) {
    this.text = text;
  }

}
