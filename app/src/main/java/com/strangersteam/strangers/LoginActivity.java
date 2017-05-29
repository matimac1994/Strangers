package com.strangersteam.strangers;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.strangersteam.strangers.serverConn.RequestQueueSingleton;
import com.strangersteam.strangers.serverConn.SecurityProvider;
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
    @Bind (R.id.login_login_button) Button _loginButton;
    @Bind (R.id.login_sign_in_button) Button _signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLogin(view);
            }
        });

        _signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignIn(view);
            }
        });

    }


    public void onClickSignIn(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void onClickLogin(View view){

        String loginString = _loginET.getText().toString();
        String passwordString = _passET.getText().toString();

        if(!validate())
            onLoginFailed();
        _loginButton.setEnabled(false);

        loginRequest(loginString,passwordString);

    }

    private void onLoginFailed() {
        Toast.makeText(this,R.string.login_failed, Toast.LENGTH_LONG);
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
                            String token = response.getString("token");
                            SecurityProvider.saveToken(getApplicationContext(),token);
                            //przykład wyciągnięcia token z shared preferencese: sharedPreferences.getString(getResources().getString(R.string.auth_token),null);
                            //_loginButton.setEnabled(true);
                        } catch (JSONException e) {
                            Log.e("JSONException", e.getMessage());
                            _passET.setError(getString(R.string.unknown_error));
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
                            _passET.setError(getString(R.string.login_invalid_data));
                        }else{
                            _passET.setError(getString(R.string.unknown_error));
                        }

                    }
                });

        RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private boolean validate(){

        boolean valid = true;

        String email = _loginET.getText().toString();
        String password = _passET.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginET.setError(getString(R.string.not_valid_mail));
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

}
