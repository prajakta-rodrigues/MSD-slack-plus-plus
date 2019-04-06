package prattleTests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import edu.northeastern.ccs.im.server.repositories.FriendRequestRepository;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * tests for friend request repository
 */
public class FriendRequestRepositoryTest {

  /**
   * The friend request repository.
   */
  private FriendRequestRepository friendRequestRepository;

  /**
   * The executed query
   */
  private PreparedStatement value;

  /**
   * The connection.
   */
  private Connection connection;

  /**
   * the ResultSet returned after executing a query
   */
  private ResultSet resultSet;

  /**
   * Inits the data
   *
   * @throws SQLException the SQL exception
   */
  @Before
  public void initData()
      throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    /* The db. */
    DataSource db = Mockito.mock(DataSource.class);
    friendRequestRepository = new FriendRequestRepository(db);
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
    Mockito.when(metaData.getColumnCount()).thenReturn(4);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(metaData.getColumnCount()).thenReturn(2);
    Mockito.when(metaData.getColumnName(1)).thenReturn("sender_id");
    Mockito.when(metaData.getColumnName(2)).thenReturn("receiver_id");

    Field fr = Class.forName("edu.northeastern.ccs.im.server.commands.ACommand")
        .getDeclaredField("friendRequestRepository");
    fr.setAccessible(true);
    fr.set(null, friendRequestRepository);
  }


  /**
   * Tests SQL exception for hasPendingFriendRequestException.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testHasPendingFriendRequestException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    assertFalse(friendRequestRepository.hasPendingFriendRequest(1, 2));
  }

  /**
   * Tests SQL exception for hasPendingFriendRequestException.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testHasPendingFriendRequestException2() throws SQLException {
    value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    assertFalse(friendRequestRepository.hasPendingFriendRequest(1, 2));
  }

  /**
   * Tests hasPendingFriendRequest.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testHasPendingFriendRequest() throws SQLException {
    Mockito.when(resultSet.getObject(1)).thenReturn(2);
    Mockito.when(resultSet.getObject(2)).thenReturn(1);
    assertTrue(friendRequestRepository.hasPendingFriendRequest(1, 2));
  }

  /**
   * Tests hasPendingFriendRequest.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testHasPendingFriendRequest2() throws SQLException {
    Mockito.when(resultSet.getObject(1)).thenReturn(2);
    Mockito.when(resultSet.getObject(2)).thenReturn(1);
    assertFalse(friendRequestRepository.hasPendingFriendRequest(2, 1));
  }

  /**
   * Tests SQL exception for updatePendingFriendRequest
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testUpdatePendingFriendRequestException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    assertFalse(friendRequestRepository.successfullySendFriendRequest(1, 2));
    assertFalse(friendRequestRepository.successfullySendFriendRequest(1, 2));
  }

  /**
   * Tests updatePendingFriendRequest
   */
  @Test
  public void testUpdatePendingFriendRequest2() {
    assertTrue(friendRequestRepository.successfullySendFriendRequest(1, 2));
    assertTrue(friendRequestRepository.successfullySendFriendRequest(1, 2));
  }

  /**
   * Tests SQL exception for updatePendingFriendRequest
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testUpdatePendingFriendRequestException2() throws SQLException {
    value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    assertFalse(friendRequestRepository.successfullySendFriendRequest(1, 2));
    assertFalse(friendRequestRepository.successfullySendFriendRequest(1, 2));
  }
}