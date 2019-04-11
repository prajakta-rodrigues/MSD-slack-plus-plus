package edu.northeastern.ccs.im.server.repositories;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.sql.DataSource;

import edu.northeastern.ccs.im.server.models.SlackGroup;
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
          if (rs.first()) {
            group = groupFromResultSet(rs);
          }
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } finally {
      closeConnection(connection);
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
      String query = "select * from slack.group where name = ? AND !deleted LIMIT 1";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setString(1, groupName);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          if (rs.first()) {
            group = groupFromResultSet(rs);
          }
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } finally {
      closeConnection(connection);
    }
    return group;
  }

  /**
   * Add a group to the database, if valid
   *
   * @param group the group to add
   * @return boolean representing whether or not the write was successful
   */
  public boolean addGroup(SlackGroup group) {
    int count = 0;
    try {
      connection = dataSource.getConnection();
      String query = "CALL slack.make_group(?,?,?,?)";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, group.getCreatorId());
        preparedStmt.setInt(2, group.getGroupId());
        preparedStmt.setString(3, group.getGroupName());
        preparedStmt.setString(4, group.getPassword());
        count = preparedStmt.executeUpdate();
      }

    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } finally {
      closeConnection(connection);
    }
    return count > 0;
  }

  /**
   * Find if the group has the given member.
   *
   * @param memberId the id of the user in question
   * @param groupId  the name of the group
   * @return Whether or not the given userId is a member of the given group.
   */
  public boolean groupHasMember(int memberId, int groupId) {
    boolean hasMember = false;
    try {
      connection = dataSource.getConnection();
      String query = "SELECT user_id " +
              "FROM slack.user_group ug JOIN slack.group g ON (g.id = ug.group_id)" +
              "WHERE group_id = ? AND user_id = ? AND !deleted";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, groupId);
        preparedStmt.setInt(2, memberId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          hasMember = !DatabaseConnection.resultsList(rs).isEmpty();
        }
      }
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } finally {
      closeConnection(connection);
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
              "WHERE ug.user_id = ? AND !deleted";
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
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } finally {
      closeConnection(connection);
    }
    return groups.toString();
  }

  /**
   * Gets the group by channel id.
   *
   * @param channelId the channel id
   * @return the group by id
   */
  public SlackGroup getGroupByChannelId(int channelId) {
    SlackGroup group = null;
    try {
      connection = dataSource.getConnection();
      String query = "select * from slack.group where channel_id = ? AND !deleted LIMIT 1";
      try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
        preparedStmt.setInt(1, channelId);
        try (ResultSet rs = preparedStmt.executeQuery()) {
          if (rs.first()) {
            group = groupFromResultSet(rs);
          }
        }
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } finally {
      closeConnection(connection);
    }
    return group;
  }

  /**
   * 'Deletes' the group by id.
   *
   * @param groupId the id of the group to be deleted.
   * @return whether or not the update succeeded.
   */
  public boolean deleteGroup(int modId, int groupId) {
    boolean success = false;
    String query = "CALL delete_group(?,?,?)";
    try {
      connection = dataSource.getConnection();
      try (CallableStatement stmt = connection.prepareCall(query)) {
        stmt.setInt(1, modId);
        stmt.setInt(2, groupId);
        stmt.registerOutParameter(3, Types.TINYINT);
        stmt.executeUpdate();
        success = stmt.getBoolean(3);
      }
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    } finally {
      closeConnection(connection);
    }
    return success;
  }

  /**
   * Converts a resultSet into a SlackGroup.
   * @param rs the Result of the query.
   * @return the SlackGroup represented by the ResultSet
   * @throws SQLException the exception thrown by JDBC
   */
  private static SlackGroup groupFromResultSet(ResultSet rs) throws SQLException {
    return new SlackGroup(
            rs.getInt("id"),
            rs.getInt("creator_id"),
            rs.getString("name"),
            rs.getInt("channel_id"),
            rs.getBoolean("deleted"),
            rs.getString("password"));
  }
}
