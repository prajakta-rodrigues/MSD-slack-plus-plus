package edu.northeastern.ccs.im.server.repositories;

import edu.northeastern.ccs.im.server.utility.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
   * Checks if the given user has a friend request from the other user.
   *
   * @param didIReceiveARequestId the user checking his requests
   * @param didTheySendMeARequestId the user whose request is being looked for
   * @return true if there is a pending request, false if not
   */
  public boolean hasPendingFriendRequest(Integer didIReceiveARequestId, Integer didTheySendMeARequestId) {
    boolean hasPendingRequest = false;
    try {
      connection = dataSource.getConnection();
      String query = "select sender_id from slack.friend_request WHERE receiver_id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, didIReceiveARequestId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          for (Map<String, Object> result : results) {
            if (result.get("sender_id").equals(didTheySendMeARequestId)) {
              hasPendingRequest = true;
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
    return hasPendingRequest;
  }

  /**
   * Updates the friend request table for the given users
   *
   * @param senderId the user sending a request
   * @param receiverId the user receiving a request
   * @return true if it was a successful update
   */
  public boolean successfullySendFriendRequest(Integer senderId, Integer receiverId) {
    int count = 0;
    String query = "insert into slack.friend_request (sender_id, receiver_id) values (?,?)";
    try {
      connection = dataSource.getConnection();
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, senderId);
        preparedStmt.setInt(2, receiverId);
        count = preparedStmt.executeUpdate();
        connection.close();
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    finally {
      closeConnection(connection);
    }
    return count == 1;
  }
}