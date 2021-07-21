package com.example.locationtracker.Model;

public class LocationModel {
    private String tripId;
    private String mLatitude;
    private String mLongitude;

    public LocationModel(String tripId, String mLatitude, String mLongitude) {
        this.tripId = tripId;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
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

}
