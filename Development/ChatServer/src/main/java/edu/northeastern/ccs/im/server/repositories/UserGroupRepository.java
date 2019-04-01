package edu.northeastern.ccs.im.server.repositories;

import edu.northeastern.ccs.im.server.utility.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.sql.DataSource;

/**
 * The User Group Repository.
 */
public class UserGroupRepository extends Repository {

  /**
   * Instantiates a new user group repository.
   *
   * @param ds the ds
   */
  public UserGroupRepository(DataSource ds) {
    super(ds);
  }

  /**
   * Returns a list of the names of all moderators on a group.
   *
   * @param groupId the id of the desired group.
   * @return the list of moderators.
   */
  public List<String> getModerators(int groupId) {
    List<String> mods = new ArrayList<>();
    List<Map<String, Object>> results = getHandlesQuery(groupId);
    try {
      for (Map<String, Object> result : results) {
        if ((Boolean) result.get("isModerator")) {
          mods.add(String.valueOf(result.get("handle")));
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } finally {
      closeConnection(connection);
    }
    return mods;
  }

  /**
   * Returns all of the group members from the given channel id.
   *
   * @param groupId the channel id whose group members are being sought
   * @return the list of group members.
   */
  public List<String> getGroupMembers(int groupId) {
    List<String> groupMembers = new ArrayList<>();
    List<Map<String, Object>> results = getHandlesQuery(groupId);
    try {
      for (Map<String, Object> result : results) {
        groupMembers.add(String.valueOf(result.get("handle")));
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } finally {
      closeConnection(connection);
    }
    return groupMembers;
  }

  /**
   * Gets the handles (and moderators) of all users in the desired group.
   *
   * @param groupId the group id of the desired group
   * @return the result set of the query translated as a result list
   */
  private List<Map<String, Object>> getHandlesQuery(int groupId) {
    List<Map<String, Object>> results = null;
    try {
      connection = dataSource.getConnection();
      String query = "SELECT handle, isModerator "
          + "FROM slack.user_group ug JOIN slack.user u ON (ug.user_id = u.id) "
          + "WHERE group_id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, groupId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          results = DatabaseConnection.resultsList(rs);
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } finally {
      closeConnection(connection);
    }
    return results;
  }


  /**
   * Checks if given user is moderator of given group.
   *
   * @param userId the user id of the user
   * @param groupId the group id of the group
   * @return true, if is moderator
   * @throws SQLException the SQL exception
   */
  public boolean isModerator(int userId, int groupId) throws SQLException {
    try {
      connection = dataSource.getConnection();
      String query = "select isModerator from slack.user_group where user_id = ? and group_id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, userId);
        preparedStmt.setInt(2, groupId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          connection.close();
          return (Boolean) results.get(0).get("isModerator");
        }
      }
    } catch (SQLException e) {
      throw e;
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return false;
  }

  /**
   * Removes a member from a group.
   *
   * @param groupId the group to remove the user from.
   * @param userId the user to remove.
   * @return whether or not the delete was a success
   */
  public boolean removeMember(int groupId, int userId) {
    String query = "DELETE FROM slack.user_group WHERE group_id = ? AND user_id = ?";
    int result = 0;
    try {
      connection = dataSource.getConnection();
      try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setInt(1, groupId);
        stmt.setInt(2, userId);
        result = stmt.executeUpdate();
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } finally {
      closeConnection(connection);
    }
    return result > 0;
  }
}
