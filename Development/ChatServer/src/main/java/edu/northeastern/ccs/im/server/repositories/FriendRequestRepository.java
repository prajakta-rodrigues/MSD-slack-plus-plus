package edu.northeastern.ccs.im.server.repositories;

import edu.northeastern.ccs.im.server.User;
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
}
