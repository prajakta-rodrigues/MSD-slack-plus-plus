package edu.northeastern.ccs.im.server;

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
  static final int PORT = 4545;

  /**
   * Amount of time we should wait for a signal to arrive.
   */
  static final int DELAY_IN_MS = 50;

  /**
   * Number of threads available in our thread pool.
   */
  static final int THREAD_POOL_SIZE = 20;

  /**
   * Delay between times the thread pool runs the client check.
   */
  static final int CLIENT_CHECK_DELAY = 200;

  /**
   * Name of the private user who handles bad requests.
   */
  static final String BOUNCER_ID = "Bouncer";

  static final String SLACKBOT = "Slackbot";

  public static final int LATEST_MESSAGES_COUNT = 10;

  static final int GENERAL_ID = 1;

  /**
   * Delay for notifications
   */
  static final int CHECK_NOTIFICATION_DELAY = 30000;

  /**
   * Private constructor to prevent anyone from creating one of these.
   */
  private ServerConstants() {
    /* does nothing. */
  }

}
