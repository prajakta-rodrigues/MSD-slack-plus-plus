package prattleTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;

import edu.northeastern.ccs.im.server.Models.Message;
import edu.northeastern.ccs.im.server.Prattle;
import edu.northeastern.ccs.im.server.Models.User;
import edu.northeastern.ccs.im.server.Models.UserType;
import edu.northeastern.ccs.im.server.repositories.FriendRepository;
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

/**
 * tests for friend repository
 */
public class FriendRepositoryTest {

  /**
   * The friend repository.
   */
  private FriendRepository friendRepository;

  /**
   * The user repository.
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

  private User omar;
  private User mark;

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
    friendRepository = new FriendRepository(db);
    userRepository = Mockito.mock(UserRepository.class);
    connection = Mockito.mock(Connection.class);
    Mockito.when(db.getConnection()).thenReturn(connection);
    value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(value).setString(Mockito.anyInt(), anyString());
    Mockito.when(value.executeUpdate()).thenReturn(1);
    Mockito.doNothing().when(connection).close();

    resultSet = Mockito.mock(ResultSet.class);
    Mockito.when(value.executeQuery()).thenReturn(resultSet);
    /* the Metadata returned after executing a query */
    ResultSetMetaData metaData = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(resultSet.getMetaData()).thenReturn(metaData);
    Mockito.when(metaData.getColumnCount()).thenReturn(3);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(metaData.getColumnCount()).thenReturn(3);
    Mockito.when(metaData.getColumnName(1)).thenReturn("user1_id");
    Mockito.when(metaData.getColumnName(2)).thenReturn("user2_id");
    Mockito.when(metaData.getColumnName(3)).thenReturn("exists");
    Field fr = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("friendRepository");
    fr.setAccessible(true);
    Field ur = Class.forName("edu.northeastern.ccs.im.server.Prattle")
        .getDeclaredField("userRepository");
    ur.setAccessible(true);
    ur.set(null, userRepository);

    omar = new User(1, "omar", "password", UserType.GENERAL);
    mark = new User(2, "mark", "password", UserType.GENERAL);


  }

  /**
   * Tests SQL exception for areFriends.
   *
   * @throws SQLException the SQL Exception
   */
  @Test

  public void testAreFriendsException() throws SQLException {
    Mockito.when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
    assertFalse(friendRepository.areFriends(1, 2));
  }

  /**
   * Tests SQL exception for areFriends.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testAreFriendsException2() throws SQLException {
    value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    assertFalse(friendRepository.areFriends(1, 2));
  }

  /**
   * Tests are not friends
   */
  @Test
  public void testAreNotFriends() {
    assertFalse(friendRepository.areFriends(1, 2));
  }

  /**
   * Tests are friends
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testAreFriends() throws SQLException {
    Mockito.when(resultSet.getObject(3)).thenReturn((long) 1);
    assertTrue(friendRepository.areFriends(1, 2));
  }

  /**
   * Tests SQL exception for getFriendByUserId.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testGetFriendByUserIdException() throws SQLException {
    Mockito.when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
    friendRepository.getFriendsByUserId(1);
  }

  /**
   * Tests SQL exception for getFriendByUserId.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testGetFriendByUserIdException2() throws SQLException {
    value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    friendRepository.getFriendsByUserId(1);
  }

  /**
   * Test get friends by user id with no friends
   */
  @Test
  public void testGetFriendByUserId() {
    List<Integer> friendIds = friendRepository.getFriendsByUserId(5);
    assertEquals(0, friendIds.size());
  }

  /**
   * Test get friends by user id
   */
  @Test
  public void testGetFriendsByUserId2() {
    Mockito.when(userRepository.getUserByUserName(anyString())).thenReturn(mark);
    Mockito.when(userRepository.getUserByUserId(anyInt())).thenReturn(omar);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/friend mark"));
    Prattle.commandMessage(Message.makeCommandMessage("mark", 2, "/friend omar"));
    friendRepository.getFriendsByUserId(1);
  }

  /**
   * Test get friends by user id
   */
  @Test
  public void testGetFriendsByUserId3() throws SQLException {
    Mockito.when(userRepository.getUserByUserName(anyString())).thenReturn(mark);
    Mockito.when(userRepository.getUserByUserId(anyInt())).thenReturn(omar);
    Mockito.when(resultSet.getObject(1)).thenReturn(1);
    Mockito.when(resultSet.getObject(2)).thenReturn(2);
    Prattle.commandMessage(Message.makeCommandMessage("omar", 1, "/friend mark"));
    Prattle.commandMessage(Message.makeCommandMessage("mark", 2, "/friend omar"));
    List<Integer> friendIds = friendRepository.getFriendsByUserId(1);
    assertTrue(friendIds.contains(1));
    assertTrue(friendIds.contains(2));
  }

  /**
   * Tests SQL exception for successfullyAcceptFriendRequest.
   *
   * @throws SQLException the SQL Exception
   */
  @Test
  public void testSuccessfullyAcceptFriendRequestException() throws SQLException {
    Mockito.when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
    assertFalse(friendRepository.successfullyAcceptFriendRequest(1, 2));
  }

  /**
   * Tests successfullyAcceptFriendRequest.
   */
  @Test
  public void testSuccessfullyAcceptFriendRequest()  {
    assertTrue(friendRepository.successfullyAcceptFriendRequest(1, 2));
  }
}
