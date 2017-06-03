package com.strangersteam.strangers;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.strangersteam.strangers.adapters.MyEventsListAdapter;
import com.strangersteam.strangers.adapters.UserEventsListAdapter;
import com.strangersteam.strangers.model.StrangersEventListItem;
import com.strangersteam.strangers.serverConn.AuthJsonArrayRequest;
import com.strangersteam.strangers.serverConn.RequestQueueSingleton;
import com.strangersteam.strangers.serverConn.ServerConfig;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserEventsActivity extends AppCompatActivity {

    public static final String EVENT_ID = "EVENT_ID";

    StrangersEventListItem event;

    private RecyclerView recyclerView;
    private String ownerNick;


    private Long eventId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_events);

        Intent intent = this.getIntent();
        ownerNick = intent.getStringExtra("USER_NICK");
        setUpToolbar();



        recyclerView = (RecyclerView) findViewById(R.id.user_events_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initList(Collections.EMPTY_LIST);
    }



    private void initList(List<StrangersEventListItem> strangersEventList) {
        RecyclerView.Adapter adapter = new UserEventsListAdapter(this, strangersEventList);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    private void setUpToolbar(){
        final Toolbar toolbar = (Toolbar) findViewById(R.id.user_events_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.user_events_toolbar_title) + " " + ownerNick);
    }

    @Override
    public void onStart() {
        super.onStart();
        Long eventId = this.getIntent().getLongExtra(EVENT_ID,-1);
        if(eventId == -1){
            Toast.makeText(this,"Brak eventu do wyswietlenia, błąd", Toast.LENGTH_SHORT).show();
        }else{
            this.eventId= eventId;
        }
        myEventsRequest();

    }

    private void myEventsRequest() {

        String myEventsUrl = ServerConfig.MY_EVENTS;

        JsonArrayRequest jsonArrayRequest = new AuthJsonArrayRequest(
                this.getApplicationContext(),
                Request.Method.GET,
                myEventsUrl,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            ObjectMapper mapper = new ObjectMapper();
                            CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, StrangersEventListItem.class);
                            List<StrangersEventListItem> events= null;
                            String jsonString = response.toString();

                            events = mapper.readValue(jsonString,listType);
                            initList(events);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplication(),error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
        );
        RequestQueueSingleton.getInstance(getApplication()).addToRequestQueue(jsonArrayRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ShowEventActivity.class);
        intent.putExtra(ShowEventActivity.EVENT_ID, eventId);
        startActivity(intent);
        finish();
    }
//
//    public void onEventCardView(View view) {
//        Intent intent = new Intent(this, ShowEventActivity.class);
//        Integer taggedPosition = (Integer) view.getTag();
//        intent.putExtra(ShowEventActivity.EVENT_ID, get);
//        startActivity(intent);
//        this.finish();
//        // TODO: 03.06.2017 Przechodzenie do wybranego eventu zamknięcie pozostałych tak żeby nie można było się zapętlić XD
//    }
}
