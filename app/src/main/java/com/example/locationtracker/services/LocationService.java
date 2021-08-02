package com.example.locationtracker.services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.locationtracker.Database.LocationDBHelper;
import com.example.locationtracker.Model.LocationModel;
import com.example.locationtracker.R;
import com.example.locationtracker.constants.LocationConstants;
import com.example.locationtracker.utility.Utility;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LocationService extends Service {
    public static String SERVICE_MESSAGE = "Send Data From service";
    Context context;
    double speed;
    int interval = 0;
    int fastestInterval = 0;

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getFastestInterval() {
        return fastestInterval;
    }

    public void setFastestInterval(int fastestInterval) {
        this.fastestInterval = fastestInterval;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    CallStateReceiver callStateReceiver;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLocations() != null) {
                double speed = locationResult.getLastLocation().getSpeed();
                setSpeed(speed);
                String latitude = String.valueOf(locationResult.getLastLocation().getLatitude());
                String longitude = String.valueOf(locationResult.getLastLocation().getLongitude());
                LocationModel model = new LocationModel(Utility.getRouteIdFromPref(context), latitude, longitude);
                insertLocationData(context, model);
                sendMessageToUi(speed);
                Log.d("Location Service Lat", "onLocationResult: " + model.getmLatitude() + " " + model.getmLongitude() + " " + model.getTripId() + " " + speed);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startLocationService() {
        String channelId = "LOCATION_NOTIFICATION_CHANNEL";
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelId
        );
        builder.setSmallIcon(R.drawable.ic_notify);
        builder.setContentTitle("Tracking your route");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Tracking");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null &&
                    notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Location Service",
                        notificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This channel uses location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(getInterval())                                     //means - set the interval in which you want to get locations
                .setFastestInterval(getFastestInterval())                             //means - if a location is available sooner you can get it (i.e. another app is using the location services).
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(100);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(LocationConstants.LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationService() {
        if (callStateReceiver != null) {
            unregisterReceiver(callStateReceiver);
            callStateReceiver = null;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();

        if (intent != null) {
            String action = intent.getAction();
            String user = intent.getStringExtra("userType");
            setTime(user);
            if (action != null) {
                if (action.equals(LocationConstants.ACTION_START_SERVICE)) {
                    startLocationService();
                    registerCallStateReceiver();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void setTime(String user) {
        switch (user) {
            case "walk":
                setInterval(10000);//10sec
                setFastestInterval(8000);
                break;
            case "bike":
                setInterval(60000);//1min
                setFastestInterval(30000);
                break;
            case "car":
                setInterval(4*60000);//4min
                setFastestInterval(4*20000);
                break;
            case "train":
                setInterval(6*60000);//6min
                setFastestInterval(6*20000);
                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroy() {
        stopLocationService();
        super.onDestroy();
    }

    private void sendMessageToUi(double speed) {
        Intent intent = new Intent(SERVICE_MESSAGE);
        intent.putExtra("SPEED", speed);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void insertLocationData(Context context, LocationModel locationmodel) {
        LocationDBHelper locationDBHelper = LocationDBHelper.getInstance(context);
        locationDBHelper.insertDataToLocationMaster(locationmodel);
    }

    private void registerCallStateReceiver() {
        callStateReceiver = new CallStateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(callStateReceiver, intentFilter);
    }

    public class CallStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                System.out.println("Receiver start");
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.d("TAG", "onReceive number: " + incomingNumber);
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    Toast.makeText(context, "Incoming Call State", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, "Ringing State Number is -" + incomingNumber, Toast.LENGTH_SHORT).show();
                    //  sendMessage(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    if (getSpeed() > 23) {
//                        AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                        audio.setRingerMode(0);
                        makeSilent();
                        Log.d("TAG", "onReceive number: " + incomingNumber.substring(3));
                        sendMessage(incomingNumber.substring(3));
                    }
                }
                if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))) {
                    Toast.makeText(context, "Call Received State", Toast.LENGTH_SHORT).show();
                }
                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    Toast.makeText(context, "Call Idle State", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void makeSilent() {
        AudioManager am;
        am = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        //For Normal mode
        //  am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        //For Silent mode
        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        //For Vibrate mode
        // am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

    }

    private void sendMessage(String incomingNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        String message = "I am driving. Will call you later";
        smsManager.sendTextMessage(incomingNumber, null, message, null, null);
    }

}

//https://stackoverflow.com/questions/20398898/how-to-get-speed-in-android-app-using-location-or-accelerometer-or-some-other-wa
