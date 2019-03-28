package prattleTests;

import edu.northeastern.ccs.im.server.utility.LanguageSupport;
import org.junit.Assert;
import org.junit.Test;

/**
 * For testing language support cases.
 */
public class LanguageSupportTest {

    /**
     * Tests for multi-language support instance
     */
    @Test
    public void testLanguageSupportInstance() {
        LanguageSupport ls = LanguageSupport.getInstance();
        Assert.assertNotNull(ls);
    }

    /**
     * Tests for multi-language support
     */
    @Test
    public void testLanguageSupport() {
        LanguageSupport ls = LanguageSupport.getInstance();
        String translated =  ls.getLanguage("spanish","Print out the handles of the users in a group.");
        Assert.assertEquals("Imprime los manejadores de los usuarios en un grupo.", translated);
    }
}
