package prattleTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
   *
   * @throws SQLException the SQL exception
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
   * Test get group by id exception.
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
}
