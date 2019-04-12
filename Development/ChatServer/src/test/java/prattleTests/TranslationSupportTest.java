package prattleTests;

import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import edu.northeastern.ccs.im.server.utility.TranslationSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Testing the translation support class.
 */
public class TranslationSupportTest {

    private TranslationSupport translationSupport;
    /**
     * Setup for the test.
     */
    @Before
    public void init() {
        translationSupport = TranslationSupport.getInstance();
    }
    /**
     * Tests for multi-language support instance
     */
    @Test
    public void testTranslationSupportInstance() {
        Assert.assertNotNull(translationSupport);
    }

    /**
     * Tests for language supported by api
     */
    @Test
    public void testLanguageSupported() {
        boolean canBeTranslated =  translationSupport.isLanguageSupported("spanish");
        Assert.assertTrue(canBeTranslated);
    }

    /**
     * Tests for language supported not by api
     */
    @Test
    public void testLanguageNotSupported() {
        boolean cannotBeTranslated =  translationSupport.isLanguageSupported("spanishh");
        Assert.assertFalse(cannotBeTranslated);
    }

    /**
     * Tests for retrieving the language codes
     */
    @Test
    public void testGetLanguageCode() {
        String languageCode =  translationSupport.getLanguageCode("spanish");
        Assert.assertEquals("es",languageCode);
    }

    /**
     * Tests for retrieving all languages supported by the cloud api
     */
    @Test
    public void testGetAllSupportedLanguages() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Translate translate = Mockito.mock(Translate.class);
        Field gr = Class.forName("edu.northeastern.ccs.im.server.utility.TranslationSupport")
                .getDeclaredField("translate");
        gr.setAccessible(true);
        gr.set(translationSupport,translate);
        Language language = Mockito.mock(Language.class);
        List<Language> languages = new ArrayList<>();
        languages.add(language);
        Mockito.when(translate.listSupportedLanguages()).thenReturn(languages);
        Mockito.when(language.getCode()).thenReturn("es");
        Mockito.when(language.getName()).thenReturn("spanish");
        String result  = translationSupport.getAllLanguagesSupported();
        Assert.assertEquals("Languages are:\n" +
                "Language: spanish    Code: es",result);
    }

    /**
     * Tests for retrieving all languages supported by the cloud api Excpetion
     */
    @Test
    public void testGetAllSupportedLanguagesException() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Translate translate = Mockito.mock(Translate.class);
        Field gr = Class.forName("edu.northeastern.ccs.im.server.utility.TranslationSupport")
            .getDeclaredField("translate");
        gr.setAccessible(true);
        gr.set(translationSupport,translate);
        Language language = Mockito.mock(Language.class);
        List<Language> languages = new ArrayList<>();
        languages.add(language);
        Mockito.when(translate.listSupportedLanguages()).thenThrow(new NullPointerException());
        Mockito.when(language.getCode()).thenReturn("es");
        Mockito.when(language.getName()).thenReturn("spanish");
        String result  = translationSupport.getAllLanguagesSupported();
        Assert.assertEquals("Languages are:",result);
    }

    /**
     * Tests translated text by the cloud api
     */
    @Test
    public void testTranslatedText() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Translate translate = Mockito.mock(Translate.class);

        Field gr = Class.forName("edu.northeastern.ccs.im.server.utility.TranslationSupport")
                .getDeclaredField("translate");
        gr.setAccessible(true);
        gr.set(translationSupport,translate);
        Translation translation=Mockito.mock(Translation.class);
        Mockito.when(translation.getTranslatedText()).thenReturn("hola");
        Mockito.when(translate.translate(Mockito.anyString(), Mockito.anyObject())).thenReturn(translation);
        Mockito.when(translation.getTranslatedText()).thenReturn("hola");
        String result  = translationSupport.translateTextToGivenLanguage("Hello","spanish");
        Assert.assertEquals("hola",result);
    }

    /**
     * Tests translated text by the cloud api Exception
     */
    @Test
    public void testTranslatedTextException() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Translate translate = Mockito.mock(Translate.class);

        Field gr = Class.forName("edu.northeastern.ccs.im.server.utility.TranslationSupport")
            .getDeclaredField("translate");
        gr.setAccessible(true);
        gr.set(translationSupport,translate);
        Translation translation=Mockito.mock(Translation.class);
        Mockito.when(translation.getTranslatedText()).thenReturn("hola");
        Mockito.when(translate.translate(Mockito.anyString(), Mockito.anyObject())).thenThrow(new NullPointerException());
        Mockito.when(translation.getTranslatedText()).thenReturn("hola");
        String result  = translationSupport.translateTextToGivenLanguage("Hello","spanish");
        Assert.assertEquals("Google translate api is not working",result);
    }

}
