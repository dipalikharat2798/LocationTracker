package com.example.locationtracker.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.locationtracker.Database.LocationDBHelper;
import com.example.locationtracker.R;
import com.example.locationtracker.constants.LocationConstants;
import com.example.locationtracker.services.LocationService;
import com.example.locationtracker.utility.Utility;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "MainActivity";
    TextView speed_tv;
    RadioGroup radiogroup_btn;
    RadioButton milesradio_btn, kmradio_btn, meterradio_btn;
    Button startstop_btn, myRoute_btn, track_btn, myroutes_btn;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    double speed;
    LocationDBHelper locationDBHelper;
    String mConvertionUnit = "";
    String RouteId = "";
    String location_usertype="";

    public String getLocation_usertype() {
        return location_usertype;
    }

    public void setLocation_usertype(String location_usertype) {
        this.location_usertype = location_usertype;
    }

    public String getRouteId() {
        return RouteId;
    }

    public void setRouteId(String routeId) {
        RouteId = routeId;
    }

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
        mConvertionUnit = "Miles";
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        NotificationManager notificationManager =
                (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLocationServiceRunning()) {
            startstop_btn.setText("Stop");
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
        if (!isLocationServiceRunning() && location_usertype!=null) {
            Utility.saveDataToPreferences(MainActivity.this);
            setRouteId(Utility.getRouteIdFromPref(MainActivity.this));
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.putExtra("userType",getLocation_usertype());
            intent.setAction(LocationConstants.ACTION_START_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location Service started", Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseOption() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.select_option, null);
        builder.setCancelable(false);
        builder.setView(dialogView);
        CardView imageViewwalk = dialogView.findViewById(R.id.walkBtn);
        CardView imageViewbike = dialogView.findViewById(R.id.bikeBtn);
        CardView imageViewcar = dialogView.findViewById(R.id.carBtn);
        CardView imageViewtrain = dialogView.findViewById(R.id.trainBtn);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        imageViewwalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation_usertype("walk");
                alertDialog.dismiss();
                Toast.makeText(MainActivity.this, "walking", Toast.LENGTH_SHORT).show();
                startLocationService();
            }
        });
        imageViewbike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation_usertype("bike");
                alertDialog.dismiss();
                Toast.makeText(MainActivity.this, "Bike", Toast.LENGTH_SHORT).show();
                startLocationService();
            }
        });
        imageViewcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation_usertype("car");
                alertDialog.dismiss();
                Toast.makeText(MainActivity.this, "car", Toast.LENGTH_SHORT).show();
                startLocationService();
            }
        });
        imageViewtrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation_usertype("train");
                alertDialog.dismiss();
                Toast.makeText(MainActivity.this, "train", Toast.LENGTH_SHORT).show();
                startLocationService();
            }
        });

    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            stopService(intent);
            setRouteId("NA");
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


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.myroutes_btn:
                intent = new Intent(this, RouteListActivity.class);
                this.startActivity(intent);
                break;

            case R.id.track_btn:
                //  String routeID = Utility.getRouteID();
                if (!getRouteId().equalsIgnoreCase("NA")) {
                    intent = new Intent(this, MapsActivity.class);
                    intent.putExtra("ROUTEID", getRouteId());
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
                      // startLocationService();
                        chooseOption();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speed_tv.setText("O");
    }
}