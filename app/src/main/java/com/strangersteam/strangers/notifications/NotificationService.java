package com.strangersteam.strangers.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import java.util.List;

/**
 * Created by kroli on 02.06.2017.
 */

public class NotificationService {

    public static void cancel(Context context, int notificationId){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notificationId);
    }

    private static void notify(Context context, StrangerNotification strangerNotification){

        NotificationBuilder notificationBuilder = createNotificationBuilder(context,strangerNotification.getNotificationType());

        Notification notification = notificationBuilder.buildNotification(strangerNotification);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationBuilder.getNotificationId(), notification);

    }

    private static NotificationBuilder createNotificationBuilder(Context context, NotificationType notificationType) {

        NotificationBuildStrategy strategy;
        switch (notificationType){
            case ONE_EVENT_MSG:
                strategy = new OneMsgNotificationBuildStrategy();
                break;
            case FEW_MY_EVENTS_MSG:
                strategy = new FewMyEventsMsgNotificationBuildStrategy();
                break;
            case FEW_ATTEND_EVENTS_MSG:
                strategy = new FewAttendEventMsgNotificationBuildStrategy();
                break;
            case NEW_FEW_MY_EVENT_ATTENDERS:
                strategy = new FewEventsNewAttendersNotificationBuildStrategy();
                break;
            case NEW_ONE_MY_EVENT_ATTENDERS:
                strategy = new OneEventNewAttendersNotificationBuildStratedy();
                break;
            default:
                throw new IllegalArgumentException();
        }

       return new NotificationBuilder(context,strategy);
    }

    public static void notifyAll(Context applicationContext, List<StrangerNotification> notifications) {
        for( StrangerNotification notification : notifications){
            notify(applicationContext,notification);
        }
    }
}
