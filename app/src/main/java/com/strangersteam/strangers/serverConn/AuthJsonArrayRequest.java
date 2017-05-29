package com.strangersteam.strangers.serverConn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.strangersteam.strangers.LoginActivity;
import com.strangersteam.strangers.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kroli on 29.05.2017.
 */

public class AuthJsonArrayRequest extends JsonArrayRequest {

    private Context applicationContext;

    public AuthJsonArrayRequest(Context applicationContext, int method, String url, JSONArray jsonRequest, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, ForbiddenErrorListener.create(errorListener,applicationContext));
        this.applicationContext = applicationContext;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers = new HashMap<>(super.getHeaders());
        headers.put(SecurityProvider.TOKEN_HEADER_NAME,SecurityProvider.getToken(applicationContext));
        return headers;
    }
}

