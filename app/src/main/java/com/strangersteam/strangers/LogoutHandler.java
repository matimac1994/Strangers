package com.strangersteam.strangers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.strangersteam.strangers.serverConn.SecurityProvider;

/**
 * Created by kroli on 29.05.2017.
 */

public class LogoutHandler {

    public static void logout(Context applicationContext) {
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(SecurityProvider.TOKEN_HEADER_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(SecurityProvider.TOKEN_HEADER_NAME).apply();
        Intent intent = new Intent(applicationContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        applicationContext.startActivity(intent);
    }
}
