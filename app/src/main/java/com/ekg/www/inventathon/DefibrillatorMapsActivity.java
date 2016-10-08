package com.ekg.www.inventathon;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.android.gms.analytics.internal.zzy.l;

public class DefibrillatorMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    private List<LatLng> getLocations() {
        List<LatLng> locations = new ArrayList<>();
        locations.add(new LatLng(-34, 151));
        return locations;
    }

    private final List<LatLng> LOCATIONS = getLocations();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defibrillator_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        for (LatLng location : LOCATIONS) {
            mMap.addMarker(new MarkerOptions().position(location).title("Defibrillator"));
        }
        // Move the camera to be centered on the first debfibrillator - arbitrary.
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LOCATIONS.get(0)));
    }
}
