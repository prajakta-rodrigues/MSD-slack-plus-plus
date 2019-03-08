package edu.northeastern.ccs.im.server;

import java.util.function.BiFunction;

interface Command extends BiFunction<String, String, String> {
  String description();
}
