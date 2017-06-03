package com.strangersteam.strangers.notifications;

import android.content.Intent;

import com.strangersteam.strangers.MainActivity;
import com.strangersteam.strangers.R;

/**
 * Created by kroli on 02.06.2017.
 */

public class FewMyEventsMsgNotificationBuildStrategy implements NotificationBuildStrategy<FewEventsMsgContent> {

    public static final int FEW_MY_EVENTS_NOTIFICATION_ID = 2;

    @Override
    public Class<?> getNotificationParentClass() {
        return MainActivity.class;
    }

    @Override
    public Class<?> getNotificationTargetClass() {
        return MainActivity.class;
    }

    @Override
    public void putExtraData(Intent intent, FewEventsMsgContent notificationContent) {
        intent.putExtra(MainActivity.FRAGMENT_ID_EXTRA, FEW_MY_EVENTS_NOTIFICATION_ID);
    }

    @Override
    public int getNotificationId() {
        return FEW_MY_EVENTS_NOTIFICATION_ID;
    }
}
