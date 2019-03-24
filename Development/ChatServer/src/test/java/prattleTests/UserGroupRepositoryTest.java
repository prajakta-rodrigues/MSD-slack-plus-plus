package prattleTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.mysql.jdbc.Connection;

import edu.northeastern.ccs.im.server.repositories.UserGroupRepository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Test;
import org.mockito.Mockito;

public class UserGroupRepositoryTest {

  /**
   * The user group repository.
   */
  private UserGroupRepository userGroupRepository;

  /**
   * Tests that getting moderators works.
   * @throws SQLException
   */
  @Test
  public void testGetMods() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userGroupRepository = new UserGroupRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setString(Mockito.anyInt(), Mockito.anyString());
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    Mockito.when(value.executeQuery()).thenReturn(resultSet);
    ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(resultSet.getMetaData()).thenReturn(metadata);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(metadata.getColumnCount()).thenReturn(3);
    Mockito.when(metadata.getColumnName(1)).thenReturn("user_id");
    Mockito.when(metadata.getColumnName(2)).thenReturn("group_id");
    Mockito.when(metadata.getColumnName(3)).thenReturn("isModerator");
    Mockito.when(metadata.getColumnName(4)).thenReturn("created_date");

    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(resultSet.getObject(1)).thenReturn("Omar");
    Mockito.when(resultSet.getObject(2)).thenReturn(-1);
    Mockito.when(resultSet.getObject(3)).thenReturn(true);
    List<String> mods = userGroupRepository.getModerators("hi");
    assertEquals(1, mods.size());
    assertTrue(mods.contains("Omar"));
  }

  /**
   * Tests that getting moderators works if you're not a mod.
   * @throws SQLException
   */
  @Test
  public void testGetMods2() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userGroupRepository = new UserGroupRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setString(Mockito.anyInt(), Mockito.anyString());
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    Mockito.when(value.executeQuery()).thenReturn(resultSet);
    ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(resultSet.getMetaData()).thenReturn(metadata);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(metadata.getColumnCount()).thenReturn(3);
    Mockito.when(metadata.getColumnName(1)).thenReturn("user_id");
    Mockito.when(metadata.getColumnName(2)).thenReturn("group_id");
    Mockito.when(metadata.getColumnName(3)).thenReturn("isModerator");
    Mockito.when(metadata.getColumnName(4)).thenReturn("created_date");

    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(resultSet.getObject(1)).thenReturn("Omar");
    Mockito.when(resultSet.getObject(2)).thenReturn(-1);
    Mockito.when(resultSet.getObject(3)).thenReturn(false);
    List<String> mods = userGroupRepository.getModerators("hi");
    assertEquals(0, mods.size());
    assertTrue(!mods.contains("Omar"));
  }

  /**
   * Test get moderators exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetModsException() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userGroupRepository = new UserGroupRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    userGroupRepository.getModerators("group1");
  }

  /**
   * Test get moderators exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetModsException2() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    userGroupRepository = new UserGroupRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    userGroupRepository.getModerators("group2");
  }

}


