package com.strangersteam.strangers.notifications;

import android.content.Intent;

import com.strangersteam.strangers.MainActivity;
import com.strangersteam.strangers.ShowEventActivity;

/**
 * Created by kroli on 02.06.2017.
 */

public class OneMsgNotificationBuildStrategy implements NotificationBuildStrategy<OneMsgNotificationContent> {
    public static final int ONE_MESSAGE_NOTIFICATION_ID = 1;

    @Override
    public Class<?> getNotificationParentClass() {
        return MainActivity.class;
    }

    @Override
    public Class<?> getNotificationTargetClass() {
        return ShowEventActivity.class;
    }

    @Override
    public void putExtraData(Intent intent, OneMsgNotificationContent notificationContent) {
        intent.putExtra(ShowEventActivity.EVENT_ID, notificationContent.getEventId());
    }

    @Override
    public int getNotificationId() {
        return ONE_MESSAGE_NOTIFICATION_ID;
    }


}
