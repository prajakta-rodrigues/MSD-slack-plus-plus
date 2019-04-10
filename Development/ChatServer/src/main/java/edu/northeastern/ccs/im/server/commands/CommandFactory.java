package edu.northeastern.ccs.im.server.commands;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import edu.northeastern.ccs.im.server.models.UserType;

/**
 * Helper class to instantiate a Map of all the supported commands.
 */
public abstract class CommandFactory {

  private CommandFactory(){}

  /**
   * The Constant COMMANDS.
   */
  private static final Map<UserType, Map<String, Command>> COMMANDS;

  private static final Map<String, Command> USER_COMMANDS;

  private static final Map<String, Command> GOVT_COMMANDS;

  static {
    // Populate the known COMMANDS
    COMMANDS = new Hashtable<>();
    USER_COMMANDS = new Hashtable<>();
    GOVT_COMMANDS = new Hashtable<>();
    COMMANDS.put(UserType.GENERAL, USER_COMMANDS);
    COMMANDS.put(UserType.GOVERNMENT, GOVT_COMMANDS);
    USER_COMMANDS.put("/group", new Group());
    USER_COMMANDS.put("/groups", new Groups());
    USER_COMMANDS.put("/creategroup", new CreateGroup());
    USER_COMMANDS.put("/circle", new Circle());
    USER_COMMANDS.put("/dm", new Dm());
    USER_COMMANDS.put("/help", new Help());
    USER_COMMANDS.put("/notification", new NotificationHandler());
    USER_COMMANDS.put("/groupmembers", new GroupMembers());
    USER_COMMANDS.put("/invite", new SendGroupInvite());
    USER_COMMANDS.put("/invites", new GroupInvites());
    USER_COMMANDS.put("/sentinvites", new GroupSentInvites());
    USER_COMMANDS.put("/accept", new AcceptGroupInvite());
    USER_COMMANDS.put("/friend", new Friend());
    USER_COMMANDS.put("/friends", new Friends());
    USER_COMMANDS.put("/kick", new Kick());
    GOVT_COMMANDS.put("/wiretap", new WireTap());
    GOVT_COMMANDS.put("/help", new Help());
    USER_COMMANDS.put("/dom", new Dom());
    USER_COMMANDS.put("/addmoderator", new AddModerator());
    USER_COMMANDS.put("/86", new EightySix());
    USER_COMMANDS.put("/dnd", new Dnd());
    USER_COMMANDS.put("/search", new SearchUsers());
    USER_COMMANDS.put("/translate", new Translate());
    USER_COMMANDS.put("/lang", new Languages());
  }

  public static Map<UserType, Map<String, Command>> getCommands() {
    return Collections.unmodifiableMap(COMMANDS);
  }
}
