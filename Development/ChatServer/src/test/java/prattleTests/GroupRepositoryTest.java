package prattleTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.mysql.jdbc.Connection;
import edu.northeastern.ccs.im.server.repositories.GroupRepository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Test;
import org.mockito.Mockito;

public class GroupRepositoryTest {

  /**
   * The group repository.
   */
  private GroupRepository groupRepository;

  /**
   * Tests that get group members works.
   * @throws SQLException SQL Exception.
   */
  @Test
  public void testGetGroupMembers() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    groupRepository = new GroupRepository(ds);
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
    Mockito.when(metadata.getColumnCount()).thenReturn(3);
    Mockito.when(metadata.getColumnName(1)).thenReturn("id");
    Mockito.when(metadata.getColumnName(2)).thenReturn("name");
    Mockito.when(metadata.getColumnName(3)).thenReturn("created_date");
    Mockito.when(metadata.getColumnName(4)).thenReturn("private");
    Mockito.when(metadata.getColumnName(5)).thenReturn("parent_id");
    Mockito.when(metadata.getColumnName(6)).thenReturn("channel_id");

    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(resultSet.getObject(1)).thenReturn(-1);
    Mockito.when(resultSet.getObject(2)).thenReturn("Omar");
    Mockito.when(resultSet.getObject(6)).thenReturn(-1);
    List<String> groupMembers = groupRepository.getGroupMembers(-1);
    assertEquals(1, groupMembers.size());
    assertTrue(groupMembers.contains("Omar"));
  }

  /**
   * Test get group members exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetGroupMembersException() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    groupRepository = new GroupRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    groupRepository.getGroupMembers(-1);
  }

  /**
   * Test get group members exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetGroupMembersException2() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    groupRepository = new GroupRepository(ds);
    Connection connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    groupRepository.getGroupMembers(-1);
  }

}
