package com.strangersteam.strangers;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.strangersteam.strangers.model.EventType;
import com.strangersteam.strangers.model.StrangersEventMarker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class GMapFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnInfoWindowLongClickListener {


    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    private GoogleMap mMap;

    private Marker mMarker;
    private Marker mLastSelectedMarker;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map_map_view);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        addMarkersToMap();

        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowLongClickListener(this);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 13));

    }

    private void addMarkersToMap(){

        List<StrangersEventMarker> mockEvents = new ArrayList<>();

        StrangersEventMarker strangersEventMarker1 = new StrangersEventMarker();
        strangersEventMarker1.setPosition(new LatLng(50.071665, 19.942853));//pk
        strangersEventMarker1.setTitle("Spotkanie zespołu Strangers");
        strangersEventMarker1.setDetails("Stwórz z nami społeczność!");
        strangersEventMarker1.setDate(new GregorianCalendar(2017, 3,11,7,30));
        strangersEventMarker1.setType(EventType.NOW);
        mockEvents.add(strangersEventMarker1);

        StrangersEventMarker strangersEventMarker2 = new StrangersEventMarker();
        strangersEventMarker2.setPosition(new LatLng(50.067685, 19.946097));//galeria
        strangersEventMarker2.setTitle("SZOPING");
        strangersEventMarker2.setDetails("zapraszam do galerii na małe zakupy albo kawe i ciastko :D");
        strangersEventMarker2.setDate(new GregorianCalendar(2017, 3,11,7,30));
        strangersEventMarker2.setType(EventType.NOW);
        mockEvents.add(strangersEventMarker2);


        StrangersEventMarker strangersEventMarker3 = new StrangersEventMarker();
        strangersEventMarker3.setPosition(new LatLng(50.064375, 19.939535));//rynek
        strangersEventMarker3.setTitle("piwko w pijalni");
        strangersEventMarker3.setDetails("Wieczorem na piwko ?? :>>");
        strangersEventMarker3.setDate(new GregorianCalendar(2017, 3,11,20,30));
        strangersEventMarker3.setType(EventType.FUTURE);
        mockEvents.add(strangersEventMarker3);


        for(StrangersEventMarker event: mockEvents){
            mMarker = mMap.addMarker(generateMarkerOpt(event));
            mMarker.setTag(event.getId());
        }

    }

    private MarkerOptions generateMarkerOpt(StrangersEventMarker event){
        MarkerOptions mopt = new MarkerOptions();
        mopt.position(event.getPosition());
        mopt.title(event.getTitle());
        mopt.draggable(false);

        if(event.getType() == EventType.FUTURE){
            mopt.alpha(0.6f);//trzeba zrobic cos zeby rozrozniac eventy pod wzgledem wygladu
            mopt.snippet(new SimpleDateFormat("HH:mm EEEE, dd-MMM", new Locale("pl","PL")).format(new Date(event.getDate().getTimeInMillis())));
        }else{
            mopt.snippet("teraz!");
            mopt.alpha(1.0f);
        }

        mopt.flat(true);

        return mopt;
    }



    @Override
    public boolean onMarkerClick(final Marker marker) {

        mLastSelectedMarker = marker;
        mLastSelectedMarker.showInfoWindow();
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
//przekażemy tam tylko id eventa, reszte będzie trzeba pobrać w tamtej aktywnosci
        Intent intent = new Intent(getContext(), ShowEventActivity.class);
        intent.putExtra("EVENT_ID", (Long)marker.getTag());
        startActivity(intent);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {

    }
}
