package com.strangersteam.strangers.notifications;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Created by kroli on 02.06.2017.
 */

public class StrangerNotification implements NotificationContent{
    private NotificationType notificationType;
    private String title;
    private String content;
    private Long itemId;

    @Override
    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}