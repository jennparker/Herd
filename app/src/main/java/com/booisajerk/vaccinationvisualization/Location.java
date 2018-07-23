package com.booisajerk.vaccinationvisualization;

import com.google.android.gms.maps.model.LatLng;

public class Location {
    private String name;
    private double percentTotal;
    private double lat;
    private double lng;

    Location(String name, double percentTotal, double lat, double lng) {
        this.name = name;
        this.percentTotal = percentTotal;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public double getPercentTotal() {
        return percentTotal;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }
}
