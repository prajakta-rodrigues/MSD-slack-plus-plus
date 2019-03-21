package edu.northeastern.ccs.im.server.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.sql.DataSource;

import edu.northeastern.ccs.im.server.SlackGroup;
import edu.northeastern.ccs.im.server.utility.DatabaseConnection;

public class GroupRepository extends Repository {

  public GroupRepository(DataSource ds) {
    super(ds);
  }

  public GroupRepository() { super(); }

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
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return count > 0;
  }

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
    } catch (SQLException e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
    }
    return groups.toString();
  }
}
