package com.example.locationtracker.Model;

public class RouteIdListModel {
    private String toatalDistance;

    public RouteIdListModel(String routeId) {
        this.routeId = routeId;
    }

    private String routeId;

    public RouteIdListModel(String toatalDistance, String routeId) {
        this.toatalDistance = toatalDistance;
        this.routeId = routeId;
    }

    public String getToatalDistance() {
        return toatalDistance;
    }

    public void setToatalDistance(String toatalDistance) {
        this.toatalDistance = toatalDistance;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}
