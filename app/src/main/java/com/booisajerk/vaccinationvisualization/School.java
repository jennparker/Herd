package com.booisajerk.vaccinationvisualization;

import com.google.android.gms.maps.model.LatLng;

public class School {
    private String name;
    private int percentTotal;
    private double lat;
    private double lng;

    School(String name, int percentTotal, double lat, double lng) {
        this.name = name;
        this.percentTotal = percentTotal;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public int getPercentTotal() {
        return percentTotal;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }
}
