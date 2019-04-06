package edu.northeastern.ccs.im.server.repositories;

import edu.northeastern.ccs.im.server.utility.DatabaseConnection;

/**
 * Factory class for Repositories.
 */
public abstract class RepositoryFactory {

  private RepositoryFactory(){}

  private static DirectMessageRepository directMessageRepository;
  private static FriendRepository friendRepository;
  private static FriendRequestRepository friendRequestRepository;
  private static GroupInviteRepository groupInviteRepository;
  private static GroupRepository groupRepository;
  private static MessageRepository messageRepository;
  private static NotificationRepository notificationRepository;
  private static UserGroupRepository userGroupRepository;
  private static UserRepository userRepository;

  public static DirectMessageRepository getDirectMessageRepository() {
    if (directMessageRepository == null) {
      directMessageRepository = new DirectMessageRepository(DatabaseConnection.getDataSource());
    }
    return directMessageRepository;
  }

  public static FriendRepository getFriendRepository() {
    if (friendRepository == null) {
      friendRepository = new FriendRepository(DatabaseConnection.getDataSource());
    }
    return friendRepository;
  }

  public static FriendRequestRepository getFriendRequestRepository() {
    if (friendRequestRepository == null) {
      friendRequestRepository = new FriendRequestRepository(DatabaseConnection.getDataSource());
    }
    return friendRequestRepository;
  }

  public static GroupInviteRepository getGroupInviteRepository() {
    if (groupInviteRepository == null) {
      groupInviteRepository = new GroupInviteRepository(DatabaseConnection.getDataSource());
    }
    return groupInviteRepository;
  }

  public static GroupRepository getGroupRepository() {
    if (groupRepository == null) {
      groupRepository = new GroupRepository(DatabaseConnection.getDataSource());
    }
    return groupRepository;
  }

  public static MessageRepository getMessageRepository() {
    if (messageRepository == null) {
      messageRepository = new MessageRepository(DatabaseConnection.getDataSource());
    }
    return messageRepository;
  }

  public static NotificationRepository getNotificationRepository() {
    if (notificationRepository == null) {
      notificationRepository = new NotificationRepository(DatabaseConnection.getDataSource());
    }
    return notificationRepository;
  }

  public static UserGroupRepository getUserGroupRepository() {
    if (userGroupRepository == null) {
      userGroupRepository = new UserGroupRepository(DatabaseConnection.getDataSource());
    }
    return userGroupRepository;
  }

  public static UserRepository getUserRepository() {
    if (userRepository == null) {
      userRepository = new UserRepository();
    }
    return userRepository;
  }
}
