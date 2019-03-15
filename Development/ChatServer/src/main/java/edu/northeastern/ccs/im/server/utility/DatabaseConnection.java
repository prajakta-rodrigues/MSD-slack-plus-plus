package edu.northeastern.ccs.im.server.utility;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * The Class DatabaseConnection.
 */
public class DatabaseConnection {


  private static final String HOST = ConfigurationProperties.getInstance().getProperty("dbhost");

  private static final String DB_NAME = ConfigurationProperties.getInstance().getProperty("dbname");

  private static final String USERNAME =
      ConfigurationProperties.getInstance().getProperty("dbusername");

  private static final String PASSWORD =
      ConfigurationProperties.getInstance().getProperty("dbpasword");

  private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

  /**
   * To restrict instantiation of class.
   */
  private DatabaseConnection() {

  }

  /**
   * Maps result Set into list.
   *
   * @param resultSet the result set to be mapped
   * @return the list of mapped objects
   */
  public static List<Map<String, Object>> resultsList(ResultSet resultSet) {
    ArrayList<Map<String, Object>> list = new ArrayList<>();
    try {
      ResultSetMetaData md = resultSet.getMetaData();
      int columns = md.getColumnCount();
      while (resultSet.next()) {
        HashMap<String, Object> row = new HashMap<>(columns);
        for (int i = 1; i <= columns; i++) {
          row.put(md.getColumnName(i), resultSet.getObject(i));
        }
        list.add(row);
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return list;
  }

  /**
   * Gets the database connection.
   *
   * @return the connection
   */
  public static DataSource getDataSource() {
    MysqlDataSource ds = new MysqlDataSource();
    ds.setUrl(String.format("%s%s?useSSL=false", HOST, DB_NAME));
    ds.setUser(USERNAME);
    ds.setPassword(PASSWORD);
    return ds;
  }
}
