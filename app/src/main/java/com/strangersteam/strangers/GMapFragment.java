package com.strangersteam.strangers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.strangersteam.strangers.model.EventType;
import com.strangersteam.strangers.model.StrangersEventMarker;


import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.strangersteam.strangers.serverConn.AuthJsonArrayRequest;
import com.strangersteam.strangers.serverConn.RequestQueueSingleton;
import com.strangersteam.strangers.serverConn.ServerConfig;

import org.json.JSONArray;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        LocationListener, GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLocation;

    private List<Marker> markersOnMap;
    private Marker mLastSelectedMarker;

    private FloatingActionMenu menu;

    private boolean isCameraMovingtoMarker;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        markersOnMap = new ArrayList<>();
        isCameraMovingtoMarker = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


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
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        mMap.setOnCameraIdleListener(this);

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //wydaje mi się że trzeba to tutaj wywołać, bo to 'wymusi' psrawdzenie gpsa
            //ale to tylko moje zdanie XD
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),13));
        downloadMarkersAndAddToMap();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(15000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

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
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onCameraIdle() {
        if(isCameraMovingtoMarker){
            isCameraMovingtoMarker = !isCameraMovingtoMarker;
            return;
        }

        mMap.getUiSettings().setScrollGesturesEnabled(false);
        Toast.makeText(getActivity(),"Pobieram se dane, nie ruszaj ", Toast.LENGTH_SHORT).show();
        downloadMarkersAndAddToMap();
    }

    private void downloadMarkersAndAddToMap() {
        LatLngBounds mapBound = mMap.getProjection().getVisibleRegion().latLngBounds;
        downloadMarkersRequest(mapBound);
    }

    private void downloadMarkersRequest(LatLngBounds mapBound) {

        String markersUrl = ServerConfig.markersOnMapByBounds(mapBound);

        JsonArrayRequest jsonArrayRequest = new AuthJsonArrayRequest(
                getActivity().getApplicationContext(),
                Request.Method.GET,
                markersUrl,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, StrangersEventMarker.class);
                            List<StrangersEventMarker> downloadedMarkers= null;
                            String jsonString = response.toString();

                            downloadedMarkers = mapper.readValue(jsonString,listType);
                            refreshMarkersOnMap(downloadedMarkers);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),"Parsing markers error", Toast.LENGTH_LONG).show();
                        } finally {
                            mMap.getUiSettings().setScrollGesturesEnabled(true);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //todo co teraz??
                        mMap.getUiSettings().setScrollGesturesEnabled(true);
                        System.out.println(error.getMessage());
                        Toast.makeText(getActivity(), "onErrorResponse: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueueSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    private void refreshMarkersOnMap(List<StrangersEventMarker> downloadedMarkers) {
        removeOldMarkers();
        addMarkersToMap(downloadedMarkers);
    }

    private void removeOldMarkers() {
        for(Marker marker : markersOnMap){
            marker.remove();
        }
    }

    private void addMarkersToMap(List<StrangersEventMarker> mockEvents){

        for(StrangersEventMarker event: mockEvents){
            System.out.println(event.getTitle());
            Marker marker = mMap.addMarker(generateMarkerOpt(event));
            marker.setTag(event.getId());
            markersOnMap.add(marker);
        }

    }

    private MarkerOptions generateMarkerOpt(StrangersEventMarker event){
        MarkerOptions mopt = new MarkerOptions();
        mopt.position(new LatLng(event.getPosition().getLatitude(),event.getPosition().getLongitude()));
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
        isCameraMovingtoMarker = true;
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        System.out.println("CLIK");
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
        Marker marker = mMap.addMarker(markerOptions);
        marker.showInfoWindow();
    }
}
