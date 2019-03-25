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
      String query = "select * from slack.user_group where group_id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, groupId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          for (Map<String, Object> result : results) {
            if ((Boolean) result.get("isModerator")) {
              mods.add(String.valueOf(result.get("user_id")));
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
   * @param channelId the channel id whose group members are being sought
   * @return the list of group members.
   */
  public List<String> getGroupMembers(int channelId) {
    List<String> groupMembers = new ArrayList<>();
    try {
      connection = dataSource.getConnection();
      String query = "select * from slack.user_group where group_id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, channelId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          for (Map<String, Object> result : results) {
            groupMembers.add(String.valueOf(result.get("user_id")));
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
