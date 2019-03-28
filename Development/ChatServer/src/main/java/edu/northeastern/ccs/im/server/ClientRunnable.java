package edu.northeastern.ccs.im.server;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import org.mindrot.jbcrypt.BCrypt;
import edu.northeastern.ccs.im.server.repositories.NotificationRepository;
import edu.northeastern.ccs.im.server.repositories.UserRepository;
import edu.northeastern.ccs.im.server.utility.DatabaseConnection;

import static edu.northeastern.ccs.im.server.ServerConstants.GENERAL_ID;

/**
 * Instances of this class handle all of the incoming communication from a single IM client.
 * Instances are created when the client signs-on with the server. After instantiation, it is
 * executed periodically on one of the threads from the thread pool and will stop being run only
 * when the client signs off.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0 International
 * License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/. It
 * is based on work originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 *
 * @version 1.3
 */
public class ClientRunnable implements Runnable {

  /**
   * Utility class which we will use to send and receive communication to this client.
   */
  private NetworkConnection connection;

  /**
   * Id for the active channel that the client is sending messages to.
   */
  private int activeChannelId = GENERAL_ID;

  /**
   * Id for the user for whom we use this ClientRunnable to communicate.
   */
  private int userId;

  /**
   * Name that the client used when connecting to the server.
   */
  private String name;

  /**
   * Whether this client has been initialized, set its user name, and is ready to receive messages.
   */
  private boolean initialized;

  /**
   * Whether this client has been terminated, either because he quit or due to prolonged inactivity.
   */
  private boolean terminate;

  /**
   * The timer that keeps track of the clients activity.
   */
  private ClientTimer timer;

  /**
   * The future that is used to schedule the client for execution in the thread pool.
   */
  private ScheduledFuture<?> runnableMe;

  /**
   * Collection of messages queued up to be sent to this client.
   */
  private Queue<Message> waitingList;

  private final UserRepository userRepository;
  
  private NotificationRepository notificationRepository;

  /**
   * Whether this client has been authenticated to send messages to other users
   */
  private boolean authenticated;

  /**
   * Create a new thread with which we will communicate with this single client.
   *
   * @param network NetworkConnection used by this new client
   */
  public ClientRunnable(NetworkConnection network) {
    // Create the class we will use to send and receive communication
    connection = network;
    // Mark that we are not initialized
    initialized = false;
    // Mark that we are not terminated
    terminate = false;
    // Create the queue of messages to be sent
    waitingList = new ConcurrentLinkedQueue<>();
    // Mark that the client is active now and start the timer until we
    // terminate for inactivity.
    timer = new ClientTimer();

    authenticated = false;

    userRepository = new UserRepository();
    notificationRepository = new NotificationRepository(DatabaseConnection.getDataSource());
  }

  /**
   * Check to see for an initialization attempt and process the message sent.
   */
  protected void checkForInitialization() {
    // Check if there are any input messages to read
    Iterator<Message> messageIter = connection.iterator();
    if (messageIter.hasNext()) {
      // If a message exists, try to use it to initialize the connection
      Message msg = messageIter.next();
      Message sendMsg;
      if (null == msg.getName()) {
        return;
      }
      if (userExists(msg.getName())) {
        sendMsg =
            Message.makeBroadcastMessage(ServerConstants.BOUNCER_ID, "Enter Password for user");
      } else {
        sendMsg = Message.makeBroadcastMessage(ServerConstants.BOUNCER_ID,
            "User is not registered with system. Enter Password for user");
      }

      initialized = true;
      // Update the time until we terminate this client due to inactivity.
      timer.updateAfterInitialization();
      enqueueMessage(sendMsg);
      setName(msg.getName());
      authenticated = false;
    }
  }

  private void authenticateUser(Message msg) {
    Message sendMsg;
    User user = userRepository.getUserByUserName(msg.getName());
    if (user == null) {
      sendMsg = Message.makeBroadcastMessage(ServerConstants.SLACKBOT, "Illegal Message");
      enqueueMessage(sendMsg);
      return;
    }
    if (BCrypt.checkpw(msg.getText(), user.getPassword())) {
      setName(user.getUserName());
      userId = user.getUserId();
      Prattle.authenticateClient(this);
      // Set that the client is initialized.
      authenticated = true;
      sendMsg = Message.makeBroadcastMessage(ServerConstants.SLACKBOT,
          "Succesful login. Continue to message");
    } else {
      sendMsg = Message.makeBroadcastMessage(ServerConstants.BOUNCER_ID,
          "Wrong password for given username. Try again.");
      authenticated = false;
    }
    enqueueMessage(sendMsg);
  }

  private boolean userExists(String userName) {
    return null != userRepository.getUserByUserName(userName);
  }

  /**
   * Check if the message is properly formed. At the moment, this means checking that the identifier
   * is set properly.
   *
   * @param msg Message to be checked
   * @return True if message is correct; false otherwise
   */
  private boolean messageChecks(Message msg) {
    // Check that the message name matches.
    return (msg.getName() != null) && (msg.getName().compareToIgnoreCase(getName()) == 0);
  }

  /**
   * Immediately send this message to the client. This returns if we were successful or not in our
   * attempt to send the message.
   *
   * @param message Message to be sent immediately.
   * @return True if we sent the message successfully; false otherwise.
   */
  protected boolean sendMessage(Message message) {
    ChatLogger.info("\t" + message);
    return connection.sendMessage(message);
  }

  /**
   * Add the given message to this client to the queue of message to be sent to the client.
   *
   * @param message Complete message to be sent.
   */
  void enqueueMessage(Message message) {
    waitingList.add(message);
  }

  /**
   * Get the name of the user for which this ClientRunnable was created.
   *
   * @return Returns the name of this client.
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name of the user for which this ClientRunnable was created.
   *
   * @param name The name for which this ClientRunnable.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the name of the user for which this ClientRunnable was created.
   *
   * @return Returns the current value of userName.
   */
  public int getUserId() {
    return userId;
  }

  /**
   * Return if this thread has completed the initialization process with its client and is read to
   * receive messages.
   *
   * @return True if this thread's client should be considered; false otherwise.
   */
  public boolean isInitialized() {
    return initialized;
  }

  /**
   * Perform the periodic actions needed to work with this client.
   *
   * @see java.lang.Thread#run()
   */
  public void run() {
    // The client must be initialized before we can do anything else
    if (!initialized) {
      checkForInitialization();
    } else {
      handleIncomingMessages();
      handleNotifications();
      handleOutgoingMessages();
    }
    // Finally, check if this client have been inactive for too long and,
    // when they have, terminate the client.
    if (timer.isBehind()) {
      ChatLogger.error("Timing out or forcing off a user " + name);
      terminate = true;
    }
    if (terminate) {
      terminateClient();
    }
  }

  /**
   * Checks for new notifications for user.
   */
  private void handleNotifications() {
    List<Notification> listNotifications = notificationRepository.getAllNewNotificationsByReceiverId(userId);
    if(listNotifications != null && !listNotifications.isEmpty()) {
      Message sendMsg;
      sendMsg = Message.makeBroadcastMessage(ServerConstants.SLACKBOT,
          "You have new notifications: \n" + NotificationConvertor.getNotificationsAsText(listNotifications));
      enqueueMessage(sendMsg);
      notificationRepository.markNotificationsAsNotNew(listNotifications);
    }
  }

  /**
   * Checks incoming messages and performs appropriate actions based on the type of message.
   */
  private void handleIncomingMessages() {
    // Client has already been initialized, so we should first check
    // if there are any input
    // messages.
    Iterator<Message> messageIter = connection.iterator();
    if (messageIter.hasNext()) {
      // Get the next message
      Message msg = messageIter.next();
      // If the message is a broadcast message, send it out
      if (msg.terminate()) {
        // Stop sending the poor client message.
        terminate = true;
        // Reply with a quit message.
        enqueueMessage(Message.makeQuitMessage(name));
      } else {
        // Check if the message is legal formatted
        if (messageChecks(msg)) {
          respondToMessage(msg);
        } else {
          Message sendMsg;
          sendMsg = Message.makeBroadcastMessage(ServerConstants.BOUNCER_ID,
              "Last message was rejected because it specified an incorrect user name.");
          enqueueMessage(sendMsg);
        }
      }
    }
  }

  private void respondToMessage(Message msg) {
    // Check for our "special messages"
    if (authenticated && msg.isBroadcastMessage()) {
      // Check for our "special messages"
      Prattle.broadcastMessage(msg);
    } else if (authenticated && msg.isCommandMessage()) {
      Prattle.commandMessage(msg);
    } else if (msg.isAuthenticate()) {
      authenticateUser(msg);
    } else if (msg.isRegister()) {
      registerUser(msg);
    }
  }

  private void registerUser(Message msg) {
    Message sendMsg;
    int id = (msg.getName().hashCode() & 0xfffffff);
    String hashedPwd = BCrypt.hashpw(msg.getText(), BCrypt.gensalt(8));
    boolean result = userRepository.addUser(new User(id, msg.getName(), hashedPwd));
    if (result) {
      sendMsg = Message.makeBroadcastMessage(ServerConstants.SLACKBOT,
          "Registration done. Continue to message.");
      setName(msg.getName());
      userId = id;
      Prattle.authenticateClient(this);
      authenticated = true;

    } else {
      sendMsg = Message.makeBroadcastMessage(ServerConstants.SLACKBOT,
          "Registration failed. Try connecting after some time.");
    }

    enqueueMessage(sendMsg);
  }

  /**
   * Sends the enqueued messages to the printer and makes sure they were sent out.
   */
  private void handleOutgoingMessages() {
    // Check to make sure we have a client to send to.
    boolean keepAlive = true;
    if (!waitingList.isEmpty()) {
      keepAlive = false;
      // Send out all of the message that have been added to the
      // queue.
      do {
        Message msg = waitingList.remove();
        boolean sentGood = sendMessage(msg);
        keepAlive |= sentGood;
        // Update the time until we terminate the client for inactivity.
        timer.updateAfterActivity();

      } while (!waitingList.isEmpty());
    }
    terminate |= !keepAlive;
  }

  /**
   * Store the object used by this client runnable to control when it is scheduled for execution in
   * the thread pool.
   *
   * @param future Instance controlling when the runnable is executed from within the thread pool.
   */
  void setFuture(ScheduledFuture<?> future) {
    runnableMe = future;
  }

  /**
   * Terminate a client that we wish to remove. This termination could happen at the client's
   * request or due to system need.
   */
  private void terminateClient() {
    // Once the communication is done, close this connection.
    connection.close();
    // Remove the client from our client listing.
    Prattle.removeClient(this);
    // And remove the client from our client pool.
    runnableMe.cancel(false);
  }

  /**
   * Returns the id of this user's active channel.
   *
   * @return id of the current active channel of the user.
   */
  public int getActiveChannelId() {
    return this.activeChannelId;
  }

  /**
   * Sets the active channel id to the given id.
   *
   * @param channelId id of the new active channel of this user.
   */
  public void setActiveChannelId(int channelId) {
    this.activeChannelId = channelId;
    userRepository.setActiveChannel(channelId, this.userId);
  }
}
