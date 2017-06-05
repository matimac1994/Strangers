package com.strangersteam.strangers;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddEventMarkerActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnInfoWindowLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener{

    private GoogleMap mMap;
    private LatLng cameraLatLng;

    private Marker mLastSelectedMarker;
    private Marker mLastAddedMarker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_event_marker);
        setUpToolbar();

        Intent intent = this.getIntent();
        cameraLatLng = (LatLng) intent.getExtras().getParcelable("CAMERA_POSITION");



        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.add_event_marker_map);
        mapFragment.getMapAsync(this);
    }

    private void setUpToolbar(){
        final Toolbar toolbar = (Toolbar) findViewById(R.id.add_event_marker_toolbar);
        setSupportActionBar(toolbar);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        if(cameraLatLng != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraLatLng, 14));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this, AddEventActivity.class);
        intent.putExtra("MARKER_POSITION", marker.getPosition());
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng point) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.title(getString(R.string.add_event_marker_title_marker));
        Marker marker = mMap.addMarker(markerOptions);
        if(mLastAddedMarker != null){
            mLastAddedMarker.remove();
        }
        mLastAddedMarker = marker;
        mLastAddedMarker.showInfoWindow();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mLastSelectedMarker = marker;
        mLastSelectedMarker.showInfoWindow();
        return false;
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
}
