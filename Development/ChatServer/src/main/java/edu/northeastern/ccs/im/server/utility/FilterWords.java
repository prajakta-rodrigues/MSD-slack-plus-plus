package edu.northeastern.ccs.im.server.utility;

import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.northeastern.ccs.im.server.constants.ServerConstants;

/**
 * The Class FilterWords.
 */
public class FilterWords {

  private FilterWords(){}
  
  /** The filter words. */
  private static String filterWordStr;
    
  /** The Constant LOGGER. */
  private static final Logger LOGGER = Logger.getLogger(FilterWords.class.getName());
  
  static {
    StringBuilder tmp = new StringBuilder();
    try {
      InputStream inputStream = FilterWords.class.getClassLoader()
          .getResourceAsStream(ServerConstants.FILTER_WORDS_FILE_NM);
      if(null != inputStream) {
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(inputStream);
        while (sc.hasNext()) {
          tmp.append(sc.nextLine());
          tmp.append("|");
        }
        filterWordStr = tmp.length() > 0 ? tmp.substring(0, tmp.length() - 1) : tmp.toString();
      }
    } catch (Exception exception) {
      LOGGER.log(Level.SEVERE, "Unable to read filter words file");
    }
  }

  /**
   * Filter swear words from message.
   *
   * @param msg the msg
   * @return the string
   */
  public static String filterSwearWordsFromMessage(String msg) {
    msg = msg.replaceAll(filterWordStr, "***");
    return msg;
  }
  
}
