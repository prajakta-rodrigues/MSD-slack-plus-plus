package edu.northeastern.ccs.im.server;

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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashSet;
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

  private static DirectMessageRepository dmRepository;

  private static UserRepository userRepository;

  private static UserGroupRepository userGroupRepository;

  private static NotificationRepository notificationRepository;

  private static FriendRequestRepository friendRequestRepository;

  private static MessageRepository messageRepository;

  private static FriendRepository friendRepository;

  private static final Map<String, Command> COMMANDS;

  /**
   * The groupInvite Repository;
   */
  private static GroupInviteRepository groupInviteRepository;

  /**
   * The Language support Instance.
   */
  private static final LanguageSupport languageSupport = LanguageSupport.getInstance();

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
    COMMANDS.put("/group", new Group());
    COMMANDS.put("/groups", new Groups());
    COMMANDS.put("/creategroup", new CreateGroup());
    COMMANDS.put("/circle", new Circle());
    COMMANDS.put("/dm", new Dm());
    COMMANDS.put("/help", new Help());
    COMMANDS.put("/notification", new NotificationHandler());
    COMMANDS.put("/groupmembers", new GroupMembers());
    COMMANDS.put("/invite", new SendGroupInvite());
    COMMANDS.put("/invites", new GroupInvites());
    COMMANDS.put("/sentinvites", new GroupSentInvites());
    COMMANDS.put("/accept", new AcceptGroupInvite());
    COMMANDS.put("/friend", new Friend());
    COMMANDS.put("/friends", new Friends());
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
    String params[] = null;
    if (param != null) {
      params = param.split(" ");
    }
    int senderId = message.getUserId();

    String callbackContents = COMMANDS.keySet().contains(commandLower)
        ? COMMANDS.get(commandLower).apply(params, senderId)
        : String.format("Command %s not recognized", command);
    // send callback message
    ClientRunnable client = getClient(senderId);
    if (client != null && client.isInitialized()) {
      client
          .enqueueMessage(Message.makeBroadcastMessage(ServerConstants.SLACKBOT, callbackContents));
    }
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
    if (authenticated.remove(dead.getUserId()) != null
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

    /* (non-Javadoc)
     * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
     */
    @Override
    public String apply(String params[], Integer senderId) {
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
        if(!messages.isEmpty()){
          latestMessages.append("\n" + "-------------------------");
        }
        return latestMessages.toString();
      } else {
        return String.format("Group %s does not exist", params[0]);
      }
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.server.Command#description()
     */
    @Override
    public String description() {
      return "Change your current chat room to the specified Group.\nParameters: group name";
    }
  }

  /**
   * List all groups on the server.
   */
  private static class Groups implements Command {

    /* (non-Javadoc)
     * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
     */
    @Override
    public String apply(String param[], Integer senderId) {
      return groupRepository.groupsHavingMember(senderId);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.server.Command#description()
     */
    @Override
    public String description() {
      return "Print out the names of each Group you are a member of";
    }
  }

  /**
   * Create a Group with the given name.
   */
  private static class CreateGroup implements Command {

    /* (non-Javadoc)
     * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
     */
    @Override
    public String apply(String params[], Integer senderId) {
      if (params == null || params.length < 1) {
        return "No Group Name provided";
      }
      if (groupRepository.getGroupByName(params[0]) != null) {
        return "A group with this name already exists";
      }
      if (groupRepository.addGroup(new SlackGroup(senderId, params[0]))) {
        return String.format("Group %s created", params[0]);
      } else {
        // need a better way to handle write failures.
        return "Something went wrong and your group was not created.";
      }
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.server.Command#description()
     */
    @Override
    public String description() {
      return "Create a group with the given name.\nParameters: Group name";
    }
  }

  /**
   * List all active users on the server.
   */
  private static class Circle implements Command {

    /**
     * Lists all of the active users on the server.
     *
     * @param ignoredParam Ignored parameter.
     * @param senderId the id of the sender.
     * @return the list of active users as a String.
     */
    @Override
    public String apply(String params[], Integer senderId) {
      StringBuilder activeUsers = new StringBuilder("Active Users:");
      for (ClientRunnable activeUser : authenticated.values()) {
        activeUsers.append("\n");
        activeUsers.append(activeUser.getName());
      }
      return activeUsers.toString();
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.server.Command#description()
     */
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
     * @param ignoredParam Ignored parameter.
     * @param senderId the id of the sender.
     * @return the list of active users as a String.
     */
    @Override
    public String apply(String params[], Integer senderId) {
      StringBuilder availableCOMMANDS = new StringBuilder("Available COMMANDS:");

      for (Map.Entry<String, Command> command : COMMANDS.entrySet()) {
        String nextLine = "\n" + command.getKey() + " " + languageSupport
            .getLanguage("english", command.getValue().description());
        availableCOMMANDS.append(nextLine);
      }
      return availableCOMMANDS.toString();
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.server.Command#description()
     */
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
     * @param receiverName name of the receiver.
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

    /*
     * (non-Javadoc)
     *
     * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
     */
    @Override
    public String apply(String params[], Integer senderId) {

      List<Notification> listNotifications =
          notificationRepository.getAllNotificationsByReceiverId(senderId);
      if (listNotifications == null || listNotifications.isEmpty()) {
        return "No notifications to show";
      }
      String result = NotificationConvertor.getNotificationsAsText(listNotifications);
      notificationRepository.markNotificationsAsNotNew(listNotifications);
      return "Notifications:\n" + result;
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.northeastern.ccs.im.server.Command#description()
     */
    @Override
    public String description() {
      return "Shows recent notifications";
    }

  }

  /**
   * List all the group members in a group
   */
  private static class GroupMembers implements Command {

    /**
     * Lists all the group members in a group
     *
     * @param ignoredParam Ignored parameter.
     * @param senderId the id of the sender.
     * @return the list of active users as a String.
     */
    @Override
    public String apply(String params[], Integer senderId) {
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
  
  private static class SendGroupInvite implements Command {

    @Override
    public String apply(String params[], Integer senderId) {
      if (null == params) {
        return "No username or group given";
      }

      int groupId = -1;
      if (params.length == 2) {
        String groupName = params[1];
        SlackGroup group = groupRepository.getGroupByName(groupName);
        groupId = group.getGroupId();
      } else if (params.length == 1) {
        ClientRunnable currClient = getClient(senderId);
        if (currClient == null) {
          return "Your client is null";
        }
        int currChannelId = currClient.getActiveChannelId();
        SlackGroup group = groupRepository.getGroupByChannelId(currChannelId);
        if (null == group) {
          return "Group doesn't exist";
        }
        groupId = group.getGroupId();
      } else {
        return "Command message not recogized";
      }
      boolean isModerator = false;

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
        Notification notification = Notification.makeGroupInviteNotification(groupId, senderId, inviteeId);
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
     * @param ignoredParam ignored parameter.
     * @param senderId the id of the sender.
     * @return the two users being noted as friends as a String.
     */
    @Override
    public String apply(String params[], Integer senderId) {
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

  private static class GroupInvites implements Command {

    @Override
    public String apply(String params[], Integer senderId) {
      List<InvitorsGroup> listInvites =
          groupInviteRepository.getGroupInvitationsByInviteeId(senderId);
      StringBuilder result = new StringBuilder();
      result.append("Invitations:\n");
      for (InvitorsGroup invite : listInvites) {
        result.append(String.format("Moderator %s invites you to join group %s",
            invite.getInvitorHandle(), invite.getGroupName()) + "\n");
      }
      return result.toString();
    }

    @Override
    public String description() {
      return "Check all the group invites received";
    }

  }

  private static class GroupSentInvites implements Command {
    @Override
    public String apply(String[] params, Integer senderId) {
      List<InviteesGroup> listInvites =
          groupInviteRepository.getGroupInvitationsByInvitorId(senderId);
      StringBuilder result = new StringBuilder();
      result.append("Invitations sent:\n");
      for (InviteesGroup invite : listInvites) {
        result.append(String.format("Invite sent to user %s for group %s",
            invite.getInviteeHandle(), invite.getGroupName()) + "\n");
      }
      return result.toString();
    }

    @Override
    public String description() {
      return "Displays all the group invites sent by you to other users";
    }

  }

  private static class AcceptGroupInvite implements Command {

    @Override
    public String apply(String params[], Integer userId) {

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
      return "Error while processing request";
  }
   @Override
      public String description() {
        return "Accepts group invite request. \n Parameters : groupname";
      }
  }
  /**
   * Friends a User
   */
  private static class Friend implements Command {

    /**
     * Lists all of the active users on the server.
     *
     * @param toFriend the desired friend's handle
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
}