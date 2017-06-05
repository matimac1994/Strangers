package com.strangersteam.strangers;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

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
            goToMap();
        }
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
            case R.id.edit_profile_female:
                if(checked)
                    isFemale = true;
                break;
            case R.id.edit_profile_male:
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
