package prattleTests;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

import javax.sql.DataSource;

import edu.northeastern.ccs.im.server.SlackGroup;
import edu.northeastern.ccs.im.server.repositories.GroupRepository;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The Class GroupRepositoryTest.
 */
public class GroupRepositoryTest {

  /** The executed query */
  private PreparedStatement value;

  /** The group repository. */
  private GroupRepository groupRepository;

  /** The connection. */
  private Connection connection;

  /** the ResultSet returned after executing a query */
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
    groupRepository = new GroupRepository(db);
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
    ResultSetMetaData md = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(resultSet.getMetaData()).thenReturn(md);
    Mockito.when(md.getColumnCount()).thenReturn(4);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(md.getColumnName(1)).thenReturn("id");
    Mockito.when(md.getColumnName(2)).thenReturn("name");
    Mockito.when(md.getColumnName(3)).thenReturn("channel_id");
    Mockito.when(md.getColumnName(4)).thenReturn("creator_id");
    Mockito.when(resultSet.getObject(1)).thenReturn(1);
    Mockito.when(resultSet.getObject(2)).thenReturn("testing");
    Mockito.when(resultSet.getObject(3)).thenReturn(1);
    Mockito.when(resultSet.getObject(4)).thenReturn(1);
  }

  @Test
  public void testAddGroupSuccess() {
    assertTrue(groupRepository.addGroup(new SlackGroup(1, "newGroup")));
  }

  @Test
  public void testAddGroupFail() throws SQLException {
    Mockito.when(value.executeUpdate()).thenReturn(0);
    assertFalse(groupRepository.addGroup(new SlackGroup(1, "takenName")));
  }

  @Test
  public void testAddGroupException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    assertFalse(groupRepository.addGroup(new SlackGroup(1, "frig")));
  }

  @Test
  public void testGetGroupByNameSuccess() {
    SlackGroup group = groupRepository.getGroupByName("testing");
    assertEquals("testing", group.getGroupName());
  }

  @Test
  public void testGetGroupByNameException() throws SQLException {
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    assertNull(groupRepository.getGroupByName("nonexistent"));
  }

  @Test
  public void testGroupHasMemberTrue() {
    assertTrue(groupRepository.groupHasMember(1, "testing"));
  }

  @Test
  public void testGroupHasMemberFalse() throws SQLException {
    Mockito.when(resultSet.next()).thenReturn(false);
    assertFalse(groupRepository.groupHasMember(2, "testing"));
  }

  @Test
  public void testGroupHasMemberException() throws SQLException {
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    assertFalse(groupRepository.groupHasMember(2, "testing"));
  }

  @Test
  public void testGroupsHavingMemberSome() {
    assertEquals("testing\n", groupRepository.groupsHavingMember(1));
  }

  @Test
  public void testGroupsHavingMemberNone() throws SQLException {
    Mockito.when(resultSet.next()).thenReturn(false);
    assertEquals("", groupRepository.groupsHavingMember(2));
  }

  @Test
  public void testGroupsHavingMemberException() throws SQLException {
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    assertEquals("", groupRepository.groupsHavingMember(2));
  }
  
  /**
   * Test get group by id.
   */
  @Test
  public void testGetGroupById() {
    SlackGroup group = groupRepository.getGroupById(1);
    assertEquals(1 , group.getGroupId());
  }
  
  /**
   * Test get group by id exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetGroupByIdException() throws SQLException {
    Mockito.when(value.executeQuery()).thenThrow(new SQLException());
    assertNull(groupRepository.getGroupById(1));
  }
}
