package edu.northeastern.ccs.im.server.utility;

import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helps to know all languages supported by google cloud api
 */
public class TranslationSupport {
    private static TranslationSupport singleton;
    private  Translate translate;
    private JSONObject jsonObj;

    private static final String LANGUAGES_FILE_NM = "languages.json";
    private static final Logger LOGGER = Logger.getLogger(TranslationSupport.class.getName());


    private TranslationSupport() {
        try {
            translate = TranslateOptions.getDefaultInstance().getService();
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
    public String getLanguageCode(String language) {
        return (String )jsonObj.get(language);
    }

    /**
     * gives all the supported language codes.
     *
     * @return the code corresponding to the language.
     */
    public String getAllLanguagesSupported() {
        List<Language> languages = new ArrayList<>();
         try {
             languages =translate.listSupportedLanguages();
         }  catch (Exception e){
             LOGGER.log(Level.SEVERE, e.getMessage(), e);
         }
        StringBuilder text = new StringBuilder("Languages are:");
        for (Language language : languages) {
            String line = "\nLanguage: "+language.getName()+"    Code: "+language.getCode();
            text.append(line);
        }
        return text.toString();
    }

    /**
     * gives all the supported language codes.
     *
     * @param textToTranslate the text to translate
     * @param targetLanguage the target language
     * @return the translated text.
     */
    public String translateTextToGivenLanguage(String textToTranslate, String targetLanguage) {
        String result = "Google translate api is not working";
        try {
            Translation translation = translate.translate(textToTranslate,Translate.TranslateOption.targetLanguage(getLanguageCode(targetLanguage)));
            result =  translation.getTranslatedText();
        }catch (Exception e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

}
