package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import edu.northeastern.ccs.im.server.commands.Command;
import edu.northeastern.ccs.im.server.commands.CommandFactory;
import edu.northeastern.ccs.im.server.constants.ServerConstants;
import edu.northeastern.ccs.im.server.models.Message;
import edu.northeastern.ccs.im.server.models.User;
import edu.northeastern.ccs.im.server.models.UserType;
import edu.northeastern.ccs.im.server.repositories.MessageRepository;
import edu.northeastern.ccs.im.server.repositories.RepositoryFactory;
import edu.northeastern.ccs.im.server.repositories.UserRepository;
import edu.northeastern.ccs.im.server.utility.ChatLogger;
import edu.northeastern.ccs.im.server.utility.FilterWords;
import static edu.northeastern.ccs.im.server.constants.ServerConstants.GENERAL_ID;

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
   * The user repository.
   */
  private static UserRepository userRepository;

  /**
   * The message repository.
   */
  private static MessageRepository messageRepository;

  /**
   * Constant COMMANDS.
   */
  private static final Map<UserType, Map<String, Command>> COMMANDS;


  // All of the static initialization occurs in this "method"
  static {
    // Create the new queue of active threads.
    active = new ConcurrentLinkedQueue<>();
    authenticated = new Hashtable<>();

    userRepository = RepositoryFactory.getUserRepository();
    messageRepository = RepositoryFactory.getMessageRepository();

    channelMembers = new Hashtable<>();
    channelMembers.put(GENERAL_ID, Collections.synchronizedSet(new HashSet<>()));

    COMMANDS = CommandFactory.getCommands();
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

    User user = userRepository.getUserByUserId(senderId);
    ClientRunnable client = getClient(senderId);

    if (client == null || !client.isInitialized()) {
      return;
    }

    if (null == user || null == user.getType()) {
      client
          .enqueueMessage(
              Message.makeBroadcastMessage(ServerConstants.SLACKBOT, "User not recognized"));
      return;
    }

    Map<String, Command> commands = COMMANDS.get(user.getType());

    if (commands == null) {
      client
          .enqueueMessage(
              Message.makeBroadcastMessage(ServerConstants.SLACKBOT, "No commands available"));
      return;
    }

    String callbackContents = commands.keySet().contains(commandLower)
        ? commands.get(commandLower).apply(params, senderId)
        : ErrorMessages.COMMAND_NOT_RECOGNIZED;
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
   * Get all clients on the server that have been authenticated (successfully logged in).
   *
   * @return Authenticated clientRunnables
   */
  public static Collection<ClientRunnable> getAuthenticatedClients() {
    return authenticated.values();
  }

  /**
   * Get all active clients in the specified channel.
   * @param channelId the channelId.
   * @return the clients in the specified channel.
   */
  public static Collection<ClientRunnable> getChannelClients(int channelId) {
    return channelMembers.getOrDefault(channelId, new HashSet<>());
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
   * @param userType type of the user.
   */
  static void authenticateClient(ClientRunnable toAuthenticate, UserType userType) {
    authenticated.put(toAuthenticate.getUserId(), toAuthenticate);
    if (userType.equals(UserType.GENERAL)) {
      if(!channelMembers.containsKey(GENERAL_ID)) {
        channelMembers.put(GENERAL_ID, Collections.synchronizedSet(new HashSet<ClientRunnable>()));
      }
      Set<ClientRunnable> channelSet = channelMembers.get(GENERAL_ID);
      channelSet.add(toAuthenticate);
    }
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
  public static void changeClientChannel(int channelId, ClientRunnable client) {
    int oldChannel = client.getActiveChannelId();
    if (channelId != oldChannel) {
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
  }
}
