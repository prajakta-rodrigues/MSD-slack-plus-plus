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
    }
    finally {
      if (null != connection) {
        try {
          connection.close();
        } catch (SQLException e) {
          LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
      }
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
    }
    finally {
      if (null != connection) {
        try {
          connection.close();
        } catch (SQLException e) {
          LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
      }
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
          connection.close();
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    finally {
      if (null != connection) {
        try {
          connection.close();
        } catch (SQLException e) {
          LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
      }
    }
    return results;
  }
}
