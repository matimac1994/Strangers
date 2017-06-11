package com.strangersteam.strangers.notifications;

import android.content.Context;
import android.content.Intent;

import com.strangersteam.strangers.MainActivity;
import com.strangersteam.strangers.R;

/**
 * Created by kroli on 02.06.2017.
 */

public class FewAttendEventMsgNotificationBuildStrategy implements NotificationBuildStrategy{
    public static final int FEW_ATTENDING_EVENTS_NOTIFICATION_ID = 3;

    @Override
    public Class<?> getNotificationParentClass() {
        return MainActivity.class;
    }

    @Override
    public Class<?> getNotificationTargetClass() {
        return MainActivity.class;
    }

    @Override
    public void putExtraData(Intent intent, StrangerNotification notificationContent) {
        intent.putExtra(MainActivity.FRAGMENT_ID_EXTRA, FEW_ATTENDING_EVENTS_NOTIFICATION_ID);
    }

    @Override
    public int getNotificationId() {
        return FEW_ATTENDING_EVENTS_NOTIFICATION_ID;
    }

    @Override
    public String getNotificationTitle(Context context, StrangerNotification strangerNotification) {
        return context.getString(R.string.notification_title_new_msgs);
    }

    @Override
    public String getNotificationContent(Context context, StrangerNotification strangerNotification) {
        return strangerNotification.getContent();
    }
}
