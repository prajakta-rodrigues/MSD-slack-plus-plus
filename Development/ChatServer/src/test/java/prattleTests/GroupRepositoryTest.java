package prattleTests;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import edu.northeastern.ccs.im.server.SlackGroup;
import edu.northeastern.ccs.im.server.repositories.GroupRepository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GroupRepositoryTest {

  private GroupRepository groupRepository;
  private PreparedStatement value;
  private Connection connection;
  private Map<String, Object> fakeGroup;

  @Before
  public void initData() throws SQLException {
    DataSource ds = Mockito.mock(DataSource.class);
    groupRepository = new GroupRepository(ds);
    connection = Mockito.mock(Connection.class);
    Mockito.when(ds.getConnection()).thenReturn(connection);
    value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(value).setString(Mockito.anyInt(), Mockito.anyString());
    Mockito.when(value.executeUpdate()).thenReturn(1);
    Mockito.doNothing().when(connection).close();
    fakeGroup.put("id", 1);
    fakeGroup.put("creator_id", 1);
    fakeGroup.put("name", "fakeGroup");
    fakeGroup.put("channel_id", 2);
    Mockito.doNothing().when(value.executeQuery());
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

  }

  @Test
  public void testGetGroupByNameFail() {

  }

  @Test
  public void testGetGroupByNameException() {

  }

  @Test
  public void testGroupHasMemberTrue() {

  }

  @Test
  public void testGroupHasMemberFalse() {

  }

  @Test
  public void testGroupHasMemberException() {

  }

  @Test
  public void testGroupsHavingMemberSome() {

  }

  @Test
  public void testGroupsHavingMemberNone() {

  }

  @Test
  public void testGroupsHavingMemberException() {

  }
}
