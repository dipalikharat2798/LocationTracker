package com.example.locationtracker.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.example.locationtracker.Model.LocationModel;

import java.util.ArrayList;
import java.util.List;

import static com.example.locationtracker.Database.LocationContract.LOCATION_MASTER.LOCATION_LATITUDE;
import static com.example.locationtracker.Database.LocationContract.LOCATION_MASTER.LOCATION_LONGITUDE;
import static com.example.locationtracker.Database.LocationContract.LOCATION_MASTER.LOCATION_MASTER_TABLE;
import static com.example.locationtracker.Database.LocationContract.LOCATION_MASTER.LOCATION_ROW_ID;
import static com.example.locationtracker.Database.LocationContract.LOCATION_MASTER.LOCATION_TIMESTAMP;
import static com.example.locationtracker.Database.LocationContract.LOCATION_MASTER.LOCATION_TRIP_ID;

public class LocationDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Location.db";
    public static final int DATABASE_VERSION = 1;

    private static LocationDBHelper sInstance;

    private LocationDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized LocationDBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LocationDBHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteTable(db);
    }

    private void deleteTable(SQLiteDatabase db) {
        db.execSQL(SQL_DROP_LOCATION_TABLE);
    }

    //Create Table
    private static final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE IF NOT EXISTS " + LOCATION_MASTER_TABLE + " (" + LOCATION_ROW_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LOCATION_TRIP_ID + " VARCHAR(50), "
            + LOCATION_LATITUDE + " VARCHAR(50), " + LOCATION_LONGITUDE + " VARCHAR(50), "
            + LOCATION_TIMESTAMP + " VARCHAR(50)) ";

    //Drop table sms
    private static final String SQL_DROP_LOCATION_TABLE =
            "DROP TABLE IF EXISTS " + LOCATION_MASTER_TABLE;

    public boolean insertDataToLocationMaster(LocationModel locationmodel) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();
            String sql = "INSERT INTO " + LOCATION_MASTER_TABLE + "(" + LOCATION_TRIP_ID + "," + LOCATION_LATITUDE + ","
                    + LOCATION_LONGITUDE + ","
                    + LOCATION_TIMESTAMP
                    + ") VALUES (?,?,?,datetime('now', 'localtime'))";
            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindString(1, locationmodel.getTripId());
            stmt.bindString(2, locationmodel.getmLatitude() + "");
            stmt.bindString(3, locationmodel.getmLongitude() + "");

            stmt.execute();
            stmt.clearBindings();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return true;
    }

    public List<String[]> getDistinctRoutes() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = null;
        String[] selectArgs = null;
        List<String[]> tableData = new ArrayList<>();
        query = "SELECT DISTINCT " + LOCATION_TRIP_ID + " FROM " + LOCATION_MASTER_TABLE;
        selectArgs = null;
        Cursor cursor = db.rawQuery(query, selectArgs);
        while (cursor.moveToNext()) {
            String[] columnData = new String[cursor.getColumnCount()];
            for (int i = 0; i < columnData.length; i++) {
                columnData[i] = cursor.getString(i);
            }
            tableData.add(columnData);
        }
        return tableData;
    }

    public List<String[]> getRouteCoordinates(String routeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = null;
        String[] selectArgs = null;
        List<String[]> tableData = new ArrayList<>();
        query = "SELECT " + LOCATION_LATITUDE + "," + LOCATION_LONGITUDE + " FROM " + LOCATION_MASTER_TABLE + " WHERE " + LOCATION_TRIP_ID + " == '" + routeId + "'";
        selectArgs = null;
        Cursor cursor = db.rawQuery(query, selectArgs);
        while (cursor.moveToNext()) {
            String[] columnData = new String[cursor.getColumnCount()];
            for (int i = 0; i < columnData.length; i++) {
                columnData[i] = cursor.getString(i);
            }
            tableData.add(columnData);
        }
        return tableData;
    }
    public boolean deleteDataFromLocationMaster(String routeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = null;
        query = " DELETE " + " FROM " + LOCATION_MASTER_TABLE + " WHERE " + LOCATION_TRIP_ID + " == '" + routeId + "'";
        db.execSQL(query);
        return true;
    }
}
