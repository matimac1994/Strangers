package com.strangersteam.strangers;

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
import com.google.android.gms.maps.model.MarkerOptions;

public class GMapFragment extends Fragment implements OnMapReadyCallback{

    private GoogleMap mMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng politechnikaKrakowska = new LatLng(50.071665, 19.942853);
        mMap.addMarker(new MarkerOptions().position(politechnikaKrakowska).title("Politechnika Krakowska"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(politechnikaKrakowska));

    }
}
