package com.strangersteam.strangers.serverConn;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.strangersteam.strangers.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by kroli on 29.05.2017.
 */
public class AuthTokenProvider {

    public static final String TOKEN_HEADER_NAME = "X-Auth-Token";

    public static void saveToken(Context applicationContext, String token) {
        SharedPreferences sharedPreferences = getSharedPrefs(applicationContext);
        sharedPreferences.edit().putString(TOKEN_HEADER_NAME,token).apply();
    }

    public static String getToken(Context applicationContext) {
        SharedPreferences sharedPreferences = getSharedPrefs(applicationContext);
        String token = sharedPreferences.getString(TOKEN_HEADER_NAME,"");
        System.out.println(token);
        return token;
    }

    private static SharedPreferences getSharedPrefs(Context applicationContext) {
        return applicationContext.getSharedPreferences(TOKEN_HEADER_NAME,MODE_PRIVATE);
    }

    public static boolean isTokenExist(Context applicationContext) {
        return !(getToken(applicationContext).trim().isEmpty());
    }
}

