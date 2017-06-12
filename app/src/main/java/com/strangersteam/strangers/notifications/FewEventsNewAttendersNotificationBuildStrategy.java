package com.strangersteam.strangers.notifications;

import android.content.Context;
import android.content.Intent;

import com.strangersteam.strangers.MainActivity;
import com.strangersteam.strangers.MyEventsFragment;
import com.strangersteam.strangers.R;
import com.strangersteam.strangers.ShowEventActivity;

/**
 * Created by kroli on 12.06.2017.
 */

class FewEventsNewAttendersNotificationBuildStrategy implements NotificationBuildStrategy {

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
        intent.putExtra(MainActivity.FRAGMENT_ID_EXTRA, MainActivity.FRAGMENT_MY_EVENTS);
    }

    @Override
    public int getNotificationId() {
        return NotificationType.NEW_FEW_MY_EVENT_ATTENDERS.ordinal();
    }

    @Override
    public String getNotificationTitle(Context context, StrangerNotification strangerNotification) {
        return context.getString(R.string.notifications_new_attenders);
    }

    @Override
    public String getNotificationContent(Context context, StrangerNotification strangerNotification) {
        return context.getString(R.string.notifications_to_your_event_joined_users) + strangerNotification.getContent();
    }
}
