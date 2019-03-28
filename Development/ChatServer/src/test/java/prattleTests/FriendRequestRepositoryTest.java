package prattleTests;

import static org.junit.Assert.assertEquals;

import edu.northeastern.ccs.im.server.Message;
import edu.northeastern.ccs.im.server.Prattle;
import edu.northeastern.ccs.im.server.User;
import edu.northeastern.ccs.im.server.repositories.FriendRequestRepository;
import edu.northeastern.ccs.im.server.repositories.UserRepository;
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
   * The user repository
   */
  private UserRepository userRepository;

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
    userRepository = Mockito.mock(UserRepository.class);
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

    User omar = new User(1, "omar", "password");
    User mark = new User(2, "mark", "password");

    Field ur = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("userRepository");
    ur.setAccessible(true);
    ur.set(null, userRepository);

    Mockito.when(userRepository.getUserByUserId(Mockito.anyInt())).thenReturn(omar);
    Mockito.when(userRepository.getUserByUserName(Mockito.anyString())).thenReturn(mark);
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
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
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
    assertEquals(friendIds.size(), 1);
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
    friendRequestRepository.hasPendingFriendRequest(1, 2);
  }

  /**
   * Tests SQL exception for hasPendingFriendRequestException.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testHasPendingFriendRequestException2() throws SQLException {
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    friendRequestRepository.hasPendingFriendRequest(1, 2);
  }

  /**
   * Tests SQL exception for updatePendingFriendRequestException.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testUpdatePendingFriendRequestException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    friendRequestRepository.updatePendingFriendRequest(1, 2, true);
    friendRequestRepository.updatePendingFriendRequest(1, 2, false);
  }

  /**
   * Tests SQL exception for updatePendingFriendRequestException.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testUpdatePendingFriendRequestException2() throws SQLException {
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    friendRequestRepository.updatePendingFriendRequest(1, 2, true);
    friendRequestRepository.updatePendingFriendRequest(1, 2, false);
  }

  /**
   * Tests SQL exception for areFriends.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testAreFriendsException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    friendRequestRepository.areFriends(1, 2);
  }

  /**
   * Tests SQL exception for areFriends.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testAreFriendsException2() throws SQLException {
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    friendRequestRepository.areFriends(1, 2);
  }

}
