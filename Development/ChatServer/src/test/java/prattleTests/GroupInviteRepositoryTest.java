package prattleTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import edu.northeastern.ccs.im.server.GroupInvitation;
import edu.northeastern.ccs.im.server.InviteesGroup;
import edu.northeastern.ccs.im.server.InvitorsGroup;
import edu.northeastern.ccs.im.server.repositories.GroupInviteRepository;

/**
 * tests for group invite repository
 */
public class GroupInviteRepositoryTest {


  private GroupInviteRepository groupInviteRepository;

  private PreparedStatement ps;

  @Before
  public void init() throws SQLException {
    DataSource db;
    Connection connection;
    db = Mockito.mock(DataSource.class);
    groupInviteRepository = new GroupInviteRepository(db);
    connection = Mockito.mock(Connection.class);
    Mockito.when(db.getConnection()).thenReturn(connection);
    ps = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(ps);
  }

  @Test
  public void testAddSuccess() throws SQLException {
    GroupInvitation groupInvitation =
        new GroupInvitation(1, 1, 1, Timestamp.valueOf(LocalDateTime.now()));
    Mockito.doNothing().when(ps).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(ps).setTimestamp(Mockito.anyInt(), Mockito.any(Timestamp.class));
    Mockito.when(ps.executeUpdate()).thenReturn(1);
    groupInviteRepository.add(groupInvitation);
  }

  @Test
  public void testAddFail() throws SQLException {
    GroupInvitation groupInvitation =
        new GroupInvitation(1, 1, 1, Timestamp.valueOf(LocalDateTime.now()));
    Mockito.doNothing().when(ps).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(ps).setTimestamp(Mockito.anyInt(), Mockito.any(Timestamp.class));
    Mockito.when(ps.executeUpdate()).thenReturn(0);
    groupInviteRepository.add(groupInvitation);
  }

  @Test(expected = SQLException.class)
  public void testAddSQLException() throws SQLException {
    GroupInvitation groupInvitation =
        new GroupInvitation(1, 1, 1, Timestamp.valueOf(LocalDateTime.now()));
    Mockito.doNothing().when(ps).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(ps).setTimestamp(Mockito.anyInt(), Mockito.any(Timestamp.class));
    Mockito.when(ps.executeUpdate()).thenThrow(new SQLException());
    groupInviteRepository.add(groupInvitation);
  }

  @Test
  public void testAddException() throws SQLException {
    GroupInvitation groupInvitation =
        new GroupInvitation(1, 1, 1, Timestamp.valueOf(LocalDateTime.now()));
    Mockito.doNothing().when(ps).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(ps).setTimestamp(Mockito.anyInt(), Mockito.any(Timestamp.class));
    Mockito.when(ps.executeUpdate()).thenThrow(new IllegalArgumentException());
    groupInviteRepository.add(groupInvitation);
  }

  @Test
  public void testGetGroupInvitationsByInviteeId() throws SQLException {
    Mockito.doNothing().when(ps).setInt(Mockito.anyInt(), Mockito.anyInt());
    ResultSet rs = Mockito.mock(ResultSet.class);
    ResultSetMetaData md = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(rs.getMetaData()).thenReturn(md);
    Mockito.when(md.getColumnCount()).thenReturn(2);
    Mockito.when(rs.next()).thenReturn(true).thenReturn(false);
    Mockito.when(md.getColumnName(1)).thenReturn("handle");
    Mockito.when(md.getColumnName(2)).thenReturn("name");
    Mockito.when(rs.getObject(1)).thenReturn("raj");
    Mockito.when(rs.getObject(2)).thenReturn("grip");
    Mockito.when(ps.executeQuery()).thenReturn(rs);
    List<InvitorsGroup> ls = groupInviteRepository.getGroupInvitationsByInviteeId(0);
    assertEquals(1, ls.size());
    assertEquals("grip", ls.get(0).getGroupName());
    assertEquals("raj", ls.get(0).getInvitorHandle());
  }

  @Test
  public void testGetGroupInvitationsByInviteeIdSQLException() throws SQLException {
    Mockito.doNothing().when(ps).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(ps.executeQuery()).thenThrow(new SQLException());
    List<InvitorsGroup> ls = groupInviteRepository.getGroupInvitationsByInviteeId(0);
    assertEquals(0, ls.size());
  }

  @Test
  public void testGetGroupInvitationsByInviteeIdException() throws SQLException {
    Mockito.doNothing().when(ps).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(ps.executeQuery()).thenThrow(new IllegalArgumentException());
    List<InvitorsGroup> ls = groupInviteRepository.getGroupInvitationsByInviteeId(0);
    assertEquals(0, ls.size());
  }


  @Test
  public void testGetGroupInvitationsByInvitorId() throws SQLException {
    Mockito.doNothing().when(ps).setInt(Mockito.anyInt(), Mockito.anyInt());
    ResultSet rs = Mockito.mock(ResultSet.class);
    ResultSetMetaData md = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(rs.getMetaData()).thenReturn(md);
    Mockito.when(md.getColumnCount()).thenReturn(2);
    Mockito.when(rs.next()).thenReturn(true).thenReturn(false);
    Mockito.when(md.getColumnName(1)).thenReturn("handle");
    Mockito.when(md.getColumnName(2)).thenReturn("name");
    Mockito.when(rs.getObject(1)).thenReturn("raj");
    Mockito.when(rs.getObject(2)).thenReturn("grip");
    Mockito.when(ps.executeQuery()).thenReturn(rs);
    List<InviteesGroup> ls = groupInviteRepository.getGroupInvitationsByInvitorId(0);
    assertEquals(1, ls.size());
  }

  @Test
  public void testGetGroupInvitationsByInvitorIdSQLException() throws SQLException {
    Mockito.doNothing().when(ps).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(ps.executeQuery()).thenThrow(new SQLException());
    List<InviteesGroup> ls = groupInviteRepository.getGroupInvitationsByInvitorId(0);
    assertEquals(0, ls.size());
  }

  @Test
  public void testGetGroupInvitationsByInvitorIdException() throws SQLException {
    Mockito.doNothing().when(ps).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(ps.executeQuery()).thenThrow(new IllegalArgumentException());
    List<InviteesGroup> ls = groupInviteRepository.getGroupInvitationsByInvitorId(0);
    assertEquals(0, ls.size());
  }


  @Test
  public void testAcceptInviteSuccess() throws SQLException {
    Mockito.doNothing().when(ps).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(ps).setBoolean(Mockito.anyInt(), Mockito.anyBoolean());
    Mockito.doNothing().when(ps).setTimestamp(Mockito.anyInt(), Mockito.any(Timestamp.class));
    Mockito.when(ps.executeUpdate()).thenReturn(1);
    assertTrue(groupInviteRepository.acceptInvite(1, 1));
  }


  @Test
  public void testAcceptInviteFailedInsert() throws SQLException {
    Mockito.doNothing().when(ps).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(ps).setBoolean(Mockito.anyInt(), Mockito.anyBoolean());
    Mockito.doNothing().when(ps).setTimestamp(Mockito.anyInt(), Mockito.any(Timestamp.class));
    Mockito.when(ps.executeUpdate()).thenReturn(1).thenReturn(0);
    assertFalse(groupInviteRepository.acceptInvite(1, 0));
  }

  @Test
  public void testAcceptInviteFailedDelete() throws SQLException {
    Mockito.doNothing().when(ps).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(ps).setBoolean(Mockito.anyInt(), Mockito.anyBoolean());
    Mockito.doNothing().when(ps).setTimestamp(Mockito.anyInt(), Mockito.any(Timestamp.class));
    Mockito.when(ps.executeUpdate()).thenReturn(0).thenReturn(1);
    assertFalse(groupInviteRepository.acceptInvite(0, 0));
  }

  @Test(expected = SQLException.class)
  public void testAcceptInviteSQLException() throws SQLException {
    Mockito.doThrow(new SQLException()).when(ps).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(ps).setBoolean(Mockito.anyInt(), Mockito.anyBoolean());
    Mockito.doNothing().when(ps).setTimestamp(Mockito.anyInt(), Mockito.any(Timestamp.class));
    Mockito.when(ps.executeUpdate()).thenReturn(1).thenReturn(0);
    groupInviteRepository.acceptInvite(1, 0);
  }


  @Test
  public void testAcceptInviteException() throws SQLException {
    Mockito.doNothing().when(ps).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(ps).setBoolean(Mockito.anyInt(), Mockito.anyBoolean());
    Mockito.doNothing().when(ps).setTimestamp(Mockito.anyInt(), Mockito.any(Timestamp.class));
    Mockito.when(ps.executeUpdate()).thenReturn(1).thenThrow(new IllegalArgumentException());
    assertFalse(groupInviteRepository.acceptInvite(1, 0));
  }


}
