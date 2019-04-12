package edu.northeastern.ccs.im.server.commands;


import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;
import java.util.Arrays;
import edu.northeastern.ccs.im.server.utility.TranslationSupport;
/**
 * Translate a given string
 */
 class Translate extends ACommand {

   private static TranslationSupport translationSupport;

  static  {
    translationSupport = TranslationSupport.getInstance();
  }

  @Override
   public String apply(String[] params, Integer senderId) {

    if (params == null) {
      return ErrorMessages.INCORRECT_COMMAND_PARAMETERS;
    }
    if(!translationSupport.isLanguageSupported(params[0])){
      return ErrorMessages.INVALID_LANG;
    }
    if(params.length<2){
      return ErrorMessages.INCORRECT_COMMAND_PARAMETERS;
    }
    String[] words = Arrays.copyOfRange(params,1,params.length);

    return translationSupport.translateTextToGivenLanguage(String.join(" ", words),params[0]);

  }

  @Override
  public String description() {
    return CommandDescriptions.TRANSLATE_DESCRIPTION;
  }
}
