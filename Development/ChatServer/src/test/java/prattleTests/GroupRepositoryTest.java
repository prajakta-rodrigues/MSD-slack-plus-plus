package prattleTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.mysql.jdbc.Connection;
import static org.junit.Assert.assertNull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import edu.northeastern.ccs.im.server.SlackGroup;
import edu.northeastern.ccs.im.server.repositories.GroupRepository;

/**
 * The Class GroupRepositoryTest.
 */
public class GroupRepositoryTest {

  /** The group repository. */
  private GroupRepository groupRepository;
  
  /** The connection. */
  private Connection connection;
  
  /** The db. */
  private DataSource db;
  
  /**
   * Inits the.
   *
   * @throws SQLException the SQL exception
   */
  @Before
  public void init() throws SQLException {
    db = Mockito.mock(DataSource.class);
    groupRepository = new GroupRepository(db);
    connection = Mockito.mock(Connection.class);
    Mockito.when(db.getConnection()).thenReturn(connection);

  }

  /**
   * Test get group by id.
   */
  @Test
  public void testGetGroupById() throws SQLException {
    groupRepository = new GroupRepository(db);
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    Mockito.when(preparedStmt.executeQuery()).thenReturn(resultSet);
    ResultSetMetaData md = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(resultSet.getMetaData()).thenReturn(md);
    Mockito.when(md.getColumnCount()).thenReturn(3);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(md.getColumnName(1)).thenReturn("id");
    Mockito.when(md.getColumnName(2)).thenReturn("name");
    Mockito.when(md.getColumnName(3)).thenReturn("channel_id");
    Mockito.when(resultSet.getObject(1)).thenReturn(1);
    Mockito.when(resultSet.getObject(2)).thenReturn("testing");
    Mockito.when(resultSet.getObject(3)).thenReturn(1);
    SlackGroup group = groupRepository.getGroupById(1);
    assertEquals(1 , group.getGroupId());
  }
  
  /**
   * Test get group by id.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetGroupByIdException() throws SQLException {
    groupRepository = new GroupRepository(db);
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(preparedStmt.executeQuery()).thenThrow(new SQLException());
    assertNull(groupRepository.getGroupById(1));
  }
  
  /**
   * Tests that get group members works.
   *
   * @throws SQLException SQL Exception.
   */
  @Test
  public void testGetGroupMembers() throws SQLException {
    groupRepository = new GroupRepository(db);
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
    groupRepository = new GroupRepository(db);
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
    groupRepository = new GroupRepository(db);
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    groupRepository.getGroupMembers(-1);
  }
}
