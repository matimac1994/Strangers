package com.strangersteam.strangers;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class ShowEventActivity extends AppCompatActivity implements OnMapReadyCallback{

    GoogleMap mMap;
    Marker mMarker;
    LatLng position;
    String eventTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.show_event_map_view);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        eventTitle = intent.getStringExtra("MARKER_TITLE");
        Bundle bundle = getIntent().getBundleExtra("MARKER_POSITION");
        position = bundle.getParcelable("LatLng");

        TextView textView = (TextView) findViewById(R.id.show_event_name_of_event);
        textView.setText(eventTitle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMarker = mMap.addMarker(new MarkerOptions().position(position)
                .title(eventTitle));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 15));
    }
}
