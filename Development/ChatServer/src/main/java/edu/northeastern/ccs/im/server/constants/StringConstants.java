package edu.northeastern.ccs.im.server.constants;


/**
 * List of String constants.
 */
public class StringConstants {

  private StringConstants() {
  }

  public static final String ENGLISH = "english";
  public static final String ACTIVE_CHANNEL_SET = "Active channel set to Group %s";
  public static final String LINE_SEPARATOR = "-------------------------";
  public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";


  public static class ErrorMessages {

    private ErrorMessages() {
    }

    public static final String GROUP_TAKEN = "A group with this name already exists.";
    public static final String NON_EXISTING_GROUP = "The desired group does not exist.";
    public static final String NON_EXISTING_USER = "The desired user does not exist.";
    public static final String UNSUCCESSFUL_INVITE = "Invite successfully sent.";
    public static final String COMMAND_ALREADY_PROCESSED = "Your request has already been sent before.";
    public static final String FRIEND_ONESELF_ERROR = "You cannot be friends with yourself on this app. xD";
    public static final String INCORRECT_COMMAND_PARAMETERS = "You did not specify the correct parameters.";
    public static final String COMMAND_NOT_RECOGNIZED = "Command not recognized.";
    public static final String NOT_MODERATOR = "You are not a moderator of this group.";
    public static final String ONLY_MODERATOR_FAILURE = "You are the only moderator of the group. Please add another moderator before removing yourself from being one.";
    public static final String GENERIC_ERROR = "Something went wrong. Please try again later.";
    public static final String INCORRECT_DATE_FORMAT = "Incorrect format specified for dates.";
    public static final String INVALID_LANG = "You have to enter a valid language or code. Check /lang command to find the supported languages.";
    public static final String NON_POSITIVE_MESSAGE_NUMBER = "Your message number must be positive.";
    public static final String NOT_ENOUGH_MESSAGES = "Error: You have sent less than the given number of messages to this channel.";
    public static final String NO_NOTIFICATIONS = "No notifications to show.";
    public static final String UNSUCCESSFUL_KICK = "Failed to kick the desired member.";
    public static final String USER_NOT_IN_GROUP = "The desired user is not within the group. Send them an invite first.";
    public static final String CURRENT_USER_NOT_IN_GROUP = "You are not a member of this group.";
    public static final String PASSWRD_REQURIED = "This group requires a password.";
    public static final String INCORRECT_PASSWRD = "Incorrect password.";
    public static final String ALREADY_FRIENDS = "You are already friends with %s.";
    public static final String ALREADY_MODERATOR = "The desired user is already a moderator.";
    public static final String UNSUCCESSFUL_DND = "Unable to set DND.";
    public static final String NOT_FRIENDS = "You are not friends with %s. Send them a friend request to direct message.";
    public static final String NO_ACTIVE_FRIENDS = "No friends are active.";
    public static final String ALREADY_IN_GROUP = "You are already part of the group.";
    public static final String NO_INVITE = "You do not have an invite to the group.";
    public static final String FAILED_SET_PARENTAL_CONTROL = "Unable to set parental control mode";
    public static final String NO_SEARCH_TERM = "Please enter a search term to find similar usernames";
  }

  public static class CommandDescriptions {

    private CommandDescriptions() {
    }
    public static final String ACCEPT_GROUP_INVITE_DESCRIPTION = "Accepts group invite request. Parameters : group name.";
    public static final String ADD_MODERATOR_DESCRIPTION = "Adds the given user as a moderator.Parameters: User to add as a moderator.";
    public static final String CIRCLE_DESCRIPTION = "Print out the handles of the active users on the server.";
    public static final String CREATE_GROUP_DESCRIPTION = "Create a group with the given name.Parameters: Group name, (optional) password.";
    public static final String DM_DESCRIPTION = "Start a DM with the given user. Parameters: user id.";
    public static final String DND_DESCRIPTION = "Sets Do not disturb mode and no notifications will be shown. Parameters: true.";
    public static final String DOM_DESCRIPTION = "Removes a user's moderatorship.";
    public static final String EIGHTY_SIX_DESCRIPTION = "As the moderator, destroys your current active group.";
    public static final String FRIEND_DESCRIPTION = "Friends the user with the given handle. Parameters: User to friend.";
    public static final String FRIENDS_DESCRIPTION = "Print out the names of all of my friends.";
    public static final String GROUP_DESCRIPTION = "Change your current chat room to the specified Group. Parameters: group name, (if locked) password.";
    public static final String GROUP_INVITES_DESCRIPTION = "Check all the group invites received.";
    public static final String GROUP_MEMBERS_DESCRIPTION = "Print out the handles of the users in a group.";
    public static final String GROUPS_DESCRIPTION = "Print out the names of each Group you are a member of.";
    public static final String GROUP_SENT_INVITES_DESCRIPTIONS = "Displays all the group invites sent by you to other users.";
    public static final String HELP_DESCRIPTION = "Lists all of the available commands.";
    public static final String KICK_DESCRIPTION = "As the moderator of your active group, kick a member from your group. Parameters: handle of the user to kick.";
    public static final String LANGUAGES_DESCRIPTION = "Find all the available languages which you can use /translate on.";
    public static final String NOTIFICATION_DESCRIPTION = "Shows recent notifications.";
    public static final String RECALL_DESCRIPTION = "Recalls a message based on the given number. Parameters: the number of most recently sent message to recall.";
    public static final String SEND_GROUP_INVITE_DESCRIPTION = "Send out group invite to user. Parameters : handle, groupName.";
    public static final String TRANSLATE_DESCRIPTION = "You can translate any sentence. Parameters: language to translate it to.";
    public static final String WIRETAP_DESCRIPTION = "Wiretap conversations of a user. Parameters: <handle> <startDate> <endDate> (Date format:" + DATE_FORMAT_STRING + ").";
    public static final String PARENTAL_CONTROL_DESCRIPTION = "You can set the parental control mode on to filter content";
    public static final String SEARCH_DESCRIPTION = "Search for users starting with similar search term.Parameters: search term.";
  }

  public static class CommandMessages {

    private CommandMessages() {
    }

    public static final String EIGHTY_SIX_NOTIFICATION = "Group %s has been terminated by Moderator %s and is no longer active. You have been redirected to general.";
    public static final String EIGHTY_SIX_SUCCESS = "You have 86'd the group.";
    public static final String SUCCESSFUL_INVITE = "Invite successfully sent.";
    public static final String SUCCESSFUL_INVITE_ACCEPT = "Invite accepted successfully!";
    public static final String SUCCESSFUL_RECALL = "You have recalled the desired message.";
    public static final String SUCCESSFUL_KICK = "Successfully kicked the desired user from the group.";
    public static final String INVITES_SENT = "Invite sent to user %s for group %s.";
    public static final String GROUP_INVITES = "Moderator %s invites you to join group %s.";
    public static final String SUCCESSFUL_FRIEND_REQUEST_SENT = "%s sent %s a friend request.";
    public static final String NEW_FRIENDS = "%s and %s are now friends.";
    public static final String SUCCESSFUL_DOM = "%s removed themselves from being a moderator of this group.";
    public static final String SUCCESSFUL_DND = "Set DND mode to %s.";
    public static final String SUCCESSFUL_DM = "You are now messaging %s.";
    public static final String SUCCESSFUL_GROUP_CREATED = "Group %s created.";
    public static final String SUCCESSFUL_MODERATOR_ADD = "%s added %s as a moderator of this group.";
    public static final String JOIN_WITH_PASS = " Join group with password: ";
    public static final String SUCCESSFUL_PARENTAL_CONTROL = "Parental control Mode set to %s.";
    public static final String NO_USERS_FOUND = "No users found";
    public static final String USERS_FOUND = "Users with similar names are:"; 
  }

  public static class NotificationMessages {

    private NotificationMessages() {
    }

    public static final String DELETED_GROUP_TAG = "  DELETED";
    public static final String GROUP_STRING = "group";
    public static final String NAME_STRING = "name";
  }

}
