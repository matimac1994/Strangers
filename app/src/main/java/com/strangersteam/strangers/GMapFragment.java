package com.strangersteam.strangers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
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
import com.android.volley.toolbox.JsonRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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
import com.strangersteam.strangers.notifications.FewEventsMsgContent;
import com.strangersteam.strangers.notifications.OneMsgNotificationContent;
import com.strangersteam.strangers.notifications.NotificationService;
import com.strangersteam.strangers.notifications.NotificationType;
import com.strangersteam.strangers.notifications.StrangerNotification;
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
        GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnCameraIdleListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    private List<Marker> markersOnMap;
    private Marker mLastSelectedMarker;

    private boolean isCameraMoveIntoMarker;

    private FloatingActionMenu _floatingActionMenu;
    private double latitudeDefault = 50.07222148;
    private double longitudeDefault = 19.9410975;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        markersOnMap = new ArrayList<>();
        isCameraMoveIntoMarker = false;

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
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
        addEventHereBtn.setLabelText(getString(R.string.map_fragment_here_button));
        addEventHereBtn.setImageResource(R.drawable.ic_my_location_white_24dp);
        addEventHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddEventActivity.class);
                if (mLocation != null) {
                    intent.putExtra("MARKER_POSITION", new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
                } else {
                    intent.putExtra("MARKER_POSITION", new LatLng(latitudeDefault, longitudeDefault));
                }

                startActivity(intent);

            }
        });
        _floatingActionMenu.addMenuButton(addEventHereBtn);
        final FloatingActionButton addEventSomewhereBtn = new FloatingActionButton(getActivity());
        addEventSomewhereBtn.setButtonSize(FloatingActionButton.SIZE_MINI);
        addEventSomewhereBtn.setLabelText(getString(R.string.map_fragment_somewhere_button));
        addEventSomewhereBtn.setImageResource(R.drawable.ic_add_location_white_24dp);
        addEventSomewhereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddEventMarkerActivity.class);
                if (mLocation != null) {
                    intent.putExtra("CAMERA_POSITION", new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
                } else {
                    intent.putExtra("CAMERA_POSITION", new LatLng(latitudeDefault, longitudeDefault));
                }
                startActivity(intent);
            }
        });
        _floatingActionMenu.addMenuButton(addEventSomewhereBtn);

        _floatingActionMenu.setClosedOnTouchOutside(true);
    }

    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        mMap.setOnCameraIdleListener(this);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        downloadMarkersAndAddToMap();
        mMap.getUiSettings().setScrollGesturesEnabled(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            goToLocation(latitudeDefault, longitudeDefault);//nie wiem ale tak se tutaj wkleje hehehehe
            return;
        }

        mLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if(mLocation != null){
            goToLocation(mLocation.getLatitude(), mLocation.getLongitude());
        }else{
            goToLocation(latitudeDefault, longitudeDefault);
        }
    }

    private void goToLocation(double latitude, double longitude) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(13)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onConnectionSuspended(int i) {

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

        AuthJsonArrayRequest jsonArrayRequest = new AuthJsonArrayRequest(
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
        Intent intent = new Intent(getContext(), ShowEventActivity.class);
        intent.putExtra("EVENT_ID", (Long)marker.getTag());
        startActivity(intent);
    }


    @Override
    public void onInfoWindowLongClick(Marker marker) {

        NotificationService notificationService = new NotificationService();
        StrangerNotification strangerNotification = new StrangerNotification();
        strangerNotification.setNotificationType(NotificationType.FEW_EVENTS_MSG);

        FewEventsMsgContent content = new FewEventsMsgContent();
        content.setTitle("Nowe wiadomości");
        String[] temp = {"Wyjscie na piwo","łyżwy"};
        content.setEvents(temp);

        strangerNotification.setNotificationContent(content);
        notificationService.notify(getContext(), strangerNotification);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(),connectionResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
    }
}
