package com.example.locationtracker;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng m1 = new LatLng(20, 40);
        LatLng m2 = new LatLng(25, 45);
        LatLng m3 = new LatLng(30, 50);
        LatLng m4 = new LatLng(35, 55);
        LatLng m5 = new LatLng(40, 60);

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        marker = mMap.addMarker(new MarkerOptions().
                position(m1)
                .title("My Marker")
                .draggable(true)
                .snippet("Lat")
        );

        PolylineOptions rectOption = new PolylineOptions()
                .add(m1)
                .add(m2)
                .add(m3)
                .add(m4)
                .add(m5);
        Polyline polyline = mMap.addPolyline(rectOption);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(m1));

//        List<LatLng> points = decodePoly(_path); // list of latlng
//        for (int i = 0; i < points.size() - 1; i++) {
//            LatLng src = points.get(i);
//            LatLng dest = points.get(i + 1);
//
//            // mMap is the Map Object
//            Polyline line = mMap.addPolyline(
//                    new PolylineOptions().add(
//                            new LatLng(src.latitude, src.longitude),
//                            new LatLng(dest.latitude,dest.longitude)
//                    ).width(2).color(Color.BLUE).geodesic(true)
//            );
//        }

       // https://stackoverflow.com/questions/16311076/how-to-dynamically-add-polylines-from-an-arraylist
    }
}