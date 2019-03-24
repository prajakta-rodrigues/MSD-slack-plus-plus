package edu.northeastern.ccs.im.server.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.sql.DataSource;

import edu.northeastern.ccs.im.server.utility.DatabaseConnection;

public class GroupRepository extends Repository {

  public GroupRepository(DataSource ds) {
    super(ds);
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
            groupMembers.add(String.valueOf(result.get("name")));
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