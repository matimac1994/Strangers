package com.strangersteam.strangers.notifications;

/**
 * Created by kroli on 02.06.2017.
 */

public class StrangerNotification<T extends NotificationContent> {
    private NotificationType notificationType;
    private T notificationContent;

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public T getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(T notificationContent) {
        this.notificationContent = notificationContent;
    }

    public int getNotificationId() {
        return 0;
    }
}
