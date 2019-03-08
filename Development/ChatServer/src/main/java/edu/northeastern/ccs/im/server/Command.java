package edu.northeastern.ccs.im.server;

import java.util.function.Function;

abstract class Command implements Function<String, String> {
  protected abstract String description();
}
