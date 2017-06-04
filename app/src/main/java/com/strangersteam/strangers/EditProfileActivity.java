package com.strangersteam.strangers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.strangersteam.strangers.serverConn.AuthMultipartFileRequest;
import com.strangersteam.strangers.serverConn.RequestQueueSingleton;
import com.strangersteam.strangers.serverConn.ServerConfig;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity implements EditProfileDatePickerFragment.OnCompleteListener {

    private static final int RESULT_LOAD_IMAGE = 1;

    private Uri imageUri;

    private Calendar birthdayDate;
    private String nick;
    private String phootoUrl;
    private boolean isFemale;

    private Button chooseImageBtn;
    private Button saveButton;

    private TextView nickTV;
    private TextView dateSelectedTV;

    private ImageView photoIV;

    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setUpToolbar();
        setUpButtons();
        initializeValuesAndShow();
    }

    private void setUpToolbar(){
        final Toolbar toolbar = (Toolbar) findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.edit_profile_title_toolbar));
    }

    private void setUpButtons(){
        radioGroup = (RadioGroup) findViewById(R.id.edit_profile_radio_group);
        chooseImageBtn = (Button) findViewById(R.id.edit_profile_choose_photo_button);
        chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });
        
        saveButton = (Button) findViewById(R.id.edit_profile_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAll();
            }
        });
    }

    private void saveAll() {
        byte[] data = getPhoto();

        AuthMultipartFileRequest request = new AuthMultipartFileRequest(
                getApplicationContext(),
                Request.Method.POST,
                ServerConfig.UPLOAD_PHOTO,
                "photo",
                data,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),"OK",Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage() ,Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private byte[] getPhoto() {
        Bitmap bitmap = ((BitmapDrawable)photoIV.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private void initializeValuesAndShow(){
        dateSelectedTV = (TextView) findViewById(R.id.edit_profile_birthday_tv);
        nickTV = (TextView) findViewById(R.id.edit_profile_username);
        photoIV = (ImageView) findViewById(R.id.edit_profile_photo_iv);
        Intent intent;
        intent = this.getIntent();
        if(intent.hasExtra("USER_NICK")){
            nick = intent.getStringExtra("USER_NICK");
            phootoUrl = intent.getStringExtra("USER_PHOTO");
            isFemale = intent.getBooleanExtra("USER_SEX",false);
            //birthdayDate = (Calendar)intent.getSerializableExtra("USER_BIRTHDAY");
        }
        setUpDateSelectedTV();
    }


    private void setUpDateSelectedTV(){
        if(birthdayDate != null){
            dateSelectedTV.setText(new SimpleDateFormat("dd.MM.yyyy", new Locale("pl", "PL")).format(birthdayDate.getTimeInMillis()));
        }
        else {
            dateSelectedTV.setText(getString(R.string.edit_profile_birthday_not_select_tv));
        }

        if(phootoUrl != null){
            initProfileImageView(phootoUrl);
        }

        if(nick != null){
            nickTV.setText(nick);
        }

    }

    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == RESULT_LOAD_IMAGE){
                imageUri = data.getData();
                initProfileImageView(imageUri);
            }
        }
    }

    private void initProfileImageView(Uri uri){
        if(uri != null){
            Picasso.with(EditProfileActivity.this)
                    .load(uri)
                    .noPlaceholder()
                    .centerCrop()
                    .fit()
                    .into(photoIV);
        }
    }

    private void initProfileImageView(String uri){
        if(uri != null){
            Picasso.with(EditProfileActivity.this)
                    .load(uri)
                    .noPlaceholder()
                    .centerCrop()
                    .fit()
                    .into((ImageView) findViewById(R.id.edit_profile_photo_iv));
        }
    }

    @Override
    public void onComplete(int year, int month, int day) {
        birthdayDate = new GregorianCalendar();
        birthdayDate.set(Calendar.YEAR, year);
        birthdayDate.set(Calendar.MONTH, month);
        birthdayDate.set(Calendar.DAY_OF_MONTH, day);
        setUpDateSelectedTV();
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


    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new EditProfileDatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(birthdayDate != null){
            outState.putSerializable("BIRTHDAY_DATE", birthdayDate);
        }

        if(imageUri != null){
            outState.putParcelable("IMAGE_URI", imageUri);
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

        if(savedInstanceState.containsKey("IMAGE_URI")){
            imageUri = (Uri)savedInstanceState.getParcelable("IMAGE_URI");
            initProfileImageView(imageUri);
        }
    }
}
