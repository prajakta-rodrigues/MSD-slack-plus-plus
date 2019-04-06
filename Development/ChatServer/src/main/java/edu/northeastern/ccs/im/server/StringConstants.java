package edu.northeastern.ccs.im.server;


/**
 * List of String constants.
 */
public abstract class StringConstants {

  private StringConstants(){}

  public static final String NONEXISTING_GROUP = "Your group is nonexistent";

  public static final String NOT_MODERATOR = "You are not a moderator of this group.";

  public static final String ONLY_MODERATOR_FAILURE = "You are the only moderator of the group. " +
          "Please add another moderator before removing yourself from being one.";
}
