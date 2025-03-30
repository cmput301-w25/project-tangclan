package com.example.tangclan;

import java.io.Serializable;

public class LocationData implements Serializable {
    private String lid;
    private String mid;
    private double latitude;
    private double longitude;
    private String name;

    public LocationData() {}

    public LocationData(String mid, double latitude, double longitude, String name) {
        this.mid = mid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    // Getters and setters
    public String getLid() { return lid; }
    public void setLid(String lid) { this.lid = lid; }
    public String getMid() { return mid; }
    public void setMid(String mid) { this.mid = mid; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}