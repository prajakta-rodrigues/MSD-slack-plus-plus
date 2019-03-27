package edu.northeastern.ccs.im.server.repositories;

import edu.northeastern.ccs.im.server.utility.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.sql.DataSource;

/**
 * The Class FriendRequestRepository provides methods to interact with friend requests.
 */
public class FriendRequestRepository extends Repository {

  /**
   * Instantiates a new friend request repository.
   *
   * @param ds the ds
   */
  public FriendRequestRepository(DataSource ds) {
    super(ds);
  }

  /**
   * Returns a list of friends for the given userId
   *
   * @param userId the user id of the sought user
   * @return the list of friends
   */
  public List<String> getFriendsByUserId(int userId) {
    List<String> myFriends = new ArrayList<>();
    try {
      connection = dataSource.getConnection();
      String query = "select handle from slack.user u JOIN slack.friend_request fr ON (u.id = fr.sender_id) WHERE accepted = true, id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, userId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          for (Map<String, Object> result : results) {
            myFriends.add(String.valueOf(result.get("handle")));
          }
          connection.close();
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return myFriends;
  }

  /**
   * Checks if the given user has a friend request from the other user.
   *
   * @param senderId the user checking his requests
   * @param receiverId the user whose request is being looked for
   * @return true if there is a pending request, false if not
   */
  public boolean hasPendingFriendRequest(Integer senderId, Integer receiverId) {
    boolean hasPendingRequest = false;
    try {
      connection = dataSource.getConnection();
      String query = "select receiver_id, accepted from slack.friend_request WHERE sender_id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, senderId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          for (Map<String, Object> result : results) {
            if (result.get(receiverId).equals(receiverId)) {
              hasPendingRequest = (Boolean) result.get("accepted");
            }
          }
          connection.close();
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return hasPendingRequest;
  }

  /**
   * Updates the friend request table for the given users based on the given boolean.
   *
   * @param senderId the user sending his request
   * @param receiverId the user who is being sent a request
   * @param accepted whether or not the request has been accepted
   * @return true if there is a pending request, false if not
   */
  public void updatePendingFriendRequest(Integer senderId, Integer receiverId, boolean accepted) {
    try {
      connection = dataSource.getConnection();
      String query = "update slack.friend_request set accepted = ? where sender_id = ? AND receiver_id = ? ";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setBoolean(1, accepted);
        preparedStmt.setInt(2, senderId);
        preparedStmt.setInt(3, receiverId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          connection.close();
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
  }


}