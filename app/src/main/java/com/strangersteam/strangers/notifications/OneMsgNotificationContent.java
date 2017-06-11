package com.strangersteam.strangers.notifications;

/**
 * Created by kroli on 02.06.2017.
 */

public class OneMsgNotificationContent implements NotificationContent{

    private Long eventId;
    private String eventName;
    private String messageContent;

    @Override
    public void setTitle(String title) {
        this.eventName = title;
    }

    @Override
    public void setContent(String content) {
        this.messageContent = content;
    }

    @Override
    public String getTitle() {
        return eventName;
    }

    @Override
    public String getContent() {
        return messageContent;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Long getEventId() {
        return eventId;
    }
}
