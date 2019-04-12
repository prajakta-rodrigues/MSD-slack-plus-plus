package prattleTests;

import edu.northeastern.ccs.im.server.Prattle;
import edu.northeastern.ccs.im.server.models.Message;
import edu.northeastern.ccs.im.server.models.MessageHistory;
import edu.northeastern.ccs.im.server.models.SlackGroup;
import edu.northeastern.ccs.im.server.repositories.FriendRequestRepository;
import edu.northeastern.ccs.im.server.repositories.GroupRepository;
import edu.northeastern.ccs.im.server.repositories.MessageRepository;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;

/**
 * test the message repository
 */
public class MessageRepositoryTest {

  /**
   * The message repository.
   */
  private MessageRepository messageRepository;

  /**
   * The connection.
   */
  private Connection connection;

  /**
   * The executed query
   */
  private PreparedStatement value;

  /**
   * the ResultSet returned after executing a query
   */
  private ResultSet resultSet;


  /**
   * Setup for the test.
   *
   * @throws SQLException the SQL exception
   */
  @Before
  public void init()
      throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    DataSource db = Mockito.mock(DataSource.class);
    messageRepository = new MessageRepository(db);
    connection = Mockito.mock(Connection.class);
    Mockito.when(db.getConnection()).thenReturn(connection);
    value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(value).setString(Mockito.anyInt(), Mockito.anyString());
    Mockito.when(value.executeUpdate()).thenReturn(1);
    Mockito.doNothing().when(connection).close();

    resultSet = Mockito.mock(ResultSet.class);
    Mockito.when(value.executeQuery()).thenReturn(resultSet);
    /* the Metadata returned after executing a query */
    ResultSetMetaData metaData = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(resultSet.getMetaData()).thenReturn(metaData);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(metaData.getColumnCount()).thenReturn(7);
    Mockito.when(metaData.getColumnName(1)).thenReturn("id");
    Mockito.when(metaData.getColumnName(2)).thenReturn("sender_id");
    Mockito.when(metaData.getColumnName(5)).thenReturn("channel_id");
    Mockito.when(metaData.getColumnName(6)).thenReturn("text");
    Mockito.when(metaData.getColumnName(7)).thenReturn("deleted");

    Field fr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("messageRepository");
    fr.setAccessible(true);
    fr.set(null, messageRepository);
  }


  /**
   * Test add message.
   *
   * @throws SQLException the sql exception
   */
  @Test
  public void testAddMessage() throws SQLException {
    Mockito.doNothing().when(value).setTimestamp(anyInt(), Mockito.anyObject());
    Mockito.doNothing().when(value).setInt(anyInt(), anyInt());
    Mockito.doNothing().when(value).setString(anyInt(), Mockito.anyString());
    Mockito.when(value.executeUpdate()).thenReturn(1);
    Mockito.doNothing().when(connection).close();
    assertTrue(
        messageRepository.saveMessage(Message.makeMessage("BCT", "koka", 1, "hello people")));
  }

  /**
   * Test add message failure.
   *
   * @throws SQLException the sql exception
   */
  @Test
  public void testAddMessageFail() throws SQLException {
    Mockito.doNothing().when(value).setTimestamp(anyInt(), Mockito.anyObject());
    Mockito.doNothing().when(value).setInt(anyInt(), anyInt());
    Mockito.doNothing().when(value).setString(anyInt(), Mockito.anyString());
    Mockito.when(value.executeUpdate()).thenReturn(0);
    Mockito.doNothing().when(connection).close();
    assertFalse(
        messageRepository.saveMessage(Message.makeMessage("BCT", "koka", 1, "hello people")));

  }

  /**
   * Test add message exception.
   *
   * @throws SQLException the sql exception
   */
  @Test
  public void testAddMessageSQLException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    Mockito.doNothing().when(connection).close();
    assertFalse(
        messageRepository.saveMessage(Message.makeMessage("BCT", "koka", 1, "hello people")));
  }

  /**
   * Test add message for any exception.
   *
   * @throws SQLException the sql exception
   */
  @Test
  public void testAddMessageAnyException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString()))
        .thenThrow(new IllegalArgumentException());
    Mockito.doNothing().when(connection).close();
    assertFalse(
        messageRepository.saveMessage(Message.makeMessage("BCT", "koka", 1, "hello people")));
  }

  /**
   * Test get messages from channel.
   *
   * @throws SQLException the sql exception
   */
  @Test
  public void testGetMessagesFromChannel() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(anyInt(), anyInt());
    Mockito.doNothing().when(preparedStmt).setInt(anyInt(), anyInt());
    Mockito.when(preparedStmt.executeQuery()).thenReturn(resultSet);
    Mockito.when(resultSet.getMetaData()).thenReturn(metadata);
    Mockito.when(metadata.getColumnCount()).thenReturn(4);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    Mockito.when(metadata.getColumnName(1)).thenReturn("type");
    Mockito.when(metadata.getColumnName(2)).thenReturn("handle");
    Mockito.when(metadata.getColumnName(3)).thenReturn("channel_id");
    Mockito.when(metadata.getColumnName(4)).thenReturn("TEXT");
    Mockito.when(resultSet.getObject(1)).thenReturn("BCT");
    Mockito.when(resultSet.getObject(2)).thenReturn("koka");
    Mockito.when(resultSet.getObject(3)).thenReturn(1);
    Mockito.when(resultSet.getObject(4)).thenReturn("hey jaffa");
    Mockito.doNothing().when(connection).close();
    List<Message> messages = messageRepository.getLatestMessagesFromChannel(1, 2);
    assertEquals(2, messages.size());

  }


  /**
   * Test get messages from channel sql exception.
   *
   * @throws SQLException the sql exception
   */
  @Test
  public void testGetMessagesFromChannelSQLException() throws SQLException { ;
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    Mockito.doNothing().when(connection).close();
    List<Message> messages = messageRepository.getLatestMessagesFromChannel(1, 2);
    assertEquals(0, messages.size());

  }

  /**
   * Test get messages from channel any other exception.
   *
   * @throws SQLException the sql exception
   */
  @Test
  public void testGetMessagesFromChannelAnyException() throws SQLException {
    Mockito.when(value.executeQuery()).thenThrow(new IllegalArgumentException());
    Mockito.doNothing().when(connection).close();
    List<Message> messages = messageRepository.getLatestMessagesFromChannel(1, 2);
    assertEquals(0, messages.size());

  }

  @Test
  public void testGetDirectMessageHistory() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(anyInt(), anyInt());
    Mockito.doNothing().when(preparedStmt).setInt(anyInt(), anyInt());
    Mockito.when(preparedStmt.executeQuery()).thenReturn(resultSet);
    Mockito.when(resultSet.getMetaData()).thenReturn(metadata);
    Mockito.when(metadata.getColumnCount()).thenReturn(5);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    Mockito.when(metadata.getColumnName(1)).thenReturn("sender");
    Mockito.when(metadata.getColumnName(2)).thenReturn("user1");
    Mockito.when(metadata.getColumnName(3)).thenReturn("user2");
    Mockito.when(metadata.getColumnName(4)).thenReturn("text");
    Mockito.when(metadata.getColumnName(5)).thenReturn("sent_date");
    Mockito.when(resultSet.getObject(1)).thenReturn("arya").thenReturn("jean");
    Mockito.when(resultSet.getObject(2)).thenReturn("arya").thenReturn("joe");
    Mockito.when(resultSet.getObject(3)).thenReturn("frodo");
    Mockito.when(resultSet.getObject(4)).thenReturn("hey frodo");
    Mockito.when(resultSet.getObject(5)).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
    Mockito.doNothing().when(connection).close();
    List<MessageHistory> messages = messageRepository
        .getDirectMessageHistory(1, Timestamp.valueOf(LocalDateTime.now()),
            Timestamp.valueOf(LocalDateTime.now()));
    assertEquals(2, messages.size());
  }

  @Test
  public void testGetDirectMessageHistorySQLExceptions() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    messageRepository.getDirectMessageHistory(1, Timestamp.valueOf(LocalDateTime.now()),
        Timestamp.valueOf(LocalDateTime.now()));
    List<MessageHistory> messages = messageRepository
        .getDirectMessageHistory(1, Timestamp.valueOf(LocalDateTime.now()),
            Timestamp.valueOf(LocalDateTime.now()));
    assertEquals(0, messages.size());
  }

  @Test
  public void testGetDirectMessageHistoryException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString()))
        .thenThrow(new IllegalArgumentException());
    messageRepository.getDirectMessageHistory(1, Timestamp.valueOf(LocalDateTime.now()),
        Timestamp.valueOf(LocalDateTime.now()));
    List<MessageHistory> messages = messageRepository
        .getDirectMessageHistory(1, Timestamp.valueOf(LocalDateTime.now()),
            Timestamp.valueOf(LocalDateTime.now()));
    assertEquals(0, messages.size());
  }

  @Test
  public void testGetGroupMessageHistory() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(anyInt(), anyInt());
    Mockito.doNothing().when(preparedStmt).setInt(anyInt(), anyInt());
    Mockito.when(preparedStmt.executeQuery()).thenReturn(resultSet);
    Mockito.when(resultSet.getMetaData()).thenReturn(metadata);
    Mockito.when(metadata.getColumnCount()).thenReturn(5);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    Mockito.when(metadata.getColumnName(1)).thenReturn("id");
    Mockito.when(metadata.getColumnName(2)).thenReturn("handle");
    Mockito.when(metadata.getColumnName(3)).thenReturn("text");
    Mockito.when(metadata.getColumnName(4)).thenReturn("sent_date");
    Mockito.when(metadata.getColumnName(5)).thenReturn("name");
    Mockito.when(resultSet.getObject(1)).thenReturn(1).thenReturn(2);
    Mockito.when(resultSet.getObject(2)).thenReturn("arya").thenReturn("test");
    Mockito.when(resultSet.getObject(3)).thenReturn("howdy frodo");
    Mockito.when(resultSet.getObject(5)).thenReturn("frodo");
    Mockito.when(resultSet.getObject(4)).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
    Mockito.doNothing().when(connection).close();
    List<MessageHistory> messages = messageRepository
        .getGroupMessageHistory(1, "test", Timestamp.valueOf(LocalDateTime.now()),
            Timestamp.valueOf(LocalDateTime.now()));
    assertEquals(2, messages.size());
  }

  @Test
  public void testGetGroupMessageHistorySQLExceptions() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    messageRepository.getDirectMessageHistory(1, Timestamp.valueOf(LocalDateTime.now()),
        Timestamp.valueOf(LocalDateTime.now()));
    List<MessageHistory> messages = messageRepository
        .getGroupMessageHistory(1, "test", Timestamp.valueOf(LocalDateTime.now()),
            Timestamp.valueOf(LocalDateTime.now()));
    assertEquals(0, messages.size());
  }

  @Test
  public void testGetGroupMessageHistoryException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString()))
        .thenThrow(new IllegalArgumentException());
    messageRepository.getDirectMessageHistory(1, Timestamp.valueOf(LocalDateTime.now()),
        Timestamp.valueOf(LocalDateTime.now()));
    List<MessageHistory> messages = messageRepository
        .getGroupMessageHistory(1, "test", Timestamp.valueOf(LocalDateTime.now()),
            Timestamp.valueOf(LocalDateTime.now()));
    assertEquals(0, messages.size());
  }

  /**
   * Test recall message exception.
   *
   * @throws SQLException the sql exception
   */
  @Test
  public void testRecallMessageException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    assertFalse(messageRepository.recallMessage(1, 2, 3));
  }

  /**
   * Test recall message exception.
   *
   * @throws SQLException the sql exception
   */
  @Test
  public void testRecallMessageException2() throws SQLException {
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(anyInt(), anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    assertFalse(messageRepository.recallMessage(1, 2, 3));
  }

  /**
   * Test get message id exception.
   *
   * @throws SQLException the sql exception
   */
  @Test
  public void testGetMessageException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    try {
      messageRepository.getMessageId(1, 2, 3);
      fail("This should've thrown an exception.");
    } catch (SQLException e) {
      // expect this
    }
  }

  /**
   * Test get message id exception.
   *
   * @throws SQLException the sql exception
   */
  @Test
  public void testGetMessageException2() throws SQLException {
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(anyInt(), anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    try {
      messageRepository.getMessageId(1, 2, 3);
      fail("This should've thrown an exception.");
    } catch (SQLException e) {
      // we expect this
    }
  }

  /**
   * Test recall message
   */
  @Test
  public void testRecallMessage() throws SQLException {
    MessageRepository messageRepository = Mockito.mock(MessageRepository.class);
    Mockito.when(messageRepository.getMessageId(anyInt(), anyInt(), anyInt())).thenReturn(686);
    assertFalse(messageRepository.recallMessage(1, 2, 3));
  }

  /**
   * Test recall message
   */
  @Test
  public void testRecallMessage2() {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "hello"));
    assertTrue(messageRepository.recallMessage(2, 1, 1));
  }

  /**
   * Test get message id
   */
  @Test
  public void testGetMessageId() throws SQLException {
    MessageRepository messageRepository = Mockito.mock(MessageRepository.class);
    Mockito.when(messageRepository.getMessageId(anyInt(), anyInt(), anyInt())).thenReturn(686);
    assertEquals(686, messageRepository.getMessageId(1, 2, 3));
  }

  /**
   * Test get message id
   */
  @Test
  public void testGetMessageId2() throws SQLException {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "hello"));
    Mockito.when(resultSet.getObject(1)).thenReturn(24);
    assertEquals(24, messageRepository.getMessageId(2, 0, 1));;
  }

  /**
   * Test get message id with an out of bounds message number
   */
  @Test
  public void testGetMessageIdMessageDoesNotExist() throws SQLException {
    Prattle.commandMessage(Message.makeCommandMessage("omar", 2, "okaySir"));
    assertEquals(-1, messageRepository.getMessageId(2, 10, 1));;
  }
}