package com.strangersteam.strangers.serverConn;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.strangersteam.strangers.LoginActivity;

/**
 * Created by kroli on 29.05.2017.
 */
public class ForbiddenErrorListener {
    public static Response.ErrorListener create(final Response.ErrorListener errorListener, final Context applicationContext) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse.statusCode == 403){
                    SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(SecurityProvider.TOKEN_HEADER_NAME,Context.MODE_PRIVATE);
                    sharedPreferences.edit().remove(SecurityProvider.TOKEN_HEADER_NAME).apply();
                    Intent intent = new Intent(applicationContext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    applicationContext.startActivity(intent);
                    Toast.makeText(applicationContext,"Sesja wygasła", Toast.LENGTH_SHORT).show();
                }else{
                    errorListener.onErrorResponse(error);
                }
            }
        };
    }
}
