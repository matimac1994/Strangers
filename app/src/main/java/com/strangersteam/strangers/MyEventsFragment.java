package com.strangersteam.strangers;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.strangersteam.strangers.adapters.MyEventsListAdapter;
import com.strangersteam.strangers.model.EventMessage;
import com.strangersteam.strangers.model.StrangerUser;
import com.strangersteam.strangers.model.StrangersEvent;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyEventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyEventsListAdapter myEventsListAdapter;
    private StrangersEvent event;
    private List<StrangersEvent> strangersEventList = new ArrayList<>();


    public MyEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_events, container, false);
        //Get the reference to recyclerView
        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_events_recycler_view);
        //Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Long eventId = Long.valueOf(0);
        //teraz będzie trzeba puknąć na serwer po informacje o evencie o id eventId;
        //potem dostaniemy taki obiekt (teraz musimy go czyms wypelnic :D ):
        event = new StrangersEvent();
        event.setPosition(new LatLng(50.064375, 19.939535));
        event.setTitle("piwko w pijalni");
        event.setWhere("Pijalnia");
        event.setDetails("Wieczorem na piwko ?? :>> mam wolny wieczór pomyślałem że warto byłoby poznac nowych ludzi, zapraszam! :D");
        event.setDate(new GregorianCalendar(2017, 3,11,20,30));

        //zalozyciel eventa
        StrangerUser eventOwner = new StrangerUser();
        eventOwner.setId(3);
        eventOwner.setNick("Mateusz69");
        eventOwner.setAge(52);
        eventOwner.setFemale(false);
        event.setOwner(eventOwner);

        //fejkowi uzytkownicy, beda pobrani razem z eventem z serwera
        List<StrangerUser> attenders = new ArrayList<>();
        StrangerUser attender1 = new StrangerUser();
        attender1.setId(1);
        attender1.setNick("Dominik");
        attenders.add(attender1);
        StrangerUser attender2 = new StrangerUser();
        attender2.setId(2);
        attender2.setNick("Kaziu102");
        attenders.add(attender2);
        event.setAttenders(attenders);

        //fejkowe wiadomosci, tez beda pobrane z serwera razem z eventem
        List<EventMessage> messages = new ArrayList<>();
        EventMessage message = new EventMessage();
        message.setUser(attender1);
        message.setContent("PEWNIE ŻE WPADNĘ! później klabbing??");
        messages.add(message);
        event.setMessages(messages);

        strangersEventList.add(event);

        RecyclerView.Adapter adapter = new MyEventsListAdapter(strangersEventList);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }

}
