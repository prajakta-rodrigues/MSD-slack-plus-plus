package edu.northeastern.ccs.im.server.repositories;

import edu.northeastern.ccs.im.server.utility.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.sql.DataSource;

/**
 * The Class FriendRepository provides methods to interact with friends.
 */
public class FriendRepository extends Repository {

  /**
   * Instantiates a new friend repository.
   *
   * @param ds the ds
   */
  public FriendRepository(DataSource ds) {
    super(ds);
  }

  public boolean successfullyAcceptFriendRequest(int senderId, int receiverId) {
    boolean success = true;
    String query = "CALL slack.accept_friend_request(?,?)";
    try {
      connection = dataSource.getConnection();
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, senderId);
        preparedStmt.setInt(2, receiverId);
        preparedStmt.executeUpdate();
        connection.close();
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      success = false;
    } finally {
      closeConnection(connection);
    }
    return success;
  }

  /**
   * Determines if the two given users are friends.
   *
   * @param senderId the first user's id
   * @param receiverId the second user's id
   * @return true if the two users are friends, false otherwise
   */
  public boolean areFriends(Integer senderId, Integer receiverId) {
    boolean areFriends = false;
    try {
      connection = dataSource.getConnection();
      String query = "select count(*) AS 'exists' from slack.friend WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, senderId);
        preparedStmt.setInt(2, receiverId);
        preparedStmt.setInt(3, receiverId);
        preparedStmt.setInt(4, senderId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          for (Map<String, Object> result : results) {
            areFriends = (Long) result.get("exists") > 0;
          }
          connection.close();
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } finally {
      closeConnection(connection);
    }
    return areFriends;
  }

  /**
   * Returns a list of friend ids for the given userId
   *
   * @param userId the user id of the sought user
   * @return the list of friend ids
   */
  public List<Integer> getFriendsByUserId(int userId) {
    List<Integer> myFriends = new ArrayList<>();
    try {
      connection = dataSource.getConnection();
      String query = "select user2_id from slack.friend where user1_id = ? union select user1_id from slack.friend where user2_id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, userId);
        preparedStmt.setInt(2, userId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          for (Map<String, Object> result : results) {
            if(result.get("user1_id") != null) {
              myFriends.add((Integer) result.get("user1_id"));
            }
            if(result.get("user2_id") != null) {
              myFriends.add((Integer) result.get("user2_id"));
            }
          }
          connection.close();
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    finally {
      closeConnection(connection);
    }
    return myFriends;
  }

}
