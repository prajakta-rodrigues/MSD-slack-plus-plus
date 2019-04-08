package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.utility.TranslationSupport;

/**
 * List all languages available to translate
 */
class Languages implements Command {

  private static TranslationSupport translationSupport = TranslationSupport.getInstance();

  @Override
  public String apply(String[] params, Integer senderId) {



    return translationSupport.getAllLanguagesSupported();
  }

  @Override
  public String description() {
    return "find all the available languages which you can use /translate on";
  }
}
