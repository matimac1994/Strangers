package com.strangersteam.strangers.notifications;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.strangersteam.strangers.model.StrangerEventMessage;
import com.strangersteam.strangers.serverConn.AuthJsonArrayRequest;
import com.strangersteam.strangers.serverConn.AuthJsonObjectRequest;
import com.strangersteam.strangers.serverConn.RequestQueueSingleton;
import com.strangersteam.strangers.serverConn.ServerConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NotificationIntentService extends IntentService {

    private static final String ACTION_START = "ACTION_START";

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");

        processStartNotification(intent);

    }

    private void processStartNotification(final Intent intent) {
        AuthJsonArrayRequest jsonObjectRequest = new AuthJsonArrayRequest(
                getApplicationContext(),
                Request.Method.GET,
                ServerConfig.NOTIFICATIONS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            ObjectMapper mapper = new ObjectMapper();
                            CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, StrangerNotification.class);

                            List<StrangerNotification> notifications = mapper.readValue(response.toString(), listType);
                            Log.d(getClass().getSimpleName(), "received " + notifications.size() + " notifications");
                            NotificationService.notifyAll(getApplicationContext(), notifications);
                        }catch (IOException e){
                            e.printStackTrace();
                        }finally {
                            WakefulBroadcastReceiver.completeWakefulIntent(intent);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        WakefulBroadcastReceiver.completeWakefulIntent(intent);
                        Log.e(getClass().getSimpleName(),"error: " + error.getMessage());
                    }
                }
        );

        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }

}
