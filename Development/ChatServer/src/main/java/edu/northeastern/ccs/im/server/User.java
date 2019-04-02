package edu.northeastern.ccs.im.server;

/**
 * Represents a User on the server
 */
public class User {

  private final int userId;
  private final String userName;
  private final String password;
  private final UserType type;

  /**
   * Constructs a User
   *
   * @param userId the unique user id
   * @param userName the handle of the user
   * @param password the user's password
   */
  public User(int userId, String userName, String password, UserType type) {
    this.userId = userId;
    this.userName = userName;
    this.password = password;
    this.type = type;
  }

  /**
   * @return the User's user id
   */
  public int getUserId() {
    return userId;
  }

  /**
   * @return the User's username
   */
  public String getUserName() {
    return userName;
  }

  /**
   * @return the User's password
   */
  public String getPassword() {
    return password;
  }

  public UserType getType() {
    return type;
  }


}
