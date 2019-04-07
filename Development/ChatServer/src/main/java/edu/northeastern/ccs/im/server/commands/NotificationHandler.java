package edu.northeastern.ccs.im.server.commands;

import java.util.List;

import edu.northeastern.ccs.im.server.models.Notification;
import edu.northeastern.ccs.im.server.models.NotificationConvertor;

/**
 * The Class NotificationHandler handles command notification.
 */
class NotificationHandler extends ACommand {

  @Override
  public String apply(String[] params, Integer senderId) {

    List<Notification> listNotifications =
            notificationRepository.getAllNotificationsByReceiverId(senderId);
    if (listNotifications == null || listNotifications.isEmpty()) {
      return "No notifications to show";
    }
    String result = NotificationConvertor.getNotificationsAsText(listNotifications);
    notificationRepository.markNotificationsAsNotNew(listNotifications);
    return "Notifications:\n" + result;
  }


  @Override
  public String description() {
    return "Shows recent notifications";
  }

}
