package edu.northeastern.ccs.im.server.commands;

import java.util.function.BiFunction;

/**
 * Command Object that accepts a parameter and the senderId to perform necessary changes.
 */
public interface Command extends BiFunction<String[], Integer, String> {
  /**
   * Produces a String description of the purpose of this Command.
   *
   * @return String description
   */
  String description();
}
