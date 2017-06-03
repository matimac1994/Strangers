package com.strangersteam.strangers.serverConn;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kroli on 29.05.2017.
 */

public class AuthJsonArrayRequest extends JsonArrayRequest {

    private Context applicationContext;
    private  Map<String,String> params;

    public AuthJsonArrayRequest(Context applicationContext, int method, String url, Map<String,String> params, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, url, null, listener, ForbiddenErrorListener.create(errorListener,applicationContext));
        this.applicationContext = applicationContext;
        this.params = params;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers = new HashMap<>(super.getHeaders());
        headers.put(AuthTokenProvider.TOKEN_HEADER_NAME, AuthTokenProvider.getToken(applicationContext));
        return headers;
    }
}

