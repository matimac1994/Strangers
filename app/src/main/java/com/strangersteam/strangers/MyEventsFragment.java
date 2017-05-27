package com.strangersteam.strangers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.android.gms.maps.model.LatLng;
import com.strangersteam.strangers.adapters.MyEventsListAdapter;
import com.strangersteam.strangers.model.EventMessage;
import com.strangersteam.strangers.model.LatLngHolder;
import com.strangersteam.strangers.model.StrangerUser;
import com.strangersteam.strangers.model.StrangersEvent;
import com.strangersteam.strangers.model.StrangersEventListItem;
import com.strangersteam.strangers.model.StrangersEventMarker;
import com.strangersteam.strangers.serverConn.RequestQueueSingleton;
import com.strangersteam.strangers.serverConn.ServerConfig;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyEventsFragment extends Fragment {

    private RecyclerView recyclerView;

    public MyEventsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_my_events, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_events_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        initList(Collections.EMPTY_LIST);
        return rootView;
    }

    private void initList(List<StrangersEventListItem> strangersEventList) {
        RecyclerView.Adapter adapter = new MyEventsListAdapter(strangersEventList);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onStart() {
        super.onStart();

        myEventsRequest();

    }

    private void myEventsRequest() {

        String myEventsUrl = ServerConfig.MY_EVENTS;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
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

                    }
                }
        );

        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }
}
