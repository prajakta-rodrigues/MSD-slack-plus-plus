package edu.northeastern.ccs.im.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;


import edu.northeastern.ccs.im.server.utility.DatabaseConnection;

/**
 * The Class UserRepository.
 */
public class UserRepository {

	/** The data source. */
	private DataSource dataSource;
	
	/** The connection. */
	private Connection connection;
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

	/**
	 * Instantiates a new user repository.
	 *
	 * @param ds the datasource
	 */
	public UserRepository(DataSource ds) {
		this.dataSource = ds;
	}

	/**
	 * Gets the user by user name.
	 *
	 * @param userName the user name
	 * @return the user by user name
	 */
	public User getUserByUserName(String userName) {
		User user = null; 
		try {
			connection = dataSource.getConnection();
			String query = "select * from slack.user where handle = ?";
			try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
				preparedStmt.setString(1, userName);
				try (ResultSet rs = preparedStmt.executeQuery()) {

					List<Map<String, Object>> results = DatabaseConnection.resultsList(rs);

					for (Map<String, Object> result : results) {
						user = new User(Integer.parseInt(String.valueOf(result.get("id"))),
								String.valueOf(result.get("handle")), String.valueOf(result.get("password")));
					}
					connection.close();
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return user;
	}

	/**
	 * Adds the user.
	 *
	 * @param user the user
	 * @return true, if successful
	 */
	public boolean addUser(User user) {
		int result = 0;
		try {
			connection = dataSource.getConnection();
			String query = "insert into slack.user(id, handle, password, account_created_date) values(?,?,?,?)";
			try (PreparedStatement preparedStmt = connection.prepareStatement(query)) {
				preparedStmt.setInt(1, user.getUserId());
				preparedStmt.setString(2, user.getUserName());
				preparedStmt.setString(3, user.getPassword());
				preparedStmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
				result = preparedStmt.executeUpdate();
				connection.close();
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);

		}
		return result == 1;
	}

}
