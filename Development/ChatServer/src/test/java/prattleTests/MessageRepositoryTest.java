package prattleTests;

import edu.northeastern.ccs.im.client.CommandLineMain;
import edu.northeastern.ccs.im.client.IMConnection;
import edu.northeastern.ccs.im.server.Message;
import edu.northeastern.ccs.im.server.MessageType;
import edu.northeastern.ccs.im.server.User;
import edu.northeastern.ccs.im.server.repositories.MessageRepository;
import edu.northeastern.ccs.im.server.repositories.UserRepository;
import edu.northeastern.ccs.im.server.utility.DatabaseConnection;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.io.StringReader;
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
     * Test add message.
     *
     * @throws SQLException the sql exception
     */
    @Test
    public void testAddMessage() throws SQLException {
        DataSource ds = Mockito.mock(DataSource.class);
        messageRepository = new MessageRepository(ds);
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(ds.getConnection()).thenReturn(connection);
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
        DataSource ds = Mockito.mock(DataSource.class);
        messageRepository = new MessageRepository(ds);
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(ds.getConnection()).thenReturn(connection);
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
    public void testAddMessageException() throws SQLException {
        DataSource ds = Mockito.mock(DataSource.class);
        messageRepository = new MessageRepository(ds);
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(ds.getConnection()).thenReturn(connection);
        PreparedStatement value = Mockito.mock(PreparedStatement.class);
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
        Mockito.doNothing().when(connection).close();
        assertFalse(messageRepository.saveMessage(Message.makeMessage("BCT", "koka", 1,"hello people")));
    }

    /**
     * Test get messages from channel.
     *
     * @throws SQLException the sql exception
     */
    @Test
    public void getMessagesFromChannel() throws SQLException {
        DataSource ds = Mockito.mock(DataSource.class);
        messageRepository = new MessageRepository(ds);
        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(ds.getConnection()).thenReturn(connection);
        PreparedStatement value = Mockito.mock(PreparedStatement.class);
        Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
        Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
        Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(value.executeQuery()).thenReturn(resultSet);

        ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(resultSet.getMetaData()).thenReturn(metadata);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(metadata.getColumnCount()).thenReturn(4);
        Mockito.when(metadata.getColumnName(1)).thenReturn("type");
        Mockito.when(metadata.getColumnName(2)).thenReturn("sender_id");
        Mockito.when(metadata.getColumnName(3)).thenReturn("channel_id");
        Mockito.when(metadata.getColumnName(4)).thenReturn("TEXT");
        Mockito.doNothing().when(connection).close();
        List<Message> messages = messageRepository.getLatestMessagesFromChannel(1, 1);
        assertEquals(0,messages.size());

    }


}