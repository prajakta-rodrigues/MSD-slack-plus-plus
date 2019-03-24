package edu.northeastern.ccs.im.server.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.sql.DataSource;
import edu.northeastern.ccs.im.server.Notification;
import edu.northeastern.ccs.im.server.NotificationType;
import edu.northeastern.ccs.im.server.utility.DatabaseConnection;

public class NotificationRepository extends Repository {

  public NotificationRepository(DataSource ds) {
    super(ds);
  }

  public List<Notification> getAllNotificationsByReceiverId(int receiverId) { 
    List<Notification> listNotifications = new ArrayList<>();
    try {
      connection = dataSource.getConnection();
      String query = "select * from slack.notification where receiver_id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, receiverId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          listNotifications = getNotificationsFromResultSet(rs);
          connection.close();
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return listNotifications;
  }

  public boolean addNotification(Notification notification) {
    int result = 0;
    try {
      connection = dataSource.getConnection();
      String query = "insert into slack.notification(receiver_id, associated_user_id , "
          + "associated_group_id, type, new, created_date) values(?,?,?,?,?,?)";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, notification.getRecieverId());
        preparedStmt.setNull(2, Types.INTEGER);
        preparedStmt.setNull(3, Types.INTEGER);
        preparedStmt.setString(4, notification.getType().name());
        preparedStmt.setBoolean(5, notification.isNew());
        preparedStmt.setTimestamp(6, notification.getCreatedDate());
        result = preparedStmt.executeUpdate();
        connection.close();
      }
    } catch (SQLException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return result == 1;
  }


  public List<Notification> getAllNewNotificationsByReceiverId(int receiverId) {
    List<Notification> listNotifications = new ArrayList<>();
    try {
      connection = dataSource.getConnection();
      String query = "select * from slack.notification where receiver_id = ? and new = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, receiverId);
        preparedStmt.setBoolean(2, true);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          listNotifications = getNotificationsFromResultSet(rs);
          
          connection.close();
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return listNotifications;
    
  }

  private List<Notification> getNotificationsFromResultSet(ResultSet rs) {
    List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
    Notification notification = null;
    List<Notification> listNotifications = new ArrayList<>();
    for (Map<String, Object> result : results) {
      notification = new Notification();
      notification.setId((Integer)result.get("id"));
      notification.setRecieverId(Integer.parseInt(String.valueOf(result.get("receiver_id"))));
      if (result.get("associated_user_id") != null) {
        notification.setAssociatedUserId(
            Integer.parseInt(String.valueOf(result.get("associated_user_id"))));
      }
      if (result.get("associated_group_id") != null) {
        notification.setAssociatedGroupId(
            Integer.parseInt(String.valueOf(result.get("associated_group_id"))));
      }
      if (result.get("created_date")!= null) {
      notification
          .setCreatedDate(Timestamp.valueOf(String.valueOf(result.get("created_date"))));
      }
      notification.setType(NotificationType.valueOf(String.valueOf(result.get("type"))));
      notification.setNew(Boolean.parseBoolean(String.valueOf(result.get("new"))));
      listNotifications.add(notification);
    }
    return listNotifications;
  }

  public boolean markNotificationsAsNotNew(List<Notification> listNotifications) {
    int result = 0;
    try {
      connection = dataSource.getConnection();
      StringBuilder builder = new StringBuilder();
      builder.append("(");
      for (int i = 0; i < listNotifications.size(); i++) {
        builder.append("?,");
      }
      builder = builder.deleteCharAt(builder.length() - 1);
      builder.append(")");
      String query = "update slack.notification set new = ? where id in " + builder.toString();
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setBoolean(1, false);
        for (int i = 2; i <= listNotifications.size() + 1; i++) {
          preparedStmt.setInt(i, listNotifications.get(i - 2).getId());
        }
        result = preparedStmt.executeUpdate();
        connection.close();
      }

    } catch (SQLException e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return result == 1;
}
  
}
