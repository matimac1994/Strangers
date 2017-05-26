package com.strangersteam.strangers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.strangersteam.strangers.model.EventType;
import com.strangersteam.strangers.model.StrangersEventMarker;


import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

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
        GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLocation;

    private Marker mMarker;
    private Marker mLastSelectedMarker;

    private FloatingActionMenu menu;


    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buildGoogleApiClient();
        menu = (FloatingActionMenu) view.findViewById(R.id.add_event_menu);

        final FloatingActionButton addEventHereBtn = new FloatingActionButton(getActivity());
        addEventHereBtn.setButtonSize(FloatingActionButton.SIZE_MINI);
        addEventHereBtn.setLabelText("Dodej event tu i teraz!");
        addEventHereBtn.setImageResource(R.drawable.ic_my_location_white_24dp);
        menu.addMenuButton(addEventHereBtn);
        final FloatingActionButton addEventSomewhereBtn = new FloatingActionButton(getActivity());
        addEventSomewhereBtn.setButtonSize(FloatingActionButton.SIZE_MINI);
        addEventSomewhereBtn.setLabelText("Wybierz gdize chcesz utworzyć event!");
        addEventSomewhereBtn.setImageResource(R.drawable.ic_add_location_white_24dp);
        menu.addMenuButton(addEventSomewhereBtn);

        menu.setClosedOnTouchOutside(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_map_view);
        mapFragment.getMapAsync(this);
    }

    public void onStart(){
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);


        addMarkersToMap();

        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),13));

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
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

    @Override
    public void onMapLongClick(LatLng point) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        mMap.addMarker(markerOptions);
        this.mMarker.showInfoWindow();
    }
}
