package edu.northeastern.ccs.im.server.constants;

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
public class ServerConstants {

  /**
   * The port number to listen on.
   */
  public static final int PORT = 4545;

  /**
   * Amount of time we should wait for a signal to arrive.
   */
  public static final int DELAY_IN_MS = 50;

  /**
   * Number of threads available in our thread pool.
   */
  public static final int THREAD_POOL_SIZE = 20;

  /**
   * Delay between times the thread pool runs the client check.
   */
  public static final int CLIENT_CHECK_DELAY = 200;

  /**
   * Name of the private user who handles bad requests.
   */
  public static final String BOUNCER_ID = "Bouncer";

  public static final String SLACKBOT = "Slackbot";

  public static final int LATEST_MESSAGES_COUNT = 10;

  public static final int GENERAL_ID = 1;
  
  public static final String FILTER_WORDS_FILE_NM = "swear.txt";

  /**
   * Delay for notifications
   */
  public static final int CHECK_NOTIFICATION_DELAY = 5000;

  /**
   * Private constructor to prevent anyone from creating one of these.
   */
  private ServerConstants() {
    /* does nothing. */
  }

}
