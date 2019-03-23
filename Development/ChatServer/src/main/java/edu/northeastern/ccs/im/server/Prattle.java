package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.server.repositories.GroupRepository;
import edu.northeastern.ccs.im.server.repositories.UserRepository;
import edu.northeastern.ccs.im.server.utility.DatabaseConnection;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
   * Collection of threads that are currently being used.
   */
  private static ConcurrentLinkedQueue<ClientRunnable> active;

  /**
   * Collection of groups that are on the server.
   */
  private static ConcurrentLinkedQueue<SlackGroup> groups;

  /**
   * Factory for making instances of direct message sessions and groups
   */
  private static ChannelFactory channelFactory;

  private static final Map<String, Command> commands;

  // All of the static initialization occurs in this "method"
  static {
    // Create the new queue of active threads.
    active = new ConcurrentLinkedQueue<>();
    groups = new ConcurrentLinkedQueue<>();
    channelFactory = ChannelFactory.makeFactory();
    groups.add(channelFactory.makeGroup(null, "general"));
    // Populate the known commands
    commands = new Hashtable<>();
    commands.put("/group", new Group());
    commands.put("/groups", new Groups());
    commands.put("/creategroup", new CreateGroup());
    commands.put("/circle", new Circle());
    commands.put("/dm", new Dm());
    commands.put("/help", new Help());
    commands.put("/groupmembers", new GroupMembers());
  }

  /**
   * Broadcast a given message to all the other IM clients currently on the system. This message
   * _will_ be sent to the client who originally sent it.
   *
   * @param message Message that the client sent.
   */
  public static void broadcastMessage(Message message) {
    // Loop through all of our active threads
    for (ClientRunnable tt : active) {
      // Do not send the message to any clients that are not ready to receive it.
      if (tt.isInitialized() && message.getChannelId() == tt.getActiveChannelId()) {
        tt.enqueueMessage(message);
      }
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
    String param = messageContents.length > 1 ? messageContents[1] : null;
    String senderId = message.getName();

    String callbackContents = commands.keySet().contains(commandLower)
        ? commands.get(commandLower).apply(param, senderId)
        : String.format("Command %s not recognized", command);
    // send callback message
    ClientRunnable client = getClient(senderId);
    if (client != null && client.isInitialized()) {
      client.enqueueMessage(Message.makeBroadcastMessage("SlackBot", callbackContents));
    }
  }

  /**
   * get Client by senderId.  To be changed with database integration so not worrying about
   * efficiency right now.
   *
   * @param senderId id of the sender
   * @return Client associated with the senderID
   */
  public static ClientRunnable getClient(String senderId) {
    for (ClientRunnable client : active) {
      if (client.getName().equals(senderId)) {
        return client;
      }
    }
    return null;
  }

  /**
   * get Channel by channelId. To be changed with database integration so not worrying about
   * efficiency right now.
   *
   * @param channelId id of the desired channel
   * @return Client associated with the senderID
   */
  public static SlackGroup getGroupByChannelId(int channelId) {
    for (SlackGroup g : groups) {
      if (g.getChannelId() == channelId) {
        return g;
      }
    }
    return null;
  }


  /**
   * Remove the given IM client from the list of active threads.
   *
   * @param dead Thread which had been handling all the I/O for a client who has since quit.
   */
  public static void removeClient(ClientRunnable dead) {
    // Test and see if the thread was in our list of active clients so that we
    // can remove it.
    if (!active.remove(dead)) {
      ChatLogger.info("Could not find a thread that I tried to remove!\n");
    }
  }

  /**
   * Terminates the server.
   */
  public static void stopServer() {
    isReady = false;
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
   * Change sender's active channel to the specified Group.
   */
  private static class Group implements Command {

    @Override
    public String apply(String groupName, String senderId) {
      if (groupName == null) {
        return "No Group Name provided";
      }
      SlackGroup targetGroup = getGroup(groupName);
      ClientRunnable sender = getClient(senderId);
      if (groupName.length() > 3 && groupName.substring(0, 3).equals("DM:") && !groupName
          .contains(senderId)) {
        return "You are not authorized to use this DM";
      }
      if (targetGroup != null)

      {
        if (sender != null) {
          sender.setActiveChannelId(targetGroup.getChannelId());
          return String.format("Active channel set to Group %s", groupName);
        } else {
          return "Sender not found";
        }
      } else

      {
        return String.format("Group %s does not exist", groupName);
      }
    }

    /**
     * get Group by groupName.  To be changed with database integration.
     *
     * @param groupName name of the group
     * @return Group associated with the groupName
     */
    private static SlackGroup getGroup(String groupName) {
      for (SlackGroup group : groups) {
        if (group.getGroupName().equals(groupName)) {
          return group;
        }
      }
      return null;
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
    public String apply(String param, String senderId) {
      StringBuilder groupNames = new StringBuilder();
      for (SlackGroup group : groups) {
        groupNames.append(String.format("%n%s", group.getGroupName()));
      }
      return groupNames.toString();
    }

    @Override
    public String description() {
      return "Print out the names of each available Group on the server";
    }
  }

  /**
   * Create a Group with the given name.
   */
  private static class CreateGroup implements Command {

    @Override
    public String apply(String groupName, String senderId) {
      if (groupName == null) {
        return "No Group Name provided";
      }
      try {
        groups.add(channelFactory.makeGroup(senderId, groupName));
        return String.format("Group %s created", groupName);
      } catch (IllegalArgumentException e) {
        return e.getMessage();
      }
    }

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
    public String apply(String ignoredParam, String senderId) {
      StringBuilder activeUsers = new StringBuilder("Active Users:");
      for (ClientRunnable activeUser : active) {
        activeUsers.append("\n");
        activeUsers.append(activeUser.getName());
      }
      return activeUsers.toString();
    }

    @Override
    public String description() {
      return "Print out the handles of the active users on the server";
    }
  }

  /**
   * List all available commands to use.
   */
  private static class Help implements Command {

    /**
     * Lists all of the available commands.
     *
     * @param ignoredParam Ignored parameter.
     * @param senderId the id of the sender.
     * @return the list of active users as a String.
     */
    @Override
    public String apply(String ignoredParam, String senderId) {
      StringBuilder availableCommands = new StringBuilder("Available Commands:");
      for (Map.Entry<String, Command> command : commands.entrySet()) {
        String nextLine = "\n" + command.getKey() + " " + command.getValue().description();
        availableCommands.append(nextLine);
      }
      return availableCommands.toString();
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
     * Lists all of the active users on the server.
     *
     * @param userId Ignored parameter.
     * @param senderId the id of the sender.
     * @return the list of active users as a String.
     */
    @Override
    public String apply(String userId, String senderId) {
      if (userId == null) {
        return "No user provided to direct message.";
      }
      if (!active.contains(getClient(userId))) {
        return "The provided user is not active";
      }
      try {
        String groupName = "DM:" + senderId + "-" + userId;
        groups.add(channelFactory.makeGroup(senderId, groupName));
        return String.format("%s created", groupName);
      } catch (IllegalArgumentException e) {
        return e.getMessage();
      }
    }

    @Override
    public String description() {
      return "Start a DM with the given user.\nParameters: user id";
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
    public String apply(String ignoredParam, String senderId) {
      ClientRunnable currClient = getClient(senderId);
      if (currClient == null) {
        return "Your client is null";
      }
      int currChannelId = currClient.getActiveChannelId();
      SlackGroup currGroup = getGroupByChannelId(currChannelId);
      if (currGroup == null) {
        return "Your group is non-existent";
      }
      List<String> mods = currGroup.getModerators();
      GroupRepository groupRepo = new GroupRepository(DatabaseConnection.getDataSource());
      List<String> queriedMembers = groupRepo.getGroupMembers(currChannelId);
      StringBuilder groupMembers = new StringBuilder("Group Members:");
      for (String member : queriedMembers) {
        groupMembers.append("\n");
        if (mods.contains(member)) {
          groupMembers.append("*");
        }
        groupMembers.append("member");
      }
      return groupMembers.toString();
    }

    @Override
    public String description() {
      return "Print out the handles of the active users on the server";
    }
  }
}
