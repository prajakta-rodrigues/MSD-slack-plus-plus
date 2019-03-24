package edu.northeastern.ccs.im.server.repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
}