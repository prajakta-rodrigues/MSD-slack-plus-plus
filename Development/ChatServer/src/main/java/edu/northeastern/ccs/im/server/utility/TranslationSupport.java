package edu.northeastern.ccs.im.server.utility;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helps to know all languages supported by google cloud api
 */
public class TranslationSupport {
    private static TranslationSupport singleton;

    private JSONObject jsonObj;

    private static final String LANGUAGES_FILE_NM = "languages.json";
    private static final Logger LOGGER = Logger.getLogger(TranslationSupport.class.getName());


    private TranslationSupport() {
        try {
            JSONParser jsonParser = new JSONParser();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(LANGUAGES_FILE_NM);
            //Use JSONObject for simple JSON and JSONArray for array of JSON.
            jsonObj = (JSONObject) jsonParser.parse(new InputStreamReader(inputStream,"UTF-8"));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Get instance translation support.
     *
     * @return the translation support singleton object
     */
    public static TranslationSupport getInstance(){
        if (null == singleton) {
            singleton = new TranslationSupport();
        }
        return singleton;
    }

    /**
     * is language present in list of languages supported.
     *
     * @param language the language
     * @return the language is supported or not.
     */
    public boolean isLanguageSupported(String language) {
        return jsonObj.containsKey(language);
    }
    /**
     * gives the language code.
     *
     * @param language the language
     * @return the code corresponding to the language.
     */
    public String LanguageCode(String language) {
        return (String )jsonObj.get(language);
    }
}
