package edu.northeastern.ccs.im.server.commands;


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
      return "You have to enter a language";
    }
    if(!translationSupport.isLanguageSupported(params[0])){
      return "You have to enter a valid language or code. check /lang command to find the supported languages";
    }
    if(params.length<2){
      return "You have to enter some text to translate";
    }
    String[] words = Arrays.copyOfRange(params,1,params.length);

    return translationSupport.translateTextToGivenLanguage(String.join(" ", words),params[0]);

  }

  @Override
  public String description() {
    return "You can translate any sentence \n" +
        "Parameters: language to translate it to";
  }
}
