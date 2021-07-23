package com.example.locationtracker.activities;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
        if (! coordList.isEmpty()) {
            marker = mMap.addMarker(new MarkerOptions().
                    position(coordList.get(0))
                    .title("My Marker")
                    .draggable(true)
                    .snippet("Lat")
            );

            CameraUpdate myLocation = CameraUpdateFactory.newLatLngZoom(coordList.get(0), 15);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coordList.get(0)));
            mMap.animateCamera(myLocation);
        }else {
//            LatLng m1 = new LatLng(Double.parseDouble(distinctRoutes.get(0)[0]),Double.parseDouble(distinctRoutes.get(0)[1]));
//            marker = mMap.addMarker(new MarkerOptions().
//                    position(m1)
//                    .title("My Marker")
//                    .draggable(true)
//                    .snippet("Lat"));
            Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
        }

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


