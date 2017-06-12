package com.strangersteam.strangers.notifications;

import android.content.Context;
import android.content.Intent;

import com.strangersteam.strangers.MainActivity;
import com.strangersteam.strangers.R;
import com.strangersteam.strangers.ShowEventActivity;

/**
 * Created by kroli on 12.06.2017.
 */

class NewAttendersNotificationBuildStrategy implements NotificationBuildStrategy {

    public static final int NEW_MY_EVENT_ATTENDER_NOTIFICATION_ID = 5;

    @Override
    public Class<?> getNotificationParentClass() {
        return MainActivity.class;
    }

    @Override
    public Class<?> getNotificationTargetClass() {
        return ShowEventActivity.class;
    }

    @Override
    public void putExtraData(Intent intent, StrangerNotification notificationContent) {
        intent.putExtra(ShowEventActivity.EVENT_ID,notificationContent.getItemId());
    }

    @Override
    public int getNotificationId() {
        return NEW_MY_EVENT_ATTENDER_NOTIFICATION_ID;
    }

    @Override
    public String getNotificationTitle(Context context, StrangerNotification strangerNotification) {
        return strangerNotification.getTitle();
    }

    @Override
    public String getNotificationContent(Context context, StrangerNotification strangerNotification) {
        return context.getString(R.string.notifications_somebody_joined_to_your_event) + strangerNotification.getContent();
    }
}
