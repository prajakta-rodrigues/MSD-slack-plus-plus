package edu.northeastern.ccs.im.server.commands;

import edu.northeastern.ccs.im.server.constants.StringConstants.CommandDescriptions;
import edu.northeastern.ccs.im.server.constants.StringConstants.ErrorMessages;
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
      return ErrorMessages.NO_NOTIFICATIONS;
    }
    String result = NotificationConvertor.getNotificationsAsText(listNotifications);
    notificationRepository.markNotificationsAsNotNew(listNotifications);
    return "Notifications:\n" + result;
  }


  @Override
  public String description() {
    return CommandDescriptions.NOTIFICATION_DESCRIPTION;
  }

}
