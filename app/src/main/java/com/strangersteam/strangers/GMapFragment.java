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
        LatLng politechnikaKrakowska = new LatLng(50.071665, 19.942853);
        mMarker = mMap.addMarker(new MarkerOptions().position(politechnikaKrakowska)
                .title("Spotkanie zespołu Politechnika Krakowska"));
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        mLastSelectedMarker = marker;
        mLastSelectedMarker.showInfoWindow();
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        Intent intent = new Intent(getContext(), ShowEventActivity.class);
        Bundle args = new Bundle();
        args.putParcelable("LatLng", marker.getPosition());
        intent.putExtra("MARKER_POSITION", args);
        intent.putExtra("MARKER_TITLE", marker.getTitle());
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
