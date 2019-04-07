package edu.northeastern.ccs.im.server.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.sql.DataSource;


import edu.northeastern.ccs.im.server.models.User;
import edu.northeastern.ccs.im.server.models.UserType;
import edu.northeastern.ccs.im.server.utility.DatabaseConnection;

/**
 * The Class UserRepository.
 */
public class UserRepository extends Repository {

  public UserRepository(DataSource ds) {
    super(ds);
  }

  public UserRepository() { super(); }

	/**
	 * Gets the user by user name.
	 *
	 * @param userName the user name
	 * @return the user by user name
	 */
	public User getUserByUserName(String userName) {
		User user = null; 
		try {
			connection = dataSource.getConnection();
			String query = "select * from slack.user where handle = ?";
			try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
				preparedStmt.setString(1, userName);
				try (ResultSet rs = preparedStmt.executeQuery()) {

					List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);

					for (Map<String, Object> result : results) {
						user = new User(Integer.parseInt(String.valueOf(result.get("id"))),
								String.valueOf(result.get("handle")), String.valueOf(result.get("password")),
								UserType.valueOf((String)result.get("type")));
					}
					connection.close();
				}
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		finally {
          closeConnection(connection);
	    }
		return user;
	}

	/**
	 * Adds the user.
	 *
	 * @param user the user
	 * @return true, if successful
	 */
	public boolean addUser(User user) {
		int result = 0;
		try {
			connection = dataSource.getConnection();
			String query = "insert into slack.user(id, handle, password, type, account_created_date) values(?,?,?,?,?)";
			try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
				preparedStmt.setInt(1, user.getUserId());
				preparedStmt.setString(2, user.getUserName());
				preparedStmt.setString(3, user.getPassword());
				preparedStmt.setString(4, user.getType().name());
				preparedStmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
				result = preparedStmt.executeUpdate();
				connection.close();
			}
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		finally {
          closeConnection(connection);
	    }
		return result == 1;
	}
	
    /**
     * Gets the user by user id.
     *
     * @param userId the user id
     * @return the user by user id
     */
    public User getUserByUserId(int userId) {
        User user = null; 
        try {
            connection = dataSource.getConnection();
            String query = "select * from slack.user where id = ?";
            try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
                preparedStmt.setInt(1, userId);
                try (ResultSet rs = preparedStmt.executeQuery()) {

                    List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);

                    for (Map<String, Object> result : results) {
                        user = new User(Integer.parseInt(String.valueOf(result.get("id"))),
                                String.valueOf(result.get("handle")), String.valueOf(result.get("password")),
                                UserType.valueOf((String)result.get("type")));
                    }
                    connection.close();
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        finally {
          closeConnection(connection);
        }
        return user;
    }

  /**
   * Sets whether or not the user is active in the database or not.
   *
   * @param isActive the user's active state
   * @param userId the user in question
   * @return Whether or not the update was a success
   */
  public boolean setActive(boolean isActive, int userId) {
    	int result = 0;
    	try {
    	  connection = dataSource.getConnection();
    	  String query = "UPDATE slack.user SET is_active = ? WHERE id = ?";
    	  try (PreparedStatement stmt = connection.prepareStatement(query)) {
    	    stmt.setBoolean(1, isActive);
    	    stmt.setInt(2, userId);
    	    result = stmt.executeUpdate();
        }
      } catch (SQLException e) {
    	  LOGGER.log(Level.SEVERE, e.getMessage(), e);
      } finally {
        closeConnection(connection);
      }
      return result > 0;
		}


  /**
   * Sets the active channel Id of the user.
   *
   * @param activeChanelId the new channel id of the user.
   * @param userId the user in question
   * @return whether or not the update was a success.
   */
  public boolean setActiveChannel(int activeChanelId, int userId) {
      int result = 0;
      try {
        connection = dataSource.getConnection();
        String query = "UPDATE slack.user SET active_channel = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
          stmt.setInt(1, activeChanelId);
          stmt.setInt(2, userId);
          result = stmt.executeUpdate();
        }
      } catch (SQLException e) {
        LOGGER.log(Level.SEVERE, e.getMessage(), e);
      } finally {
        closeConnection(connection);
      }
      return result > 0;
    }
  
  public boolean getDNDStatus(int userId) {
    try {
      connection = dataSource.getConnection();
      String query = "select dnd from slack.user WHERE id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, userId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          return (Boolean)results.get(0).get("dnd");
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    } catch(Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    finally {
      closeConnection(connection);
    }
    return false;
  }
  
  public boolean setDNDStatus(int userId, boolean dnd) {
    int result = 0;
    try {
      connection = dataSource.getConnection();
      String query = "update slack.user set dnd = ? WHERE id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(2, userId);
        preparedStmt.setBoolean(2, dnd);
        result = preparedStmt.executeUpdate();
      }
    } catch (SQLException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } finally {
      closeConnection(connection);
    }
    return result > 0;
  }
}

