package prattleTests;

import static org.junit.Assert.assertEquals;
import java.lang.reflect.Field;
import org.junit.Before;
import org.junit.Test;
import edu.northeastern.ccs.im.server.utility.FilterWords;

/**
 * The Class FilterWordsTest.
 */
public class FilterWordsTest {

  /**
   * Init.
   *
   * @throws NoSuchFieldException the no such field exception
   * @throws SecurityException the security exception
   * @throws IllegalArgumentException the illegal argument exception
   * @throws IllegalAccessException the illegal access exception
   */
  @Before
  public void init() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
      IllegalAccessException {
    String words = "bp|gp";
    Field list = FilterWords.class.getDeclaredField("filterWordStr");
    list.setAccessible(true);
    list.set(null, words);
  }

  /**
   * Test filter words.
   */
  @Test
  public void testFilterWords() {
    assertEquals("*** there ***", FilterWords.filterSwearWordsFromMessage("bp there gp"));
  }
  
  /**
   * Test no filter words.
   */
  @Test
  public void testNoFilterWords() {
    assertEquals("hey there", FilterWords.filterSwearWordsFromMessage("hey there"));
  }
  

}
