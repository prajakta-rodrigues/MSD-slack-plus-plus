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
   * Returns a list of friend ids for the given userId
   *
   * @param userId the user id of the sought user
   * @return the list of friend ids
   */
  public List<Integer> getFriendsByUserId(int userId) {
    List<Integer> myFriends = new ArrayList<>();
    try {
      connection = dataSource.getConnection();
      String query = "select * from slack.user u JOIN slack.friend_request fr on (u.id = fr.sender_id) WHERE accepted = true and id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, userId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          for (Map<String, Object> result : results) {
            myFriends.add((Integer) result.get("receiver_id"));
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
            if (result.get("receiver_id").equals(receiverId)) {
              hasPendingRequest = true;
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
   * Updates the friend request table for the given users based on the given boolean
   *
   * @param senderId the user accepting a request
   * @param receiverId the user who initially sent a friend request
   * @param accepted true if the request has been accepted, false if it's sent for 1st time
   * @return true if it was a successful update
   */
  public boolean updatePendingFriendRequest(Integer senderId, Integer receiverId,
      boolean accepted) {
    int count = 0;
    String query;
    try {
      connection = dataSource.getConnection();
      if (accepted) {
        query = "update slack.friend_request set accepted = ? where sender_id = ? and receiver_id = ?";
      } else {
        query = "insert into slack.friend_request (accepted, sender_id, receiver_id) values (?,?,?)";
      }
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setBoolean(1, accepted);
        preparedStmt.setInt(2, senderId);
        preparedStmt.setInt(3, receiverId);
        count = preparedStmt.executeUpdate();
        connection.close();
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return count == 1;
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
      String query = "select accepted from slack.friend_request WHERE sender_id = ? AND receiver_id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, senderId);
        preparedStmt.setInt(2, receiverId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          for (Map<String, Object> result : results) {
            areFriends = (Boolean) result.get("accepted");
          }
          connection.close();
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return areFriends;
  }

}