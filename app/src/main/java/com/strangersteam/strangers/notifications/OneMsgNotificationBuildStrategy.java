package com.strangersteam.strangers.notifications;

import android.content.Context;
import android.content.Intent;

import com.strangersteam.strangers.MainActivity;
import com.strangersteam.strangers.ShowEventActivity;

/**
 * Created by kroli on 02.06.2017.
 */

public class OneMsgNotificationBuildStrategy implements NotificationBuildStrategy {
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
    public void putExtraData(Intent intent, StrangerNotification notification) {
        intent.putExtra(ShowEventActivity.EVENT_ID, notification.getItemId());
    }

    @Override
    public int getNotificationId() {
        return ONE_MESSAGE_NOTIFICATION_ID;
    }

    @Override
    public String getNotificationTitle(Context context, StrangerNotification strangerNotification) {
        return strangerNotification.getTitle();
    }

    @Override
    public String getNotificationContent(Context context, StrangerNotification strangerNotification) {
        return strangerNotification.getContent();
    }
}
