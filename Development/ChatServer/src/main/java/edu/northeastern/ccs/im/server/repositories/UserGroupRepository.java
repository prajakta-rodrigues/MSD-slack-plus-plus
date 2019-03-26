package edu.northeastern.ccs.im.server.repositories;

import edu.northeastern.ccs.im.server.utility.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    try {
      connection = dataSource.getConnection();
      String query = "select handle, isModerator "
          + "FROM slack.user_group ug JOIN slack.user u ON (ug.user_id = u.id) "
          + "WHERE group_id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, groupId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          for (Map<String, Object> result : results) {
            if ((Boolean) result.get("isModerator")) {
              mods.add(String.valueOf(result.get("handle")));
            }
            connection.close();
          }
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return mods;
  }

  /**
   * Returns all of the group members from the given channel id.
   *
   * @param group_id the channel id whose group members are being sought
   * @return the list of group members.
   */
  public List<String> getGroupMembers(int group_id) {
    List<String> groupMembers = new ArrayList<>();
    try {
      connection = dataSource.getConnection();
      String query = "select handle from slack.user u, slack.user_group g where u.id = g.user_id and g.group_id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, group_id);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          for (Map<String, Object> result : results) {
            groupMembers.add(String.valueOf(result.get("handle")));
          }
          connection.close();
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return groupMembers;
  }
}
