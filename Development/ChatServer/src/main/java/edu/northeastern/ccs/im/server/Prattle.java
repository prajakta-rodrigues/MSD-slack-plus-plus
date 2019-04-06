package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.server.Models.GroupInvitation;
import edu.northeastern.ccs.im.server.Models.InviteesGroup;
import edu.northeastern.ccs.im.server.Models.InvitorsGroup;
import edu.northeastern.ccs.im.server.Models.Message;
import edu.northeastern.ccs.im.server.Models.MessageHistory;
import edu.northeastern.ccs.im.server.Models.Notification;
import edu.northeastern.ccs.im.server.Models.NotificationConvertor;
import edu.northeastern.ccs.im.server.Models.NotificationType;
import edu.northeastern.ccs.im.server.Models.SlackGroup;
import edu.northeastern.ccs.im.server.Models.User;
import edu.northeastern.ccs.im.server.Models.UserType;
import edu.northeastern.ccs.im.server.repositories.FriendRepository;
import edu.northeastern.ccs.im.server.repositories.FriendRequestRepository;
import edu.northeastern.ccs.im.server.repositories.GroupRepository;
import edu.northeastern.ccs.im.server.repositories.UserGroupRepository;
import edu.northeastern.ccs.im.server.utility.DatabaseConnection;
import edu.northeastern.ccs.im.server.repositories.MessageRepository;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import edu.northeastern.ccs.im.server.repositories.DirectMessageRepository;
import edu.northeastern.ccs.im.server.repositories.GroupInviteRepository;
import edu.northeastern.ccs.im.server.repositories.NotificationRepository;
import edu.northeastern.ccs.im.server.repositories.UserRepository;
import edu.northeastern.ccs.im.server.utility.LanguageSupport;

import static edu.northeastern.ccs.im.server.ServerConstants.GENERAL_ID;

/**
 * A network server that communicates with IM clients that connect to it. This version of the server
 * spawns a new thread to handle each client that connects to it. At this point, messages are
 * broadcast to all of the other clients. It does not send a response when the user has gone
 * off-line.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0 International
 * License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/. It
 * is based on work originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 *
 * @version 1.3
 */
public abstract class Prattle {

  /**
   * Don't do anything unless the server is ready.
   */
  private static boolean isReady = false;

  /**
   * The active.
   */
  private static ConcurrentLinkedQueue<ClientRunnable> active;

  /**
   * Collection of threads that are currently being used.
   */
  private static Map<Integer, ClientRunnable> authenticated;

  /**
   * Channels to its members.
   */
  private static Map<Integer, Set<ClientRunnable>> channelMembers;

  /**
   * Repositories holding JDBC queries.
   */
  private static GroupRepository groupRepository;

  /**
   * The dm repository.
   */
  private static DirectMessageRepository dmRepository;

  /**
   * The user repository.
   */
  private static UserRepository userRepository;

  /**
   * The user group repository.
   */
  private static UserGroupRepository userGroupRepository;

  /**
   * The notification repository.
   */
  private static NotificationRepository notificationRepository;

  /**
   * The friend request repository.
   */
  private static FriendRequestRepository friendRequestRepository;

  /**
   * The message repository.
   */
  private static MessageRepository messageRepository;

  /**
   * The friend repository.
   */
  private static FriendRepository friendRepository;

  /**
   * The Constant COMMANDS.
   */
  private static final Map<UserType,Map<String, Command>> COMMANDS;

  private static final Map<String, Command> USER_COMMANDS;
  
  private static final Map<String, Command> GOVT_COMMANDS;
  
  /**
   * The groupInvite Repository;.
   */
  private static GroupInviteRepository groupInviteRepository;

  /**
   * The Language support Instance.
   */
  private static final LanguageSupport languageSupport = LanguageSupport.getInstance();

  /**
   * List of String constants.
   */
  private static final String NONEXISTING_GROUP = "Your group is nonexistent";

  private static final String NOT_MODERATOR = "You are not a moderator of this group.";

  private static final String ONLY_MODERATOR_FAILURE = "You are the only moderator of the group. Please add another moderator before removing yourself from being one.";


  // All of the static initialization occurs in this "method"
  static {
    // Create the new queue of active threads.
    active = new ConcurrentLinkedQueue<>();
    authenticated = new Hashtable<>();
    groupRepository = new GroupRepository();
    dmRepository = new DirectMessageRepository();
    userRepository = new UserRepository();
    userGroupRepository = new UserGroupRepository(DatabaseConnection.getDataSource());
    friendRequestRepository = new FriendRequestRepository(DatabaseConnection.getDataSource());
    friendRepository = new FriendRepository(DatabaseConnection.getDataSource());
    notificationRepository = new NotificationRepository(DatabaseConnection.getDataSource());
    messageRepository = new MessageRepository(DatabaseConnection.getDataSource());
    groupInviteRepository = new GroupInviteRepository(DatabaseConnection.getDataSource());

    channelMembers = new Hashtable<>();
    channelMembers.put(GENERAL_ID, Collections.synchronizedSet(new HashSet<>()));
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
  }

  /**
   * Broadcast a given message to all the other IM clients currently on the system. This message
   * _will_ be sent to the client who originally sent it.
   *
   * @param message Message that the client sent.
   */
  public static void broadcastMessage(Message message) {
    int channelId = message.getChannelId();
    // Loop through all of our active threads
    if (channelMembers.containsKey(channelId)) {
      messageRepository.saveMessage(message);
      for (ClientRunnable tt : channelMembers.get(channelId)) {
        // Do not send the message to any clients that are not ready to receive it.
        if (tt.isInitialized() && message.getChannelId() == tt.getActiveChannelId()) {
          tt.enqueueMessage(message);
        }
      }
    } else {
      ChatLogger.info("Could not find the corresponding channel " + channelId + "\n");
    }
  }

  /**
   * Execute a command based on the message provided.  Message output will only be sent to the
   * client who originally sent it.
   *
   * @param message Message containing the command being executed by the client.
   */
  public static void commandMessage(Message message) {
    String[] messageContents = message.getText().split(" ");
    String command = messageContents[0];
    String commandLower = command.toLowerCase();
    String param =
        messageContents.length > 1 ? message.getText().substring(message.getText().indexOf(' ') + 1)
            : null;
    String[] params = null;
    if (param != null) {
      params = param.split(" ");
    }
    int senderId = message.getUserId();
    
    User user =  userRepository.getUserByUserId(senderId);
    ClientRunnable client = getClient(senderId);
    
    if(client == null || !client.isInitialized()) {
    	return;
    }
    
    if(null == user || null == user.getType()) {
    	client
        .enqueueMessage(Message.makeBroadcastMessage(ServerConstants.SLACKBOT, "User not recognized"));
    	return;
    }
    
    Map<String, Command> commands = COMMANDS.get(user.getType());
    
    if(commands == null) {
    	client
        .enqueueMessage(Message.makeBroadcastMessage(ServerConstants.SLACKBOT, "No commands available"));
    	return;
    }

    String callbackContents = commands.keySet().contains(commandLower)
        ? commands.get(commandLower).apply(params, senderId)
        : String.format("Command %s not recognized", command);
    // send callback message
      client
          .enqueueMessage(Message.makeBroadcastMessage(ServerConstants.SLACKBOT, callbackContents));
  }

  /**
   * get Client by senderId.  To be changed with database integration so not worrying about
   * efficiency right now.
   *
   * @param senderId id of the sender
   * @return Client associated with the senderID
   */
  public static ClientRunnable getClient(int senderId) {
    return authenticated.get(senderId);
  }


  /**
   * Remove the given IM client from the list of active threads.
   *
   * @param dead Thread which had been handling all the I/O for a client who has since quit.
   */
  public static void removeClient(ClientRunnable dead) {
    // Test and see if the thread was in our list of active clients so that we
    // can remove it.

    if (authenticated.remove(dead.getUserId()) == null
        || !active.remove(dead)
        || !channelMembers.get(dead.getActiveChannelId()).remove(dead)) {
      ChatLogger.info("Could not find a thread that I tried to remove!\n");
    }
    userRepository.setActive(false, dead.getUserId());
  }

  /**
   * Terminates the server.
   */
  public static void stopServer() {
    isReady = false;
  }

  /**
   * Registers a ClientRunnable that has successfully logged in.
   *
   * @param toAuthenticate the ClientRunnable that has just logged in
   */
  static void authenticateClient(ClientRunnable toAuthenticate) {
    authenticated.put(toAuthenticate.getUserId(), toAuthenticate);
    channelMembers.get(GENERAL_ID).add(toAuthenticate);
    userRepository.setActive(true, toAuthenticate.getUserId());
  }

  /**
   * Start up the threaded talk server. This class accepts incoming connections on a specific port
   * specified on the command-line. Whenever it receives a new connection, it will spawn a thread to
   * perform all of the I/O with that client. This class relies on the server not receiving too many
   * requests -- it does not include any code to limit the number of extant threads.
   *
   * @param args String arguments to the server from the command line. At present the only legal
   * (and required) argument is the port on which this server should list.
   */
  public static void main(String[] args) {
    // Connect to the socket on the appropriate port to which this server connects.
    try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
      serverSocket.configureBlocking(false);
      serverSocket.socket().bind(new InetSocketAddress(ServerConstants.PORT));
      // Create the Selector with which our channel is registered.
      Selector selector = SelectorProvider.provider().openSelector();
      // Register to receive any incoming connection messages.
      serverSocket.register(selector, SelectionKey.OP_ACCEPT);
      // Create our pool of threads on which we will execute.
      ScheduledExecutorService threadPool = Executors
          .newScheduledThreadPool(ServerConstants.THREAD_POOL_SIZE);
      // If we get this far than the server is initialized correctly
      isReady = true;
      // Now listen on this port as long as the server is ready
      while (isReady) {
        // Check if we have a valid incoming request, but limit the time we may wait.
        while (selector.select(ServerConstants.DELAY_IN_MS) != 0) {
          // Get the list of keys that have arrived since our last check
          Set<SelectionKey> acceptKeys = selector.selectedKeys();
          // Now iterate through all of the keys
          Iterator<SelectionKey> it = acceptKeys.iterator();
          while (it.hasNext()) {
            // Get the next key; it had better be from a new incoming connection
            SelectionKey key = it.next();
            it.remove();
            // Assert certain things I really hope is true
            assert key.isAcceptable();
            assert key.channel() == serverSocket;
            // Create new thread to handle client for which we just received request.
            createClientThread(serverSocket, threadPool);
          }
        }
      }
    } catch (IOException ex) {
      ChatLogger.error("Fatal error: " + ex.getMessage());
      throw new IllegalStateException(ex.getMessage());
    }
  }

  /**
   * Create a new thread to handle the client for which a request is received.
   *
   * @param serverSocket The channel to use.
   * @param threadPool The thread pool to add client to.
   */
  private static void createClientThread(ServerSocketChannel serverSocket,
      ScheduledExecutorService threadPool) {
    try {
      // Accept the connection and create a new thread to handle this client.
      SocketChannel socket = serverSocket.accept();
      // Make sure we have a connection to work with.
      if (socket != null) {
        NetworkConnection connection = new NetworkConnection(socket);
        ClientRunnable tt = new ClientRunnable(connection);
        // Add the thread to the queue of active threads
        active.add(tt);
        // Have the client executed by our pool of threads.
        ScheduledFuture<?> clientFuture = threadPool
            .scheduleAtFixedRate(tt, ServerConstants.CLIENT_CHECK_DELAY,
                ServerConstants.CLIENT_CHECK_DELAY, TimeUnit.MILLISECONDS);
        tt.setFuture(clientFuture);
      }
    } catch (AssertionError ae) {
      ChatLogger.error("Caught Assertion: " + ae.toString());
    } catch (IOException e) {
      ChatLogger.error("Caught Exception: " + e.toString());
    }
  }

  /**
   * Change client channel.
   *
   * @param channelId the channel id
   * @param client the client
   */
  private static void changeClientChannel(int channelId, ClientRunnable client) {
    if (client == null) {
      throw new IllegalArgumentException("Client not found");
    }
    int oldChannel = client.getActiveChannelId();
    client.setActiveChannelId(channelId);
    if (channelMembers.containsKey(channelId)) {
      channelMembers.get(channelId).add(client);
    } else {
      Set<ClientRunnable> channelSet = Collections.synchronizedSet(new HashSet<>());
      channelSet.add(client);
      channelMembers.put(channelId, channelSet);
    }
    channelMembers.get(oldChannel).remove(client);
    if (channelMembers.get(oldChannel).isEmpty()) {
      channelMembers.remove(oldChannel);
    }
  }

  /**
   * Change sender's active channel to the specified Group.
   */
  private static class Group implements Command {

    @Override
    public String apply(String[] params, Integer senderId) {
      List<Message> messages;
      if (params == null || params.length == 0) {
        return "No Group Name provided";
      }
      SlackGroup targetGroup = groupRepository.getGroupByName(params[0]);
      ClientRunnable sender = getClient(senderId);
      if (targetGroup != null) {
        if (!groupRepository.groupHasMember(senderId, targetGroup.getGroupId())) {
          return "You are not a member of this group";
        }
        int channelId = targetGroup.getChannelId();
        try {
          changeClientChannel(channelId, sender);
          messages = messageRepository
              .getLatestMessagesFromChannel(channelId, ServerConstants.LATEST_MESSAGES_COUNT);
        } catch (IllegalArgumentException e) {
          return e.getMessage();
        }
        StringBuilder latestMessages =
            new StringBuilder(
                String.format("Active channel set to Group %s", targetGroup.getGroupName()));
        for (Message msg : messages) {
          String nextLine = "\n" + msg.getName() + " : " + msg.getText();
          latestMessages.append(nextLine);
        }
        if (!messages.isEmpty()) {
          latestMessages.append("\n" + "-------------------------");
        }
        return latestMessages.toString();
      } else {
        return String.format("Group %s does not exist", params[0]);
      }
    }

    @Override
    public String description() {
      return "Change your current chat room to the specified Group.\nParameters: group name";
    }
  }

  /**
   * List all groups on the server.
   */
  private static class Groups implements Command {

    @Override
    public String apply(String[] param, Integer senderId) {
      return groupRepository.groupsHavingMember(senderId);
    }

    @Override
    public String description() {
      return "Print out the names of each Group you are a member of";
    }
  }

  /**
   * Create a Group with the given name.
   */
  private static class CreateGroup implements Command {

    @Override
    public String apply(String[] params, Integer senderId) {
      if (params == null || params.length < 1) {
        return "No Group Name provided";
      }
      if (groupRepository.getGroupByName(params[0]) != null) {
        return "A group with this name already exists";
      }
      if (groupRepository.addGroup(new SlackGroup(senderId, params[0]))) {
        return String.format("Group %s created", params[0]);
      } else {
        return "Something went wrong and your group was not created.";
      }
    }

    @Override
    public String description() {
      return "Create a group with the given name.\nParameters: Group name";
    }
  }

  /**
   * List all active friend on the server.
   */
  private static class Circle implements Command {

    /**
     * Lists all of the active friends on the server.
     *
     * @param params the params
     * @param senderId the id of the sender.
     * @return the list of active friends as a String.
     */
    @Override
    public String apply(String[] params, Integer senderId) {
      List<Integer> friendIds = friendRepository.getFriendsByUserId(senderId);
      StringBuilder activeFriends = new StringBuilder("Active Friends:");
      for (ClientRunnable activeUser : authenticated.values()) {
        int activeUserId = activeUser.getUserId();
        if (friendIds.contains(activeUserId)) {
          activeFriends.append("\n");
          activeFriends.append(activeUser.getName());
        }
      }
      String activeFriendsList = activeFriends.toString();
      if (activeFriendsList.equals("Active Friends:")) {
        activeFriendsList = "No friends are active.";
      }
      return activeFriendsList;
    }

    @Override
    public String description() {
      return "Print out the handles of the active users on the server";
    }
  }

  /**
   * List all available COMMANDS to use.
   */
  private static class Help implements Command {

    /**
     * Lists all of the available COMMANDS.
     *
     * @param params the params
     * @param senderId the id of the sender.
     * @return the list of active users as a String.
     */
    @Override
    public String apply(String[] params, Integer senderId) {
      StringBuilder availableCOMMANDS = new StringBuilder("Available COMMANDS:");
      User user =  userRepository.getUserByUserId(senderId);
      Map<String, Command> commands = COMMANDS.get(user.getType());
      
      for (Map.Entry<String, Command> command : commands.entrySet()) {
        String nextLine = "\n" + command.getKey() + " " + languageSupport
            .getLanguage("english", command.getValue().description());
        availableCOMMANDS.append(nextLine);
      }
      return availableCOMMANDS.toString();
    }

    @Override
    public String description() {
      return "Lists all of the available commands.";
    }
  }

  /**
   * Starts a Dm.
   */
  private static class Dm implements Command {

    /**
     * Starts a direct message with the specified user, if possible.
     *
     * @param params the params
     * @param senderId the id of the sender.
     * @return the list of active users as a String.
     */
    @Override
    public String apply(String[] params, Integer senderId) {
      if (params == null || params.length < 1) {
        return "No user name provided";
      }
      User receiver = userRepository.getUserByUserName(params[0]);
      if (receiver == null) {
        return String.format("User %s not found!", params[0]);
      }
      int receiverId = receiver.getUserId();
      int existingId = dmRepository.getDMChannel(senderId, receiverId);
      int channelId = existingId > 0 ? existingId : dmRepository.createDM(senderId, receiverId);
      ClientRunnable sender = getClient(senderId);
      if (channelId < 0) {
        return "Failed to create direct message. Try again later.";
      } else if (!senderId.equals(receiverId) && !friendRepository
          .areFriends(senderId, receiverId)) {
        return "You are not friends with " + params[0]
            + ". Send them a friend request to direct message.";
      } else {
        try {
          changeClientChannel(channelId, sender);
        } catch (IllegalArgumentException e) {
          return e.getMessage();
        }
        return String.format("You are now messaging %s", params[0]);
      }
    }

    @Override
    public String description() {
      return "Start a DM with the given user.\nParameters: user id";
    }
  }

  /**
   * The Class NotificationHandler handles command notification.
   */
  private static class NotificationHandler implements Command {


    @Override
    public String apply(String[] params, Integer senderId) {

      List<Notification> listNotifications =
          notificationRepository.getAllNotificationsByReceiverId(senderId);
      if (listNotifications == null || listNotifications.isEmpty()) {
        return "No notifications to show";
      }
      String result = NotificationConvertor.getNotificationsAsText(listNotifications);
      notificationRepository.markNotificationsAsNotNew(listNotifications);
      return "Notifications:\n" + result;
    }


    @Override
    public String description() {
      return "Shows recent notifications";
    }

  }

  /**
   * List all the group members in a group.
   */
  private static class GroupMembers implements Command {

    /**
     * Lists all the group members in a group.
     *
     * @param params the params
     * @param senderId the id of the sender.
     * @return the list of active users as a String.
     */
    @Override
    public String apply(String[] params, Integer senderId) {
      ClientRunnable currClient = getClient(senderId);
      int currChannelId = currClient.getActiveChannelId();
      SlackGroup currGroup = groupRepository.getGroupByChannelId(currChannelId);
      if (currGroup == null) {
        return "Your group is non-existent.";
      }
      List<String> mods = userGroupRepository.getModerators(currGroup.getGroupId());
      List<String> queriedMembers = userGroupRepository.getGroupMembers(currGroup.getGroupId());
      StringBuilder groupMembers = new StringBuilder("Group Members:");
      for (String member : queriedMembers) {
        groupMembers.append("\n");
        if (mods.contains(member)) {
          groupMembers.append("*");
        }
        groupMembers.append(member);
      }
      return groupMembers.toString();
    }

    @Override
    public String description() {
      return "Print out the handles of the users in a group.";
    }
  }

  /**
   * The Class SendGroupInvite sends group invite.
   */
  private static class SendGroupInvite implements Command {


    @Override
    public String apply(String[] params, Integer senderId) {
      if (null == params) {
        return "No username or group given";
      }
      SlackGroup group;
      if (params.length == 2) {
        String groupName = params[1];
        group = groupRepository.getGroupByName(groupName);
      } else if (params.length == 1) {
        ClientRunnable currClient = getClient(senderId);
        if (currClient == null) {
          return "Your client is null";
        }
        int currChannelId = currClient.getActiveChannelId();
        group = groupRepository.getGroupByChannelId(currChannelId);
      } else {
        return "Command message not recogized";
      }
      if (null == group) {
        return "Group doesn't exist";
      }
      int groupId = group.getGroupId();

      boolean isModerator;

      try {
        isModerator = userGroupRepository.isModerator(senderId, groupId);
      } catch (SQLException ex) {
        return "Unable to send request";
      }

      if (!isModerator) {
        return "You are not a moderator of given group";
      }

      User user = userRepository.getUserByUserName(params[0]);

      if (null == user) {
        return "Invited user doesn't exist";
      }
      int inviteeId = user.getUserId();

      GroupInvitation groupInvitation =
          new GroupInvitation(senderId, inviteeId, groupId, Timestamp.valueOf(LocalDateTime.now()));
      boolean result = false;
      try {
        result = groupInviteRepository.add(groupInvitation);
      } catch (SQLException e) {
        if (e.getErrorCode() == ErrorCodes.MYSQL_DUPLICATE_PK) {
          return "You have already invited the user";
        }
      }
      if (result) {
        Notification notification = Notification
            .makeGroupInviteNotification(groupId, senderId, inviteeId);
        notificationRepository.addNotification(notification);
        return "Invite sent successfully";
      }
      return "Failed to send invite";
    }

    @Override
    public String description() {
      return "Send out group invite to user.\n Parameters : handle, groupName";
    }
  }

  /**
   * Displays all of a User's friends.
   */
  private static class Friends implements Command {

    /**
     * Lists all of the active users on the server.
     *
     * @param params the params
     * @param senderId the id of the sender.
     * @return the two users being noted as friends as a String.
     */
    @Override
    public String apply(String[] params, Integer senderId) {
      List<Integer> friendIds = friendRepository.getFriendsByUserId(senderId);
      StringBuilder listOfFriends;
      if (friendIds.isEmpty()) {
        listOfFriends = new StringBuilder("You have no friends. :(");
      } else {
        listOfFriends = new StringBuilder("My friends:");
      }
      for (Integer friendId : friendIds) {
        listOfFriends.append("\n");
        listOfFriends.append(userRepository.getUserByUserId(friendId).getUserName());
      }
      return listOfFriends.toString();
    }

    @Override
    public String description() {
      return "Print out the names of all of my friends.";
    }

  }

  /**
   * The Class GroupInvites for checking invitations received.
   */
  private static class GroupInvites implements Command {

    @Override
    public String apply(String[] params, Integer senderId) {
      List<InvitorsGroup> listInvites =
          groupInviteRepository.getGroupInvitationsByInviteeId(senderId);
      StringBuilder result = new StringBuilder();
      result.append("Invitations:\n");
      for (InvitorsGroup invite : listInvites) {
        result.append(String.format("Moderator %s invites you to join group %s",
            invite.getInvitorHandle(), invite.getGroupName()));
        result.append("\n");
      }
      return result.toString();
    }

    @Override
    public String description() {
      return "Check all the group invites received";
    }

  }

  /**
   * The Class GroupSentInvites for checking all sent invitations.
   */
  private static class GroupSentInvites implements Command {

    @Override
    public String apply(String[] params, Integer senderId) {
      List<InviteesGroup> listInvites =
          groupInviteRepository.getGroupInvitationsByInvitorId(senderId);
      StringBuilder result = new StringBuilder();
      result.append("Invitations sent:\n");
      for (InviteesGroup invite : listInvites) {
        result.append(String.format("Invite sent to user %s for group %s",
            invite.getInviteeHandle(), invite.getGroupName()));
        result.append("\n");
      }
      return result.toString();
    }

    @Override
    public String description() {
      return "Displays all the group invites sent by you to other users";
    }

  }

  /**
   * The Class AcceptGroupInvite for accepting group invites.
   */
  private static class AcceptGroupInvite implements Command {

    @Override
    public String apply(String[] params, Integer userId) {

      if (null == params) {
        return "No group specified";
      }

      SlackGroup group = groupRepository.getGroupByName(params[0]);

      if (group == null) {
        return "Specified group doesn't exist";
      }

      boolean result = false;

      try {
        result = groupInviteRepository.acceptInvite(userId, group.getGroupId());
      } catch (SQLException e) {
        if (e.getErrorCode() == ErrorCodes.MYSQL_DUPLICATE_PK) {
          return "You are already part of the group";
        }
      }

      if (result) {
        return "Invite accepted successfully!";
      }
      return "You do not have an invite to the group";
    }

    @Override
    public String description() {
      return "Accepts group invite request. \n Parameters : groupname";
    }
  }

  /**
   * Friends a User.
   */
  private static class Friend implements Command {

    /**
     * Lists all of the active users on the server.
     *
     * @param params the params
     * @param senderId the id of the sender.
     * @return the two users being noted as friends as a String.
     */
    @Override
    public String apply(String[] params, Integer senderId) {
      if (null == params) {
        return "No user specified";
      }

      User newFriend = userRepository.getUserByUserName(params[0]);
      User currUser = userRepository.getUserByUserId(senderId);
      String currUserHandle = currUser.getUserName();
      if (newFriend == null) {
        return "The specified user does not exist.";
      }
      Integer toFriendId = newFriend.getUserId();
      if (senderId.equals(toFriendId)) { // adding oneself as a friend
        return "You cannot be friends with yourself on this app. xD";
      }
      if (friendRepository.areFriends(senderId, toFriendId)) { // already friends
        return "You are already friends with " + params[0] + ".";
      }
      if (friendRequestRepository.hasPendingFriendRequest(senderId, toFriendId)) {
        if (friendRepository.successfullyAcceptFriendRequest(senderId, toFriendId)) {
          Notification friendRequestNotif = Notification
              .makeFriendRequestNotification(senderId, toFriendId,
                  NotificationType.FRIEND_REQUEST_APPROVED);
          notificationRepository.addNotification(friendRequestNotif);
          return currUserHandle + " and " + params[0] + " are now friends.";
        }
        return "Something went wrong and we could not accept " + params[0] + "'s friend request.";
      } else {
        if (friendRequestRepository.successfullySendFriendRequest(senderId, toFriendId)) {
          Notification friendRequestNotif = Notification
              .makeFriendRequestNotification(senderId, toFriendId, NotificationType.FRIEND_REQUEST);
          notificationRepository.addNotification(friendRequestNotif);

          return currUserHandle + " sent " + params[0] + " a friend request.";
        }
        return "You already sent " + params[0] + " a friend request.";
      }
    }

    @Override
    public String description() {
      return "Friends the user with the given handle.\nParameters: User to friend";
    }
  }

  /**
   * Kick a member from a group.
   */
  private static class Kick implements Command {

    /**
     * removes users from the group.
     *
     * @param params the params
     * @param senderId the id of the sender.
     * @return the used removed form group as string.
     */
    @Override
    public String apply(String[] params, Integer senderId) {
      if (params == null) {
        return "You have not specified a member to kick.";
      }
      ClientRunnable mod = getClient(senderId);
      SlackGroup group = groupRepository.getGroupByChannelId(mod.getActiveChannelId());
      if (group == null) {
        return "You must set a group as your active channel to kick a member.";
      }
      boolean isModerator;
      try {
        isModerator = userGroupRepository.isModerator(senderId, group.getGroupId());
      } catch (Exception e) {
        return "Error while checking if you are moderator";
      }

      if (!isModerator) {
        return "You are not the moderator of this group.";
      }

      User toKick = userRepository.getUserByUserName(params[0]);
      if (toKick == null) {
        return "user does not exist";
      }

      if (!groupRepository.groupHasMember(toKick.getUserId(), group.getGroupId())) {
        return String.format("Could not find %s as a member of this group.", params[0]);
      }
      return userGroupRepository.removeMember(group.getGroupId(), toKick.getUserId()) ?
          String.format("User %s successfully kicked from group.", toKick.getUserName()) :
          String.format("Something went wrong. Failed to kick member %s.", toKick.getUserName());
    }

    @Override
    public String description() {
      return "As the moderator of your active group, kick a member from your group.\n" +
          "Parameters: handle of the user to kick";
    }
  }
  
  /**
   * Help users with privilege to wiretap other users
   */
  private static class WireTap implements Command {
    
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
        str.append(message.toString() + "\n");
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
    

  /**
   * Removes a user's moderatorship, if applicable
   */
  private static class Dom implements Command {

    /**
     * Removes a user's moderatorship
     *
     * @param ignoredParams the ignored params
     * @param senderId the id of the user wanting to remove their moderatorship
     * @return an informative message on the result of this command.
     */
    @Override
    public String apply(String[] ignoredParams, Integer senderId) {
      ClientRunnable currClient = getClient(senderId);
      String userHandle = currClient.getName();
      int currChannelId = currClient.getActiveChannelId();
      SlackGroup currGroup = groupRepository.getGroupByChannelId(currChannelId);
      if (currGroup == null) {
        return NONEXISTING_GROUP;
      }
      int currGroupId = currGroup.getGroupId();
      List<String> mods = userGroupRepository.getModerators(currGroupId);
      if (!mods.contains(userHandle)) {
        return NOT_MODERATOR;
      }
      if (mods.size() == 1) {
        return ONLY_MODERATOR_FAILURE;
      }
      userGroupRepository.removeModerator(senderId, currGroupId);
      return userHandle + " removed themself from being a moderator of this group.";
    }

    @Override
    public String description() {
      return "Removes a user's moderatorship.";
    }
  }

  /**
   * Adds a moderator to a group.
   */
  private static class AddModerator implements Command {

    /**
     * Adds a moderator to a group
     *
     * @param params the user being added as a moderator
     * @param senderId the user trying to add a moderator
     * @return an informative message on the result of this command.
     */
    @Override
    public String apply(String[] params, Integer senderId) {
      if (params == null) {
        return "Invalid command parameters.";
      }
      ClientRunnable currClient = getClient(senderId);
      String userHandle = currClient.getName();
      String newModHandle = params[0];
      int currChannelId = currClient.getActiveChannelId();
      SlackGroup currGroup = groupRepository.getGroupByChannelId(currChannelId);
      if (currGroup == null) {
        return NONEXISTING_GROUP;
      }
      int groupId = currGroup.getGroupId();
      List<String> mods = userGroupRepository.getModerators(groupId);
      if (!mods.contains(userHandle)) {
        return NOT_MODERATOR;
      }
      if (!userGroupRepository.getGroupMembers(groupId).contains(newModHandle)) {
        return "The desired user is not part of the group. Send them an invite first.";
      }
      if (mods.contains(newModHandle)) {
        return "The desired user is already a moderator";
      }
      User newMod = userRepository.getUserByUserName(newModHandle);
      int newModId = newMod.getUserId();
      userGroupRepository.addModerator(newModId, groupId);
      Notification modNotification = Notification
          .makeNewModeratorNotification(groupId, senderId, newModId);
      notificationRepository.addNotification(modNotification);
      return userHandle + " added " + newModHandle + " as a moderator of this group.";
    }
    
    @Override
    public String description() {
      return "Adds the given user as a moderator.\nParameters: User to add as a moderator.";
    }
  }
}
