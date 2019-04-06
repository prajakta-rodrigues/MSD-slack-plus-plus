package prattleTests;

import edu.northeastern.ccs.im.server.Message;
import edu.northeastern.ccs.im.server.MessageHistory;
import edu.northeastern.ccs.im.server.repositories.MessageRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
   * Setup for the test.
   *
   * @throws SQLException the SQL exception
   */
  @Before
  public void init() throws SQLException {
    DataSource db;
    db = Mockito.mock(DataSource.class);
    messageRepository = new MessageRepository(db);
    connection = Mockito.mock(Connection.class);
    Mockito.when(db.getConnection()).thenReturn(connection);

  }

  /**
   * Test add message.
   *
   * @throws SQLException the sql exception
   */
  @Test
  public void testAddMessage() throws SQLException {
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(value).setString(Mockito.anyInt(), Mockito.anyString());
    Mockito.doNothing().when(value).setTimestamp(Mockito.anyInt(), Mockito.anyObject());
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(value).setString(Mockito.anyInt(), Mockito.anyString());
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
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(value).setString(Mockito.anyInt(), Mockito.anyString());
    Mockito.doNothing().when(value).setTimestamp(Mockito.anyInt(), Mockito.anyObject());
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(value).setString(Mockito.anyInt(), Mockito.anyString());
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
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
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
  public void testGetMessagesFromChannelSQLException() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(preparedStmt.executeQuery()).thenThrow(new SQLException());
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
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(preparedStmt.executeQuery()).thenThrow(new IllegalArgumentException());
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
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
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
    List<MessageHistory> messages = messageRepository.getDirectMessageHistory(1, Timestamp.valueOf(LocalDateTime.now()), 
        Timestamp.valueOf(LocalDateTime.now()));
    assertEquals(2 , messages.size());
  }
  
  @Test
  public void testGetDirectMessageHistorySQLExceptions() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    messageRepository.getDirectMessageHistory(1, Timestamp.valueOf(LocalDateTime.now()), 
        Timestamp.valueOf(LocalDateTime.now()));
    List<MessageHistory> messages = messageRepository.getDirectMessageHistory(1, Timestamp.valueOf(LocalDateTime.now()), 
        Timestamp.valueOf(LocalDateTime.now()));
    assertEquals(0 , messages.size());
  }
  
  @Test
  public void testGetDirectMessageHistoryException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new IllegalArgumentException());
    messageRepository.getDirectMessageHistory(1, Timestamp.valueOf(LocalDateTime.now()), 
        Timestamp.valueOf(LocalDateTime.now()));
    List<MessageHistory> messages = messageRepository.getDirectMessageHistory(1, Timestamp.valueOf(LocalDateTime.now()), 
        Timestamp.valueOf(LocalDateTime.now()));
    assertEquals(0 , messages.size());
  }
  
  @Test
  public void testGetGroupMessageHistory() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
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
    List<MessageHistory> messages = messageRepository.getGroupMessageHistory(1, "test",Timestamp.valueOf(LocalDateTime.now()), 
        Timestamp.valueOf(LocalDateTime.now()));
    assertEquals(2 , messages.size());
  }
  
  @Test
  public void testGetGroupMessageHistorySQLExceptions() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    messageRepository.getDirectMessageHistory(1, Timestamp.valueOf(LocalDateTime.now()), 
        Timestamp.valueOf(LocalDateTime.now()));
    List<MessageHistory> messages = messageRepository.getGroupMessageHistory(1, "test", Timestamp.valueOf(LocalDateTime.now()), 
        Timestamp.valueOf(LocalDateTime.now()));
    assertEquals(0 , messages.size());
  }
  
  @Test
  public void testGetGroupMessageHistoryException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new IllegalArgumentException());
    messageRepository.getDirectMessageHistory(1, Timestamp.valueOf(LocalDateTime.now()), 
        Timestamp.valueOf(LocalDateTime.now()));
    List<MessageHistory> messages = messageRepository.getGroupMessageHistory(1, "test", Timestamp.valueOf(LocalDateTime.now()), 
        Timestamp.valueOf(LocalDateTime.now()));
    assertEquals(0 , messages.size());
  }
  
  
  
  

}