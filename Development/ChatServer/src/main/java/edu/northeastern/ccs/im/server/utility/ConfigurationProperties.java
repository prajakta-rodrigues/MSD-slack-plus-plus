package edu.northeastern.ccs.im.server.utility;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Class ConfigurationProperties.
 */
public class ConfigurationProperties {
  
  private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
  
  private static ConfigurationProperties singleton;
  
  private Properties prop;

  /**
   * Instantiates a new configuration properties.
   */
  private ConfigurationProperties() {
    prop = new Properties();
    String propFileName = "config.properties";
    try {
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

      if (inputStream != null) {
        prop.load(inputStream);
        inputStream.close();
      } else {
        LOGGER.log(Level.SEVERE, "Unable to load properties file");
      }
    } 
    catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }

  }

  /**
   * Gets the single instance of ConfigurationProperties.
   *
   * @return single instance of ConfigurationProperties
   */
  public static ConfigurationProperties getInstance(){
    if (null == singleton) {
      singleton = new ConfigurationProperties();
    }
    return singleton;
  }

  /**
   * Gets the property by name.
   *
   * @param propertyName the property name
   * @return the property
   */
  public String getProperty(String propertyName) {
    if(null == prop) {
      return null;
    }
    return prop.getProperty(propertyName);
  }
}
