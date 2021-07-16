package com.example.locationtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "LocationTracker";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "myLocation";
    private static final String Record_id="Record_id";
    private static final String Route_id="Route_id";
    private static final String Latitude="Lat";
    private static final String Lang="Lang";
    private static final String Timestamp="Timestamp";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + Record_id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Route_id + " TEXT,"
                + Latitude + " DOUBLE,"
                + Lang + " DOUBLE,"
                + Timestamp + " TEXT)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }
    // this method is use to add new bill to our sqlite database.
    public void addNewLocation(String Route_id,double Lat,double Long, String Timestamp) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Route_id, Route_id);
        values.put(Latitude, Lat);
        values.put(Lang, Long);
        values.put(Timestamp, Timestamp);


        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }
    // we have created a new method for reading all the Locations.
    public ArrayList<LocationModal> readLocations() {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorLocations = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        // on below line we are creating a new array list.
        ArrayList<LocationModal> LocationModalArrayList = new ArrayList<>();

        // moving our cursor to first position.
        if (cursorLocations.moveToFirst()) {
            do {
                LocationModalArrayList.add(new LocationModal(
                        cursorLocations.getString(1),
                        cursorLocations.getDouble(2),
                        cursorLocations.getDouble(3),
                        cursorLocations.getString(4)));
            } while (cursorLocations.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorLocations.close();
        return LocationModalArrayList;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
