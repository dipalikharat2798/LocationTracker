package com.example.locationtracker.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.locationtracker.Database.LocationDBHelper;
import com.example.locationtracker.R;
import com.example.locationtracker.constants.LocationConstants;
import com.example.locationtracker.services.LocationService;
import com.example.locationtracker.utility.Utility;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "MainActivity";
    TextView speed_tv;
    RadioGroup radiogroup_btn;
    RadioButton milesradio_btn, kmradio_btn, meterradio_btn;
    Button startstop_btn, myRoute_btn, track_btn, myroutes_btn;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    double speed;
    LocationDBHelper locationDBHelper;
    String mConvertionUnit="";

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            speed = intent.getDoubleExtra("SPEED", 0.00);
            Log.d(TAG, "onReceive: " + speed);
            if (mConvertionUnit.equalsIgnoreCase("meters")) {
                speed_tv.setText(String.valueOf(Utility.getSpeedInMeters(speed)));
            } else if (mConvertionUnit.equalsIgnoreCase("Kilometers")) {
                speed_tv.setText(String.valueOf(Utility.getSpeedInKm(speed)));
            } else if (mConvertionUnit.equalsIgnoreCase("Miles")) {
                speed_tv.setText(String.valueOf(Utility.getSpeedInMiles(speed)));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speed_tv = findViewById(R.id.speed_tv);
        radiogroup_btn = (RadioGroup) findViewById(R.id.radio_group);
        radiogroup_btn.setOnCheckedChangeListener(this);
        track_btn = findViewById(R.id.track_btn);
        track_btn.setOnClickListener(this);
        myroutes_btn = findViewById(R.id.myroutes_btn);
        myroutes_btn.setOnClickListener(this);
        startstop_btn = findViewById(R.id.startstop_btn);
        startstop_btn.setOnClickListener(this);
        milesradio_btn = findViewById(R.id.milesradio_btn);
        kmradio_btn = findViewById(R.id.kmradio_btn);
        meterradio_btn = findViewById(R.id.meterradio_btn);
        locationDBHelper = LocationDBHelper.getInstance(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLocationServiceRunning()) {
            startstop_btn.setText("Stop");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationService.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Utility.saveDataToPreferences(MainActivity.this);
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(LocationConstants.ACTION_START_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location Service started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            stopService(intent);
            Toast.makeText(this, "Location Service stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver, new IntentFilter(LocationService.SERVICE_MESSAGE));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReceiver);        //<-- Unregister to avoid memoryleak
    }

//    private void getData() {
//        List<String[]> distinctRoutes = locationDBHelper.getDistinctRoutes();
//        for (int i = 0; i < distinctRoutes.size(); i++) {
//            Log.d(TAG, "getData: " + distinctRoutes.get(i)[0]);
//        }
//    }
//
//    private void getRouteData() {
//        List<String[]> distinctRoutes = locationDBHelper.getRouteCoordinates("");
//        for (int i = 0; i < distinctRoutes.size(); i++) {
//            Log.d(TAG, "getData: " + distinctRoutes.get(i)[0]);
//        }
//    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.myroutes_btn:
                intent = new Intent(this, RouteListActivity.class);
                this.startActivity(intent);
                break;

            case R.id.track_btn:
                String routeID = Utility.getRouteID();
                if (!routeID.equalsIgnoreCase("NA")) {
                    intent = new Intent(this, MapsActivity.class);
                    intent.putExtra("ROUTEID", Utility.getRouteID());
                    this.startActivity(intent);
                }
                break;

            case R.id.startstop_btn:
                if (startstop_btn.getText().toString().equalsIgnoreCase("Stop")) {
                    stopLocationService();
                    startstop_btn.setText("Start");
                } else {
                    if (ContextCompat.checkSelfPermission(
                            getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CODE_LOCATION_PERMISSION);

                    } else {
                        startLocationService();
                    }
                    startstop_btn.setText("Stop");
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.meterradio_btn:
                mConvertionUnit = "meters";
                Toast.makeText(MainActivity.this, "meterradio_btn ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.kmradio_btn:
                mConvertionUnit = "Kilometers";
                Toast.makeText(MainActivity.this, "kmradio_btn ", Toast.LENGTH_SHORT).show();
                break;
            case R.id.milesradio_btn:
                mConvertionUnit = "Miles";
                Toast.makeText(MainActivity.this, "milesradio_btn ", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}