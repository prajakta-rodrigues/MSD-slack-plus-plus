package prattleTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import edu.northeastern.ccs.im.server.Message;
import edu.northeastern.ccs.im.server.Prattle;
import edu.northeastern.ccs.im.server.repositories.FriendRequestRepository;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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
      throws SQLException, ClassNotFoundException, NoSuchFieldException {
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
    Mockito.when(metaData.getColumnCount()).thenReturn(4);
    Mockito.when(metaData.getColumnName(1)).thenReturn("sender_id");
    Mockito.when(metaData.getColumnName(2)).thenReturn("receiver_id");
    Mockito.when(metaData.getColumnName(3)).thenReturn("accepted");
    Mockito.when(metaData.getColumnName(4)).thenReturn("sent_date");

    Field ur = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("userRepository");
    ur.setAccessible(true);
  }

  /**
   * Tests SQL exception for getFriendByUserId.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testGetFriendByUserIdException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    friendRequestRepository.getFriendsByUserId(1);
  }

  /**
   * Tests SQL exception for getFriendByUserId.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testGetFriendByUserIdException2() throws SQLException {
    value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    friendRequestRepository.getFriendsByUserId(1);
  }

  /**
   * Test get friends by user id
   */
  @Test
  public void testGetFriendByUserId() {
    List<Integer> friendIds = friendRequestRepository.getFriendsByUserId(1);
    assertEquals(1, friendIds.size());
  }

  @Test
  public void testGetFriendsByUserId2() {
    Prattle.commandMessage(Message.makeCommandMessage("Omar", 1, "/friend mark"));
    Prattle.commandMessage(Message.makeCommandMessage("Mark", 2, "/friend omar"));
    friendRequestRepository.getFriendsByUserId(1);
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
    Mockito.when(resultSet.getObject(1)).thenReturn(1);
    Mockito.when(resultSet.getObject(2)).thenReturn(2);
    Mockito.when(resultSet.getObject(3)).thenReturn(false);
    assertTrue(friendRequestRepository.hasPendingFriendRequest(1, 2));
  }

  /**
   * Tests hasPendingFriendRequest.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testHasPendingFriendRequest2() throws SQLException {
    Mockito.when(resultSet.getObject(1)).thenReturn(1);
    Mockito.when(resultSet.getObject(2)).thenReturn(2);
    Mockito.when(resultSet.getObject(3)).thenReturn(false);
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
    assertFalse(friendRequestRepository.updatePendingFriendRequest(1, 2, false));
    assertFalse(friendRequestRepository.updatePendingFriendRequest(1, 2, true));
  }

  /**
   * Tests updatePendingFriendRequest
   */
  @Test
  public void testUpdatePendingFriendRequest2() {
    assertTrue(friendRequestRepository.updatePendingFriendRequest(1, 2, true));
    assertTrue(friendRequestRepository.updatePendingFriendRequest(1, 2, false));
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
    assertFalse(friendRequestRepository.updatePendingFriendRequest(1, 2, false));
    assertFalse(friendRequestRepository.updatePendingFriendRequest(1, 2, true));
  }

  /**
   * Tests SQL exception for areFriends.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testAreFriendsException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    assertFalse(friendRequestRepository.areFriends(1, 2));
  }

  /**
   * Tests SQL exception for areFriends.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testAreFriendsException2() throws SQLException {
    value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    assertFalse(friendRequestRepository.areFriends(1, 2));
  }

  /**
   * Tests are not friends
   */
  @Test
  public void testAreNotFriends() {
    assertFalse(friendRequestRepository.areFriends(1, 2));
  }

  /**
   * Tests are friends
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testAreFriends() throws SQLException {
    Mockito.when(resultSet.getObject(3)).thenReturn(true);
    assertTrue(friendRequestRepository.areFriends(1, 2));
  }
}