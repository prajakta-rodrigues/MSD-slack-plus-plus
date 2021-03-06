package edu.northeastern.ccs.im.server.repositories;

import edu.northeastern.ccs.im.server.models.MessageRecipientType;
import edu.northeastern.ccs.im.server.models.Message;
import edu.northeastern.ccs.im.server.models.MessageHistory;
import edu.northeastern.ccs.im.server.utility.DatabaseConnection;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * api for CRUD operations on the message table in the database.
 */
public class MessageRepository extends Repository {

  /**
   * Instantiates a new message repository.
   *
   * @param ds the ds
   */
  public MessageRepository(DataSource ds) {
    super(ds);
  }

  private String sentDate = "sent_date";

  /**
   * Adds the message.
   *
   * @param message the message
   * @return true, if successful
   */
  public boolean saveMessage(Message message) {
    int result = 0;
    try {
      connection = dataSource.getConnection();
      String query =
          "insert into slack.message(sender_id, type, sent_date, channel_id, TEXT, deleted) values(?,?,?,?,?, false)";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, message.getUserId());
        preparedStmt.setString(2, message.getMsgType().toString());
        preparedStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
        preparedStmt.setInt(4, message.getChannelId());
        preparedStmt.setString(5, message.getText());
        result = preparedStmt.executeUpdate();
        connection.close();
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);

    } finally {
      closeConnection(connection);
    }
    return result == 1;
  }

  /**
   * Gets the user by user name.
   *
   * @param channelId the channel id
   * @param numberOfMessages the number of recent messages you want
   * @return the user by user name
   */
  public List<Message> getLatestMessagesFromChannel(int channelId, int numberOfMessages) {
    List<Message> messages = new ArrayList<>();
    try {
      connection = dataSource.getConnection();
      String query = "select msg.type,u.handle,msg.channel_id,msg.TEXT "
          + "from slack.message msg JOIN slack.user u ON (msg.sender_id=u.id)"
          + "where msg.channel_id =? AND deleted = false ORDER BY msg.sent_date DESC LIMIT ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, channelId);
        preparedStmt.setInt(2, numberOfMessages);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          messages = getMessagesFromResultSet(rs);
          connection.close();
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } finally {
      closeConnection(connection);
    }
    return messages;
  }


  /**
   * Gets the messages from result set.
   *
   * @param rs the result set
   * @return the messages from result set
   */
  private List<Message> getMessagesFromResultSet(ResultSet rs) {
    List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
    List<Message> messages = new ArrayList<>();
    for (Map<String, Object> result : results) {
      Message message = Message.makeMessage(String.valueOf(result.get("type")),
          String.valueOf(result.get("handle")),
          Integer.parseInt(String.valueOf(result.get("channel_id"))),
          String.valueOf(result.get("text")));

      messages.add(message);
    }
    return messages;
  }


  /**
   * Gets the direct message history.
   *
   * @param userId the user id
   * @param startDate the start date
   * @param endDate the end date
   * @return the direct message history
   */
  public List<MessageHistory> getDirectMessageHistory(int userId, Timestamp startDate,
      Timestamp endDate) {
    List<MessageHistory> messageHistory = new ArrayList<>();
    try {
      connection = dataSource.getConnection();
      String query =
          "select first.sender, second.user1 , second.user2 , first.text, first.sent_date from "
              + "(select z.handle as sender, m.text, m.sent_date, m.channel_id from message m, user z "
              + "where m.sender_id = z.id and sent_date between ? and ?) as first " + "join "
              + "(select u.handle as user1, v.handle as user2, dm.channel_id from direct_message dm, user u, "
              + "user v where u.id = dm.user1_id and v.id = dm.user2_id and (dm.user1_id = ? or dm.user2_id = ?)) "
              + "as second on first.channel_id = second.channel_id";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setTimestamp(1, startDate);
        preparedStmt.setTimestamp(2, endDate);
        preparedStmt.setInt(3, userId);
        preparedStmt.setInt(4, userId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          for (Map<String, Object> result : results) {
            String senderUserName = (String) result.get("sender");
            if (senderUserName.equals(result.get("user1"))) {
              messageHistory.add(new MessageHistory((String) result.get("user2"),
                  MessageRecipientType.USER, senderUserName, MessageRecipientType.USER,
                  String.valueOf(result.get("text")), (Timestamp) result.get(sentDate)));
            } else {
              messageHistory
                  .add(new MessageHistory((String) result.get("user1"), MessageRecipientType.USER,
                      (String) result.get("user2"), MessageRecipientType.USER,
                      String.valueOf(result.get("text")), (Timestamp) result.get(sentDate)));
            }
          }
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } finally {
      closeConnection(connection);
    }
    return messageHistory;

  }

  /**
   * Gets the group message history.
   *
   * @param userId the user id
   * @param userHandle the user handle
   * @param startDate the start date
   * @param endDate the end date
   * @return the group message history
   */
  public List<MessageHistory> getGroupMessageHistory(int userId, String userHandle,
      Timestamp startDate, Timestamp endDate) {
    List<MessageHistory> messageHistory = new ArrayList<>();
    try {
      connection = dataSource.getConnection();
      String query = "SELECT u.id, u.handle, m.text, m.sent_date, g.name FROM slack.message m, "
          + "slack.group g, slack.user_group ug, slack.user u where m.channel_id = g.channel_id "
          + "and g.id = ug.group_id and u.id = m.sender_id "
          + "and ug.user_id = ? and m.sent_date between ? and ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, userId);
        preparedStmt.setTimestamp(2, startDate);
        preparedStmt.setTimestamp(3, endDate);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          for (Map<String, Object> result : results) {
            if ((Integer) result.get("id") == userId) {
              messageHistory
                  .add(new MessageHistory((String) result.get("name"), MessageRecipientType.GROUP,
                      (String) result.get("handle"), MessageRecipientType.USER,
                      (String) result.get("text"), (Timestamp) result.get(sentDate)));
            } else {
              messageHistory.add(new MessageHistory(userHandle, MessageRecipientType.USER,
                  (String) result.get("name"), MessageRecipientType.GROUP,
                  (String) result.get("text"), (Timestamp) result.get(sentDate)));
            }
          }
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } finally {
      closeConnection(connection);
    }
    return messageHistory;
  }


  /**
   * Recalls the desired message number from the given channel Id.
   *
   * @param senderId the sender id
   * @param messageNumber the number message to recall
   * @param channelId the channel Id
   * @return if the recall was successful
   */
  public boolean recallMessage(int senderId, int messageNumber, int channelId) {
    int count = 0;
    try {
      int messageId = getMessageId(senderId, messageNumber, channelId);
      connection = dataSource.getConnection();
      String query = "UPDATE slack.message SET deleted = true where id = ? AND sender_id = ? AND channel_Id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, messageId);
        preparedStmt.setInt(2, senderId);
        preparedStmt.setInt(3, channelId);
        count = preparedStmt.executeUpdate();
      }
    } catch (SQLException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    } finally {
      closeConnection(connection);
    }
    return count > 0;
  }

  /**
   * Gets the message id of the message that the sender wants to recall.
   *
   * @param senderId the sender id
   * @param messageNumber the message number to recall
   * @param channelId the channel id of the message
   * @return the message id of the desired message to recall
   * @throws SQLException if the message does not exist
   */
  public int getMessageId(int senderId, int messageNumber, int channelId) throws SQLException {
    int messageId = -1;
    connection = dataSource.getConnection();
    String query = "SELECT id FROM slack.message where sender_id = ? AND channel_Id = ? AND deleted = false";
    try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
      preparedStmt.setInt(1, senderId);
      preparedStmt.setInt(2, channelId);
      try (ResultSet rs = preparedStmt.executeQuery()) {
        List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
        int messageToRecall = results.size() - 1 - messageNumber;
        for (Map<String, Object> result : results) {
          if (messageToRecall == 0) {
            messageId = (Integer) result.get("id");
            break;
          }
          messageToRecall--;
        }
      }
    }
    return messageId;
  }
}
