package com.example.locationtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationtracker.Database.LocationDBHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    TextView startcor_tv, stopcor_tv, distance_tv, speed_tv;
    RadioGroup radiogroup_btn;
    Button tackmaproute_btn, myRoute_btn, startservice_btn, stopservice_btn;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    double speed;
    LocationDBHelper locationDBHelper;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            speed = intent.getDoubleExtra("Speed", 0.00);
            String text = speed_tv.getText().toString();
            Log.d(TAG, "onReceive: " + text);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speed_tv = findViewById(R.id.speed_tv);
        startcor_tv = findViewById(R.id.startcor_tv);
        stopcor_tv = findViewById(R.id.stopcor_tv);
        distance_tv = findViewById(R.id.distance_tv);
        radiogroup_btn = (RadioGroup) findViewById(R.id.radio_group);
        startservice_btn = findViewById(R.id.startservice_btn);
        stopservice_btn = findViewById(R.id.stopservice_btn);
        tackmaproute_btn = findViewById(R.id.tracMapLocaton_btn);

        locationDBHelper = LocationDBHelper.getInstance(MainActivity.this);

        getData();

        radiogroup_btn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);
                //Toast.makeText(MainActivity.this, ""+rb, Toast.LENGTH_SHORT).show();
                View radioButton = radiogroup_btn.findViewById(checkedId);
                int index = radiogroup_btn.indexOfChild(radioButton);

                // Add logic here

                switch (index) {
                    case 0: // first button
                        String stringdouble = Double.toString(speed);
                        speed_tv.setText(stringdouble);
                        Toast.makeText(getApplicationContext(), "Selected button number " + index, Toast.LENGTH_SHORT).show();
                        break;
                    case 1: // secondbutton
                        double speedinMiles = speed * 2.23694;
                        stringdouble = Double.toString(speedinMiles);
                        speed_tv.setText(stringdouble);
                        Toast.makeText(getApplicationContext(), "Selected button number " + index, Toast.LENGTH_SHORT).show();
                        break;
                    case 2: // secondbutton
                        double speedm = speed * 18 / 5;
                        stringdouble = Double.toString(speedm);
                        speed_tv.setText(stringdouble);
                        Toast.makeText(getApplicationContext(), "Selected button number " + index, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        startservice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION);

                } else {
                    startLocationService();
                }
            }
        });
        stopservice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationService();
                // getApplicationContext().stopService(new Intent(MainActivity.this,LocationService.class));
            }
        });

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

    public void Button1(View view) {
        double speedinMiles = speed * 2.23694;
        String stringdouble = Double.toString(speedinMiles);
        speed_tv.setText(stringdouble);

    }

    public void Button2(View view) {
        String stringdouble = Double.toString(speed);
        speed_tv.setText(stringdouble);
    }

    public void Button3(View view) {
        double speedm = speed * 18 / 5;
        String stringdouble = Double.toString(speedm);
        speed_tv.setText(stringdouble);
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
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location Service started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STOP_SERVICE);
            startService(intent);
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

    private void getData() {
        List<String[]> distinctRoutes = locationDBHelper.getDistinctRoutes();
        for (int i = 0; i < distinctRoutes.size(); i++) {
            Log.d(TAG, "getData: " + distinctRoutes.get(i)[0]);
        }
    }

    private void getRouteData() {
        List<String[]> distinctRoutes = locationDBHelper.getRouteCoordinates("");
        for (int i = 0; i < distinctRoutes.size(); i++) {
            Log.d(TAG, "getData: " + distinctRoutes.get(i)[0]);
        }
    }
}