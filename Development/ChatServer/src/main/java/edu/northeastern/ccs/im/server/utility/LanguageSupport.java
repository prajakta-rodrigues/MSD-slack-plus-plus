package edu.northeastern.ccs.im.server.utility;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * used for retrieving commands in different languages.
 */
public class LanguageSupport {

    private static LanguageSupport singleton;

    private JSONObject jsonObj;

    private static final String LANGUAGES_FILE_NM = "translations.json";
    private static final Logger LOGGER = Logger.getLogger(LanguageSupport.class.getName());


    private LanguageSupport() {
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
     * Get instance language support.
     *
     * @return the language support singleton object
     */
    public static LanguageSupport getInstance(){
        if (null == singleton) {
            singleton = new LanguageSupport();
        }
        return singleton;
    }

    /**
     * Gets language.
     *
     * @param language the language
     * @param text     the text
     * @return the language
     */
    public String getLanguage(String language, String text) {
        JSONObject langJson = (JSONObject) jsonObj.get(language);
        return  (String) langJson.get(text);
    }
}
