package com.example.locationtracker;

public class LocationModal {
    int id;
    String Route_id;
    double Lat;
    double Long;
    String Timestamp;

    public LocationModal(String route_id, double lat, double aLong, String timestamp) {
        Route_id = route_id;
        Lat = lat;
        Long = aLong;
        Timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoute_id() {
        return Route_id;
    }

    public void setRoute_id(String route_id) {
        Route_id = route_id;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }
}
