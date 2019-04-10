package edu.northeastern.ccs.im.server.constants;


/**
 * List of String constants.
 */
public class StringConstants {

  private StringConstants(){}

  public static class ErrorMessages {
    private ErrorMessages(){}
    public static final String NONEXISTING_GROUP = "Your group is nonexistent";

    public static final String NOT_MODERATOR = "You are not a moderator of this group.";

    public static final String ONLY_MODERATOR_FAILURE = "You are the only moderator of the group. " +
            "Please add another moderator before removing yourself from being one.";

    public static final String GENERIC_ERROR = "Something went wrong. Please try again later.";
  }

  public static class CommandDescriptions {
    private CommandDescriptions(){}
    public static final String EIGHTYSIX_DESCRIPTION = "As the moderator, destroys your current " +
            "active group.";
  }

  public static class CommandMessages {
    private CommandMessages(){}
    public static final String EIGHTYSIX_NOTIFICATION = "Group %s has been terminated by " +
            "Moderator %s and is no longer active. You have been redirected to general";
    public static final String EIGHTYSIX_SUCCESS = "You have 86'd the group.";
  }
}
