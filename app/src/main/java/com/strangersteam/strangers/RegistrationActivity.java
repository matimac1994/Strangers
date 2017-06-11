package com.strangersteam.strangers;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.strangersteam.strangers.notifications.NotificationEventReceiver;
import com.strangersteam.strangers.serverConn.AuthJsonObjectRequest;
import com.strangersteam.strangers.serverConn.AuthStringRequest;
import com.strangersteam.strangers.serverConn.AuthTokenProvider;
import com.strangersteam.strangers.serverConn.RequestQueueSingleton;
import com.strangersteam.strangers.serverConn.ServerConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegistrationActivity extends AppCompatActivity implements EditProfileDatePickerFragment.OnCompleteListener  {

    @Bind (R.id.registration_nick_edit_text) EditText _loginET;
    @Bind (R.id.registration_password_edit_text) EditText _passET;
    @Bind (R.id.registration_password2_edit_text) EditText _pass2ET;
    @Bind (R.id.registration_sign_in_button) Button _signInButton;

    private Calendar birthdayDate;
    private TextView dateSelectedTV;
    private boolean isFemale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        initTextViews();

        _signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignIn(view);
            }
        });
    }

    private void initTextViews() {
        dateSelectedTV = (TextView) findViewById(R.id.registration_birthday_tv);
    }

    public void onClickSignIn(View view){
        if(validate()){
           registerRequest();
        }
    }

    private void registerRequest() {
        String url = ServerConfig.REGISTER;

        final String login = _loginET.getText().toString();
        final String password = _passET.getText().toString();

        Map<String,String> params = new HashMap<>();
        params.put("login", login);
        params.put("hashedPw", password);
        params.put("birthdate", Long.toString(birthdayDate.getTimeInMillis()));
        params.put("female", Boolean.toString(isFemale));

        AuthJsonObjectRequest request = new AuthJsonObjectRequest(
                getApplicationContext(),
                Request.Method.POST,
                url,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(),"Zarejestrowano",Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(),"Logowanie...",Toast.LENGTH_SHORT).show();
                        loginRequest(login,password);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error.networkResponse.statusCode == 400){
                            _loginET.setError("Login zajęty");
                        }
                    }
                }
        );

        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
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
                            NotificationEventReceiver.setupAlarm(getApplicationContext());
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

    private boolean validate(){

        boolean valid = true;

        String login = _loginET.getText().toString();
        String password = _passET.getText().toString();
        String password2 = _pass2ET.getText().toString();

        if (login.isEmpty()) {
            _loginET.setError(getString(R.string.not_valid_login));
            valid = false;
        } else {
            _loginET.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            _passET.setError(getString(R.string.not_valid_password));
            valid = false;
        } else {
            _passET.setError(null);
        }

        if (!password.equals(password2)){
            _pass2ET.setError(getString(R.string.different_password2));
            valid = false;
        }

        if(birthdayDate == null){
            Toast.makeText(this, getString(R.string.type_birthday), Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if(birthdayDate.after(Calendar.getInstance())){
            Toast.makeText(this, getString(R.string.valid_birthday), Toast.LENGTH_SHORT).show();
            valid = false;
        }

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.registration_radio_group);
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, getString(R.string.valid_sex), Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private void goToMap() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public void onComplete(int year, int month, int day) {
        birthdayDate = new GregorianCalendar();
        birthdayDate.set(Calendar.YEAR, year);
        birthdayDate.set(Calendar.MONTH, month);
        birthdayDate.set(Calendar.DAY_OF_MONTH, day);
        setUpDateSelectedTV();
    }

    private void setUpDateSelectedTV(){
        if(birthdayDate != null){
            dateSelectedTV.setText(new SimpleDateFormat("dd.MM.yyyy", new Locale("pl", "PL")).format(birthdayDate.getTimeInMillis()));
        }
        else {
            dateSelectedTV.setText(getString(R.string.edit_profile_birthday_not_select_tv));
        }
    }

    public void showDatePickerDialog(View view) {
        DialogFragment newFragment = new EditProfileDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()){
            case R.id.registration_female:
                if(checked)
                    isFemale = true;
                break;
            case R.id.registration_male:
                if(checked)
                    isFemale = false;
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(birthdayDate != null){
            outState.putSerializable("BIRTHDAY_DATE", birthdayDate);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey("BIRTHDAY_DATE")){
            birthdayDate = (Calendar)savedInstanceState.getSerializable("BIRTHDAY_DATE");
            setUpDateSelectedTV();
        }
    }
}
