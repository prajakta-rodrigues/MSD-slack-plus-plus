package prattleTests;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.DataSource;

import edu.northeastern.ccs.im.server.repositories.DirectMessageRepository;

import static junit.framework.Assert.assertEquals;

public class DirectMessageRepositoryTest {

  /** The dm repository. */
  private DirectMessageRepository dmRepository;

  /** The connection. */
  private Connection connection;


  @Before
  public void initData() throws SQLException {
    DataSource db = Mockito.mock(DataSource.class);
    dmRepository = new DirectMessageRepository(db);
    connection = Mockito.mock(Connection.class);
    Mockito.when(db.getConnection()).thenReturn(connection);
    /* The executed query */ /** The executed query */
    PreparedStatement value = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(value);
    Mockito.doNothing().when(value).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(value).setString(Mockito.anyInt(), Mockito.anyString());
    Mockito.when(value.executeUpdate()).thenReturn(1);
    Mockito.doNothing().when(connection).close();

    /* the ResultSet returned after executing a query */ /** the ResultSet returned after executing a query */
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    Mockito.when(value.executeQuery()).thenReturn(resultSet);
    /* the Metadata returned after executing a query */
    ResultSetMetaData md = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(resultSet.getMetaData()).thenReturn(md);
    Mockito.when(md.getColumnCount()).thenReturn(1);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
    Mockito.when(md.getColumnName(1)).thenReturn("channel_id");
    Mockito.when(resultSet.getObject(1)).thenReturn(1);
  }

  @Test
  public void testGetDMChannel() {
    assertEquals(1, dmRepository.getDMChannel(1, 2));
  }

  @Test
  public void testGetDMChannelException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    assertEquals(-1, dmRepository.getDMChannel(1, 2));
  }

  @Test
  public void testCreateDM() {
    assertEquals(1, dmRepository.createDM(1, 2));
  }

  @Test
  public void testCreateDMException() throws SQLException {
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());
    assertEquals(-1, dmRepository.createDM(1, 2));
  }
}
