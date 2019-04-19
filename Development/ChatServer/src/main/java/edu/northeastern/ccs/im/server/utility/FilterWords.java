package edu.northeastern.ccs.im.server.utility;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.northeastern.ccs.im.server.constants.ServerConstants;

public class FilterWords {
  
  private static List<String> listFilterWords;
    
  static final Logger LOGGER = Logger.getLogger(FilterWords.class.getName());
  
  static {
    listFilterWords = new ArrayList<>();
    try {
      InputStream inputStream = FilterWords.class.getClassLoader()
          .getResourceAsStream(ServerConstants.FILTER_WORDS_FILE_NM);
      if(null != inputStream) {
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(inputStream);
        while (sc.hasNext()) {
          listFilterWords.add(sc.nextLine());
        }
      }
    } catch (Exception exception) {
      LOGGER.log(Level.SEVERE, "Unable to read filter words file");
    }
  }

  public static String filterSwearWordsFromMessage(String msg) {
    StringBuilder replacement;
    for(String filterWord : listFilterWords) {
      replacement = new StringBuilder();
      for(int i = 0; i < filterWord.length(); i++) {
        replacement.append("*");  
      }
      msg = msg.replaceAll(filterWord, replacement.toString());
    }
    return msg;
  }
  
}
