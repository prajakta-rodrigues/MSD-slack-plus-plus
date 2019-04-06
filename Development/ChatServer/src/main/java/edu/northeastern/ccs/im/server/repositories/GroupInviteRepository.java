package edu.northeastern.ccs.im.server.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.sql.DataSource;
import edu.northeastern.ccs.im.server.Models.GroupInvitation;
import edu.northeastern.ccs.im.server.Models.InviteesGroup;
import edu.northeastern.ccs.im.server.Models.InvitorsGroup;
import edu.northeastern.ccs.im.server.utility.DatabaseConnection;

/**
 * The Class GroupInviteRepository provides methods for accessing database 
 * entity group_invitations.
 */
public class GroupInviteRepository extends Repository {


  /**
   * Instantiates a new group invite repository.
   *
   * @param dataSource the data source
   */
  public GroupInviteRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Adds the group invitation.
   *
   * @param groupInvitation the group invitation
   * @return true, if successful
   * @throws SQLException when any error is propagated from database
   */
  public boolean add(GroupInvitation groupInvitation) throws SQLException {
    int result = 0;
    try {
      connection = dataSource.getConnection();
      String query =
          "insert into slack.group_invitation(invitor_id , invitee_id , group_id , created_date) values(?,?,?,?)";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, groupInvitation.getInvitorId());
        preparedStmt.setInt(2, groupInvitation.getInviteeId());
        preparedStmt.setInt(3, groupInvitation.getGroupId());
        preparedStmt.setTimestamp(4, groupInvitation.getCreatedDate());
        result = preparedStmt.executeUpdate();
      }
    } catch (SQLException e) {
      throw e;
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }

    return result == 1;

  }

  /**
   * Gets the group invitations by invitee id.
   *
   * @param inviteeId the id of the invitee of the invite
   * @return the group invitations by invitee id
   */
  public List<InvitorsGroup> getGroupInvitationsByInviteeId(int inviteeId) {
    InvitorsGroup groupInvitation;
    List<InvitorsGroup> listGroupInvitation = new ArrayList<>();
    try {
      connection = dataSource.getConnection();
      String query = "select u.handle, g.name from slack.user u, slack.group g, "
          + "slack.group_invitation ug where ug.invitee_id = ?"
          + " and u.id = ug.invitor_id and g.id = ug.group_id";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, inviteeId);

        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          for (Map<String, Object> result : results) {
            groupInvitation = new InvitorsGroup(String.valueOf(result.get("handle")), 
                String.valueOf(result.get("name")));
            listGroupInvitation.add(groupInvitation);
          }
        }
      }

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }
    return listGroupInvitation;
  }

  /**
   * Gets the group invitations by invitor id.
   *
   * @param invitorId the invitor id of the invite
   * @return the group invitations by invitor id
   */
  public List<InviteesGroup> getGroupInvitationsByInvitorId(int invitorId) {
    InviteesGroup groupInvitation;
    List<InviteesGroup> listGroupInvitation = new ArrayList<>();
    try {
      connection = dataSource.getConnection();
      String query = "select u.handle, g.name from slack.user u, slack.group g, "
          + "slack.group_invitation ug where ug.invitor_id = ?"
          + " and u.id = ug.invitee_id and g.id = ug.group_id";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, invitorId);

        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          for (Map<String, Object> result : results) {
            groupInvitation = new InviteesGroup(String.valueOf(result.get("handle")), 
                String.valueOf(result.get("name")));
            listGroupInvitation.add(groupInvitation);
          }
        }
      }

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
    }
    return listGroupInvitation;
  }
  

  /**
   * Accept invite for given parameters.
   *
   * @param userId the user id of the invite
   * @param groupId the group id of the invite
   * @return true, if successful
   * @throws SQLException when any error is propagated from database
   */
  public boolean acceptInvite(Integer userId, int groupId) throws SQLException {
    int rs;
    boolean result = false;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      String query = "delete from slack.group_invitation where invitee_id = ? and group_id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, userId);
        preparedStmt.setInt(2, groupId);
        rs = preparedStmt.executeUpdate();
      }
      
      if(rs == 0) {
        return false;
      }
      
      result = insertIntoUserGroup(connection , userId , groupId);
      connection.commit();  
    }
    catch(SQLException e) {
      connection.rollback();
      throw e;
    }
    catch(Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    finally {
      closeConnection(connection);
    }
    return result;
  }

  /**
   * Insert into user group.
   *
   * @param connection the connection
   * @param userId the user id of the invite
   * @param groupId the group id of the invite
   * @return true, if successful
   * @throws SQLException when any error is propagated from database
   */
  private boolean insertIntoUserGroup(Connection connection, Integer userId, int groupId) throws SQLException {
    int rs = 0;
    String query = "insert into slack.user_group(user_id, group_id, isModerator, created_date) values(?,?,?,?)";
    try(PreparedStatement preparedStmt = connection.prepareStatement(query)) {
      preparedStmt.setInt(1, userId);
      preparedStmt.setInt(2, groupId);
      preparedStmt.setBoolean(3, false);
      preparedStmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
      rs = preparedStmt.executeUpdate();
    }
    return rs == 1;
  }

}
