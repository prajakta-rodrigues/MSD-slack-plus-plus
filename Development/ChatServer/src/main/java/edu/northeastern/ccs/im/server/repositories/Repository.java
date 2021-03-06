package edu.northeastern.ccs.im.server.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import edu.northeastern.ccs.im.server.utility.DatabaseConnection;

abstract class Repository {
  /** The data source. */
  DataSource dataSource;

  /** The connection. */
  Connection connection;

  /** The Constant LOGGER. */
  static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

  /**
   * Instantiates a new repository.
   *
   * @param ds the datasource
   */
  Repository(DataSource ds) {
    this.dataSource = ds;
  }

  /**
   * Instantiates a new repository.
   *
   */
  Repository() {
    this.dataSource = DatabaseConnection.getDataSource();
  }
  
  protected void closeConnection(Connection connection) {
    if (null != connection) {
      try {
        connection.close();
      } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, e.getMessage(), e);
      }
    }
  }
}

