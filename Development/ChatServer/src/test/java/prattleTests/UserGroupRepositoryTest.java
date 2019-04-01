package prattleTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import edu.northeastern.ccs.im.server.repositories.UserGroupRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * tests for the user group repository
 */
public class UserGroupRepositoryTest {

  /**
   * The user group repository.
   */
  private UserGroupRepository userGroupRepository;

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
  public void initData() throws SQLException {
    /* The db. */
    DataSource db = Mockito.mock(DataSource.class);
    userGroupRepository = new UserGroupRepository(db);
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
    Mockito.when(metaData.getColumnCount()).thenReturn(5);
    Mockito.when(metaData.getColumnName(1)).thenReturn("user_id");
    Mockito.when(metaData.getColumnName(2)).thenReturn("group_id");
    Mockito.when(metaData.getColumnName(3)).thenReturn("isModerator");
    Mockito.when(metaData.getColumnName(4)).thenReturn("handle");
  }

  /**
   * Tests that getting moderators works.
   *
   * @throws SQLException SQL Exception
   */
  @Test
  public void testGetMods() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(resultSet.getObject(1)).thenReturn("Omar");
    Mockito.when(resultSet.getObject(2)).thenReturn(1);
    Mockito.when(resultSet.getObject(3)).thenReturn(true);
    Mockito.when(resultSet.getObject(4)).thenReturn("Omar");
    List<String> mods = userGroupRepository.getModerators(1);
    assertEquals(1, mods.size());
    assertTrue(mods.contains("Omar"));
  }

  /**
   * Tests that getting moderators works if you're not a mod.
   */
  @Test
  public void testGetMods2() throws SQLException {
    Mockito.when(resultSet.getObject(1)).thenReturn("Omar");
    Mockito.when(resultSet.getObject(2)).thenReturn(-1);
    Mockito.when(resultSet.getObject(3)).thenReturn(false);
    Mockito.when(resultSet.getObject(4)).thenReturn("Omar");
    List<String> mods = userGroupRepository.getModerators(1);
    assertTrue(!mods.contains("Omar"));
  }

  /**
   * Test get moderators exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetModsException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    userGroupRepository.getModerators(1);
  }

  /**
   * Test get moderators exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetModsException2() throws SQLException {
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    userGroupRepository.getModerators(1);
  }

  /**
   * Tests that get group members works.
   *
   * @throws SQLException SQL Exception.
   */
  @Test
  public void testGetGroupMembers() throws SQLException {
    Mockito.when(resultSet.getObject(1)).thenReturn("Omar");
    Mockito.when(resultSet.getObject(2)).thenReturn(-1);
    Mockito.when(resultSet.getObject(3)).thenReturn(true);
    Mockito.when(resultSet.getObject(4)).thenReturn("Omar");
    List<String> groupMembers = userGroupRepository.getGroupMembers(-1);
    Assert.assertEquals(1, groupMembers.size());
    assertTrue(groupMembers.contains("Omar"));
  }

  /**
   * Test get group members exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetGroupMembersException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    userGroupRepository.getGroupMembers(-1);
  }

  /**
   * Test get group members exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetGroupMembersException2() throws SQLException {
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    userGroupRepository.getGroupMembers(-1);
  }

  /**
   * Test Kick members from group Exception.
   *
   * @throws SQLException the sql exception
   */
  @Test
  public void testKickMembersException() throws SQLException {
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    Mockito.doNothing().when(connection).close();
    userGroupRepository.removeMember(-1,-1);
  }

  /**
   * Test Kick members from group Exception.
   *
   * @throws SQLException the sql exception
   */
  @Test
  public void testKickMembersException2() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    userGroupRepository.removeMember(-1,-1);
  }


  /**
   * Test remove group member.
   *
   * @throws SQLException the sql exception
   */
  @Test
  public void testRemoveGroupMember() throws SQLException {
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(value.executeUpdate()).thenReturn(1);
    Mockito.doNothing().when(connection).close();
    assertTrue(userGroupRepository.removeMember(-1,-1));
  }

}


