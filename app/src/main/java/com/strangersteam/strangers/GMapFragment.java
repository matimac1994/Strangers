package com.strangersteam.strangers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
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
        GoogleMap.OnCameraIdleListener {

    private GoogleMap mMap;
    private Location mLocation;

    private List<Marker> markersOnMap;
    private Marker mLastSelectedMarker;

    private boolean isCameraMoveIntoMarker;

    private FloatingActionMenu _floatingActionMenu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markersOnMap = new ArrayList<>();
        isCameraMoveIntoMarker = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.add_event_menu);
        configFloatingActionMenu();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_map_view);
        mapFragment.getMapAsync(this);
    }

    private void configFloatingActionMenu() {
        final FloatingActionButton addEventHereBtn = new FloatingActionButton(getActivity());
        addEventHereBtn.setButtonSize(FloatingActionButton.SIZE_MINI);
        addEventHereBtn.setLabelText("Dodej event tu i teraz!");
        addEventHereBtn.setImageResource(R.drawable.ic_my_location_white_24dp);
        addEventHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddEventActivity.class);
                startActivity(intent);
            }
        });
        _floatingActionMenu.addMenuButton(addEventHereBtn);
        final FloatingActionButton addEventSomewhereBtn = new FloatingActionButton(getActivity());
        addEventSomewhereBtn.setButtonSize(FloatingActionButton.SIZE_MINI);
        addEventSomewhereBtn.setLabelText("Wybierz gdzie chcesz utworzyć event!");
        addEventSomewhereBtn.setImageResource(R.drawable.ic_add_location_white_24dp);
        addEventSomewhereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddEventMarkerActivity.class);
                startActivity(intent);
            }
        });
        _floatingActionMenu.addMenuButton(addEventSomewhereBtn);

        _floatingActionMenu.setClosedOnTouchOutside(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        mMap.setOnCameraIdleListener(this);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        getCurrentLocation();
        downloadMarkersAndAddToMap();
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
    public void onMapClick(LatLng latLng) {

    }

    private void getCurrentLocation() {
        final LocationManager mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location != null){
                    mLocation = location;
                    mLocationManager.removeUpdates(this);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(mLocation != null){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
                    .zoom(13)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        else{
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.867, 151.206), 13));
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

    }

    @Override
    public void onCameraIdle() {
        if(isCameraMoveIntoMarker){
            isCameraMoveIntoMarker = !isCameraMoveIntoMarker;
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
                            List<StrangersEventMarker> downloadedMarkers = null;
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
        isCameraMoveIntoMarker = true;
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
