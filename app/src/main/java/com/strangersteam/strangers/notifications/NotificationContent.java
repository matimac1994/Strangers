package com.strangersteam.strangers.notifications;

/**
 * Created by kroli on 02.06.2017.
 */

public interface NotificationContent {

    public NotificationType getNotificationType();
    public String getTitle();
    public String getContent();
    public Long getItemId();
}

