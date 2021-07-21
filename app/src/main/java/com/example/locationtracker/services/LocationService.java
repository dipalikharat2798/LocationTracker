package com.example.locationtracker;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.locationtracker.Database.LocationDBHelper;
import com.example.locationtracker.Model.LocationModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LocationService extends Service {
    public static String SERVICE_MESSAGE = "Send Data From service";
    Context context;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLocations() != null) {
                //speed
                double speed = locationResult.getLastLocation().getSpeed();
                double a = 3.6 * (speed);
                int kmhSpeed = (int) (Math.round(a));
                //lat and longitude
                String lattitude = String.valueOf(locationResult.getLastLocation().getLatitude());
                String longitude = String.valueOf(locationResult.getLastLocation().getLongitude());
                //trackid and time
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                String strDate = sdf.format(cal.getTime());

                String timeMilli = String.valueOf(cal.getTimeInMillis());
                LocationModel model = new LocationModel(strDate, lattitude, longitude, timeMilli);

                //store in db
                context = getApplicationContext();
                //  insertLocationData(context, model);
                sendMessageToUi(kmhSpeed);
                Log.d("Location Service Lat", "onLocationResult: " + lattitude + " " + longitude + " " + timeMilli + " " + strDate + " " + kmhSpeed);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startLocationService() {
        String channelId = "Location_notification_channel";
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
        builder.setContentTitle("Location SErvice");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
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
                .setInterval(4000)                                     //means - set the interval in which you want to get locations
                .setFastestInterval(2000)                             //means - if a location is available sooner you can get it (i.e. another app is using the location services).
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(100);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Constants.ACTION_START_SERVICE)) {
                    startLocationService();
                } else if (action.equals(Constants.ACTION_STOP_SERVICE)) {
                    stopLocationService();
                    Log.d("TAG", "onStartCommand: Stopped service");
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        stopLocationService();
        super.onDestroy();
    }

    private void sendMessageToUi(double speed) {
        Intent intent = new Intent(SERVICE_MESSAGE);
        intent.putExtra("Speed", speed);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void insertLocationData(Context context, LocationModel locationmodel) {
        LocationDBHelper locationDBHelper = LocationDBHelper.getInstance(context);
        locationDBHelper.insertDataToLocationMaster(locationmodel);
    }

}

//https://stackoverflow.com/questions/20398898/how-to-get-speed-in-android-app-using-location-or-accelerometer-or-some-other-wa
