package com.strangersteam.strangers;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;
import com.strangersteam.strangers.model.StrangerUser;
import com.strangersteam.strangers.serverConn.AuthJsonObjectRequest;
import com.strangersteam.strangers.serverConn.RequestQueueSingleton;
import com.strangersteam.strangers.serverConn.ServerConfig;

import org.json.JSONObject;

import java.io.IOException;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;
import com.strangersteam.strangers.model.StrangerUser;
import com.strangersteam.strangers.serverConn.RequestQueueSingleton;
import com.strangersteam.strangers.serverConn.ServerConfig;

import org.json.JSONObject;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment{

    Button editProfileBtn;
    ImageView ownerPhotoIV;
    TextView ownerNickTV;
    TextView ownerAgeTV;
    TextView ownerSexTV;

    StrangerUser user;
    String femaleString;
    String maleString;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        femaleString = getString(R.string.female);
        maleString = getString(R.string.male);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        editProfileBtn = (Button) view.findViewById(R.id.my_profile_edit_profile_button) ;
        ownerPhotoIV = (ImageView) view.findViewById(R.id.my_profile_picture);
        ownerNickTV = (TextView) view.findViewById(R.id.my_profile_username);
        ownerAgeTV = (TextView) view.findViewById(R.id.my_profile_age);
        ownerSexTV = (TextView) view.findViewById(R.id.my_profile_gender);

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                if(user != null){
                    intent.putExtra("USER_NICK", user.getNick());
                    intent.putExtra("USER_PHOTO", user.getPhotoUrl());
                    //intent.putExtra("USER_BIRTHDAY", user.getBirthday());
                    intent.putExtra("USER_SEX", user.isFemale());
                }

                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        myUserRequest();
    }

    private void myUserRequest() {

        String myUserUrl = ServerConfig.MY_USER;

        JsonObjectRequest jsonObjectRequest = new AuthJsonObjectRequest(
                getActivity().getApplicationContext(),
                Request.Method.GET,
                myUserUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            ObjectMapper objectMapper = new ObjectMapper();
                            user = objectMapper.readValue(response.toString(),StrangerUser.class);
                            fillUserData(user);
                        }catch (IOException e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(),"error response: " + error.getMessage(),Toast.LENGTH_LONG).show();
                        Log.e(getClass().getName(), error.getMessage()!=null?error.getMessage():"null");
                    }
                }
        );
        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);

    }

    private void fillUserData(StrangerUser user) {
        if(user.getPhotoUrl() == null || user.getPhotoUrl().isEmpty()){
            Picasso.with(getActivity()).load(R.drawable.temp_logo_picture)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.temp_logo_picture)
                    .into(ownerPhotoIV);
        }else{
            Picasso.with(getActivity()).load(user.getPhotoUrl())
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.temp_logo_picture)
                    .into(ownerPhotoIV);
        }

        ownerNickTV.setText(user.getNick());
        ownerAgeTV.setText(String.valueOf(user.getAge()));
        ownerSexTV.setText(user.isFemale()?femaleString:maleString);
    }
}
