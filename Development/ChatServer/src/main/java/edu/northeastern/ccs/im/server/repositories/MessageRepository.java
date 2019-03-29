package edu.northeastern.ccs.im.server.repositories;

import edu.northeastern.ccs.im.server.Message;
import edu.northeastern.ccs.im.server.utility.DatabaseConnection;

import javax.sql.DataSource;
import java.sql.Connection;
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
public class MessageRepository extends Repository{

    public MessageRepository(DataSource ds) {
        super(ds);
    }

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
            String query = "insert into slack.message(sender_id, type, sent_date, channel_id, TEXT) values(?,?,?,?,?)";
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

        }
        finally {
          closeConnection(connection);
        }
        return result == 1;
    }

    /**
     * Gets the user by user name.
     *
     * @param numberOfMessages the number of recent messages you want
     * @return the user by user name
     */
    public List<Message> getLatestMessagesFromChannel(int channelId,int numberOfMessages) {
        List<Message> messages = new ArrayList<>();
        try {
            connection = dataSource.getConnection();
            String query = "select msg.type,u.handle,msg.channel_id,msg.TEXT " +
                    "from slack.message msg JOIN slack.user u ON (msg.sender_id=u.id)" +
                    "where msg.channel_id =? ORDER BY msg.sent_date DESC LIMIT ?";
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
        }
        finally {
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

}
