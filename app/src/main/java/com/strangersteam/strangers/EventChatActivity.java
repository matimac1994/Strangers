package com.strangersteam.strangers;

import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.strangersteam.strangers.adapters.ChatListAdapter;
import com.strangersteam.strangers.model.StrangerEventMessage;
import com.strangersteam.strangers.model.StrangersEvent;
import com.strangersteam.strangers.model.StrangersEventListItem;
import com.strangersteam.strangers.serverConn.AuthJsonArrayRequest;
import com.strangersteam.strangers.serverConn.AuthJsonObjectRequest;
import com.strangersteam.strangers.serverConn.RequestQueueSingleton;
import com.strangersteam.strangers.serverConn.ServerConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventChatActivity extends AppCompatActivity {

    public static final String EVENT_ID = "EVENT_ID";

    private ListView mChatListView;
    private ChatListAdapter mChatListAdapter;

    private EditText messageET;
    private Button sendMessageBTN;
    private TextView emptyListView;

    private Long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_chat);

        setUpToolbar();
        initViews();

    }

    private void setUpToolbar(){
        final Toolbar toolbar = (Toolbar) findViewById(R.id.chat_event_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.chat_event_toolbar_title));
    }

    private void initViews() {
        setUpChatListView();

        messageET = (EditText) findViewById(R.id.chat_event_chat_text);
        sendMessageBTN = (Button) findViewById(R.id.chat_event_chat_button);
        emptyListView = (TextView) findViewById(R.id.chat_event_empty_tv_list_view);
        
        sendMessageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String msgContent = messageET.getText().toString();
        if(msgContent.trim().isEmpty() || eventId == null || eventId < 1){
            return;
        }else{
            sendMsgRequest(eventId,msgContent);
        }
    }

    private void setUpChatListView() {
        mChatListView = (ListView) findViewById(R.id.chat_event_chat_list_view);
        mChatListAdapter = new ChatListAdapter(this, new ArrayList<StrangerEventMessage>());
        mChatListView.setAdapter(mChatListAdapter);
        setupAutoScroll();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Long eventId = this.getIntent().getLongExtra(EVENT_ID,-1);
        if(eventId == -1){
            Toast.makeText(this,"Brak eventu do wyswietlenia, błąd", Toast.LENGTH_SHORT).show();
        }else{
            this.eventId= eventId;
            eventRequest(eventId);
        }
    }

    private void eventRequest(Long eventId) {
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

    private void sendMsgRequest(Long eventId, String content){
        String eventUrl = ServerConfig.sendMessage(eventId);

        HashMap<String, String> params = new HashMap<>();
        params.put("content", content);

        JsonArrayRequest jsonArrayRequest = new AuthJsonArrayRequest(
                getApplicationContext(),
                Request.Method.POST,
                eventUrl,
                params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            ObjectMapper mapper = new ObjectMapper();
                            CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, StrangerEventMessage.class);
                            List<StrangerEventMessage> events= null;
                            String jsonString = response.toString();

                            events = mapper.readValue(jsonString,listType);
                            fillChatListViewFromServer(events);
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

        RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);

    }

    private void fillEventData(StrangersEvent event) {
        getSupportActionBar().setTitle(getString(R.string.chat_event_toolbar_title) + " " + event.getTitle());
        fillChatListViewFromServer(event.getMessages());
    }


    private void fillChatListViewFromServer(List<StrangerEventMessage> eventMessages){
        ChatListAdapter listAdapter = new ChatListAdapter(this, eventMessages);
        mChatListView.setAdapter(listAdapter);
        setupAutoScroll();

        if(eventMessages.isEmpty()){
            mChatListView.setVisibility(View.GONE);
            emptyListView.setVisibility(View.VISIBLE);
        }
        else{
            mChatListView.setVisibility(View.VISIBLE);
            emptyListView.setVisibility(View.GONE);
        }
    }

    private void setupAutoScroll(){
        this.mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //mChatListView.setSelection(mChatListView.getCount() - 1);
                mChatListView.smoothScrollToPosition(mChatListAdapter.getCount()-1);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
