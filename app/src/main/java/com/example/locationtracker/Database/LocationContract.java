package com.example.locationtracker.Database;

public final class LocationContract {
    public LocationContract() {
    }

    public static class LOCATION_MASTER {
        //table name
        public static final String LOCATION_MASTER_TABLE = "location_master_table";

        //Columns
        public static final String LOCATION_ROW_ID = "location_row_id";
        public static final String LOCATION_TRIP_ID = "location_trip_id";
        public static final String LOCATION_LATITUDE = "location_latitude";
        public static final String LOCATION_LONGITUDE = "location_longitude";
        public static final String LOCATION_TIMESTAMP = "location_timestamp";

    }
}
