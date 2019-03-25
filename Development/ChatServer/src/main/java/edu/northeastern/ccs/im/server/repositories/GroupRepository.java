package edu.northeastern.ccs.im.server.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.sql.DataSource;

import edu.northeastern.ccs.im.server.SlackGroup;
import edu.northeastern.ccs.im.server.utility.DatabaseConnection;

/**
 * The Class GroupRepository provides methods to interact with database entity group.
 */
public class GroupRepository extends Repository {

  /**
   * Instantiates a new group repository.
   *
   * @param ds the ds
   */
  public GroupRepository(DataSource ds) {
    super(ds);
  }

  public GroupRepository() { super(); }

  /**
   * Gets the group by id.
   *
   * @param groupId the group id
   * @return the group by id
   */
  public SlackGroup getGroupById(int groupId) {
    SlackGroup group = null;
    try {
      connection = dataSource.getConnection();
      String query = "select * from slack.group where id = ? LIMIT 1";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, groupId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);
          for (Map<String, Object> result : results) {
            group = new SlackGroup((Integer)result.get("id"),
                    (Integer)result.get("creator_id"),
                    String.valueOf(result.get("name")),
                    (Integer)result.get("channel_id"));
          }
          connection.close();
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return group;

  }

  /**
   * Get Group by name
   *
   * @param groupName the name of the group
   * @return the Group
   */
  public SlackGroup getGroupByName(String groupName) {
    SlackGroup group = null;
    try {
      connection = dataSource.getConnection();
      String query = "select * from slack.group where name = ? LIMIT 1";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setString(1, groupName);
        try (ResultSet rs = preparedStmt.executeQuery()) {

          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);

          for (Map<String, Object> result : results) {
            group = new SlackGroup( (Integer)result.get("id"),
                    (Integer)result.get("creator_id"),
                    String.valueOf(result.get("name")),
                    (Integer)result.get("channel_id"));
          }
          connection.close();
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return group;
  }

  /**
   * Add a group to the database, if valid
   * @param group the group to add
   * @return boolean representing whether or not the write was successful
   */
  public boolean addGroup(SlackGroup group) {
    int count = 0;
    try {
      connection = dataSource.getConnection();
      String query = "CALL slack.make_group(?,?,?)";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, group.getCreatorId());
        preparedStmt.setInt(2, group.getGroupId());
        preparedStmt.setString(3, group.getGroupName());
        count = preparedStmt.executeUpdate();
      }
      connection.close();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return count > 0;
  }

  /**
   * Find if the group has the given member.
   *
   * @param memberId the id of the user in question
   * @param groupName the name of the group
   * @return Whether or not the given userId is a member of the given group.
   */
  public boolean groupHasMember(int memberId, String groupName) {
    boolean hasMember = false;
    try {
      connection = dataSource.getConnection();
      String query = "SELECT user_id " +
              "FROM slack.group g JOIN slack.user_group ug ON (g.id = ug.group_id) " +
              "WHERE g.name like ? AND user_id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setString(1, groupName);
        preparedStmt.setInt(2, memberId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          hasMember = !DatabaseConnection.resultsList(rs).isEmpty();
        }
      }
      connection.close();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return hasMember;
  }

  /**
   * Returns all the groups that the given user is a member of.
   *
   * @param memberId id of the user
   * @return All groups that have the given member.
   */
  public String groupsHavingMember(int memberId) {
    StringBuilder groups = new StringBuilder();
    try {
      connection = dataSource.getConnection();
      String query = "SELECT name " +
              "FROM slack.group g JOIN slack.user_group ug ON (g.id = ug.group_id)" +
              "WHERE ug.user_id = ?";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, memberId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);

          for (Map<String, Object> result : results) {
            groups.append(result.get("name"));
            groups.append("\n");
          }
        }
      }
      connection.close();
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return groups.toString();
  }
}
