package prattleTests;

import edu.northeastern.ccs.im.server.Message;
import edu.northeastern.ccs.im.server.repositories.MessageRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * test the message respository
 */
public class MessageRepositoryTest {
    /**
     * The message repository.
     */
    private MessageRepository messageRepository;

    /**
     * The datasource.
     */
    private DataSource db;

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
        assertTrue(messageRepository.saveMessage(Message.makeMessage("BCT", "koka", 1,"hello people")));
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
        assertFalse(messageRepository.saveMessage(Message.makeMessage("BCT", "koka", 1,"hello people")));

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
        assertFalse(messageRepository.saveMessage(Message.makeMessage("BCT", "koka", 1,"hello people")));
    }

    /**
     * Test add message for any exception.
     *
     * @throws SQLException the sql exception
     */
    @Test
    public void testAddMessageAnyException() throws SQLException {
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new IllegalArgumentException());
        Mockito.doNothing().when(connection).close();
        assertFalse(messageRepository.saveMessage(Message.makeMessage("BCT", "koka", 1,"hello people")));
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


}