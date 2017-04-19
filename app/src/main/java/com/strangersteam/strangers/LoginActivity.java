package com.strangersteam.strangers;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.strangersteam.strangers.serverConn.RequestQueueSingleton;
import com.strangersteam.strangers.serverConn.ServerConfig;
import com.strangersteam.strangers.serverConn.dto.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private EditText loginET;
    private EditText passET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.loginET = (EditText) findViewById(R.id.login_login_edit_text);
        this.passET = (EditText) findViewById(R.id.login_password_edit_text);
    }


    public void onClickSignIn(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void onClickLogin(View view){

        String loginString = loginET.getText().toString();
        String passwordString = passET.getText().toString();

        //todo  walidacja bedzie tutaj potrzebna - np czy wpisane dane

        loginRequest(loginString,passwordString);

    }

    private void loginRequest(String loginString, String passwordString) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("login",loginString);
        params.put("password",passwordString);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ServerConfig.LOGIN, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            //jako odpowiedz na logowanie zakończone sukcesem dosjatemy json z jednym polem "token"
                            //ten token to tajny token unikalny dla każego użytkownia
                            //za każdym razem kiedy będziemy wykonywać zapytanie wygamające autoryzacji
                            //będzie trzeba przesłać ten  token w nagłówku zapytania, dlatego zapisuje go w sharedPreferences bo łatwo ladnie i wygodnie
                            //będzie też można łatwo sprawdzić czy użytkownik jest zalogowany - sprawdzamy czy w szaref prefs jest wartość z takiem token
                            //przy wylogowaniu albo jak dostaniemy gdzies 403 ze skonczyla sie sesja to bedziemy usuwan z szared prefs tokena
                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.auth_token),MODE_PRIVATE);
                            sharedPreferences.edit().putString(getResources().getString(R.string.auth_token),response.getString("token")).apply();
                            //przykład wyciągnięcia token z shared preferencese: sharedPreferences.getString(getResources().getString(R.string.auth_token),null);
                        } catch (JSONException e) {
                            Log.e("JSONException", e.getMessage());
                            passET.setError(getString(R.string.unknown_error));
                            return;
                        }

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null && error.networkResponse.statusCode == 403){
                            passET.setError(getString(R.string.login_invalid_data));
                        }else{
                            passET.setError(getString(R.string.unknown_error));
                        }

                    }
                });

        RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

}
