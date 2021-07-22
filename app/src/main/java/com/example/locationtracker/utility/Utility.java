package com.example.locationtracker.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.locationtracker.constants.LocationConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class Utility {
    
    public static void saveDataToPreferences(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(LocationConstants.LOCATION_PREF, MODE_PRIVATE).edit();
        editor.putString("ROUTEID", getRouteID());
        editor.apply();
    }

    public static String getRouteIdFromPref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(LocationConstants.LOCATION_PREF, MODE_PRIVATE);
        String routeID = prefs.getString("ROUTEID", "NA");
        return routeID;
    }

    public static String getRouteID() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String mRouteId = sdf.format(cal.getTime());
        return mRouteId;
    }

    public static int getSpeedInMeters(double speed) {
        int newspeed = (int) speed;
        int convertSpeed = 3600 * newspeed;//  meter/hours
        return convertSpeed;
    }

    public static int getSpeedInMiles(double speed) {
        int newspeed = (int) speed;
        int convertSpeed = (int) (2.23694 * newspeed);
        return convertSpeed;
    }

    public static int getSpeedInKm(double speed) {
        int newspeed = (int) speed;
        int convertSpeed = (int) (3.6 * newspeed);
        return convertSpeed;
    }

}
