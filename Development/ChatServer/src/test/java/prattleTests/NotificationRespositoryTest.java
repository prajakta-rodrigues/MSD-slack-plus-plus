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
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import edu.northeastern.ccs.im.server.Notification;
import edu.northeastern.ccs.im.server.NotificationType;
import edu.northeastern.ccs.im.server.repositories.NotificationRepository;

/**
 * The Class NotificationRespositoryTest.
 */
public class NotificationRespositoryTest {


  /**
   * The notification repository.
   */
  private NotificationRepository notificationRepository;

  /**
   * The datasource.
   */
  private DataSource db;

  /**
   * The connection.
   */
  private Connection connection;

  /**
   * Setup for the test.
   *
   * @throws SQLException the SQL exception
   */
  @Before
  public void init() throws SQLException {
    db = Mockito.mock(DataSource.class);
    notificationRepository = new NotificationRepository(db);
    connection = Mockito.mock(Connection.class);
    Mockito.when(db.getConnection()).thenReturn(connection);

  }

  /**
   * Test get all notifications by receiver id.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetAllNotificationsByReceiverId() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    ResultSetMetaData md = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(preparedStmt.executeQuery()).thenReturn(resultSet);
    Mockito.when(resultSet.getMetaData()).thenReturn(md);
    Mockito.when(md.getColumnCount()).thenReturn(6);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    Mockito.when(md.getColumnName(1)).thenReturn("id");
    Mockito.when(md.getColumnName(2)).thenReturn("receiver_id");
    Mockito.when(md.getColumnName(3)).thenReturn("associated_user_id");
    Mockito.when(md.getColumnName(4)).thenReturn("associated_group_id");
    Mockito.when(md.getColumnName(5)).thenReturn("created_date");
    Mockito.when(md.getColumnName(6)).thenReturn("type");
    Mockito.when(md.getColumnName(7)).thenReturn("new");
    Mockito.when(resultSet.getObject(1)).thenReturn(1);
    Mockito.when(resultSet.getObject(2)).thenReturn(2);
    Mockito.when(resultSet.getObject(3)).thenReturn(1).thenReturn(null);
    Mockito.when(resultSet.getObject(4)).thenReturn(3).thenReturn(null);
    Mockito.when(resultSet.getObject(5)).thenReturn(Timestamp.valueOf(LocalDateTime.now()))
        .thenReturn(null);
    Mockito.when(resultSet.getObject(6)).thenReturn(NotificationType.FRIEND_REQUEST.name());
    Mockito.when(resultSet.getObject(7)).thenReturn(true);
    List<Notification> list = notificationRepository.getAllNotificationsByReceiverId(1);
    assertEquals(2, list.size());
  }

  /**
   * Test get all notifications by receiver id SQL exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetAllNotificationsByReceiverIdSQLException() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(preparedStmt.executeQuery()).thenThrow(new SQLException());
    List<Notification> list = notificationRepository.getAllNotificationsByReceiverId(1);
    assertEquals(0, list.size());
  }

  /**
   * Test get all notifications by receiver id any exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetAllNotificationsByReceiverIdAnyException() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(preparedStmt.executeQuery()).thenThrow(new IllegalArgumentException());
    List<Notification> list = notificationRepository.getAllNotificationsByReceiverId(1);
    assertEquals(0, list.size());
  }


  /**
   * Test add notification success.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testAddNotificationSuccess() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(preparedStmt).setNull(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(preparedStmt).setBoolean(Mockito.anyInt(), Mockito.anyBoolean());
    Mockito.doNothing().when(preparedStmt).setString(Mockito.anyInt(), Mockito.anyString());
    Mockito.doNothing().when(preparedStmt)
        .setTimestamp(Mockito.anyInt(), Mockito.any(Timestamp.class));
    Mockito.when(preparedStmt.executeUpdate()).thenReturn(1);
    Notification notification = new Notification();
    notification.setType(NotificationType.FRIEND_REQUEST);
    notification.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
    assertTrue(notificationRepository.addNotification(notification));
  }


  /**
   * Test add notification exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testAddNotificationException() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.when(preparedStmt.executeUpdate()).thenThrow(new IllegalArgumentException());
    Notification notification = new Notification();
    notification.setType(NotificationType.FRIEND_REQUEST);
    notification.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
    assertFalse(notificationRepository.addNotification(notification));
  }

  /**
   * Test add notification SQL exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testAddNotificationSQLException() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.when(preparedStmt.executeUpdate()).thenThrow(new SQLException());
    Notification notification = new Notification();
    notification.setType(NotificationType.FRIEND_REQUEST);
    notification.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
    assertFalse(notificationRepository.addNotification(notification));
  }


  /**
   * Test get new notifications by receiver id.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetNewNotificationsByReceiverId() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    ResultSetMetaData md = Mockito.mock(ResultSetMetaData.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(preparedStmt.executeQuery()).thenReturn(resultSet);
    Mockito.when(resultSet.getMetaData()).thenReturn(md);
    Mockito.when(md.getColumnCount()).thenReturn(6);
    Mockito.when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    Mockito.when(md.getColumnName(1)).thenReturn("id");
    Mockito.when(md.getColumnName(2)).thenReturn("receiver_id");
    Mockito.when(md.getColumnName(3)).thenReturn("associated_user_id");
    Mockito.when(md.getColumnName(4)).thenReturn("associated_group_id");
    Mockito.when(md.getColumnName(5)).thenReturn("created_date");
    Mockito.when(md.getColumnName(6)).thenReturn("type");
    Mockito.when(md.getColumnName(7)).thenReturn("new");
    Mockito.when(resultSet.getObject(1)).thenReturn(1);
    Mockito.when(resultSet.getObject(2)).thenReturn(2);
    Mockito.when(resultSet.getObject(3)).thenReturn(1).thenReturn(null);
    Mockito.when(resultSet.getObject(4)).thenReturn(3).thenReturn(null);
    Mockito.when(resultSet.getObject(5)).thenReturn(Timestamp.valueOf(LocalDateTime.now()))
        .thenReturn(null);
    Mockito.when(resultSet.getObject(6)).thenReturn(NotificationType.FRIEND_REQUEST.name());
    Mockito.when(resultSet.getObject(7)).thenReturn(true);
    List<Notification> list = notificationRepository.getAllNewNotificationsByReceiverId(1);
    assertEquals(2, list.size());
  }

  /**
   * Test get new notifications by receiver id SQL exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetNewNotificationsByReceiverIdSQLException() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(preparedStmt.executeQuery()).thenThrow(new SQLException());
    List<Notification> list = notificationRepository.getAllNewNotificationsByReceiverId(1);
    assertEquals(0, list.size());
  }

  /**
   * Test get new all notifications by receiver id any exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testGetNewAllNotificationsByReceiverIdAnyException() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.when(preparedStmt.executeQuery()).thenThrow(new IllegalArgumentException());
    List<Notification> list = notificationRepository.getAllNewNotificationsByReceiverId(1);
    assertEquals(0, list.size());
  }

  /**
   * Test mark notifications as not new.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testMarkNotificationsAsNotNew() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(preparedStmt).setBoolean(Mockito.anyInt(), Mockito.anyBoolean());
    List<Notification> listNotifications = new ArrayList<>();
    Notification e = new Notification();
    e.setId(1);
    listNotifications.add(e);
    Mockito.when(preparedStmt.executeUpdate()).thenReturn(1);
    assertTrue(notificationRepository.markNotificationsAsNotNew(listNotifications));

  }

  /**
   * Test mark notifications as not new SQL exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testMarkNotificationsAsNotNewSQLException() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(preparedStmt).setBoolean(Mockito.anyInt(), Mockito.anyBoolean());
    List<Notification> listNotifications = new ArrayList<>();
    Notification e = new Notification();
    e.setId(1);
    listNotifications.add(e);
    Mockito.when(preparedStmt.executeUpdate()).thenThrow(new IllegalArgumentException());
    assertFalse(notificationRepository.markNotificationsAsNotNew(listNotifications));

  }

  /**
   * Test mark notifications as not new exception.
   *
   * @throws SQLException the SQL exception
   */
  @Test
  public void testMarkNotificationsAsNotNewException() throws SQLException {
    PreparedStatement preparedStmt = Mockito.mock(PreparedStatement.class);
    Mockito.when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStmt);
    Mockito.doNothing().when(preparedStmt).setInt(Mockito.anyInt(), Mockito.anyInt());
    Mockito.doNothing().when(preparedStmt).setBoolean(Mockito.anyInt(), Mockito.anyBoolean());
    List<Notification> listNotifications = new ArrayList<>();
    Notification e = new Notification();
    e.setId(1);
    listNotifications.add(e);
    Mockito.when(preparedStmt.executeUpdate()).thenThrow(new SQLException());
    assertFalse(notificationRepository.markNotificationsAsNotNew(listNotifications));

  }


}
