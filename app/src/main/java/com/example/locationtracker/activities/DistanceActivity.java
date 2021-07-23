package com.example.locationtracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.locationtracker.Database.LocationDBHelper;
import com.example.locationtracker.Model.LatLangModel;
import com.example.locationtracker.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class DistanceActivity extends AppCompatActivity {
    TextView distance_tv,old_tv,new_tv;
    private static final String TAG = "DISTANCE ACTIVITY";
    String recivedRouteId;
    LocationDBHelper locationDBHelper;
    double lat1;
    double lon1;
    double lat2;
    double lon2;
    double sum;

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getLat1() {
        return lat1;
    }

    public void setLat1(double lat1) {
        this.lat1 = lat1;
    }

    public double getLon1() {
        return lon1;
    }

    public void setLon1(double lon1) {
        this.lon1 = lon1;
    }

    public double getLat2() {
        return lat2;
    }

    public void setLat2(double lat2) {
        this.lat2 = lat2;
    }

    public double getLon2() {
        return lon2;
    }

    public void setLon2(double lon2) {
        this.lon2 = lon2;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);
        distance_tv=findViewById(R.id.distance_tv);
        old_tv=findViewById(R.id.oldpoints_tv);
        new_tv=findViewById(R.id.newpoints_tv);
        setSum(0);
        locationDBHelper = LocationDBHelper.getInstance(DistanceActivity.this);
        recivedRouteId = getIntent().getStringExtra("ROUTEID");
        List<LatLng> coordList = new ArrayList<LatLng>();
        List<LatLangModel> latLangModels = new ArrayList<>();

        List<String[]> distinctRoutes = locationDBHelper.getRouteCoordinates(recivedRouteId);
        for (int i = 0; i < distinctRoutes.size(); i++) {
            Log.d(TAG, "getData: Lat " + distinctRoutes.get(i)[0] + " Long " + distinctRoutes.get(i)[1]);
            coordList.add(new LatLng(Double.parseDouble(distinctRoutes.get(i)[0]), Double.parseDouble(distinctRoutes.get(i)[1])));
        }
        for (int i = 0; i < coordList.size() - 1; i++) {
            LatLng src = coordList.get(i);
            LatLng dest = coordList.get(i + 1);
            old_tv.setText("lat "+src.latitude+" Lang "+src.longitude);
            new_tv.setText("lat "+dest.latitude+" Lang "+dest.longitude);
            setLat1(src.latitude);
            setLon1(src.longitude);
            setLat2(dest.latitude);
            setLon2(dest.longitude);
            Log.d(TAG, "onCreate: "+sum);
            sum=sum+distance(getLat1(),getLon1(),getLat2(),getLon2());
            Log.d(TAG, "onCreate: "+sum);
            distance_tv.setText(sum+"");
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}