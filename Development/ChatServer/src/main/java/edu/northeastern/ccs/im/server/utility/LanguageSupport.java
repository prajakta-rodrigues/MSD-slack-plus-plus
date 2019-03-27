package edu.northeastern.ccs.im.server.utility;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
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


    private LanguageSupport() {
        try {
            JSONParser jsonParser = new JSONParser();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(LANGUAGES_FILE_NM);
            //Use JSONObject for simple JSON and JSONArray for array of JSON.
            jsonObj = (JSONObject) jsonParser.parse(new InputStreamReader(inputStream,"UTF-8"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

//        JSONParser jsonParser = new JSONParser();
//
//        try (FileReader reader = new FileReader(LANGUAGES_FILE_NM))
//        {
//            //Read JSON file
//            jsonObj = (JSONObject)jsonParser.parse(reader);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

    }

    /**
     * Get instance language support.
     *
     * @return the language support
     */
    public static LanguageSupport getInstance(){
        if (null == singleton) {
            singleton = new LanguageSupport();
        }
        return singleton;
    }



    public Map<String, String> getDescription(JSONObject obj) {
        HashMap<String, String> map = new HashMap<String, String>();
        Set<String> keys = obj.keySet();
        for(String key:keys) {
            String value = jsonObj.get(key).toString();
            map.put(key, value);
        }
        return map;
    }

    public String getLanguage(String language, String text) {
        JSONObject langJson = (JSONObject) jsonObj.get(language);
        return  (String) langJson.get(text);
    }
}
