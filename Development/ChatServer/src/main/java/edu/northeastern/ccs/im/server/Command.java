package edu.northeastern.ccs.im.server;

import java.util.function.BiFunction;

/**
 * Command Object that accepts a parameter and the senderId to perform necessary changes.
 */
interface Command extends BiFunction<String, String, String> {
  /**
   * Produces a String description of the purpose of this Command.
   *
   * @return String description
   */
  String description();
}
