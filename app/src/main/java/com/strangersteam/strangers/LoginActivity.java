package com.strangersteam.strangers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.strangersteam.strangers.serverConn.RequestQueueSingleton;
import com.strangersteam.strangers.serverConn.AuthTokenProvider;
import com.strangersteam.strangers.serverConn.ServerConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Bind (R.id.login_login_edit_text) EditText _loginET;
    @Bind (R.id.login_password_edit_text) EditText _passET;
    @Bind (R.id.login_login_button) Button loginButton;
    @Bind (R.id.login_register_button) Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if(AuthTokenProvider.isTokenExist(getApplicationContext())){
            Toast.makeText(this,"Debug Jesteś już zalogowany",Toast.LENGTH_SHORT).show();
            goToMap();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogin(view);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignIn(view);
            }
        });

    }


    public void onClickSignIn(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void onClickLogin(View view){
        if(!validate()){
            onLoginFailed();
        }
        String loginString = _loginET.getText().toString();
        String passwordString = _passET.getText().toString();
        loginButton.setEnabled(false);
        loginRequest(loginString,passwordString);

    }

    private boolean validate(){
        boolean valid = true;

        String login = _loginET.getText().toString();
        String password = _passET.getText().toString();

        if (login.isEmpty()) {
            _loginET.setError(getString(R.string.not_valid_login));
            valid = false;
        } else {
            _loginET.setError(null);
        }

        if (password.isEmpty() || password.length() < 2) {
            _passET.setError(getString(R.string.not_valid_password));
            valid = false;
        } else {
            _passET.setError(null);
        }

        return valid;
    }

    private void onLoginFailed() {
        loginButton.setEnabled(true);
        Toast.makeText(this,R.string.login_failed, Toast.LENGTH_LONG).show();

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
                            String token = response.getString("token");
                            AuthTokenProvider.saveToken(getApplicationContext(),token);
                            goToMap();
                        } catch (JSONException e) {
                            Log.e("JSONException", e.getMessage());
                            _passET.setError(getString(R.string.unknown_error));
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if(error.networkResponse != null && error.networkResponse.statusCode == 403){
                            _passET.setError(getString(R.string.login_invalid_data));
                        }else{
                            _passET.setError(getString(R.string.unknown_error));
                        }

                    }
                });

        RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void goToMap() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

}
