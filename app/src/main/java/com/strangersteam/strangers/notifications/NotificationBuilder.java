package com.strangersteam.strangers.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;


import com.strangersteam.strangers.R;

public class NotificationBuilder {

    private Context context;
    private NotificationBuildStrategy strategy;

    public NotificationBuilder(Context context, NotificationBuildStrategy strategy) {
        this.context = context;
        this.strategy = strategy;
    }

    public int getNotificationId() {
        return strategy.getNotificationId();
    }

    public Notification buildNotification(StrangerNotification strangerNotification){
         NotificationCompat.Builder mBuilder =
                 new NotificationCompat.Builder(context)
                         .setSmallIcon(R.drawable.strangers_logo)
                         .setContentTitle(strangerNotification.getNotificationContent().getTitle())
                         .setContentText(strangerNotification.getNotificationContent().getContent())
                         .setAutoCancel(true);


         PendingIntent resultPendingIntent = createPendingIntent(strangerNotification);

         mBuilder.setContentIntent(resultPendingIntent);

         return  mBuilder.build();
    }

    private PendingIntent createPendingIntent(StrangerNotification strangerNotification) {
        TaskStackBuilder stackBuilder = createStackBuilder(strangerNotification);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        return resultPendingIntent;
    }


    private TaskStackBuilder createStackBuilder(StrangerNotification strangerNotification) {
        Intent resultIntent = createNotificationIntent(strangerNotification);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(strategy.getNotificationParentClass());
        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder;
    }

    private Intent createNotificationIntent(StrangerNotification strangerNotification) {
        Intent intent = new Intent(context, strategy.getNotificationTargetClass());
        strategy.putExtraData(intent,strangerNotification.getNotificationContent());
        return intent;
    }

}
