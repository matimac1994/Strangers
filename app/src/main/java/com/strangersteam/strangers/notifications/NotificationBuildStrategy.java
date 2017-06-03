package com.strangersteam.strangers.notifications;

import android.content.Intent;

/**
 * Created by kroli on 02.06.2017.
 */

public interface NotificationBuildStrategy<T extends NotificationContent> {
    Class<?> getNotificationParentClass();

    Class<?> getNotificationTargetClass();

    void putExtraData(Intent intent, T notificationContent);

    int getNotificationId();
}
