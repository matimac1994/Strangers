package com.strangersteam.strangers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity {

    @Bind (R.id.sign_email_edit_text) EditText _loginET;
    @Bind (R.id.sign_nick_edit_text) EditText nickET;
    @Bind (R.id.sign_password_edit_text) EditText _passET;
    @Bind (R.id.sign_password2_edit_text) EditText _pass2ET;
    @Bind (R.id.sign_sign_in_button) Button _signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        _signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignIn(view);
            }
        });
    }


    public void onClickSignIn(View view){

        if(!validate())
            return;

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private boolean validate(){

        boolean valid = true;

        String email = _loginET.getText().toString();
        String password = _passET.getText().toString();
        String password2 = _pass2ET.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginET.setError(getString(R.string.not_valid_mail));
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
        }



        return valid;
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }


}
