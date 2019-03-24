package edu.northeastern.ccs.im.server;

import java.sql.Timestamp;

public class Notification {

  private int id;
  private int recieverId;
  private int associatedUserId;
  private NotificationType type;
  private Timestamp createdDate;
  private boolean isNew;
  private int associatedGroupId;


  public Notification() {
    super();
  }

  public int getRecieverId() {
    return recieverId;
  }



  public void setRecieverId(int recieverId) {
    this.recieverId = recieverId;
  }


  public int getAssociatedUserId() {
    return associatedUserId;
  }



  public void setAssociatedUserId(int associatedUserId) {
    this.associatedUserId = associatedUserId;
  }

  public Timestamp getCreatedDate() {
    return createdDate;
  }



  public void setCreatedDate(Timestamp createdDate) {
    this.createdDate = createdDate;
  }



  public boolean isNew() {
    return isNew;
  }



  public void setNew(boolean isNew) {
    this.isNew = isNew;
  }



  public int getAssociatedGroupId() {
    return associatedGroupId;
  }



  public void setAssociatedGroupId(int associatedGroupId) {
    this.associatedGroupId = associatedGroupId;
  }

  public NotificationType getType() {
    return type;
  }

  public void setType(NotificationType type) {
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }



}
