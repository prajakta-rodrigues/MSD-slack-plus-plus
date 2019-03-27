package edu.northeastern.ccs.im.server.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.sql.DataSource;

import edu.northeastern.ccs.im.server.utility.DatabaseConnection;

/**
 * Repository for direct Messages.
 */
public class DirectMessageRepository extends Repository {

  public DirectMessageRepository() {
    super();
  }

  public DirectMessageRepository(DataSource ds) {
    super(ds);
  }

  /**
   * Return the channel Id corresponding to the direct messages between users.
   * @param senderId the user who is trying to a send a message to another user
   * @param receiverId the user who is receiving a direct message
   * @return the channel Id associated with the direct messages between the two users. Return -1
   * if the query fails.
   */
  public int getDMChannel(int senderId, int receiverId) {
    int channelId = -1;
    String query = "SELECT DISTINCT channel_id " +
            "FROM slack.direct_message " +
            "WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
    try {
      connection = dataSource.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setInt(1, senderId);
        stmt.setInt(2, receiverId);
        stmt.setInt(3, receiverId);
        stmt.setInt(4, senderId);
        ResultSet rs = stmt.executeQuery();
        List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
        for (Map tuple : results) {
          channelId = (Integer)tuple.get("channel_id");
        }
      }
      connection.close();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    finally {
      closeConnection(connection);
    }
    return channelId;
  }

  /**
   * Create a new direct message between users and return the channelId
   * of the newly created dm.
   *
   * @param senderId Sender of the direct message.
   * @param receiverId Receiver of the direct message.
   * @return channelId of the newly created dm. Returns -1 if create fails.
   */
  public int createDM(int senderId, int receiverId) {
    int channelId = -1;
    String query = "CALL slack.make_dm(?,?)";
    try {
      connection = dataSource.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setInt(1, senderId);
        stmt.setInt(2, receiverId);
        ResultSet rs = stmt.executeQuery();
        List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
        for (Map tuple : results) {
          channelId = (Integer)tuple.get("channel_id");
        }
      }
      connection.close();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    finally {
      closeConnection(connection);
    }
    return channelId;
  }
}
