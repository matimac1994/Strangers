package com.strangersteam.strangers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.strangersteam.strangers.notifications.NotificationEventReceiver;
import com.strangersteam.strangers.serverConn.AuthTokenProvider;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kroli on 11.06.2017.
 */

public class LoginHandler {

    public static void onLogin(Context applicationContext, JSONObject response, AppCompatActivity loginActivity) throws JSONException{
        String token = response.getString("token");
        AuthTokenProvider.saveToken(applicationContext,token);
        NotificationEventReceiver.setupAlarm(applicationContext);
        goToMap(applicationContext, loginActivity);
        runNotifications(applicationContext);
    }

    private static void runNotifications(Context applicationContext) {
        NotificationEventReceiver.setupAlarm(applicationContext);
    }

    public static void onLogged(Context applicationContext, AppCompatActivity loginActivity){
        goToMap(applicationContext,loginActivity);
    }

    private static void goToMap(Context applicationContext, AppCompatActivity loginActivity) {
        Intent intent = new Intent(applicationContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        applicationContext.startActivity(intent);
        loginActivity.finish();
        loginActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
