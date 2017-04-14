package com.strangersteam.strangers;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.strangersteam.strangers.adapters.AttendersListAdapter;
import com.strangersteam.strangers.model.EventMessage;
import com.strangersteam.strangers.model.StrangerUser;
import com.strangersteam.strangers.model.StrangersEvent;
import com.strangersteam.strangers.model.StrangersEventMarker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class ShowEventActivity extends AppCompatActivity implements
        OnMapReadyCallback{

    GoogleMap mMap;
    Marker mMarker;

    StrangersEvent event;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.show_event_map_view);
        mapFragment.getMapAsync(this);

        //----lista biorących udział
        RecyclerView attendersRecyclerView = (RecyclerView) findViewById(R.id.show_event_attenders_recycler_view);
        // nie wiem XD
        attendersRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, OrientationHelper.HORIZONTAL,false);
        attendersRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.Adapter attendersListAdapter = new AttendersListAdapter(Collections.EMPTY_LIST);
        attendersRecyclerView.setAdapter(attendersListAdapter);



        Long eventId = this.getIntent().getLongExtra("EVENT_ID",0);
        //teraz będzie trzeba puknąć na serwer po informacje o evencie o id eventId;
        //potem dostaniemy taki obiekt (teraz musimy go czyms wypelnic :D ):
        event= new StrangersEvent();
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
        attender1.setPhotoResId(R.drawable.dominik);
        attenders.add(attender1);
        StrangerUser attender3 = new StrangerUser();
        attender3.setId(2);
        attender3.setNick("Oldzi");
        attender3.setPhotoResId(R.drawable.olga);
        attenders.add(attender3);
        StrangerUser attender4 = new StrangerUser();
        attender4.setId(2);
        attender4.setNick("Linczi");
        attender4.setPhotoResId(R.drawable.linko);
        attenders.add(attender4);
        StrangerUser attender5 = new StrangerUser();
        attender5.setId(2);
        attender5.setNick("KordiXXX");
        attender5.setPhotoResId(R.drawable.kordix);
        attenders.add(attender5);
        StrangerUser attender6 = new StrangerUser();
        attender6.setId(2);
        attender6.setNick("Kapeć");
        attender6.setPhotoResId(R.drawable.kopcio);
        attenders.add(attender6);
        StrangerUser attender7 = new StrangerUser();
        attender7.setId(2);
        attender7.setNick("Macek");
        attender7.setPhotoResId(R.drawable.macek);
        attenders.add(attender7);
        StrangerUser attender8 = new StrangerUser();
        attender8.setId(2);
        attender8.setNick("Kasia");
        attender8.setPhotoResId(R.drawable.temp_logo_picture);
        attenders.add(attender8);
        StrangerUser attender2 = new StrangerUser();
        attender2.setId(2);
        attender2.setNick("Kaziu102");
        attender2.setPhotoResId(R.drawable.temp_logo_picture);
        attenders.add(attender2);
        event.setAttenders(attenders);

        //fejkowe wiadomosci, tez beda pobrane z serwera razem z eventem
        List<EventMessage> messages = new ArrayList<>();
        EventMessage message = new EventMessage();
        message.setUser(attender1);
        message.setContent("PEWNIE ŻE WPADNĘ! później klabbing??");
        messages.add(message);
        event.setMessages(messages);

        //teraz trzeba te dane z eventu ktory pobierzemy z internetu ustawic do poszczegółnych pól
        //todo przemyslec jak będa wyswietlani użytkownicy którzy dołączyli oraz wiadomosci eventu, to musza być jakieś listy czy coś
        //podstawowe info o evencie
        TextView titleTV = (TextView) findViewById(R.id.show_event_name_of_event_tv);
        titleTV.setText(event.getTitle());
        TextView whenTV = (TextView) findViewById(R.id.show_event_when_where_tv);
        whenTV.setText(new SimpleDateFormat("HH:mm EEEE, dd-MMM-yyyy", new Locale("pl","PL")).format(new Date(event.getDate().getTimeInMillis())));//pewnie da sie jakos ladniej xDD
        whenTV.append(", " + event.getWhere());
        TextView whereTV = (TextView) findViewById(R.id.show_event_details_tv);
        whereTV.setText(event.getDetails());

        //zalozyciel eventu
        TextView ownerNickTV = (TextView) findViewById(R.id.show_event_username);
        ownerNickTV.setText(event.getOwner().getNick());
        TextView ownerAgeTV = (TextView) findViewById(R.id.show_event_age);
        ownerAgeTV.setText(String.valueOf(event.getOwner().getAge()));//jak nie dalem toString to brało int jako id stringa z resourcesow zamist "22" xD
        TextView ownerSexTV = (TextView) findViewById(R.id.show_event_gender);
        ownerSexTV.setText(event.getOwner().isFemale()?"kobieta":"mezczyzna");//to taki skrócony zapis ifa warunek?jesli_prawda:jesli_falsz

        //lista attendersow
        RecyclerView.Adapter adapter = new AttendersListAdapter(ShowEventActivity.this, event.getAttenders());
        attendersRecyclerView.swapAdapter(adapter,false);
        attendersRecyclerView.setNestedScrollingEnabled(false);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //to bedzie trzeba robic dopiero po otrzymaniu eventu z serwera
        mMarker = mMap.addMarker(new MarkerOptions().position(event.getPosition())
                .title(event.getTitle()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 15));
    }
}
