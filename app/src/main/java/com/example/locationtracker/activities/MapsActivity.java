package com.example.locationtracker.activities;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.example.locationtracker.Database.LocationDBHelper;
import com.example.locationtracker.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private Marker marker;
    private String recivedRouteId;
    LocationDBHelper locationDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationDBHelper = LocationDBHelper.getInstance(MapsActivity.this);
        recivedRouteId = getIntent().getStringExtra("ROUTEID");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ArrayList<LatLng> coordList = new ArrayList<LatLng>();

        // getRouteData(recivedRouteId);
//        coordList.add(new LatLng(0, 0));
//        coordList.add(new LatLng(1, 1));
//        coordList.add(new LatLng(2, 2));

        List<String[]> distinctRoutes = locationDBHelper.getRouteCoordinates(recivedRouteId);
        for (int i = 0; i < distinctRoutes.size(); i++) {
            Log.d(TAG, "getData: Lat " + distinctRoutes.get(i)[0] + "Long"+ distinctRoutes.get(i)[1]);
            coordList.add(new LatLng(Double.parseDouble(distinctRoutes.get(i)[0]),Double.parseDouble(distinctRoutes.get(i)[1])));
        }

        for (int i = 0; i < coordList.size() - 1; i++) {
            LatLng src = coordList.get(i);
            LatLng dest = coordList.get(i + 1);

            // mMap is the Map Object
            Polyline line = mMap.addPolyline(
                    new PolylineOptions().add(
                            new LatLng(src.latitude, src.longitude),
                            new LatLng(dest.latitude, dest.longitude)
                    ).width(5).color(Color.RED).geodesic(true)
            );
        }


        marker = mMap.addMarker(new MarkerOptions().
                position(coordList.get(0))
                .title("My Marker")
                .draggable(true)
                .snippet("Lat")
        );

        CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(coordList.get(0),15);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(coordList.get(0)));
        mMap.animateCamera(myLocation);

    }

    private void getRouteData(String routId) {
        List<String[]> distinctRoutes = locationDBHelper.getRouteCoordinates(routId);
        for (int i = 0; i < distinctRoutes.size(); i++) {
            Log.d(TAG, "getData: Lat " + distinctRoutes.get(i)[0] + "Long" + distinctRoutes.get(i)[1]);
            // coordList.add(new LatLng(Double.parseDouble(distinctRoutes.get(i)[0]),Double.parseDouble(distinctRoutes.get(i)[1])));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}


