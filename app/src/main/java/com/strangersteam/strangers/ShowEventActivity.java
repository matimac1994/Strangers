package com.strangersteam.strangers;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.strangersteam.strangers.adapters.AttendersListAdapter;
import com.strangersteam.strangers.model.EventType;
import com.strangersteam.strangers.model.StrangerUser;
import com.strangersteam.strangers.model.StrangersEvent;
import com.strangersteam.strangers.model.UserEventRelation;
import com.strangersteam.strangers.serverConn.AuthJsonObjectRequest;
import com.strangersteam.strangers.serverConn.AuthStringRequest;
import com.strangersteam.strangers.serverConn.RequestQueueSingleton;
import com.strangersteam.strangers.serverConn.ServerConfig;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;


public class ShowEventActivity extends AppCompatActivity implements
        OnMapReadyCallback{

    public static final String EVENT_ID = "EVENT_ID";
    GoogleMap mMap;
    Marker mMarker;

    StrangersEvent mEvent;

    TextView titleTV;
    TextView whenTV;
    TextView whereTV;
    ImageView ownerPhotoIV;
    TextView ownerNickTV;
    TextView ownerAgeTV;
    TextView ownerSexTV;
    TextView emptyRecyclerView;
    TextView chatTitle;
    Button switchToChat;

    Button eventActionButton;

    RecyclerView attendersRecyclerView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_show_event);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initViews();

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.show_event_map_view);
        mapFragment.getMapAsync(this);

    }

    private void initViews(){

        setUpAttendersRecyclerView();

        titleTV = (TextView) findViewById(R.id.show_event_name_of_event_tv);
        whenTV = (TextView) findViewById(R.id.show_event_when_where_tv);
        whereTV = (TextView) findViewById(R.id.show_event_details_tv);
        ownerPhotoIV = (ImageView) findViewById(R.id.show_event_profile_picture);
        ownerNickTV = (TextView) findViewById(R.id.show_event_username);
        ownerAgeTV = (TextView) findViewById(R.id.show_event_age);
        ownerSexTV = (TextView) findViewById(R.id.show_event_gender);
        eventActionButton = (Button) findViewById(R.id.show_event_action_button);
        emptyRecyclerView = (TextView) findViewById(R.id.show_event_empty_tv_recycler_view);
        chatTitle = (TextView) findViewById(R.id.show_event_chat_title);
        switchToChat = (Button) findViewById(R.id.show_event_switch_to_chat);
        switchToChat.setVisibility(View.GONE);
        switchToChat.setClickable(false);

    }

    private void setUpAttendersRecyclerView(){
        attendersRecyclerView = (RecyclerView) findViewById(R.id.show_event_attenders_recycler_view);
        attendersRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, OrientationHelper.HORIZONTAL,false);
        attendersRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.Adapter attendersListAdapter = new AttendersListAdapter(Collections.EMPTY_LIST);
        attendersRecyclerView.setAdapter(attendersListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Long eventId = this.getIntent().getLongExtra(EVENT_ID,0);
        getEventRequest(eventId);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
    }

    private void getEventRequest(Long eventId) {
        String eventUrl = ServerConfig.eventById(eventId);
        JsonObjectRequest jsonObjectRequest = new AuthJsonObjectRequest(
                getApplicationContext(),
                Request.Method.GET,
                eventUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            StrangersEvent event = new ObjectMapper().readValue(response.toString(),StrangersEvent.class);
                            fillEventData(event);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(),error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void fillEventData(StrangersEvent event) {
        fillBaseInfo(event);
        fillOwner(event);
        fillRecyclerViewFromServer(event);
        fillChat(event);
        fillMiniMap(event);
        fillActionButton(event);
        this.mEvent = event;
    }

    private void fillBaseInfo(StrangersEvent event) {
        titleTV.setText(event.getTitle());
        whenTV.setText(new SimpleDateFormat("HH:mm EEEE, dd-MMM-yyyy", new Locale("pl","PL")).format(new Date(event.getDate().getTimeInMillis())));//pewnie da sie jakos ladniej xDD
        whenTV.append(", " + event.getWhere());
        whereTV.setText(event.getDetails());
    }

    private void fillOwner(StrangersEvent event) {
        StrangerUser user = event.getOwner();
        if(user.getPhotoUrl() == null || user.getPhotoUrl().isEmpty()){
            Picasso.with(this).load(R.drawable.temp_logo_picture)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.temp_logo_picture)
                    .into(ownerPhotoIV);
        }else{
            Picasso.with(this).load(user.getPhotoUrl())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.temp_logo_picture)
                    .into(ownerPhotoIV);
        }
        ownerNickTV.setText(user.getNick());
        ownerAgeTV.setText(String.valueOf(user.getAge()));//jak nie dalem toString to brało int jako id stringa z resourcesow zamist "22" xD
        ownerSexTV.setText(user.isFemale()?getString(R.string.female):getString(R.string.male));
    }

    private void fillRecyclerViewFromServer(StrangersEvent event){
        RecyclerView.Adapter adapter = new AttendersListAdapter(ShowEventActivity.this, event.getAttenders());
        attendersRecyclerView.swapAdapter(adapter,false);
        attendersRecyclerView.setNestedScrollingEnabled(false);
        if(event.getAttenders().isEmpty()){
            attendersRecyclerView.setVisibility(View.GONE);
            emptyRecyclerView.setVisibility(View.VISIBLE);
        }
        else{
            attendersRecyclerView.setVisibility(View.VISIBLE);
            emptyRecyclerView.setVisibility(View.GONE);
        }
    }

    private void fillChat(StrangersEvent event) {
        if(event.getUsEvRelation()==UserEventRelation.STRANGER){
            chatTitle.setText("");
            switchToChat.setVisibility(View.GONE);
            switchToChat.setClickable(false);
        }else{
            if(event.getMessages().size() != 1){
                chatTitle.setText(getString(R.string.show_event_chat_title) + "(" + event.getMessages().size() + " " + getString(R.string.messages) + ")");
            }else{
                chatTitle.setText(getString(R.string.show_event_chat_title) + "(" + event.getMessages().size() + " " + getString(R.string.message) + ")");
            }

            switchToChat.setVisibility(View.VISIBLE);
            switchToChat.setClickable(true);
        }


    }

    private void fillMiniMap(StrangersEvent event) {
        mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(event.getPosition().getLatitude(), event.getPosition().getLongitude()))
                .title(event.getTitle()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 15));
    }

    private void fillActionButton(StrangersEvent event) {
        if(event.getType() != EventType.HISTORIC){
            eventActionButton.setText(getActionButtonText(event.getUsEvRelation()));
            eventActionButton.setOnClickListener(chooseActionButtonListener(event.getUsEvRelation(),event.getId()));
        }
    }

    private View.OnClickListener chooseActionButtonListener(UserEventRelation usEvRelation, Long eventId) {
        switch (usEvRelation){
            case OWNER:
                return endEventClickListener(eventId);
            case ATTENDER:
                return cancelEventClickListener(eventId);
            case STRANGER:
                return joinEventClickListener(eventId);
            default:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // nic ma nie robić XDD   zwracam takie zamiast nulla żeby nie sypało błędami
                    }
                };
        }
    }

    private String getActionButtonText(UserEventRelation usEvRelation) {
        switch (usEvRelation){
            case OWNER:
                return getString(R.string.show_event_action_button_end_event);
            case ATTENDER:
                return getString(R.string.show_event_action_button_quit_event);
            case STRANGER:
                return getString(R.string.show_event_action_button_join_event);
            default:
                return "nic";
        }
    }



    private View.OnClickListener joinEventClickListener(final Long eventId) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinEventRequest(eventId);
            }
        };
    }



    private View.OnClickListener cancelEventClickListener(final Long eventId) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelEventRequest(eventId);
            }
        };
    }

    private View.OnClickListener endEventClickListener(final Long eventId) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endEventRequest();
            }
        };
    }

    public void onClickChat(View view) {
        Intent intent = new Intent(this, EventChatActivity.class);
        intent.putExtra(EventChatActivity.EVENT_ID, mEvent.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void joinEventRequest(final Long eventId) {
        String joinEventUrl = ServerConfig.joinEvent(eventId);

        StringRequest stringRequest = new AuthStringRequest(
                getApplicationContext(),
                Request.Method.POST,
                joinEventUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), R.string.show_event_joined_to_event, Toast.LENGTH_SHORT).show();
                        getEventRequest(eventId);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
    public void onClickProfilePhoto(View view) {
        Intent intent = new Intent(this, UserEventsActivity.class);
        intent.putExtra("USER_NICK", mEvent.getOwner().getNick());
        intent.putExtra(EventChatActivity.EVENT_ID, mEvent.getId());
        startActivity(intent);
        finish();
    }

    private void cancelEventRequest(final Long eventId) {
        String joinEventUrl = ServerConfig.cancelEvent(eventId);

        StringRequest stringRequest = new AuthStringRequest(
                getApplicationContext(),
                Request.Method.POST,
                joinEventUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), R.string.show_event_canceled_event, Toast.LENGTH_SHORT).show();
                        getEventRequest(eventId);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "error: " + error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
        );

        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void endEventRequest() {

        Toast.makeText(this,"Not implemented jeszcze heh", Toast.LENGTH_SHORT).show();
    }

    public void onClickProfilePhoto(View view) {
        Intent intent = new Intent(this, UserEventsActivity.class);
        intent.putExtra("USER_NICK", mEvent.getOwner().getNick());
        intent.putExtra(EventChatActivity.EVENT_ID, mEvent.getId());
        startActivity(intent);
        finish();
    }

    public void onClickChat(View view) {
        Intent intent = new Intent(this, EventChatActivity.class);
        intent.putExtra(EventChatActivity.EVENT_ID, mEvent.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

}
