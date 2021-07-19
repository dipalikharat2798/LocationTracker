package com.example.locationtracker.Model;

public class LocationModel {
    private String tripId;
    private String mLatitude;
    private String mLongitude;
    private String mTimestamp;

    public LocationModel(String tripId, String mLatitude, String mLongitude, String mTimestamp) {
        this.tripId = tripId;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mTimestamp = mTimestamp;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(String mLatitude) {
        this.mLatitude = mLatitude;
    }

    public String getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(String mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getmTimestamp() {
        return mTimestamp;
    }

    public void setmTimestamp(String mTimestamp) {
        this.mTimestamp = mTimestamp;
    }
}
