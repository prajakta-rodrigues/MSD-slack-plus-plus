package edu.northeastern.ccs.im.server.models;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * The Class Notification represents notification entity from database.
 */
public class Notification {

  /**
   * The id.
   */
  private int id;

  /**
   * The reciever id.
   */
  private int recieverId;

  /**
   * The associated user id of the user involved in the notification.
   */
  private int associatedUserId;

  /**
   * The type.
   */
  private NotificationType type;

  /**
   * The created date.
   */
  private Timestamp createdDate;

  /**
   * The is new represents if the notification is new.
   */
  private boolean isNew;

  /**
   * The associated group id of the group involved in the notification.
   */
  private int associatedGroupId;

  /**
   * Instantiates a new notification.
   */
  public Notification() {
    super();
  }

  /**
   * Gets the reciever id.
   *
   * @return the reciever id
   */
  public int getRecieverId() {
    return recieverId;
  }


  /**
   * Sets the reciever id.
   *
   * @param recieverId the new reciever id
   */
  public void setRecieverId(int recieverId) {
    this.recieverId = recieverId;
  }


  /**
   * Gets the associated user id.
   *
   * @return the associated user id
   */
  public int getAssociatedUserId() {
    return associatedUserId;
  }


  /**
   * Sets the associated user id.
   *
   * @param associatedUserId the new associated user id
   */
  public void setAssociatedUserId(int associatedUserId) {
    this.associatedUserId = associatedUserId;
  }

  /**
   * Gets the created date.
   *
   * @return the created date
   */
  public Timestamp getCreatedDate() {
    return createdDate;
  }


  /**
   * Sets the created date.
   *
   * @param createdDate the new created date
   */
  public void setCreatedDate(Timestamp createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * Checks if is new.
   *
   * @return true, if is new
   */
  public boolean isNew() {
    return isNew;
  }


  /**
   * Sets the new.
   *
   * @param isNew the new new
   */
  public void setNew(boolean isNew) {
    this.isNew = isNew;
  }


  /**
   * Gets the associated group id.
   *
   * @return the associated group id
   */
  public int getAssociatedGroupId() {
    return associatedGroupId;
  }


  /**
   * Sets the associated group id.
   *
   * @param associatedGroupId the new associated group id
   */
  public void setAssociatedGroupId(int associatedGroupId) {
    this.associatedGroupId = associatedGroupId;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public NotificationType getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type the new type
   */
  public void setType(NotificationType type) {
    this.type = type;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(int id) {
    this.id = id;
  }


  /**
   * Make group invite notification.
   *
   * @param groupId the group id
   * @param senderId the sender of the request
   * @param inviteeId the invitee id
   * @return the notification
   */
  public static Notification makeGroupInviteNotification(int groupId, int senderId, int inviteeId) {
    Notification notification = new Notification();
    notification.setAssociatedGroupId(groupId);
    notification.setAssociatedUserId(senderId);
    notification.setRecieverId(inviteeId);
    notification.setType(NotificationType.GROUP_INVITE);
    notification.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
    notification.setNew(true);
    return notification;
  }


  /**
   * Makes a friend request notification from the given sender id to the second given receiver id.
   *
   * @param senderId the sender of the request
   * @param receiverId the receiver of the request
   * @param type the type of friend request notification
   * @return the friend request notification
   */
  public static Notification makeFriendRequestNotification(int senderId, int receiverId,
                                                           NotificationType type) {
    Notification notification = new Notification();
    notification.setType(type);
    notification.setAssociatedUserId(senderId);
    notification.setRecieverId(receiverId);
    notification.setNew(true);
    notification.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
    return notification;
  }

  /**
   * Makes a new moderator notification from the given sender id to the second given receiver id.
   *
   * @param groupId the group that is gaining a moderator.
   * @param senderId the user adding a new moderator
   * @param receiverId the user being added as a moderator
   * @return the friend request notification
   */
  public static Notification makeNewModeratorNotification(int groupId, int senderId, int receiverId) {
    Notification notification = new Notification();
    notification.setType(NotificationType.NEW_MODERATOR);
    notification.setAssociatedGroupId(groupId);
    notification.setAssociatedUserId(senderId);
    notification.setRecieverId(receiverId);
    notification.setNew(true);
    notification.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
    return notification;
  }

}