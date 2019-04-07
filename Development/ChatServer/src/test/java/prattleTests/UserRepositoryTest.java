package prattleTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.mockito.Mockito;

import edu.northeastern.ccs.im.server.models.User;
import edu.northeastern.ccs.im.server.models.UserType;
import edu.northeastern.ccs.im.server.repositories.UserRepository;

/**
 * The Class UserRepositoryTest.
 */
public class UserRepositoryTest {

  /**
   * The user repository.
   */
  private UserRepository userRepository;

  /**
   * Test add user success.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testAddUserSuccess() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userRepository = new UserRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(value).setString(Mockito.anyInt(), Mockito.anyString());
    Mockito.doNothing().when(value).setTimestamp(Mockito.anyInt(), Mockito.anyObject());
    Mockito.when(value.executeUpdate()).thenReturn(1);
    Mockito.doNothing().when(connection).close();
    assertTrue(userRepository.addUser(new User(0, "test", "pwd", UserType.GENERAL)));
  }

  /**
   * Test add user fail.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testAddUserFail() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userRepository = new UserRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(value).setString(Mockito.anyInt(), Mockito.anyString());
    Mockito.doNothing().when(value).setTimestamp(Mockito.anyInt(), Mockito.anyObject());
    Mockito.when(value.executeUpdate()).thenReturn(0);
    Mockito.doNothing().when(connection).close();
    assertFalse(userRepository.addUser(new User(0, "test", "pwd", UserType.GENERAL)));
  }


  /**
   * Test add user exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testAddUserException() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userRepository = new UserRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    assertFalse(userRepository.addUser(new User(0, "test", "pwd", UserType.GENERAL)));
  }


  /**
   * Test get user by id exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetUserByNameException() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userRepository = new UserRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    assertNull(userRepository.getUserByUserName("tets"));
  }

  /**
   * Test get user by id success
   */
  @Test
  public void testGetUserByNameSuccess() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userRepository = new UserRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    Mockito.when(value.executeQuery()).thenReturn(resultSet);
    ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(resultSet.getMetaData()).thenReturn(metadata);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(metadata.getColumnCount()).thenReturn(4);
    Mockito.when(metadata.getColumnName(1)).thenReturn("handle");
    Mockito.when(metadata.getColumnName(2)).thenReturn("password");
    Mockito.when(metadata.getColumnName(3)).thenReturn("id");
    Mockito.when(metadata.getColumnName(4)).thenReturn("type");
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(resultSet.getObject(1)).thenReturn("Prajakta");
    Mockito.when(resultSet.getObject(2)).thenReturn("pwd");
    Mockito.when(resultSet.getObject(3)).thenReturn(1);
    Mockito.when(resultSet.getObject(4)).thenReturn("GENERAL");
    User user = userRepository.getUserByUserName("itest");
    assertEquals(1, user.getUserId());
  }


  /**
   * Test get user by id exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetUserByIdException() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userRepository = new UserRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    assertNull(userRepository.getUserByUserId(1));
  }

  /**
   * Test get user by id success
   */
  @Test
  public void testGetUserByIdSuccess() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userRepository = new UserRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    Mockito.when(value.executeQuery()).thenReturn(resultSet);
    ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(resultSet.getMetaData()).thenReturn(metadata);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(metadata.getColumnCount()).thenReturn(4);
    Mockito.when(metadata.getColumnName(1)).thenReturn("handle");
    Mockito.when(metadata.getColumnName(2)).thenReturn("password");
    Mockito.when(metadata.getColumnName(3)).thenReturn("id");
    Mockito.when(metadata.getColumnName(4)).thenReturn("type");
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(resultSet.getObject(1)).thenReturn("Prajakta");
    Mockito.when(resultSet.getObject(2)).thenReturn("pwd");
    Mockito.when(resultSet.getObject(3)).thenReturn(1);
    Mockito.when(resultSet.getObject(4)).thenReturn("GENERAL");
    User user = userRepository.getUserByUserId(1);
    assertEquals(1, user.getUserId());
  }

  @Test
  public void testSetActiveTrue() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userRepository = new UserRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setBoolean(Mockito.anyInt(), Mockito.anyBoolean());
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());

    Mockito.when(value.executeUpdate()).thenReturn(1);
    assertTrue(userRepository.setActive(true, 1));
  }

  @Test
  public void testSetActiveFalse() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userRepository = new UserRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setBoolean(Mockito.anyInt(), Mockito.anyBoolean());
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());

    Mockito.when(value.executeUpdate()).thenReturn(1);
    assertTrue(userRepository.setActive(false, 1));
  }

  @Test
  public void testSetActiveFail() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userRepository = new UserRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setBoolean(Mockito.anyInt(), Mockito.anyBoolean());
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());

    Mockito.when(value.executeUpdate()).thenReturn(0);
    assertFalse(userRepository.setActive(false, 1));
  }

  @Test
  public void testSetActiveException() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userRepository = new UserRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setBoolean(Mockito.anyInt(), Mockito.anyBoolean());
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());

    Mockito.when(value.executeUpdate()).thenReturn(1);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    assertFalse(userRepository.setActive(true, 1));
  }

  @Test
  public void testSetActiveChannelSuccess() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userRepository = new UserRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());

    Mockito.when(value.executeUpdate()).thenReturn(1);
    assertTrue(userRepository.setActiveChannel(1, 1));
  }

  @Test
  public void testSetActiveChannelFail() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userRepository = new UserRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());

    Mockito.when(value.executeUpdate()).thenReturn(0);
    assertFalse(userRepository.setActiveChannel(1, 1));
  }

  @Test
  public void testSetActiveChannelException() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userRepository = new UserRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());

    Mockito.when(value.executeUpdate()).thenThrow(new SQLException());
    assertFalse(userRepository.setActiveChannel(1, 1));
  }
}
