package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.repositories.DirectMessageRepository;
import edu.northeastern.ccs.im.server.repositories.FriendRepository;
import edu.northeastern.ccs.im.server.repositories.FriendRequestRepository;
import edu.northeastern.ccs.im.server.repositories.GroupInviteRepository;
import edu.northeastern.ccs.im.server.repositories.GroupRepository;
import edu.northeastern.ccs.im.server.repositories.MessageRepository;
import edu.northeastern.ccs.im.server.repositories.NotificationRepository;
import edu.northeastern.ccs.im.server.repositories.RepositoryFactory;
import edu.northeastern.ccs.im.server.repositories.UserGroupRepository;
import edu.northeastern.ccs.im.server.repositories.UserRepository;

/**
 * Abstract command class that holds the shared repositories to be used by all commands that extend
 * this class.
 */
abstract class ACommand implements Command {

  ACommand(){}

  /**
   * Repositories holding JDBC queries.
   */
  static DirectMessageRepository dmRepository;
  static GroupRepository groupRepository;
  static UserRepository userRepository;
  static UserGroupRepository userGroupRepository;
  static FriendRequestRepository friendRequestRepository;
  static FriendRepository friendRepository;
  static NotificationRepository notificationRepository;
  static MessageRepository messageRepository;
  static GroupInviteRepository groupInviteRepository;

  static {
    // instantiate the repositories to be used by all commands.
    groupRepository = RepositoryFactory.getGroupRepository();
    dmRepository = RepositoryFactory.getDirectMessageRepository();
    userRepository = RepositoryFactory.getUserRepository();
    userGroupRepository = RepositoryFactory.getUserGroupRepository();
    friendRequestRepository = RepositoryFactory.getFriendRequestRepository();
    friendRepository = RepositoryFactory.getFriendRepository();
    notificationRepository = RepositoryFactory.getNotificationRepository();
    messageRepository = RepositoryFactory.getMessageRepository();
    groupInviteRepository = RepositoryFactory.getGroupInviteRepository();
  }
}
