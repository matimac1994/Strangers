package com.strangersteam.strangers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.strangersteam.strangers.notifications.NotificationEventReceiver;
import com.strangersteam.strangers.serverConn.AuthTokenProvider;

/**
 * Created by kroli on 29.05.2017.
 */

public class LogoutHandler {

    public static void logout(Context applicationContext) {
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(AuthTokenProvider.TOKEN_HEADER_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(AuthTokenProvider.TOKEN_HEADER_NAME).apply();
        stopNotifications(applicationContext);
        Intent intent = new Intent(applicationContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        applicationContext.startActivity(intent);
    }

    private static void stopNotifications(Context applicationContext) {
        NotificationEventReceiver.cancelOldAlarm(applicationContext);
    }
}
