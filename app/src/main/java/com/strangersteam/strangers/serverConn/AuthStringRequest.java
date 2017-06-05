package com.strangersteam.strangers.serverConn;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kroli on 29.05.2017.
 */

public class AuthStringRequest extends StringRequest {

    private Context applicationContext;

    public AuthStringRequest(Context applicationContext, int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        this.applicationContext = applicationContext;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers = new HashMap<>(super.getHeaders());
        headers.put(AuthTokenProvider.TOKEN_HEADER_NAME, AuthTokenProvider.getToken(applicationContext));
        return headers;
    }


}
