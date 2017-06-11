package com.strangersteam.strangers.notifications;

import android.content.Context;
import android.content.Intent;

/**
 * Created by kroli on 02.06.2017.
 */

public interface NotificationBuildStrategy {
    Class<?> getNotificationParentClass();

    Class<?> getNotificationTargetClass();

    void putExtraData(Intent intent, StrangerNotification notificationContent);

    int getNotificationId();

    String getNotificationTitle(Context context, StrangerNotification strangerNotification);

    String getNotificationContent(Context context, StrangerNotification strangerNotification);
}
